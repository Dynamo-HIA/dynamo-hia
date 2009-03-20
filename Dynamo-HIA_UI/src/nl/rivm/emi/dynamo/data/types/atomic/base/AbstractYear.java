package nl.rivm.emi.dynamo.data.types.atomic.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
abstract public class AbstractYear extends AtomicTypeBase<Integer>{

	/**
	 * Constructor used for overriding.
	 * 
	 * @param myElementName
	 * @param lowerLimit
	 * @param upperLimit
	 * @throws ConfigurationException
	 */
	public AbstractYear(String myElementName){
		super(myElementName, new Integer(0));
		modelUpdateValueStrategy = assembleModelStrategy();
		viewUpdateValueStrategy = assembleViewStrategy();
	}

	public Integer getDefaultValue() {
		return new Integer(1999);
	}

	public Integer fromString(String inputString) {
		Integer result = null;
		try {
			result = Integer.decode(inputString);
			return result;
		} catch (NumberFormatException e) {
			result = null;
			return result;
		}
	}

	public String toString(Integer inputValue) {
		return Integer.toString(inputValue.intValue());
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equalsIgnoreCase(elementName)) {
			result = false;
		}
		return result;
	}

	public String convert4View(Object modelValue) {
		String result = (String)viewUpdateValueStrategy.convert(modelValue);
		return result.toString();
	}
	
	public Object convert4Model(String viewString) {
		Object result = modelUpdateValueStrategy.convert(viewString);
		return result;
	}

	private UpdateValueStrategy assembleModelStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy.setConverter(new ValueModelConverter(
				"ValueModelConverter"));
		return resultStrategy;
	}

	private UpdateValueStrategy assembleViewStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy.setConverter(new ValueViewConverter(
				"ValueViewConverter"));
		return resultStrategy;
	}

	public class ValueModelConverter implements IConverter {
		// Log log = LogFactory.getLog(this.getClass());
		String debugString = "";
		/**
		 * Pattern for matching String input. Provides an initial validation that
		 * should prevent subsequent conversions from blowing up.
		 */
		final public Pattern matchPattern = Pattern
				.compile("^\\d*$");

		public ValueModelConverter(String debugString) {
			this.debugString = debugString;
		}

		public Object convert(Object arg0) {
			// log.debug(debugString + " convert(Object) entered with:" +
			// arg0.toString());
			try {
				Integer candidate = 4711;
				if (arg0 instanceof String) {
					Matcher matcher = matchPattern.matcher((String)arg0);
					if (matcher.matches()) {
						candidate = Integer.decode((String)arg0);
					} else {
						candidate = -4711;
					}
				}
				return candidate;
			} catch (Exception e) {
				return 4712;
			}
		}

		public Object getFromType() {
			return (Object) String.class;
		}

		public Object getToType() {
			return (Object) Integer.class;
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
			String floatString = "NoFloat";
			try {
				if (arg0 == null) {
					floatString = "";
				} else {
					if (arg0 instanceof Float) {
						floatString = ((Float) arg0).toString();
					}
				}
				return floatString;
			} catch (Exception e) {
				floatString = e.getClass().getName();
				return floatString;
			}
		}

		public Object getFromType() {
			// log.debug(debugString + " getFromType() entered.");
			return (Object) Float.class;
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
