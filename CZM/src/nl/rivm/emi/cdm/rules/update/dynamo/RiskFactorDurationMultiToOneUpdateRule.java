package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import Jama.Matrix;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.exceptions.DynamoUpdateRuleConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.DynamoManyToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation for DYNAMO-HIA
 * 
 * @author boshuizh
 * 
 */
public class RiskFactorDurationMultiToOneUpdateRule extends
		DynamoManyToOneUpdateRuleBase implements ConfigurationEntryPoint {

	Log log = LogFactory.getLog(this.getClass().getName());

	String[] requiredTags = { "charID", "durationClass" };

	float stepsize = 1;
	int durationClass = -1;
	int riskFactorIndex = 3;
	int characteristicIndex = 4;
	private final String durationClassLabel = "durationClass";
	private static String charIDLabel = "charID";

	public RiskFactorDurationMultiToOneUpdateRule(String configFileName,
			int randomSeed) throws ConfigurationException {

	}

	public RiskFactorDurationMultiToOneUpdateRule()
			throws ConfigurationException {
		// empty constructor needed in order to be loaded
		// temporary;
		super();
	}

	public Object update(Object[] currentValues, Long seed)
			throws CDMUpdateRuleException {

		try {

			float newValue = -1;
			float oldValue = getFloat(currentValues, characteristicIndex);
			int ageValue = (int) getFloat(currentValues, getAgeIndex());
			if (ageValue < 0) {
				newValue = oldValue;
				return newValue;
			} else {
				int riskValue = getInteger(currentValues, riskFactorIndex);

				if (riskValue == durationClass)
					newValue = oldValue + stepsize;
				else
					newValue = 0;

				return newValue;
			}
		} catch (CDMUpdateRuleException e) {
			log.fatal(e.getMessage());
			log
					.fatal("this message was issued by RiskFactorDurationMultiToOneUpdateRule"
							+ " when updating characteristic number "
							+ "characteristicIndex");
			e.printStackTrace();
			throw e;
		}
	}

	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		boolean success = false;
		try {
			XMLConfiguration configurationFileConfiguration;
	
			configurationFileConfiguration = new XMLConfiguration(configurationFile);
	
			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFileConfiguration.clear();
			
			// Validate the xml by xsd schema
			configurationFileConfiguration.setValidating(true);			
			configurationFileConfiguration.load();
			
			handleCharID(configurationFileConfiguration);
			handleDurationClass(configurationFileConfiguration);
	
			success = true;
	
			return success;			
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(log, e.getMessage(), e, configurationFile.getAbsolutePath());			
		}					
		return success;			
	}

		
	private void handleDurationClass(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			int nCat = simulationConfiguration.getInt(durationClassLabel);
			log.debug("Setting duration class to " + nCat);
			setDurationClass(nCat);
		} catch (NoSuchElementException e) {
			throw new CDMConfigurationException(String.format(
					CDMConfigurationException.noConfigurationTagMessage,
					this.durationClassLabel, this.getClass().getSimpleName(),
					durationClassLabel));
		}
	}

	protected void handleCharID(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		try {
			int readCharacteristicIndex = simulationConfiguration
					.getInt(charIDLabel);
			if (characteristicIndex != readCharacteristicIndex)
				log
						.fatal("the characteristics number in the rule-configuration file does not match"
								+ "the expected value of 4 in the duration update rule");

		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noUpdateCharIDMessage);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase#update(java
	 * .lang.Object[], java.lang.Long)
	 */

	public int getDurationClass() {
		return durationClass;
	}

	public void setDurationClass(int durationClass) {
		this.durationClass = durationClass;
	}
}
