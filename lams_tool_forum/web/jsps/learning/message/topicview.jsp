
<c:forEach var="msgDto" items="${topicThread}">
	<c:set var="indentSize" value="${msgDto.level*3}" />
	<c:set var="hidden" value="${msgDto.message.hideFlag}" />
	<div style="margin-left:<c:out value="${indentSize}"/>em;">
		<table cellspacing="0" class="forum">
			<tr>
				<th class="first">
					<c:choose>
						<c:when test='${(sessionMap.mode == "teacher") || (not hidden)}'>
							<b> <c:out value="${msgDto.message.subject}" /> </b>
						</c:when>
						<c:otherwise>
							<fmt:message key="topic.message.subject.hidden" />
						</c:otherwise>
					</c:choose>
				</th>
			</tr>
			<tr>
				<td class="first posted-by">
					<c:if test='${(sessionMap.mode == "teacher") || (not hidden)}'>
						<fmt:message key="lable.topic.subject.by" />
						<c:out value="${msgDto.author}" />
								-
								<fmt:formatDate value="${msgDto.message.created}" type="time" timeStyle="short" />
						<fmt:formatDate value="${msgDto.message.created}" type="date" dateStyle="full" />
					</c:if>
				</td>
			</tr>
			<tr>
				<td>
					<c:if test='${(not hidden) || (hidden && sessionMap.mode == "teacher")}'>
						<c:out value="${msgDto.message.body}" escapeXml="false" />
					</c:if>
					<c:if test='${hidden}'>
						<fmt:message key="topic.message.body.hidden" />
					</c:if>
				</td>
			</tr>

			<c:if test="${not empty msgDto.message.attachments}">
				<tr>
					<td>
						<c:if test='${(not hidden) || (hidden && sessionMap.mode == "teacher")}'>
							<c:forEach var="file" items="${msgDto.message.attachments}">
								<c:set var="downloadURL">
									<html:rewrite page="/download/?uuid=${file.fileUuid}&versionID=${file.fileVersionId}&preferDownload=true" />
								</c:set>
								<a href="<c:out value='${downloadURL}' escapeXml='false'/>"> <c:out value="${file.fileName}" /> </a>
							</c:forEach>
						</c:if>
						<c:if test='${hidden}'>
							<fmt:message key="topic.message.attachment.hidden" />
						</c:if>
					</td>
				</tr>
			</c:if>
			<c:if test="${((msgDto.released && msgDto.isAuthor) || sessionMap.mode=='teacher') && (not empty msgDto.mark)}">
				<tr>
					<td>
						<span class="field-name" ><fmt:message key="lable.topic.title.mark"/></span>
						<BR>
							${msgDto.mark}
					</td>
				</tr>
				<tr>
					<td>
						<span class="field-name" ><fmt:message key="lable.topic.title.comment"/></span>
						<BR>
						<c:choose>
							<c:when test="${empty msgDto.comment}">
								<fmt:message key="message.not.avaliable"/>
							</c:when>
							<c:otherwise>
								<c:out value="${msgDto.comment}" escapeXml="false" />
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:if>
			<tr>
				<td>
					<div class="right-buttons">
						<!--  Hide/Unhide Button -->
						<c:if test='${sessionMap.mode == "teacher"}'>
							<!--  call the hide action -->
							<c:choose>
								<c:when test="${hidden}">
									<!--  display a show link  -->
									<c:set var="hidetopic">
										<html:rewrite page="/learning/updateMessageHideFlag.do?sessionMapID=${sessionMapID}&msgId=${msgDto.message.uid}&hideFlag=false" />
									</c:set>
									<html:link href="${hidetopic}" styleClass="button">
										<fmt:message key="label.show" /> 
									</html:link>
								</c:when>
								<c:otherwise>
									<!--  display a hide link -->
									<c:set var="hidetopic">
										<html:rewrite page="/learning/updateMessageHideFlag.do?sessionMapID=${sessionMapID}&topicID=${msgDto.message.uid}&hideFlag=true" />
									</c:set>
									<html:link href="${hidetopic}" styleClass="button">
										<fmt:message key="label.hide" />
									</html:link>
								</c:otherwise>
							</c:choose>
						</c:if>
						
						<!--  Edit Button -->
						<c:if test="${not hidden}">
							<c:if test='${(sessionMap.mode == "teacher") || (msgDto.isAuthor && not sessionMap.finishedLock && sessionMap.allowEdit && (empty msgDto.mark))}'>
								<c:set var="edittopic">
									<html:rewrite page="/learning/editTopic.do?sessionMapID=${sessionMapID}&topicID=${msgDto.message.uid}&rootUid=${sessinoMap.rootUid}&create=${msgDto.message.created.time}" />
								</c:set>
								<html:link href="${edittopic}" styleClass="button">
									<fmt:message key="label.edit" />
								</html:link>
							</c:if>
						</c:if>

						<!--  Reply Button -->
						<c:if test="${(not sessionMap.finishedLock) && (not sessionMap.noMorePosts)}">
							<c:set var="replytopic">
								<html:rewrite page="/learning/newReplyTopic.do?sessionMapID=${sessionMapID}&parentID=${msgDto.message.uid}&rootUid=${sessionMap.rootUid}" />
							</c:set>
							<html:link href="${replytopic}" styleClass="button">
								<fmt:message key="label.reply" />
							</html:link>
						</c:if>
					</div>
				</td>
			</tr>
		</table>
	</div>
</c:forEach>

