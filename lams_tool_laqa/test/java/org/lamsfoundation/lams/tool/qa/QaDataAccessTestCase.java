/***************************************************************************
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
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.tool.qa;

import java.util.Date;
import java.util.TreeSet;

import org.lamsfoundation.lams.test.AbstractLamsTestCase;
import org.lamsfoundation.lams.tool.qa.dao.hibernate.QaContentDAO;
import org.lamsfoundation.lams.tool.qa.dao.hibernate.QaQueContentDAO;
import org.lamsfoundation.lams.tool.qa.dao.hibernate.QaQueUsrDAO;
import org.lamsfoundation.lams.tool.qa.dao.hibernate.QaSessionDAO;
import org.lamsfoundation.lams.tool.qa.dao.hibernate.QaUsrRespDAO;



/**
 * @author ozgurd
 */
public class QaDataAccessTestCase extends AbstractLamsTestCase
{
	//These both refer to the same entry in the db.
	protected final long TEST_EXISTING_CONTENT_ID = 10;
	protected final long DEFAULT_CONTENT_ID = 10;
	 
	protected final long TEST_NEW_CONTENT_ID = 11;
	
	protected final long TEST_EXISTING_SESSION_ID = 101;
	protected final long TEST_NEW_SESSION_ID = 102;
	
	protected final long TEST_NEW_USER_ID = 700;
	protected final long TEST_EXISTING_QUE_CONTENT_ID = 20;
	protected final long TEST_NEW_QUE_CONTENT_ID = 23;
	
	protected final long TEST_NONEXISTING_CONTENT_ID=2475733396382404l;
	
    protected final long ONE_DAY = 60 * 60 * 1000 * 24;
    
    public final String NOT_ATTEMPTED = "NOT_ATTEMPTED";
    public final String INCOMPLETE = "INCOMPLETE";
    public static String COMPLETED = "COMPLETED";
	
	protected QaContentDAO qaContentDAO;
	protected QaSessionDAO qaSessionDAO;
	protected QaQueUsrDAO  qaQueUsrDAO;
	protected QaQueContentDAO qaQueContentDAO;
	protected QaUsrRespDAO  qaUsrRespDAO;
	
	
	protected QaSession qaSession;
	protected QaQueContent qaQueContent;
	
	
	public QaDataAccessTestCase(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        qaContentDAO = (QaContentDAO) this.context.getBean("qaContentDAO");
        qaSessionDAO = (QaSessionDAO) this.context.getBean("qaSessionDAO");
        qaQueUsrDAO = (QaQueUsrDAO) this.context.getBean("qaQueUsrDAO");
        qaQueContentDAO = (QaQueContentDAO) this.context.getBean("qaQueContentDAO");
        qaUsrRespDAO = (QaUsrRespDAO) this.context.getBean("qaUsrRespDAO");
        
    }

    protected String[] getContextConfigLocation()
    {
        return new String[] {"/org/lamsfoundation/lams/tool/qa/testqaApplicationContext.xml" };
    }
    
    protected String getHibernateSessionFactoryName()
    {
    	return "qaSessionFactory";
    }
    
    protected void tearDown() throws Exception
    {
    	super.tearDown();
    }
    
    /*
    protected QaQueUsr getExistingUser(String username, String fullname)
    {
    	QaQueContent qaQueContent = qaQueContentDAO.getQaQueById(TEST_EXISTING_QUE_CONTENT_ID);
		QaSession qaSession = qaSessionDAO.getQaSessionById(TEST_EXISTING_SESSION_ID);
    	
        QaQueUsr qaQueUsr= new QaQueUsr(new Long(TEST_NEW_USER_ID),
    									username,
										fullname,
										qaQueContent, 
										qaSession, 
										new TreeSet());
    	qaQueUsrDAO.createUsr(qaQueUsr);
    	return qaQueUsr;
    }
    
    
    protected QaUsrResp getNewResponse(String response, QaQueContent qaQueContent)
    {
    	QaUsrResp qaUsrResp= new QaUsrResp(response, false,
											new Date(System.currentTimeMillis()),
											"",
											qaQueContent,
											getExistingUser("randomstriker","Michael Random"));
		qaUsrRespDAO.createUserResponse(qaUsrResp);
		return qaUsrResp;
    }
    */
    
    
    public void  testInitDB()
    {
    	QaContent qa = new QaContent();
    	qa.setQaContentId(new Long(TEST_NEW_CONTENT_ID+1));
    	qa.setTitle("New - Put Title Here");
    	qa.setInstructions("New - Put instructions here.");
    	qa.setCreationDate(new Date(System.currentTimeMillis()));
    	qa.setCreatedBy(0);
        qa.setUsernameVisible(false);
        qa.setDefineLater(false);
        qa.setSynchInMonitor(false);
        qa.setOnlineInstructions("New- online instructions");
        qa.setOfflineInstructions("New- offline instructions");
        qa.setReportTitle("New-Report title");
        qa.setQaQueContents(new TreeSet());
        qa.setQaSessions(new TreeSet());
        
        //create new qa que content
        QaQueContent qaQueContent = new QaQueContent("What planet are you from",
        											4, 
    												qa, 
    												new TreeSet(), 
    												new TreeSet());
        
        qa.getQaQueContents().add(qaQueContent);
        
        //create the new content
        qaContentDAO.createQa(qa);
    }
    

}
