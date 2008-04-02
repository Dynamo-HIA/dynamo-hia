package nl.rivm.emi.cdm.simulation;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

public class SimulationFromXMLFactory {
	/**
	 * Currently implemented structure. <?xml version="1.0" encoding="UTF-8"?>
	 * <sim lb="netalsof"> <timestep>nnn</timestep> <runmode>longitudinal</runmode>
	 * <stepsbetweensaves>nnnnn</stepsbetweensaves> <stepsinrun>nnnn</stepsinrun>
	 * <stoppingcondition/> <pop lb="manipel"/> <updaterules>
	 * <updaterule>classname</updaterule> <updaterule>classname</updaterule>
	 * <updaterule>classname</updaterule> <updaterule>classname</updaterule>
	 * </updaterules> </sim>
	 */
	public static final String simulationLabel = "sim";

	public static final String labelLabel = "lb";

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
			String label = simulationConfiguration.getString(labelLabel);
			if (label == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noSimulationLabelMessage);
			}
			simulation.setLabel(label);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationLabelMessage);
		}
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
			simulation.setRunMode(runmode);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationRunmodeMessage);
		}
		try {
			int stepsBetweenSaves = simulationConfiguration
					.getInt(stepsBetweenSavesLabel);
			simulation.setStepsBetweenSaves(stepsBetweenSaves);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationStepsBetweenSavesMessage);
		}
		try {
			int stepsInRun = simulationConfiguration.getInt(stepsInRunLabel);
			simulation.setStepsInRun(stepsInRun);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationStepsInRunMessage);
		}
		try {
			String stoppingCondition = simulationConfiguration
					.getString(stoppingConditionLabel);
			if (stoppingCondition == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noSimulationStoppingConditionMessage);
			}
			simulation.setStoppingCondition(stoppingCondition);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationStoppingConditionMessage);
		}
		try {
			String populationFileName = simulationConfiguration
					.getString(populationLabel);
			if (populationFileName == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noSimulationPopulationMessage);
			}
			simulation.setPopulationByFileName(populationFileName);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationPopulationMessage);
		}
		List<SubnodeConfiguration> updaterulesConfigurations = simulationConfiguration
				.configurationsAt(updaterulesLabel);
		if (updaterulesConfigurations.size() == 1) {
			SubnodeConfiguration updaterulesConfiguration = updaterulesConfigurations
					.get(0);
			List<SubnodeConfiguration> updateruleConfigurations = updaterulesConfiguration
					.configurationsAt(updateruleLabel);
			if (updateruleConfigurations.size() > 0) {
				Iterator<SubnodeConfiguration> iterator = updateruleConfigurations
						.iterator();
				while (iterator.hasNext()) {
					SubnodeConfiguration currentValueNode = iterator.next();
					String updateruleClassName = currentValueNode
							.getString(updateruleLabel);
				}

			} else {
				throw new ConfigurationException(
						CDMConfigurationException.noSimulationUpdateruleMessage);
			}
		} else {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationUpdaterulesMessage);
		}
		return simulation;
	}
}
