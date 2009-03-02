package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;

public abstract class AbstractIntegerType extends NumberRangeTypeBase<Integer> implements
		IXMLHandlingLayer<Integer> {

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	static final public Pattern matchPattern = Pattern.compile("^\\d*$");

	public AbstractIntegerType(String elementName, Integer minValue, Integer maxValue) throws ConfigurationException {
		super(elementName, minValue, maxValue);
	}

	public boolean inRange(Integer testValue) {
		boolean result = false;
		if (!(MIN_VALUE.compareTo(testValue) > 0)
				&& !(MAX_VALUE.compareTo(testValue) < 0)) {
			result = true;
		}
		return result;
	}

	public Integer fromString(String inputString) {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString(Integer inputValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getDefaultValue() {
		return 0;
	}

	@Override
	Integer convert4Model(String viewString) {
		Integer result = null;
		Matcher matcher = matchPattern.matcher(viewString);
		if (matcher.matches()) {
			result = Integer.parseInt(viewString);
		} else {
		}
		return result;
	}

	@Override
	public String convert4View(Object modelValue) {
		return modelValue.toString();
	}

	@Override
	public UpdateValueStrategy getModelUpdateValueStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateValueStrategy getViewUpdateValueStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer handle(ConfigurationNode node) throws ConfigurationException {
		Integer result = null;
		if (getXMLElementName().equals(node.getName())) {
			Object valueObject = node.getValue();
			if (valueObject != null) {
				if (valueObject instanceof String) {
					String valueString = (String) valueObject;
					Integer convertedString = convert4Model(valueString);
					if (convertedString != null) {
						result = convertedString;
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

	public void setDefault() {
		// TODO Auto-generated method stub

	}

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

	/**
	 * Get the WritableValue for databinding purposes.
	 * 
	 * TODO Refactor it higher up the inheritence tree.
	 *
	 * @return
	 */
	abstract public WritableValue getObservableValue();

	abstract protected String streamValue();
}
