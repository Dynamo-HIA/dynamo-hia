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
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;

public abstract class ResultTypeType extends StringChoiceTypeBase implements
		IXMLHandlingLayer<String> {

	public ResultTypeType(String[] possibleChoices) {
		super("resulttype", possibleChoices);
	}

	/**
	 * Get the WritableValue for databinding purposes.
	 * 
	 * TODO Refactor it higher up the inheritence tree.
	 * 
	 * @return
	 */
	abstract public WritableValue getObservableValue();

	@Override
	Object convert4Model(String viewString) {
		return viewString;
	}

	@Override
	public String convert4View(Object modelValue) {
		return (String) modelValue;
	}

	@Override
	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return null;
	}

	@Override
	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return null;
	}

	public String handle(ConfigurationNode node) throws ConfigurationException {
		String result = null;
		if (getXMLElementName().equals(node.getName())) {
			Object valueObject = node.getValue();
			if (valueObject != null) {
				if (valueObject instanceof String) {
					String valueString = (String) valueObject;
					if (valueString != "") {
						result = isPossible(valueString);
						if (result == null) {
							throw new ConfigurationException(
									"Non supported tag value: " + valueString);
						}
					} else {
						throw new ConfigurationException("Tag has empty value.");
					}
				} else {
					throw new ConfigurationException(
							"Tag has non String value.");
				}
			} else {
				throw new ConfigurationException("Tag has null value.");
			}
		} else {
			throw new ConfigurationException("Incorrect tag for this handler.");
		}
		return result;
	}

	private String isPossible( String valueString) {
		String result = null;
		for (String possibleChoice : possibleChoices) {
			if (possibleChoice.equals(valueString)) {
				result = valueString;
				break;
			}
		}
		return result;
	}

	abstract public void setDefault();

	public void streamEvents(String value, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "", super
				.getXMLElementName());
		writer.add(event);
		event = eventFactory.createCharacters(streamValue());
		writer.add(event);
		event = eventFactory
				.createEndElement("", "", super.getXMLElementName());
		writer.add(event);
	}

	abstract protected String streamValue();

	@Override
	public boolean inRange(String testValue) {
		return (isPossible(testValue) != null);
	}

	@Override
	public String toString(String inputValue) {
		return inputValue;
	}

	@Override
	public String fromString(String inputString) {
		return inputString;
	}
}
