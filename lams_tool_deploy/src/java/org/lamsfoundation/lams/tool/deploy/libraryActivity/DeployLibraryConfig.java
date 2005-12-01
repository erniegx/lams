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

/*
 * Created on 24/11/2005
 *
 */
package org.lamsfoundation.lams.tool.deploy.libraryActivity;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lamsfoundation.lams.tool.deploy.DeployConfig;
import org.lamsfoundation.lams.tool.deploy.DeployException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author mtruong
 *
 * Encapsulates configuration data for the Library deployer
 */
public class DeployLibraryConfig extends DeployConfig {
    
    private static Log log = LogFactory.getLog(DeployLibraryConfig.class);
    
    private static final String LEARNING_LIBRARY = "learningLibrary";
    private static final String LIBRARY_INSERT_SCRIPT = "libraryInsertScriptPath";
    private static final String TEMPLATE_ACTIVITY_INSERT_SCRIPT = "templateActivityInsertScriptPath";
    private static final String TOOL_ACTIVITY = "toolActivity";
    private static final String LEARNING_LIBRARY_LIST = "learningLibraryList";
    private static final String TOOL_ACTIVITY_LIST = "toolActivityList";
       
    private ArrayList learningLibraryList; 
    
    public DeployLibraryConfig()
    {       
        xstream.alias(ROOT_ELEMENT, DeployLibraryConfig.class);
        xstream.alias(LEARNING_LIBRARY, LearningLibrary.class);
        xstream.alias(TOOL_ACTIVITY, ToolActivity.class);
    }
    
    public DeployLibraryConfig(	String dbUsername, 
            					String dbPassword,
            					String dbDriverClass,
            					String dbDriverUrl,
            					ArrayList learningLibraries)
    {
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setDbDriverClass(dbDriverClass);
        setDbDriverUrl(dbDriverUrl);
        this.learningLibraryList = learningLibraries;
        
        xstream.alias(ROOT_ELEMENT, DeployLibraryConfig.class);
        xstream.alias(LEARNING_LIBRARY, LearningLibrary.class);
        xstream.alias(TOOL_ACTIVITY, ToolActivity.class);
    }
    
    public DeployLibraryConfig(String configurationFilePath) throws ParserConfigurationException, IOException, SAXException
    {
        super();
        xstream.alias(ROOT_ELEMENT, DeployLibraryConfig.class);
        xstream.alias(LEARNING_LIBRARY, LearningLibrary.class);
        xstream.alias(TOOL_ACTIVITY, ToolActivity.class);
        updateConfigurationProperties(configurationFilePath);
    }
    
    /**
     * Takes in the file path location of the XML configuration file.
     * Parses the configuration file and will create 
     * LearningLibrary object(s) and ToolActivity object(s).
     * 
     * @param configFilePath the file path for the XML configuration file
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public void updateConfigurationProperties(String configFilePath) throws ParserConfigurationException, IOException, SAXException
    {       
        String xml = readFile(configFilePath);
        DeployLibraryConfig config = (DeployLibraryConfig)deserialiseXML(xml);        
        copyProperties(config);
    }
   
    /**
     *  Upon deserialisation of the xml string, a new object will be created. 
	 * 	The properties of this object will be copied to the calling object.
	 * 	Only copy properties if the properties are not null
     *  @param config
     */
    private void copyProperties(DeployLibraryConfig config)
    {
        if (config.getDbUsername() != null)
            this.setDbUsername(config.getDbUsername());
        if (config.getDbPassword() != null)
	        this.setDbPassword(config.getDbPassword());
        if (config.getDbDriverUrl() != null)
	        this.setDbDriverUrl(config.getDbDriverUrl());
        if (config.getDbDriverClass() != null)
	        this.setDbDriverClass(config.getDbDriverClass());   
        if (config.getLearningLibraryList() != null)
	        this.setLearningLibraryList(config.getLearningLibraryList());
    }
    
    public void printObjectProperties()
    {
        System.out.println("========Object Properties=======");
        System.out.println("DbUsername: " + getDbUsername());
        System.out.println("DbPassword: " + getDbPassword());
        System.out.println("DbDriverClass: " + getDbDriverClass());
        System.out.println("DbDriverUrl: " + getDbDriverUrl());  
        ArrayList learningLibraries = getLearningLibraryList();
        for (int j=0; j<learningLibraries.size(); j++)
        {
            LearningLibrary libraryActivity = (LearningLibrary)learningLibraries.get(j);
            System.out.println("\t Learning Library " + j + "-> libraryInsertScriptPath: " + libraryActivity.getLibraryInsertScriptPath());
            System.out.println("\t Learning Library " + j + "-> templateActivityInsertScriptPath: " + libraryActivity.getTemplateActivityInsertScriptPath());
            
            ArrayList list = libraryActivity.getToolActivityList();
            for (int i=0; i< list.size(); i++)
            {
               ToolActivity a = (ToolActivity)list.get(i);
               System.out.println("\t\tToolActivity "+ i + "-> ToolSignature: " + a.getToolSignature());
               System.out.println("\t\tToolActivity "+ i + "-> ToolActivityScriptPath: " + a.getToolActivityInsertScriptPath());
            }        
        }
        System.out.println("========End Object Properties=======");
    }
    
    public void validateProperties() throws DeployException {
        boolean valid;
        validationError = ""; // object attribute - will be updated by validateProperty() if something is missing.

        valid = validateStringProperty(getDbUsername(), DB_USERNAME);
        valid = valid && validateStringProperty(getDbPassword(), DB_PASSWORD);
        valid = valid && validateStringProperty(getDbDriverClass(), DB_PASSWORD);
        valid = valid && validateStringProperty(getDbDriverUrl(), DB_DRIVER_URL);
        valid = valid && validateListProperty(getLearningLibraryList(),LEARNING_LIBRARY_LIST);
        
        //iterate through learning libraries
        ArrayList learningLibraries = getLearningLibraryList();
        if (learningLibraries != null)
        {
	        Iterator libraryIterator = learningLibraries.iterator();
	        while (libraryIterator.hasNext())
	        {
	            LearningLibrary learningLibrary = (LearningLibrary)libraryIterator.next();
	            valid = valid && validateStringProperty(learningLibrary.getLibraryInsertScriptPath(), LIBRARY_INSERT_SCRIPT);
	            valid = valid && validateStringProperty(learningLibrary.getTemplateActivityInsertScriptPath(), TEMPLATE_ACTIVITY_INSERT_SCRIPT);
	            ArrayList toolActivities = learningLibrary.getToolActivityList();
	            
	            valid = valid && validateListProperty(toolActivities, TOOL_ACTIVITY_LIST);
	            if (toolActivities != null)
	            {
		            Iterator toolActivityIterator = toolActivities.iterator();		            
		            while (toolActivityIterator.hasNext())
		            {
		                ToolActivity toolActivity = (ToolActivity)toolActivityIterator.next();
		                valid = valid && validateStringProperty(toolActivity.getToolActivityInsertScriptPath(), TOOL_ACTIVITY_INSERT_SCRIPT_PATH);
		                valid = valid && validateStringProperty(toolActivity.getToolSignature(), TOOL_SIGNATURE);
		            }
	            }
	            
	        }
        }
        
      
        if ( ! valid )
            throw new DeployException("Invalid deployment properties: "+validationError);
    }
    
    /* public void writePropertiesToFile(Writer writer)
    {
        xstream.toXML(this, writer);       
    } */
    
    /*protected void setProperty(String key, String value) throws DeployException {
        if ( key == null )
            throw new DeployException("Invalid parameter: Key is null. ");
   
        //super.setProperty(key, value);
        
        System.out.println("LibraryConfig " + key + " is: " + value);
        
        if ( key.equalsIgnoreCase(DB_USERNAME) ) {
            setDbUsername(value);
        }

        if ( key.equalsIgnoreCase(DB_PASSWORD) ) {
            setDbPassword(value);
        }

        if ( key.equalsIgnoreCase(DB_DRIVER_CLASS) ) {
            setDbDriverClass(value);
        }

        if ( key.equalsIgnoreCase(DB_DRIVER_URL) ) {
            setDbDriverUrl(value);
        }       
    } */
    
    
    /**
     * @return Returns the learningLibraryList.
     */
    public ArrayList getLearningLibraryList() {
        return learningLibraryList;
    }
    /**
     * @param learningLibraryList The learningLibraryList to set.
     */
    public void setLearningLibraryList(ArrayList learningLibraryList) {
        this.learningLibraryList = learningLibraryList;
    }
}
