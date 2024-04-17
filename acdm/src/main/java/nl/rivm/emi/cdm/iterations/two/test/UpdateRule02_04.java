package nl.rivm.emi.cdm.iterations.two.test;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.prngcollection.MersenneTwister;
import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.NeedsSeed;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.StepSizeSpecific;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdateRule02_04 extends ManyToOneUpdateRuleBase implements
		CharacteristicSpecific, StepSizeSpecific, NeedsSeed {

	Log log = LogFactory.getLog(this.getClass().getName());

	int characteristicId = 2;

	float stepSize = 1;

	final int maxRandInt = 100000;

	final int threshold = (int) (0.25F * maxRandInt);

	// TODO Implement
	Random prnGenerator = new MersenneTwister();

	public long setAndNextSeed(long seed) {
		prnGenerator.setSeed(seed);
		return prnGenerator.nextLong();
	}

	private float convertObjectValueToFloat(Object currentValue)
			throws CDMUpdateRuleException {
		float currentValue1;
		if (currentValue instanceof Float) {
			currentValue1 = (Float) currentValue;
		} else {
			if (currentValue instanceof Integer) {
				currentValue1 = ((Integer) currentValue).floatValue();
			} else {
				throw new CDMUpdateRuleException(String.format(
						CDMUpdateRuleException.wrongParameterMessage,
						currentValue.getClass().getSimpleName()));
			}
		}
		return currentValue1;
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

	@Override
	public Object update(Object[] currentValues, Long seed)
			throws CDMUpdateRuleException, CDMUpdateRuleException {
		Float newValue = null;
		if ((currentValues[characteristicId] != null)
				&& (currentValues[1] != null)) {
			if (currentValues[characteristicId] instanceof Float) {
				float currentValue = ((Float) currentValues[characteristicId])
						.floatValue();
				float currentValue1 = convertObjectValueToFloat(currentValues[1]);
				int randomValue = prnGenerator.nextInt(maxRandInt);
				log.debug("Random value returned: " + randomValue
						+ ", threshold: " + threshold);
				if (randomValue < threshold) {
					newValue = currentValue
							+ (0.01F * (((currentValue1 * 2F) - 1F) * (randomValue / (25F * maxRandInt))));
				} else {
					log.debug("Above threshold.");
					newValue = currentValue;
				}
			}
		}
		return newValue;
	}
}
