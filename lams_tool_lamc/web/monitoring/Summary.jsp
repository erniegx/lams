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
<%@ taglib uri="tags-function" prefix="fn" %>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

		<c:if test="${userExceptionNoToolSessions == 'true'}"> 	
				<table class="forms" align="center">
					<tr> 
						<td NOWRAP valign=top align=center> 
							<b> <bean:message key="error.noLearnerActivity"/> </b>
						</td> 
					<tr>
				</table>
		</c:if>						
		
		<c:if test="${userExceptionNoToolSessions != 'true'}"> 	
			<html:hidden property="selectedToolSessionId"/>							
			<input type="hidden" name="isToolSessionChanged"/>
				<table class="forms">
						<tr> 
						<td NOWRAP  valign=top align=center>  <b> <bean:message key="label.selectGroup"/> </b>
									<select name="monitoredToolSessionId" onchange="javascript:submitSession(this.value,'submitSession');">
									<c:forEach var="toolSessionName" items="${sessionScope.summaryToolSessions}">
										<c:forEach var="toolSessionId" items="${sessionScope.summaryToolSessionsId}">
											<c:out value="${toolSessionName.key}"/> -<c:out value="${toolSessionId.value}"/>
										
												<c:if test="${toolSessionName.key == toolSessionId.key}"> 			
											
													<c:set var="SELECTED_SESSION" scope="request" value=""/>
													<c:if test="${sessionScope.selectionCase == 2}"> 			
														<c:set var="currentMonitoredToolSession" scope="session" value="All"/>
													</c:if>						
													
													<c:if test="${toolSessionId.value == sessionScope.currentMonitoredToolSession}"> 			
															<c:set var="SELECTED_SESSION" scope="request" value="SELECTED"/>
													</c:if>						
													
													<c:if test="${toolSessionId.value != 'All'}"> 		
														<option value="<c:out value="${toolSessionId.value}"/>"  <c:out value="${requestScope.SELECTED_SESSION}"/>> <c:out value="${toolSessionName.value}"/>  </option>						
													</c:if>						
													
													<c:if test="${toolSessionId.value == 'All'}"> 	
														<option value="<c:out value="${toolSessionId.value}"/>"  <c:out value="${requestScope.SELECTED_SESSION}"/>>  All  </option>						
													</c:if>						
											</c:if>							
										</c:forEach>		  	
									</c:forEach>		  	
									</select>
							</td> 
						<tr>					


					
		  	 		<c:set var="queIndex" scope="request" value="0"/>
					<c:forEach var="currentDto" items="${sessionScope.listMonitoredAnswersContainerDto}">
					<c:set var="queIndex" scope="request" value="${queIndex +1}"/>
			  	 		<c:set var="currentQuestionId" scope="request" value="${currentDto.questionUid}"/>
			  	 		<tr>
			  	 			<td> &nbsp&nbsp&nbsp</td>
			  	 		</tr>
						<tr>			
							<td NOWRAP valign=top align=left><b>  <bean:message key="label.question.only"/> <c:out value="${queIndex}"/>:</b>
								<c:out value="${currentDto.question}"/> &nbsp (<bean:message key="label.weight"/> 
								<c:out value="${currentDto.weight}"/>  <bean:message key="label.percent"/>)
							 </td>
						</tr>	
						<tr>					
							<td NOWRAP valign=top align=left>  <b> <bean:message key="label.mc.options.col"/>  </b> 
								<table align=left>
									<c:forEach var="answersData" items="${currentDto.candidateAnswersCorrect}">
										<tr>			
											<td NOWRAP valign=top align=left>
												&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
												<c:out value="${answersData.candidateAnswer}"/> 
												
												<c:if test="${answersData.correct == 'true'}"> 		
													&nbsp (<bean:message key="label.correct"/>)
												</c:if>																		
											</td>	
										</tr>
									</c:forEach>		  	
								</table>
							</td>  
						</tr>			

					</c:forEach>		  	

		  	 		<tr>
		  	 			<td NOWRAP valign=top align=left> <b> 
		  	 				<bean:message key="label.passingMark"/> </b> <c:out value="${passMark}"/> <bean:message key="label.percent"/>  
		  	 			</td>
		  	 		</tr>
				</table>

			 <h2>    <bean:message key="label.studentMarks"/>  </h2>

				<c:if test="${sessionScope.currentMonitoredToolSession =='All'}"> 						 
					<jsp:include page="/monitoring/SummaryAllSessions.jsp" />
				</c:if>						
				<c:if test="${sessionScope.currentMonitoredToolSession !='All'}"> 						 
					<jsp:include page="/monitoring/SummaryIndividualSession.jsp" />
				</c:if>						
				
		</c:if>						
		
		
		
			<table class="forms">
					<tr>			
							<td valign=top align=left>
								<table align=center>
								
										<tr>			
											<td colspan=2 valign=top align=left>
												<b>  <bean:message key="label.reflection"/> <bean:message key="label.include.runOffline"/> </b> 
											 </td>
										</tr>	
								
								
									<c:forEach var="currentDto" items="${reflectionsContainerDTO}">
							  	 		<c:set var="userName" scope="request" value="${currentDto.userName}"/>
							  	 		<c:set var="userId" scope="request" value="${currentDto.userId}"/>
							  	 		<c:set var="sessionId" scope="request" value="${currentDto.sessionId}"/>
							  	 		<c:set var="reflectionUid" scope="request" value="${currentDto.reflectionUid}"/>
										<tr>			
											<td valign=top align=left>
												 <c:out value="${userName}" escapeXml="false"/> 
											 </td>

											<td valign=top align=left>
												<c:url value="/monitoring.do" var="openNotebook">
													<c:param name="method" value="openNotebook" />
													<c:param name="uid" value="${reflectionUid}" />
													<c:param name="userId" value="${userId}" />
													<c:param name="userName" value="${userName}" />
													<c:param name="sessionId" value="${sessionId}" />													
												</c:url>
				
												<html:link href="${fn:escapeXml(openNotebook)}" target="_blank">
													<bean:message key="label.view" />
												</html:link>

											 </td>
										</tr>	
									</c:forEach>		
								</table>  	
							 </td>
						</tr>	
				</table>		  	 								
		


	