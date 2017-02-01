/**
 *  '$RCSfile$'
 *  Copyright: 2004 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *
 *   '$Author: daigle $'
 *     '$Date: 2009-08-05 07:32:58 +1000 (Wed, 05 Aug 2009) $'
 * '$Revision: 5015 $'
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import edu.ucsb.nceas.metacat.database.DBConnection;
import edu.ucsb.nceas.metacat.database.DBConnectionPool;

/**
 * EventLog is used to intialize and store a log of events that occur in an
 * application. The events are registered with the logger as they occur, but
 * EventLog writes them to permenant storage when it is most convenient or
 * efficient. EventLog is a Singleton as there should always be only one object
 * for these logging events.
 * 
 * TODO: Logging to the database needn't be synchronous with the event.  
 * Instead, a separate thread can be launched that periodically sleeps and only
 * wakes periodically to see if metacat is idle.  The log event can be cached
 * and inserted later when the thread wakes and finds metacat idle.
 * 
 * TODO: Write a function that archives a part of the log table to an 
 * external text file so that the log table doesn't get to big.  This 
 * function should be able to be called manually or on a schedule. 
 * 
 * TODO: Write an access function that returns an XML report for a
 * specific subset of events.  Users should be able to query on
 * principal, docid/rev, date, event, and possibly other fields.
 * 
 * @author jones
 */
public class EventLog
{
    /**
     * The single instance of the event log that is always returned.
     */
    private static EventLog self = null;
    private Logger logMetacat = Logger.getLogger(EventLog.class);

    /**
     * A private constructor that initializes the class when getInstance() is
     * called.
     */
    private EventLog()
    {
    }

    /**
     * Return the single instance of the event log after initializing it if it
     * wasn't previously initialized.
     * 
     * @return the single EventLog instance
     */
    public static EventLog getInstance()
    {
        if (self == null) {
            self = new EventLog();
        }
        return self;
    }

    /**
     * Log an event of interest to the application. The information logged can
     * include basic identification information about the principal or computer
     * that initiated the event.
     * 
     * @param ipAddress the internet protocol address for the event
	 * @param principal the principal for the event (a username, etc)
	 * @param docid the identifier of the document to which the event applies
	 * @param event the string code for the event
     */
    public void log(String ipAddress, String principal, String docid,
			String event)
    {
        EventLogData logData = new EventLogData(ipAddress, principal, docid,
                event);
        insertLogEntry(logData);
    }
    
    /**
     * Insert a single log event record to the database.
     * 
     * @param logData the data to be logged when an event occurs
     */
    private void insertLogEntry(EventLogData logData)
    {
        String insertString = "insert into access_log"
                + "(ip_address, principal, docid, "
                + "event, date_logged) "
                + "values ("
                + "'" + logData.getIpAddress() + "', " 
                + "'" + logData.getPrincipal() + "', "
                + "'" + logData.getDocid() + "', "
                + "'" + logData.getEvent() + "', "
                + " ? " + ")"; 

        DBConnection dbConn = null;
        int serialNumber = -1;
        try {
            // Get a database connection from the pool
            dbConn = DBConnectionPool.getDBConnection("EventLog.insertLogEntry");
            serialNumber = dbConn.getCheckOutSerialNumber();
            
            // Execute the insert statement
            PreparedStatement stmt = dbConn.prepareStatement(insertString);
            stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
        	logMetacat.error("Error while logging event to database: " 
                    + e.getMessage());
        } finally {
            // Return database connection to the pool
            DBConnectionPool.returnDBConnection(dbConn, serialNumber);
        }
    }
    
    /**
     * Get a report of the log events that match a set of filters.  The
     * filter parameters can be null; log records are subset based on
     * non-null filter parameters.
     * 
     * @param ipAddress the internet protocol address for the event
	 * @param principal the principal for the event (a username, etc)
	 * @param docid the identifier of the document to which the event applies
	 * @param event the string code for the event
	 * @param startDate beginning of date range for query
	 * @param endDate end of date range for query
	 * @return an XML-formatted report of the access log entries
     */
    public String getReport(String[] ipAddress, String[] principal, String[] docid,
            String[] event, Timestamp startDate, Timestamp endDate)
    {
        StringBuffer resultDoc = new StringBuffer();
        StringBuffer query = new StringBuffer();
        query.append("select entryid, ip_address, principal, docid, "
            + "event, date_logged from access_log");
//                        + ""
//                        + "event, date_logged) " + "values (" + "'"
//                        + logData.getIpAddress() + "', " + "'"
//                        + logData.getPrincipal() + "', " + "'"
//                        + logData.getDocid() + "', " + "'" + logData.getEvent()
//                        + "', " + " ? " + ")";
        if (ipAddress != null || principal != null || docid != null
                        || event != null || startDate != null || endDate != null) {
            query.append(" where ");
        }
        boolean clauseAdded = false;
        int startIndex = 0;
        int endIndex = 0;
        
        if (ipAddress != null) {
            query.append(generateSqlClause(clauseAdded, "ip_address", ipAddress));
            clauseAdded = true;
        }
        if (principal != null) {
            query.append(generateSqlClause(clauseAdded, "principal", principal));
            clauseAdded = true;
        }
        if (docid != null) {
            query.append(generateSqlClause(clauseAdded, "docid", docid));
            clauseAdded = true;
        }
        if (event != null) {
            query.append(generateSqlClause(clauseAdded, "event", event));
            clauseAdded = true;
        }
        if (startDate != null) {
            if (clauseAdded) {
                query.append(" and ");
            }
            query.append("date_logged > ?");
            clauseAdded = true;
            startIndex++;
        }
        if (endDate != null) {
            if (clauseAdded) {
                query.append(" and ");
            }
            query.append("date_logged < ?");
            clauseAdded = true;
            endIndex = startIndex + 1;
        }
        DBConnection dbConn = null;
        int serialNumber = -1;
        try {
            // Get a database connection from the pool
            dbConn = DBConnectionPool.getDBConnection("EventLog.getReport");
            serialNumber = dbConn.getCheckOutSerialNumber();

            // Execute the query statement
            PreparedStatement stmt = dbConn.prepareStatement(query.toString());
            if (startIndex > 0) {
                stmt.setTimestamp(startIndex, startDate); 
            }
            if (endIndex > 0) {
                stmt.setTimestamp(endIndex, endDate);
            }
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            //process the result and return it as an XML document
            resultDoc.append("<?xml version=\"1.0\"?>\n");
            resultDoc.append("<log>\n");
            while (rs.next()) {
                resultDoc.append(generateXmlRecord(rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4), 
                                rs.getString(5), rs.getTimestamp(6)));
            }
            resultDoc.append("</log>");
            stmt.close();
        } catch (SQLException e) {
        	logMetacat.info("Error while logging event to database: "
                            + e.getMessage());
        } finally {
            // Return database connection to the pool
            DBConnectionPool.returnDBConnection(dbConn, serialNumber);
        }
        return resultDoc.toString();
    }
    
    /**
     * Utility method to help build a SQL query from an array of values.  For each
     * value in the array an 'OR' clause is constructed.
     * 
     * @param addOperator a flag indicating whether to add an 'AND' operator 
     *                    to the clause
     * @param column the name of the column to filter against
     * @param values the values to match in the SQL query
     * @return a String representation of the SQL query clause
     */
    private String generateSqlClause(boolean addOperator, String column, 
            String[] values)
    {
        StringBuffer clause = new StringBuffer();
        if (addOperator) {
            clause.append(" and ");
        }
        clause.append("(");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                clause.append(" or ");
            }
            clause.append(column);
            clause.append(" like '");
            clause.append(values[i]);
            clause.append("'");
        }
        clause.append(")");
        return clause.toString();
    }
    
    /**
     * Format each returned log record as an XML structure.
     * 
     * @param entryId the identifier of the log entry
     * @param ipAddress the internet protocol address for the event
	 * @param principal the principal for the event (a username, etc)
	 * @param docid the identifier of the document to which the event applies
	 * @param event the string code for the event
     * @param dateLogged the date on which the event occurred
     * @return String containing the formatted XML
     */
    private String generateXmlRecord(String entryId, String ipAddress, 
            String principal, String docid, String event, Timestamp dateLogged)
    {
        StringBuffer rec = new StringBuffer();
        rec.append("<logEntry>");
        rec.append(generateXmlElement("entryid", entryId));
        rec.append(generateXmlElement("ipAddress", ipAddress));
        rec.append(generateXmlElement("principal", principal));
        rec.append(generateXmlElement("docid", docid));
        rec.append(generateXmlElement("event", event));
        rec.append(generateXmlElement("dateLogged", dateLogged.toString()));
        rec.append("</logEntry>\n");

        return rec.toString();
    }
    
    /**
     * Return an XML formatted element for a given name/value pair.
     * 
     * @param name the name of the xml element
     * @param value the content of the xml element
     * @return the formatted XML element as a String
     */
    private String generateXmlElement(String name, String value)
    {
        return "<" + name + ">" + value + "</" + name + ">";
    }
}
