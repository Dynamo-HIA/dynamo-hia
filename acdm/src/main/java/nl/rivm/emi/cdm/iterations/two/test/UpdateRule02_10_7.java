package nl.rivm.emi.cdm.iterations.two.test;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.StepSizeSpecific;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdateRule02_10_7 extends OneToOneUpdateRuleBase implements
		CharacteristicSpecific, StepSizeSpecific {

	Log log = LogFactory.getLog(this.getClass().getName());

	int characteristicId = 7;

	float stepSize = 1;

	@Override
	public Object update(Object currentValue) throws CDMUpdateRuleException {
		Integer newValue = null;
		if (currentValue!= null){
				newValue = 1;
		}
		return newValue;
	}

	public int getCharacteristicId() {
		return characteristicId;
	}

	public void setCharacteristicId(int characteristicId) {
		log.info("Setting characteristicId to " + characteristicId);
		this.characteristicId = characteristicId;
	}

	public float getStepSize() {
		return stepSize;
	}

	public void setStepSize(float stepSize) throws CDMUpdateRuleException {
		log.info("Setting stepSize to " + stepSize);
		this.stepSize = stepSize;
	}
}
