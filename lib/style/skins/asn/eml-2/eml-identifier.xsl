<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-identifier.xsl,v $'
  *      Authors: Matthew Brooke
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: leinfelder $'
  *     '$Date: 2008-08-27 19:12:14 $'
  * '$Revision: 1.5 $'
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
  * convert an XML file that is valid with respect to the eml-variable.dtd
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" encoding="utf-8" indent="yes" />
  
  <xsl:variable name="partialURL">/metacat/<xsl:value-of select="*[local-name()='eml']/@packageId"/>/html</xsl:variable>
  <xsl:variable name="docURL" select="concat($contextURL,$partialURL)" />
  <xsl:variable name="docRegURL" select="concat($registryurl,$partialURL)" />

	<!--*************** displays dataset citation********-->
  <xsl:template name="identifier">
    <dl class="citation section">
      <dt>Data Set Citation</dt>
      <dd><p>
        <xsl:for-each select="creator">
          <xsl:if test="position() &gt; 1">
            <xsl:if test="last() &gt; 2">,</xsl:if>
            <xsl:if test="position() = last()"> and</xsl:if>
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:call-template name="creatorCitation" />
          <xsl:if test="position() = last()">. </xsl:if>
        </xsl:for-each>
        <xsl:value-of select="substring(string(pubDate),1,4)"/>
        <xsl:if test="substring(string(pubDate),1,4) != ''">. </xsl:if>
        <b><xsl:value-of select="title"/></b>.<br/>
        <xsl:if test="boolean($registryname)">
          <xsl:value-of select="$registryname"/>: 
        </xsl:if>
        <xsl:variable name="docLongURL" select="concat($tripleURI,$docid)" />
        
        <span class="lsid">Document ID:&#160; 
          <xsl:choose>
            <xsl:when test="boolean($lsidauthority)">
             <a href="{$docLongURL}"><span id="documentId"><xsl:call-template name="lsid"/></span></a>
            </xsl:when>
            <xsl:otherwise>
             <a href="{$docLongURL}"><span id="documentId"><xsl:value-of select="../@packageId"/></span></a>
            </xsl:otherwise>
          </xsl:choose>
        </span>
	<br/>
	<span id="doiIdentifier"></span>
      </p>
</dd>
    </dl>
  </xsl:template>
   
   <!--************** creates lsid dataset id **************-->
   <xsl:template name="lsid">
		<xsl:variable name="lsidString1" select="concat('urn:lsid:',string($lsidauthority),':')"/>
		<xsl:variable name="lsidString2" select="concat($lsidString1, substring-before(string(../@packageId),'.'), ':')"/>
		<xsl:variable name="lsidString3" select="concat($lsidString2, substring-before(substring-after(string(../@packageId),'.'),'.'), ':')"/>
		<xsl:variable name="lsidString4" select="concat($lsidString3, substring-after(substring-after(string(../@packageId),'.'),'.'))"/>
		<xsl:value-of select="$lsidString4"/>
   </xsl:template>
   
   <!--************** creates citation for a creator in "Last FM" format **************-->
   <xsl:template name="creatorCitation">
	   	<xsl:for-each select="individualName">	
	   		
	   		<xsl:value-of select="surName"/>
	   		<xsl:text> </xsl:text>
	   		
	   		<xsl:for-each select="givenName">
	   			<xsl:value-of select="substring(string(.),1,1)"/>
	   		</xsl:for-each>
	   	</xsl:for-each>
	   	
	   	<xsl:if test="string(individualName/surName) != ''"> 
	   		<xsl:if test="string(organizationName) != ''"> of </xsl:if>
	   	</xsl:if>
	   	
	   	<xsl:for-each select="organizationName">
	   		<xsl:value-of select="."/>
	   	</xsl:for-each>
   </xsl:template>

    
 </xsl:stylesheet>
