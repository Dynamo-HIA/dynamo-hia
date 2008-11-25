package nl.rivm.emi.cdm.exceptions;

import org.apache.commons.configuration.ConfigurationException;

public class CDMConfigurationException extends ConfigurationException {

	public CDMConfigurationException(String message) {
		super(message);
	}

	/* Configuration in general. */
	static public final String noMessageMessage = "No sensible message yet.";

	static public final String wrongClassMessage = "Class of type SubnodeConfiguration expected at this point.";

	public static final String noConfigurationTagMessage = "Configuration doesn't contain the tag searched.";
	/* Characteristic */
	static public final String noFileMessage = "Configuration file missing or cannot be read: ";

	static public final String noCharacteristicsMessage = "No Characteristics tag found in configuration: ";

	static public final String noCharacteristicMessage = "No Characteristic tag found in configuration: ";

	static public final String noCharacteristicIndexMessage = "No index for Characteristic found in configuration: ";

	static public final String noCharacteristicLabelMessage = "No label for Characteristic found in configuration: ";

	public static final String noUpdateCharIDMessage = "No message in CDMConfigurationException.noUpdateCharIDMessage";
	
	/* added by hendriek */
	static public final String noNumberofValuesLabelMessage = "No value for number of elements found in for compound Characteristic in configuration: ";

	/* Characteristic types */
	static public final String noCharacteristicTypeMessage = "No type for Characteristic found in configuration: ";

	static public final String noCharacteristicPossibleValuesMessage = "No possiblevalues for Characteristic found in configuration: ";

	static public final String noCharacteristicPossibleValueValueMessage = "No value in possiblevalues for Characteristic found in configuration: ";

	static public final String characteristicsConfigurationNotInitializedMessage = "Characteristics configuration has not been initialized.";

	static public final String noCharacteristicLimitsMessage = "No possiblevalues for Characteristic found in configuration: ";

	static public final String noCharacteristicLimitsValueMessage = "No value in possiblevalues for Characteristic found in configuration: ";

	
	
	static public final String wrongCharacteristicLowerLimitConfigurationFormatMessage = "Lower limit configuration for Characteristic no correct Float format.";

	static public final String wrongCharacteristicUpperLimitConfigurationFormatMessage = "Upper limit configuration for Characteristic no correct Float format.";

	/* Simulation */
	static public final String noSimulationLabelMessage = "No label for Simulation found in configuration: ";

	static public final String noSimulationTimestepMessage = "No timestep for Simulation found in configuration: ";

	static public final String noSimulationRunmodeMessage = "No runmode for Simulation found in configuration: ";

	static public final String noSimulationStepsBetweenSavesMessage = "No stepsbetweensaves for Simulation found in configuration: ";

	static public final String noSimulationStepsInRunMessage = "No stepsinrun for Simulation found in configuration: ";

	static public final String noSimulationStoppingConditionMessage = "No stoppingcondition for Simulation found in configuration: ";

	static public final String noSimulationPopulationMessage = "No pop for Simulation found in configuration: ";

	static public final String noSimulationUpdaterulesMessage = "No \"updaterule\" elements for Simulation found in configuration: ";

	static public final String noOrTooManySimulationUpdateruleMessage = "No or too many \"updaterules\" elements for Simulation found in configuration: ";

	/* Generator */
	static public final String noGeneratorLabelMessage = "No label for Generator found in configuration: ";

	static public final String noGeneratorPopulationSizeMessage = "No populationsize for Generator found in configuration: ";

	static public final String noGeneratorRngClassNameMessage = "No rngclassname for Generator found in configuration: ";

	static public final String noGeneratorRngSeedMessage = "No rngseed for Generator found in configuration: ";

	static public final String noGeneratorCharacteristicsMessage = "No characteristics for Generator found in configuration: ";

	static public final String noGeneratorCharacteristicIdMessage = "No (characteristic) id for Generator found in configuration: ";

	static public final String invalidGeneratorRngClassNameMessage = "Invalid rngclassname for Generator found in configuration: ";

	/* Update Rules */
	public static final String invalidUpdateRuleClassNameMessage = "Invalid classname for UpdateRule found in configuration: ";

	public static final String invalidUpdateRuleClassTypeMessage = "Class of UpdateRule is not a subtype of UpdateRuleMarker.";

	static public final String noCharacteristicIndex4UpdateRuleMessage = "No characteristic index found in simulation updaterule configuration: ";

	static public final String noConfigurationFileNameConfigured4UpdateRuleMessage = "No configuration filename configured for updaterule %1$s that needs one.";

	static public final String configuredConfigurationFileName4UpdateRuleDoesNotExistOrCannotReadMessage = "Configured configuration file %1$s for updaterule %2$s does not exist or cannot be read.";

	static public final String wrongConfigurationFile4UpdateRuleMessage = "Configuration file does not have the format updaterule %1$s expects.";

	public static final String invalidUpdateRuleConfigurationFileFormatMessage = "Invalid configuration file %1$s for UpdateRule %2$s.";
	public static final String noUpdateTransitionMatrixFileNameMessage = "No message for CDMConfigurationException.noUpdateTransitionMatrixFileNameMessage yet.";
}
