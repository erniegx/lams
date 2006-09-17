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

package org.lamsfoundation.lams.tool.sbmt.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.sbmt.SubmitFilesContent;
import org.lamsfoundation.lams.tool.sbmt.SubmitFilesSession;
import org.lamsfoundation.lams.tool.sbmt.SubmitUser;
import org.lamsfoundation.lams.tool.sbmt.dto.AuthoringDTO;
import org.lamsfoundation.lams.tool.sbmt.dto.FileDetailsDTO;
import org.lamsfoundation.lams.tool.sbmt.dto.SubmitUserDTO;
import org.lamsfoundation.lams.tool.sbmt.dto.SessionDTO;
import org.lamsfoundation.lams.tool.sbmt.dto.StatisticDTO;
import org.lamsfoundation.lams.tool.sbmt.service.ISubmitFilesService;
import org.lamsfoundation.lams.tool.sbmt.service.SubmitFilesServiceProxy;
import org.lamsfoundation.lams.tool.sbmt.util.SbmtConstants;
import org.lamsfoundation.lams.tool.sbmt.util.SbmtWebUtils;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * @author Manpreet Minhas
 * @struts.action
 * 				path="/monitoring"
 * 				parameter="method"
 * 				scope="request"
 * 				validate="false"
 * 				name="SbmtMonitoringForm" 				
 * 
 * @struts.action-forward name="listMark" path="/monitoring/mark/mark.jsp"
 * @struts.action-forward name="updateMark" path="/monitoring/mark/updatemark.jsp"
 * @struts.action-forward name="listAllMarks" path="/monitoring/mark/allmarks.jsp"
 * 
 * @struts.action-forward name="success" path="/monitoring/monitoring.jsp"
 * 
 * @struts.action-forward name="statistic" path="/monitoring/parts/statisticpart.jsp"
 * 
 * @struts.action-forward name="viewReflect" path="/monitoring/notebook.jsp"
 */
public class MonitoringAction extends LamsDispatchAction {
	
	public ISubmitFilesService submitFilesService;
	
    
	private class SessionComparator implements Comparator<SessionDTO>{
		public int compare(SessionDTO o1, SessionDTO o2) {
			if(o1 != null && o2 != null){
				return o1.getSessionName().compareTo(o2.getSessionName());
			}else if(o1 != null)
				return 1;
			else
				return -1;
		}
	}
    /**
     * Default ActionForward for Monitor
     */
    public ActionForward unspecified(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
    {
    	Long contentID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_CONTENT_ID));
    	submitFilesService = getSubmitFilesService();
    	
//    	List userList = submitFilesService.getUsers(sessionID);
        List submitFilesSessionList = submitFilesService.getSubmitFilesSessionByContentID(contentID);
        summary(request, submitFilesSessionList);
		statistic(request, submitFilesSessionList);
		
		//instruction
		SubmitFilesContent persistContent = submitFilesService.getSubmitFilesContent(contentID);
		//if this content does not exist, then reset the contentID to current value to keep it on HTML page.
		persistContent.setContentID(contentID); 
		
		AuthoringDTO authorDto = new AuthoringDTO(persistContent);
		request.setAttribute(SbmtConstants.AUTHORING_DTO,authorDto);
		request.setAttribute(SbmtConstants.PAGE_EDITABLE, new Boolean(SbmtWebUtils.isSbmtEditable(persistContent)));
		
		return mapping.findForward("success");
    }
    /**
     * AJAX call to refresh statisitic page.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward doStatistic(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
    {
    	Long contentID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_CONTENT_ID));
    	submitFilesService = getSubmitFilesService();
    	
//    	List userList = submitFilesService.getUsers(sessionID);
        List submitFilesSessionList = submitFilesService.getSubmitFilesSessionByContentID(contentID);
		statistic(request, submitFilesSessionList);
		
		return mapping.findForward("statistic");
		
    }
	/**
	 * Release mark
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward releaseMarks(ActionMapping mapping,
			   ActionForm form,
			   HttpServletRequest request,
			   HttpServletResponse response){

		//get service then update report table
		submitFilesService = getSubmitFilesService();
		Long sessionID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		submitFilesService.releaseMarksForSession(sessionID);
		try {
			PrintWriter out = response.getWriter();
			SubmitFilesSession session = submitFilesService.getSessionById(sessionID);
			String sessionName = "";
			if(session != null)
				sessionName = session.getSessionName();
			out.write(getMessageService().getMessage("msg.mark.released",new String[]{sessionName}));
			out.flush();
		} catch (IOException e) {
		}
		
		return null;
	}
	/**
	 * Download submit file marks by MS Excel file format.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward downloadMarks(ActionMapping mapping,
			   ActionForm form,
			   HttpServletRequest request,
			   HttpServletResponse response){
		
		Long sessionID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		submitFilesService = getSubmitFilesService();
		//return FileDetailsDTO list according to the given sessionID
		Map userFilesMap = submitFilesService.getFilesUploadedBySession(sessionID);
		//construct Excel file format and download
		String errors = null;
		try {
			//create an empty excel file
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Marks");
			sheet.setColumnWidth((short)0,(short)5000);
			HSSFRow row,row1=null,row2=null,row3=null,row4=null;
			HSSFCell cell;
			Iterator iter = userFilesMap.values().iterator();
			Iterator dtoIter; 
			boolean first = true;
			int idx = 0;
			int fileCount = 0;
			while(iter.hasNext()){
				List list = (List) iter.next();
				dtoIter = list.iterator();
				first = true;
				
				while(dtoIter.hasNext()){
					FileDetailsDTO dto = (FileDetailsDTO) dtoIter.next();
					if(first){
						row = sheet.createRow(idx++);
						cell = row.createCell((short) 0);
						cell.setCellValue(dto.getOwner().getFirstName()+" "+dto.getOwner().getLastName());
						sheet.addMergedRegion(new Region(idx-1,(short)0,idx-1, (short)1));
						first = false;
						row1 = sheet.createRow(idx+1);
						cell = row1.createCell((short) 0);
						cell.setCellValue("File name");
						row2 = sheet.createRow(idx+2);
						cell = row2.createCell((short) 0);
						cell.setCellValue("File description");
						row3 = sheet.createRow(idx+3);
						cell = row3.createCell((short) 0);
						cell.setCellValue("Marks");
						row4 = sheet.createRow(idx+4);
						cell = row4.createCell((short) 0);
						cell.setCellValue("Comments");
						idx += 6;
						fileCount = 0;
					}
					++fileCount;
					sheet.setColumnWidth((short)fileCount,(short)8000);
					cell = row1.createCell((short) fileCount);
					cell.setCellValue(dto.getFilePath());
					
					cell = row2.createCell((short) fileCount);
					cell.setCellValue(dto.getFileDescription());
					
					cell = row3.createCell((short) fileCount);
					if(dto.getMarks() != null){
						String marks = "";
						try {
							NumberFormat format = NumberFormat.getInstance();
							format.setMaximumFractionDigits(1);
							marks = format.format(NumberUtils.createFloat(dto.getMarks()));
						} catch (Exception e) {
						}
						cell.setCellValue(marks);
					}else
						cell.setCellValue("");
					
					cell = row4.createCell((short) fileCount);
					cell.setCellValue(dto.getComments());
				}
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			//construct download file response header
			String fileName = "marks" + sessionID+".xls";
			String mineType = "application/vnd.ms-excel";
			String header = "attachment; filename=\"" + fileName + "\";";
			response.setContentType(mineType);
			response.setHeader("Content-Disposition",header);

			byte[] data = bos.toByteArray();
			response.getOutputStream().write(data,0,data.length);
			response.getOutputStream().flush();
		} catch (Exception e) {
			log.error(e);
			errors =new ActionMessage("monitoring.download.error",e.toString()).toString();
		}

		if(errors != null){
			try {
				PrintWriter out = response.getWriter();
				out.write(errors);
				out.flush();
			} catch (IOException e) {
			}
		}
			
		return null;
	}
	//**********************************************************
	// Mark udpate/view methods
	//**********************************************************
	/**
	 * Display special user's marks information. 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward listMark(ActionMapping mapping,
							   ActionForm form,
							   HttpServletRequest request,
							   HttpServletResponse response){
		Long sessionID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		Integer userID = WebUtil.readIntParam(request,"userID");

		submitFilesService = getSubmitFilesService();
		//return FileDetailsDTO list according to the given userID and sessionID
		List files = submitFilesService.getFilesUploadedByUser(userID,sessionID);
		
		request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID,sessionID);
		request.setAttribute("report",files);
		return mapping.findForward("listMark");
	}
	/**
	 * Display update mark initial page.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward newMark(ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response){
		
		Long sessionID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		Long detailID = new Long(WebUtil.readLongParam(request,"detailID"));
		String updateMode = request.getParameter("updateMode");
		
		submitFilesService = getSubmitFilesService();
		
		List report = new ArrayList<FileDetailsDTO>();
		report.add(submitFilesService.getFileDetails(detailID));
		
		request.setAttribute("report",report);
		request.setAttribute("updateMode", updateMode);
		request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID,sessionID);
		
		return mapping.findForward("updateMark");
	}



	/**
	 * Update mark.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward updateMark(ActionMapping mapping,
							   ActionForm form,
							   HttpServletRequest request,
							   HttpServletResponse response){
		Long sessionID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		Integer userID = WebUtil.readIntParam(request,"userID");
		Long detailID = new Long(WebUtil.readLongParam(request,"detailID"));
		String updateMode = request.getParameter("updateMode");
		Long reportID= new Long(WebUtil.readLongParam(request,"reportID"));
		
		ActionMessages errors = new ActionMessages();  
		//check whether the mark is validate
		String markStr = request.getParameter("marks");
		Long marks = null;
		try {
			marks = Long.parseLong(markStr);
		} catch (Exception e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("errors.mark.invalid.number"));
		}
		
		String comments = WebUtil.readStrParam(request,"comments",true);
		if(!errors.isEmpty()){
			submitFilesService = getSubmitFilesService();
			List report = new ArrayList<FileDetailsDTO>();
			FileDetailsDTO fileDetail = submitFilesService.getFileDetails(detailID);
			//echo back the input, even they are wrong.
			fileDetail.setComments(comments);
			fileDetail.setMarks(markStr);
			report.add(fileDetail);
			
			request.setAttribute("report",report);
			request.setAttribute("updateMode", updateMode);
			request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID,sessionID);
			
			
			saveErrors(request,errors);
			return mapping.findForward("updateMark");
		}
		
		//get service then update report table
		submitFilesService = getSubmitFilesService();
		
		submitFilesService.updateMarks(reportID,marks,comments);
		
		request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID,sessionID);
		if(StringUtils.equals(updateMode, "listMark")){
			List report = submitFilesService.getFilesUploadedByUser(userID,sessionID);
			request.setAttribute("report",report);
			return mapping.findForward("listMark");
		}else{
			Map report = submitFilesService.getFilesUploadedBySession(sessionID);
			request.setAttribute("reports",report);
			return mapping.findForward("listAllMarks");
		}
	}
	/**
	 * View mark of all learner from same tool content ID. 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward listAllMarks(ActionMapping mapping,
			   ActionForm form,
			   HttpServletRequest request,
			   HttpServletResponse response){
		
		Long sessionID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		submitFilesService = getSubmitFilesService();
		//return FileDetailsDTO list according to the given sessionID
		Map userFilesMap = submitFilesService.getFilesUploadedBySession(sessionID);
		request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID,sessionID);
//		request.setAttribute("user",submitFilesService.getUserDetails(userID));
		request.setAttribute("reports",userFilesMap);
		
		return mapping.findForward("listAllMarks");

	}	
	//**********************************************************
	// Private methods
	//**********************************************************
	
	private ISubmitFilesService getSubmitFilesService(){
		return SubmitFilesServiceProxy
			   .getSubmitFilesService(this.getServlet()
			   .getServletContext());
	}
	
	/**
	 * Return ResourceService bean.
	 */
	private MessageService getMessageService() {
	      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
	      return (MessageService) wac.getBean("sbmtMessageService");
	}
	/**
	 * Save file mark information into HttpRequest
	 * @param request
	 * @param sessionID
	 * @param userID
	 * @param detailID
	 * @param updateMode
	 */
	private void setMarkPage(HttpServletRequest request, Long sessionID, Long userID, Long detailID, String updateMode) {

	}
	/**
	 * Save statistic information into request
	 * @param request
	 * @param submitFilesSessionList
	 */
	private void statistic(HttpServletRequest request, List submitFilesSessionList) {
		Iterator it;
		Map<SessionDTO, StatisticDTO> sessionStatisticMap = new TreeMap<SessionDTO, StatisticDTO>(this.new SessionComparator());

		// build a map with all users in the submitFilesSessionList
		it = submitFilesSessionList.iterator();
		while (it.hasNext()) {
			
			SubmitFilesSession sfs = (SubmitFilesSession) it.next();
			Long sessionID = sfs.getSessionID();
			String sessionName = sfs.getSessionName();
				
			//return FileDetailsDTO list according to the given sessionID
			Map userFilesMap = submitFilesService.getFilesUploadedBySession(sessionID);
			Iterator iter = userFilesMap.values().iterator();
			Iterator dtoIter; 
			int notMarkedCount = 0;
			int markedCount = 0;
			while(iter.hasNext()){
				List list = (List) iter.next();
				dtoIter = list.iterator();
				while(dtoIter.hasNext()){
					FileDetailsDTO dto = (FileDetailsDTO) dtoIter.next();
					if(dto.getMarks() == null)
						notMarkedCount++;
					else
						markedCount++;
				}
			}
			StatisticDTO statisticDto = new StatisticDTO();
			SessionDTO sessionDto = new SessionDTO();
			statisticDto.setMarkedCount(markedCount);
			statisticDto.setNotMarkedCount(notMarkedCount);
			statisticDto.setTotalUploadedFiles(markedCount+notMarkedCount);
			sessionDto.setSessionID(sessionID);
			sessionDto.setSessionName(sessionName);
			sessionStatisticMap.put(sessionDto,statisticDto);
		}

		request.setAttribute("statisticList",sessionStatisticMap);
	}
	/**
	 * Save Summary information into HttpRequest.
	 * @param request
	 * @param submitFilesSessionList
	 */
	private void summary(HttpServletRequest request, List submitFilesSessionList) {
		Map<SessionDTO, List> sessionUserMap = new TreeMap<SessionDTO, List>(this.new SessionComparator());
        
        //build a map with all users in the submitFilesSessionList
        Iterator it = submitFilesSessionList.iterator();
        while(it.hasNext()){
        	SessionDTO sessionDto = new SessionDTO();
        	SubmitFilesSession sfs = (SubmitFilesSession)it.next();
        	
            Long sessionID = sfs.getSessionID();
            sessionDto.setSessionID(sessionID);
            sessionDto.setSessionName(sfs.getSessionName());
            
            boolean hasReflect = sfs.getContent().isReflectOnActivity();
            //construct LearnerDTO list
            List<SubmitUser> userList = submitFilesService.getUsersBySession(sessionID);
            List<SubmitUserDTO> learnerList = new ArrayList<SubmitUserDTO>();
            for(SubmitUser user : userList){
            	SubmitUserDTO learnerDto = new SubmitUserDTO(user);
            	learnerDto.setHasRefection(hasReflect);
            	learnerList.add(learnerDto);
            }
            sessionUserMap.put(sessionDto, learnerList);
        }
        
		//request.setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID,sessionID);
		request.setAttribute("sessionUserMap",sessionUserMap);
	}
	
	public ActionForward viewReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Long userUid = WebUtil.readLongParam(request,SbmtConstants.ATTR_USER_UID);
		Long sessionID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
		
		submitFilesService = getSubmitFilesService();
		SubmitUser userDto = submitFilesService.getUserByUid(userUid);

		submitFilesService = getSubmitFilesService();
		NotebookEntry notebookEntry = submitFilesService.getEntry(sessionID, 
				CoreNotebookConstants.NOTEBOOK_TOOL, 
				SbmtConstants.TOOL_SIGNATURE, userDto.getUserID());
		
		SubmitFilesSession session = submitFilesService.getSessionById(sessionID);
		
		SubmitUserDTO userDTO = new SubmitUserDTO(userDto);
		if(notebookEntry == null){
			userDTO.setFinishReflection(false);
			userDTO.setReflect(null);
		}else{
			userDTO.setFinishReflection(true);
			userDTO.setReflect(notebookEntry.getEntry());
		}
		userDTO.setReflectInstrctions(session.getContent().getReflectInstructions());
		
		request.setAttribute("userDTO", userDTO);
		return mapping.findForward("viewReflect");
	}
}
