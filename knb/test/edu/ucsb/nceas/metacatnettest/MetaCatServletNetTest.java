/**
 *  '$RCSfile$'
 *  Copyright: 2000 Regents of the University of California and the
 *              National Center for Ecological Analysis and Synthesis
 *    Purpose: To test the MetaCatURL class by JUnit
 *    Authors: Jing Tao
 *
 *   '$Author: tao $'
 *     '$Date: 2011-06-18 08:40:16 +1000 (Sat, 18 Jun 2011) $'
 * '$Revision: 6157 $'
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

package edu.ucsb.nceas.metacatnettest;

import edu.ucsb.nceas.MCTestCase;
import edu.ucsb.nceas.metacat.*;
import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * A JUnit test for testing Step class processing
 */
public class MetaCatServletNetTest extends MCTestCase {
	private static String metacatURL;
	private static String lterUsername;
	private static String lterPassword;
	static {
		try {		
			metacatURL = PropertyService.getProperty("test.metacatUrl");
			lterUsername = PropertyService.getProperty("lterUser");
			lterPassword =  PropertyService.getProperty("lterPassword");
		} catch (PropertyNotFoundException pnfe) {
			System.err.println("could not find metacat URL in MetacatServletNetTest: "
					+ pnfe.getMessage());
		} catch (Exception e) {
			System.err.println("Exception in initialize option in MetacatServletNetTest: "
					+ e.getMessage());
		}
	}

	private String serialNumber;

	/**
	 * Constructor to build the test
	 *
	 * @param name the name of the test method
	 */
	public MetaCatServletNetTest(String name) {
		super(name);
	}

	/**
	 * Constructor to build the test
	 *
	 * @param name the name of the test method
	 */
	public MetaCatServletNetTest(String name, String serial) {
		super(name);
		serialNumber = serial;
	}

	/**
	 * Establish a testing framework by initializing appropriate objects
	 */
	public void setUp() {

	}

	/**
	 * Release any objects after tests are complete
	 */
	public void tearDown() {
	}

	/**
	 * Create a suite of tests to be run together
	 */
	public static Test suite() {
		double number = 0;
		String serial = null;

		TestSuite suite = new TestSuite();
		suite.addTest(new MetaCatServletNetTest("initialize"));
		suite.addTest(new MetaCatServletNetTest("testNCEASLoginFail"));
		//Should put a login successfully at the end of login test
		//So insert or update can have cookie.
		suite.addTest(new MetaCatServletNetTest("testNCEASLogin"));

		//create random number for docid, so it can void repeat
		number = Math.random() * 100000;
		serial = Integer.toString(((new Double(number)).intValue()));
		debug("serial: " + serial);
		suite.addTest(new MetaCatServletNetTest("testInsertXMLDocument", serial));
		suite.addTest(new MetaCatServletNetTest("testReadXMLDocumentXMLFormat", serial));
		suite.addTest(new MetaCatServletNetTest("testUpdateXMLDocument", serial));
		suite.addTest(new MetaCatServletNetTest("testReadXMLDocumentHTMLFormat", serial));
		suite.addTest(new MetaCatServletNetTest("testReadXMLDocumentZipFormat", serial));

		suite.addTest(new MetaCatServletNetTest("testDeleteXMLDocument", serial));

		//insert invalid xml document
		number = Math.random() * 100000;
		serial = Integer.toString(((new Double(number)).intValue()));
		suite.addTest(new MetaCatServletNetTest("testInsertInvalidateXMLDocument",
						serial));
		//insert non well formed document
		number = Math.random() * 100000;
		serial = Integer.toString(((new Double(number)).intValue()));
		suite.addTest(new MetaCatServletNetTest("testInsertNonWellFormedXMLDocument",
				serial));
		//insert data file  
		number = Math.random() * 100000;
		serial = Integer.toString(((new Double(number)).intValue()));
		suite.addTest(new MetaCatServletNetTest("testLogOut"));

		return suite;
	}

	/**
	 * Run an initial test that always passes to check that the test
	 * harness is working.
	 */
	public void initialize() {
		assertTrue(1 == 1);
	}

	/**
	 * Test the login to nceas succesfully
	 */
	public void testNCEASLogin() {
	    System.out.println("Testing nceas login");
		debug("\nRunning: testNCEASLogin test");
		assertTrue(logIn(username, password));
		//assertTrue( withProtocol.getProtocol().equals("http"));
	}

	/**
	 * Test the login to nceas failed
	 */
	public void testNCEASLoginFail() {
		debug("\nRunning: testNCEASLoginFail test");
		System.out.println("Testing nceas login fail");
		String passwd = "12345678";
		assertTrue(!logIn(username, passwd));

	}

	/**
	 * Test the login to lter failed
	 */
	public void testLterReferralLoginFail() {
		debug("\nRunning: testLterReferralLoginFail test");
			assertTrue(!logIn(lterUsername, lterPassword));
		//assertTrue( withProtocol.getProtocol().equals("http"));
	}

	/**
	 * Test insert a xml document successfully
	 */
	public void testInsertXMLDocument() throws PropertyNotFoundException {
		debug("\nRunning: testInsertXMLDocument test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "1";
		debug("insert docid: " + name);
		String content = "<?xml version=\"1.0\"?>"
				+ "<!DOCTYPE acl PUBLIC \"-//ecoinformatics.org//"
				+ "eml-access-2.0.0beta6//EN\" \"http://pine.nceas.ucsb."
				+ "edu:8080/tao/dtd/eml-access-2.0.0beta6.dtd\">"
				+ "<acl authSystem=\"knb\" order=\"allowFirst\">" + "<identifier>" + name
				+ "</identifier>" + "<allow>"
				+ "<principal>uid=john,o=NCEAS,dc=ecoinformatics,dc=org</principal>"
				+ "<permission>all</permission>" + "</allow>" + "<allow>"
				+ "<principal>public</principal>" + "<permission>read</permission>"
				+ "</allow>" + "</acl>";
		debug("xml document: " + content);
		assertTrue(handleXMLDocument(content, name, "insert"));

	}

	/**
	 * Test insert a invalidate xml document successfully
	 * In the String, there is no <!Doctype ... Public/System/>
	 */
	public void testInsertInvalidateXMLDocument() throws PropertyNotFoundException  {
		debug("\nRunning: testInsertInvalidateXMLDocument test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "1";
		debug("insert docid: " + name);
		String content = "<?xml version=\"1.0\"?>"
				+ "<acl authSystem=\"knb\" order=\"allowFirst\">" + "<identifier>" + name
				+ "</identifier>" + "<allow>"
				+ "<principal>uid=john,o=NCEAS,dc=ecoinformatics,dc=org</principal>"
				+ "<permission>all</permission>" + "</allow>" + "<allow>"
				+ "<principal>public</principal>" + "<permission>read</permission>"
				+ "</allow>" + "</acl>";
		debug("xml document: " + content);
		assertTrue(handleXMLDocument(content, name, "insert"));
	}

	/**
	 * Test insert a non well-formed xml document successfully
	 * There is no </acl> in this string
	 */
	public void testInsertNonWellFormedXMLDocument() throws PropertyNotFoundException  {
		debug("\nRunning: testInsertNonWellFormedXMLDocument test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "1";
		debug("insert non well-formed docid: " + name);
		String content = "<?xml version=\"1.0\"?>"
				+ "<acl authSystem=\"knb\" order=\"allowFirst\">" + "<identifier>" + name
				+ "</identifier>" + "<allow>"
				+ "<principal>uid=john,o=NCEAS,dc=ecoinformatics,dc=org</principal>"
				+ "<permission>all</permission>" + "</allow>" + "<allow>"
				+ "<principal>public</principal>" + "<permission>read</permission>"
				+ "</allow>";

		debug("xml document: " + content);
		assertTrue(!handleXMLDocument(content, name, "insert"));
	}

	/**
	 * Test read a xml document  in xml format successfully
	 */
	public void testReadXMLDocumentXMLFormat() throws PropertyNotFoundException  {
		debug("\nRunning: testReadXMLDocumentXMLFormat test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "1";
		assertTrue(handleReadAction(name, "xml"));

	}

	/**
	 * Test read a xml document  in html format successfully
	 */
	public void testReadXMLDocumentHTMLFormat() throws PropertyNotFoundException {
		debug("\nRunning: testReadXMLDocumentHTMLFormat test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "1";
		assertTrue(handleReadAction(name, "html"));

	}

	/**
	 * Test read a xml document  in zip format successfully
	 */
	public void testReadXMLDocumentZipFormat() throws PropertyNotFoundException {
		debug("\nRunning: testReadXMLDocumentZipFormat test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "1";
		assertTrue(handleReadAction(name, "zip"));

	}

	/**
	 * Test insert a xml document successfully
	 */
	public void testUpdateXMLDocument() throws PropertyNotFoundException {
		debug("\nRunning: testUpdateXMLDocument test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "2";
		debug("update docid: " + name);
		String content = "<?xml version=\"1.0\"?>"
				+ "<!DOCTYPE acl PUBLIC \"-//ecoinformatics.org//"
				+ "eml-access-2.0.0beta6//EN\" \"http://pine.nceas.ucsb."
				+ "edu:8080/tao/dtd/eml-access-2.0.0beta6.dtd\">"
				+ "<acl authSystem=\"knb\" order=\"allowFirst\">" + "<identifier>" + name
				+ "</identifier>" + "<allow>"
				+ "<principal>uid=john,o=NCEAS,dc=ecoinformatics,dc=org</principal>"
				+ "<permission>all</permission>" + "</allow>" + "<allow>"
				+ "<principal>public</principal>" + "<permission>read</permission>"
				+ "</allow>" + "</acl>";
		debug("xml document: " + content);
		assertTrue(handleXMLDocument(content, name, "update"));

	}

	/**
	 * Test insert a data file successfully
	 */
	public void testInertDataFile() throws PropertyNotFoundException {
		debug("\nRunning: testInertDataFile test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "1";
		debug("insert data file docid: " + name);
		debug("insert data file ");
		File hello = new File("test/jones.204.22.xml");

		assertTrue(insertDataFile(name, hello));
	}

	/**
	 * Test delete a xml document successfully
	 */
	public void testDeleteXMLDocument() throws PropertyNotFoundException {
		debug("\nRunning: testDeleteXMLDocument test");
		String name = "john" + PropertyService.getProperty("document.accNumSeparator") + serialNumber
				+ PropertyService.getProperty("document.accNumSeparator") + "2";
		debug("delete docid: " + name);
		assertTrue(handleDeleteFile(name));
	}

	/**
	 * Test logout action
	 */
	public void testLogOut() {
		debug("\nRunning: testLogOut test");
		assertTrue(handleLogOut());

	}

	/**
	 * Method to hanld login action
	 *
	 * @param usrerName, the DN name of the test method
	 * @param passWord, the passwd of the user
	 */

	public boolean logIn(String userName, String passWord) {
		Properties prop = new Properties();
		prop.put("action", "login");
		prop.put("qformat", "xml");
		prop.put("username", userName);
		prop.put("password", passWord);

		// Now contact metacat
		String response = getMetacatString(prop);
		debug("Login Message: " + response);
		boolean connected = false;
		if (response.indexOf("<login>") != -1) {
			connected = true;
		} else {

			connected = false;
		}

		return connected;
	}

	/**
	 * Method to hanld logout action
	 *
	 * @param usrerName, the DN name of the test method
	 * @param passWord, the passwd of the user
	 */

	public boolean handleLogOut() {
		boolean disConnected = false;
		Properties prop = new Properties();
		prop.put("action", "logout");
		prop.put("qformat", "xml");

		String response = getMetacatString(prop);
		debug("Logout Message: " + response);
		edu.ucsb.nceas.morpho.framework.HttpMessage.setCookie(null);

		if (response.indexOf("<logout>") != -1) {
			disConnected = true;
		} else {
			disConnected = false;
		}

		return disConnected;
	}

	/**
	 * Method to hanld read both xml and data file
	 *
	 * @param docid, the docid of the document want to read
	 * @param qformat, the format of document user want to get
	 */
	public boolean handleReadAction(String docid, String qformat) {
		Properties prop = new Properties();
		String message = "";
		prop.put("action", "read");
		prop.put("qformat", qformat);
		prop.put("docid", docid);

		message = getMetacatString(prop);
		message = message.trim();
		//MetacatUtil.debugMessage("Read Message: "+message, 30);
		if (message == null || message.equals("") || message.indexOf("<error>") != -1) {//there was an error

			return false;
		} else {//successfully
			return true;
		}

	}

	/**
	 * Method to hanld inset or update xml document
	 *
	 * @param xmlDocument, the content of xml qformat
	 * @param docid, the docid of the document
	 * @param action, insert or update
	 */
	public boolean handleXMLDocument(String xmlDocument, String docid, String action)

	{ //-attempt to write file to metacat
		String access = "no";
		StringBuffer fileText = new StringBuffer();
		StringBuffer messageBuf = new StringBuffer();
		String accessFileId = null;
		Properties prop = new Properties();
		prop.put("action", action);
		prop.put("public", access); //This is the old way of controlling access
		prop.put("doctext", xmlDocument);
		prop.put("docid", docid);

		String message = getMetacatString(prop);
		debug("Insert or Update Message: " + message);
		if (message.indexOf("<error>") != -1) {//there was an error

			return false;
		} else if (message.indexOf("<success>") != -1) {//the operation worked
			//write the file to the cache and return the file object
			return true;

		} else {//something weird happened.
			return false;
		}

	}

	public boolean handleDeleteFile(String name) {

		Properties prop = new Properties();
		prop.put("action", "delete");
		prop.put("docid", name);

		String message = getMetacatString(prop);
		debug("Delete Message: " + message);
		if (message.indexOf("<error>") != -1) {//there was an error

			return false;
		} else if (message.indexOf("<success>") != -1) {//the operation worked
			//write the file to the cache and return the file object
			return true;

		} else {//something weird happened.
			return false;
		}
	}

	/**
	 * sends a data file to the metacat using "multipart/form-data" encoding
	 *
	 * @param id the id to assign to the file on metacat (e.g., knb.1.1)
	 * @param file the file to send
	 */
	public boolean insertDataFile(String id, File file) {
		String response = null;
		//Get response for calling sendDataFile function
		response = sendDataFile(id, file);

		if (response.indexOf("success") != -1) {
			//insert successfully
			return true;
		} else {
			return false;
		}
	}

	/**
	 * sends a data file to the metacat using "multipart/form-data" encoding
	 *
	 * @param id the id to assign to the file on metacat (e.g., knb.1.1)
	 * @param file the file to send
	 */
	public String sendDataFile(String id, File file) {
		String response = "";
		InputStream returnStream = null;

		// Now contact metacat and send the request
		try {
			//FileInputStream data = new FileInputStream(file);
			System.setProperty("java.protocol.handler.pkgs", "HTTPClient");
			URL url = new URL(metacatURL);
			edu.ucsb.nceas.morpho.framework.HttpMessage msg = new edu.ucsb.nceas.morpho.framework.HttpMessage(
					url);
			Properties args = new Properties();
			args.put("action", "upload");
			args.put("docid", id);

			Properties dataStreams = new Properties();
			String filename = file.getAbsolutePath();
			System.out.println("the absolute path is " + filename);
			dataStreams.put("datafile", filename);

			returnStream = msg.sendPostData(args, dataStreams);

			InputStreamReader returnStreamReader = new InputStreamReader(returnStream);
			StringWriter sw = new StringWriter();
			int len;
			char[] characters = new char[512];
			while ((len = returnStreamReader.read(characters, 0, 512)) != -1) {
				sw.write(characters, 0, len);
			}
			returnStreamReader.close();
			response = sw.toString();
			sw.close();

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return response;
	}

	public String getMetacatString(Properties prop) {
		String response = null;

		// Now contact metacat and send the request
		try {
			InputStreamReader returnStream = new InputStreamReader(
					getMetacatInputStream(prop));
			StringWriter sw = new StringWriter();
			int len;
			char[] characters = new char[512];
			while ((len = returnStream.read(characters, 0, 512)) != -1) {
				sw.write(characters, 0, len);
			}
			returnStream.close();
			response = sw.toString();
			sw.close();
		} catch (Exception e) {
			return null;
		}

		return response;
	}

	/**
	 * Send a request to Metacat
	 *
	 * @param prop the properties to be sent to Metacat
	 * @return InputStream as returned by Metacat
	 */
	public InputStream getMetacatInputStream(Properties prop) {
		InputStream returnStream = null;
		// Now contact metacat and send the request
		try {

			URL url = new URL(metacatURL);
			edu.ucsb.nceas.morpho.framework.HttpMessage msg = new edu.ucsb.nceas.morpho.framework.HttpMessage(
					url);
			returnStream = msg.sendPostMessage(prop);
			return returnStream;
		} catch (Exception e) {
			e.printStackTrace(System.err);

		}
		return returnStream;

	}

}
