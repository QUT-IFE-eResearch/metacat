<%@ page language="java"%>
<%@ page import="java.util.Properties" %>
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<%@ include file="settings.jsp" %>
<%
  String sender = PropertyService.getProperty("email.sender");
  String recipients = PropertyService.getProperty("email.recipient");
  String recipientsCc = "";
  //String recipientsCc = PropertyService.getProperty("email.recipient_cc");
  String recipientsBcc = PropertyService.getProperty("email.recipient_bcc");
  String subject = null;
  String messageText = null;
  String host = PropertyService.getProperty("email.mailhost");
  boolean isMsgSent = false;
	String q = request.getParameter("q");
	q = (q!=null)? q.trim() : "";
  
  if (q.equals("request")) {
		boolean validLdapEmail = true;
		recipientsCc = request.getParameter("ldap_email");
		if(recipientsCc.equals("badEmail")){
			validLdapEmail = false;
		}
	 	if(!validLdapEmail){	
			recipientsCc = PropertyService.getProperty("email.recipient_cc");
			System.out.println(recipientsCc);
		}
  		subject = "Request for File in " + SKIN_NAME.toUpperCase();
  		
		String ldap_dn = request.getParameter("ldap_dn");
		ldap_dn = (ldap_dn!=null)? ldap_dn.trim() : "";
    		String packageName = request.getParameter("packageName");
		packageName = (packageName!=null)? packageName.trim() : "";
		String docId = request.getParameter("docId");
		docId = (docId!=null)? docId.trim() : "";
		String reasons = request.getParameter("reasons");
		reasons = (reasons!=null)? reasons.trim() : "";
		String objectName = request.getParameter("objectName");
		objectName = (objectName!=null)? objectName.trim() : "";
		String entityName = request.getParameter("entityName");
		entityName = (entityName!=null)? entityName.trim() : "";
  
		StringBuilder sb = new StringBuilder();
		sb.append("Request for File \r\n");
		sb.append("-------------------------------------\r\n\r\n");
		sb.append("A request for access a file has been made. "); sb.append("\r\n\r\n");
		sb.append("Ldap DN: "); sb.append(ldap_dn); sb.append("\r\n\r\n");
		sb.append("Package Name: "); sb.append(packageName); sb.append("\r\n\r\n");
		sb.append("Document Id: "); sb.append(docId); sb.append("\r\n\r\n");
		sb.append("Table Name: "); sb.append(entityName); sb.append("\r\n\r\n");
		sb.append("File Name: "); sb.append(objectName); sb.append("\r\n\r\n");
		sb.append("Reasons: "); sb.append(reasons); sb.append("\r\n\r\n");
		sb.append("An email is sent to the data librarian. "); sb.append("\r\n\r\n");
		if(!validLdapEmail){
			sb.append("Data librarian: Please verify the user email on ldap structure, it appears to be malformed."); sb.append("\r\n\r\n");
		}
		sb.append("-------------------------------------\r\n\r\n");
		messageText = sb.toString();
   
    }
	else if (q.equals("register")) {
                recipientsCc = PropertyService.getProperty("email.recipient_cc");
    		subject = "Request for Access to " + SKIN_NAME;
    	
    		String packageName = request.getParameter("packageName");
		packageName = (packageName!=null)? packageName.trim() : "";
		String docId = request.getParameter("docId");
		docId = (docId!=null)? docId.trim() : "";
		String name = request.getParameter("name");
		name = (name!=null)? name.trim() : "";
		String email = request.getParameter("email");
		email = (email!=null)? email.trim() : "";
		String phone = request.getParameter("phone");
		phone = (phone!=null)? phone.trim() : "";
		String reasons = request.getParameter("reasons");
		reasons = (reasons!=null)? reasons.trim() : "";
		String objectName = request.getParameter("objectName");
		objectName = (objectName!=null)? objectName.trim() : "";
		String entityName = request.getParameter("entityName");
		entityName = (entityName!=null)? entityName.trim() : "";
  
		StringBuilder sb = new StringBuilder();
		sb.append("Request Access\r\n");
		sb.append("-------------------------------------\r\n\r\n");
		sb.append("A request for access has been made. "); sb.append("\r\n\r\n");
		sb.append("Name: "); sb.append(name); sb.append("\r\n\r\n");
		sb.append("Email: "); sb.append(email); sb.append("\r\n\r\n");
		sb.append("Phone: "); sb.append(phone); sb.append("\r\n\r\n");
		sb.append("Package Name: "); sb.append(packageName); sb.append("\r\n\r\n");
		sb.append("Document Id: "); sb.append(docId); sb.append("\r\n\r\n");
		sb.append("Table Name: "); sb.append(entityName); sb.append("\r\n\r\n");
		sb.append("File Name: "); sb.append(objectName); sb.append("\r\n\r\n");
		sb.append("Reasons: "); sb.append(reasons); sb.append("\r\n\r\n");
		sb.append("-------------------------------------\r\n\r\n");
		messageText = sb.toString();
    }

	// smtp settings
    Properties props = new Properties();
    props.put("mail.host", host);
    props.put("mail.transport.protocol", "smtp");
    //props.put("mail.smtp.host", "smtp.gmail.com");
    //props.put("mail.smtp.socketFactory.port", "465");
    //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    //props.put("mail.smtp.auth", "true");
    //props.put("mail.smtp.port", "465");
    
	try {
      	Session mailSession = Session.getInstance(props, null);
	  	//Session mailSession = Session.getInstance(props,
        //  new javax.mail.Authenticator() {
        //    protected PasswordAuthentication getPasswordAuthentication() {
        //      return new PasswordAuthentication("admin@tern-supersites.net.au","2cheeky4words");
        //    }
      	//});
      
		Message msg = new MimeMessage(mailSession);
		msg.setFrom(new InternetAddress(sender));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
		if (!recipientsCc.isEmpty()) {
			InternetAddress[] ia = null;
			try { 
				ia = InternetAddress.parse(recipientsCc); 
			} 
			catch (AddressException ae) { 
				ia = null; 
			}
        if (ia != null && ia.length > 0) {
          msg.setRecipients(Message.RecipientType.CC, ia);
        }
      }
      if (!recipientsBcc.isEmpty()) {
        InternetAddress[] ia = null;
        try { ia = InternetAddress.parse(recipientsBcc); } catch (AddressException ae) { ia = null; }
        if (ia != null && ia.length > 0) {
          msg.setRecipients(Message.RecipientType.BCC, ia);
        }
      }
      
      msg.setSubject(subject);
      msg.setText(messageText);
      Transport.send(msg);
      isMsgSent = true;
    } catch (Exception e) {
		%>
		{ "isMsgSent": false}
		<%
    }
    if (isMsgSent) { 
    	%>
		{ "isMsgSent": <%=isMsgSent%>} 
		<%
    }
    else{
    	%>
		{ "isMsgSent": <%=isMsgSent%>}
		<%
    }
    

%>  
