<%
/**
<!--
  *   '$RCSfile$'
  *   '$Author$'
  *   '$Date$'
  *   '$Revision$'
  *
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
-->
  */
%>
<%@ page import="org.apache.tools.mail.MailMessage" %>
<%@ page import="java.io.PrintStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.net.URLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="edu.ucsb.nceas.metacat.spatial.XSLTransform" %>
<%
	System.out.println("********************************************************");
	System.out.println("********************************************************");
	System.out.println("**************** metacat_spatialresolver.jsp*****************\n");


	String _request = request.getParameter("REQUEST");
	_request = (_request!=null)? _request.trim() : "";

	System.out.println("request param: >>" + _request);
	System.out.println("uri: >>" + request.getRequestURI());
	System.out.println("request string: >>" + request.getQueryString()+" \n");
					   
	// connect to the metacat spatial option, request the info using the wms getFeatureId call
	// parse the response for the url to the metacat document and then redirect there. The query 
	// should look like
	// VERSION=1.1.1&REQUEST=GetFeatureInfo&SRS=EPSG:4326&BBOX=-143.09099999999998,19.856,-96.79899999999999,43.002&WIDTH=600&HEIGHT=300&LAYERS=topp:RIVERS&FORMAT=text/html&FEATURE_COUNT=1&QUERY_LAYERS=topp:RIVERS&X=328&Y=218

	if (_request.toLowerCase().equals("aoimetacatquery")) {
		// bounded box
		System.out.println("handling AoiMetacatQuery");
		String urlString = "http://dataknp.sanparks.org:8080/knp/metacat?action=spatial_query&"+ request.getQueryString();
		
		System.out.println("redirecting to: " + urlString);
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location",urlString);
		
		
		/***********
		URL url = new URL(urlString);
		URLConnection c = url.openConnection();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(c.getInputStream()));
		String inputLine;
		//StringBuffer xmlResults = new StringBuffer(4096);
		try {
		PrintWriter _out = response.getWriter();
			while ((inputLine = in.readLine()) != null) {
				//	xmlResults.append(inputLine);
				//System.out.println(inputLine);
				out.println(inputLine);
			} 
		} catch (Exception e) { e.printStackTrace(); }

		***/
		
		
		// transform xml into html
		//String xslPath = "/tmp/spatial_results.xsl";
		//XSLTransform.transform(xmlResults.toString(), xslPath, response.getWriter(), null);


		/*
		System.out.println("redirecting to: " + urlString);
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location",urlString);
		*/
	} else {
		// feature info
		if ( request.getQueryString().indexOf("metacat_testdata") > -1 ) {

			URL url = new URL("http://dataknp.sanparks.org:8080/geoserver/wms?"+request.getQueryString());
			URLConnection c = url.openConnection();
			BufferedReader in = new BufferedReader(
			new InputStreamReader(
			c.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				if ( inputLine.startsWith("url") ) {

					StringTokenizer _st = new StringTokenizer(inputLine, " ");
					_st.nextToken(); // url string
					_st.nextToken(); // equals sign
					String _redirectTo = _st.nextToken();
					System.out.println("redirecting to: " + _redirectTo); 
					response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
					String newLocn = _redirectTo;
					response.setHeader("Location",newLocn);
				}
			}
			in.close();

		} else {
			System.out.println(" -- not a metacat query");
			String urlString = "/geoserver/wms?"+ request.getQueryString();

			System.out.println("redirecting to: " + urlString);
			response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			response.setHeader("Location",urlString);
		}
	}

if (false) {
%><!--html>
<head>
<title>Metacat Spatial Resolver</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="styles.css" rel="stylesheet" type="text/css">
</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" background="images/blue_pixel_bg.jpg">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <!--DWLayoutTable-->
  <tr>
          <td  width="700" height="20" background="images/blue_pixel_bg.jpg"></td>

  </tr>
    
 
  
  
  <tr> 
    <td bgcolor="#6699CC"> <table width="700" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td>&nbsp;</td>
          <td><td>&nbsp;</td>
        </tr>
      </table>
  </tr>
</table>
</body>
</html-->
<% } %>
