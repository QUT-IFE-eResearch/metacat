<%@ page language="java" %>
<%@ page import="java.util.Set,java.util.Map,java.util.Vector,edu.ucsb.nceas.utilities.PropertiesMetaData" %>
<%@ page import="edu.ucsb.nceas.utilities.MetaDataGroup,edu.ucsb.nceas.utilities.MetaDataProperty" %>
<% 
/**
 *  '$RCSfile$'
 *    Copyright: 2008 Regents of the University of California and the
 *               National Center for Ecological Analysis and Synthesis
 *  For Details: http://www.nceas.ucsb.edu/
 *
 *   '$Author: daigle $'
 *     '$Date: 2008-07-29 10:31:02 -0700 (Tue, 29 Jul 2008) $'
 * '$Revision: 4176 $'
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

<html>
<head>

<title>Geoserver Configuration</title>
<link rel="stylesheet" type="text/css" 
        href="<%= request.getContextPath() %>/admin/admin.css"></link>
<script language="JavaScript" type="text/JavaScript" src="<%= request.getContextPath() %>/admin/admin.js"></script>

</head>
<body>
<%@ include file="./header-section.jsp"%>

<img src="<%= request.getContextPath() %>/metacat-logo.png" width="100px" align="right"/> 
<h2>Geoserver Configuration</h2>

<p>Reset the geoserver password here.  If there are any issues, or you want to defer the password
   reset until later, choose to bypass this screen.  You can configure the password later by 
   following the <a href="<%= request.getContextPath() %>/docs/user/geoserver-manual-configure.html">geoserver password change instructions.</a>
</p>
<!-- MCD TODO add geoserver instructions page -->
<br clear="right"/>

<%@ include file="page-message-section.jsp"%>

<form method="POST" name="configuration_form" action="<%= request.getContextPath() %>/admin" 
                                        onsubmit="return submitForm(this);">

	<h3>Geoserver Password Configuration</h3>
	<hr class="config-line">
	
	<div class="form-row">
				<img class="question-mark" src="style/images/question-mark.gif" 
	           		 onClick="helpWindow('<%= request.getContextPath() %>','metacat-configure.html#GeoserverUpdatePassword')"/>
				<div class="textinput-label"><label for="geoserver.username" title="Geoserver user name">User Name</label></div>
				<input class="textinput" id="geoserver.username" 
					   name="geoserver.username" 	             		    	    	           		    	             			
	           		   value="<%= request.getAttribute("geoserver.username") %>"/> 
	</div>
	<div class="form-row">
				<img class="question-mark" src="style/images/question-mark.gif" 
	           		 onClick="helpWindow('<%= request.getContextPath() %>','metacat-configure.html#GeoserverUpdatePassword')"/>
				<div class="textinput-label"><label for="geoserver.password" title="Geoserver user name">Password</label></div>
				<input class="textinput"  id="geoserver.password" 
					   name="geoserver.password" 
					   type="password"	             		    	    	           		    	             			
	           		   value="<%= request.getAttribute("geoserver.password") %>"/> 
	</div>

	<input type="hidden" name="configureType" value="geoserver"/>
	<input type="hidden" name="processForm" value="true"/>
	<input class=left-button type="submit" value="Update"/>
	<input class=button type="button" value="Bypass" onClick="forward('./admin?configureType=geoserver&bypass=true&processForm=true')">
	<input class=button type="button" value="Cancel" onClick="forward('./admin')"> 

</form>

<%@ include file="./footer-section.jsp"%>

</body>
</html>
