<%@ page errorPage="jsperrorpage.html" %>
<%
/*
 *   '$RCSfile$'
 *     Authors: Matthew Brooke
 *   Copyright: 2000 Regents of the University of California and the
 *              National Center for Ecological Analysis and Synthesis
 * For Details: http://www.nceas.ucsb.edu/
 *
 *    '$Author: daigle $'
 *      '$Date: 2008-07-07 14:25:34 +1000 (Mon, 07 Jul 2008) $'
 *  '$Revision: 4080 $'
 *
 * Settings file for pisco portal
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

<%@ include file="../../../../common/common-settings.jsp"%>
<%@ include file="../../../../common/configure-check.jsp"%>

<% 
// Global settings for metacat are in common-settings.jsp.  Use this file to
// override or add to global values for this skin.

  // default title for :
  String      DEFAULT_PORTALHEAD_TITLE = "NEW Biocomplexity Data Search";
  
  // if true, POST variables echoed at bottom of client's browser window in a big yellow box
  // Set this value to override the global value
  // boolean     DEBUG_TO_BROWSER      = false;
  
  // Add any local post fields to COMMON_SEARCH_METACAT_POST_FIELDS, 
  // SIMPLE_SEARCH_METACAT_POST_FIELDS, and ADVANCED_SEARCH_METACAT_POST_FIELD here
  COMMON_SEARCH_METACAT_POST_FIELDS +=
        "<input type=\"hidden\" name=\"qformat\"       value=\"knb2\"\\>\n"
       +"<input type=\"hidden\" name=\"returnfield\"   value=\"dataset/dataTable/distribution/online/url\"\\>\n"
       +"<input type=\"hidden\" name=\"returnfield\"   value=\"dataset/dataTable/distribution/inline\"\\>\n";
          
  SIMPLE_SEARCH_METACAT_POST_FIELDS +=
       "<input type=\"hidden\" name=\"qformat\"       value=\"knb2\"\\>\n"
      +"<input type=\"hidden\" name=\"returnfield\"   value=\"dataset/dataTable/distribution/online/url\"\\>\n"
      +"<input type=\"hidden\" name=\"returnfield\"   value=\"dataset/dataTable/distribution/inline\"\\>\n";

  ADVANCED_SEARCH_METACAT_POST_FIELDS +=
       "<input type=\"hidden\" name=\"qformat\"       value=\"knb2\"\\>\n"
      +"<input type=\"hidden\" name=\"returnfield\"   value=\"dataset/dataTable/distribution/online/url\"\\>\n"
      +"<input type=\"hidden\" name=\"returnfield\"   value=\"dataset/dataTable/distribution/inline\"\\>\n";

   
%>
