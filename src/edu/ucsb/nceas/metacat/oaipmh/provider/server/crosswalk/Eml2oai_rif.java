/**
 * Copyright 2006 Institute for Sustainable Resources, Queensland University of Technology
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.ucsb.nceas.metacat.oaipmh.provider.server.crosswalk;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;


/**
 * Convert native "item" to oai_rif. In this case, the native "item" is assumed
 * to already be formatted as an OAI <record> element, with the possible
 * exception that multiple metadataFormats may be present in the <metadata>
 * element. The "crosswalk", merely involves pulling out the one that is
 * requested.
 */
public class Eml2oai_rif extends Crosswalk {
  
  /* Class fields */

  
  private static String dirPath = null;
  private static final String XSLT_NAME_210_RIFCS12 = "eml210toRifCs12.xsl";
  private static final String SCHEMA_LOCATION =
          "http://ands.org.au/standards/rif-cs/registryObjects " + 
          "http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd";

  /* Instance fields */
  private Transformer transformer = null;
  private Transformer transformer210RIFCS12 = null;
  
  
  /* Constructors */

  /**
   * The constructor assigns the schemaLocation associated with this crosswalk.
   * Since the crosswalk is trivial in this case, no properties are utilized.
   * 
   * @param properties
   *          properties that are needed to configure the crosswalk.
   */
  public Eml2oai_rif(Properties properties) throws OAIInternalServerError {
    super(SCHEMA_LOCATION);
    String xsltPath210 = dirPath + "/" + XSLT_NAME_210_RIFCS12;
    
    try {
      //TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
      TransformerFactory tFactory210 = TransformerFactory.newInstance();
      FileInputStream fileInputStream210 = new FileInputStream(xsltPath210);
      StreamSource xslSource210 = new StreamSource(fileInputStream210);
      this.transformer210RIFCS12 = tFactory210.newTransformer(xslSource210);
    } 
    catch (Exception e) {
      e.printStackTrace();
      throw new OAIInternalServerError(e.getMessage());
    }
  }
  
  
  /* Class methods */
  
  public static void setDirPath(String configDir) {
    Eml2oai_rif.dirPath = configDir;
  }


  
  /* Instance methods */


  /**
   * Perform the actual crosswalk.
   * 
   * @param nativeItem
   *          the native "item". In this case, it is already formatted as an OAI
   *          <record> element, with the possible exception that multiple
   *          metadataFormats are present in the <metadata> element.
   * @return a String containing the FileMap to be stored within the <metadata>
   *         element.
   * @exception CannotDisseminateFormatException
   *              nativeItem doesn't support this format.
   */
  public String createMetadata(Object nativeItem)
      throws CannotDisseminateFormatException {
    HashMap recordMap = (HashMap) nativeItem;
    try {
      //String xmlRec = (new String((byte[]) recordMap.get("recordBytes"),
      //    "UTF-8")).trim();
      String xmlRec = (String) recordMap.get("recordBytes");
      xmlRec = xmlRec.trim();
      
      if (xmlRec.startsWith("<?")) {
        int offset = xmlRec.indexOf("?>");
        xmlRec = xmlRec.substring(offset + 2);
      }
      
      //currently only has one transformer
      transformer = transformer210RIFCS12;
      
      StringReader stringReader = new StringReader(xmlRec);
      StreamSource streamSource = new StreamSource(stringReader);
      StringWriter stringWriter = new StringWriter();
      synchronized (transformer) {
        transformer.transform(streamSource, new StreamResult(stringWriter));
      }
      return stringWriter.toString();
    } 
    catch (Exception e) {
      throw new CannotDisseminateFormatException(e.getMessage());
    }
  }
  
  
  /**
   * Can this nativeItem be represented in DC format?
   * 
   * @param nativeItem
   *          a record in native format
   * @return true if DC format is possible, false otherwise.
   */
  public boolean isAvailableFor(Object nativeItem) {
    return true;
  }
  
}
