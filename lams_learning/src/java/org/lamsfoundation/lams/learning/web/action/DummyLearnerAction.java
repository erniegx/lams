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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.learning.service.ICoreLearnerService;
import org.lamsfoundation.lams.learning.service.LearnerServiceProxy;
import org.lamsfoundation.lams.learning.web.util.LearningWebUtil;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.lesson.dto.LessonDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;


/** 
 * 
 * <p>The action servlet that interacts with the dummy learner interface. Based
 * on LearnerAction. To be removed when the Flash interface is built.</p>
 * 
 * ----------------XDoclet Tags--------------------
 * 
 * @struts:action path="/dummylearner" 
 *                parameter="method" 
 *                validate="false"
 * @struts:action-forward name="controlActivity" path="/dummyControlFrame.jsp"
 * @struts:action-forward name="displayActivity" path="/DisplayActivity.do"
 * 
 * ----------------XDoclet Tags--------------------
 * 
 */
public class DummyLearnerAction extends LamsDispatchAction 
{
    //---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	private static Logger log = Logger.getLogger(DummyLearnerAction.class);
	
    //---------------------------------------------------------------------
    // Class level constants - Struts forward
    //---------------------------------------------------------------------
    private static final String CONTROL_ACTIVITY = "controlActivity";
    private static final String DISPLAY_ACTIVITY = "displayActivity";
   
    // Session attributes
    private static final String PARAM_LESSONS = "lessons";
    
    /**
     * <p>The Struts dispatch method that retrieves all active lessons for a 
     * requested user.</p>
     * 
     * @param mapping An ActionMapping class that will be used by the Action class to tell
     * the ActionServlet where to send the end-user.
     *
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward getActiveLessons(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        //initialize service object
        ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());

        //get learner.
        Integer learner = LearningWebUtil.getUserId();
        if(log.isDebugEnabled())
            log.debug("Getting active lessons for leaner:["+learner+"]");

        LessonDTO [] lessons = learnerService.getActiveLessonsFor(learner);
        request.getSession().setAttribute(PARAM_LESSONS, lessons); 
        
        return mapping.findForward(CONTROL_ACTIVITY);
    }


    /**
     * <p>The structs dispatch action that joins a learner into a lesson. The
     * learner could either start a lesson or resume a lesson.</p>
     * 
     * @param mapping An ActionMapping class that will be used by the Action class to tell
     * the ActionServlet where to send the end-user.
     * 
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * 
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward joinLesson(ActionMapping mapping,
                                    ActionForm form,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        //initialize service object
        ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());

        //get user and lesson based on request.
        Integer learner = LearningWebUtil.getUserId();
        long lessonID = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);

        
        if(log.isDebugEnabled())
            log.debug("The learner ["+learner+"] is joining the lesson ["+lessonID+"]");

        //join user to the lesson on the server
        LearnerProgress learnerProgress = learnerService.joinLesson(learner,lessonID);
        
        if(log.isDebugEnabled())
            log.debug("The learner ["+learner+"] joined lesson. The"
                      +"progress data is:"+learnerProgress.toString());
        
        LearningWebUtil.putLearnerProgressInRequest(request, learnerProgress);
        return mapping.findForward(DISPLAY_ACTIVITY);
    }
    
    

}