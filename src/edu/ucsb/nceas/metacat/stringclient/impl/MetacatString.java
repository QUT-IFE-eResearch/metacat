/**
 *  '$RCSfile$'
 *  Copyright: 2000 Regents of the University of California and the
 *              National Center for Ecological Analysis and Synthesis
 *
 *   '$Author: tao $'
 *     '$Date: 2003-09-03 10:00:38 +1000 (Wed, 03 Sep 2003) $'
 * '$Revision: 1808 $'
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

/**
 *  This interface provides methods for initializing and logging in to a 
 *  Metacat server, and then querying, reading, transforming, inserting, 
 *  updating and deleting documents from that server.
 */
public interface MetacatString
{
    /**
     *  Method used to log in to a metacat server. Implementations will need
     *  to cache a cookie value to make the session persistent.  Each time a
     *  call is made to one of the other methods (e.g., read), the cookie will
     *  need to be passed back to the metacat server along with the request.
     *
     *  @param username   the username of the user, like an LDAP DN
     *  @param password   the password for that user for authentication
     */
    public void login(String username, String password);
    
    /**
     *  Method used to log out a metacat server. When Metacat server will end
     *  the session when this call is invoken.
     *
     *  @throws MetacatInaccessibleException when the metacat server can not be
     *                                    reached or does not respond
     */
    public void logout();

    /**
     * Read an XML document from the metacat server session, accessed by docid,
     * and returned as a String.
     *
     * @param docid the identifier of the document to be read
     * @return a String for accessing the document
     */
    public String read(String docid);

    /**
     * Query the metacat document store with the given metacat-compatible 
     * query document, and return the result set as a String
     */
    public String query(String xmlQuery);

    /**
     * Insert an XML document into the repository.
     *
     * @param docid the docid to insert the document
     * @param xmlDocument a String for accessing the XML document to be inserted
     * @param schema a String for accessing the DTD or XML Schema for 
     *               the document
     * @return the metacat response message
     */
    public String insert(String docid, String xmlDocument, String schema);

    /**
     * Update an XML document in the repository.
     *
     * @param docid the docid to update
     * @param xmlDocument a String for accessing the XML text to be updated
     * @param schema a String for accessing the DTD or XML Schema for 
     *               the document
     * @return the metacat response message
     */
    public String update(String docid, String xmlDocument, String schema);

    /**
     * Delete an XML document in the repository.
     *
     * @param docid the docid to delete
     * @return the metacat response message
     * @throws InsufficientKarmaException when the user has insufficent rights 
     *                                    for the operation
     * @throws MetacatInaccessibleException when the metacat server can not be
     *                                    reached or does not respond
     * @throws MetacatException when the metacat server generates another error
     */
    public String delete(String docid);

    /**
     * When the MetacatFactory creates an instance it needs to set the
     * MetacatUrl to which connections should be made.
     *
     * @param metacatUrl the URL for the metacat server
     */
    public void setMetacatUrl(String metacatUrl);
}
