<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-entity.xsl,v $'
  *      Authors: Jivka Bojilova
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: berkley $'
  *     '$Date: 2004-07-26 23:09:45 $'
  * '$Revision: 1.1 $'
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
  * convert an XML file that is valid with respect to the eml-file.dtd
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" encoding="utf-8" indent="yes" />
<!-- This module only provide some templates. They can be called by other templates-->
<xsl:template match="text()" mode="afterName"/>
<xsl:template match="text()" mode="lastInfo"/>
<xsl:template match="text()" mode="afterInfo"/>

<xsl:template name="entity">
  <xsl:param name="entitytype" select="local-name()"/>
  <xsl:param name="entityindex" select="position()"/>
  
  <xsl:variable name="ref_id" select="references"/>
  <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
  
  <xsl:choose>
  <xsl:when test="$displaymodule='entity' or $displaymodule='printall'">
    <div class="{$entitytype} entity">
      <dl class="entityInfo section">
        <dt>Entity Info</dt>
        <dd>
          <xsl:apply-templates select="$node/entityName"/>
          <xsl:apply-templates select="$node" mode="afterName"/>
          <xsl:apply-templates select="$node/alternateIdentifier"/>
          <xsl:apply-templates select="$node/entityDescription"/>
          <xsl:apply-templates select="$node/additionalInfo"/>
          <xsl:apply-templates select="$node" mode="lastInfo"/>
        </dd>
      </dl>
      <xsl:apply-templates select="$node" mode="afterInfo"/>
      <xsl:if test="$node/physical">
        <!-- Distribution Info -->
        <xsl:for-each select="$node/physical">
          <xsl:variable name="p_ref_id" select="references"/>
          <xsl:variable name="p_node" select=". | $ids[@id=$p_ref_id]"/>
          <xsl:call-template name="distribution">
            <xsl:with-param name="level">entitylevel</xsl:with-param>
            <xsl:with-param name="entitytype" select="$entitytype"/>
            <xsl:with-param name="entityindex" select="$entityindex"/>
            <xsl:with-param name="physicalindex" select="position()"/>
            <xsl:with-param name="physicalnode" select="$p_node"/>
          </xsl:call-template>
        </xsl:for-each>
        <!-- call physical module without showing distribution -->
        <dl class="physical section">
          <dt>Physical Structure Description</dt>
          <dd>
            <xsl:apply-templates select="$node/physical" mode="withoutDistribution"/>
          </dd>
        </dl>
      </xsl:if>
      <xsl:if test="$node/coverage">
        <dl class="coverage section">
          <dt>Coverage Description:</dt>
          <dd>
            <xsl:for-each select="$node/coverage">
              <xsl:call-template name="coverage"/>
            </xsl:for-each>
          </dd>
        </dl>
      </xsl:if>
      <xsl:if test="$node/methods | $node/method">
        <dl class="method section">
          <dt>Method Description:</dt>
          <dd>
            <xsl:for-each select="$node/methods | $node/method">
              <xsl:call-template name="method"/>
            </xsl:for-each>
          </dd>
        </dl>
      </xsl:if>
      <xsl:if test="$node/constraint">
        <dl class="constraint section">
          <dt>Constraint:</dt>
          <dd>
            <xsl:for-each select="$node/constraint">
              <xsl:call-template name="constraint"/>
            </xsl:for-each>
          </dd>
        </dl>
      </xsl:if>
      <xsl:if test="$node/attributeList and ($withAttributes='1' or $displaymodule='printall')">
        <dl class="attributeList section">
          <dt>Attribute(s) Info:</dt>
          <dd>
            <xsl:apply-templates select="$node/attributeList">
              <xsl:with-param name="entitytype" select="$entitytype"/>
              <xsl:with-param name="entityindex" select="$entityindex"/>
            </xsl:apply-templates>
          </dd>
        </dl>
      </xsl:if>

    </div>
  </xsl:when>
  <xsl:otherwise>
    <xsl:call-template name="chooseattributelist"/>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

  
  <xsl:template match="entityName">
    <dl class="row"><dt>Name:</dt><dd><b><xsl:value-of select="."/></b></dd></dl>
  </xsl:template>
  
  <xsl:template match="alternateIdentifier">
    <dl class="row"><dt>Identifier:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>
  
  <xsl:template match="entityDescription">
    <dl class="row"><dt>Description:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>
  
  <xsl:template match="additionalInfo">
    <dl class="row">
      <dt>Additional Info:</dt>
      <dd><xsl:call-template name="text"/></dd>
    </dl>
  </xsl:template>
  

  <xsl:template name="entityName">
    <xsl:param name="entityfirstColStyle"/>
    <tr><td width="{$firstColWidth}" class="{$entityfirstColStyle}">
    Name:</td><td width="{$secondColWidth}" class="{$secondColStyle}">
    <b><xsl:value-of select="."/></b></td></tr>
  </xsl:template>
  
  <xsl:template name="entityalternateIdentifier">
     <xsl:param name="entityfirstColStyle"/>
     <tr><td width="{$firstColWidth}" class="{$entityfirstColStyle}">
            Identifier:</td><td width="{$secondColWidth}" class="{$secondColStyle}">
            <xsl:value-of select="."/></td></tr>
  </xsl:template>
  
  <xsl:template name="entityDescription">
      <xsl:param name="entityfirstColStyle"/> 
      <tr><td width="{$firstColWidth}" class="{$entityfirstColStyle}">
      Description:</td><td width="{$secondColWidth}" class="{$secondColStyle}">
      <xsl:value-of select="."/></td></tr>
  </xsl:template>
  
  <xsl:template name="entityadditionalInfo">
      <xsl:param name="entityfirstColStyle"/> 
      <tr><td width="{$firstColWidth}" class="{$entityfirstColStyle}">
      Additional Info:</td><td width="{$secondColWidth}">
        <xsl:call-template name="text"/>
      </td></tr>
  </xsl:template>
  

</xsl:stylesheet>
