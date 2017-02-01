<%@ page    language="java" %>
<%
/**
  *  '$RCSfile$'
  *      Authors: Matt Jones
  *    Copyright: 2008 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: leinfelder $'
  *     '$Date: 2008-08-27 07:53:56 +1000 (Wed, 27 Aug 2008) $'
  * '$Revision: 4319 $'
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
%>

<%@ include file="../../../common/common-settings.jsp"%>
<%@ include file="../../../common/configure-check.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<html>
<head>
<title>Metacat Spatial Option Demo</title>

<link rel='StyleSheet' type='text/css' href='<%=CONTEXT_URL%>/spatial/mapbuilder/lib/skin/default/docsStyle.css'>
<link rel='StyleSheet' type='text/css' href='<%=CONTEXT_URL%>/spatial/mapbuilder/lib/skin/default/mapStyle.css'>
<link rel='StyleSheet' type='text/css' href='<%=CONTEXT_URL%>/spatial/mapbuilder/lib/skin/default/button.css'>
<link rel='StyleSheet' type='text/css' href='<%=CONTEXT_URL%>/spatial/mapbuilder/lib/skin/default/scalebar-fat.css'>

<script type="text/javascript">
  var mbConfigUrl="config.xml";
</script>

<script type="text/javascript" src="<%=CONTEXT_URL%>/spatial/mapbuilder/lib/Mapbuilder.js"></script>
</head>
<body onload="mbDoLoad()">

    <table border="0">
            <tr>
            <td colspan="2">
              <div id="mainMapPane" style="background-color:#b8d5f5;" />
              <div id="loading">
               <p> Loading Map
                <img src="<%=CONTEXT_URL%>/spatial/mapbuilder/lib/skin/default/images/Loading.gif"/>
               </p>
             </div>
            </td>

            </tr>
            <tr align="left">
                    <td>

                    <table border="0" width="100%">
                                    <tr valign="top"> 
                                      <td id="legend" align="left"/>
                                      <td width="45%" align="right">
                                        <table>
                                          <tr>
                                            <td align="center" id="cursorTrack" />
                                            <td align="right" id="mainButtonBar" /> 
                                          </tr>
                                          <tr><td colspan="2" align="right" id="locationsSelect" /> </tr>
                                          <!-- <tr><td colspan="2" align="right" id="scalebar"/> </tr> -->
                                          <tr> 
                                           <td colspan="2" align="right" style="font-size:8pt;"> 
                                             <a style="text-decoration:none;" href="<%=CONTEXT_URL%>/spatial/kml.jsp">
                                               <br/> 
                                               Download Google Earth KML
                                               <img border="0" src="<%=CONTEXT_URL%>/style/images/kml.gif">
                                             </a>
                                           </td>
                                          </tr> 
                                        </table>
                                      </td>
                                      
                                    </tr> 
                    </table>

                    </td>
            </tr>
    </table>


</body>
</html>
