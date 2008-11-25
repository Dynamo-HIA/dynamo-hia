package nl.rivm.emi.cdm.characteristic;

import java.util.ArrayList;

import nl.rivm.emi.cdm.characteristic.types.AbstractCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractContinuousCharacteristicType;

/**
 * The Class <code>Characteristic</code> is an abstraction for both
 * diseases and riskfactors.
 * In a <code>Simulation</code> environment the <code>Characteristics</code> 
 * must each have a unique base one index.
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

/**
 * <code>elementName</code> representing this <code>Object</code> 
 * in XML configurationfiles.
 */
	public static final String xmlElementName = "ch";
	
	/* toegevoegd door Hendriek */
	/**
	 * Number of elementes in this characteristics (used for compound types)</code> 
	 * in XML configurationfiles.
	 */
		public int numberOfElements = -1;

/**
 * Default constructor, initializing nothing.
 *
 */	public Characteristic() {
		super();
	}
 
/**
 * Constructor initializing the index of the disease in the 
 * collection of risk-factors and the name of the 
 * <code>Characteristic</code>
 * @param index The index of the disease in the 
 * collection of risk-factors.
 * @param label The name of the <code>Characteristic</code>.
 */
	public Characteristic(int index, String label) {
		this.index = index;
		this.label = label;
	}

	/**
	 * Setter initializing the index of the disease in the 
	 * collection of risk-factors.
	 * @param index The index of the disease in the 
	 * collection of risk-factors.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Getter retrieving the index of the disease in the 
	 * collection of risk-factors.
	 * @return index The index of the disease in the 
	 * collection of risk-factors.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Setter for the label of the disease.
	 * @param label The label of the disease.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Getter for the label of the disease.
	 * @return label The label of the disease.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Setter for the type of the disease.
	 * @param type The type of the disease.
	 */
	public void setType(AbstractCharacteristicType type) {
		this.type = type;
	}

	/**
	 * Getter for the type of the disease.
	 * @return type The type of the disease.
	 */
	public AbstractCharacteristicType getType() {
		return type;
	}

	/**
	 * Getter for the possible value at the <code>index</code> of the 
	 * <code>Characteristic</code> if any.
	 * Works for categorical <code>CharacteristicType</code>s only.
	 * @param index Index in the array of possible values.
	 * @return The type of the <code>Characteristic</code>.
	 */
	public String getPossibleValue(int index) {
		String possibleValue = null;
		if (type instanceof AbstractCategoricalCharacteristicType) {
			((AbstractCategoricalCharacteristicType) type).getPossibleValue(index);
		}
		return possibleValue;
	}

	/**
	 * Getter for the lower limit of the value-range
	 * <code>Characteristic</code>.
	 * Works for continuous <code>CharacteristicType</code>s only.
	 * @return The lower limit of the value-range.
	 */
	public Float getLowerLimit() {
		Float lowerLimit = null;
		if (type instanceof AbstractContinuousCharacteristicType) {
			((AbstractContinuousCharacteristicType) type).getLowerLimit();
		}
		return lowerLimit;
	}

	/**
	 * Getter for the upper limit of the value-range
	 * <code>Characteristic</code>.
	 * Works for continuous <code>CharacteristicType</code>s only.
	 * @return The upper limit of the value-range.
	 */
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

	/**
	 * Dumps the <code>Characteristic</code> instance's content in a human 
	 * readable form.
	 * 
	 * @return The <code>String</code> with the report.
	 */
	public String humanreadableReport() {
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append(label + "\n");
		resultBuffer.append(type.humanReadableReport());
		return resultBuffer.toString();
	}
	/* toegevoegd door Hendriek */
	/**
	 * @return: number of elements in the (compound) characteristic
	 */
public int getNumberOfElements() {
		
		// TODO Auto-generated method stub
		return numberOfElements;
	}

/* toegevoegd door Hendriek */

/**
 * Setter initializing the number of elements in a compound characteristic
 * @param value The index of the disease in the 
 * collection of risk-factors.
 */
public void setNumberOfElements(int value) {
	this.numberOfElements = value;
}
}
