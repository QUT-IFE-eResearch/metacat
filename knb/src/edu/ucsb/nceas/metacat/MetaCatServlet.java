/**
 *  '$RCSfile$'
 *    Purpose: A Class that implements a metadata catalog as a java Servlet
 *  Copyright: 2006 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Matt Jones, Dan Higgins, Jivka Bojilova, Chad Berkley, Matthew Perry
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

package edu.ucsb.nceas.metacat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ecoinformatics.eml.EMLParser;

import au.com.bytecode.opencsv.CSVWriter;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

import edu.ucsb.nceas.metacat.accesscontrol.AccessControlException;
import edu.ucsb.nceas.metacat.accesscontrol.AccessControlForSingleFile;
import edu.ucsb.nceas.metacat.accesscontrol.AccessControlInterface;
import edu.ucsb.nceas.metacat.accesscontrol.AccessControlList;
import edu.ucsb.nceas.metacat.cart.CartManager;
import edu.ucsb.nceas.metacat.database.DBConnection;
import edu.ucsb.nceas.metacat.database.DBConnectionPool;
import edu.ucsb.nceas.metacat.database.DatabaseService;
import edu.ucsb.nceas.metacat.dataquery.DataQuery;
import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.properties.SkinPropertyService;
import edu.ucsb.nceas.metacat.replication.ForceReplicationHandler;
import edu.ucsb.nceas.metacat.replication.ReplicationService;
import edu.ucsb.nceas.metacat.service.ServiceService;
import edu.ucsb.nceas.metacat.service.SessionService;
import edu.ucsb.nceas.metacat.service.XMLSchemaService;
import edu.ucsb.nceas.metacat.shared.ServiceException;
import edu.ucsb.nceas.metacat.shared.BaseException;
import edu.ucsb.nceas.metacat.shared.HandlerException;
import edu.ucsb.nceas.metacat.shared.MetacatUtilException;
import edu.ucsb.nceas.metacat.spatial.SpatialHarvester;
import edu.ucsb.nceas.metacat.spatial.SpatialQuery;
import edu.ucsb.nceas.metacat.util.AuthUtil;
import edu.ucsb.nceas.metacat.util.ConfigurationUtil;
import edu.ucsb.nceas.metacat.util.ErrorSendingErrorException;
import edu.ucsb.nceas.metacat.util.DocumentUtil;
import edu.ucsb.nceas.metacat.util.MetacatUtil;
import edu.ucsb.nceas.metacat.util.RequestUtil;
import edu.ucsb.nceas.metacat.util.ResponseUtil;
import edu.ucsb.nceas.metacat.util.SystemUtil;
import edu.ucsb.nceas.metacat.util.SessionData;
import edu.ucsb.nceas.metacat.workflow.WorkflowSchedulerClient;
import edu.ucsb.nceas.utilities.FileUtil;
import edu.ucsb.nceas.utilities.GeneralPropertyException;
import edu.ucsb.nceas.utilities.LSIDUtil;
import edu.ucsb.nceas.utilities.ParseLSIDException;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;
import edu.ucsb.nceas.utilities.UtilException;

/**
 * A metadata catalog server implemented as a Java Servlet
 *
 * Valid actions are:
 * 
 * action=login
 *     username
 *     password
 *     qformat
 * action=logout
 *     qformat
 * action=query -- query the values of all elements and attributes and return a result set of nodes
 *     meta_file_id --
 *     returndoctype --
 *     filterdoctype --
 *     returnfield --
 *     owner --
 *     site --
 *     operator --
 *     casesensitive --
 *     searchmode --
 *     anyfield --
 * action=spatial_query -- run a spatial query.  these queries may include any of the
 *                         queries supported by the WFS / WMS standards
 *     xmax --
 *     ymax --
 *     xmin --
 *     ymin --
 *     skin --
 *     pagesize --
 *     pagestart --
 * action=squery -- structured query (see pathquery.dtd)
 *     query --
 *     pagesize --
 *     pagestart --
 * action=export -- export a zip format for data packadge
 *     docid -- 
 * action=read -- read any metadata/data file from Metacat and from Internet
 *     archiveEntryName --
 *     docid --
 *     qformat --
 *     metadatadocid --
 * action=readinlinedata -- read inline data only
 *     inlinedataid
 * action=insert -- insert an XML document into the database store
 *     qformat -- 
 *     docid --
 *     doctext --
 *     dtdtext --
 * action=insertmultipart -- insert an xml document into the database using multipart encoding
 *     qformat -- 
 *     docid --
 * action=update -- update an XML document that is in the database store
 *     qformat -- 
 *     docid --
 *     doctext --
 *     dtdtext --
 * action=delete -- delete an XML document from the database store
 *     docid --
 * action=validate -- validate the xml contained in valtext
 *     valtext --
 *     docid --
 * action=setaccess -- change access permissions for a user on a document.
 *     docid --
 *     principal --
 *     permission --
 *     permType --
 *     permOrder --
 * action=getaccesscontrol -- retrieve acl info for Metacat document
 *     docid -- 
 * action=getprincipals -- retrieve a list of principals in XML
 * action=getalldocids -- retrieves a list of all docids registered with the system
 *     scope --
 * action=getlastdocid --
 *     scope --
 *     username --
 * action=isregistered -- checks to see if the provided docid is registered
 *     docid --
 * action=getrevisionanddoctype -- get a document's revision and doctype from database 
 *     docid --
 * action=getversion -- 
 * action=getdoctypes -- retrieve all doctypes (publicID) 
 * action=getdtdschema -- retrieve a DTD or Schema file
 *     doctype --
 * action=getlog -- get a report of events that have occurred in the system
 *     ipAddress --  filter on one or more IP addresses>
 *     principal -- filter on one or more principals (LDAP DN syntax)
 *     docid -- filter on one or more document identifiers (with revision)
 *     event -- filter on event type (e.g., read, insert, update, delete)
 *     start -- filter out events before the start date-time
 *     end -- filter out events before the end date-time
 * action=getloggedinuserinfo -- get user info for the currently logged in user
 *     ipAddress --  filter on one or more IP addresses>
 *     principal -- filter on one or more principals (LDAP DN syntax)
 *     docid -- filter on one or more document identifiers (with revision)
 *     event -- filter on event type (e.g., read, insert, update, delete)
 *     start -- filter out events before the start date-time
 *     end -- filter out events before the end date-time
 * action=shrink -- Shrink the database connection pool size if it has grown and 
 *                  extra connections are no longer being used.
 * action=buildindex --
 *     docid --
 * action=refreshServices --
 * action=scheduleWorkflow -- Schedule a workflow to be run.  Scheduling a workflow 
 *                            registers it with the scheduling engine and creates a row
 *                            in the scheduled_job table.  Note that this may be 
 *                            extracted into a separate servlet.
 *     delay -- The amount of time from now before the workflow should be run.  The 
 *              delay can be expressed in number of seconds, minutes, hours and days, 
 *              for instance 30s, 2h, etc.
 *     starttime -- The time that the workflow should first run.  If both are provided
 *                  this takes precedence over delay.  The time should be expressed as: 
 *                  MM/dd/yyyy HH:mm:ss with the timezone assumed to be that of the OS.
 *     endtime -- The time when the workflow should end. The time should be expressed as: 
 *                  MM/dd/yyyy HH:mm:ss with the timezone assumed to be that of the OS.
 *     intervalvalue -- The numeric value of the interval between runs
 *     intervalunit -- The unit of the interval between runs.  Can be s, m, h, d for 
 *                     seconds, minutes, hours and days respectively
 *     workflowid -- The lsid of the workflow that we want to schedule.  This workflow
 *                   must already exist in the database.
 *     karid -- The karid for the workflow that we want to schedule.
 *     workflowname -- The name of the workflow.
 *     forwardto -- If provided, forward to this page when processing is done.
 *     qformat -- If provided, render results using the stylesheets associated with
 *                this skin.  Default is xml.
 * action=unscheduleWorkflow -- Unschedule a workflow.  Unscheduling a workflow 
 *                            removes it from the scheduling engine and changes the 
 *                            status in the scheduled_job table to " unscheduled.  Note 
 *                            that this may be extracted into a separate servlet.
 *     workflowjobname -- The job ID for the workflow run that we want to unschedule.  This
 *                      is held in the database as scheduled_job.name
 *     forwardto -- If provided, forward to this page when processing is done.
 *     qformat -- If provided, render results using the stylesheets associated with
 *                this skin.  Default is xml.
 * action=rescheduleWorkflow -- Unschedule a workflow.  Rescheduling a workflow 
 *                            registers it with the scheduling engine and changes the 
 *                            status in the scheduled_job table to " scheduled.  Note 
 *                            that this may be extracted into a separate servlet.
 *     workflowjobname -- The job ID for the workflow run that we want to reschedule.  This
 *                      is held in the database as scheduled_job.name
 *     forwardto -- If provided, forward to this page when processing is done.
 *     qformat -- If provided, render results using the stylesheets associated with
 *                this skin.  Default is xml.
 * action=deleteScheduledWorkflow -- Delete a workflow.  Deleting a workflow 
 *                            removes it from the scheduling engine and changes the 
 *                            status in the scheduled_job table to " deleted.  Note 
 *                            that this may be extracted into a separate servlet.
 *     workflowjobname -- The job ID for the workflow run that we want to delete.  This
 *                      is held in the database as scheduled_job.name
 *     forwardto -- If provided, forward to this page when processing is done.
 *     qformat -- If provided, render results using the stylesheets associated with
 *                this skin.  Default is xml.
 *     
 *     
 * Here are some of the common parameters for actions
 *     doctype -- document type list returned by the query (publicID) 
 *     qformat=xml -- display resultset from query in XML 
 *     qformat=html -- display resultset from query in HTML 
 *     qformat=zip -- zip resultset from query
 *     docid=34 -- display the document with the document ID number 34 
 *     doctext -- XML text of the document to load into the database 
 *     acltext -- XML access text for a document to load into the database 
 *     dtdtext -- XML DTD text for a new DTD to load into Metacat XML Catalog 
 *     query -- actual query text (to go with 'action=query' or 'action=squery')
 *     valtext -- XML text to be validated 
 *     scope --can limit the query by the scope of the id
 *     docid --the docid to check
 *     datadoc -- data document name (id)
 */
public class MetaCatServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Timer timer = null;
    private static boolean _sitemapScheduled;
    private static boolean _firstHalfInitialized = false;
    private static boolean _fullyInitialized = false;
    
    // Constants -- these should be final in a servlet
    private static final String PROLOG = "<?xml version=\"1.0\"?>";
    private static final String SUCCESS = "<success>";
    private static final String SUCCESSCLOSE = "</success>";
    private static final String ERROR = "<error>";
    private static final String ERRORCLOSE = "</error>";
    public static final String SCHEMALOCATIONKEYWORD = ":schemaLocation";
    public static final String NONAMESPACELOCATION = ":noNamespaceSchemaLocation";
    public static final String EML2KEYWORD = ":eml";
    private static final String FALSE = "false";
    private static final String TRUE  = "true";
    private static String LOG_CONFIG_NAME = null;
    public static final String APPLICATION_NAME = "metacat";
    
    /**
     * Initialize the servlet by creating appropriate database connections
     */
    public void init(ServletConfig config) throws ServletException {
    	Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
    	try {
    		if(_firstHalfInitialized) {
    			return;
    		}
    		
            super.init(config);
            
            ServletContext context = config.getServletContext();
            context.setAttribute("APPLICATION_NAME", APPLICATION_NAME);
            
            ServiceService serviceService = ServiceService.getInstance(context);
            logMetacat.debug("MetaCatServlet.init - ServiceService singleton created " + serviceService);
            
            // Initialize the properties file
            String dirPath = ServiceService.getRealConfigDir();
            
            LOG_CONFIG_NAME = dirPath + "/log4j.properties";
            PropertyConfigurator.configureAndWatch(LOG_CONFIG_NAME);
            
            // Register preliminary services
            ServiceService.registerService("PropertyService", PropertyService.getInstance(context));         
            ServiceService.registerService("SkinPropertyService", SkinPropertyService.getInstance());
            ServiceService.registerService("SessionService", SessionService.getInstance()); 
            
    		// Check to see if the user has requested to bypass configuration 
            // (dev option) and check see if metacat has been configured.
    		// If both are false then stop the initialization
            if (!ConfigurationUtil.bypassConfiguration() && !ConfigurationUtil.isMetacatConfigured()) {
            	return;
            }  
            
            _firstHalfInitialized = true;
            
            initSecondHalf(context);
            
    	} catch (ServiceException se) {
        	String errorMessage = 
        		"Service problem while intializing MetaCat Servlet: " + se.getMessage();
            logMetacat.error("MetaCatServlet.init - " + errorMessage);
            se.printStackTrace(System.out);
            throw new ServletException(errorMessage);
        } catch (MetacatUtilException mue) {
        	String errorMessage = "Metacat utility problem while intializing MetaCat Servlet: " 
        		+ mue.getMessage();
            logMetacat.error("MetaCatServlet.init - " + errorMessage);
            mue.printStackTrace(System.out);
            throw new ServletException(errorMessage);
        } 
    }

            
	/**
	 * Initialize the remainder of the servlet. This is the part that can only
	 * be initialized after metacat properties have been configured
	 * 
	 * @param context
	 *            the servlet context of MetaCatServlet
	 */
	public void initSecondHalf(ServletContext context) throws ServletException {
		
		Logger logMetacat = Logger.getLogger(MetaCatServlet.class);

		try {			
			ServiceService.registerService("DatabaseService", DatabaseService.getInstance());
			
			// initialize DBConnection pool
			DBConnectionPool connPool = DBConnectionPool.getInstance();
			logMetacat.debug("MetaCatServlet.initSecondHalf - DBConnection pool initialized: " + connPool.toString());
			
			ServiceService.registerService("XMLSchemaService", XMLSchemaService.getInstance());
//			ServiceService.registerService("SchedulerService", SchedulerService.getInstance());

			// check if eml201 document were corrected or not. if not, correct eml201 documents.
			// Before Metacat 1.8.1, metacat uses tag RELEASE_EML_2_0_1_UPDATE_6 as eml
			// schema, which accidentily points to wrong version of eml-resource.xsd.
			String correctedEML201Doc = PropertyService.getProperty("document.eml201DocumentCorrected");
			if (correctedEML201Doc != null && correctedEML201Doc.equals(FALSE)) {
				logMetacat.info("MetaCatServlet.initSecondHalf - Start to correct eml201 documents");
				EML201DocumentCorrector correct = new EML201DocumentCorrector();
				boolean success = correct.run();
				if (success) {
					PropertyService.setProperty("document.eml201DocumentCorrected", TRUE);
				}
				logMetacat.info("MetaCatServlet.initSecondHalf - Finish to correct eml201 documents");
			}

			// Index the paths specified in the metacat.properties
			checkIndexPaths();

			// initiate the indexing Queue
			IndexingQueue.getInstance();

			// start the IndexingThread if indexingTimerTaskTime more than 0.
			// It will index all the documents not yet indexed in the database
			int indexingTimerTaskTime = Integer.parseInt(PropertyService
					.getProperty("database.indexingTimerTaskTime"));
			int delayTime = Integer.parseInt(PropertyService
					.getProperty("database.indexingInitialDelay"));

			if (indexingTimerTaskTime > 0) {
				timer = new Timer();
				timer.schedule(new IndexingTimerTask(), delayTime, indexingTimerTaskTime);
			}
			
			/*
			 * If spatial option is turned on and set to regenerate the spatial
			 * cache on restart, trigger the harvester regeneratation method
			 */
			if (PropertyService.getProperty("spatial.runSpatialOption").equals("true") && 
					PropertyService.getProperty("spatial.regenerateCacheOnRestart").equals("true")) {

				// Begin timer
				long before = System.currentTimeMillis();

				// if either the point or polygon shape files do not exist, then regenerate the entire spatial cache
				// this may be expensive with many documents		
				SpatialHarvester sh = new SpatialHarvester();
				sh.regenerate();
				sh.destroy();

				// After running the first time, we want to to set
				// regenerateCacheOnRestart to false
				// so that it does not regenerate the cache every time tomcat is
				// restarted
				PropertyService.setProperty("spatial.regenerateCacheOnRestart", "false");

				// End timer
				long after = System.currentTimeMillis();
				logMetacat.info("MetaCatServlet.initSecondHalf - Spatial Harvester Time  " 
						+ (after - before) + "ms");

			} else {
				logMetacat.info("MetaCatServlet.initSecondHalf - Spatial cache is not set to regenerate on restart");
			}
		
			// Set up the replication log file by setting the "replication.logfile.name" 
			// system property and reconfiguring the log4j property configurator.
			String replicationLogPath = PropertyService.getProperty("replication.logdir") 
				+ FileUtil.getFS() + ReplicationService.REPLICATION_LOG_FILE_NAME;				
			
			if (FileUtil.getFileStatus(replicationLogPath) == FileUtil.DOES_NOT_EXIST) {
				FileUtil.createFile(replicationLogPath);
			}

			if (FileUtil.getFileStatus(replicationLogPath) < FileUtil.EXISTS_READ_WRITABLE) {
				logMetacat.error("MetaCatServlet.initSecondHalf - Replication log file: " + replicationLogPath 
						+ " does not exist read/writable.");
			}
			
			System.setProperty("replication.logfile.name", replicationLogPath);			
			PropertyConfigurator.configureAndWatch(LOG_CONFIG_NAME);
			
			SessionService.unRegisterAllSessions();
			
			_sitemapScheduled = false;

			_fullyInitialized = true;
			
			logMetacat.warn("MetaCatServlet.initSecondHalf - Metacat (" + MetacatVersion.getVersionID()
					+ ") initialized.");
			
		} catch (SQLException sqle) {
			String errorMessage = "SQL problem while intializing MetaCat Servlet: "
					+ sqle.getMessage();
			logMetacat.error("MetaCatServlet.initSecondHalf - " + errorMessage);
			sqle.printStackTrace(System.out);
			throw new ServletException(errorMessage);
		} catch (IOException ie) {
			String errorMessage = "IO problem while intializing MetaCat Servlet: "
					+ ie.getMessage();
			logMetacat.error("MetaCatServlet.initSecondHalf - " + errorMessage);
			ie.printStackTrace(System.out);
			throw new ServletException(errorMessage);
		} catch (GeneralPropertyException gpe) {
			String errorMessage = "Could not retrieve property while intializing MetaCat Servlet: "
					+ gpe.getMessage();
			logMetacat.error("MetaCatServlet.initSecondHalf - " + errorMessage);
			gpe.printStackTrace(System.out);
			throw new ServletException(errorMessage);
		} catch (ServiceException se) {
			String errorMessage = "Service problem while intializing MetaCat Servlet: "
				+ se.getMessage();
			logMetacat.error("MetaCatServlet.initSecondHalf - " + errorMessage);
			se.printStackTrace(System.out);
			throw new ServletException(errorMessage);
		} catch (UtilException ue) {
        	String errorMessage = "Utility problem while intializing MetaCat Servlet: " 
        		+ ue.getMessage();
            logMetacat.error("MetaCatServlet.initSecondHalf - " + errorMessage);
            ue.printStackTrace(System.out);
            throw new ServletException(errorMessage);
        } 
	}
    
    /**
	 * Close all db connections from the pool
	 */
    public void destroy() {
    	Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
    	
    	ServiceService.stopAllServices();
    	
        // Close all db connection
        logMetacat.warn("MetaCatServlet.destroy - Destroying MetacatServlet");
        timer.cancel();
        IndexingQueue.getInstance().setMetacatRunning(false);
        DBConnectionPool.release();
    }
    
    /** Handle "GET" method requests from HTTP clients */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        // Process the data and send back the response
        handleGetOrPost(request, response);
    }
    
    /** Handle "POST" method requests from HTTP clients */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        // Process the data and send back the response
        handleGetOrPost(request, response);
    }
    
    /**
	 * Index the paths specified in the metacat.properties
	 */
    private void checkIndexPaths() {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);

        Vector<String> pathsForIndexing = null;
        try {  
        	pathsForIndexing = SystemUtil.getPathsForIndexing();
        }
        catch (MetacatUtilException ue) {
        	pathsForIndexing = null;
            logMetacat.error("MetaCatServlet.checkIndexPaths - not find index paths.  Setting " 
            		+ "pathsForIndexing to null: " + ue.getMessage());
        }
        
        if (pathsForIndexing != null && !pathsForIndexing.isEmpty()) {
            
            logMetacat.debug("MetaCatServlet.checkIndexPaths - Indexing paths specified in metacat.properties....");
            
            DBConnection conn = null;
            int serialNumber = -1;
            PreparedStatement pstmt = null;
            PreparedStatement pstmt1 = null;
            ResultSet rs = null;
            
            for (String pathIndex : pathsForIndexing) {
                logMetacat.debug("MetaCatServlet.checkIndexPaths - Checking if '" + pathIndex  + "' is indexed.... ");
                
                try {
                    //check out DBConnection
                    conn = DBConnectionPool.
                            getDBConnection("MetaCatServlet.checkIndexPaths");
                    serialNumber = conn.getCheckOutSerialNumber();
                    
                    pstmt = conn.prepareStatement(
                            "SELECT * FROM xml_path_index " + "WHERE path = ?");
                    pstmt.setString(1, pathIndex);
                    
                    pstmt.execute();
                    rs = pstmt.getResultSet();
                    
                    if (!rs.next()) {
                        logMetacat.debug("MetaCatServlet.checkIndexPaths - not indexed yet.");
                        rs.close();
                        pstmt.close();
                        conn.increaseUsageCount(1);
                        
                        logMetacat.debug("MetaCatServlet.checkIndexPaths - Inserting following path in xml_path_index: "
                                + pathIndex);
                        if(pathIndex.indexOf("@")<0){
                            pstmt = conn.prepareStatement("SELECT DISTINCT n.docid, "
                                    + "n.nodedata, n.nodedatanumerical, n.parentnodeid"
                                    + " FROM xml_nodes n, xml_index i WHERE"
                                    + " i.path = ? and n.parentnodeid=i.nodeid and"
                                    + " n.nodetype LIKE 'TEXT' order by n.parentnodeid");
                        } else {
                            pstmt = conn.prepareStatement("SELECT DISTINCT n.docid, "
                                    + "n.nodedata, n.nodedatanumerical, n.parentnodeid"
                                    + " FROM xml_nodes n, xml_index i WHERE"
                                    + " i.path = ? and n.nodeid=i.nodeid and"
                                    + " n.nodetype LIKE 'ATTRIBUTE' order by n.parentnodeid");
                        }
                        pstmt.setString(1, pathIndex);
                        pstmt.execute();
                        rs = pstmt.getResultSet();
                        
                        int count = 0;
                        logMetacat.debug("MetaCatServlet.checkIndexPaths - Executed the select statement for: "
                                + pathIndex);
                        
                        try {
                            while (rs.next()) {
                                
                                String docid = rs.getString(1);
                                String nodedata = rs.getString(2);
                                float nodedatanumerical = rs.getFloat(3);
                                int parentnodeid = rs.getInt(4);
                                
                                if (!nodedata.trim().equals("")) {
                                    pstmt1 = conn.prepareStatement(
                                            "INSERT INTO xml_path_index"
                                            + " (docid, path, nodedata, "
                                            + "nodedatanumerical, parentnodeid)"
                                            + " VALUES (?, ?, ?, ?, ?)");
                                    
                                    pstmt1.setString(1, docid);
                                    pstmt1.setString(2, pathIndex);
                                    pstmt1.setString(3, nodedata);
                                    pstmt1.setFloat(4, nodedatanumerical);
                                    pstmt1.setInt(5, parentnodeid);
                                    
                                    pstmt1.execute();
                                    pstmt1.close();
                                    
                                    count++;
                                    
                                }
                            }
                        } catch (Exception e) {
                            logMetacat.error("MetaCatServlet.checkIndexPaths - Exception:" + e.getMessage());
                            e.printStackTrace(System.out);
                        }
                        
                        rs.close();
                        pstmt.close();
                        conn.increaseUsageCount(1);
                        
                        logMetacat.info("MetaCatServlet.checkIndexPaths - Indexed " + count + " records from xml_nodes for '"
                                + pathIndex + "'");
                        
                    } else {
                        logMetacat.debug("MetaCatServlet.checkIndexPaths - already indexed.");
                    }
                    
                    rs.close();
                    pstmt.close();
                    conn.increaseUsageCount(1);
                    
                } catch (Exception e) {
                    logMetacat.error("MetaCatServlet.checkIndexPaths - Error in MetaCatServlet.checkIndexPaths: "
                            + e.getMessage());
                    e.printStackTrace(System.out);
                }finally {
                    //check in DBonnection
                    DBConnectionPool.returnDBConnection(conn, serialNumber);
                }
                
                
            }
            
            logMetacat.debug("MetaCatServlet.checkIndexPaths - Path Indexing Completed");
        }
    }
    
    /**
	 * Control servlet response depending on the action parameter specified
	 */
	@SuppressWarnings("unchecked")
	private void handleGetOrPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Logger logMetacat = Logger.getLogger(MetaCatServlet.class);

		// Update the last update time for this user if they are not new
		HttpSession httpSession = request.getSession(false);
		if (httpSession != null) {
			SessionService.touchSession(httpSession.getId());
		}
		
		// Each time metacat is called, check to see if metacat has been 
		// configured. If not then forward to the administration servlet
		if (!ConfigurationUtil.isMetacatConfigured()) {
			try {
				RequestUtil.forwardRequest(request, response, "/admin?action=configure", null);
				return;
			} catch (MetacatUtilException mue) {
				logMetacat.error("MetacatServlet.handleGetOrPost - utility error when forwarding to " + 
						"configuration screen: " + mue.getMessage());
				mue.printStackTrace(System.out);
				throw new ServletException("MetacatServlet.handleGetOrPost - utility error when forwarding to " + 
						"configuration screen: " + mue.getMessage());
			}
		}

		// if we get here, metacat is configured.  If we have not completed the 
		// second half of the initialization, do so now.  This allows us to initially
		// configure metacat without a restart.
		if (!_fullyInitialized) {
			initSecondHalf(request.getSession().getServletContext());
		}
		
		/*
		 * logMetacat.debug("Connection pool size: "
		 * +connPool.getSizeOfDBConnectionPool(),10); logMetacat.debug("Free
		 * DBConnection number: "
		 */
		// If all DBConnection in the pool are free and DBConnection pool
		// size is greater than initial value, shrink the connection pool
		// size to initial value
		DBConnectionPool.shrinkDBConnectionPoolSize();

		// Debug message to print out the method which have a busy DBConnection
		try {
			@SuppressWarnings("unused")
			DBConnectionPool pool = DBConnectionPool.getInstance();
//			pool.printMethodNameHavingBusyDBConnection();
		} catch (SQLException e) {
			logMetacat.error("MetaCatServlet.handleGetOrPost - Error in MetacatServlet.handleGetOrPost: " + e.getMessage());
			e.printStackTrace(System.out);
		}

		try {
			String ctype = request.getContentType();
			if (ctype != null && ctype.startsWith("multipart/form-data")) {
				handleMultipartForm(request, response);
				return;
			} 

			String name = null;
			String[] value = null;
			String[] docid = new String[3];
			Hashtable<String, String[]> params = new Hashtable<String, String[]>();

			// Check if this is a simple read request that doesn't use the
			// "action" syntax
			// These URLs are of the form:
			// http://localhost:8180/knb/metacat/docid/skinname
			// e.g., http://localhost:8180/knb/metacat/test.1.1/knb
			String pathInfo = request.getPathInfo();
			if (pathInfo != null) {
				String[] path = pathInfo.split("/");
				if (path.length > 1) {
					String docidToRead = path[1];
					String docs[] = new String[1];
					docs[0] = docidToRead;
					logMetacat.debug("MetaCatServlet.handleGetOrPost - READING DOCID FROM PATHINFO: " + docs[0]);
					params.put("docid", docs);
					String skin = null;
					if (path.length > 2) {
						skin = path[2];
						String skins[] = new String[1];
						skins[0] = skin;
						params.put("qformat", skins);
					}
					handleReadAction(params, request, response, "public", null, null);
					return;
				}
			}

			Enumeration<String> paramlist = 
				(Enumeration<String>) request.getParameterNames();
			while (paramlist.hasMoreElements()) {

				name = paramlist.nextElement();
				value = request.getParameterValues(name);

				// Decode the docid and mouse click information
				// THIS IS OBSOLETE -- I THINK -- REMOVE THIS BLOCK
				// 4/12/2007d
				// MBJ
				if (name.endsWith(".y")) {
					docid[0] = name.substring(0, name.length() - 2);
					params.put("docid", docid);
					name = "ypos";
				}
				if (name.endsWith(".x")) {
					name = "xpos";
				}

				params.put(name, value);
			}

			// handle param is emptpy
			if (params.isEmpty() || params == null) {
				return;
			}

			// if the user clicked on the input images, decode which image
			// was clicked then set the action.
			if (params.get("action") == null) {
				PrintWriter out = response.getWriter();
				response.setContentType("text/xml");
				out.println("<?xml version=\"1.0\"?>");
				out.println("<error>");
				out.println("Action not specified");
				out.println("</error>");
				out.close();
				return;
			}

			String action = (params.get("action"))[0];
			logMetacat.info("MetaCatServlet.handleGetOrPost - Action is: " + action);

			// This block handles session management for the servlet
			// by looking up the current session information for all actions
			// other than "login" and "logout"
			String userName = null;
			String password = null;
			String[] groupNames = null;
			String sessionId = null;
			name = null;

			// handle login action
			if (action.equals("login")) {
				PrintWriter out = response.getWriter();
				handleLoginAction(out, params, request, response);
				out.close();

				// handle logout action
			} else if (action.equals("logout")) {
				PrintWriter out = response.getWriter();
				handleLogoutAction(out, params, request, response);
				out.close();

				// handle shrink DBConnection request
			} else if (action.equals("validatesession")) {
				PrintWriter out = response.getWriter();
				String idToValidate = null;
				String idsToValidate[] = params.get("sessionid");
				if (idsToValidate != null) {
					idToValidate = idsToValidate[0];
				}
				SessionService.validateSession(out, response, idToValidate);
				out.close();

				// handle shrink DBConnection request
			} else if (action.equals("shrink")) {
				PrintWriter out = response.getWriter();
				boolean success = false;
				// If all DBConnection in the pool are free and DBConnection
				// pool
				// size is greater than initial value, shrink the connection
				// pool
				// size to initial value
				success = DBConnectionPool.shrinkConnectionPoolSize();
				if (success) {
					// if successfully shrink the pool size to initial value
					out.println("DBConnection Pool shrunk successfully.");
				}// if
				else {
					out.println("DBConnection pool not shrunk successfully.");
				}
				// close out put
				out.close();

				// aware of session expiration on every request
			} else {
				SessionData sessionData = RequestUtil.getSessionData(request);
				
				if (sessionData != null) {
					userName = sessionData.getUserName();
					password = sessionData.getPassword();
					groupNames = sessionData.getGroupNames();
					sessionId = sessionData.getId();
				}

				logMetacat.info("MetaCatServlet.handleGetOrPost - The user is : " + userName);
			}
			// Now that we know the session is valid, we can delegate the
			// request to a particular action handler
			if (action.equals("query")) {
				ServletOutputStream streamOut = response.getOutputStream();
				PrintWriter out = new PrintWriter(streamOut);
				handleQuery(out, params, response, userName, groupNames, sessionId);
				out.close();
			} else if (action.equals("squery")) {
				ServletOutputStream streamOut = response.getOutputStream();
				PrintWriter out = new PrintWriter(streamOut);
				if (params.containsKey("query")) {
					handleSQuery(out, params, response, userName, groupNames, sessionId);
					out.close();
				} else {
					out.println("Illegal action squery without \"query\" parameter");
					out.close();
				}
			} else if (action.trim().equals("spatial_query")) {

				logMetacat
						.debug("MetaCatServlet.handleGetOrPost - ******************* SPATIAL QUERY ********************");
				ServletOutputStream streamOut = response.getOutputStream();
				PrintWriter out = new PrintWriter(streamOut);
				handleSpatialQuery(out, params, response, userName, groupNames, sessionId);
				out.close();

			} else if (action.trim().equals("dataquery")) {

				logMetacat.debug("MetaCatServlet.handleGetOrPost - ******************* DATA QUERY ********************");
				handleDataquery(params, response, sessionId);
			} else if (action.trim().equals("editcart")) {
				logMetacat.debug("MetaCatServlet.handleGetOrPost - ******************* EDIT CART ********************");
				handleEditCart(params, response, sessionId);
			} else if (action.equals("export")) {

				handleExportAction(params, response, userName, groupNames, password);
			} else if (action.equals("read")) {
				if (params.get("archiveEntryName") != null) {
					ArchiveHandler.getInstance().readArchiveEntry(params, request,
							response, userName, password, groupNames);
				} else {
					handleReadAction(params, request, response, userName, password,
							groupNames);
				}
			} else if (action.equals("readinlinedata")) {
				handleReadInlineDataAction(params, request, response, userName, password,
						groupNames);
			} else if (action.equals("insert") || action.equals("update")) {
				PrintWriter out = response.getWriter();
				if ((userName != null) && !userName.equals("public")) {
					handleInsertOrUpdateAction(request, response, out, params, userName,
							groupNames);
				} else {
					response.setContentType("text/xml");
					out.println("<?xml version=\"1.0\"?>");
					out.println("<error>");
					out.println("Permission denied for user " + userName + " " + action);
					out.println("</error>");
				}
				out.close();
			} else if (action.equals("delete")) {
				PrintWriter out = response.getWriter();
				if ((userName != null) && !userName.equals("public")) {
					handleDeleteAction(out, params, request, response, userName,
							groupNames);
				} else {
					response.setContentType("text/xml");
					out.println("<?xml version=\"1.0\"?>");
					out.println("<error>");
					out.println("Permission denied for " + action);
					out.println("</error>");
				}
				out.close();
			} else if (action.equals("validate")) {
				PrintWriter out = response.getWriter();
				handleValidateAction(out, params);
				out.close();
			} else if (action.equals("setaccess")) {
				PrintWriter out = response.getWriter();
				handleSetAccessAction(out, params, userName, request, response);
				out.close();
			} else if (action.equals("getaccesscontrol")) {
				PrintWriter out = response.getWriter();
				handleGetAccessControlAction(out, params, response, userName, groupNames);
				out.close();
			} else if (action.equals("isauthorized")) {
				PrintWriter out = response.getWriter();
				DocumentUtil.isAuthorized(out, params, request, response);
				out.close();
			} else if (action.equals("getprincipals")) {
				PrintWriter out = response.getWriter();
				handleGetPrincipalsAction(out, userName, password);
				out.close();
			} else if (action.equals("getdoctypes")) {
				PrintWriter out = response.getWriter();
				handleGetDoctypesAction(out, params, response);
				out.close();
			} else if (action.equals("getdtdschema")) {
				PrintWriter out = response.getWriter();
				handleGetDTDSchemaAction(out, params, response);
				out.close();
			} else if (action.equals("getlastdocid")) {
				PrintWriter out = response.getWriter();
				handleGetMaxDocidAction(out, params, response);
				out.close();
			} else if (action.equals("getalldocids")) {
				PrintWriter out = response.getWriter();
				handleGetAllDocidsAction(out, params, response);
				out.close();
			} else if (action.equals("isregistered")) {
				PrintWriter out = response.getWriter();
				handleIdIsRegisteredAction(out, params, response);
				out.close();
			} else if (action.equals("getrevisionanddoctype")) {
				PrintWriter out = response.getWriter();
				handleGetRevisionAndDocTypeAction(out, params);
				out.close();
			} else if (action.equals("getversion")) {
				response.setContentType("text/xml");
				PrintWriter out = response.getWriter();
				out.println(MetacatVersion.getVersionAsXml());
				out.close();
			} else if (action.equals("getlog")) {
				handleGetLogAction(params, request, response, userName, groupNames);
			} else if (action.equals("getloggedinuserinfo")) {
				PrintWriter out = response.getWriter();
				response.setContentType("text/xml");
				out.println("<?xml version=\"1.0\"?>");
				out.println("\n<user>\n");
				out.println("\n<username>\n");
				out.println(userName);
				out.println("\n</username>\n");
				if (name != null) {
					out.println("\n<name>\n");
					out.println(name);
					out.println("\n</name>\n");
				}
				if (groupNames != null && groupNames.length > 0) {
					String groupNameStr = "";
					for (int i = 0; i < groupNames.length; i++) {
						groupNameStr += groupNames[i];
						if (i < groupNames.length - 1) {
							groupNameStr += ":";
						}
					}
					out.println("\n<groupNames>\n");
					out.println(groupNameStr);
					out.println("\n</groupNames>\n");
				}
				if (AuthUtil.isAdministrator(userName, groupNames)) {
					out.println("<isAdministrator></isAdministrator>\n");
				}
				if (AuthUtil.isModerator(userName, groupNames)) {
					out.println("<isModerator></isModerator>\n");
				}
				out.println("\n</user>\n");
				out.close();
			} else if (action.equals("buildindex")) {
				handleBuildIndexAction(params, request, response, userName, groupNames);
			} else if (action.equals("login") || action.equals("logout")) {
				/*
				 * } else if (action.equals("protocoltest")) { String testURL =
				 * "metacat://dev.nceas.ucsb.edu/NCEAS.897766.9"; try { testURL =
				 * ((String[]) params.get("url"))[0]; } catch (Throwable t) { }
				 * String phandler = System
				 * .getProperty("java.protocol.handler.pkgs");
				 * response.setContentType("text/html"); PrintWriter out =
				 * response.getWriter(); out.println("<body
				 * bgcolor=\"white\">"); out.println("<p>Handler property:
				 * <code>" + phandler + "</code></p>"); out.println("<p>Starting
				 * test for:<br>"); out.println(" " + testURL + "</p>"); try {
				 * URL u = new URL(testURL); out.println("<pre>");
				 * out.println("Protocol: " + u.getProtocol()); out.println("
				 * Host: " + u.getHost()); out.println(" Port: " + u.getPort());
				 * out.println(" Path: " + u.getPath()); out.println(" Ref: " +
				 * u.getRef()); String pquery = u.getQuery(); out.println("
				 * Query: " + pquery); out.println(" Params: "); if (pquery !=
				 * null) { Hashtable qparams =
				 * MetacatUtil.parseQuery(u.getQuery()); for (Enumeration en =
				 * qparams.keys(); en .hasMoreElements();) { String pname =
				 * (String) en.nextElement(); String pvalue = (String)
				 * qparams.get(pname); out.println(" " + pname + ": " + pvalue); } }
				 * out.println("</pre>"); out.println("</body>");
				 * out.close(); } catch (MalformedURLException mue) {
				 * System.out.println( "bad url from
				 * MetacatServlet.handleGetOrPost");
				 * out.println(mue.getMessage()); mue.printStackTrace(out);
				 * out.close(); }
				 */
			} else if (action.equals("refreshServices")) {
				// TODO MCD this interface is for testing. It should go through
				// a ServiceService class and only work for an admin user. Move 
				// to the MetacatAdminServlet
				ServiceService.refreshService("XMLSchemaService");
				return;
			} else if (action.equals("scheduleWorkflow")) {
				try {
					WorkflowSchedulerClient.getInstance().scheduleJob(request, response,
							params, userName, groupNames);
					return;
				} catch (BaseException be) {
					ResponseUtil.sendErrorXML(response,
							ResponseUtil.SCHEDULE_WORKFLOW_ERROR, be);
					return;
				}
			} else if (action.equals("unscheduleWorkflow")) {
				try {
					WorkflowSchedulerClient.getInstance().unScheduleJob(request,
							response, params, userName, groupNames);
					return;
				} catch (BaseException be) {
					ResponseUtil.sendErrorXML(response,
							ResponseUtil.UNSCHEDULE_WORKFLOW_ERROR, be);
					return;
				}
			} else if (action.equals("rescheduleWorkflow")) {
				try {
					WorkflowSchedulerClient.getInstance().reScheduleJob(request,
							response, params, userName, groupNames);
					return;
				} catch (BaseException be) {
					ResponseUtil.sendErrorXML(response,
							ResponseUtil.RESCHEDULE_WORKFLOW_ERROR, be);
					return;
				}
			} else if (action.equals("getScheduledWorkflow")) {
				try {
					WorkflowSchedulerClient.getInstance().getJobs(request, response,
							params, userName, groupNames);
					return;
				} catch (BaseException be) {
					ResponseUtil.sendErrorXML(response,
							ResponseUtil.GET_SCHEDULED_WORKFLOW_ERROR, be);
					return;
				}
			} else if (action.equals("deleteScheduledWorkflow")) {
				try {
					WorkflowSchedulerClient.getInstance().deleteJob(request, response,
							params, userName, groupNames);
					return;
				} catch (BaseException be) {
					ResponseUtil.sendErrorXML(response,
							ResponseUtil.DELETE_SCHEDULED_WORKFLOW_ERROR, be);
					return;
				}

			} else {
				PrintWriter out = response.getWriter();
				out.println("<?xml version=\"1.0\"?>");
				out.println("<error>");
				out.println("Error: action: " + action + " not registered.  Please report this error.");
				out.println("</error>");
				out.close();
			}

			// Schedule the sitemap generator to run periodically
			scheduleSitemapGeneration(request);


		} catch (PropertyNotFoundException pnfe) {
			String errorString = "Critical property not found: " + pnfe.getMessage();
			logMetacat.error("MetaCatServlet.handleGetOrPost - " + errorString);
			pnfe.printStackTrace(System.out);
			throw new ServletException(errorString);
		} catch (MetacatUtilException ue) {
			String errorString = "Utility error: " + ue.getMessage();
			logMetacat.error("MetaCatServlet.handleGetOrPost - " + errorString);
			ue.printStackTrace(System.out);
			throw new ServletException(errorString);
		} catch (ServiceException se) {
			String errorString = "Service error: " + se.getMessage();
			logMetacat.error("MetaCatServlet.handleGetOrPost - " + errorString);
			se.printStackTrace(System.out);
			throw new ServletException(errorString);
		} catch (HandlerException he) {
			String errorString = "Handler error: " + he.getMessage();
			logMetacat.error("MetaCatServlet.handleGetOrPost - " + errorString);
			he.printStackTrace(System.out);
			throw new ServletException(errorString);
		} catch (ErrorSendingErrorException esee) {
			String errorString = "Error sending error message: " + esee.getMessage();
			logMetacat.error("MetaCatServlet.handleGetOrPost - " + errorString);
			esee.printStackTrace(System.out);
			throw new ServletException(errorString);
		} catch (ErrorHandledException ehe) {
			// Nothing to do here.  We assume if we get here, the error has been 
			// written to ouput.  Continue on and let it display.
		} 
	}
    
	
	private void handleDataquery(
			Hashtable<String, String[]> params,
            HttpServletResponse response,
            String sessionId) throws PropertyNotFoundException, IOException {
		
		DataQuery dq = null;
		if (sessionId != null) {
			dq = new DataQuery(sessionId);
		}
		else {
			dq = new DataQuery();
		}
		
    	String dataqueryXML = (params.get("dataquery"))[0];

    	ResultSet rs = null;
		try {
			rs = dq.executeQuery(dataqueryXML);
		} catch (Exception e) {
			//probably need to do something here
			e.printStackTrace();
			return;
		}
    	
		//process the result set
		String qformat = "csv";
		String[] temp = params.get("qformat");
		if (temp != null && temp.length > 0) {
			qformat = temp[0];
		}
		String fileName = "query-results." + qformat;
		
		//get the results as csv file
		if (qformat != null && qformat.equalsIgnoreCase("csv")) {
			response.setContentType("text/csv");
			//response.setContentType("application/csv");
	        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
	        
			Writer writer = new OutputStreamWriter(response.getOutputStream());
			CSVWriter csv = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
			try {
				
				csv.writeAll(rs, true);
				
				csv.flush();
				response.flushBuffer();
				
				rs.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return;
		}
		
	}
	
	private void handleEditCart(
			Hashtable<String, String[]> params,
            HttpServletResponse response,
            String sessionId) throws PropertyNotFoundException, IOException {
		
		CartManager cm = null;
		if (sessionId != null) {
			cm = new CartManager(sessionId);
		}
		else {
			cm = new CartManager();
		}
		
    	String editOperation = (params.get("operation"))[0];
    	
    	String[] docids = params.get("docid");
    	String[] field = params.get("field");
    	String[] path = params.get("path");
    	
    	Map<String,String> fields = null;
    	if (field != null && path != null) {
    		fields = new HashMap<String,String>();
    		fields.put(field[0], path[0]);
    	}
    	
    	//TODO handle attribute map (metadata fields)
    	cm.editCart(editOperation, docids, fields);
    	
	}
	
    // ///////////////////////////// METACAT SPATIAL ///////////////////////////
    
    /**
	 * handles all spatial queries -- these queries may include any of the
	 * queries supported by the WFS / WMS standards
	 * 
	 * handleSQuery(out, params, response, username, groupnames, sess_id);
	 */
    private void handleSpatialQuery(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletResponse response,
            String username, String[] groupnames,
            String sess_id) throws PropertyNotFoundException {
        
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        
        if ( !PropertyService.getProperty("spatial.runSpatialOption").equals("true") ) {
            response.setContentType("text/html");
            out.println("<html> Metacat Spatial Option is turned off </html>");
            out.close();
            return ;
        }
        
        /*
		 * Perform spatial query against spatial cache
		 */
        float _xmax = Float.valueOf( (params.get("xmax"))[0] ).floatValue();
        float _ymax = Float.valueOf( (params.get("ymax"))[0] ).floatValue();
        float _xmin = Float.valueOf( (params.get("xmin"))[0] ).floatValue();
        float _ymin = Float.valueOf( (params.get("ymin"))[0] ).floatValue();
        SpatialQuery sq = new SpatialQuery();
        Vector<String> docids = sq.filterByBbox( _xmin, _ymin, _xmax, _ymax );
        // logMetacat.info(" --- Spatial Query completed. Passing on the SQuery
		// handler");
        // logMetacat.warn("\n\n ******* after spatial query, we've got " +
		// docids.size() + " docids \n\n");
        
        /*
		 * Create an array matching docids
		 */
        String [] docidArray = new String[docids.size()];
        docids.toArray(docidArray);
        
        /*
		 * Create squery string
		 */
        String squery = DocumentIdQuery.createDocidQuery( docidArray );
        // logMetacat.info("-----------\n" + squery + "\n------------------");
        String[] queryArray = new String[1];
        queryArray[0] = squery;
        params.put("query", queryArray);
        
        /*
		 * Determine qformat
		 */
        String[] qformatArray = new String[1];
        try {
            String _skin = (params.get("skin"))[0];
            qformatArray[0] = _skin;
        } catch (java.lang.NullPointerException e) {
            // should be "default" but keep this for backwards compatibility
			// with knp site
            logMetacat.warn("MetaCatServlet.handleSpatialQuery - No SKIN specified for metacat actions=spatial_query... defaulting to 'knp' skin !\n");
            qformatArray[0] = "knp";
        }
        params.put("qformat", qformatArray);
        
        // change the action
        String[] actionArray = new String[1];
        actionArray[0] = "squery";
        params.put("action", actionArray);
        
        /*
		 * Pass the docids to the DBQuery contructor
		 */
        // This is a hack to get the empty result set to show...
        // Otherwise dbquery sees no docidOverrides and does a full % percent
		// query
        if (docids.size() == 0)
            docids.add("");
        
        DBQuery queryobj = new DBQuery(docids);
        queryobj.findDocuments(response, out, params, username, groupnames, sess_id);
        
    }
    
    // LOGIN & LOGOUT SECTION
    /**
	 * Handle the login request. Create a new session object. Do user
	 * authentication through the session.
	 */
    private void handleLoginAction(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletRequest request, HttpServletResponse response) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        AuthSession sess = null;
        
        if(params.get("username") == null){
            response.setContentType("text/xml");
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println("Username not specified");
            out.println("</error>");
            return;
        }
        
        // }
        
        if(params.get("password") == null){
            response.setContentType("text/xml");
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println("Password not specified");
            out.println("</error>");
            return;
        }
        
        String un = (params.get("username"))[0];
        logMetacat.info("MetaCatServlet.handleLoginAction - user " + un + " is trying to login");
        String pw = (params.get("password"))[0];
        
        String qformat = "xml";
        if (params.get("qformat") != null) {
            qformat = (params.get("qformat"))[0];
        }
        
        try {
            sess = new AuthSession();
        } catch (Exception e) {
        	String errorMsg = "MetacatServlet.handleLoginAction - Problem in MetacatServlet.handleLoginAction() authenicating session: "
                + e.getMessage();
            logMetacat.error(errorMsg);
            e.printStackTrace(System.out);
            return;
        }
        boolean isValid = sess.authenticate(request, un, pw);
        
        //if it is authernticate is true, store the session
        if (isValid) {
            HttpSession session = sess.getSessions();
            String id = session.getId();
            
            logMetacat.debug("MetaCatServlet.handleLoginAction - Store session id " + id
                    + " which has username" + session.getAttribute("username")
                    + " into hash in login method");
            try {
				SessionService.registerSession(id, 
						(String) session.getAttribute("username"), 
						(String[]) session.getAttribute("groupnames"), 
						(String) session.getAttribute("password"), 
						(String) session.getAttribute("name"));
			} catch (ServiceException se) {
				String errorMsg = "MetacatServlet.handleLoginAction - service problem registering session: "
						+ se.getMessage();
				logMetacat.error("MetaCatServlet.handleLoginAction - " + errorMsg);
				se.printStackTrace(System.out);
				return;
			} 			
        }
                
        // format and transform the output
        if (qformat.equals("xml")) {
            response.setContentType("text/xml");
            out.println(sess.getMessage());
        } else {
            try {
                DBTransform trans = new DBTransform();
                response.setContentType("text/html");
                trans.transformXMLDocument(sess.getMessage(),
                        "-//NCEAS//login//EN", "-//W3C//HTML//EN", qformat,
                        out, null, null);
            } catch (Exception e) {               
                logMetacat.error("MetaCatServlet.handleLoginAction - General error"
                        + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }
    
    /**
     * Handle the logout request. Close the connection.
     */
    private void handleLogoutAction(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletRequest request, HttpServletResponse response) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        String qformat = "xml";
        if(params.get("qformat") != null){
            qformat = params.get("qformat")[0];
        }
        
        // close the connection
        HttpSession sess = request.getSession(false);
        logMetacat.info("MetaCatServlet.handleLogoutAction - After get session in logout request");
        if (sess != null) {
            logMetacat.info("MetaCatServlet.handleLogoutAction - The session id " + sess.getId()
            + " will be invalidate in logout action");
            logMetacat.info("MetaCatServlet.handleLogoutAction - The session contains user "
                    + sess.getAttribute("username")
                    + " will be invalidate in logout action");
            sess.invalidate();
            SessionService.unRegisterSession(sess.getId());
        }
        
        // produce output
        StringBuffer output = new StringBuffer();
        output.append("<?xml version=\"1.0\"?>");
        output.append("<logout>");
        output.append("User logged out");
        output.append("</logout>");
        
        //format and transform the output
        if (qformat.equals("xml")) {
            response.setContentType("text/xml");
            out.println(output.toString());
        } else {
            try {
                DBTransform trans = new DBTransform();
                response.setContentType("text/html");
                trans.transformXMLDocument(output.toString(),
                        "-//NCEAS//login//EN", "-//W3C//HTML//EN", qformat,
                        out, null, null);
            } catch (Exception e) {
                logMetacat.error("MetaCatServlet.handleLogoutAction - General error: "
                        + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }
    
    // END OF LOGIN & LOGOUT SECTION
    
    // SQUERY & QUERY SECTION
    /**
     * Retreive the squery xml, execute it and display it
     *
     * @param out the output stream to the client
     * @param params the Hashtable of parameters that should be included in the
     *            squery.
     * @param response the response object linked to the client
     * @param conn the database connection
     */
    private void handleSQuery(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletResponse response, String user, String[] groups,
            String sessionid) throws PropertyNotFoundException {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        long squeryWarnLimit = Long.parseLong(PropertyService.getProperty("database.squeryTimeWarnLimit"));
    	
        long startTime = System.currentTimeMillis();
        DBQuery queryobj = new DBQuery();
        queryobj.findDocuments(response, out, params, user, groups, sessionid);
        long outPutTime = System.currentTimeMillis();
        long runTime = outPutTime - startTime;

        if (runTime > squeryWarnLimit) {
        	logMetacat.warn("MetaCatServlet.handleSQuery - Long running squery.  Total time: " + runTime + 
        			" ms, squery: " + ((String[])params.get("query"))[0]);
        }
        logMetacat.debug("MetaCatServlet.handleSQuery - squery: " + ((String[])params.get("query"))[0] + 
        		" ran in " + runTime + " ms");
    }
    
    /**
     * Create the xml query, execute it and display the results.
     *
     * @param out the output stream to the client
     * @param params the Hashtable of parameters that should be included in the
     *            squery.
     * @param response the response object linked to the client
     */
    private void handleQuery(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletResponse response, String user, String[] groups,
            String sessionid) throws PropertyNotFoundException {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        long queryWarnLimit = Long.parseLong(PropertyService.getProperty("database.queryTimeWarnLimit"));
    	
        //create the query and run it
        String xmlquery = DBQuery.createSQuery(params);
        String[] queryArray = new String[1];
        queryArray[0] = xmlquery;
        params.put("query", queryArray);
        long startTime = System.currentTimeMillis();
        DBQuery queryobj = new DBQuery();
        queryobj.findDocuments(response, out, params, user, groups, sessionid);
        long outPutTime = System.currentTimeMillis();
        long runTime = outPutTime -startTime;

        if (runTime > queryWarnLimit) {
        	logMetacat.warn("MetaCatServlet.handleQuery - Long running squery.  Total time: " + runTime + 
        			" ms, squery: " + ((String[])params.get("query"))[0]);
        }
        logMetacat.debug("MetaCatServlet.handleQuery - query: " + ((String[])params.get("query"))[0] + 
        		" ran in " + runTime + " ms");
    }
    
    // END OF SQUERY & QUERY SECTION
    
    //Exoport section
    /**
     * Handle the "export" request of data package from Metacat in zip format
     *
     * @param params the Hashtable of HTTP request parameters
     * @param response the HTTP response object linked to the client
     * @param user the username sent the request
     * @param groups the user's groupnames
     */
    private void handleExportAction(Hashtable<String, String[]> params,
            HttpServletResponse response,
            String user, String[] groups, String passWord) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        // Output stream
        ServletOutputStream out = null;
        // Zip output stream
        ZipOutputStream zOut = null;
        DBQuery queryObj = null;
        
        String[] docs = new String[10];
        String docId = "";
        
        try {
            // read the params
            if (params.containsKey("docid")) {
                docs = params.get("docid");
            }
            // Create a DBuery to handle export
            queryObj = new DBQuery();
            // Get the docid
            docId = docs[0];
            // Make sure the client specify docid
            if (docId == null || docId.equals("")) {
                response.setContentType("text/xml"); //MIME type
                // Get a printwriter
                PrintWriter pw = response.getWriter();
                // Send back message
                pw.println("<?xml version=\"1.0\"?>");
                pw.println("<error>");
                pw.println("You didn't specify requested docid");
                pw.println("</error>");
                // Close printwriter
                pw.close();
                return;
            }
            // Get output stream
            out = response.getOutputStream();
            response.setContentType("application/zip"); //MIME type
            response.setHeader("Content-Disposition",
                    "attachment; filename="
                    + docId + ".zip"); // Set the name of the zip file
            
            zOut = new ZipOutputStream(out);
            zOut = queryObj
                    .getZippedPackage(docId, out, user, groups, passWord);
            zOut.finish(); //terminate the zip file
            zOut.close(); //close the zip stream
            
        } catch (Exception e) {
            try {
                response.setContentType("text/xml"); //MIME type
                // Send error message back
                if (out != null) {
                    PrintWriter pw = new PrintWriter(out);
                    pw.println("<?xml version=\"1.0\"?>");
                    pw.println("<error>");
                    pw.println(e.getMessage());
                    pw.println("</error>");
                    // Close printwriter
                    pw.close();
                    // Close output stream
                    out.close();
                }
                // Close zip output stream
                if (zOut != null) {
                    zOut.close();
                }
            } catch (IOException ioe) {
                logMetacat.error("MetaCatServlet.handleExportAction - Problem with the servlet output: "
                        + ioe.getMessage());
                ioe.printStackTrace(System.out);
            }
            
            logMetacat.error("MetaCatServlet.handleExportAction - General error: "
                    + e.getMessage());
            e.printStackTrace(System.out);
            
        }
        
    }
    
    /**
     * In eml2 document, the xml can have inline data and data was stripped off
     * and store in file system. This action can be used to read inline data
     * only
     *
     * @param params the Hashtable of HTTP request parameters
     * @param response the HTTP response object linked to the client
     * @param user the username sent the request
     * @param groups the user's groupnames
     */
    private void handleReadInlineDataAction(Hashtable<String, String[]> params,
            HttpServletRequest request, HttpServletResponse response,
            String user, String passWord, String[] groups) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        String[] docs = new String[10];
        String inlineDataId = null;
        String docId = "";
        ServletOutputStream out = null;
        
        try {
            // read the params
            if (params.containsKey("inlinedataid")) {
                docs = params.get("inlinedataid");
            }
            // Get the docid
            inlineDataId = docs[0];
            // Make sure the client specify docid
            if (inlineDataId == null || inlineDataId.equals("")) {
                throw new Exception("You didn't specify requested inlinedataid"); }
            
            // check for permission
            docId = 
            	DocumentUtil.getDocIdWithoutRevFromInlineDataID(inlineDataId);
            PermissionController controller = new PermissionController(docId);
            // check top level read permission
            if (!controller.hasPermission(user, groups,
                    AccessControlInterface.READSTRING)) {
                throw new Exception("User " + user
                        + " doesn't have permission " + " to read document "
                        + docId);
            } else {
                //check data access level
                try {
                    Hashtable<String,String> unReadableInlineDataList =
                            PermissionController.getUnReadableInlineDataIdList(docId,
                            user, groups, false);
                    String inlineDataIdWithoutRev = DocumentUtil.getInlineDataIdWithoutRev(inlineDataId);
                    if (unReadableInlineDataList.containsValue(inlineDataIdWithoutRev)) {
                        throw new Exception("User " + user
                                + " doesn't have permission " + " to read inlinedata "
                                + inlineDataId);
                        
                    }//if
                }//try
                catch (Exception e) {
                    throw e;
                }//catch
            }//else
            
            // Get output stream
            out = response.getOutputStream();
            // read the inline data from the file
            String inlinePath = PropertyService.getProperty("application.inlinedatafilepath");
            File lineData = new File(inlinePath, inlineDataId);
            FileInputStream input = new FileInputStream(lineData);
            byte[] buffer = new byte[4 * 1024];
            int bytes = input.read(buffer);
            while (bytes != -1) {
                out.write(buffer, 0, bytes);
                bytes = input.read(buffer);
            }
            out.close();
            
            EventLog.getInstance().log(request.getRemoteAddr(), user,
                    inlineDataId, "readinlinedata");
        } catch (Exception e) {
            try {
                PrintWriter pw = null;
                // Send error message back
                if (out != null) {
                    pw = new PrintWriter(out);
                } else {
                    pw = response.getWriter();
                }
                pw.println("<?xml version=\"1.0\"?>");
                pw.println("<error>");
                pw.println(e.getMessage());
                pw.println("</error>");
                // Close printwriter
                pw.close();
                // Close output stream if out is not null
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                logMetacat.error("MetaCatServlet.handleReadInlineDataAction - Problem with the servlet output: "
                        + ioe.getMessage());
            }
            logMetacat.error("MetaCatServlet.handleReadInlineDataAction - General error: "
                    + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
    
    /**
     * Handle the "read" request of metadata/data files from Metacat or any
     * files from Internet; transformed metadata XML document into HTML
     * presentation if requested; zip files when more than one were requested.
     *
     * @param params the Hashtable of HTTP request parameters
     * @param request the HTTP request object linked to the client
     * @param response the HTTP response object linked to the client
     * @param user the username sent the request
     * @param groups the user's groupnames
     */
    private void handleReadAction(Hashtable<String, String[]> params, HttpServletRequest request,
            HttpServletResponse response, String user, String passWord,
            String[] groups) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        ServletOutputStream out = null;
        ZipOutputStream zout = null;
        PrintWriter pw = null;
        boolean zip = false;
        boolean withInlineData = true;
        
        try {
            String[] docs = new String[0];
            String docid = "";
            String qformat = "";
            String abstrpath = null;
            
            // read the params
            if (params.containsKey("docid")) {
                docs = params.get("docid");
            }
            if (params.containsKey("qformat")) {
                qformat = params.get("qformat")[0];
            }
            // the param for only metadata (eml)
            // we don't support read a eml document without inline data now.
            /*if (params.containsKey("inlinedata")) {
             
                String inlineData = ((String[]) params.get("inlinedata"))[0];
                if (inlineData.equalsIgnoreCase("false")) {
                    withInlineData = false;
                }
            }*/
            if ((docs.length > 1) || qformat.equals("zip")) {
                zip = true;
                out = response.getOutputStream();
                response.setContentType("application/zip"); //MIME type
                zout = new ZipOutputStream(out);
            }
            // go through the list of docs to read
            for (int i = 0; i < docs.length; i++) {
            	String providedFileName = null;
            	if (params.containsKey(docs[i])) {
                    providedFileName = params.get(docs[i])[0];
                }
                try {
                    
                    URL murl = new URL(docs[i]);
                    Hashtable<String,String> murlQueryStr = MetacatUtil.parseQuery(
                            murl.getQuery());
                    // case docid="http://.../?docid=aaa"
                    // or docid="metacat://.../?docid=bbb"
                    if (murlQueryStr.containsKey("docid")) {
                        // get only docid, eliminate the rest
                        docid = murlQueryStr.get("docid");
                        if (zip) {
                            addDocToZip(request, docid, providedFileName, zout, user, groups);
                        } else {
                        	if (out == null) {
                        		out = response.getOutputStream();
                        	}
                            readFromMetacat(request, response, out, docid, qformat,
                                    abstrpath, user, groups, zip, zout,
                                    withInlineData, params);
                        }
                        
                        // case docid="http://.../filename"
                    } else {
                        docid = docs[i];
                        if (zip) {
                            addDocToZip(request, docid, providedFileName, zout, user, groups);
                        } else {
                            readFromURLConnection(response, docid);
                        }
                    }
                    
                } catch (MalformedURLException mue) {
                    docid = docs[i];
                    if (zip) {
                        addDocToZip(request, docid, providedFileName, zout, user, groups);
                    } else {
                    	if (out == null) {
                    		out = response.getOutputStream();
                    	}
                        readFromMetacat(request, response, out, docid, qformat,
                                abstrpath, user, groups, zip, zout,
                                withInlineData, params);
                    }
                }
            }
            
            if (zip) {
                zout.finish(); //terminate the zip file
                zout.close(); //close the zip stream
            }
            
        } catch (McdbDocNotFoundException notFoundE) {
            // To handle doc not found exception
            // the docid which didn't be found
            String notFoundDocId = notFoundE.getUnfoundDocId();
            String notFoundRevision = notFoundE.getUnfoundRevision();
            logMetacat.warn("MetaCatServlet.handleReadAction - Missed id: " + notFoundDocId);
            logMetacat.warn("MetaCatServlet.handleReadAction - Missed rev: " + notFoundRevision);
            try {
                // read docid from remote server
                readFromRemoteMetaCat(response, notFoundDocId,
                        notFoundRevision, user, passWord, out, zip, zout);
                // Close zout outputstream
                if (zout != null) {
                    zout.close();
                }
                // close output stream
                if (out != null) {
                    out.close();
                }
                
            } catch (Exception exc) {
                logMetacat.error("MetaCatServlet.handleReadAction - General error when trying to " + 
                		"read from remote metacat: " + exc.getMessage());
                exc.printStackTrace(System.out);
                try {
                    if (out != null) {
                        response.setContentType("text/xml");
                        // Send back error message by printWriter
                        pw = new PrintWriter(out);
                        pw.println("<?xml version=\"1.0\"?>");
                        pw.println("<error>");
                        pw.println(notFoundE.getMessage());
                        pw.println("</error>");
                        pw.close();
                        out.close();
                        
                    } else {
                        response.setContentType("text/xml"); //MIME type
                        // Send back error message if out = null
                        if (pw == null) {
                            // If pw is null, open the respnose
                            pw = response.getWriter();
                        }
                        pw.println("<?xml version=\"1.0\"?>");
                        pw.println("<error>");
                        pw.println(notFoundE.getMessage());
                        pw.println("</error>");
                        pw.close();
                    }
                    // close zout
                    if (zout != null) {
                        zout.close();
                    }
                } catch (IOException ie) {
                    logMetacat.error("MetaCatServlet.handleReadAction - I/O problem when trying to " + 
                    		"return error response: " + ie.getMessage());
                    ie.printStackTrace(System.out);
                }
            }
        } catch (Exception e) {
            try {
                
                if (out != null) {
                    response.setContentType("text/xml"); //MIME type
                    pw = new PrintWriter(out);
                    pw.println("<?xml version=\"1.0\"?>");
                    pw.println("<error>");
                    pw.println(e.getMessage());
                    pw.println("</error>");
                    pw.close();
                    out.close();
                } else {
                    response.setContentType("text/xml"); //MIME type
                    // Send back error message if out = null
                    if (pw == null) {
                        pw = response.getWriter();
                    }
                    pw.println("<?xml version=\"1.0\"?>");
                    pw.println("<error>");
                    pw.println(e.getMessage());
                    pw.println("</error>");
                    pw.close();
                    
                }
                // Close zip output stream
                if (zout != null) {
                    zout.close();
                }
                
            } catch (Exception e2) {
                logMetacat.error("MetaCatServlet.handleReadAction - Problem with the servlet output: "
                        + e2.getMessage());
                e2.printStackTrace(System.out);
                
            }
            
            logMetacat.error("MetaCatServlet.handleReadAction - General error when reading from metacat: "
                    + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
    
    /** read metadata or data from Metacat
     */
    private void readFromMetacat(HttpServletRequest request, HttpServletResponse response,
    		OutputStream out, String docid, String qformat,
            String abstrpath, String user, String[] groups, boolean zip,
            ZipOutputStream zout, boolean withInlineData, Hashtable<String, String[]> params)
            throws ClassNotFoundException, IOException, SQLException,
            McdbException, Exception {
    	
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        try {
        	
            if (docid.startsWith("urn:")) {
            	docid = LSIDUtil.getDocId(docid, true);               	
            }
            
            // here is hack for handle docid=john.10(in order to tell mike.jim.10.1
            // mike.jim.10, we require to provide entire docid with rev). But
            // some old client they only provide docid without rev, so we need
            // to handle this suituation. First we will check how many
            // seperator here, if only one, we will append the rev in xml_documents
            // to the id.
            docid = appendRev(docid);
            
            DocumentImpl doc = new DocumentImpl(docid);
            
            //check the permission for read
            if (!DocumentImpl.hasReadPermission(user, groups, docid)) {
                Exception e = new Exception("User " + user
                        + " does not have permission"
                        + " to read the document with the docid " + docid);
                
                throw e;
            }
            
            if (doc.getRootNodeID() == 0) {
                // this is data file
                String filepath = PropertyService.getProperty("application.datafilepath");
                if (!filepath.endsWith("/")) {
                    filepath += "/";
                }
                String filename = filepath + docid;
                FileInputStream fin = null;
                fin = new FileInputStream(filename);
                
                //MIME type
                String contentType = getServletContext().getMimeType(filename);
                if (contentType == null) {
                    ContentTypeProvider provider = new ContentTypeProvider(
                            docid);
                    contentType = provider.getContentType();
                    logMetacat.info("MetaCatServlet.readFromMetacat - Final contenttype is: "
                            + contentType);
                }
                
                response.setContentType(contentType);
                // if we decide to use "application/octet-stream" for all data
                // returns
                // response.setContentType("application/octet-stream");

                // check for the existence of a metadatadocid parameter,
                // if this is sent, then send a filename which contains both
                // the metadata docid and the data docid, so the link with
                // metadata is explicitly encoded in the filename.
                String metadatadocid = null;
                Vector<String> nameparts = new Vector<String>();

                if(params.containsKey("metadatadocid")) {
                    metadatadocid = params.get("metadatadocid")[0];
                }
                if (metadatadocid != null && !metadatadocid.equals("")) {
                    nameparts.add(metadatadocid);
                }
                // we'll always have the docid, include it in the name
                String doctype = doc.getDoctype();
								// TODO: fix this to lookup the associated FGDC metadata document,
								// and grab the doctype tag for it.  These should be set to something 
								// consistent, not 'metadata' as it stands...
                //if (!doctype.equals("metadata")) {
                //    nameparts.add(docid);
                //} 
								nameparts.add(docid);
                // Set the name of the data file to the entity name plus docid,
                // or if that is unavailable, use the docid alone
                String docname = doc.getDocname();
                if (docname != null && !docname.equals("")) {
                    nameparts.add(docname); 
                }
                // combine the name elements with a dash, using a 'join' equivalent
                String outputname = null;
                String delimiter = "-";
                Iterator<String> iter = nameparts.iterator();
                StringBuffer buffer = new StringBuffer(iter.next());
                while (iter.hasNext()) buffer.append(delimiter).append(iter.next());
                outputname = buffer.toString();    
                
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + outputname + "\"");
                
                try {
//                    ServletOutputStream out = response.getOutputStream();
                    byte[] buf = new byte[4 * 1024]; // 4K buffer
                    int b = fin.read(buf);
                    while (b != -1) {
                        out.write(buf, 0, b);
                        b = fin.read(buf);
                    }
                } finally {
                    if (fin != null) fin.close();
                }
                
            } else {
                // this is metadata doc
//            	ServletOutputStream streamOut = response.getOutputStream();
                if (qformat.equals("xml") || qformat.equals("")) {
                    // if equals "", that means no qformat is specified. hence
                    // by default the document should be returned in xml format
                    // set content type first
                    response.setContentType("text/xml"); //MIME type
                    response.setHeader("Content-Disposition",
                            "attachment; filename=" + docid + ".xml");
                    
                    // Try to get the metadata file from disk. If it isn't
					// found, create it from the db and write it to disk then.
					try {
						PrintWriter pw = new PrintWriter(out);
						doc.toXml(pw, user, groups, withInlineData);				
					} catch (Exception e) {
						// any exceptions in reading the xml from disc, and we go back to the
						// old way of creating the xml directly.
						logMetacat.error("MetaCatServlet.readFromMetacat - could not read from document file " + docid 
								+ ": " + e.getMessage());
						e.printStackTrace(System.out);
						PrintWriter pw = new PrintWriter(out);
						doc.toXmlFromDb(pw, user, groups, withInlineData);
					}
                } else {
                	// TODO MCD, this should read from disk as well?
                    //*** This is a metadata doc, to be returned in a skin/custom format.
                    //*** Add param to indicate if public has read access or not.
                    if (user != null && !user.equals("public")) {
                        if (DocumentImpl.hasReadPermission("public", null, docid))
                            params.put("publicRead", new String[] {"true"});
                        else
                            params.put("publicRead", new String[] {"false"});
                    }
                    
                    response.setContentType("text/html"); //MIME type
                    PrintWriter pw = new PrintWriter(out);
                    
                    // Look up the document type
                    String doctype = doc.getDoctype();
                    // Transform the document to the new doctype
                    DBTransform dbt = new DBTransform();
                    dbt.transformXMLDocument(doc.toString(user, groups,
                            withInlineData), doctype, "-//W3C//HTML//EN",
                            qformat, pw, params, null);
                }
                
            }
            EventLog.getInstance().log(request.getRemoteAddr(), user,
                    docid, "read");
        } catch (Exception except) {
            throw except;
        }
    }
    
    /**
     * read data from URLConnection
     */
    private void readFromURLConnection(HttpServletResponse response,
            String docid) throws IOException, MalformedURLException {
        ServletOutputStream out = response.getOutputStream();
        String contentType = getServletContext().getMimeType(docid); //MIME
        // type
        if (contentType == null) {
            if (docid.endsWith(".xml")) {
                contentType = "text/xml";
            } else if (docid.endsWith(".css")) {
                contentType = "text/css";
            } else if (docid.endsWith(".dtd")) {
                contentType = "text/plain";
            } else if (docid.endsWith(".xsd")) {
                contentType = "text/xml";
            } else if (docid.endsWith("/")) {
                contentType = "text/html";
            } else {
                File f = new File(docid);
                if (f.isDirectory()) {
                    contentType = "text/html";
                } else {
                    contentType = "application/octet-stream";
                }
            }
        }
        response.setContentType(contentType);
        // if we decide to use "application/octet-stream" for all data returns
        // response.setContentType("application/octet-stream");
        
        // this is http url
        URL url = new URL(docid);
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
            if (bis != null) bis.close();
        }
        
    }
    
    /**
     * read file/doc and write to ZipOutputStream
     *
     * @param docid
     * @param zout
     * @param user
     * @param groups
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SQLException
     * @throws McdbException
     * @throws Exception
     */
    private void addDocToZip(HttpServletRequest request, String docid, String providedFileName,
            ZipOutputStream zout, String user, String[] groups) throws
            ClassNotFoundException, IOException, SQLException, McdbException,
            Exception {
        byte[] bytestring = null;
        ZipEntry zentry = null;
        
        try {
            URL url = new URL(docid);
            
            // this http url; read from URLConnection; add to zip
            //use provided file name if we have one
            if (providedFileName != null && providedFileName.length() > 1) {
            	zentry = new ZipEntry(providedFileName);
            }
            else {
            	zentry = new ZipEntry(docid);
            }
            zout.putNextEntry(zentry);
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(url.openStream());
                byte[] buf = new byte[4 * 1024]; // 4K buffer
                int b = bis.read(buf);
                while (b != -1) {
                    zout.write(buf, 0, b);
                    b = bis.read(buf);
                }
            } finally {
                if (bis != null) bis.close();
            }
            zout.closeEntry();
            
        } catch (MalformedURLException mue) {
            
            // this is metacat doc (data file or metadata doc)
            try {
                DocumentImpl doc = new DocumentImpl(docid);
                
                //check the permission for read
                if (!DocumentImpl.hasReadPermission(user, groups, docid)) {
                    Exception e = new Exception("User " + user
                            + " does not have "
                            + "permission to read the document with the docid "
                            + docid);
                    throw e;
                }
                
                if (doc.getRootNodeID() == 0) {
                    // this is data file; add file to zip
                    String filepath = PropertyService.getProperty("application.datafilepath");
                    if (!filepath.endsWith("/")) {
                        filepath += "/";
                    }
                    String filename = filepath + docid;
                    FileInputStream fin = null;
                    fin = new FileInputStream(filename);
                    try {
                    	//use provided file name if we have one
                        if (providedFileName != null && providedFileName.length() > 1) {
                        	zentry = new ZipEntry(providedFileName);
                        }
                        else {
                        	zentry = new ZipEntry(docid);
                        }
                        zout.putNextEntry(zentry);
                        byte[] buf = new byte[4 * 1024]; // 4K buffer
                        int b = fin.read(buf);
                        while (b != -1) {
                            zout.write(buf, 0, b);
                            b = fin.read(buf);
                        }
                    } finally {
                        if (fin != null) fin.close();
                    }
                    zout.closeEntry();
                    
                } else {
                    // this is metadata doc; add doc to zip
                    bytestring = doc.toString().getBytes();
                    //use provided file name if given
                    if (providedFileName != null && providedFileName.length() > 1) {
                    	zentry = new ZipEntry(providedFileName);
                    }
                    else {
                    	zentry = new ZipEntry(docid + ".xml");
                    }
                    zentry.setSize(bytestring.length);
                    zout.putNextEntry(zentry);
                    zout.write(bytestring, 0, bytestring.length);
                    zout.closeEntry();
                }
                EventLog.getInstance().log(request.getRemoteAddr(), user,
                        docid, "read");
            } catch (Exception except) {
                throw except;
            }
        }
    }
    
    /**
     * If metacat couldn't find a data file or document locally, it will read
     * this docid from its home server. This is for the replication feature
     */
    private void readFromRemoteMetaCat(HttpServletResponse response,
            String docid, String rev, String user, String password,
            ServletOutputStream out, boolean zip, ZipOutputStream zout)
            throws Exception {
        // Create a object of RemoteDocument, "" is for zipEntryPath
        RemoteDocument remoteDoc = new RemoteDocument(docid, rev, user,
                password, "");
        String docType = remoteDoc.getDocType();
        // Only read data file
        if (docType.equals("BIN")) {
            // If it is zip format
            if (zip) {
                remoteDoc.readDocumentFromRemoteServerByZip(zout);
            } else {
                if (out == null) {
                    out = response.getOutputStream();
                }
                response.setContentType("application/octet-stream");
                remoteDoc.readDocumentFromRemoteServer(out);
            }
        } else {
            throw new Exception("Docid: " + docid + "." + rev
                    + " couldn't find");
        }
    }
    
    /**
     * Handle the database putdocument request and write an XML document to the
     * database connection
     */
    private void handleInsertOrUpdateAction(HttpServletRequest request,
            HttpServletResponse response, PrintWriter out, Hashtable<String, String[]> params,
            String user, String[] groups) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        DBConnection dbConn = null;
        int serialNumber = -1;
        String output = "";
        String qformat = null;
        if(params.containsKey("qformat"))
        {
          qformat = params.get("qformat")[0];
        }
        
        if(params.get("docid") == null){
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println("Docid not specified");
            out.println("</error>");
            logMetacat.error("MetaCatServlet.handleInsertOrUpdateAction - Docid not specified");
            return;
        }
        
        try {
			if (!AuthUtil.canInsertOrUpdate(user, groups)) {
				out.println("<?xml version=\"1.0\"?>");
				out.println("<error>");
				out.println("User '" + user + "' not allowed to insert and update");
				out.println("</error>");
				logMetacat.error("MetaCatServlet.handleInsertOrUpdateAction - User '" + user + "' not allowed to insert and update");
				return;
			}
		} catch (MetacatUtilException ue) {
			logMetacat.error("MetaCatServlet.handleInsertOrUpdateAction - Could not determine if user could insert or update: "
					+ ue.getMessage());
			ue.printStackTrace(System.out);
		}
        
        try {
            // Get the document indicated
            logMetacat.debug("MetaCatServlet.handleInsertOrUpdateAction - params: " + params.toString());
            
            String[] doctext = params.get("doctext");
            String pub = null;
            if (params.containsKey("public")) {
                pub = params.get("public")[0];
            }
            
            StringReader dtd = null;
            if (params.containsKey("dtdtext")) {
                String[] dtdtext = params.get("dtdtext");
                try {
                    if (!dtdtext[0].equals("")) {
                        dtd = new StringReader(dtdtext[0]);
                    }
                } catch (NullPointerException npe) {
                }
            }
            
            if(doctext == null){
                out.println("<?xml version=\"1.0\"?>");
                out.println("<error>");
                out.println("Document text not submitted");
                out.println("</error>");
                return;
            }
            
            logMetacat.debug("MetaCatServlet.handleInsertOrUpdateAction - the xml document in metacat servlet (before parsing):\n" + doctext[0]);
            StringReader xmlReader = new StringReader(doctext[0]);
            boolean validate = false;
            DocumentImplWrapper documentWrapper = null;
            try {
                // look inside XML Document for <!DOCTYPE ... PUBLIC/SYSTEM ...
                // >
                // in order to decide whether to use validation parser
                validate = needDTDValidation(xmlReader);
                if (validate) {
                    // set a dtd base validation parser
                    String rule = DocumentImpl.DTD;
                    documentWrapper = new DocumentImplWrapper(rule, validate);
                } else {
                    
                    String namespace = XMLSchemaService.findDocumentNamespace(xmlReader);
                    
                    if (namespace != null) {
                        if (namespace.compareTo(DocumentImpl.EML2_0_0NAMESPACE) == 0
                                || namespace.compareTo(
                                DocumentImpl.EML2_0_1NAMESPACE) == 0) {
                            // set eml2 base	 validation parser
                            String rule = DocumentImpl.EML200;
                            // using emlparser to check id validation
                            @SuppressWarnings("unused")
							EMLParser parser = new EMLParser(doctext[0]);
                            documentWrapper = new DocumentImplWrapper(rule, true);
                        } else if (namespace.compareTo(
                                DocumentImpl.EML2_1_0NAMESPACE) == 0) {
                            // set eml2 base validation parser
                            String rule = DocumentImpl.EML210;
                            // using emlparser to check id validation
                            @SuppressWarnings("unused")
							EMLParser parser = new EMLParser(doctext[0]);
                            documentWrapper = new DocumentImplWrapper(rule, true);
                        } else {
                            // set schema base validation parser
                            String rule = DocumentImpl.SCHEMA;
                            documentWrapper = new DocumentImplWrapper(rule, true);
                        }
                    } else {
                        documentWrapper = new DocumentImplWrapper("", false);
                    }
                }
                
                String[] action = params.get("action");
                String[] docid = params.get("docid");
                String newdocid = null;
                
                String doAction = null;
                if (action[0].equals("insert") || action[0].equals("insertmultipart")) {
                    doAction = "INSERT";
                } else if (action[0].equals("update")) {
                    doAction = "UPDATE";
                }
                
                try {
                    // get a connection from the pool
                    dbConn = DBConnectionPool
                            .getDBConnection("MetaCatServlet.handleInsertOrUpdateAction");
                    serialNumber = dbConn.getCheckOutSerialNumber();
                    
                    // write the document to the database and disk
                    String accNumber = docid[0];
                    logMetacat.debug("MetaCatServlet.handleInsertOrUpdateAction - " + doAction + " "
                            + accNumber + "...");
                    if (accNumber == null || accNumber.equals("")) {
                        logMetacat.warn("MetaCatServlet.handleInsertOrUpdateAction - writing with null acnumber");
                        newdocid = documentWrapper.write(dbConn, doctext[0], pub, dtd,
                                doAction, null, user, groups);
                        EventLog.getInstance().log(request.getRemoteAddr(),
                                user, "", action[0]);
                    } else {
	                    newdocid = documentWrapper.write(dbConn, doctext[0], pub, dtd,
	                            doAction, accNumber, user, groups);
	                            
	                    EventLog.getInstance().log(request.getRemoteAddr(),
	                            user, accNumber, action[0]);
                    }
                } finally {
                    // Return db connection
                    DBConnectionPool.returnDBConnection(dbConn, serialNumber);
                }
                
                // set content type and other response header fields first
                //response.setContentType("text/xml");
                output += "<?xml version=\"1.0\"?>";
                output += "<success>";
                output += "<docid>" + newdocid + "</docid>";
                output += "</success>";
                
            } catch (NullPointerException npe) {
                //response.setContentType("text/xml");
                output += "<?xml version=\"1.0\"?>";
                output += "<error>";
                output += npe.getMessage();
                output += "</error>";
                logMetacat.warn("MetaCatServlet.handleInsertOrUpdateAction - Null pointer error when writing eml document to the database: " + npe.getMessage());
                npe.printStackTrace();
            }
        } catch (Exception e) {
            //response.setContentType("text/xml");
            output += "<?xml version=\"1.0\"?>";
            output += "<error>";
            output += e.getMessage();
            output += "</error>";
            logMetacat.warn("MetaCatServlet.handleInsertOrUpdateAction - General error when writing eml document to the database: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (qformat == null || qformat.equals("xml")) {
            response.setContentType("text/xml");
            out.println(output);
        } else {
            try {
                DBTransform trans = new DBTransform();
                response.setContentType("text/html");
                trans.transformXMLDocument(output,
                        "message", "-//W3C//HTML//EN", qformat,
                        out, null, null);
            } catch (Exception e) {
                
                logMetacat.error("MetaCatServlet.handleInsertOrUpdateAction - General error: "
                        + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }
    
    /**
     * Parse XML Document to look for <!DOCTYPE ... PUBLIC/SYSTEM ... > in
     * order to decide whether to use validation parser
     */
    private static boolean needDTDValidation(StringReader xmlreader)
    throws IOException {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        StringBuffer cbuff = new StringBuffer();
        java.util.Stack<String> st = new java.util.Stack<String>();
        boolean validate = false;
        boolean commented = false;
        int c;
        int inx;
        
        // read from the stream until find the keywords
        while ((st.empty() || st.size() < 4) && ((c = xmlreader.read()) != -1)) {
            cbuff.append((char) c);
            
            if ((inx = cbuff.toString().indexOf("<!--")) != -1) {
            	commented = true;
            }
            
            // "<!DOCTYPE" keyword is found; put it in the stack
            if ((inx = cbuff.toString().indexOf("<!DOCTYPE")) != -1) {
                cbuff = new StringBuffer();
                st.push("<!DOCTYPE");
            }
            // "PUBLIC" keyword is found; put it in the stack
            if ((inx = cbuff.toString().indexOf("PUBLIC")) != -1) {
                cbuff = new StringBuffer();
                st.push("PUBLIC");
            }
            // "SYSTEM" keyword is found; put it in the stack
            if ((inx = cbuff.toString().indexOf("SYSTEM")) != -1) {
                cbuff = new StringBuffer();
                st.push("SYSTEM");
            }
            // ">" character is found; put it in the stack
            // ">" is found twice: fisrt from <?xml ...?>
            // and second from <!DOCTYPE ... >
            if ((inx = cbuff.toString().indexOf(">")) != -1) {
                cbuff = new StringBuffer();
                st.push(">");
            }
        }
        
        // close the stream
        xmlreader.reset();
        
        // check the stack whether it contains the keywords:
        // "<!DOCTYPE", "PUBLIC" or "SYSTEM", and ">" in this order
        if (st.size() == 4) {
            if ((st.pop()).equals(">")
            && ((st.peek()).equals("PUBLIC") | (st.pop()).equals("SYSTEM"))
                    && (st.pop()).equals("<!DOCTYPE")) {
                validate = true && !commented;
            }
        }
        
        logMetacat.info("MetaCatServlet.needDTDValidation - Validation for dtd is " + validate);
        return validate;
    }
    
    // END OF INSERT/UPDATE SECTION
    
    /**
     * Handle the database delete request and delete an XML document from the
     * database connection
     */
    private void handleDeleteAction(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletRequest request, HttpServletResponse response,
            String user, String[] groups) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        String[] docid = params.get("docid");
        
        if(docid == null){
            response.setContentType("text/xml");
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println("Docid not specified.");
            out.println("</error>");
            logMetacat.error("MetaCatServlet.handleDeleteAction - Docid not specified for the document to be deleted.");
        } else {
            
            // delete the document from the database
            try {
                
                try {
                    // null means notify server is null
                    DocumentImpl.delete(docid[0], user, groups, null);
                    EventLog.getInstance().log(request.getRemoteAddr(),
                            user, docid[0], "delete");
                    response.setContentType("text/xml");
                    out.println("<?xml version=\"1.0\"?>");
                    out.println("<success>");
                    out.println("Document deleted.");
                    out.println("</success>");
                    logMetacat.info("MetaCatServlet.handleDeleteAction - Document deleted.");
                    
                    // Delete from spatial cache if runningSpatialOption
                    if ( PropertyService.getProperty("spatial.runSpatialOption").equals("true") ) {
                        SpatialHarvester sh = new SpatialHarvester();
                        sh.addToDeleteQue( DocumentUtil.getSmartDocId( docid[0] ) );
                        sh.destroy();
                    }
                    
                } catch (AccessionNumberException ane) {
                    response.setContentType("text/xml");
                    out.println("<?xml version=\"1.0\"?>");
                    out.println("<error>");
                    //out.println("Error deleting document!!!");
                    out.println(ane.getMessage());
                    out.println("</error>");
                    logMetacat.error("MetaCatServlet.handleDeleteAction - Document could not be deleted " + 
                    		"due to accession number error: " + ane.getMessage());
                    ane.printStackTrace(System.out);
                }
            } catch (Exception e) {
                response.setContentType("text/xml");
                out.println("<?xml version=\"1.0\"?>");
                out.println("<error>");
                out.println(e.getMessage());
                out.println("</error>");
                logMetacat.error("MetaCatServlet.handleDeleteAction - Document could not be deleted " +
                        " due to general error: " + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }
    
    /**
     * Handle the validation request and return the results to the requestor
     */
    private void handleValidateAction(PrintWriter out, Hashtable<String, String[]> params) {
        
        // Get the document indicated
        String valtext = null;
        DBConnection dbConn = null;
        int serialNumber = -1;
        
        try {
            valtext = params.get("valtext")[0];
        } catch (Exception nullpe) {
            
            String docid = null;
            try {
                // Find the document id number
                docid = params.get("docid")[0];
                
                // Get the document indicated from the db
                DocumentImpl xmldoc = new DocumentImpl(docid);
                valtext = xmldoc.toString();
                
            } catch (NullPointerException npe) {
                
                out.println("<error>Error getting document ID: " + docid
                        + "</error>");
                //if ( conn != null ) { util.returnConnection(conn); }
                return;
            } catch (Exception e) {
                
                out.println(e.getMessage());
            }
        }
        
        try {
            // get a connection from the pool
            dbConn = DBConnectionPool
                    .getDBConnection("MetaCatServlet.handleValidateAction");
            serialNumber = dbConn.getCheckOutSerialNumber();
            DBValidate valobj = new DBValidate(dbConn);
//            boolean valid = valobj.validateString(valtext);
            
            // set content type and other response header fields first
            
            out.println(valobj.returnErrors());
            
        } catch (NullPointerException npe2) {
            // set content type and other response header fields first
            
            out.println("<error>Error validating document.</error>");
        } catch (Exception e) {
            
            out.println(e.getMessage());
        } finally {
            // Return db connection
            DBConnectionPool.returnDBConnection(dbConn, serialNumber);
        }
    }
    
    /**
     * Handle "getrevsionanddoctype" action Given a docid, return it's current
     * revision and doctype from data base The output is String look like
     * "rev;doctype"
     */
    private void handleGetRevisionAndDocTypeAction(PrintWriter out,
            Hashtable<String, String[]> params) {
        // To store doc parameter
        String[] docs = new String[10];
        // Store a single doc id
        String givenDocId = null;
        // Get docid from parameters
        if (params.containsKey("docid")) {
            docs = params.get("docid");
        }
        // Get first docid form string array
        givenDocId = docs[0];
        
        try {
            // Make sure there is a docid
            if (givenDocId == null || givenDocId.equals("")) { throw new Exception(
                    "User didn't specify docid!"); }//if
            
            // Create a DBUtil object
            DBUtil dbutil = new DBUtil();
            // Get a rev and doctype
            String revAndDocType = dbutil
                    .getCurrentRevisionAndDocTypeForGivenDocument(givenDocId);
            out.println(revAndDocType);
            
        } catch (Exception e) {
            // Handle exception
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println(e.getMessage());
            out.println("</error>");
        }
        
    }
    
    /**
     * Handle "getaccesscontrol" action. Read Access Control List from db
     * connection in XML format
     */
    private void handleGetAccessControlAction(PrintWriter out,
            Hashtable<String,String[]> params, HttpServletResponse response, String username,
            String[] groupnames) {
    	
    	Logger logMetacat = Logger.getLogger(MetaCatServlet.class);

        String docid = params.get("docid")[0];
        if (docid.startsWith("urn:")) {
        	try {
        		String actualDocId = LSIDUtil.getDocId(docid, false);
        		docid = actualDocId;
        	} catch (ParseLSIDException ple) {
        		logMetacat.error("MetaCatServlet.handleGetAccessControlAction - MetaCatServlet.handleGetAccessControlAction - " +
        				"could not parse lsid: " + docid + " : " + ple.getMessage());        		
        	}
        }
        
        String qformat = "xml";
        if (params.get("qformat") != null) {
            qformat = (params.get("qformat"))[0];
        }
        
        try {
        	AccessControlForSingleFile acfsf = new AccessControlForSingleFile(docid);
            String acltext = acfsf.getACL(username, groupnames);
            if (qformat.equals("xml")) {
                response.setContentType("text/xml");
            	out.println(acltext);
            } else {
            	DBTransform trans = new DBTransform();
            	response.setContentType("text/html");
            	trans.transformXMLDocument(acltext,"-//NCEAS//getaccesscontrol//EN", 
            		"-//W3C//HTML//EN", qformat, out, params, null);              
            }            
        } catch (Exception e) {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println(e.getMessage());
            out.println("</error>");
        } 
//        finally {
//            // Retrun db connection to pool
//            DBConnectionPool.returnDBConnection(dbConn, serialNumber);
//        }
    }
    
    /**
     * Handle the "getprincipals" action. Read all principals from
     * authentication scheme in XML format
     */
    private void handleGetPrincipalsAction(PrintWriter out, String user,
            String password) {
        try {
            AuthSession auth = new AuthSession();
            String principals = auth.getPrincipals(user, password);
            out.println(principals);
            
        } catch (Exception e) {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println(e.getMessage());
            out.println("</error>");
        }
    }
    
    /**
     * Handle "getdoctypes" action. Read all doctypes from db connection in XML
     * format
     */
    private void handleGetDoctypesAction(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletResponse response) {
        try {
            DBUtil dbutil = new DBUtil();
            String doctypes = dbutil.readDoctypes();
            out.println(doctypes);
        } catch (Exception e) {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println(e.getMessage());
            out.println("</error>");
        }
    }
    
    /**
     * Handle the "getdtdschema" action. Read DTD or Schema file for a given
     * doctype from Metacat catalog system
     */
    private void handleGetDTDSchemaAction(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletResponse response) {
        
        String doctype = null;
        String[] doctypeArr = params.get("doctype");
        
        // get only the first doctype specified in the list of doctypes
        // it could be done for all doctypes in that list
        if (doctypeArr != null) {
            doctype = params.get("doctype")[0];
        }
        
        try {
            DBUtil dbutil = new DBUtil();
            String dtdschema = dbutil.readDTDSchema(doctype);
            out.println(dtdschema);
            
        } catch (Exception e) {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println(e.getMessage());
            out.println("</error>");
        }
        
    }
    
    /**
     * Check if the document is registered in either the xml_documents or xml_revisions table
     * @param out the writer to write the xml results to
     * @param params request parameters
     * @param response the http servlet response
     */
    private void handleIdIsRegisteredAction(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletResponse response) {
        String id = null;
        boolean exists = false;
        if(params.get("docid") != null) {
            id = params.get("docid")[0];
        }
        
        try {
            DBUtil dbutil = new DBUtil();
            exists = dbutil.idExists(id);
        } catch (Exception e) {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println(e.getMessage());
            out.println("</error>");
        }
        
        out.println("<?xml version=\"1.0\"?>");
        out.println("<isRegistered>");
        out.println("<docid>" + id + "</docid>");
        out.println("<exists>" + exists + "</exists>");
        out.println("</isRegistered>");
    }
    
    /**
     * Handle the "getalldocids" action. return a list of all docids registered
     * in the system
     */
    private void handleGetAllDocidsAction(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletResponse response) {
        String scope = null;
        if(params.get("scope") != null) {
            scope = params.get("scope")[0];
        }
        
        try {
            DBUtil dbutil = new DBUtil();
            Vector<String> docids = dbutil.getAllDocids(scope);
            out.println("<?xml version=\"1.0\"?>");
            out.println("<idList>");
            out.println("  <scope>" + scope + "</scope>");
            for(int i=0; i<docids.size(); i++) {
                String docid = docids.elementAt(i);
                out.println("  <docid>" + docid + "</docid>");
            }
            out.println("</idList>");
            
        } catch (Exception e) {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println(e.getMessage());
            out.println("</error>");
        }
    }
    
    /**
     * Handle the "getlastdocid" action. Get the latest docid with rev number
     * from db connection in XML format
     */
    private void handleGetMaxDocidAction(PrintWriter out, Hashtable<String, String[]> params,
            HttpServletResponse response) {
        
        String scope = params.get("scope")[0];
        if (scope == null) {
            scope = params.get("username")[0];
        }
        
        try {
            
            DBUtil dbutil = new DBUtil();
            String lastDocid = dbutil.getMaxDocid(scope);
            out.println("<?xml version=\"1.0\"?>");
            out.println("<lastDocid>");
            out.println("  <scope>" + scope + "</scope>");
            out.println("  <docid>" + lastDocid + "</docid>");
            out.println("</lastDocid>");
            
        } catch (Exception e) {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println(e.getMessage());
            out.println("</error>");
        }
    }
    
    /**
     * Print a report from the event log based on filter parameters passed in
     * from the web.
     *
     * @param params the parameters from the web request
     * @param request the http request object for getting request details
     * @param response the http response object for writing output
     */
    private void handleGetLogAction(Hashtable<String, String[]> params, HttpServletRequest request,
            HttpServletResponse response, String username, String[] groups) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        try {
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            
            // Check that the user is authenticated as an administrator account
            if (!AuthUtil.isAdministrator(username, groups)) {
                out.print("<error>");
                out.print("The user \"" + username +
                        "\" is not authorized for this action.");
                out.print("</error>");
                return;
            }
            
            // Get all of the parameters in the correct formats
            String[] ipAddress = params.get("ipaddress");
            String[] principal = params.get("principal");
            String[] docid = params.get("docid");
            String[] event = params.get("event");
            String[] startArray = params.get("start");
            String[] endArray = params.get("end");
            String start = null;
            String end = null;
            if (startArray != null) {
                start = startArray[0];
            }
            if (endArray != null) {
                end = endArray[0];
            }
            Timestamp startDate = null;
            Timestamp endDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if (start != null) {
                    startDate = new Timestamp((format.parse(start)).getTime());
                }
                if (end != null) {
                    endDate = new Timestamp((format.parse(end)).getTime());
                }
            } catch (ParseException e) {
                logMetacat.error("MetaCatServlet.handleGetLogAction - Failed to created Timestamp from input.");
            }
            
            // Request the report by passing the filter parameters
            out.println(EventLog.getInstance().getReport(ipAddress, principal,
                    docid, event, startDate, endDate));
            out.close();
        } catch (IOException e) {
			logMetacat.error("MetaCatServlet.handleGetLogAction - Could not open http response for writing: "
					+ e.getMessage());
			e.printStackTrace(System.out);
		} catch (MetacatUtilException ue) {
			logMetacat.error("MetaCatServlet.handleGetLogAction - Could not determine if user is administrator: "
					+ ue.getMessage());
			ue.printStackTrace(System.out);
		}
    }
    
    /**
	 * Rebuild the index for one or more documents. If the docid parameter is
	 * provided, rebuild for just that one document or list of documents. If
	 * not, then rebuild the index for all documents in the xml_documents table.
	 * 
	 * @param params
	 *            the parameters from the web request
	 * @param request
	 *            the http request object for getting request details
	 * @param response
	 *            the http response object for writing output
	 * @param username
	 *            the username of the authenticated user
	 */
    private void handleBuildIndexAction(Hashtable<String, String[]> params,
            HttpServletRequest request, HttpServletResponse response,
            String username, String[] groups) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        
        // Get all of the parameters in the correct formats
        String[] docid = params.get("docid");
        
        // Rebuild the indices for appropriate documents
        try {
            response.setContentType("text/xml");
            PrintWriter out = response.getWriter();
            
            // Check that the user is authenticated as an administrator account
            if (!AuthUtil.isAdministrator(username, groups)) {
                out.print("<error>");
                out.print("The user \"" + username +
                        "\" is not authorized for this action.");
                out.print("</error>");
                return;
            }
            
            // Process the documents
            out.println("<success>");
            if (docid == null || docid.length == 0) {
                // Process all of the documents
                try {
                    Vector<String> documents = getDocumentList();
                    Iterator<String> it = documents.iterator();
                    while (it.hasNext()) {
                        String id = it.next();
                        buildDocumentIndex(id, out);
                    }
                } catch (SQLException se) {
                    out.print("<error>");
                    out.print(se.getMessage());
                    out.println("</error>");
                }
            } else {
                // Only process the requested documents
                for (int i = 0; i < docid.length; i++) {
                    buildDocumentIndex(docid[i], out);
                }
            }
            out.println("</success>");
            out.close();
        } catch (IOException e) {
            logMetacat.error("MetaCatServlet.handleBuildIndexAction - Could not open http response for writing: "
                    + e.getMessage());
            e.printStackTrace(System.out);
        } catch (MetacatUtilException ue) {
			logMetacat.error("MetaCatServlet.handleBuildIndexAction - Could not determine if user is administrator: "
					+ ue.getMessage());
			ue.printStackTrace(System.out);
		}
    }
    
    /**
     * Build the index for one document by reading the document and
     * calling its buildIndex() method.
     *
     * @param docid the document (with revision) to rebuild
     * @param out the PrintWriter to which output is printed
     */
    private void buildDocumentIndex(String docid, PrintWriter out) {
        try {
            DocumentImpl doc = new DocumentImpl(docid, false);
            doc.buildIndex();
            out.print("<docid>" + docid);
            out.println("</docid>");
        } catch (McdbException me) {
            out.print("<error>");
            out.print(me.getMessage());
            out.println("</error>");
        }
    }
    
    /**
     * Handle documents passed to metacat that are encoded using the
     * "multipart/form-data" mime type. This is typically used for uploading
     * data files which may be binary and large.
     */
    private void handleMultipartForm(HttpServletRequest request,
            HttpServletResponse response) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        PrintWriter out = null;
        String action = null;
        
        // Parse the multipart form, and save the parameters in a Hashtable and
        // save the FileParts in a hashtable
        
        Hashtable<String,String[]> params = new Hashtable<String,String[]>();
        Hashtable<String,String> fileList = new Hashtable<String,String>();
        int sizeLimit = 1000;
        try {
        	sizeLimit = 
        		(new Integer(PropertyService.getProperty("replication.datafilesizelimit"))).intValue();
        } catch (PropertyNotFoundException pnfe) {
        	logMetacat.error("MetaCatServlet.handleMultipartForm - Could not determine data file size limit.  Using 1000. " 
        			+ pnfe.getMessage());
        }
        logMetacat.debug("MetaCatServlet.handleMultipartForm - The size limit of uploaded data files is: " + sizeLimit);
        
        try {
            MultipartParser mp = new MultipartParser(request,
                    sizeLimit * 1024 * 1024);
            Part part;
            
            while ((part = mp.readNextPart()) != null) {
                String name = part.getName();
                
                if (part.isParam()) {
                    // it's a parameter part
                    ParamPart paramPart = (ParamPart) part;
                    String[] values = new String[1];
                    values[0] = paramPart.getStringValue();
                    params.put(name, values);
                    if (name.equals("action")) {
                        action = values[0];
                    }
                } else if (part.isFile()) {
                    // it's a file part
                    FilePart filePart = (FilePart) part;
                    String fileName = filePart.getFileName();
                    String fileTempLocation;
                    
                    // the filePart will be clobbered on the next loop, save to disk
                    fileTempLocation = MetacatUtil.writeTempUploadFile(filePart, fileName);
                    fileList.put(name, fileTempLocation);
                    fileList.put("filename", fileName);
                    fileList.put("name", fileTempLocation);
                } else {
                    logMetacat.info("MetaCatServlet.handleMultipartForm - Upload name '" + name + "' was empty.");
                }
            }
        } catch (IOException ioe) {
            try {
                out = response.getWriter();
            } catch (IOException ioe2) {
                logMetacat.fatal("MetaCatServlet.handleMultipartForm - Fatal Error: couldn't get response output stream.");
            }
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println("Error: problem reading multipart data: " + ioe.getMessage());
            out.println("</error>");
            out.close();
            return;
        }
        
        // Get the session information
        String username = null;
        String password = null;
        String[] groupnames = null;
        String sess_id = null;
        
        // be aware of session expiration on every request
        HttpSession sess = request.getSession(true);
        if (sess.isNew()) {
            // session expired or has not been stored b/w user requests
            username = "public";
            sess.setAttribute("username", username);
        } else {
            username = (String) sess.getAttribute("username");
            password = (String) sess.getAttribute("password");
            groupnames = (String[]) sess.getAttribute("groupnames");
            try {
                sess_id = (String) sess.getId();
            } catch (IllegalStateException ise) {
                System.out
                        .println("error in  handleMultipartForm: this shouldn't "
                        + "happen: the session should be valid: "
                        + ise.getMessage());
            }
        }
        
        // Get the out stream
        try {
            out = response.getWriter();
        } catch (IOException ioe2) {
            logMetacat.error("MetaCatServlet.handleMultipartForm - couldn't get response "
                    + "output stream: " + ioe2.getMessage());
            ioe2.printStackTrace(System.out);
        }
        
        if (action.equals("upload")) {
            if (username != null && !username.equals("public")) {
                handleUploadAction(request, out, params, fileList, username,
                        groupnames, response);
            } else {                
                out.println("<?xml version=\"1.0\"?>");
                out.println("<error>");
                out.println("Permission denied for " + action);
                out.println("</error>");
            }
        } else if(action.equals("insertmultipart")) {
          if (username != null && !username.equals("public")) {
            logMetacat.debug("MetaCatServlet.handleMultipartForm - handling multipart insert");
              handleInsertMultipartAction(request, response,
                            out, params, fileList, username, groupnames);
          } else {
              out.println("<?xml version=\"1.0\"?>");
              out.println("<error>");
              out.println("Permission denied for " + action);
              out.println("</error>");
          }
        } else {
            /*
             * try { out = response.getWriter(); } catch (IOException ioe2) {
             * System.err.println("Fatal Error: couldn't get response output
             * stream.");
             */
            out.println("<?xml version=\"1.0\"?>");
            out.println("<error>");
            out.println("Error: action not registered.  Please report this error.");
            out.println("</error>");
        }
        out.close();
    }
    
    /**
     * Handle the upload action by saving the attached file to disk and
     * registering it in the Metacat db
     */
    private void handleInsertMultipartAction(HttpServletRequest request, 
            HttpServletResponse response,
            PrintWriter out, Hashtable<String,String[]> params, Hashtable<String,String> fileList,
            String username, String[] groupnames)
    {
      Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
      String action = null;
      String docid = null;
      String qformat = null;
      String output = "";
      
      /*
       * response.setContentType("text/xml"); try { out =
       * response.getWriter(); } catch (IOException ioe2) {
       * System.err.println("Fatal Error: couldn't get response output
       * stream.");
       */
      
      if (params.containsKey("docid")) 
      {
          docid = params.get("docid")[0];
      }
      
      if(params.containsKey("qformat")) 
      {
          qformat = params.get("qformat")[0];
      }
      
      // Make sure we have a docid and datafile
      if (docid != null && fileList.containsKey("datafile")) 
      {
        logMetacat.info("MetaCatServlet.handleInsertMultipartAction - Uploading data docid: " + docid);
        // Get a reference to the file part of the form
        //FilePart filePart = (FilePart) fileList.get("datafile");
        String fileName = fileList.get("filename");
        logMetacat.debug("MetaCatServlet.handleInsertMultipartAction - Uploading filename: " + fileName);
        // Check if the right file existed in the uploaded data
        if (fileName != null) 
        {
              
          try 
          {
              //logMetacat.info("Upload datafile " + docid
              // +"...", 10);
              //If document get lock data file grant
            if (DocumentImpl.getDataFileLockGrant(docid)) 
            {
              // Save the data file to disk using "docid" as the name
              String datafilepath = PropertyService.getProperty("application.datafilepath");
              File dataDirectory = new File(datafilepath);
              dataDirectory.mkdirs();
              File newFile = null;
    //          File tempFile = null;
              String tempFileName = fileList.get("name");
              String newFileName = dataDirectory + File.separator + docid;
              long size = 0;
              boolean fileExists = false;
                      
              try 
              {
                newFile = new File(newFileName);
                fileExists = newFile.exists();
                logMetacat.info("MetaCatServlet.handleInsertMultipartAction - new file status is: " + fileExists);
                if(fileExists)
                {
                  newFile.delete();
                  newFile.createNewFile();
                  fileExists = false;
                }
                
                if ( fileExists == false ) 
                {
                    // copy file to desired output location
                    try 
                    {
                        MetacatUtil.copyFile(tempFileName, newFileName);
                    } 
                    catch (IOException ioe) 
                    {
                        logMetacat.error("MetaCatServlet.handleInsertMultipartAction - IO Exception copying file: " +
                                ioe.getMessage());
                        ioe.printStackTrace(System.out);
                    }
                    size = newFile.length();
                    if (size == 0) 
                    {
                        throw new IOException("MetaCatServlet.handleInsertMultipartAction - Uploaded file is 0 bytes!");
                    }
                }
                logMetacat.info("MetaCatServlet.handleInsertMultipartAction - Uploading the following to Metacat:" +
                        fileName + ", " + docid + ", " +
                        username + ", " + groupnames);
                FileReader fr = new FileReader(newFile);
                
                char[] c = new char[1024];
                int numread = fr.read(c, 0, 1024);
                StringBuffer sb = new StringBuffer();
                while(numread != -1)
                {
                  sb.append(c, 0, numread);
                  numread = fr.read(c, 0, 1024);
                }
                
                Enumeration<String> keys = params.keys();
                while(keys.hasMoreElements())
                { //convert the params to arrays
                  String key = keys.nextElement();
                  String[] paramValue = params.get(key);
                  String[] s = new String[1];
                  s[0] = paramValue[0];
                  params.put(key, s);
                }
                //add the doctext to the params
                String doctext = sb.toString();
                String[] doctextArr = new String[1];
                doctextArr[0] = doctext;
                params.put("doctext", doctextArr);
                //call the insert routine
                handleInsertOrUpdateAction(request, response, out, 
                          params, username, groupnames);
              }
              catch(Exception e)
              {
                throw e;
              }
            }
          }
          catch(Exception e)
          {
              logMetacat.error("MetaCatServlet.handleInsertMultipartAction - error uploading text file via multipart: " + e.getMessage());
              e.printStackTrace(System.out);
          }
        }
      }
    }
    
    /**
     * Handle the upload action by saving the attached file to disk and
     * registering it in the Metacat db
     */
    private void handleUploadAction(HttpServletRequest request,
            PrintWriter out, Hashtable<String, String[]> params, Hashtable<String,String> fileList,
            String username, String[] groupnames, HttpServletResponse response) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        //PrintWriter out = null;
        //Connection conn = null;
//        String action = null;
        String docid = null;
        String qformat = null;
        String output = "";
        
        /*
         * response.setContentType("text/xml"); try { out =
         * response.getWriter(); } catch (IOException ioe2) {
         * System.err.println("Fatal Error: couldn't get response output
         * stream.");
         */
        
        if (params.containsKey("docid")) {
            docid = params.get("docid")[0];
        }
        
        if(params.containsKey("qformat")) {
            qformat = params.get("qformat")[0];
        }
        
        // Make sure we have a docid and datafile
        if (docid != null && fileList.containsKey("datafile")) {
            logMetacat.info("MetaCatServlet.handleUploadAction - Uploading data docid: " + docid);
            // Get a reference to the file part of the form
            //FilePart filePart = (FilePart) fileList.get("datafile");
            String fileName = fileList.get("filename");
            logMetacat.info("MetaCatServlet.handleUploadAction - Uploading filename: " + fileName);
            // Check if the right file existed in the uploaded data
            if (fileName != null) {
                
                try {
                    //logMetacat.info("Upload datafile " + docid
                    // +"...", 10);
                    //If document get lock data file grant
                    if (DocumentImpl.getDataFileLockGrant(docid)) {
                        // Save the data file to disk using "docid" as the name
                        String datafilepath = PropertyService.getProperty("application.datafilepath");
                        File dataDirectory = new File(datafilepath);
                        dataDirectory.mkdirs();
                        File newFile = null;
    //                    File tempFile = null;
                        String tempFileName = fileList.get("name");
                        String newFileName = dataDirectory + File.separator + docid;
                        long size = 0;
                        boolean fileExists = false;
                        
                        try {
                            newFile = new File(newFileName);
                            fileExists = newFile.exists();
                            logMetacat.info("MetaCatServlet.handleUploadAction - new file status is: " + fileExists);
                            if ( fileExists == false ) {
                                // copy file to desired output location
                                try {
                                    MetacatUtil.copyFile(tempFileName, newFileName);
                                } catch (IOException ioe) {
                                    logMetacat.error("IO Exception copying file: " +
                                            ioe.getMessage());
                                    ioe.printStackTrace(System.out);
                                }
                                size = newFile.length();
                                if (size == 0) {
                                    throw new IOException("Uploaded file is 0 bytes!");
                                }
                            }
                            logMetacat.info("MetaCatServlet.handleUploadAction - Uploading the following to Metacat:" +
                                    fileName + ", " + docid + ", " +
                                    username + ", " + groupnames);
                            //register the file in the database (which generates
                            // an exception
                            //if the docid is not acceptable or other untoward
                            // things happen
                            DocumentImpl.registerDocument(fileName, "BIN", docid,
                                    username, groupnames);
                        } catch (Exception ee) {
                            //delete the file to create
                            // if the revision already exists, don't delete
                            // the file
                            if ( fileExists == false ) {
                                newFile.delete();
                            }
                            logMetacat.info("MetaCatServlet.handleUploadAction - in Exception: fileExists is " + fileExists);
                            logMetacat.error("MetaCatServlet.handleUploadAction - Upload Error: " + ee.getMessage());
                            ee.printStackTrace(System.out);
                            throw ee;
                        }
                        
                        EventLog.getInstance().log(request.getRemoteAddr(),
                                username, docid, "upload");
                        // Force replication this data file
                        // To data file, "insert" and update is same
                        // The fourth parameter is null. Because it is
                        // notification server
                        // and this method is in MetaCatServerlet. It is
                        // original command,
                        // not get force replication info from another metacat
                        ForceReplicationHandler frh = new ForceReplicationHandler(
                                docid, "insert", false, null);
                        logMetacat.debug("MetaCatServlet.handleUploadAction - ForceReplicationHandler created: " + frh.toString());
                        
                        // set content type and other response header fields
                        // first
                        output += "<?xml version=\"1.0\"?>";
                        output += "<success>";
                        output += "<docid>" + docid + "</docid>";
                        output += "<size>" + size + "</size>";
                        output += "</success>";
                    }
                    
                } catch (Exception e) {
                    
                    output += "<?xml version=\"1.0\"?>";
                    output += "<error>";
                    output += e.getMessage();
                    output += "</error>";
                }
            } else {
                // the field did not contain a file
                output += "<?xml version=\"1.0\"?>";
                output += "<error>";
                output += "The uploaded data did not contain a valid file.";
                output += "</error>";
            }
        } else {
            // Error bcse docid missing or file missing
            output += "<?xml version=\"1.0\"?>";
            output += "<error>";
            output += "The uploaded data did not contain a valid docid "
                    + "or valid file.";
            output += "</error>";
        }
        
        if (qformat == null || qformat.equals("xml")) {
            response.setContentType("text/xml");
            out.println(output);
        } else {
            try {
                DBTransform trans = new DBTransform();
                response.setContentType("text/html");
                trans.transformXMLDocument(output,
                        "message", "-//W3C//HTML//EN", qformat,
                        out, null, null);
            } catch (Exception e) {
                
                logMetacat.error("MetaCatServlet.handleUploadAction - General error: "
                        + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }
    
    /*
     * A method to handle set access action
     */
    private void handleSetAccessAction(PrintWriter out, Hashtable<String, String[]> params,
            String username, HttpServletRequest request, HttpServletResponse response) {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);

        String permissionStr = null;
        String permType = null;
        String permOrder = null;
        Vector<String> errorList = new Vector<String>();
        String error = null;
        Vector<String> successList = new Vector<String>();
        String success = null;
        boolean isEmlPkgMember = false;
        
        String[] docList = params.get("docid");
        String[] principalList = params.get("principal");
        String[] permissionList = params.get("permission");
        String[] permTypeList = params.get("permType");
        String[] permOrderList = params.get("permOrder");
        String[] qformatList = params.get("qformat");
        String[] accessBlock = params.get("accessBlock");
        
        if(accessBlock != null) {
        	if (docList == null) {
        		errorList.addElement("MetaCatServlet.handleSetAccessAction - Doc id missing.  Please check your parameter list, it should look like: "
                    + "?action=setaccess&docid=<doc_id>&accessBlock=<access_section>");
                outputResponse(successList, errorList, out);
                return;
        	}
        	
        	try {
	            AccessControlForSingleFile accessControl = 
	            	new AccessControlForSingleFile(docList[0]);
	            accessControl.insertPermissions(accessBlock[0]);
	            successList.addElement("MetaCatServlet.handleSetAccessAction - successfully replaced access block for doc id: " + docList[0]);
        	} catch(AccessControlException ace) {
        		errorList.addElement("MetaCatServlet.handleSetAccessAction - access control error when setting " + 
        			"access block: " + ace.getMessage());
        	} 
        	outputResponse(successList, errorList, out);
        	return;
        }
        
        // Make sure the parameter is not null
        if (docList == null || principalList == null || permTypeList == null
                || permissionList == null) {
            error = "MetaCatServlet.handleSetAccessAction - Please check your parameter list, it should look like: "
                    + "?action=setaccess&docid=pipeline.1.1&principal=public"
                    + "&permission=read&permType=allow&permOrder=allowFirst";
            errorList.addElement(error);
            outputResponse(successList, errorList, out);
            return;
        }
        
        // Only select first element for permission, type and order
        permissionStr = permissionList[0];
        permType = permTypeList[0];
        if (permOrderList != null) {
            permOrder = permOrderList[0];
        }
        
        // Get package doctype set
        Vector<String> packageSet = null;
        try {
        	packageSet = 
        		MetacatUtil.getOptionList(PropertyService.getProperty("xml.packagedoctypeset"));
        } catch (PropertyNotFoundException pnfe) {
        	logMetacat.error("MetaCatServlet.handleSetAccessAction - Could not find package doctype set.  Setting to null: " 
        			+ pnfe.getMessage());
        }
        //debug
        if (packageSet != null) {
            for (int i = 0; i < packageSet.size(); i++) {
                logMetacat.debug("MetaCatServlet.handleSetAccessAction - doctype in package set: "
                        + packageSet.elementAt(i));
            }
        }
        
        // handle every accessionNumber
        for (int i = 0; i < docList.length; i++) {
        	String docid = docList[i];
            if (docid.startsWith("urn:")) {
            	try {
            		String actualDocId = LSIDUtil.getDocId(docid, false);
            		docid = actualDocId;
            	} catch (ParseLSIDException ple) {
            		logMetacat.error("MetaCatServlet.handleSetAccessAction - " +
            				"could not parse lsid: " + docid + " : " + ple.getMessage()); 
            		ple.printStackTrace(System.out);
            	}
            }
        	
            String accessionNumber = docid;
            String owner = null;
            String publicId = null;
            // Get document owner and public id
            try {
                owner = getFieldValueForDoc(accessionNumber, "user_owner");
                publicId = getFieldValueForDoc(accessionNumber, "doctype");
            } catch (Exception e) {
                logMetacat.error("MetaCatServlet.handleSetAccessAction - Error in handleSetAccessAction: "
                        + e.getMessage());
                e.printStackTrace(System.out);
                error = "Error in set access control for document - " + accessionNumber + e.getMessage();
                errorList.addElement(error);
                continue;
            }
            //check if user is the owner. Only owner can do owner
            if (username == null || owner == null || !username.equals(owner)) {
                error = "User - " + username + " does not have permission to set "
                        + "access control for docid - " + accessionNumber;
                errorList.addElement(error);
                continue;
            }
            
            //*** Find out if the document is a Member of an EML package.
            //*** (for efficiency, only check if it isn't already true
            //*** for the case of multiple files).
            if (isEmlPkgMember == false)
                isEmlPkgMember = (DBUtil.findDataSetDocIdForGivenDocument(accessionNumber) != null);
            
            // If docid publicid is BIN data file or other beta4, 6 package document
            // we could not do set access control. Because we don't want inconsistent
            // to its access docuemnt
            if (publicId != null && packageSet != null
                    && packageSet.contains(publicId) && isEmlPkgMember) {
                error = "Could not set access control to document " + accessionNumber
                        + "because it is in a pakcage and it has a access file for it";
                errorList.addElement(error);
                continue;
            }
            
            // for every principle
            for (int j = 0; j < principalList.length; j++) {
                String principal = principalList[j];
                try {
                    //insert permission
                    AccessControlForSingleFile accessControl = 
                    	new AccessControlForSingleFile(accessionNumber);
                    int permissionInt = AccessControlList.intValue(permissionStr);
                    accessControl.insertPermissions(principal, new Long(permissionInt), permType, permOrder, null, null);
                } catch (Exception ee) {
                    logMetacat.error("MetaCatServlet.handleSetAccessAction - Error inserting permission: "
                            + ee.getMessage());
                    ee.printStackTrace(System.out);
                    error = "Failed to set access control for document "
                            + accessionNumber + " because " + ee.getMessage();
                    errorList.addElement(error);
                    continue;
                }
            }
            
            //force replication when this action is called
            boolean isXml = true;
            if (publicId.equalsIgnoreCase("BIN")) {
            	isXml = false;
            }
            ForceReplicationHandler frh = 
            	new ForceReplicationHandler(accessionNumber, isXml, null);
            logMetacat.debug("MetaCatServlet.handleSetAccessAction - ForceReplicationHandler created: " + frh.toString());
            
        }
        successList.addElement("MetaCatServlet.handleSetAccessAction - successfully added individual access for doc id: " + docList[0]);
        
        if (params.get("forwardto")  != null) {
        	try {
        		RequestUtil.forwardRequest(request, response, params);
        	} catch (MetacatUtilException mue) {
        		logMetacat.error("metaCatServlet.handleSetAccessAction - could not forward " +
        				"request. Sending output to response writer");
        		mue.printStackTrace(System.out);
        		outputResponse(successList, errorList, out);
        	}       		
        } else {
        	outputResponse(successList, errorList, out);
        }
    }
    
    /*
     * A method try to determin a docid's public id, if couldn't find null will
     * be returned.
     */
    private String getFieldValueForDoc(String accessionNumber, String fieldName)
    throws Exception {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        if (accessionNumber == null || accessionNumber.equals("")
        || fieldName == null || fieldName.equals("")) { throw new Exception(
                "Docid or field name was not specified"); }
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String fieldValue = null;
        String docId = null;
        DBConnection conn = null;
        int serialNumber = -1;
        
        // get rid of revision if access number has
        docId = DocumentUtil.getDocIdFromString(accessionNumber);
        try {
            //check out DBConnection
            conn = DBConnectionPool
                    .getDBConnection("MetaCatServlet.getPublicIdForDoc");
            serialNumber = conn.getCheckOutSerialNumber();
            pstmt = conn.prepareStatement("SELECT " + fieldName
                    + " FROM xml_documents " + "WHERE docid = ? ");
            
            pstmt.setString(1, docId);
            pstmt.execute();
            rs = pstmt.getResultSet();
            boolean hasRow = rs.next();
        //    int perm = 0;
            if (hasRow) {
                fieldValue = rs.getString(1);
            } else {
                throw new Exception("Could not find document: "
                        + accessionNumber);
            }
        } catch (Exception e) {
            logMetacat.error("MetaCatServlet.getFieldValueForDoc - General error: "
                    + e.getMessage());
            e.printStackTrace(System.out);
            throw e;
        } finally {
            try {
                rs.close();
                pstmt.close();
                
            } finally {
                DBConnectionPool.returnDBConnection(conn, serialNumber);
            }
        }
        return fieldValue;
    }
    
    /*
     * Get the list of documents from the database and return the list in an
     * Vector of identifiers.
     *
     * @ returns the array of identifiers
     */
    private Vector<String> getDocumentList() throws SQLException {
        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        Vector<String> docList = new Vector<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        DBConnection conn = null;
        int serialNumber = -1;
        
        try {
            //check out DBConnection
            conn = DBConnectionPool
                    .getDBConnection("MetaCatServlet.getDocumentList");
            serialNumber = conn.getCheckOutSerialNumber();
            pstmt = conn.prepareStatement("SELECT docid, rev"
                    + " FROM xml_documents ");
            pstmt.execute();
            rs = pstmt.getResultSet();
            while (rs.next()) {
                String docid = rs.getString(1);
                String rev = rs.getString(2);
                docList.add(docid + "." + rev);
            }
        } catch (SQLException sqle) {
            logMetacat.error("MetaCatServlet.getDocumentList - General exception: "
                    + sqle.getMessage());
            sqle.printStackTrace(System.out);
            throw sqle;
        } finally {
            try {
                rs.close();
                pstmt.close();
                
            } catch (SQLException se) {
                logMetacat.error("MetaCatServlet.getDocumentList - General exception: "
                        + se.getMessage());
                se.printStackTrace(System.out);
                throw se;
            } finally {
                DBConnectionPool.returnDBConnection(conn, serialNumber);
            }
        }
        return docList;
    }
    
    /*
     * A method to output setAccess action result
     */
    private void outputResponse(Vector<String> successList, Vector<String> errorList,
            PrintWriter out) {
        boolean error = false;
        boolean success = false;
        // Output prolog
        out.println(PROLOG);
        // output success message
        if (successList != null) {
            for (int i = 0; i < successList.size(); i++) {
                out.println(SUCCESS);
                out.println(successList.elementAt(i));
                out.println(SUCCESSCLOSE);
                success = true;
            }
        }
        // output error message
        if (errorList != null) {
            for (int i = 0; i < errorList.size(); i++) {
                out.println(ERROR);
                out.println(errorList.elementAt(i));
                out.println(ERRORCLOSE);
                error = true;
            }
        }
        
        // if no error and no success info, send a error that nothing happened
        if (!error && !success) {
            out.println(ERROR);
            out.println("Nothing happend for setaccess action");
            out.println(ERRORCLOSE);
        }
    }
    
    /*
     * If the given docid only have one seperter, we need
     * append rev for it. The rev come from xml_documents
     */
    private static String appendRev(String docid) throws Exception {
//        Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
        String newAccNum = null;
        String separator = PropertyService.getProperty("document.accNumSeparator");
        int firstIndex = docid.indexOf(separator);
        int lastIndex = docid.lastIndexOf(separator);
        if (firstIndex == lastIndex) {
            
            //only one seperater
            int rev = DBUtil.getLatestRevisionInDocumentTable(docid);
            if (rev == -1) {
                throw new Exception("the requested docid '"
                        + docid+ "' does not exist");
            } else {
                newAccNum = docid+ separator+ rev;
            }
        } else {
            // in other suituation we don't change the docid
            newAccNum = docid;
        }
        //logMetacat.debug("The docid will be read is "+newAccNum);
        return newAccNum;
    }
    
    /**
     * Schedule the sitemap generator to run periodically and update all
     * of the sitemap files for search indexing engines.
     *
     * @param request a servlet request, from which we determine the context
     */
    private void scheduleSitemapGeneration(HttpServletRequest request) {
    	Logger logMetacat = Logger.getLogger(MetaCatServlet.class);
    	if (!_sitemapScheduled) {
        	String directoryName = null;
        	String skin = null;
        	long sitemapInterval = 0;
        	
        	try {
				directoryName = SystemUtil.getContextDir() + FileUtil.getFS() + "sitemaps";
				skin = PropertyService.getProperty("application.default-style");
				sitemapInterval = 
					Integer.parseInt(PropertyService.getProperty("sitemap.interval"));
			} catch (PropertyNotFoundException pnfe) {
				logMetacat.error("MetaCatServlet.scheduleSitemapGeneration - Could not run site map generation because property " 
						+ "could not be found: " + pnfe.getMessage());
			}
			
            File directory = new File(directoryName);
            directory.mkdirs();
            String urlRoot = request.getRequestURL().toString();
            Sitemap smap = new Sitemap(directory, urlRoot, skin);
            long firstDelay = 60*1000;   // 60 seconds delay
            timer.schedule(smap, firstDelay, sitemapInterval);
            _sitemapScheduled = true;
        }
    }
    
    /**
     * Reports whether the MetaCatServlet has been fully initialized
     * 
     * @return true if fully intialized, false otherwise
     */
    public static boolean isFullyInitialized() {
    	return _fullyInitialized;
    }
}
