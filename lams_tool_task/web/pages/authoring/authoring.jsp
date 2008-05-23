<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.tool.taskList.TaskListConstants"%>


<lams:html>
<lams:head>
	<title><fmt:message key="label.authoring.title" /></title>

	<%@ include file="/common/tabbedheader.jsp"%>
	<%@ include file="/common/fckeditorheader.jsp"%>

	<script>
        function init(){
            var tag = document.getElementById("currentTab");
	    	if(tag.value != "")
	    		selectTab(tag.value);
            else
                selectTab(1); //select the default tab;
          
        }     
        
        function doSelectTab(tabId) {
        	// start optional tab controller stuff
        	var tag = document.getElementById("currentTab");
	    	tag.value = tabId;
	    	// end optional tab controller stuff
	    	
	    	//if we're leaving Condition tab its addCondition area should be closed
	    	if (tabId != 4)	window.parent.hideConditionMessage();
	    	
	    	selectTab(tabId);
	    	
	    	//for advanceTab
	    	if(tabId == 2)
	    		changeMinTasksComplete(-1);
        } 

        function doUploadOnline() {
        	var myForm = $("authoringForm");
        	myForm.action = "<c:url value='/authoring/uploadOnlineFile.do'/>";
        	myForm.submit();
        }
        
        function doUploadOffline() {
        	var myForm = $("authoringForm");
        	myForm.action = "<c:url value='/authoring/uploadOfflineFile.do'/>";
        	myForm.submit();
        }
        
        function changeMinTasksComplete(initVal){
			var tb = document.getElementById("itemTable");
			var num = tb.getElementsByTagName("tr");
			var sel = document.getElementById("minimumNumberTasksComplete");
			var newField = sel.options;
			var len = sel.length;
			
			//when first enter, it should get value from TaskList
			var selIdx=initVal < 0?-1:initVal;
			for (var idx=0;idx<len;idx++)
			{
				if(newField[0].selected && selIdx == -1 ){
					selIdx = newField[0].value;
				}
				sel.removeChild(newField[0]);
			}
		
			for(var i=0;i<=num.length;i++){
				var opt = document.createElement("option");
				var optT =document.createTextNode(i);
				opt.value=i;
				//get back user choosen value
				if(selIdx > 0 && selIdx==i){
					opt.selected = true;
				}else{
					opt.selected = false;
				}
				opt.appendChild(optT);
				sel.appendChild(opt);
			}
		}
    </script>
</lams:head>

<body class="stripes" onLoad="init()">

<div id="page">
		<h1>
			<fmt:message key="label.authoring.heading" />
		</h1>
<div id="header">
		<lams:Tabs useKey="true" control="true">
			<lams:Tab id="1" key="label.authoring.heading.basic" />
			<lams:Tab id="2" key="label.authoring.heading.advance" />
			<lams:Tab id="3" key="label.authoring.heading.instructions" />
			<lams:Tab id="4" key="label.authoring.heading.conditions" />
		</lams:Tabs></div>
		<!-- start tabs -->
<div id="content">
		<!-- end tab buttons -->
		
		<%@ include file="/common/messages.jsp"%>

		<html:form action="authoring/update" method="post" styleId="authoringForm" enctype="multipart/form-data">
		<c:set var="formBean" value="<%= request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY) %>" />
		<html:hidden property="taskList.contentId" />
		<html:hidden property="sessionMapID" />
		<html:hidden property="contentFolderID" />
		<html:hidden property="currentTab" styleId="currentTab" />

		<lams:help toolSignature="<%= TaskListConstants.TOOL_SIGNATURE %>" module="authoring"/>

			<!-- tab content 1 (Basic) -->
			<lams:TabBody id="1" titleKey="label.authoring.heading.basic.desc" page="basic.jsp" />
			<!-- end of content (Basic) -->

			<!-- tab content 2 (Advanced) -->
			<lams:TabBody id="2" titleKey="label.authoring.heading.advance.desc" page="advance.jsp" />
			<!-- end of content (Advanced) -->

			<!-- tab content 3 (Instructions) -->
			<lams:TabBody id="3" titleKey="label.authoring.heading.instructions.desc" page="instructions.jsp" />
			<!-- end of content (Instructions) -->

			<!-- tab content 4 (Conditions) -->
			<lams:TabBody id="4" titleKey="label.authoring.heading.conditions.desc" page="conditions.jsp" />
			<!-- end of content (Instructions) -->

			<!-- Button Row -->
			<%--  Default value 
				cancelButtonLabelKey="label.authoring.cancel.button"
				saveButtonLabelKey="label.authoring.save.button"
				cancelConfirmMsgKey="authoring.msg.cancel.save"
				accessMode="author"
			--%>

			<lams:AuthoringButton formID="authoringForm" clearSessionActionUrl="/clearsession.do" 
				toolSignature="<%=TaskListConstants.TOOL_SIGNATURE%>" toolContentID="${formBean.taskList.contentId}" 
				 customiseSessionID="${formBean.sessionMapID}"
				 contentFolderID="${formBean.contentFolderID}" />
	</html:form>

</div>

<div id="footer"></div>
<!-- end page div -->
</div>

<script type="text/javascript">
	changeMinTasksComplete(${formBean.taskList.minimumNumberTasksComplete});
</script>


</body>
</lams:html>
