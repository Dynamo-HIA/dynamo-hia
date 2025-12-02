package nl.rivm.emi.dynamo.data.types.atomic.base;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.WritableValue;

abstract public class AbstractFlexibleUpperLimitInteger extends
		NumberRangeTypeBase<Integer> {

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern.compile("^\\d*$");

	public AbstractFlexibleUpperLimitInteger(String XMLElementName,
			Integer lowerLimit, Integer upperLimit) {
		super(XMLElementName, lowerLimit, upperLimit);
		this.modelUpdateValueStrategy = assembleModelStrategy();
		this.viewUpdateValueStrategy = assembleViewStrategy();
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

	public Integer getDefaultValue() {
		return 0;
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
		@SuppressWarnings("unchecked")
		String result = (String) viewUpdateValueStrategy.convert(modelValue);
		return result.toString();
	}

	public Object convert4Model(String viewString) {
		@SuppressWarnings("unchecked")
		Object result = modelUpdateValueStrategy.convert(viewString);
		return result;
	}

	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private UpdateValueStrategy assembleModelStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy.setConverter(new ValueModelConverter(
				"ValueModelConverter"));
		return resultStrategy;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private UpdateValueStrategy assembleViewStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy
				.setConverter(new ValueViewConverter("ValueViewConverter"));
		return resultStrategy;
	}

	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return modelUpdateValueStrategy;
	}

	@SuppressWarnings("rawtypes")
	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return viewUpdateValueStrategy;
	}
}
