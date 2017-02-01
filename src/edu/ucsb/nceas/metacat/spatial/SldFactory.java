/**
 *  '$RCSfile$'
 *  Copyright: 2000 Regents of the University of California and the
 *  National Center for Ecological Analysis and Synthesis
 *
 *  Author: Matthew Perry
 *  '$Date: 2008-07-29 09:07:10 +1000 (Tue, 29 Jul 2008) $'
 *  '$Revision: 4167 $'
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Purpose: Servlet to generate a styled layer descriptor (SLD)
 *           with a filter to restrict features on
 *           a WMS map to given docids
 *
 *  Status: Currently this is just a working stub
 *          with some hardcoded values. Since this needs to be
 *          fired off on every map redraw, we'll need a way to make 
 *          the generation of docid list very efficient. 
 *
 *  Usage: Append the URL as an "SLD" to any WMS GetMap request
 *    ex: http://server/context/wms?...&SLD=http://server/context/sldfactory?originalSld=data_points_style
 */


package edu.ucsb.nceas.metacat.spatial;

import edu.ucsb.nceas.utilities.XMLUtilities;
import edu.ucsb.nceas.metacat.util.SystemUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;
import javax.servlet.ServletOutputStream;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Vector;

import org.w3c.dom.*;

/**
 * Class to generate Styled Layer Descriptors allowing geoserver to 
 * filter the feature rendering based on various contraints 
 * such as permissions, non-spatial queries and skin configuration.
 */
public class SldFactory extends HttpServlet {
  
  static Document document;
  static String sld;
  
  /** 
   * Handle "GET" method requests from HTTP clients 
   *
   * @param request Incoming servlet request.
   * @param response Servlet response.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException
  {
    handleGetOrPost(request, response);
  }

  /** 
   * Handle "POST" method requests from HTTP clients 
   *
   * @param request Incoming servlet request.
   * @param response Servlet response.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException
  {
    handleGetOrPost(request, response);
  }

  /**
	 * Control servlet response depending on the action parameter specified
	 * Modifies the original SLD according to various access contraints and
	 * sends it as the servlet response.
	 * 
	 * @param request
	 *            Incoming servlet request.
	 * @param response
	 *            Servlet response.
	 */
	private void handleGetOrPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String dataset = request.getParameter("originalSld");

		try {
			String certPath = SystemUtil.getContextDir();
			String filename = certPath + "/data/styles/" + dataset;
			String sld = getSld(filename);
			System.out.println(sld);
			response.setContentType("text/xml");
			response.getWriter().write(sld);
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
  
  /**
	 * Given a filename of an existing SLD document, reads the sld and adds an
	 * ogc:Filter to exclude/include certain docids based on user's permissions.
	 * The sld file is specified without path and must exist within geoserver's
	 * styles folder (context/data/styles/*)
	 * 
	 * returns SLD document as a String.
	 * 
	 * @param filename
	 *            Filename of the base sld.
	 * @todo Implement the doc id list queries for contraints.
	 */
  private String getSld(String filename) throws FileNotFoundException
  {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    Vector allowedDocids = new Vector();

	    /*
	     * TODO : 
	     *  Get the list of allowable docids due to 
	     *    access contraints (based on current logged-in user)
	     *    skin filtering (eg organization name) 
	     *    existing non-spatial query (eg taxonomic query)
	     *
	     *  These could ideally be cached as session variables to improve
	     *  speed over running expensive SQL queries on 
	     *  each map redraw.
	     *
	     * Combined, these will be a vector of allowed docids that
	     * are to be displayed on the map
	     *
	     * Below is just a hardcoded example of the above...
	     *
	     */
	    allowedDocids.add("nceas.288");
	    allowedDocids.add("nrs.720");

	    String rulesString = "";

	    try {
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    document = builder.parse( new File(filename) );	       
		    Element root = document.getDocumentElement();

		    NodeList elemList = document.getElementsByTagName("Rule");
		    Node ruleNode = elemList.item(0);

		    // Get the Element/node comprising the ogc:Filter for all of the above
		    Element filterElement = getFilterElement(document,allowedDocids);
		    
		    // Append the Filter to the ruleNode
		    ruleNode.appendChild(filterElement);
		    
		    // Write new DOM as a string
		    rulesString = XMLUtilities.getDOMTreeAsString((Node) root, true);

	    } catch (SAXException sxe) {
	       sxe.printStackTrace();
	    } catch (ParserConfigurationException pce) {
	       pce.printStackTrace();
	    } catch (IOException ioe) {
	       ioe.printStackTrace();
	    } 
	    return rulesString;
  }

  /**
   * Given a DOM document and a vector of docids,
   * Creates an OGC Filter node
   * 
   * @param inDoc	DOM document.
   * @param docids 	Vector of documents allowed in the map.
   */
  private Element getFilterElement(Document inDoc, Vector docids) 
  {
	  Element filterElem = inDoc.createElement("ogc:Filter");
	  Element orElem = inDoc.createElement("ogc:Or");
	  String docid = "";
	  
	  // Begin loop thru vector of allowed docids
	  for (int i = 0; i < docids.size(); i++) {
		  Element opElem = inDoc.createElement("ogc:PropertyIsEqualTo");

		  Element propertyElem = inDoc.createElement("ogc:PropertyName");
		  Text propertyText = inDoc.createTextNode("docid");
		  propertyElem.appendChild(propertyText);
		  
		  Element literalElem = inDoc.createElement("ogc:Literal");	  

		  docid = (String)docids.elementAt(i);
		  Text literalText = inDoc.createTextNode(docid); 
		  literalElem.appendChild(literalText);
		  
		  opElem.appendChild(propertyElem);
		  opElem.appendChild(literalElem);

		  orElem.appendChild(opElem);
		  // END LOOP
	  }
	  
	  filterElem.appendChild(orElem);
	  return filterElem;	  
  }
  
}
