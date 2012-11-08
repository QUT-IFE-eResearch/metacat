/**
 *  '$RCSfile$'
 *  Copyright: 2005 University of New Mexico and the 
 *             Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *   '$Author: daigle $'
 *     '$Date: 2008-07-07 14:25:34 +1000 (Mon, 07 Jul 2008) $'
 * '$Revision: 4080 $'
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * mERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package edu.ucsb.nceas.metacat.advancedsearch;

import java.io.*;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;

import edu.ucsb.nceas.metacat.util.SystemUtil;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

/**
 * @author dcosta
 * 
 * Stylizer class applies the resultset.xsl stylesheet to the pathquery
 * results returned by Metacat.
 */
public class Stylizer {

	private Logger logMetacat = Logger.getLogger(Stylizer.class);

	/**
	 * Applies the resultset.xsl stylesheet to the pathquery result string
	 * returned by Metacat.
	 * 
	 * @param resultset       the pathquery result string from Metacat
	 * @param sessionId       the user's session id
	 * @param metacatURL      the URL to the Metacat server
	 * @param qformat         The qformat (skin) to use when displaying results.
	 * @param xslPath         File path to the resultset.xsl stylesheet.
	 * 
	 * @return htmlString     the result of the transformation from XML to HTML
	 */
	public String resultsetToHTML(final String resultset, final String sessionId,
			final String metacatURL, final String qformat, final String xslPath) {
		String htmlString = "";
		Result result;
		StringWriter stringWriter = new StringWriter();
		Transformer transformer;
		TransformerFactory transformerFactory;
		Source xmlSource;
		File xsltFile = new File(xslPath);
		Source xsltSource;
		StringReader stringReader = new StringReader(resultset);

		xmlSource = new javax.xml.transform.stream.StreamSource(stringReader);
		xsltSource = new javax.xml.transform.stream.StreamSource(xsltFile);
		result = new javax.xml.transform.stream.StreamResult(stringWriter);

		// create an instance of TransformerFactory
		transformerFactory = TransformerFactory.newInstance();

		try {
			transformer = transformerFactory.newTransformer(xsltSource);

			String contextURL = SystemUtil.getContextURL();
			transformer.setParameter("contextURL", contextURL);

			if (sessionId != null) {
				transformer.setParameter("sessid", sessionId);
			}

			transformer.setParameter("metacatURL", metacatURL);

			if ((qformat != null) && (!qformat.equals(""))) {
				transformer.setParameter("qformat", qformat);
			}

			transformer.transform(xmlSource, result);
			htmlString = stringWriter.toString();
		} catch (TransformerConfigurationException tce) {
			// Error generated by the parser
			Throwable x = tce; // Use the contained exception, if any

			if (tce.getException() != null) {
				x = tce.getException();
			}

			x.printStackTrace();
		} catch (TransformerException te) {
			// Error generated by the parser
			Throwable x = te; // Use the contained exception, if any

			if (te.getException() != null) {
				x = te.getException();
			}

			x.printStackTrace();
		} catch (PropertyNotFoundException pnfe) {
			pnfe.printStackTrace();
		}

		return htmlString;
	}

}