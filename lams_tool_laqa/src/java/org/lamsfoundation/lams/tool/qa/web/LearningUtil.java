/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
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
package org.lamsfoundation.lams.tool.qa.web;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.qa.QaAppConstants;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueUsr;
import org.lamsfoundation.lams.tool.qa.QaQuestion;
import org.lamsfoundation.lams.tool.qa.QaSession;
import org.lamsfoundation.lams.tool.qa.QaUsrResp;
import org.lamsfoundation.lams.tool.qa.dto.GeneralLearnerFlowDTO;
import org.lamsfoundation.lams.tool.qa.dto.QaQuestionDTO;
import org.lamsfoundation.lams.tool.qa.service.IQaService;
import org.lamsfoundation.lams.tool.qa.util.QaComparator;
import org.lamsfoundation.lams.tool.qa.web.form.QaLearningForm;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;

/**
 * 
 * Keeps all operations needed for Learning mode.
 * 
 * @author Ozgur Demirtas
 * 
 */
public class LearningUtil implements QaAppConstants {
    static Logger logger = Logger.getLogger(LearningUtil.class.getName());

    public static void saveFormRequestData(HttpServletRequest request, QaLearningForm qaLearningForm) {
	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	qaLearningForm.setToolSessionID(toolSessionID);

	String userID = request.getParameter("userID");
	qaLearningForm.setUserID(userID);

	String httpSessionID = request.getParameter("httpSessionID");
	qaLearningForm.setHttpSessionID(httpSessionID);

	String totalQuestionCount = request.getParameter("totalQuestionCount");
	qaLearningForm.setTotalQuestionCount(totalQuestionCount);
    }

    public static GeneralLearnerFlowDTO buildGeneralLearnerFlowDTO(QaContent qaContent) {
	GeneralLearnerFlowDTO generalLearnerFlowDTO = new GeneralLearnerFlowDTO();
	generalLearnerFlowDTO.setActivityTitle(qaContent.getTitle());
	generalLearnerFlowDTO.setActivityInstructions(qaContent.getInstructions());
	generalLearnerFlowDTO.setReportTitleLearner(qaContent.getReportTitle());

	if (qaContent.isQuestionsSequenced())
	    generalLearnerFlowDTO.setQuestionListingMode(QUESTION_LISTING_MODE_SEQUENTIAL);
	else
	    generalLearnerFlowDTO.setQuestionListingMode(QUESTION_LISTING_MODE_COMBINED);

	generalLearnerFlowDTO.setUserNameVisible(new Boolean(qaContent.isUsernameVisible()).toString());
	generalLearnerFlowDTO.setShowOtherAnswers(new Boolean(qaContent.isShowOtherAnswers()).toString());
	generalLearnerFlowDTO.setActivityOffline(new Boolean(qaContent.isRunOffline()).toString());
	generalLearnerFlowDTO.setAllowRichEditor(new Boolean(qaContent.isAllowRichEditor()).toString());
	generalLearnerFlowDTO.setAllowRateAnswers(new Boolean(qaContent.isAllowRateAnswers()).toString());
	
	generalLearnerFlowDTO.setTotalQuestionCount(new Integer(qaContent.getQaQuestions().size()));

	//create mapQuestions
	Map<String, QaQuestionDTO> mapQuestions = new TreeMap<String, QaQuestionDTO>();
	for (QaQuestion question : qaContent.getQaQuestions()) {
	    int displayOrder = question.getDisplayOrder();
	    if (displayOrder != 0) {
		//add the question to the questions Map in the displayOrder
		QaQuestionDTO questionDTO = new QaQuestionDTO(question);
		mapQuestions.put(questionDTO.getDisplayOrder(), questionDTO);
	    }
	}
	generalLearnerFlowDTO.setMapQuestionContentLearner(mapQuestions);
	
	return generalLearnerFlowDTO;
    }

//    /**
//     * Create users of the responses
//     * 
//     * @param mapAnswers
//     * 
//     */
//    public static void storeResponses(Map<String, String> mapAnswers, IQaService qaService, Long toolContentID, Long toolSessionID) {
//
//	// get back login user DTO
//	HttpSession ss = SessionManager.getSession();
//	UserDTO toolUser = (UserDTO) ss.getAttribute(AttributeNames.USER);
//	Long userId = new Long(toolUser.getUserID().longValue());
//
//	// obtain QaContent to be used in creating QaQueUsr
//	QaContent qaContent = qaService.getQa(toolContentID.longValue());
//
//	QaQueUsr user = qaService.getUserByIdAndSession(userId, toolSessionID);
//
//	// check if Attempt to Entry is allowed, if so create the responses
//	if (!user.isResponseFinalized() || !qaContent.isLockWhenFinished()) {
//	    
//	    Set<QaQuestion> questionsToStore;
//	    if (qaContent.isQuestionsSequenced()) {
//		String currentQuestionIndex = qaLearningForm.getCurrentQuestionIndex();
//		QaQuestion currentQuestion = qaService.getQuestionContentByDisplayOrder(new Long(currentQuestionIndex), qaContent.getQaContentId());
//		questionsToStore = new LinkedHashSet<QaQuestion>();
//		questionsToStore.add(currentQuestion);
//		
//	    } else {
//		questionsToStore = qaContent.getQaQuestions();
//	    }
//
//	    for (QaQuestion question : questionsToStore) {
//
//		String displayOrder = new Long(question.getDisplayOrder()).toString();
//		String answer = (String) mapAnswers.get(displayOrder);
//
//		QaUsrResp response = qaService.getResponseByUserAndQuestion(user.getQueUsrId(), question.getUid());
//		// if response doesn't exist
//		if (response == null) {
//		    response = new QaUsrResp(answer, new Date(System.currentTimeMillis()), "", question, user, true);
//		    qaService.createQaUsrResp(response);
//
//		//if answer has changed
//		} else if (! answer.equals(response.getAnswer())) {
//		    response.setAnswer(answer);
//		    response.setAttemptTime(new Date(System.currentTimeMillis()));
//		    response.setTimezone("");
//		    qaService.updateUserResponse(response);
//		}
//	    }
//
//	}
//    }

    public static String getRemainingQuestionCount(int currentQuestionIndex, String totalQuestionCount) {
	int remainingQuestionCount = new Long(totalQuestionCount).intValue() - currentQuestionIndex + 1;
	return new Integer(remainingQuestionCount).toString();
    }

    /**
     * feedBackAnswersProgress(HttpServletRequest request, int
     * currentQuestionIndex) give user feedback on the remaining questions
     * 
     * @param qaLearningForm
     *                return void
     */
    public static String feedBackAnswersProgress(HttpServletRequest request, int currentQuestionIndex,
	    String totalQuestionCount) {
	int remainingQuestionCount = new Long(totalQuestionCount).intValue() - currentQuestionIndex + 1;
	String userFeedback = "";
	if (remainingQuestionCount != 0) {
	    userFeedback = "Remaining question count: " + remainingQuestionCount;
	} else {
	    userFeedback = "End of the questions.";
	}

	return userFeedback;
    }
    
    /**
     */
    public static void populateAnswers(Map sessionMap, QaContent qaContent, QaQueUsr qaQueUsr,
	    Map<String, QaQuestionDTO> mapQuestions, GeneralLearnerFlowDTO generalLearnerFlowDTO,
	    IQaService qaService) {
	
	//create mapAnswers
	Map<String, String> mapAnswers = (Map) sessionMap.get(MAP_ALL_RESULTS_KEY);
	if (mapAnswers == null) {
	    mapAnswers = new TreeMap<String, String>(new QaComparator());
	    
	    // get responses from DB
	    Map<String, String> mapAnswersFromDb = new TreeMap<String, String>();
	    for (QaQuestion question : qaContent.getQaQuestions()) {
		Long questionUid = question.getUid();
		QaUsrResp dbResponse = qaService.getResponseByUserAndQuestion(qaQueUsr.getQueUsrId(), questionUid);
		if (dbResponse != null) {
		    mapAnswersFromDb.put(String.valueOf(question.getDisplayOrder()), dbResponse.getAnswer());
		}
	    }	    
	    
	    // maybe we have come in from the review screen, if so get the answers from db.
	    if (mapAnswersFromDb.size() > 0) {
		mapAnswers.putAll(mapAnswersFromDb);
	    } else {
		for (Map.Entry pairs : mapQuestions.entrySet()) {
		    mapAnswers.put(pairs.getKey().toString(), "");
		}
	    }
	}
	String currentAnswer = (String) mapAnswers.get("1");
	generalLearnerFlowDTO.setCurrentQuestionIndex(new Integer(1));
	generalLearnerFlowDTO.setCurrentAnswer(currentAnswer);
	sessionMap.put(MAP_SEQUENTIAL_ANSWERS_KEY, mapAnswers);
	generalLearnerFlowDTO.setMapAnswers(mapAnswers);
	sessionMap.put(MAP_ALL_RESULTS_KEY, mapAnswers);
    }
}
