<?xml version="1.0"?>
<!--
  *  '$RCSfile: emlroot.xsl,v $'
  *      Authors: Matt Jones
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2008-12-10 01:32:50 $'
  * '$Revision: 1.9 $'
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/02/xpath-function" version="1.0">
  <xsl:import href="eml-access.xsl"/>
  <xsl:import href="eml-additionalmetadata.xsl"/>
  <xsl:import href="eml-attribute.xsl"/>
  <xsl:import href="eml-attribute-enumeratedDomain.xsl"/>
  <xsl:import href="eml-constraint.xsl"/>
  <xsl:import href="eml-coverage.xsl"/>
  <xsl:import href="eml-dataset.xsl"/>
  <xsl:import href="eml-datatable.xsl"/>
  <xsl:import href="eml-distribution.xsl"/>
  <xsl:import href="eml-entity.xsl"/>
  <xsl:import href="eml-identifier.xsl"/>
  <xsl:import href="eml-literature.xsl"/>
  <xsl:import href="eml-method.xsl"/>
  <xsl:import href="eml-otherentity.xsl"/>
  <xsl:import href="eml-party.xsl"/>
  <xsl:import href="eml-physical.xsl"/>
  <xsl:import href="eml-project.xsl"/>
  <xsl:import href="eml-protocol.xsl"/>
  <xsl:import href="eml-resource.xsl"/>
  <xsl:import href="eml-settings.xsl"/>
  <xsl:import href="eml-software.xsl"/>
  <xsl:import href="eml-spatialraster.xsl"/>
  <xsl:import href="eml-spatialvector.xsl"/>
  <xsl:import href="eml-storedprocedure.xsl"/>
  <xsl:import href="eml-text.xsl"/>
  <xsl:import href="eml-view.xsl"/>

  <!-- global variables to store id node set in case to be referenced-->
  <xsl:variable name="ids" select="//*[@id!='']"/>
  
  <xsl:template match="*[local-name()='eml']">
    <div id="tabMetadata">
    <xsl:for-each select="dataset">
      <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references"/>
        <xsl:variable name="references" select="$ids[@id=$ref_id]" />
        <xsl:for-each select="$references">
          <xsl:call-template name="emldataset"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="emldataset"/>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <!-- Additional metadata-->
    <xsl:if test="additionalMetadata">
    <xsl:if test="$displaymodule='additionalmetadata' or $displaymodule='printall' or ($displaymodule='dataset' and $withAdditionalMetadataLink='1')">
      <dl class="additionalMetadata section">
        <dt>Additional Metadata</dt>
        <dd>
          <xsl:if test="$displaymodule='additionalmetadata'">
            <xsl:call-template name="additionalmetadata">
              <xsl:with-param name="index" select="$additionalmetadataindex"/>
              <xsl:with-param name="useindex" select="true()"/>
            </xsl:call-template>
          </xsl:if>
          <xsl:if test="$displaymodule='printall'">
            <xsl:for-each select="additionalMetadata">
              <xsl:call-template name="additionalmetadata">
                <xsl:with-param name="index" select="position()"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:if>
          <xsl:if test="$displaymodule='dataset'">
            <xsl:if test="$withAdditionalMetadataLink='1'">
              <xsl:call-template name="additionalmetadataURL"/>
            </xsl:if>
          </xsl:if>
        </dd>
      </dl>
    </xsl:if>
    </xsl:if>
    
    <xsl:for-each select="citation">
      <xsl:call-template name="emlcitation"/>
    </xsl:for-each>
    <xsl:for-each select="software">
      <xsl:call-template name="emlsoftware"/>
    </xsl:for-each>
    
    <xsl:call-template name="emlprotocol"/>
      
    <xsl:if test="$displaymodule='dataset' or $displaymodule='printall'">
      <!-- Display access information -->
      <xsl:for-each select="access">
        <xsl:call-template name="access"/>
      </xsl:for-each>
    </xsl:if>
    
    </div>
    <div id="tabData"></div>
  </xsl:template>

   <!--********************************************************
                            show dataset
       ********************************************************-->
  <xsl:template name="emldataset">
    <div class="{$datasetStyle}">
      <xsl:if test="$displaymodule='dataset'">
        <xsl:call-template name="datasetpart"/>
      </xsl:if>
      <xsl:if test="$displaymodule='entity'">
        <xsl:call-template name="entitypart"/>
      </xsl:if>
      <xsl:if test="$displaymodule='attribute'">
        <xsl:call-template name="attributepart"/>
      </xsl:if>
      <xsl:if test="$displaymodule='attributedomain'">
        <xsl:call-template name="datasetattributedomain"/>
      </xsl:if>
      <xsl:if test="$displaymodule='attributecoverage'">
        <xsl:call-template name="datasetattributecoverage"/>
      </xsl:if>
      <xsl:if test="$displaymodule='attributemethod'">
        <xsl:call-template name="datasetattributemethod"/>
      </xsl:if>
      <xsl:if test="$displaymodule='inlinedata'">
        <xsl:call-template name="emlinlinedata"/>
      </xsl:if>
      <xsl:if test="$displaymodule='attributedetail'">
        <xsl:call-template name="entityparam"/>
      </xsl:if>
      <xsl:if test="$displaymodule='printall'">
        <xsl:call-template name="printalltemplate"/>
      </xsl:if>
    </div>
  </xsl:template>

  <!--*************** Data set display *************-->
  <xsl:template name="datasetpart">
    <xsl:call-template name="identifier"/>
    <xsl:apply-templates select="." mode="dataset"/>
  </xsl:template>
   

  <!--************ Entity diplay *****************-->
  <xsl:template name="entitypart">
    <div>
      <h3>Entity Description</h3>
      <xsl:call-template name="identifier">
        <xsl:with-param name="packageID" select="../@packageId"/>
        <xsl:with-param name="system" select="../@system"/>
      </xsl:call-template>
      <!-- find the subtree to process -->
      <xsl:call-template name="entityparam"/>
    </div>
  </xsl:template>

   <!--************ Attribute display *****************-->
   <xsl:template name="attributedetailpart">
   </xsl:template>

    <xsl:template name="attributepart">
      <tr><td width="100%">

            <h3>Attributes Description</h3>

      </td></tr>
      <tr>
           <td width="100%">
              <!-- find the subtree to process -->
            <xsl:if test="$entitytype='dataTable'">
              <xsl:for-each select="dataTable">
                  <xsl:if test="position()=$entityindex">
                      <xsl:for-each select="attributeList">
                         <xsl:call-template name="attributelist">
                            <xsl:with-param name="docid" select="$docid"/>
                            <xsl:with-param name="entitytype" select="$entitytype"/>
                            <xsl:with-param name="entityindex" select="$entityindex"/>
                         </xsl:call-template>
                      </xsl:for-each>
                  </xsl:if>
              </xsl:for-each>
            </xsl:if>
          </td>
      </tr>
   </xsl:template>

<!--************************Attribute Domain display module************************-->
<xsl:template name="datasetattributedomain">
  <div>
    <h3>Attribute Domain</h3>
    <!-- find the subtree to process -->
    <xsl:call-template name="entityparam"/>
  </div>
</xsl:template>


   <!--************************Attribute Method display module************************-->
   <xsl:template name="datasetattributemethod">
      <tr><td>
            <h3>Attribute Method</h3>
      </td></tr>
      <tr>
           <td width="100%">
             <!-- find the subtree to process -->
             <xsl:call-template name="entityparam"/>
          </td>
      </tr>
   </xsl:template>


   <!--************************Attribute Coverage display module************************-->
   <xsl:template name="datasetattributecoverage">
     <tr><td>
            <h3>Attribute Coverage</h3>
      </td></tr>
      <tr>
           <td width="100%">
             <!-- find the subtree to process -->
             <xsl:call-template name="entityparam"/>
          </td>
      </tr>
   </xsl:template>


  <!--************************Print all display module************************-->
  <xsl:template name="printalltemplate">
    <!-- find the subtree to process -->
    <xsl:call-template name="datasetpart"/>
  </xsl:template>


<xsl:template name="entityparam">
  <xsl:choose>
  <xsl:when test="$entitytype=''">
    <xsl:variable name="allEntities" select="dataTable|spatialRaster|spatialVector|storedProcedure|view|otherEntity"/>
    <xsl:variable name="entitynode" select="$allEntities[position()=$entityindex]"/>
    <xsl:variable name="entitynodetype" select="local-name($entitynode)"/>
    <xsl:variable name="groupentityindex" select="count(preceding-sibling::*[local-name()=$entitynodetype])+1"/>

    <xsl:choose>
    <xsl:when test="$displaymodule='attributedetail'">
      <xsl:apply-templates select="$entitynode/attributeList" mode="single">
        <xsl:with-param name="attributeindex" select="$attributeindex"/>
        <xsl:with-param name="entitytype" select="$entitynodetype"/>
        <xsl:with-param name="entityindex" select="$groupentityindex"/>
      </xsl:apply-templates>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="chooseentity">
        <xsl:with-param name="entitytype" select="$entitynodetype"/>
        <xsl:with-param name="entityindex" select="$groupentityindex"/>
      </xsl:call-template>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:when>
  <xsl:otherwise>
    <xsl:choose>
    <xsl:when test="$displaymodule='attributedetail'">
      <xsl:variable name="entities" select="*[local-name()=$entitytype]"/>
      <xsl:variable name="entity" select="$entities[position()=$entityindex]"/>
      <xsl:apply-templates select="$entity/attributeList" mode="single">
        <xsl:with-param name="attributeindex" select="$attributeindex"/>
        <xsl:with-param name="entitytype" select="$entitytype"/>
        <xsl:with-param name="entityindex" select="$entityindex"/>
      </xsl:apply-templates>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="chooseentity">
        <xsl:with-param name="entitytype" select="$entitytype"/>
        <xsl:with-param name="entityindex" select="$entityindex"/>
      </xsl:call-template>
    </xsl:otherwise>
 	  </xsl:choose>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="chooseentity" match='dataset'>
  <xsl:param name="entityindex"/>
  <xsl:param name="entitytype"/>
  <xsl:variable name="entities" select="*[local-name()=$entitytype]"/>
  <xsl:variable name="entity" select="$entities[position()=$entityindex]"/>
  <xsl:for-each select="$entity">
    <xsl:choose>
    <xsl:when test="$displaymodule='entity' or $displaymodule='printall'">
        <xsl:call-template name="entity">
          <xsl:with-param name="entitytype" select="$entitytype"/>
          <xsl:with-param name="entityindex" select="$entityindex"/>
        </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="chooseattributelist"/>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template name="chooseattributelist">
  <xsl:for-each select="attributeList">
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
    <xsl:for-each select="$node[not(references)]">
      <xsl:call-template name="chooseattribute"/>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

<xsl:template name="chooseattribute">
  <xsl:variable name="attr" select="attribute[position()=$attributeindex]"/>
  <xsl:if test="$displaymodule='attributedomain'">
    <xsl:apply-templates select="$attr/measurementScale/*/nonNumericDomain"/>
  </xsl:if>
  <xsl:if test="$displaymodule='attributecoverage'">
    <xsl:for-each select="$attr/coverage">
      <xsl:call-template name="coverage"/>
    </xsl:for-each>
  </xsl:if>
  <xsl:if test="$displaymodule='attributemethod'">
    <xsl:for-each select="$attr/methods | $attr/method">
      <xsl:call-template name="method"/>
    </xsl:for-each>
  </xsl:if>
</xsl:template>


<!--*************************Distribution Inline Data display module*****************-->
<xsl:template name="emlinlinedata">
  <div>
    <h3>Inline Data</h3>
    <xsl:if test="$distributionlevel='toplevel'">
      <xsl:call-template name="choosedistribution"/>
    </xsl:if>
    <xsl:if test="$distributionlevel='entitylevel'">
      <xsl:variable name="entities" select="*[local-name()=$entitytype]"/>
      <xsl:variable name="entity" select="$entities[position()=$entityindex]"/>
      <xsl:variable name="ref_id" select="$entity/references"/>
      <xsl:variable name="node" select="$entity | $ids[@id=$ref_id]"/>
      <xsl:for-each select="$node[not(references)]">
        <xsl:call-template name="choosephysical"/>
      </xsl:for-each>
    </xsl:if>
  </div>
</xsl:template>

<xsl:template name="choosephysical">
  <xsl:for-each select="physical[position()=$physicalindex]">
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
    <xsl:for-each select="$node[not(references)]">
      <xsl:call-template name="choosedistribution"/>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

<xsl:template name="choosedistribution">
  <xsl:for-each select="distribution[position()=$distributionindex]">
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
    <xsl:for-each select="$node/inline">
      <pre><xsl:value-of select="."/></pre>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

<!--********************************************************
Citation part
********************************************************-->
<xsl:template name="emlcitation">
  <xsl:choose>
  <xsl:when test="$displaymodule='inlinedata'">
    <xsl:call-template name="emlinlinedata"/>
  </xsl:when>
  <xsl:otherwise>
  <dl>
    <dt>Citation Description</dt>
    <dd>
      <xsl:call-template name="identifier">
        <xsl:with-param name="packageID" select="../@packageId"/>
        <xsl:with-param name="system" select="../@system"/>
      </xsl:call-template>
      <xsl:call-template name="citation"/>
    </dd>
  </dl>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--********************************************************
Software part
********************************************************-->
<xsl:template name="emlsoftware">
  <xsl:choose>
  <xsl:when test="$displaymodule='inlinedata'">
    <xsl:call-template name="emlinlinedata"/>
  </xsl:when>
  <xsl:otherwise>
    <dl class="software section">
      <dt>Software Description</dt>
      <dd>
        <xsl:call-template name="identifier">
          <xsl:with-param name="packageID" select="../@packageId"/>
          <xsl:with-param name="system" select="../@system"/>
        </xsl:call-template>
        <xsl:call-template name="software"/>
      </dd>
    </dl>
  </xsl:otherwise>
  </xsl:choose>
</xsl:template>

  <!--********************************************************
              Protocal part
  ********************************************************-->
  <xsl:template name="emlprotocol">
    <xsl:if test="protocol">
    <xsl:choose>
      <xsl:when test="$displaymodule='inlinedata'">
        <xsl:call-template name="emlinlinedata"/>
      </xsl:when>
      <xsl:otherwise>
        <dl class="protocol section">
          <dt>Protocol Description</dt>
          <dd>
            <xsl:call-template name="identifier">
              <xsl:with-param name="packageID" select="../@packageId"/>
              <xsl:with-param name="system" select="../@system"/>
            </xsl:call-template>
            <xsl:for-each select="protocol">
              <xsl:call-template name="protocol"/>
            </xsl:for-each>
          </dd>
        </dl>
      </xsl:otherwise>
    </xsl:choose>
    </xsl:if>
  </xsl:template>

  <!--********************************************************
            additionalmetadata part
  ********************************************************-->
  <xsl:template name="additionalmetadataURL">
    <ul>
      <xsl:for-each select="additionalMetadata">
        <li><a><xsl:attribute name="href"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$docid"/>&amp;displaymodule=additionalmetadata&amp;additionalmetadataindex=<xsl:value-of select="position()"/></xsl:attribute><b><xsl:text>Item </xsl:text><xsl:value-of select="position()"/></b></a></li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <!--********************************************************
              download xml part
  ********************************************************-->
  <xsl:template name="xml">
    <xsl:param name="index"/>
      <dl class="emlDownload section">
        <dt>Metadata download:</dt>
        <dd>
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="$xmlURI"/><xsl:value-of select="$docid"/>
            </xsl:attribute>
            <b>Ecological Metadata Language (EML) File</b>
          </a>
        </dd>
      </dl>
  </xsl:template>
  
  <xsl:template name="references">
    <xsl:choose>
      <xsl:when test="references!=''">
        <xsl:variable name="ref_id" select="references" />
        <xsl:variable name="ref_node" select="$ids[@id=$ref_id]" />
        <xsl:apply-templates select="$ref_node/*"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="./*"/>
      </xsl:otherwise>
    </xsl:choose>
	</xsl:template>
  
  <xsl:template match="references">
    <xsl:variable name="ref_node" select="$ids[@id=.]" />
    <xsl:apply-templates select="$ids[@id=.]"/>
	</xsl:template>
  
  <xsl:template name="simplefield">
    <xsl:param name="label"/>
    <xsl:param name="value"/>
    <xsl:param name="copy" select="false()"/>
    <xsl:if test="$value and normalize-space($value)!=''">
      <dl>
        <dt><xsl:value-of select="$label"/>:</dt>
        <dd>
          <xsl:choose>
          <xsl:when test="$copy">
            <xsl:copy-of select="$value"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$value"/>
          </xsl:otherwise>
          </xsl:choose>
        </dd>
      </dl>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="* | @*" mode="simplefield">
    <xsl:param name="label"/>
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label" select="$label"/>
      <xsl:with-param name="value" select="."/>
    </xsl:call-template>
	</xsl:template>
  
  <xsl:template match="*" mode="complexfield">
    <xsl:param name="label"/>
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label" select="$label"/>
      <xsl:with-param name="value"><xsl:apply-templates select="."/></xsl:with-param>
      <xsl:with-param name="copy" select="true()"/>
    </xsl:call-template>
	</xsl:template>
  
</xsl:stylesheet>