package au.edu.qut.isr.metacat;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This Filter checks for Morpho requests, and blocks them. Used for temporarily
 * blocking a Metacat from write access (by any client).
 * 
 * @author vaughan hobbs
 * 
 */
public class MorphoBlockingFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;

			// Block all attempts to connect via Java HTTP client
			String userAgent = httpRequest.getHeader("User-Agent");
			if (userAgent.contains("Java")) {
				// Send 500 error, with message
				((HttpServletResponse) response)
						.sendError(
								500,
								"Metacat is temporarily not accepting non-browser client requests. Please contact Data Manager for more information.");
			}

			// if a different Morpho user agent has been specified, then check
			// POST action for write attempts
			String method = httpRequest.getMethod();
			if (method.equalsIgnoreCase("post")) {
				// For POST requests, check action
				String action = httpRequest.getParameter("action");
				if (isWriteAction(action)) {
					((HttpServletResponse) response)
							.sendError(
									500,
									"Metacat is temporarily in read-only mode. Please contact Data Manager for more information.");
				}
			}
		} catch (Exception e) {
			// continue with chain

		}

		chain.doFilter(request, response);
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

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
