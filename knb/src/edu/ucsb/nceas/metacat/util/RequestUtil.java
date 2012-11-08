/**
 *  '$RCSfile$'
 *    Purpose: A Class that implements administrative methods 
 *  Copyright: 2008 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Michael Daigle
 * 
 *   '$Author: daigle $'
 *     '$Date: 2009-10-07 03:55:18 +1000 (Wed, 07 Oct 2009) $'
 * '$Revision: 5076 $'
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

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.service.SessionService;
import edu.ucsb.nceas.metacat.shared.MetacatUtilException;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

public class RequestUtil {
	
	private static Logger logMetacat = Logger.getLogger(RequestUtil.class);
	
	/**
	 * private constructor - all methods are static so there is no
     * no need to instantiate.
	 */
	private RequestUtil() {}
	
	/**
	 * Forward a request that was received by this servlet on to another JSP
	 * page or servlet to continue handling the request.
	 * 
	 * @param request
	 *            to be forwarded
	 * @param response
	 *            that can be used for writing output to the client
	 * @param destination
	 *            the context-relative URL to which the request is forwarded
	 * @param params the request parameters.  these will be added to the request
	 */
	public static void forwardRequest(HttpServletRequest request, HttpServletResponse response, 
			String destinationUrl, Hashtable<String, String[]> params) throws MetacatUtilException {

		destinationUrl += "?" + paramsToQuery(params);
		
		logMetacat.debug("Forwarding request to " + destinationUrl);
		ServletContext servletContext = request.getSession()
				.getServletContext();

		try {
			servletContext.getRequestDispatcher(destinationUrl).forward(request, response);
		}  catch (IOException ioe) {
			throw new MetacatUtilException("RequestUtil.forwardRequest - I/O error when forwarding to " + 
					destinationUrl + " : " + ioe.getMessage());			
		} catch (ServletException se) {
			throw new MetacatUtilException("RequestUtil.forwardRequest - Servlet error when forwarding to " + 
					destinationUrl + " : " + se.getMessage());			
		}
	}
	
	/**
	 * Forward a request that was received by this servlet on to another JSP
	 * page or servlet to continue handling the request.  In this case, the page
	 * must be referenced in a paramter named "forwardto".  If the qformat is 
	 * provided, the file will be retrieved from that skin.  Otherwise, the file 
	 * will be retrieved from the system default skin.
	 * 
	 * For more specific file location, use: forwardRequest(request,response, destinationUrl, params)
	 * 
	 * @param request
	 *            to be forwarded
	 * @param response
	 *            that can be used for writing output to the client
	 * @param params
	 *            the request parameters.  these will be added to the request.
	 */
	public static void forwardRequest(HttpServletRequest request, HttpServletResponse response, 
			Hashtable<String, String[]> params) throws MetacatUtilException {

		String forwardTos[] = params.get("forwardto");
		if (forwardTos == null || forwardTos[0].equals("")) {
			throw new MetacatUtilException("RequestUtil.forwardRequest - forwardto must be set in parameters when forwarding.");			
		}
		
		String forwardTo = forwardTos[0];
		String qformat = null;
		
		String qformats[] = params.get("qformat");
		if (qformats == null || qformats.length == 0) {
			try {
				qformat = PropertyService.getProperty("application.default-style");
			} catch (PropertyNotFoundException pnfe) {
				qformat = "default";
				logMetacat.warn("RequestUtil.forwardRequest - could not get property " + 
						"'application.default-style'. Using 'default'");
			}
		} else {
			qformat = qformats[0];
		}
		
		String destinationUrl = "/style/skins/" + qformat + "/" + forwardTo;
		destinationUrl += "?" + paramsToQuery(params);
		
		logMetacat.debug("RequestUtil.forwardRequest - Forwarding request to " + destinationUrl);
		ServletContext servletContext = request.getSession()
				.getServletContext();
		try {
			servletContext.getRequestDispatcher(destinationUrl).forward(request, response);
		} catch (IOException ioe) {
			throw new MetacatUtilException("RequestUtil.forwardRequest - I/O error when forwarding to " + 
					destinationUrl + " : " + ioe.getMessage());			
		} catch (ServletException se) {
			throw new MetacatUtilException("RequestUtil.forwardRequest - Servlet error when forwarding to " + 
					destinationUrl + " : " + se.getMessage());			
		}
	}
	


	/**
	 * Post a request and return the response body
	 * 
	 * @param httpClient
	 *            The HttpClient to use in the post.  This is passed in because
     * 			  the same client may be used in several posts
	 * @param url
	 *            the url to post to
	 * @param paramMap
	 *            map of parameters to add to the post
	 * @returns a string holding the response body
	 */
	public static String post(HttpClient httpClient, String url,
			HashMap<String, String> paramMap) throws IOException, HttpException {

		PostMethod method = new PostMethod(url);

		// Configure the form parameters
		if (paramMap != null) {
			Set<String> paramNames = paramMap.keySet();
			for (String paramName : paramNames) {
				method.addParameter(paramName, paramMap.get(paramName));
			}
		}

		// Execute the POST method
		int statusCode = httpClient.executeMethod(method);
		if (statusCode != -1) {
			String contents = method.getResponseBodyAsString();
			method.releaseConnection();
			return (contents);
		}

		return null;
	}
	
	public static String get(String urlString, Hashtable<String, String[]> params)  throws MetacatUtilException {	
		try {
			URL url = new URL(urlString);
			URLConnection urlConn = url.openConnection();
			
			urlConn.setDoOutput(true);
			
			PrintWriter pw = new PrintWriter(urlConn.getOutputStream());
			String queryString = paramsToQuery(params);
			logMetacat.debug("Sending get request: " + urlString + "?" + queryString);
			pw.print(queryString);
			pw.close();
			
			// get the input from the request
			BufferedReader in = 
				new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			
			return sb.toString();
		} catch (MalformedURLException mue) {
			throw new MetacatUtilException("URL error when contacting: " + urlString + " : " + mue.getMessage());
		} catch (IOException ioe) {
			throw new MetacatUtilException("I/O error when contacting: " + urlString + " : " + ioe.getMessage());
		} 
	}
	
	/**
	 * Get a cookie from a request by the cookie name
	 * 
	 * @param request
	 *            the request from which to get the cookie
	 * @param cookieName
	 *            the name of the cookie to look for
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName)  {
		Cookie sessionCookies[] = request.getCookies();

		if (sessionCookies == null) {
			return null;
		}
		
		for (int i = 0; i < sessionCookies.length; i++) {
			if(sessionCookies[i].getName().equals(cookieName)) {
				return sessionCookies[i];
			}
		}	
		
		return null;
	}
	
	/**
	 * Get the session data from a request. The Scenarios we can run across
	 * here: 
	 * -- the session id parameter was set in the request parameters 
	 * -- request.getSession returns a new session. There is a chance that the
	 *    session id was set in a cookie. Check for a JSESSIONID cookie and use
	 *    that id if provided. 
	 * -- request.getSession returns a session that is a)
	 *    preexisting or b) new but without a JSESSIONID cookie. Use the session id
	 *    from this session
	 * 
	 * @param request
	 *            the request from which to get the session data
	 * @return the session data object representing the active session for this
	 *         request. If there is no active session, the public session data
	 *         is returned
	 */
	public static SessionData getSessionData(HttpServletRequest request) {
		SessionData sessionData = null;
		String sessionId = null;

		Hashtable<String, String[]> params = getParameters(request);

		if (params.containsKey("sessionid")) {
			// the session id is specified in the request parameters
			sessionId = ((String[]) params.get("sessionid"))[0];
			logMetacat.debug("session ID provided in request properties: " + sessionId);
		} else {
			HttpSession session = request.getSession(true);
			if (session.isNew()) {
				// this is a new session
				Cookie sessionCookie = RequestUtil.getCookie(request, "JSESSIONID");
				if (sessionCookie != null) {
					// and there is a JSESSIONID cookie
					sessionId = sessionCookie.getValue();
					logMetacat.debug("session ID provided in request cookie: "
							+ sessionId);
				}
			}
			if (sessionId == null) {
				// there is an existing session (session is old)
				sessionId = session.getId();
				logMetacat.debug("session ID retrieved from request: " + sessionId);
			}
		}

		// if the session id is registered in SessionService, get the
		// SessionData for it. Otherwise, use the public session.
		if (SessionService.isSessionRegistered(sessionId)) {
			logMetacat.debug("retrieving session data from session service "
					+ "for session id " + sessionId);
			sessionData = SessionService.getRegisteredSession(sessionId);
		} else {
			logMetacat.debug("using public session.  Given session id is "
					+ "registered: " + sessionId);
			sessionData = SessionService.getPublicSession();
		}
		
		return sessionData;
	}

	/**
	 * Get a cookie from a request by the cookie name
	 * 
	 * @param request
	 *            the request from which to get the cookie
	 * @param cookieName
	 *            the name of the cookie to look for
	 */
	@SuppressWarnings("unchecked")
	public static Hashtable<String, String[]> getParameters(HttpServletRequest request)  {
		Hashtable<String, String[]> params = new Hashtable<String, String[]>();
		
		Enumeration<String> paramlist = (Enumeration<String>)request.getParameterNames();
		while (paramlist.hasMoreElements()) {
			String name = (String) paramlist.nextElement();
			String[] value = request.getParameterValues(name);
			params.put(name, value);
		}
		
		return params;
	}
	
	/**
	 * Add a list of errors to the request. The pages will pick up the errors
	 * and display them where appropriate.
	 * 
	 * @param request
	 *            the request that will get forwarded
	 * @param errorVector
	 *            a list of error strings
	 */
	public static void setRequestErrors(HttpServletRequest request,
			Vector<String> errorVector) {
		request.setAttribute("formErrors", "true");
		request.setAttribute("processingErrors", errorVector);
	}
	
	/**
	 * Add a list of form errors to the request. The pages will pick up the
	 * errors and display them where appropriate.
	 * 
	 * @param request
	 *            the request that will get forwarded
	 * @param errorVector
	 *            a list of form error strings
	 */
	public static void setRequestFormErrors(HttpServletRequest request,
			Vector<String> errorVector) {
		request.setAttribute("formErrors", "true");
		request.setAttribute("formFieldErrors", errorVector);
	}
	
	/**
	 * Add a list of success messages to the request. The pages will pick up the
	 * messages and display them where appropriate.
	 * 
	 * @param request
	 *            the request that will get forwarded
	 * @param errorVector
	 *            a list of success message strings
	 */
	public static void setRequestSuccess(HttpServletRequest request,
			Vector<String> successVector) {
		request.setAttribute("formSuccess", "true");
		request.setAttribute("processingSuccess", successVector);
	}
	
	/**
	 * Add a list of general messages to the request. The pages will pick up the
	 * messages and display them where appropriate.
	 * 
	 * @param request
	 *            the request that will get forwarded
	 * @param errorVector
	 *            a list of general message strings
	 */
	public static void setRequestMessage(HttpServletRequest request,
			Vector<String> messageVector) {
		request.setAttribute("formMessage", "true");
		request.setAttribute("processingMessage", messageVector);
	}
	
	/**
	 * Add a list of general messages to the request. The pages will pick up the
	 * messages and display them where appropriate.
	 * 
	 * @param request
	 *            the request that will get forwarded
	 * @param errorVector
	 *            a list of general message strings
	 */
	public static void clearRequestMessages(HttpServletRequest request) {
		request.setAttribute("formMessage", null);
		request.setAttribute("formSuccess", null);
		request.setAttribute("formErrors", null);
		request.setAttribute("processingMessage", null);
		request.setAttribute("processingSuccess", null);
		request.setAttribute("formFieldErrors", null);
		request.setAttribute("processingErrors", null);
	}
	
	/**
	 * Add the user's login id to the session on this request
	 * 
	 * @param request
	 *            the request that will get forwarded
	 * @param userId
	 *            the user's login id
	 */
	public static void setUserId(HttpServletRequest request, String userId) {
		request.getSession().setAttribute("userId", userId);
	}
	
	private static String paramsToQuery(Hashtable<String, String[]> params) {
		String query = "";
		if (params != null) {
			boolean firstParam = true;
			for (String paramName : params.keySet()) {
				if (firstParam) {
					firstParam = false;
				} else {
					query += "&";
				}
				query += paramName + "=" + params.get(paramName)[0];
			}
		}
		
		return query;
	}
	

}
