
package nl.rivm.emi.dynamo.estimation;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
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
import nl.rivm.emi.dynamo.output.DynamoOutputFactory;

import org.apache.commons.configuration.XMLConfigurationToo;
import org.apache.commons.logging.Log;
import org.eclipse.swt.widgets.Display;
/**
 * @author boshuizh
 *
 */
public class DynamoSimulationRunnableWorker  implements Runnable{
	private Log log;
	private Simulation[] simulation;
	private DynSimRunPRInterface pr;
	 ExceptionListener [] listener = new  ExceptionListener[1];
	 /* only room for a single listener, as only the main progrqam will listen to this event */
	// FinishingListener listener2 = new  FinishingListener();
	
	private String preCharConfig;
	private String simName;
	private String baseDir;
//	private boolean runInOne;
	private ModelParameters p;
	private ScenarioInfo scen;
	private int age;
	private int generation;
	private int g;
	
	private boolean newborns;
	private String simFileName;
	private DynamoOutputFactory output;
	private Display display;

	/** This runnables runs the simulation for one age and gender group and add the results to the output object
	 *   it also updates the progress bar
	 * 
	 * @param display
	 * @param log
	 * @param pr
	 * @param param
	 * @param scenInfo
	 * @param simFileName
	 * @param output
	 * @param age
	 * @param g
	 * @param generation
	 * @param newborns
	 */
	public DynamoSimulationRunnableWorker(Display display, Log log, DynSimRunPRInterface pr, 
			 ModelParameters param, ScenarioInfo scenInfo, String simFileName, 
			 DynamoOutputFactory output, int age, int g, int generation, boolean newborns) {
		this.log = log;
		this.pr = pr;
		this.display=display;
		this.scen=scenInfo;
		this.p=param;
		this.age=age;
		this.g=g;
		this.generation=generation;
		this.newborns=newborns;
		this.simFileName=simFileName;
		this.output=output;
		/* replace the seed in scenInfo, otherwise all age/sex groups are run with the same random seed */
		long seed=scenInfo.getRandomSeed();
		seed=(seed<<generation)+g*14132-age*11;
		scenInfo.setRandomSeed(seed);
		
		
	}
	
	public void run()
	{
		log.info("start worker");
		
		simulation = new Simulation[scen.getNPopulations()];
		log.info("simulations object made");
		for (int n = 0; n < scen.getNPopulations(); n++)
			simulation[n] = new Simulation();
        log.info("all simulations made");
		InitialPopulationFactory popFactory;
		try {
			popFactory = new InitialPopulationFactory(
					this.p, this.scen, this.pr,age, age, g,
					g);
			log.info("popfactory made");
	Population [] pop = popFactory.manufactureInitialPopulation(age, age, g,
			g, generation, generation, newborns);
	log.info("simulation pop made");
	pop = runPopulation(pop, simFileName);
	log.info("starting extracting age "+age+" gender "+g);
	output.extractNumbersFromPopulation(pop);
//	log.fatal("end extracting");
	//this.pr.updateProgressIndicator();
	
	;
	
	
	if (display !=null) {
		
		if (!display.isDisposed()) {
		
			
      display.asyncExec(
		 new Runnable() {  
    	
    	public void run() {  
    		
    		
    	if (display.isDisposed() )
            return;
    	
    	pr.update();

    	          }
    })
	  ; }}
	
	/* flushes the events queue: otherwise it does not work!!! */
	if (!(display==null)) if (!display.isDisposed()) while (display.readAndDispatch()) {};
	//display.wake();
	

	//Display.getDefault().getThread().sleep(200);
	
	
  
		} catch (DynamoInconsistentDataException e) {
			e.printStackTrace();
			  listener[0].actionPerformed(new ActionEvent(e,0,"DynamoInconsistentData")); 
			
		} catch (Exception e) {
			e.printStackTrace();
			listener[0].actionPerformed(new ActionEvent(e,0,"")); 
			
		}}

	private Population[] runPopulation(Population[] pop,
			String simFileName) throws Exception {

		;
		// Assemble the simulation file name
		XMLConfigurationToo simulationConfiguration;
		String simulationFilePath = simFileName + ".xml";
		// log.debug("simulationFilePath" + simulationFilePath);

		/* run the simulation for each population */
		
		
		for (int popNum = 0; popNum < scen.getNPopulations(); popNum++) {
			File simulationConfigurationFile;
			if (popNum != 0)
				simulationFilePath = simFileName + "_scen_" + popNum + ".xml";

			simulationConfigurationFile = new File(simulationFilePath);
			 //log.fatal("simulationFile made for scenario " );

			// assertTrue(CharacteristicsConfigurationMapSingleton
			// .getInstance().size() > 1);
			if (!(CharacteristicsConfigurationMapSingleton.getInstance().size() > 1)) {
				throw new DynamoConfigurationException(
						"More than 1 disease needs to be configured.");
			}
			// calculate frequency of risk factor values during simulation

			if (simulationConfigurationFile.exists()) {
				/* oud */
			/*	simulationConfiguration = new XMLConfiguration(
						simulationConfigurationFile);*/
				
				simulationConfiguration = new XMLConfigurationToo();
				simulationConfiguration.setDelimiterParsingDisabled(true);
				simulationConfiguration.setFile(simulationConfigurationFile);
				simulationConfiguration.load();

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
					simulationConfiguration.setValidating(false);
					simulationConfiguration.load();
				}

				/* read the configuration file */
				/*
				 * the false means that the initial population should not be
				 * read from xml file
				 */
				/* deze stap neemt veel tijd: daarom maar een maal doen */
				if (simulation[popNum].getLabel().equals("Not initialized"))
					simulation[popNum] = SimulationFromXMLFactory
							.manufacture_DOMPopulationTree(
									simulationConfiguration, false);
				/*
				 * set the initial population to the population (taken earlier
				 * from the Modelparameter object
				 */
				simulation[popNum].setPopulation(pop[popNum]);
				/* daly type is an array over the alternative scenario's , thus not including the reference scenario */
				if (popNum>0 && scen.getDalyType()[popNum] ) simulation[popNum].setIsDaly(true);
				else simulation[popNum].setIsDaly(false);

				// log.info("simulationFile loaded for scenario " +
				// scennum);

				if (pop[popNum] == null)
					throw new CDMConfigurationException(
							"no population found for scenario " + popNum);
				// log.info("starting run for population " + scennum);
				/*
				 * run the simulation for this population This is done by the
				 * new Simulation Object DynamoSimulation that is a shell around
				 * the "old" CDM Simulation Object (it contains the CDM-object
				 * as a field) as for instance a progress bar could be added
				 * that way
				 */
				log.debug("start running scenario " );
				runScenario(popNum, pr);

				// todo: remove progress indicator and add to output
				// log.info("Run  complete for population " + scennum);

			}
		}
	
		log.debug("numbers are extracted");
		return pop;
	}

	public void runScenario(int populationNum, DynSimRunPRInterface dsi)
			throws Exception {

		log.info("start of runScenario for scen "+populationNum);
		Population population = simulation[populationNum].getPopulation();
		int stepsInRun = 105;

		

		
		
		@SuppressWarnings("unused")
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
				

				simulation[populationNum].processCharVals(individual);
				
				// TODO if (stepCount==0 ) simulation[scennum].disableIsDaly();

				

			}
			
		}
		log.debug("end simulation");
		
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public Simulation[] getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation[] simulation) {
		this.simulation = simulation;
	}

	public DynSimRunPRInterface getPr() {
		return pr;
	}

	public void setPr(DynSimRunPRInterface pr) {
		this.pr = pr;
	}

	

	public String getPreCharConfig() {
		return preCharConfig;
	}

	public void setPreCharConfig(String preCharConfig) {
		this.preCharConfig = preCharConfig;
	}

	public String getSimName() {
		return simName;
	}

	public void setSimName(String simName) {
		this.simName = simName;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	

	public ModelParameters getP() {
		return p;
	}

	public void setP(ModelParameters p) {
		this.p = p;
	}

	public ScenarioInfo getScen() {
		return scen;
	}

	public void setScen(ScenarioInfo scen) {
		this.scen = scen;
	}

	public void addExceptionListener(ExceptionListener listener) {
		
		 this.listener[0]= listener;

		}
	
/*	public void addFinishingListener(FinishingListener listener) {
		
		 this.listener= listener;

		}*/

		
	}
	