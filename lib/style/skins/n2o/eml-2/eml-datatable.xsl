<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-datatable.xsl,v $'
  *      Authors: Jivka Bojilova
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2008-12-10 01:32:50 $'
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
  * convert an XML file that is valid with respect to the eml-file.dtd
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" encoding="utf-8" indent="yes" />
  <!-- This module is for datatable module-->
  <xsl:template match="dataTable" mode="lastInfo">
    <xsl:apply-templates select="caseSensitive"/>
    <xsl:apply-templates select="numberOfRecords"/>
  </xsl:template>

  <xsl:template name="dataTable">
  </xsl:template>

  <xsl:template match="caseSensitive">
    <dl class="row"><dt>Case Sensitive?</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="numberOfRecords">
    <dl class="row"><dt>Number Of Records:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

</xsl:stylesheet>
