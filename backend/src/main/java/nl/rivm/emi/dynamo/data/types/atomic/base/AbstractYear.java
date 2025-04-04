package nl.rivm.emi.dynamo.data.types.atomic.base;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.WritableValue;



/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
abstract public class AbstractYear extends AbstractFlexibleUpperLimitInteger {

	
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
	
	/**
	 * Constructor used for overriding.
	 * 
	 * @param myElementName
	 * @param lowerLimit
	 * @param upperLimit
	 * @throws ConfigurationException
	 */
	public AbstractYear(String XMLElementName,
			Integer lowerLimit, Integer upperLimit) {
		super(XMLElementName, lowerLimit, upperLimit);
		this.modelUpdateValueStrategy = assembleModelStrategy();
		this.viewUpdateValueStrategy = assembleViewStrategy();
		MIN_VALUE = this.getDefaultValue();
	}
	
	
	public boolean inRange(Integer testValue) {
		boolean result = false;
		if (!(MIN_VALUE.compareTo(testValue) > 0)
				&& !(MAX_VALUE.compareTo(testValue) < 0)) {
			result = true;
		}
		return result;
	}

	public void setMAX_VALUE(Integer newMaxValue) {
		MAX_VALUE = newMaxValue;
	}

	public Integer fromString(String inputString) {
		Integer result = null;
		try {
			result = Integer.valueOf(inputString);
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

	/**
	 * The default year will be the current year
	 */
	public Integer getDefaultValue() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return new Integer(calendar.get(Calendar.YEAR));
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

	public String convert4View(Object modelValue) {
		String result = (String) viewUpdateValueStrategy.convert(modelValue);
		return result.toString();
	}

	public Object convert4Model(String viewString) {
		Object result = modelUpdateValueStrategy.convert(viewString);
		return result;
	}

	@Override
	public String convert4File(Object modelValue) {
		Integer nakedValue = null;
		if (modelValue instanceof WritableValue) {
			nakedValue = (Integer) ((WritableValue) modelValue).doGetValue();
		} else {
			nakedValue = (Integer) modelValue;
		}
		String viewValue = convert4View(nakedValue);
		return viewValue;
	}

	private UpdateValueStrategy assembleModelStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy.setConverter(new ValueModelConverter(
				"ValueModelConverter"));
		return resultStrategy;
	}

	private UpdateValueStrategy assembleViewStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy
				.setConverter(new ValueViewConverter("ValueViewConverter"));
		return resultStrategy;
	}

	public class ValueModelConverter implements IConverter {
		Log log = LogFactory.getLog(this.getClass());
		String debugString = "";

		public ValueModelConverter(String debugString) {
			this.debugString = debugString;
		}

		public Object convert(Object arg0) {
			log.debug(debugString + " convert(Object) entered with:"
					+ arg0.toString());
			try {
				Integer result = null;
				try {
					result = Integer.valueOf(arg0.toString());
					if (!inRange(result)) {
						result = null;
					}
					return result;
				} catch (NumberFormatException e) {
					result = null;
					return result;
				}
			} catch (Exception e) {
				return 4712;
			}
		}

		public Object getFromType() {
			// log.debug(debugString + " getFromType() entered.");
			return (Object) String.class;
		}

		public Object getToType() {
			// log.debug(debugString + " getToType() entered.");
			return (Object) Float.class;
		}
	}

	public class ValueViewConverter implements IConverter {
		// Log log = LogFactory.getLog(this.getClass());
		String debugString = "";

		public ValueViewConverter(String debugString) {
			this.debugString = debugString;
		}

		public Object convert(Object arg0) {
			// log.debug(debugString + " convert(Object) entered with:" +
			// arg0.toString());
			String integerString = "NoInteger";
			try {
				if (arg0 == null) {
					integerString = "";
				} else {
					if (arg0 instanceof Integer) {
						integerString = ((Integer) arg0).toString();
					}
				}
				return integerString;
			} catch (Exception e) {
				integerString = e.getClass().getName();
				return integerString;
			}
		}

		public Object getFromType() {
			// log.debug(debugString + " getFromType() entered.");
			return (Object) Integer.class;
		}

		public Object getToType() {
			// log.debug(debugString + " getToType() entered.");
			return (Object) String.class;
		}
	}

	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return modelUpdateValueStrategy;
	}

	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return viewUpdateValueStrategy;
	}
	
	
	
}
