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

public abstract class NumberOfYearsType extends AbstractIntegerType /* implements
		IXMLHandlingLayer*/ {
	static final protected String XMLElementName = "numberofyears";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 * @throws ConfigurationException 
	 */

	public NumberOfYearsType() throws ConfigurationException {
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
