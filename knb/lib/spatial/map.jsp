<%
	String map = request.getParameter("map");
    if (map == null) 
	    map = "demisWorldMap";
	 
	String template = request.getParameter("template");
    if (template == null) 
	    template = "default";

	// Currently "mapbuilder" and "openlayers" are supported
	String client = request.getParameter("template");
    if (client != "mapbuilder" || client != "openlayers" || client == null) 
	    client = "mapbuilder";
	
	if (client == "mapbuilder") {
	    String mapbuilderURL = request.getContextPath() + "/spatial/mapbuilder/demo/metacat/index.jsp" +
	                           "?map=" + map +
	                           "&template=" + template;                        
	    response.sendRedirect(mapbuilderURL);
	} 
%>