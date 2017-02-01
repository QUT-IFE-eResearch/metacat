/**
 *  '$RCSfile$'
 *    Purpose: A Class for upgrading the database to version 1.5
 *  Copyright: 2000 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Saurabh Garg
 *
 *   '$Author: daigle $'
 *     '$Date: 2009-08-25 07:37:27 +1000 (Tue, 25 Aug 2009) $'
 * '$Revision: 5031 $'
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


import java.io.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;

import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

public class upgrade_db_to_1_5{

    static String database = null;
    static String url = null;
    static String user = null;
    static String password = null;
    static {
    	try {
    	    database = PropertyService.getProperty("database.type");
    	    url = PropertyService.getProperty("database.connectionURI");
    	    user = PropertyService.getProperty("database.user");
    	    password = PropertyService.getProperty("database.password");
    	} catch (PropertyNotFoundException pnfe) {
            System.err.println("Error in upgrade_db_to_1_5 static block:"
                    + pnfe.getMessage());
            pnfe.printStackTrace();
    	}
     }

    public static void upgrade_xml_nodes(Connection sqlca) throws SQLException{
        Statement sqlStatement = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        // Delete old nodedatanumerical column from xml_nodes if one exsists
        try {
            System.out.println(
                "Deleting old nodedatanumerical column from xml_nodes...");

            pstmt = sqlca.prepareStatement(
                "ALTER TABLE xml_nodes DROP COLUMN nodedatanumerical");
            pstmt.execute();
            pstmt.close();

            System.out.println("Done.");
        } catch (Exception e) {
            System.out.println(" column not found.");
        }

        // Create nodedatanumerical column in xml_nodes
        System.out.println(
            "Creating new nodedatanumerical column in xml_nodes...");

        if (database.equals("oracle")) {
            pstmt = sqlca.prepareStatement(
                "ALTER TABLE xml_nodes ADD nodedatanumerical NUMBER");
        }
        else {
            pstmt = sqlca.prepareStatement(
                "ALTER TABLE xml_nodes ADD nodedatanumerical FLOAT8");
        }
        pstmt.execute();
        pstmt.close();

        System.out.println("Done.");

        // Copy numerical values from nodedata to
        // nodedatanumerical column in xml_nodes
        System.out.println(
            "Please be patient as the next upgrade step can be extremely time consuming.");
        System.out.println(
            "Copy numerical values from nodedata to nodedatanumerical "
            + "in xml_nodes...");

        sqlStatement = sqlca.createStatement();
        rset = sqlStatement.executeQuery(
            "SELECT DISTINCT NODEID, NODEDATA "
            + "FROM xml_nodes WHERE "
            + "nodedata IS NOT NULL AND "
            + "UPPER(nodedata) = LOWER(nodedata)");

        int count = 0;
        while (rset.next()) {

            String nodeid = rset.getString(1);
            String nodedata = rset.getString(2);

            try {
                if (!nodedata.trim().equals("")) {
                    double s = Double.parseDouble(nodedata);

                    pstmt = sqlca.prepareStatement(
                        "update xml_nodes set nodedatanumerical = " + s +
                        " where nodeid=" + nodeid);
                    pstmt.execute();
                    pstmt.close();

                    count++;
                    if (count%5 == 0) {
                        System.out.println(count + "...");
                    }
                }
            } catch (NumberFormatException nfe) {

            } catch (Exception e) {
                System.out.println("Exception:" + e.getMessage());
            }
        }
        System.out.println("\nDone. " + count + " values copied.");

        rset.close();
        sqlStatement.close();
    }


    public static void main(String [] ags) throws ClassNotFoundException, InstantiationException, IllegalAccessException{

        try {
            Connection sqlca = null;
            Class dbDriverClass = null;
            // Create a JDBC connection to the database
            if (database.equals("oracle")) {
            	dbDriverClass = Class.forName("oracle.jdbc.driver.OracleDriver");
            } 
            else if (database.equals("postgresql")) {
            	dbDriverClass = Class.forName("org.postgresql.Driver");
            } 
            else if (database.equals("sqlserver")) {
            	dbDriverClass = Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
            }
            DriverManager.registerDriver((Driver)dbDriverClass.newInstance());
            sqlca = DriverManager.getConnection(url, user, password);

            // Upgrade the xml_nodes table only if it oracle
	    if (database.equals("oracle")) {
            	upgrade_xml_nodes(sqlca);
	    }
            // Close the connection...
            sqlca.close();
        } catch (SQLException ex) {
            System.out.println("SQLException:" + ex.getMessage());
        }
    }
}
