package edu.ucsb.nceas.metacat;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

public class GetPrincipalsJsonServlet extends HttpServlet {

	private static final long serialVersionUID = -1171061752533095108L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleRequest(req, resp);
	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		AuthSession authSession;
		try {
		    ServletContext servletContext = req.getSession().getServletContext();
		    String xslPath = servletContext.getRealPath("style/common/principals2json.xsl");

		    // Get principals as XML
			authSession = new AuthSession();
			String result = authSession.getPrincipals(null, null);
			
			// Set up XSLT transform
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			StreamSource xsltSource = new StreamSource(new File(xslPath));
			StreamSource xml = new StreamSource(new StringReader(result));
			StringWriter resultWriter = new StringWriter();
			Result resultJson = new javax.xml.transform.stream.StreamResult(resultWriter);
			
			// Transform XML result of getPrincipals
			Transformer transformer = transformerFactory.newTransformer(xsltSource);
			transformer.transform(xml, resultJson);

			// Write to response
			resp.getWriter().println(resultWriter.toString());
			resp.setContentType("application/json");
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
