<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-method.xsl,v $'
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

  <xsl:template name="method">
  <div>
    <xsl:for-each select="methodStep">
      <dl class="centity">
        <dt><b><xsl:text>Step </xsl:text><xsl:value-of select="position()"/>:</b></dt>
        <dd>
          <xsl:call-template name="step"/>
          <xsl:for-each select="dataSource">
            <xsl:apply-templates mode="dataset"/>
          </xsl:for-each>
        </dd>
      </dl>
    </xsl:for-each>
    <xsl:for-each select="sampling">
      <xsl:call-template name="sampling"/>
    </xsl:for-each>
    <xsl:for-each select="qualityControl">
      <dl><dt><b><xsl:text>Quality Control Step </xsl:text><xsl:value-of select="position()"/>:</b></dt>
        <dd><xsl:call-template name="step"/></dd>
      </dl>
    </xsl:for-each>
  </div>
  </xsl:template>


 <!-- *********************************************
      Sampling
      *********************************************-->

  <xsl:template name="sampling">
    <xsl:for-each select="studyExtent">
      <xsl:for-each select="coverage">
        <dl class="row"><dt>Sampling Coverage:</dt>
          <dd><xsl:call-template name="coverage"/></dd>
        </dl>
      </xsl:for-each>
      <xsl:for-each select="description">
        <dl class="row"><dt>Sampling Area And Frequency:</dt>
          <dd><xsl:call-template name="text"/></dd>
        </dl>
      </xsl:for-each>
    </xsl:for-each>
    <xsl:for-each select="samplingDescription">
      <dl class="row">
        <dt>Sampling Description:</dt>
        <dd><xsl:call-template name="text"/></dd>
      </dl>
    </xsl:for-each>
    <xsl:for-each select="spatialSamplingUnits">
      <xsl:call-template name="spatialSamplingUnits"/>
    </xsl:for-each>
    <xsl:for-each select="citation">
      <dl class="row">
        <dt>Sampling Citation:</dt>
        <dd><xsl:call-template name="citation"/></dd>
      </dl>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="spatialSamplingUnits">
    <xsl:for-each select="referenceEntityId">
      <dl><dt>Sampling Unit Reference:</dt><dd><xsl:value-of select="."/></dd></dl>
    </xsl:for-each>
    <xsl:for-each select="coverage">
      <dl>
        <dt>Sampling Unit Location:</dt>
        <dd><xsl:call-template name="coverage"/></dd>
      </dl>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>

