<%@ page language="java" %>
<%@ include file="settings.jsp" %>
<%
  String[][] sidemenu = { {"Home", "", "home"},
                          {"About the Network", "about/about.jsp", "about"},
                          {"For Growers", "grower/grower.jsp", "grower"},
                          {"For Policy Makers", "policy/policymaker.jsp", "policymaker"},
                          {"For Researchers", "researcher/researcher.jsp", "researcher"},
                          {"Network Research", "researcher/nresearch.jsp", "nresearch"},
                          {"Data Portal", "knb", "knb"},
                          {"Uploading Data", "manual/manual.jsp", "manual"},
                          {"Join the Network", "researcher/join.jsp", "join"},
                          {"Contacts", "contact/contact.jsp", "contact"}
                        };
%>  

        <div class="navigationColumn left">
          <div class="sideMenuList roundedBorders bgWhiteAlpha60">
          
                <ul>
<%  
  for (int i = 0; i < sidemenu.length; ++i) {
    String label = sidemenu[i][0];
    String url = MAIN_SITE_URL + "/" + sidemenu[i][1];
    String pageid = sidemenu[i][2];
    String selected = "";
    if ( pageid.equals("knb") ) selected = " class=\"selected\"";
%>              
                  <li<%=selected%>>
                    <span> <a href="<%=url%>" id="sideMenuLink<%=i%>"><%=label%></a> </span>
                  </li>
<%}%>
                </ul>
          </div>
          
          <div id="loginArea" class="moduleArea roundedBorders bgWhiteAlpha60">
            <div class="moduleHeading">Login &amp; Registration</div>
            <div class="moduleContent">
              <p>Logging into your account enables you to search any additional, non-public data for which you may have access privileges:</p>
              <em id="loginStatus" class="loginStatus"> &nbsp; </em>
              <form id="loginform" name="loginform" method="post" action="auth.jsp" onSubmit="return allowLoginSubmit(this);">
                <label for="username">Username:</label><br/>
                <input id="username" name="username" type="text" value="" /><br/>
                <label for="password">Password:</label><br/>
                <input id="password" name="password" type="password" maxlength="50" value="" />
                <input type="hidden" name="organization" value="<%=LDAP_ORGANIZATION%>" />
                <input type="submit" name="q" value="<%=LOGIN_LABEL%>" class="button_login" /><br/>
              </form>                
              <p>By logging in, you agree to the  <a href="<%=SKIN_URL%>/terms.jsp">Terms and Conditions</a></p>
              <p><a href="<%=MAIN_SITE_URL%>/researcher/join.jsp">Join the Network</a></p>
              <a href="<%=MAIN_SITE_URL%>/knb/dataProvider?verb=Identify">OAI-PMH Data Provider</a>
            </div>
          </div>
        </div> <!-- navigationColumn -->