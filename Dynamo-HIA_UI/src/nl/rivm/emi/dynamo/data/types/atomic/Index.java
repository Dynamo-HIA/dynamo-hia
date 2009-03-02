package nl.rivm.emi.dynamo.data.types.atomic;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.core.databinding.UpdateValueStrategy;

/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
 public class Index extends
		FlexibleUpperLimitNumberRangeTypeBase<Integer> implements
		IXMLHandlingLayer<Integer>{
	static final protected String XMLElementName = "index";

	static final protected Integer hardUpperLimit = new Integer(9);
	
	public Index(Integer lowerLimit, Integer upperLimit) throws ConfigurationException {
		super(XMLElementName, lowerLimit, upperLimit);
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
		Integer result = null;
		try {
			result = Integer.decode(inputString);
			if (!inRange(result)) {
				result = null;
			}
			return result;
		} catch (NumberFormatException e) {
			result = null;
			return result;
		}
	}

	public String toString(Integer inputValue) {
		return Integer.toString(inputValue.intValue());
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equalsIgnoreCase(elementName)) {
			result = false;
		}
		return result;
	}

	public Integer setMAX_VALUE(Integer newUpperLimit) {
		Integer oldUpperLimit = null;
		if ((hardUpperLimit.compareTo(newUpperLimit) >= 0)
				&& (MIN_VALUE.compareTo(newUpperLimit) < 0)) {
			oldUpperLimit = MAX_VALUE;
			MAX_VALUE = newUpperLimit;
		}
		return oldUpperLimit;
	}

	@Override
	Object convert4Model(String viewString) {
		Integer modelValue = Integer.decode(viewString);
		return modelValue;
	}

	@Override
	public String convert4View(Object modelValue) {
		String viewValue = ((Integer)modelValue).toString();
		return viewValue;
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

	public void streamEvents(Integer value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
}
