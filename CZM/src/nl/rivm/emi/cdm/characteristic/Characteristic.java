package nl.rivm.emi.cdm.characteristic;

import java.util.ArrayList;

import nl.rivm.emi.cdm.characteristic.types.AbstractCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractContinuousCharacteristicType;

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
	 * The type of the Characteristic. One of "categorical",
	 * "numericalcontinuous" or "numericaldiscrete".
	 */
	private AbstractCharacteristicType type = null;

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

	public String getPossibleValue(int index) {
		String possibleValue = null;
		if (type instanceof AbstractCategoricalCharacteristicType) {
			((AbstractCategoricalCharacteristicType) type).getPossibleValue(index);
		}
		return possibleValue;
	}

	public Float getLowerLimit() {
		Float lowerLimit = null;
		if (type instanceof AbstractContinuousCharacteristicType) {
			((AbstractContinuousCharacteristicType) type).getLowerLimit();
		}
		return lowerLimit;
	}

	public Float getUpperLimit() {
		Float upperLimit = null;
		if (type instanceof AbstractContinuousCharacteristicType) {
			((AbstractContinuousCharacteristicType) type).getUpperLimit();
		}
		return upperLimit;
	}

	/**
	 * Check whether the Characteristic instance has a minimal set of values.
	 * 
	 * @return The this reference when this test is OK, null otherwise. TODO
	 *         This test must be kept in sync with the structure of the XML
	 *         configuration.
	 */
	public Characteristic isValid() {
		Characteristic thisWhenValid = null;
		if (index != -1) {
			if (label != null) {
				if (type != null) {
					if(type instanceof AbstractCategoricalCharacteristicType){
						ArrayList possibleValues = ((AbstractCategoricalCharacteristicType)type).getPossibleValues();
					if (( possibleValues != null) && (possibleValues.size() > 0)) {
						thisWhenValid = this;
					}
					}
				}
			}
		}
		return thisWhenValid;
	}

	public String humanreadableReport() {
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append(label + "\n");
		resultBuffer.append(type.humanReadableReport());
		// for(int count = 0; count< possibleValues.size(); count++){
		// resultBuffer.append("Possible value at index " + (count+1) + " value
		// " + possibleValues.get(count));
		// }
		return resultBuffer.toString();
	}
}
