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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $$Id$$ */	
package org.lamsfoundation.lams.learning.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.learning.service.ICoreLearnerService;
import org.lamsfoundation.lams.learning.service.LearnerServiceProxy;
import org.lamsfoundation.lams.learning.web.util.ActivityMapping;
import org.lamsfoundation.lams.learning.web.util.LearningWebUtil;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;



/** 
 * Action class to display an activity. This is used when Flash calls starts of the 
 * learning process. It is needed to put the learner progress and the activity in the request, 
 * on which LoadToolActivityAction relies. If you try to go straight to LoadToolActivityAction
 * then the data won't be in the request.
 * 
 * XDoclet definition:
 * 
 * ----------------XDoclet Tags-------------------- 
 * @struts:action path="/DisplayActivity" name="activityForm"
 *                validate="false" scope="request"
 * @struts:action-forward name="displayParallelActivity" path="/DisplayParallelActivity.do"
 * @struts:action-forward name="displayOptionsActivity" path="/DisplayOptionsActivity.do"
 * @struts:action-forward name="loadToolActivity" path="/LoadToolActivity.do"
 * @struts:action-forward name="parallelWait" path="/parallelWait.do"
 * @struts:action-forward name="lessonComplete" path="/lessonComplete.do"
 * 
 * 
 * ----------------XDoclet Tags--------------------
 */
public class DisplayActivityAction extends ActivityAction {
    
    //---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	private static Logger log = Logger.getLogger(DisplayActivityAction.class);
	
	/** 
	 * Gets an activity from the request (attribute) and forwards onto a
	 * display action using the ActionMappings class. If no activity is
	 * in request then use the current activity in learnerProgress.
	 */
	public ActionForward execute(ActionMapping mapping,
	                             ActionForm actionForm,
	                             HttpServletRequest request,
	                             HttpServletResponse response) 
	{
		ICoreLearnerService learnerService = getLearnerService();

		// Flash can only send the lessonID as that is all it has...
        Integer learnerId = LearningWebUtil.getUserId();
	    Long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID, true );
	    // hack until Flash changes its url - current sending progressId
	    if ( lessonId == null ) {
	    	lessonId = WebUtil.readLongParam(request,"progressId");
	    }
	    LearnerProgress learnerProgress = learnerService.getProgress(learnerId, lessonId);
	    
	    LearningWebUtil.putLearnerProgressInRequest(request, learnerProgress);
		
		ActivityMapping actionMappings = LearnerServiceProxy.getActivityMapping(getServlet().getServletContext());
		ActionForward forward =actionMappings.getProgressForward(learnerProgress,false,request,learnerService);
		setupProgressString(actionForm, request);
	
		if(log.isDebugEnabled())
		    log.debug(forward.toString());
		    
		return 	forward;
	}
}