package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.eclipse.core.databinding.UpdateValueStrategy;

/*
 * Nonnegative Integer without fixed upper limit.
 */
public class UniqueName extends AtomicTypeBase<String> implements ContainerType{
	static final protected String XMLElementName = "uniquename";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	static final public Pattern matchPattern = Pattern
			.compile("^\\w*$");


	public UniqueName(){
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

	Object convert4Model(String viewString) {
		return viewString;
	}

	public String convert4View(Object modelValue) {
		return (String)modelValue;
	}

	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return null;
	}

	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return null;
	}

	public Object getDefaultValue() {
		return "MustBeUnique";
	}
}
