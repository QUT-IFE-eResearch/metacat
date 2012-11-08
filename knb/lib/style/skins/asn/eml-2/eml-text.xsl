<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-text.xsl,v $'
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
<!-- This module is for text module in eml2 document. It is a table and self contained-->

  <xsl:template name="text">
    <xsl:if test="(section and normalize-space(section)!='') or (para and normalize-space(para)!='')">
      <div>
        <xsl:apply-templates  select="." mode="text"/>
      </div>
    </xsl:if>
  </xsl:template>


  <!-- *********************************************************************** -->
  <!-- Template for section-->
  <xsl:template match="section" mode="text">
    <xsl:if test="normalize-space(.)!=''">
      <xsl:if test="title and normalize-space(title)!=''">
        <div class="sectionTitle"><xsl:value-of select="title"/></div>
      </xsl:if>
      <xsl:if test="para and normalize-space(para)!=''">
        <xsl:apply-templates select="para" mode="lowlevel"/>
      </xsl:if>
      <xsl:if test="section and normalize-space(section)!=''">
        <xsl:apply-templates select="section" mode="lowlevel"/>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <!-- Section template for low level. Cteate a nested table and second column -->
  <xsl:template match="section" mode="lowlevel">
    <div>
      <xsl:if test="title and normalize-space(title)!=''">
        <div class="sectionTitle"><xsl:value-of select="title"/></div>
      </xsl:if>
      <xsl:if test="para and normalize-space(para)!=''">
        <xsl:apply-templates select="para" mode="lowlevel"/>
      </xsl:if>
      <xsl:if test="section and normalize-space(section)!=''">
        <xsl:apply-templates select="section" mode="lowlevel"/>
      </xsl:if>
    </div>
  </xsl:template>

  <!-- para template for text mode-->
  <xsl:template match="para" mode="text">
    <p><xsl:apply-templates mode="lowlevel"/></p>
  </xsl:template>

  <!-- para template without table structure. It does actually transfer.
       Currently, only get the text and it need more revision-->
  <xsl:template match="para" mode="lowlevel">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
