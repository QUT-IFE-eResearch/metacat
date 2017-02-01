<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml.xsl,v $'
  *      Authors: Matt Jones
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: obrien $'
  *     '$Date: 2008-08-28 22:59:33 $'
  * '$Revision: 1.8 $'
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
  *
  * This is an XSLT (http://www.w3.org/TR/xslt) stylesheet designed to
  * convert an XML file that is valid with respect to the eml-dataset.dtd
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
  <xsl:output method="html"  />
-->
<!DOCTYPE html [
  <!ENTITY inchead SYSTEM "../include_head.jsp?jqueryui=true">
  <!ENTITY incheader SYSTEM "../include_header.jsp">
  <!ENTITY incside SYSTEM "../include_side.jsp">
  <!ENTITY incfooter SYSTEM "../include_footer.jsp">
  <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/02/xpath-function" xmlns:eml="eml://ecoinformatics.org/eml-2.1.0" version="1.0">
  <xsl:import href="emlroot.xsl"/>

  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="utf-8" indent="yes" />

  <xsl:template match="/">
    <xsl:variable name="docTitle" select="./eml:eml/dataset/title" />
    <html>
      <head>
        <title>RELRP - <xsl:value-of select="$docTitle"/></title>
        <meta name="DC.Title" content="{$docTitle}"/>
        &inchead;
      </head>
      <body>
        &incheader;
        <div class="mainContainer">
          <div class="mainContent">
            <div style="overflow:auto;">
              &incside;
              
              <div class="contentColumn left">
                <div class="moduleArea multiBorders">
                  <div class="moduleHeading moduleHeadingGreen">RELRP Data Catalogue</div>
                  <div id="documentArea">
                  
                    <div class="shortcutNav">
                      <div class="left"><a href="javascript:history.back()">Back</a></div>
                      <div class="right"><a href="{$contextURL}">New Search</a></div>
                      <div class="clear"></div>
                    </div>
                    <h2><xsl:value-of select="$docTitle"/></h2>
                    <div id="tabs" class="tabs">
                      <ul>
                        <li><a href="#tabMetadata">Metadata</a></li>
                        <xsl:if test="$displaymodule='dataset' or $displaymodule='printall'">
                        <li><a href="#tabData">Data Files</a></li>
                        </xsl:if>
                      </ul>
                      <xsl:apply-templates select="*[local-name()='eml']"/>
                    </div> <!--tabs-->
                    <div class="shortcutNav">
                      <div class="left"><a href="javascript:history.back()">Back</a></div>
                      <div class="right"><a href="{$contextURL}">New Search</a></div>
                      <div class="clear"></div>
                    </div>
                    
                  </div> <!-- documentArea -->
      
                </div> <!-- moduleArea -->
              </div> <!-- contentColumn -->
            </div>

          </div> <!-- mainContent -->
        </div> <!-- mainContainer -->
      
        &incfooter;
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
