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
package org.lamsfoundation.lams.tool.qa;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * @author Ozgur Demirtas
 * 
 * The value object that maps to our model database table: tl_laqa11_que_content The relevant hibernate mapping
 * resides in: QaQuestion.hbm.xml
 * 
 * Holds question content within a particular content
 */
public class QaQuestion implements Serializable, Comparable, Nullable {

    private static final long serialVersionUID = -4028785701106936621L;

    /** identifier field */
    private Long uid;

    /** nullable persistent field */
    private String question;

    /** nullable persistent field */
    private int displayOrder;

    private String feedback;

    private boolean required;
    
    /** nullable persistent field */
    private QaContent qaContent;

    /** persistent field */
    private Set qaQueUsers;

    /** default constructor */
    public QaQuestion() {
    }

    public QaQuestion(String question, int displayOrder, String feedback, boolean required,
	    QaContent qaContent, Set qaQueUsers) {
	this.question = question;
	this.displayOrder = displayOrder;
	this.feedback = feedback;
	this.required = required;
	this.qaContent = qaContent;
	this.qaQueUsers = qaQueUsers;
    }

    public static QaQuestion newInstance(QaQuestion queContent, QaContent newQaContent) {
	QaQuestion newQueContent = new QaQuestion(queContent.getQuestion(), queContent.getDisplayOrder(),
		queContent.getFeedback(), queContent.isRequired(), newQaContent, new TreeSet());
	return newQueContent;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("qaQueContentId: ", getUid()).append("question: ", getQuestion())
		.append("displayOrder: ", getDisplayOrder()).toString();
    }

    @Override
    public boolean equals(Object other) {
	if (!(other instanceof QaQuestion)) {
	    return false;
	}
	QaQuestion castOther = (QaQuestion) other;
	return new EqualsBuilder().append(this.getUid(), castOther.getUid()).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(getUid()).toHashCode();
    }

    /**
     * @return Returns the displayOrder.
     */
    public int getDisplayOrder() {
	return displayOrder;
    }

    /**
     * @param required
     * Does this question have to be answered.
     */
    public void setRequired(boolean required) {
	this.required = required;
    }

    /**
     * @return Does this question have to be answered.
     */
    public boolean isRequired() {
	return required;
    }

    /**
     * @param displayOrder
     *                The displayOrder to set.
     */
    public void setDisplayOrder(int displayOrder) {
	this.displayOrder = displayOrder;
    }


    /**
     * @return Returns the qaContent.
     */
    public org.lamsfoundation.lams.tool.qa.QaContent getQaContent() {
	return qaContent;
    }

    /**
     * @param qaContent
     *                The qaContent to set.
     */
    public void setQaContent(org.lamsfoundation.lams.tool.qa.QaContent qaContent) {
	this.qaContent = qaContent;
    }

    /**
     * @return Returns the qaQueUsers.
     */
    public Set getQaQueUsers() {
	if (qaQueUsers == null) {
	    setQaQueUsers(new TreeSet());
	}
	return qaQueUsers;
    }

    /**
     * @param qaQueUsers
     *                The qaQueUsers to set.
     */
    public void setQaQueUsers(Set qaQueUsers) {
	this.qaQueUsers = qaQueUsers;
    }

    /**
     * @return Returns the question.
     */
    public String getQuestion() {
	return question;
    }

    /**
     * @param question
     *                The question to set.
     */
    public void setQuestion(String question) {
	this.question = question;
    }

    public boolean isNull() {
	return false;
    }

    public int compareTo(Object o) {
	//QaQuestion queContent = (QaQuestion) o;

	// if the object does not exist yet, then just return any one of 0, -1, 1. Should not make a difference.
	/*
	 * if (uid == null) return 1; else return (int) (uid.longValue() - queContent.uid.longValue());
	 */
	return 1;
    }

    /**
     * @return Returns the uid.
     */
    public Long getUid() {
	return uid;
    }

    /**
     * @param uid
     *                The uid to set.
     */
    public void setUid(Long uid) {
	this.uid = uid;
    }

    /**
     * @return Returns the feedback.
     */
    public String getFeedback() {
	return feedback;
    }

    /**
     * @param feedback
     *                The feedback to set.
     */
    public void setFeedback(String feedback) {
	this.feedback = feedback;
    }
}
