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
<link href="../style/skins/default/default.css" rel="stylesheet" type="text/css">
<style>
   ul     { font-size: 12pt; list-style-type: none; padding:1px; padding-left:16px; }
   a      { color: darkblue }
   .title { font-size: 16pt; width:100%; color:darkblue; 
            text-align:center; padding: 1px; }
   .content { border:1px black solid; width:750px; background-color:white}
</style>
</head>
<body align="center">

<!-- WFS URL :
     <%=xml%> 
 -->

  <table width="750px" align="center" cellspacing="0" cellpadding="0" >
    <tr> 
      <td width="10" align="right" valign="top">
        <img src="../style/skins/default/images/panelhead_bg_lcorner.gif" 
          width="10" height="21">
      </td>
      <td class="sectionheader">
        Map Query Results
      </td>
      <td width="10" align="left" valign="top"> 
        <img src="../style/skins/default/images/panelhead_bg_rcorner.gif" 
          width="10" height="21">
      </td>
    </tr>
    <tr><td colspan="3">
<div class="content">
  <xtags:style xml="<%=xml%>" xsl="<%=xsl%>"/>
</div>
     </tr></td>
    </table>
</body>
</html>
