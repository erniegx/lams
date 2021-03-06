package org.lamsfoundation.lams.monitoring.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.gradebook.GradebookUserActivity;
import org.lamsfoundation.lams.gradebook.service.IGradebookService;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.ActivityOrderComparator;
import org.lamsfoundation.lams.learningdesign.BranchingActivity;
import org.lamsfoundation.lams.learningdesign.ComplexActivity;
import org.lamsfoundation.lams.learningdesign.ContributionTypes;
import org.lamsfoundation.lams.learningdesign.GateActivity;
import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.learningdesign.PermissionGateActivity;
import org.lamsfoundation.lams.learningdesign.SequenceActivity;
import org.lamsfoundation.lams.learningdesign.ToolActivity;
import org.lamsfoundation.lams.learningdesign.Transition;
import org.lamsfoundation.lams.learningdesign.dao.IActivityDAO;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.service.ILessonService;
import org.lamsfoundation.lams.monitoring.dto.ContributeActivityDTO;
import org.lamsfoundation.lams.monitoring.dto.PermissionGateDTO;
import org.lamsfoundation.lams.monitoring.dto.TblGroupDTO;
import org.lamsfoundation.lams.monitoring.dto.TblUserDTO;
import org.lamsfoundation.lams.monitoring.service.IMonitoringService;
import org.lamsfoundation.lams.tool.ToolSession;
import org.lamsfoundation.lams.tool.service.ILamsCoreToolService;
import org.lamsfoundation.lams.tool.service.ILamsToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.CentralConstants;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Displays TBL monitor.
 *
 * @author Andrey Balan
 */
public class TblMonitoringAction extends LamsDispatchAction {

    private static Logger log = Logger.getLogger(TblMonitoringAction.class);

    private static ILessonService lessonService;
    private static IMonitoringService monitoringService;
    private static ILamsCoreToolService coreToolService;
    private static ILamsToolService toolService;
    private static IActivityDAO activityDAO;
    private static IGradebookService gradebookService;

    /**
     * Displays addStudent page.
     */
    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	initServices();

	long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	Lesson lesson = lessonService.getLesson(lessonId);
	request.setAttribute("lesson", lesson);
	request.setAttribute("totalLearnersNumber", lesson.getAllLearners().size());

	List<Activity> lessonActivities = getLessonActivities(lesson);
	setupAvailableActivityTypes(request, lessonActivities);
	return mapping.findForward("tblmonitor");
    }

    /**
     * Shows Teams page
     */
    public ActionForward teams(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {

	long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	Lesson lesson = lessonService.getLesson(lessonId);

	List<Activity> lessonActivities = getLessonActivities(lesson);
	setupAvailableActivityTypes(request, lessonActivities);
	boolean isScratchieAvailable = (request.getAttribute("isScratchieAvailable") != null)
		&& ((Boolean) request.getAttribute("isScratchieAvailable"));
	boolean isIraMcqAvailable = (request.getAttribute("isIraMcqAvailable") != null)
		&& ((Boolean) request.getAttribute("isIraMcqAvailable"));
	boolean isIraAssessmentAvailable = (request.getAttribute("isIraAssessmentAvailable") != null)
		&& ((Boolean) request.getAttribute("isIraAssessmentAvailable"));
	Long iraToolActivityId = request.getAttribute("iraToolActivityId") == null ? null
		: (Long) request.getAttribute("iraToolActivityId");
	Long traToolActivityId = request.getAttribute("traToolActivityId") == null ? null
		: (Long) request.getAttribute("traToolActivityId");
	Long leaderselectionToolActivityId = request.getAttribute("leaderselectionToolActivityId") == null ? null
		: (Long) request.getAttribute("leaderselectionToolActivityId");

	//get all mcq and assessment scores
	List<GradebookUserActivity> iraGradebookUserActivities = new LinkedList<>();
	List<GradebookUserActivity> traGradebookUserActivities = new LinkedList<>();
	if (isIraMcqAvailable || isIraAssessmentAvailable) {
	    iraGradebookUserActivities = gradebookService.getGradebookUserActivities(iraToolActivityId);
	}
	if (isScratchieAvailable) {
	    traGradebookUserActivities = gradebookService.getGradebookUserActivities(traToolActivityId);
	}

	Set<Long> leaderUserIds = leaderselectionToolActivityId == null ? new HashSet<Long>()
		: toolService.getLeaderUserId(leaderselectionToolActivityId);

	GroupingActivity groupingActivity = getGroupingActivity(lesson);
	Grouping grouping = groupingActivity == null ? null : groupingActivity.getCreateGrouping();
	Set<Group> groups = grouping == null ? null : grouping.getGroups();

	Set<TblGroupDTO> groupDtos = new TreeSet<TblGroupDTO>();
	if (groups != null) {
	    for (Group group : groups) {
		TblGroupDTO groupDto = new TblGroupDTO(group);
		groupDtos.add(groupDto);

		if (group.getUsers() != null) {
		    for (User user : group.getUsers()) {
			TblUserDTO userDto = new TblUserDTO(user.getUserDTO());
			groupDto.getUserList().add(userDto);

			//set up all user leaders
			if (leaderUserIds.contains(new Long(user.getUserId()))) {
			    userDto.setGroupLeader(true);
			    groupDto.setGroupLeader(userDto);
			}

			if (isIraMcqAvailable || isIraAssessmentAvailable) {
			    //find according iraGradebookUserActivity
			    for (GradebookUserActivity iraGradebookUserActivity : iraGradebookUserActivities) {
				if (iraGradebookUserActivity.getLearner().getUserId().equals(user.getUserId())) {
				    userDto.setIraScore(iraGradebookUserActivity.getMark());
				    break;
				}
			    }
			}

			if (isScratchieAvailable) {
			    //find according traGradebookUserActivity
			    for (GradebookUserActivity traGradebookUserActivity : traGradebookUserActivities) {
				if (traGradebookUserActivity.getLearner().getUserId().equals(user.getUserId())) {
				    //we set traScore multiple times, but it's doesn't matter
				    groupDto.setTraScore(traGradebookUserActivity.getMark());
				    break;
				}
			    }
			}
		    }
		}
	    }
	}
	request.setAttribute("groupDtos", groupDtos);

	return mapping.findForward("teams");
    }

    /**
     * Shows Gates page
     */
    public ActionForward gates(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);

	List<PermissionGateDTO> permissionGates = new ArrayList<PermissionGateDTO>();

	List<ContributeActivityDTO> contributeActivities = monitoringService.getAllContributeActivityDTO(lessonId);
	if (contributeActivities != null) {
	    for (ContributeActivityDTO contributeActivity : contributeActivities) {

		if (contributeActivity.getContributeEntries() != null) {

		    //check if there is any persmission gates entries
		    for (ContributeActivityDTO.ContributeEntry contributeEntry : contributeActivity
			    .getContributeEntries()) {
			if (ContributionTypes.PERMISSION_GATE.equals(contributeEntry.getContributionType())) {

			    Long activityId = contributeActivity.getActivityID();
			    Activity activity = monitoringService.getActivityById(activityId);
			    PermissionGateDTO gateDto = new PermissionGateDTO((PermissionGateActivity) activity);

			    gateDto.setUrl(contributeEntry.getURL());
			    gateDto.setComplete(contributeEntry.getIsComplete());

			    int waitingLearnersCount = lessonService.getCountLearnersHaveAttemptedActivity(activity);
			    gateDto.setWaitingLearnersCount(waitingLearnersCount);

			    permissionGates.add(gateDto);
			    break;
			}
		    }
		}
	    }
	}

	request.setAttribute("permissionGates", permissionGates);
	return mapping.findForward("gates");
    }

    /**
     * Shows forum page
     */
    public ActionForward forum(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {

	long forumActivityId = WebUtil.readLongParam(request, "activityId");
	ToolActivity forumActivity = (ToolActivity) monitoringService.getActivityById(forumActivityId);

	int attemptedLearnersNumber = lessonService.getCountLearnersHaveAttemptedActivity(forumActivity);
	request.setAttribute("attemptedLearnersNumber", attemptedLearnersNumber);

	Set<ToolSession> toolSessions = forumActivity.getToolSessions();
	request.setAttribute("toolSessions", toolSessions);

	return mapping.findForward("forum");
    }

    /**
     * Shows peerreview page
     */
    public ActionForward peerreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {

	long peerreviewActivityId = WebUtil.readLongParam(request, "activityId");
	ToolActivity peerreviewActivity = (ToolActivity) monitoringService.getActivityById(peerreviewActivityId);

	int attemptedLearnersNumber = lessonService.getCountLearnersHaveAttemptedActivity(peerreviewActivity);
	request.setAttribute("attemptedLearnersNumber", attemptedLearnersNumber);

	Set<ToolSession> toolSessions = peerreviewActivity.getToolSessions();
	request.setAttribute("toolSessions", toolSessions);

	return mapping.findForward("peerreview");
    }

    /**
     * Shows sequence diagram page
     */
    public ActionForward sequence(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	Lesson lesson = lessonService.getLesson(lessonId);
	request.setAttribute("lesson", lesson);
	return mapping.findForward("sequence");
    }

    /**
     * Returns lesson activities sorted by the learning design order.
     */
    @SuppressWarnings("unchecked")
    private List<Activity> getLessonActivities(Lesson lesson) {

	/*
	 * Hibernate CGLIB is failing to load the first activity in the sequence as a ToolActivity for some mysterious
	 * reason Causes a ClassCastException when you try to cast it, even if it is a ToolActivity.
	 *
	 * THIS IS A HACK to retrieve the first tool activity manually so it can be cast as a ToolActivity - if it is
	 * one
	 */
	Activity firstActivity = activityDAO
		.getActivityByActivityId(lesson.getLearningDesign().getFirstActivity().getActivityId());
	List<Activity> activities = new ArrayList<Activity>();
	sortActivitiesByLearningDesignOrder(firstActivity, activities);

	return activities;
    }

    /**
     * Sort all activities by the learning design order.
     *
     * @param activity
     * @param sortedActivities
     */
    private void sortActivitiesByLearningDesignOrder(Activity activity, List<Activity> sortedActivities) {
	sortedActivities.add(activity);

	//in case of branching activity - add all activities based on their orderId
	if (activity.isBranchingActivity()) {
	    BranchingActivity branchingActivity = (BranchingActivity) activity;
	    Set<SequenceActivity> sequenceActivities = new TreeSet<SequenceActivity>(new ActivityOrderComparator());
	    sequenceActivities.addAll(branchingActivity.getActivities());
	    for (Activity sequenceActivityNotInitialized : sequenceActivities) {
		SequenceActivity sequenceActivity = (SequenceActivity) monitoringService
			.getActivityById(sequenceActivityNotInitialized.getActivityId());
		Set<Activity> childActivities = new TreeSet<Activity>(new ActivityOrderComparator());
		childActivities.addAll(sequenceActivity.getActivities());

		//add one by one in order to initialize all activities
		for (Activity childActivity : childActivities) {
		    Activity activityInit = monitoringService.getActivityById(childActivity.getActivityId());
		    sortedActivities.add(activityInit);
		}
	    }

	    // In case of complex activity (parallel, help or optional activity) add all its children activities.
	    // They will be sorted by orderId
	} else if (activity.isComplexActivity()) {
	    ComplexActivity complexActivity = (ComplexActivity) activity;
	    Set<Activity> childActivities = new TreeSet<Activity>(new ActivityOrderComparator());
	    childActivities.addAll(complexActivity.getActivities());

	    // add one by one in order to initialize all activities
	    for (Activity childActivity : childActivities) {
		Activity activityInit = monitoringService.getActivityById(childActivity.getActivityId());
		sortedActivities.add(activityInit);
	    }
	}

	Transition transitionFrom = activity.getTransitionFrom();
	if (transitionFrom != null) {
	    // query activity from DB as transition holds only proxied activity object
	    Long nextActivityId = transitionFrom.getToActivity().getActivityId();
	    Activity nextActivity = monitoringService.getActivityById(nextActivityId);

	    sortActivitiesByLearningDesignOrder(nextActivity, sortedActivities);
	}
    }

    private GroupingActivity getGroupingActivity(Lesson lesson) {
	Set<Activity> activities = new TreeSet<Activity>();

	/*
	 * Hibernate CGLIB is failing to load the first activity in the sequence as a ToolActivity for some mysterious
	 * reason Causes a ClassCastException when you try to cast it, even if it is a ToolActivity.
	 *
	 * THIS IS A HACK to retrieve the first tool activity manually so it can be cast as a ToolActivity - if it is
	 * one
	 */
	Activity firstActivity = monitoringService
		.getActivityById(lesson.getLearningDesign().getFirstActivity().getActivityId());
	activities.add(firstActivity);
	activities.addAll(lesson.getLearningDesign().getActivities());

	for (Activity activity : activities) {
	    if (activity instanceof GroupingActivity) {
		return (GroupingActivity) activity;
	    }
	}

	return null;
    }

    private void setupAvailableActivityTypes(HttpServletRequest request, List<Activity> activities) {
	
	//check if there is Scratchie activity. It's used only in case of LKC TBL monitoring, when all assessment are treated as AEs
	boolean isScratchieAvailable = false;
	for (Activity activity : activities) {
	    if (activity instanceof ToolActivity) {
		ToolActivity toolActivity = (ToolActivity) activity;
		String toolSignature = toolActivity.getTool().getToolSignature();
		if (CentralConstants.TOOL_SIGNATURE_SCRATCHIE.equals(toolSignature)) {
		    isScratchieAvailable = true;
		    break;
		}
	    }
	}

	boolean scratchiePassed = false;
	boolean iraPassed = false;
	String assessmentToolContentIds = "";
	String assessmentActivityTitles = "";
	for (Activity activity : activities) {
	    if (activity instanceof ToolActivity) {
		ToolActivity toolActivity = (ToolActivity) activity;
		String toolSignature = toolActivity.getTool().getToolSignature();
		Long toolContentId = toolActivity.getToolContentId();
		Long toolActivityId = toolActivity.getActivityId();
		String toolTitle = toolActivity.getTitle();

		//count only the first MCQ or Assessmnet as iRA
		if (!iraPassed && (CentralConstants.TOOL_SIGNATURE_MCQ.equals(toolSignature)
			|| isScratchieAvailable && CentralConstants.TOOL_SIGNATURE_ASSESSMENT.equals(toolSignature))) {
		    iraPassed = true;
		    if (CentralConstants.TOOL_SIGNATURE_MCQ.equals(toolSignature)) {
			request.setAttribute("isIraMcqAvailable", true);

		    } else {
			request.setAttribute("isIraAssessmentAvailable", true);
		    }
		    request.setAttribute("iraToolContentId", toolContentId);
		    request.setAttribute("iraToolActivityId", toolActivityId);

		    continue;
		}

		//aes are counted only after Scratchie activity, or for LKC TBL monitoring
		if ((scratchiePassed || !isScratchieAvailable) && CentralConstants.TOOL_SIGNATURE_ASSESSMENT.equals(toolSignature)) {
		    request.setAttribute("isAeAvailable", true);
		    //prepare assessment details to be passed to Assessment tool
		    assessmentToolContentIds += toolContentId + ",";
		    assessmentActivityTitles += toolTitle + "\\,";

		} else if (CentralConstants.TOOL_SIGNATURE_FORUM.equals(toolSignature)) {
		    request.setAttribute("isForumAvailable", true);
		    request.setAttribute("forumActivityId", toolActivityId);

		} else if (CentralConstants.TOOL_SIGNATURE_PEER_REVIEW.equals(toolSignature)) {
		    request.setAttribute("isPeerreviewAvailable", true);
		    request.setAttribute("peerreviewToolContentId", toolContentId);

		    //tRA is the first scratchie activity
		} else if (!scratchiePassed && CentralConstants.TOOL_SIGNATURE_SCRATCHIE.equals(toolSignature)) {
		    scratchiePassed = true;

		    request.setAttribute("isScratchieAvailable", true);
		    request.setAttribute("traToolContentId", toolContentId);
		    request.setAttribute("traToolActivityId", toolActivityId);
		}

		if (CentralConstants.TOOL_SIGNATURE_LEADERSELECTION.equals(toolSignature)) {
		    request.setAttribute("leaderselectionToolActivityId", toolActivityId);
		    request.setAttribute("leaderselectionToolContentId", toolContentId);
		}

	    } else if (activity instanceof GateActivity) {
		request.setAttribute("isGatesAvailable", true);
	    }
	}

	request.setAttribute("assessmentToolContentIds", assessmentToolContentIds);
	request.setAttribute("assessmentActivityTitles", assessmentActivityTitles);
    }

    private void initServices() {
	ServletContext servletContext = this.getServlet().getServletContext();
	WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	if (lessonService == null) {
	    lessonService = (ILessonService) ctx.getBean("lessonService");
	}

	if (monitoringService == null) {
	    monitoringService = (IMonitoringService) ctx.getBean("monitoringService");
	}

	if (coreToolService == null) {
	    coreToolService = (ILamsCoreToolService) ctx.getBean("lamsCoreToolService");
	}

	if (toolService == null) {
	    toolService = (ILamsToolService) ctx.getBean("lamsToolService");
	}

	if (activityDAO == null) {
	    activityDAO = (IActivityDAO) ctx.getBean("activityDAO");
	}

	if (gradebookService == null) {
	    gradebookService = (IGradebookService) ctx.getBean("gradebookService");
	}

    }

}
