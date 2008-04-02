package nl.rivm.emi.cdm.characteristic;

import java.util.ArrayList;

import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;

/**
 * Characteristics each have a unique base one index among themselves.
 * 
 */
public class Characteristic {
	/**
	 * The unique index. -1 indicates it hasn't been initialized.
	 */
	private int index = -1;
/**
 * The name of the Characteristic.
 */
	private String label = null;
/**
 * The type of the Characteristic.
 * One of "categorical", "numericalcontinuous" or "numericaldiscrete".
 */
	private AbstractCharacteristicType type = null;

	private ArrayList<String> possibleValues = null;

	public static final String xmlElementName = "ch";

	public Characteristic() {
		super();
	}
	
	public Characteristic(int index, String label) {
		this.index = index;
		this.label = label;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public AbstractCharacteristicType getType() {
		return type;
	}

	public void setType(AbstractCharacteristicType type) {
		this.type = type;
	}

	public ArrayList<String> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(ArrayList<String> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public void createPossibleValues() {
		this.possibleValues = new ArrayList<String>();
	}

	public void addValue(String possibleValue){
		possibleValues.add(possibleValue);
	}
	public String getValue(int index){
		return possibleValues.get(index);
	}

/**
 * Check whether the Characteristic instance has a minimal set of values.
 * @return The this reference when this test is OK, null otherwise.
 * TODO This test must be kept in sync with the structure of the XML configuration.
 */
	public Characteristic isValid() {
		Characteristic thisWhenValid = null;
		if (index != -1) {
			if (label != null) {
				if (type != null) {
					if ((possibleValues != null) && (possibleValues.size() > 0)) {
						thisWhenValid = this;
					}
				}
			}
		}
		return thisWhenValid;
	}
}
