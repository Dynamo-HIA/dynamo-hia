package nl.rivm.emi.dynamo.data.types.atomic;

public class Percentage extends NumberRangeTypeBase<Float>{

	public Percentage(){
		super("percent", new Float(0), new Float(100));
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
		Float result = null;
		try {
			result = Float.parseFloat(inputString);
			if (!inRange(result)) {
				result = null;
			}
			return result;
		} catch (NumberFormatException e) {
			result = null;
			return result;
		}
	}

	public String toString(Float inputValue) {
		// TODO Auto-generated method stub
		return null;
	}
}
