package nl.rivm.emi.dynamo.data.types.atomic;
/**
 * Nonnegative Integer without fixed upper limit. 
 * This to enable adjustment to the range of categories the transitions can cover. 
 */
public class TransitionSource extends FlexibleUpperLimitNumberRangeTypeBase<Integer> implements ContainerType{
	static final protected String XMLElementName = "from";

	public TransitionSource(){
		super(XMLElementName , new Integer(1), new Integer(Integer.MAX_VALUE));
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

	public Integer setMAX_VALUE(Integer newUpperLimit){
		Integer oldUpperLimit = MAX_VALUE;
		MAX_VALUE = newUpperLimit;
		return oldUpperLimit;
	}
}
