<%@ page    language="java" %>
<%
/*
*  '$RCSfile$'
*    Copyright: 2009 Regents of the University of California and the
*               National Center for Ecological Analysis and Synthesis
*
*   '$Author: daigle $'
*     '$Date: 2008-07-06 21:25:34 -0700 (Sun, 06 Jul 2008) $'
* '$Revision: 4080 $'
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
<%@ include file="settings.jsp"%>

<html>
    <head>
        <title>Sanparks TPC Report Search</title>
        <link rel="stylesheet" type="text/css" href="<%=STYLE_SKINS_URL%>/sanparks/sanparks.css"/>
        <link rel="stylesheet" type="text/css" href="<%=STYLE_SKINS_URL%>/sanparks/sanparks-tpc.css"/>
        <link rel="stylesheet" type="text/css" href="<%=STYLE_SKINS_URL%>/sanparks/header2.css"/>
        <link rel="stylesheet" type="text/css" href="<%=STYLE_SKINS_URL%>/sanparks/left-nav2.css"/>
        <link rel="stylesheet" type="text/css" href="<%=STYLE_SKINS_URL%>/sanparks/footer2.css"/>
        <script language="JavaScript" type="text/JavaScript" src="<%=STYLE_SKINS_URL%>/sanparks/sanparks.js"></script>
        <script language="JavaScript" type="text/JavaScript" src="<%=STYLE_COMMON_URL%>/style/skins/sanparks/searchWorkflowPathQuery.js"></script>
        <script language="JavaScript" type="text/JavaScript" src="<%=STYLE_COMMON_URL%>/branding.js"></script>
		<script language="JavaScript" type="text/JavaScript" src="<%=STYLE_COMMON_URL%>/widgets/form-fields-widget.js"></script>
		<script language="JavaScript" type="text/JavaScript" src="<%=STYLE_COMMON_URL%>/prototype-1.5.1.1/prototype.js"></script>
		<script language="JavaScript" type="text/JavaScript" src="<%=STYLE_COMMON_URL%>/ajax-utils.js"></script>
		<script language="JavaScript" type="text/JavaScript" src="<%=STYLE_COMMON_URL%>/searchWorkflow.js"></script>  
    </head>
    <body>            
        <script type="text/javascript">
			window.onload=function() {
 				setWorkflowQueryFormField('tpcSearch');
 				submitFormIntoDiv('<%=SERVLET_URL%>', 'tpcSearch', 'workflow-search-results');
			}
		</script>
        
        <table class="page-section" cellpadding="0" cellspacing="0" border="0">
        	<tr>
        		<td>
        			<jsp:include page="header2.jsp"/>
        		</td>
        	</tr>
        	<tr>
        		<td>
        			<table class="center-content-section" cellpadding="0" cellspacing="0" border="0">
        				<tr>
        					<td class="left-nav-section">
        						<!-- Main content -->
								<jsp:include page="left-nav2.jsp"/>
 							</td>
 
							<td class="search-section">
 								<table cellpadding="0" cellspacing="0" border="0">
     								<tr>
     									<td><img src="<%=STYLE_SKINS_URL%>/sanparks/images/search_box_nw.jpg" /></td>
     									<td><img src="<%=STYLE_SKINS_URL%>/sanparks/images/search_box_n.jpg" /></td>
     									<td><img src="<%=STYLE_SKINS_URL%>/sanparks/images/search_box_ne.jpg" /></td>
     								</tr>
     								<tr>
     									<td class="search-box-w"><img src="<%=STYLE_SKINS_URL%>/sanparks/images/search_box_w.jpg" /></td>
     									<td>
			      						  	<form name="tpcSearch" id="tpcSearch">
			        							<input name="query" id="query" type="hidden" />
												<input name="qformat" value="tpc-result" type="hidden" />
												<!-- input name="qformat" value="xml" type="hidden" / -->
												<input name="action" value="squery" type="hidden" />  		
												<div class="form-base-row" id="form-base-row">
													<div class="field-label dropdown-field-label" id='field-selector-section' >Add search term</div>
													<select class="dropdown-input" name="dd-field-selector" id="dd-field-selector" onchange="addSearchDropdownBefore(this)">
														<option value="name">Name</option>
														<option value="keyword">Keyword</option>
														<option value="creator">Creator</option>
														<option value="description">Description</option>
														<option value="date-created">Date Created</option>
														<option value="workflow-lsid">Workflow LSID</option>
													</select>         		
			        								<input class="submit-button" value="Search" type="button" onclick="setWorkflowQueryFormField('tpcSearch');submitFormIntoDiv('<%=SERVLET_URL%>','tpcSearch','workflow-search-results')">
			        							</div> 
			        						</form>
        								</td>
        								<td class="search-box-e"><img src="<%=STYLE_SKINS_URL%>/sanparks/images/search_box_e.jpg" /></td>
			        				</tr>
			        				<tr>
			     						<td><img src="<%=STYLE_SKINS_URL%>/sanparks/images/search_box_sw.jpg" /></td>
			     						<td><img src="<%=STYLE_SKINS_URL%>/sanparks/images/search_box_s.jpg" /></td>
			     						<td><img src="<%=STYLE_SKINS_URL%>/sanparks/images/search_box_se.jpg" /></td>
			     					</tr>
			     					<tr>
			     						<td></td>
			     						<td colspan="2">
			     		     				<div id="workflow-search-results"></div>
			     						</td>
			     					</tr>
     							</table>
			    			</td>
			    		</tr>   
			     		<tr class="footer-content-section">
			    			<td colspan="3">     
			        			<!-- Footer Content -->
			       				<jsp:include page="footer2.jsp" />
			     			</td>
			    		</tr>
			     	</table>
			    </td>
			</tr>
		</table>
    </body>
</html>
