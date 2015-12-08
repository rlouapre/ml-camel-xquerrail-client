package com.rjrudin.marklogic.camel.sample;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;

/**
 * Simple Java class for showing how we can implement custom delimited-text
 * processing and then feed the results into a Velocity template for XML
 * generation.
 * 
 * We expect the delimited text to have two values - a name, consisting of a
 * space-delimited first and last name; and a birth date. Ingesting this via
 * mlcp would result in two elements, but we'll instead show how we can use
 * Velocity to create 3 elements instead - one for first name, one for last
 * name, and one for date of birth. This is a simple example, but it shows how
 * you can create the precise XML you want before ingesting it either via mlcp
 * or XCC.
 */
public class XquerrailInitializeProcessor {

	@Handler
	public void processQuery(Exchange exchange) {
		URI uri;
		try {
			uri = new URI(exchange.getIn().getHeader("XccUri").toString());
			if (uri == null || uri.toString() == "") {
				throw new RuntimeException("XccUri header is required");
			}
			ContentSource contentSource = ContentSourceFactory.newContentSource(uri);

			Session session = contentSource.newSession();

			// xquery version "1.0-ml";
			// import module namespace app = "http://xquerrail.com/application"
			// at "/main/_framework/application.xqy";
			// import module namespace config = "http://xquerrail.com/config" at
			// "/main/_framework/config.xqy";
			// declare variable $APP := ();
			// (app:reset(), app:bootstrap($APP))[0]

			Request request = session.newAdhocQuery("xquery version \"1.0-ml\";\n"
					+ "import module namespace app = \"http://xquerrail.com/application\" at \"/main/_framework/application.xqy\";\n"
					+ "import module namespace config = \"http://xquerrail.com/config\" at \"/main/_framework/config.xqy\";\n"
					+ "declare variable $APP := ();\n" + "(app:reset(), app:bootstrap($APP))[0]\n");

//			XdmValue value = ValueFactory.newJSObject(onode);
//
//			// create a new XName object referencing the above namespace
//			XName xname = new XName("http://xquerrail.com/domain", "REQUEST-EXTERNAL");
//
//			// Create a Variable (name + value) instance
//			XdmVariable contentVariable = ValueFactory.newVariable(xname, value);
//
//			// bind the Variable to the Request
//			request.setVariable(contentVariable);

			// request.setNewStringVariable("http://xquerrail.com/domain",
			// "uri",
			// exchange.getIn().getHeader("MlXccUri").toString());

			ResultSequence rs = session.submitRequest(request);

			exchange.getIn().setBody(rs.asString());

			session.close();
		} catch (URISyntaxException | XccConfigException | RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
