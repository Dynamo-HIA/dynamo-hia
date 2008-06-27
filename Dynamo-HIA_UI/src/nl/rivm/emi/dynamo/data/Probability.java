package nl.rivm.emi.dynamo.data;

public class Probability {
	static public float MINIMUM_VALUE = 0F;
	static public float MAXIMUM_VALUE = 1F;

	static Float createValid(float candidate) {
		Float result = null;
		if ((candidate >= MINIMUM_VALUE) && (candidate <= MAXIMUM_VALUE)) {
			result = new Float(candidate);
		}
		return result;
	}
}
