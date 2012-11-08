<%@ page language="java"%>
<%
	 /*
	 '$RCSfile$'
	 Copyright: 2003 Regents of the University of California and the
	 National Center for Ecological Analysis and Synthesis
	 '$Author: leinfelder $'
	 '$Date: 2008-06-10 10:18:06 -0700 (Tue, 10 Jun 2008) $'
	 '$Revision: 3966 $'
	
	 This program is free software; you can redistribute it and/or modify
	 it under the terms of the GNU General Public License as published by
	 the Free Software Foundation; either version 2 of the License, or
	 (at your option) any later version.
	
	 This program is distributed in the hope that it will be useful,
	 but WITHOUT ANY WARRANTY; without even the implied warranty of
	 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 GNU General Public License for more details.
	
	 You should have received a copy of the GNU General Public License
	 along with this program; if not, write to the Free Software
	 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
	 */
%>
<%@ include file="settings.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>SANParks - South African National Park Data Repository</title>
	<!-- This file is meant to be included in a parent file, so link css stylesheet there. -->
</head>

<body>
<!--  div class="header-div" -->
<table class="header-section" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td><a href="http://www.SANParks.org/" title="SANParks.org Home"><img class="top-row-image" src="<%=STYLE_SKINS_URL%>/sanparks/images/logofade_top.jpg" alt="SANParks.org Home" /></a></td>	
		<td><img class="top-row-image" src="<%=STYLE_SKINS_URL%>/sanparks/images/sanparks_name.jpg" alt="SANParks.org Home" /></td>
		<td class="header-spacer"></td>
		<td><img class="top-row-image" src="<%=STYLE_SKINS_URL%>/sanparks/images/giraffe_top_left.jpg" alt="SANParks.org Home" /></td>
		<td><img class="top-row-image" src="<%=STYLE_SKINS_URL%>/sanparks/images/giraffe_top_right.jpg" alt="SANParks.org Home" /></td>
	</tr>
	<tr>
		<td><a href="http://www.SANParks.org/" title="SANParks.org Home"><img src="<%=STYLE_SKINS_URL%>/sanparks/images/logofade_bottom.jpg" alt="SANParks.org Home" /></a></td>
		<td colspan="3" class="header-menu">
			<a class="header-menu-first-item" href="./" target="_top" >Repository Home</a>
			<a class="header-menu-item" href="<%=CGI_URL%>/register-dataset.cgi?cfg=sanparks" target="_top" >Register Data</a>

<%
			if(request.getRequestURI().endsWith("searchWorkflowRun.jsp")) {
%>
				<span class="header-menu-item">TPC Status</span>	
<%
			} else {
%>
				<a class="header-menu-item" href="<%=STYLE_SKINS_URL%>/sanparks/searchWorkflowRun.jsp">TPC Status</a>
<%
			}

 
			if(request.getRequestURI().endsWith("searchWorkflow.jsp")) {
%>
				<span class="header-menu-item">TPC Workflows</span>	
<%
			} else {
%>
				<a class="header-menu-item" href="<%=STYLE_SKINS_URL%>/sanparks/searchWorkflow.jsp">TPC Workflows</a>
<%
			}
%> 
					
		</td>
		<td><img src='<%=STYLE_SKINS_URL%>/sanparks/images/giraffe_bottom_right.jpg' alt='Giraffe' /></td>
	</tr>
</table>
<!--  /div -->
</body>
</html>