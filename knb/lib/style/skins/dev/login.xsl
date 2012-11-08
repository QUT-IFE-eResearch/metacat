<?xml version="1.0"?>
<!--
  *  '$RCSfile$'
  *      Authors: Jivka Bojilova
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: daigle $'
  *     '$Date: 2008-04-03 09:28:31 +1000 (Thu, 03 Apr 2008) $'
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
  * convert an XML file with information about login action
  * into an HTML format suitable for rendering with modern web browsers.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>

  <xsl:template match="/">
    <html>
      <xsl:apply-templates/>
    </html>
  </xsl:template>

  <xsl:template match="*" name="login">
  <xsl:choose>

    <xsl:when test="name(self::node())='login'">
      <head>
        <link rel="stylesheet" type="text/css" 
              href="./style/skins/dev/rowcol.css" />
        <script language="JavaScript">
          <![CDATA[
          window.location="./style/skins/dev/index.jsp"
          ]]>
        </script>
      </head>
      <body class="emlbody">
	<noscript>
          Please click <a href="./style/skins/dev/metacat.html">here</a>
          to enter Metacat. To disable this message in the future
          please enable JavaScript on your browser.
        </noscript>
      </body>
    </xsl:when>

    <xsl:when test="name(self::node())='unauth_login'">
      <head>
        <link rel="stylesheet" type="text/css" 
              href="./style/skins/dev/rowcol.css" />
        <script language="JavaScript">
              <![CDATA[
              window.location="./style/skins/dev/login.html"
              ]]>
        </script>
      </head>
      <body class="emlbody">
	<noscript>
              Please click <a href="./style/skins/dev/login.html">here</a>
              to enter Metacat. To disable this message in the future
              please enable JavaScript on your browser.
        </noscript>
      </body>
    </xsl:when>

    <xsl:when test="name(self::node())='logout'">
      <head>
        <link rel="stylesheet" type="text/css" 
              href="./style/skins/dev/rowcol.css" />
        <script language="JavaScript">
              <![CDATA[
              window.location="./style/skins/dev/index.html"
              ]]>
        </script>
      </head>
      <body class="emlbody">
        <noscript>
              Please click <a href="./style/skins/dev/index.html">here</a>
              to enter Metacat. To disable this message in the future
              please enable JavaScript on your browser.
        </noscript>
      </body>
    </xsl:when>

    <xsl:when test="name(self::node())='error_login'">
      <head>
        <link rel="stylesheet" type="text/css" 
              href="./style/skins/dev/rowcol.css" />
      </head>
      <body class="emlbody">
        <p style="color:red"> <b> Error Page </b> </p>
        <p> <xsl:value-of select="message"/> </p>
        <p> <a href="./style/skins/dev/login.html">Try again</a> 
            <a href="./style/skins/dev/index.html">Continue</a>  </p>
      </body>
    </xsl:when>

  </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
