<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-party.xsl,v $'
  *      Authors: Matthew Brooke
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2007-11-01 22:45:17 $'
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

  <xsl:output method="html" encoding="utf-8" indent="yes" />
  <!-- This module is for party member and it is self contained-->

  <xsl:template name="party">
    <dl class="party"><dt></dt>
      <dd>
        <xsl:choose>
          <xsl:when test="references!=''">
            <xsl:variable name="ref_id" select="references"/>
            <xsl:variable name="references" select="$ids[@id=$ref_id]" />
              <xsl:for-each select="$references">
                <xsl:apply-templates mode="party"/>
              </xsl:for-each>
              <xsl:call-template name="partyrole"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates mode="party"/>
            <xsl:call-template name="partyrole"/>
          </xsl:otherwise>
        </xsl:choose>
      </dd>
    </dl>
  </xsl:template>

  <!-- *********************************************************************** -->

  <xsl:template match="individualName" mode="party">
    <xsl:if test="normalize-space(.)!=''">
      <dl><dt>Individual:</dt>
        <dd>
          <b><xsl:value-of select="./salutation"/><xsl:text> </xsl:text>
          <xsl:value-of select="./givenName"/><xsl:text> </xsl:text>
          <xsl:value-of select="./surName"/></b>&#160;| 
					<a><xsl:attribute name="href">http://trove.nla.gov.au/article/result?q=%22<xsl:value-of select="../electronicMailAddress"/>%22</xsl:attribute>
                    View Publications</a>
        </dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="organizationName" mode="party">
    <xsl:if test="normalize-space(.)!=''">
      <dl><dt>Organization:</dt>
        <dd>
          <xsl:choose>
            <xsl:when test="boolean(../individualName) or boolean(../positionName)">
              <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise>
              <b><xsl:value-of select="."/></b>
            </xsl:otherwise>
          </xsl:choose>
        </dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="positionName" mode="party">
    <xsl:if test="normalize-space(.)!=''">
      <dl><dt>Position:</dt>
        <dd>
          <xsl:choose>
            <xsl:when test="boolean(../individualName)">
              <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise>
              <b><xsl:value-of select="."/></b>
            </xsl:otherwise>
          </xsl:choose>
        </dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="address" mode="party">
    <xsl:if test="normalize-space(.)!=''">
      <xsl:call-template name="addressCommon"/>
    </xsl:if>
  </xsl:template>

  <!-- This template will be call by other place-->
  <xsl:template name="address">
    <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <xsl:call-template name="addressCommon"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="addressCommon"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="addressCommon">
    <xsl:if test="normalize-space(.)!=''">
    <dl><dt>Address:</dt>
      <dd>
        <xsl:for-each select="deliveryPoint">
          <xsl:value-of select="."/><xsl:text>, </xsl:text><br/>
        </xsl:for-each>
        <!-- only include comma if city exists... -->
        <xsl:if test="normalize-space(city)!=''">
          <xsl:value-of select="city"/><xsl:text>, </xsl:text><br/>
        </xsl:if>
        <xsl:if test="normalize-space(administrativeArea)!='' or normalize-space(postalCode)!=''">
          <xsl:value-of select="administrativeArea"/><xsl:text> </xsl:text><xsl:value-of select="postalCode"/><xsl:text> </xsl:text><br/>
        </xsl:if>
        <xsl:if test="normalize-space(country)!=''">
          <xsl:value-of select="country"/>
        </xsl:if>
      </dd>
    </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="phone" mode="party">
    <dl><dt>Phone:</dt>
      <dd>
        <xsl:value-of select="."/>
        <xsl:if test="normalize-space(./@phonetype)!=''">
          <xsl:text> (</xsl:text><xsl:value-of select="./@phonetype"/><xsl:text>)</xsl:text>
        </xsl:if>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="electronicMailAddress" mode="party">
    <xsl:if test="normalize-space(.)!=''">
      <dl><dt>Email Address:</dt>
        <dd>
          <xsl:if test="$withHTMLLinks='1'">
            <a><xsl:attribute name="href">mailto:<xsl:value-of select="."/></xsl:attribute><xsl:value-of select="./entityName"/><xsl:value-of select="."/></a>
          </xsl:if>
          <xsl:if test="$withHTMLLinks='0'">
            <xsl:value-of select="."/>
          </xsl:if>
        </dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="onlineUrl" mode="party">
    <xsl:if test="normalize-space(.)!=''">
      <dl><dt>Web Address:</dt>
        <dd>
          <xsl:if test="$withHTMLLinks='1'">
            <a><xsl:attribute name="href"><xsl:if test="not(contains(.,':/'))">http://</xsl:if><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="./entityName"/><xsl:value-of select="."/></a>
          </xsl:if>
          <xsl:if test="$withHTMLLinks='0'">
            <xsl:value-of select="."/>
          </xsl:if>
        </dd>
      </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="userId" mode="party">
    <xsl:if test="normalize-space(.)!=''">
      <dl><dt>Id:</dt><dd><xsl:value-of select="."/></dd>
      </dl>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="partyrole">
    <xsl:for-each select="role">
      <xsl:if test="normalize-space(.)!=''">
        <dl><dt>Role:</dt><dd><xsl:value-of select="."/></dd>
        </dl>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template match="text()" mode="party" />
  
</xsl:stylesheet>
