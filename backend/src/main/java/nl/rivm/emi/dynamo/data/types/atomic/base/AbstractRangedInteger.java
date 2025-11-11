package nl.rivm.emi.dynamo.data.types.atomic.base;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

abstract public class AbstractRangedInteger extends
		NumberRangeTypeBase<Integer> implements VerifyListener{

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern.compile("^\\d*$");

	public AbstractRangedInteger(String XMLElementName, Integer lowerLimit,
			Integer upperLimit) {
		super(XMLElementName, lowerLimit, upperLimit);
		modelUpdateValueStrategy = assembleModelStrategy();
		viewUpdateValueStrategy = assembleViewStrategy();
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
		return MIN_VALUE;
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
					// Doesn't work with prefix zeroes.
//					result = Integer.decode(arg0.toString());
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
			return (Object) Integer.class;
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
	
	public void verifyText(VerifyEvent arg0) {
		Text myText = (Text) arg0.widget;
		String currentContent = myText.getText();
		String candidateContent = currentContent.substring(0, arg0.start)
				+ arg0.text
				+ currentContent.substring(arg0.end, currentContent.length());
//		log.debug("VerifyEvent with current content: " + currentContent + " , candidate content: " + candidateContent);
		arg0.doit = false;
		myText.setBackground(new Color(null, 0xff, 0xff, 0xff));
		try {
			if (candidateContent.length() == 0) {
				myText.setBackground(new Color(null, 0xff, 0xff, 0xcc));
				arg0.doit = true;
			} else {
				if (matchPattern.matcher(candidateContent)
						.matches()) {
					Integer candidateInteger = Integer.valueOf(candidateContent);
					NumberRangeTypeBase<Integer> myAtomicType = (NumberRangeTypeBase<Integer>)this;
					if (myAtomicType.inRange(candidateInteger)) {
						arg0.doit = true;
						myText.setBackground(new Color(null, 0xff, 0xff, 0xff));
					}
				} else {
					arg0.doit = false;
					myText.setBackground(new Color(null, 0xff, 0xbb, 0xbb));
				}
			}
//			log.debug("verifyText, normal exit with doIt=" + arg0.doit);
		} catch (Exception e) {
			arg0.doit = false;
//			log.debug("verifyText, exception exit with doIt=" + arg0.doit);
		}
	}

}
