<%@ page language="java" %>
<%@ include file="settings.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<% try { %>
<c:import url="${MAIN_SITE_URL}/include_footer.jsp" />
<% } catch (Exception e) { %>
  <div class="footerContainer">
    <div class="footerContent">
      FOOTER: Please ensure that <%=MAIN_SITE_URL%>/include_footer.jsp exists and runs with no errors.
    </div>
  </div>
<% } %>