package nl.rivm.emi.cdm.characteristic.types;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegerCategoricalCharacteristicType extends
		AbstractCategoricalCharacteristicType {
	
	static final String myTypeLabel = "numericaldiscrete";

	ArrayList<Integer> possibleValues = new ArrayList<Integer>();

	Pattern integerPattern = Pattern.compile("^\\d++$");

	public IntegerCategoricalCharacteristicType() {
		super(myTypeLabel);
	}

	public boolean addPossibleValue(Object value) {
		boolean success = false;
		if (value instanceof Integer) {
			// Double values not allowed.
			if (!isValueValid(value)) {
				possibleValues.add((Integer) value);
				success = true;
			}
		} else {
			if (value instanceof String) {
				Matcher integerMatcher = integerPattern.matcher((String) value);
				boolean integerMatch = integerMatcher.matches();
				if (integerMatch) {
					Integer integerValue = Integer.decode((String) value);
					if (!isValueValid(value)) {
						possibleValues.add(integerValue);
						success = true;
					}
				}
			}
		}
		return success;
	}

	public boolean isValueValid(Object value) {
		return possibleValues.contains((Integer) value);
	}

	public Integer getValue(int index) {
		return possibleValues.get(index);
	}

	@Override
	public Integer getNumberOfPossibleValues() {
		return new Integer(possibleValues.size());
	}
}
