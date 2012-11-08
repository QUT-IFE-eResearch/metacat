<%@ taglib uri="/WEB-INF/taglibs-xtags.tld" prefix="xtags" %>
<%
    /* Get the WFS GetFeature request and the XSL Stylesheet
     * If not specified, use the defaults
     */
     String xml = request.getParameter("wfsurl");
     if (xml == null)
     {
         xml = "wfs.xml";
     }

     String xsl = request.getParameter("xsl");
     if (xsl == null) 
     {
         xsl = "wfs.xsl";
     }

%>

<html>
<head>
<title> Map Query </title>
<style>
   ul     { font-size: 12pt; list-style-type: none; padding:1px; padding-left:16px; }
   a      { color: darkblue }
   .title { font-size: 16pt; width:100%; color:darkblue; border: 1px black solid;
            text-align:center; padding: 1px; }
</style>
</head>
<body>
<div class="title"> Map Query </div>
<xtags:style xml="<%=xml%>" xsl="<%=xsl%>"/>
</body>
</html>
