/***************************************************************************
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
 * ***********************************************************************/

/**
 * 
 * <p>Starts up the Define Later module. 
 *  It reuses majority of the functionality from existing authoring module.
 * </p>
 * 
 * @author Ozgur Demirtas
 * 
*/
package org.lamsfoundation.lams.tool.vote.web;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;
import org.lamsfoundation.lams.tool.vote.VoteApplicationException;
import org.lamsfoundation.lams.tool.vote.VoteUtils;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;
import org.lamsfoundation.lams.tool.vote.service.VoteServiceProxy;


public class VoteDLStarterAction extends Action implements VoteAppConstants {
	static Logger logger = Logger.getLogger(VoteDLStarterAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
  								throws IOException, ServletException, VoteApplicationException {
		VoteUtils.cleanUpSessionAbsolute(request);
		logger.debug("init defineLater mode. removed attributes...");
		
		VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
		
		IVoteService voteService = VoteServiceProxy.getVoteService(getServlet().getServletContext());
		logger.debug("voteService: " + voteService);
		//request.getSession().setAttribute(TOOL_SERVICE, voteService);
		voteAuthoringForm.setVoteService(voteService);
		
		VoteStarterAction voteStarterAction= new VoteStarterAction();
	    return voteStarterAction.executeDefineLater(mapping, voteAuthoringForm, request, response);
	}
}
