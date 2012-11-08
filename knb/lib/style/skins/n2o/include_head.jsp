<%@ page language="java" %>
<%@ include file="settings.jsp" %>
<%
    boolean jqueryui = (request.getParameter("jqueryui") != null && request.getParameter("jqueryui").equals("true"));
    String title = request.getParameter("title");
    if (title != null && !title.isEmpty()) {
%>
  <title><%=title%></title>
  <meta name="DC.Title" content="<%=title%>"/>
<%  } %>
  <meta name="description" content="n2o network, n2o repository, nitrous oxide emissions research and data from australia and world wide, stored in a collaborative repository"/>
  <meta name="keywords" content="n2o, nitrous, oxide, repository, data, samples, research, australia, network, emissions"/>
  <meta name="DC.Creator.PersonalName" content="Dewi Wahyuni"/>
  <meta name="DC.Description" content="n2o network, n2o repository, nitrous oxide emissions research and data from australia and world wide, stored in a collaborative repository"/>
  <meta name="DC.Subject" content="n2o, nitrous, oxide, repository, data, samples, research, australia, network, emissions"/>
  <meta name="DC.Date.Created" scheme="ISO8601" content="2009-12-18"/>
  <meta name="DC.Date.ValidTo" scheme="ISO8601" content="2010-12-14"/>
  <meta name="DC.Identifier" scheme="URI" content="http://www.n2o.net.au/knb"/>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
<% if (jqueryui) { %>
  <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" />
<% } %>
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
<%
   boolean showMap = (request.getParameter("showMap") != null && request.getParameter("showMap").equals("true"));
   if (showMap) { 
%>  
  <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false"></script>
  <script type="text/javascript" src="http://openlayers.org/api/2.11/OpenLayers.js"></script>
  <script type="text/javascript" src="<%=SKIN_URL%>/map.js"></script>
<% } %>
  <script type="text/javascript" src="<%=SKIN_URL%>/google_analytics.js"></script>  