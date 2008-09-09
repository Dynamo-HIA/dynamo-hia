package nl.rivm.emi.dynamo.data.types;

public class Age {
	static public Float MIN_VALUE = new Float(0F);
	static public Float MAX_VALUE = new Float(95F);

	static public boolean inRange(Float testValue) {
		boolean result = false;
		if (!(MIN_VALUE.compareTo(testValue) > 0)
				&& !(MAX_VALUE.compareTo(testValue) < 0)) {
			result = true;
		}
		return result;
	}
}
