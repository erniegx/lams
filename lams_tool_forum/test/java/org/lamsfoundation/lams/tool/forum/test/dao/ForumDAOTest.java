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

package org.lamsfoundation.lams.tool.forum.test.dao;

import org.lamsfoundation.lams.tool.forum.persistence.Forum;
import org.lamsfoundation.lams.tool.forum.test.DAOBaseTest;
import org.lamsfoundation.lams.tool.forum.test.TestUtils;

public class ForumDAOTest extends DAOBaseTest{

	public ForumDAOTest(String name) {
		super(name);
	}
	
	public void testSave(){
		Forum forum = TestUtils.getForumA();
		forumDao.saveOrUpdate(forum);
		
		Forum tForum = forumDao.getById(forum.getUid());
		assertEquals(tForum.getContentId(),new Long(1));
		
		//remove test data
		forumDao.delete(forum);
	}
	
	public void testDelete(){
		Forum forum = TestUtils.getForumA();
		forumDao.saveOrUpdate(forum);
			
		forumDao.delete(forum);
		
		assertNull(forumDao.getById(forum.getUid()));
	}
	public void testGetByContentId(){
		
		Forum forum = TestUtils.getForumA();
		
		forumDao.saveOrUpdate(forum);
		Forum tforum = forumDao.getByContentId(forum.getContentId());
		assertEquals(tforum, forum);
		
		//remove test data
		forumDao.delete(forum);
		
	}

}
