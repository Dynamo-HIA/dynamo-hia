package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.eclipse.core.databinding.UpdateValueStrategy;

/*
 * Nonnegative Integer without fixed upper limit.
 */
public class Name extends AbstractString implements PayloadType{
	static final protected String XMLElementName = "name";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern
			.compile("^\\w*$");


	public Name(){
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
