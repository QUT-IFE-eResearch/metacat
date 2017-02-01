<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-attribute.xsl,v $'
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2008-12-10 01:32:50 $'
  * '$Revision: 1.5 $'
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
  
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" encoding="utf-8" indent="yes" />

<xsl:template name="attributelist">
</xsl:template>
<xsl:template name="singleattribute">
</xsl:template>

<xsl:template match="attributeList">
  <xsl:param name="entitytype"/>
  <xsl:param name="entityindex"/>
  
  <xsl:variable name="ref_id" select="references"/>
  <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
  
  <xsl:for-each select="$node">
    <xsl:if test="attribute">
      <xsl:call-template name="attributecommonvertical">
        <xsl:with-param name="entitytype" select="$entitytype"/>
        <xsl:with-param name="entityindex" select="$entityindex"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:for-each>
  
</xsl:template>

<xsl:template name="attributecommonvertical">
  <xsl:param name="entitytype"/>
  <xsl:param name="entityindex"/>

  <xsl:variable name="ref_id" select="attribute/references"/>
  <xsl:variable name="attributes" select="attribute | $ids[@id=$ref_id]"/>

  <table class="attribute">
    <tr>
      <th class="highlightAttrib">Attribute Description<br/>(label and definition)</th>
      <xsl:if test="$attributes/storageType">
        <th class="highlightAttrib">Type of Value</th>
      </xsl:if>
      <th class="highlightAttrib">Measurement Type and Domain</th>
      <xsl:if test="$attributes/missingValueCode">
        <th class="highlightAttrib">Missing Value Code</th>
      </xsl:if>
      <xsl:if test="$attributes/accuracy/attributeAccuracyReport">
        <th class="highlightAttrib">Accuracy Report</th>
      </xsl:if>
      <xsl:if test="$attributes/accuracy/quantitativeAttributeAccuracyAssessment">
        <th class="highlightAttrib">Accuracy Assessment</th>
      </xsl:if>
      <xsl:if test="$attributes/coverage">
        <th class="highlightAttrib">Coverage</th>
      </xsl:if>
      <xsl:if test="$attributes/methods | $attributes/method">
        <th class="highlightAttrib">Method</th>
      </xsl:if>
    </tr>

    <xsl:for-each select="attribute">
      <xsl:variable name="index" select="position()"/>
      <xsl:variable name="stripes">
        <xsl:choose>
        <xsl:when test="position() mod 2 = 0">
          <xsl:value-of select="$colevenStyle"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$coloddStyle"/>
        </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="innerstripes">
        <xsl:choose>
        <xsl:when test="position() mod 2 = 0">
          <xsl:value-of select="$innercolevenStyle"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$innercoloddStyle"/>
        </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="ref_id" select="references"/>
      <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
      
      <tr>
        <td class="description">
          <b><xsl:value-of select="$node/attributeName"/></b>
          <xsl:text> - </xsl:text>
          <xsl:value-of select="$node/attributeLabel"/><br/>
          <xsl:value-of select="$node/attributeDefinition"/>
        </td>
        <xsl:if test="$attributes/storageType">
          <td class="type"><xsl:value-of select="$node/storageType"/></td>
        </xsl:if>
        <td class="measurement">
          <xsl:apply-templates select="$node/measurementScale">
            <xsl:with-param name="entitytype" select="$entitytype"/>
            <xsl:with-param name="entityindex" select="$entityindex"/>
            <xsl:with-param name="attributeindex" select="$index"/>
            <xsl:with-param name="stripes" select="$innerstripes"/>
          </xsl:apply-templates>
        </td>
        <xsl:if test="$attributes/missingValueCode">
          <td><xsl:apply-templates select="$node/missingValueCode"/></td>
        </xsl:if>
        <xsl:if test="$attributes/accuracy/attributeAccuracyReport">
          <td><xsl:value-of select="$node/accuracy/attributeAccuracyReport"/></td>
        </xsl:if>
        <xsl:if test="$attributes/accuracy/quantitativeAttributeAccuracyAssessment">
          <td><xsl:apply-templates select="$node/accuracy/quantitativeAttributeAccuracyAssessment"/></td>
        </xsl:if>
        <xsl:if test="$attributes/coverage">
          <xsl:apply-templates select="$node/coverage"/>
        </xsl:if>
        <xsl:if test="$attributes/methods | $attributes/method">
          <xsl:apply-templates select="$node/methods | $node/method"/>
        </xsl:if>
      </tr>
    </xsl:for-each>
    
  </table>
</xsl:template>

<xsl:template match="attributeList" mode="single">
  <xsl:param name="entitytype"/>
  <xsl:param name="entityindex"/>
  <xsl:param name="attributeindex"/>

  <xsl:variable name="attributeListRefId" select="references"/>
  <xsl:variable name="attributeListNode" select=". | $ids[@id=$attributeListRefId]"/>
  <xsl:variable name="attributeSelectedNode" select="$attributeListNode/attribute[position()=$attributeindex]"/>
  <xsl:variable name="attributeRefId" select="$attributeSelectedNode/references"/>
  <xsl:variable name="attributeNode" select="$attributeSelectedNode | $ids[@id=$attributeRefId]"/>
  
  <!-- First row for attribute name-->
  <dl class="row">
    <dt>Column Name</dt>
    <dd><b><xsl:value-of select="$attributeNode/attributeName"/></b></dd>
  </dl>
  <!-- Second row for attribute label-->
  <dl class="row">
    <dt>Column Label</dt>
    <dd>
      <xsl:for-each select="$attributeNode/attributeLabel">
        <xsl:value-of select="."/>&#160;<br/>
      </xsl:for-each>
    </dd>
  </dl>
  <!-- Third row for attribute defination-->
  <dl class="row">
    <dt>Definition</dt>
    <dd><xsl:value-of select="$attributeNode/attributeDefinition"/></dd>
  </dl>
  <!-- The fourth row for attribute storage type-->
  <dl class="row">
    <dt>Type of Value</dt>
    <dd>
      <xsl:for-each select="$attributeNode/storageType">
        <xsl:value-of select="."/>&#160;<br/>
      </xsl:for-each>
    </dd>
  </dl>
  <!-- The fifth row for meaturement type-->
  <dl class="row">
    <dt>Measurement Type</dt>
    <dd><xsl:value-of select="local-name($attributeNode/measurementScale/*)"/></dd>
  </dl>
  <!-- The sixth row for meaturement domain-->
  <dl class="row">
    <dt>Measurement Domain</dt>
    <dd>
      <xsl:apply-templates select="$attributeNode/measurementScale">
        <xsl:with-param name="entitytype" select="$entitytype"/>
        <xsl:with-param name="entityindex" select="$entityindex"/>
        <xsl:with-param name="attributeindex" select="$attributeindex"/>
      </xsl:apply-templates>
    </dd>
  </dl>
  <!-- The seventh row for missing value code-->
  <dl class="row">
    <dt>Missing Value Code</dt>
    <dd><xsl:apply-templates select="$attributeNode/missingValueCode"/></dd>
  </dl>
  <!-- The eighth row for accuracy report-->
  <dl class="row">
    <dt>Accuracy Report</dt>
    <dd><xsl:value-of select="$attributeNode/accuracy/attributeAccuracyReport"/></dd>
  </dl>
  <!-- The ninth row for quality accuracy accessment -->
  <dl class="row">
    <dt>Accuracy Assessment</dt>
    <dd><xsl:apply-templates select="$attributeNode/accuracy/quantitativeAttributeAccuracyAssessment"/></dd>
  </dl>
   <!-- The tenth row for coverage-->
  <dl class="row">
    <dt>Coverage</dt>
    <dd><xsl:apply-templates select="$attributeNode/coverage"/></dd>
  </dl>
   <!-- The eleventh row for method-->
  <dl class="row">
    <dt>Method</dt>
    <dd><xsl:apply-templates select="$attributeNode/methods | $attributeNode/method"/></dd>
  </dl>
</xsl:template>


  <xsl:template match="measurementScale">
    <xsl:param name="stripes"/>
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>
    <xsl:param name="attributeindex"/>
    
    <div><xsl:value-of select="local-name(./*)"/></div>
    <xsl:apply-templates select="./*">
      <xsl:with-param name="stripes" select="$stripes"/>
      <xsl:with-param name="entitytype" select="$entitytype"/>
      <xsl:with-param name="entityindex" select="$entityindex"/>
      <xsl:with-param name="attributeindex" select="$attributeindex"/>
    </xsl:apply-templates>
  </xsl:template>
 
  <xsl:template match="nominal | ordinal">
    <xsl:param name="stripes"/>
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>
    <xsl:param name="attributeindex"/>
    <xsl:apply-templates select="nonNumericDomain" mode="attribute">
      <xsl:with-param name="stripes" select="$stripes"/>
      <xsl:with-param name="entitytype" select="$entitytype"/>
      <xsl:with-param name="entityindex" select="$entityindex"/>
      <xsl:with-param name="attributeindex" select="$attributeindex"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="nonNumericDomain" mode="attribute">
    <xsl:param name="stripes"/>
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>
    <xsl:param name="attributeindex"/>
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
    <xsl:choose>
      <!-- if numericdomain only has one text domain,
        it will be displayed inline otherwise will be showed as a link-->
      <xsl:when test="count($node/textDomain)=1 and not($node/enumeratedDomain)">
        <xsl:apply-templates select="$node/textDomain"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$withHTMLLinks='1'">
          <a><xsl:attribute name="href"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$docid"/>&amp;displaymodule=attributedomain&amp;entitytype=<xsl:value-of select="$entitytype"/>&amp;entityindex=<xsl:value-of select="$entityindex"/>&amp;attributeindex=<xsl:value-of select="$attributeindex"/></xsl:attribute>
            <b>Domain Info</b>
          </a>
        </xsl:if>
        <xsl:if test="$withHTMLLinks='0'">
          <xsl:apply-templates select=".">
            <xsl:with-param name="entitytype" select="$entitytype"/>
            <xsl:with-param name="entityindex" select="$entityindex"/>
            <xsl:with-param name="attributeindex" select="$attributeindex"/>
          </xsl:apply-templates>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="interval | ratio">
    <xsl:param name="stripes"/>
    <table>
    <xsl:if test="unit/standardUnit">
      <tr>
        <td width="{$firstColWidth}" class="{$firstColStyle}"><b>Unit</b></td>
        <td width="{$secondColWidth}" class="{$stripes}"><xsl:value-of select="unit/standardUnit"/></td>
      </tr>
    </xsl:if>
    <xsl:if test="unit/customUnit">
      <tr>
        <td width="{$firstColWidth}" class="{$firstColStyle}"><b>Unit</b></td>
        <td width="{$secondColWidth}" class="{$stripes}"><xsl:value-of select="unit/customUnit"/></td>
      </tr>
    </xsl:if>
    <xsl:for-each select="precision">
      <tr>
        <td width="{$firstColWidth}" class="{$firstColStyle}"><b>Precision</b></td>
        <td width="{$secondColWidth}" class="{$stripes}"><xsl:value-of select="."/></td>
      </tr>
    </xsl:for-each>
    <xsl:apply-templates select="numericDomain">
      <xsl:with-param name="stripes" select="$stripes"/>
    </xsl:apply-templates>
    </table>
  </xsl:template>


  <xsl:template match="numericDomain">
    <xsl:param name="stripes"/>
    
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
    
    <tr>
      <td width="{$firstColWidth}" class="{$firstColStyle}"><b>Type</b></td>
      <td width="{$secondColWidth}" class="{$stripes}"><xsl:value-of select="$node/numberType"/></td>
    </tr>
    <xsl:for-each select="$node/bounds">
      <tr>
        <td width="{$firstColWidth}" class="{$firstColStyle}"><b>Min</b></td>
        <td width="{$secondColWidth}" class="{$stripes}">
          <xsl:for-each select="minimum">
            <xsl:value-of select="."/>&#160;
          </xsl:for-each>
        </td>
      </tr>
      <tr>
        <td width="{$firstColWidth}" class="{$firstColStyle}"><b>Max</b></td>
        <td width="{$secondColWidth}" class="{$stripes}">
          <xsl:for-each select="maximum">
            <xsl:value-of select="."/>&#160;
          </xsl:for-each>
        </td>
      </tr>
    </xsl:for-each>
  </xsl:template>

 <xsl:template match="dateTime | datetime">
    <xsl:param name="stripes"/>
    <table>
    <tr><td width="{$firstColWidth}" class="{$firstColStyle}"><b>Format</b></td>
         <td width="{$secondColWidth}" class="{$stripes}">
            <xsl:value-of select="formatString"/>
         </td>
    </tr>
     <tr><td width="{$firstColWidth}" class="{$firstColStyle}"><b>Precision</b></td>
         <td width="{$secondColWidth}" class="{$stripes}">
            <xsl:value-of select="dateTimePrecision"/>
         </td>
    </tr>
    <xsl:call-template name="timedomain"/>
    </table>
 </xsl:template>


  <xsl:template name="timedomain">
    <xsl:param name="stripes"/>
    <xsl:variable name="ref_id" select="references"/>
    <xsl:variable name="node" select=". | $ids[@id=$ref_id]"/>
    
    <xsl:for-each select="$node/bounds">
      <tr><td width="{$firstColWidth}" class="{$firstColStyle}"><b>Min</b></td>
          <td width="{$secondColWidth}" class="{$stripes}">
            <xsl:for-each select="minimum">
              <xsl:value-of select="."/>&#160;
            </xsl:for-each>
          </td>
      </tr>
      <tr><td width="{$firstColWidth}" class="{$firstColStyle}"><b>Max</b></td>
          <td width="{$secondColWidth}" class="{$stripes}">
            <xsl:for-each select="maximum">
              <xsl:value-of select="."/>&#160;
            </xsl:for-each>
          </td>
      </tr>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="missingValueCode">
    <table>
      <tr>
        <td><b>Code</b></td>
        <td><xsl:value-of select="code"/></td>
      </tr>
      <tr>
        <td><b>Expl</b></td>
        <td><xsl:value-of select="codeExplanation"/></td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="quantitativeAttributeAccuracyAssessment">
    <table>
      <tr>
        <td><b>Value</b></td>
        <td><xsl:value-of select="attributeAccuracyValue"/></td>
      </tr>
      <tr>
        <td><b>Expl</b></td>
        <td><xsl:value-of select="attributeAccuracyExplanation"/></td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="attribute/coverage">
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>
    <xsl:param name="attributeindex"/>
    <xsl:if test="$withHTMLLinks='1'">
      <a><xsl:attribute name="href"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$docid"/>&amp;displaymodule=attributecoverage&amp;entitytype=<xsl:value-of select="$entitytype"/>&amp;entityindex=<xsl:value-of select="$entityindex"/>&amp;attributeindex=<xsl:value-of select="$attributeindex"/></xsl:attribute>
       <b>Coverage Info</b></a>
    </xsl:if>
    <xsl:if test="$withHTMLLinks='0'">
      <xsl:call-template name="coverage"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="attribute/methods | attribute/method">
    <xsl:param name="entitytype"/>
    <xsl:param name="entityindex"/>
    <xsl:param name="attributeindex"/>
    <xsl:if test="$withHTMLLinks='1'">
       <a><xsl:attribute name="href"><xsl:value-of select="$tripleURI"/><xsl:value-of select="$docid"/>&amp;displaymodule=attributemethod&amp;entitytype=<xsl:value-of select="$entitytype"/>&amp;entityindex=<xsl:value-of select="$entityindex"/>&amp;attributeindex=<xsl:value-of select="$attributeindex"/></xsl:attribute>
       <b>Method Info</b></a>
    </xsl:if>
    <xsl:if test="$withHTMLLinks='0'">
      <xsl:call-template name="method"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
