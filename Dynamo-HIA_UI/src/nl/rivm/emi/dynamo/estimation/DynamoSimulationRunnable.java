package nl.rivm.emi.dynamo.estimation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.DomLevelTraverser;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.rules.update.base.ManyToManyUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRules4Simulation;
import nl.rivm.emi.cdm.simulation.RunModes;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.cdm.stax.StAXEntryPoint;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class DynamoSimulationRunnable extends DomLevelTraverser {

	private static final long serialVersionUID = 6377558357121377722L;

	private Log log = LogFactory.getLog(getClass().getName());

	private Simulation simulation;

	private Shell parentShell;
	
	String preCharConfig;
	String simName;
	String baseDir;
	 /*
	 * model parameter is an object containing the
	 * parameters of the model and the initial population. The parameters are 
	 * written to XML files that are input-parameters for the model, and
	 * the population is extracted directly and fed into the simulation.
	 * There is also an option to write the intitial population to XML, but this is not
	 * used here.
	 * 
	 */    
	private ModelParameters p;
 /*
	 * object with all information needed both before
	 * and after the running of the simulation
	 */


	private ScenarioInfo scen;
	
	private DynamoOutputFactory output;


	public DynamoSimulationRunnable(Shell parentShell, String simName, String baseDir) {
		super();
		configureSimulation(simName, baseDir);
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
	 * 
	 * 
	 */
	private void configureSimulation(String simName, String baseDir) {

		/* make an instance of the basedirectory object that is a singleton containing the basedirectory */
		BaseDirectory B = BaseDirectory
		.getInstance(baseDir);
		this.baseDir=baseDir;
		this.simName=simName;
		/*
		 * make the strings with the filenames of the files that are read by CDM
		 * These have fixed names, and are in the directory with the
		 * simulationname
		 */
	
		
		/*
		 * preCharConfig is a file that contains the configuration of the
		 * characteristics of each simulated individual
		 */
		String directoryName = baseDir + "Simulations" + File.separator
		+ simName;
		preCharConfig = directoryName + File.separator
				+ "modelconfiguration" + File.separator + "charconfig.XML";
		/*
		 * simFileName is a file that contains the configuration of the
		 * simulation
		 */

		 // to
																		// add
		p = new ModelParameters();
		try {
			scen = p.estimateModelParameters(simName, parentShell);
		} catch (ConfigurationException e3) {
			displayErrorMessage(e3);
			log.fatal(e3.getMessage());
			e3.printStackTrace();

		
			

		}catch (DynamoInconsistentDataException e) {
			// TODO Auto-generated catch blockdisplayErrorMessage(e3);
			log.fatal(e.getMessage());
			displayInconsistentDataMessage(e);
			
			e.printStackTrace();
		}
																		// " .XML";
		 run();
	}

	/**
	 * @param simName
	 * @param preCharConfig
	 * @param simFileName
	 */
	public void run() {
		HierarchicalConfiguration simulationConfiguration;
		try {
		
			String directoryName = baseDir + "Simulations" + File.separator
			+ simName;
			String simFileName = directoryName + File.separator
			+ "modelconfiguration" + File.separator + "simulation";
			/*
			 * simulation is an object that contains the population
			 * that is simulated and carries out the simulation
			 */
			simulation=new Simulation();
			log.info("ModelParameters estimated and written");

			File multipleCharacteristicsFile = new File(preCharConfig);
			log.info("charFile made.");
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			log.info("charFile handled.");
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();
			log.info("empty charmap made");
			/*
			 * array pop contains the stimulated populations for the different
			 * scenario's calculate the number of populations that are needed to
			 * carry out this simulation
			 */
			int nPopulations = scen.getNScenarios() + 1;

			if (p.getRiskType() != 2)
				for (int scennum = 1; scennum < scen.getNScenarios(); scennum++) {
					if (scen.getInitialPrevalenceType()[scennum]
							&& (!scen.getTransitionType()[scennum]))
						nPopulations--;
				}
			
			/* get the initial population from the modelparameters object */
			Population[] pop = p.getInitialPopulation();
			/* run the simulation for each population */
			for (int scennum = 0; scennum < nPopulations; scennum++) {
				File simulationConfigurationFile;
				if (scennum == 0)
					simulationConfigurationFile = new File(simFileName + ".xml");
				else
					simulationConfigurationFile = new File(simFileName
							+ "_scen_" + scennum + ".xml");
				log.info("simulationFile made for scenario " + scennum);

				assertTrue(CharacteristicsConfigurationMapSingleton
						.getInstance().size() > 1);
				// calculate frequency of risk factor values during simulation

				if (simulationConfigurationFile.exists()) {
					simulationConfiguration = new XMLConfiguration(
							simulationConfigurationFile);
					log.info("simulationconfuration made for scenario "
							+ scennum);

					/* read the configuration file  */
					/* the false means that the initial population should not be read from xml file */
					
					simulation = SimulationFromXMLFactory
							.manufacture_DOMPopulationTree(
									simulationConfiguration, false);
					/* set the initial population to the population (taken earlier from the Modelparameter
					 * object
					 */
					simulation.setPopulation(pop[scennum]);
					
					log.info("simulationFile loaded for scenario " + scennum);
					
					if (pop[scennum] == null)
						throw new CDMConfigurationException(
								"no population found for scenario " + scennum);
					log.info("starting run for population " + scennum);
					/* run the simulation for this population
					 * This is done by the new Simulation Object DynamoSimulation that is a shell
					 * around the "old" CDM Simulation Object (it contains the CDM-object as a field)
					 * as for instance a progress bar could be added that way
					 * 
					 */
					
					runScenario(scennum);
					log.info("Run  complete for population " + scennum);

				}
			}
			/* display the output */
			
			Output_UI ui = new Output_UI(parentShell, scen, simName, pop);
		} catch (DynamoConfigurationException e) {
			displayErrorMessage(e);
			log.fatal(e.getMessage());
			e.printStackTrace();
		}  catch (Exception e) {displayErrorMessage(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			log.debug("Longitudinal: Processing individual "
					+ individual.getLabel());
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

	private void displayInconsistentDataMessage(Exception e) {
		Shell shell = new Shell(parentShell);
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox
				.setMessage("The parameters of the model could not be estimated"
						+ " Message given: "
						+ e.getMessage()
						+ ". Please change the input of the model.");
		e.printStackTrace();
		if (messageBox.open() == SWT.OK) {
			shell.dispose();
		}

		shell.open();
		
	}

	private void displayErrorMessage(Exception e) {

		Shell shell = new Shell(parentShell);
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setMessage("Errors during configuration of the model"
				+ " Message given: " + e.getMessage());
		e.printStackTrace();
		if (messageBox.open() == SWT.OK) {
			shell.dispose();
		}

		shell.open();
		}

}