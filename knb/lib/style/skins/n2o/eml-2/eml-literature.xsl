<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml-literature.xsl,v $'
  *      Authors: Matthew Brooke
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: tao $'
  *     '$Date: 2008-12-09 22:44:43 $'
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

  <!--<xsl:import href="eml-resource.xsl"/>-->
  <xsl:output method="html" encoding="utf-8" indent="yes" />

  <xsl:template name="citation">
    <xsl:param name="label"/>
    <xsl:param name="showPosition"/>
    <div class="citation rows">
      <xsl:if test="label">
        <h5>
          <xsl:value-of select="label"/>
          <xsl:if test="showPosition">
            <xsl:text> </xsl:text><xsl:value-of select="position()"/>
          </xsl:if>
        </h5>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="references!=''">
          <xsl:variable name="ref_id" select="references"/>
          <xsl:variable name="references" select="$ids[@id=$ref_id]" />
          <xsl:for-each select="$references">
            <xsl:call-template name="citationCommon"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="citationCommon"/>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template name="citationCommon">
    <xsl:call-template name="resource">
      <xsl:with-param name="creator">Author(s):</xsl:with-param>
    </xsl:call-template>

    <xsl:for-each select="article">
      <xsl:call-template name="citationarticle"/>
    </xsl:for-each>

    <xsl:for-each select="book">
      <xsl:call-template name="citationbook"/>
    </xsl:for-each>

    <xsl:for-each select="chapter">
      <xsl:call-template name="citationchapter"/>
    </xsl:for-each>

    <xsl:for-each select="editedBook">
      <xsl:call-template name="citationeditedBook"/>
    </xsl:for-each>

    <xsl:for-each select="manuscript">
      <xsl:call-template name="citationmanuscript"/>
    </xsl:for-each>

    <xsl:for-each select="report">
      <xsl:call-template name="citationreport"/>
    </xsl:for-each>

    <xsl:for-each select="thesis">
      <xsl:call-template name="citationthesis"/>
    </xsl:for-each>

    <xsl:for-each select="conferenceProceedings">
      <xsl:call-template name="citationconferenceProceedings"/>
    </xsl:for-each>

    <xsl:for-each select="personalCommunication">
      <xsl:call-template name="citationpersonalCommunication"/>
    </xsl:for-each>

    <xsl:for-each select="map">
      <xsl:call-template name="citationmap"/>
    </xsl:for-each>

    <xsl:for-each select="generic">
      <xsl:call-template name="citationgeneric"/>
    </xsl:for-each>

    <xsl:for-each select="audioVisual">
      <xsl:call-template name="citationaudioVisual"/>
    </xsl:for-each>

    <xsl:for-each select="presentation">
      <xsl:call-template name="citationpresentation"/>
    </xsl:for-each>

    <xsl:if test="access and normalize-space(access)!=''">
      <xsl:for-each select="access">
        <xsl:call-template name="access"/>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="type">
    <xsl:param name="value"/>
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Type</xsl:with-param>
      <xsl:with-param name="value" select="$value"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="referenceType">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Reference Type</xsl:with-param>
      <xsl:with-param name="value" select="referenceType"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="journal">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Journal</xsl:with-param>
      <xsl:with-param name="value" select="journal"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="volume">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Volume</xsl:with-param>
      <xsl:with-param name="value" select="volume"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="numberOfVolumes">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Number of Volumes</xsl:with-param>
      <xsl:with-param name="value" select="numberOfVolumes"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="issue">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Issue</xsl:with-param>
      <xsl:with-param name="value" select="issue"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="chapterNumber">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Chapter Number</xsl:with-param>
      <xsl:with-param name="value" select="chapterNumber"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="reportNumber">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Report Number</xsl:with-param>
      <xsl:with-param name="value" select="reportNumber"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="editor">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="copy" select="true()"/>
      <xsl:with-param name="label">Book Editor</xsl:with-param>
      <xsl:with-param name="value">
        <xsl:for-each select="editor">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>    
  </xsl:template>
  <xsl:template name="bookTitle">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Book Title</xsl:with-param>
      <xsl:with-param name="value" select="bookTitle"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="pageRange">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Page Range</xsl:with-param>
      <xsl:with-param name="value" select="pageRange"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="totalPages">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Total Pages</xsl:with-param>
      <xsl:with-param name="value" select="totalPages"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="totalFigures">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Total Figures</xsl:with-param>
      <xsl:with-param name="value" select="totalFigures"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="totalTables">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Total Tables</xsl:with-param>
      <xsl:with-param name="value" select="totalTables"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="publisher">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="copy" select="true()"/>
      <xsl:with-param name="label">Publisher</xsl:with-param>
      <xsl:with-param name="value">
        <xsl:for-each select="publisher">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>    
  </xsl:template>
  <xsl:template name="institution">
    <xsl:param name="label">Institution</xsl:param>
    <xsl:call-template name="simplefield">
      <xsl:with-param name="copy" select="true()"/>
      <xsl:with-param name="label" select="$label"/>
      <xsl:with-param name="value">
        <xsl:for-each select="institution">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>    
  </xsl:template>
  <xsl:template name="publicationPlace">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Publication Place</xsl:with-param>
      <xsl:with-param name="value">
        <xsl:for-each select="publicationPlace">
          <xsl:if test="position() > 1">; </xsl:if>
          <xsl:value-of select="publicationPlace"/>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="edition">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Edition</xsl:with-param>
      <xsl:with-param name="value" select="edition"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="ISSN">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">ISSN</xsl:with-param>
      <xsl:with-param name="value" select="ISSN"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="ISBN">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">ISBN</xsl:with-param>
      <xsl:with-param name="value" select="ISBN"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="degree">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Degree</xsl:with-param>
      <xsl:with-param name="value" select="degree"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="conferenceName">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Conference Name</xsl:with-param>
      <xsl:with-param name="value" select="conferenceName"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="conferenceDate">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Date</xsl:with-param>
      <xsl:with-param name="value" select="conferenceDate"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="conferenceLocation">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="copy" select="true()"/>
      <xsl:with-param name="label">Location</xsl:with-param>
      <xsl:with-param name="value">
        <xsl:for-each select="conferenceLocation">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>    
  </xsl:template>
  <xsl:template name="communicationType">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Communication Type</xsl:with-param>
      <xsl:with-param name="value" select="communicationType"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="recipient">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="copy" select="true()"/>
      <xsl:with-param name="label">Recipient</xsl:with-param>
      <xsl:with-param name="value">
        <xsl:for-each select="recipient">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>    
  </xsl:template>
  <xsl:template name="scale">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Scale</xsl:with-param>
      <xsl:with-param name="value" select="scale"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="originalPublication">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Supplemental Info for Original Publication</xsl:with-param>
      <xsl:with-param name="value" select="originalPublication"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="reprintEdition">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Reprint Edition</xsl:with-param>
      <xsl:with-param name="value" select="reprintEdition"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="reviewedItem">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="label">Review Item</xsl:with-param>
      <xsl:with-param name="value" select="reviewedItem"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="performer">
    <xsl:call-template name="simplefield">
      <xsl:with-param name="copy" select="true()"/>
      <xsl:with-param name="label">Performer</xsl:with-param>
      <xsl:with-param name="value">
        <xsl:for-each select="performer">
          <xsl:call-template name="party"/>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>    
  </xsl:template>


  <xsl:template name="citationarticle">
    <xsl:call-template name="type">
      <xsl:with-param name="value">ARTICLE</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="journal" />
    <xsl:call-template name="volume" />
    <xsl:call-template name="issue" />
    <xsl:call-template name="pageRange" />
    <xsl:call-template name="publisher" />
    <xsl:call-template name="publicationPlace" />
  </xsl:template>

  <xsl:template name="citationbook">
    <xsl:param name="notshow" />
    <xsl:if test="$notshow=''">
      <xsl:call-template name="type">
        <xsl:with-param name="value">BOOK</xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:call-template name="publisher" />
    <xsl:call-template name="publicationPlace" />
    <xsl:call-template name="edition" />
    <xsl:call-template name="volume" />
    <xsl:call-template name="numberOfVolumes" />
    <xsl:call-template name="totalPages" />
    <xsl:call-template name="totalFigures" />
    <xsl:call-template name="totalTables" />
    <xsl:call-template name="ISBN" />
  </xsl:template>

  <xsl:template name="citationchapter">
    <xsl:param name="notshow" />
    <xsl:if test="$notshow=''">
      <xsl:call-template name="type">
        <xsl:with-param name="value">CHAPTER</xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:call-template name="chapterNumber" />
    <xsl:call-template name="editor" />
    <xsl:call-template name="bookTitle" />
    <xsl:call-template name="pageRange" />
    <xsl:call-template name="citationbook">
      <xsl:with-param name="notshow" select="yes"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="citationeditedBook">
    <xsl:call-template name="citationbook"/>
  </xsl:template>

  <xsl:template name="citationmanuscript">
    <xsl:call-template name="type">
      <xsl:with-param name="value">MANUSCRIPT</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="institution" />
    <xsl:call-template name="totalPages" />
  </xsl:template>

  <xsl:template name="citationreport">
    <xsl:call-template name="type">
      <xsl:with-param name="value">REPORT</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="reportNumber" />
    <xsl:call-template name="publisher" />
    <xsl:call-template name="publicationPlace" />
    <xsl:call-template name="totalPages" />
  </xsl:template>

  <xsl:template name="citationthesis">
    <xsl:call-template name="type">
      <xsl:with-param name="value">THESIS</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="degree" />
    <xsl:call-template name="institution">
      <xsl:with-param name="label">Degree Institution</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="totalPages" />
  </xsl:template>

  <xsl:template name="citationconferenceProceedings">
    <xsl:call-template name="type">
      <xsl:with-param name="value">CONFERENCE PROCEEDINGS</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="conferenceName" />
    <xsl:call-template name="conferenceDate" />
    <xsl:call-template name="conferenceLocation" />

    <xsl:call-template name="citationchapter">
      <xsl:with-param name="notshow" select="yes"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="citationpersonalCommunication">
    <xsl:call-template name="type">
      <xsl:with-param name="value">PERSONAL COMMUNICATION</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="publisher" />
    <xsl:call-template name="publicationPlace" />
    <xsl:call-template name="communicationType" />
    <xsl:call-template name="recipient" />
  </xsl:template>

  <xsl:template name="citationmap">
    <xsl:call-template name="type">
      <xsl:with-param name="value">MAP</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="publisher" />
    <xsl:call-template name="edition" />
    <xsl:for-each select="geographicCoverage">
      <xsl:call-template name="geographicCoverage"/>
    </xsl:for-each>
    <xsl:call-template name="scale" />
  </xsl:template>

  <xsl:template name="citationgeneric">
    <xsl:call-template name="type">
      <xsl:with-param name="value">Generic Citation</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="publisher" />
    <xsl:call-template name="publicationPlace" />
    <xsl:call-template name="referenceType" />
    <xsl:call-template name="volume" />
    <xsl:call-template name="numberOfVolumes" />
    <xsl:call-template name="totalPages" />
    <xsl:call-template name="totalFigures" />
    <xsl:call-template name="totalTables" />
    <xsl:call-template name="edition" />
    <xsl:call-template name="originalPublication" />
    <xsl:call-template name="reprintEdition" />
    <xsl:call-template name="reviewedItem" />
    <xsl:call-template name="ISBN" />
    <xsl:call-template name="ISSN" />
  </xsl:template>

  <xsl:template name="citationaudioVisual">
    <xsl:call-template name="type">
      <xsl:with-param name="value">Media Citation</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="publisher" />
    <xsl:call-template name="publicationPlace" />
    <xsl:call-template name="performer" />
    <xsl:call-template name="ISBN" />
  </xsl:template>

  <xsl:template name="citationpresentation">
    <xsl:call-template name="type">
      <xsl:with-param name="value">Presentation</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="conferenceName" />
    <xsl:call-template name="conferenceDate" />
    <xsl:call-template name="conferenceLocation" />
  </xsl:template>

</xsl:stylesheet>
