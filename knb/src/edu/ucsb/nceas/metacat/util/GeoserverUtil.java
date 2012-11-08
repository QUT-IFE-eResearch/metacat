/**
 *  '$RCSfile$'
 *    Purpose: A Class that implements administrative methods 
 *  Copyright: 2008 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Michael Daigle
 * 
 *   '$Author: daigle $'
 *     '$Date: 2008-07-06 21:25:34 -0700 (Sun, 06 Jul 2008) $'
 * '$Revision: 4080 $'
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

package edu.ucsb.nceas.metacat.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.shared.MetacatUtilException;
import edu.ucsb.nceas.utilities.FileUtil;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

public class GeoserverUtil {
	
	private static Logger logMetacat = Logger.getLogger(GeoserverUtil.class);
	
	/**
	 * private constructor - all methods are static so there is no
     * no need to instantiate.
	 */
	private GeoserverUtil() {}
	
	/**
	 * Log in to geoserver as the administrative user. If the geoserver.username
	 * and geoserver.password already exists in metacat.properties, we assume
	 * that the password was already changed at least once. We use these values.
	 * Otherwise we use the default values.
	 * 
	 * @param httpClient
	 *            the HttpClient we will use to post. This is passed in since it
	 *            may need to be used after login by other methods.
	 */
	public static void loginAdmin(HttpClient httpClient) throws MetacatUtilException {
		try {
			String username = 
				PropertyService.getProperty("geoserver.username");
			String password = 
				PropertyService.getProperty("geoserver.password");
			String defaultUserName = 
				PropertyService.getProperty("geoserver.defaultUsername");
			String defaultPassword = 
				PropertyService.getProperty("geoserver.defaultPassword");
			String loginSuccessString = 
				PropertyService.getProperty("geoserver.loginSuccessString");
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			// if the username and password exist, we assume the geoserver was 
			// already updated at least once.  Use the existing values as the 
			// current admin values.  Otherwise,use the default values.
			if (username != null && !username.equals("")
					&& password != null && !password.equals("")) {
				paramMap.put("username", username);
				paramMap.put("password", password);
			} else {
				paramMap.put("username", defaultUserName);
				paramMap.put("password", defaultPassword);
			}
			
			// Create the post url from values in metacat.properties
			String loginPostPage = 
				PropertyService.getProperty("geoserver.loginPostPage");
			String loginPostURL = SystemUtil.getContextURL() + FileUtil.getFS()
					+ loginPostPage;
			
			logMetacat.debug("loginPostURL: " + loginPostURL);
			
			// Do the post
			String postResult = RequestUtil.post(httpClient, loginPostURL, paramMap);
			
			// check to see if the success string is part of the result
			if(postResult.indexOf(loginSuccessString) == -1) {
				logMetacat.debug(postResult);
				throw new MetacatUtilException("Could not log in to geoserver.");
			}

		} catch (PropertyNotFoundException pnfe) {
			throw new MetacatUtilException("Property error while logging in with " 
					+ "default account: " + pnfe.getMessage());
		} catch (IOException ioe) {
			throw new MetacatUtilException("I/O error while logging in with " 
					+ "default account: " + ioe.getMessage());
		} 
	}
	
	/**
	 * Change the password on the geoserver. The loginAdmin method must have
	 * already been called using the same HttpClient that is passed to this
	 * method.
	 * 
	 * @param httpClient
	 *            the HttpClient we will use to post. This should have been used
	 *            in the login post.
	 * @param username
	 *            the new user name
	 * @param password
	 *            the new password
	 */
	public static void changePassword(HttpClient httpClient, String username, String password) 
		throws MetacatUtilException {
		try {	
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("username", username);
			paramMap.put("password", password);
			
			// Create the post url from values in metacat.properties
			String passwordPostPage = 
				PropertyService.getProperty("geoserver.passwordPostPage");
			String passwordPostURL = SystemUtil.getContextURL() + FileUtil.getFS()
					+ passwordPostPage;
			
			String passwordSuccessString = 
				PropertyService.getProperty("geoserver.passwordSuccessString");

			logMetacat.debug("passwordPostURL: " + passwordPostURL);
			
			// Do the post
			String postResult = RequestUtil.post(httpClient, passwordPostURL, paramMap);
			
			// check to see if the success string is part of the result
			if(postResult.indexOf(passwordSuccessString) == -1) {
				logMetacat.debug(postResult);
				throw new MetacatUtilException("Could not change geoserver password.");
			}
			
			// send a post to apply the password changes.  Unfortunately, there really
			// isn't anything of the geoserver side saying if the post passed, so we have
			// to assume it did.
			String applyPostPage = 
				PropertyService.getProperty("geoserver.applyPostPage");
			String applyPostURL = SystemUtil.getContextURL() + FileUtil.getFS()
				+ applyPostPage;
			RequestUtil.post(httpClient, applyPostURL, null);		
			
		} catch (PropertyNotFoundException pnfe) {
			throw new MetacatUtilException("Property error while logging in with " 
					+ "default account: " + pnfe.getMessage());
		} catch (IOException ioe) {
			throw new MetacatUtilException("I/O error while logging in with " 
					+ "default account: " + ioe.getMessage());
		} 
	}
	
	/**
	 * Reports whether geoserver is configured.
	 * 
	 * @return a boolean that is true if geoserver is configured or bypassed
	 */
	public static boolean isGeoserverConfigured() throws MetacatUtilException {
		String geoserverConfiguredString = PropertyService.UNCONFIGURED;
		try {
			geoserverConfiguredString = PropertyService.getProperty("configutil.geoserverConfigured");
		} catch (PropertyNotFoundException pnfe) {
			throw new MetacatUtilException("Could not determine if geoservice are configured: "
					+ pnfe.getMessage());
		}
		// geoserver is configured if not unconfigured
		return !geoserverConfiguredString.equals(PropertyService.UNCONFIGURED);
	}
}
