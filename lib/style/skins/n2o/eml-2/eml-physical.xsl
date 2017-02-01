<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-physical.xsl,v $'
  *      Authors: Matthew Brooke
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: sgarg $'
  *     '$Date: 2005-12-16 20:55:16 $'
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

  <xsl:template match="physical">
    <xsl:param name="level">entitylevel</xsl:param>
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>

    <xsl:call-template name="physicalcommon"/>

    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
    <xsl:call-template name="distribution">
      <xsl:with-param name="level" select="$level"/>
      <xsl:with-param name="entitytype" select="$entitytype"/>
      <xsl:with-param name="entityindex" select="$entityindex"/>
      <xsl:with-param name="physicalindex" select="position()"/>
      <xsl:with-param name="physicalnode" select="$node"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="physical" mode="withoutDistribution">
    <xsl:call-template name="physicalcommon"/>
  </xsl:template>

  <xsl:template name="physical">
  </xsl:template>

  <xsl:template name="physicalcommon">
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>

    <xsl:apply-templates select="$node/objectName"/>
    <xsl:apply-templates select="$node/size"/>
    <xsl:apply-templates select="$node/authentication"/>
    <xsl:apply-templates select="$node/compressionMethod"/>
    <xsl:apply-templates select="$node/encodingMethod"/>
    <xsl:apply-templates select="$node/characterEncoding"/>
    <xsl:apply-templates select="$node/dataFormat/textFormat"/>
    <xsl:apply-templates select="$node/dataFormat/externallyDefinedFormat"/>
    <xsl:apply-templates select="$node/dataFormat/binaryRasterFormat"/>
  </xsl:template>

  <xsl:template match="objectName">
    <dl class="row"><dt>Object Name:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="size">
    <dl class="row"><dt>Size:</dt>
      <dd>
        <xsl:value-of select="."/><xsl:text> </xsl:text>
        <xsl:value-of select="./@unit"/>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="authentication">
    <dl class="row"><dt>Authentication:</dt>
      <dd>
        <xsl:value-of select="."/><xsl:text> </xsl:text>
        <xsl:if test="./@method">
          <xsl:text>Calculated By </xsl:text><xsl:value-of select="./@method"/>
        </xsl:if>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="compressionMethod">
    <dl class="row"><dt>Compression Method:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="encodingMethod">
    <dl class="row"><dt>Encoding Method:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="characterEncoding">
    <dl class="row"><dt>Character Encoding:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <!--***********************************************************
      TextFormat templates
      ***********************************************************-->
  <xsl:template match="textFormat">
    <dl class="row"><dt>Text Format:</dt><dd><xsl:apply-templates/></dd></dl>
  </xsl:template>

  <xsl:template match="numHeaderLines">
    <dl>
      <dt>Number of Header Lines:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="numFooterLines">
    <dl>
      <dt>Number of Footer Lines:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="recordDelimiter">
    <dl>
      <dt>Record Delimiter:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="physicalLineDelimiter">
    <dl>
      <dt>Line Delimiter:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="numPhysicalLinesPerRecord">
    <dl>
      <dt>Line Number For One Record:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="maxRecordLength">
    <dl>
      <dt>Maximum Record Length:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="attributeOrientation">
    <dl>
      <dt>Attribute Orientation:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="simpleDelimited">
    <dl>
      <dt>Simple Delimited:</dt>
      <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="complex">
    <dl>
      <dt>Complex Delimited:</dt>
      <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="textFixed">
    <dl>
      <dt>Text Fixed:</dt>
      <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="textDelimited">
    <dl>
      <dt>Text Delimited:</dt>
      <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="collapseDelimiters">
    <dl>
      <dt>Consecutive Delimiters are Single:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="quoteCharacter">
    <dl>
      <dt>Quote Character:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="literalCharacter">
    <dl>
      <dt>Literal Character:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="fieldDelimiter">
    <dl>
      <dt>Field Delimeter:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="fieldWidth">
    <dl>
      <dt>Field Width:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="lineNumber">
    <dl>
      <dt>Line Number:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="fieldStartColumn">
    <dl>
      <dt>Field Start Column:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <!--***********************************************************
      externallyDefinedFormat templates
      ***********************************************************-->
  <xsl:template match="externallyDefinedFormat">
    <dl>
      <dt>Externally Defined Format:</dt>
      <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="formatName">
    <dl>
      <dt>Format Name:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="formatVersion">
    <dl>
      <dt>Format Version:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="citation">
    <dl>
      <dt>Citation:</dt>
      <dd><xsl:call-template name="citation"/></dd>
    </dl>
  </xsl:template>


  <!--***********************************************************
      binaryRasterFormat templates
      ***********************************************************-->
  <xsl:template match="binaryRasterFormat">
    <dl>
      <dt>Binary Raster Format:</dt>
      <dd><xsl:apply-templates/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="rowColumnOrientation">
    <dl>
      <dt>Orientation:</dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="multiBand">
    <dl>
      <dt>Multiple Bands:</dt>
      <dd>
        <dl><dt>Number of Spectral Bands:</dt><dd><xsl:value-of select="./nbands"/></dd></dl>
        <dl><dt>Layout:</dt><dd><xsl:value-of select="./layout"/></dd></dl>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="nbits">
    <dl><dt>Number of Bits (/pixel/band):</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="byteorder">
    <dl><dt>Byte Order:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="skipbytes">
    <dl><dt>Skipped Bytes:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="bandrowbytes">
    <dl><dt>Number of Bytes (/band/row):</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="totalrowbytes">
    <dl><dt>Total Number of Byte (/row):</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

  <xsl:template match="bandgapbytes">
    <dl><dt>Number of Bytes between Bands:</dt><dd><xsl:value-of select="."/></dd></dl>
  </xsl:template>

</xsl:stylesheet>
