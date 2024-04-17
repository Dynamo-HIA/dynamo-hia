package nl.rivm.emi.cdm.characteristic.types;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueStringParser;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;

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
					Integer integerValue = Integer.valueOf((String) value);
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
		boolean valid = false;
		if (value instanceof String) {
			valid = possibleValues.contains((String) value);
		} else {
			if (value instanceof Integer) {
				valid = possibleValues.contains((Integer) value);
			}
		}
		return valid;
	}

	public Integer getPossibleValue(int index) {
		return possibleValues.get(index);
	}

	@Override
	public Integer getNumberOfPossibleValues() {
		return new Integer(possibleValues.size());
	}

	@Override
	public String humanReadableReport() {
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("Typelabel: " + myTypeLabel + "\n");
		for (int count = 0; count < possibleValues.size(); count++) {
			resultBuffer.append("Possible value at index " + (count + 1)
					+ " value " + possibleValues.get(count) + "\n");
		}
		return resultBuffer.toString();
	}

	public ArrayList<Integer> getPossibleValues() {
		return possibleValues;
	}

	@Override
	public Object convertFromString(String valueAsString,
			int indexInConfiguration) {
		Integer integerValue = CharacteristicValueStringParser
				.parseStringToInteger(valueAsString);
		IntCharacteristicValue intCharVal = new IntCharacteristicValue(1,
				indexInConfiguration, integerValue);
		return intCharVal;
	}
}
