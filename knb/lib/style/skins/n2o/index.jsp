<!DOCTYPE HTML>
<%@ page language="java"%>
<%@ include file="settings.jsp"%>
<%@ include file="session_vars.jsp"%>
<% 
boolean showMap = PropertyService.getProperty("spatial.runSpatialOption").equals("true");
%>  
<html>
<head>
  <jsp:include page="include_head.jsp">
    <jsp:param name="title" value="N2O Repository"/>
    <jsp:param name="showMap" value="<%=showMap%>"/>
  </jsp:include>
</head>
<!--[if lt IE 8 ]>
<body class="ie">
<![endif]-->
<!--[if (gt IE 7)|!(IE)]><!-->
<body class="">
<!--<![endif]-->
  <div class="centerContainer">
  
    <jsp:include page="include_header.jsp" />
    
    <div class="mainContent">
    
      <jsp:include page="include_side.jsp" />
      
      <div class="contentColumn left">
        <jsp:include page="include_searchbox.jsp">
          <jsp:param name="sessionId" value="<%=sessionId%>" />
        </jsp:include>
        
<% if (showMap) { %>
        <div id="mapArea" class="moduleArea roundedBorders bgWhiteAlpha90">
          <div class="moduleHeading">Data catalogue map</div>
          <div class="moduleContent">
            <div id="map"></div>
            <form id="mapForm" name="mapForm" method="post" action="<%=SERVLET_URL%>">
              <input type="hidden" id="mapFormSessionId" name="sessionid" value="<%=sessionId%>"/>
              <input type="hidden" name="action" value="spatial_query"/>
              <input type="hidden" name="skin" value="<%=SKIN_NAME%>"/>
              <input type="hidden" name="xmin" value=""/>
              <input type="hidden" name="ymin" value=""/>
              <input type="hidden" name="xmax" value=""/>
              <input type="hidden" name="ymax" value=""/>
            </form>
          </div>
        </div>
<% } %>
      </div> <!-- contentColumn -->
    </div> <!-- mainContent -->
    
    <jsp:include page="include_footer.jsp" />
    
  </div> <!-- centerContainer -->
</body>
</html>