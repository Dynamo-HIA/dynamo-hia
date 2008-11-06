package nl.rivm.emi.dynamo.data;

public class BiGender {
	static public int NUMBER_OF_CLASSES = 2;
	static public int MINIMUM_VALUE = 0;
	static public int MAXIMUM_VALUE = 1;
	static public int FEMALE_INDEX = 0;
	static public int MALE_INDEX = 1;
	static public String labels[] = { "female", "male" };

	private BiGender() {
	}

	/**
	 * Method to validate the content of the candidate.
	 * 
	 * @param candidate
	 * @return When the candidate is valid an Integer instance containing it is
	 *         returned, null otherwise.
	 * 
	 */
	static Integer validate(int candidate) {
		Integer result = null;
		if ((candidate >= MINIMUM_VALUE) && (candidate <= MAXIMUM_VALUE)) {
			result = new Integer(candidate);
		}
		return result;
	}
}
