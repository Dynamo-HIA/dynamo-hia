package nl.rivm.emi.dynamo.estimation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;
import nl.rivm.emi.dynamo.output.DynamoOutputFactory;
import nl.rivm.emi.dynamo.output.ErrorMessageWindow;
import nl.rivm.emi.dynamo.output.Output_UI;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class DynamoSimulationRunnable extends DomLevelTraverser {

	private static final long serialVersionUID = 6377558357121377722L;

	private Log log = LogFactory.getLog(getClass().getName());

	private Simulation simulation;

	private Shell parentShell;
	String errorMessage = null;
	String preCharConfig;
	String simName;
	String baseDir;
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

	public DynamoSimulationRunnable(Shell parentShell, String simName,
			String baseDir) throws DynamoInconsistentDataException {
		super();
		this.parentShell = parentShell;
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

		scen = p.estimateModelParameters(this.simName, this.parentShell);
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
		XMLConfiguration simulationConfiguration;
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
			simulation = new Simulation();
			// log.info("ModelParameters estimated and written");

			File multipleCharacteristicsFile = new File(preCharConfig);
			// log.info("charFile made.");
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			// log.info("charFile handled.");
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();
			// log.info("empty charmap made");
			/*
			 * array pop contains the stimulated populations for the different
			 * scenario's calculate the number of populations that are needed to
			 * carry out this simulation
			 */
			int nPopulations = scen.getNPopulations();

			/* get the initial population from the modelparameters object */
			Population[] pop = p.getInitialPopulation();

			// Assemble the simulation file name
			simulationFilePath = simFileName + ".xml";
			// log.debug("simulationFilePath" + simulationFilePath);

			/* run the simulation for each population */
			for (int scennum = 0; scennum < nPopulations; scennum++) {
				File simulationConfigurationFile;
				if (scennum != 0)
					simulationFilePath = simFileName + "_scen_" + scennum
							+ ".xml";

				simulationConfigurationFile = new File(simulationFilePath);
				// log.info("simulationFile made for scenario " + scennum);

				// assertTrue(CharacteristicsConfigurationMapSingleton
				// .getInstance().size() > 1);
				if (!(CharacteristicsConfigurationMapSingleton.getInstance()
						.size() > 1)) {
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
					 * TODO: VALIDATION IS FOR FUTURE USE NICE TO HAVE FEATURE
					 * KEEP IT IN THE CODE The following schemas are not
					 * validated: sim.xsd
					 */
					if (!"sim".equals(simulationConfiguration
							.getRootElementName())) {
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

					simulation = SimulationFromXMLFactory
							.manufacture_DOMPopulationTree(
									simulationConfiguration, false);
					/*
					 * set the initial population to the population (taken
					 * earlier from the Modelparameter object
					 */
					simulation.setPopulation(pop[scennum]);

					// log.info("simulationFile loaded for scenario " +
					// scennum);

					if (pop[scennum] == null)
						throw new CDMConfigurationException(
								"no population found for scenario " + scennum);
					// log.info("starting run for population " + scennum);
					/*
					 * run the simulation for this population This is done by
					 * the new Simulation Object DynamoSimulation that is a
					 * shell around the "old" CDM Simulation Object (it contains
					 * the CDM-object as a field) as for instance a progress bar
					 * could be added that way
					 */

					runScenario(scennum);
					// log.info("Run  complete for population " + scennum);

				}
			}
			/* display the output */
			DynamoOutputFactory output=null;
			try {
				output = new DynamoOutputFactory(scen, pop);

			} catch (DynamoScenarioException e) {
				new ErrorMessageWindow(e, parentShell);
				e.printStackTrace();
			} catch (DynamoOutputException e) {
				// TODO let user enter new starting year and make new
				e.printStackTrace();
				new ErrorMessageWindow(
						"starting year of simulation is given as "
								+ scen.getStartYear()
								+ " while newborn data are only present starting at year "
								+ scen.getNewbornStartYear()
								+ ". Therefore starting year of simulation is assumed to be "
								+ scen.getNewbornStartYear(), this.parentShell);

				scen.setStartYear(scen.getNewbornStartYear());

				try {
					output = new DynamoOutputFactory(scen, pop);
				} catch (DynamoScenarioException e1) {
					new ErrorMessageWindow(e1, parentShell);

					e1.printStackTrace();
				} catch (DynamoOutputException e1) {

					new ErrorMessageWindow(e1, parentShell);
					e1.printStackTrace();
				}

			}
			/* make the output screen */

			new Output_UI(parentShell, output, simName, this.baseDir);
			/* write the output object to a file */
			String resultFileName = this.baseDir + File.separator
					+ "Simulations" + File.separator + this.simName
					+ File.separator + "results" + File.separator
					+ "resultObject.obj";
			File resultFile = new File(resultFileName);

			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(new BufferedOutputStream(
						new FileOutputStream(resultFile)));
				out.writeObject(output);
				// this.output.writeDataToDisc("c:\\hendriek\\java\\~datastream.obj");
				// this.output.readDataFromDisc("c:\\hendriek\\java\\~datastream.obj");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				this.displayErrorMessage(e, resultFileName);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.displayErrorMessage(e, resultFileName);
				e.printStackTrace();
			}

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

	public void resultScreen(String baseDir, String simName, Shell parentShell) {
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
				output = (DynamoOutputFactory) inputStream
						.readObject();
			} catch (FileNotFoundException e1) {
				new ErrorMessageWindow(
						"Error message while reading the results object with message: "
								+ e1.getMessage(), parentShell);
				e1.printStackTrace();
			} catch (IOException e2) {
				new ErrorMessageWindow(
						"Error message while reading the results object with message: "
								+ e2.getMessage(), parentShell);
				e2.printStackTrace();
			} catch (ClassNotFoundException e3) {
				new ErrorMessageWindow(
						"Error message while reading the results object with message: "
								+ e3.getMessage(), parentShell);
				e3.printStackTrace();
			}

			new Output_UI(parentShell, output, simName, this.baseDir);
		} else
			new ErrorMessageWindow("No file with filename " + resultFileName
					+ " exists to read the results from.", parentShell);

	}

	private String handleErrorMessage(String cdmErrorMessage, Exception e,
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

	public void runScenario(int scennum) throws Exception {

		Population population = simulation.getPopulation();
		int stepsInRun = 105;

		int size = population.size();

		Shell shell = new Shell(parentShell);
		shell.setText("Simulation of scenario " + scennum + " running ....");
		shell.setLayout(new FillLayout());
		shell.setSize(600, 50);

		ProgressBar bar = new ProgressBar(shell, SWT.NULL);
		bar.setBounds(10, 10, 200, 32);
		bar.setMinimum(0);

		shell.open();

		int step = (int) Math.floor(size / 50);
		if (step < 1)
			step = 1;
		int currentIndividual = 0;
		int currentProgressIndicator = 0;
		bar.setMaximum(size / step);
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

				simulation.processCharVals(individual);

			}
			if (currentIndividual > step * currentProgressIndicator) {
				bar.setSelection(currentProgressIndicator);
				currentProgressIndicator++;
			}
		}
		/*
		 * while (!shell.isDisposed ()) { if (!display.readAndDispatch ())
		 * display.sleep (); }
		 */
		shell.close();
	}

	private void displayErrorMessage(Exception e, String simulationFilePath) {

		Shell shell = new Shell(parentShell);
		String cause = "";
		if (e.getCause() != null) {
			cause += this.handleErrorMessage("", e, simulationFilePath);
		}
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setMessage("Errors during configuration of the model"
				+ " Message given: " + e.getMessage() + cause);
		messageBox.open();
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
