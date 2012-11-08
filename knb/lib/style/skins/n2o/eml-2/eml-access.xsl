<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-access.xsl,v $'
  *      Authors: Matt Jones
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2007-11-01 22:41:29 $'
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
  * convert an XML file that is valid with respect to the eml-dataset.dtd
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" encoding="utf-8" indent="yes" />  

  <xsl:template name="access">
    <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <xsl:call-template name="accessCommon"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="accessCommon"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="accessCommon">
    <dl class="access section">
      <dt>Access</dt>
      <dd>
        <dl class="row">
          <dt>Access Control:</dt>
          <dd>
            <dl class="row">
              <dt>Auth System:</dt>
              <dd><xsl:value-of select="./@authSystem"/></dd>
            </dl>
            <dl class="row">
              <dt>Order:</dt>
              <dd><xsl:value-of select="./@order"/></dd>
            </dl>
          </dd>
        </dl>
        <xsl:if test="normalize-space(./@order)='allowFirst' and (allow)">
          <xsl:call-template name="allow_deny">
            <xsl:with-param name="permission" select="'allow'"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="(deny)">
          <xsl:call-template name="allow_deny">
            <xsl:with-param name="permission" select="'deny'"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="normalize-space(./@order)='denyFirst' and (allow)">
          <xsl:call-template name="allow_deny">
            <xsl:with-param name="permission" select="'allow'"/>
          </xsl:call-template>
        </xsl:if>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template name="allow_deny">
    <xsl:param name="permission"/>
    <xsl:choose>
      <xsl:when test="$permission='allow'">
        <dl class="row">
          <dt>Allow:</dt>
          <dd>
            <table xsl:use-attribute-sets="cellspacing" width="100%">
              <xsl:for-each select="allow">
                <xsl:call-template name="accessRuleItem"/>
              </xsl:for-each>
            </table>
          </dd>
        </dl>
      </xsl:when>
      <xsl:otherwise>
        <dl class="row">
          <dt>Deny:</dt>
          <dd>
            <table xsl:use-attribute-sets="cellspacing" width="100%">
              <xsl:for-each select="deny">
                <xsl:call-template name="accessRuleItem"/>
              </xsl:for-each>
            </table>
          </dd>
        </dl>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="accessRuleItem">
    <tr>
      <td>
        <xsl:text>[</xsl:text>
        <xsl:for-each select="./permission">
          <xsl:if test="position() > 1"><xsl:text>, </xsl:text></xsl:if>
          <xsl:value-of select="."/>
        </xsl:for-each>
        <xsl:text>] </xsl:text>
      </td>
      <td>
        <xsl:for-each select="principal">
          <xsl:value-of select="."/><br/>
        </xsl:for-each>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
