<%@ page language="java"%>
<% 
/**
 *  '$RCSfile$'
 *    Copyright: 2008 Regents of the University of California and the
 *               National Center for Ecological Analysis and Synthesis
 *  For Details: http://www.nceas.ucsb.edu/
 *
 *   '$Author: daigle $'
 *     '$Date: 2008-12-27 07:05:06 +1000 (Sat, 27 Dec 2008) $'
 * '$Revision: 4697 $'
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

<hr class="config-line">

<%
if( request.getSession().getAttribute("userId") != null) {  
%>
  <div class=small-message>
    <p>You are logged in as: <%= request.getSession().getAttribute("userId") %></p>
    <a href="<%= request.getContextPath() %>/admin?configureType=login">log in as different user</a> |
    <a href="<%= request.getContextPath() %>/docs/user/index.html" target="_blank">view metacat user documentation</a>
  </div>
<% 
} else {
%>
  <div class=small-message>
    <p>You are not logged in.</p>
    <a href="<%= request.getContextPath() %>/admin?configureType=login">log in as different user</a> |
    <a href="<%= request.getContextPath() %>/docs/user/index.html" target="_blank">view metacat user documentation</a>
  </div>
<%
}
%>