package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class Probability extends NumberRangeTypeBase<Float> implements PayloadType<Float> {
	static final protected String XMLElementName = "prob";

	public Probability(){
	super("prob", new Float(0F), new Float(1F));
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
	Object convert4Model(String viewString) {
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
