package nl.rivm.emi.dynamo.data.types.atomic.base;
/**
 * 
 */
import java.util.regex.Pattern;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class AbstractBoolean extends AtomicTypeBase<Boolean> implements
		PayloadType<Boolean> {
	Log log = LogFactory.getLog(getClass().getName());
	/**
	 * Pattern for matching String input. Provides an initial validation that
	 * should prevent subsequent conversions from blowing up.
	 */
	final public Pattern matchPattern = Pattern.compile("(^true$)(^false$)");

	public AbstractBoolean(String XMLElementName) {
		super(XMLElementName, Boolean.FALSE);
		modelUpdateValueStrategy = assembleModelStrategy();
		viewUpdateValueStrategy = assembleViewStrategy();
	}

	public Boolean fromString(String inputString) {
		return (Boolean)modelUpdateValueStrategy.convert(inputString);
	}

	public String toString(Boolean inputValue) {
		return (String)viewUpdateValueStrategy.convert(inputValue);
	}

	@Override
	public Boolean getDefaultValue() {
		return Boolean.FALSE;
	}

	public String getElementName() {
		return XMLElementName;
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equals(elementName)) {
			result = false;
		}
		return result;
	}

	public Object convert4Model(String viewString) {
		Object result = modelUpdateValueStrategy.convert(viewString);
		return result;
	}

	@Override
	public String convert4View(Object modelValue) {
		String result = "0";
		if (Boolean.TRUE.equals(modelValue)) {
			result = "1";
		}
		return result;
	}

	@Override
	public String convert4File(Object modelValue) {
		Boolean nakedValue = null;
		if (modelValue instanceof WritableValue) {
			nakedValue = (Boolean) ((WritableValue) modelValue).doGetValue();
		} else {
			nakedValue = (Boolean) modelValue;
		}
		String viewValue = convert4View(nakedValue);
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
		resultStrategy
				.setConverter(new ValueViewConverter("ValueViewConverter"));
		return resultStrategy;
	}

	public UpdateValueStrategy getModelUpdateValueStrategy() {
		return modelUpdateValueStrategy;
	}

	public UpdateValueStrategy getViewUpdateValueStrategy() {
		return viewUpdateValueStrategy;
	}

	public class ValueModelConverter implements IConverter {
		String debugString = "";

		public ValueModelConverter(String debugString) {
			this.debugString = debugString;
		}

		public Object convert(Object viewString) {
			// log.debug(debugString + " convert(Object) entered with:" +
			// arg0.toString());
			Boolean result = null;
			if (!(viewString instanceof String)) {
				log.fatal("AbstractBoolean was fed wrong Object type: "
						+ viewString.getClass().getName()
						+ " should be String!");
			} else {
				result = Boolean.FALSE;
				if ("true".equals(viewString)) {
					result = Boolean.TRUE;
				}
			}
			return result;
		}

		public Object getFromType() {
			// log.debug(debugString + " getFromType() entered.");
			return (Object) String.class;
		}

		public Object getToType() {
			// log.debug(debugString + " getToType() entered.");
			return (Object) Boolean.class;
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
			String result = null;
			if (!(arg0 instanceof Boolean)) {
				log.fatal("AbstractBoolean was fed wrong Object type: "
						+ arg0.getClass().getName()
						+ " should be Boolean.");
			} else {
				if(arg0.equals(Boolean.TRUE)){
					result = "true";
				} else {
					result = "false";
				}
			}
			return result;
			}
		

		public Object getFromType() {
			// log.debug(debugString + " getFromType() entered.");
			return (Object) Boolean.class;
		}

		public Object getToType() {
			// log.debug(debugString + " getToType() entered.");
			return (Object) String.class;
		}
	}
	
	// TODO(mondeelr) Develop verifyListener.
	public class AbstractBooleanVerifyListener implements VerifyListener {
		Log log = LogFactory.getLog(this.getClass().getName());

		public void verifyText(VerifyEvent arg0) {
			Text myText = (Text) arg0.widget;
			String currentContent = myText.getText();
			String candidateContent = currentContent.substring(0, arg0.start)
					+ arg0.text
					+ currentContent.substring(arg0.end, currentContent.length());
			log.debug("VerifyEvent with current content: " + currentContent
					+ " , candidate content: " + candidateContent);
			arg0.doit = false;
			myText.setBackground(new Color(null, 0xff, 0xff, 0xff));
			try {
				if (candidateContent.length() == 0) {
					myText.setBackground(new Color(null, 0xff, 0xff, 0xcc));
					arg0.doit = true;
				} else {
					if ((((Value)XMLTagEntityEnum.STANDARDVALUE.getTheType()).matchPattern.matcher(candidateContent)).matches()) {
						Float candidateFloat = Float.valueOf(candidateContent);
						if (((NumberRangeTypeBase<Float>) XMLTagEntitySingleton
								.getInstance().get("value"))
								.inRange(candidateFloat)) {
							arg0.doit = true;
							myText.setBackground(new Color(null, 0xff, 0xff, 0xff));
						}
					} else {
						arg0.doit = false;
						myText.setBackground(new Color(null, 0xff, 0xbb, 0xbb));
					}
				}
				log.debug("verifyText, normal exit with doIt=" + arg0.doit);
			} catch (Exception e) {
				arg0.doit = false;
				log.debug("verifyText, exception exit with doIt=" + arg0.doit);
			}
		}

	}
}