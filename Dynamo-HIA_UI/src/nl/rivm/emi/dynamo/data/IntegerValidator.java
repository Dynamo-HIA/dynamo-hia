package nl.rivm.emi.dynamo.data;

public class IntegerValidator {
	 private int MINIMUM_VALUE =Integer.MIN_VALUE;
	 private int MAXIMUM_VALUE = Integer.MAX_VALUE;

	public IntegerValidator() {
	}

	/**
	 * Method to validate the content of the candidate.
	 * 
	 * @param candidate
	 * @return When the candidate is valid an Integer instance containing it is
	 *         returned, null otherwise.
	 * 
	 */
	public Integer validate(int candidate) {
		Integer result = null;
		if ((candidate >= MINIMUM_VALUE) && (candidate <= MAXIMUM_VALUE)) {
			result = new Integer(candidate);
		}
		return result;
	}

	/**
	 * Method to validate the content of the candidate.
	 * 
	 * @param candidate
	 * @return When the candidate is valid an Integer instance containing it is
	 *         returned, null otherwise.
	 * 
	 */
	public Integer validate(Integer candidate) {
		int intCandidate = candidate.intValue();
		Integer result = null;
		if ((intCandidate >= MINIMUM_VALUE) && (intCandidate <= MAXIMUM_VALUE)) {
			result = candidate;
		}
		return result;
	}
}
