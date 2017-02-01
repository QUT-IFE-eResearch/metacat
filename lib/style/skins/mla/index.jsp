<!DOCTYPE HTML>
<%@ page language="java"%>
<%@ include file="settings.jsp"%>
<%@ include file="session_vars.jsp"%>
<% 
boolean showMap = PropertyService.getProperty("spatial.runSpatialOption").equals("true");
%>  
<html>
<head>
  <title>RELRP Data Catalogue</title>
  <jsp:include page="include_head.jsp">
    <jsp:param name="showMap" value="<%=showMap%>"/>
  </jsp:include>
</head>
<!--[if lt IE 9 ]>
<body class="ie">
<![endif]-->
<!--[if (gt IE 8)|!(IE)]><!-->
<body class="">
<!--<![endif]-->
  <jsp:include page="include_header.jsp" />
  <div class="mainContainer">
    <div class="mainContent">
      <div style="overflow:auto;">
        <jsp:include page="include_side.jsp" />
        <div class="contentColumn left">
          <div class="moduleArea multiBorders">
            <div class="moduleHeading moduleHeadingGreen">RELRP Data Catalogue</div>
            <jsp:include page="include_searchbox.jsp">
              <jsp:param name="sessionId" value="<%=sessionId%>" />
            </jsp:include>
<% if (showMap) { %>
            <div id="mapArea">
              <div class="sectionHeader">
                Data catalogue map
              </div>
              <p>Click on the data points on the map below to access the records in the data catalogue. </p>
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
<% } %>
          </div> <!-- moduleArea -->
        </div> <!-- contentColumn -->
      </div>

    </div> <!-- mainContent -->
  </div> <!-- mainContainer -->
  
  <jsp:include page="include_footer.jsp" />
</body>
</html>