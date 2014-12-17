<%@ include file="/common/taglibs.jsp"%>

<%-- If you change this file, remember to update the copy made for CNG-12 --%>

<!--   Advance Tab Content    -->

<p class="small-space-top">
	<html:checkbox property="forum.lockWhenFinished" styleClass="noBorder" styleId="lockWhenFinished"/>
	<label for="lockWhenFinished">
		<fmt:message key="label.authoring.advance.lock.on.finished" />
	</label>
</p>

<p>
	<html:checkbox property="forum.allowEdit" styleClass="noBorder" styleId="allowEdit"/>
	<label for="allowEdit">
		<fmt:message key="label.authoring.advance.allow.edit" />
	</label>
</p>

<p>
	<html:checkbox property="forum.allowRateMessages" styleClass="noBorder" styleId="allowRateMessages" onclick="checkRating()" />
	<label for="allowRateMessages">
		<fmt:message key="label.authoring.advance.allow.rate.postings" />
	</label>
</p>

<p style="margin-left:60px;">
	<fmt:message key="label.authoring.advance.minimum.reply" />
	<html:select property="forum.minimumRate" styleId="minimumRate"  onmouseup="validateRatings(true);">
		<html:option value="0">
			<fmt:message key="label.authoring.advance.no.minimum" />
		</html:option>
		<html:option value="1">1</html:option>
		<html:option value="2">2</html:option>
		<html:option value="3">3</html:option>
		<html:option value="4">4</html:option>
		<html:option value="5">5</html:option>
		<html:option value="6">6</html:option>
		<html:option value="7">7</html:option>
		<html:option value="8">8</html:option>
		<html:option value="9">9</html:option>
		<html:option value="10">10</html:option>
	</html:select>

	<fmt:message key="label.authoring.advance.maximum.reply" />
	<html:select property="forum.maximumRate" styleId="maximumRate"   onmouseup="validateRatings(false);">
		<html:option value="0">
			<fmt:message key="label.authoring.advance.no.maximum" />
		</html:option>
		<html:option value="1">1</html:option>
		<html:option value="2">2</html:option>
		<html:option value="3">3</html:option>
		<html:option value="4">4</html:option>
		<html:option value="5">5</html:option>
		<html:option value="6">6</html:option>
		<html:option value="7">7</html:option>
		<html:option value="8">8</html:option>
		<html:option value="9">9</html:option>
		<html:option value="10">10</html:option>
	</html:select>
</p>

<p>
	<html:checkbox property="forum.allowUpload" styleClass="noBorder" styleId="allowUpload"/>
	<label for="allowUpload">
		<fmt:message key="label.authoring.advance.allow.upload" />
	</label>
</p>

<p>
	<html:checkbox property="forum.allowRichEditor" styleId="richEditor" styleClass="noBorder"/>
	<label for="richEditor">
		<fmt:message key="label.authoring.advance.use.richeditor" />
	</label>
</p>

<p>
	<html:checkbox property="forum.limitedMinCharacters" styleId="limited-min-characters" styleClass="noBorder"/>
	<label for="limited-min-characters">
		<fmt:message key="label.authoring.advance.min.limited.input" />
	</label>

	<html:text property="forum.minCharacters" styleId="min-characters" />
</p>

<p>
	<html:checkbox property="forum.limitedMaxCharacters" styleId="limited-max-characters" styleClass="noBorder"/>
	<label for="limited-max-characters">
		<fmt:message key="label.authoring.advance.limited.input" />
	</label>

	<html:text property="forum.maxCharacters" styleId="max-characters" />
</p>

<p style="margin-bottom: 15px;"><fmt:message key="label.authoring.advanced.send.emails.to" /> </p>
<p style="margin-left: 60px; ">
	<html:checkbox property="forum.notifyLearnersOnForumPosting" styleId="notifyLearnersOnForumPosting" styleClass="noBorder"/>
	<label for="notifyLearnersOnForumPosting">
		<fmt:message key="label.authoring.advanced.learners" />
	</label>
</p>
<p style="margin-left: 60px;">
	<html:checkbox property="forum.notifyTeachersOnForumPosting" styleId="notifyTeachersOnForumPosting" styleClass="noBorder"/>
	<label for="notifyTeachersOnForumPosting">
		<fmt:message key="label.authoring.advanced.teachers" />
	</label>
</p>

<p>
	<html:checkbox property="forum.notifyLearnersOnMarkRelease" styleId="notifyLearnersOnMarkRelease" styleClass="noBorder"/>
	<label for="notifyLearnersOnMarkRelease">
		<fmt:message key="label.authoring.advanced.notify.mark.release" />
	</label>
</p>

<p>
	<html:checkbox property="forum.reflectOnActivity" styleClass="noBorder"	styleId="reflectOn"/>
	<label for="reflectOn">
		<fmt:message key="advanced.reflectOnActivity" />
	</label>
</p>

<p>
	<span class="space-left"> 
		<html:textarea property="forum.reflectInstructions" styleId="reflectInstructions" cols="30" rows="3" /> 
	</span>
</p>
<script type="text/javascript">
	//automatically turn on refect option if there are text input in refect instruction area
	var ra = document.getElementById("reflectInstructions");
	var rao = document.getElementById("reflectOn");
	function turnOnRefect() {
		if (isEmpty(ra.value)) {
			//turn off	
			rao.checked = false;
		} else {
			//turn on
			rao.checked = true;
		}
	}

	ra.onkeyup = turnOnRefect;
</script>

<h2>
	<fmt:message key="message.posting.limiting" />
</h2>

<p>
	<html:radio property="forum.allowNewTopic" value="true"
		onclick="allowNewTopic()" styleId="allowNewTopic1" styleClass="noBorder"/>
	<label for="allowNewTopic1">
		<fmt:message key="label.authoring.advance.allow.new.topics" />
	</label>
</p>

<p>
	<html:radio property="forum.allowNewTopic" value="false"
		onclick="allowNewTopic()" styleId="allowNewTopic2" styleClass="noBorder"/>
	<label for="allowNewTopic2">
		<fmt:message key="label.authoring.advance.number.reply" />
	</label>
</p>

<p>
	<fmt:message key="label.authoring.advance.minimum.reply" />
	<html:select property="forum.minimumReply" styleId="minimumReply">
		<html:option value="0">
			<fmt:message key="label.authoring.advance.no.minimum" />
		</html:option>
		<html:option value="1">1</html:option>
		<html:option value="2">2</html:option>
		<html:option value="3">3</html:option>
		<html:option value="4">4</html:option>
		<html:option value="5">5</html:option>
		<html:option value="6">6</html:option>
		<html:option value="7">7</html:option>
		<html:option value="8">8</html:option>
		<html:option value="9">9</html:option>
		<html:option value="10">10</html:option>
	</html:select>

	<fmt:message key="label.authoring.advance.maximum.reply" />
	<html:select property="forum.maximumReply" styleId="maximumReply">
		<html:option value="0">
			<fmt:message key="label.authoring.advance.no.maximum" />
		</html:option>
		<html:option value="1">1</html:option>
		<html:option value="2">2</html:option>
		<html:option value="3">3</html:option>
		<html:option value="4">4</html:option>
		<html:option value="5">5</html:option>
		<html:option value="6">6</html:option>
		<html:option value="7">7</html:option>
		<html:option value="8">8</html:option>
		<html:option value="9">9</html:option>
		<html:option value="10">10</html:option>
	</html:select>

</p>

<script type="text/javascript">

	var rich = document.getElementById("richEditor");
	var limitedMinCharacters = document.getElementById("limited-min-characters");
	var minCharacters = document.getElementById("min-characters");
	var limitedMaxCharacters = document.getElementById("limited-max-characters");
	var maxCharacters = document.getElementById("max-characters");

	limitedMinCharacters.onclick = initAdvanced;
	limitedMaxCharacters.onclick = initAdvanced;
	function initAdvanced() {
		if (limitedMinCharacters.checked) {
			minCharacters.disabled = false;
			rich.checked = false;
		} else {
			minCharacters.disabled = true;
		}

		if (limitedMaxCharacters.checked) {
			maxCharacters.disabled = false;
			rich.checked = false;
		} else {
			maxCharacters.disabled = true;
		}
	}

	rich.onclick = function() {
		if (this.checked) {
			minCharacters.disabled = true;
			limitedMinCharacters.checked = false;
			maxCharacters.disabled = true;
			limitedMaxCharacters.checked = false;
		} else {
			minCharacters.disabled = false;
			maxCharacters.disabled = false;
		}
	}
	initAdvanced();

	minCharacters.onblur = checkIntValue;
	maxCharacters.onblur = checkIntValue;

	function checkIntValue() {
		var min = 0;
		var errors = '';
		var num = parseFloat(this.value);
		if (isNaN(num)) {
			errors = "<fmt:message key="js.error.invalid.number"/>\n";
		} else if (num <= min) {
			errors = "<fmt:message key="js.error.min.number"/>";
		}

		if (errors) {
			alert("<fmt:message key="js.error.title"/>\n" + errors);
		}
	}

	function allowNewTopic() {
		var allowNew = document.getElementsByName("forum.allowNewTopic");
		var min = document.getElementById("minimumReply");
		var max = document.getElementById("maximumReply");

		//enable if rate is on
		if (allowNew[0].checked) {
			min.disabled = true;
			max.disabled = true;
		}
		//enable reply limited drop list
		if (allowNew[1].checked) {
			min.disabled = false;
			max.disabled = false;
		}
	}
	allowNewTopic();

	function checkRating() {
		var allowRate = document.getElementsByName("forum.allowRateMessages");
		var min = document.getElementById("minimumRate");
		var max = document.getElementById("maximumRate");

		if (allowRate[0].checked) {
			min.disabled = false;
			max.disabled = false;
		} else {
			min.disabled = true;
			max.disabled = true;
		}
	}
	checkRating();

	function validateRatings(isMinimunRateDropdownUsed) {
		var minRateDropDown = document.getElementById("minimumRate");
		var minRatings = parseInt(minRateDropDown.options[minRateDropDown.selectedIndex].value);
		var maxRateDropDown = document.getElementById("maximumRate");
		var maxRatings = parseInt(maxRateDropDown.options[maxRateDropDown.selectedIndex].value);

		if ((minRatings > maxRatings) && !(maxRatings == 0)) {
			if (isMinimunRateDropdownUsed) {
				minRateDropDown.selectedIndex = maxRateDropDown.selectedIndex;
			} else {
				maxRateDropDown.selectedIndex = minRateDropDown.selectedIndex;
			}

			alert('<fmt:message key="js.error.validate.number"/>');
		}
	}

	function checkReflection() {
		var ropt = document.getElementById("reflectOn");
		var rins = document.getElementById("reflectInstructions");
		if (ropt.checked) {
			rins.disabled = false;
		} else {
			rins.disabled = true;
		}
	}
</script>
