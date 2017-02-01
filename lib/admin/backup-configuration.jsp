<%@ page language="java"%>
<%@ page
	import="edu.ucsb.nceas.metacat.util.OrganizationUtil,edu.ucsb.nceas.metacat.properties.PropertyService"%>
<%
	/**
	 *  '$RCSfile$'
	 *    Copyright: 2008 Regents of the University of California and the
	 *               National Center for Ecological Analysis and Synthesis
	 *  For Details: http://www.nceas.ucsb.edu/
	 *
	 *   '$Author: daigle $'
	 *     '$Date: 2008-11-19 15:10:40 -0800 (Wed, 19 Nov 2008) $'
	 * '$Revision: 4585 $'
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

<title>Backup Directory Configuration</title>
<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/admin/admin.css"></link>
<script language="JavaScript" type="text/JavaScript"
	src="<%= request.getContextPath() %>/admin/admin.js"></script>

</head>
<body>
<img src="<%= request.getContextPath() %>/metacat-logo.png"
	width="100px" align="right" />
<h2>Backup Directory Configuration</h2>

Metacat will back up configuration values in a location outside of the application installation 
directories.  In this way, you won't have to re-enter the entire configuration every time you reinstall 
Metacat.  <br><br>

<%
  String backupDir = (String)request.getAttribute("backupBaseDir");
  String backupDirStatus = (String)request.getAttribute("backupDirStatus");

  if (backupDirStatus.equals("hiddenExistsPopulated")) {
%>
  The following directory was discovered with existing backup files.  If this is not the 
  correct backup directory, please correct below.<br><br>
  
  <%= backupDir %>
  
<%
  } else if (backupDirStatus.equals("unknown")) {
%>
  The system could not discover an optimal backup location.  Please enter a location that you have
  permissions to below.<br><br>

<%
  } else {
%>  
  The following directory was determined to be optimal for creating backup directories.  If 
  this is not correct, please correct below.<br><br>
  
   <%= backupDir %>
<%
  }
%>

<br class="auth-header">

<%@ include file="./page-message-section.jsp"%>
<hr class="config-line">
<br>

<form name="backupform" method="post"
	action="<%= request.getContextPath() %>/admin" target="_top"
	onsubmit="return validateAndSubmitForm(this);" id="backupform">

<table class="backup">
	<tr>
		<td>Backup File Directory:</td>
	</tr>
	<tr>
		<td><input class="backup-input" name="backup-dir" type="text"
			maxlength="256"
			value="<%= backupDir %>"></td>
	</tr>
	<tr>
		<td class="textinput-description">[Backup directory]</td>
	</tr>
</table>

<br>
<hr class="config-line">

<input type="hidden" name="configureType" value="backup"/>
<input type="hidden" name="processForm" value="true"/>
<input class=left-button type="submit" value="Save"/>

</body>
</html>
