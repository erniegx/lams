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

/* $$Id$$ */	

package org.lamsfoundation.lams.tool.sbmt;

import java.io.Serializable;

import org.apache.log4j.Logger;
/** 
 * @hibernate.class table="tl_lasbmt11_instruction_files"
 * @serial  3555065437595925246L
*/
public class InstructionFiles implements Serializable,Cloneable{

	private static final long serialVersionUID = 3555065437595925246L;
	private static Logger log = Logger.getLogger(InstructionFiles.class);
	
	private Long uid;
	private Long uuID;
	private Long versionID;
	private String name;
	private String type;

	/**
     * @hibernate.id generator-class="identity" type="java.lang.Long" column="uid"
	 * @return Returns the uid.
	 */
	public Long getUid() {
		return uid;
	}
	/**
	 * @param uid The uid to set.
	 */
	public void setUid(Long fileID) {
		this.uid = fileID;
	}
	/**
	 * 
     * @hibernate.property column="uuid" length="20"
	 * @return Returns the uuID.
	 */
	public Long getUuID() {
		return uuID;
	}
	/**
	 * @param uuID The uuID to set.
	 */
	public void setUuID(Long uuID) {
		this.uuID = uuID;
	}
	/**
	 * 
     * @hibernate.property column="version_id" length="20"
	 * @return Returns the versionID.
	 */
	public Long getVersionID() {
		return versionID;
	}
	/**
	 * @param versionID The versionID to set.
	 */
	public void setVersionID(Long versionID) {
		this.versionID = versionID;
	}
    public Object clone(){
		Object obj = null;
		try {
			obj = super.clone();
			//never clone key!
			((InstructionFiles)obj).setUid(null);
		} catch (CloneNotSupportedException e) {
			log.error("When clone " + InstructionFiles.class + " failed");
		}
		
		return obj;
	}
	/**
	 * @hibernate.property column="type" length="20"
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @hibernate.property column="name" length="255"
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
