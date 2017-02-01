<?xml version="1.0"?>
<!--
*  '$RCSfile$'
*      Authors: Matt Jones, CHad Berkley
*    Copyright: 2000 Regents of the University of California and the
*               National Center for Ecological Analysis and Synthesis
*  For Details: http://www.nceas.ucsb.edu/
*
*   '$Author: daigle $'
*     '$Date: 2008-04-02 15:28:31 -0800 (Wed, 02 Apr 2008) $'
* '$Revision: 3780 $'
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
* convert an XML file showing the resultset of a query
* into an HTML format suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="./resultset-table.xsl"/>
   <xsl:output method="html" encoding="iso-8859-1"
              doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
              doctype-system="http://www.w3.org/TR/html4/loose.dtd"
              indent="yes" />

  <xsl:output method="html"/>
  <xsl:param name="sessid"/>
  <xsl:param name="qformat">asn</xsl:param>
  <xsl:param name="enableediting">false</xsl:param>
  <xsl:param name="contextURL"/>
  <xsl:param name="cgi-prefix"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <title>TERN Repository</title>
        <meta name="description" content="tern, tern repository, tern ecological research and data from australia and world wide, stored in a collaborative repository" />
        <meta name="keywords" content="tern, repository, data, samples, research, australia, network, ecological, terrestrial, ecosystem" />
        <meta name="DC.Creator.PersonalName" content="Dewi Wahyuni" />
        <meta name="DC.Description" content="tern network, TERN research and data from australia  stored in a collaborative repository" />
        <meta name="DC.Title" content="ASN Repository" />
        <meta name="DC.Subject" content="tern, repository, data, samples, research, australia, network, emissions" />
        <meta name="DC.Date.Created" scheme="ISO8601" content="2009-12-18" />
        <meta name="DC.Date.ValidTo" scheme="ISO8601" content="2010-12-14" />
        <meta name="DC.Identifier" scheme="URI" content="http://www.tern.net.au" />
        <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

        <link rel="SHORTCUT ICON" href="{$contextURL}/style/skins/{$qformat}/images/header.ico"/>
        <link rel="stylesheet" type="text/css" href="{$contextURL}/style/skins/{$qformat}/{$qformat}.css" />

        <script type="text/javascript" src="{$contextURL}/style/skins/{$qformat}/{$qformat}.js"></script>
        <script type="text/javascript" src="{$contextURL}/style/skins/{$qformat}/branding.js"></script>
        <script type="text/javascript">
          <![CDATA[
          function submitform(action,form_ref) {
              form_ref.action.value=action;
              form_ref.sessionid.value="]]><xsl:value-of select="$sessid" /><![CDATA[";
              form_ref.qformat.value="]]><xsl:value-of select="$qformat" /><![CDATA[";
              form_ref.submit();
          }
          ]]>
        </script>
      </head>

      <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
        <script type="text/javascript">
          <![CDATA[
            insertTemplateOpening("]]><xsl:value-of select="$contextURL" /><![CDATA[");
            insertSearchBox("]]><xsl:value-of select="$contextURL" /><![CDATA[");
           ]]>
        </script>
        <xsl:call-template name="resultstable"/>
        <script type="text/javascript">
          <![CDATA[
            insertTemplateClosing("]]><xsl:value-of select="$contextURL" /><![CDATA[");
           ]]>
        </script>
        <script type="text/javascript" src="{$contextURL}/style/skins/{$qformat}/google-analytics.js"></script>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>