package nl.rivm.emi.cdm.iterations.two.test;

import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.StepSizeSpecific;

/**
 * Update rule for incrementing the age Characteristic. Has been made specific
 * to that characteristic.
 * 
 * @author mondeelr
 * 
 */
public class UpdateRule02_02 extends ManyToOneUpdateRuleBase implements
		CharacteristicSpecific, StepSizeSpecific {

	private int characteristicId;

	private float stepSize;

	/**
	 * Default constructor for loading via classname.
	 */
	public UpdateRule02_02() {
		super();
	}

	public UpdateRule02_02(int characteristicId) {
		super();
		setCharacteristicId(characteristicId);
	}

	public Float update(Object[] currentValues) 
	//throws WrongUpdateRuleException 
	{
		Float returnValue = null;
		if(!(currentValues[1] == null)&&(currentValues[1] instanceof Integer) &&!(currentValues[2]==null)){
			Characteristic characteristic1 = CharacteristicsConfigurationMapSingleton.getInstance().getCharacteristic(1);
			String derefValue = characteristic1.getPossibleValue((Integer)currentValues[1]);
			float currVal1 = ((Integer)currentValues[1]).floatValue();
			float currVal2 = ((Float)currentValues[2]).floatValue();
			returnValue = currVal2*(1F+((100F - currVal1)/100F));
					}
		return returnValue;
		}

	public int getCharacteristicId() {
		return this.characteristicId;
	}

	public void setCharacteristicId(int characteristicId) {
		this.characteristicId = characteristicId;
	}

	public float getStepSize() {
		return stepSize;
	}

	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}
}
