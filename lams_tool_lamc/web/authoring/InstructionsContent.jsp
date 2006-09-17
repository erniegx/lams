<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as 
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>

<%@ taglib uri="tags-bean" prefix="bean"%> 
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-logic-el" prefix="logic-el" %>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

				<table class="forms">
					<tr> <td>
						<jsp:include page="/McErrorBox.jsp" />
					</td> </tr>
				</table>

				<table class="forms">
				<tr> 
					<td NOWRAP colspan=2 valign=top>
						<lams:SetEditor id="richTextOnlineInstructions" text="${sessionScope.richTextOnlineInstructions}" small="true" key="label.onlineInstructions.col"/>					
					</td> 
				</tr>
				

				<tr>
					<td colspan=2 NOWRAP align=left valign=top width="50%">
						<table width="50%" cellspacing="8" align="CENTER" class="forms">
									<c:forEach var='file' items='${sessionScope.listOnlineFilesMetadata}'>
											<tr>
												<td NOWRAP valign=top>
													 <c:out value="${file.filename}"/> 
												</td>
												<td NOWRAP valign=top>												
													<c:set var="viewURL">
														<html:rewrite page="/download/?uuid=${file.uuid}&preferDownload=false"/>
													</c:set>
													<a href="javascript:launchInstructionsPopup('<c:out value='${viewURL}' escapeXml='false'/>')">
														 	<bean:message key="label.view"/> 
													</a>

												</td>
												<td NOWRAP valign=top>													
													<c:set var="downloadURL">
															<html:rewrite page="/download/?uuid=${file.uuid}&preferDownload=true"/>
													</c:set>
													<a href="<c:out value='${downloadURL}' escapeXml='false'/>">
														<bean:message key="label.download"/> 
													</a>
												</td>
												<td NOWRAP valign=top>
													<img src="<c:out value="${tool}"/>images/delete.gif" align=left onclick="javascript:submitDeleteFile('<c:out value="${file.uuid}"/>','deleteOnlineFile');"> 
												</td>
											</tr>
				         			</c:forEach>
	         			</table>
					</td> 
				</tr>
				

				<tr> 
					<td class="field-name">
    	      				 <bean:message key="label.onlineFiles" /> 
          			</td>
          			<td NOWRAP valign=top> 
							<html:file  property="theOnlineFile"></html:file>
						 	<html:submit onclick="javascript:submitMethod('submitOnlineFiles');" styleClass="buttonLeft">
								 <bean:message key="button.upload"/> 
							</html:submit>
					</td> 
				</tr>

				
				<tr> 
					<td NOWRAP colspan=2 valign=top>
						<lams:SetEditor id="richTextOfflineInstructions" text="${richTextOfflineInstructions}" small="true" key="label.offlineInstructions.col"/>																			
					</td> 
				</tr>

				<tr> 
					<td colspan=2 NOWRAP align=left valign=top width="50%">
						<table width="50%" cellspacing="8" align="CENTER" class="forms">
									<c:forEach var='file' items='${sessionScope.listOfflineFilesMetadata}'>
											<tr>
												<td NOWRAP valign=top>
													 <c:out value="${file.filename}"/> 
												</td>
												<td NOWRAP valign=top>												
													<c:set var="viewURL">
														<html:rewrite page="/download/?uuid=${file.uuid}&preferDownload=false"/>
													</c:set>
													<a href="javascript:launchInstructionsPopup('<c:out value='${viewURL}' escapeXml='false'/>')">
														 	<bean:message key="label.view"/> 
													</a>

												</td>
												<td NOWRAP valign=top>													
													<c:set var="downloadURL">
															<html:rewrite page="/download/?uuid=${file.uuid}&preferDownload=true"/>
													</c:set>
													<a href="<c:out value='${downloadURL}' escapeXml='false'/>">
														<bean:message key="label.download"/> 
													</a>
												</td>
												<td NOWRAP valign=top>
													<img src="<c:out value="${tool}"/>images/delete.gif" align=left onclick="javascript:submitDeleteFile('<c:out value="${file.uuid}"/>','deleteOnlineFile');"> 
												</td>
				         			</c:forEach>
	         			</table>
					</td> 
				</tr>

				
				<tr> 
					<td class="field-name">
							 <bean:message key="label.offlineFiles" /> 
          			</td>
          			<td NOWRAP valign=top> 
							<html:file  property="theOfflineFile"></html:file>
						 	<html:submit onclick="javascript:submitMethod('submitOfflineFiles');" styleClass="buttonLeft">
								 <bean:message key="button.upload"/> 
							</html:submit>
					</td> 

				</tr>

				<html:hidden property="fileItem"/>
				<html:hidden property="offlineFile"/>				
				<html:hidden property="uuid"/>				
				
			</table>	  	
