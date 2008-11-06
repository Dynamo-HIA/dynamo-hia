package nl.rivm.emi.dynamo.data.types.atomic;
/*
 * Nonnegative Integer without fixed upper limit.
 */
public class Category extends NumberRangeTypeBase<Integer>{

	public Category(){
		super("cat", new Integer(0), new Integer(Integer.MAX_VALUE));
	}
		
	public boolean inRange(Integer testValue) {
		boolean result = false;
		if (!(MIN_VALUE.compareTo(testValue) > 0)
				&& !(MAX_VALUE.compareTo(testValue) < 0)) {
			result = true;
		}
		return result;
	}

	public Integer fromString(String inputString) {
		Integer result = null;
		try {
			result = Integer.decode(inputString);
			if (!inRange(result)) {
				result = null;
			}
			return result;
		} catch (NumberFormatException e) {
			result = null;
			return result;
		}
	}

	public String toString(Integer inputValue) {
		return Integer.toString(inputValue.intValue());
	}
}
