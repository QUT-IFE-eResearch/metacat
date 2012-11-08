/**
 *  '$RCSfile$'
 *  Copyright: 2000 Regents of the University of California and the
 *              National Center for Ecological Analysis and Synthesis
 *
 *   '$Author: tao $'
 *     '$Date: 2003-09-05 10:57:22 +1000 (Fri, 05 Sep 2003) $'
 * '$Revision: 1818 $'
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

package edu.ucsb.nceas.metacat.stringclient.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import edu.ucsb.nceas.utilities.HttpMessage;


import org.globus.ogsa.impl.ogsi.GridServiceImpl;
import edu.ucsb.nceas.metacat.stringclient.MetacatString.MetacatStringPortType;
import java.rmi.RemoteException;

/**
 *  This interface provides methods for initializing and logging in to a 
 *  Metacat server, and then querying, reading, transforming, inserting, 
 *  updating and deleting documents from that server.
 */
public class MetacatStringImpl extends GridServiceImpl 
                               implements MetacatStringPortType
{
    /** The URL string for the metacat server */
    private String metacatUrl="http://dev.nceas.ucsb.edu/tao/servlet/metacat";

    /**
     *  Method used to log in to a metacat server. Implementations will need
     *  to cache a cookie value to make the session persistent.  Each time a
     *  call is made to one of the other methods (e.g., read), the cookie will
     *  need to be passed back to the metacat server along with the request.
     *
     *  @param username   the username of the user, like an LDAP DN
     *  @param password   the password for that user for authentication
     */
    public void login(String username, String password) throws RemoteException
        
    {
        Properties prop = new Properties();
        prop.put("action", "login");
        prop.put("qformat", "xml");
        prop.put("username", username);
        prop.put("password", password);

        String response = null;
        try {
            response = sendDataForString(prop);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }

        if (response.indexOf("<login>") == -1) {
            HttpMessage.setCookie(null);
            throw new RemoteException(response);
        }
    }

    /**
     *  Method used to log out a metacat server. When Metacat server will end
     *  the session when this call is invoken.
     *
     *  @throws MetacatInaccessibleException when the metacat server can not be
     *                                    reached or does not respond
     */
    public void logout() throws RemoteException
    {
        Properties prop = new Properties();
        prop.put("action", "logout");
        prop.put("qformat", "xml"); 
       
        String response = null;
        try {
            response = sendDataForString(prop);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        
        if (response.indexOf("<logout>") == -1) {
            throw new RemoteException(response);
        }
    }
    
    
    /**
     * Read an XML document from the metacat server session, accessed by docid,
     * and returned as a String.
     *
     * @param docid the identifier of the document to be read
     * @return a String for accessing the document
     */
    public String read(String docid) throws RemoteException
    {
        Properties prop = new Properties();
        prop.put("action", "read");
        prop.put("qformat", "xml");
        prop.put("docid", docid);

        String response = null;
        try {
            response = sendDataForString(prop);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        if (response != null && response.indexOf("<error>") != -1) {
                if (response.indexOf("does not have permission") != -1) {
                    throw new RemoteException(response);
                } else {
                    throw new RemoteException(response);
                }
         }
     
        return response;
    }

    /**
     * Query the metacat document store with the given metacat-compatible 
     * query document, and return the result set as a Reader.
     *
     * @param xmlQuery a string for accessing the XML version of the query
     * @return a string for accessing the result set
     */
    public String query(String xmlQuery) throws RemoteException 
    {
        String query = xmlQuery;
      
        //set up properties
        Properties prop = new Properties();
        prop.put("action", "squery");
        prop.put("qformat", "xml");
        prop.put("query", query);
        
        String response = null;
        try {
            response = sendDataForString(prop);
          
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
       
        return response;
    }

    /**
     * Insert an XML document into the repository.
     *
     * @param docid the docid to insert the document
     * @param xmlDocument a Reader for accessing the XML document to be inserted
     * @param schema a Reader for accessing the DTD or XML Schema for 
     *               the document
     * @return the metacat response message
     */
    public String insert(String docid, String xmlDocument, String schema)
        throws RemoteException
    {
        String doctext = xmlDocument;
        String schematext = schema;
     
     //set up properties
        Properties prop = new Properties();
        prop.put("action", "insert");
        prop.put("docid", docid);
        prop.put("doctext", doctext);
        if (schematext != null) {
            prop.put("dtdtext", schematext);
        }
        
        String response = null;
        try {
            response = sendDataForString(prop);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }

        // Check for an error condition
        if (response.indexOf("<error>") != -1) {
            if (response.indexOf("does not have permission") != -1) {
                throw new RemoteException(response);
            } else {
                throw new RemoteException(response);
            }
        }

        return response;
    }

    /**
     * Update an XML document in the repository.
     *
     * @param docid the docid to update
     * @param xmlDocument a Reader for accessing the XML text to be updated
     * @param schema a Reader for accessing the DTD or XML Schema for 
     *               the document
     * @return the metacat response message
     */
    public String update(String docid, String xmlDocument, String schema)
        throws RemoteException
    {
        String doctext = xmlDocument;
        String schematext = schema;
      

        //set up properties
        Properties prop = new Properties();
        prop.put("action", "update");
        prop.put("docid", docid);
        prop.put("doctext", doctext);
        if (schematext != null) {
            prop.put("dtdtext", schematext);
        }
        
        String response = null;
        try {
            response = sendDataForString(prop);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }

        // Check for an error condition
        if (response.indexOf("<error>") != -1) {
            if (response.indexOf("does not have permission") != -1) {
                throw new RemoteException(response);
            } else {
                throw new RemoteException(response);
            }
        }

        return response;
    }

    /**
     * Delete an XML document in the repository.
     *
     * @param docid the docid to delete
     * @return the metacat response message
     */
    public String delete(String docid) throws RemoteException
    {
        //set up properties
        Properties prop = new Properties();
        prop.put("action", "delete");
        prop.put("docid", docid);
        
        String response = null;
        try {
            response = sendDataForString(prop);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }

        // Check for an error condition
        if (response.indexOf("<error>") != -1) {
            if (response.indexOf("does not have permission") != -1) {
                throw new RemoteException(response);
            } else {
                throw new RemoteException(response);
            }
        }

        return response;
    }
    
    /**
     * When the MetacatFactory creates an instance it needs to set the
     * MetacatUrl to which connections should be made.
     *
     * @param metacatUrl the URL for the metacat server
     */
    public void setMetacatUrl(String metacatUrl) throws RemoteException
    {
      this.metacatUrl = metacatUrl;
    }
 
    /************************************************************************
     * PRIVATE METHODS
     ************************************************************************/

    /**
     * Send a request to metacat.
     *
     * @param prop the properties to be URL encoded and sent
     */
    synchronized private InputStream sendDataOnce(Properties prop) 
        throws Exception
    {
        InputStream returnStream = null;
        URL url = new URL(metacatUrl);
        HttpMessage msg = new HttpMessage(url);
        returnStream = msg.sendPostData(prop);
        return returnStream;
    }

    /**
     * Send a request to Metacat
     *
     * @param prop  the properties to be sent to Metacat
     * @return      InputStream as returned by Metacat
     */
    synchronized private InputStream sendData(Properties prop) throws Exception
    {   
        InputStream returnStream = null;

        /*
            Note:  The reason that there are three try statements all executing
            the same code is that there is a problem with the initial connection
            using the HTTPClient protocol handler.  These try statements make 
            sure that a connection is made because it gives each connection a 
            2nd and 3rd chance to work before throwing an error.
            THIS IS A TOTAL HACK.  THIS NEEDS TO BE LOOKED INTO AFTER THE BETA1
            RELEASE OF MORPHO!!!  cwb (7/24/01)
          */
        try {
           return sendDataOnce(prop);
        } catch (Exception e) {
            try {
                return sendDataOnce(prop);
            } catch (Exception e2) {
                try {
                    return sendDataOnce(prop);
                } catch (Exception e3) {
                    System.err.println(
                            "Failed to send data to metacat 3 times.");
                    throw e3;
                }
            }
        }
    }

    /**
     * Send a request to Metacat
     *
     * @param prop  the properties to be sent to Metacat
     * @return      a string as returned by Metacat
     */
    synchronized private String sendDataForString(Properties prop) 
        throws Exception
    {
        String response = null;

        try {
            InputStreamReader returnStream =
                    new InputStreamReader(sendData(prop));
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
            throw e;
        }
        return response;
    }
}
