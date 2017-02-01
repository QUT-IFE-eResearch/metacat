/**
 *  '$RCSfile$'
 *    Purpose: A Class that implements session utility methods 
 *  Copyright: 2008 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Michael Daigle
 * 
 *   '$Author: leinfelder $'
 *     '$Date: 2010-08-19 07:47:32 +1000 (Thu, 19 Aug 2010) $'
 * '$Revision: 5502 $'
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ucsb.nceas.metacat.service;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.shared.BaseService;
import edu.ucsb.nceas.metacat.shared.ServiceException;
import edu.ucsb.nceas.metacat.util.SessionData;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

public class SessionService extends BaseService {
	
	private static SessionService sessionService = null;
	private static int sessionTimeoutMinutes;
	
	private static Logger logMetacat = Logger.getLogger(SessionService.class);
	private static Hashtable<String, SessionData> sessionHash = null;
	
	private static final String PUBLIC_SESSION_ID = "0";
	
	private static Object lockObj = new Object();

	/**
	 * private constructor since this is a singleton
	 */
	private SessionService() throws ServiceException {		
		_serviceName = "SessionService";
		
		String sessionTimeoutStr = null;
		try {
			sessionHash = new Hashtable<String, SessionData>();
			sessionTimeoutStr = PropertyService.getProperty("session.timeoutMinutes");
			sessionTimeoutMinutes = Integer.parseInt(sessionTimeoutStr);
		
			logMetacat.debug("SessionService() - Registering public session id: " + 
					PUBLIC_SESSION_ID);
			registerSession(PUBLIC_SESSION_ID, "public", null, null, "Public User");
		} catch (PropertyNotFoundException pnfe) {
			throw new ServiceException("SessionService() - Error getting property: " + 
					pnfe.getMessage());
		} catch (NumberFormatException nfe) {
			throw new ServiceException("SessionService() - Error parsing session timeout minutes: " + 
					sessionTimeoutStr);
		}
	}
	
	/**
	 * Get the single instance of SessionService.
	 * 
	 * @return the single instance of SessionService
	 */
	public static SessionService getInstance() throws ServiceException {
		if (sessionService == null) {
			sessionService = new SessionService();
		}
		return sessionService;
	}
	
	public boolean refreshable() {
		return false;
	}
	
	public void doRefresh() throws ServiceException {
		return;
	}
	
	public void stop() throws ServiceException {
		return;
	}
	
	/**
	 * Register a session in the session hash table.  This uses
	 * the parameters passed in to create a SessionData object.
	 * 
	 * @param sessionId
	 *            the ID of the session to register
	 * @param userName
	 *            the user name of the session
	 * @param groupNames
	 *            the group names for the session
	 * @param password
	 *            the password for the session
	 */
	public static void registerSession(String sessionId, String userName,
			String[] groupNames, String password, String name) throws ServiceException {
		synchronized(lockObj) {
			if (sessionId == null) {
				throw new ServiceException("SessionService.registerSession - " + 
						"Cannot register a null session id");
			}
			logMetacat.debug("SessionService.registerSession - Registering session id: " + sessionId);
			SessionData sessionData = new SessionData(sessionId, userName, groupNames,
					password, name);
			sessionHash.put(sessionId, sessionData);
		}
	}
	
	/**
	 * Register a session in the session hash table.
	 * 
	 * @param sessionData
	 *            the session data object to add to the session hash
	 */
	public static void registerSession(SessionData sessionData) throws ServiceException {
		synchronized(lockObj) {
			if (sessionData == null) {
				throw new ServiceException("SessionService.registerSession - " + 
						"Cannot register null session data");
			}
			logMetacat.debug("SessionService.registerSession - Registering session " + 
					"data with id: " + sessionData.getId());
			sessionHash.put(sessionData.getId(), sessionData);
		}
	}
	
	/**
	 * Unregister a session from the session hash table.
	 * 
	 * @param sessionId
	 *            the id of the session to remove.
	 */
	public static void unRegisterSession(String sessionId) {
		synchronized(lockObj) {
			if (sessionId == null) {
				logMetacat.error("SessionService.unRegisterSession - trying to " + 
					"unregister a session with null id");
				return;
			}
			if (sessionId.equals(PUBLIC_SESSION_ID)) {
				logMetacat.error("SessionService.unRegisterSession - cannot unregister public session, " +
					"sessionId=" + sessionId);
				return;
			}
		
			logMetacat.info("SessionService.unRegisterSession - unRegistering session: " + sessionId);
			sessionHash.remove(sessionId);
		}
	}
	
	/**
	 * Unregister all sessions from the session hash table except the public session.
	 * 
	 * @param sessionId
	 *            the id of the session to remove.
	 */
	public static void unRegisterAllSessions() {
		synchronized(lockObj) {
			Enumeration<String> keyEnum = sessionHash.keys();
			while (keyEnum.hasMoreElements()) {
				String sessionId = keyEnum.nextElement();
				if (!sessionId.equals(PUBLIC_SESSION_ID)) {
					logMetacat.info("SessionService.unRegisterAllSessions - unRegistering session: " + sessionId);
					sessionHash.remove(sessionId);
				}
			}
		}
	}
	
	/**
	 * Check if a session is registered in the session hash table. 
	 * 
	 * @param sessionId
	 *            the id of the session to look for.
	 */
	public static boolean isSessionRegistered(String sessionId) {		
		if (sessionId == null) {
			logMetacat.error("SessionService.isSessionRegistered - trying to check if a " + 
					"session with null id is registered");
			return false;
		}
		
		checkTimeout(sessionId);
		
		return sessionHash.containsKey(sessionId);
	}
	
	/**
	 * Check if a session is registered in the session hash table. Write results
	 * in XML format to output.
	 * 
	 * @param out
	 *            the output stream to write to.
	 * @param sessionId
	 *            the id of the session to look for.
	 */
	public static void validateSession(PrintWriter out, HttpServletResponse response, 
			String sessionId) {		
		
		response.setContentType("text/xml");
		out.println("<?xml version=\"1.0\"?>");
		out.write("<validateSession><status>");
		if (validateSession(sessionId)) {
			out.write("valid");
		} else {
			out.write("invalid");
		}
		out.write("</status>");
		out.write("<sessionId>" + sessionId + "</sessionId></validateSession>");				
	}
	
	/**
	 * Check if a session is registered in the session hash table. Return
	 * true if the session is valid and false otherwise.
	 * 
	 * @param sessionId
	 *            the id of the session to look for.
	 */
	public static boolean validateSession(String sessionId) {				
		if (sessionId != null && !sessionId.equals(PUBLIC_SESSION_ID) && isSessionRegistered(sessionId)) {
			return true;
		} else {
			return false;
		}			
	}
	
	/**
	 * Get a registered session from the session hash table. 
	 * TODO MCD need to time sessions out
	 * 
	 * @param sessionId
	 *            the id of the session to retrieve.
	 */
	public static SessionData getRegisteredSession(String sessionId) {
		if (sessionId == null) {
			logMetacat.error("SessionService.getRegisteredSession - trying to get a session with null id");
			return null;
		}
		checkTimeout(sessionId);
		
		return sessionHash.get(sessionId);
	}
	
	/**
	 * Get the public session from the session hash table. 
	 */
	public static SessionData getPublicSession() {
		return sessionHash.get(PUBLIC_SESSION_ID);
	}
	
	/**
	 * Keep a session active by updating its last accessed time. 
	 * 
	 * @param sessionId
	 *            the id of the session to update.
	 */
	public static synchronized void touchSession(String sessionId) {
		if (sessionId == null) {
			logMetacat.error("SessionService.touchSession - trying to touch a session with null id");
		} else if (isSessionRegistered(sessionId)) {
			synchronized(lockObj) {
				SessionData sessionData = getRegisteredSession(sessionId);
				sessionData.setLastAccessedTime();
			}
		}
	}
	
	private static void checkTimeout (String sessionId) {
		SessionData sessionData = null;
		if ((sessionData = sessionHash.get(sessionId)) != null) {
			Calendar expireTime = Calendar.getInstance();
			Calendar lastAccessedTime = sessionData.getLastAccessedTime();
			expireTime.add(Calendar.MINUTE, 0 - sessionTimeoutMinutes);
			if(lastAccessedTime.compareTo(expireTime) < 0 ) {
				unRegisterSession(sessionId);
			}
		}		
	}

}
