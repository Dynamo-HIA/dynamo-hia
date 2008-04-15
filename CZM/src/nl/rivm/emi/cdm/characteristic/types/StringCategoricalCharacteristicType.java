package nl.rivm.emi.cdm.characteristic.types;

import java.util.ArrayList;

public class StringCategoricalCharacteristicType extends
		AbstractCategoricalCharacteristicType {
	static final String myTypeLabel = "categorical";

	ArrayList<String> possibleValues = new ArrayList<String>();

	public StringCategoricalCharacteristicType() {
		super(myTypeLabel);
	}

	/**
	 * An ArrayList is used because the possible values can then reliably
	 * referred to by index. If an old set of values is replaced, the reference
	 * is returned.
	 */
	public boolean addPossibleValue(Object value) {
		boolean success = false;
		if (value instanceof String) {
			if (!isValueValid(value)) {
				possibleValues.add((String) value);
				success = true;
			}
		}
		return success;
	}

	public boolean isValueValid(Object value) {
		return possibleValues.contains(value);
	}

	public String getPossibleValue(int index) {
		return possibleValues.get(index);
	}

	public Integer getNumberOfPossibleValues() {
		return new Integer(possibleValues.size());
	}

	public ArrayList<String> getPossibleValues() {
		return possibleValues;
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
}
