/* 
  Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
  USA

  http://www.gnu.org/licenses/gpl.txt 
*/

package org.lamsfoundation.lams.usermanagement.exception;

import org.lamsfoundation.lams.usermanagement.User;

/**
 * The current ticket doesn't have sufficient rights for the requested action.
 */
public class UserAccessDeniedException extends Exception {
	
   /**
	* Constructs a new instance of this class.
	*/
    public UserAccessDeniedException() {
    	this("Access Denied for user");
	}
	
    /**
	* Constructs a new instance of this class given a message describing the
	* failure cause.
	*
	* @param s description
	*/
	public UserAccessDeniedException(String s) {
		super(s);
	}
	
   /**
	* Constructs a new instance of this class given a message describing the
	* failure and a root throwable.
	*
	* @param s description
	* @param cause root throwable cause
	*/
   public UserAccessDeniedException(String s, Throwable cause) {
   		super(s,cause);
	
   }
	
   /**
	* Constructs a new instance of this class given a root throwable.
	*
	* @param cause root failure cause
	*/
   public UserAccessDeniedException(Throwable cause) {
	   super("Access Denied for user", cause);
	   }
	
   /**
	* Constructs a new instance of this class.
	*/
   public UserAccessDeniedException(User user) {
	   super("Access Denied for user userID="+user.getUserId().toString());
   }
	
  /**
	* Constructs a new instance of this class given a root throwable.
	*
	* @param cause root failure cause
	*/
  public UserAccessDeniedException(User user, Throwable cause) {
	  super("Access Denied for user userID="+user.getUserId().toString(), cause);
	   }
	
}
