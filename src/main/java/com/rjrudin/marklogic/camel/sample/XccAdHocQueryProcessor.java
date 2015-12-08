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
import com.marklogic.xcc.ValueFactory;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;
import com.marklogic.xcc.types.XName;
import com.marklogic.xcc.types.XdmValue;
import com.marklogic.xcc.types.XdmVariable;

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
public class XccAdHocQueryProcessor {

	@Handler
	public void processQuery(Exchange exchange) {
		URI uri;
		try {
			uri = new URI(exchange.getIn().getHeader("XccUri").toString());
			ContentSource contentSource = ContentSourceFactory.newContentSource(uri);

			Session session = contentSource.newSession();

			Request request = session.newAdhocQuery("xquery version \"1.0-ml\";\n"
					+ "import module namespace domain = \"http://xquerrail.com/domain\" at \"/ext/domain.xqy\";\n"
					+ "declare variable $domain:uri as xs:string external;\n"
					+ "declare variable $domain:content as item() external;\n"
					+ "(fn:data ($domain:uri), domain:insert($domain:uri, $domain:content))");

			// create an unnamed xs:string value
			XdmValue value = ValueFactory.newElement(exchange.getIn().getBody());

			// create a new XName object referencing the above namespace
			XName xname = new XName("http://xquerrail.com/domain", "content");

			// Create a Variable (name + value) instance
			XdmVariable contentVariable = ValueFactory.newVariable(xname, value);

			// bind the Variable to the Request
			request.setVariable(contentVariable);

			request.setNewStringVariable("http://xquerrail.com/domain", "uri",
					exchange.getIn().getHeader("MlXccUri").toString());

			ResultSequence rs = session.submitRequest(request);

			exchange.getIn().setBody(rs.asString());

			session.close();
		} catch (URISyntaxException | XccConfigException | RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
