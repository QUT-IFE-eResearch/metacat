<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-spatialraster.xsl,v $'
  *      Authors: Jivka Bojilova
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2008-12-10 01:42:28 $'
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
  * convert an XML file that is valid with respect to the eml-file.dtd
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" encoding="utf-8" indent="yes" />
  <!-- This module is for spatialRaster module-->

  <xsl:template match="spatialRaster" mode="lastInfo">
    <xsl:apply-templates select="spatialReference" mode="complexfield">
      <xsl:with-param name="label">Spatial Reference</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="georeferenceInfo" mode="complexfield">
      <xsl:with-param name="label">Grid Position</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="horizontalAccuracy" mode="complexfield">
      <xsl:with-param name="label">Horizontal Accuracy</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="verticalAccuracy" mode="complexfield">
      <xsl:with-param name="label">Vertical Accuracy</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="cellSizeXDirection" mode="simplefield">
      <xsl:with-param name="label">Cell Size(X)</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="cellSizeYDirection" mode="simplefield">
      <xsl:with-param name="label">Cell Size(Y)</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="numberOfBands" mode="simplefield">
      <xsl:with-param name="label">Number of Bands</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="rasterOrigin" mode="simplefield">
      <xsl:with-param name="label">Origin</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="columns" mode="simplefield">
      <xsl:with-param name="label">Max Raster Objects(X)</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="rows" mode="simplefield">
      <xsl:with-param name="label">Max Raster Objects(Y)</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="verticals" mode="simplefield">
      <xsl:with-param name="label">Max Raster Objects(Z)</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="cellGeometry" mode="simplefield">
      <xsl:with-param name="label">Cell Geometry</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="toneGradation" mode="simplefield">
      <xsl:with-param name="label">Number of Colors</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="scaleFactor" mode="simplefield">
      <xsl:with-param name="label">Scale Factor</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="offset" mode="simplefield">
      <xsl:with-param name="label">Offset</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="imageDescription" mode="complexfield">
      <xsl:with-param name="label">Image Info</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="horizontalAccuracy | verticalAccuracy">
    <xsl:call-template name="dataQuality"/>
  </xsl:template>

  <xsl:template match="spatialReference">
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>

    <xsl:apply-templates select="horizCoordSysName" mode="simplefield">
      <xsl:with-param name="label">Name of Coordinate System</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="horizCoordSysDef"/>
    <xsl:apply-templates select="vertCoordSys/altitudeSysDef" mode="complexfield">
      <xsl:with-param name="label">Altitude System Definition</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="vertCoordSys/depthSysDef" mode="complexfield">
      <xsl:with-param name="label">Depth System Definition</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="horizCoordSysDef">
    <xsl:apply-templates select="geogCoordSys" mode="complexfield">
      <xsl:with-param name="label">
        <xsl:text>Definition of </xsl:text>
        <xsl:value-of select="./@name"/>
        <xsl:text> (Geographic Coordinate System)</xsl:text>
      </xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="projCoordSys"/>
  </xsl:template>

  <xsl:template match="projCoordSys">
    <xsl:apply-templates select="geogCoordSys" mode="complexfield">
      <xsl:with-param name="label">
        <xsl:text>Definition of </xsl:text>
        <xsl:value-of select="../@name"/>
        <xsl:text> (Geographic Coordinate System)</xsl:text>
      </xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="projection" mode="complexfield">
      <xsl:with-param name="label">Projection in Geo Coord. System</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="projection">
    <xsl:for-each select="parameter">
      <xsl:call-template name="simplefield">
        <xsl:with-param name="label"><xsl:value-of select="./@name"/>:</xsl:with-param>
        <xsl:with-param name="value">
          <xsl:value-of select="./@value"/><xsl:text> - </xsl:text>
          <xsl:value-of select="./@description"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="unit">
      <xsl:call-template name="simplefield">
        <xsl:with-param name="label">Unit</xsl:with-param>
        <xsl:with-param name="value" select="./@name"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="altitudeSysDef">
    <xsl:apply-templates select="altitudeDatumName" mode="simplefield">
      <xsl:with-param name="label">Datum</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="altitudeResolution" mode="simplefield">
      <xsl:with-param name="label">Resolution</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="altitudeDistanceUnits" mode="simplefield">
      <xsl:with-param name="label">Distance Unit</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="altitudeEncodingMethod" mode="simplefield">
      <xsl:with-param name="label">Encoding Method</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="depthSysDef">
    <xsl:apply-templates select="depthDatumName" mode="simplefield">
      <xsl:with-param name="label">Datum</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="depthResolution" mode="simplefield">
      <xsl:with-param name="label">Resolution</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="depthDistanceUnits" mode="simplefield">
      <xsl:with-param name="label">Distance Unit</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="depthEncodingMethod" mode="simplefield">
      <xsl:with-param name="label">Encoding Method</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="geogCoordSys">
    <xsl:apply-templates select="datum/@name" mode="simplefield">
      <xsl:with-param name="label">Datum</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="spheroid" mode="complexfield">
      <xsl:with-param name="label">Spheroid</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="primeMeridian" mode="complexfield">
      <xsl:with-param name="label">Prime Meridian</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="unit/@name" mode="simplefield">
      <xsl:with-param name="label">Unit</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="spheroid">
    <xsl:apply-templates select="./@name" mode="simplefield">
      <xsl:with-param name="label">Name</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="./@semiAxisMajor" mode="simplefield">
      <xsl:with-param name="label">Semi Axis Major</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="./@denomFlatRatio" mode="simplefield">
      <xsl:with-param name="label">Denom Flat Ratio</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="primeMeridian">
    <xsl:apply-templates select="./@name" mode="simplefield">
      <xsl:with-param name="label">Name</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="./@longitude" mode="simplefield">
      <xsl:with-param name="label">Longitude</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="georeferenceInfo">
    <xsl:apply-templates select="cornerPoint" mode="complexfield">
      <xsl:with-param name="label">Corner Point</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="controlPoint" mode="complexfield">
      <xsl:with-param name="label">Control Point</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="bilinearFit" mode="complexfield">
      <xsl:with-param name="label">Bilinear Fit</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="cornerPoint">
    <xsl:apply-templates select="corner" mode="simplefield">
      <xsl:with-param name="label">Corner</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="xCoordinate" mode="simplefield">
      <xsl:with-param name="label">X Coordinate</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="yCoordinate" mode="simplefield">
      <xsl:with-param name="label">Y Coordinate</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="pointInPixel" mode="simplefield">
      <xsl:with-param name="label">Point in Pixel</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="controlPoint">
    <xsl:apply-templates select="column" mode="simplefield">
      <xsl:with-param name="label">Column Location</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="row" mode="simplefield">
      <xsl:with-param name="label">Row Location</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="xCoordinate" mode="simplefield">
      <xsl:with-param name="label">X Coordinate</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="yCoordinate" mode="simplefield">
      <xsl:with-param name="label">Y Coordinate</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="pointInPixel" mode="simplefield">
      <xsl:with-param name="label">Point in Pixel</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="bilinearFit">
    <xsl:apply-templates select="xIntercept" mode="simplefield">
      <xsl:with-param name="label">X Intercept</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="xSlope" mode="simplefield">
      <xsl:with-param name="label">X Slope</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="yIntercept" mode="simplefield">
      <xsl:with-param name="label">Y Intercept</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="ySlope" mode="simplefield">
      <xsl:with-param name="label">Y Slope</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="dataQuality">
    <xsl:apply-templates select="accuracyReport" mode="simplefield">
      <xsl:with-param name="label">Report</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="quantitativeAccuracyReport" mode="complexfield">
      <xsl:with-param name="label">Quantitative Report</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="quantitativeAccuracyReport">
    <xsl:apply-templates select="quantitativeAccuracyValue" mode="simplefield">
      <xsl:with-param name="label">Accuracy Value</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="quantitativeAccuracyMethod" mode="complexfield">
      <xsl:with-param name="label">Method</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="imageDescription">
    <xsl:apply-templates select="illuminationElevationAngle" mode="simplefield">
      <xsl:with-param name="label">Illumination Elevation</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="illuminationAzimuthAngle" mode="simplefield">
      <xsl:with-param name="label">Illumination Azimuth</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="imageOrientationAngle" mode="simplefield">
      <xsl:with-param name="label">Image Orientation</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="imagingCondition" mode="simplefield">
      <xsl:with-param name="label">Code Affecting Quality of Image</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="imageQualityCode" mode="simplefield">
      <xsl:with-param name="label">Quality</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="cloudCoverPercentage" mode="simplefield">
      <xsl:with-param name="label">Cloud Coverage</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="preProcessingTypeCode" mode="simplefield">
      <xsl:with-param name="label">PreProcessing</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="compressionGenerationQuality" mode="simplefield">
      <xsl:with-param name="label">Compression Quality</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="triangulationIndicator" mode="simplefield">
      <xsl:with-param name="label">Triangulation Indicator</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="radionmetricDataAvailability" mode="simplefield">
      <xsl:with-param name="label">Availability of Radionmetric Data</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="cameraCalibrationInformationAvailability" mode="simplefield">
      <xsl:with-param name="label">Availability of Camera Calibration Correction</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="filmDistortionInformationAvailability" mode="simplefield">
      <xsl:with-param name="label">Availability of Calibration Reseau</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="lensDistortionInformationAvailability" mode="simplefield">
      <xsl:with-param name="label">Availability of Lens Aberration Correction</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="bandDescription" mode="complexfield">
      <xsl:with-param name="label">Band Description</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="bandDescription">
    <xsl:apply-templates select="sequenceIdentifier" mode="simplefield">
      <xsl:with-param name="label">Sequence Identifier</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="highWavelength" mode="simplefield">
      <xsl:with-param name="label">High Wave Length</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="lowWaveLength" mode="simplefield">
      <xsl:with-param name="label">Low Wave Length</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="waveLengthUnits" mode="simplefield">
      <xsl:with-param name="label">Wave Length Units</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="peakResponse" mode="simplefield">
      <xsl:with-param name="label">Peak Response</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>


  <xsl:template name="spatialRaster">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:param name="spatialrastersubHeaderStyle"/>
    <xsl:param name="docid"/>
    <xsl:param name="entityindex"/>
    <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
      <xsl:choose>
        <xsl:when test="references!=''">
          <xsl:variable name="ref_id" select="references"/>
          <xsl:variable name="references" select="$ids[@id=$ref_id]" />
          <xsl:for-each select="$references">
            <xsl:call-template name="spatialRastercommon">
              <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
              <xsl:with-param name="spatialrastersubHeaderStyle" select="$spatialrastersubHeaderStyle"/>
              <xsl:with-param name="docid" select="$docid"/>
              <xsl:with-param name="entityindex" select="$entityindex"/>
            </xsl:call-template>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="spatialRastercommon">
            <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
            <xsl:with-param name="spatialrastersubHeaderStyle" select="$spatialrastersubHeaderStyle"/>
            <xsl:with-param name="docid" select="$docid"/>
            <xsl:with-param name="entityindex" select="$entityindex"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </table>
  </xsl:template>

  <xsl:template name="spatialRastercommon">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:param name="spatialrastersubHeaderStyle"/>
    <xsl:param name="docid"/>
    <xsl:param name="entityindex"/>
    <xsl:for-each select="entityName">
      <xsl:call-template name="entityName">
        <xsl:with-param name="entityfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="alternateIdentifier">
      <xsl:call-template name="entityalternateIdentifier">
        <xsl:with-param name="entityfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="entityDescription">
      <xsl:call-template name="entityDescription">
        <xsl:with-param name="entityfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="additionalInfo">
      <xsl:call-template name="entityadditionalInfo">
        <xsl:with-param name="entityfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>
    <!-- call physical moduel without show distribution(we want see it later)-->
    <xsl:if test="physical">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Physical Structure Description:
        </td></tr>
      <xsl:for-each select="physical">
        <tr><td colspan="2">
            <xsl:call-template name="physical">
              <xsl:with-param name="physicalfirstColStyle" select="$spatialrasterfirstColStyle"/>
              <xsl:with-param name="notshowdistribution">yes</xsl:with-param>
            </xsl:call-template>
          </td></tr>
      </xsl:for-each>
    </xsl:if>
    <xsl:if test="coverage">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Coverage Description:
        </td></tr>
    </xsl:if>
    <xsl:for-each select="coverage">
      <tr><td colspan="2">
          <xsl:call-template name="coverage">
          </xsl:call-template>
        </td></tr>
    </xsl:for-each>
    <xsl:if test="methods | method">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Method Description:
        </td></tr>
    </xsl:if>
    <xsl:for-each select="methods | method">
      <tr><td colspan="2">
          <xsl:call-template name="method">
            <xsl:with-param name="methodfirstColStyle" select="$spatialrasterfirstColStyle"/>
            <xsl:with-param name="methodsubHeaderStyle" select="$spatialrastersubHeaderStyle"/>
          </xsl:call-template>
        </td></tr>
    </xsl:for-each>
    <xsl:if test="constraint">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Constraint:
        </td></tr>
    </xsl:if>
    <xsl:for-each select="constraint">
      <tr><td colspan="2">
          <xsl:call-template name="constraint">
            <xsl:with-param name="constraintfirstColStyle" select="$spatialrasterfirstColStyle"/>
          </xsl:call-template>
        </td></tr>
    </xsl:for-each>


    <xsl:for-each select="spatialReference">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Spatial Reference:
        </td></tr>
      <xsl:call-template name="spatialReference">
        <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="georeferenceInfo">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Grid Postion:
        </td></tr>
      <xsl:call-template name="georeferenceInfo">
        <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="horizontalAccuracy">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Horizontal Accuracy:
        </td></tr>
      <xsl:call-template name="dataQuality">
        <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="verticalAccuracy">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Vertical Accuracy:
        </td></tr>
      <xsl:call-template name="dataQuality">
        <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="cellSizeXDirection">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Cell Size(X):
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="cellSizeYDirection">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Cell Size(Y):
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="numberOfBands">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Number of Bands:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="rasterOrigin">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Origin:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="columns">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Max Raster Objects(X):
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="rows">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Max Raster Objects(Y):
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="verticals">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Max Raster Objects(Z):
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="cellGeometry">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Cell Geometry:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="toneGradation">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Number of Colors:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="scaleFactor">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Scale Factor:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="offset">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Offset:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="imageDescription">
      <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
      Image Info:
        </td></tr>
      <xsl:call-template name="imageDescription">
        <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
      </xsl:call-template>
    </xsl:for-each>


    <xsl:if test="$withAttributes='1' or $displaymodule='printall'">
      <xsl:for-each select="attributeList">
        <xsl:call-template name="spatialRasterAttributeList">
          <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
          <xsl:with-param name="spatialrastersubHeaderStyle" select="$spatialrastersubHeaderStyle"/>
          <xsl:with-param name="docid" select="$docid"/>
          <xsl:with-param name="entityindex" select="$entityindex"/>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:if>
    <!-- Here to display distribution info-->
    <xsl:for-each select="physical">
      <xsl:call-template name="spatialRasterShowDistribution">
        <xsl:with-param name="docid" select="$docid"/>
        <xsl:with-param name="entityindex" select="$entityindex"/>
        <xsl:with-param name="physicalindex" select="position()"/>
        <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
        <xsl:with-param name="spatialrastersubHeaderStyle" select="$spatialrastersubHeaderStyle"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <!--****************************************************
     spatial reference
    ****************************************************-->
  <xsl:template name="spatialReference">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <xsl:call-template name="spatialReferenceCommon">
            <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="spatialReferenceCommon">
          <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>


  <xsl:template name="spatialReferenceCommon">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:for-each select="horizCoordSysName">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Name of Coordinate System:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="horizCoordSysDef/geogCoordSys">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Definition of <xsl:text> </xsl:text><xsl:value-of select="../@name"/> <xsl:text> </xsl:text> (Geographic Coordinate System):
        </td>
        <td width="{$secondColWidth}">
          <xsl:call-template name="geogCoordSysType">
            <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
          </xsl:call-template>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="horizCoordSysDef/projCoordSys">
      <xsl:for-each select="geogCoordSys">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Definition of<xsl:text> </xsl:text><xsl:value-of select="../../@name"/><xsl:text> </xsl:text>(Geographic Coordinate System):
          </td>
          <td width="{$secondColWidth}">
            <xsl:call-template name="geogCoordSysType">
              <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
            </xsl:call-template>
          </td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="projection">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Projection in Geo Coord. System:
          </td>
          <td width="{$secondColWidth}">
            <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
              <xsl:for-each select="parameter">
                <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                    <xsl:value-of select="./@name"/>:
                  </td>
                  <td width="{$secondColWidth}">
                    <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
                      <tr>
                        <td width="{$firstColWidth}" class="{$secondColStyle}">
                          <xsl:value-of select="./@value"/>
                        </td>
                        <td width="{$secondColWidth}" class="{$secondColStyle}">
                          <xsl:value-of select="./@description"/>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </xsl:for-each>
              <xsl:for-each select="unit">
                <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Unit:
                  </td>
                  <td width="{$secondColWidth}" class="{$secondColStyle}">
                    <xsl:value-of select="./@name"/>
                  </td>
                </tr>
              </xsl:for-each>
            </table>
          </td>
        </tr>
      </xsl:for-each>
    </xsl:for-each>
    <xsl:for-each select="vertCoordSys/altitudeSysDef">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Altitude System Definition:
        </td>
        <td width="{$secondColWidth}">
          <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
            <xsl:for-each select="altitudeDatumName">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Datum:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="altitudeResolution">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Resolution:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="altitudeDistanceUnits">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Distance Unit:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="altitudeEncodingMethod">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Encoding Method:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="vertCoordSys/depthSysDef">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Depth System Definition:
        </td>
        <td width="{$secondColWidth}">
          <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
            <xsl:for-each select="depthDatumName">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Datum:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="depthResolution">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Resolution:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="depthDistanceUnits">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Distance Unit:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="depthEncodingMethod">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Encoding Method:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="geogCoordSysType">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
      <xsl:for-each select="datum">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Datum:
          </td>
          <td width="{$secondColWidth}" class="{$secondColStyle}">
            <xsl:value-of select="./@name"/>
          </td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="spheroid">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Spheroid:
          </td>
          <td width="{$secondColWidth}">
            <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                     Name:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="./@name"/>
                </td>
              </tr>
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                     Semi Axis Major:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="./@semiAxisMajor"/>
                </td>
              </tr>
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                     Denom Flat Ratio:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="./@denomFlatRatio"/>
                </td>
              </tr>
            </table>

          </td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="primeMeridian">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Prime Meridian:
          </td>
          <td width="{$secondColWidth}">
            <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                     Name:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="./@name"/>
                </td>
              </tr>
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                     Longitude:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="./@longitude"/>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="unit">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Unit:
          </td>
          <td width="{$secondColWidth}" class="{$secondColStyle}">
            <xsl:value-of select="./@name"/>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <!--*******************************************************
     georeferenceinfo
    *******************************************************-->
  <xsl:template name="georeferenceInfo">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:for-each select="cornerPoint">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Corner Point:
        </td>
        <td width="{$secondColWidth}">
          <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
            <xsl:for-each select="corner">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Corner:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="xCoordinate">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        xCoordinate:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="yCoordinate">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        yCoordinate:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="pointInPixel">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Point in Pixel:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="controlPoint">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Control Point:
        </td>
        <td width="{$secondColWidth}">
          <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
            <xsl:for-each select="column">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Column Location:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="row">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Row Location:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="xCoordinate">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        xCoordinate:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="yCoordinate">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        yCoordinate:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="pointInPixel">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Point in Pixel:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="bilinearFit">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Bilinear Fit:
        </td>
        <td width="{$secondColWidth}">
          <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
            <xsl:for-each select="xIntercept">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        X Intercept:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="xSlope">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        X Slope:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="yIntercept">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Y Intercept:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
            <xsl:for-each select="ySlope">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                        Y Slope:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="."/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </xsl:for-each>
  </xsl:template>

  <!--********************************************************
   data quality
   ********************************************************-->
  <xsl:template name="dataQuality1">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:for-each select="accuracyReport">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Report:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:if test="quantitativeAccuracyReport">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Quantitative Report:
        </td>
        <td width="{$secondColWidth}">
          <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
            <xsl:for-each select="quantitativeAccuracyReport">
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                       Accuracy Value:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="quantitativeAccuracyValue"/>
                </td>
              </tr>
              <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
                       Method:
                </td>
                <td width="{$secondColWidth}" class="{$secondColStyle}">
                  <xsl:value-of select="quantitativeAccuracyMethod"/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>

  <!--********************************************************
   imageDescription
   *********************************************************-->
  <xsl:template name="imageDescription">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:for-each select="illuminationElevationAngle">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Illumination Elevation:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="illuminationAzimuthAngle">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Illumination Azimuth:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="imageOrientationAngle">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Image Orientation:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="imagingCondition">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Code Affectting Quality of Image:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="imageQualityCode">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Quality:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="cloudCoverPercentage">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Cloud Coverage:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="preProcessingTypeCode">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           PreProcessing:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="compressionGenerationQuality">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Compression Quality:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="triangulationIndicator">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Triangulation Indicator:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="radionmetricDataAvailability">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Availability of Radionmetric Data:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="cameraCalibrationInformationAvailability">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Availability of Camera Calibration Correction:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="filmDistortionInformationAvailability">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Availability of Calibration Reseau:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="lensDistortionInformationAvailability">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Availability of Lens Aberration Correction:
        </td>
        <td width="{$secondColWidth}" class="{$secondColStyle}">
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="bandDescription">
      <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Band Description:
        </td>
        <td width="{$secondColWidth}">
          <xsl:call-template name="bandDescription">
            <xsl:with-param name="spatialrasterfirstColStyle" select="$spatialrasterfirstColStyle"/>
          </xsl:call-template>
        </td>
      </tr>
    </xsl:for-each>
  </xsl:template>

  <!--***********************************************
    band description
    ************************************************-->
  <xsl:template name="bandDescription">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <table xsl:use-attribute-sets="cellspacing" class="{$tabledefaultStyle}" width="100%">
      <xsl:for-each select="sequenceIdentifier">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
          Sequence Identifier:
          </td>
          <td width="{$secondColWidth}" class="{$secondColStyle}">
            <xsl:value-of select="."/>
          </td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="highWavelength">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           High Wave Length:
          </td>
          <td width="{$secondColWidth}" class="{$secondColStyle}">
            <xsl:value-of select="."/>
          </td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="lowWaveLength">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           High Wave Length:
          </td>
          <td width="{$secondColWidth}" class="{$secondColStyle}">
            <xsl:value-of select="."/>
          </td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="waveLengthUnits">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Wave Length Units:
          </td>
          <td width="{$secondColWidth}" class="{$secondColStyle}">
            <xsl:value-of select="."/>
          </td>
        </tr>
      </xsl:for-each>
      <xsl:for-each select="peakResponse">
        <tr><td width="{$firstColWidth}" class="{$spatialrasterfirstColStyle}">
           Peak Response:
          </td>
          <td width="{$secondColWidth}" class="{$secondColStyle}">
            <xsl:value-of select="."/>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="spatialRasterShowDistribution">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:param name="spatialrastersubHeaderStyle"/>
    <xsl:param name="docid"/>
    <xsl:param name="level">entitylevel</xsl:param>
    <xsl:param name="entitytype">spatialRaster</xsl:param>
    <xsl:param name="entityindex"/>
    <xsl:param name="physicalindex"/>

    <xsl:for-each select="distribution">
      <tr><td colspan="2">
          <xsl:call-template name="distribution">
            <xsl:with-param name="docid" select="$docid"/>
            <xsl:with-param name="level" select="$level"/>
            <xsl:with-param name="entitytype" select="$entitytype"/>
            <xsl:with-param name="entityindex" select="$entityindex"/>
            <xsl:with-param name="physicalindex" select="$physicalindex"/>
            <xsl:with-param name="distributionindex" select="position()"/>
            <xsl:with-param name="disfirstColStyle" select="$spatialrasterfirstColStyle"/>
            <xsl:with-param name="dissubHeaderStyle" select="$spatialrastersubHeaderStyle"/>
          </xsl:call-template>
        </td></tr>
    </xsl:for-each>
  </xsl:template>


  <xsl:template name="spatialRasterAttributeList">
    <xsl:param name="spatialrasterfirstColStyle"/>
    <xsl:param name="spatialrastersubHeaderStyle"/>
    <xsl:param name="docid"/>
    <xsl:param name="entitytype">spatialRaster</xsl:param>
    <xsl:param name="entityindex"/>
    <tr><td class="{$spatialrastersubHeaderStyle}" colspan="2">
        <xsl:text>Attribute(s) Info:</xsl:text>
      </td></tr>
    <tr><td colspan="2">
        <xsl:call-template name="attributelist">
          <xsl:with-param name="docid" select="$docid"/>
          <xsl:with-param name="entitytype" select="$entitytype"/>
          <xsl:with-param name="entityindex" select="$entityindex"/>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
