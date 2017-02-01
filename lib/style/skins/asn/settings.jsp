<%@ page  errorPage="jsperrorpage.html"  import="javax.servlet.http.*,edu.ucsb.nceas.metacat.properties.PropertyService,edu.ucsb.nceas.metacat.util.SystemUtil" %>
<%
/*
 *   '$RCSfile$'
 *     Authors: Matthew Brooke
 *   Copyright: 2000 Regents of the University of California and the
 *              National Center for Ecological Analysis and Synthesis
 * For Details: http://www.nceas.ucsb.edu/
 *
 *    '$Author: daigle $'
 *      '$Date: 2008-07-06 21:25:34 -0700 (Sun, 06 Jul 2008) $'
 *  '$Revision: 4080 $'
 *
 * Settings file for the default metacat skin
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
<%@ include file="../../common/configure-check.jsp"%>
<%

// GLOBAL CONSTANTS FOR KNB PORTAL PAGE /////////////////////////////////////

// if true, POST variables echoed at bottom of client's browser window in a big yellow box
// Set this value to override the global value
boolean DEBUG_TO_BROWSER = false;

String COMMON_SEARCH_METACAT_POST_FIELDS = "<input type=\"hidden\" name=\"action\" value=\"query\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"originator/individualName/surName\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"originator/individualName/givenName\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"originator/organizationName\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"creator/individualName/surName\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"creator/organizationName\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"dataset/title\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"keyword\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"creator/individualName/givenName\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"idinfo/citation/citeinfo/title\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"idinfo/citation/citeinfo/origin\" />\n"
			+ "<input type=\"hidden\" name=\"returnfield\" value=\"idinfo/keywords/theme/themekey\" />\n"
			+ "<input type=\"hidden\" name=\"returndoctype\" value=\"metadata\" />\n"
			+ "<input type=\"hidden\" name=\"returndoctype\" value=\"-//ecoinformatics.org//eml-dataset-2.0.0beta6//EN\" />\n"
			+ "<input type=\"hidden\" name=\"returndoctype\" value=\"-//ecoinformatics.org//eml-dataset-2.0.0beta4//EN\" />\n"
			+ "<input type=\"hidden\" name=\"returndoctype\" value=\"eml://ecoinformatics.org/eml-2.1.0\" />\n"
			+ "<input type=\"hidden\" name=\"returndoctype\" value=\"eml://ecoinformatics.org/eml-2.0.1\" />\n"
			+ "<input type=\"hidden\" name=\"returndoctype\" value=\"eml://ecoinformatics.org/eml-2.0.0\" />\n"
			+ "<input type=\"hidden\" name=\"returndoctype\" value=\"-//NCEAS//eml-dataset-2.0//EN\" />\n"
			+ "<input type=\"hidden\" name=\"returndoctype\" value=\"-//NCEAS//resource//EN\" />\n";

String SIMPLE_SEARCH_METACAT_POST_FIELDS = "<input type=\"hidden\" name=\"operator\" value=\"UNION\" />\n"
			+ COMMON_SEARCH_METACAT_POST_FIELDS;

String ADVANCED_SEARCH_METACAT_POST_FIELDS = "<input type=\"hidden\" name=\"operator\" value=\"INTERSECT\" />\n"
			+ COMMON_SEARCH_METACAT_POST_FIELDS;

// label for logout form button when user *is* logged in:
String LOGOUT_LABEL = "Logout";

// label for login form button when user is *not* logged in:
String LOGIN_LABEL = "Login";

// last part of LDAP username to be appended after organization
// String LDAP_DOMAIN = ",dc=ecoinformatics,dc=org";
String LDAP_DOMAIN = ","+PropertyService.getProperty("auth.base");

String KNB_SITE_URL = PropertyService.getProperty("application.knbSiteURL");
String CONTEXT_NAME = PropertyService.getProperty("application.context");
String DEFAULT_STYLE = PropertyService.getProperty("application.default-style");
String SERVER_URL = SystemUtil.getServerURL();
String CGI_URL = SystemUtil.getCGI_URL();
String CONTEXT_URL = SystemUtil.getContextURL();
String SERVLET_URL = SystemUtil.getServletURL();
String STYLE_SKINS_URL = SystemUtil.getStyleSkinsURL();
String STYLE_COMMON_URL = SystemUtil.getStyleCommonURL();
String DEFAULT_STYLE_URL = SystemUtil.getDefaultStyleURL();
String SUPPORTEMAIL = PropertyService.getProperty("email.recipient");
String ORGANIZATION = PropertyService.getProperty("auth.defaultOrg");
String SKIN_NAME = "asn";
String SKIN_URL = STYLE_SKINS_URL + "/" + SKIN_NAME;
String MAIN_SITE_URL = SERVER_URL;
pageContext.setAttribute("MAIN_SITE_URL",MAIN_SITE_URL);
String MINTSERVICE = PropertyService.getProperty("server.mintservice");
String MINTSERVICESYSID = PropertyService.getProperty("server.mintservicesysid");
String JSONPCALLBACK = PropertyService.getProperty("server.jsonpcallback");

// Add any local post fields to COMMON_SEARCH_METACAT_POST_FIELDS, 
// SIMPLE_SEARCH_METACAT_POST_FIELDS, and ADVANCED_SEARCH_METACAT_POST_FIELD here
COMMON_SEARCH_METACAT_POST_FIELDS += "<input type=\"hidden\" name=\"qformat\" value=\""+SKIN_NAME+"\" />\n";
SIMPLE_SEARCH_METACAT_POST_FIELDS += "<input type=\"hidden\" name=\"qformat\" value=\""+SKIN_NAME+"\" />\n";
ADVANCED_SEARCH_METACAT_POST_FIELDS += "<input type=\"hidden\" name=\"qformat\" value=\""+SKIN_NAME+"\" />\n";
%>