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
package org.lamsfoundation.lams.learningdesign;

import java.io.Serializable;
import java.util.Set;
import org.lamsfoundation.lams.learningdesign.strategy.BranchingActivityStrategy;

/** 
 * @author Mitchell Seaton
 * @version 2.1
 * 
 * @hibernate.class 
*/
abstract public class BranchingActivity extends ComplexActivity implements Serializable {

	// types are used on the URLS to determine which type of branch is expected 
	// the code should always then check against the activity to make sure it is correct
	public static final String CHOSEN_TYPE = "chosen";
	public static final String GROUP_BASED_TYPE = "group";
	public static final String TOOL_BASED_TYPE = "tool";

	private Integer startXcoord;
	private Integer startYcoord;
	private Integer endXcoord;
	private Integer endYcoord;
	
	/** full constructor */
    public BranchingActivity(Long activityId, 
            Integer id, 
            String description, 
            String title, 
            Integer xcoord, 
            Integer ycoord, 
            Integer orderId, 
            Boolean defineLater, 
            java.util.Date createDateTime, 
            LearningLibrary learningLibrary, 
            Activity parentActivity,
            Activity libraryActivity,
			Integer parentUIID,
            LearningDesign learningDesign, 
            Grouping grouping, 
            Integer activityTypeId,  
            Transition transitionTo,
            Transition transitionFrom,
            String languageFile,
            Integer startXcoord,
            Integer startYcoord,
            Integer endXcoord,
            Integer endYcoord,
            Set activities) {
        super(activityId, 
                id, 
                description, 
                title, 
                xcoord, 
                ycoord, 
                orderId, 
                defineLater, 
                createDateTime, 
                learningLibrary, 
                parentActivity, 
				libraryActivity,
				parentUIID,
                learningDesign, 
                grouping, 
                activityTypeId,  
                transitionTo,
				transitionFrom,
				languageFile,
                activities);
        super.activityStrategy = new BranchingActivityStrategy(this);
        this.startXcoord = startXcoord;
        this.startYcoord = startYcoord;
        this.endXcoord = endXcoord;
        this.endYcoord = endYcoord;
   }

    /** default constructor */
    public BranchingActivity() {
        super.activityStrategy = new BranchingActivityStrategy(this);
    }

    /** minimal constructor */
    public BranchingActivity(Long activityId, 
            Boolean defineLater, 
            java.util.Date createDateTime, 
            org.lamsfoundation.lams.learningdesign.LearningLibrary learningLibrary, 
            org.lamsfoundation.lams.learningdesign.Activity parentActivity, 
            org.lamsfoundation.lams.learningdesign.LearningDesign learningDesign, 
            org.lamsfoundation.lams.learningdesign.Grouping grouping, 
            Integer activityTypeId,  
            Transition transitionTo,
            Transition transitionFrom,
            Set activities) {
      super(activityId, 
              defineLater, 
              createDateTime, 
              learningLibrary, 
              parentActivity, 
              learningDesign, 
              grouping, 
              activityTypeId, 
              transitionTo,
			  transitionFrom,
              activities);
      super.activityStrategy = new BranchingActivityStrategy(this);
    }
    
    /**
     * @see org.lamsfoundation.lams.util.Nullable#isNull()
     */
    public boolean isNull()
    {
        return false;
    }

	/**
	 * @hibernate.property column="end_xcoord" length="11"
	 */
	public Integer getEndXcoord() {
		return endXcoord;
	}

	public void setEndXcoord(Integer endXcoord) {
		this.endXcoord = endXcoord;
	}

	/**
	 * @hibernate.property column="end_ycoord" length="11"
	 */
	public Integer getEndYcoord() {
		return endYcoord;
	}

	public void setEndYcoord(Integer endYcoord) {
		this.endYcoord = endYcoord;
	}

	/**
	 * @hibernate.property column="start_xcoord" length="11"
	 */
	public Integer getStartXcoord() {
		return startXcoord;
	}

	public void setStartXcoord(Integer startXcoord) {
		this.startXcoord = startXcoord;
	}

	/**
	 * @hibernate.property column="start_ycoord" length="11"
	 */
	public Integer getStartYcoord() {
		return startYcoord;
	}

	public void setStartYcoord(Integer startYcoord) {
		this.startYcoord = startYcoord;
	}
	
	

}
