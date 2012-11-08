<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-project.xsl,v $'
  *      Authors: Matthew Brooke
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
  * convert an XML file that is valid with respect to the eml-variable.dtd
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" encoding="utf-8" indent="yes" />

  <xsl:template name="project">
    <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <xsl:call-template name="projectcommon"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="projectcommon"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="projectcommon">
    <xsl:call-template name="projecttitle"/>
    <xsl:call-template name="projectpersonnel"/>
    <xsl:call-template name="projectabstract"/>
    <xsl:call-template name="projectfunding"/>
    <xsl:call-template name="projectstudyareadescription"/>
    <xsl:call-template name="projectdesigndescription"/>
    <xsl:call-template name="projectrelatedproject"/>
  </xsl:template>

  <xsl:template name="projecttitle">
    <xsl:for-each select="title">
      <dl class="row"><dt>Title:</dt><dd><xsl:value-of select="../title"/></dd></dl>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="projectpersonnel">
    <dl class="row"><dt>Personnel:</dt>
      <dd>
        <xsl:for-each select="personnel">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template name="projectabstract">
    <xsl:for-each select="abstract">
      <dl class="row">
        <dt>Abstract:</dt>
        <dd><xsl:call-template name="text"/></dd>
      </dl>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="projectfunding">
    <xsl:for-each select="funding">
      <dl class="row">
        <dt>Funding:</dt>
        <dd><xsl:call-template name="text"/></dd>
      </dl>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="projectstudyareadescription">
    <xsl:for-each select="studyAreaDescription">
      <dl class="row">
        <dt>Study Area:</dt>
        <dd>
          <xsl:for-each select="descriptor">
            <xsl:for-each select="descriptorValue">
              <dl class="row">
                <dt><xsl:value-of select="../@name"/></dt>
                <dd>
                  <xsl:choose>
                    <xsl:when test="./@citableClassificationSystem">
                      <xsl:value-of select="."/>&#160;<xsl:value-of select="./@name_or_id"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="."/>&#160;<xsl:value-of select="./@name_or_id"/>&#160;(No Citable Classification System)
                    </xsl:otherwise>
                  </xsl:choose>
                </dd>
              </dl>
            </xsl:for-each>
            <xsl:for-each select="citation">
              <dl class="row">
                <dt>Citation:</dt>
                <dd><xsl:call-template name="citation"/></dd>
              </dl>
            </xsl:for-each>
          </xsl:for-each>
        </dd>
      </dl>
      <xsl:for-each select="citation">
        <dl class="row">
          <dt>Study Area Citation:</dt>
          <dd><xsl:call-template name="citation"/></dd>
        </dl>
      </xsl:for-each>

      <xsl:for-each select="coverage">
        <dl class="row">
          <dt>Study Area Coverage:</dt>
          <dd><xsl:call-template name="coverage"/></dd>
        </dl>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="projectdesigndescription">
    <xsl:for-each select="designDescription">
      <xsl:for-each select="description">
        <dl class="row">
          <dt>Design Description:</dt>
          <dd><xsl:call-template name="text"/></dd>
        </dl>
      </xsl:for-each>
      <xsl:for-each select="citation">
        <dl class="row">
          <dt>Design Citation:</dt>
          <dd><xsl:call-template name="citation"/></dd>
        </dl>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="projectrelatedproject">
    <xsl:for-each select="relatedProject">
      <dl class="row">
        <dt>Related Project:</dt>
        <dd><xsl:call-template name="project"/></dd>
      </dl>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
