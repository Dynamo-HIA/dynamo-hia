package nl.rivm.emi.cdm.iterations.two.test;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.prngcollection.MersenneTwister;
import nl.rivm.emi.cdm.updaterules.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.updaterules.base.ManyToOneUpdateRuleBase;
import nl.rivm.emi.cdm.updaterules.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.updaterules.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.updaterules.base.NeedsSeed;
import nl.rivm.emi.cdm.updaterules.base.StepSizeSpecific;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdateRule02_10_1 extends OneToOneUpdateRuleBase implements
		CharacteristicSpecific, StepSizeSpecific {

	Log log = LogFactory.getLog(this.getClass().getName());

	int characteristicId = 1;

	float stepSize = 1;

	@Override
	public Object update(Object currentValue) throws CDMUpdateRuleException {
		Integer newValue = null;
		if (currentValue!= null){
			if (currentValue instanceof Integer) {
				int currentIntValue = ((Integer) currentValue)
						.intValue();
				newValue = Math.min(currentIntValue, 10);
			}
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
