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
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.core.databinding.UpdateValueStrategy;

public abstract class RootType extends XMLTagEntity implements
		IXMLHandlingLayer, IStaxEventContributor {

	public RootType(String rootTagName) {
		super(rootTagName);
	}

	public Float handle(ConfigurationNode node) throws ConfigurationException {
		Boolean result = null;
		if (XMLElementName.equals(node.getName())) {
			Object valueObject = node.getValue();
			if (valueObject != null) {
				if (valueObject instanceof String) {
					String valueString = (String)valueObject;
					if (valueString != "") {
						if("true".equalsIgnoreCase(valueString)||"1".equals(valueString)){
							result = Boolean.TRUE;
						} else {
							if("false".equalsIgnoreCase(valueString)||"0".equals(valueString)){
								result = Boolean.FALSE;
							} else {
								throw new ConfigurationException("Non supported tag value: " + valueString);
							}
						}
					} else {
						throw new ConfigurationException("Tag has empty value.");
					}
				} else {
					throw new ConfigurationException("Tag has non String value.");
				}
			} else {
				throw new ConfigurationException("Tag has null value.");
			}
		} else {
			throw new ConfigurationException("Incorrect tag for this handler.");
		}
		return  null ;//result;
	}

 public void streamEvents(String value, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException, UnexpectedFileStructureException,
			IOException {
		XMLEvent event = eventFactory.createStartElement("", "", super.getXMLElementName());
		writer.add(event);
		eventFactory.createCharacters(streamValue());
		writer.add(event);
		event = eventFactory.createEndElement("", "", super.getXMLElementName());
		writer.add(event);
 }
	
	abstract protected String streamValue();
}
