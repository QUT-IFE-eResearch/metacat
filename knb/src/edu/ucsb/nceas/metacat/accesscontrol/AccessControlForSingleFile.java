/**
 *  '$RCSfile$'
 *    Purpose: A Class that loads eml-access.xml file containing ACL 
 *             for a metadata document into relational DB
 *  Copyright: 2000 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Jivka Bojilova
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

package edu.ucsb.nceas.metacat.accesscontrol;

import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.ucsb.nceas.metacat.DocInfoHandler;
import edu.ucsb.nceas.metacat.McdbException;
import edu.ucsb.nceas.metacat.PermissionController;
import edu.ucsb.nceas.metacat.database.DBConnection;
import edu.ucsb.nceas.metacat.database.DBConnectionPool;
import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.metacat.shared.AccessException;
import edu.ucsb.nceas.metacat.util.DocumentUtil;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;


/** 
 * A Class that loads eml-access.xml file containing ACL for a metadata
 * document into relational DB. It extends DefaultHandler class to handle
 * SAX parsing events when processing the XML stream.
 */
public class AccessControlForSingleFile implements AccessControlInterface 
{

  private String _docId;
  private Logger logMetacat = Logger.getLogger(AccessControlForSingleFile.class);

 
    /**
	 * Construct an instance of the AccessControlForSingleFile class.  This
	 * instance will represent one file only.
	 * 
	 * @param myAccessNumber
	 *            the docid or docid with dev will be controlled
	 */
	public AccessControlForSingleFile(String accessionNumber)
			throws AccessControlException {

		// Get rid of dev if myaccessNumber has one;
		_docId = DocumentUtil.getDocIdFromString(accessionNumber);
		if (_docId == null || _docId.equals("")) {
			throw new AccessControlException("AccessControlForSingleFile() - Accession number "
							+ "can't be null in constructor");
		}

		logMetacat.debug("AccessControlForSingleFile() - docid: " + _docId);
	}
  
  	/**
	 * Insert a single access record into the database based on access DAO
	 * object
	 * 
	 * @param xmlAccessDAO
	 *            dao object holding info to insert
	 */
	public void insertPermissions(XMLAccessDAO xmlAccessDAO) 
			throws AccessControlException, PermOrderException{
		insertPermissions(xmlAccessDAO.getPrincipalName(), xmlAccessDAO.getPermission(), 
				xmlAccessDAO.getPermType(), xmlAccessDAO.getPermOrder(), xmlAccessDAO.getAccessFileId(), xmlAccessDAO.getSubTreeId());
	}

	/**
	 * Insert a single access record into the database.
	 * 
	 * @param principalName
	 *            the principal credentials
	 * @param permission
	 *            the permission
	 * @param permType
	 *            the permission type
	 * @param permOrder
	 *            the permission order
	 */
	public void insertPermissions(String principalName, Long permission, String permType, String permOrder, String accessFileId, String subTreeId) 
			throws AccessControlException, PermOrderException {
		try {
			// The addXMLAccess method will create the permission record if it does not exist.  
			// It will bitwise OR to permissions if the principal already has a record for this
			// doc id.
			XMLAccessAccess xmlAccessAccess = new XMLAccessAccess();
			//System.out.println("permission in accessControlForSingleFile.insertPermissions: " + permission);
			xmlAccessAccess.addXMLAccess(_docId, principalName, new Long(permission), permType, permOrder, accessFileId, subTreeId);
		} catch (AccessException ae) {
			throw new AccessControlException("AccessControlForSingleFile.insertPermissions - "
					+ "DB access error when inserting permissions: " + ae.getMessage());
		} 
	}
  
	/**
	 * Replace existing permissions with a given block of permissions for this
	 * document.
	 * 
	 * @param accessBlock
	 *            the xml access block. This is the same structure as that
	 *            returned by the getdocumentinfo action in metacat.
	 */
	public void insertPermissions(String accessBlock) throws AccessControlException {
		try {	
			// use DocInfoHandler to parse the access section into DAO objects
			XMLReader parser = null;
			DocInfoHandler docInfoHandler = new DocInfoHandler(_docId); 
			ContentHandler chandler = docInfoHandler;

			// Get an instance of the parser
			String parserName = PropertyService.getProperty("xml.saxparser");
			parser = XMLReaderFactory.createXMLReader(parserName);

			// Turn off validation
			parser.setFeature("http://xml.org/sax/features/validation", false);
			parser.setContentHandler((ContentHandler)chandler);
			parser.setErrorHandler((ErrorHandler)chandler);

			parser.parse(new InputSource(new StringReader(accessBlock)));
			
			XMLAccessAccess xmlAccessAccess = new XMLAccessAccess();
				
			// replace all access on the document
	        Vector<XMLAccessDAO> accessControlList = docInfoHandler.getAccessControlList();
	        xmlAccessAccess.replaceAccess(_docId, accessControlList);

		} catch (PropertyNotFoundException pnfe) {
			throw new AccessControlException("AccessControlForSingleFile.insertPermissions - "
					+ "property error when replacing permissions: " + pnfe.getMessage());
		} catch (AccessException ae) {
			throw new AccessControlException("AccessControlForSingleFile.insertPermissions - "
					+ "DB access error when replacing permissions: " + ae.getMessage());
		} catch (SAXException se) {
			throw new AccessControlException("AccessControlForSingleFile.insertPermissions - "
					+ "SAX error when replacing permissions: " + se.getMessage());
		} catch(IOException ioe) {
			throw new AccessControlException("AccessControlForSingleFile.insertPermissions - "
					+ "I/O error when replacing permissions: " + ioe.getMessage());
		}
	}
  
	/**
	 * Check if access control comination for
	 * docid/principal/permission/permorder/permtype already exists.
	 * 
	 * @param xmlAccessDAO
	 *            the dao object holding the access we want to check for.
	 * @return true if the Access Control for this file already exists in the DB
	 */
	public boolean accessControlExists(XMLAccessDAO xmlAccessDAO) throws AccessControlException {
		boolean exists = false;
		PreparedStatement pstmt = null;
		DBConnection conn = null;
		int serialNumber = -1;
		try {
			//check out DBConnection
			conn=DBConnectionPool.getDBConnection
                               ("AccessControlForSingleFiel.accessControlExists");
			serialNumber=conn.getCheckOutSerialNumber();
			pstmt = conn.prepareStatement(
				"SELECT * FROM xml_access " + 
				"WHERE docid = ? " +
				"AND principal_name = ? " +
				"AND permission = ? " +
				"AND perm_type = ? " +
				"AND perm_order = ? ");
     
			// Bind the values to the query
			pstmt.setString(1, _docId);
			pstmt.setString(2, xmlAccessDAO.getPrincipalName());
			pstmt.setLong(3, xmlAccessDAO.getPermission());
			pstmt.setString(4, xmlAccessDAO.getPermType());
			pstmt.setString(5, xmlAccessDAO.getPermOrder());
      
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			exists = rs.next();
      
		} catch (SQLException sqle){
			throw new AccessControlException("AccessControlForSingleFile.accessControlExists - SQL error when " +  
					"checking if access control exists: " + sqle.getMessage());
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException sqle) {
				logMetacat.error("AccessControlForSingleFile.accessControlExists - Could not close " + 
						"prepared statement: " +sqle.getMessage());
			} finally {
				DBConnectionPool.returnDBConnection(conn, serialNumber);
			}
		}
    
		return exists;  
	}
  
	/**
	 * Get Access Control List information for document from db connetion. User
	 * or Group should have permissions for reading access control information
	 * for a document specified by
	 * 
	 * @param user
	 *            name of user connected to Metacat system
	 * @param groups
	 *            names of user's groups to which user belongs
	 */
	public String getACL(String user, String[] groups)
			throws AccessControlException {
		StringBuffer output = new StringBuffer();
		boolean hasPermission = false;

		try {   
			hasPermission = isOwned(user);
			if (!hasPermission) {
				PermissionController controller = new PermissionController(_docId);
				hasPermission = 
					controller.hasPermission(user, groups, READSTRING);
			}

			// if the user has permissions, get the access dao list for this doc and return
			// it as a string.  Otherwise, get the string for an empty access dao list 
			// (which will return the access section with no allow or deny sections)
			if (hasPermission) {
				// Get a list of all access dao objects for this docid
				XMLAccessAccess xmlAccessAccess = new XMLAccessAccess();
				Vector<XMLAccessDAO> xmlAccessDAOList = xmlAccessAccess.getXMLAccessForDoc(_docId);
				output.append(getAccessString(xmlAccessDAOList));
			} else {
				output.append(getAccessString(new Vector<XMLAccessDAO>()));
			}

			return output.toString();

		} catch (SQLException sqle) {
			throw new AccessControlException("AccessControlForSingleFile.getACL() - SQL error when " + 
					"getting ACL: " + sqle.getMessage());
		} catch (AccessException ae) {
			throw new AccessControlException("AccessControlForSingleFile.getACL() - DB access error when " + 
					"getting ACL: " + ae.getMessage());
		} catch (McdbException mcdb) {
			throw new AccessControlException("AccessControlForSingleFile.getACL() - MCDB error when " + 
					"getting ACL: " + mcdb.getMessage());
		}
	}
	
	/**
	 * Get the access xml for all access on this docid
	 * 
	 * @return string representation of access
	 */
	public String getAccessString() throws AccessControlException {
		Vector<XMLAccessDAO> xmlAccessDAOList = null;
		
		try {
			// Get a list of all access dao objects for this docid
			XMLAccessAccess xmlAccessAccess = new XMLAccessAccess();
			xmlAccessDAOList = xmlAccessAccess.getXMLAccessForDoc(_docId);
		} catch (AccessException ae) {
				throw new AccessControlException("AccessControlForSingleFile.getAccessString() - DB access error when " + 
						"getting access string: " + ae.getMessage());
		} 
		
		return getAccessString(xmlAccessDAOList);
	}
	
	/**
	 * Put together an xml representation of the objects in a given access dao list
	 * @param xmlAccessDAOList list of xml access DAO objects
	 * @return string representation of access
	 */
	public String getAccessString(Vector<XMLAccessDAO> xmlAccessDAOList) throws AccessControlException {
			
		StringBuffer output = new StringBuffer();
		StringBuffer tmpOutput = new StringBuffer();
		StringBuffer allowOutput = new StringBuffer();
		StringBuffer denyOutput = new StringBuffer();
		
		String principal = null;
		int permission = -1;
		String permOrder = ALLOWFIRST;
		String permType = null;
		String accessfileid = null;
		String subtreeid = null;
		
		// We assume that all the records will have the same permission order, so we can just
		// grab the perm order from the first one.
		if (xmlAccessDAOList.size() > 0) {
			permOrder = xmlAccessDAOList.get(0).getPermOrder();
			accessfileid = xmlAccessDAOList.get(0).getAccessFileId();
			subtreeid = xmlAccessDAOList.get(0).getSubTreeId();
		}

		output.append("<access authSystem=\"knb\" order=\"" + permOrder + "\" id=\"" + _docId + "\" scope=\"document\"");
		if (accessfileid != null) {
			output.append(" accessfileid=\"" + accessfileid + "\"");
		}
		if (subtreeid != null) {
			output.append(" subtreeid=\"" + subtreeid + "\"");
		}
		
		output.append(">\n");
		
		for (XMLAccessDAO xmlAccessDAO : xmlAccessDAOList) {
			principal = xmlAccessDAO.getPrincipalName();
			permission = xmlAccessDAO.getPermission().intValue();
			//System.out.println("accessControlForSingleFile.getAccessString: permission is set to: " + permission);
			permType = xmlAccessDAO.getPermType();
			
			tmpOutput.append("    <" + permType + ">\n");
			tmpOutput.append("      <principal>" + principal + "</principal>\n");
	
			if ((permission & READ) == READ) {
				tmpOutput.append("      <permission>read</permission>\n");
			}
			if ((permission & WRITE) == WRITE) {
				tmpOutput.append("      <permission>write</permission>\n");
			}
			if ((permission & ALL) == ALL) {
				tmpOutput.append("      <permission>all</permission>\n");
			}
			if ((permission & CHMOD) == CHMOD) {
				tmpOutput.append("      <permission>chmod</permission>\n");
			}

			tmpOutput.append("    </" + permType + ">\n");
			
			if (permType.equals(ALLOW)) {
				allowOutput.append(tmpOutput);
			} else if (permType.equals(DENY)) {
				denyOutput.append(tmpOutput);
			}
			tmpOutput = new StringBuffer();
		}
		
		// This just orders the allow/deny sections based on the permOrder.  Not 
		// required, but convenient later when parsing the output.
		if (permOrder.equals(ALLOWFIRST)) {
			output.append(allowOutput);
			output.append(denyOutput);
		} else if (permOrder.equals(DENYFIRST)) {
			output.append(denyOutput);
			output.append(allowOutput);
		}
		
		output.append("</access>");
					
		return output.toString();
	}
	
	/**
	 * check if the docid represented in this class is owned by the user
	 * 
	 * @param user
	 *            the user credentials
	 * @return true if doc is owned by user, false otherwise
	 */
	private boolean isOwned(String user) throws SQLException {
		PreparedStatement pstmt = null;
		DBConnection conn = null;
		int serialNumber = -1;
		try {
			// check out DBConnection
			conn = DBConnectionPool.getDBConnection("AccessControlList.isOwned");
			serialNumber = conn.getCheckOutSerialNumber();
			pstmt = conn.prepareStatement("SELECT 'x' FROM xml_documents "
					+ "WHERE docid = ? " + "AND user_owner = ?");
			pstmt.setString(1, _docId);
			pstmt.setString(2, user);
			pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			boolean hasRow = rs.next();
			return hasRow;
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} finally {
				DBConnectionPool.returnDBConnection(conn, serialNumber);
			}
		}
	}
}
