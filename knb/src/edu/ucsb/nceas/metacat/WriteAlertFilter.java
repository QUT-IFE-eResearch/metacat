package edu.ucsb.nceas.metacat;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;

import edu.ucsb.nceas.metacat.properties.PropertyService;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

/**
 * This Filter checks for Metacat write actions, and sends alerts to registered
 * recipients about them. Used for alerting Data Managers that a record has
 * changed, as a trigger for any manual data management processes.
 * 
 * @author Vaughan Hobbs
 * 
 */
public class WriteAlertFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			// check POST action for write attempts
			String method = httpRequest.getMethod();
			if (method.equalsIgnoreCase("post")) {
				// For POST requests, check action
				String action = httpRequest.getParameter("action");
				if (isWriteAction(action)) {
					sendAlertToDataManager(httpRequest);
				}
			}
		} catch (Exception e) {
			// in event of error, continue with chain without attempting to
			// alert Data Manager
		}

		chain.doFilter(request, response);
	}

	private void sendAlertToDataManager(HttpServletRequest httpRequest) throws PropertyNotFoundException, MessagingException {
		String serverName = httpRequest.getServerName();
		String serverAlias = createServerAlias(serverName);
		
		String docId = httpRequest.getParameter("docid");

		String messageText = "A record '" + docId
				+ "' has been inserted or modified on " + serverName
				+ ". \nPlease perform any manual checks or procedures.";
		String sender = PropertyService.getProperty("email.sender");
		String recipient = PropertyService.getProperty("email.recipient");
		String recipientsCc = PropertyService.getProperty("email.recipient_cc");
		String recipientsBcc = PropertyService
				.getProperty("email.recipient_bcc");
		String host = PropertyService.getProperty("email.mailhost");
		String subject = "[" + serverAlias + "] Created or updated record " + docId
				+ " on system " + serverName;
		Properties mailProps = new Properties();
		mailProps.put("mail.host", host);
		mailProps.put("mail.transport.protocol", "smtp");

		if (recipient != null) {
			Session mailSession = Session.getInstance(mailProps, null);
			Message msg = new MimeMessage(mailSession);
			msg.setFrom(new InternetAddress(sender));
			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(recipient));
			msg.setRecipients(Message.RecipientType.CC,
					InternetAddress.parse(recipientsCc));
			msg.setRecipients(Message.RecipientType.BCC,
					InternetAddress.parse(recipientsBcc));
			msg.setSubject(subject);
			msg.setText(messageText);
	
			Transport.send(msg);
		}
	}

	private String createServerAlias(String serverName) {
		return serverName;
	}

	private boolean isWriteAction(String action) {
		if (action == null) {
			// Can't be a write attempt, return false
			return false;
		} else {
			// detect all write attempts
			return action.equalsIgnoreCase("insert")
					|| action.equalsIgnoreCase("insertmultipart")
					|| action.equalsIgnoreCase("update")
					|| action.equalsIgnoreCase("delete")
					|| action.equalsIgnoreCase("setaccess");
		}
	}

}
