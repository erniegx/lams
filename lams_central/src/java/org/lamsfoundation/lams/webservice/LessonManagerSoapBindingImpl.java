/****************************************************************
 * Copyright (C) 2006 LAMS Foundation (http://lamsfoundation.org)
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

package org.lamsfoundation.lams.webservice;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.lamsfoundation.lams.integration.ExtCourseClassMap;
import org.lamsfoundation.lams.integration.ExtServerOrgMap;
import org.lamsfoundation.lams.integration.ExtUserUseridMap;
import org.lamsfoundation.lams.integration.security.Authenticator;
import org.lamsfoundation.lams.integration.service.IntegrationService;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.monitoring.service.IMonitoringService;
import org.lamsfoundation.lams.monitoring.service.MonitoringServiceProxy;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.DateUtil;
import org.lamsfoundation.lams.web.util.HttpSessionManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * <a href="LessonManagerSoapBindingImpl.java.html"><i>View Source</i><a>
 * </p>
 * 
 * @author <a href="mailto:fyang@melcoe.mq.edu.au">Fei Yang</a>
 */
public class LessonManagerSoapBindingImpl implements LessonManager {
	private static IntegrationService service = (IntegrationService) WebApplicationContextUtils
			.getRequiredWebApplicationContext(HttpSessionManager.getInstance().getServletContext())
			.getBean("integrationService");

	private static IMonitoringService monitoringService = MonitoringServiceProxy
			.getMonitoringService(HttpSessionManager.getInstance().getServletContext());

	public Long startLesson(String serverId, String datetime, String hashValue, String username,
			long ldId, String courseId, String title, String desc, String countryIsoCode,
			String langIsoCode) throws RemoteException {
		try {
			ExtServerOrgMap serverMap = service.getExtServerOrgMap(serverId);
			Authenticator.authenticate(serverMap,datetime,username,hashValue);
			ExtUserUseridMap userMap = service.getExtUserUseridMap(serverMap, username);
			ExtCourseClassMap orgMap = service.getExtCourseClassMap(serverMap, userMap, courseId,
					countryIsoCode, langIsoCode);
			// 1. init lesson
			Lesson lesson = monitoringService.initializeLesson(title, desc, ldId, orgMap
					.getOrganisation().getOrganisationId(), userMap.getUser().getUserId());
			// 2. create lessonClass for lesson
			createLessonClass(lesson, orgMap.getOrganisation(), userMap.getUser());
			// 3. start lesson
			monitoringService.startLesson(lesson.getLessonId(), userMap.getUser().getUserId());
			return lesson.getLessonId();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
	}

	public Long scheduleLesson(String serverId, String datetime, String hashValue, String username,
			long ldId, String courseId, String title, String desc, String startDate,
			String countryIsoCode, String langIsoCode) throws RemoteException {
		try {
			ExtServerOrgMap serverMap = service.getExtServerOrgMap(serverId);
			Authenticator.authenticate(serverMap,datetime,username,hashValue);
			ExtUserUseridMap userMap = service.getExtUserUseridMap(serverMap, username);
			ExtCourseClassMap orgMap = service.getExtCourseClassMap(serverMap, userMap, courseId,
					countryIsoCode, langIsoCode);
			// 1. init lesson
			Lesson lesson = monitoringService.initializeLesson(title, desc, ldId, orgMap
					.getOrganisation().getOrganisationId(), userMap.getUser().getUserId());
			// 2. create lessonClass for lesson
			createLessonClass(lesson, orgMap.getOrganisation(), userMap.getUser());
			// 3. schedule lesson
			Date date = DateUtil.convertFromLAMSFlashFormat(startDate);
			monitoringService.startLessonOnSchedule(lesson.getLessonId(), date, userMap.getUser().getUserId());
			return lesson.getLessonId();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		} 
	}

	public boolean deleteLearningSession(String serverId, String datetime, String hashValue,
			String username, long lsId) throws RemoteException {
		try {
			ExtServerOrgMap serverMap = service.getExtServerOrgMap(serverId);
			Authenticator.authenticate(serverMap,datetime,username,hashValue);
			ExtUserUseridMap userMap = service.getExtUserUseridMap(serverMap, username);
			monitoringService.removeLesson(lsId, userMap.getUser().getUserId());
			return true;
		}catch(Exception e){
			throw new RemoteException(e.getMessage(), e);
		}
	}
	
	private void createLessonClass(Lesson lesson, Organisation organisation, User creator){
		List<User> staffList = new LinkedList<User>();
		staffList.add(creator);
		List<User> learnerList = new LinkedList<User>();
		monitoringService.createLessonClassForLesson(lesson.getLessonId(), organisation, 
				"learnerGroup", learnerList, "staffGroup", staffList,
				creator.getUserId());
		
	}

}
