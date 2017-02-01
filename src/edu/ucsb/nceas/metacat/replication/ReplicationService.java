/**
 *  '$RCSfile$'
 *    Purpose: A Class that implements replication for metacat
 *  Copyright: 2000 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Chad Berkley
 *
 *   '$Author: leinfelder $'
 *     '$Date: 2011-05-19 05:17:00 +1000 (Thu, 19 May 2011) $'
 * '$Revision: 6090 $'
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
import java.util.Date;
import java.io.*;
import java.sql.*;
import java.net.*;
import java.text.*;

import javax.servlet.http.*;

import edu.ucsb.nceas.metacat.DocInfoHandler;
import edu.ucsb.nceas.metacat.DocumentImpl;
import edu.ucsb.nceas.metacat.DocumentImplWrapper;
import edu.ucsb.nceas.metacat.EventLog;
import edu.ucsb.nceas.metacat.McdbException;
import edu.ucsb.nceas.metacat.accesscontrol.AccessControlException;
import edu.ucsb.nceas.metacat.accesscontrol.AccessControlForSingleFile;
import edu.ucsb.nceas.metacat.accesscontrol.PermOrderException;
import edu.ucsb.nceas.metacat.accesscontrol.XMLAccessDAO;
import edu.ucsb.nceas.metacat.database.DBConnection;
import edu.ucsb.nceas.metacat.database.DBConnectionPool;
import edu.ucsb.nceas.metacat.database.DatabaseService;
import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.shared.BaseService;
import edu.ucsb.nceas.metacat.shared.HandlerException;
import edu.ucsb.nceas.metacat.shared.ServiceException;
import edu.ucsb.nceas.metacat.util.DocumentUtil;
import edu.ucsb.nceas.metacat.util.MetacatUtil;
import edu.ucsb.nceas.metacat.util.SystemUtil;
import edu.ucsb.nceas.utilities.FileUtil;
import edu.ucsb.nceas.utilities.GeneralPropertyException;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

import org.apache.log4j.Logger;
import org.xml.sax.*;

public class ReplicationService extends BaseService {

	private static ReplicationService replicationService = null;

	private long timeInterval;
	private Date firstTime;
	private boolean timedReplicationIsOn = false;
	Timer replicationDaemon;
	private static Vector<String> fileLocks = new Vector<String>();
//	private Thread lockThread = null;
	public static final String FORCEREPLICATEDELETE = "forcereplicatedelete";
	private static String TIMEREPLICATION = "replication.timedreplication";
	private static String TIMEREPLICATIONINTERVAl ="replication.timedreplicationinterval";
	private static String FIRSTTIME = "replication.firsttimedreplication";
	private static final int TIMEINTERVALLIMIT = 7200000;
	public static final String REPLICATIONUSER = "replication";

	public static final String REPLICATION_LOG_FILE_NAME = "metacatreplication.log";
	public static String METACAT_REPL_ERROR_MSG = null;
	private static Logger logReplication = Logger.getLogger("ReplicationLogging");
	private static Logger logMetacat = Logger.getLogger(ReplicationService.class);

	private ReplicationService() throws ServiceException {
		_serviceName = "ReplicationService";
		
		initialize();
	}
	
	private void initialize() throws ServiceException {
				
		// initialize db connections to handle any update requests
		// deltaT = util.getProperty("replication.deltaT");
		// the default deltaT can be set from metacat.properties
		// create a thread to do the delta-T check but don't execute it yet
		replicationDaemon = new Timer(true);
		try {
			String replLogFile = PropertyService.getProperty("replication.logdir")
				+ FileUtil.getFS() + REPLICATION_LOG_FILE_NAME;
			METACAT_REPL_ERROR_MSG = "An error occurred in replication.  Please see the " +
				"replication log at: " + replLogFile;
			
			String timedRepIsOnStr = 
				PropertyService.getProperty("replication.timedreplication");
			timedReplicationIsOn = (new Boolean(timedRepIsOnStr)).booleanValue();
			logReplication.info("ReplicationService.initialize - The timed replication on is" + timedReplicationIsOn);

			String timeIntervalStr = 
				PropertyService.getProperty("replication.timedreplicationinterval");
			timeInterval = (new Long(timeIntervalStr)).longValue();
			logReplication.info("ReplicationService.initialize - The timed replication time Interval is " + timeInterval);

			String firstTimeStr = 
				PropertyService.getProperty("replication.firsttimedreplication");
			logReplication.info("ReplicationService.initialize - first replication time form property is " + firstTimeStr);
			firstTime = ReplicationHandler.combinateCurrentDateAndGivenTime(firstTimeStr);

			logReplication.info("ReplicationService.initialize - After combine current time, the real first time is "
					+ firstTime.toString() + " minisec");

			// set up time replication if it is on
			if (timedReplicationIsOn) {
				replicationDaemon.scheduleAtFixedRate(new ReplicationHandler(),
						firstTime, timeInterval);
				logReplication.info("ReplicationService.initialize - deltaT handler started with rate="
						+ timeInterval + " mini seconds at " + firstTime.toString());
			}

		} catch (PropertyNotFoundException pnfe) {
			throw new ServiceException(
					"ReplicationService.initialize - Property error while instantiating "
							+ "replication service: " + pnfe.getMessage());
		} catch (HandlerException he) {
			throw new ServiceException(
					"ReplicationService.initialize - Handler error while instantiating "
							+ "replication service" + he.getMessage());
		} 
	}

	/**
	 * Get the single instance of SessionService.
	 * 
	 * @return the single instance of SessionService
	 */
	public static ReplicationService getInstance() throws ServiceException {
		if (replicationService == null) {
			replicationService = new ReplicationService();
		}
		return replicationService;
	}

	public boolean refreshable() {
		return true;
	}

	protected void doRefresh() throws ServiceException {
		return;
	}
	
	public void stop() throws ServiceException{
		
	}

	public void stopReplication() throws ServiceException {
	      //stop the replication server
	      replicationDaemon.cancel();
	      replicationDaemon = new Timer(true);
	      timedReplicationIsOn = false;
	      try {
	    	  PropertyService.setProperty("replication.timedreplication", (new Boolean(timedReplicationIsOn)).toString());
	      } catch (GeneralPropertyException gpe) {
	    	  logReplication.warn("ReplicationService.stopReplication - Could not set replication.timedreplication property: " + gpe.getMessage());
	      }

	      logReplication.info("ReplicationService.stopReplication - deltaT handler stopped");
		return;
	}
	
	protected void startReplication(Hashtable<String, String[]> params) throws ServiceException {

	       String firstTimeStr = "";
	      //start the replication server
	       if ( params.containsKey("rate") ) {
	        timeInterval = new Long(
	               new String(((String[])params.get("rate"))[0])).longValue();
	        if(timeInterval < TIMEINTERVALLIMIT) {
	            //deltaT<30 is a timing mess!
	            timeInterval = TIMEINTERVALLIMIT;
	            throw new ServiceException("Replication deltaT rate cannot be less than "+
	                    TIMEINTERVALLIMIT + " millisecs and system automatically setup the rate to "+TIMEINTERVALLIMIT);
	        }
	      } else {
	        timeInterval = TIMEINTERVALLIMIT ;
	      }
	      logReplication.info("ReplicationService.startReplication - New rate is: " + timeInterval + " mini seconds.");
	      if ( params.containsKey("firsttime"))
	      {
	         firstTimeStr = ((String[])params.get("firsttime"))[0];
	         try
	         {
	           firstTime = ReplicationHandler.combinateCurrentDateAndGivenTime(firstTimeStr);
	           logReplication.info("ReplicationService.startReplication - The first time setting is "+firstTime.toString());
	         }
	         catch (HandlerException e)
	         {
	            throw new ServiceException(e.getMessage());
	         }
	         logReplication.warn("After combine current time, the real first time is "
	                                  +firstTime.toString()+" minisec");
	      }
	      else
	      {
	    	  logMetacat.error("ReplicationService.startReplication - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
	          logReplication.error("ReplicationService.startReplication - You should specify the first time " +
	                                  "to start a time replication");
	          return;
	      }
	      
	      timedReplicationIsOn = true;
	      try {
	      // save settings to property file
	      PropertyService.setProperty(TIMEREPLICATION, (new Boolean(timedReplicationIsOn)).toString());
	      // note we couldn't use firstTime object because it has date info
	      // we only need time info such as 10:00 PM
	      PropertyService.setProperty(FIRSTTIME, firstTimeStr);
	      PropertyService.setProperty(TIMEREPLICATIONINTERVAl, (new Long(timeInterval)).toString());
	      } catch (GeneralPropertyException gpe) {
	    	  logReplication.warn("ReplicationService.startReplication - Could not set property: " + gpe.getMessage());
	      }
	      replicationDaemon.cancel();
	      replicationDaemon = new Timer(true);
	      replicationDaemon.scheduleAtFixedRate(new ReplicationHandler(), firstTime,
	                                            timeInterval);
	      
	      logReplication.info("ReplicationService.startReplication - deltaT handler started with rate=" +
	                                    timeInterval + " milliseconds at " +firstTime.toString());

	}
	
	public void runOnce() throws ServiceException {
	      //updates this server exactly once
	      replicationDaemon.schedule(new ReplicationHandler(), 0);
	}

	/**
	 * This method can add, delete and list the servers currently included in
	 * xml_replication.
	 * action           subaction            other needed params
	 * ---------------------------------------------------------
	 * servercontrol    add                  server
	 * servercontrol    delete               server
	 * servercontrol    list
	 */
	protected static void handleServerControlRequest(PrintWriter out,
			Hashtable<String, String[]> params, HttpServletResponse response) {
		String subaction = ((String[]) params.get("subaction"))[0];
		DBConnection dbConn = null;
		int serialNumber = -1;
		PreparedStatement pstmt = null;
		String replicate = null;
		String server = null;
		String dataReplicate = null;
		String hub = null;
		try {
			//conn = util.openDBConnection();
			dbConn = DBConnectionPool
					.getDBConnection("MetacatReplication.handleServerControlRequest");
			serialNumber = dbConn.getCheckOutSerialNumber();

			// add server to server list
			if (subaction.equals("add")) {
				replicate = ((String[]) params.get("replicate"))[0];
				server = ((String[]) params.get("server"))[0];

				//Get data replication value
				dataReplicate = ((String[]) params.get("datareplicate"))[0];
				//Get hub value
				hub = ((String[]) params.get("hub"))[0];

				String toDateSql = DatabaseService.getDBAdapter().toDate("01/01/1980","MM/DD/YYYY");
				String sql = "INSERT INTO xml_replication "
						+ "(server, last_checked, replicate, datareplicate, hub) "
						+ "VALUES (?," + toDateSql + ",?,?,?)";
				
				pstmt = dbConn.prepareStatement(sql);
						
				pstmt.setString(1, server);
				pstmt.setInt(2, Integer.parseInt(replicate));
				pstmt.setInt(3, Integer.parseInt(dataReplicate));
				pstmt.setInt(4, Integer.parseInt(hub));
				
				String sqlReport = "XMLAccessAccess.getXMLAccessForDoc - SQL: " + sql;
				sqlReport += " [" + server + "," + replicate + 
					"," + dataReplicate + "," + hub + "]";
				
				logMetacat.info(sqlReport);
				
				pstmt.execute();
				pstmt.close();
				dbConn.commit();
				out.println("Server " + server + " added");
				response.setContentType("text/html");
				out.println("<html><body><table border=\"1\">");
				out.println("<tr><td><b>server</b></td><td><b>last_checked</b></td><td>");
				out.println("<b>replicate</b></td>");
				out.println("<td><b>datareplicate</b></td>");
				out.println("<td><b>hub</b></td></tr>");
				pstmt = dbConn.prepareStatement("SELECT * FROM xml_replication");
				//increase dbconnection usage
				dbConn.increaseUsageCount(1);

				pstmt.execute();
				ResultSet rs = pstmt.getResultSet();
				boolean tablehasrows = rs.next();
				while (tablehasrows) {
					out.println("<tr><td>" + rs.getString(2) + "</td><td>");
					out.println(rs.getString(3) + "</td><td>");
					out.println(rs.getString(4) + "</td><td>");
					out.println(rs.getString(5) + "</td><td>");
					out.println(rs.getString(6) + "</td></tr>");

					tablehasrows = rs.next();
				}
				out.println("</table></body></html>");

				// download certificate with the public key on this server
				// and import it as a trusted certificate
				String certURL = ((String[]) params.get("certificate"))[0];
				if (certURL != null && !certURL.equals("")) {
					downloadCertificate(certURL);
				}

				// delete server from server list
			} else if (subaction.equals("delete")) {
				server = ((String[]) params.get("server"))[0];
				pstmt = dbConn.prepareStatement("DELETE FROM xml_replication "
						+ "WHERE server LIKE '" + server + "'");
				pstmt.execute();
				pstmt.close();
				dbConn.commit();
				out.println("Server " + server + " deleted");
				response.setContentType("text/html");
				out.println("<html><body><table border=\"1\">");
				out.println("<tr><td><b>server</b></td><td><b>last_checked</b></td><td>");
				out.println("<b>replicate</b></td>");
				out.println("<td><b>datareplicate</b></td>");
				out.println("<td><b>hub</b></td></tr>");

				pstmt = dbConn.prepareStatement("SELECT * FROM xml_replication");
				//increase dbconnection usage
				dbConn.increaseUsageCount(1);
				pstmt.execute();
				ResultSet rs = pstmt.getResultSet();
				boolean tablehasrows = rs.next();
				while (tablehasrows) {
					out.println("<tr><td>" + rs.getString(2) + "</td><td>");
					out.println(rs.getString(3) + "</td><td>");
					out.println(rs.getString(4) + "</td><td>");
					out.println(rs.getString(5) + "</td><td>");
					out.println(rs.getString(6) + "</td></tr>");
					tablehasrows = rs.next();
				}
				out.println("</table></body></html>");

				// list servers in server list
			} else if (subaction.equals("list")) {
				response.setContentType("text/html");
				out.println("<html><body><table border=\"1\">");
				out.println("<tr><td><b>server</b></td><td><b>last_checked</b></td><td>");
				out.println("<b>replicate</b></td>");
				out.println("<td><b>datareplicate</b></td>");
				out.println("<td><b>hub</b></td></tr>");
				pstmt = dbConn.prepareStatement("SELECT * FROM xml_replication");
				pstmt.execute();
				ResultSet rs = pstmt.getResultSet();
				boolean tablehasrows = rs.next();
				while (tablehasrows) {
					out.println("<tr><td>" + rs.getString(2) + "</td><td>");
					out.println(rs.getString(3) + "</td><td>");
					out.println(rs.getString(4) + "</td><td>");
					out.println(rs.getString(5) + "</td><td>");
					out.println(rs.getString(6) + "</td></tr>");
					tablehasrows = rs.next();
				}
				out.println("</table></body></html>");
			} else {

				out.println("<error>Unkonwn subaction</error>");

			}
			pstmt.close();
			//conn.close();

		} catch (Exception e) {
			logMetacat.error("ReplicationService.handleServerControlRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleServerControlRequest - Error in "
					+ "MetacatReplication.handleServerControlRequest " + e.getMessage());
			e.printStackTrace(System.out);
		} finally {
			try {
				pstmt.close();
			}//try
			catch (SQLException ee) {
				logMetacat.error("ReplicationService.handleServerControlRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.handleServerControlRequest - Error in MetacatReplication.handleServerControlRequest to close pstmt "
						+ ee.getMessage());
			}//catch
			finally {
				DBConnectionPool.returnDBConnection(dbConn, serialNumber);
			}//finally
		}//finally

	}

	// download certificate with the public key from certURL and
	// upload it onto this server; it then must be imported as a
	// trusted certificate
	private static void downloadCertificate(String certURL) throws FileNotFoundException,
			IOException, MalformedURLException, PropertyNotFoundException {

		// the path to be uploaded to
		String certPath = SystemUtil.getContextDir();

		// get filename from the URL of the certificate
		String filename = certURL;
		int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
		if (slash > -1) {
			filename = filename.substring(slash + 1);
		}

		// open file output strem to write the input into it
		File f = new File(certPath, filename);
		synchronized (f) {
			try {
				if (f.exists()) {
					throw new IOException("File already exist: " + f.getCanonicalFile());
					// if ( f.exists() && !f.canWrite() ) {
					// throw new IOException("Not writable: " +
					// f.getCanonicalFile());
				}
			} catch (SecurityException se) {
				// if a security manager exists,
				// its checkRead method is called for f.exist()
				// or checkWrite method is called for f.canWrite()
				throw se;
			}

			// create a buffered byte output stream
			// that uses a default-sized output buffer
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream out = new BufferedOutputStream(fos);

			// this should be http url
			URL url = new URL(certURL);
			BufferedInputStream bis = null;
			try {
				bis = new BufferedInputStream(url.openStream());
				byte[] buf = new byte[4 * 1024]; // 4K buffer
				int b = bis.read(buf);
				while (b != -1) {
					out.write(buf, 0, b);
					b = bis.read(buf);
				}
			} finally {
				if (bis != null)
					bis.close();
			}
			// the input and the output streams must be closed
			bis.close();
			out.flush();
			out.close();
			fos.close();
		} // end of synchronized(f)
	}

	/**
	 * when a forcereplication request comes in, local host sends a read request
	 * to the requesting server (remote server) for the specified docid. Then
	 * store it in local database.
	 */
	protected static void handleForceReplicateRequest(PrintWriter out,
			Hashtable<String, String[]> params, HttpServletResponse response,
			HttpServletRequest request) {
		String server = ((String[]) params.get("server"))[0]; // the server that
		String docid = ((String[]) params.get("docid"))[0]; // sent the document
		String dbaction = "UPDATE"; // the default action is UPDATE
		//    boolean override = false;
		//    int serverCode = 1;
		DBConnection dbConn = null;
		int serialNumber = -1;

		try {
			//if the url contains a dbaction then the default action is overridden
			if (params.containsKey("dbaction")) {
				dbaction = ((String[]) params.get("dbaction"))[0];
				//serverCode = MetacatReplication.getServerCode(server);
				//override = true; //we are now overriding the default action
			}
			logReplication.info("ReplicationService.handleForceReplicateRequest - force replication request from " + server);
			logReplication.info("ReplicationService.handleForceReplicateRequest - Force replication request from: " + server);
			logReplication.info("ReplicationService.handleForceReplicateRequest - Force replication docid: " + docid);
			logReplication.info("ReplicationService.handleForceReplicateRequest - Force replication action: " + dbaction);
			// sending back read request to remote server
			URL u = new URL("https://" + server + "?server="
					+ MetacatUtil.getLocalReplicationServerName() + "&action=read&docid="
					+ docid);
			String xmldoc = ReplicationService.getURLContent(u);

			// get the document info from server
			URL docinfourl = new URL("https://" + server + "?server="
					+ MetacatUtil.getLocalReplicationServerName()
					+ "&action=getdocumentinfo&docid=" + docid);

			String docInfoStr = ReplicationService.getURLContent(docinfourl);

			//dih is the parser for the docinfo xml format
			DocInfoHandler dih = new DocInfoHandler();
			XMLReader docinfoParser = ReplicationHandler.initParser(dih);
			docinfoParser.parse(new InputSource(new StringReader(docInfoStr)));
			//      Hashtable<String,Vector<AccessControlForSingleFile>> docinfoHash = dih.getDocInfo();
			Hashtable<String, String> docinfoHash = dih.getDocInfo();

			
			// Get home server of this docid
			String homeServer = (String) docinfoHash.get("home_server");
			String createdDate = (String) docinfoHash.get("date_created");
			String updatedDate = (String) docinfoHash.get("date_updated");
			logReplication.info("ReplicationService.handleForceReplicateRequest - homeServer: " + homeServer);
			// Get Document type
			String docType = (String) docinfoHash.get("doctype");
			logReplication.info("ReplicationService.handleForceReplicateRequest - docType: " + docType);
			String parserBase = null;
			// this for eml2 and we need user eml2 parser
			if (docType != null
					&& (docType.trim()).equals(DocumentImpl.EML2_0_0NAMESPACE)) {
				logReplication.warn("ReplicationService.handleForceReplicateRequest - This is an eml200 document!");
				parserBase = DocumentImpl.EML200;
			} else if (docType != null
					&& (docType.trim()).equals(DocumentImpl.EML2_0_1NAMESPACE)) {
				logReplication.warn("ReplicationService.handleForceReplicateRequest - This is an eml2.0.1 document!");
				parserBase = DocumentImpl.EML200;
			} else if (docType != null
					&& (docType.trim()).equals(DocumentImpl.EML2_1_0NAMESPACE)) {
				logReplication.warn("ReplicationService.handleForceReplicateRequest - This is an eml2.1.0 document!");
				parserBase = DocumentImpl.EML210;
			}
			logReplication.warn("ReplicationService.handleForceReplicateRequest - The parserBase is: " + parserBase);

			// Get DBConnection from pool
			dbConn = DBConnectionPool
					.getDBConnection("MetacatReplication.handleForceReplicateRequest");
			serialNumber = dbConn.getCheckOutSerialNumber();
			// write the document to local database
			DocumentImplWrapper wrapper = new DocumentImplWrapper(parserBase, false);
			//try this independently so we can set access even if the update action is invalid (i.e docid has not changed)
			try {
				wrapper.writeReplication(dbConn, xmldoc, null, null,
						dbaction, docid, null, null, homeServer, server, createdDate,
						updatedDate);
			} finally {

				//process extra access rules before dealing with the write exception (doc exist already)			
		        Vector<XMLAccessDAO> accessControlList = dih.getAccessControlList();
		        if (accessControlList != null) {
		        	AccessControlForSingleFile acfsf = new AccessControlForSingleFile(docid);
		        	for (XMLAccessDAO xmlAccessDAO : accessControlList) {
		        		if (!acfsf.accessControlExists(xmlAccessDAO)) {
		        			acfsf.insertPermissions(xmlAccessDAO);
							logReplication.info("ReplicationService.handleForceReplicateRequest - document " + docid
									+ " permissions added to DB");
		        		}
		            }
		        }
		        
		        // process the real owner and updater
				String user = (String) docinfoHash.get("user_owner");
				String updated = (String) docinfoHash.get("user_updated");
		        updateUserOwner(dbConn, docid, user, updated);

				logReplication.info("ReplicationService.handleForceReplicateRequest - document " + docid + " added to DB with "
						+ "action " + dbaction);
				EventLog.getInstance().log(request.getRemoteAddr(), REPLICATIONUSER, docid,
						dbaction);
			}
		} catch (SQLException sqle) {
			logMetacat.error("ReplicationService.handleForceReplicateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateRequest - SQL error when adding doc " + docid + 
					" to DB with action " + dbaction + ": " + sqle.getMessage());
		} catch (MalformedURLException mue) {
			logMetacat.error("ReplicationService.handleForceReplicateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateRequest - URL error when adding doc " + docid + 
					" to DB with action " + dbaction + ": " + mue.getMessage());
		} catch (SAXException se) {
			logMetacat.error("ReplicationService.handleForceReplicateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateRequest - SAX parsing error when adding doc " + docid + 
					" to DB with action " + dbaction + ": " + se.getMessage());
		} catch (HandlerException he) {
			logMetacat.error("ReplicationService.handleForceReplicateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateRequest - Handler error when adding doc " + docid + 
					" to DB with action " + dbaction + ": " + he.getMessage());
		} catch (IOException ioe) {
			logMetacat.error("ReplicationService.handleForceReplicateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateRequest - I/O error when adding doc " + docid + 
					" to DB with action " + dbaction + ": " + ioe.getMessage());
		} catch (PermOrderException poe) {
			logMetacat.error("ReplicationService.handleForceReplicateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateRequest - Permissions order error when adding doc " + docid + 
					" to DB with action " + dbaction + ": " + poe.getMessage());
		} catch (AccessControlException ace) {
			logMetacat.error("ReplicationService.handleForceReplicateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateRequest - Permissions order error when adding doc " + docid + 
					" to DB with action " + dbaction + ": " + ace.getMessage());
		} catch (Exception e) {
			logMetacat.error("ReplicationService.handleForceReplicateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateRequest - General error when adding doc " + docid + 
					" to DB with action " + dbaction + ": " + e.getMessage());
		} finally {
			// Return the checked out DBConnection
			DBConnectionPool.returnDBConnection(dbConn, serialNumber);
		}//finally
	}

	/*
	 * when a forcereplication delete request comes in, local host will delete this
	 * document
	 */
	protected static void handleForceReplicateDeleteRequest(PrintWriter out,
			Hashtable<String, String[]> params, HttpServletResponse response,
			HttpServletRequest request) {
		String server = ((String[]) params.get("server"))[0]; // the server that
		String docid = ((String[]) params.get("docid"))[0]; // sent the document
		try {
			logReplication.info("ReplicationService.handleForceReplicateDeleteRequest - force replication delete request from " + server);
			logReplication.info("ReplicationService.handleForceReplicateDeleteRequest - force replication delete docid " + docid);
			logReplication.info("ReplicationService.handleForceReplicateDeleteRequest - Force replication delete request from: " + server);
			logReplication.info("ReplicationService.handleForceReplicateDeleteRequest - Force replication delete docid: " + docid);
			DocumentImpl.delete(docid, null, null, server);
			logReplication.info("ReplicationService.handleForceReplicateDeleteRequest - document " + docid + " was successfully deleted ");
			EventLog.getInstance().log(request.getRemoteAddr(), REPLICATIONUSER, docid,
					"delete");
			logReplication.info("ReplicationService.handleForceReplicateDeleteRequest - document " + docid + " was successfully deleted ");
		} catch (Exception e) {
			logMetacat.error("ReplicationService.handleForceReplicateDeleteRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("document " + docid
					+ " failed to delete because " + e.getMessage());
			logReplication.error("ReplicationService.handleForceReplicateDeleteRequest - error: " + e.getMessage());

		}//catch

	}

	/**
	 * when a forcereplication data file request comes in, local host sends a
	 * readdata request to the requesting server (remote server) for the specified
	 * docid. Then store it in local database and file system
	 */
	protected static void handleForceReplicateDataFileRequest(Hashtable<String, String[]> params,
			HttpServletRequest request) {

		//make sure there is some parameters
		if (params.isEmpty()) {
			return;
		}
		// Get remote server
		String server = ((String[]) params.get("server"))[0];
		// the docid should include rev number
		String docid = ((String[]) params.get("docid"))[0];
		// Make sure there is a docid and server
		if (docid == null || server == null || server.equals("")) {
			logMetacat.error("ReplicationService.handleForceReplicateDataFileRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateDataFileRequest - Didn't specify docid or server for replication");
			return;
		}

		// Overide or not
		//    boolean override = false;
		// dbaction - update or insert
		String dbaction = null;

		try {
			//docid was switch to two parts uinque code and rev
			//String uniqueCode=MetacatUtil.getDocIdFromString(docid);
			//int rev=MetacatUtil.getVersionFromString(docid);
			if (params.containsKey("dbaction")) {
				dbaction = ((String[]) params.get("dbaction"))[0];
			} else//default value is update
			{
				dbaction = "update";
			}

			logReplication.info("ReplicationService.handleForceReplicateDataFileRequest - force replication request from " + server);
			logReplication.info("ReplicationService.handleForceReplicateDataFileRequest - Force replication request from: " + server);
			logReplication.info("ReplicationService.handleForceReplicateDataFileRequest - Force replication docid: " + docid);
			logReplication.info("ReplicationService.handleForceReplicateDataFileRequest - Force replication action: " + dbaction);
			// get the document info from server
			URL docinfourl = new URL("https://" + server + "?server="
					+ MetacatUtil.getLocalReplicationServerName()
					+ "&action=getdocumentinfo&docid=" + docid);

			String docInfoStr = ReplicationService.getURLContent(docinfourl);

			//dih is the parser for the docinfo xml format
			DocInfoHandler dih = new DocInfoHandler();
			XMLReader docinfoParser = ReplicationHandler.initParser(dih);
			docinfoParser.parse(new InputSource(new StringReader(docInfoStr)));
			Hashtable<String, String> docinfoHash = dih.getDocInfo();
			
			String docName = (String) docinfoHash.get("docname");

			String docType = (String) docinfoHash.get("doctype");

			String docHomeServer = (String) docinfoHash.get("home_server");

			String createdDate = (String) docinfoHash.get("date_created");

			String updatedDate = (String) docinfoHash.get("date_updated");
			logReplication.info("ReplicationService.handleForceReplicateDataFileRequest - docHomeServer of datafile: " + docHomeServer);

			//if action is delete, we don't delete the data file. Just archieve
			//the xml_documents
			/*if (dbaction.equals("delete"))
			{
			  //conn = util.getConnection();
			  DocumentImpl.delete(docid,user,null);
			  //util.returnConnection(conn);
			}*/
			//To data file insert or update is same
			if (dbaction.equals("insert") || dbaction.equals("update")) {
				//Get data file and store it into local file system.
				// sending back readdata request to server
				URL url = new URL("https://" + server + "?server="
						+ MetacatUtil.getLocalReplicationServerName()
						+ "&action=readdata&docid=" + docid);
				String datafilePath = PropertyService
						.getProperty("application.datafilepath");

				Exception writeException = null;
				//register data file into xml_documents table and wite data file
				//into file system
				try {
					DocumentImpl.writeDataFileInReplication(url.openStream(),
							datafilePath, docName, docType, docid, null, docHomeServer,
							server, DocumentImpl.DOCUMENTTABLE, false, createdDate,
							updatedDate);
				} catch (Exception e) {
					writeException = e;
				}
				
				// process the real owner and updater
				DBConnection dbConn = DBConnectionPool.getDBConnection("ReplicationService.handleForceDataFileRequest");
		        int serialNumber = dbConn.getCheckOutSerialNumber();
		        dbConn.setAutoCommit(false);
				String user = (String) docinfoHash.get("user_owner");
				String updated = (String) docinfoHash.get("user_updated");
		        updateUserOwner(dbConn, docid, user, updated);
		        DBConnectionPool.returnDBConnection(dbConn, serialNumber);
		        
		        Vector<XMLAccessDAO> accessControlList = dih.getAccessControlList();
		        if (accessControlList != null) {
		        	AccessControlForSingleFile acfsf = new AccessControlForSingleFile(docid);
		        	for (XMLAccessDAO xmlAccessDAO : accessControlList) {
		        		if (!acfsf.accessControlExists(xmlAccessDAO)) {
		        			acfsf.insertPermissions(xmlAccessDAO);
							logReplication.info("ReplicationService.handleForceReplicateRequest - document " + docid
									+ " permissions added to DB");
		        		}
		            }
		        }

				if (writeException != null) {
					throw writeException;
				}

				//false means non-timed replication
				logReplication.info("ReplicationService.handleForceReplicateDataFileRequest - datafile " + docid + " added to DB with "
						+ "action " + dbaction);
				EventLog.getInstance().log(request.getRemoteAddr(), REPLICATIONUSER,
						docid, dbaction);
			}

		} catch (Exception e) {
			logMetacat.error("ReplicationService.handleForceReplicateDataFileRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleForceReplicateDataFileRequest - Datafile " + docid
					+ " failed to added to DB with " + "action " + dbaction + " because "
					+ e.getMessage());
			logReplication.error("ReplicationService.handleForceReplicateDataFileRequest - ERROR in MetacatReplication.handleForceDataFileReplicate"
					+ "Request(): " + e.getMessage());
		}
	}

	/**
	 * Grants or denies a lock to a requesting host.
	 * The servlet parameters of interrest are:
	 * docid: the docid of the file the lock is being requested for
	 * currentdate: the timestamp of the document on the remote server
	 *
	 */
	protected static void handleGetLockRequest(PrintWriter out,
			Hashtable<String, String[]> params, HttpServletResponse response) {

		try {

			String docid = ((String[]) params.get("docid"))[0];
			String remoteRev = ((String[]) params.get("updaterev"))[0];
			DocumentImpl requestDoc = new DocumentImpl(docid);
			logReplication.info("ReplicationService.handleGetLockRequest - lock request for " + docid);
			int localRevInt = requestDoc.getRev();
			int remoteRevInt = Integer.parseInt(remoteRev);

			if (remoteRevInt >= localRevInt) {
				if (!fileLocks.contains(docid)) { //grant the lock if it is not already locked
					fileLocks.add(0, docid); //insert at the beginning of the queue Vector
					//send a message back to the the remote host authorizing the insert
					out
							.println("<lockgranted><docid>" + docid
									+ "</docid></lockgranted>");
					//          lockThread = new Thread(this);
					//          lockThread.setPriority(Thread.MIN_PRIORITY);
					//          lockThread.start();
					logReplication.info("ReplicationService.handleGetLockRequest - lock granted for " + docid);
				} else { //deny the lock
					out.println("<filelocked><docid>" + docid + "</docid></filelocked>");
					logReplication.info("ReplicationService.handleGetLockRequest - lock denied for " + docid
							+ "reason: file already locked");
				}
			} else {//deny the lock.
				out.println("<outdatedfile><docid>" + docid + "</docid></filelocked>");
				logReplication.info("ReplicationService.handleGetLockRequest - lock denied for " + docid
						+ "reason: client has outdated file");
			}
			//conn.close();
		} catch (Exception e) {
			logMetacat.error("ReplicationService.handleGetLockRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleGetLockRequest - error requesting file lock from MetacatReplication."
					+ "handleGetLockRequest: " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Sends all of the xml_documents information encoded in xml to a requestor
	 * the format is:
	 * <!ELEMENT documentinfo (docid, docname, doctype, doctitle, user_owner,
	 *                  user_updated, home_server, public_access, rev)/>
	 * all of the subelements of document info are #PCDATA
	 */
	protected static void handleGetDocumentInfoRequest(PrintWriter out,
			Hashtable<String, String[]> params, HttpServletResponse response) {
		String docid = ((String[]) (params.get("docid")))[0];
		StringBuffer sb = new StringBuffer();

		try {

			DocumentImpl doc = new DocumentImpl(docid);
			sb.append("<documentinfo><docid>").append(docid);
			sb.append("</docid><docname>").append(doc.getDocname());
			sb.append("</docname><doctype>").append(doc.getDoctype());
			sb.append("</doctype>");
			sb.append("<user_owner>").append(doc.getUserowner());
			sb.append("</user_owner><user_updated>").append(doc.getUserupdated());
			sb.append("</user_updated>");
			sb.append("<date_created>");
			sb.append(doc.getCreateDate());
			sb.append("</date_created>");
			sb.append("<date_updated>");
			sb.append(doc.getUpdateDate());
			sb.append("</date_updated>");
			sb.append("<home_server>");
			sb.append(doc.getDocHomeServer());
			sb.append("</home_server>");
			sb.append("<public_access>").append(doc.getPublicaccess());
			sb.append("</public_access><rev>").append(doc.getRev());
			sb.append("</rev>");

			sb.append("<accessControl>");

			AccessControlForSingleFile acfsf = new AccessControlForSingleFile(docid); 
			sb.append(acfsf.getAccessString());
			
			sb.append("</accessControl>");

			sb.append("</documentinfo>");
			response.setContentType("text/xml");
			out.println(sb.toString());

		} catch (Exception e) {
			logMetacat.error("ReplicationService.handleGetDocumentInfoRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleGetDocumentInfoRequest - error in metacatReplication.handlegetdocumentinforequest "
					+ "for doc: " + docid + " : " + e.getMessage());
		}

	}

	/**
	 * Sends a datafile to a remote host
	 */
	protected static void handleGetDataFileRequest(OutputStream outPut,
			Hashtable<String, String[]> params, HttpServletResponse response)

	{
		// File path for data file
		String filepath;
		// Request docid
		String docId = ((String[]) (params.get("docid")))[0];
		//check if the doicd is null
		if (docId == null) {
			logMetacat.error("ReplicationService.handleGetDataFileRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleGetDataFileRequest - Didn't specify docid for replication");
			return;
		}

		//try to open a https stream to test if the request server's public key
		//in the key store, this is security issue
		try {
			filepath = PropertyService.getProperty("application.datafilepath");
			String server = params.get("server")[0];
			URL u = new URL("https://" + server + "?server="
					+ MetacatUtil.getLocalReplicationServerName() + "&action=test");
			String test = ReplicationService.getURLContent(u);
			//couldn't pass the test
			if (test.indexOf("successfully") == -1) {
				//response.setContentType("text/xml");
				//outPut.println("<error>Couldn't pass the trust test</error>");
				logMetacat.error("ReplicationService.handleGetDataFileRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.handleGetDataFileRequest - Couldn't pass the trust test");
				return;
			}
		}//try
		catch (Exception ee) {
			return;
		}//catch

		if (!filepath.endsWith("/")) {
			filepath += "/";
		}
		// Get file aboslute file name
		String filename = filepath + docId;

		//MIME type
		String contentType = null;
		if (filename.endsWith(".xml")) {
			contentType = "text/xml";
		} else if (filename.endsWith(".css")) {
			contentType = "text/css";
		} else if (filename.endsWith(".dtd")) {
			contentType = "text/plain";
		} else if (filename.endsWith(".xsd")) {
			contentType = "text/xml";
		} else if (filename.endsWith("/")) {
			contentType = "text/html";
		} else {
			File f = new File(filename);
			if (f.isDirectory()) {
				contentType = "text/html";
			} else {
				contentType = "application/octet-stream";
			}
		}

		// Set the mime type
		response.setContentType(contentType);

		// Get the content of the file
		FileInputStream fin = null;
		try {
			// FileInputStream to metacat
			fin = new FileInputStream(filename);
			// 4K buffer
			byte[] buf = new byte[4 * 1024];
			// Read data from file input stream to byte array
			int b = fin.read(buf);
			// Write to outStream from byte array
			while (b != -1) {
				outPut.write(buf, 0, b);
				b = fin.read(buf);
			}
			// close file input stream
			fin.close();

		}//try
		catch (Exception e) {
			logMetacat.error("ReplicationService.handleGetDataFileRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleGetDataFileRequest - error getting data file from MetacatReplication."
					+ "handlGetDataFileRequest " + e.getMessage());
			e.printStackTrace(System.out);
		}//catch

	}

	/**
	 * Sends a document to a remote host
	 */
	protected static void handleGetDocumentRequest(PrintWriter out,
			Hashtable<String, String[]> params, HttpServletResponse response) {

		String urlString = null;
		String documentPath = null;
		try {
			// try to open a https stream to test if the request server's public
			// key
			// in the key store, this is security issue
			String server = params.get("server")[0];
			urlString = "https://" + server + "?server="
					+ MetacatUtil.getLocalReplicationServerName() + "&action=test";
			URL u = new URL(urlString);
			String test = ReplicationService.getURLContent(u);
			// couldn't pass the test
			if (test.indexOf("successfully") == -1) {
				response.setContentType("text/xml");
				out.println("<error>Couldn't pass the trust test " + test + " </error>");
				out.close();
				return;
			}

			String docid = params.get("docid")[0];
			logReplication.debug("ReplicationService.handleGetDocumentRequest - MetacatReplication.handleGetDocumentRequest for docid: "
					+ docid);
			DocumentImpl di = new DocumentImpl(docid);

			String documentDir = PropertyService
					.getProperty("application.documentfilepath");
			documentPath = documentDir + FileUtil.getFS() + docid;

			// if the document does not exist on disk, read it from db and write
			// it to disk.
			if (FileUtil.getFileStatus(documentPath) == FileUtil.DOES_NOT_EXIST
					|| FileUtil.getFileSize(documentPath) == 0) {
				FileWriter fileWriter = new FileWriter(documentPath);
				di.toXml(fileWriter, null, null, true);
			}

			// read the file from disk and sent it to PrintWriter
			// PrintWriter out = new PrintWriter(streamOut);
			di.readFromFileSystem(out, null, null, documentPath);

			// response.setContentType("text/xml");
			// out.print(di.toString(null, null, true));

			logReplication.info("ReplicationService.handleGetDocumentRequest - document " + docid + " sent");

		} catch (MalformedURLException mue) {
			logMetacat.error("ReplicationService.handleGetDocumentRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleGetDocumentRequest - Url error when getting document from MetacatReplication."
					+ "handlGetDocumentRequest for url: " + urlString + " : "
					+ mue.getMessage());
			// e.printStackTrace(System.out);
			response.setContentType("text/xml");
			out.println("<error>" + mue.getMessage() + "</error>");
		} catch (IOException ioe) {
			logMetacat.error("ReplicationService.handleGetDocumentRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleGetDocumentRequest - I/O error when getting document from MetacatReplication."
					+ "handlGetDocumentRequest for file: " + documentPath + " : "
					+ ioe.getMessage());
			// e.printStackTrace(System.out);
			response.setContentType("text/xml");
			out.println("<error>" + ioe.getMessage() + "</error>");
		} catch (PropertyNotFoundException pnfe) {
			logMetacat.error("ReplicationService.handleGetDocumentRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication
					.error("ReplicationService.handleGetDocumentRequest - Error getting property when getting document from MetacatReplication."
							+ "handlGetDocumentRequest for file: "
							+ documentPath
							+ " : "
							+ pnfe.getMessage());
			// e.printStackTrace(System.out);
			response.setContentType("text/xml");
			out.println("<error>" + pnfe.getMessage() + "</error>");
		} catch (McdbException me) {
			logReplication
					.error("ReplicationService.handleGetDocumentRequest - Document implementation error  getting property when getting document from MetacatReplication."
							+ "handlGetDocumentRequest for file: "
							+ documentPath
							+ " : "
							+ me.getMessage());
			// e.printStackTrace(System.out);
			response.setContentType("text/xml");
			out.println("<error>" + me.getMessage() + "</error>");
		}

	}

	/**
	 * Sends a list of all of the documents on this sever along with their
	 * revision numbers. The format is: <!ELEMENT replication (server, updates)>
	 * <!ELEMENT server (#PCDATA)> <!ELEMENT updates ((updatedDocument |
	 * deleteDocument | revisionDocument)*)> <!ELEMENT updatedDocument (docid,
	 * rev, datafile*)> <!ELEMENT deletedDocument (docid, rev)> <!ELEMENT
	 * revisionDocument (docid, rev, datafile*)> <!ELEMENT docid (#PCDATA)>
	 * <!ELEMENT rev (#PCDATA)> <!ELEMENT datafile (#PCDATA)> note that the rev
	 * in deletedDocument is always empty. I just left it in there to make the
	 * parser implementation easier.
	 */
	protected static void handleUpdateRequest(PrintWriter out, Hashtable<String, String[]> params,
			HttpServletResponse response) {
		// Checked out DBConnection
		DBConnection dbConn = null;
		// DBConenction serial number when checked it out
		int serialNumber = -1;
		PreparedStatement pstmt = null;
		// Server list to store server info of xml_replication table
		ReplicationServerList serverList = null;

		try {
			// Check out a DBConnection from pool
			dbConn = DBConnectionPool
					.getDBConnection("MetacatReplication.handleUpdateRequest");
			serialNumber = dbConn.getCheckOutSerialNumber();
			// Create a server list from xml_replication table
			serverList = new ReplicationServerList();

			// Get remote server name from param
			String server = ((String[]) params.get("server"))[0];
			// If no servr name in param, return a error
			if (server == null || server.equals("")) {
				response.setContentType("text/xml");
				out.println("<error>Request didn't specify server name</error>");
				out.close();
				return;
			}//if

			//try to open a https stream to test if the request server's public key
			//in the key store, this is security issue
			URL u = new URL("https://" + server + "?server="
					+ MetacatUtil.getLocalReplicationServerName() + "&action=test");
			String test = ReplicationService.getURLContent(u);
			//couldn't pass the test
			if (test.indexOf("successfully") == -1) {
				response.setContentType("text/xml");
				out.println("<error>Couldn't pass the trust test</error>");
				out.close();
				return;
			}

			// Check if local host configure to replicate xml documents to remote
			// server. If not send back a error message
			if (!serverList.getReplicationValue(server)) {
				response.setContentType("text/xml");
				out
						.println("<error>Configuration not allow to replicate document to you</error>");
				out.close();
				return;
			}//if

			// Store the sql command
			StringBuffer docsql = new StringBuffer();
			StringBuffer revisionSql = new StringBuffer();
			// Stroe the docid list
			StringBuffer doclist = new StringBuffer();
			// Store the deleted docid list
			StringBuffer delsql = new StringBuffer();
			// Store the data set file
			Vector<Vector<String>> packageFiles = new Vector<Vector<String>>();

			// Append local server's name and replication servlet to doclist
			doclist.append("<?xml version=\"1.0\"?><replication>");
			doclist.append("<server>")
					.append(MetacatUtil.getLocalReplicationServerName());
			//doclist.append(util.getProperty("replicationpath"));
			doclist.append("</server><updates>");

			// Get correct docid that reside on this server according the requesting
			// server's replicate and data replicate value in xml_replication table
			docsql.append(DatabaseService.getDBAdapter().getReplicationDocumentListSQL());
			//docsql.append("select docid, rev, doctype from xml_documents where (docid not in (select a.docid from xml_documents a, xml_revisions b where a.docid=b.docid and a.rev<=b.rev)) ");
			revisionSql.append("select docid, rev, doctype from xml_revisions ");
			// If the localhost is not a hub to the remote server, only replicate
			// the docid' which home server is local host (server_location =1)
			if (!serverList.getHubValue(server)) {
				String serverLocationDoc = " and a.server_location = 1";
				String serverLocationRev = "where server_location = 1";
				docsql.append(serverLocationDoc);
				revisionSql.append(serverLocationRev);
			}
			logReplication.info("ReplicationService.handleUpdateRequest - Doc sql: " + docsql.toString());

			// Get any deleted documents
			delsql.append("select distinct docid from ");
			delsql.append("xml_revisions where docid not in (select docid from ");
			delsql.append("xml_documents) ");
			// If the localhost is not a hub to the remote server, only replicate
			// the docid' which home server is local host (server_location =1)
			if (!serverList.getHubValue(server)) {
				delsql.append("and server_location = 1");
			}
			logReplication.info("ReplicationService.handleUpdateRequest - Deleted sql: " + delsql.toString());

			// Get docid list of local host
			pstmt = dbConn.prepareStatement(docsql.toString());
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			boolean tablehasrows = rs.next();
			//If metacat configed to replicate data file
			//if ((util.getProperty("replicationsenddata")).equals("on"))
			boolean replicateData = serverList.getDataReplicationValue(server);
			if (replicateData) {
				while (tablehasrows) {
					String recordDoctype = rs.getString(3);
					Vector<String> packagedoctypes = MetacatUtil
							.getOptionList(PropertyService
									.getProperty("xml.packagedoctype"));
					//if this is a package file, put it at the end
					//because if a package file is read before all of the files it
					//refers to are loaded then there is an error
					if (recordDoctype != null && !packagedoctypes.contains(recordDoctype)) {
						//If this is not data file
						if (!recordDoctype.equals("BIN")) {
							//for non-data file document
							doclist.append("<updatedDocument>");
							doclist.append("<docid>").append(rs.getString(1));
							doclist.append("</docid><rev>").append(rs.getInt(2));
							doclist.append("</rev>");
							doclist.append("</updatedDocument>");
						}//if
						else {
							//for data file document, in datafile attributes
							//we put "datafile" value there
							doclist.append("<updatedDocument>");
							doclist.append("<docid>").append(rs.getString(1));
							doclist.append("</docid><rev>").append(rs.getInt(2));
							doclist.append("</rev>");
							doclist.append("<datafile>");
							doclist.append(PropertyService
									.getProperty("replication.datafileflag"));
							doclist.append("</datafile>");
							doclist.append("</updatedDocument>");
						}//else
					}//if packagedoctpes
					else { //the package files are saved to be put into the xml later.
						Vector<String> v = new Vector<String>();
						v.add(rs.getString(1));
						v.add(String.valueOf(rs.getInt(2)));
						packageFiles.add(v);
					}//esle
					tablehasrows = rs.next();
				}//while
			}//if
			else //metacat was configured not to send data file
			{
				while (tablehasrows) {
					String recordDoctype = rs.getString(3);
					if (!recordDoctype.equals("BIN")) { //don't replicate data files
						Vector<String> packagedoctypes = MetacatUtil
								.getOptionList(PropertyService
										.getProperty("xml.packagedoctype"));
						if (recordDoctype != null
								&& !packagedoctypes.contains(recordDoctype)) { //if this is a package file, put it at the end
							//because if a package file is read before all of the files it
							//refers to are loaded then there is an error
							doclist.append("<updatedDocument>");
							doclist.append("<docid>").append(rs.getString(1));
							doclist.append("</docid><rev>").append(rs.getInt(2));
							doclist.append("</rev>");
							doclist.append("</updatedDocument>");
						} else { //the package files are saved to be put into the xml later.
							Vector<String> v = new Vector<String>();
							v.add(rs.getString(1));
							v.add(String.valueOf(rs.getInt(2)));
							packageFiles.add(v);
						}
					}//if
					tablehasrows = rs.next();
				}//while
			}//else

			pstmt = dbConn.prepareStatement(delsql.toString());
			//usage count should increas 1
			dbConn.increaseUsageCount(1);

			pstmt.execute();
			rs = pstmt.getResultSet();
			tablehasrows = rs.next();
			while (tablehasrows) { //handle the deleted documents
				doclist.append("<deletedDocument><docid>").append(rs.getString(1));
				doclist.append("</docid><rev></rev></deletedDocument>");
				//note that rev is always empty for deleted docs
				tablehasrows = rs.next();
			}

			//now we can put the package files into the xml results
			for (int i = 0; i < packageFiles.size(); i++) {
				Vector<String> v = packageFiles.elementAt(i);
				doclist.append("<updatedDocument>");
				doclist.append("<docid>").append(v.elementAt(0));
				doclist.append("</docid><rev>");
				doclist.append(v.elementAt(1));
				doclist.append("</rev>");
				doclist.append("</updatedDocument>");
			}
			// add revision doc list  
			doclist.append(prepareRevisionDoc(dbConn, revisionSql.toString(),
					replicateData));

			doclist.append("</updates></replication>");
			logReplication.info("ReplicationService.handleUpdateRequest - doclist: " + doclist.toString());
			pstmt.close();
			//conn.close();
			response.setContentType("text/xml");
			out.println(doclist.toString());

		} catch (Exception e) {
			logMetacat.error("ReplicationService.handleUpdateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleUpdateRequest - error in MetacatReplication." + "handleupdaterequest: "
					+ e.getMessage());
			//e.printStackTrace(System.out);
			response.setContentType("text/xml");
			out.println("<error>" + e.getMessage() + "</error>");
		} finally {
			try {
				pstmt.close();
			}//try
			catch (SQLException ee) {
				logMetacat.error("ReplicationService.handleUpdateRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.handleUpdateRequest - Error in MetacatReplication."
						+ "handleUpdaterequest to close pstmt: " + ee.getMessage());
			}//catch
			finally {
				DBConnectionPool.returnDBConnection(dbConn, serialNumber);
			}//finally
		}//finally

	}//handlUpdateRequest

	/**
	 * 
	 * @param dbConn connection for doing the update
	 * @param docid the document id to update
	 * @param owner the user_owner
	 * @param updater the user_updated
	 * @throws SQLException
	 */
	public static void updateUserOwner(DBConnection dbConn, String docid, String owner, String updater) throws SQLException {
	
		String sql = 
			"UPDATE xml_documents " +
			"SET user_owner = ?, " +
			"user_updated = ? " +
			"WHERE docid = ?;";
		PreparedStatement pstmt = dbConn.prepareStatement(sql);
		//usage count should increas 1
		dbConn.increaseUsageCount(1);

		docid = DocumentUtil.getSmartDocId(docid);
		pstmt.setString(1, owner);
		pstmt.setString(2, updater);
		pstmt.setString(3, docid);
		pstmt.execute();
		pstmt.close();
		
		dbConn.commit();
	}
	
	/*
	 * This method will get the xml string for document in xml_revision
	 * The schema look like <!ELEMENT revisionDocument (docid, rev, datafile*)>
	 */
	private static String prepareRevisionDoc(DBConnection dbConn, String revSql,
			boolean replicateData) throws Exception {
		logReplication.warn("ReplicationService.prepareRevisionDoc - The revision document sql is " + revSql);
		StringBuffer revDocList = new StringBuffer();
		PreparedStatement pstmt = dbConn.prepareStatement(revSql);
		//usage count should increas 1
		dbConn.increaseUsageCount(1);

		pstmt.execute();
		ResultSet rs = pstmt.getResultSet();
		boolean tablehasrows = rs.next();
		while (tablehasrows) {
			String recordDoctype = rs.getString(3);

			//If this is data file and it isn't configured to replicate data
			if (recordDoctype.equals("BIN") && !replicateData) {
				// do nothing
				continue;
			} else {

				revDocList.append("<revisionDocument>");
				revDocList.append("<docid>").append(rs.getString(1));
				revDocList.append("</docid><rev>").append(rs.getInt(2));
				revDocList.append("</rev>");
				// data file
				if (recordDoctype.equals("BIN")) {
					revDocList.append("<datafile>");
					revDocList.append(PropertyService
							.getProperty("replication.datafileflag"));
					revDocList.append("</datafile>");
				}
				revDocList.append("</revisionDocument>");

			}//else
			tablehasrows = rs.next();
		}
		//System.out.println("The revision list is"+ revDocList.toString());
		return revDocList.toString();
	}

	/**
	 * Returns the xml_catalog table encoded in xml
	 */
	public static String getCatalogXML() {
		return handleGetCatalogRequest(null, null, null, false);
	}

	/**
	 * Sends the contents of the xml_catalog table encoded in xml
	 * The xml format is:
	 * <!ELEMENT xml_catalog (row*)>
	 * <!ELEMENT row (entry_type, source_doctype, target_doctype, public_id,
	 *                system_id)>
	 * All of the sub elements of row are #PCDATA

	 * If printFlag == false then do not print to out.
	 */
	protected static String handleGetCatalogRequest(PrintWriter out,
			Hashtable<String, String[]> params, HttpServletResponse response,
			boolean printFlag) {
		DBConnection dbConn = null;
		int serialNumber = -1;
		PreparedStatement pstmt = null;
		try {
			/*conn = MetacatReplication.getDBConnection("MetacatReplication." +
			                                          "handleGetCatalogRequest");*/
			dbConn = DBConnectionPool
					.getDBConnection("MetacatReplication.handleGetCatalogRequest");
			serialNumber = dbConn.getCheckOutSerialNumber();
			pstmt = dbConn.prepareStatement("select entry_type, "
					+ "source_doctype, target_doctype, public_id, "
					+ "system_id from xml_catalog");
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			boolean tablehasrows = rs.next();
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\"?><xml_catalog>");
			while (tablehasrows) {
				sb.append("<row><entry_type>").append(rs.getString(1));
				sb.append("</entry_type><source_doctype>").append(rs.getString(2));
				sb.append("</source_doctype><target_doctype>").append(rs.getString(3));
				sb.append("</target_doctype><public_id>").append(rs.getString(4));
				// system id may not have server url on front.  Add it if not.
				String systemID = rs.getString(5);
				if (!systemID.startsWith("http://")) {
					systemID = SystemUtil.getContextURL() + systemID;
				}
				sb.append("</public_id><system_id>").append(systemID);
				sb.append("</system_id></row>");

				tablehasrows = rs.next();
			}
			sb.append("</xml_catalog>");
			//conn.close();
			if (printFlag) {
				response.setContentType("text/xml");
				out.println(sb.toString());
			}
			pstmt.close();
			return sb.toString();
		} catch (Exception e) {
			logMetacat.error("ReplicationService.handleGetCatalogRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.handleGetCatalogRequest - error in MetacatReplication.handleGetCatalogRequest:"
					+ e.getMessage());
			e.printStackTrace(System.out);
			if (printFlag) {
				out.println("<error>" + e.getMessage() + "</error>");
			}
		} finally {
			try {
				pstmt.close();
			}//try
			catch (SQLException ee) {
				logMetacat.error("ReplicationService.handleGetCatalogRequest - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.handleGetCatalogRequest - Error in MetacatReplication.handleGetCatalogRequest: "
						+ ee.getMessage());
			}//catch
			finally {
				DBConnectionPool.returnDBConnection(dbConn, serialNumber);
			}//finally
		}//finally

		return null;
	}

	/**
	 * Sends the current system date to the remote server.  Using this action
	 * for replication gets rid of any problems with syncronizing clocks
	 * because a time specific to a document is always kept on its home server.
	 */
	protected static void handleGetTimeRequest(PrintWriter out,
			Hashtable<String, String[]> params, HttpServletResponse response) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		java.util.Date localtime = new java.util.Date();
		String dateString = formatter.format(localtime);
		response.setContentType("text/xml");

		out.println("<timestamp>" + dateString + "</timestamp>");
	}

	/**
	 * this method handles the timeout for a file lock.  when a lock is
	 * granted it is granted for 30 seconds.  When this thread runs out
	 * it deletes the docid from the queue, thus eliminating the lock.
	 */
	public void run() {
		try {
			logReplication.info("ReplicationService.run - thread started for docid: "
					+ (String) fileLocks.elementAt(0));

			Thread.sleep(30000); //the lock will expire in 30 seconds
			logReplication.info("thread for docid: "
					+ (String) fileLocks.elementAt(fileLocks.size() - 1) + " exiting.");

			fileLocks.remove(fileLocks.size() - 1);
			//fileLocks is treated as a FIFO queue.  If there are more than one lock
			//in the vector, the first one inserted will be removed.
		} catch (Exception e) {
			logMetacat.error("ReplicationService.run - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.run - error in file lock thread from "
					+ "MetacatReplication.run: " + e.getMessage());
		}
	}

	/**
	 * Returns the name of a server given a serverCode
	 * @param serverCode the serverid of the server
	 * @return the servername or null if the specified serverCode does not
	 *         exist.
	 */
	public static String getServerNameForServerCode(int serverCode) {
		//System.out.println("serverid: " + serverCode);
		DBConnection dbConn = null;
		int serialNumber = -1;
		PreparedStatement pstmt = null;
		try {
			dbConn = DBConnectionPool.getDBConnection("MetacatReplication.getServer");
			serialNumber = dbConn.getCheckOutSerialNumber();
			String sql = new String("select server from "
					+ "xml_replication where serverid = " + serverCode);
			pstmt = dbConn.prepareStatement(sql);
			//System.out.println("getserver sql: " + sql);
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			boolean tablehasrows = rs.next();
			if (tablehasrows) {
				//System.out.println("server: " + rs.getString(1));
				return rs.getString(1);
			}

			//conn.close();
		} catch (Exception e) {
			logMetacat.error("ReplicationService.getServerNameForServerCode - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.getServerNameForServerCode - Error in MetacatReplication.getServer: " + e.getMessage());
		} finally {
			try {
				pstmt.close();
			}//try
			catch (SQLException ee) {
				logMetacat.error("ReplicationService.getServerNameForServerCode - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.getServerNameForServerCode - Error in MetacactReplication.getserver: "
						+ ee.getMessage());
			}//catch
			finally {
				DBConnectionPool.returnDBConnection(dbConn, serialNumber);
			}//fianlly
		}//finally

		return null;
		//return null if the server does not exist
	}

	/**
	 * Returns a server code given a server name
	 * @param server the name of the server
	 * @return integer > 0 representing the code of the server, 0 if the server
	 *  does not exist.
	 */
	public static int getServerCodeForServerName(String server) throws ServiceException {
		DBConnection dbConn = null;
		int serialNumber = -1;
		PreparedStatement pstmt = null;
		int serverCode = 0;

		try {

			//conn = util.openDBConnection();
			dbConn = DBConnectionPool.getDBConnection("MetacatReplication.getServerCode");
			serialNumber = dbConn.getCheckOutSerialNumber();
			pstmt = dbConn.prepareStatement("SELECT serverid FROM xml_replication "
					+ "WHERE server LIKE '" + server + "'");
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			boolean tablehasrows = rs.next();
			if (tablehasrows) {
				serverCode = rs.getInt(1);
				pstmt.close();
				//conn.close();
				return serverCode;
			}

		} catch (SQLException sqle) {
			throw new ServiceException("ReplicationService.getServerCodeForServerName - " 
					+ "SQL error when getting server code: " + sqle.getMessage());

		} finally {
			try {
				pstmt.close();
				//conn.close();
			}//try
			catch (Exception ee) {
				logMetacat.error("ReplicationService.getServerCodeForServerName - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.getServerNameForServerCode - Error in MetacatReplicatio.getServerCode: "
						+ ee.getMessage());

			}//catch
			finally {
				DBConnectionPool.returnDBConnection(dbConn, serialNumber);
			}//finally
		}//finally

		return serverCode;
	}

	/**
	 * Method to get a host server information for given docid
	 * @param conn a connection to the database
	 */
	public static Hashtable<String, String> getHomeServerInfoForDocId(String docId) {
		Hashtable<String, String> sl = new Hashtable<String, String>();
		DBConnection dbConn = null;
		int serialNumber = -1;
		docId = DocumentUtil.getDocIdFromString(docId);
		PreparedStatement pstmt = null;
		int serverLocation;
		try {
			//get conection
			dbConn = DBConnectionPool.getDBConnection("ReplicationHandler.getHomeServer");
			serialNumber = dbConn.getCheckOutSerialNumber();
			//get a server location from xml_document table
			pstmt = dbConn.prepareStatement("select server_location from xml_documents "
					+ "where docid = ?");
			pstmt.setString(1, docId);
			pstmt.execute();
			ResultSet serverName = pstmt.getResultSet();
			//get a server location
			if (serverName.next()) {
				serverLocation = serverName.getInt(1);
				pstmt.close();
			} else {
				pstmt.close();
				//ut.returnConnection(conn);
				return null;
			}
			pstmt = dbConn.prepareStatement("select server, last_checked, replicate "
					+ "from xml_replication where serverid = ?");
			//increase usage count
			dbConn.increaseUsageCount(1);
			pstmt.setInt(1, serverLocation);
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			boolean tableHasRows = rs.next();
			if (tableHasRows) {

				String server = rs.getString(1);
				String last_checked = rs.getString(2);
				if (!server.equals("localhost")) {
					sl.put(server, last_checked);
				}

			} else {
				pstmt.close();
				//ut.returnConnection(conn);
				return null;
			}
			pstmt.close();
		} catch (Exception e) {
			logMetacat.error("ReplicationService.getHomeServerInfoForDocId - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.getHomeServerInfoForDocId - error in replicationHandler.getHomeServer(): "
					+ e.getMessage());
		} finally {
			try {
				pstmt.close();
				//ut.returnConnection(conn);
			} catch (Exception ee) {
				logMetacat.error("ReplicationService.getHomeServerInfoForDocId - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.getHomeServerInfoForDocId - Eror irn rplicationHandler.getHomeServer() "
						+ "to close pstmt: " + ee.getMessage());
			} finally {
				DBConnectionPool.returnDBConnection(dbConn, serialNumber);
			}

		}//finally
		return sl;
	}

	/**
	 * Returns a home server location  given a accnum
	 * @param accNum , given accNum for a document
	 *
	 */
	public static int getHomeServerCodeForDocId(String accNum) throws ServiceException {
		DBConnection dbConn = null;
		int serialNumber = -1;
		PreparedStatement pstmt = null;
		int serverCode = 1;
		String docId = DocumentUtil.getDocIdFromString(accNum);

		try {

			// Get DBConnection
			dbConn = DBConnectionPool
					.getDBConnection("ReplicationHandler.getServerLocation");
			serialNumber = dbConn.getCheckOutSerialNumber();
			pstmt = dbConn.prepareStatement("SELECT server_location FROM xml_documents "
					+ "WHERE docid LIKE '" + docId + "'");
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			boolean tablehasrows = rs.next();
			//If a document is find, return the server location for it
			if (tablehasrows) {
				serverCode = rs.getInt(1);
				pstmt.close();
				//conn.close();
				return serverCode;
			}
			//if couldn't find in xml_documents table, we think server code is 1
			//(this is new document)
			else {
				pstmt.close();
				//conn.close();
				return serverCode;
			}

		} catch (SQLException sqle) {
			throw new ServiceException("ReplicationService.getHomeServerCodeForDocId - " 
					+ "SQL error when getting home server code for docid: " + docId + " : " 
					+ sqle.getMessage());

		} finally {
			try {
				pstmt.close();
				//conn.close();

			} catch (SQLException sqle) {
				logMetacat.error("ReplicationService.getHomeServerCodeForDocId - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.getHomeServerCodeForDocId - ReplicationService.getHomeServerCodeForDocId - " 
						+ "SQL error when getting home server code for docid: " + docId + " : " 
						+ sqle.getMessage());
			} finally {
				DBConnectionPool.returnDBConnection(dbConn, serialNumber);
			}//finally
		}//finally
		//return serverCode;
	}

	/**
	 * This method returns the content of a url
	 * @param u the url to return the content from
	 * @return a string representing the content of the url
	 * @throws java.io.IOException
	 */
	public static String getURLContent(URL u) throws java.io.IOException {
		char istreamChar;
		int istreamInt;
		logReplication.debug("ReplicationService.getURLContent - Before open the stream" + u.toString());
		InputStream input = u.openStream();
		logReplication.debug("ReplicationService.getURLContent - After open the stream" + u.toString());
		InputStreamReader istream = new InputStreamReader(input);
		StringBuffer serverResponse = new StringBuffer();
		while ((istreamInt = istream.read()) != -1) {
			istreamChar = (char) istreamInt;
			serverResponse.append(istreamChar);
		}
		istream.close();
		input.close();

		return serverResponse.toString();
	}

//	/**
//	 * Method for writing replication messages to a log file specified in
//	 * metacat.properties
//	 */
//	public static void replLog(String message) {
//		try {
//			FileOutputStream fos = new FileOutputStream(PropertyService
//					.getProperty("replication.logdir")
//					+ "/metacatreplication.log", true);
//			PrintWriter pw = new PrintWriter(fos);
//			SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//			java.util.Date localtime = new java.util.Date();
//			String dateString = formatter.format(localtime);
//			dateString += " :: " + message;
//			// time stamp each entry
//			pw.println(dateString);
//			pw.flush();
//		} catch (Exception e) {
//			logReplication.error("error writing to replication log from "
//					+ "MetacatReplication.replLog: " + e.getMessage());
//			// e.printStackTrace(System.out);
//		}
//	}

//	/**
//	 * Method for writing replication messages to a log file specified in
//	 * metacat.properties
//	 */
//	public static void replErrorLog(String message) {
//		try {
//			FileOutputStream fos = new FileOutputStream(PropertyService
//					.getProperty("replication.logdir")
//					+ "/metacatreplicationerror.log", true);
//			PrintWriter pw = new PrintWriter(fos);
//			SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//			java.util.Date localtime = new java.util.Date();
//			String dateString = formatter.format(localtime);
//			dateString += " :: " + message;
//			//time stamp each entry
//			pw.println(dateString);
//			pw.flush();
//		} catch (Exception e) {
//			logReplication.error("error writing to replication error log from "
//					+ "MetacatReplication.replErrorLog: " + e.getMessage());
//			//e.printStackTrace(System.out);
//		}
//	}

	/**
	 * Returns true if the replicate field for server in xml_replication is 1.
	 * Returns false otherwise
	 */
	public static boolean replToServer(String server) {
		DBConnection dbConn = null;
		int serialNumber = -1;
		PreparedStatement pstmt = null;
		try {
			dbConn = DBConnectionPool.getDBConnection("MetacatReplication.repltoServer");
			serialNumber = dbConn.getCheckOutSerialNumber();
			pstmt = dbConn.prepareStatement("select replicate from "
					+ "xml_replication where server like '" + server + "'");
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			boolean tablehasrows = rs.next();
			if (tablehasrows) {
				int i = rs.getInt(1);
				if (i == 1) {
					pstmt.close();
					//conn.close();
					return true;
				} else {
					pstmt.close();
					//conn.close();
					return false;
				}
			}
		} catch (SQLException sqle) {
			logMetacat.error("ReplicationService.replToServer - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
			logReplication.error("ReplicationService.replToServer - SQL error in MetacatReplication.replToServer: "
					+ sqle.getMessage());
		} finally {
			try {
				pstmt.close();
				//conn.close();
			}//try
			catch (Exception ee) {
				logMetacat.error("ReplicationService.replToServer - " + ReplicationService.METACAT_REPL_ERROR_MSG);                         
				logReplication.error("ReplicationService.replToServer - Error in MetacatReplication.replToServer: "
						+ ee.getMessage());
			}//catch
			finally {
				DBConnectionPool.returnDBConnection(dbConn, serialNumber);
			}//finally
		}//finally
		return false;
		//the default if this server does not exist is to not replicate to it.
	}

}
