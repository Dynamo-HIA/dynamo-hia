package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class AbstractValue extends NumberRangeTypeBase<Float> implements PayloadType<Float>{

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern
			.compile("^\\d*\\.?\\d*$");

	public AbstractValue(String XMLElementName){
		this(XMLElementName, 0F, Float.MAX_VALUE);
	}

	/**
	 * Constructor for use by subclasses.
	 * @param elementName
	 * @param minimum
	 * @param maximum
	 * @throws ConfigurationException 
	 */
	public AbstractValue(String elementName, Float minimum, Float maximum){
		super(elementName, minimum, maximum);
		modelUpdateValueStrategy = assembleModelStrategy();
		viewUpdateValueStrategy = assembleViewStrategy();
	}
	
	public boolean inRange(Float testValue) {
		boolean result = false;
		if (!(MIN_VALUE.compareTo(testValue) > 0)
				&& !(MAX_VALUE.compareTo(testValue) < 0)) {
			result = true;
		}
		return result;
	}
	public Float fromString(String inputString) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString(Float inputValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Float getDefaultValue() {
		return 0F;
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
		String result = (String)viewUpdateValueStrategy.convert(modelValue);
		return result.toString();
	}
	
	public Object convert4Model(String viewString) {
		Object result = modelUpdateValueStrategy.convert(viewString);
		return result;
	}

	@Override
	public String convert4File(Object modelValue) {
		Float nakedValue = null;
		if(modelValue instanceof WritableValue){
		nakedValue =  (Float)((WritableValue)modelValue).doGetValue();
		} else {
			nakedValue = (Float) modelValue;
		}
		String viewValue =  convert4View(nakedValue);
		return viewValue;
	}

	protected UpdateValueStrategy assembleModelStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy.setConverter(new ValueModelConverter(
				"ValueModelConverter"));
		return resultStrategy;
	}

	protected UpdateValueStrategy assembleViewStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy.setConverter(new ValueViewConverter(
				"ValueViewConverter"));
		return resultStrategy;
	}

	public class ValueModelConverter implements IConverter {
		// Log log = LogFactory.getLog(this.getClass());
		String debugString = "";

		public ValueModelConverter(String debugString) {
			this.debugString = debugString;
		}

		public Object convert(Object arg0) {
			// log.debug(debugString + " convert(Object) entered with:" +
			// arg0.toString());
			try {
				Float floatCandidate = 4711F;
				if (arg0 instanceof String) {
					if ("".equals(arg0)) {
						floatCandidate = null;
					} else {
						floatCandidate = Float.parseFloat((String) arg0);
					}
				}
				return floatCandidate;
			} catch (Exception e) {
				return 4712F;
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
