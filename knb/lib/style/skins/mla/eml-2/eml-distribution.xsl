<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-distribution.xsl,v $'
  *      Authors: Matthew Brooke
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: obrien $'
  *     '$Date: 2009-03-17 20:36:38 $'
  * '$Revision: 1.6 $'
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
  <xsl:import href="eml-text.xsl" />
  <xsl:output method="html" encoding="utf-8" indent="yes" />

  <!-- Distribution Info sections -->
  <xsl:template name="distribution">
    <xsl:param name="level" select="$distributionlevel"/>
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>
    <xsl:param name="physicalindex"/>
    <xsl:param name="physicalnode" select="."/>

    <!-- Select all distribution including references -->
    <xsl:variable name="ref_ids" select="$physicalnode/distribution/references"/>
    <xsl:variable name="dist_nodes" select="$physicalnode/distribution | $ids[@id=$ref_ids]"/>
    <xsl:if test="$dist_nodes/online">
      <dl class="distribution section">
        <dt>Online Distribution Info</dt>
        <dd><xsl:apply-templates select="$dist_nodes/online"/></dd>
      </dl>
    </xsl:if>
    <xsl:if test="$dist_nodes/offline">
      <dl class="distribution section">
        <dt>Offline Distribution Info</dt>
        <dd><xsl:apply-templates select="$dist_nodes/offline"/></dd>
      </dl>
    </xsl:if>
    <xsl:if test="$dist_nodes/inline">
      <dl class="distribution section">
        <dt>Inline Data</dt>
        <dd>
          <xsl:for-each select="distribution">
            <xsl:call-template name="inlinedistribution">
              <xsl:with-param name="level" select="$level"/>
              <xsl:with-param name="entitytype" select="$entitytype"/>
              <xsl:with-param name="entityindex" select="$entityindex"/>
              <xsl:with-param name="physicalindex" select="$physicalindex"/>
              <xsl:with-param name="distributionindex" select="position()"/>
            </xsl:call-template>
          </xsl:for-each>
        </dd>
      </dl>
    </xsl:if>
    <xsl:apply-templates mode="entityaccess" select="$dist_nodes/access"/>
  </xsl:template>
  

  <!-- ********************************************************************* -->
  <!-- *******************************  Online data  *********************** -->
  <!-- ********************************************************************* -->
  <xsl:template match="online">
    <xsl:apply-templates select="url"/>
    <xsl:apply-templates select="connection"/>
    <xsl:apply-templates select="connectionDefinition"/>
  </xsl:template>

  <xsl:template match="url">
    <xsl:variable name="URL" select="."/>
    <dl class="row">
      <dt><xsl:text>Download File:</xsl:text></dt>
      <dd>
        <xsl:if test="$withHTMLLinks='1'">
          <a>
          <xsl:choose>
          <xsl:when test="starts-with($URL,'ecogrid')">
            <xsl:variable name="URL1" select="substring-after($URL, 'ecogrid://')"/> 
            <xsl:variable name="docID" select="substring-after($URL1, '/')"/>
            <xsl:attribute name="href"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$docID"/></xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="href"><xsl:if test="not(contains(.,':/'))">http://</xsl:if><xsl:value-of select="$URL"/></xsl:attribute>
          </xsl:otherwise>
          </xsl:choose>
          <xsl:attribute name="target">_blank</xsl:attribute>
          <xsl:choose>
      <!-- onlineDescription is a sib of url or connection, and might be used
      in a resource level distribution. In a physical tree, the objectName
      is required, so that should appear as the anchor instead -->
          <xsl:when test="../onlineDescription">
            <xsl:value-of select="../onlineDescription"/>
          </xsl:when>
          <xsl:when test="../../../objectName">
            <xsl:value-of select="../../../objectName"/>
          </xsl:when>
          <xsl:otherwise>
					  <xsl:value-of select="." />
          </xsl:otherwise>
          </xsl:choose>
          </a>
        </xsl:if>
        <xsl:if test="$withHTMLLinks='0'">
          <xsl:value-of select="."/>
        </xsl:if>
      </dd> 
    </dl>
  </xsl:template>

  <xsl:template match="connection">
    <xsl:apply-templates select="references"/>
    <xsl:if test="not(references) or normalize-space(references)=''">
      <xsl:if test="parameter">
        <dl class="row">
          <dt><xsl:text>Parameter(s):</xsl:text></dt>
          <dd>
            <xsl:call-template name="renderParameters"/>
          </dd>
        </dl>
      </xsl:if>
      <xsl:apply-templates select="connectionDefinition"/>
    </xsl:if>
  </xsl:template>

  <xsl:template name="renderParameters">
    <xsl:for-each select="parameter" >
      <dl class="row">
        <dt>
          <xsl:text>&#160;&#160;&#160;&#160;&#160;</xsl:text>
          <xsl:value-of select="name" />
        </dt>
        <dd>
         <xsl:value-of select="value" />
        </dd>
      </dl>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="connectionDefinition">
    <xsl:apply-templates select="references"/>
    
    <xsl:if test="not(references) or normalize-space(references)=''">
      <dl class="row">
        <dt><xsl:text>Schema Name:</xsl:text></dt>
        <dd><xsl:value-of select="schemeName" /></dd>
      </dl>
      <dl class="row">
        <dt><xsl:text>Description:</xsl:text></dt>
        <dd><xsl:apply-templates select="description"/></dd>
      </dl>
      <xsl:for-each select="parameterDefinition">
        <xsl:call-template name="renderParameterDefinition"/>
      </xsl:for-each>
    </xsl:if>
    
  </xsl:template>

  <xsl:template match="description">
    <xsl:call-template name="text"/>
  </xsl:template>

  <xsl:template name="renderParameterDefinition">
    <dl>
      <dt>
        <xsl:text>&#160;&#160;&#160;&#160;&#160;</xsl:text>
        <xsl:value-of select="name" /><xsl:text>:</xsl:text>
      </dt>
      <dd>
        <dl>
          <dt>
            <xsl:choose>
              <xsl:when test="defaultValue">
                <xsl:value-of select="defaultValue" />
              </xsl:when>
              <xsl:otherwise>
                &#160;
              </xsl:otherwise>
            </xsl:choose>
          </dt>
          <dd>
            <xsl:value-of select="definition" />
          </dd>
        </dl>
      </dd>
    </dl>
  </xsl:template>

  <!-- ********************************************************************* -->
  <!-- *******************************  Offline data  ********************** -->
  <!-- ********************************************************************* -->

  <xsl:template match="offline">
    <xsl:if test="(mediumName) and normalize-space(mediumName)!=''">
      <dl class="row">
        <dt><xsl:text>Medium:</xsl:text></dt>
        <dd><xsl:value-of select="mediumName"/></dd>
      </dl>
    </xsl:if>
    <xsl:if test="(mediumDensity) and normalize-space(mediumDensity)!=''">
      <dl class="row">
        <dt><xsl:text>Medium Density:</xsl:text></dt>
        <dd>
          <xsl:value-of select="mediumDensity"/>
          <xsl:if test="(mediumDensityUnits) and normalize-space(mediumDensityUnits)!=''">
            <xsl:text> (</xsl:text>
            <xsl:value-of select="mediumDensityUnits"/>
            <xsl:text>)</xsl:text>
          </xsl:if>
        </dd>
      </dl>
    </xsl:if>
    <xsl:if test="(mediumVol) and normalize-space(mediumVol)!=''">
      <dl class="row">
        <dt><xsl:text>Volume:</xsl:text></dt>
        <dd><xsl:value-of select="mediumVol"/></dd>
      </dl>
    </xsl:if>
    <xsl:if test="(mediumFormat) and normalize-space(mediumFormat)!=''">
      <dl class="row">
        <dt><xsl:text>Format:</xsl:text></dt>
        <dd><xsl:value-of select="mediumFormat"/></dd>
      </dl>
    </xsl:if>
    <xsl:if test="(mediumNote) and normalize-space(mediumNote)!=''">
      <dl class="row">
        <dt><xsl:text>Notes:</xsl:text></dt>
        <dd><xsl:value-of select="mediumNote"/></dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <!-- ********************************************************************* -->
  <!-- *******************************  Inline data  *********************** -->
  <!-- ********************************************************************* -->
  <xsl:template name="inlinedistribution">
    <xsl:param name="level"/>
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>
    <xsl:param name="physicalindex"/>
    <xsl:param name="distributionindex"/>
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="ref_node" select="$ids[@id=$ref_id]"/>
    <xsl:apply-templates select="inline | $ref_node/inline">
      <xsl:with-param name="level" select="$level"/>
      <xsl:with-param name="entitytype" select="$entitytype"/>
      <xsl:with-param name="entityindex" select="$entityindex"/>
      <xsl:with-param name="physicalindex" select="$physicalindex"/>
      <xsl:with-param name="distributionindex" select="$distributionindex"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="inline">
    <xsl:param name="level"/>
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>
    <xsl:param name="physicalindex"/>
    <xsl:param name="distributionindex"/>
    <xsl:variable name="urlbase" select="concat($tripleURI,$docid,'&amp;displaymodule=inlinedata&amp;distributionlevel=',$level,'&amp;distributionindex=',$distributionindex)"/>
    <xsl:variable name="url">
      <xsl:value-of select="$urlbase"/>
      <xsl:if test="$level='entitylevel'">
        <xsl:text>&amp;entitytype=</xsl:text>
        <xsl:value-of select="$entitytype"/>
        <xsl:text>&amp;entityindex=</xsl:text>
        <xsl:value-of select="$entityindex"/>
        <xsl:text>&amp;physicalindex=</xsl:text>
        <xsl:value-of select="$physicalindex"/>
      </xsl:if>
    </xsl:variable>

    <dl class="row">
      <dt><xsl:text>Inline Data:</xsl:text></dt>
      <dd>
        <xsl:if test="$withHTMLLinks='1'">
          <a href="{$url}"><b>Inline Data</b></a>
        </xsl:if>
        <xsl:if test="$withHTMLLinks='0'">
          <xsl:value-of select="$url"/>
        </xsl:if>
      </dd>
    </dl>
  </xsl:template>
	
	<!-- handle entity access level-->
	<xsl:template mode="entityaccess" match="access">
		<div>
			<xsl:call-template name="access"/>
		</div>
	</xsl:template>


</xsl:stylesheet>
