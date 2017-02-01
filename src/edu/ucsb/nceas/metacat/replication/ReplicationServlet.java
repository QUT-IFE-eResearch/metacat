/**
 *  '$RCSfile$'
 *    Purpose: A Class that implements replication for metacat
 *  Copyright: 2000 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Chad Berkley
 *
 *   '$Author: daigle $'
 *     '$Date: 2009-03-25 13:41:15 -0800 (Wed, 25 Mar 2009) $'
 * '$Revision: 4861 $'
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

package edu.ucsb.nceas.metacat.replication;

import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import edu.ucsb.nceas.metacat.service.ServiceService;
import edu.ucsb.nceas.metacat.service.SessionService;
import edu.ucsb.nceas.metacat.shared.MetacatUtilException;
import edu.ucsb.nceas.metacat.shared.ServiceException;
import edu.ucsb.nceas.metacat.util.AuthUtil;
import edu.ucsb.nceas.metacat.util.SessionData;

import org.apache.log4j.Logger;

public class ReplicationServlet extends HttpServlet {

	private static final long serialVersionUID = -2898600143193513155L;

	private static Logger logReplication = Logger.getLogger("ReplicationLogging");
	private static Logger logMetacat = Logger.getLogger(ReplicationServlet.class);

	/**
	 * Initialize the servlet by creating appropriate database connections
	 */
	public void init(ServletConfig config) throws ServletException {

		try {
			// Register preliminary services
			ServiceService.registerService("ReplicationService", ReplicationService
					.getInstance());
			
		} catch (ServiceException se) {
			String errorMessage = "ReplicationServlet.init - Service problem while intializing Replication Servlet: "
					+ se.getMessage();
			logMetacat.error("ReplicationServlet.init - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error(errorMessage);
			throw new ServletException(errorMessage);
		} 
	}

	public void destroy() {
		//ServiceService.
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Process the data and send back the response
		handleGetOrPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Process the data and send back the response
		handleGetOrPost(request, response);
	}

	private void handleGetOrPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		Hashtable<String, String[]> params = new Hashtable<String, String[]>();
		Enumeration<String> paramlist = request.getParameterNames();

		while (paramlist.hasMoreElements()) {
			String name = (String) paramlist.nextElement();
			String[] value = request.getParameterValues(name);
			params.put(name, value);
		}

		String action = "";
		if (!params.isEmpty() && params.get("action") != null) {
			action = ((String[]) params.get("action"))[0];
		}
		String server = null;

		try {
			// check if the server is included in the list of replicated servers
			if (!action.equals("servercontrol") && !action.equals("stop")
					&& !action.equals("start") && !action.equals("getall")) {

				server = ((String[]) params.get("server"))[0];
				if (ReplicationService.getServerCodeForServerName(server) == 0) {
					logReplication.debug("ReplicationServlet.handleGetOrPost - Action \"" + action + "\" rejected for server: "
							+ server);
					return;
				} else {
					logReplication.debug("ReplicationServlet.handleGetOrPost - Action \"" + action + "\" accepted for server: "
							+ server);
				}
			} else {
				// start, stop, getall and servercontrol need to check if user is administor
				HttpSession sess = request.getSession(true);
				SessionData sessionData = null;
				String sess_id = "";
				String username = "";
				String[] groupnames = { "" };

				if (params.containsKey("sessionid")) {
					sess_id = ((String[]) params.get("sessionid"))[0];
					logReplication.info("ReplicationServlet.handleGetOrPost - in has sessionid " + sess_id);
					if (SessionService.isSessionRegistered(sess_id)) {
						logReplication.info("ReplicationServlet.handleGetOrPost - find the id " + sess_id + " in hash table");
						sessionData = SessionService.getRegisteredSession(sess_id);
					}
				}
				if (sessionData == null) {
					sessionData = new SessionData(sess.getId(), 
							(String) sess.getAttribute("username"), 
							(String[]) sess.getAttribute("groups"),
							(String) sess.getAttribute("password"), 
							(String) sess.getAttribute("name"));
				}

				username = sessionData.getUserName();
				logReplication.warn("ReplicationServlet.handleGetOrPost - The user name from session is: " + username);
				groupnames = sessionData.getGroupNames();
				if (!AuthUtil.isAdministrator(username, groupnames)) {
					out = response.getWriter();
					out.print("<error>");
					out.print("The user \"" + username
							+ "\" is not authorized for this action.");
					out.print("</error>");
					out.close();
					logReplication.warn("ReplicationServlet.handleGetOrPost - The user \"" + username
							+ "\" is not authorized for this action: " + action);
					return;
				}

			}// else

			if (action.equals("readdata")) {
				OutputStream outStream = response.getOutputStream();
				//to get the data file.
				ReplicationService.handleGetDataFileRequest(outStream, params, response);
				outStream.close();
			} else if (action.equals("forcereplicatedatafile")) {
				//read a specific docid from remote host, and store it into local host
				ReplicationService.handleForceReplicateDataFileRequest(params, request);
			} else if (action.equals("stop")) {
				// stop the replication server
				ReplicationService.getInstance().stopReplication();
				out.println("Replication Handler Stopped");
			} else if (action.equals("start")) {
				ReplicationService.getInstance().startReplication(params);
				out.println("Replication Handler Started");
			} else if (action.equals("getall")) {
				ReplicationService.getInstance().runOnce();
				response.setContentType("text/html");
				out.println("<html><body>\"Get All\" Done</body></html>");
			} else if (action.equals("forcereplicate")) {
				// read a specific docid from remote host, and store it into
				// local host
				ReplicationService.handleForceReplicateRequest(out, params, response,
						request);
			} else if (action.equals("forcereplicatedelete")) {
				// read a specific docid from remote host, and store it into
				// local host
				ReplicationService.handleForceReplicateDeleteRequest(out, params,
						response, request);
			} else if (action.equals("update")) {
				// request an update list from the server
				ReplicationService.handleUpdateRequest(out, params, response);
			} else if (action.equals("read")) {
				// request a specific document from the server
				// note that this could be replaced by a call to metacatServlet
				// handleGetDocumentAction().
				ReplicationService.handleGetDocumentRequest(out, params, response);				
			} else if (action.equals("getlock")) {
				ReplicationService.handleGetLockRequest(out, params, response);
			} else if (action.equals("getdocumentinfo")) {
				ReplicationService.handleGetDocumentInfoRequest(out, params, response);
			} else if (action.equals("gettime")) {
				ReplicationService.handleGetTimeRequest(out, params, response);
			} else if (action.equals("getcatalog")) {
				ReplicationService.handleGetCatalogRequest(out, params, response, true);
			} else if (action.equals("servercontrol")) {
				ReplicationService.handleServerControlRequest(out, params, response);
			} else if (action.equals("test")) {
				response.setContentType("text/html");
				out.println("<html><body>Test successfully</body></html>");
			}

		} catch (ServiceException e) {
			logMetacat.error("ReplicationServlet.handleGetOrPost - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationServlet.handleGetOrPost - Error in ReplicationServlet.handleGetOrPost: " + e.getMessage());
		} catch (MetacatUtilException mue) {
			logMetacat.error("ReplicationServlet.handleGetOrPost - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationServlet.handleGetOrPost - Metacat utility error in ReplicationServlet.handleGetOrPost: "
							+ mue.getMessage());
		} finally {
			out.close();
		}
	}
}
