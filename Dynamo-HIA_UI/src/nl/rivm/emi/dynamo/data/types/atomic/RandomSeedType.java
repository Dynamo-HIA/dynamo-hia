package nl.rivm.emi.dynamo.data.types.atomic;

import org.apache.commons.configuration.ConfigurationException;


public abstract class RandomSeedType extends AbstractFloatType {
	static final protected String XMLElementName = "randomseed";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 * @throws ConfigurationException 
	 */

	public RandomSeedType() throws ConfigurationException {
		super(XMLElementName, 0F, Float.MAX_VALUE);
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
