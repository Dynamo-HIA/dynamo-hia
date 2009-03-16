package nl.rivm.emi.dynamo.data.types.atomic;


import java.util.Calendar;
import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.eclipse.core.databinding.UpdateValueStrategy;

public class Year extends NumberRangeTypeBase<Integer> implements ContainerType{
	static final protected String XMLElementName = "year";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 * 
	 * Here the regular expression for years has 
	 * a range between 0000 and 9999 
	 * (with an obligation to use four digits)
	 * 
	 */
	static final public Pattern matchPattern = Pattern
			.compile("\\d\\d\\d\\d");
	

	public Year(){				
		super(XMLElementName, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR) + 30);
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
	public Object convert4Model(String viewString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String convert4View(Object modelValue) {
		// TODO Auto-generated method stub
		return null;
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
}
