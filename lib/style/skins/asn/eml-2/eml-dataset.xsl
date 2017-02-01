<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-dataset.xsl,v $'
  *      Authors: Matt Jones
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2008-12-09 22:44:43 $'
  * '$Revision: 1.6 $'
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
<xsl:import href="eml-datatable-access.xsl"/>

<xsl:output method="html" encoding="utf-8" indent="yes" />

<xsl:template match="dataset" mode="dataset">
	
	<div id="DocumentLicence" style="display:none">
		<xsl:value-of select="intellectualRights/para"/>
	</div>
	<div id="PackageTitle" style="display:none">
		<xsl:value-of select="title"/>
	</div>
	
  <xsl:variable name="ref_id" select="references"/>
  <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
  <xsl:for-each select="$node[not(references)]">
    <xsl:if test="$withEntityLinks='1' or $displaymodule='printall'">
      <xsl:call-template name="datasetentity"/>
    </xsl:if>
    <xsl:call-template name="datasetresource"/>
    <xsl:if test="not($ref_id)">
      <xsl:call-template name="datasetaccess"/>
    </xsl:if>
    <xsl:call-template name="datasetpurpose"/>
    <xsl:call-template name="datasetmaintenance"/>
    <xsl:call-template name="datasetcontact"/>
    <xsl:call-template name="datasetpublisher"/>
    <xsl:call-template name="datasetpubplace"/>
    <xsl:call-template name="datasetmethod"/>
    <xsl:call-template name="datasetproject"/>
  </xsl:for-each>
</xsl:template>

<xsl:template name="datasetresource">
  <xsl:call-template name="resource">
		<xsl:with-param name="hideCitationInfo" select="true()"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="datasetpurpose">
  <xsl:if test="purpose">
  <dl class="purpose section">
    <dt><xsl:text>Purpose:</xsl:text></dt>
    <dd>
      <xsl:for-each select="purpose">
        <xsl:call-template name="text"/>
      </xsl:for-each>
    </dd>
  </dl>
  </xsl:if>
</xsl:template>

<xsl:template name="datasetmaintenance">
  <xsl:if test="maintenance">
  <dl class="maintenance section">
    <dt><xsl:text>Maintenance:</xsl:text></dt>
    <dd>
      <xsl:for-each select="maintenance">
        <dl class="centity"><dt></dt>
          <dd>
            <dl class="row">
              <dt>Description:</dt>
              <dd>
                <xsl:for-each select="description">
                  <xsl:call-template name="text"/>
                </xsl:for-each>
              </dd>
            </dl>
            <dl class="row">
              <dt>Frequency:</dt>
              <dd><xsl:value-of select="maintenanceUpdateFrequency"/></dd>
            </dl>
            <xsl:if test="changeHistory">
            <dl class="row">
              <dt>History:</dt>
              <dd>
                <xsl:for-each select="changeHistory">
                  <xsl:call-template name="historydetails"/>
                </xsl:for-each>
              </dd>
            </dl>
            </xsl:if>
          </dd>
        </dl>
      </xsl:for-each>
    </dd>
  </dl>
  </xsl:if>
</xsl:template>

<xsl:template name="historydetails">
  <dl class="centity"><dt></dt>
    <dd>
      <dl><dt>scope:</dt><dd><xsl:value-of select="changeScope"/></dd></dl>
      <dl><dt>old value:</dt><dd><xsl:value-of select="oldValue"/></dd></dl>
      <dl><dt>change date:</dt><dd><xsl:value-of select="changeDate"/></dd></dl>
      <xsl:if test="comment and normalize-space(comment)!=''">
        <dl><dt>comment:</dt><dd><xsl:value-of select="comment"/></dd></dl>
      </xsl:if>
    </dd>
  </dl>
</xsl:template>

<xsl:template name="datasetcontact">
  <xsl:if test="contact">
    <dl class="contact section"><dt><xsl:text>Contact(s)</xsl:text></dt>
      <dd>
        <xsl:for-each select="contact">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </dd>
    </dl>
  </xsl:if>
</xsl:template>

<xsl:template name="datasetpublisher">
  <xsl:if test="publisher">
    <dl class="publisher section">
      <dt><xsl:text>Publisher</xsl:text></dt>
      <dd>
        <xsl:for-each select="publisher">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </dd>
    </dl>
  </xsl:if>
</xsl:template>

<xsl:template name="datasetpubplace">
  <xsl:if test="pubPlace">
    <dl class="pubPlace section">
      <dt><xsl:text>Publish Place:</xsl:text></dt>
      <dd><ul>
        <xsl:for-each select="pubPlace">
          <li><xsl:value-of select="."/></li>
        </xsl:for-each>
      </ul></dd>
    </dl>
  </xsl:if>
</xsl:template>

<xsl:template name="datasetmethod">
  <xsl:if test="methods">
    <dl class="methods section">
      <dt><xsl:text>Methods Info</xsl:text></dt>
      <dd>
        <xsl:for-each select="methods">
          <xsl:call-template name="method"/>
        </xsl:for-each>
      </dd>
    </dl>
  </xsl:if>
</xsl:template>

<xsl:template name="datasetproject">
  <xsl:if test="project">
    <dl class="project section">
      <dt><xsl:text>Project Info</xsl:text></dt>
      <dd>
        <xsl:for-each select="project">
          <xsl:call-template name="project"/>
        </xsl:for-each>
      </dd>
    </dl>
  </xsl:if>
</xsl:template>

  <xsl:template name="datasetaccess">
    <xsl:for-each select="access">
      <xsl:call-template name="access"/>
    </xsl:for-each>
  </xsl:template>

<xsl:template name="datasetentity" >
  <xsl:if test="$displaymodule='printall' or $displaymodule='dataset'">
    <xsl:call-template name="xml"/>
  </xsl:if>
  <div class="datasetEntity">
    <xsl:call-template name="listEntities">
      <xsl:with-param name="title">Data Table</xsl:with-param>
      <xsl:with-param name="entity" select="dataTable"/>
    </xsl:call-template>
    <xsl:call-template name="listEntities">
      <xsl:with-param name="title">Spatial Raster</xsl:with-param>
      <xsl:with-param name="entity" select="spatialRaster"/>
    </xsl:call-template>
    <xsl:call-template name="listEntities">
      <xsl:with-param name="title">Spatial Vector</xsl:with-param>
      <xsl:with-param name="entity" select="spatialVector"/>
    </xsl:call-template>
    <xsl:call-template name="listEntities">
      <xsl:with-param name="title">Stored Procedure</xsl:with-param>
      <xsl:with-param name="entity" select="storedProcedure"/>
    </xsl:call-template>
    <xsl:call-template name="listEntities">
      <xsl:with-param name="title">View</xsl:with-param>
      <xsl:with-param name="entity" select="view"/>
    </xsl:call-template>
    <xsl:call-template name="listEntities">
      <xsl:with-param name="title">Other Entity</xsl:with-param>
      <xsl:with-param name="entity" select="otherEntity"/>
    </xsl:call-template>
  </div>
</xsl:template>

  <xsl:template name="entityurl">
    <xsl:param name="showtype"/>
    <xsl:param name="type"/>
    <xsl:param name="index"/><div id="inDataSet" style="display:none">true</div>
    <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <div>
            Metadata: 
            <a>
              <xsl:attribute name="href"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$docid"/>&amp;displaymodule=entity&amp;entitytype=<xsl:value-of select="$type"/>&amp;entityindex=<xsl:value-of select="$index"/></xsl:attribute>
              <b><xsl:value-of select="./physical/objectName"/></b>
            </a>
          </div>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <div>
          <xsl:value-of select="./entityName"/> 
         <br/>(<a><xsl:attribute name="href"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$docid"/>&amp;displaymodule=entity&amp;entitytype=<xsl:value-of select="$type"/>&amp;entityindex=<xsl:value-of select="$index"/></xsl:attribute>View Metadata</a> 
          <xsl:if test="./physical/distribution/online/url">
            <xsl:variable name="URL" select="./physical/distribution/online/url"/>
            <xsl:text> | </xsl:text>
            <!--<a target="_blank">-->
              <xsl:choose>
                <xsl:when test="starts-with($URL,'ecogrid')">
                  <xsl:variable name="URL1" select="substring-after($URL, 'ecogrid://')"/>
                  <xsl:variable name="dataDocID" select="substring-after($URL1, '/')"/>
                  <!--<xsl:attribute name="href"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$dataDocID"/></xsl:attribute>-->
				  <xsl:call-template name="datasetaccesslist">
		       		<xsl:with-param name="dUrl"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$dataDocID"/></xsl:with-param>
		       		<xsl:with-param name="dataId"><xsl:value-of select="$docid"/></xsl:with-param>
		       	  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <!--<xsl:attribute name="href"><xsl:value-of select="$URL"/></xsl:attribute>-->
				  <xsl:call-template name="datasetaccesslist">
		       		<xsl:with-param name="dUrl"><xsl:value-of select="$URL"/></xsl:with-param>
		       		<xsl:with-param name="dataId"><xsl:value-of select="$docid"/></xsl:with-param>
		       	  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
              <!--Download File 
              <img src="/knb/style/images/page_white_put.png" style="margin:0px 0px; padding:0px;" border="0" alt="download"/>
            </a>--> | <xsl:call-template name="viewLicence"/>
          </xsl:if>)
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="text()" mode="dataset" />
  <xsl:template match="text()" mode="resource" />
  
  <xsl:template name="listEntities">
    <xsl:param name="title"/>
    <xsl:param name="entity"/>
    <xsl:if test="count($entity) > 0">
      <xsl:variable name="entitytype" select="local-name($entity)"/>
      <dl class="{$entitytype}List section">
        <dt><xsl:value-of select="$title"/></dt>
        <dd>
          <xsl:choose>
          <xsl:when test="$displaymodule!='printall'">
            <ul>
            <xsl:for-each select="$entity">
              <li>
              <xsl:call-template name="entityurl">
                <xsl:with-param name="type" select="$entitytype"/>
                <xsl:with-param name="showtype" select="$title"/>
                <xsl:with-param name="index" select="position()"/>
              </xsl:call-template>
              </li>
            </xsl:for-each>
            </ul>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="$entity">
              <xsl:call-template name="entity"/>
            </xsl:for-each>
<!--            
              <xsl:for-each select="../.">
              <xsl:call-template name="chooseentity">
                <xsl:with-param name="entitytype" select="$entitytype"/>
                <xsl:with-param name="entityindex" select="position()"/>
              </xsl:call-template>
              </xsl:for-each>
-->
          </xsl:otherwise>
          </xsl:choose>
        </dd>
      </dl>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
