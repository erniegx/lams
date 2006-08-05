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


package org.lamsfoundation.lams.tool.qa.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.qa.GeneralLearnerFlowDTO;
import org.lamsfoundation.lams.tool.qa.QaAppConstants;
import org.lamsfoundation.lams.tool.qa.QaApplicationException;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueUsr;
import org.lamsfoundation.lams.tool.qa.QaSession;
import org.lamsfoundation.lams.tool.qa.service.IQaService;
import org.lamsfoundation.lams.tool.qa.service.QaServiceProxy;
import org.lamsfoundation.lams.web.servlet.AbstractExportPortfolioServlet;
/**
 * <p> Enables exporting portfolio for teacher and learner modes. </p> 
 * 
 * @author Ozgur Demirtas
 */

public class ExportServlet  extends AbstractExportPortfolioServlet implements QaAppConstants{
	static Logger logger = Logger.getLogger(ExportServlet.class.getName());
	private static final long serialVersionUID = -1779093489007108143L;
	private final String FILENAME = "qa_main.html";
	
	
	public String doExport(HttpServletRequest request, HttpServletResponse response, String directoryName, Cookie[] cookies)
	{
	    logger.debug("dispathcing doExport");
	    //request.setAttribute(IS_PORTFOLIO_EXPORT, new Boolean(true).toString());
	    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
	    logger.debug("basePath:" + basePath);

		if (StringUtils.equals(mode,ToolAccessMode.LEARNER.toString())){
		    learner(request,response,directoryName,cookies);
		}else if (StringUtils.equals(mode,ToolAccessMode.TEACHER.toString())){
			teacher(request,response,directoryName,cookies);
		}

		writeResponseToFile(basePath+"/export/exportportfolio.jsp",directoryName,FILENAME,cookies);
		
		return FILENAME;
	}
    
	public void learner(HttpServletRequest request, HttpServletResponse response, String directoryName, Cookie[] cookies)
    {
	    logger.debug("starting learner mode...");
	    
    	IQaService qaService = QaServiceProxy.getQaService(getServletContext());
        
    	logger.debug("userID:" + userID);
    	logger.debug("toolSessionID:" + toolSessionID);
    	
        if (userID == null || toolSessionID == null)
        {
            String error = "Tool session Id or user Id is null. Unable to continue";
            logger.error(error);
            throw new QaApplicationException(error);
        }
        
        QaSession qaSession=qaService.retrieveQaSessionOrNullById(toolSessionID.longValue());
        
        QaQueUsr learner = qaService.getQaUserBySession(userID,qaSession.getUid());
        logger.debug("learner: " + learner);
        
        if (learner == null)
        {
            String error="The user with user id " + userID + " does not exist in this session or session may not exist.";
            logger.error(error);
            throw new QaApplicationException(error);
        }
        
        QaContent content=qaSession.getQaContent();
        logger.debug("content: " + content);
        logger.debug("content id: " + content.getQaContentId());
        
        if (content == null)
        {
            String error="The content for this activity has not been defined yet.";
            logger.error(error);
            throw new QaApplicationException(error);
        }

        logger.debug("calling learning mode toolSessionID:" + toolSessionID + " userID: " + userID );
    	QaMonitoringAction qaMonitoringAction= new QaMonitoringAction();
    	logger.debug("start refreshSummaryData for learner mode.");

    	GeneralLearnerFlowDTO generalLearnerFlowDTO= LearningUtil.buildGeneralLearnerFlowDTO(content);
	    logger.debug("generalLearnerFlowDTO: " + generalLearnerFlowDTO);
    	qaMonitoringAction.refreshSummaryData(request, content, qaService, true, true, toolSessionID.toString(), 
    	        userID.toString(), generalLearnerFlowDTO, false);
    	logger.debug("end refreshSummaryData for learner mode.");
    	
    	generalLearnerFlowDTO =(GeneralLearnerFlowDTO)request.getAttribute(GENERAL_LEARNER_FLOW_DTO);
    	logger.debug("final generalLearnerFlowDTO: " + generalLearnerFlowDTO);

    	logger.debug("for the special case of export portfolio we place generalLearnerFlowDTO into session scope");
    	request.getSession().setAttribute(GENERAL_LEARNER_FLOW_DTO, generalLearnerFlowDTO);

    	request.getSession().setAttribute(PORTFOLIO_EXPORT_MODE, "learner");
    	logger.debug("ending learner mode: ");
    }
    
    public void teacher(HttpServletRequest request, HttpServletResponse response, String directoryName, Cookie[] cookies)
    {
        logger.debug("starting teacher mode...");
        
        IQaService qaService = QaServiceProxy.getQaService(getServletContext());
       
        if (toolContentID==null)
        {
            String error="Tool Content Id is missing. Unable to continue";
            logger.error(error);
            throw new QaApplicationException(error);
        }

        QaContent content=qaService.loadQa(toolContentID.longValue());
        logger.debug("content: " + content);
        
        if (content == null)
        {
            String error="Data is missing from the database. Unable to Continue";
            logger.error(error);
            throw new QaApplicationException(error);
        }
		
        QaMonitoringAction qaMonitoringAction= new QaMonitoringAction();
        logger.debug("start refreshSummaryData for teacher mode.");

        GeneralLearnerFlowDTO generalLearnerFlowDTO= LearningUtil.buildGeneralLearnerFlowDTO(content);
	    logger.debug("generalLearnerFlowDTO: " + generalLearnerFlowDTO);
        qaMonitoringAction.refreshSummaryData(request, content, qaService, true, false, 
                null, null, generalLearnerFlowDTO, false);
        logger.debug("end refreshSummaryData for teacher mode.");
        logger.debug("teacher uses content id: " + content.getQaContentId());
        
    	generalLearnerFlowDTO =(GeneralLearnerFlowDTO)request.getAttribute(GENERAL_LEARNER_FLOW_DTO);
    	logger.debug("final generalLearnerFlowDTO: " + generalLearnerFlowDTO);
    	
    	logger.debug("for the special case of export portfolio we place generalLearnerFlowDTO into session scope");
    	request.getSession().setAttribute(GENERAL_LEARNER_FLOW_DTO, generalLearnerFlowDTO);

        request.getSession().setAttribute(PORTFOLIO_EXPORT_MODE, "teacher");
        logger.debug("ending teacher mode: ");
    }
}
