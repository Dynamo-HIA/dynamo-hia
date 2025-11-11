package nl.rivm.emi.dynamo.data.types.atomic.base;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * @author mondeelr <br/>
 *         Specialisation for the support of booleans.<br/>
 */
/*
 * 20090331 Converters must convert from Boolean to Boolean when bound to a
 * radiobutton.
 */
public class AbstractBoolean extends AtomicTypeBase<Boolean> implements
		PayloadType<Boolean> {
	Log log = LogFactory.getLog(getClass().getName());
	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern.compile("(^true$)(^false$)");

	/**
	 * Constructor that binds the elementname and assembles and sets nescessary
	 * converters.
	 * 
	 * @param xmlElementName
	 *            The name of the supported elementname.
	 */
	public AbstractBoolean(String XMLElementName) {
		super(XMLElementName, Boolean.FALSE);
		modelUpdateValueStrategy = assembleModelStrategy();
		viewUpdateValueStrategy = assembleViewStrategy();
	}

	/**
	 *Converts from the view-String to the Boolean needed for the modelobject.
	 * 
	 * @param inputString
	 *            The String produced by the view.
	 * @return The resulting Boolean after conversion.
	 */
	@SuppressWarnings("unchecked")
	public Boolean fromString(String inputString) {
		return (Boolean) modelUpdateValueStrategy.convert(inputString);
	}

	/**
	 *Converts from the Boolean in the modelobject to the view-String.
	 * 
	 * @param inputValue
	 *            The Boolean from the modelobject.
	 * @return The String for the view.
	 */
	@SuppressWarnings("unchecked")
	public String toString(Boolean inputValue) {
		return (String) viewUpdateValueStrategy.convert(inputValue);
	}

	/**
	 *Returns the default Boolean value.
	 * 
	 * 
	 * @return The Boolean value that is considered default.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase#getDefaultValue
	 * ()
	 */
	@Override
	public Boolean getDefaultValue() {
		return Boolean.FALSE;
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equals(elementName)) {
			result = false;
		}
		return result;
	}

	public Object convert4Model(String viewString) {
		@SuppressWarnings("unchecked")
		Object result = modelUpdateValueStrategy.convert(viewString);
		return result;
	}

	@Override
	public String convert4View(Object modelValue) {
		log.debug("convert4View(" + modelValue + ")");
		String result = "0";
		if (Boolean.TRUE.equals(modelValue)) {
			result = "1";
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String convert4File(Object modelValue) {
		Boolean nakedValue = null;
		log.debug("convert4File(" + modelValue + ")");
		if (modelValue instanceof WritableValue) {
			log.debug("Type WritableValue: "
					+ ((WritableValue) modelValue).doGetValue());
		
			Object value = ((WritableValue) modelValue).doGetValue();
			if (value instanceof Boolean) {
				log.debug("Type Boolean");
				nakedValue = (Boolean) ((WritableValue) modelValue)
						.doGetValue();
			} else if (value instanceof String) {
				log.debug("Type String");
				nakedValue = Boolean.valueOf((String) ((WritableValue) modelValue)
						.doGetValue());
			}
		} else {
			if (modelValue instanceof Boolean) {
				log.debug("Type Boolean");
				nakedValue = (Boolean) modelValue;
			} else if (modelValue instanceof String) {
				log.debug("Type String");
				nakedValue = Boolean.valueOf((String) modelValue);
			}
		}
		// String viewValue = convert4View(nakedValue);
		String fileValue = "false";
		if (nakedValue) {
			fileValue = "true";
		}
		return fileValue;
		/*
		 * String nakedValue = null; if (modelValue instanceof WritableValue) {
		 * nakedValue = (String) ((WritableValue) modelValue).doGetValue(); }
		 * else { nakedValue = (String) modelValue; } // String viewValue =
		 * convert4View(nakedValue); String fileValue = nakedValue; return
		 * fileValue;
		 */
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected UpdateValueStrategy assembleModelStrategy() {	
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy.setConverter(new ValueModelConverter(
				"ValueModelConverter"));
		return resultStrategy;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected UpdateValueStrategy assembleViewStrategy() {
		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
		resultStrategy
				.setConverter(new ValueViewConverter("ValueViewConverter"));
		return resultStrategy;
	}

	@SuppressWarnings("rawtypes")
	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return modelUpdateValueStrategy;
	}

	@SuppressWarnings("rawtypes")
	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return viewUpdateValueStrategy;
	}

	@SuppressWarnings("rawtypes")
	public class ValueModelConverter implements IConverter {
		String debugString = "";

		public ValueModelConverter(String debugString) {
			this.debugString = debugString;
			log.debug(debugString + " Initializing.");
		}

		public Object convert(Object viewObject) {
			log
					.debug(debugString + " convert(Object) entered with a: "
							+ viewObject.getClass().getName() + " value: "
							+ viewObject);
			Boolean result = null;
			if (!(viewObject instanceof String)) {
				log.fatal("AbstractBoolean was fed wrong Object type: "
						+ viewObject.getClass().getName()
						+ " should be String!");
			} else {
				result = Boolean.FALSE;
				if (("true".equals(viewObject)) || ("1".equals(viewObject))) {
					result = Boolean.TRUE;
				}
			}
			return result;
		}

		public Object getFromType() {
			log.debug(debugString + " getFromType() entered.");
			return (Object) Boolean.class;
		}

		public Object getToType() {
			log.debug(debugString + " getToType() entered.");
			return (Object) String.class;
		}
	}

	@SuppressWarnings("rawtypes")
	public class ValueViewConverter implements IConverter {
		Log log = LogFactory.getLog(this.getClass());
		String debugString = "";

		public ValueViewConverter(String debugString) {
			this.debugString = debugString;
			log.debug(debugString + " Initializing.");
		}

		public Object convert(Object modelObject) {
			log.debug(debugString + " convert(Object) entered with:"
					+ modelObject.toString());
			String result = null;
			if (!(modelObject instanceof Boolean)) {
				log.fatal("AbstractBoolean was fed wrong Object type: "
						+ modelObject.getClass().getName()
						+ " should be Boolean.");
			} else {
				if (modelObject.equals(Boolean.TRUE)) {
					result = "1";
				} else {
					result = "0";
				}
			}
			return result;
		}

		public Object getFromType() {
			log.debug(debugString + " getFromType() entered.");
			return (Object) Boolean.class;
		}

		public Object getToType() {
			log.debug(debugString + " getToType() entered.");
			return (Object) String.class;
		}
	}
}
