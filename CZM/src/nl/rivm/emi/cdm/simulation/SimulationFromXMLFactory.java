package nl.rivm.emi.cdm.simulation;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicType;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

public class SimulationFromXMLFactory {
	/**
	 * Currently implemented structure.
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <sim lb="netalsof">
	 *   <timestep>nnn</timestep>
	 *   <runmode>longitudinal</runmode>
	 *   <stepsbetweensaves>nnnnn</stepsbetweensaves>
	 *   <stepsinrun>nnnn</stepsinrun>
	 *   <stoppingcondition/>
	 *   <pop lb="manipel"/>
	 *   <updaterules>
	 *      <updaterule>classname</updaterule>
	 *      <updaterule>classname</updaterule>
	 *      <updaterule>classname</updaterule>
	 *      <updaterule>classname</updaterule>
	 *   </updaterules>
	 * </sim>
	 */
	public static final String simulationLabel = "sim";

	public static final String timestepLabel = "timestep";

	public static final String runmodeLabel = "runmode";

	public static final String stepsBetweenSavesLabel = "stepsbetweensaves";

	public static final String stepsInRunLabel = "stepsinrun";

	public static final String stoppingConditionLabel = "stoppingcondition";

	public static final String populationLabel = "pop";

	public static final String updaterulesLabel = "updaterules";

	public static final String updateruleLabel = "updaterule";

	public static Simulation manufacture(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		Simulation simulation = new Simulation();
		try {
			 float timeStep = simulationConfiguration.getFloat(timestepLabel);
			simulation.setTimeStep(timeStep);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationTimestepMessage);
		}
		try {
			String runmode = simulationConfiguration.getString(runmodeLabel);
			if (runmode == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noSimulationRunmodeMessage);
			}
			simulation.setLabel(label);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicLabelMessage);
		}
		try {
			String type = simulationConfiguration.getString(typeLabel);
			if (type == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicTypeMessage);
			}
			simulation.setType(type);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicTypeMessage);
		}
		if (!CharacteristicType.continuousTypeString.equals(simulation
				.getType())) {
			List<SubnodeConfiguration> possibleValuesConfigurations = simulationConfiguration
					.configurationsAt(possibleValuesLabel);
			if (possibleValuesConfigurations.size() == 1) {
				SubnodeConfiguration possibleValuesConfiguration = possibleValuesConfigurations
						.get(0);
				List<SubnodeConfiguration> valueConfigurations = possibleValuesConfiguration
						.configurationsAt(valueLabel);
				if (valueConfigurations.size() > 0) {
					Iterator<SubnodeConfiguration> iterator = valueConfigurations
							.iterator();
					while (iterator.hasNext()) {
						SubnodeConfiguration currentValueNode = iterator.next();
						String value = currentValueNode.getString(valueLabel);
					}

				} else {
					throw new ConfigurationException(
							CDMConfigurationException.noCharacteristicValueMessage);
				}
			} else {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicPossibleValuesMessage);
			}
		}
		return simulation;
	}

}
