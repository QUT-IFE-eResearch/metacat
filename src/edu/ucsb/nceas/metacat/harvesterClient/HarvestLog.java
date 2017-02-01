/**
 *  '$RCSfile$'
 *  Copyright: 2004 University of New Mexico and the 
 *                  Regents of the University of California
 *
 *   '$Author: daigle $'
 *     '$Date: 2009-08-25 07:34:17 +1000 (Tue, 25 Aug 2009) $'
 * '$Revision: 5030 $'
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package edu.ucsb.nceas.metacat.harvesterClient;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

/**
 * Manages log entries to be inserted to the HARVEST_LOG table.
 *
 * @author  costa
 */
public class HarvestLog {

  private Connection conn;    
  private Harvester harvester;              // The parent Harvester object
  private int harvestLogID;
  private Date harvestDate;
  private int status;
  private final String marker =
"*****************************************************************************";
  private String message;
  private String harvestOperationCode;
  private int siteScheduleID;
  private String explanation;
  private String harvestOperationCodeLevel;
  private String timestamp;
  private HarvestDetailLog harvestDetailLog;  // Associated detail log, if any
    

  /** 
   * Creates a new instance of HarvestLog. This constructor is used when
   * creating log entries that do not involve an error on a harvest document.
   * (For that type of log entry, use the alternate constructor below.)
   *
   * @param  harvester       the parent Harvester object
   * @param  conn            the database connection
   * @param  harvestLogID    the primary key in the HARVEST_LOG table
   * @param  harvestDate     the date of this harvest
   * @param  status          the status of the harvest operation
   * @param  message         the message text of the harvest operation
   * @param  harvestOperationCode  the harvest operation code
   * @param  siteScheduleID  the siteScheduleID for which this operation was
   *                         performed. 0 indicates that the operation did not
   *                         involve a particular harvest site.
   */
  public HarvestLog(Harvester  harvester,
                    Connection conn,
                    int        harvestLogID,
                    Date       harvestDate,
                    int        status,
                    String     message, 
                    String     harvestOperationCode,
                    int        siteScheduleID
                   ) {
    Date now = new Date();
    timestamp = now.toString();

    this.harvester = harvester;
    this.conn = conn;
    this.harvestLogID = harvestLogID;
    this.harvestDate = harvestDate;
    this.status = status;
    this.message = message;
    this.harvestOperationCode = harvestOperationCode;
    this.siteScheduleID = siteScheduleID;
    
    harvestOperationCodeLevel = 
            getHarvestOperationCodeLevel(harvestOperationCode);
    explanation = getExplanation(harvestOperationCode);
    dbInsertHarvestLogEntry();   // Insert this entry to the HARVEST_LOG table
  }
    

  /** 
   * Creates a new instance of HarvestLog and inserts this entry to the
   * HARVEST_LOG table. This version of the constructor also instantiates a 
   * HarvestDetailLog object and inserts it to the HARVEST_DETAIL_LOG table.
   *
   * @param  harvester       the parent Harvester object
   * @param  conn            the database connection
   * @param  harvestLogID    the primary key in the HARVEST_LOG table
   * @param  detailLogID     the primary key in the HARVEST_DETAIL_LOG table
   * @param  harvestDate     the date of this harvest
   * @param  status          the status of the harvest operation
   * @param  message         the message text of the harvest operation
   * @param  harvestOperationCode  the harvest operation code
   * @param  siteScheduleID  the siteScheduleID for which this operation was
   *                         performed. 0 indicates that the operation did not
   *                         involve a particular harvest site.
   * @param  harvestDocument the HarvestDocument involved in this operation
   * @param  errorMessage    the error message generated by this operation
   */
  public HarvestLog(Harvester  harvester,
                    Connection conn,
                    int        harvestLogID,
                    int        detailLogID,
                    Date       harvestDate,
                    int        status,
                    String     message, 
                    String     harvestOperationCode,
                    int        siteScheduleID,
                    HarvestDocument harvestDocument,
                    String     errorMessage
                   ) {
    Date now = new Date();
    timestamp = now.toString();

    this.harvester = harvester;
    this.conn = conn;
    this.harvestLogID = harvestLogID;
    this.harvestDate = harvestDate;
    this.status = status;
    this.message = message;
    this.harvestOperationCode = harvestOperationCode;
    this.siteScheduleID = siteScheduleID;
    this.harvestDetailLog = new HarvestDetailLog(harvester, conn, detailLogID,
                                                 harvestLogID, harvestDocument,
                                                 errorMessage);
    harvestOperationCodeLevel = 
            getHarvestOperationCodeLevel(harvestOperationCode);
    explanation = getExplanation(harvestOperationCode);
    dbInsertHarvestLogEntry();               // Insert to the HARVEST_LOG table
    harvestDetailLog.dbInsertHarvestDetailLogEntry(); //  HARVEST_DETAIL_LOG
  }
    

  /**
   * Inserts a new entry into the HARVEST_LOG table, based on the contents of
   * this HarvestLog object. Not yet implemented.
   */
  void dbInsertHarvestLogEntry() {
    String dequotedMessage = harvester.dequoteText(message);
    String insertString;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    Statement stmt;

    insertString = "INSERT INTO HARVEST_LOG " +
                   "(HARVEST_LOG_ID, HARVEST_DATE, STATUS, MESSAGE," +
                   " HARVEST_OPERATION_CODE, SITE_SCHEDULE_ID) " +
                   "values(" +
                   harvestLogID + ", " +
                   "'" + simpleDateFormat.format(harvestDate) + "', " +
                   status + ", " +
                   "'" + timestamp + ": " + dequotedMessage + "', " +
                   "'" + harvestOperationCode + "', " +
                   siteScheduleID +
                   ")";
    try {
      stmt = conn.createStatement();
      stmt.executeUpdate(insertString);
      stmt.close();
    }
    catch(SQLException e) {
      System.out.println("SQLException: " + e.getMessage());
    }
  }
  

  /**
   * Maps each code level to an integer value.
   * 
   * @param codeLevel        the code level: "error", "warning", "notice",
   *                         "info", or "debug"
   * @return codeLevelValue  the corresponding code level value
   */
  public int getCodeLevelValue(String codeLevel) {
    int codeLevelValue = 0;
    
    if (codeLevel.equalsIgnoreCase("error")) {
      codeLevelValue = 1;
    }
    else if (codeLevel.equalsIgnoreCase("warning")) {
      codeLevelValue = 2;
    }
    else if (codeLevel.equalsIgnoreCase("notice")) {
      codeLevelValue = 3;
    }
    else if (codeLevel.equalsIgnoreCase("info")) {
      codeLevelValue = 4;
    }
    else if (codeLevel.equalsIgnoreCase("debug")) {
      codeLevelValue = 5;
    }
    
    return codeLevelValue;
  }
  

  /**
   * Returns an explanation string based on the value of a
   * harvestOperationCode string. The explanation string is a description
   * of the harvest operation specified by the harvestOperationCode.
   * 
   * @param  harvestOperationCode  string value of the harvest operation code
   * @return the explanation for this harvest operation, a String
   */
  public String getExplanation(String harvestOperationCode) {
    String explanation;
    String fieldName = "EXPLANATION";
    
    explanation = getHarvestOperation(fieldName, harvestOperationCode);
        
    return explanation;
  }
  

  /**
   * Returns either an explanation string or a harvest operation code level 
   * string based on the value of a harvest operation code property. The
   * explanation and the code level are stored as comma-separated strings in
   * the metacat.properties file. For example, the HarvesterStartup
   * harvest operation code stores its explanation and code level like so:
   * 
   *            HarvesterStartup=Harvester start up,Info
   *
   * @param  fieldName  the field name to match, e.g. "EXPLANATION"
   * @param  harvestOperationCode  string value of the harvest operation code
   * @return the explanation string or the harvestOperationCodeLevel string
   */
  String getHarvestOperation(String fieldName, String harvestOperationCode) {
    String explanation = "No explanation available";
    String harvestOperationCodeLevel = "debug";
    String propertyValue = null;
    String returnString = "";
    StringTokenizer stringTokenizer;
    
    try {
		propertyValue = PropertyService.getProperty(harvestOperationCode);
	} catch (PropertyNotFoundException pnfe) {
		System.out.println("Error trying to get property: " + harvestOperationCode
					+ " : " + pnfe.getMessage());
	}
	stringTokenizer = new StringTokenizer(propertyValue, ",");
    
    explanation = (String) stringTokenizer.nextElement();
    harvestOperationCodeLevel = (String) stringTokenizer.nextElement();
                                           
    if (fieldName.equals("EXPLANATION")) {
      returnString = explanation;
    }
    else if (fieldName.equals("HARVEST_OPERATION_CODE_LEVEL")) {
      returnString = harvestOperationCodeLevel;
    }
    
    return returnString;
  }
  

  /**
   * Returns a code level string based on a harvestOperationCode string.
   * The code level string is one of a set of possible code levels:
   * "error", "warning", "notice", "info", or "debug".
   * 
   * @param  harvestOperationCode  string value of the harvest operation code
   * @return the code level value, a String, one of the following:
   *         "error", "warning", "notice", "info", or "debug"
   */
  public String getHarvestOperationCodeLevel(String harvestOperationCode) {
    String harvestOperationCodeLevel;
    String fieldName = "HARVEST_OPERATION_CODE_LEVEL";
    
    harvestOperationCodeLevel = 
            getHarvestOperation(fieldName, harvestOperationCode);
        
    return harvestOperationCodeLevel;
  }
  

  /**
   * Access function for the siteScheduleID field.
   * 
   * @return  siteScheduleID, an int. If 0, indicates that this log entry does
   *          not pertain to a particular site.
   */
  int getSiteScheduleID() {
    return siteScheduleID;
  }
  

  /**
   * Determines whether this log entry had an error status.
   * 
   * @return  isError  true if this log entry had an error status, else false 
   */
  boolean isErrorEntry () {
    boolean isError;
    
    isError = (status != 0);
    
    return isError;
  }


  /**
   * Prints the contents of this HarvestLog object. Used in generating reports.
   * 
   * @param out        the PrintStream to write to
   * @param maxLevel  the maximum code level to output. If this log entry has a
   *                  higher code level than the maxLevel, no output
   *                  is issued. For example, if the maxLevel is "error"
   *                  (level 1), then anything lower ("warning", "notice", etc.)
   *                  will not generate any output.
   */
  public void printOutput(PrintStream out, String maxLevel) {
    int codeLevelValue = getCodeLevelValue(harvestOperationCodeLevel);
    int maxLevelValue = getCodeLevelValue(maxLevel);
    
    if (codeLevelValue <= maxLevelValue) {    
      out.println("");
      out.println(marker);
      out.println("*");
      out.println("* harvestLogID:         " + harvestLogID);
      out.println("* harvestDate:          " + harvestDate);
      out.println("* status:               " + status);
      out.println("* message:              " + message);
      out.println("* harvestOperationCode: " + harvestOperationCode);
      out.println("* description:          " + explanation);

      if (harvestOperationCode.equals("harvester.GetHarvestListSuccess") ||
        harvestOperationCode.equals("harvester.GetHarvestListError")) {
        if (siteScheduleID != 0) {
          harvester.printHarvestSiteSchedule(out, siteScheduleID);
        }
      }
    
      if (harvestDetailLog != null) {
        harvestDetailLog.printOutput(out);
      }

      out.println("*");
      out.println(marker);
    }
  }

}
