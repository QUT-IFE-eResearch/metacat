<%@ page    language="java" %>
<%
/**
 *  '$RCSfile$'
 *    Copyright: 2004 Regents of the University of California and the
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
<title>Account Management</title>
  <link rel="stylesheet" type="text/css" 
        href="<%=STYLE_SKINS_URL%>/account/account.css"></link>
  <script language="JavaScript" type="text/JavaScript"
          src="<%=STYLE_SKINS_URL%>/account/account.js"></script>
  <script language="JavaScript" type="text/JavaScript"
          src="<%=STYLE_COMMON_URL%>/branding.js"></script>
</head>
<body>
      <script language="JavaScript">
          insertTemplateOpening("<%=CONTEXT_URL%>");
          insertSearchBox("<%=CONTEXT_URL%>");
      </script>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr><td colspan="5">

<p>The <b>Account Manager</b> is used to manage your preferences regaring your
ecoinformatics.org account.  Currently you can change your password if you
know your previous password, and , if you have forgotten your password,
you can reset it to a new value that will be emailed to the address on 
record for the account.</p>

<p>Account holders enjoy several services provided by ecoinformatics.org, 
but most notably they have access to the CVS repositories.</p>

<p>New accounts can be requested by emailing pmc@ecoinformatics.org with your
full name (given name and surname), email address, institutional affiliation, and a statement regarding why you want the account (e.g., which projects you are involved with).</p>

<br><br>
<tr><td colspan="5">
<b>Choose an action:</b>

    <ul>
        <li><a href="<%=CGI_URL%>/ldapweb.cgi?cfg=account&amp;stage=changepass">Change your password</a>
        <li><a href="<%=CGI_URL%>/ldapweb.cgi?cfg=account&amp;stage=resetpass">Reset your password</a>
    </ul>
</td>
</tr>
</table>

<script language="JavaScript">          
    insertTemplateClosing("<%=CONTEXT_URL%>");
</script>
</body>
</html>
