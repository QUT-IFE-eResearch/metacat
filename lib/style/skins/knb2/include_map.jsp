<% 
 /**
  *  '$RCSfile$'
  *      Authors: Matt Jones, CHad Berkley
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: perry $'
  *     '$Date: 2006-12-07 09:14:19 +1000 (Thu, 07 Dec 2006) $'
  * '$Revision: 3106 $'
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
  * This is an XSLT (http://www.w3.org/TR/xslt) stylesheet designed to
  * convert an XML file showing the resultset of a query
  * into an HTML format suitable for rendering with modern web browsers.
  */
%>
<!-- *********************** START MAP TABLE ************************* -->
<table width="740" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="10" align="right" valign="top"><img src="images/panelhead_bg_lcorner.gif" width="10" height="21"></td>
    <td width="720" class="sectionheader"> KNB Data Catalog Map</td>
    <td width="10" align="left" valign="top"><img src="images/panelhead_bg_rcorner.gif" width="10" height="21"></td>
  </tr>
  <tr> 
    <td colspan="3">
      <table width="740" border="0" cellpadding="0" cellspacing="0" class="subpanel">
       <tr><td>
        <!-- <iframe frameborder="0" src="spatial/map.html" width="738" height="520">You need a browser that supports iframes</iframe> -->
	<iframe scrolling="no" frameborder="0" width="736" height="520" src="spatial/map.html">
	You need iframe support 
	</iframe>
       </td></tr>
      </table>
    </td>
  </tr>
</table>
<!-- ************************* END MAP TABLE ************************* -->
