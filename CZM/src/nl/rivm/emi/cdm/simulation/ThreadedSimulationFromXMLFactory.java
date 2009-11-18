package nl.rivm.emi.cdm.simulation;

/**
 * Near clone of the non-threaded version. Should be drop-in compatible.
 */
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.rules.update.UpdateRules4SimulationFromXMLFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadedSimulationFromXMLFactory extends Thread {
	private Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory");

	/**
	 * Currently implemented structure. <?xml version="1.0" encoding="UTF-8"?>
	 * <sim lb="netalsof"> <timestep>nnn</timestep>
	 * <runmode>longitudinal</runmode>
	 * <stepsbetweensaves>nnnnn</stepsbetweensaves>
	 * <stepsinrun>nnnn</stepsinrun> <stoppingcondition/> <pop lb="manipel"/>
	 * <updaterules> <updaterule>classname</updaterule>
	 * <updaterule>classname</updaterule> <updaterule>classname</updaterule>
	 * <updaterule>classname</updaterule> </updaterules> </sim>
	 */
	public final String simulationLabel = "sim";

	public final String labelLabel = "lb";

	public final String timestepLabel = "timestep";

	public final String runmodeLabel = "runmode";

	public final String stepsBetweenSavesLabel = "stepsbetweensaves";

	public final String stepsInRunLabel = "stepsinrun";

	public final String stoppingConditionLabel = "stoppingcondition";

	public final String populationLabel = "pop";
	HierarchicalConfiguration simulationConfiguration = null;
	Simulation theSimulation = null;
	ConfigurationException anException = null;

	static public Simulation manufacture_DOMPopulationTree(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		ThreadedSimulationFromXMLFactory factory = new ThreadedSimulationFromXMLFactory();
		factory.init(simulationConfiguration);
		factory.setPriority(MAX_PRIORITY);
		factory.run();
		if (factory.getException() != null) {
			throw factory.getException();
		}
		return factory.getResult();
	}

	public void init(HierarchicalConfiguration simulationConfiguration) {
		this.simulationConfiguration = simulationConfiguration;
	}

	synchronized public void run() {
		try {
			theSimulation = internal_manufacture_DOMPopulationTree(simulationConfiguration);
		} catch (ConfigurationException e) {
			anException = e;
		}
	}

	public Simulation getResult() {
		return theSimulation;
	}

	public ConfigurationException getException() {
		return anException;
	}

	private Simulation internal_manufacture_DOMPopulationTree(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		Simulation simulation = new Simulation();
		handleLabel(simulationConfiguration, simulation);
		handleTimestep(simulationConfiguration, simulation);
		handleRunMode(simulationConfiguration, simulation);
		handleStepsBetweenSaves(simulationConfiguration, simulation);
		handleStepsInRun(simulationConfiguration, simulation);
		handleStoppingCondition(simulationConfiguration, simulation);
		handlePopulation_DOMTree(simulationConfiguration, simulation);
		simulation.setUpdateRuleStorage(UpdateRules4SimulationFromXMLFactory
				.manufacture(simulationConfiguration, simulation));
		return simulation;
	}

	public Simulation manufacture_Population_StAX(
			HierarchicalConfiguration simulationConfiguration)
			throws ConfigurationException {
		Simulation simulation = new Simulation();
		handleLabel(simulationConfiguration, simulation);
		handleTimestep(simulationConfiguration, simulation);
		handleRunMode(simulationConfiguration, simulation);
		handleStepsBetweenSaves(simulationConfiguration, simulation);
		handleStepsInRun(simulationConfiguration, simulation);
		handleStoppingCondition(simulationConfiguration, simulation);
		handlePopulation_StAX(simulationConfiguration, simulation);
		simulation.setUpdateRuleStorage(UpdateRules4SimulationFromXMLFactory
				.manufacture(simulationConfiguration, simulation));
		return simulation;
	}

	private void handlePopulation_DOMTree(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
		try {
			String populationFileName = simulationConfiguration
					.getString(populationLabel);
			if (populationFileName == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noSimulationPopulationMessage);
			}
			simulation.setPopulationByFileName_DOM(populationFileName);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationPopulationMessage);
		}
	}

	private void handlePopulation_StAX(
			HierarchicalConfiguration simulationConfiguration,
			Simulation simulation) throws ConfigurationException {
		try {
			String populationFileName = simulationConfiguration
					.getString(populationLabel);
			if (populationFileName == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noSimulationPopulationMessage);
			}
			simulation.setPopulationByFileName_StAX(populationFileName);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationPopulationMessage);
		}
	}

	private void handleStoppingCondition(
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

	private void handleStepsInRun(
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

	private void handleStepsBetweenSaves(
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

	private void handleRunMode(
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

	private void handleTimestep(
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

	private void handleLabel(HierarchicalConfiguration simulationConfiguration,
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
