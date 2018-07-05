/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

package org.lamsfoundation.lams.tool.taskList.web.controller;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.learning.web.bean.ActivityPositionDTO;
import org.lamsfoundation.lams.learning.web.util.LearningWebUtil;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.taskList.TaskListConstants;
import org.lamsfoundation.lams.tool.taskList.dto.TasListItemDTO;
import org.lamsfoundation.lams.tool.taskList.model.TaskList;
import org.lamsfoundation.lams.tool.taskList.model.TaskListItem;
import org.lamsfoundation.lams.tool.taskList.model.TaskListItemAttachment;
import org.lamsfoundation.lams.tool.taskList.model.TaskListItemComment;
import org.lamsfoundation.lams.tool.taskList.model.TaskListSession;
import org.lamsfoundation.lams.tool.taskList.model.TaskListUser;
import org.lamsfoundation.lams.tool.taskList.service.ITaskListService;
import org.lamsfoundation.lams.tool.taskList.service.TaskListException;
import org.lamsfoundation.lams.tool.taskList.service.UploadTaskListFileException;
import org.lamsfoundation.lams.tool.taskList.util.TaskListItemAttachmentComparator;
import org.lamsfoundation.lams.tool.taskList.util.TaskListItemCommentComparator;
import org.lamsfoundation.lams.tool.taskList.util.TaskListItemComparator;
import org.lamsfoundation.lams.tool.taskList.web.form.ReflectionForm;
import org.lamsfoundation.lams.tool.taskList.web.form.TaskListItemForm;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.DateUtil;
import org.lamsfoundation.lams.util.FileValidatorUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Steve.Ni
 */

@Controller
@RequestMapping("/learning")
public class LearningController implements TaskListConstants{

    private static Logger log = Logger.getLogger(LearningController.class);

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    @Qualifier("lataskTaskListService")
    private ITaskListService taskListService;

    /**
     * Read taskList data from database and put them into HttpSession. It will redirect to init.do directly after this
     * method run successfully.
     *
     * This method will avoid read database again and lost un-saved resouce item lost when user "refresh page",
     *
     */
    @RequestMapping("/start")
    public String start(@ModelAttribute TaskListItemForm taskListItemForm, HttpServletRequest request) {

	// initial Session Map
	SessionMap<String, Object> sessionMap = new SessionMap<>();
	request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);

	// save toolContentID into HTTPSession
	ToolAccessMode mode = WebUtil.readToolAccessModeParam(request, AttributeNames.PARAM_MODE, true);

	Long sessionId = Long.valueOf(request.getParameter(TaskListConstants.PARAM_TOOL_SESSION_ID));

	request.setAttribute(TaskListConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
	request.setAttribute(AttributeNames.ATTR_MODE, mode);
	request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID, sessionId);

	// get back the taskList and item list and display them on page
	TaskListUser taskListUser = null;
	if (mode != null && mode.isTeacher()) {
	    // monitoring mode - user is specified in URL
	    // taskListUser may be null if the user was force completed.
	    taskListUser = getSpecifiedUser(taskListService, sessionId,
		    WebUtil.readIntParam(request, AttributeNames.PARAM_USER_ID, false));
	} else {
	    taskListUser = getCurrentUser(taskListService, sessionId);
	}

	TaskList taskList = taskListService.getTaskListBySessionId(sessionId);

	// Create set of TaskListItems besides this filtering out items added by users from other groups
	TreeSet<TaskListItem> items = new TreeSet<>(new TaskListItemComparator());
	if (mode.isLearner()) {

	    List<TaskListUser> grouppedUsers = taskListService.getUserListBySessionId(sessionId);
	    Set<TaskListItem> allTaskListItems = taskList.getTaskListItems();

	    for (TaskListItem item : allTaskListItems) {
		for (TaskListUser grouppedUser : grouppedUsers) {
		    if (item.isCreateByAuthor() || grouppedUser.getUserId().equals(item.getCreateBy().getUserId())) {
			items.add(item);
		    }
		}
	    }

	} else {
	    items.addAll(taskList.getTaskListItems());
	}

	// check whehter finish lock is on/off
	boolean lock = taskList.getLockWhenFinished() && taskListUser != null && taskListUser.isSessionFinished();

	// get notebook entry
	String entryText = new String();
	if (taskListUser != null) {
	    NotebookEntry notebookEntry = taskListService.getEntry(sessionId, CoreNotebookConstants.NOTEBOOK_TOOL,
		    TaskListConstants.TOOL_SIGNATURE, taskListUser.getUserId().intValue());
	    if (notebookEntry != null) {
		entryText = notebookEntry.getEntry();
	    }
	}

	// set complete flag for display purpose
	if (taskListUser != null) {
	    taskListService.retrieveComplete(items, taskListUser);
	}

	TreeSet<TasListItemDTO> itemDTOs = new TreeSet<>(new Comparator<TasListItemDTO>() {
	    @Override
	    public int compare(TasListItemDTO o1, TasListItemDTO o2) {
		if (o1 != null && o2 != null) {
		    return o1.getTaskListItem().getSequenceId() - o2.getTaskListItem().getSequenceId();
		} else if (o1 != null) {
		    return 1;
		} else {
		    return -1;
		}
	    }
	});

	boolean isPreviousTaskCompleted = true;
	for (TaskListItem item : items) {
	    TasListItemDTO itemDTO = new TasListItemDTO(item);

	    // checks if this item met all comment requirements
	    boolean isCommentRequirementsMet = true;
	    if (item.isCommentsRequired()) {
		isCommentRequirementsMet = false;
		Set<TaskListItemComment> comments = item.getComments();
		for (TaskListItemComment comment : comments) {
		    if (taskListUser.getUserId().equals(comment.getCreateBy().getUserId())) {
			isCommentRequirementsMet = true;
		    }
		}
	    }
	    itemDTO.setCommentRequirementsMet(isCommentRequirementsMet);

	    // checks if this item met all attachment requirements
	    boolean isAttachmentRequirementsMet = true;
	    if (item.isFilesRequired()) {
		isAttachmentRequirementsMet = false;
		Set<TaskListItemAttachment> attachments = item.getAttachments();
		for (TaskListItemAttachment attachment : attachments) {
		    if (taskListUser.getUserId().equals(attachment.getCreateBy().getUserId())) {
			isAttachmentRequirementsMet = true;
		    }
		}
	    }
	    itemDTO.setAttachmentRequirementsMet(isAttachmentRequirementsMet);

	    // checks if this item is allowed by its parent
	    boolean isAllowedByParent = true;
	    if (item.isChildTask()) {
		for (TaskListItem parentItem : items) {
		    if (parentItem.getTitle().equals(item.getParentTaskName()) && !parentItem.isComplete()) {
			isAllowedByParent = false;
		    }
		}
	    }
	    itemDTO.setAllowedByParent(isAllowedByParent);

	    // checks whether this TaskListItem shoud be displayed open or close
	    boolean isDisplayedOpen = true;
	    if ((item.getDescription() != null) && (item.getDescription().length() > 1000)) {
		isDisplayedOpen = false;
	    }
	    itemDTO.setDisplayedOpen(isDisplayedOpen);

	    // sets whether the previous TaskListItem was completed
	    itemDTO.setPreviousTaskCompleted(isPreviousTaskCompleted);
	    isPreviousTaskCompleted = item.isComplete();

	    // filter out comments and attachments which belong to another group
	    Set filteredComments = new TreeSet<>(new TaskListItemCommentComparator());
	    Set filteredAttachments = new TreeSet<>(new TaskListItemAttachmentComparator());
	    if (mode.isLearner()) {

		List<TaskListUser> grouppedUsers = taskListService.getUserListBySessionId(sessionId);
		Set<TaskListItemComment> comments = item.getComments();
		Set<TaskListItemAttachment> attachments = item.getAttachments();

		for (TaskListItemComment comment : comments) {
		    for (TaskListUser grouppedUser : grouppedUsers) {
			if (grouppedUser.getUserId().equals(comment.getCreateBy().getUserId())) {
			    filteredComments.add(comment);
			}
		    }
		}

		for (TaskListItemAttachment attachment : attachments) {
		    for (TaskListUser grouppedUser : grouppedUsers) {
			if (grouppedUser.getUserId().equals(attachment.getCreateBy().getUserId())) {
			    filteredAttachments.add(attachment);
			}
		    }
		}
	    } else {
		filteredComments = item.getComments();
		filteredAttachments = item.getAttachments();
	    }
	    itemDTO.setComments(filteredComments);
	    itemDTO.setAttachments(filteredAttachments);

	    itemDTOs.add(itemDTO);
	}

	// construct taskList dto field

	Integer numberCompletedTasks = taskListService.getNumTasksCompletedByUser(sessionId, taskListUser.getUserId());
	Integer minimumNumberTasks = taskList.getMinimumNumberTasks();
	if ((minimumNumberTasks - numberCompletedTasks) > 0) {
	    String MinimumNumberTasksStr = taskListService.getMessageService().getMessage(
		    "lable.learning.minimum.view.number", new Object[] { minimumNumberTasks, numberCompletedTasks });
	    taskList.setMinimumNumberTasksErrorStr(MinimumNumberTasksStr);
	}

	// basic information
	sessionMap.put(TaskListConstants.ATTR_TITLE, taskList.getTitle());
	sessionMap.put(TaskListConstants.ATTR_FINISH_LOCK, lock);
	sessionMap.put(TaskListConstants.ATTR_USER_FINISHED, taskListUser != null && taskListUser.isSessionFinished());
	sessionMap.put(TaskListConstants.ATTR_USER_VERIFIED_BY_MONITOR, taskListUser.isVerifiedByMonitor());
	sessionMap.put(AttributeNames.PARAM_TOOL_SESSION_ID, sessionId);
	sessionMap.put(AttributeNames.ATTR_MODE, mode);
	sessionMap.put(TaskListConstants.ATTR_TASK_LIST_ITEM_DTOS, itemDTOs);
	// reflection information
	sessionMap.put(TaskListConstants.ATTR_REFLECTION_ON, taskList.isReflectOnActivity());
	sessionMap.put(TaskListConstants.ATTR_REFLECTION_INSTRUCTION, taskList.getReflectInstructions());
	sessionMap.put(TaskListConstants.ATTR_REFLECTION_ENTRY, entryText);

	// add define later support
	if (taskList.isDefineLater()) {
	    return "pages/learning/definelater";
	}

	// set contentInUse flag to true!
	taskList.setContentInUse(true);
	taskList.setDefineLater(false);
	taskListService.saveOrUpdateTaskList(taskList);

	ActivityPositionDTO activityPosition = LearningWebUtil.putActivityPositionInRequestByToolSessionId(sessionId,
		request, applicationContext.getServletContext());
	sessionMap.put(AttributeNames.ATTR_ACTIVITY_POSITION, activityPosition);

	// check if there is submission deadline
	Date submissionDeadline = taskList.getSubmissionDeadline();
	if (submissionDeadline != null) {
	    HttpSession ss = SessionManager.getSession();
	    UserDTO learnerDto = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    TimeZone learnerTimeZone = learnerDto.getTimeZone();
	    Date tzSubmissionDeadline = DateUtil.convertToTimeZoneFromDefault(learnerTimeZone, submissionDeadline);
	    Date currentLearnerDate = DateUtil.convertToTimeZoneFromDefault(learnerTimeZone, new Date());
	    sessionMap.put(TaskListConstants.ATTR_SUBMISSION_DEADLINE, tzSubmissionDeadline);

	    // calculate whether deadline has passed, and if so forward to "submissionDeadline"
	    if (currentLearnerDate.after(tzSubmissionDeadline)) {
		return "pages/learning/submissionDeadline";
	    }

	}

	sessionMap.put(TaskListConstants.ATTR_TASKLIST, taskList);

	return "pages/learning/learning";
    }

    /**
     * Mark taskList item as complete status.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/completeItem")
    public String complete(HttpServletRequest request, HttpServletResponse response) {
	String mode = request.getParameter(AttributeNames.ATTR_MODE);
	String sessionMapID = request.getParameter(TaskListConstants.ATTR_SESSION_MAP_ID);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(sessionMapID);
	Long sessionId = (Long) sessionMap.get(TaskListConstants.ATTR_TOOL_SESSION_ID);

	doComplete(request);

//	ActionRedirect redirect = new ActionRedirect(mapping.findForwardConfig(TaskListConstants.SUCCESS));
//	redirect.addParameter(AttributeNames.ATTR_MODE, mode);
//	redirect.addParameter(AttributeNames.PARAM_TOOL_SESSION_ID, sessionId);
	return "redirect:/learning/start.do";
    }

    /**
     * Finish learning session.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/finish")
    public String finish(@ModelAttribute ReflectionForm reflectionForm, Errors errors, HttpServletRequest request, HttpServletResponse response) {

	// get back SessionMap
	String sessionMapID = request.getParameter(TaskListConstants.ATTR_SESSION_MAP_ID);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(sessionMapID);

	Long sessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);

	// auto run mode, when use finish the only one taskList item, mark it as complete then finish this activity as
	// well.
	String taskListItemUid = request.getParameter(TaskListConstants.PARAM_ITEM_UID);
	if (taskListItemUid != null) {
	    doComplete(request);
	    // NOTE:So far this flag is useless(31/08/2006).
	    // set flag, then finish page can know redir target is parent(AUTO_RUN) or self(normal)
	    request.setAttribute(TaskListConstants.ATTR_RUN_AUTO, true);
	} else {
	    request.setAttribute(TaskListConstants.ATTR_RUN_AUTO, false);
	}

	if (!validateBeforeFinish(request, sessionMapID, errors)) {
	    return "redirect:/";
	    // getInputForward
	}

	// get sessionId from HttpServletRequest
	String nextActivityUrl = null;
	try {
	    HttpSession ss = SessionManager.getSession();
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    Long userID = new Long(user.getUserID().longValue());

	    nextActivityUrl = taskListService.finishToolSession(sessionId, userID);
	    request.setAttribute(TaskListConstants.ATTR_NEXT_ACTIVITY_URL, nextActivityUrl);
	} catch (TaskListException e) {
	    log.error("Failed get next activity url:" + e.getMessage());
	}

	return "pages/learning/finish";
    }

    /**
     * Initial page for add taskList item (single file or URL).
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/addtask")
    public String addTask(@ModelAttribute TaskListItemForm taskListItemForm, HttpServletRequest request) {

	taskListItemForm.setMode(WebUtil.readStrParam(request, AttributeNames.ATTR_MODE));
	taskListItemForm.setSessionMapID(WebUtil.readStrParam(request, TaskListConstants.ATTR_SESSION_MAP_ID));
	return "pages/learning/parts/addtask";
    }

    /**
     * Save new user task into database.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/saveNewTask")
    public String saveNewTask(@ModelAttribute TaskListItemForm taskListItemForm, Errors errors,
	    HttpServletRequest request) {
	// get back SessionMap
	String sessionMapID = request.getParameter(TaskListConstants.ATTR_SESSION_MAP_ID);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(sessionMapID);

	validateTaskListItem(taskListItemForm, errors);

	if (errors.hasErrors()) {
	    return "pages/learning/parts/addtask";
	}

	// create a new TaskListItem
	TaskListItem item = new TaskListItem();
	Long sessionId = (Long) sessionMap.get(TaskListConstants.ATTR_TOOL_SESSION_ID);
	TaskListUser taskListUser = getCurrentUser(taskListService, sessionId);
	item.setTitle(taskListItemForm.getTitle());
	item.setDescription(taskListItemForm.getDescription());
	item.setCreateDate(new Timestamp(new Date().getTime()));
	item.setCreateByAuthor(false);
	item.setCreateBy(taskListUser);

	// setting SequenceId
	TaskList taskList = (TaskList) sessionMap.get(TaskListConstants.ATTR_TASKLIST);
	Set<TaskListItem> taskListItems = taskList.getTaskListItems();
	int maxSeq = 0;
	for (TaskListItem dbItem : taskListItems) {
	    if (dbItem.getSequenceId() > maxSeq) {
		maxSeq = dbItem.getSequenceId();
	    }
	}
	maxSeq++;
	item.setSequenceId(maxSeq);
	taskListItems.add(item);
	taskListService.saveOrUpdateTaskList(taskList);

	// redirect
	String mode = request.getParameter(AttributeNames.ATTR_MODE);
	request.setAttribute(AttributeNames.ATTR_MODE, mode);
	request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID, sessionId);
	return "pages/learning/start";
    }

    /**
     * Adds new user commment.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/addNewComment")
    public String addNewComment(@ModelAttribute TaskListItemForm taskListItemForm, HttpServletRequest request) {

	String mode = request.getParameter(AttributeNames.ATTR_MODE);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(taskListItemForm.getSessionMapID());
	request.setAttribute(TaskListConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
	Long sessionId = (Long) sessionMap.get(TaskListConstants.ATTR_TOOL_SESSION_ID);

	boolean isTestHarness = Boolean.valueOf(request.getParameter("testHarness"));
	String commentMessage = isTestHarness ? request.getParameter("comment__textarea")
		: taskListItemForm.getComment();
	if (commentMessage == null || StringUtils.isBlank(commentMessage)) {
	    return "pages/learning/learning";
	}

	TaskListItemComment comment = new TaskListItemComment();
	comment.setComment(commentMessage);
	UserDTO user = (UserDTO) SessionManager.getSession().getAttribute(AttributeNames.USER);
	TaskListUser taskListUser = taskListService.getUserByIDAndSession(new Long(user.getUserID().intValue()),
		sessionId);
	comment.setCreateBy(taskListUser);
	comment.setCreateDate(new Timestamp(new Date().getTime()));

	// persist TaskListItem changes in DB
	Long itemUid = new Long(request.getParameter(TaskListConstants.PARAM_ITEM_UID));
	TaskListItem dbItem = taskListService.getTaskListItemByUid(itemUid);
	Set<TaskListItemComment> dbComments = dbItem.getComments();
	dbComments.add(comment);
	taskListService.saveOrUpdateTaskListItem(dbItem);

	// to make available new changes be visible in jsp page
	sessionMap.put(TaskListConstants.ATTR_TASK_LIST_ITEM, dbItem);

	// form.reset(mapping, request);

//	ActionRedirect redirect = new ActionRedirect(mapping.findForwardConfig(TaskListConstants.SUCCESS));
//	redirect.addParameter(AttributeNames.ATTR_MODE, mode);
//	redirect.addParameter(AttributeNames.PARAM_TOOL_SESSION_ID, sessionId);
	return "redirect:/learning/start.do";
    }

    /**
     * Uploads specified file to repository and associates it with current TaskListItem.
     *
     * @param mapping
     * @param form
     * @param type
     * @param request
     * @return
     * @throws UploadTaskListFileException
     */
    @RequestMapping("/uploadFile")
    public String uploadFile(@ModelAttribute TaskListItemForm taskListItemForm, Errors errors,
	    HttpServletRequest request) throws UploadTaskListFileException {

	String mode = request.getParameter(AttributeNames.ATTR_MODE);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(taskListItemForm.getSessionMapID());
	request.setAttribute(TaskListConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
	Long sessionId = (Long) sessionMap.get(TaskListConstants.ATTR_TOOL_SESSION_ID);

	FormFile file = taskListItemForm.getUploadedFile();

	if (file == null || StringUtils.isBlank(file.getFileName())) {
	    return "pages/learning/learning";
	}

	// validate file size
	FileValidatorUtil.validateFileSize(file, false, errors);
	if (errors.hasErrors()) {
	    return "pages/learning/learning";
	}

	// upload to repository
	UserDTO user = (UserDTO) SessionManager.getSession().getAttribute(AttributeNames.USER);
	TaskListUser taskListUser = taskListService.getUserByIDAndSession(new Long(user.getUserID().intValue()),
		sessionId);
	TaskListItemAttachment att = taskListService.uploadTaskListItemFile(file, taskListUser);

	// persist TaskListItem changes in DB
	Long itemUid = new Long(request.getParameter(TaskListConstants.PARAM_ITEM_UID));
	TaskListItem dbItem = taskListService.getTaskListItemByUid(itemUid);
	Set<TaskListItemAttachment> dbAttachments = dbItem.getAttachments();
	dbAttachments.add(att);
	taskListService.saveOrUpdateTaskListItem(dbItem);

	// to make available new changes be visible in jsp page
	sessionMap.put(TaskListConstants.ATTR_TASK_LIST_ITEM, dbItem);

	// form.reset(mapping, request);

//	ActionRedirect redirect = new ActionRedirect(mapping.findForwardConfig(TaskListConstants.SUCCESS));
//	redirect.addParameter(AttributeNames.ATTR_MODE, mode);
//	redirect.addParameter(AttributeNames.PARAM_TOOL_SESSION_ID, sessionId);
	return "redirect:/learning/start.do";
    }

    /**
     * Display empty reflection form.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/newReflection")
    public String newReflection(@ModelAttribute ReflectionForm reflectionForm, HttpServletRequest request,
	    Errors errors, HttpServletResponse response) {

	// get session value
	String sessionMapID = WebUtil.readStrParam(request, TaskListConstants.ATTR_SESSION_MAP_ID);

	if (!validateBeforeFinish(request, sessionMapID, errors)) {
	    return "pages/learning/learning";
	}

	HttpSession ss = SessionManager.getSession();
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);

	reflectionForm.setUserID(user.getUserID());
	reflectionForm.setSessionMapID(sessionMapID);

	// get the existing reflection entry

	SessionMap<String, Object> map = (SessionMap<String, Object>) request.getSession().getAttribute(sessionMapID);
	Long toolSessionID = (Long) map.get(AttributeNames.PARAM_TOOL_SESSION_ID);
	NotebookEntry entry = taskListService.getEntry(toolSessionID, CoreNotebookConstants.NOTEBOOK_TOOL,
		TaskListConstants.TOOL_SIGNATURE, user.getUserID());

	if (entry != null) {
	    reflectionForm.setEntryText(entry.getEntry());
	}

	return "pages/learning/parts/notebook";
    }

    /**
     * Submit reflection form input database.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/submitReflection")
    public String submitReflection(@ModelAttribute ReflectionForm reflectionForm, Errors errors,
	    HttpServletRequest request, HttpServletResponse response) {

	Integer userId = reflectionForm.getUserID();

	String sessionMapID = WebUtil.readStrParam(request, TaskListConstants.ATTR_SESSION_MAP_ID);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(sessionMapID);
	Long sessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);

	// check for existing notebook entry
	NotebookEntry entry = taskListService.getEntry(sessionId, CoreNotebookConstants.NOTEBOOK_TOOL,
		TaskListConstants.TOOL_SIGNATURE, userId);

	if (entry == null) {
	    // create new entry
	    taskListService.createNotebookEntry(sessionId, CoreNotebookConstants.NOTEBOOK_TOOL,
		    TaskListConstants.TOOL_SIGNATURE, userId, reflectionForm.getEntryText());
	} else {
	    // update existing entry
	    entry.setEntry(reflectionForm.getEntryText());
	    entry.setLastModified(new Date());
	    taskListService.updateEntry(entry);
	}

	return finish(reflectionForm, errors, request, response);
    }

    // *************************************************************************************
    // Private method
    // *************************************************************************************

    private TaskListUser getCurrentUser(ITaskListService service, Long sessionId) {
	// try to get form system session
	HttpSession ss = SessionManager.getSession();
	// get back login user DTO
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	TaskListUser taskListUser = service.getUserByIDAndSession(new Long(user.getUserID().intValue()), sessionId);

	if (taskListUser == null) {
	    TaskListSession session = service.getSessionBySessionId(sessionId);
	    taskListUser = new TaskListUser(user, session);
	    service.createUser(taskListUser);
	}
	return taskListUser;
    }

    private TaskListUser getSpecifiedUser(ITaskListService service, Long sessionId, Integer userId) {
	TaskListUser taskListUser = service.getUserByIDAndSession(new Long(userId.intValue()), sessionId);
	if (taskListUser == null) {
	    log.error("Unable to find specified user for taskList activity. Screens are likely to fail. SessionId="
		    + sessionId + " UserId=" + userId);
	}
	return taskListUser;
    }

    /**
     * @param itemForm
     * @return
     */
    private void validateTaskListItem(TaskListItemForm itemForm, Errors errors) {
	if (StringUtils.isBlank(itemForm.getTitle())) {
	    errors.reject(TaskListConstants.ERROR_MSG_TITLE_BLANK);
	}
    }

    private boolean validateBeforeFinish(HttpServletRequest request, String sessionMapID, Errors errors) {
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(sessionMapID);
	Long sessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);

	HttpSession ss = SessionManager.getSession();
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	Long userID = new Long(user.getUserID().longValue());

	int numberCompletedTasks = taskListService.getNumTasksCompletedByUser(sessionId, userID);
	int minimumNumberTasks = taskListService.getTaskListBySessionId(sessionId).getMinimumNumberTasks();
	// if current user view less than reqired view count number, then just return error message.
	if ((minimumNumberTasks - numberCompletedTasks) > 0) {
	    errors.reject("lable.learning.minimum.view.number",
		    new Object[] { minimumNumberTasks, numberCompletedTasks }, null);
	    return false;
	}

	return true;
    }

    /**
     * Set complete flag for given taskList item.
     *
     * @param request
     * @param sessionId
     */
    private void doComplete(HttpServletRequest request) {
	// get back sessionMap
	String sessionMapID = request.getParameter(TaskListConstants.ATTR_SESSION_MAP_ID);
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);

	Long taskListItemUid = new Long(request.getParameter(TaskListConstants.PARAM_ITEM_UID));
	HttpSession ss = SessionManager.getSession();
	// get back login user DTO
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);

	Long sessionId = (Long) sessionMap.get(TaskListConstants.ATTR_TOOL_SESSION_ID);
	taskListService.setItemComplete(taskListItemUid, new Long(user.getUserID().intValue()), sessionId);
	sessionMapID = "4";
    }

}