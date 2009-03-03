package nl.rivm.emi.dynamo.data.types.atomic;

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

import nl.rivm.emi.dynamo.data.interfaces.IReferenceValue;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class ReferenceValue extends XMLTagEntity implements IXMLHandlingLayer {

	public ReferenceValue() {
		super("referencevalue");
	}

	public ConfigurationObjectBase handle(ConfigurationObjectBase modelObject,
			ConfigurationNode node)
			throws ConfigurationException {
		if (!(modelObject instanceof IReferenceValue)) {
			throw new ConfigurationException("Incorrect type of modelObject: "
					+ modelObject.getClass().getName());
		}
		Float value = null;
		if (XMLElementName.equals(node.getName())) {
			value = Float.parseFloat((String) node.getValue());
		} else {
			throw new ConfigurationException("Incorrect tag \""
					+ node.getName() + "\" found, \"" + XMLElementName
					+ "\" expected.");
		}

		if (value != null) {
			((IReferenceValue) modelObject).putReferenceValue(value);
		} else {
			throw new ConfigurationException("Incorrect \"" + XMLElementName + "\" tag.");
		}
		return modelObject;
	}

	public Object handle(ConfigurationNode node) throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConfigurationOK() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setDefault() {
		// TODO Auto-generated method stub
		
	}

	public void streamEvents(Object value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
}
