<%@ page import="edu.ucsb.nceas.metacat.util.SystemUtil" %>
<%

String url = SystemUtil.getContextURL();
String layerName = "metacat:data_points";

String redirectURL = url + "/wms?service=WMS&request=GetMap&format=application/vnd.google-earth.kml+XML&width=1024&height=1024&srs=EPSG:4326&layers=" + layerName + "&styles=&bbox=-180,-90,180,90";

response.sendRedirect(redirectURL);
%>
