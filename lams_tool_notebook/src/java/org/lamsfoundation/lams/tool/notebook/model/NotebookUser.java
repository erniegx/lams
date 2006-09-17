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

package org.lamsfoundation.lams.tool.notebook.model;

import org.lamsfoundation.lams.usermanagement.dto.UserDTO;

/**
 * 
 * Caches the user details. This allows the tool to be more efficient at
 * displaying user names but means that when people's names change, they won't
 * change in the "old" tool data.
 * 
 * @hibernate.class table="tl_lantbk11_user"
 */

public class NotebookUser implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3701664859818409197L;

	// Fields
	private Long uid;

	private Long userId;

	private String lastName;

	private String firstName;

	private String loginName;

	private NotebookSession notebookSession;
	
	private Boolean finishedActivity;
	
	private Long entryUID;

	// Constructors

	/** default constructor */
	public NotebookUser() {
	}

	public NotebookUser(UserDTO user, NotebookSession notebookSession) {
		this.userId = new Long(user.getUserID().intValue());
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.loginName = user.getLogin();
		this.notebookSession = notebookSession;
		this.finishedActivity = false;
	}

	/** full constructor */
	public NotebookUser(Long userId, String lastName, String firstName,
			NotebookSession notebookSession) {
		this.userId = userId;
		this.lastName = lastName;
		this.firstName = firstName;
		this.notebookSession = notebookSession;
	}

	// Property accessors
	/**
	 * @hibernate.id generator-class="native" type="java.lang.Long" column="uid"
	 */
	public Long getUid() {
		return this.uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	/**
	 * @hibernate.property column="user_id" length="20"
	 * 
	 */
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @hibernate.property column="last_name" length="255"
	 * 
	 */
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * @hibernate.property column="login_name" length="255"
	 * 
	 */
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @hibernate.property column="first_name" length="255"
	 * 
	 */
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * @hibernate.property column="finishedActivity" 
	 */
	public Boolean getFinishedActivity() {
		return finishedActivity;
	}

	public void setFinishedActivity(Boolean finishedActivity) {
		this.finishedActivity = finishedActivity;
	}
	
	/**
	 * @hibernate.many-to-one not-null="true"
	 * @hibernate.column name="notebook_session_uid"
	 * 
	 */
	public NotebookSession getNotebookSession() {
		return this.notebookSession;
	}

	public void setNotebookSession(NotebookSession notebookSession) {
		this.notebookSession = notebookSession;
	}

	/**
	 * @hibernate.property column="entry_uid"
	 */
	public Long getEntryUID() {
		return entryUID;
	}

	public void setEntryUID(Long entryUID) {
		this.entryUID = entryUID;
	}
	
	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(
				Integer.toHexString(hashCode())).append(" [");
		buffer.append("userId").append("='").append(getUserId()).append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof NotebookUser))
			return false;
		NotebookUser castOther = (NotebookUser) other;

		return ((this.getUid() == castOther.getUid()) || (this.getUid() != null
				&& castOther.getUid() != null && this.getUid().equals(
				castOther.getUid())));
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result
				+ (getUid() == null ? 0 : this.getUid().hashCode());
		return result;
	}
}