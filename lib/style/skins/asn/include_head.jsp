<%@ page language="java" %>
<%@ include file="settings.jsp" %>
  <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
  <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
  <meta name="description" content="tern, tern repository, tern ecological research and data from australia and world wide, stored in a collaborative repository"/>
  <meta name="keywords" content="tern, repository, data, samples, research, australia, network, ecological, terrestrial, ecosystem"/>
  <meta name="DC.Creator.PersonalName" content="Dewi Wahyuni"/>
  <meta name="DC.Description" content="tern network, TERN research and data from australia  stored in a collaborative repository"/>
  <meta name="DC.Title" content="ASN Repository"/>
  <meta name="DC.Subject" content="tern, repository, data, samples, research, australia, network, emissions"/>
  <meta name="DC.Date.Created" scheme="ISO8601" content="2009-12-18"/>
  <meta name="DC.Date.ValidTo" scheme="ISO8601" content="2010-12-14"/>
  <meta name="DC.Identifier" scheme="URI" content="http://www.tern.net.au"/>

  <link rel="icon" type="image/ico" href="<%=MAIN_SITE_URL%>/templates/asn/images/favicon.ico"/> 
  <link rel="stylesheet" type="text/css" href="<%=MAIN_SITE_URL%>/templates/asn/css/styles.css" />
  <link href="<%=SKIN_URL%>/jquery-ui.css" type="text/css" rel="stylesheet"/>
  <link rel="stylesheet" type="text/css" href="<%=SKIN_URL%>/<%=SKIN_NAME%>.css" />
  

  <script type="text/javascript">
    var SKIN_NAME  = "<%=SKIN_NAME%>";
    var SKIN_URL  = "<%=SKIN_URL%>";
    var LOGIN_LABEL = "<%=LOGIN_LABEL%>";
    var LOGOUT_LABEL = "<%=LOGOUT_LABEL%>";
    var ORGANIZATION = "<%=ORGANIZATION%>";
    var SUPPORTEMAIL = "<%=SUPPORTEMAIL%>";
  </script>
  
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
  <script type="text/javascript" src="<%=SKIN_URL%>/jquery-ui.min.js"></script>
  <script type="text/javascript" src="<%=SKIN_URL%>/jquery.blockUI.js"></script>
  <script type="text/javascript" src="<%=SKIN_URL%>/<%=SKIN_NAME%>.js"></script>
<%
   boolean showMap = (request.getParameter("showMap") != null && request.getParameter("showMap").equals("true"));
   if (showMap) { 
%>  
  <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false"></script>
  <script type="text/javascript" src="http://openlayers.org/api/2.11/OpenLayers.js"></script>
  <script type="text/javascript" src="<%=SKIN_URL%>/map.js"></script>
<% } %>
  <script type="text/javascript" src="<%=MAIN_SITE_URL%>/templates/asn/js/google_analytics.js"></script>
