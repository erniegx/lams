package org.lamsfoundation.lams.tool.forum.web.actions;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.tool.forum.core.PersistenceException;
import org.lamsfoundation.lams.tool.forum.dto.MessageDTO;
import org.lamsfoundation.lams.tool.forum.persistence.Attachment;
import org.lamsfoundation.lams.tool.forum.persistence.ForumToolSession;
import org.lamsfoundation.lams.tool.forum.persistence.ForumUser;
import org.lamsfoundation.lams.tool.forum.persistence.Message;
import org.lamsfoundation.lams.tool.forum.service.IForumService;
import org.lamsfoundation.lams.tool.forum.util.ForumConstants;
import org.lamsfoundation.lams.tool.forum.web.forms.MessageForm;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * User: conradb
 * Date: 24/06/2005
 * Time: 10:54:09
 */
public class LearningAction extends Action {
  	private static Logger log = Logger.getLogger(LearningAction.class);
	private IForumService forumService;


   public final ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
  		String param = mapping.getParameter();
  		//--------------Forum Level ------------------
	  	if (param.equals("viewForum")) {
       		return viewForm(mapping, form, request, response);
        }
	  	if (param.equals("newTopic")) {
	  		return newTopic(mapping, form, request, response);
	  	}
	  	if (param.equals("finish")) {
	  		return finish(mapping, form, request, response);
	  	}
	  	
	  	//--------------Topic Level ------------------
	  	if (param.equals("viewTopic")) {
       		return viewTopic(mapping, form, request, response);
        }
	  	if (param.equals("createTopic")) {
	  		return createTopic(mapping, form, request, response);
	  	}
	  	if (param.equals("newReplyTopic")) {
	  		return newReplyTopic(mapping, form, request, response);
	  	}
	  	if (param.equals("replyTopic")) {
	  		return replyTopic(mapping, form, request, response);
	  	}
        if (param.equals("editTopic")) {
        	return editTopic(mapping, form, request, response);
        }
        if (param.equals("updateTopic")) {
        	return updateTopic(mapping, form, request, response);
        }
        if (param.equals("deleteAttachment")) {
        	return deleteAttachment(mapping, form, request, response);
        }
        
		return mapping.findForward("error");
    }
	//==========================================================================================
	// Forum level  methods
	//==========================================================================================
   /**
    * Display root topics of a forum. This page will be the initial page of Learner page.
    *  
    */
	private ActionForward viewForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		//get sessionId from HttpServletRequest
		Long sessionId = new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		//cache this sessionId into HttpSession
		request.getSession().setAttribute(AttributeNames.PARAM_TOOL_SESSION_ID, sessionId);
		
		//Try to get ForumID according to sessionId
		forumService = getForumManager();
		ForumToolSession session = forumService.getSessionBySessionId(sessionId);
		if(session == null || session.getForum() == null){
			log.error("Failed on getting session by given sessionID:" + sessionId);
			return mapping.findForward("error");
		}
		Long forumId = session.getForum().getUid();
		request.getSession().setAttribute(ForumConstants.FORUM_ID, forumId);
		
		//get all root topic to display on init page
		List rootTopics = forumService.getRootTopics(sessionId);
		request.setAttribute(ForumConstants.AUTHORING_TOPICS_LIST,rootTopics);
		return mapping.findForward("success");
	}
	/**
	 * Display empty page for a new topic in forum
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward newTopic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		return mapping.findForward("success");
	}
	/**
	 * Learner click "finish" button in forum page, this method will turn on session status flag for this learner.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward finish(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		//get sessionId from HttpServletRequest
		Long sessionId = new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		forumService = getForumManager();
		ForumToolSession session = forumService.getSessionBySessionId(sessionId);
		session.setStatus(ForumConstants.SESSION_STATUS_FINISHED);
		forumService.updateSession(session);
		
		return mapping.findForward("success");
	}

	//==========================================================================================
	// Topic level  methods
	//==========================================================================================
	
	/**
	 * Display read-only page for a special topic. Topic will arrange by Tree structure.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward viewTopic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Long topicId = new Long(WebUtil.readLongParam(request,"topicId"));
		
		forumService = getForumManager();
		//get root topic list
		List msgDtoList = forumService.getTopicThread(topicId);
		request.setAttribute(ForumConstants.AUTHORING_TOPIC_THREAD,msgDtoList);
		return mapping.findForward("success");
	}
	/**
	 * Create a new root topic. 
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws PersistenceException
	 */
	public ActionForward createTopic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException, PersistenceException {
		
		MessageForm messageForm = (MessageForm) form;
		
		Message message = messageForm.getMessage();
		message.setIsAuthored(false);
		message.setCreated(new Date());
		message.setUpdated(new Date());
		message.setLastReplyDate(new Date());
		ForumUser forumUser = getCurrentUser();
		message.setCreatedBy(forumUser);
		message.setModifiedBy(forumUser);
		setAttachment(messageForm, message);
		
		//save message into database
		forumService = getForumManager();
		Long forumId = (Long) request.getSession().getAttribute(ForumConstants.FORUM_ID);
		Long sessionId = (Long) request.getSession().getAttribute(AttributeNames.PARAM_TOOL_SESSION_ID);
		forumService.createRootTopic(forumId,sessionId,message);
		
		//echo back current root topic to fourm init page
		List rootTopics = forumService.getRootTopics(sessionId);
		request.setAttribute(ForumConstants.AUTHORING_TOPICS_LIST,rootTopics);
		return mapping.findForward("success");
	}
	/**
	 * Dipslay replay topic page. Message form subject will include parent topics same subject.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward newReplyTopic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Long parentId = new Long(WebUtil.readLongParam(request,"parentId"));
		
		//get parent topic, it can decide default subject of reply.
    	MessageDTO topic = getTopic(parentId);
    	
    	if(topic != null && topic.getMessage() != null){
        	//echo back current topic subject to web page
    		MessageForm msgForm = (MessageForm)form;
    		msgForm.getMessage().setSubject("Re:"+topic.getMessage().getSubject());
    	}
    	
    	//cache this parentId in order to create reply
    	request.getSession().setAttribute("parentId",parentId);
		return mapping.findForward("success");
	}
	/**
	 * Create a replayed topic for a parent topic. 
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward replyTopic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Long parentId = (Long) request.getSession().getAttribute("parentId");
		
		MessageForm messageForm = (MessageForm) form;
		Message message = messageForm.getMessage();
		message.setIsAuthored(false);
		message.setCreated(new Date());
		message.setUpdated(new Date());
		message.setLastReplyDate(new Date());
		ForumUser forumUser = getCurrentUser();
		message.setCreatedBy(forumUser);
		message.setModifiedBy(forumUser);
		setAttachment(messageForm, message);
		
		//save message into database
		forumService = getForumManager();
		forumService.replyTopic(parentId,message);
		
		//echo back this topic thread into page
		forumService = getForumManager();
		Long rootTopicId = forumService.getRootTopicId(parentId);
		List msgDtoList = forumService.getTopicThread(rootTopicId);
		request.setAttribute(ForumConstants.AUTHORING_TOPIC_THREAD,msgDtoList);
		
		return mapping.findForward("success");
	}
	/**
	 * Display a editable form for a special topic in order to update it.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws PersistenceException
	 */
  	public ActionForward editTopic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws PersistenceException {
  		Long topicId = new Long(WebUtil.readLongParam(request,"topicId"));
  		
    	MessageDTO topic = getTopic(topicId);
    	//echo current topic content to web page
    	if(topic != null){
    		MessageForm msgForm = (MessageForm)form;
    		msgForm.setMessage(topic.getMessage());
    		request.setAttribute(ForumConstants.AUTHORING_TOPIC,topic);
    	}
    	
    	//cache this topicId in order to create reply
    	request.getSession().setAttribute("topicId",topicId);
    	return mapping.findForward("success");
    }
  	/**
  	 * Update a topic.
  	 * 
  	 * @param mapping
  	 * @param form
  	 * @param request
  	 * @param response
  	 * @return
  	 * @throws PersistenceException
  	 */
  	public ActionForward updateTopic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws PersistenceException {
    	//get value from HttpSession
		Long topicId = (Long) request.getSession().getAttribute("topicId");
		forumService = getForumManager();
		
		MessageForm messageForm = (MessageForm) form;
		Message message = messageForm.getMessage();
		//get PO from database and sync with Form
		Message messagePO = forumService.getMessage(topicId);
		messagePO.setSubject(message.getSubject());
		messagePO.setBody(message.getBody());
		messagePO.setUpdated(new Date());
		messagePO.setModifiedBy(getCurrentUser());
		setAttachment(messageForm, messagePO);
		
		//save message into database
		forumService.updateTopic(messagePO);

		//echo back this topic thread into page
		forumService = getForumManager();
		Long rootTopicId = forumService.getRootTopicId(topicId);
		List msgDtoList = forumService.getTopicThread(rootTopicId);
		request.setAttribute(ForumConstants.AUTHORING_TOPIC_THREAD,msgDtoList);
		
		return mapping.findForward("success");
    }

    /**
     * Only delete the attachemnt file for current topic.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws PersistenceException
     */
    public ActionForward deleteAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws PersistenceException {
    	//get value from HttpSession
    	Long topicId = (Long) request.getSession().getAttribute("topicId");
		Long versionID = new Long(WebUtil.readLongParam(request,"versionID"));
		Long uuID = new Long(WebUtil.readLongParam(request,"uuid"));
		forumService = getForumManager();
		forumService.deleteFromRepository(uuID,versionID);
//		get value from HttpSession
		Message messagePO = forumService.getMessage(topicId);
		messagePO.setUpdated(new Date());
		messagePO.setModifiedBy(getCurrentUser());
		messagePO.setAttachments(null);
		//save message into database
		forumService.updateTopic(messagePO);
		
    	return mapping.findForward("success");
    }

	//==========================================================================================
	// Utility methods
	//==========================================================================================
	/**
	 * @param topicId
	 * @return
	 */
	private MessageDTO getTopic(Long topicId) {
		//get Topic content according to TopicID
		forumService = getForumManager();
		MessageDTO topic = MessageDTO.getMessageDTO(forumService.getMessage(topicId));
		return topic;
	}
	
	/**
	 * Get login user information from system level session. Check it whether it exists in database or not, and save it
	 * if it does not exists. Return an instance of PO of ForumUser.
	 * @return
	 * 		Current user instance
	 */
	private ForumUser getCurrentUser() {
//			get login user (author)
		HttpSession ss = SessionManager.getSession();
		//get back login user DTO
		UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		//check whether this user exist or not
		ForumUser forumUser = forumService.getUserByUserId(new Long(user.getUserID().intValue()));
		if(forumUser == null){
			//if user not exist, create new one in database
			forumUser = new ForumUser(user);
			forumService.createUser(forumUser);
		}
		return forumUser;
	}
	/**
	 * Get Forum Service.
	 * 
	 * @return
	 */
  	private IForumService getForumManager() {
  	    if ( forumService == null ) {
  	      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
  	      forumService = (IForumService) wac.getBean(ForumConstants.FORUM_SERVICE);
  	    }
  	    return forumService;
  	}
	/**
	 * @param messageForm
	 * @param message
	 */
	private void setAttachment(MessageForm messageForm, Message message) {
		if(messageForm.getAttachmentFile() != null 
			&&  !StringUtils.isEmpty(messageForm.getAttachmentFile().getFileName())){
			forumService = getForumManager();
			Attachment att = forumService.uploadAttachment(messageForm.getAttachmentFile());
			//only allow one attachment, so replace whatever
			Set attSet = attSet = new HashSet();
			attSet.add(att);
			message.setAttachments(attSet);
		}
	}
}
