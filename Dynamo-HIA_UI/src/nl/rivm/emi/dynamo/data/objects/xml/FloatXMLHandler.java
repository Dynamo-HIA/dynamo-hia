package nl.rivm.emi.dynamo.data.objects.xml;

/**
 * Handler for 
 * <classes>
 * 	<class>
 * 		<index>1</index>
 * 		<name>jan</name>
 * 	</class>
 * 	.......
 * </classes>
 * XML fragments.
 */
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.types.interfaces.IPureXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class FloatXMLHandler extends
		BaseXMLHandler<Float> {

	public FloatXMLHandler(String xmlElementName) {
		super(xmlElementName);
	}

	protected Float convert(String valueString) {
		Float result;
		result = Float.valueOf(valueString);
		return result;
	}

	protected String streamValue(Float value) {
				return value.toString();
	}
}
