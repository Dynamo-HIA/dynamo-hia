package nl.rivm.emi.dynamo.data.types;

public class Sex {
	static final public Integer MIN_VALUE = new Integer(0);
	static final public Integer MAX_VALUE = new Integer(1);
	public static final String XMLElementName = "sex";

	static public boolean inRange(Integer testValue) {
		boolean result = false;
		if (!(MIN_VALUE.compareTo(testValue) > 0)
				&& !(MAX_VALUE.compareTo(testValue) <= 0)) {
			result = true;
		}
		return result;
	}
}
