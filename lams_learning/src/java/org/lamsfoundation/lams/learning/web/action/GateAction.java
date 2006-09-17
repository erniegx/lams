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

/* $$Id$$ */	
package org.lamsfoundation.lams.learning.web.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.lamsfoundation.lams.learning.service.ICoreLearnerService;
import org.lamsfoundation.lams.learning.service.LearnerServiceException;
import org.lamsfoundation.lams.learning.service.LearnerServiceProxy;
import org.lamsfoundation.lams.learning.web.util.LearningWebUtil;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.PermissionGateActivity;
import org.lamsfoundation.lams.learningdesign.ScheduleGateActivity;
import org.lamsfoundation.lams.learningdesign.SynchGateActivity;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;


/**
 * <p>The action servlet that deals with gate activity. This class allows the 
 * learner to knock gate when they reach the gate. The knocking process will
 * be triggered by the lams progress engine in the first place. The learner
 * can also trigger the knocking process by clicking on the button on the 
 * waiting page.</p>
 * 
 * <p>Learner will progress to the next activity if the gate is open. Otherwise,
 * the learner should see the waiting page.</p>
 * 
 * <p>Has a special override key - if the parameter force is set and the 
 * lesson is a preview lesson, then the gate will be opened straight away. This
 * allows the author to see gate shut initially but override it and open it 
 * rather than being held up by the gate. </p>
 * 
 * @author Jacky Fang
 * @since  2005-4-7
 * @version 1.1
 * 
 * ----------------XDoclet Tags--------------------
 * 
 * @struts:action name="GateForm"
 * 				  path="/gate" 
 *                parameter="method" 
 *                validate="false"
 * 
 * @struts:action-forward name="permissionGate" path=".permissionGate"
 * @struts:action-forward name="scheduleGate" path=".scheduleGate"
 * @struts:action-forward name="synchGate" path=".synchGate"
 * ----------------XDoclet Tags--------------------
 */
public class GateAction extends LamsDispatchAction
{

    //---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	// private static Logger log = Logger.getLogger(GateAction.class);

    //---------------------------------------------------------------------
    // Class level constants - Struts forward
    //---------------------------------------------------------------------
    private static final String VIEW_PERMISSION_GATE = "permissionGate";
    private static final String VIEW_SCHEDULE_GATE = "scheduleGate";
    private static final String VIEW_SYNCH_GATE = "synchGate";
	
    /** Input parameter. Boolean value */
    public static final String PARAM_FORCE_GATE_OPEN  = "force";

    //---------------------------------------------------------------------
    // Struts Dispatch Method
    //---------------------------------------------------------------------    
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public ActionForward knockGate(ActionMapping mapping,
                                   ActionForm form,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        boolean forceGate = WebUtil.readBooleanParam(request,PARAM_FORCE_GATE_OPEN,false);
        Long activityId = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
        
        //initialize service object
        ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());
        Activity activity = learnerService.getActivity(activityId);
        Lesson lesson = learnerService.getLessonByActivity(activity);
        User learner = LearningWebUtil.getUser(learnerService);
        
        Integer totalNumActiveLearners =  learnerService.getCountActiveLearnersByLesson(lesson.getLessonId());
        //knock the gate
        boolean gateOpen = learnerService.knockGate(activityId,learner,forceGate);
        
        // if the gate is open, let the learner go to the next activity ( updating the cached learner progress on the way )
        // pass only the ids in to completeActivity, so that the service level looks up the objects.
        // if we reuse our cached entries, hibernate may throw session errors (if the objects are CGLIB entities).
        if(gateOpen)
        {
            String nextActivityUrl = learnerService.completeActivity(learner.getUserId(),activityId);
            response.sendRedirect(nextActivityUrl);
            return null;
        }
        //if the gate is closed, ask the learner to wait ( updating the cached learner progress on the way )
        else {
            return findViewByGateType(mapping, (DynaActionForm)form, activity, totalNumActiveLearners, lesson.isPreviewLesson());
        }
    }
	
    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------
    /**
     * Dispatch view the according to the gate type.
     * 
     * @param mapping An ActionMapping class that will be used by the Action 
     * class to tell the ActionServlet where to send the end-user.
     * @param gateForm The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param permissionGate the gate acitivty object
     * @param totalNumActiveLearners total number of active learners in the lesson (may not all be logged in)
     * @return An ActionForward class that will be returned to the ActionServlet 
     * 		   indicating where the user is to go next.
     */
    private ActionForward findViewByGateType(ActionMapping mapping, 
                                             DynaActionForm gateForm, 
                                             Activity gate,
                                             Integer totalNumActiveLearners,
                                             boolean isPreviewLesson)
    {
        //dispatch the view according to the type of the gate.
       	if ( gate != null ) {
       		gateForm.set("totalLearners",totalNumActiveLearners);
       		gateForm.set("previewLesson",isPreviewLesson);
       		gateForm.set("activityId",gate.getActivityId());
	        if(gate.isSynchGate())
	            return viewSynchGate(mapping,gateForm,(SynchGateActivity)gate);
	        else if(gate.isScheduleGate())
	            return viewScheduleGate(mapping,gateForm,(ScheduleGateActivity)gate);
	        else if(gate.isPermissionGate())
	            return viewPermissionGate(mapping,gateForm,(PermissionGateActivity)gate);
	        else
	            throw new LearnerServiceException("Invalid gate activity. " +
	            		"gate id ["+gate.getActivityId()+"] - the type ["+
	            		gate.getActivityTypeId()+"] is not a gate type");
      	} else {
    		throw new LearnerServiceException("Gate activity missing. " +
        		"gate id ["+gate.getActivityId()+"]");
    	}
    }

    /**
     * Set up the form attributes specific to the permission gate and navigate
     * to the permission gate view.
     * @param mapping An ActionMapping class that will be used by the Action 
     * class to tell the ActionServlet where to send the end-user.
     * @param gateForm The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param permissionGate the gate acitivty object
     * @return An ActionForward class that will be returned to the ActionServlet 
     * 		   indicating where the user is to go next.
     */
    private ActionForward viewPermissionGate(ActionMapping mapping,
                                             DynaActionForm gateForm,
                                             PermissionGateActivity permissionGate)
    {
        gateForm.set("gate",permissionGate);
        return mapping.findForward(VIEW_PERMISSION_GATE);
    }

    /**
     * Set up the form attributes specific to the schedule gate and navigate
     * to the schedule gate view.
     * 
     * @param mapping An ActionMapping class that will be used by the Action 
     * class to tell the ActionServlet where to send the end-user.
     * @param gateForm The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param permissionGate the gate acitivty object
     * @return An ActionForward class that will be returned to the ActionServlet 
     * 		   indicating where the user is to go next.
     */
    private ActionForward viewScheduleGate(ActionMapping mapping, 
                                           DynaActionForm gateForm,
                                           ScheduleGateActivity scheduleGate)
    {
        gateForm.set("gate",scheduleGate);
        gateForm.set("waitingLearners",new Integer(scheduleGate.getWaitingLearners().size()));
        gateForm.set("startingTime",scheduleGate.getGateStartDateTime());
        gateForm.set("endingTime",scheduleGate.getGateEndDateTime());
        
        return mapping.findForward(VIEW_SCHEDULE_GATE);
    }

    /**
     * Set up the form attributes specific to the synch gate and navigate
     * to the synch gate view.
     * 
     * @param mapping An ActionMapping class that will be used by the Action 
     * class to tell the ActionServlet where to send the end-user.
     * @param gateForm The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param permissionGate the gate acitivty object
     * @return An ActionForward class that will be returned to the ActionServlet 
     * 		   indicating where the user is to go next.
     */
    private ActionForward viewSynchGate(ActionMapping mapping,
                                        DynaActionForm gateForm,
                                        SynchGateActivity synchgate)
    {
        gateForm.set("gate",synchgate);
        gateForm.set("waitingLearners",new Integer(synchgate.getWaitingLearners().size()));
        return mapping.findForward(VIEW_SYNCH_GATE);
    }

}
