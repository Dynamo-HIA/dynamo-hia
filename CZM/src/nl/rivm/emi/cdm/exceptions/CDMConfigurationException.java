package nl.rivm.emi.cdm.exceptions;

import org.apache.commons.configuration.ConfigurationException;

public class CDMConfigurationException extends ConfigurationException {

	public CDMConfigurationException(String message) {
		super(message);
	}

	/* Characteristic */
	static public final String noFileMessage = "Configuration file missing or cannot be read: ";

	static public final String noCharacteristicsMessage = "No Characteristics tag found in configuration: ";

	static public final String noCharacteristicMessage = "No Characteristic tag found in configuration: ";

	static public final String noCharacteristicIndexMessage = "No index for Characteristic found in configuration: ";

	static public final String noCharacteristicLabelMessage = "No label for Characteristic found in configuration: ";

	static public final String noCharacteristicTypeMessage = "No type for Characteristic found in configuration: ";

	static public final String noCharacteristicPossibleValuesMessage = "No possiblevalues for Characteristic found in configuration: ";

	static public final String noCharacteristicPossibleValueValueMessage = "No value in possiblevalues for Characteristic found in configuration: ";

	/* Simulation */
	static public final String noSimulationLabelMessage = "No label for Simulation found in configuration: ";

	static public final String noSimulationTimestepMessage = "No timestep for Simulation found in configuration: ";

	static public final String noSimulationRunmodeMessage = "No runmode for Simulation found in configuration: ";

	static public final String noSimulationStepsBetweenSavesMessage = "No stepsbetweensaves for Simulation found in configuration: ";

	static public final String noSimulationStepsInRunMessage = "No stepsinrun for Simulation found in configuration: ";

	static public final String noSimulationStoppingConditionMessage = "No stoppingcondition for Simulation found in configuration: ";

	static public final String noSimulationPopulationMessage = "No pop for Simulation found in configuration: ";

	static public final String noSimulationUpdaterulesMessage = "No updaterules for Simulation found in configuration: ";

	static public final String noSimulationUpdateruleMessage = "No updaterule for Simulation found in configuration: ";

	/* Generator */
	static public final String noGeneratorLabelMessage = "No label for Generator found in configuration: ";

	static public final String noGeneratorPopulationSizeMessage = "No populationsize for Generator found in configuration: ";

	static public final String noGeneratorRngClassNameMessage = "No rngclassname for Generator found in configuration: ";

	static public final String noGeneratorRngSeedMessage = "No rngseed for Generator found in configuration: ";

	static public final String noGeneratorCharacteristicsMessage = "No characteristics for Generator found in configuration: ";

	static public final String noGeneratorCharacteristicIdMessage = "No (characteristic) id for Generator found in configuration: ";

	static public final String invalidGeneratorRngClassNameMessage = "Invalid rngclassname for Generator found in configuration: ";
}
