package nl.rivm.emi.dynamo.data.types.atomic.base;

import java.util.regex.Pattern;


import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * Pretty plain base-class for String type configuration items.
 */
abstract public class AbstractString extends AtomicTypeBase<String> {

	/**
	 * Pattern for matching String input. Provides an initial validation
	 * that should prevent subsequent conversions from blowing up.
	 */
	// NB(mondeelr) Refine this pattern.
	
	final public Pattern matchPattern = Pattern.compile("^\\w*$");


	/**
	 * Constructor used for overriding.
	 * 
	 * @param myElementName
	 * @param lowerLimit
	 * @param upperLimit
	 * @throws ConfigurationException
	 */
	public AbstractString(String myElementName) {
		super(myElementName, new String());
		modelUpdateValueStrategy = assembleModelStrategy();
		viewUpdateValueStrategy = assembleViewStrategy();
	}

	public String getDefaultValue() {
		return new String("Emptyness2BFilled");
	}

	public String fromString(String inputString) {
		return inputString;
	}

	public String toString(String inputString) {
		return inputString;
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equalsIgnoreCase(elementName)) {
			result = false;
		}
		return result;
	}

	public String getElementName() {
		return XMLElementName;
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
		String nakedValue = null;
		if(modelValue instanceof WritableValue){
		nakedValue = (String)((WritableValue)modelValue).doGetValue();
		} else {
			nakedValue = (String) modelValue;
		}
		String viewValue =  convert4View(nakedValue);
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
		// Log log = LogFactory.getLog(this.getClass());
		String debugString = "";
		public ValueModelConverter(String debugString) {
			this.debugString = debugString;
		}

		/**
		 * Very lenient convert for the moment.
		 */
		public Object convert(Object arg0) {
			// log.debug(debugString + " convert(Object) entered with:" +
			// arg0.toString());
			Object result = null;
			if (arg0 instanceof String) {
				result = arg0;
			}
			return result;
		}

		public Object getFromType() {
			return (Object) String.class;
		}

		public Object getToType() {
			return (Object) String.class;
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
			Object result = null;
			if (arg0 instanceof String) {
				result = arg0;
			}
			return result;
		}

		public Object getFromType() {
			// log.debug(debugString + " getFromType() entered.");
			return (Object) String.class;
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
