<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-coverage.xsl,v $'
  *      Authors: Matthew Brooke
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: sgarg $'
  *     '$Date: 2006-06-15 18:57:05 $'
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
  <xsl:import href="eml-literature.xsl"/>
  <xsl:output method="html" encoding="utf-8" indent="yes" />

  <!-- This module is for coverage and it is self contained(It is a table
       and will handle reference by it self)-->
  <xsl:template name="coverage">
    <div class="coverage">
      <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <xsl:call-template name="listCoverage"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="listCoverage"/>
      </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template name="listCoverage"> 
    <xsl:if test="geographicCoverage">
      <dl class="geographicCoverage section">
        <dt><xsl:text>Geographic Coverage</xsl:text></dt>
        <dd>
          <xsl:for-each select="geographicCoverage">
            <xsl:call-template name="geographicCoverage"/>
          </xsl:for-each>
        </dd>
      </dl>
    </xsl:if>
    <xsl:if test="temporalCoverage">
      <dl class="temporalCoverage section">
        <dt><xsl:text>Temporal Coverage</xsl:text></dt>
        <dd>
          <xsl:for-each select="temporalCoverage">
            <xsl:call-template name="temporalCoverage"/>
          </xsl:for-each>
        </dd>
      </dl>
    </xsl:if>
    <xsl:if test="taxonomicCoverage">
      <dl class="taxonomicCoverage section">
        <dt><xsl:text>Taxonomic Coverage</xsl:text></dt>
        <dd>
          <xsl:for-each select="taxonomicCoverage">
            <xsl:call-template name="taxonomicCoverage"/>
          </xsl:for-each>
        </dd>
      </dl>
    </xsl:if>
  </xsl:template>

 <!-- ********************************************************************* -->
 <!-- **************  G E O G R A P H I C   C O V E R A G E  ************** -->
 <!-- ********************************************************************* -->
  <xsl:template name="geographicCoverage">
    <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <xsl:call-template name="geographicCovCommon"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="geographicCovCommon"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="geographicCovCommon">
    <dl class="centity">
      <dt></dt>
      <dd>
        <xsl:apply-templates select="geographicDescription"/>
        <xsl:apply-templates select="boundingCoordinates"/>
        <xsl:for-each select="datasetGPolygon">
          <xsl:if test="datasetGPolygonOuterGRing">
            <xsl:apply-templates select="datasetGPolygonOuterGRing"/>
          </xsl:if>
          <xsl:if test="datasetGPolygonExclusionGRing">
            <xsl:apply-templates select="datasetGPolygonExclusionGRing"/>
          </xsl:if>
        </xsl:for-each>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="geographicDescription">
    <dl>
      <dt><xsl:text>Geographic Description:</xsl:text></dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="boundingCoordinates">
    <dl class="boundingCoordinates">
      <dt><xsl:text>Bounding Coordinates:</xsl:text></dt>
      <dd>
        <xsl:apply-templates select="westBoundingCoordinate"/>
        <xsl:apply-templates select="eastBoundingCoordinate"/>
        <xsl:apply-templates select="northBoundingCoordinate"/>
        <xsl:apply-templates select="southBoundingCoordinate"/>
        <xsl:apply-templates select="boundingAltitudes"/>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="westBoundingCoordinate">
    <dl class="row">
      <dt><xsl:text>West: &#160;</xsl:text></dt>
      <dd><xsl:value-of select="."/>&#160; degrees</dd>
    </dl>
  </xsl:template>

  <xsl:template match="eastBoundingCoordinate">
    <dl class="row">
      <dt><xsl:text>East: &#160;</xsl:text></dt>
      <dd><xsl:value-of select="."/>&#160; degrees</dd>
    </dl>
  </xsl:template>

  <xsl:template match="northBoundingCoordinate">
    <dl class="row">
      <dt><xsl:text>North: &#160;</xsl:text></dt>
      <dd><xsl:value-of select="."/>&#160; degrees</dd>
    </dl>
  </xsl:template>

  <xsl:template match="southBoundingCoordinate">
    <dl class="row">
      <dt><xsl:text>South: &#160;</xsl:text></dt>
      <dd><xsl:value-of select="."/>&#160; degrees</dd>
    </dl>
  </xsl:template>

  <xsl:template match="boundingAltitudes">
    <dl>
      <dt><xsl:text>Minimum Altitude:</xsl:text></dt>
      <dd><xsl:apply-templates select="altitudeMinimum"/></dd>
    </dl>
    <dl>
      <dt><xsl:text>Maximum Altitude:</xsl:text></dt>
      <dd><xsl:apply-templates select="altitudeMaximum"/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="altitudeMinimum">
     <xsl:value-of select="."/> &#160;<xsl:value-of select="../altitudeUnits"/>
  </xsl:template>

  <xsl:template match="altitudeMaximum">
    <xsl:value-of select="."/> &#160;<xsl:value-of select="../altitudeUnits"/>
  </xsl:template>

  <xsl:template match="datasetGPolygonOuterGRing">
    <dl>
      <dt><xsl:text>G-Ploygon(Outer Ring): </xsl:text></dt>
      <dd>
        <xsl:apply-templates select="gRingPoint"/>
        <xsl:apply-templates select="gRing"/>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="datasetGPolygonExclusionGRing">
    <dl>
      <dt><xsl:text>G-Ploygon(Exclusion Ring): </xsl:text></dt>
      <dd>
        <xsl:apply-templates select="gRingPoint"/>
        <xsl:apply-templates select="gRing"/>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="gRing">
    <xsl:text>(GRing) &#160;</xsl:text>
    <xsl:text>Latitude: </xsl:text>
    <xsl:value-of select="gRingLatitude"/>,
    <xsl:text>Longitude: </xsl:text>
    <xsl:value-of select="gRingLongitude"/><br/>
  </xsl:template>

  <xsl:template match="gRingPoint">
    <xsl:text>Latitude: </xsl:text>
    <xsl:value-of select="gRingLatitude"/>,
    <xsl:text>Longitude: </xsl:text>
    <xsl:value-of select="gRingLongitude"/><br/>
  </xsl:template>

<!-- ********************************************************************* -->
<!-- ****************  T E M P O R A L   C O V E R A G E  **************** -->
<!-- ********************************************************************* -->

  <xsl:template name="temporalCoverage">
    <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <xsl:call-template name="temporalCovCommon"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="temporalCovCommon"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="temporalCovCommon" >
    <dl class="centity">
      <dt></dt>
      <dd>
        <xsl:apply-templates select="singleDateTime"/>
        <xsl:apply-templates select="rangeOfDates"/>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="singleDateTime">
    <dl>
      <dt><xsl:text>Date:</xsl:text></dt>
      <dd><xsl:call-template name="singleDateType"/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="rangeOfDates">
    <dl>
      <dt><xsl:text>Begin:</xsl:text></dt>
      <dd><xsl:apply-templates select="beginDate"/></dd>
    </dl>
    <dl>
      <dt><xsl:text>End:</xsl:text></dt>
      <dd><xsl:apply-templates select="endDate"/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="beginDate">
    <xsl:call-template name="singleDateType"/>
  </xsl:template>

  <xsl:template match="endDate">
    <xsl:call-template name="singleDateType"/>
  </xsl:template>

  <xsl:template name="singleDateType">
    <xsl:if test="calendarDate">
      <xsl:value-of select="calendarDate"/>
      <xsl:if test="./time and normalize-space(./time)!=''">
        <xsl:text>&#160; at &#160;</xsl:text>
        <xsl:apply-templates select="time"/>
      </xsl:if>
    </xsl:if>
    <xsl:if test="alternativeTimeScale">
      <xsl:apply-templates select="alternativeTimeScale"/>
    </xsl:if>
  </xsl:template>


  <xsl:template match="alternativeTimeScale">
    <dl><dt><xsl:text>Timescale:</xsl:text></dt><dd><xsl:value-of select="timeScaleName"/></dd></dl>
    <dl><dt><xsl:text>Time estimate:</xsl:text></dt><dd><xsl:value-of select="timeScaleAgeEstimate"/></dd></dl>
    <xsl:if test="timeScaleAgeUncertainty and normalize-space(timeScaleAgeUncertainty)!=''">
      <dl><dt><xsl:text>Time uncertainty:</xsl:text></dt><dd><xsl:value-of select="timeScaleAgeUncertainty"/></dd></dl>
    </xsl:if>
    <xsl:if test="timeScaleAgeExplanation and normalize-space(timeScaleAgeExplanation)!=''">
      <dl><dt><xsl:text>Time explanation:</xsl:text></dt><dd><xsl:value-of select="timeScaleAgeExplanation"/></dd></dl>
    </xsl:if>
    <xsl:if test="timeScaleCitation and normalize-space(timeScaleCitation)!=''">
      <dl><dt><xsl:text>Citation:</xsl:text></dt><dd><xsl:apply-templates select="timeScaleCitation"/></dd></dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="timeScaleCitation">
    <!-- Using citation module here -->
    <xsl:call-template name="citation"/>
  </xsl:template>

<!-- ********************************************************************* -->
<!-- ***************  T A X O N O M I C   C O V E R A G E  *************** -->
<!-- ********************************************************************* -->
  <xsl:template name="taxonomicCoverage">
    <xsl:choose>
    <xsl:when test="references!=''">
      <xsl:variable name="ref_id" select="references"/>
      <xsl:variable name="references" select="$ids[@id=$ref_id]" />
      <xsl:for-each select="$references">
        <xsl:call-template name="taxonomicCovCommon"/>
      </xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="taxonomicCovCommon"/>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="taxonomicCovCommon">
    <xsl:if test="taxonomicSystem">
    <dl class="taxonomicSystem section">
      <dt><xsl:text>Taxonomic System:</xsl:text></dt>
      <dd><xsl:apply-templates select="taxonomicSystem"/></dd>
    </dl>
    </xsl:if>
    <xsl:if test="generalTaxonomicCoverage">
    <dl class="generalTaxonomicCoverage section">
      <dt><xsl:text>General Coverage:</xsl:text></dt>
      <dd><xsl:value-of select="./generalTaxonomicCoverage"/></dd>
    </dl>
    </xsl:if>
    <xsl:if test="taxonomicClassification">
    <dl class="taxonomicClassification section">
      <dt><xsl:text>Taxon:</xsl:text></dt>
      <dd><xsl:apply-templates select="taxonomicClassification"/></dd>
    </dl>
    </xsl:if>
  </xsl:template>

  <xsl:template match="taxonomicSystem">
    <xsl:apply-templates select="./*"/>
  </xsl:template>

  <xsl:template match="classificationSystem">
    <dl class="centity">
      <dt><xsl:text>Classification Citation </xsl:text><xsl:value-of select="position()"/></dt>
      <dd>
        <xsl:for-each select="classificationSystemCitation">
          <xsl:call-template name="citation"/>
        </xsl:for-each>
        <xsl:if test="classificationSystemModifications and normalize-space(classificationSystemModifications)!=''">
          <dl>
            <dt><xsl:text>Modification</xsl:text></dt>
            <dd><xsl:value-of select="classificationSystemModifications"/></dd>
          </dl>
        </xsl:if>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="identificationReference">
    <dl class="row">
      <dt><xsl:text>ID Reference:</xsl:text></dt>
      <dd><xsl:call-template name="citation"/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="identifierName">
    <dl class="row">
      <dt><xsl:text>ID Name:</xsl:text></dt>
      <dd><xsl:call-template name="party"/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="taxonomicProcedures">
    <dl class="row">
      <dt><xsl:text>Procedures:</xsl:text></dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="taxonomicCompleteness">
    <dl class="row">
      <dt><xsl:text>Completeness:</xsl:text></dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="vouchers">
    <dl class="row">
      <dt><xsl:text>Vouchers:</xsl:text></dt>
      <dd>
        <xsl:apply-templates select="specimen"/>
        <xsl:apply-templates select="repository"/>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="specimen">
    <dl class="row">
      <dt><xsl:text>Specimen:</xsl:text></dt>
      <dd><xsl:value-of select="."/></dd>
    </dl>
  </xsl:template>

  <xsl:template match="repository">
    <dl class="row">
      <dt><xsl:text>Repository:</xsl:text></dt>
      <dd>
        <xsl:for-each select="originator">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </dd>
    </dl>
  </xsl:template>


  <xsl:template match="taxonomicClassification">
    <xsl:if test="not(boolean(../taxonomicClassification/taxonomicClassification)) and position()=1">
      <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
        <tr>
          <td class="tablehead" width="30%">Rank Name</td>
          <td class="tablehead" width="30%">Rank Value</td>
          <td class="tablehead" width="40%">Common Names</td>
        </tr>
			</table>
		</xsl:if>	
			
    <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
      <xsl:if test="../taxonomicClassification/taxonomicClassification">
        <tr>
          <th width="30%">Rank Name</th>
          <th width="30%">Rank Value</th>
          <th width="40%">Common Names</th>
        </tr>
      </xsl:if>

      <tr>
        <td width="30%"><xsl:apply-templates select="./taxonRankName" mode="nest"/></td>
        <td width="30%"><xsl:apply-templates select="./taxonRankValue" mode="nest"/></td>
        <td width="40%"><xsl:apply-templates select="commonName" mode="nest"/></td>
      </tr>
      <xsl:apply-templates select="taxonomicClassification" mode="nest"/>
    </table>
  </xsl:template>

  
  
  <xsl:template match="taxonRankName" mode="nest" >
    <xsl:apply-templates select=".." mode="indent"/><xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="taxonomicClassification" mode="indent">
    <xsl:if test="boolean(../../taxonomicClassification)">
      <xsl:text>&#160;&#160;</xsl:text>
      <xsl:apply-templates select=".." mode="indent"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="taxonRankValue" mode="nest">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="commonName" mode="nest">
    <xsl:if test="position() &gt; 1"><xsl:text>, </xsl:text></xsl:if>
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="taxonomicClassification" mode="nest">
		<tr>
			<td width="30%"><xsl:apply-templates select="taxonRankName" mode="nest"/></td>
			<td width="30%"><xsl:apply-templates select="taxonRankValue" mode="nest"/></td>
			<td width="40%"><xsl:apply-templates select="commonName" mode="nest"/></td>
		</tr>
		<xsl:apply-templates select="taxonomicClassification" mode="nest"/>
  </xsl:template>

</xsl:stylesheet>