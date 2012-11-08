/**
 *  '$RCSfile$'
 *  Copyright: 2007 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ucsb.nceas.metacat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import edu.ucsb.nceas.metacat.database.DBConnection;
import edu.ucsb.nceas.metacat.database.DBConnectionPool;
import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

/**
 * A Sitemap represents a document that lists all of the content of the Metacat
 * server for use by harvesting spiders that wish to index the contents of the
 * Metacat site. It is used to generate an XML representation of all of the URLs
 * of the site in order to facilitate indexing of the metacat site by search
 * engines.
 * 
 * @author Matt Jones
 */
public class Sitemap extends TimerTask {
	
	private static Logger logMetacat = Logger.getLogger(Sitemap.class);

    /**
     * Construct a new instance of the Sitemap.
     * 
     * @param directory
     *            the location to store sitemap files
     * @param urlRoot
     *            the base URL for constructing sitemap URLs
     * @param skin
     *            the format skin to be used in URLs
     */
    public Sitemap(File directory, String urlRoot, String skin) {
        super();
        this.directory = directory;
        this.urlRoot = urlRoot;
        this.skin = skin;
    }

    /**
     * Execute the timed task when called, in this case by generating the
     * sitemap files needed for this Metacat instance.
     */
    public void run() {
        generateSitemaps();
    }

    /**
     * Generate all of the sitemap files needed to list the URLs from this
     * instance of Metacat, using the open sitemap format described here:
     * 
     * URLs are written to a single file, unless the maximum number of URLs
     * allowed inthe sitemap file is exceeded, in which subsequent numbered
     * files are created.
     * 
     * @param directory
     *            an existing File directory in which to write the sitemaps
     * @param urlRoot
     *            the base URL to use in constructing document URLs
     * @param skin
     *            the name of the skin to be used in formatting metacat
     *            documents
     */
    public void generateSitemaps() {

        logMetacat.info("Running the Sitemap task.");

        // Test if the passed in File is a directory
        if (directory.isDirectory()) {
            // Query xml_documents to get list of documents
            StringBuffer query = new StringBuffer();
            // TODO: make the doctype configurable in the query
            String sql =
            	"SELECT xml_documents.docid, xml_documents.rev " +
            	"FROM xml_documents, xml_access " +
                "WHERE xml_documents.doctype LIKE 'eml:%' " + 
                "AND xml_documents.docid = xml_access.docid " +
                "AND xml_access.principal_name = 'public' " +
                "AND xml_access.perm_type = 'allow' " +
                "order by docid, rev";
            query.append(sql);

            DBConnection dbConn = null;
            int serialNumber = -1;
            try {
                // Get a database connection from the pool
                dbConn = DBConnectionPool
                        .getDBConnection("Sitemap.generateSitemap()");
                serialNumber = dbConn.getCheckOutSerialNumber();

                // Execute the query statement
                PreparedStatement stmt = dbConn.prepareStatement(query
                        .toString());
                stmt.execute();
                ResultSet rs = stmt.getResultSet();

                // Loop through all of the documents, and write them to a
                // sitemap
                File sitemapFile = null;
                FileWriter sitemap = null;
                int counter = 0;
                int fileNumber = 0;
                while (rs.next()) {
                    // Check if a new sitemap file needs to be created
                    if (counter % MAX_URLS_IN_FILE == 0) {

                        // if a sitemap file is already open
                        if (sitemapFile != null && sitemapFile.canWrite()) {
                            // write the footer and close the file
                            writeSitemapFooter(sitemap);
                        }

                        // Open a new sitemap file for writing
                        fileNumber++;
                        sitemapFile = new File(directory, fileRoot + fileNumber
                                + ".xml");
                        sitemap = new FileWriter(sitemapFile);

                        // Write the sitemap document header for the new file
                        writeSitemapHeader(sitemap);
                    }

                    String separator = PropertyService.getProperty("document.accNumSeparator");
                    String docid = rs.getString(1) + separator
                            + rs.getString(2);
                    writeSitemapEntry(sitemap, docid);
                    counter++;
                }
                stmt.close();
                writeSitemapFooter(sitemap);
            } catch (SQLException e) {
                logMetacat.warn("Error while writing to the sitemap file: "
                        + e.getMessage());
            } catch (IOException ioe) {
                logMetacat.warn("Could not open or write to the sitemap file."
                        + ioe.getMessage());
            } catch (PropertyNotFoundException pnfe) {
                logMetacat.warn("Could not retrieve the account number separator."
                        + pnfe.getMessage());
            } finally {
                // Return database connection to the pool
                DBConnectionPool.returnDBConnection(dbConn, serialNumber);
            }
        } else {
            logMetacat.warn("Sitemap not created because directory not valid.");
        }
    }

    /**
     * Write the header information in a single sitemap file. This includes the
     * XML prolog, the root element and namespace declaration, and the elements
     * leading up to the first URL entry.
     * 
     * @param sitemap
     *            the Writer to use for writing the header
     * @throws IOException
     *             if there is a problem writing to the Writer
     */
    private void writeSitemapHeader(Writer sitemap) throws IOException {
        sitemap.write(PROLOG);
        String header = "<urlset xmlns=\"http://www.google.com/schemas/sitemap/0.84\">\n";
        sitemap.write(header);
        sitemap.flush();
    }

    /**
     * Write a URL entry to a single sitemap file. This includes the XML markup
     * surrounding a particular site URL.
     * 
     * @param sitemap
     *            the Writer to use for writing the URL
     * @param docid
     *            the identifier to be written in the URL
     * @param urlRoot
     *            the base URL to be used in constructing a URL
     * @param skin
     *            the name of the skin to be used in constructing a URL
     * @throws IOException
     *             if there is a problem writing to the Writer
     */
    private void writeSitemapEntry(Writer sitemap, String docid)
            throws IOException {
        if (sitemap != null && docid != null && urlRoot != null) {
            StringBuffer url = new StringBuffer();
            url.append(urlRoot);
            if (!urlRoot.endsWith("/")) {
                url.append("/");
            }
            url.append(docid);
            if (skin != null) {
                url.append("/");
                url.append(skin);
            }
            sitemap.write("<url><loc>");
            sitemap.write(url.toString());
            sitemap.write("</loc>");
            // <lastmod>2005-01-01</lastmod>
            // <changefreq>monthly</changefreq>
            // <priority>0.8</priority>
            sitemap.write("</url>");
            sitemap.write("\n");
            sitemap.flush();
        }
    }

    /**
     * Write the footer information in a single sitemap file and close the file.
     * This includes the closing tag for the root element.
     * 
     * @param sitemap
     *            the Writer to use for writing the footer
     * @throws IOException
     *             if there is a problem writing to the Writer
     */
    private void writeSitemapFooter(Writer sitemap) throws IOException {
        if (sitemap != null)
        {
	    	String footer = "</urlset>\n";
	        sitemap.write(footer);
	        sitemap.close();
        }
    }

    // Member variables

    /** The directory in which sitemaps are written. */
    private File directory;

    /** The root url for constructing sitemap URLs. */
    private String urlRoot;

    /** The name of the format skin to be used in sitemap URLs. */
    private String skin;

    /** Maximum number of URLs to write to a single sitemap file */
    static final int MAX_URLS_IN_FILE = 25000; // 50,000 according to Google

    /** The root name to be used in naming sitemap files. */
    static final String fileRoot = "metacat";

    /** A String constant containing the XML prolog to be written in files. */
    static final String PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
}
