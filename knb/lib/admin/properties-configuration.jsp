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
 *     '$Date: 2009-08-15 10:38:05 +1000 (Sat, 15 Aug 2009) $'
 * '$Revision: 5028 $'
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

<title>Metacat Properties Configuration</title>
<link rel="stylesheet" type="text/css" 
        href="<%= request.getContextPath() %>/admin/admin.css"></link>
<script language="JavaScript" type="text/JavaScript" src="<%= request.getContextPath() %>/admin/admin.js"></script>

</head>
<body>
<%@ include file="./header-section.jsp"%>

<img src="<%= request.getContextPath() %>/metacat-logo.png" width="100px" align="right"/> 
<h2>Metacat Properties Configuration</h2>

<p>Enter Metacat System properties here.  All Fields must be filled in before saving.
</p>
<br clear="right"/>

<%@ include file="page-message-section.jsp"%>

<form method="POST" name="configuration_form" action="<%= request.getContextPath() %>/admin" 
                                        onsubmit="return submitForm(this);">
<% 
	// metadata holds all group and properties metadata
    PropertiesMetaData metadata = (PropertiesMetaData)request.getAttribute("metadata");
	if (metadata != null) {
		// each group describes a section of properties
		Map<Integer, MetaDataGroup> groupMap = metadata.getGroups();
		Set<Integer> groupIdSet = groupMap.keySet();

		
		for (Integer groupId : groupIdSet) {
			// for this group, display the header (group name)
			MetaDataGroup metaDataGroup = (MetaDataGroup)groupMap.get(groupId);
			if (groupId == 0) {
				// get all the properties in this group
				Map<Integer, MetaDataProperty> propertyMap = 
					metadata.getPropertiesInGroup(metaDataGroup.getIndex());
				Set<Integer> propertyIndexes = propertyMap.keySet();
				// iterate through each property and display appropriately
				for (Integer propertyIndex : propertyIndexes) {
					MetaDataProperty metaDataProperty = propertyMap.get(propertyIndex);
	    			String fieldType = metaDataProperty.getFieldType(); 
	    			if (fieldType.equals("hidden")) {
%>
						<input type="hidden"
                               name="<%= metaDataProperty.getKey() %>" 	             		    	    	           		    	             			
	           		    	   value="<%= request.getAttribute(metaDataProperty.getKey()) %>"/>
<%
	    			}
				}
			} else {		
%>
		<h3><%= metaDataGroup.getName()  %></h3>
		<hr class="config-line">
<%
			// get all the properties in this group
			Map<Integer, MetaDataProperty> propertyMap = 
				metadata.getPropertiesInGroup(metaDataGroup.getIndex());
			Set<Integer> propertyIndexes = propertyMap.keySet();
			// iterate through each property and display appropriately
			for (Integer propertyIndex : propertyIndexes) {
				MetaDataProperty metaDataProperty = propertyMap.get(propertyIndex);
    			String fieldType = metaDataProperty.getFieldType(); 
    			if (fieldType.equals("select")) {
%> 
				<div class="form-row">
					<img class="question-mark" src="style/images/question-mark.gif"  
							onClick="helpWindow('<%= request.getContextPath() %>','<%= metaDataProperty.getHelpFile() %>')"/>
     				<div class="textinput-label"><label for="<%= metaDataProperty.getKey() %>"><%= metaDataProperty.getLabel() %></label></div>
					<select class="textinput" name="<%= metaDataProperty.getKey() %>">
<%
					Vector<String> fieldOptionValues = metaDataProperty.getFieldOptionValues();
					Vector<String> fieldOptionNames = metaDataProperty.getFieldOptionNames();
					for (int i = 0; i < fieldOptionNames.size(); i++) {
%>
					<option value="<%= fieldOptionValues.elementAt(i) %>"
<%
						if (fieldOptionValues.elementAt(i).equals(request.getAttribute(metaDataProperty.getKey()))) {
%>
							selected="yes"
<%
						}
%>
					> <%= fieldOptionNames.elementAt(i) %>
<%
				}
%>
					</select>
				</div>
<%		
				} else if (fieldType.equals("password")) {
%>
				<div class="form-row">
					<img class="question-mark" src="style/images/question-mark.gif"  
	           		       onClick="helpWindow('<%= request.getContextPath() %>','<%= metaDataProperty.getHelpFile() %>')"/>
					<div class="textinput-label"><label for="<%= metaDataProperty.getKey() %>" ><%= metaDataProperty.getLabel() %></label></div>
					<input class="textinput" id="<%= metaDataProperty.getKey() %>" 
							name="<%= metaDataProperty.getKey() %>" 	             		    	    	           		    	             			
	           		    	value="<%= request.getAttribute(metaDataProperty.getKey()) %>"
	           		    	type="<%= fieldType %>"/> 
				</div>
<%
				} else {
%>
				<div class="form-row">
					<img class="question-mark" class=question-mark 
					     src="style/images/question-mark.gif" 
					     onClick="helpWindow('<%= request.getContextPath() %>','<%= metaDataProperty.getHelpFile() %>')"/>
					<div class="textinput-label"><label for="<%= metaDataProperty.getKey() %> "><%= metaDataProperty.getLabel() %></label>	</div>					
					<input class="textinput" id="<%= metaDataProperty.getKey() %>" 
							name="<%= metaDataProperty.getKey() %>" 
	    			        value="<%= request.getAttribute(metaDataProperty.getKey()) %>"	             		    	    	           		    	             			
	           		    	type="<%= fieldType %> "/>	
				</div>          		    
<%   			
				}
    			
    			if (metaDataProperty.getDescription() != null) {
%>

					<div class="textinput-description">[<%= metaDataProperty.getDescription() %>]</div>
<%
    			}
			}
			}
		}
			
	}
%>

			<input type="hidden" name="configureType" value="properties"/>
			<input type="hidden" name="processForm" value="true"/>
			<input class=left-button type="submit" value="Save"/>
			<input class=button type="button" value="Cancel" onClick="forward('./admin')"> 

</form>

<%@ include file="./footer-section.jsp"%>

</body>
</html>
