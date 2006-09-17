/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
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

/* $Id$ */

package org.lamsfoundation.lams.tool.notebook.dto;

import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.tool.notebook.model.NotebookUser;
import org.lamsfoundation.lams.tool.notebook.service.INotebookService;
import org.lamsfoundation.lams.tool.notebook.service.NotebookServiceProxy;

public class NotebookUserDTO implements Comparable{
	
	public Long uid;
	
	public String loginName;
	
	public String firstName;
	
	public String lastName;
	
	public NotebookEntryDTO entryDTO;

	public Long entryUID;
	
	public NotebookUserDTO(NotebookUser user, NotebookEntry entry) {
		this.uid = user.getUid();
		this.loginName = user.getLoginName();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.entryUID = user.getEntryUID();
		this.entryDTO = new NotebookEntryDTO(entry);
	}
	
	public NotebookUserDTO(NotebookUser user) {
		this.uid = user.getUid();
		this.loginName = user.getLoginName();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.entryUID = user.getEntryUID();		
	}

	public int compareTo(Object o) {
		int returnValue;
		NotebookUserDTO toUser = (NotebookUserDTO)o;
		returnValue = this.lastName.compareTo(toUser.lastName);
		if (returnValue == 0) {
			returnValue = this.uid.compareTo(toUser.uid);			
		}
		return returnValue;		
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public NotebookEntryDTO getEntryDTO() {
		return entryDTO;
	}

	public void setEntryDTO(NotebookEntryDTO entryDTO) {
		this.entryDTO = entryDTO;
	}

	public Long getEntryUID() {
		return entryUID;
	}

	public void setEntryUID(Long entryUID) {
		this.entryUID = entryUID;
	}
}
