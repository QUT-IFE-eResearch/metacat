/**
 *  '$RCSfile$'
 *    Purpose: A Class that searches a relational DB for elements and
 *             attributes that have free text matches a query string,
 *             or structured query matches to a path specified node in the
 *             XML hierarchy.  It returns a result set consisting of the
 *             document ID for each document that satisfies the query
 *  Copyright: 2000 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Matt Jones
 *
 *   '$Author: berkley $'
 *     '$Date: 2010-08-12 05:17:16 +1000 (Thu, 12 Aug 2010) $'
 * '$Revision: 5489 $'
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

import java.io.*;
import java.util.zip.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import org.w3c.dom.ls.*;

import edu.ucsb.nceas.metacat.accesscontrol.AccessControlInterface;
import edu.ucsb.nceas.metacat.database.DBConnection;
import edu.ucsb.nceas.metacat.database.DBConnectionPool;
import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.util.AuthUtil;
import edu.ucsb.nceas.metacat.util.DocumentUtil;
import edu.ucsb.nceas.metacat.util.MetacatUtil;
import edu.ucsb.nceas.morpho.datapackage.Triple;
import edu.ucsb.nceas.morpho.datapackage.TripleCollection;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;


/**
 * A Class that searches a relational DB for elements and attributes that have
 * free text matches a query string, or structured query matches to a path
 * specified node in the XML hierarchy. It returns a result set consisting of
 * the document ID for each document that satisfies the query
 */
public class DBQuery
{

    static final int ALL = 1;

    static final int WRITE = 2;

    static final int READ = 4;
    
    private String qformat = "xml";

    //private Connection conn = null;
    private String parserName = null;

    private Logger logMetacat = Logger.getLogger(DBQuery.class);

    /** true if the metacat spatial option is installed **/
    private final boolean METACAT_SPATIAL = true;

    /** useful if you just want to grab a list of docids. Since the docids can be very long,
         it is a vector of vector  **/
    Vector docidOverride = new Vector();
    
    // a hash table serves as query reuslt cache. Key of hashtable
    // is a query string and value is result xml string
    private static Hashtable queryResultCache = new Hashtable();
    
    // Capacity of the query result cache
    private static final int QUERYRESULTCACHESIZE;
    static {
    	int qryRsltCacheSize = 0;
    	try {
    		qryRsltCacheSize = Integer.parseInt(PropertyService.getProperty("database.queryresultCacheSize"));
    	} catch (PropertyNotFoundException pnfe) {
    		System.err.println("Could not get QUERYRESULTCACHESIZE property in static block: "
					+ pnfe.getMessage());
    	}
    	QUERYRESULTCACHESIZE = qryRsltCacheSize;
    }
    

    // Size of page for non paged query
    private static final int NONPAGESIZE = 99999999;
    /**
     * the main routine used to test the DBQuery utility.
     * <p>
     * Usage: java DBQuery <xmlfile>
     *
     * @param xmlfile the filename of the xml file containing the query
     */
    static public void main(String[] args)
    {

        if (args.length < 1) {
            System.err.println("Wrong number of arguments!!!");
            System.err.println("USAGE: java DBQuery [-t] [-index] <xmlfile>");
            return;
        } else {
            try {

                int i = 0;
                boolean showRuntime = false;
                boolean useXMLIndex = false;
                if (args[i].equals("-t")) {
                    showRuntime = true;
                    i++;
                }
                if (args[i].equals("-index")) {
                    useXMLIndex = true;
                    i++;
                }
                String xmlfile = args[i];

                // Time the request if asked for
                double startTime = System.currentTimeMillis();

                // Open a connection to the database
                //Connection dbconn = util.openDBConnection();

                double connTime = System.currentTimeMillis();

                // Execute the query
                DBQuery queryobj = new DBQuery();
                FileReader xml = new FileReader(new File(xmlfile));
                Hashtable nodelist = null;
                //nodelist = queryobj.findDocuments(xml, null, null, useXMLIndex);

                // Print the reulting document listing
                StringBuffer result = new StringBuffer();
                String document = null;
                String docid = null;
                result.append("<?xml version=\"1.0\"?>\n");
                result.append("<resultset>\n");

                if (!showRuntime) {
                    Enumeration doclist = nodelist.keys();
                    while (doclist.hasMoreElements()) {
                        docid = (String) doclist.nextElement();
                        document = (String) nodelist.get(docid);
                        result.append("  <document>\n    " + document
                                + "\n  </document>\n");
                    }

                    result.append("</resultset>\n");
                }
                // Time the request if asked for
                double stopTime = System.currentTimeMillis();
                double dbOpenTime = (connTime - startTime) / 1000;
                double readTime = (stopTime - connTime) / 1000;
                double executionTime = (stopTime - startTime) / 1000;
                if (showRuntime) {
                    System.out.print("  " + executionTime);
                    System.out.print("  " + dbOpenTime);
                    System.out.print("  " + readTime);
                    System.out.print("  " + nodelist.size());
                    System.out.println();
                }
                //System.out.println(result);
                //write into a file "result.txt"
                if (!showRuntime) {
                    File f = new File("./result.txt");
                    FileWriter fw = new FileWriter(f);
                    BufferedWriter out = new BufferedWriter(fw);
                    out.write(result.toString());
                    out.flush();
                    out.close();
                    fw.close();
                }

            } catch (Exception e) {
                System.err.println("Error in DBQuery.main");
                System.err.println(e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * construct an instance of the DBQuery class
     *
     * <p>
     * Generally, one would call the findDocuments() routine after creating an
     * instance to specify the search query
     * </p>
     *

     * @param parserName the fully qualified name of a Java class implementing
     *            the org.xml.sax.XMLReader interface
     */
    public DBQuery() throws PropertyNotFoundException
    {
        String parserName = PropertyService.getProperty("xml.saxparser");
        this.parserName = parserName;
    }

    /**
     * 
     * Construct an instance of DBQuery Class
     * BUT accept a docid Vector that will supersede
     * the query.printSQL() method
     *
     * If a docid Vector is passed in,
     * the docids will be used to create a simple IN query 
     * without the multiple subselects of the printSQL() method
     *
     * Using this constructor, we just check for 
     * a docidOverride Vector in the findResultDoclist() method
     *
     * @param docids List of docids to display in the resultset
     */
    public DBQuery(Vector docids) throws PropertyNotFoundException
    {
    	// since the query will be too long to be handled, so we divided the 
    	// docids vector into couple vectors.
    	int size = (new Integer(PropertyService.getProperty("database.appResultsetSize"))).intValue();
    	logMetacat.info("DBQuery.DBQuery - The size of select doicds is "+docids.size());
    	logMetacat.info("DBQuery.DBQuery - The application result size in metacat.properties is "+size);
    	Vector subset = new Vector();
    	if (docids != null && docids.size() > size)
    	{
    		int index = 0;
    		for (int i=0; i< docids.size(); i++)
    		{
    			
    			if (index < size)
    			{  	
    				subset.add(docids.elementAt(i));
    				index ++;
    			}
    			else
    			{
    				docidOverride.add(subset);
    				subset = new Vector();
    				subset.add(docids.elementAt(i));
    			    index = 1;
    			}
    		}
    		if (!subset.isEmpty())
    		{
    			docidOverride.add(subset);
    		}
    		
    	}
    	else
    	{
    		this.docidOverride.add(docids);
    	}
        
        String parserName = PropertyService.getProperty("xml.saxparser");
        this.parserName = parserName;
    }

  /**
   * Method put the search result set into out printerwriter
   * @param resoponse the return response
   * @param out the output printer
   * @param params the paratermer hashtable
   * @param user the user name (it maybe different to the one in param)
   * @param groups the group array
   * @param sessionid  the sessionid
   */
  public void findDocuments(HttpServletResponse response,
                                       PrintWriter out, Hashtable params,
                                       String user, String[] groups,
                                       String sessionid) throws PropertyNotFoundException
  {
    boolean useXMLIndex = (new Boolean(PropertyService.getProperty("database.usexmlindex")))
               .booleanValue();
    findDocuments(response, out, params, user, groups, sessionid, useXMLIndex);

  }


    /**
     * Method put the search result set into out printerwriter
     * @param resoponse the return response
     * @param out the output printer
     * @param params the paratermer hashtable
     * @param user the user name (it maybe different to the one in param)
     * @param groups the group array
     * @param sessionid  the sessionid
     */
    public void findDocuments(HttpServletResponse response,
                                         PrintWriter out, Hashtable params,
                                         String user, String[] groups,
                                         String sessionid, boolean useXMLIndex)
    {
      int pagesize = 0;
      int pagestart = 0;
      long transferWarnLimit = 0; 
      
      if(params.containsKey("pagesize") && params.containsKey("pagestart"))
      {
        String pagesizeStr = ((String[])params.get("pagesize"))[0];
        String pagestartStr = ((String[])params.get("pagestart"))[0];
        if(pagesizeStr != null && pagestartStr != null)
        {
          pagesize = (new Integer(pagesizeStr)).intValue();
          pagestart = (new Integer(pagestartStr)).intValue();
        }
      }
      
      String xmlquery = null;
      String qformat = null;
      // get query and qformat
      try {
    	xmlquery = ((String[])params.get("query"))[0];

        logMetacat.info("DBQuery.findDocuments - SESSIONID: " + sessionid);
        logMetacat.info("DBQuery.findDocuments - xmlquery: " + xmlquery);
        qformat = ((String[])params.get("qformat"))[0];
        logMetacat.info("DBQuery.findDocuments - qformat: " + qformat);
      }
      catch (Exception ee)
      {
        logMetacat.error("DBQuery.findDocuments - Couldn't retrieve xmlquery or qformat value from "
                  +"params hashtable in DBQuery.findDocuments: "
                  + ee.getMessage()); 
      }
      // Get the XML query and covert it into a SQL statment
      QuerySpecification qspec = null;
      if ( xmlquery != null)
      {
         xmlquery = transformQuery(xmlquery);
         try
         {
           qspec = new QuerySpecification(xmlquery,
                                          parserName,
                                          PropertyService.getProperty("document.accNumSeparator"));
         }
         catch (Exception ee)
         {
           logMetacat.error("DBQuery.findDocuments - error generating QuerySpecification object: "
                                    + ee.getMessage());
         }
      }



      if (qformat != null && qformat.equals(MetacatUtil.XMLFORMAT))
      {
        //xml format
        response.setContentType("text/xml");
        createResultDocument(xmlquery, qspec, out, user, groups, useXMLIndex, 
          pagesize, pagestart, sessionid, qformat);
      }//if
      else
      {
        //knb format, in this case we will get whole result and sent it out
        response.setContentType("text/html");
        PrintWriter nonout = null;
        StringBuffer xml = createResultDocument(xmlquery, qspec, nonout, user,
                                                groups, useXMLIndex, pagesize, 
                                                pagestart, sessionid, qformat);
        
        //transfer the xml to html
        try
        {
         long startHTMLTransform = System.currentTimeMillis();
         DBTransform trans = new DBTransform();
         response.setContentType("text/html");

         // if the user is a moderator, then pass a param to the 
         // xsl specifying the fact
         if(AuthUtil.isModerator(user, groups)){
        	 params.put("isModerator", new String[] {"true"});
         }

         trans.transformXMLDocument(xml.toString(), "-//NCEAS//resultset//EN",
                                 "-//W3C//HTML//EN", qformat, out, params,
                                 sessionid);
         long transformRunTime = System.currentTimeMillis() - startHTMLTransform;
         
         transferWarnLimit = Long.parseLong(PropertyService.getProperty("dbquery.transformTimeWarnLimit"));
         
         if (transformRunTime > transferWarnLimit) {
         	logMetacat.warn("DBQuery.findDocuments - The time to transfrom resultset from xml to html format is "
                  		                             + transformRunTime);
         }
          MetacatUtil.writeDebugToFile("---------------------------------------------------------------------------------------------------------------Transfrom xml to html  "
                             + transformRunTime);
          MetacatUtil.writeDebugToDelimiteredFile(" " + transformRunTime, false);
        }
        catch(Exception e)
        {
         logMetacat.error("DBQuery.findDocuments - Error in MetaCatServlet.transformResultset:"
                                +e.getMessage());
         }

      }//else

  }
    
    
  
  /**
   * Transforms a hashtable of documents to an xml or html result and sent
   * the content to outputstream. Keep going untill hastable is empty. stop it.
   * add the QuerySpecification as parameter is for ecogrid. But it is duplicate
   * to xmlquery String
   * @param xmlquery
   * @param qspec
   * @param out
   * @param user
   * @param groups
   * @param useXMLIndex
   * @param sessionid
   * @return
   */
    public StringBuffer createResultDocument(String xmlquery,
                                              QuerySpecification qspec,
                                              PrintWriter out,
                                              String user, String[] groups,
                                              boolean useXMLIndex)
    {
    	return createResultDocument(xmlquery,qspec,out, user,groups, useXMLIndex, 0, 0,"", qformat);
    }

  /*
   * Transforms a hashtable of documents to an xml or html result and sent
   * the content to outputstream. Keep going untill hastable is empty. stop it.
   * add the QuerySpecification as parameter is for ecogrid. But it is duplicate
   * to xmlquery String
   */
  public StringBuffer createResultDocument(String xmlquery,
                                            QuerySpecification qspec,
                                            PrintWriter out,
                                            String user, String[] groups,
                                            boolean useXMLIndex, int pagesize,
                                            int pagestart, String sessionid, 
                                            String qformat)
  {
    DBConnection dbconn = null;
    int serialNumber = -1;
    StringBuffer resultset = new StringBuffer();

    //try to get the cached version first    
    // Hashtable sessionHash = MetaCatServlet.getSessionHash();
    // HttpSession sess = (HttpSession)sessionHash.get(sessionid);

    
    resultset.append("<?xml version=\"1.0\"?>\n");
    resultset.append("<resultset>\n");
    resultset.append("  <pagestart>" + pagestart + "</pagestart>\n");
    resultset.append("  <pagesize>" + pagesize + "</pagesize>\n");
    resultset.append("  <nextpage>" + (pagestart + 1) + "</nextpage>\n");
    resultset.append("  <previouspage>" + (pagestart - 1) + "</previouspage>\n");

    resultset.append("  <query>" + xmlquery + "</query>");
    //send out a new query
    if (out != null)
    {
      out.println(resultset.toString());
    }
    if (qspec != null)
    {
      try
      {

        //checkout the dbconnection
        dbconn = DBConnectionPool.getDBConnection("DBQuery.findDocuments");
        serialNumber = dbconn.getCheckOutSerialNumber();

        //print out the search result
        // search the doc list
        Vector givenDocids = new Vector();
        StringBuffer resultContent = new StringBuffer();
        if (docidOverride == null || docidOverride.size() == 0)
        {
        	logMetacat.debug("DBQuery.createResultDocument - Not in map query");
        	resultContent = findResultDoclist(qspec, out, user, groups,
                    dbconn, useXMLIndex, pagesize, pagestart, 
                    sessionid, givenDocids, qformat);
        }
        else
        {
        	logMetacat.debug("DBQuery.createResultDocument - In map query");
        	// since docid can be too long to be handled. We divide it into several parts
        	for (int i= 0; i<docidOverride.size(); i++)
        	{
        	   logMetacat.debug("DBQuery.createResultDocument - in loop===== "+i);
        		givenDocids = (Vector)docidOverride.elementAt(i);
        		StringBuffer subset = findResultDoclist(qspec, out, user, groups,
                        dbconn, useXMLIndex, pagesize, pagestart, 
                        sessionid, givenDocids, qformat);
        		resultContent.append(subset);
        	}
        }
           
        resultset.append(resultContent);
      } //try
      catch (IOException ioe)
      {
        logMetacat.error("DBQuery.createResultDocument - IO error: " + ioe.getMessage());
      }
      catch (SQLException e)
      {
        logMetacat.error("DBQuery.createResultDocument - SQL Error: " + e.getMessage());
      }
      catch (Exception ee)
      {
        logMetacat.error("DBQuery.createResultDocument - General exception: "
                                 + ee.getMessage());
        ee.printStackTrace();
      }
      finally
      {
        DBConnectionPool.returnDBConnection(dbconn, serialNumber);
      } //finally
    }//if
    String closeRestultset = "</resultset>";
    resultset.append(closeRestultset);
    if (out != null)
    {
      out.println(closeRestultset);
    }

    //default to returning the whole resultset
    return resultset;
  }//createResultDocuments

    /*
     * Find the doc list which match the query
     */
    private StringBuffer findResultDoclist(QuerySpecification qspec,
                                      PrintWriter out,
                                      String user, String[]groups,
                                      DBConnection dbconn, boolean useXMLIndex,
                                      int pagesize, int pagestart, String sessionid, 
                                      Vector givenDocids, String qformat)
                                      throws Exception
    {
      StringBuffer resultsetBuffer = new StringBuffer();
      String query = null;
      int count = 0;
      int index = 0;
      ResultDocumentSet docListResult = new ResultDocumentSet();
      PreparedStatement pstmt = null;
      String docid = null;
      String docname = null;
      String doctype = null;
      String createDate = null;
      String updateDate = null;
      StringBuffer document = null;
      boolean lastpage = false;
      int rev = 0;
      double startTime = 0;
      int offset = 1;
      long startSelectionTime = System.currentTimeMillis();
      ResultSet rs = null;
           
   
      // this is a hack for offset. in postgresql 7, if the returned docid list is too long,
      //the extend query which base on the docid will be too long to be run. So we 
      // have to cut them into different parts. Page query don't need it somehow.
      if (out == null)
      {
        // for html page, we put everything into one page
        offset =
            (new Integer(PropertyService.getProperty("database.webResultsetSize"))).intValue();
      }
      else
      {
          offset =
              (new Integer(PropertyService.getProperty("database.appResultsetSize"))).intValue();
      }

      /*
       * Check the docidOverride Vector
       * if defined, we bypass the qspec.printSQL() method
       * and contruct a simpler query based on a 
       * list of docids rather than a bunch of subselects
       */
      if ( givenDocids == null || givenDocids.size() == 0 ) {
          query = qspec.printSQL(useXMLIndex);
      } else {
          logMetacat.info("DBQuery.findResultDoclist - docid override " + givenDocids.size());
          StringBuffer queryBuffer = new StringBuffer( "SELECT docid,docname,doctype,date_created, date_updated, rev " );
          queryBuffer.append( " FROM xml_documents WHERE docid IN (" );
          for (int i = 0; i < givenDocids.size(); i++) {  
              queryBuffer.append("'");
              queryBuffer.append( (String)givenDocids.elementAt(i) );
              queryBuffer.append("',");
          }
          // empty string hack 
          queryBuffer.append( "'') " );
          query = queryBuffer.toString();
      } 
      String ownerQuery = getOwnerQuery(user);
      //logMetacat.debug("query: " + query);
      logMetacat.debug("DBQuery.findResultDoclist - owner query: " + ownerQuery);
      // if query is not the owner query, we need to check the permission
      // otherwise we don't need (owner has all permission by default)
      if (!query.equals(ownerQuery))
      {
        // set user name and group
        qspec.setUserName(user);
        qspec.setGroup(groups);
        // Get access query
        String accessQuery = qspec.getAccessQuery();
        if(!query.endsWith("WHERE")){
            query = query + accessQuery;
        } else {
            query = query + accessQuery.substring(4, accessQuery.length());
        }
        
      }
      logMetacat.debug("DBQuery.findResultDoclist - final selection query: " + query);
      String selectionAndExtendedQuery = null;
      // we only get cache for public
      if (user != null && user.equalsIgnoreCase("public") 
     		 && pagesize == 0 && PropertyService.getProperty("database.queryCacheOn").equals("true"))
      {
    	  selectionAndExtendedQuery = query +qspec.getReturnDocList()+qspec.getReturnFieldList();
   	      String cachedResult = getResultXMLFromCache(selectionAndExtendedQuery);
   	      logMetacat.debug("DBQuery.findResultDoclist - The key of query cache is " + selectionAndExtendedQuery);
   	      //System.out.println("==========the string from cache is "+cachedResult);
   	      if (cachedResult != null)
   	      {
   	    	logMetacat.info("DBQuery.findResultDoclist - result from cache !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
   	    	 if (out != null)
   	         {
   	             out.println(cachedResult);
   	         }
   	    	 resultsetBuffer.append(cachedResult);
   	    	 return resultsetBuffer;
   	      }
      }
      
      startTime = System.currentTimeMillis() / 1000;
      pstmt = dbconn.prepareStatement(query);
      rs = pstmt.executeQuery();

      double queryExecuteTime = System.currentTimeMillis() / 1000;
      logMetacat.debug("DBQuery.findResultDoclist - Time to execute select docid query is "
                    + (queryExecuteTime - startTime));
      MetacatUtil.writeDebugToFile("\n\n\n\n\n\nExecute selection query  "
              + (queryExecuteTime - startTime));
      MetacatUtil.writeDebugToDelimiteredFile(""+(queryExecuteTime - startTime), false);

      boolean tableHasRows = rs.next();
      
      if(pagesize == 0)
      { //this makes sure we get all results if there is no paging
        pagesize = NONPAGESIZE;
        pagestart = NONPAGESIZE;
      } 
      
      int currentIndex = 0;
      while (tableHasRows)
      {
        logMetacat.debug("DBQuery.findResultDoclist - getting result: " + currentIndex);
        docid = rs.getString(1).trim();
        logMetacat.debug("DBQuery.findResultDoclist -  processing: " + docid);
        docname = rs.getString(2);
        doctype = rs.getString(3);
        logMetacat.debug("DBQuery.findResultDoclist - processing: " + doctype);
        createDate = rs.getString(4);
        updateDate = rs.getString(5);
        rev = rs.getInt(6);
        
         Vector returndocVec = qspec.getReturnDocList();
       if (returndocVec.size() == 0 || returndocVec.contains(doctype))
        {
          logMetacat.debug("DBQuery.findResultDoclist - NOT Back tracing now...");
           document = new StringBuffer();

           String completeDocid = docid
                            + PropertyService.getProperty("document.accNumSeparator");
           completeDocid += rev;
           document.append("<docid>").append(completeDocid).append("</docid>");
           if (docname != null)
           {
               document.append("<docname>" + docname + "</docname>");
           }
           if (doctype != null)
           {
              document.append("<doctype>" + doctype + "</doctype>");
           }
           if (createDate != null)
           {
               document.append("<createdate>" + createDate + "</createdate>");
           }
           if (updateDate != null)
           {
             document.append("<updatedate>" + updateDate + "</updatedate>");
           }
           // Store the document id and the root node id
           
           docListResult.addResultDocument(
             new ResultDocument(docid, (String) document.toString()));
           logMetacat.info("DBQuery.findResultDoclist - real result: " + docid);
           currentIndex++;
           count++;
        }//else
        
        // when doclist reached the offset number, send out doc list and empty
        // the hash table
        if (count == offset && pagesize == NONPAGESIZE)
        { //if pagesize is not 0, do this later.
          //reset count
          //logMetacat.warn("############doing subset cache");
          count = 0;
          handleSubsetResult(qspec, resultsetBuffer, out, docListResult,
                              user, groups,dbconn, useXMLIndex, qformat);
          //reset docListResult
          docListResult = new ResultDocumentSet();
        }
       
       logMetacat.debug("DBQuery.findResultDoclist - currentIndex: " + currentIndex);
       logMetacat.debug("DBQuery.findResultDoclist - page comparator: " + (pagesize * pagestart) + pagesize);
       if(currentIndex >= ((pagesize * pagestart) + pagesize))
       {
         ResultDocumentSet pagedResultsHash = new ResultDocumentSet();
         for(int i=pagesize*pagestart; i<docListResult.size(); i++)
         {
           pagedResultsHash.put(docListResult.get(i));
         }
         
         docListResult = pagedResultsHash;
         break;
       }
       // Advance to the next record in the cursor
       tableHasRows = rs.next();
       if(!tableHasRows)
       {
         ResultDocumentSet pagedResultsHash = new ResultDocumentSet();
         //get the last page of information then break
         if(pagesize != NONPAGESIZE)
         {
           for(int i=pagesize*pagestart; i<docListResult.size(); i++)
           {
             pagedResultsHash.put(docListResult.get(i));
           }
           docListResult = pagedResultsHash;
         }
         
         lastpage = true;
         break;
       }
     }//while
     
     rs.close();
     pstmt.close();
     long docListTime = System.currentTimeMillis() - startSelectionTime;
     long docListWarnLimit = Long.parseLong(PropertyService.getProperty("dbquery.findDocListTimeWarnLimit"));
     if (docListTime > docListWarnLimit) {
    	 logMetacat.warn("DBQuery.findResultDoclist - Total time to get docid list is: "
                          + docListTime);
     }
     MetacatUtil.writeDebugToFile("---------------------------------------------------------------------------------------------------------------Total selection: "
             + docListTime);
     MetacatUtil.writeDebugToDelimiteredFile(" "+ docListTime, false);
     //if docListResult is not empty, it need to be sent.
     if (docListResult.size() != 0)
     {
      
       handleSubsetResult(qspec,resultsetBuffer, out, docListResult,
                              user, groups,dbconn, useXMLIndex, qformat);
     }

     resultsetBuffer.append("\n<lastpage>" + lastpage + "</lastpage>\n");
     if (out != null)
     {
         out.println("\n<lastpage>" + lastpage + "</lastpage>\n");
     }
     
     // now we only cached none-paged query and user is public
     if (user != null && user.equalsIgnoreCase("public") 
    		 && pagesize == NONPAGESIZE && PropertyService.getProperty("database.queryCacheOn").equals("true"))
     {
       //System.out.println("the string stored into cache is "+ resultsetBuffer.toString());
  	   storeQueryResultIntoCache(selectionAndExtendedQuery, resultsetBuffer.toString());
     }
          
     return resultsetBuffer;
    }//findReturnDoclist


    /*
     * Send completed search hashtable(part of reulst)to output stream
     * and buffer into a buffer stream
     */
    private StringBuffer handleSubsetResult(QuerySpecification qspec,
                                           StringBuffer resultset,
                                           PrintWriter out, ResultDocumentSet partOfDoclist,
                                           String user, String[]groups,
                                       DBConnection dbconn, boolean useXMLIndex,
                                       String qformat)
                                       throws Exception
   {
     double startReturnFieldTime = System.currentTimeMillis();
     // check if there is a record in xml_returnfield
     // and get the returnfield_id and usage count
     int usage_count = getXmlReturnfieldsTableId(qspec, dbconn);
     boolean enterRecords = false;

     // get value of database.xmlReturnfieldCount
     int count = (new Integer(PropertyService
                            .getProperty("database.xmlReturnfieldCount")))
                            .intValue();

     // set enterRecords to true if usage_count is more than the offset
     // specified in metacat.properties
     if(usage_count > count){
         enterRecords = true;
     }

     if(returnfield_id < 0){
         logMetacat.warn("DBQuery.handleSubsetResult - Error in getting returnfield id from"
                                  + "xml_returnfield table");
         enterRecords = false;
     }

     // get the hashtable containing the docids that already in the
     // xml_queryresult table
     logMetacat.info("DBQuery.handleSubsetResult - size of partOfDoclist before"
                             + " docidsInQueryresultTable(): "
                             + partOfDoclist.size());
     long startGetReturnValueFromQueryresultable = System.currentTimeMillis();
     Hashtable queryresultDocList = docidsInQueryresultTable(returnfield_id,
                                                        partOfDoclist, dbconn);

     // remove the keys in queryresultDocList from partOfDoclist
     Enumeration _keys = queryresultDocList.keys();
     while (_keys.hasMoreElements()){
         partOfDoclist.remove((String)_keys.nextElement());
     }
     
     long queryResultReturnValuetime = System.currentTimeMillis() - startGetReturnValueFromQueryresultable;
     long queryResultWarnLimit = 
    	 Long.parseLong(PropertyService.getProperty("dbquery.findQueryResultsTimeWarnLimit"));
     
     if (queryResultReturnValuetime > queryResultWarnLimit) {
    	 logMetacat.warn("DBQuery.handleSubsetResult - Time to get return fields from xml_queryresult table is (Part1 in return fields) " +
    		 queryResultReturnValuetime);
     }
     MetacatUtil.writeDebugToFile("-----------------------------------------Get fields from xml_queryresult(Part1 in return fields) " +
    		 queryResultReturnValuetime);
     MetacatUtil.writeDebugToDelimiteredFile(" " + queryResultReturnValuetime,false);
     
     long startExtendedQuery = System.currentTimeMillis();
     // backup the keys-elements in partOfDoclist to check later
     // if the doc entry is indexed yet
     Hashtable partOfDoclistBackup = new Hashtable();
     Iterator itt = partOfDoclist.getDocids();
     while (itt.hasNext()){
       Object key = itt.next();
         partOfDoclistBackup.put(key, partOfDoclist.get(key));
     }

     logMetacat.info("DBQuery.handleSubsetResult - size of partOfDoclist after"
                             + " docidsInQueryresultTable(): "
                             + partOfDoclist.size());

     //add return fields for the documents in partOfDoclist
     partOfDoclist = addReturnfield(partOfDoclist, qspec, user, groups,
                                        dbconn, useXMLIndex, qformat);
     long extendedQueryRunTime = startExtendedQuery - System.currentTimeMillis();
     long extendedQueryWarnLimit = 
    	 Long.parseLong(PropertyService.getProperty("dbquery.extendedQueryRunTimeWarnLimit"));
  
     if (extendedQueryRunTime > extendedQueryWarnLimit) {
    	 logMetacat.warn("DBQuery.handleSubsetResult - Get fields from index and node table (Part2 in return fields) "
        		                                          + extendedQueryRunTime);
     }
     MetacatUtil.writeDebugToFile("-----------------------------------------Get fields from extened query(Part2 in return fields) "
             + extendedQueryRunTime);
     MetacatUtil.writeDebugToDelimiteredFile(" "
             + extendedQueryRunTime, false);
     //add relationship part part docid list for the documents in partOfDocList
     //partOfDoclist = addRelationship(partOfDoclist, qspec, dbconn, useXMLIndex);

     long startStoreReturnField = System.currentTimeMillis();
     Iterator keys = partOfDoclist.getDocids();
     String key = null;
     String element = null;
     String query = null;
     int offset = (new Integer(PropertyService
                               .getProperty("database.queryresultStringLength")))
                               .intValue();
     while (keys.hasNext())
     {
         key = (String) keys.next();
         element = (String)partOfDoclist.get(key);
         
	 // check if the enterRecords is true, elements is not null, element's
         // length is less than the limit of table column and if the document
         // has been indexed already
         if(enterRecords && element != null
		&& element.length() < offset
		&& element.compareTo((String) partOfDoclistBackup.get(key)) != 0){
             query = "INSERT INTO xml_queryresult (returnfield_id, docid, "
                 + "queryresult_string) VALUES (?, ?, ?)";

             PreparedStatement pstmt = null;
             pstmt = dbconn.prepareStatement(query);
             pstmt.setInt(1, returnfield_id);
             pstmt.setString(2, key);
             pstmt.setString(3, element);
            
             dbconn.increaseUsageCount(1);
             try
             {
            	 pstmt.execute();
             }
             catch(Exception e)
             {
            	 logMetacat.warn("DBQuery.handleSubsetResult - couldn't insert the element to xml_queryresult table "+e.getLocalizedMessage());
             }
             finally
             {
                pstmt.close();
             }
         }
        
         // A string with element
         String xmlElement = "  <document>" + element + "</document>";

         //send single element to output
         if (out != null)
         {
             out.println(xmlElement);
         }
         resultset.append(xmlElement);
     }//while
     
     double storeReturnFieldTime = System.currentTimeMillis() - startStoreReturnField;
     long storeReturnFieldWarnLimit = 
    	 Long.parseLong(PropertyService.getProperty("dbquery.storeReturnFieldTimeWarnLimit"));

     if (storeReturnFieldTime > storeReturnFieldWarnLimit) {
    	 logMetacat.warn("DBQuery.handleSubsetResult - Time to store new return fields into xml_queryresult table (Part4 in return fields) "
                   + storeReturnFieldTime);
     }
     MetacatUtil.writeDebugToFile("-----------------------------------------Insert new record to xml_queryresult(Part4 in return fields) "
             + storeReturnFieldTime);
     MetacatUtil.writeDebugToDelimiteredFile(" " + storeReturnFieldTime, false);
     
     Enumeration keysE = queryresultDocList.keys();
     while (keysE.hasMoreElements())
     {
         key = (String) keysE.nextElement();
         element = (String)queryresultDocList.get(key);
         // A string with element
         String xmlElement = "  <document>" + element + "</document>";
         //send single element to output
         if (out != null)
         {
             out.println(xmlElement);
         }
         resultset.append(xmlElement);
     }//while
     double returnFieldTime = System.currentTimeMillis() - startReturnFieldTime;
     long totalReturnFieldWarnLimit = 
    	 Long.parseLong(PropertyService.getProperty("dbquery.totalReturnFieldTimeWarnLimit"));

     if (returnFieldTime > totalReturnFieldWarnLimit) {
    	 logMetacat.warn("DBQuery.handleSubsetResult - Total time to get return fields is: "
                           + returnFieldTime);
     }
     MetacatUtil.writeDebugToFile("DBQuery.handleSubsetResult - ---------------------------------------------------------------------------------------------------------------"+
    		 "Total to get return fields  " + returnFieldTime);
     MetacatUtil.writeDebugToDelimiteredFile("DBQuery.handleSubsetResult - "+ returnFieldTime, false);
     return resultset;
 }

   /**
    * Get the docids already in xml_queryresult table and corresponding
    * queryresultstring as a hashtable
    */
   private Hashtable docidsInQueryresultTable(int returnfield_id,
                                              ResultDocumentSet partOfDoclist,
                                              DBConnection dbconn){

         Hashtable returnValue = new Hashtable();
         PreparedStatement pstmt = null;
         ResultSet rs = null;

         // get partOfDoclist as string for the query
         Iterator keylist = partOfDoclist.getDocids();
         StringBuffer doclist = new StringBuffer();
         while (keylist.hasNext())
         {
             doclist.append("'");
             doclist.append((String) keylist.next());
             doclist.append("',");
         }//while


         if (doclist.length() > 0)
         {
             doclist.deleteCharAt(doclist.length() - 1); //remove the last comma

             // the query to find out docids from xml_queryresult
             String query = "select docid, queryresult_string from "
                          + "xml_queryresult where returnfield_id = " +
                          returnfield_id +" and docid in ("+ doclist + ")";
             logMetacat.info("DBQuery.docidsInQueryresultTable - Query to get docids from xml_queryresult:"
                                      + query);

             try {
                 // prepare and execute the query
                 pstmt = dbconn.prepareStatement(query);
                 dbconn.increaseUsageCount(1);
                 pstmt.execute();
                 rs = pstmt.getResultSet();
                 boolean tableHasRows = rs.next();
                 while (tableHasRows) {
                     // store the returned results in the returnValue hashtable
                     String key = rs.getString(1);
                     String element = rs.getString(2);

                     if(element != null){
                         returnValue.put(key, element);
                     } else {
                         logMetacat.info("DBQuery.docidsInQueryresultTable - Null elment found ("
                         + "DBQuery.docidsInQueryresultTable)");
                     }
                     tableHasRows = rs.next();
                 }
                 rs.close();
                 pstmt.close();
             } catch (Exception e){
                 logMetacat.error("DBQuery.docidsInQueryresultTable - Error getting docids from "
                                          + "queryresult: " + e.getMessage());
              }
         }
         return returnValue;
     }


   /**
    * Method to get id from xml_returnfield table
    * for a given query specification
    */
   private int returnfield_id;
   private int getXmlReturnfieldsTableId(QuerySpecification qspec,
                                           DBConnection dbconn){
       int id = -1;
       int count = 1;
       PreparedStatement pstmt = null;
       ResultSet rs = null;
       String returnfield = qspec.getSortedReturnFieldString();

       // query for finding the id from xml_returnfield
       String query = "SELECT returnfield_id, usage_count FROM xml_returnfield "
            + "WHERE returnfield_string LIKE ?";
       logMetacat.info("DBQuery.getXmlReturnfieldsTableId - ReturnField Query:" + query);

       try {
           // prepare and run the query
           pstmt = dbconn.prepareStatement(query);
           pstmt.setString(1,returnfield);
           dbconn.increaseUsageCount(1);
           pstmt.execute();
           rs = pstmt.getResultSet();
           boolean tableHasRows = rs.next();

           // if record found then increase the usage count
           // else insert a new record and get the id of the new record
           if(tableHasRows){
               // get the id
               id = rs.getInt(1);
               count = rs.getInt(2) + 1;
               rs.close();
               pstmt.close();

               // increase the usage count
               query = "UPDATE xml_returnfield SET usage_count ='" + count
                   + "' WHERE returnfield_id ='"+ id +"'";
               logMetacat.info("DBQuery.getXmlReturnfieldsTableId - ReturnField Table Update:"+ query);

               pstmt = dbconn.prepareStatement(query);
               dbconn.increaseUsageCount(1);
               pstmt.execute();
               pstmt.close();

           } else {
               rs.close();
               pstmt.close();

               // insert a new record
               query = "INSERT INTO xml_returnfield (returnfield_string, usage_count)"
                   + "VALUES (?, '1')";
               logMetacat.info("DBQuery.getXmlReturnfieldsTableId - ReturnField Table Insert:"+ query);
               pstmt = dbconn.prepareStatement(query);
               pstmt.setString(1, returnfield);
               dbconn.increaseUsageCount(1);
               pstmt.execute();
               pstmt.close();

               // get the id of the new record
               query = "SELECT returnfield_id FROM xml_returnfield "
                   + "WHERE returnfield_string LIKE ?";
               logMetacat.info("DBQuery.getXmlReturnfieldsTableId - ReturnField query after Insert:" + query);
               pstmt = dbconn.prepareStatement(query);
               pstmt.setString(1, returnfield);

               dbconn.increaseUsageCount(1);
               pstmt.execute();
               rs = pstmt.getResultSet();
               if(rs.next()){
                   id = rs.getInt(1);
               } else {
                   id = -1;
               }
               rs.close();
               pstmt.close();
           }

       } catch (Exception e){
           logMetacat.error("DBQuery.getXmlReturnfieldsTableId - Error getting id from xml_returnfield in "
                                     + "DBQuery.getXmlReturnfieldsTableId: "
                                     + e.getMessage());
           id = -1;
       }

       returnfield_id = id;
       return count;
   }


    /*
     * A method to add return field to return doclist hash table
     */
    private ResultDocumentSet addReturnfield(ResultDocumentSet docListResult,
                                      QuerySpecification qspec,
                                      String user, String[]groups,
                                      DBConnection dbconn, boolean useXMLIndex,
                                      String qformat)
                                      throws Exception
    {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String docid = null;
      String fieldname = null;
      String fieldtype = null;
      String fielddata = null;
      String relation = null;

      if (qspec.containsExtendedSQL())
      {
        qspec.setUserName(user);
        qspec.setGroup(groups);
        Vector extendedFields = new Vector(qspec.getReturnFieldList());
        Vector results = new Vector();
        Iterator keylist = docListResult.getDocids();
        StringBuffer doclist = new StringBuffer();
        Vector parentidList = new Vector();
        Hashtable returnFieldValue = new Hashtable();
        while (keylist.hasNext())
        {
          String key = (String)keylist.next();
          doclist.append("'");
          doclist.append(key);
          doclist.append("',");
        }
        if (doclist.length() > 0)
        {
          Hashtable controlPairs = new Hashtable();
          doclist.deleteCharAt(doclist.length() - 1); //remove the last comma
          boolean tableHasRows = false;
        

           String extendedQuery =
               qspec.printExtendedSQL(doclist.toString(), useXMLIndex);
           logMetacat.info("DBQuery.addReturnfield - Extended query: " + extendedQuery);

           if(extendedQuery != null){
//        	   long extendedQueryStart = System.currentTimeMillis();
               pstmt = dbconn.prepareStatement(extendedQuery);
               //increase dbconnection usage count
               dbconn.increaseUsageCount(1);
               pstmt.execute();
               rs = pstmt.getResultSet();
               tableHasRows = rs.next();
               while (tableHasRows) {
                   ReturnFieldValue returnValue = new ReturnFieldValue();
                   docid = rs.getString(1).trim();
                   fieldname = rs.getString(2);
                   
                   if(qformat.toLowerCase().trim().equals("xml"))
                   {
                       byte[] b = rs.getBytes(3);
                       fielddata = new String(b, 0, b.length);
                   }
                   else
                   {
                       fielddata = rs.getString(3);
                   }
                   
                   //System.out.println("raw fielddata: " + fielddata);
                   fielddata = MetacatUtil.normalize(fielddata);
                   //System.out.println("normalized fielddata: " + fielddata);
                   String parentId = rs.getString(4);
                   fieldtype = rs.getString(5);
                   StringBuffer value = new StringBuffer();

                   //handle case when usexmlindex is true differently
                   //at one point merging the nodedata (for large text elements) was 
                   //deemed unnecessary - but now it is needed.  but not for attribute nodes
                   if (useXMLIndex || !containsKey(parentidList, parentId)) {
                	   //merge node data only for non-ATTRIBUTEs
                	   if (fieldtype != null && !fieldtype.equals("ATTRIBUTE")) {
	                	   //try merging the data
	                	   ReturnFieldValue existingRFV =
	                		   getArrayValue(parentidList, parentId);
	                	   if (existingRFV != null) {
	                		   fielddata = existingRFV.getFieldValue() + fielddata;
	                	   }
                	   }
                	   //System.out.println("fieldname: " + fieldname + " fielddata: " + fielddata);

                       value.append("<param name=\"");
                       value.append(fieldname);
                       value.append("\">");
                       value.append(fielddata);
                       value.append("</param>");
                       //set returnvalue
                       returnValue.setDocid(docid);
                       returnValue.setFieldValue(fielddata);
                       returnValue.setFieldType(fieldtype);
                       returnValue.setXMLFieldValue(value.toString());
                       // Store it in hastable
                       putInArray(parentidList, parentId, returnValue);
                   }
                   else {
                       
                       // need to merge nodedata if they have same parent id and
                       // node type is text
                       fielddata = (String) ( (ReturnFieldValue)
                                             getArrayValue(
                           parentidList, parentId)).getFieldValue()
                           + fielddata;
                       //System.out.println("fieldname: " + fieldname + " fielddata: " + fielddata);
                       value.append("<param name=\"");
                       value.append(fieldname);
                       value.append("\">");
                       value.append(fielddata);
                       value.append("</param>");
                       returnValue.setDocid(docid);
                       returnValue.setFieldValue(fielddata);
                       returnValue.setFieldType(fieldtype);
                       returnValue.setXMLFieldValue(value.toString());
                       // remove the old return value from paretnidList
                       parentidList.remove(parentId);
                       // store the new return value in parentidlit
                       putInArray(parentidList, parentId, returnValue);
                   }
                   tableHasRows = rs.next();
               } //while
               rs.close();
               pstmt.close();

               // put the merger node data info into doclistReult
               Enumeration xmlFieldValue = (getElements(parentidList)).
                   elements();
               while (xmlFieldValue.hasMoreElements()) {
                   ReturnFieldValue object =
                       (ReturnFieldValue) xmlFieldValue.nextElement();
                   docid = object.getDocid();
                   if (docListResult.containsDocid(docid)) {
                       String removedelement = (String) docListResult.
                           remove(docid);
                       docListResult.
                           addResultDocument(new ResultDocument(docid,
                               removedelement + object.getXMLFieldValue()));
                   }
                   else {
                       docListResult.addResultDocument(
                         new ResultDocument(docid, object.getXMLFieldValue()));
                   }
               } //while
//               double docListResultEnd = System.currentTimeMillis() / 1000;
//               logMetacat.warn(
//                   "Time to prepare ResultDocumentSet after"
//                   + " execute extended query: "
//                   + (docListResultEnd - extendedQueryEnd));
           }
       }//if doclist lenght is great than zero
     }//if has extended query

      return docListResult;
    }//addReturnfield

  
  /**
   * removes the <?xml version="1.0"?> tag from the beginning.  This takes a
   * string as a param instead of a hashtable.
   *
   * @param xmlquery a string representing a query.
   */
   private  String transformQuery(String xmlquery)
   {
     xmlquery = xmlquery.trim();
     int index = xmlquery.indexOf("?>");
     if (index != -1)
     {
       return xmlquery.substring(index + 2, xmlquery.length());
     }
     else
     {
       return xmlquery;
     }
   }
   
   /*
    * Method to store query string and result xml string into query result
    * cache. If the size alreay reache the limitation, the cache will be
    * cleared first, then store them.
    */
   private void storeQueryResultIntoCache(String query, String resultXML)
   {
	   synchronized (queryResultCache)
	   {
		   if (queryResultCache.size() >= QUERYRESULTCACHESIZE)
		   {
			   queryResultCache.clear();
		   }
		   queryResultCache.put(query, resultXML);
		   
	   }
   }
   
   /*
    * Method to get result xml string from query result cache. 
    * Note: the returned string can be null.
    */
   private String getResultXMLFromCache(String query)
   {
	   String resultSet = null;
	   synchronized (queryResultCache)
	   {
          try
          {
        	 logMetacat.info("DBQuery.getResultXMLFromCache - Get query from cache");
		     resultSet = (String)queryResultCache.get(query);
		   
          }
          catch (Exception e)
          {
        	  resultSet = null;
          }
		   
	   }
	   return resultSet;
   }
   
   /**
    * Method to clear the query result cache.
    */
   public static void clearQueryResultCache()
   {
	   synchronized (queryResultCache)
	   {
		   queryResultCache.clear();
	   }
   }


    /*
     * A method to search if Vector contains a particular key string
     */
    private boolean containsKey(Vector parentidList, String parentId)
    {

        Vector tempVector = null;

        for (int count = 0; count < parentidList.size(); count++) {
            tempVector = (Vector) parentidList.get(count);
            if (parentId.compareTo((String) tempVector.get(0)) == 0) { return true; }
        }
        return false;
    }
    
    /*
     * A method to put key and value in Vector
     */
    private void putInArray(Vector parentidList, String key,
            ReturnFieldValue value)
    {

        Vector tempVector = null;
        //only filter if the field type is NOT an attribute (say, for text)
        String fieldType = value.getFieldType();
        if (fieldType != null && !fieldType.equals("ATTRIBUTE")) {
        
	        for (int count = 0; count < parentidList.size(); count++) {
	            tempVector = (Vector) parentidList.get(count);
	
	            if (key.compareTo((String) tempVector.get(0)) == 0) {
	                tempVector.remove(1);
	                tempVector.add(1, value);
	                return;
	            }
	        }
        }

        tempVector = new Vector();
        tempVector.add(0, key);
        tempVector.add(1, value);
        parentidList.add(tempVector);
        return;
    }

    /*
     * A method to get value in Vector given a key
     */
    private ReturnFieldValue getArrayValue(Vector parentidList, String key)
    {

        Vector tempVector = null;

        for (int count = 0; count < parentidList.size(); count++) {
            tempVector = (Vector) parentidList.get(count);

            if (key.compareTo((String) tempVector.get(0)) == 0) { return (ReturnFieldValue) tempVector
                    .get(1); }
        }
        return null;
    }

    /*
     * A method to get enumeration of all values in Vector
     */
    private Vector getElements(Vector parentidList)
    {
        Vector enumVector = new Vector();
        Vector tempVector = null;

        for (int count = 0; count < parentidList.size(); count++) {
            tempVector = (Vector) parentidList.get(count);

            enumVector.add(tempVector.get(1));
        }
        return enumVector;
    }

  

    /*
     * A method to create a query to get owner's docid list
     */
    private String getOwnerQuery(String owner)
    {
        if (owner != null) {
            owner = owner.toLowerCase();
        }
        StringBuffer self = new StringBuffer();

        self.append("SELECT docid,docname,doctype,");
        self.append("date_created, date_updated, rev ");
        self.append("FROM xml_documents WHERE docid IN (");
        self.append("(");
        self.append("SELECT DISTINCT docid FROM xml_nodes WHERE \n");
        self.append("nodedata LIKE '%%%' ");
        self.append(") \n");
        self.append(") ");
        self.append(" AND (");
        self.append(" lower(user_owner) = '" + owner + "'");
        self.append(") ");
        return self.toString();
    }

    /**
     * format a structured query as an XML document that conforms to the
     * pathquery.dtd and is appropriate for submission to the DBQuery
     * structured query engine
     *
     * @param params The list of parameters that should be included in the
     *            query
     */
    public static String createSQuery(Hashtable params) throws PropertyNotFoundException
    {
        StringBuffer query = new StringBuffer();
        Enumeration elements;
        Enumeration keys;
        String filterDoctype = null;
        String casesensitive = null;
        String searchmode = null;
        Object nextkey;
        Object nextelement;
        //add the xml headers
        query.append("<?xml version=\"1.0\"?>\n");
        query.append("<pathquery version=\"1.2\">\n");



        if (params.containsKey("meta_file_id")) {
            query.append("<meta_file_id>");
            query.append(((String[]) params.get("meta_file_id"))[0]);
            query.append("</meta_file_id>");
        }

        if (params.containsKey("returndoctype")) {
            String[] returnDoctypes = ((String[]) params.get("returndoctype"));
            for (int i = 0; i < returnDoctypes.length; i++) {
                String doctype = (String) returnDoctypes[i];

                if (!doctype.equals("any") && !doctype.equals("ANY")
                        && !doctype.equals("")) {
                    query.append("<returndoctype>").append(doctype);
                    query.append("</returndoctype>");
                }
            }
        }

        if (params.containsKey("filterdoctype")) {
            String[] filterDoctypes = ((String[]) params.get("filterdoctype"));
            for (int i = 0; i < filterDoctypes.length; i++) {
                query.append("<filterdoctype>").append(filterDoctypes[i]);
                query.append("</filterdoctype>");
            }
        }

        if (params.containsKey("returnfield")) {
            String[] returnfield = ((String[]) params.get("returnfield"));
            for (int i = 0; i < returnfield.length; i++) {
                query.append("<returnfield>").append(returnfield[i]);
                query.append("</returnfield>");
            }
        }

        if (params.containsKey("owner")) {
            String[] owner = ((String[]) params.get("owner"));
            for (int i = 0; i < owner.length; i++) {
                query.append("<owner>").append(owner[i]);
                query.append("</owner>");
            }
        }

        if (params.containsKey("site")) {
            String[] site = ((String[]) params.get("site"));
            for (int i = 0; i < site.length; i++) {
                query.append("<site>").append(site[i]);
                query.append("</site>");
            }
        }

        //allows the dynamic switching of boolean operators
        if (params.containsKey("operator")) {
            query.append("<querygroup operator=\""
                    + ((String[]) params.get("operator"))[0] + "\">");
        } else { //the default operator is UNION
            query.append("<querygroup operator=\"UNION\">");
        }

        if (params.containsKey("casesensitive")) {
            casesensitive = ((String[]) params.get("casesensitive"))[0];
        } else {
            casesensitive = "false";
        }

        if (params.containsKey("searchmode")) {
            searchmode = ((String[]) params.get("searchmode"))[0];
        } else {
            searchmode = "contains";
        }

        //anyfield is a special case because it does a
        //free text search. It does not have a <pathexpr>
        //tag. This allows for a free text search within the structured
        //query. This is useful if the INTERSECT operator is used.
        if (params.containsKey("anyfield")) {
            String[] anyfield = ((String[]) params.get("anyfield"));
            //allow for more than one value for anyfield
            for (int i = 0; i < anyfield.length; i++) {
                if (anyfield[i] != null && !anyfield[i].equals("")) {
                    query.append("<queryterm casesensitive=\"" + casesensitive
                            + "\" " + "searchmode=\"" + searchmode
                            + "\"><value>" + anyfield[i]
                            + "</value></queryterm>");
                }
            }
        }

        //this while loop finds the rest of the parameters
        //and attempts to query for the field specified
        //by the parameter.
        elements = params.elements();
        keys = params.keys();
        while (keys.hasMoreElements() && elements.hasMoreElements()) {
            nextkey = keys.nextElement();
            nextelement = elements.nextElement();

            //make sure we aren't querying for any of these
            //parameters since the are already in the query
            //in one form or another.
            Vector ignoredParams = new Vector();
            ignoredParams.add("returndoctype");
            ignoredParams.add("filterdoctype");
            ignoredParams.add("action");
            ignoredParams.add("qformat");
            ignoredParams.add("anyfield");
            ignoredParams.add("returnfield");
            ignoredParams.add("owner");
            ignoredParams.add("site");
            ignoredParams.add("operator");
            ignoredParams.add("sessionid");
            ignoredParams.add("pagesize");
            ignoredParams.add("pagestart");
            ignoredParams.add("searchmode");

            // Also ignore parameters listed in the properties file
            // so that they can be passed through to stylesheets
            String paramsToIgnore = PropertyService
                    .getProperty("database.queryignoredparams");
            StringTokenizer st = new StringTokenizer(paramsToIgnore, ",");
            while (st.hasMoreTokens()) {
                ignoredParams.add(st.nextToken());
            }
            if (!ignoredParams.contains(nextkey.toString())) {
                //allow for more than value per field name
                for (int i = 0; i < ((String[]) nextelement).length; i++) {
                    if (!((String[]) nextelement)[i].equals("")) {
                        query.append("<queryterm casesensitive=\""
                                + casesensitive + "\" " + "searchmode=\""
                                + searchmode + "\">" + "<value>" +
                                //add the query value
                                ((String[]) nextelement)[i]
                                + "</value><pathexpr>" +
                                //add the path to query by
                                nextkey.toString() + "</pathexpr></queryterm>");
                    }
                }
            }
        }
        query.append("</querygroup></pathquery>");
        //append on the end of the xml and return the result as a string
        return query.toString();
    }

    /**
     * format a simple free-text value query as an XML document that conforms
     * to the pathquery.dtd and is appropriate for submission to the DBQuery
     * structured query engine
     *
     * @param value the text string to search for in the xml catalog
     * @param doctype the type of documents to include in the result set -- use
     *            "any" or "ANY" for unfiltered result sets
     */
    public static String createQuery(String value, String doctype)
    {
        StringBuffer xmlquery = new StringBuffer();
        xmlquery.append("<?xml version=\"1.0\"?>\n");
        xmlquery.append("<pathquery version=\"1.0\">");

        if (!doctype.equals("any") && !doctype.equals("ANY")) {
            xmlquery.append("<returndoctype>");
            xmlquery.append(doctype).append("</returndoctype>");
        }

        xmlquery.append("<querygroup operator=\"UNION\">");
        //chad added - 8/14
        //the if statement allows a query to gracefully handle a null
        //query. Without this if a nullpointerException is thrown.
        if (!value.equals("")) {
            xmlquery.append("<queryterm casesensitive=\"false\" ");
            xmlquery.append("searchmode=\"contains\">");
            xmlquery.append("<value>").append(value).append("</value>");
            xmlquery.append("</queryterm>");
        }
        xmlquery.append("</querygroup>");
        xmlquery.append("</pathquery>");

        return (xmlquery.toString());
    }

    /**
     * format a simple free-text value query as an XML document that conforms
     * to the pathquery.dtd and is appropriate for submission to the DBQuery
     * structured query engine
     *
     * @param value the text string to search for in the xml catalog
     */
    public static String createQuery(String value)
    {
        return createQuery(value, "any");
    }

    /**
     * Check for "READ" permission on @docid for @user and/or @group from DB
     * connection
     */
    private boolean hasPermission(String user, String[] groups, String docid)
            throws SQLException, Exception
    {
        // Check for READ permission on @docid for @user and/or @groups
        PermissionController controller = new PermissionController(docid);
        return controller.hasPermission(user, groups,
                AccessControlInterface.READSTRING);
    }

    /**
     * Get all docIds list for a data packadge
     *
     * @param dataPackageDocid, the string in docId field of xml_relation table
     */
    private Vector getCurrentDocidListForDataPackage(String dataPackageDocid)
    {
        DBConnection dbConn = null;
        int serialNumber = -1;
        Vector docIdList = new Vector();//return value
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String docIdInSubjectField = null;
        String docIdInObjectField = null;

        // Check the parameter
        if (dataPackageDocid == null || dataPackageDocid.equals("")) { return docIdList; }//if

        //the query stirng
        String query = "SELECT subject, object from xml_relation where docId = ?";
        try {
            dbConn = DBConnectionPool
                    .getDBConnection("DBQuery.getCurrentDocidListForDataPackage");
            serialNumber = dbConn.getCheckOutSerialNumber();
            pStmt = dbConn.prepareStatement(query);
            //bind the value to query
            pStmt.setString(1, dataPackageDocid);

            //excute the query
            pStmt.execute();
            //get the result set
            rs = pStmt.getResultSet();
            //process the result
            while (rs.next()) {
                //In order to get the whole docIds in a data packadge,
                //we need to put the docIds of subject and object field in
                // xml_relation
                //into the return vector
                docIdInSubjectField = rs.getString(1);//the result docId in
                                                      // subject field
                docIdInObjectField = rs.getString(2);//the result docId in
                                                     // object field

                //don't put the duplicate docId into the vector
                if (!docIdList.contains(docIdInSubjectField)) {
                    docIdList.add(docIdInSubjectField);
                }

                //don't put the duplicate docId into the vector
                if (!docIdList.contains(docIdInObjectField)) {
                    docIdList.add(docIdInObjectField);
                }
            }//while
            //close the pStmt
            pStmt.close();
        }//try
        catch (SQLException e) {
            logMetacat.error("DBQuery.getCurrentDocidListForDataPackage - Error in getDocidListForDataPackage: "
                    + e.getMessage());
        }//catch
        finally {
            try {
                pStmt.close();
            }//try
            catch (SQLException ee) {
                logMetacat.error("DBQuery.getCurrentDocidListForDataPackage - SQL Error: "
                                + ee.getMessage());
            }//catch
            finally {
                DBConnectionPool.returnDBConnection(dbConn, serialNumber);
            }//fianlly
        }//finally
        return docIdList;
    }//getCurrentDocidListForDataPackadge()

    /**
     * Get all docIds list for a data packadge
     *
     * @param dataPackageDocid, the string in docId field of xml_relation table
     */
    private Vector getOldVersionDocidListForDataPackage(String dataPackageDocidWithRev)
    {

        Vector docIdList = new Vector();//return value
        Vector tripleList = null;
        String xml = null;

        // Check the parameter
        if (dataPackageDocidWithRev == null || dataPackageDocidWithRev.equals("")) { return docIdList; }//if

        try {
            //initial a documentImpl object
            DocumentImpl packageDocument = new DocumentImpl(dataPackageDocidWithRev);
            //transfer to documentImpl object to string
            xml = packageDocument.toString();

            //create a tripcollection object
            TripleCollection tripleForPackage = new TripleCollection(
                    new StringReader(xml));
            //get the vetor of triples
            tripleList = tripleForPackage.getCollection();

            for (int i = 0; i < tripleList.size(); i++) {
                //put subject docid into docIdlist without duplicate
                if (!docIdList.contains(((Triple) tripleList.elementAt(i))
                        .getSubject())) {
                    //put subject docid into docIdlist
                    docIdList.add(((Triple) tripleList.get(i)).getSubject());
                }
                //put object docid into docIdlist without duplicate
                if (!docIdList.contains(((Triple) tripleList.elementAt(i))
                        .getObject())) {
                    docIdList.add(((Triple) (tripleList.get(i))).getObject());
                }
            }//for
        }//try
        catch (Exception e) {
            logMetacat.error("DBQuery.getCurrentDocidListForDataPackage - General error: "
                    + e.getMessage());
        }//catch

        // return result
        return docIdList;
    }//getDocidListForPackageInXMLRevisions()

    /**
     * Check if the docId is a data packadge id. If the id is a data packadage
     * id, it should be store in the docId fields in xml_relation table. So we
     * can use a query to get the entries which the docId equals the given
     * value. If the result is null. The docId is not a packadge id. Otherwise,
     * it is.
     *
     * @param docId, the id need to be checked
     */
    private boolean isDataPackageId(String docId)
    {
        boolean result = false;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = "SELECT docId from xml_relation where docId = ?";
        DBConnection dbConn = null;
        int serialNumber = -1;
        try {
            dbConn = DBConnectionPool
                    .getDBConnection("DBQuery.isDataPackageId");
            serialNumber = dbConn.getCheckOutSerialNumber();
            pStmt = dbConn.prepareStatement(query);
            //bind the value to query
            pStmt.setString(1, docId);
            //execute the query
            pStmt.execute();
            rs = pStmt.getResultSet();
            //process the result
            if (rs.next()) //There are some records for the id in docId fields
            {
                result = true;//It is a data packadge id
            }
            pStmt.close();
        }//try
        catch (SQLException e) {
            logMetacat.error("DBQuery.isDataPackageId - SQL Error: "
                    + e.getMessage());
        } finally {
            try {
                pStmt.close();
            }//try
            catch (SQLException ee) {
                logMetacat.error("DBQuery.isDataPackageId - SQL Error in isDataPackageId: "
                        + ee.getMessage());
            }//catch
            finally {
                DBConnectionPool.returnDBConnection(dbConn, serialNumber);
            }//finally
        }//finally
        return result;
    }//isDataPackageId()

    /**
     * Check if the user has the permission to export data package
     *
     * @param conn, the connection
     * @param docId, the id need to be checked
     * @param user, the name of user
     * @param groups, the user's group
     */
    private boolean hasPermissionToExportPackage(String docId, String user,
            String[] groups) throws Exception
    {
        //DocumentImpl doc=new DocumentImpl(conn,docId);
        return DocumentImpl.hasReadPermission(user, groups, docId);
    }

    /**
     * Get the current Rev for a docid in xml_documents table
     *
     * @param docId, the id need to get version numb If the return value is -5,
     *            means no value in rev field for this docid
     */
    private int getCurrentRevFromXMLDoumentsTable(String docId)
            throws SQLException
    {
        int rev = -5;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = "SELECT rev from xml_documents where docId = ?";
        DBConnection dbConn = null;
        int serialNumber = -1;
        try {
            dbConn = DBConnectionPool
                    .getDBConnection("DBQuery.getCurrentRevFromXMLDocumentsTable");
            serialNumber = dbConn.getCheckOutSerialNumber();
            pStmt = dbConn.prepareStatement(query);
            //bind the value to query
            pStmt.setString(1, docId);
            //execute the query
            pStmt.execute();
            rs = pStmt.getResultSet();
            //process the result
            if (rs.next()) //There are some records for rev
            {
                rev = rs.getInt(1);
                ;//It is the version for given docid
            } else {
                rev = -5;
            }

        }//try
        catch (SQLException e) {
            logMetacat.error("DBQuery.getCurrentRevFromXMLDoumentsTable - SQL Error: "
                            + e.getMessage());
            throw e;
        }//catch
        finally {
            try {
                pStmt.close();
            }//try
            catch (SQLException ee) {
                logMetacat.error(
                        "DBQuery.getCurrentRevFromXMLDoumentsTable - SQL Error: "
                                + ee.getMessage());
            }//catch
            finally {
                DBConnectionPool.returnDBConnection(dbConn, serialNumber);
            }//finally
        }//finally
        return rev;
    }//getCurrentRevFromXMLDoumentsTable

    /**
     * put a doc into a zip output stream
     *
     * @param docImpl, docmentImpl object which will be sent to zip output
     *            stream
     * @param zipOut, zip output stream which the docImpl will be put
     * @param packageZipEntry, the zip entry name for whole package
     */
    private void addDocToZipOutputStream(DocumentImpl docImpl,
            ZipOutputStream zipOut, String packageZipEntry)
            throws ClassNotFoundException, IOException, SQLException,
            McdbException, Exception
    {
        byte[] byteString = null;
        ZipEntry zEntry = null;

        byteString = docImpl.toString().getBytes();
        //use docId as the zip entry's name
        zEntry = new ZipEntry(packageZipEntry + "/metadata/"
                + docImpl.getDocID());
        zEntry.setSize(byteString.length);
        zipOut.putNextEntry(zEntry);
        zipOut.write(byteString, 0, byteString.length);
        zipOut.closeEntry();

    }//addDocToZipOutputStream()

    /**
     * Transfer a docid vetor to a documentImpl vector. The documentImpl vetor
     * only inlcudes current version. If a DocumentImple object couldn't find
     * for a docid, then the String of this docid was added to vetor rather
     * than DocumentImple object.
     *
     * @param docIdList, a vetor hold a docid list for a data package. In
     *            docid, there is not version number in it.
     */

    private Vector getCurrentAllDocumentImpl(Vector docIdList)
            throws McdbException, Exception
    {
        //Connection dbConn=null;
        Vector documentImplList = new Vector();
        int rev = 0;

        // Check the parameter
        if (docIdList.isEmpty()) { return documentImplList; }//if

        //for every docid in vector
        for (int i = 0; i < docIdList.size(); i++) {
            try {
                //get newest version for this docId
                rev = getCurrentRevFromXMLDoumentsTable((String) docIdList
                        .elementAt(i));

                // There is no record for this docId in xml_documents table
                if (rev == -5) {
                    // Rather than put DocumentImple object, put a String
                    // Object(docid)
                    // into the documentImplList
                    documentImplList.add((String) docIdList.elementAt(i));
                    // Skip other code
                    continue;
                }

                String docidPlusVersion = ((String) docIdList.elementAt(i))
                        + PropertyService.getProperty("document.accNumSeparator") + rev;

                //create new documentImpl object
                DocumentImpl documentImplObject = new DocumentImpl(
                        docidPlusVersion);
                //add them to vector
                documentImplList.add(documentImplObject);
            }//try
            catch (Exception e) {
                logMetacat.error("DBQuery.getCurrentAllDocumentImpl - General error: "
                        + e.getMessage());
                // continue the for loop
                continue;
            }
        }//for
        return documentImplList;
    }

    /**
     * Transfer a docid vetor to a documentImpl vector. If a DocumentImple
     * object couldn't find for a docid, then the String of this docid was
     * added to vetor rather than DocumentImple object.
     *
     * @param docIdList, a vetor hold a docid list for a data package. In
     *            docid, t here is version number in it.
     */
    private Vector getOldVersionAllDocumentImpl(Vector docIdList)
    {
        //Connection dbConn=null;
        Vector documentImplList = new Vector();
        String siteCode = null;
        String uniqueId = null;
        int rev = 0;

        // Check the parameter
        if (docIdList.isEmpty()) { return documentImplList; }//if

        //for every docid in vector
        for (int i = 0; i < docIdList.size(); i++) {

            String docidPlusVersion = (String) (docIdList.elementAt(i));

            try {
                //create new documentImpl object
                DocumentImpl documentImplObject = new DocumentImpl(
                        docidPlusVersion);
                //add them to vector
                documentImplList.add(documentImplObject);
            }//try
            catch (McdbDocNotFoundException notFoundE) {
                logMetacat.error("DBQuery.getOldVersionAllDocument - Error finding doc " 
                		+ docidPlusVersion + " : " + notFoundE.getMessage());
                // Rather than add a DocumentImple object into vetor, a String
                // object
                // - the doicd was added to the vector
                documentImplList.add(docidPlusVersion);
                // Continue the for loop
                continue;
            }//catch
            catch (Exception e) {
                logMetacat.error(
                        "DBQuery.getOldVersionAllDocument - General error: "
                                + e.getMessage());
                // Continue the for loop
                continue;
            }//catch

        }//for
        return documentImplList;
    }//getOldVersionAllDocumentImple

    /**
     * put a data file into a zip output stream
     *
     * @param docImpl, docmentImpl object which will be sent to zip output
     *            stream
     * @param zipOut, the zip output stream which the docImpl will be put
     * @param packageZipEntry, the zip entry name for whole package
     */
    private void addDataFileToZipOutputStream(DocumentImpl docImpl,
            ZipOutputStream zipOut, String packageZipEntry)
            throws ClassNotFoundException, IOException, SQLException,
            McdbException, Exception
    {
        byte[] byteString = null;
        ZipEntry zEntry = null;
        // this is data file; add file to zip
        String filePath = PropertyService.getProperty("application.datafilepath");
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        String fileName = filePath + docImpl.getDocID();
        zEntry = new ZipEntry(packageZipEntry + "/data/" + docImpl.getDocID());
        zipOut.putNextEntry(zEntry);
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(fileName);
            byte[] buf = new byte[4 * 1024]; // 4K buffer
            int b = fin.read(buf);
            while (b != -1) {
                zipOut.write(buf, 0, b);
                b = fin.read(buf);
            }//while
            zipOut.closeEntry();
        }//try
        catch (IOException ioe) {
            logMetacat.error("DBQuery.addDataFileToZipOutputStream - I/O error: "
                    + ioe.getMessage());
        }//catch
    }//addDataFileToZipOutputStream()

    /**
     * create a html summary for data package and put it into zip output stream
     *
     * @param docImplList, the documentImpl ojbects in data package
     * @param zipOut, the zip output stream which the html should be put
     * @param packageZipEntry, the zip entry name for whole package
     */
    private void addHtmlSummaryToZipOutputStream(Vector docImplList,
            ZipOutputStream zipOut, String packageZipEntry) throws Exception
    {
        StringBuffer htmlDoc = new StringBuffer();
        ZipEntry zEntry = null;
        byte[] byteString = null;
        InputStream source;
        DBTransform xmlToHtml;

        //create a DBTransform ojbect
        xmlToHtml = new DBTransform();
        //head of html
        htmlDoc.append("<html><head></head><body>");
        for (int i = 0; i < docImplList.size(); i++) {
            // If this String object, this means it is missed data file
            if ((((docImplList.elementAt(i)).getClass()).toString())
                    .equals("class java.lang.String")) {

                htmlDoc.append("<a href=\"");
                String dataFileid = (String) docImplList.elementAt(i);
                htmlDoc.append("./data/").append(dataFileid).append("\">");
                htmlDoc.append("Data File: ");
                htmlDoc.append(dataFileid).append("</a><br>");
                htmlDoc.append("<br><hr><br>");

            }//if
            else if ((((DocumentImpl) docImplList.elementAt(i)).getDoctype())
                    .compareTo("BIN") != 0) { //this is an xml file so we can
                                              // transform it.
                //transform each file individually then concatenate all of the
                //transformations together.

                //for metadata xml title
                htmlDoc.append("<h2>");
                htmlDoc.append(((DocumentImpl) docImplList.elementAt(i))
                        .getDocID());
                //htmlDoc.append(".");
                //htmlDoc.append(((DocumentImpl)docImplList.elementAt(i)).getRev());
                htmlDoc.append("</h2>");
                //do the actual transform
                StringWriter docString = new StringWriter();
                xmlToHtml.transformXMLDocument(((DocumentImpl) docImplList
                        .elementAt(i)).toString(), "-//NCEAS//eml-generic//EN",
                        "-//W3C//HTML//EN", "html", docString, null, null);
                htmlDoc.append(docString.toString());
                htmlDoc.append("<br><br><hr><br><br>");
            }//if
            else { //this is a data file so we should link to it in the html
                htmlDoc.append("<a href=\"");
                String dataFileid = ((DocumentImpl) docImplList.elementAt(i))
                        .getDocID();
                htmlDoc.append("./data/").append(dataFileid).append("\">");
                htmlDoc.append("Data File: ");
                htmlDoc.append(dataFileid).append("</a><br>");
                htmlDoc.append("<br><hr><br>");
            }//else
        }//for
        htmlDoc.append("</body></html>");
        byteString = htmlDoc.toString().getBytes();
        zEntry = new ZipEntry(packageZipEntry + "/metadata.html");
        zEntry.setSize(byteString.length);
        zipOut.putNextEntry(zEntry);
        zipOut.write(byteString, 0, byteString.length);
        zipOut.closeEntry();
        //dbConn.close();

    }//addHtmlSummaryToZipOutputStream

    /**
     * put a data packadge into a zip output stream
     *
     * @param docId, which the user want to put into zip output stream,it has version
     * @param out, a servletoutput stream which the zip output stream will be
     *            put
     * @param user, the username of the user
     * @param groups, the group of the user
     */
    public ZipOutputStream getZippedPackage(String docIdString,
            ServletOutputStream out, String user, String[] groups,
            String passWord) throws ClassNotFoundException, IOException,
            SQLException, McdbException, NumberFormatException, Exception
    {
        ZipOutputStream zOut = null;
        String elementDocid = null;
        DocumentImpl docImpls = null;
        //Connection dbConn = null;
        Vector docIdList = new Vector();
        Vector documentImplList = new Vector();
        Vector htmlDocumentImplList = new Vector();
        String packageId = null;
        String rootName = "package";//the package zip entry name

        String docId = null;
        int version = -5;
        // Docid without revision
        docId = DocumentUtil.getDocIdFromString(docIdString);
        // revision number
        version = DocumentUtil.getVersionFromString(docIdString);

        //check if the reqused docId is a data package id
        if (!isDataPackageId(docId)) {

            /*
             * Exception e = new Exception("The request the doc id "
             * +docIdString+ " is not a data package id");
             */

            //CB 1/6/03: if the requested docid is not a datapackage, we just
            // zip
            //up the single document and return the zip file.
            if (!hasPermissionToExportPackage(docId, user, groups)) {

                Exception e = new Exception("User " + user
                        + " does not have permission"
                        + " to export the data package " + docIdString);
                throw e;
            }

            docImpls = new DocumentImpl(docIdString);
            //checking if the user has the permission to read the documents
            if (DocumentImpl.hasReadPermission(user, groups, docImpls
                    .getDocID())) {
                zOut = new ZipOutputStream(out);
                //if the docImpls is metadata
                if ((docImpls.getDoctype()).compareTo("BIN") != 0) {
                    //add metadata into zip output stream
                    addDocToZipOutputStream(docImpls, zOut, rootName);
                }//if
                else {
                    //it is data file
                    addDataFileToZipOutputStream(docImpls, zOut, rootName);
                    htmlDocumentImplList.add(docImpls);
                }//else
            }//if

            zOut.finish(); //terminate the zip file
            return zOut;
        }
        // Check the permission of user
        else if (!hasPermissionToExportPackage(docId, user, groups)) {

            Exception e = new Exception("User " + user
                    + " does not have permission"
                    + " to export the data package " + docIdString);
            throw e;
        } else //it is a packadge id
        {
            //store the package id
            packageId = docId;
            //get current version in database
            int currentVersion = getCurrentRevFromXMLDoumentsTable(packageId);
            //If it is for current version (-1 means user didn't specify
            // revision)
            if ((version == -1) || version == currentVersion) {
                //get current version number
                version = currentVersion;
                //get package zip entry name
                //it should be docId.revsion.package
                rootName = packageId + PropertyService.getProperty("document.accNumSeparator")
                        + version + PropertyService.getProperty("document.accNumSeparator")
                        + "package";
                //get the whole id list for data packadge
                docIdList = getCurrentDocidListForDataPackage(packageId);
                //get the whole documentImple object
                documentImplList = getCurrentAllDocumentImpl(docIdList);

            }//if
            else if (version > currentVersion || version < -1) {
                throw new Exception("The user specified docid: " + docId + "."
                        + version + " doesn't exist");
            }//else if
            else //for an old version
            {

                rootName = docIdString
                        + PropertyService.getProperty("document.accNumSeparator") + "package";
                //get the whole id list for data packadge
                docIdList = getOldVersionDocidListForDataPackage(docIdString);

                //get the whole documentImple object
                documentImplList = getOldVersionAllDocumentImpl(docIdList);
            }//else

            // Make sure documentImplist is not empty
            if (documentImplList.isEmpty()) { throw new Exception(
                    "Couldn't find component for data package: " + packageId); }//if

            zOut = new ZipOutputStream(out);
            //put every element into zip output stream
            for (int i = 0; i < documentImplList.size(); i++) {
                // if the object in the vetor is String, this means we couldn't
                // find
                // the document locally, we need find it remote
                if ((((documentImplList.elementAt(i)).getClass()).toString())
                        .equals("class java.lang.String")) {
                    // Get String object from vetor
                    String documentId = (String) documentImplList.elementAt(i);
                    logMetacat.info("DBQuery.getZippedPackage - docid: " + documentId);
                    // Get doicd without revision
                    String docidWithoutRevision = 
                    	DocumentUtil.getDocIdFromString(documentId);
                    logMetacat.info("DBQuery.getZippedPackage - docidWithoutRevsion: "
                            + docidWithoutRevision);
                    // Get revision
                    String revision = 
                    	DocumentUtil.getRevisionStringFromString(documentId);
                    logMetacat.info("DBQuery.getZippedPackage - revision from docIdentifier: "
                            + revision);
                    // Zip entry string
                    String zipEntryPath = rootName + "/data/";
                    // Create a RemoteDocument object
                    RemoteDocument remoteDoc = new RemoteDocument(
                            docidWithoutRevision, revision, user, passWord,
                            zipEntryPath);
                    // Here we only read data file from remote metacat
                    String docType = remoteDoc.getDocType();
                    if (docType != null) {
                        if (docType.equals("BIN")) {
                            // Put remote document to zip output
                            remoteDoc.readDocumentFromRemoteServerByZip(zOut);
                            // Add String object to htmlDocumentImplList
                            String elementInHtmlList = remoteDoc
                                    .getDocIdWithoutRevsion()
                                    + PropertyService.getProperty("document.accNumSeparator")
                                    + remoteDoc.getRevision();
                            htmlDocumentImplList.add(elementInHtmlList);
                        }//if
                    }//if

                }//if
                else {
                    //create a docmentImpls object (represent xml doc) base on
                    // the docId
                    docImpls = (DocumentImpl) documentImplList.elementAt(i);
                    //checking if the user has the permission to read the
                    // documents
                    if (DocumentImpl.hasReadPermission(user, groups, docImpls
                            .getDocID())) {
                        //if the docImpls is metadata
                        if ((docImpls.getDoctype()).compareTo("BIN") != 0) {
                            //add metadata into zip output stream
                            addDocToZipOutputStream(docImpls, zOut, rootName);
                            //add the documentImpl into the vetor which will
                            // be used in html
                            htmlDocumentImplList.add(docImpls);

                        }//if
                        else {
                            //it is data file
                            addDataFileToZipOutputStream(docImpls, zOut,
                                    rootName);
                            htmlDocumentImplList.add(docImpls);
                        }//else
                    }//if
                }//else
            }//for

            //add html summary file
            addHtmlSummaryToZipOutputStream(htmlDocumentImplList, zOut,
                    rootName);
            zOut.finish(); //terminate the zip file
            //dbConn.close();
            return zOut;
        }//else
    }//getZippedPackage()

    private class ReturnFieldValue
    {

        private String docid = null; //return field value for this docid

        private String fieldValue = null;

        private String xmlFieldValue = null; //return field value in xml
                                             // format
        private String fieldType = null; //ATTRIBUTE, TEXT...

        public void setDocid(String myDocid)
        {
            docid = myDocid;
        }

        public String getDocid()
        {
            return docid;
        }

        public void setFieldValue(String myValue)
        {
            fieldValue = myValue;
        }

        public String getFieldValue()
        {
            return fieldValue;
        }

        public void setXMLFieldValue(String xml)
        {
            xmlFieldValue = xml;
        }

        public String getXMLFieldValue()
        {
            return xmlFieldValue;
        }
        
        public void setFieldType(String myType)
        {
            fieldType = myType;
        }

        public String getFieldType()
        {
            return fieldType;
        }

    }
    
    /**
     * a class to store one result document consisting of a docid and a document
     */
    private class ResultDocument
    {
      public String docid;
      public String document;
      
      public ResultDocument(String docid, String document)
      {
        this.docid = docid;
        this.document = document;
      }
    }
    
    /**
     * a private class to handle a set of resultDocuments
     */
    private class ResultDocumentSet
    {
      private Vector docids;
      private Vector documents;
      
      public ResultDocumentSet()
      {
        docids = new Vector();
        documents = new Vector();
      }
      
      /**
       * adds a result document to the set
       */
      public void addResultDocument(ResultDocument rd)
      {
        if(rd.docid == null)
          return;
        if(rd.document == null)
          rd.document = "";
       
           docids.addElement(rd.docid);
           documents.addElement(rd.document);
        
      }
      
      /**
       * gets an iterator of docids
       */
      public Iterator getDocids()
      {
        return docids.iterator();
      }
      
      /**
       * gets an iterator of documents
       */
      public Iterator getDocuments()
      {
        return documents.iterator();
      }
      
      /**
       * returns the size of the set
       */
      public int size()
      {
        return docids.size();
      }
      
      /**
       * tests to see if this set contains the given docid
       */
      private boolean containsDocid(String docid)
      {
        for(int i=0; i<docids.size(); i++)
        {
          String docid0 = (String)docids.elementAt(i);
          if(docid0.trim().equals(docid.trim()))
          {
            return true;
          }
        }
        return false;
      }
      
      /**
       * removes the element with the given docid
       */
      public String remove(String docid)
      {
        for(int i=0; i<docids.size(); i++)
        {
          String docid0 = (String)docids.elementAt(i);
          if(docid0.trim().equals(docid.trim()))
          {
            String returnDoc = (String)documents.elementAt(i);
            documents.remove(i);
            docids.remove(i);
            return returnDoc;
          }
        }
        return null;
      }
      
      /**
       * add a result document
       */
      public void put(ResultDocument rd)
      {
        addResultDocument(rd);
      }
      
      /**
       * add a result document by components
       */
      public void put(String docid, String document)
      {
        addResultDocument(new ResultDocument(docid, document));
      }
      
      /**
       * get the document part of the result document by docid
       */
      public Object get(String docid)
      {
        for(int i=0; i<docids.size(); i++)
        {
          String docid0 = (String)docids.elementAt(i);
          if(docid0.trim().equals(docid.trim()))
          {
            return documents.elementAt(i);
          }
        }
        return null;
      }
      
      /**
       * get the document part of the result document by an object
       */
      public Object get(Object o)
      {
        return get((String)o);
      }
      
      /**
       * get an entire result document by index number
       */
      public ResultDocument get(int index)
      {
        return new ResultDocument((String)docids.elementAt(index), 
          (String)documents.elementAt(index));
      }
      
      /**
       * return a string representation of this object
       */
      public String toString()
      {
        String s = "";
        for(int i=0; i<docids.size(); i++)
        {
          s += (String)docids.elementAt(i) + "\n";
        }
        return s;
      }
      /*
       * Set a new document value for a given docid
       */
      public void set(String docid, String document)
      {
    	   for(int i=0; i<docids.size(); i++)
           {
             String docid0 = (String)docids.elementAt(i);
             if(docid0.trim().equals(docid.trim()))
             {
                 documents.set(i, document);
             }
           }
           
      }
    }
}
