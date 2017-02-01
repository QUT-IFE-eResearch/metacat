<%@ page language="java" %>
<%@ include file="settings.jsp" %>
<%
    boolean jqueryui = (request.getParameter("jqueryui") != null && request.getParameter("jqueryui").equals("true"));
    String title = request.getParameter("title");
    if (title != null && !title.isEmpty()) {
%>
  <title>RELRP - <%=title%></title>
  <meta name="DC.Title" content="<%=title%>"/>
<%  } %>
  <meta name="description" content="MLA RELRP data" />
  <meta name="keywords" content="MLA, RELRP, methane emission reduction, livestock, research, australia"/>
  <meta name="DC.Creator.PersonalName" content="Alvin Sebastian"/>
  <meta name="DC.Description" content="Repository of data stemming from the Meat and Livestock Australia RELRP programme"/>
  <meta name="DC.Title" content="MLA RELRP portal"/>
  <meta name="DC.Subject" content="RELRP, MLA, livestock emissions, anzsrc-for 0701"/>
  <meta name="DC.Date.Created" scheme="ISO8601" content="2012-04-11"/>
  <meta name="DC.Date.ValidTo" scheme="ISO8601" content="2016-04-11"/>
  <meta name="DC.Identifier" scheme="URI" content="http://www.relrp.org.au"/>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  
<% if (jqueryui) { %>
  <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" />
<% } %>
  <link rel="stylesheet" type="text/css" href="<%=MAIN_SITE_URL%>/<%=SKIN_NAME%>.css" />
  <link rel="stylesheet" type="text/css" href="<%=SKIN_URL%>/<%=SKIN_NAME%>.css" />
  <script type="text/javascript">
    var SKIN_NAME  = "<%=SKIN_NAME%>";
    var SKIN_URL  = "<%=SKIN_URL%>";
    var LOGIN_LABEL = "<%=LOGIN_LABEL%>";
    var LOGOUT_LABEL = "<%=LOGOUT_LABEL%>";
  </script>
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<% if (jqueryui) { %>
  <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"></script>
<% } %>
  <script type="text/javascript" src="<%=SKIN_URL%>/<%=SKIN_NAME%>.js"></script>
  <script type="text/javascript" src="<%=MAIN_SITE_URL%>/borders.js"></script>
<%
   boolean showMap = (request.getParameter("showMap") != null && request.getParameter("showMap").equals("true"));
   if (showMap) { 
%>  
  <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false"></script>
  <script type="text/javascript" src="http://openlayers.org/api/2.11/OpenLayers.js"></script>
  <script type="text/javascript" src="<%=SKIN_URL%>/map.js"></script>
<% } %>
  <script type="text/javascript" src="<%=MAIN_SITE_URL%>/google_analytics.js"></script>