package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.markers.LeafType;

import org.eclipse.core.databinding.UpdateValueStrategy;

/*
 * Nonnegative Integer without fixed upper limit.
 */
public class Name extends AtomicTypeBase<String> implements LeafType{
	static final protected String XMLElementName = "name";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	static final public Pattern matchPattern = Pattern
			.compile("^\\w*$");


	public Name(){
		super(XMLElementName, "");
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

	@Override
	Object convert4Model(String viewString) {
		return viewString;
	}

	@Override
	public String convert4View(Object modelValue) {
		return (String)modelValue;
	}

	@Override
	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return null;
	}

	@Override
	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return null;
	}

	public Object getDefaultValue() {
		return "";
	}
}
