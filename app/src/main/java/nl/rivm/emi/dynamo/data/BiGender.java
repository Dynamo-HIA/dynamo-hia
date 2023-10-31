package nl.rivm.emi.dynamo.data;

/**
 * @author mondeelr<br/>
 *         Central point for the indices and Strings used for the management of
 *         the genders.
 */
public class BiGender {
	/**
	 * Number of genders.
	 */
	static public int NUMBER_OF_CLASSES = 2;
	/**
	 * Lowest possible gender index.
	 */
	static public int MINIMUM_VALUE = 0;
	/**
	 * Highest possible gender index.
	 */
	static public int MAXIMUM_VALUE = 1;
	/**
	 * Index for the female gender.
	 */
	static final public int FEMALE_INDEX = 1;
	/**
	 * Index for the male gender.
	 */
	static final public int MALE_INDEX = 0;
	/**
	 * Gender labels.
	 */
	static final public String labels[] = { "male", "female" };

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
