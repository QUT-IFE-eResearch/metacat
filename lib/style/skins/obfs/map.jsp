<%@ page    language="java" %>
<%
/**
 *  '$RCSfile$'
 *      Authors: Matt Jones
 *    Copyright: 2008 Regents of the University of California and the
 *               National Center for Ecological Analysis and Synthesis
 *  For Details: http://www.nceas.ucsb.edu/
 *
 *   '$Author: daigle $'
 *     '$Date: 2008-07-07 14:25:34 +1000 (Mon, 07 Jul 2008) $'
 * '$Revision: 4080 $'
 * 
 * This is an HTML document for displaying metadata catalog tools
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
 */
%>

<%@ include file="../../common/common-settings.jsp"%>
<%@ include file="../../common/configure-check.jsp"%>
  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>OBFS Data Registry Interactive Map</title>
<link rel="stylesheet" href="<%=CONTEXT_URL%>/style/default.css" type="text/css">
  <link rel="stylesheet" type="text/css" 
        href="<%=STYLE_SKINS_URL%>/obfs/obfs.css"></link>
  <script language="JavaScript" type="text/JavaScript"
          src="<%=STYLE_SKINS_URL%>/obfs/obfs.js"></script>
  <script language="JavaScript" type="text/JavaScript"
          src="<%=STYLE_COMMON_URL%>/branding.js"></script>
</head>
<body>
      <script language="JavaScript">
          insertTemplateOpening("<%=CONTEXT_URL%>");
          //insertSearchBox("<%=CONTEXT_URL%>");
      </script>

<iframe scrolling="no" frameborder="0" width="750" height="520" src="spatial/map.jsp">
You need iframe support 
</iframe>

<p>&nbsp;</p>
<script language="JavaScript">          
    insertTemplateClosing("<%=CONTEXT_URL%>");
</script>
</body>
</html>
