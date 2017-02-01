<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-protocol.xsl,v $'
  *      Authors: Matthew Brooke
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2008-12-09 22:44:43 $'
  * '$Revision: 1.3 $'
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

  <xsl:template name="protocol">
    <div>
      <xsl:choose>
        <xsl:when test="references!=''">
          <xsl:variable name="ref_id" select="references"/>
          <xsl:variable name="references" select="$ids[@id=$ref_id]" />
          <xsl:for-each select="$references">
            <xsl:call-template name="protocolcommon"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="protocolcommon"/>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template name="protocolcommon">
    <xsl:call-template name="resource">
      <xsl:with-param name="creator">Author(s):</xsl:with-param>
    </xsl:call-template>
    <xsl:for-each select="proceduralStep">
      <dl>
        <dt><xsl:text>Step </xsl:text><xsl:value-of select="position()"/>:</dt>
        <dd><xsl:call-template name="step"/></dd>
      </dl>
    </xsl:for-each>
    <xsl:call-template name="protocolAccess"/>
  </xsl:template>

  <xsl:template name="step">
    <xsl:if test="description">
      <dl>
        <dt>Description:</dt>
        <dd>
          <xsl:for-each select="description">
            <xsl:call-template name="text"/>
          </xsl:for-each>
        </dd>
      </dl>
    </xsl:if>
    <xsl:if test="citation">
      <dl>
        <dt>Citation:</dt>
        <dd>
          <xsl:for-each select="citation">
            <xsl:call-template name="citation"/>
          </xsl:for-each>
        </dd>
      </dl>
    </xsl:if>
    <xsl:if test="protocol">
      <dl>
        <dt>Protocol:</dt>
        <dd>
          <xsl:for-each select="protocol">
            <xsl:call-template name="protocol"/>
          </xsl:for-each>
        </dd>
      </dl>
    </xsl:if>
    <xsl:if test="instrumentation">
      <dl>
        <dt>Instrument(s):</dt>
        <dd><ul>
          <xsl:for-each select="instrumentation">
            <li><xsl:value-of select="."/></li>
          </xsl:for-each>
        </ul></dd>
      </dl>
    </xsl:if>
    <xsl:for-each select="software">
      <div>
        <xsl:call-template name="software"/>
      </div>
    </xsl:for-each>
    <xsl:for-each select="subStep">
      <dl>
        <dt><xsl:text>Substep </xsl:text><xsl:value-of select="position()"/></dt>
        <dd><xsl:call-template name="step"/></dd>
      </dl>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="protocolAccess">
    <xsl:for-each select="access">
      <div>
        <xsl:call-template name="access"/>
      </div>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
