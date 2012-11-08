<!DOCTYPE HTML>
<%@ page language="java"%>
<%@ include file="settings.jsp"%>
<%@ include file="session_vars.jsp"%>

<% 
boolean showMap = PropertyService.getProperty("spatial.runSpatialOption").equals("true");
%>  
<html>
<head>
  <title>ASN - Data</title>
  <jsp:include page="include_head.jsp">
    <jsp:param name="showMap" value="<%=showMap%>"/>
  </jsp:include>
</head>
<!--[if lt IE 9 ]>
<body class="ie">
<![endif]-->
<!--[if (gt IE 8)|!(IE)]><!-->
<body class="full">
<!--<![endif]-->
  <div class="main">
     <jsp:include page="include_header.jsp" />
     <div class="insideWrapper">
     <jsp:include page="include_menu.jsp" />
    <div class="container" >
    <div class="main-content">
    	<div style="display:block" id="noBanner">
            <div class="contentSeparator"> </div>
		 <jsp:include page="include_banner.jsp"/>
        </div>   
		<jsp:include page="include_side.jsp" />
       <div class="templatecontentareaclass left">
          
             <jsp:include page="include_searchbox.jsp">
               <jsp:param name="sessionId" value="<%=sessionId%>" />
             </jsp:include>
          
          <div>
			<% if (showMap) { %>
            <div id="mapArea">
              Click on the data points on the map below to access the records in the data catalogue. </br> </br>
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
          </div>
       </div>
	 <p class="center">
	         <a href="<%=MAIN_SITE_URL%>/knb/dataProvider?verb=Identify" target="_blank">OAI-PMH Data Provider</a>
		<img width="12" height="12" style="margin: 0;" src="/templates/asn/images/iconext.png">
         </p>
	</div>
    </div><!--templatecontentareaclass-->
	<div class="filler clear"></div>
    </div>
      <div class="footer">
    	<jsp:include page="include_footer.jsp" />
    </div>
  </div> <!--container--> 
</body>
</html>



