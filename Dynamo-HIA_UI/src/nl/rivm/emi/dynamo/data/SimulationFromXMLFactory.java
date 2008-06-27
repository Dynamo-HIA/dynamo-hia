package nl.rivm.emi.dynamo.data;

import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.updaterules.UpdateRules4SimulationFromXMLFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimulationFromXMLFactory {
	static private Log log = LogFactory.getLog("nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory");

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

	public static Age manufacture(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		Age simulation = new Age();
		handleLabel(simulationConfiguration, simulation);
		handleTimestep(simulationConfiguration, simulation);
		handleRunMode(simulationConfiguration, simulation);
		handleStepsBetweenSaves(simulationConfiguration, simulation);
		handleStepsInRun(simulationConfiguration, simulation);
		handleStoppingCondition(simulationConfiguration, simulation);
		handlePopulation(simulationConfiguration, simulation);
		simulation.setUpdateRuleStorage(UpdateRules4SimulationFromXMLFactory
				.manufacture(simulationConfiguration, simulation));
		return simulation;
	}

	private static void handlePopulation(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
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
	}

	private static void handleStoppingCondition(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
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
	}

	private static void handleStepsInRun(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
		try {
			int stepsInRun = simulationConfiguration.getInt(stepsInRunLabel);
			log.debug("Setting stepsInRun to " + stepsInRun);
			simulation.setStepsInRun(stepsInRun);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationStepsInRunMessage);
		}
	}

	private static void handleStepsBetweenSaves(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
		try {
			int stepsBetweenSaves = simulationConfiguration
					.getInt(stepsBetweenSavesLabel);
			log.debug("Setting stepsBetweenSaves to " + stepsBetweenSaves);
			simulation.setStepsBetweenSaves(stepsBetweenSaves);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationStepsBetweenSavesMessage);
		}
	}

	private static void handleRunMode(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
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
	}

	private static void handleTimestep(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
		try {
			float timeStep = simulationConfiguration.getFloat(timestepLabel);
			log.debug("Setting timeStep to " + timeStep);
			simulation.setTimeStep(timeStep);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationTimestepMessage);
		}
	}

	private static void handleLabel(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
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
	}
}
