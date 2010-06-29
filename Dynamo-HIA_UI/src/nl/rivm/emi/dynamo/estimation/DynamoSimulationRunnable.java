package nl.rivm.emi.dynamo.estimation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import nl.rivm.emi.cdm.DomLevelTraverser;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.CSVPopulationWriter;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;
import nl.rivm.emi.dynamo.output.DynamoOutputFactory;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DynamoSimulationRunnable extends DomLevelTraverser {

	private static final long serialVersionUID = 6377558357121377722L;

	private Log log = LogFactory.getLog(getClass().getName());

	private Simulation[] simulation;

	// private Shell parentShell;
	DynSimRunPRInterface pr = null;
	String errorMessage = null;
	String preCharConfig;
	String simName;
	String baseDir;
	private boolean runInOne = true;
	/*
	 * model parameter is an object containing the parameters of the model and
	 * the initial population. The parameters are written to XML files that are
	 * input-parameters for the model, and the population is extracted directly
	 * and fed into the simulation. There is also an option to write the
	 * intitial population to XML, but this is not used here.
	 */
	private ModelParameters p;
	/*
	 * object with all information needed both before and after the running of
	 * the simulation
	 */

	private ScenarioInfo scen;

	/**
	 * @param prObject
	 *            : object containing the logger to which messages are printed
	 * @param simName
	 *            : simulation name
	 * @param baseDir
	 *            : base directory
	 * @throws DynamoInconsistentDataException
	 */
	public DynamoSimulationRunnable(
	/* Shell parentShell */DynSimRunPRInterface prObject, String simName,
			String baseDir) throws DynamoInconsistentDataException {
		super();
		// this.parentShell = parentShell;
		pr = prObject;
		try {
			this.errorMessage = null;
			configureSimulation(simName, baseDir);

		} catch (DynamoInconsistentDataException e) {
			wrapAndThrowErrorMessage(e, simName);
			e.printStackTrace();
			errorMessage = "model can not be run due to inconsistencies in the data. \nPlease correct.";
		} catch (DynamoConfigurationException e) {
			wrapAndThrowErrorMessage(e, simName);
			errorMessage = "model can not be run due to configuration errors";
			e.printStackTrace();
		}
	}

	/*
	 * public static void runDynamo () { Display display = new Display (); Shell
	 * shell = new Shell (display); ProgressBar bar = new ProgressBar (shell,
	 * SWT.SMOOTH); bar.setBounds (10, 10, 200, 32); shell.open (); for (int
	 * i=0; i<=bar.getMaximum (); i++) { try {Thread.sleep (100);} catch
	 * (Throwable th) {} bar.setSelection (i); } while (!shell.isDisposed ()) {
	 * if (!display.readAndDispatch ()) display.sleep (); } display.dispose ();
	 * }
	 */

	/**
	 * @param simName2
	 *            , String baseDir
	 * @throws DynamoInconsistentDataException
	 * @throws DynamoConfigurationException
	 * 
	 * 
	 */
	private void configureSimulation(String simName, String baseDir)
			throws DynamoConfigurationException,
			DynamoInconsistentDataException {

		/*
		 * make an instance of the basedirectory object that is a singleton
		 * containing the basedirectory
		 */
		// BaseDirectory B = BaseDirectory
		// .getInstance(baseDir);
		this.baseDir = baseDir;
		this.simName = simName;
		/*
		 * make the strings with the filenames of the files that are read by CDM
		 * These have fixed names, and are in the directory with the
		 * simulationname
		 */

		// log.debug("this.baseDir" + this.baseDir);
		// log.debug("this.simName" + this.simName);
		/*
		 * preCharConfig is a file that contains the configuration of the
		 * characteristics of each simulated individual
		 */
		String directoryName = this.baseDir + File.separator + "Simulations"
				+ File.separator + this.simName;
		// log.debug("directoryName" + directoryName);
		preCharConfig = directoryName + File.separator + "modelconfiguration"
				+ File.separator + "charconfig.XML";
		/*
		 * simFileName is a file that contains the configuration of the
		 * simulation
		 */

		// estimate the parameters
		p = new ModelParameters(this.baseDir);

		scen = p.estimateModelParameters(this.simName/* , this.parentShell */,
				pr);
		/*
		 * } catch (DynamoConfigurationException e3) { displayErrorMessage(e3,
		 * null); // log.fatal(e3.getMessage()); e3.printStackTrace(); } catch
		 * (DynamoInconsistentDataException e) { // TODO Auto-generated catch
		 * blockdisplayErrorMessage(e3); // log.fatal(e.getMessage());
		 * displayInconsistentDataMessage(e); e.printStackTrace(); }
		 */
		// " .XML";
	}

	/**
	 * @param simName
	 * @param preCharConfig
	 * @param simFileName
	 */
	public void run() {

		String simulationFilePath = null;
		try {
			if (this.errorMessage != null)
				throw new DynamoConfigurationException(this.errorMessage);
			String directoryName = baseDir + File.separator + "Simulations"
					+ File.separator + simName;
			String simFileName = directoryName + File.separator
					+ "modelconfiguration" + File.separator + "simulation";
			/*
			 * simulation is an object that contains the population that is
			 * simulated and carries out the simulation
			 */

			simulation = new Simulation[scen.getNPopulations()];
			for (int n = 0; n < scen.getNPopulations(); n++)
				simulation[n] = new Simulation();

			// log.info("ModelParameters estimated and written");

			File multipleCharacteristicsFile = new File(preCharConfig);
			// log.info("charFile made.");
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			// log.info("charFile handled.");
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();

			// log.info("charmap made");
			/*
			 * array pop contains the stimulated populations for the different
			 * scenario's calculate the number of populations that are needed to
			 * carry out this simulation
			 */

			// TODO: ONLY INI output = new DynamoOutputFactory(scen, pop);
			InitialPopulationFactory popFactory = new InitialPopulationFactory(
					p, scen, pr);

			int nIndividuals = getNIndividuals(popFactory);
			int agemax = scen.getMaxSimAge();
			if (agemax > 95)
				agemax = 95;
			int agemin = scen.getMinSimAge();
			Population[] pop = null;
			DynamoOutputFactory output = new DynamoOutputFactory(scen);
			/* for test: made 10 time lower: TODO change 200 in 2 later */
			/*
			 * with test1-12 (mostly nsim=2 or 10) this is 136 seconds in 1 run,
			 * and 160 second when running all runs separately by age and sex;
			 * so not that much difference
			 */
			if (nIndividuals > (agemax - agemin) * 2 * scen.getSimPopSize()) {
				pop = popFactory.manufactureInitialPopulation(agemin, agemax,
						0, 1, 1, 1, false);
				if (agemin == 20) {
					int stop = 0;
					stop++;
				}
				if (scen.isWithNewBorns()) {
					Population[] newborns = popFactory
							.manufactureInitialPopulation(0, 0, 0, 1, 1, scen
									.getYearsInRun(), true);

					this.runInOne = true;
					for (int p = 0; p < pop.length; p++) {
						newborns[p].addAll(pop[p]);
						pop[p] = newborns[p];

					}
				}
				output = runPopulation(pop, simFileName, output);
				log.fatal("population has run");
			} else {
				this.runInOne = false;

				ProgressIndicatorInterface pii = pr
						.createProgressIndicator("Simulation of scenarios "
								+ "running ....");

				int currentProgressIndicator = 1;
				// bar.setMaximum(size / step);
				pii.setMaximum(agemax - agemin + 1);
				if (scen.isWithNewBorns())
					pii.setMaximum(agemax - agemin + 1 + scen.getYearsInRun());
				for (int a = agemin; a <= agemax; a++) {
					for (int g = 0; g < 2; g++) {
						pop = popFactory.manufactureInitialPopulation(a, a, g,
								g, 1, 1, false);
						output = runPopulation(pop, simFileName, output);
					}
					pii.update(currentProgressIndicator);
					currentProgressIndicator++;
				}
				if (scen.isWithNewBorns()) {
					for (int generation = 1; generation <= scen.getYearsInRun(); generation++) {
						for (int g = 0; g < 2; g++) {
							pop = popFactory.manufactureInitialPopulation(0, 0,
									g, g, generation, generation, true);
							output = runPopulation(pop, simFileName, output);
						}
						pii.update(currentProgressIndicator);
						currentProgressIndicator++;
					}
				}
				pii.dispose();
			}

			/** * finalize output */

			output.makeArraysWithPopulationNumbers();
			log.fatal("output object finalized");
			// log.fatal("Starting to write populations");
			// for (int npop = 0; npop < nPopulations; npop++) {
			// String iniPopFileName = directoryName + File.separator
			// + "modelconfiguration" + File.separator + "initialPop_"
			// + npop;
			// CSVPopulationWriter.writePopulation(iniPopFileName, pop[npop],
			// 0);
			// log.fatal("Written population #" + npop);
			// }

			/* display the output */

			ScenarioParameters scenParms = null;
			scenParms = new ScenarioParameters(scen);
			/* make the output screen */
			String currentPath = this.baseDir + File.separator + "simulations"
					+ File.separator + simName + File.separator + "results";
			// Shell parentShell = pr.getShell();
			// if (parentShell != null) {
			// new Output_UI(parentShell, output, scenParms, currentPath);
			// }
			/* temporarily disabled for testing */
			pr.createOutput(output, scenParms, currentPath);
			log.fatal("output created");
			/* write the output object to a file */
			persistDynamoOutputFactory(output);
			log.fatal("output written");
			persistScenarioInfo(scenParms);
			// persistPopulationArray(pop);
		} catch (DynamoConfigurationException e) {
			this.displayErrorMessage(e, simulationFilePath);
			e.printStackTrace();
		} catch (ConfigurationException e) {
			displayErrorMessage(e, simulationFilePath);
		} catch (Exception e) {
			displayErrorMessage(e, simulationFilePath);
			e.printStackTrace();
		}
	}

	private DynamoOutputFactory runPopulation(Population[] pop,
			String simFileName, DynamoOutputFactory output) throws Exception {

		;
		// Assemble the simulation file name
		XMLConfiguration simulationConfiguration;
		String simulationFilePath = simFileName + ".xml";
		// log.debug("simulationFilePath" + simulationFilePath);

		/* run the simulation for each population */
		for (int scennum = 0; scennum < scen.getNPopulations(); scennum++) {
			File simulationConfigurationFile;
			if (scennum != 0)
				simulationFilePath = simFileName + "_scen_" + scennum + ".xml";

			simulationConfigurationFile = new File(simulationFilePath);
			// log.info("simulationFile made for scenario " + scennum);

			// assertTrue(CharacteristicsConfigurationMapSingleton
			// .getInstance().size() > 1);
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
				 * TODO: VALIDATION IS FOR FUTURE USE NICE TO HAVE FEATURE KEEP
				 * IT IN THE CODE The following schemas are not validated:
				 * sim.xsd
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
				 * the false means that the initial population should not be
				 * read from xml file
				 */
				/* deze stap neemt veel tijd: daarom maar een maal doen */
				if (simulation[scennum].getLabel().equals("Not initialized"))
					simulation[scennum] = SimulationFromXMLFactory
							.manufacture_DOMPopulationTree(
									simulationConfiguration, false);
				/*
				 * set the initial population to the population (taken earlier
				 * from the Modelparameter object
				 */
				simulation[scennum].setPopulation(pop[scennum]);

				// log.info("simulationFile loaded for scenario " +
				// scennum);

				if (pop[scennum] == null)
					throw new CDMConfigurationException(
							"no population found for scenario " + scennum);
				// log.info("starting run for population " + scennum);
				/*
				 * run the simulation for this population This is done by the
				 * new Simulation Object DynamoSimulation that is a shell around
				 * the "old" CDM Simulation Object (it contains the CDM-object
				 * as a field) as for instance a progress bar could be added
				 * that way
				 */

				runScenario(scennum, pr);

				// todo: remove progress indicator and add to output
				// log.info("Run  complete for population " + scennum);

			}
		}
		output.extractNumbersFromPopulation(pop);
		return output;
	}

	private int getNIndividuals(InitialPopulationFactory popFactory) {
		int numberOfElements = popFactory.getNumberOfDiseaseStateElements(p) + 4;
		/* populations are run in blocks of Nindividuals */
		/*
		 * The number is set so that 250000 characteristics are processed in a
		 * single run /
		 */
		int nIndividuals = 500000;
		nIndividuals /= numberOfElements;
		int npop = scen.getNPopulations();
		int nclasses = 1;
		if (p.getRiskType() != 2)
			nclasses = p.getPrevRisk()[0][0].length;
		/*
		 * the onefor all scenario may have many more individuals than another
		 * type of populations, therefore for safety count this as the maximum
		 * possible number of populations
		 */
		if (scen.getFirstOneForAllPopScenario() != -1)
			npop += nclasses * (nclasses - 1) / 2 - 1;
		nIndividuals /= npop;
		return nIndividuals;
	}

	/**
	 * Uses serialization to persist the DynamoOutputFactoryObject to a file.
	 * 
	 * @param output
	 *            The DynamoOutputFactory Object
	 */
	private void persistDynamoOutputFactory(DynamoOutputFactory output) {
		String resultFileName = this.baseDir
				+ File.separator
				+ "Simulations"
				+ File.separator
				+ this.simName
				+ File.separator
				+ /* "results" */StandardTreeNodeLabelsEnum.RESULTS
						.getNodeLabel()
				+ File.separator
				+ /* "resultObject.obj" */StandardTreeNodeLabelsEnum.RESULTSOBJECTFILE
						.getNodeLabel() + ".obj";

		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(resultFileName)));

			// out = new ObjectOutputStream(
			// new FileOutputStream(resultFile));
			out.writeObject(output);

			out.flush();
			out.close();
			;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			this.displayErrorMessage(e, resultFileName);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.displayErrorMessage(e, resultFileName);
			e.printStackTrace();
		}

		// OutputWritingRunnable writer=new
		// OutputWritingRunnable(resultFileName, output);

		// writer.run();

		// this.output.writeDataToDisc("c:\\hendriek\\java\\~datastream.obj");
		// this.output.readDataFromDisc("c:\\hendriek\\java\\~datastream.obj");

	}

	/**
	 * Uses serialization to persist the ScenarioInfo to a file.
	 * 
	 * @param output
	 *            The DynamoOutputFactory Object
	 */
	private void persistScenarioInfo(ScenarioParameters scenarioParms) {
		String resultFileName = this.baseDir
				+ File.separator
				+ "Simulations"
				+ File.separator
				+ this.simName
				+ File.separator
				+ StandardTreeNodeLabelsEnum.RESULTS.getNodeLabel()
				+ File.separator
				+ StandardTreeNodeLabelsEnum.SCENARIOPARMSOBJECTFILE
						.getNodeLabel() + ".obj";
		File resultFile = new File(resultFileName);

		ObjectOutputStream out;
		try {
			// out = new ObjectOutputStream(new BufferedOutputStream(
			// new FileOutputStream(resultFile)));
			out = new ObjectOutputStream(new FileOutputStream(resultFile));
			out.writeObject(scenarioParms);
			// out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			this.displayErrorMessage(e, resultFileName);
			e.printStackTrace();
		} catch (IOException e) {
			this.displayErrorMessage(e, resultFileName);
			e.printStackTrace();
		}
	}

	// not used
	private void persistPopulationArray(Population[] populationArray) {
		String resultFileName = this.baseDir
				+ File.separator
				+ "Simulations"
				+ File.separator
				+ this.simName
				+ File.separator
				+ StandardTreeNodeLabelsEnum.RESULTS.getNodeLabel()
				+ File.separator
				+ StandardTreeNodeLabelsEnum.POPULATIONARRAYOBJECTFILE
						.getNodeLabel() + ".obj";
		File resultFile = new File(resultFileName);

		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(resultFile)));
			out.writeObject(populationArray);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			this.displayErrorMessage(e, resultFileName);
			e.printStackTrace();
		} catch (IOException e) {
			this.displayErrorMessage(e, resultFileName);
			e.printStackTrace();
		}
	}

	public void resultScreen(String baseDir, String simName) {
		String resultFileName = this.baseDir + File.separator + "Simulations"
				+ File.separator + this.simName + File.separator + "results"
				+ File.separator + "resultObject.obj";
		File resultFile = new File(resultFileName);
		DynamoOutputFactory output = null;
		if (resultFile.exists()) {

			FileInputStream resultFileStream;
			try {
				resultFileStream = new FileInputStream(resultFileName);
				ObjectInputStream inputStream = new ObjectInputStream(
						resultFileStream);
				output = (DynamoOutputFactory) inputStream.readObject();
			} catch (FileNotFoundException e1) {
				// new ErrorMessageWindow(
				// "Error message while reading the results object with message: "
				// + e1.getMessage(), parentShell);
				pr
						.usedToBeErrorMessageWindow("Error message while reading the results object with message: "
								+ e1.getMessage());
				e1.printStackTrace();
			} catch (IOException e2) {
				// new ErrorMessageWindow(
				// "Error message while reading the results object with message: "
				// + e2.getMessage(), parentShell);
				pr
						.usedToBeErrorMessageWindow("Error message while reading the results object with message: "
								+ e2.getMessage());
				e2.printStackTrace();
			} catch (ClassNotFoundException e3) {
				// new ErrorMessageWindow(
				// "Error message while reading the results object with message: "
				// + e3.getMessage(), parentShell);
				pr
						.usedToBeErrorMessageWindow("Error message while reading the results object with message: "
								+ e3.getMessage());
				e3.printStackTrace();
			}
		}

		else
			pr.usedToBeErrorMessageWindow("No file with filename "
					+ resultFileName + " exists to read the results from.");
		String parmsFileName = this.baseDir
				+ File.separator
				+ "Simulations"
				+ File.separator
				+ this.simName
				+ File.separator
				+ StandardTreeNodeLabelsEnum.RESULTS.getNodeLabel()
				+ File.separator
				+ StandardTreeNodeLabelsEnum.SCENARIOPARMSOBJECTFILE
						.getNodeLabel() + ".obj";
		File parmsFile = new File(parmsFileName);

		if (parmsFile.exists()) {

			FileInputStream parmsFileStream;
			ScenarioParameters scenParms = null;
			try {
				parmsFileStream = new FileInputStream(parmsFileName);
				ObjectInputStream inputStream = new ObjectInputStream(
						parmsFileStream);
				scenParms = (ScenarioParameters) inputStream.readObject();
			} catch (FileNotFoundException e1) {
				// new ErrorMessageWindow(
				// "Error message while reading the resulst object with message: "
				// + e1.getMessage(), parentShell);
				pr
						.usedToBeErrorMessageWindow("Error message while reading the resulst object with message: "
								+ e1.getMessage());
				e1.printStackTrace();
			} catch (IOException e2) {
				// new ErrorMessageWindow(
				// "Error message while reading the results object with message: "
				// + e2.getMessage(), parentShell);
				pr
						.usedToBeErrorMessageWindow("Error message while reading the results object with message: "
								+ e2.getMessage());
				e2.printStackTrace();
			} catch (ClassNotFoundException e3) {
				// new ErrorMessageWindow(
				// "Error message while reading the results object with message: "
				// + e3.getMessage(), parentShell);
				pr
						.usedToBeErrorMessageWindow("Error message while reading the results object with message: "
								+ e3.getMessage());
				e3.printStackTrace();
			}
			String currentPath = this.baseDir + File.separator + "simulations"
					+ File.separator + simName + File.separator + "results";

			// Shell parentShell = pr.getShell();
			// if (parentShell != null) {
			// new Output_UI(parentShell, output, scenParms, currentPath);
			// }
			pr.createOutput(output, scenParms, currentPath);
		} else
			// new ErrorMessageWindow("No file with filename " + resultFileName
			// + " exists to read the results from.", parentShell);
			pr.usedToBeErrorMessageWindow("No file with filename "
					+ resultFileName + " exists to read the results from.");
	}

	/**
	 * Beware: Callback from the PR Object.
	 * 
	 * @param cdmErrorMessage
	 * @param e
	 * @param fileName
	 * @return
	 */
	public String handleErrorMessage(String cdmErrorMessage, Exception e,
			String fileName) {
		e.printStackTrace();
		// Show the error message and the nested cause of the error
		String errorMessage = "";
		if (e.getCause() != null) {
			if (!e.getCause().getMessage().contains(":")) {
				errorMessage = "An error occurred: " + e.getMessage() + "\n"
						+ "Cause: " + e.getCause().getMessage();
			} else {
				errorMessage = "An error occurred: " + e.getMessage() + "\n"
						+ "Cause: ";
				String[] splits = e.getCause().getMessage().split(":");
				for (int i = 1; i < splits.length; i++) {
					errorMessage += splits[i];
				}
			}
			errorMessage += " related to file: " + fileName;
		} else {
			errorMessage = cdmErrorMessage;
		}
		this.log.error(errorMessage);
		return errorMessage;
	}

	public void runScenario(int scennum, DynSimRunPRInterface dsi)
			throws Exception {

		Population population = simulation[scennum].getPopulation();
		int stepsInRun = 105;

		int size = population.size();

		ProgressIndicatorInterface pii = null;
		int step = 0;
		int currentProgressIndicator = 0;
		if (runInOne) {
			pii = dsi.createProgressIndicator("Simulation of scenario "
					+ scennum + " running ....");

			step = (int) Math.floor(size / 50);
			if (step < 1)
				step = 1;

			currentProgressIndicator = 0;
			// bar.setMaximum(size / step);
			pii.setMaximum(size / step);
		}
		int currentIndividual = 0;
		Iterator<Individual> individualIterator = population.iterator();
		while (individualIterator.hasNext()) {
			currentIndividual++;
			Individual individual = individualIterator.next();
			// log.debug("Longitudinal: Processing individual "
			// + individual.getLabel());
			for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {
				/* check if the simulation for this person can be ended */
				CharacteristicValueBase charValBase = individual.get(1);

				if (charValBase instanceof IntCharacteristicValue) {
					if (((IntCharacteristicValue) charValBase).isFull())
						break;
				}

				else {
					if (charValBase instanceof FloatCharacteristicValue)
						if (((FloatCharacteristicValue) charValBase).isFull())
							break;
				}
				/* if not, simulate */

				simulation[scennum].processCharVals(individual);

			}
			if (runInOne)
				if (currentIndividual > step * currentProgressIndicator) {
					// bar.setSelection(currentProgressIndicator);
					pii.update(currentProgressIndicator);
					currentProgressIndicator++;
				}
		}
		if (runInOne)
			pii.dispose();
	}

	private void displayErrorMessage(Exception e, String simulationFilePath) {
		pr.communicateErrorMessage(this, e, simulationFilePath);
		e.printStackTrace();
	}

	private void wrapAndThrowErrorMessage(Exception e, String simulationFilePath)
			throws DynamoInconsistentDataException {
		String cause = "";
		if (e.getCause() != null) {
			cause += this.handleErrorMessage("", e, simulationFilePath);
		}
		String totalMessage = e.getMessage() + cause;

		throw new DynamoInconsistentDataException(totalMessage);
	}

}
