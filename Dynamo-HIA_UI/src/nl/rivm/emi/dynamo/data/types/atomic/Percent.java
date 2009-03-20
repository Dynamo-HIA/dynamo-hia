package nl.rivm.emi.dynamo.data.types.atomic;

import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class Percent extends AbstractValue implements
		PayloadType<Float> {
	static final protected String XMLElementName = "percent";

	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern
			.compile("^\\d{0,3}(\\.\\d{0,8}?)?$");

//	private UpdateValueStrategy modelUpdateValueStrategy;
//	private UpdateValueStrategy viewUpdateValueStrategy;

	public Percent(){
		super("percent", new Float(0), new Float(100));
		modelUpdateValueStrategy = assembleModelStrategy();
		viewUpdateValueStrategy = assembleViewStrategy();
	}

//	public boolean inRange(Float testValue) {
//		boolean result = false;
//		if (!(MIN_VALUE.compareTo(testValue) > 0)
//				&& !(MAX_VALUE.compareTo(testValue) < 0)) {
//			result = true;
//		}
//		return result;
//	}
//
//	public Float fromString(String inputString) {
//		Float result = null;
//		try {
//			result = Float.parseFloat(inputString);
//			if (!inRange(result)) {
//				result = null;
//			}
//			return result;
//		} catch (NumberFormatException e) {
//			result = null;
//			return result;
//		}
//	}
//
//	public String toString(Float inputValue) {
//		return null;
//	}
//
//	public Float getDefaultValue() {
//		return 0F;
//	}
//
//	public String getElementName() {
//		return XMLElementName;
//	}
//
//	public boolean isMyElement(String elementName) {
//		boolean result = true;
//		if (!XMLElementName.equalsIgnoreCase(elementName)) {
//			result = false;
//		}
//		return result;
//	}
//
//	public String convert4View(Object modelValue) {
//		String result = (String)viewUpdateValueStrategy.convert(modelValue);
//		return result.toString();
//	}
//	
//	public Object convert4Model(String viewString) {
//		Object result = modelUpdateValueStrategy.convert(viewString);
//		return result;
//	}
//
//	private UpdateValueStrategy assembleModelStrategy() {
//		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
//		resultStrategy.setConverter(new PercentModelConverter(
//				"PercentModelConverter"));
//		return resultStrategy;
//	}
//
//	private UpdateValueStrategy assembleViewStrategy() {
//		UpdateValueStrategy resultStrategy = new UpdateValueStrategy();
//		resultStrategy.setConverter(new PercentViewConverter(
//				"PercentViewConverter"));
//		return resultStrategy;
//	}
//
//	public class PercentModelConverter implements IConverter {
//		// Log log = LogFactory.getLog(this.getClass());
//		String debugString = "";
//
//		public PercentModelConverter(String debugString) {
//			this.debugString = debugString;
//		}
//
//		public Object convert(Object arg0) {
//			// log.debug(debugString + " convert(Object) entered with:" +
//			// arg0.toString());
//			try {
//				Float floatCandidate = 4711F;
//				if (arg0 instanceof String) {
//					if ("".equals(arg0)) {
//						floatCandidate = null;
//					} else {
//						floatCandidate = Float.parseFloat((String) arg0);
//					}
//				}
//				return floatCandidate;
//			} catch (Exception e) {
//				return 4712F;
//			}
//		}
//
//		public Object getFromType() {
//			// log.debug(debugString + " getFromType() entered.");
//			return (Object) String.class;
//		}
//
//		public Object getToType() {
//			// log.debug(debugString + " getToType() entered.");
//			return (Object) Float.class;
//		}
//	}
//
//	public class PercentViewConverter implements IConverter {
//		// Log log = LogFactory.getLog(this.getClass());
//		String debugString = "";
//
//		public PercentViewConverter(String debugString) {
//			this.debugString = debugString;
//		}
//
//		public Object convert(Object arg0) {
//			// log.debug(debugString + " convert(Object) entered with:" +
//			// arg0.toString());
//			String floatString = "NoFloat";
//			try {
//				if (arg0 == null) {
//					floatString = "";
//				} else {
//					if (arg0 instanceof Float) {
//						floatString = ((Float) arg0).toString();
//					}
//				}
//				return floatString;
//			} catch (Exception e) {
//				floatString = e.getClass().getName();
//				return floatString;
//			}
//		}
//
//		public Object getFromType() {
//			// log.debug(debugString + " getFromType() entered.");
//			return (Object) Float.class;
//		}
//
//		public Object getToType() {
//			// log.debug(debugString + " getToType() entered.");
//			return (Object) String.class;
//		}
//	}
//
//	public UpdateValueStrategy getModelUpdateValueStrategy() {
//		return modelUpdateValueStrategy;
//	}
//
//	public UpdateValueStrategy getViewUpdateValueStrategy() {
//		return viewUpdateValueStrategy;
//	}
}
