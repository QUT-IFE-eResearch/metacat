<%@ page    language="java" %>
<%
/**
 * '$RCSfile$'
 * Copyright: 2008 Regents of the University of California and the
 *            National Center for Ecological Analysis and Synthesis
 *  '$Author: daigle $'
 *    '$Date: 2008-11-04 10:09:04 +1000 (Tue, 04 Nov 2008) $'
 * '$Revision: 4505 $'
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

<form method="POST" action="<%=SERVLET_URL%>" target="_top">
Search for: 
  <input value="INTERSECT" name="operator" type="hidden">   
  <input size="14" name="anyfield" type="text" value="">
  <input name="organizationName" value="Organization of Biological Field Stations" type="hidden">
  <input name="action" value="query" type="hidden">
  <input name="qformat" value="obfs" type="hidden">
  <input name="enableediting" value="true" type="hidden">
  <input name="operator" value="UNION" type="hidden">
  <input name="returnfield" value="originator/individualName/surName" type="hidden">
  <input name="returnfield" value="originator/individualName/givenName" type="hidden">
  <input name="returnfield" value="creator/individualName/surName" type="hidden">
  <input name="returnfield" value="creator/individualName/givenName" type="hidden">
  <input name="returnfield" value="originator/organizationName" type="hidden">
  <input name="returnfield" value="creator/organizationName" type="hidden">
  <input name="returnfield" value="dataset/title" type="hidden">
  <input name="returnfield" value="keyword" type="hidden">
    <input name="returndoctype" value="eml://ecoinformatics.org/eml-2.1.0" type="hidden">
  <input name="returndoctype" value="eml://ecoinformatics.org/eml-2.0.1" type="hidden">
  <input name="returndoctype" value="eml://ecoinformatics.org/eml-2.0.0" type="hidden">
  <input name="returndoctype" value="-//ecoinformatics.org//eml-dataset-2.0.0beta6//EN" type="hidden">
  <input name="returndoctype" value="-//ecoinformatics.org//eml-dataset-2.0.0beta4//EN" type="hidden">
  <input name="returndoctype" value="-//NCEAS//resource//EN" type="hidden">
  <input name="returndoctype" value="-//NCEAS//eml-dataset//EN" type="hidden">
  <input value="Start Search" type="submit">
</form>
