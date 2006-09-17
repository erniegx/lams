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
package org.lamsfoundation.lams.tool.rsrc.util;

import org.lamsfoundation.lams.tool.rsrc.model.Resource;
import org.lamsfoundation.lams.tool.rsrc.web.action.MonitoringAction;



/**
 * Contains helper methods used by the Action Servlets
 * 
 * @author Anthony Sukkar
 *
 */
public class ResourceWebUtils {

	public static boolean isResourceEditable(Resource resource) {
	        if ( (resource.isDefineLater() == true) && (resource.isContentInUse()==true) )
	        {
	//            throw new ResourceApplicationException("An exception has occurred: There is a bug in this tool, conflicting flags are set");
	        	 MonitoringAction.log.error("An exception has occurred: There is a bug in this tool, conflicting flags are set");
	             return false;
	        }
	        else if ( (resource.isDefineLater() == true) && (resource.isContentInUse() == false))
	            return true;
	        else if ( (resource.isDefineLater() == false) && (resource.isContentInUse() == false))
	            return true;
	        else //  (content.isContentInUse()==true && content.isDefineLater() == false)
	            return false;
		}
	
}
