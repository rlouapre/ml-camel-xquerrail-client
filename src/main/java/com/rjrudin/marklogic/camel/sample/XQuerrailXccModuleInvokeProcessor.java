package com.rjrudin.marklogic.camel.sample;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class XQuerrailXccModuleInvokeProcessor {

	
	@Handler
	public void processQuery(Exchange exchange) {
		URI uri;
		try {
			uri = new URI(exchange.getIn().getHeader("XccUri").toString());
			ContentSource contentSource = ContentSourceFactory.newContentSource(uri);

			Session session = contentSource.newSession();

			Request request = session.newModuleInvoke("/main/_framework/dispatchers/dispatcher.web.xqy");

			// create an unnamed xs:string value
			ObjectMapper om = new ObjectMapper();
			ObjectNode onode = om.createObjectNode();
			// onode.put("_id", "jsonNode_id");
			// onode.put("otherField", "otherValue");

			onode.put("type", "request:request");
			onode.put("request:method", "PUT");
			onode.put("request:route", "default_controller_action_format");
			onode.put("request:application", "app-test");
			onode.put("request:controller", "documents");
			onode.put("request:action", "insert");
			onode.put("request:format", "xml");
			onode.put("request:param::uri", exchange.getIn().getHeader("MlXccUri").toString());
//			onode.put("request:body", exchange.getIn().getBody().toString());

			XdmValue value = ValueFactory.newJSObject(onode);

			// create a new XName object referencing the above namespace
			XName xname = new XName("http://xquerrail.com/domain", "REQUEST-EXTERNAL");

			// Create a Variable (name + value) instance
			XdmVariable contentVariable = ValueFactory.newVariable(xname, value);

			// bind the Variable to the Request
			request.setVariable(contentVariable);

			// create an unnamed xs:string value
			XdmValue bodyValue = ValueFactory.newElement(exchange.getIn().getBody());

			// create a new XName object referencing the above namespace
			XName bodyXName = new XName("http://xquerrail.com/domain", "REQUEST-BODY-EXTERNAL");

			// Create a Variable (name + value) instance
			XdmVariable bodyVariable = ValueFactory.newVariable(bodyXName, bodyValue);

			// bind the Variable to the Request
			request.setVariable(bodyVariable);
			
			ResultSequence rs = session.submitRequest(request);

			exchange.getIn().setBody(rs.asString());

			session.close();
		} catch (URISyntaxException | XccConfigException | RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
