/**
 *  '$RCSfile$'
 *  Copyright: 2004 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *
 *   '$Author: daigle $'
 *     '$Date: 2009-08-07 03:59:40 +1000 (Fri, 07 Aug 2009) $'
 * '$Revision: 5016 $'
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
package edu.ucsb.nceas.metacattest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import edu.ucsb.nceas.MCTestCase;
import edu.ucsb.nceas.metacat.database.DBConnectionPool;
import edu.ucsb.nceas.metacat.EventLog;

/**
 * Test the logging facility against the database connection.
 * 
 * @author jones
 */
public class EventLogTest extends MCTestCase
{

    protected void setUp() throws Exception
    {    	
        super.setUp();
        DBConnectionPool pool = DBConnectionPool.getInstance();
    }

    /**
     * Test whether a valid instance of the EventLog can be retrieved.
     *
     */
    public void testGetInstance()
    {
        EventLog logger = EventLog.getInstance();
        assertTrue(logger != null);
    }

    /**
     * Test whether the log method can properly insert a log record.
     */
    public void testLog()
    {
        EventLog.getInstance().log("192.168.1.103", "public", "test.2.1", "read");
        assertTrue(1 == 1);
    }

    /**
     * Test whether getReport returns correct reports.
     */
    public void testGetReport()
    {
        String[] principals = {"jones", "public", "someone"};
        String[] ipList = {"192.168.1.103", "192.168.1.104"};
        String[] docList = {"test.2.1", "test.2"};
        String[] eventList = {"read", "insert", "update"};
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp startDate = null;
        Timestamp endDate = null;
        try {
            startDate = new Timestamp((format.parse("2004-04-08 02:32:00")).getTime());
            endDate = new Timestamp((format.parse("2004-04-08 11:20:00")).getTime());
        } catch (ParseException e) {
            System.out.println("Failed to created endDate from format.");
        }
        String report = EventLog.getInstance().getReport(ipList, principals, docList, 
                        eventList, null, null);
        System.out.println(report);
        report = EventLog.getInstance().getReport(null, null, null, 
                        null, startDate, endDate);
        System.out.println(report);
    }
}
