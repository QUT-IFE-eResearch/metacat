<?xml version="1.0"?>
<!--
  *  '$RCSfile: eml.xsl,v $'
  *      Authors: Matt Jones
  *    Copyright: 2000 Regents of the University of California and the
  *               National Center for Ecological Analysis and Synthesis
  *  For Details: http://www.nceas.ucsb.edu/
  *
  *   '$Author: obrien $'
  *     '$Date: 2008-08-28 22:59:33 $'
  * '$Revision: 1.8 $'
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
  * convert an XML file that is valid with respect to the eml-dataset.dtd
  * module of the Ecological Metadata Language (EML) into an HTML format
  * suitable for rendering with modern web browsers.
  <xsl:output method="html"  />
-->
<!DOCTYPE html [
  <!ENTITY inchead SYSTEM "../include_head.jsp?jqueryui=true">
  <!ENTITY incheader SYSTEM "../include_header.jsp">
  <!ENTITY incside SYSTEM "../include_side.jsp">
  <!ENTITY incmenu SYSTEM "../include_menu.jsp">
  <!ENTITY incfooter SYSTEM "../include_footer.jsp">
  <!ENTITY incbanner SYSTEM "../include_banner.jsp">
  <!ENTITY incemljs SYSTEM "../eml-front.jsp">
  <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/02/xpath-function" xmlns:eml="eml://ecoinformatics.org/eml-2.1.0" xmlns:fb="http://www.facebook.com/2008/fbml" version="1.0">
  <xsl:import href="emlroot.xsl"/>

  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="utf-8" indent="yes" />

  <xsl:template match="/">
    <xsl:variable name="docTitle" select="./eml:eml/dataset/title" />
    <html>
      <head>
        <meta property="fb:app_id" content="178411865546686"> </meta>
        <title><xsl:value-of select="concat('ASN - ',$docTitle)" /></title>
        <meta name="DC.Title" content="{$docTitle}"/>
        &inchead;
        &incemljs;
      </head>
      <body class="full">
        <div class="main">
      &incheader;
      <div class="insideWrapper">
      &incmenu;
        <div class="container">        
        <div class="main-content">    
			<div style="display:block" id="noBanner">
		&incbanner;
          	<div class="contentSeparator"> </div>
			</div> 
		&incside;
              <div class="templatecontentareaclass left">
                <div class="shortcutNav">
                  <div class="left"><a href="javascript:history.back()">&lt;&lt;&nbsp;Back</a>&nbsp;&nbsp; <a href="{$contextURL}">New Search</a></div>
                  <div class="clear"></div>
                </div>
                <div id="tabs" class="tabs">
                  <ul>
                    <li><a href="#tabMetadata">Metadata</a></li>
                    <xsl:if test="$displaymodule='dataset' or $displaymodule='printall'">
                    <li><a href="#tabData">Data Files&nbsp;</a></li>
                    </xsl:if>
                    <li><a href="#tabComments">Public Comments</a></li>
                  </ul>				
                <div class="metadatatabtext">
		<h2><xsl:value-of select="$docTitle"/></h2>
                  <xsl:apply-templates select="*[local-name()='eml']"/>
                  
                  <div id="tabComments" >
					<!-- Begin Facebook Comment tag-->
					<div id="fb-root"></div>
					<script src="http://connect.facebook.net/en_US/all.js#xfbml=1"></script>
					<fb:comments num_posts="5" width="700" href="{$docURL}"></fb:comments>
					<!--End Facebook Comment tag-->
                  </div>
		</div>
                </div> <!--tabs-->
                  <div class="clear"></div>
              </div> <!-- documentArea -->
	</div> <!-- templatecontent -->	
		<div class="filler clear"></div>
    </div>
        </div> <!-- mainContainer -->
		      <div class="footer">
		<!--div footer-->
        &incfooter;
		<!--/div footer-->
		    </div>
		</div>
        <!--Dialogs-->
        <div class="dialogs" style="display:none">
		<div id="currentSessionId"></div>
		<div id="currentLdap_dn"></div>
		<div id="dialogRegister">
		<div id="sendRegister">
			<h2>Request Access or Login to Download File</h2>
			<p class="registerFormTable" style="display:none;">In order to acces the data file you need to register. Please complete this form.</p>
  			<table>
  			<tr><td class="registerFormTable" style="display:none;">
  			<form class="registerForm" name="registerForm" action="javascript:registerOnDialog();">
    		<fieldset>
    		<legend>Register</legend>
    		<table >
    		<tr>
      			<td><label for="name" id="name_label">Name: </label><span style="color:red;">*</span></td>
      			<td><input type="text" name="name" id="name" size="25" value="" /></td>
				<td><label class="error" for="name" id="name_error">Required.</label></td>
			</tr>	
			<tr>
      			<td><label for="email" id="email_label">Email: </label><span style="color:red;">*</span></td>
      			<td><input type="text" name="email" id="email" size="25" value=""  /></td>
      			<td><label class="error" for="email" id="email_error">Required.</label></td>
			</tr>	
			<tr>
      			<td><label for="phone" id="phone_label">Telephone: </label></td>
      			<td><input type="text" name="phone" id="phone" size="25" value=""  /></td>
      			<td></td>
    		</tr>	
    		<tr>
     			<td><span style="color:red;">*</span><label for="reasons" id="reasons_label">Reasons: </label></td>
     			<td><textarea name="reasons" id="reasons" rows="3" cols="23" value=""></textarea></td>
      			<td><label class="error" for="reasons" id="reasons_error">Required.</label></td>
    		</tr>	
    		<tr>
	    		<td><label class="error" for="reasonsRequest" id="isMsgSent_error"><br></br>Your message was not sent!<br></br></label></td>
<td></td>
<td></td>
</tr>
<tr>
    			<td><a style="color: #0077BB" href="javascript:document.registerForm.reset()">reset form</a></td>
<td><input class="dialogClose" type="submit" onClick="javascript:$('#dialogRegister').dialog('close');" value="Close" /></td>
      			<td width="200"><input type="submit" class="registerFormSubmit" value="Request" /></td>
      		</tr>
      		</table>
    		</fieldset>
    		
  			</form>
  			</td>
  			<td class="LoginFormTable">
  			<form class="loginForm2" name="loginForm2" action="javascript:loginOnDialog();" default="loginbutton">
  			<fieldset>
  			<legend>Login</legend>
  			<table >
    		<tr>
      			<td><label for="username2" id="username2_label">Username</label></td>
      			<td><input type="text" name="username2" id="username2" size="20" value="" /></td>
				<td><label class="error" for="username2" id="username2_error">Required.</label></td>
				<td><input type="hidden" name="organization" value="" /></td>
			</tr>	
			<tr>
      			<td><label for="password2" id="password2_label">Password</label></td>
      			<td><input type="password" name="password2" id="password2" size="20" value="" /></td>
      			<td><label class="error" for="password2" id="password2_error">Required.</label></td>
				<td></td>
    		</tr>
    		<tr>
				<td colspan="4"><label class="error" for="loginForm2" id="loginForm2_error" style="display:inline;"><span>The username or password you have entered is incorrect.</span></label></td>
      		</tr>
<tr>
<td><input class="dialogSwapLoginRegister" type="button" onclick="javascript:swapLoginRegister();" value="Request Access" tabindex="3"/></td>
<td>    <input class="dialogClose" type="button" onClick="javascript:$('#dialogRegister').dialog('close');" value="Close" tabindex="2"/>
</td>
<td><input type="submit" class="loginFormSubmit2" value="Login" tabindex="1" id="loginbutton" /></td>
<td></td>

</tr>
  			</table>
  			</fieldset>
  			</form>
  			</td></tr>
  			</table>
  		</div>
  		<div id="registerSent" style="dysplay:none">
  			<h2>Register Form Submitted!</h2>
  			<p>Your request has been submitted and will be processed as soon as possible.</p>
		<p>If you do not receive a prompt reply please email us at</p> 
		<div class="emailSupportLink"></div>		
<div class="right">
<input class="dialogClose" type="submit" onClick="javascript:$('#dialogRegister').dialog('close');" value="Close" /></div>
  		</div>
  	</div>
  
	<div id="dialogRequest">
		<div id="sendRequest">
	  		<h2>You do not have access to this file </h2>
	  		<h2>Request for Access?</h2>
			<p>Please input your reasons for accessing the file. An email will be sent to the data librarian</p>
			<form class="requestForm" action="javascript:requestOnDialog();">
			<label for="reasons" id="reasonsRequest_label"></label>
				<textarea name="reasons" id="reasonsRequest" rows="5" cols="60" class="text-input" value=""></textarea>
				<br></br><br></br>
				<label class="error" for="reasonsRequest" id="reasonsRequest_error"><span>Please complete the form to send the request.</span></label>
				<label class="error" for="reasonsRequest" id="isMsgSent_error"><span>Your message was not sent!</span></label>
<div class="right">
                <input class="dialogClose" type="submit" onClick="javascript:$('#dialogRequest').dialog('close');" value="Close" />&#160;&#160;
				<input type="submit" class="requestFormSubmit" value="Request"/>
</div>
	  		</form>
	  	</div>
	  	<div id="requestSent" style="display:none">
		  	<h2>Request Access Notification</h2>
			    <p>Your request has been submitted and will be processed as soon as possible.</p>
			    <p>If you do not receive a prompt reply please email us at</p>
				<div class="emailSupportLink"></div>
<div class="right">
		<input class="dialogClose" type="submit" onClick="javascript:$('#dialogRequest').dialog('close');" value="Close" /></div>
	  </div>
	</div>
	<div id="viewLicence">
	  <p class="Licence">
	  </p>
<div class="right">
		<input class="dialogClose" type="submit" onClick="javascript:$('#viewLicence').dialog('close');" value="Close" /></div>
	</div>	
	<div id="dialogDownload">
	  <h2>Licence</h2>
	  <p class="licenceContent">
	  </p>
	  <p style="dysplay:none" class="errorLicence"></p>
	<div class="dialogButtonsA">
	  <input class="labelLicence" id="licenceCheck" type="checkbox"/> <label for="licenceCheck"> Accept Licence Agreement</label><p></p>
<span id="mustAcceptLicence">You have to accept the licence agreement to be able to download the data table.</span>
	</div>
<div class="cancelDownload">
<div class="dialogDownloadButtons">
	<input class="dialogClose" type="submit" onClick="javascript:$('#dialogDownload').dialog('close');" value="Cancel" />
&#160;</div>
	  <div class="acceptLicenceA">
<!--<span id="mustAcceptLicence">You have to accept the licence agreement to be able to download the data table.</span>
-->
<a id="directDownloadLink">Download File</a></div>
	</div>
	</div>
</div>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>


