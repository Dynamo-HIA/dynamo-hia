package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;

public abstract class MinAgeType extends AbstractPayLoadAge {
	static final protected String XMLElementName = "minage";

	public MinAgeType() throws ConfigurationException {
		super(XMLElementName);
	}

	public String getElementName() {
		return XMLElementName;
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equalsIgnoreCase(elementName)) {
			result = false;
		}
		return result;
	}
}
