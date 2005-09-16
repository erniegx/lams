/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/

package org.lamsfoundation.lams.util;

import junit.framework.TestCase;
import org.lamsfoundation.lams.util.FileUtil;
import org.lamsfoundation.lams.util.FileUtilException;
import java.io.File;
import java.io.IOException;

/**
 * @author mtruong
 */
public class TestFileUtil extends TestCase {
	
	private String tempSysDirName = System.getProperty("java.io.tmpdir");
	private String dirName = "testDirectory";
	
	public void testCreateDirectory() throws FileUtilException
	{
		System.out.println("******************************" + tempSysDirName);
		String dirName = tempSysDirName + "MaiTest";
		System.out.println(dirName);
		boolean created = FileUtil.createDirectory(dirName);
		assertTrue(created);
		
		File existingDir = new File(dirName);
		assertTrue(existingDir.exists());
		
		boolean deleted = FileUtil.deleteDirectory(new File(dirName));
		assertTrue(deleted);
		
		//test when supplied directoryName is null
		
		String directoryName = null;
		try
		{
			FileUtil.createDirectory(directoryName);
			fail("An exception should have been raised as the directoryName supplied is null");
		}
		catch(FileUtilException e)
		{
			assertTrue(true);
		}
		
		//	test when supplied directoryName is empty
		
		directoryName = "";
		try
		{
			FileUtil.createDirectory(directoryName);
			fail("An exception should have been raised as the directoryName supplied is an empty string");
		}
		catch(FileUtilException e)
		{
			assertTrue(true);
		}
		

	}
	

	public void testRemoveTrailingForwardSlash()
	{
		String stringToTest = "/testDirectory/";
		String endResult = FileUtil.removeTrailingForwardSlash(stringToTest);
		assertEquals(endResult, "/testDirectory");
		
	}
	
	public void testTrailingForwardSlashPresent()
	{
		String testString1 = "/testDirectory/";
		boolean isSlashAtEndOfString = FileUtil.trailingForwardSlashPresent(testString1);
		assertTrue(isSlashAtEndOfString);
		
		String testString2 = "/testDir";
		boolean isSlashAtEndOfTestString2 = FileUtil.trailingForwardSlashPresent(testString2);
		assertFalse(isSlashAtEndOfTestString2);
		
	}
	
	public void testCreateDirectoryWithTwoParameters() throws FileUtilException
	{
		boolean isCreated;
		boolean isDirDeleted;
		
		/*
		 * Case 1: both the parent and child directory names are valid
		 */
		String parentDir = "parentDirectory";
		String subDir = "subdirectory";
		String combinedPath = parentDir + "/" + subDir;
		
		isCreated = FileUtil.createDirectory(parentDir, subDir);
		assertTrue(isCreated);
		
		File dir = new File(combinedPath);
		assertTrue(dir.exists());
		
		isDirDeleted = FileUtil.deleteDirectory(new File(parentDir));
		assertEquals("Checking both parent and subdirectory is deleted", isDirDeleted,true);
	
		/*
		 * Case 2: parent directory name has a slash at the beginning,
		 * child directory name is valid
		 * eg "/parent", "child"
		 */
		String parentDir2 = "/parentDirectory";
		isCreated = FileUtil.createDirectory(parentDir2, subDir);
		assertEquals("Assert the parent + subdirectory are created", isCreated, true);
	
		isDirDeleted = FileUtil.deleteDirectory(new File(parentDir2));
		assertEquals("Checking both parent and subdirectory is deleted", isDirDeleted,true);
		
		/*
		 * Case 3: both parent and child directory names have the slash at front
		 * eg. "/parent" "/child"
		 */
		String parentDir3 = "/parentDirectory2";
		String childDir3 = "/subDirectory";
		isCreated = FileUtil.createDirectory(parentDir3, childDir3);
		assertTrue(isCreated);

		isDirDeleted = FileUtil.deleteDirectory(new File(parentDir3));
		assertEquals("Checking both parent and subdirectory is deleted", isDirDeleted,true);
		
		/*
		 * Case 4: parent has a slash at the end of name
		 * eg. "/parent/" "child"
		 */
		String parentDir4 = "/parentDirectory4/";
		String childDir4 = "/subDirectory";
		isCreated = FileUtil.createDirectory(parentDir4, childDir4);
		assertTrue(isCreated);
		
		isDirDeleted = FileUtil.deleteDirectory(new File(parentDir4));
		assertEquals("Checking both parent and subdirectory is deleted", isDirDeleted,true);
		
		
		/*
		 * Case 5: creating a sub sub directory
		 */
		String basePath = "/root Dir";
		String subDirectory = "/subDir";
		String subsubDir = "/subsubDirectory";
		isCreated = FileUtil.createDirectory(basePath+subDirectory, subsubDir);
		assertEquals("Checking if /parentDirectory/subdirectory/subsubDirectory was created", isCreated, true);
		
		isDirDeleted = FileUtil.deleteDirectory(new File(basePath));
		assertEquals("Checking both parent and subdirectory is deleted", isDirDeleted,true);
	
		//remember to delete directories that were created
	}
	
	public void testDeleteDirectory() throws FileUtilException, IOException
	{
		//create directory
		boolean isCreated;
		boolean isFileCreated;
		boolean isDeleted;
		
		String rootDir = "/testDir";
		String subDirName = "subDir";
		String subDirPath = rootDir + "/" + subDirName;
		isCreated = FileUtil.createDirectory(rootDir);
		assertTrue(isCreated);
		
		//now add some files in the directory
		File file1 = new File(rootDir, "1.txt");
		isFileCreated = file1.createNewFile();
		assertTrue(isFileCreated);
		
		File file2 = new File(rootDir, "2.txt");
		isFileCreated = file2.createNewFile();
		assertTrue(isFileCreated);
		
		//create a subdirectory
		isCreated = FileUtil.createDirectory(rootDir, subDirName);
		assertTrue(isCreated);
		
		//add a file in the subdirectory
		File file3 = new File(subDirPath, "3.txt");
		isFileCreated = file3.createNewFile();
		assertTrue(isFileCreated);
			
		//now delete the rootDir
		isDeleted = FileUtil.deleteDirectory(new File(rootDir));
		assertTrue(isDeleted);
			
	}
	
	public void testDeleteDirectoryWithStringParameter() throws FileUtilException, IOException
	{
		//	create directory
		boolean isCreated;
		boolean isDeleted;
		
		String rootDir = "/testDir";
		
		isCreated = FileUtil.createDirectory(rootDir);
		assertTrue(isCreated);
		
		isDeleted = FileUtil.deleteDirectory(rootDir);
		assertTrue(isDeleted);
		
	}
	
		
	
	

}
