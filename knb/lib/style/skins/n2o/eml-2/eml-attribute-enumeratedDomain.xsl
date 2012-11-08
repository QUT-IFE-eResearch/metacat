<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-attribute-enumeratedDomain.xsl,v $'
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: berkley $'
  *     '$Date: 2004-07-27 00:28:44 $'
  * '$Revision: 1.2 $'
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

  <xsl:template name="nonNumericDomain">
  </xsl:template>

  <xsl:template match="nonNumericDomain">
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>

    <xsl:if test="$node/enumeratedDomain">
      <dl class="enumeratedDomain section">
        <dt><xsl:text>Enumerated Domain</xsl:text></dt>
        <dd><xsl:apply-templates select="$node/enumeratedDomain"/></dd>
      </dl>
    </xsl:if>
    <xsl:if test="$node/textDomain">
      <dl class="textDomain section">
        <dt>Text Domain</dt>
        <dd><xsl:apply-templates select="$node/textDomain"/></dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="textDomain">
    <table>
      <tr class="textDomain_definition">
        <td class="label">Definition</td>
        <td><xsl:value-of select="definition"/></td>
      </tr>
      <xsl:for-each select="pattern">
        <tr class="textDomain_pattern">
          <td class="label">Pattern</td>
          <td><xsl:value-of select="."/></td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="source">
        <tr class="textDomain_source">
          <td class="label">Source</td>
          <td><xsl:value-of select="."/></td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="enumeratedDomain">
    <xsl:apply-templates select="codeDefinition"/>
    <xsl:apply-templates select="externalCodeSet"/>
    <xsl:apply-templates select="entityCodeList"/>
  </xsl:template>

  <xsl:template match="codeDefinition">
    <dl class="codeDefinition centity"><dt>Code Definition</dt>
      <dd>
        <dl>
          <dt>Order</dt>
          <dd><xsl:value-of select="./@order"/></dd>
        </dl>
        <dl>
          <dt>Code</dt>
          <dd><xsl:value-of select="code"/></dd>
        </dl>
        <dl>
          <dt>Definition</dt>
          <dd><xsl:value-of select="definition"/></dd>
        </dl>
        <dl>
          <dt>Source</dt>
          <dd><xsl:value-of select="source"/></dd>
        </dl>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="externalCodeSet">
    <dl class="externalCodeSet centity"><dt>External Set</dt>
      <dd>
        <dl>
          <dt>Set Name</dt>
          <dd><xsl:value-of select="codesetName"/></dd>
        </dl>
        <xsl:if test="citation">
          <dl>
            <dt>Citation</dt>
            <dd>
              <xsl:for-each select="citation">
                <xsl:call-template name="citation"/>
              </xsl:for-each>
            </dd>
          </dl>
        </xsl:if>
        <xsl:if test="codesetURL">
          <dl>
            <dt>URL</dt>
            <dd>
              <xsl:for-each select="codesetURL">
                <a><xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a><br/>
              </xsl:for-each>
            </dd>
          </dl>
        </xsl:if>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="entityCodeList">
    <dl class="entityCodeList centity"><dt>Enumerated Domain (Entity)</dt>
      <dd>
        <dl>
          <dt>Entity Reference</dt>
          <dd><xsl:value-of select="entityReference"/></dd>
        </dl>
        <dl>
          <dt>Attribute Value Reference</dt>
          <dd><xsl:value-of select="valueAttributeReference"/></dd>
        </dl>
        <dl>
          <dt>Attribute Definition Reference</dt>
          <dd><xsl:value-of select="definitionAttributeReference"/></dd>
        </dl>
        <xsl:if test="orderAttributeReference">
          <dl>
            <dt>Attribute Definition Order</dt>
            <dd><xsl:value-of select="orderAttributeReference"/></dd>
          </dl>
        </xsl:if>
      </dd>
    </dl>
  </xsl:template>

</xsl:stylesheet>
