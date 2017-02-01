<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-resource.xsl,v $'
  *      Authors: Matthew Brooke
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2007-11-01 22:42:31 $'
  * '$Revision: 1.4 $'
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
  <!--<xsl:import href="eml-party.xsl"/>
  <xsl:import href="eml-distribution.xsl"/>
  <xsl:import href="eml-coverage.xsl"/>-->
  <xsl:output method="html" encoding="utf-8" indent="yes" />
  <!-- This module is for resource and it is self-contained (it is table)-->
  <xsl:template name="resource">
    <xsl:param name="hideCitationInfo"/>
    <xsl:param name="creator">Data Set Owner(s):</xsl:param>
    
    <div class="resource">
      <xsl:for-each select="alternateIdentifier">
        <xsl:call-template name="resourcealternateIdentifier"/>
      </xsl:for-each>

      <xsl:for-each select="shortName">
        <xsl:call-template name="resourceshortName"/>
      </xsl:for-each>
	  
      <xsl:if test="not($hideCitationInfo)">
        <xsl:for-each select="title">
          <xsl:call-template name="resourcetitle"/>
        </xsl:for-each>

        <xsl:for-each select="pubDate">
          <xsl:call-template name="resourcepubDate"/>
        </xsl:for-each>
      </xsl:if>

      <xsl:for-each select="language">
      <xsl:call-template name="resourcelanguage" />
      </xsl:for-each>

      <xsl:for-each select="series">
      <xsl:call-template name="resourceseries" />
      </xsl:for-each>

      <div>
        <xsl:call-template name="distribution">
          <xsl:with-param name="level">toplevel</xsl:with-param>
        </xsl:call-template>
      </div>
      
      <xsl:if test="creator">
        <dl class="creator">
          <dt><xsl:value-of select="$creator"/></dt>
          <dd>
            <xsl:for-each select="creator">
              <xsl:call-template name="party"/>
            </xsl:for-each>
          </dd>
        </dl>
      </xsl:if>

      <xsl:if test="metadataProvider">
        <dl class="metadataProvider">
          <dt><xsl:text>Metadata Provider(s)</xsl:text></dt>
          <dd>
            <xsl:for-each select="metadataProvider">
              <xsl:call-template name="party"/>
            </xsl:for-each>
          </dd>
        </dl>
      </xsl:if>

      <xsl:if test="associatedParty">
        <dl class="associatedParty">
          <dt><xsl:text>Associated Party</xsl:text></dt>
          <dd>
            <xsl:for-each select="associatedParty">
              <xsl:call-template name="party"/>
            </xsl:for-each>
          </dd>
        </dl>
      </xsl:if>

      <xsl:if test="abstract">
        <dl class="abstract">
          <dt><xsl:text>Abstract</xsl:text></dt>
            <xsl:for-each select="abstract">
              <dd><xsl:call-template name="text"/></dd>
            </xsl:for-each>
        </dl>
      </xsl:if>

      <xsl:if test="keywordSet">
        <dl class="keywordSet"><dt><xsl:text>Keywords</xsl:text></dt><dd>
          <xsl:for-each select="keywordSet">
            <xsl:call-template name="resourcekeywordSet" />
          </xsl:for-each>
        </dd></dl>
      </xsl:if>

      <xsl:if test="additionalInfo">
        <dl class="additionalInfo">
          <dt><xsl:text>Additional Information</xsl:text></dt>
            <xsl:for-each select="additionalInfo">
              <dd><xsl:call-template name="text"/></dd>
            </xsl:for-each>
        </dl>
      </xsl:if>

      <xsl:if test="intellectualRights">
        <!--<dl class="intellectualRights">
          <dt><xsl:text>License and Usage Rights</xsl:text></dt>
            <xsl:for-each select="intellectualRights">
              <dd><xsl:call-template name="text"/></dd>
            </xsl:for-each>
        </dl>-->
      </xsl:if>

      <xsl:for-each select="coverage">
        <xsl:call-template name="coverage"/>
      </xsl:for-each>
      
    </div>
  </xsl:template>

  <xsl:template name="resourcealternateIdentifier" >
    <xsl:if test="normalize-space(.)!=''">
      <dl class="alternateIdentifier"><dt>Alternate Identifier:</dt>
        <dd><xsl:value-of select="."/></dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template name="resourceshortName">
    <xsl:if test="normalize-space(.)!=''">
      <dl class="shortName"><dt>Short Name:</dt>
        <dd><xsl:value-of select="."/></dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template name="resourcetitle" >
    <xsl:if test="normalize-space(.)!=''">
      <dl class="title"><dt>Title:</dt>
        <dd><b><xsl:value-of select="."/></b></dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template name="resourcepubDate">
    <xsl:if test="normalize-space(.)!=''">
      <dl class="pubDate"><dt>Publication Date:</dt>
        <dd><xsl:value-of select="../pubDate"/></dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template name="resourcelanguage">
    <xsl:if test="normalize-space(.)!=''">
      <dl class="language"><dt>Language:</dt>
        <dd><xsl:value-of select="."/></dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template name="resourceseries">
    <xsl:if test="normalize-space(.)!=''">
      <dl class="series"><dt>Series:</dt>
        <dd><xsl:value-of select="../series"/></dd>
      </dl>
    </xsl:if>
  </xsl:template>

  
  <xsl:template name="resourcekeywordSet">
    <dl><dt>
      <xsl:for-each select="keywordThesaurus">
        <xsl:if test="normalize-space(.)!=''">
          <xsl:value-of select="."/>
        </xsl:if>
      </xsl:for-each>
    </dt><dd>
      <xsl:if test="normalize-space(keyword)!=''">
        <ul>
        <xsl:for-each select="keyword">
          <li><xsl:value-of select="."/>
          <xsl:if test="./@keywordType and normalize-space(./@keywordType)!=''">
            (<xsl:value-of select="./@keywordType"/>)
          </xsl:if>
          </li>
        </xsl:for-each>
        </ul>
      </xsl:if>
    </dd></dl>
  </xsl:template>

</xsl:stylesheet>
