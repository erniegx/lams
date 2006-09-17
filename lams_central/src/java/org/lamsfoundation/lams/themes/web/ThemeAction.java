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
 * ************************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.themes.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.themes.service.IThemeService;
import org.lamsfoundation.lams.themes.web.ThemeConstants;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.util.wddx.FlashMessage;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.lamsfoundation.lams.usermanagement.exception.UserException;
import org.lamsfoundation.lams.themes.exception.ThemeException;

import org.lamsfoundation.lams.util.MessageService;

/**
 * 
 * @author Mitchell Seaton
 * 
 * @struts.action name = "ThemeAction"
 * 				  path = "/theme"
 * 				  parameter = "method"
 * 				  validate = "false"
 *
 */
public class ThemeAction extends LamsDispatchAction {
	
	private static Logger log = Logger.getLogger(ThemeAction.class);

	/** If you want the output given as a jsp, set the request parameter "jspoutput" to 
     * some value other than an empty string (e.g. 1, true, 0, false, blah). 
     * If you want it returned as a stream (ie for Flash), do not define this parameter
     */  
	public static String USE_JSP_OUTPUT = "jspoutput";
	
	/** Id of theme to be retrieved from the db */
	public static final String THEME_ID_PARAMETER = "themeID";
	
	/** Id of user setting the new theme */
	public static final String USER_ID_PARAMETER = "userID";
		
	/** The type the theme should be set for i.e. flash or jsp */
	public static final String TYPE_PARAMETER = "type";
	public static final String HTML_TYPE = "html";
	public static final String FLASH_TYPE = "flash";
	
	
	
	/** for sending acknowledgment/error messages back to flash */
	private FlashMessage flashMessage;
	
	public IThemeService getThemeService(){
		WebApplicationContext webContext = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServlet().getServletContext());
		return (IThemeService) webContext.getBean(ThemeConstants.THEME_SERVICE_BEAN_NAME);		
	}
	
	
	/** Output the supplied WDDX packet. If the request parameter USE_JSP_OUTPUT
	 * is set, then it sets the session attribute "parameterName" to the wddx packet string.
	 * If  USE_JSP_OUTPUT is not set, then the packet is written out to the 
	 * request's PrintWriter.
	 *   
	 * @param mapping action mapping (for the forward to the success jsp)
	 * @param request needed to check the USE_JSP_OUTPUT parameter
	 * @param response to write out the wddx packet if not using the jsp
	 * @param wddxPacket wddxPacket or message to be sent/displayed
	 * @param parameterName session attribute to set if USE_JSP_OUTPUT is set
	 * @throws IOException
	 */
	private ActionForward outputPacket(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response,
	        		String wddxPacket, String parameterName) throws IOException {
	    String useJSP = WebUtil.readStrParam(request, USE_JSP_OUTPUT, true);
	    if ( useJSP != null && useJSP.length() >= 0 ) {
		    request.getSession().setAttribute(parameterName,wddxPacket);
		    return mapping.findForward("success");
	    } else {
	        PrintWriter writer = response.getWriter();
	        writer.println(wddxPacket);
	        return null;
	    }
	}
	
	
	/**
	 * Returns a string representing the requested theme in WDDX format
	 * 
	 * @return String The requested theme in WDDX format
	 * @throws Exception
	 */
	public ActionForward getTheme(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws ServletException, Exception{

		Long themeId = new Long(WebUtil.readLongParam(request,THEME_ID_PARAMETER));
		IThemeService themeService = getThemeService();
		String message = themeService.getTheme(themeId);
		request.getSession().setAttribute("message",message);
		return outputPacket(mapping, request, response, message, "message");
	}


	/**
	 * This method returns a list of all available themes in
	 * WDDX format. We need to work out if this should be restricted
	 * by user.
	 * 
	 * @return String The required information in WDDX format
	 * @throws IOException
	 */
	public ActionForward getThemes(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws ServletException, Exception{
	    
	    IThemeService themeService = getThemeService();
	    String message = themeService.getThemes();
	    request.getSession().setAttribute("message",message);
	    return outputPacket(mapping, request, response, message, "message");
	}
	
	/**
	 * WDDX Packet with an acknowledgement or error if the user cannot be updated 
	 * (e.g. if theme id is invalid).
	 * 
	 * @return String Acknowledgement or error in WDDX format
	 * @throws Exception
	 */
	public ActionForward setTheme(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws ServletException, Exception{
		
		Long themeId = new Long(WebUtil.readLongParam(request,THEME_ID_PARAMETER));
		Integer userId = new Integer(WebUtil.readIntParam(request,USER_ID_PARAMETER));
		String type = WebUtil.readStrParam(request, TYPE_PARAMETER, true);
		IThemeService themeService = getThemeService();
		MessageService messageService = themeService.getMessageService();
		
		flashMessage = null;
		
		try {
			if(type==null)
				flashMessage = themeService.setTheme(userId, themeId);
			else if(type.equals(FLASH_TYPE))
				flashMessage = themeService.setFlashTheme(userId, themeId);
			else if(type.equals(HTML_TYPE))
				flashMessage = themeService.setHtmlTheme(userId, themeId);
			else
				throw new ThemeException(messageService.getMessage(IThemeService.NO_SUCH_THEME_TYPE_KEY));
		} catch (ThemeException e) {
		     flashMessage = new FlashMessage("wddxPacket", e.getMessage(), FlashMessage.ERROR);
		} catch (UserException e) {
			flashMessage = new FlashMessage("wddxPacket", e.getMessage(), FlashMessage.ERROR);
		} catch ( Exception e) {
		     flashMessage = FlashMessage.getExceptionOccured("setTheme", e.getMessage());
		}
		request.getSession().setAttribute("message", flashMessage.serializeMessage());
		return outputPacket(mapping, request, response, flashMessage.serializeMessage(), "message");
		
	}
	
}
