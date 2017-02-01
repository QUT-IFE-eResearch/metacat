<%@ page language="java" %>
<%@ include file="settings.jsp"%>
<%@ include file="session_vars.jsp"%>
<%@ page import="javax.xml.xpath.XPath"%>
<%@ page import="javax.xml.xpath.XPathFactory"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="org.xml.sax.InputSource"%>
<%@ page import="java.io.StringReader"%>
<% 
response.setContentType("application/json;charset=UTF-8");

String errorIncorrect = "Incorrect username or password - please try again";
String errorProblem = "ERROR logging in - problems connecting - please try later";
String messageLogin = "You ARE logged in";
String messageLogout = "You are NOT logged in";

Metacat metacat = null;
String loginStatus = messageLogout;
//String ldap_dn = null;
boolean isLoggedIn = false;
String q = request.getParameter("q");
q = (q!=null)? q.trim() : "";

if (q.equals(LOGOUT_LABEL)) { 
	// DO LOGOUT //////////////////////////////////////////////////////////////		
  if (sessionId!=null && !sessionId.isEmpty()) {
    Object o = rfSession.getAttribute("sess_metacatObj");
    if (o != null) {
      metacat = (Metacat) o;
      metacat.logout();			
    }
  }
  sessionId = "";
  sessionUsername = "";
  sessionPassword = "";
  sessionLdap_dn = "";
  sessionEmail = "";
  rfSession.setAttribute("sess_sessionIdObj", "");
  rfSession.setAttribute("name", "");
  rfSession.setAttribute("password", "");
  rfSession.setAttribute("ldap_dn", "");
  rfSession.setAttribute("ldap_email", "");
  isLoggedIn = false;
  loginStatus = messageLogout;
} else if (q.equals(LOGIN_LABEL)) {
	// DO LOGIN ////////////////////////////////////////////////////////////////
  if (sessionId!=null && !sessionId.isEmpty()) {
    isLoggedIn = true;
  } else {
    String username = request.getParameter("username");
    username = (username!=null)? username.trim() : "";
    
    String organization = request.getParameter("organization");
    organization = (organization!=null)? organization.trim() : "";
    organization = (organization!="people")? organization.trim() : "";
    
    String ldapUserName = "uid=" + username + ",o=" + organization + LDAP_DOMAIN;
    
    String email = "";
    String password = request.getParameter("password");
    password = (password!=null)? password.trim() : "";

    // get metacat object - either cached from session...
    Object o = rfSession.getAttribute("sess_metacatObj");
    if (o!=null) {
      metacat = (Metacat)o;
    } else {   // ...or create it if it doesn't already exist
      try {
        metacat = MetacatFactory.createMetacatConnection(SERVLET_URL);
      } catch (MetacatInaccessibleException mie) {
        throw new ServletException("Metacat connection to " + SERVLET_URL + " failed." + mie.getMessage());
      }
      if (metacat==null) {
        throw new ServletException("Metacat connection to " + SERVLET_URL + " failed - Metacat object is NULL!");
      }
      rfSession.setAttribute("sess_metacatObj", metacat);
    }
    
    // now do login...
    try {
      String metacatResponse = metacat.login(ldapUserName, password);
      if (metacatResponse!=null && metacatResponse.indexOf("<login>") >= 0) {
        sessionId = metacat.getSessionId();
        sessionId = (sessionId!=null) ? sessionId : "";
        rfSession.setAttribute("sess_sessionIdObj", sessionId);
        rfSession.setAttribute("name", username);
        rfSession.setAttribute("password", password);
	rfSession.setAttribute("ldap_dn", ldapUserName);
	/*Get Email*/
	XPath xPath = XPathFactory.newInstance().newXPath();
	String path = "/login/email";
	email = xPath.evaluate(path, new InputSource(new StringReader(metacatResponse)));
	email = email.trim();
	rfSession.setAttribute("ldap_email", email);
        sessionUsername = username;
        sessionPassword = password;
        loginStatus = messageLogin;
        isLoggedIn = true;
	sessionLdap_dn = ldapUserName;
	sessionEmail = email;
      } else {
        loginStatus = errorIncorrect;
        isLoggedIn = false;
      }
    } catch (MetacatAuthException mae) {
        loginStatus = errorIncorrect;
        isLoggedIn = false;
    } catch (MetacatInaccessibleException mie) {
        loginStatus = errorProblem;
        isLoggedIn = false;
    }
  }
} else {
  if (sessionId!=null && !sessionId.isEmpty()) {
    isLoggedIn = true;
    loginStatus = messageLogin;
  }
}
%>
{ "isLoggedIn": <%=isLoggedIn%>, "username": "<%=sessionUsername%>", "password": "<%=sessionPassword%>", "loginStatus": "<%=loginStatus%>", "sessionId": "<%=sessionId%>", "ldap_dn": "<%=sessionLdap_dn%>", "ldap_email": "<%=sessionEmail%>"}
