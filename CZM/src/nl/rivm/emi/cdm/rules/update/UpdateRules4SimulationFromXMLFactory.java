package nl.rivm.emi.cdm.rules.update;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.StepSizeSpecific;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRules4Simulation;
import nl.rivm.emi.cdm.simulation.Simulation;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdateRules4SimulationFromXMLFactory {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.updaterules.UpdateRules4SimulationFromXMLFactory");

	public static final String updaterulesLabel = "updaterules";

	public static final String updateruleLabel = "updaterule";

	public static final String characteristicIdLabel = "characteristicid";

	public static final String classNameLabel = "classname";

	public static final String configurationFileLabel = "configurationfile";

	public static UpdateRules4Simulation manufacture(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
		UpdateRules4Simulation updateRules = new UpdateRules4Simulation();
		List<SubnodeConfiguration> updateRulesConfigurations = simulationConfiguration
				.configurationsAt(updaterulesLabel);
		if (updateRulesConfigurations.size() == 1) {
			log.debug("Updaterules found");
			updateRules = new UpdateRules4Simulation();
			SubnodeConfiguration updaterulesConfiguration = updateRulesConfigurations
					.get(0);
			List<SubnodeConfiguration> updateruleConfigurations = updaterulesConfiguration
					.configurationsAt(updateruleLabel);
			if (updateruleConfigurations.size() > 0) {
				log.debug(updateruleConfigurations.size()
						+ " updaterules found");
				Iterator<SubnodeConfiguration> iterator = updateruleConfigurations
						.iterator();
				while (iterator.hasNext()) {
					SubnodeConfiguration currentValueNode = iterator.next();
					CharacteristicIdUpdateRuleTuple result = manufactureOneUpdateRuleClass(
							currentValueNode, simulation);
					log.debug(String.format(
							"Updaterule %1$s loaded for characteristicindex "
									+ result.getCharacteristicId(), result
									.getUpdateRule().getClass().getName()));
					if (result.isComplete()) {
						updateRules.put(result.getCharacteristicId(), result
								.getUpdateRule());
					}
				}
			} else {
				throw new ConfigurationException(
						CDMConfigurationException.noSimulationUpdaterulesMessage);
			}
		} else {
			throw new ConfigurationException(
					CDMConfigurationException.noOrTooManySimulationUpdateruleMessage);
		}
		return updateRules;
	}

	private static CharacteristicIdUpdateRuleTuple manufactureOneUpdateRuleClass(
			SubnodeConfiguration currentValueNode, Simulation simulation)
			throws ConfigurationException {
		CharacteristicIdUpdateRuleTuple characteristicIdUpdateRuleTuple = null;
		try {
			String characteristicIdAsString = currentValueNode
					.getString(characteristicIdLabel);
			Integer characteristicId = Integer.decode(characteristicIdAsString);
			if (characteristicId != null) {
				characteristicIdUpdateRuleTuple = new CharacteristicIdUpdateRuleTuple();
				characteristicIdUpdateRuleTuple
						.setCharacteristicId(characteristicId);
			} else {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicIndex4UpdateRuleMessage);
			}
			String updateRuleClassName = currentValueNode
					.getString(classNameLabel);
			UpdateRuleMarker updateRuleInstance = loadAndCheckUpdateRuleClass(updateRuleClassName);
			if (updateRuleInstance == null) {
				throw new ConfigurationException(
						CDMConfigurationException.invalidUpdateRuleClassNameMessage + updateRuleClassName);
			}
			if (updateRuleInstance instanceof CharacteristicSpecific) {
				((CharacteristicSpecific) updateRuleInstance)
						.setCharacteristicId(characteristicId);
			}
			if (updateRuleInstance instanceof StepSizeSpecific) {
				((StepSizeSpecific) updateRuleInstance).setStepSize(simulation
						.getStepSize());
			}
			handleConfigurationFile(currentValueNode, updateRuleInstance);
			characteristicIdUpdateRuleTuple.setUpdateRule(updateRuleInstance);
			// if
			// (NeedsConfiguration.class.asSubclass((Class<UpdateRuleMarker>)updateRuleInstance.getClass())){
			// String configurationFileName = currentValueNode
			// .getString(configurationFileLabel);
			// }
		} catch (Exception e) {
			e.printStackTrace();
			if (!(e instanceof ConfigurationException)) {
				throw new ConfigurationException(
						CDMConfigurationException.noMessageMessage);
			} else {
				throw (ConfigurationException) e;
			}
		}
		return characteristicIdUpdateRuleTuple;
	}

	private static void handleConfigurationFile(SubnodeConfiguration currentValueNode, UpdateRuleMarker updateRuleInstance) throws ConfigurationException {
		if (updateRuleInstance instanceof ConfigurationEntryPoint) {
			String configurationFileName = currentValueNode
					.getString(configurationFileLabel);
			if ((configurationFileName == null)
					|| "".equals(configurationFileName)) {
				throw new ConfigurationException(
						String
								.format(
										CDMConfigurationException.noConfigurationFileNameConfigured4UpdateRuleMessage,
										updateRuleInstance.getClass()
												.getName()));
			}
			File configurationFile = new File(configurationFileName);
			if (!configurationFile.exists() || !configurationFile.isFile()
					|| !configurationFile.canRead()) {
				throw new ConfigurationException(
						String
								.format(
										CDMConfigurationException.configuredConfigurationFileName4UpdateRuleDoesNotExistOrCannotReadMessage,
										configurationFileName,
										updateRuleInstance.getClass()
												.getName()));
			}
			((ConfigurationEntryPoint)updateRuleInstance).loadConfigurationFile(configurationFile);
		}
	}

	static private UpdateRuleMarker loadAndCheckUpdateRuleClass(String className)
			throws ConfigurationException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		Class updateRuleClass = null;
		UpdateRuleMarker instance = null;
		try {
			updateRuleClass = classLoader.loadClass(className);
			Class updateRuleMarkerClass = classLoader
					.loadClass("nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker");
			boolean success = (updateRuleClass
					.asSubclass(updateRuleMarkerClass) != null);
			if (success) {
				instance = (UpdateRuleMarker) updateRuleClass.newInstance();
			}
			return instance;
		} catch (ClassNotFoundException e) {
			return instance;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ConfigurationException(
					CDMConfigurationException.noMessageMessage);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ConfigurationException(
					CDMConfigurationException.noMessageMessage);
		}
	}

	static class CharacteristicIdUpdateRuleTuple {
		private Integer characteristicId;

		private UpdateRuleMarker updateRule;

		public Integer getCharacteristicId() {
			return characteristicId;
		}

		public void setCharacteristicId(Integer characteristicId) {
			this.characteristicId = characteristicId;
		}

		public UpdateRuleMarker getUpdateRule() {
			return updateRule;
		}

		public void setUpdateRule(UpdateRuleMarker updateRule) {
			this.updateRule = updateRule;
		}

		public boolean isComplete() {
			return ((characteristicId != null) && (updateRule != null));
		}
	}
}
