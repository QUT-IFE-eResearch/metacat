<%@ page language="java" %>
<%@ include file="settings.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<% try { %>
<c:import url="${MAIN_SITE_URL}/include_header.jsp" />
<% } catch (Exception e) { %>
  <div class="headerContainer">
    <div class="headerContent">
      HEADER: Please ensure that <%=MAIN_SITE_URL%>/include_header.jsp exists and runs with no errors.
    </div>
  </div>
<% } %>