package nl.rivm.emi.dynamo.data.types.atomic;

public class Sex extends NumberRangeTypeBase<Integer> implements ContainerType{

	public Sex(){
		super("sex", new Integer(0), new Integer(1));
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
		// TODO Auto-generated method stub
		return null;
	}
}
