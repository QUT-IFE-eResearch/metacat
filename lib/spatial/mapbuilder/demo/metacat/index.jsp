<%@page contentType="text/html"%>
<%

	/* Get the requested Web Map Context Doc
	 * If not specified, use the default
	 */
	 String wmc = request.getParameter("map");
         if (wmc == null) 
         {
		 wmc = "demisWorldMap";
	 }
         String wmcUrl = "./wmc/" + wmc + ".xml"; 

	 
	/* Get the requested Template 
	 * This will be the mapbuilder html body
	 * If not specified, use the default
	 */
	 String template = request.getParameter("template");
         if (template == null) 
         {
		 template = "default";
	 }
	 String templateFile = "./templates/" + template + ".html";
	 String configUrl = "./templates/" + template + ".xml";

%>	
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<html>
  <head>
    <title>Metacat Mapbuilder Demo</title>
    <link rel='StyleSheet' type='text/css' href='../../lib/skin/default/docsStyle.css'>
    <link rel='StyleSheet' type='text/css' href='../../lib/skin/default/mapStyle.css'>
    <link rel='StyleSheet' type='text/css' href='../../lib/skin/default/button.css'>
    <link rel='StyleSheet' type='text/css' href='../../lib/skin/default/scalebar-fat.css'>

    <script>
    // URL of Mapbuilder configuration file. Required.
    var mbConfigUrl='<%=configUrl%>';
    
    function wmcInit() {
      config.loadModel('mainMap','<%=wmcUrl%>');
    }
    </script>
    
    <script type="text/javascript" src="../../lib/Mapbuilder.js"></script>
    <style type="text/css">
       body {margin:0px; padding:0px;}
    </style>
  </head>
  <body onload="mbDoLoad(wmcInit)">
  <% pageContext.include(templateFile); %>
  </body>
</html>
