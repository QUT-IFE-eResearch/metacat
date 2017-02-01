/**
 *  '$RCSfile$'
 *  Copyright: 2008 Regents of the University of California and the
 *              National Center for Ecological Analysis and Synthesis
 *  Purpose: To test the Access Controls in metacat by JUnit
 *
 *   '$Author: daigle $'
 *     '$Date: 2009-11-25 06:28:15 +1000 (Wed, 25 Nov 2009) $'
 * '$Revision: 5139 $'
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

package edu.ucsb.nceas;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;

import edu.ucsb.nceas.metacat.database.DBConnection;
import edu.ucsb.nceas.metacat.database.DBConnectionPool;
import edu.ucsb.nceas.metacat.client.InsufficientKarmaException;
import edu.ucsb.nceas.metacat.client.Metacat;
import edu.ucsb.nceas.metacat.client.MetacatException;
import edu.ucsb.nceas.metacat.client.MetacatFactory;
import edu.ucsb.nceas.metacat.client.MetacatInaccessibleException;
import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.shared.ServiceException;
import edu.ucsb.nceas.metacat.util.RequestUtil;
import edu.ucsb.nceas.utilities.IOUtil;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;
import edu.ucsb.nceas.utilities.SortedProperties;

/**
 * A base JUnit class for Metacat tests
 */
public class MCTestCase
    extends TestCase {
	
    private static boolean printDebug = false;
    
	protected static String EML2_0_0 = "EML2_0_0";
	protected static String EML2_0_1 = "EML2_0_1";
	protected static String EML2_1_0 = "EML2_1_0";
	
	protected boolean SUCCESS = true;
	protected boolean FAILURE = false;
	
	protected static final String ALLOWFIRST = "allowFirst";
	protected static final String DENYFIRST = "denyFirst";
	
	protected String testdatadir = "test/clienttestfiles/";
	protected String prefix = "test";
	protected String testdocument = "";
	
	protected static HttpClient httpClient = null;
	
	protected static boolean metacatConnectionNeeded = false;
	protected Metacat m;

	protected static String metacatUrl;
	protected static String username;
	protected static String password;
	protected static String anotheruser;
	protected static String anotherpassword;
	
	static {
		try {
			SortedProperties testProperties = 
				new SortedProperties("build/tests/test.properties");
			testProperties.load();
			String metacatContextDir = testProperties.getProperty("metacat.contextDir");
			PropertyService.getInstance(metacatContextDir + "/WEB-INF");
//			PropertyService.getInstance();
		    String printDebugString = PropertyService.getProperty("test.printdebug");
		    printDebug = Boolean.parseBoolean(printDebugString);

		    metacatUrl = PropertyService.getProperty("test.metacatUrl");
			username = PropertyService.getProperty("test.mcUser");
			password = PropertyService.getProperty("test.mcPassword");
			anotheruser = PropertyService.getProperty("test.mcAnotherUser");
			anotherpassword = PropertyService.getProperty("test.mcAnotherPassword");
		} catch (IOException ioe) {
			System.err.println("Could not read property file in static block: " 
					+ ioe.getMessage());
		} catch (PropertyNotFoundException pnfe) {
			System.err.println("Could not get property in static block: " 
					+ pnfe.getMessage());
		} catch (ServiceException se) {
			System.err.println("Could not get PropertyService instance in static block: " 
					+ se.getMessage());
		}
	}
	
	// header blocks
	protected String testEml_200_Header = "<?xml version=\"1.0\"?><eml:eml"
		+ " xmlns:eml=\"eml://ecoinformatics.org/eml-2.0.0\""
		+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
		+ " packageId=\"eml.1.1\" system=\"knb\""
		+ " xsi:schemaLocation=\"eml://ecoinformatics.org/eml-2.0.0 eml.xsd\""
		+ " scope=\"system\">";
	
	protected String testEml_201_Header = "<?xml version=\"1.0\"?><eml:eml"
		+ " xmlns:eml=\"eml://ecoinformatics.org/eml-2.0.1\""
		+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
		+ " packageId=\"eml.1.1\" system=\"knb\""
		+ " xsi:schemaLocation=\"eml://ecoinformatics.org/eml-2.0.1 eml.xsd\""
		+ " scope=\"system\">";
	
	protected String testEml_210_Header = "<?xml version=\"1.0\"?><eml:eml"
			+ " xmlns:eml=\"eml://ecoinformatics.org/eml-2.1.0\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " packageId=\"eml.1.1\" system=\"knb\""
			+ " xsi:schemaLocation=\"eml://ecoinformatics.org/eml-2.1.0 eml.xsd\""
			+ " scope=\"system\">";

	protected String testEmlCreatorBlock = "<creator scope=\"document\">                                       "
			+ " <individualName>                                                  "
			+ "    <surName>Smith</surName>                                       "
			+ " </individualName>                                                 "
			+ "</creator>                                                         ";

	protected String testEmlContactBlock = "<contact scope=\"document\">                                       "
			+ " <individualName>                                                  "
			+ "    <surName>Jackson</surName>                                     "
			+ " </individualName>                                                 "
			+ "</contact>                                                         ";

	protected String testEmlInlineBlock1 = "<inline>                                                           "
			+ "  <admin>                                                          "
			+ "    <contact>                                                      "
			+ "      <name>Operator</name>                                        "
			+ "      <institution>PSI</institution>                               "
			+ "    </contact>                                                     "
			+ "  </admin>                                                         "
			+ "</inline>                                                          ";

	protected String testEmlInlineBlock2 = "<inline>                                                           "
			+ "  <instrument>                                                     "
			+ "    <instName>LCQ</instName>                                       "
			+ "    <source type=\"ESI\"></source>                                 "
			+ "    <detector type=\"EM\"></detector>                              "
			+ "  </instrument>                                                    "
			+ "</inline>                                                          ";

	/*
	 * Retrus an access block base on params passed and the defaul perm order -
	 * allow first
	 */
	protected String getAccessBlock(String principal, boolean grantAccess, boolean read,
			boolean write, boolean changePermission, boolean all) {
		return getAccessBlock(principal, grantAccess, read, write, changePermission, all,
				ALLOWFIRST);
	}

	/**
	 * This function returns an access block based on the params passed
	 */
	protected String getAccessBlock(String principal, boolean grantAccess, boolean read,
			boolean write, boolean changePermission, boolean all, String permOrder) {
		String accessBlock = "<access "
				+ "authSystem=\"ldap://ldap.ecoinformatics.org:389/dc=ecoinformatics,dc=org\""
				+ " order=\"" + permOrder + "\"" + " scope=\"document\"" + ">";

		accessBlock += generateOneAccessRule(principal, grantAccess, read, write,
				changePermission, all);
		accessBlock += "</access>";

		return accessBlock;

	}

	/*
	 * Gets eml access block base on given acccess rules and perm order
	 */
	protected String getAccessBlock(Vector<String> accessRules, String permOrder) {
		String accessBlock = "<access "
				+ "authSystem=\"ldap://ldap.ecoinformatics.org:389/dc=ecoinformatics,dc=org\""
				+ " order=\"" + permOrder + "\"" + " scope=\"document\"" + ">";
		// adding rules
		if (accessRules != null && !accessRules.isEmpty()) {
			for (int i = 0; i < accessRules.size(); i++) {
				String rule = (String) accessRules.elementAt(i);
				accessBlock += rule;

			}
		}
		accessBlock += "</access>";
		return accessBlock;
	}

	/*
	 * Generates a access rule for given parameter. Note this xml portion
	 * doesn't include <access></access>
	 */
	protected String generateOneAccessRule(String principal, boolean grantAccess,
			boolean read, boolean write, boolean changePermission, boolean all) {
		String accessBlock = "";

		if (grantAccess) {
			accessBlock = "<allow>";
		} else {
			accessBlock = "<deny>";
		}

		accessBlock = accessBlock + "<principal>" + principal + "</principal>";

		if (all) {
			accessBlock += "<permission>all</permission>";
		} else {
			if (read) {
				accessBlock += "<permission>read</permission>";
			}
			if (write) {
				accessBlock += "<permission>write</permission>";
			}
			if (changePermission) {
				accessBlock += "<permission>changePermission</permission>";
			}
		}

		if (grantAccess) {
			accessBlock += "</allow>";
		} else {
			accessBlock += "</deny>";
		}
		return accessBlock;

	}

	/**
	 * This function returns a valid eml document with no access rules 
	 */
	protected String getTestEmlDoc(String title, String emlVersion, String inlineData1,
			String inlineData2, String onlineUrl1, String onlineUrl2,
			String docAccessBlock, String inlineAccessBlock1, String inlineAccessBlock2,
			String onlineAccessBlock1, String onlineAccessBlock2) {

		debug("getTestEmlDoc(): title=" + title + " inlineData1=" + inlineData1
				+ " inlineData2=" + inlineData2 + " onlineUrl1=" + onlineUrl1
				+ " onlineUrl2=" + onlineUrl2 + " docAccessBlock=" + docAccessBlock
				+ " inlineAccessBlock1=" + inlineAccessBlock1 + " inlineAccessBlock2="
				+ inlineAccessBlock2 + " onlineAccessBlock1=" + onlineAccessBlock1
				+ " onlineAccessBlock2=" + onlineAccessBlock2);
		String testDocument = "";
		String header;
		if (emlVersion == EML2_0_0) {
			header = testEml_200_Header;
		} else if (emlVersion == EML2_0_1) {
			header = testEml_201_Header;
		} else {
			header = testEml_210_Header;
		}
		testDocument += header;
		
		// if this is a 2.1.0+ document, the document level access block sits
		// at the same level and before the dataset element.
		if (docAccessBlock != null && emlVersion.equals(EML2_1_0)) {
			testDocument += docAccessBlock;
		}
		
		testDocument += "<dataset scope=\"document\"><title>"
				+ title + "</title>" + testEmlCreatorBlock;

		if (inlineData1 != null) {
			testDocument = testDocument
					+ "<distribution scope=\"document\" id=\"inlineEntity1\">"
					+ inlineData1 + "</distribution>";
		}
		if (inlineData2 != null) {
			testDocument = testDocument
					+ "<distribution scope=\"document\" id=\"inlineEntity2\">"
					+ inlineData2 + "</distribution>";
		}
		if (onlineUrl1 != null) {
			testDocument = testDocument
					+ "<distribution scope=\"document\" id=\"onlineEntity1\">"
					+ "<online><url function=\"download\">" + onlineUrl1
					+ "</url></online></distribution>";
		}
		if (onlineUrl2 != null) {
			testDocument = testDocument
					+ "<distribution scope=\"document\" id=\"onlineEntity2\">"
					+ "<online><url function=\"download\">" + onlineUrl2
					+ "</url></online></distribution>";
		}
		testDocument += testEmlContactBlock;

		// if this is a 2.0.X document, the document level access block sits
		// inside the dataset element.
		if (docAccessBlock != null && 
				(emlVersion.equals(EML2_0_0) || emlVersion.equals(EML2_0_1))) {
			testDocument += docAccessBlock;
		}

		testDocument += "</dataset>";

		if (inlineAccessBlock1 != null) {
			testDocument += "<additionalMetadata>";
			testDocument += "<describes>inlineEntity1</describes>";
			testDocument += inlineAccessBlock1;
			testDocument += "</additionalMetadata>";
		}

		if (inlineAccessBlock2 != null) {
			testDocument += "<additionalMetadata>";
			testDocument += "<describes>inlineEntity2</describes>";
			testDocument += inlineAccessBlock2;
			testDocument += "</additionalMetadata>";
		}

		if (onlineAccessBlock1 != null) {
			testDocument += "<additionalMetadata>";
			testDocument += "<describes>onlineEntity1</describes>";
			testDocument += onlineAccessBlock1;
			testDocument += "</additionalMetadata>";
		}

		if (onlineAccessBlock2 != null) {
			testDocument += "<additionalMetadata>";
			testDocument += "<describes>onlineEntity2</describes>";
			testDocument += onlineAccessBlock2;
			testDocument += "</additionalMetadata>";
		}

		testDocument += "</eml:eml>";

		// System.out.println("Returning following document" + testDocument);
		return testDocument;
	}
	
	/**
	 * 
	 */
	protected String getTestDocFromFile(String filePath) throws IOException{
		StringBuffer output = new StringBuffer();
		
		FileReader fileReader = new FileReader(new File(filePath));
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String readLine = null;
		
		while ((readLine = bufferedReader.readLine()) != null) {
			output.append(readLine);
		}
		
		return output.toString();
	}

	
    /**
     * Constructor to build the test
     */
    public MCTestCase() {
        super();
    }
	
    /**
     * Constructor to build the test
     *
     * @param name the name of the test method
     */
    public MCTestCase(String name) {
        super(name);
    }
    
	/**
	 * Establish a testing framework by initializing appropriate objects
	 */
	protected void setUp() throws Exception {
		try {
			if (metacatConnectionNeeded) {
				debug("setUp() - Testing Metacat Url: " + metacatUrl);
				m = MetacatFactory.createMetacatConnection(metacatUrl);
			}
		} catch (MetacatInaccessibleException mie) {
			System.err.println("Metacat is: " + metacatUrl);
			fail("Metacat connection failed." + mie.getMessage());
		}
	}
    
    protected static void debug(String debugMessage) {
    	if (printDebug) {
    		System.err.println(debugMessage);
    	}
    }
    
	protected static Vector<Hashtable<String, Object>> dbSelect(String sqlStatement,
			String methodName) throws SQLException {
		Vector<Hashtable<String, Object>> resultVector = new Vector<Hashtable<String, Object>>();

		DBConnectionPool connPool = DBConnectionPool.getInstance();
		DBConnection dbconn = DBConnectionPool.getDBConnection(methodName);
		int serialNumber = dbconn.getCheckOutSerialNumber();

		PreparedStatement pstmt = null;

		debug("Selecting from db: " + sqlStatement);
		pstmt = dbconn.prepareStatement(sqlStatement);
		pstmt.execute();

		ResultSet resultSet = pstmt.getResultSet();
		ResultSetMetaData rsMetaData = resultSet.getMetaData();
		int numColumns = rsMetaData.getColumnCount();
		debug("Number of data columns: " + numColumns);
		while (resultSet.next()) {
			Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
			for (int i = 1; i <= numColumns; i++) {
				if (resultSet.getObject(i) != null) {
					hashTable.put(rsMetaData.getColumnName(i), resultSet.getObject(i));
				}
			}
			debug("adding data row to result vector");
			resultVector.add(hashTable);
		}

		resultSet.close();
		pstmt.close();
		DBConnectionPool.returnDBConnection(dbconn, serialNumber);

		return resultVector;
	}
	
	protected static void dbQuery(String sqlStatement, String methodName)
			throws SQLException {

		DBConnectionPool connPool = DBConnectionPool.getInstance();
		DBConnection dbconn = DBConnectionPool.getDBConnection(methodName);
		int serialNumber = dbconn.getCheckOutSerialNumber();

		Statement statement = dbconn.createStatement();

		debug("Executing against db: " + sqlStatement);
		statement.executeQuery(sqlStatement);

		statement.close();
		
		DBConnectionPool.returnDBConnection(dbconn, serialNumber);
	}
	
	protected static void dbUpdate(String sqlStatement, String methodName)
			throws SQLException {

		DBConnectionPool connPool = DBConnectionPool.getInstance();
		DBConnection dbconn = DBConnectionPool.getDBConnection(methodName);
		int serialNumber = dbconn.getCheckOutSerialNumber();

		Statement statement = dbconn.createStatement();

		debug("Executing against db: " + sqlStatement);
		statement.executeUpdate(sqlStatement);

		statement.close();

		DBConnectionPool.returnDBConnection(dbconn, serialNumber);
	}
	
	protected static void httpPost(String url, HashMap<String,String> paramMap)  throws IOException {
		debug("Posting to: " + url);
		if (httpClient == null) {
			httpClient = new HttpClient();
		}
		String postResponse = RequestUtil.post(httpClient, url, paramMap);
		debug("Post response: " + postResponse);
		if (postResponse.contains("<error>")) {
			fail("Error posting to metacat: " + postResponse);
		}
	}
	
	protected static void resetHttpClient() {
		httpClient = null;
	}
	
	/**
	 * Create a unique docid for testing insert and update. Does not
	 * include the 'revision' part of the id.
	 * 
	 * @return a String docid based on the current date and time
	 */
	protected String generateDocumentId() {
		try {
			Thread.sleep(1010);
		} catch (InterruptedException ie) {
			debug("Could not sleep: " + ie.getMessage());
		}
		
		StringBuffer docid = new StringBuffer(prefix);
		docid.append(".");

		// Create a calendar to get the date formatted properly
		String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
		SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
		pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		Calendar calendar = new GregorianCalendar(pdt);
		Date trialTime = new Date();
		calendar.setTime(trialTime);
		docid.append(calendar.get(Calendar.YEAR));
		docid.append(calendar.get(Calendar.DAY_OF_YEAR));
		docid.append(calendar.get(Calendar.HOUR_OF_DAY));
		docid.append(calendar.get(Calendar.MINUTE));
		docid.append(calendar.get(Calendar.SECOND));

		return docid.toString();
	}
	
	/**
	 * Insert a document into metacat. The expected result is passed as result
	 */

	protected String insertDocumentId(String docid, String docText, boolean result,
			boolean expectKarmaException) {
		debug("insertDocumentId() - docid=" + docid + " expectedResult=" + result
				+ " expectKarmaException=" + expectKarmaException);
		String response = null;
		try {
			response = m.insert(docid, new StringReader(docText), null);
			if (result) {
				assertTrue(response, (response.indexOf("<success>") != -1));
				assertTrue(response, response.indexOf(docid) != -1);
			} else {
				assertTrue(response, (response.indexOf("<success>") == -1));
			}
			debug("insertDocid():  response=" + response);
		} catch (MetacatInaccessibleException mie) {
			fail("Metacat Inaccessible:\n" + mie.getMessage());
		} catch (InsufficientKarmaException ike) {
			if (!expectKarmaException) {
				fail("Insufficient karma:\n" + ike.getMessage());
			}
		} catch (MetacatException me) {
			fail("Metacat Error:\n" + me.getMessage());
		} catch (Exception e) {
			fail("General exception:\n" + e.getMessage());
		}
		return response;
	}

	/**
	 * Insert a document into metacat. The expected result is passed as result
	 */

	protected String uploadDocumentId(String docid, String filePath, boolean result,
			boolean expectedKarmaException) {
		debug("uploadDocumentId() - docid=" + docid + " filePath=" + filePath
				+ " expectedResult=" + result + " expectedKarmaException="
				+ expectedKarmaException);
		String response = null;
		try {
			response = m.upload(docid, new File(filePath));
			if (result) {
				assertTrue(response, (response.indexOf("<success>") != -1));
				assertTrue(response, response.indexOf(docid) != -1);
			} else {
				assertTrue(response, (response.indexOf("<success>") == -1));
			}
			debug("uploadDocid():  response=" + response);
		} catch (MetacatInaccessibleException mie) {
			fail("Metacat Inaccessible:\n" + mie.getMessage());
		} catch (InsufficientKarmaException ike) {
			if (!expectedKarmaException) {
				fail("Insufficient karma:\n" + ike.getMessage());
			}
		} catch (MetacatException me) {
			if (result) {
				fail("Metacat Error:\n" + me.getMessage());
			} else {
				debug("Metacat Error:\n" + me.getMessage());
			}
		} catch (Exception e) {
			fail("General exception:\n" + e.getMessage());
		}
		return response;
	}

	/**
	 * Update a document in metacat. The expected result is passed as result
	 */
	protected String updateDocumentId(String docid, String docText, boolean result,
			boolean expectedKarmaFailure) {
		debug("updateDocumentId() - docid=" + docid + " expectedResult=" + result
				+ " expectedKarmaFailure=" + expectedKarmaFailure);
		String response = null;
		try {
			response = m.update(docid, new StringReader(testdocument), null);
			debug("updateDocumentId() - response=" + response);
			
			if (result) {
				assertTrue(response, (response.indexOf("<success>") != -1));
				assertTrue(response, response.indexOf(docid) != -1);
			} else {
				assertTrue(response, (response.indexOf("<success>") == -1));
			}			
		} catch (MetacatInaccessibleException mie) {
			fail("Metacat Inaccessible:\n" + mie.getMessage());
		} catch (InsufficientKarmaException ike) {
			if (!expectedKarmaFailure) {
				fail("Insufficient karma:\n" + ike.getMessage());
			}
		} catch (MetacatException me) {
			if (result) {
				fail("Metacat Error:\n" + me.getMessage());
			} else {
				debug("Metacat Error:\n" + me.getMessage());
			}
		} catch (Exception e) {
			fail("General exception:\n" + e.getMessage());
		}

		return response;
	}

	/**
	 * Delete a document into metacat. The expected result is passed as result
	 */
	protected void deleteDocumentId(String docid, boolean result, boolean expectedKarmaFailure) {
		debug("deleteDocumentId() - docid=" + docid + " expectedResult=" + result
				+ " expectedKarmaFailure=" + expectedKarmaFailure);
		try {
			Thread.sleep(5000);
			String response = m.delete(docid);
			debug("deleteDocumentId() -  response=" + response);
			
			if (result) {
				assertTrue(response, response.indexOf("<success>") != -1);
			} else {
				assertTrue(response, response.indexOf("<success>") == -1);
			}
		} catch (MetacatInaccessibleException mie) {
			fail("Metacat Inaccessible:\n" + mie.getMessage());
		} catch (InsufficientKarmaException ike) {
			if (!expectedKarmaFailure) {
				fail("Insufficient karma:\n" + ike.getMessage());
			}
		} catch (MetacatException me) {
			if (result) {
				fail("Metacat Error:\n" + me.getMessage());
			} else {
				debug("Metacat Error:\n" + me.getMessage());
			}
		} catch (Exception e) {
			fail("General exception:\n" + e.getMessage());
		}
	}
	
	/**
	 * Read a document from metacat and check if it is equal to a given string.
	 * The expected result is passed as result
	 */
	protected void readDocumentIdWhichEqualsDoc(String docid, String testDoc, boolean result,
			boolean expectedKarmaFailure) {
		debug("readDocumentIdWhichEqualsDoc() - docid=" + docid + " expectedResult=" + result
				+ " expectedKarmaFailure=" + expectedKarmaFailure);
		try {
			Reader r = new InputStreamReader(m.read(docid));
			String doc = IOUtil.getAsString(r, true);
			debug("readDocumentIdWhichEqualsDoc() -  doc=" + doc);
			
			if (result) {

				if (!testDoc.equals(doc)) {
					System.out.println("doc ***********************");
					System.out.println(doc);
					System.out.println("end doc ***********************");
					System.out.println("testDoc ***********************");
					System.out.println(testDoc);
					System.out.println("end testDoc ***********************");
				}

				assertTrue(testDoc.equals(doc));
			} else {
				assertTrue(doc.indexOf("<error>") != -1);
			}
		} catch (MetacatInaccessibleException mie) {
			fail("Metacat Inaccessible:\n" + mie.getMessage());
		} catch (InsufficientKarmaException ike) {
			if (!expectedKarmaFailure) {
				fail("Insufficient karma:\n" + ike.getMessage());
			}
		} catch (MetacatException me) {
			fail("Metacat Error:\n" + me.getMessage());
		} catch (Exception e) {
			fail("General exception:\n" + e.getMessage());
		}

	}

}
