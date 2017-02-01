<%@ page language="java" import="java.util.Vector,edu.ucsb.nceas.metacat.properties.PropertyService,edu.ucsb.nceas.metacat.util.OrganizationUtil"%>
<%@ include file="settings.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%//Vector<String> organizationList = OrganizationUtil.getOrganizations();%>

        <div class="navigationColumn left">
<% try { %>
          <c:import url="${MAIN_SITE_URL}/include_sidemenu.jsp?page=knb" />
<% } catch (Exception e) { %>
          <div class="sideMenuList multiBorders">
            SIDE MENU: Please ensure that <%=MAIN_SITE_URL%>/ include_sidemenu.jsp exists and runs with no errors.
          </div>
<% } %>
          <div style="height:10px;"></div>

          <div class="moduleArea multiBorders">
            <div class="moduleHeading moduleHeadingGreen">Login &amp; Registration</div>
            <div class="moduleContent">
              <p>Logging into your account enables you to search any additional, non-public data for which you may have access privileges:</p>
              <em id="loginStatus" class="loginStatus"> &nbsp; </em>
              <form id="loginform" name="loginform" method="post" action="auth.jsp" onSubmit="return allowLoginSubmit(this);">
                <label for="username">Username:</label><br/>
                <input id="username" name="username" type="text" style="width: 140px;" value="" /><br/>
                <label for="password">Password:</label><br/>
                <input id="password" name="password" type="password" maxlength="50" style="width:140px;" value="" /><br/>
                <input type="hidden" name="organization" value="<%=ORGANIZATION%>" />
                <input type="submit" name="q" value="<%=LOGIN_LABEL%>" class="button_login" /><br/>
                <!--
                <p>By logging in, you agree to the  <a href="<%=SKIN_URL%>/index_terms.jsp">Terms and Conditions</a></p>
                <p><a href="<%=MAIN_SITE_URL%>/join.jsp">Join the network</a></p>
                -->
              </form>                
              <!--
              <a href="<%=MAIN_SITE_URL%>/knb/dataProvider?verb=Identify">OAI-PMH Data Provider</a>
              -->
            </div>
          </div>
        </div> <!-- navigationColumn -->