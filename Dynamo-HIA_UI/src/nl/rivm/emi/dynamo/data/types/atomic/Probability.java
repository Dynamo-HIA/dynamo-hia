package nl.rivm.emi.dynamo.data.types.atomic;

public class Probability extends NumberRangeTypeBase<Float> implements LeafType<Float> {
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
}