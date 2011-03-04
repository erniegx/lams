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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA 
 * 
 * http://www.gnu.org/licenses/gpl.txt 
 * **************************************************************** 
 */  
 
/* $Id$ */  
package org.lamsfoundation.lams.tool.forum.persistence;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.lamsfoundation.lams.tool.forum.dto.AverageRatingDTO;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * DAO interface for <code>MessageRating</code>..
 * 
 * @author Andrey Balan
 * @see org.lamsfoundation.lams.tool.forum.persistence.MessageRating
 */
public class MessageRatingDAO  extends HibernateDaoSupport {

    private static final String FIND_BY_MESSAGE_AND_USER = "from " + MessageRating.class.getName()
	    + " as r where r.user.userId = ? and r.message.uid=?";

    private static final String FIND_BY_MESSAGE_ID = "from " + MessageRating.class.getName()
	    + " as r where r.message.uid=?";
    
    private static final String FIND_AVERAGE_RATING_BY_MESSAGE = "SELECT AVG(r.rating), COUNT(*) from "
	    + MessageRating.class.getName() + " as r where r.message.uid=?";

    /**
     * Return responseRating by the given imageUid and userId.
     * 
     * @param messageId
     * @param userId
     * @return
     */
    public MessageRating getRatingByMessageAndUser(Long messageId, Long userId) {
	List list = getHibernateTemplate().find(FIND_BY_MESSAGE_AND_USER, new Object[] { userId, messageId });
	if (list == null || list.size() == 0)
	    return null;
	return (MessageRating) list.get(0);
    }

    /**
     * Return list of responseRating by the the given imageUid.
     * 
     * @param messageId
     * @param userId
     * @return
     */
    public List<MessageRating> getRatingsByMessage(Long messageId) {
	return getHibernateTemplate().find(FIND_BY_MESSAGE_ID, messageId);
    }

    /**
     * Returns rating statistics by particular message
     * 
     * @param messageId
     * @return
     */
    public AverageRatingDTO getAverageRatingDTOByMessage(Long messageId) {
	List<Object[]> list = getHibernateTemplate().find(FIND_AVERAGE_RATING_BY_MESSAGE, new Object[] { messageId });
	Object[] results = list.get(0);
	
	Object averageRatingObj = (results[0] == null) ? 0 : results[0];
	NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
	numberFormat.setMaximumFractionDigits(1);	
	String averageRating = numberFormat.format(averageRatingObj);
	
	String numberOfVotes = (results[1] == null) ? "0" : String.valueOf(results[1]);
	return new AverageRatingDTO(averageRating, numberOfVotes);
    }

    /**
     * Generic method to save an object - handles both update and insert.
     * 
     * @param o
     *            the object to save
     */
    public void saveObject(Object o) {
	super.getHibernateTemplate().saveOrUpdate(o);
    }

}
