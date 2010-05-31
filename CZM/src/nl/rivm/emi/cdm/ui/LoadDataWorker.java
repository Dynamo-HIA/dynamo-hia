package nl.rivm.emi.cdm.ui;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.file.csv.PopulationCsvReader;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoadDataWorker extends SwingWorker<Simulation, String> {
	Log log = LogFactory.getLog(getClass().getSimpleName());

	private ActionListener callbackWindow = null;
	File simulationDirectory = null;
	Simulation simulation = null;

	public LoadDataWorker(ActionListener callbackWindow,
			File simulationDirectory) {
		super();
		log.info("Constructing.");
		this.callbackWindow = callbackWindow;
		this.simulationDirectory = simulationDirectory;
	}

	@Override
	public Simulation doInBackground() {
		publish("doInBackground() called.");
		log.info("doInBackground() called.");
		boolean charsLoaded = loadCharacteristics();
		if (charsLoaded) {
			simulation = loadSimulation(simulationDirectory);
			int stepsInRun = simulation.getStepsInRun();
			Population population = loadPopulation(stepsInRun);
			if (population != null) {
				simulation.setPopulation(population);
			}
		}
		return simulation;
	}

	@Override
	protected void process(List<String> chunks) {
		for (String chunk : chunks) {
			log.info("VIA PUBLISH: " + chunk);
		}
		super.process(chunks);
	}

	@Override
	public void done() {
		log.info("done() called.");
		((LoadDataInterface) callbackWindow).finishLoadData(simulation);
	}

	private boolean loadCharacteristics() {
		File characteristicsConfigurationFile = findCharacteristicsConfiguration(simulationDirectory);
		boolean success = false;
		try {
			if (simulationDirectory.isDirectory()) {
				CharacteristicsXMLConfiguration cxc = new CharacteristicsXMLConfiguration(
						characteristicsConfigurationFile);
				if (cxc != null) {
					success = true;
					log.info("Success - Characteristics loaded from: "
							+ characteristicsConfigurationFile
									.getAbsolutePath());
				}
			}
			return success;
		} catch (ConfigurationException e) {
			log.error("Characteristics could not be loaded from: "
					+ characteristicsConfigurationFile.getAbsolutePath());
			return success;
		}
	}

	private Simulation loadSimulation(File simulationDirectory) {
		log.info("Entering loadSimulation() for: "
				+ simulationDirectory.getAbsolutePath());
		File simulationConfigurationFile = null;
		Simulation mySimulation = null;
		try {
			if (simulationDirectory.isDirectory()) {
				simulationConfigurationFile = findSimulationConfiguration(simulationDirectory);
				log.info("Found simulation configurationfile at: "
						+ simulationConfigurationFile.getAbsolutePath());
				if (simulationConfigurationFile != null) {
					XMLConfiguration simulationConfigurationObject;
					simulationConfigurationObject = new XMLConfiguration(
							simulationConfigurationFile);
					mySimulation = SimulationFromXMLFactory
							.manufacture_DOMPopulationTree(
									simulationConfigurationObject, false);
					if (mySimulation != null) {
						log.info("Success - Simulation loaded from: "
								+ simulationDirectory.getAbsolutePath());
					} else {
						log
								.info("Failure - Simulation could not be loaded from: "
										+ simulationDirectory.getAbsolutePath());
					}
				} else {
					log
							.error("Error - SimulationConfigurationFile could not be found in: "
									+ simulationDirectory.getAbsolutePath());
				}
			} else {
				log.error("Error - SimulationDirectory "
						+ simulationDirectory.getAbsolutePath()
						+ " is not a directory.");
			}
			return mySimulation;
		} catch (ConfigurationException e) {
			e.printStackTrace();
			log.error("Error - Simulation could not be loaded from: "
					+ simulationConfigurationFile.getAbsolutePath());
			return mySimulation;
		} catch (Throwable e) {
			e.printStackTrace();
			log.error("Error - Loading Simulation from: "
					+ simulationConfigurationFile.getAbsolutePath()
					+ " blew up.");
			return mySimulation;
		}
	}

	private Population loadPopulation(int stepsInRun) {
		Population population = null;
		try {
			File populationConfigurationFile = findPopulationConfiguration(simulationDirectory);
			PopulationCsvReader popReader = new PopulationCsvReader(
					populationConfigurationFile);
			if (popReader.checkFileAndHeadersAgainstCharacteristics()) {
				log
						.info("Success - Population headers and characteristics check out.");
				population = popReader.readPopulation(stepsInRun);
				if (population != null) {
					log.info("Success - Population loaded from: "
							+ populationConfigurationFile.getAbsolutePath());
				} else {
					log.info("Failure - Population could not be loaded from: "
							+ populationConfigurationFile.getAbsolutePath());

				}
			} else {
				log
						.info("Failure - Population headers and characteristics do not match.");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			log.error("loadInitialPopulationFiles() blew up.");
		}
		return population;
	}

	private File findCharacteristicsConfiguration(File simulationDirectory) {
		File popDirContentItem = null;
		File[] simDirContent = simulationDirectory.listFiles();
		for (int count = 0; count < simDirContent.length; count++) {
			File simDirContentItem = simDirContent[count];
			if ((simDirContentItem.isDirectory())
					&& ("Initial Population".equalsIgnoreCase(simDirContentItem
							.getName()))) {
				File[] popDirContent = simDirContentItem.listFiles();
				for (int popCount = 0; popCount < popDirContent.length; popCount++) {
					popDirContentItem = popDirContent[popCount];
					if ((popDirContentItem.isFile())
							&& ("characteristics.xml"
									.equalsIgnoreCase(popDirContentItem
											.getName()))) {
						break;
					} else {
						popDirContentItem = null;
					}
				}
			}
		}
		return popDirContentItem;
	}

	private File findPopulationConfiguration(File simulationDirectory) {
		File popDirContentItem = null;
		File[] simDirContent = simulationDirectory.listFiles();
		for (int count = 0; count < simDirContent.length; count++) {
			File simDirContentItem = simDirContent[count];
			if ((simDirContentItem.isDirectory())
					&& ("Initial Population".equalsIgnoreCase(simDirContentItem
							.getName()))) {
				File[] popDirContent = simDirContentItem.listFiles();
				for (int popCount = 0; popCount < popDirContent.length; popCount++) {
					popDirContentItem = popDirContent[popCount];
					if ((popDirContentItem.isFile())
							&& ("population.csv"
									.equalsIgnoreCase(popDirContentItem
											.getName()))) {
						break;
					} else {
						popDirContentItem = null;
					}
				}
			}
		}
		return popDirContentItem;
	}

	private File findSimulationConfiguration(File simulationDirectory) {
		File simDirContentItem = null;
		File[] simDirContent = simulationDirectory.listFiles();
		for (int count = 0; count < simDirContent.length; count++) {
			simDirContentItem = simDirContent[count];
			log.debug("Searching Simulation configuration: "
					+ simDirContentItem.getAbsolutePath());
			if (simDirContentItem.isFile()
					&& "configuration.xml".equalsIgnoreCase(simDirContentItem
							.getName())) {
				log.debug("Found Simulation configuration: "
						+ simDirContentItem.getName());
				break;
			} else {
				simDirContentItem = null;
			}
		}
		return simDirContentItem;
	}

	/**
	 * Adapted from runPopulations from DynamoHIA-estimation.
	 * 
	 * @param pop
	 * @param simFileName
	 * @return
	 * @throws Exception
	 */
	private Simulation loadSimulation(Population pop, String simFileName)
			throws Exception {

		;
		// Assemble the simulation file name
		XMLConfiguration simulationConfiguration;
		String simulationFilePath = simFileName + ".xml";
		Simulation simulation = null;
		// log.debug("simulationFilePath" + simulationFilePath);

		File simulationConfigurationFile;
		simulationConfigurationFile = new File(simulationFilePath);
		if (!(CharacteristicsConfigurationMapSingleton.getInstance().size() > 1)) {
			throw new DynamoConfigurationException(
					"More than 1 disease needs to be configured.");
		}
		// calculate frequency of risk factor values during simulation

		if (simulationConfigurationFile.exists()) {
			simulationConfiguration = new XMLConfiguration(
					simulationConfigurationFile);
			// log.info("simulationconfuration made for scenario "
			// + scennum);

			/**
			 * TODO: VALIDATION IS FOR FUTURE USE NICE TO HAVE FEATURE KEEP IT
			 * IN THE CODE The following schemas are not validated: sim.xsd
			 */
			if (!"sim".equals(simulationConfiguration.getRootElementName())) {
				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor
				// (also
				// calls load()).
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				simulationConfiguration.clear();

				// Validate the xml by xsd schema
				simulationConfiguration.setValidating(true);
				simulationConfiguration.load();
			}

			/* read the configuration file */
			/*
			 * the false means that the initial population should not be read
			 * from xml file
			 */
			/* deze stap neemt veel tijd: daarom maar één maal doen */
			if (simulation.getLabel().equals("Not initialized"))
				simulation = SimulationFromXMLFactory
						.manufacture_DOMPopulationTree(simulationConfiguration,
								false);
			/*
			 * set the initial population to the population (taken earlier from
			 * the Modelparameter object
			 */
			simulation.setPopulation(pop);

			// log.info("simulationFile loaded for scenario " +
			// scennum);

			if (pop == null)
				throw new CDMConfigurationException("no population found.");
			// log.info("starting run for population " + scennum);
			/*
			 * run the simulation for this population This is done by the new
			 * Simulation Object DynamoSimulation that is a shell around the
			 * "old" CDM Simulation Object (it contains the CDM-object as a
			 * field) as for instance a progress bar could be added that way
			 */

			// runScenario(scennum, pr);
		}

		return simulation;
	}
}
