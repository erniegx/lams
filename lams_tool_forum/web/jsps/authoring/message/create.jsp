<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration" %>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys" %>
<%@ page import="org.lamsfoundation.lams.util.FileValidatorUtil" %>
<c:set var="UPLOAD_FILE_MAX_SIZE_AS_USER_STRING"><%=FileValidatorUtil.formatSize(Configuration.getAsInt(ConfigurationKeys.UPLOAD_FILE_LARGE_MAX_SIZE))%></c:set>

<lams:html>
	<lams:head>
		<%@ include file="/common/header.jsp"%>
		<lams:css />
	</lams:head>
	
	<body>
		<div class="panel panel-default">
		<div class="panel-body">
	
		<!-- Basic Info Form-->
		<%@ include file="/common/messages.jsp"%>

		<html:form action="/authoring/createTopic.do" focus="message.subject" enctype="multipart/form-data" styleId="topicFormId">
			<html:hidden property="sessionMapID" />
			<c:set var="formBean" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />
			<c:set var="sessionMap" value="${sessionScope[formBean.sessionMapID]}" />

			<div class="form-group">
		    <label for="message.subject"><fmt:message key="message.label.subject" /> *</label>
		    <html:text size="30" tabindex="1" property="message.subject" maxlength="60" styleClass="form-control"/>
			<html:errors property="message.subject" />
			</div>
			
			<div class="form-group">
		    <label for="forum.instructions"><fmt:message key="message.label.body" /> *</label>
			<c:set var="body" value=""/>
			<c:if test="${not empty formBean.message}">
				<c:set var="body" value="${formBean.message.body}"/>
			</c:if>
			<lams:CKEditor id="message.body" value="${body}" contentFolderID="${sessionMap.contentFolderID}"/>
			<html:errors property="message.body" />
			</div>

			<div class="form-group">
				<label for="attachmentFile"><fmt:message key="message.label.attachment" /></label>
				<lams:FileUpload fileFieldname="attachmentFile" maxFileSize="${UPLOAD_FILE_MAX_SIZE_AS_USER_STRING}" tabindex="3" />
				<html:errors property="message.attachment" />
				<lams:WaitingSpinner id="itemAttachmentArea_Busy"/>
			</div>

			<div class="voffset5 pull-right">
				<a href="#" onclick="hideMessage()" class="btn btn-default btn-xs"> <fmt:message key="button.cancel" /></a>
				<a href="#" onclick="submitMessage()" class="btn btn-default btn-xs loffset5"> <fmt:message key="button.add" /> </a>
			</div>

		</html:form>
	</body>
</lams:html>
