<%@ page import="edu.ucsb.nceas.metacat.client.*,java.util.*" %>
<% 
 /**
  *  '$RCSfile$'
  * Authors: Alvin Sebastian
  * Copyright: 2012 Queensland University of Technology
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
  *
  */

  //////////////////////////////////////////////////////////////////////////////
  //
  // NOTE: GLOBAL CONSTANTS (SETTINGS SUCH AS METACAT URL, LDAP DOMAIN	AND DEBUG 
  //       SWITCH) ARE ALL IN THE INCLUDE FILE "settings.jsp"
  //
  //////////////////////////////////////////////////////////////////////////////

  // GLOBAL VARIABLES //////////////////////////////////////////////////////////
	String sessionId = "";
	String sessionUsername = "";
	String sessionPassword = "";
	String sessionLdap_dn = "";
	String sessionEmail = "";
  HttpSession rfSession;
  //////////////////////////////////////////////////////////////////////////////	
  try {
    rfSession = request.getSession(true);
    Object sess_sessionIdObj = rfSession.getAttribute("sess_sessionIdObj");
    
    if (sess_sessionIdObj != null) {
      sessionId = (String)sess_sessionIdObj;
      Object attr = rfSession.getAttribute("name");
      if (attr != null) sessionUsername = (String) attr;
      attr = rfSession.getAttribute("password");
      if (attr != null) sessionPassword = (String) attr;
	  attr = rfSession.getAttribute("ldap_dn");
      if (attr != null) sessionLdap_dn = (String) attr;
	  attr = rfSession.getAttribute("ldap_email");
      if (attr != null) sessionEmail = (String) attr;
    }
	} catch(Exception e) {
		throw new ServletException("trying to get session: "+e);
	}
	
  if (DEBUG_TO_BROWSER) {
%>	
<table>							
	<tr> 
		<td colspan="4" align="left" valign="top" class="text_plain" bgcolor="#ffff00">
			<ul>
				<li>sessionId:&nbsp;<%=sessionId%></li>
			</ul></td>
  </tr>
</table>
<%
  }
%>
