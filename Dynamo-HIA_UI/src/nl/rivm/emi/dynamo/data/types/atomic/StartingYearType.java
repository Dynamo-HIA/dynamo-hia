package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

public abstract class StartingYearType extends AbstractIntegerType {
	static final protected String XMLElementName = "startingyear";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 * @throws ConfigurationException 
	 */

	public StartingYearType() throws ConfigurationException {
		super(XMLElementName, 0, Integer.MAX_VALUE);
	}

	static public String getElementName() {
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
