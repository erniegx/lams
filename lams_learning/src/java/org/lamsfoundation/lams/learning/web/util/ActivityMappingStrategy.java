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

package org.lamsfoundation.lams.learning.web.util;

import java.io.Serializable;

import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.lesson.LearnerProgress;

/**
 * @author daveg
 *
 */
public class ActivityMappingStrategy implements Serializable {
	
	/**
	 * Returns the struts action used to display the specified activity.
	 * @param activity, Activity to be displayed
	 * @param progress, LearnerProgress for the activity, used to check activity status
	 * @return String representing a struts action
	 */
	protected String getActivityAction(Activity activity, LearnerProgress progress) {
		String strutsAction = null;
		
		if ( activity.isComplexActivity() ) {
			if ( activity.isParallelActivity() )
		        strutsAction = "/DisplayParallelActivity.do";
		    else if (activity.isOptionsActivity()) 
		        strutsAction = "/DisplayOptionsActivity.do";
		}
		else // should be a simple activity 
		{
			if ( activity.isGroupingActivity()) {
			    strutsAction = "/grouping.do?";
			} 
			else if (activity.isGateActivity()) {
			    strutsAction = "/gate.do";
				// not completed so return wait URL
			}
			else if (activity.isToolActivity()) { 
			    strutsAction = "/LoadToolActivity.do";
				//strutsAction = "/DisplayToolActivity.do";
			}
		}
		return strutsAction;
	}

	/**
	 * Returns the struts action for displaying the parallel wait.
	 */
	protected String getWaitingAction() {
		return "/parallelWait.do";
	}

	/**
	 * Returns the struts action for displaying lesson complete.
	 */
	protected String getLessonCompleteAction() {
		return "/lessonComplete.do";
	}
	
}
