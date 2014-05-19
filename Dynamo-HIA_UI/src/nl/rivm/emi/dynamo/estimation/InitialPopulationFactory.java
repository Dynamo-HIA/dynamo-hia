package nl.rivm.emi.dynamo.estimation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.individual.DOMIndividualWriter;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InitialPopulationFactory {
	Log log = LogFactory.getLog(this.getClass().getName());
	private int numberOfElements;
	private String globalBaseDir;
	private boolean incidenceDebug = true;
	// private Shell parentShell;
	private DynSimRunPRInterface dsi = null;
	// private ProgressBar bar = null;
	private ProgressIndicatorInterface progressbar = null;
	private float[][][][][] prevalenceTransitionMatrix = null;
	private int riskType = 0;
	private int nClasses = 0;

	int nSim = 50;

	/* in case of debugging, more elements are needed */
	
	
 // index: population, age , sex
	// gives the number of durationclasses =20??
	// ja want kan niet groter worden in het scenario, wel kleiner evt
	int[][][] nDuurClasses ;

	
	Random rand; // used to draw the initial population
	MTRand rand2; /*
				 * used to generate seeds which are entered as information with
				 * each generated individual, and will be used by the update
				 * rules // TODO inlezen baseDir aanpassen aan userinterface
				 */
	Random rand3; // used to draw the scenario population
	// here the seed is reset by hand, so it does not matter that it used
	// the same seed here
	/* make a table of point from the inverse normal distribution */
	private int nPopulations;
	
	private boolean hasDalyScenarios;
	private int[] numberOfDalypopulation;
	private boolean[][][][] shouldChangeInto;
	private int individualNumber;
	
	private ModelParameters parameters;
	private ScenarioInfo scenarioInfo;
	private double[] baselineOdds;
	private float[] relRiskDuurBegin;
	private float[] relRiskDuurEnd;
	private float[][] relativeRisksCat;
	private float[] relativeRisksCont;
	private float[] alphaDuur;
	private float[][][] RRdiseaseOnDisease;
	private int[][][] nSimNew;

	/* indexes are: scenario, age ,sex, transitionmatrix */

	/* number of disease states */

	/**
	 * @author Boshuizh This class generates the initial population and
	 *         populations of newborns
	 * 
	 */
	/**
	 * @param params
	 *            : modelparameters
	 * @param scenInfo
	 *            : scenario Info object
	 * @throws DynamoInconsistentDataException
	 * @throws DynamoConfigurationException 
	 */
	public InitialPopulationFactory(ModelParameters params,

	ScenarioInfo scenInfo, DynSimRunPRInterface dsi)
			throws DynamoInconsistentDataException, DynamoConfigurationException {

		initializeInitialPopulationFactory(params,

		scenInfo);
		// this.parentShell = shell;
		this.dsi = dsi;
	}

	/**
	 * OBSOLETE and does not work anymore
	 * 
	 * @param parameters
	 * @param nSim
	 * @param simulationName
	 * @param seed
	 * @param newborns
	 *            : boolean: should this give an intial population or newborns
	 * @param scenarioInfo
	 *            : information on scenario
	 * @throws DynamoConfigurationException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public void writeInitialPopulation(ModelParameters parameters, int nSim,
			String simulationName, long seed, boolean newborns,
			ScenarioInfo scenarioInfo) throws DynamoConfigurationException {

		Population[] pop = null;// manufactureInitialPopulation();

		String baseDir = this.globalBaseDir;
		String directoryName = baseDir + File.separator + "Simulations\\"
				+ simulationName;
		String popFileName;

		if (newborns)
			popFileName = directoryName + File.separator + "modelconfiguration"
					+ File.separator + "newborns.xml";
		else
			popFileName = directoryName + File.separator + "modelconfiguration"
					+ File.separator + "population.xml";
		File initPopXMLfile = new File(popFileName);
		try {
			writeToXMLFile(pop[0], 0, initPopXMLfile);
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
			throw new DynamoConfigurationException(
					"ParserConfigurationException while writing population"
							+ "to XML with message: " + e.getMessage());
		} catch (TransformerException e) {

			e.printStackTrace();
			throw new DynamoConfigurationException(
					"TransformerException while writing population"
							+ "to XML with message: " + e.getMessage());
		}
		if (pop.length > 1 && !newborns)
			for (int scen = 1; scen < pop.length; scen++) {
				popFileName = directoryName + File.separator
						+ "modelconfiguration" + File.separator
						+ "population_scen_" + scen + ".xml";
				initPopXMLfile = new File(popFileName);
				if (pop[scen] != null)
					try {
						writeToXMLFile(pop[scen], 0, initPopXMLfile);
					} catch (ParserConfigurationException e) {

						e.printStackTrace();
						throw new DynamoConfigurationException(
								"ParserConfigurationException while writing population"
										+ "to XML with message: "
										+ e.getMessage());
					} catch (TransformerException e) {

						e.printStackTrace();
						throw new DynamoConfigurationException(
								"TransformerException while writing population"
										+ "to XML with message: "
										+ e.getMessage());
					}
				else if (scenarioInfo.getInitialPrevalenceType()[0])
					log
							.warn("trying to write non existing initial population for scenario "
									+ scen);
			}

	}

	/**
	 * this method manufactures initial populations for 1. the reference
	 * situation 2. scenario's <br>
	 * In case of categorical risk factors with scenario's in which only the
	 * initial situation is changed, only a single scenario population is
	 * manufactured, containing all subjects that are potentially changed under
	 * the scenario .<br>
	 * The effect of each individual scenario then is calculated by the
	 * post-processing (DynamoOutputfactory) <br>
	 * nSim= number of simulated individuals per age and gender as given by the
	 * user. This number is changed by the method in case zero cases would have
	 * been generated for a particular riskfactor class or duration class.
	 * 
	 * 
	 * */
	/**
	 * @param parameters
	 *            : Object with modelparameters
	 * @param simulationName
	 * @param nSim
	 *            : number of simulated individuals per age and gender ; the
	 *            method can add individuals if risk factors would not be
	 *            represented
	 * @param seed
	 * @param newborns
	 *            (boolean) should a population of newborns be generated
	 * @param scenarioInfo
	 *            ; object with scenario information
	 * @return
	 * @throws DynamoInconsistentDataException
	 * @throws DynamoConfigurationException 
	 */
	public void initializeInitialPopulationFactory(ModelParameters params,

	ScenarioInfo scenInfo) throws DynamoInconsistentDataException, DynamoConfigurationException {

		/*
		 * at this moment:
		 * 
		 * the method should be run twice: once for the existing population, and
		 * once for the newborns
		 */
		/* Make some indexes that are needed */
		this.parameters = params;
		this.scenarioInfo = scenInfo;
		
		this.numberOfElements = getNumberOfDiseaseStateElements(parameters);
		/* in case of debugging, more elements are needed */
		if (this.incidenceDebug)
			this.numberOfElements += parameters.getNDiseases();
		this.nClasses = 1;
		this.riskType = parameters.getRiskType();
		if (this.riskType != 2)
			this.nClasses = parameters.getPrevRisk()[0][0].length;
		long seed = scenInfo.getRandomSeed();
		this.nSim = scenInfo.getSimPopSize();
		rand = new Random(seed); // used to draw the initial population
		rand2 = new MTRand(seed + 111777444); /*
											 * used to generate seeds which are
											 * entered as information with each
											 * generated individual, and will be
											 * used by the update rules // TODO
											 * inlezen baseDir aanpassen aan
											 * userinterface
											 */
		rand3 = new Random(seed+2945032); // used to draw the scenario population
		// here the seed is reset by hand, so it does not matter that it used
		// the same seed here
		/* make a table of point from the inverse normal distribution */
		if (parameters.getRiskType() == 2 && nSim == 0) {
			this.nSim = 50;
			log.warn(" zero simulated group size  asked for"
					+ " continuous riskfactor; this is changed into 50 ");
		}

		/*
		 * calculate the number of populations that should be generated
		 * 
		 * this is mostly: number of scenarios+ 1 (+1=+reference scenario).
		 * However, in case of categorical risk factors with scenario's in which
		 * only the initial situation is changed, only a single large scenario
		 * population is manufactured (the "one-for-all" population), containing
		 * all subjects that are potentially changed under each scenario.
		 */
		
		this.nPopulations = scenarioInfo.getNPopulations();
		/* added 11-2011 */
		this.hasDalyScenarios = scenInfo.hasDalyScenarios();
		/*
		 * numberOfOneForAllPop = the number of the population that contains the
		 * one for all scenario
		 */
		

		

		/* add the one-for-all-scenario that still needs to be simulated */
		if (scenarioInfo.getNTranstionScenarios() != 0
				&& scenarioInfo.getRiskType() != 2)
			makeTransitionMatrixForPrevalence(parameters, scenarioInfo);

		/* initialize the ranges for the populations to generate */

		/*
		 * see which riskfactor classes should be changed into another class in
		 * the one-for-all scenario population
		 */
		int agemax = scenarioInfo.getMaxSimAge();
		if (agemax > 95)
			agemax = 95;
		int agemin = scenarioInfo.getMinSimAge();

		
		/*
		 * a new cohort of newborns is generated for each step in the
		 * simulation, the existing population needs to be generated only once
		 */

		/* count the number of duration categories in each age class */
		
		if (parameters.getRiskType() == 3) {
			for (int a = agemin; a <= agemax; a++)
				for (int g = 0; g < 2; g++) {
					nDuurClasses[a][g] = parameters.getDuurFreq()[a][g].length;
				}
		}

		individualNumber = 0;
	}

	/**
	 * @param agemin
	 *            ; 0 for newborns
	 * @param agemax
	 *            ; 0 for newborns
	 * @param gmin
	 *            : minimum gender index (0 or 1)
	 * @param gmax
	 *            : maximum gender index (0 or 1)
	 * @param generationMin
	 *            for newborns: 1 <= generationMin <=
	 *            scenarioInfo.getYearsInRun()
	 * @param generationMax
	 *            for newborns: 1 <= generationMax <=
	 *            scenarioInfo.getYearsInRun(); Not newborns: both are 1;
	 * @param newborns
	 * @return
	 */
	public Population[] manufactureInitialPopulation(int agemin, int agemax,
			int gmin, int gmax,

			int generationMin, int generationMax, boolean newborns) {

		Population[] initialPopulation = new Population[nPopulations];
		for (int scen = 0; scen < nPopulations; scen++) {
			initialPopulation[scen] = new Population("Population nr " + scen,
					null);
		}

		
		
		/* this is the number of the Individual */
		/*
		 * as newborns are created separately, they have negative numbers,
		 * starting at -1
		 */
		/* others have positive numbers, starting at +1 */
		if (parameters.getRiskType() == 2)
			DynamoLib.getInstance(nSim);
		for (int a = agemin; a <= agemax; a++)
			for (int g = gmin; g <= gmax; g++) {

				/*
				 * start with copying the needed info from the parameter object
				 * into fields in this class; This aims to speed up the program
				 * as they do not need to be repeatedly looked up
				 */
				extractDiseaseDataForThisAgeSexGroup(a, g);
				/*
				 * calculate first the numbers that will be in each riskfactor
				 * class. See the document "description of calculations" on
				 * Q:/projecten/emi-dynamohia/description of calculations/ for
				 * the method used
				 */
				nSimNew[nPopulations][a][g] = nSim;
				/*
				 * if (Math.abs(Math.round(a / 10) - a / 10) < 0.01 && g == 1)
				 * 
				 * updateProgressBar();
				 */

				
				
			/// to do: maak de method hieronder compleet met het ook trekken van de random aanvullingen uit rest zodat dat verder niet nodig is
				// idem voor type 3
				// en zet deze binnen de loop voor populatie
				
				// kan de eerste ook niet deels voor 3 worden gedaan??
				
				
				int[] cumulativeNSimPerClass;
				if (parameters.getRiskType() == 1) {					
					
					cumulativeNSimPerClass=giveNumbersPerRiskClassForSimulation(0,a, g);
				}
				
			///	
				/*
				 * repeat this for categorical riskfactors with duration, where
				 * the same "trick" is applied both to the categories, and the
				 * duration within categories
				 */
					
					
				
			
				if (parameters.getRiskType() == 3) {
					int durClass=parameters.getDurationClass();
				    int nInDurationClass=0;
				    if (durClass>0) nInDurationClass=cumulativeNSimPerClass[0] ;
				    else nInDurationClass=cumulativeNSimPerClass[durClass] -cumulativeNSimPerClass[durClass-1] ;
				    
					int[] cumulativeNSimPerDurationClass = giveNumberPerDurationClassForSimulation(0,a, g,durClass);
					
					// if extra numbers were added to the durationclass, those should also be added to the array for the riskfactorclasses
					// this means that all cumulative numbers above this class will increase
					if (cumulativeNSimPerDurationClass[cumulativeNSimPerDurationClass.length-1]> nInDurationClass )
					
						for (int c=durClass; c<cumulativeNSimPerDurationClass.length;c++)
							cumulativeNSimPerClass[c] += cumulativeNSimPerDurationClass[cumulativeNSimPerDurationClass.length-1]- nInDurationClass;
						
				}

				/*****************************************************************************
				 * 
				 * 
				 * LOOP OVER INDIVIDUALS
				 * 
				 * 
				 * 
				 * 
				 * 
				 */
				/*****************************************************************************/
				/*
				 * in Dynamo the model the current population is always run to
				 * the age of 105 , irrespective of the number of years(
				 * scenarioInfo.getYearsInRun()) that the user indicates . <br>
				 * reason: this is needed for calculation of life-expectancy
				 * 
				 * The newborns are only run for the number of years indicated
				 * by the user.
				 */

				/*
				 * technically, the newborns are added as individuals with age
				 * -x, where x is the number of simulated year before their
				 * birth.
				 */

				/*
				 * furthermore, when the individuals are created, one has to
				 * give the maximum number of times that they will be updated.
				 * This is given by the variable stepsInSimulation
				 */
				int currentRiskValue = 0;
				int stepsInSimulation = 105 - a;
				if (newborns)
					stepsInSimulation = scenarioInfo.getYearsInRun();

				/* for newborns repeat this for all generations */
				for (int generation = generationMin; generation <= generationMax; generation++) {
					/*
					 * if (Math.abs(Math.round(generation / 10) - ((float)
					 * generation) / 10) < 0.01 && g == 1)
					 * 
					 * updateProgressBar();
					 */
					for (int i = 0; i < nSimNew[a][g]; i++) {
						/*
						 * werktte niet omdat Class Individual protected;
						 * veranderd in SOR code
						 */
						/*
						 * Individual currentIndividual = new Individual( "ind",
						 * "ind_" + (i + a nSimNew[a][g] 2 + nSimNew[a][g] g) +
						 * "_bl");
						 */
						if (newborns)
							individualNumber--;
						else
							individualNumber++;
						Individual currentIndividual = new Individual("scen0",
								"ind_" + individualNumber + "_ref");
						Individual currentIndividualS=null; /* to be used in the scenario-populations */

						/*
						 * rand2.random returns an integer, while we need a long
						 * seed. therefore we double the bits. as the random
						 * generator that is build in in the update rule starts
						 * with making an integer by shifting 16 to the right,
						 * this means that the numbers are only used once, but
						 * in a different order. If this is not done, we get all
						 * values around 50% in our first draw. (long=64 bits,
						 * integer= 32 bits)
						 */
						long seed2 = (((long) rand2.random()) << 32)
								+ (long) rand2.random();
						currentIndividual.setRandomNumberGeneratorSeed(seed2);

						/*
						 * first characteristic is age . generate the
						 * configuration of the characteristic and of the
						 * simulation
						 */

						/*
						 * now generate the characteristic in the initial
						 * population
						 */
						float agestart = a;
						if (newborns)
							agestart = -generation;
						currentIndividual.luxeSet(1,
								new FloatCharacteristicValue(stepsInSimulation,
										1, agestart));

						// second characteristic is sex

						currentIndividual.luxeSet(2,
								new IntCharacteristicValue(stepsInSimulation,
										2, g));

						/*
						 * 
						 * 
						 * generate risk factors
						 */

						/*
						 * third and possibly fourth characteristic is risk
						 * factor now use index for this
						 */
						int characteristicIndex = 3;
						// if categorical then fill in proportionally;
						// randomly draw the last elements needed to get
						// exactly nSim persons
						boolean flagForRandomlyAdded = false;
						if (parameters.getRiskType() == 1
								|| parameters.getRiskType() == 3) {

							int c;

							for (c = 0; c < nClasses; c++) {
								if (i < cumulativeNSimPerClass[c])
									break;
							}
							currentRiskValue = c;
							/*
							 * if the loop was performed until the very end this
							 * means that i is equal or above highest cumulative
							 * value In that case we are going to draw randomly
							 */
							if (c == nClasses) {
								currentRiskValue = DynamoLib.draw(rest, rand);
								/*
								 * if a duration class value is draw, a duration
								 * should also be drawn
								 */
								if (currentRiskValue == parameters
										.getDurationClass())
									flagForRandomlyAdded = true;
							}

							currentIndividual.add(new IntCharacteristicValue(
									stepsInSimulation, characteristicIndex,
									currentRiskValue));

							characteristicIndex++;
						}

						float riskFactorValue = 0;
						if (parameters.getRiskType() == 2) {
							if (parameters.getRiskTypeDistribution()
									.compareToIgnoreCase("normal") == 0) {

								// simulate equi-probable points
								riskFactorValue = parameters.getMeanRisk()[a][g]
										+ parameters.getStdDevRisk()[a][g]
										* (float) DynamoLib.normInv((i + 0.5)
												/ nSim);
							} else {
								riskFactorValue = (float) parameters
										.getOffsetRisk()[a][g]
										+ (float) Math
												.exp(parameters.getMeanRisk()[a][g]
														+ parameters
																.getStdDevRisk()[a][g]
														* (float) DynamoLib
																.normInv((i + 0.5)
																		/ nSim));

							}
							currentIndividual.luxeSet(characteristicIndex,
									new FloatCharacteristicValue(
											stepsInSimulation,
											characteristicIndex,
											riskFactorValue));
							characteristicIndex++;
						}

						float currentDurationValue = 0;
						if (parameters.getRiskType() == 3) {
							if (flagForRandomlyAdded)
								currentDurationValue = DynamoLib.draw(
										parameters.getDuurFreq(a, g), rand);

							else {
								int relativeI;
								if (currentRiskValue == parameters
										.getDurationClass()) {
									int c;
									if (parameters.getDurationClass() == 0)
										relativeI = i;
									else
										relativeI = i
												- cumulativeNSimPerClass[parameters
														.getDurationClass() - 1];
									for (c = 0; c < nDuurClasses[a][g]; c++) {

										if (relativeI < cumulativeNSimPerDurationClass[c])
											break;
									}

									currentDurationValue = c;
									if (c == nDuurClasses[a][g])
										currentDurationValue = DynamoLib.draw(
												restDuration, rand);
								}
							}
							currentIndividual.luxeSet(characteristicIndex,
									new FloatCharacteristicValue(
											stepsInSimulation,
											characteristicIndex,
											currentDurationValue));

							characteristicIndex++;

						}

						/*
						 * 
						 * DISEASES /HEALTH STATE CHARACTERISTIC
						 * 
						 * generate initial probabilities of each disease state
						 * and put them in the array CharValues
						 */

						/*
						 * first calculate number of elements in the
						 * characteristic;
						 */
						float[] CharValues = calculateDiseaseStates(parameters,
								currentRiskValue, a, g, riskFactorValue,
								currentDurationValue);

						currentIndividual.luxeSet(characteristicIndex,
								new CompoundCharacteristicValue(
										stepsInSimulation, characteristicIndex,
										numberOfElements, CharValues));

						initialPopulation[0].addIndividual(currentIndividual);

						/*
						 * ******************************************************
						 * ***********************************
						 */

						/*
						 * GENERATION OF SCENARIO POPULATIONS
						 * 
						 * 
						 * This is done for 3 cases:
						 * 
						 * 1.- different initial risk factor prevalence, 
						 * 
						 * 2.- same initial prevalence: just copy reference
						 * population
						 * 
						 * 3. - daly scenarios: just copy reference population,
						 * but with different label, and different health status for newborns
						 * 
						 * 2. is done in a separate loop (not for this
						 * individual).
						 * 
						 * 
						 * The numbering of the population is in the order of
						 * the scenario's. after that the daly scenarios are placed
						 * 
						 * 
						 * /
						 * 
						 * 
						 * / 1.
						 * 
						 * 
						 * GENERATE SCENARIO POPULATIONS WITH DIFFERENT INITIAL
						 * PREVALENCE DISTRIBUTIONS 
						 */

						/*
						 * 
						 * The next part is for different initial prevalence distribution
						 */
						int newRiskValue = -1;
						float newDurationValue=-1;
						float newRiskFactorValue=-1;
						int currentscen = 0;
						int currentpop = 1; // population 0 is the reference
						// population
						
						

						while ((currentpop < nPopulations)) {

							
							
							/*  give the values to the individual */
							
									
								String riskFactorLevel="";
								if (riskType==1) riskFactorLevel=Float.toString(currentRiskValue);
								if (riskType==2) riskFactorLevel=Float.toString(riskFactorValue);
								if (riskType==3) riskFactorLevel=Float.toString(currentRiskValue)+"_"+Float.toString(currentDurationValue);
								currentIndividualS = new Individual("scen"
										+ (currentscen + 1), "ind_"
										+ individualNumber + "_"
										+ riskFactorLevel + "_" + currentpop);
								currentIndividualS
										.setRandomNumberGeneratorSeed(seed2);
								currentIndividualS.luxeSet(1,
										new FloatCharacteristicValue(
												stepsInSimulation, 1,
												(float) agestart));
								currentIndividualS.luxeSet(2,
										new IntCharacteristicValue(
												stepsInSimulation, 2, g));

								/*
								 * for continuous risk factor we just give the
								 * new distribution, meaning that everyone
								 * maintains his/her old ranking in the
								 * population
								 */

								if (parameters.getRiskType() == 2) {
									if (scenarioInfo.getIsNormal()[currentscen])
										newRiskFactorValue = scenarioInfo
												.getNewMean()[currentscen][a][g]
												+ scenarioInfo.getNewStd()[currentscen][a][g]

												* (float) DynamoLib
														.normInv((i + 0.5)
																/ nSim);
									// simulate equi-probable points
									else {
										newRiskFactorValue = (float) scenarioInfo
												.getNewOffset()[currentscen][a][g]
												+ (float) Math
														.exp(scenarioInfo
																.getNewMean()[currentscen][a][g]
																+ scenarioInfo
																		.getNewStd()[currentscen][a][g]
																* (float) DynamoLib
																		.normInv((i + 0.5)
																				/ nSim));
										;

									}

									currentIndividualS.luxeSet(3,
											new FloatCharacteristicValue(
													stepsInSimulation, 3,
													newRiskFactorValue));

								}

								/* if categorical risk factor */
								else {
									/* get transition matrix from old to new prevalences */

									float trans[][] = getPrevalenceTransitionMatrix(
											currentscen, a, g);
									/*
									 * now we draw using this transition matrix,
									 * but using the same seed for the same
									 * individual in this case, if two
									 * scenario's use the same initial
									 * prevalence rates, they will get the same
									 * numbers Also, if they are different, the
									 * random errors will be correlated so that
									 * their difference is not influenced by
									 * this
									 */
									// to do: make sure that all  riskfactor classes are present
									
									

									rand3.setSeed(seed2 + 5);
									newRiskValue = drawRiskfactorValueWithSupplyingAllValues(trans[currentRiskValue], currentpop);
											
											
									currentIndividualS
											.add(new IntCharacteristicValue(
													stepsInSimulation, 3,
													newRiskValue));

								}
								// if compound riskfactor */

								if (parameters.getRiskType() == 3) {
									newDurationValue = currentDurationValue;
									/*
									 * if the new value is the duration-class
									 * value, make the duration zero
									 */
									if ((int) (Integer) currentIndividualS
											.get(3).getValue(0) == scenarioInfo
											.getIndexDurationClass()
											&& currentRiskValue != scenarioInfo
													.getIndexDurationClass())
										newDurationValue = 0;
									currentIndividualS.luxeSet(4,
											new FloatCharacteristicValue(
													stepsInSimulation, 4,
													newDurationValue));
								}

								/*
								 * newborns and zero year olds are not bothered
								 * disease histories, so their diseases are
								 * recalculated from the new risk factor state
								 * but not the others: they keep the disease
								 * probabilities based on their old risk factors
								 * (before intervention)
								 */
								if (newborns || a == 0)

									CharValues = calculateDiseaseStates(
											parameters, newRiskValue, a, g,
											newRiskFactorValue,
											newDurationValue);

								currentIndividualS.luxeSet(characteristicIndex,
										new CompoundCharacteristicValue(
												stepsInSimulation,
												characteristicIndex,
												numberOfElements, CharValues));

								initialPopulation[currentpop]
										.addIndividual(currentIndividualS);

								/* also add the daly-scenarios */
								/* daly is een copy van het referentiescenario with the exception of the health state of newborns */
								
									if (this.scenarioInfo.getNumberOfDalyPopForThisScenario() [currentscen]>-1) {
									Individual dalyIndividual=deepCopy(currentIndividual);
									if (newborns || a == 0)
										dalyIndividual.luxeSet(characteristicIndex,
												new CompoundCharacteristicValue(
														stepsInSimulation,
														characteristicIndex,
														numberOfElements, CharValues));

										
										
									String riskLabel="";
								
									if (riskType==1||riskType==3) riskLabel= Integer.toString(newRiskValue);
									if (riskType==2) riskLabel=Float.toString(newRiskFactorValue);
									/* durations needs no labeling, as it is always zero after change */
								 /* but the category itself needs labeling */
								//	if (riskType==3) riskLabel=Integer.toString(newRiskValue)+"_dur_"+ Float.toString(newDurationValue);
									
									setLabelDalyIndividual(dalyIndividual,riskLabel);
									initialPopulation[this.scenarioInfo.getNumberOfDalyPopForThisScenario() [currentscen]]
														.addIndividual(dalyIndividual);
								}
								
			//* currentscen en currentpop zijn overblijfselen uit de tijd dat scenarios en populaties niet syncroon liepen. dit zou kunnen worden vereenvoudigd tot een for loop.

								currentscen++;
								currentpop++;

							
						} // end loop over populations of this individual

						/*
						 * 
						
						
						/*
						 * now the one year olds and newborns
						 */
						
				}// end sim loop
				;
				}
			}
		// end age and sex group

		/*
		 * 3. for same initial population, copy the population for the
		 * scenario's that have the same initial population
		 */

		/* only replace ref in the name by number of currentpop */

		

			for (int i1 = 1; i1 < nPopulations; i1++) {
				int scenNum = i1-1;
				if (!scenarioInfo.getInitialPrevalenceType()[scenNum]
						) {
					initialPopulation[i1] = deepCopy(initialPopulation[0]);

				}

			}
		
		/*
		 * if (newborns || !scenarioInfo.isWithNewBorns()) closeProgressBar();
		 */
		return initialPopulation;
	}

	/** calculates the numbers that are put in each duration category
	 * @param pop population number
	 * @param a age
	 * @param g gender
	 * @return array with cumulative numbers per class
	 */
	private int[] giveNumberPerDurationClassForSimulation(int pop,int a, int g, int nInDurationClass) {
		// NB all statements here are copied from (risktype==1)
		// so any fault found here should be mended there too.
		int[] nSimPerDurationClass;
		int[] cumulativeNSimPerDurationClass;
		cumulativeNSimPerDurationClass = new int[nDuurClasses[pop][a][g]];
		
		
		
		nSimPerDurationClass = new int[nDuurClasses[pop][a][g]];
		int c = 0;

		

			
		/*
		 * calculated the parts of nSim that have not yet been
		 * allocated
		 */
		float[] restDuration;	
		restDuration = new float[nDuurClasses[pop][a][g]];
		//float totrest = 0;
		//float totrestWithoutNegatives = 0;
		
		

		
		
		
		

		for (c = 0; c < nDuurClasses[pop][a][g]; c++) {
			
			
		
			nSimPerDurationClass[c] = (int) Math.floor(parameters
					.getDuurFreq()[a][g][c]
					* nInDurationClass);
			/*
			 * if no cases in duration class, add an extra case in
			 * case such persons exist
			 */
			if (nSimPerDurationClass[c] == 0
					&& nInDurationClass > 0
					&& parameters.getDuurFreq()[a][g][c] > 0) {
				nSimPerDurationClass[c] = 1;
				nSimNew[pop][a][g]++;
				nInDurationClass++;

			}
			restDuration[c] = parameters.getDuurFreq()[a][g][c]
					- (float) nSimPerDurationClass[c]
					/ (float) nInDurationClass;
			if (c > 0)
				cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c]
						+ cumulativeNSimPerDurationClass[c - 1];
			else
				cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c];
		}
		
		float totrestWithoutNegatives = 0;
		for (int c2 = 0; c2 < nDuurClasses[pop][a][g]; c2++) {
			
			if (restDuration[c2] < 0)
				restDuration[c2] = 0;
			else
				totrestWithoutNegatives += restDuration[c2];
		}
		 int sumNallocated=0;
		for (int c2 = 0; c2 < nDuurClasses[pop][a][g]; c2++) {
			restDuration[c2] = restDuration[c2]
					/ totrestWithoutNegatives;
			sumNallocated+=nSimPerDurationClass[c];
		}
		/*
		 * if extra points have been added (because otherwise
		 * durations would not have been present), adjust the arrays
		 * for the overall classes ==> this should be done in the calling method */
		
		
		   			
	     // fill with random drawn values
	         for (int add=1;add< nSimNew[pop][a][g]-sumNallocated;add++)
	        	nSimPerDurationClass[DynamoLib.draw(restDuration, rand)]++;
	        		
	        
	         for (c = 0; c < nClasses; c++) {
	 			if (c > 0)
	 				cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c]
	 						+ cumulativeNSimPerDurationClass[c - 1];
	 			else
	 				cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c];
	 		}
			
			
		
		return cumulativeNSimPerDurationClass;
	}

	/** calculates the numbers that are put in each risk factor category
	 * only looking at the prevalence rate of the population itself
	 * @param pop population number
	 * @param a age
	 * @param g gender
	 * @return array with cumulative numbers per class
	 */
	
	
	private int[] giveNumbersPerRiskClassForSimulation(int pop, int a, int g) {
		int[] cumulativeNSimPerClass =null ;
		
		cumulativeNSimPerClass = new int[nClasses];
		// calculate the number of persons in each risk factor class
		
		
		int[] nSimPerClass = new int[nClasses];
		// NB all statements here are copied to the next part
		// (risktype==3)
		// so any fault found here should be mended there too.
		

		int c = 0;

		for (c = 0; c < nClasses; c++) {
			nSimPerClass[c] = (int) Math.floor(parameters
					.getPrevRisk()[a][g][c]
					* nSimNew[pop][a][g]);
			/*
			 * if zero case in a class, add a person to the
			 * simulated population, in case such persons exist in
			 * the population
			 */
			if (nSimPerClass[c] == 0
					&& parameters.getPrevRisk()[a][g][c] > 0) {
				nSimPerClass[c] = 1;
				nSimNew[pop][a][g]++;

				/*
				 * in that case, the rest from other categories only
				 * needs to be distributed differently over the
				 * other categories, as this categorie already has a
				 * large part of the total as needed
				 * 
				 * We therefore recalculate the earlier
				 */

			}		
		}
		/*
		 * calculated the parts of nSim that have not yet been
		 * allocated
		 */
			
		
		float[] rest  = new float[nClasses];
		float totrest = 0;
		float totrestWithoutNegatives = 0;
		for (c = 0; c < nClasses; c++) {
			/*
			 * rest is the difference between the prevalence that is
			 * the aim to reach, and the prevalence that is reached
			 * already the rest of the data are drawn proportionally
			 * to that figure
			 */
			rest[c] = parameters.getPrevRisk()[a][g][c]
					- (float) nSimPerClass[c]
					/ (float) nSimNew[pop][a][g];
			totrest += rest[c];
			/*
			 * is totrest is negative, it should be taken into
			 * account into totrest (part that is to be distributed
			 * later), but this should not be used in the
			 * distribution itself
			 */
			if (rest[c] < 0)
				rest[c] = 0;
			else
				totrestWithoutNegatives += rest[c];

		}
        int sumNallocated=0;
		for (c = 0; c < nClasses; c++) {
			rest[c] = rest[c] / totrestWithoutNegatives;
			sumNallocated+=nSimPerClass[c];
		}

		
     // fill with random drawn values
         for (int add=1;add< nSimNew[pop][a][g]-sumNallocated;add++)
        	nSimPerClass[DynamoLib.draw(rest, rand)]++;
        		
        
         for (c = 0; c < nClasses; c++) {
 			if (c > 0)
 				cumulativeNSimPerClass[c] = nSimPerClass[c]
 						+ cumulativeNSimPerClass[c - 1];
 			else
 				cumulativeNSimPerClass[c] = nSimPerClass[c];
 		}
		
		
		
		
		return cumulativeNSimPerClass;
	}

	
	private int[] adaptNumbersPerRiskClassForSimulationForTransitions(int[] cumulativeNPerSimClass, float [] oldPrev, float [] newPrev ) throws DynamoConfigurationException, DynamoInconsistentDataException{
		
		//* if there are "transitions" from old to new prevalence, there must be at least one individual per transition.
		
		
		    float[]R=new float [oldPrev.length];
		    Arrays.fill(R,1);
				float [][] toChange = NettTransitionRateFactory
				.makeNettTransitionRates(
						oldPrev,
						newPrev,
						0, R);

				// check if enough 
				
				for (int s=0; s<oldPrev.length;s++)	
		return cumulativeNPerSimClass;
		
	}
	
	
	
	
	
	
	private int[] areFilled = new int [this.nPopulations];
	
	private int drawRiskfactorValueWithSupplyingAllValues(float[] fs,  int popnr) {
		// TODO Auto-generated method stub
		int fillValue=0;
		if (areFilled[popnr]<this.nClasses) {
			fillValue=areFilled[popnr];
			areFilled[popnr]++;
			
			
			}
		else 
		fillValue=DynamoLib.draw(fs, rand3);
		
		
		return fillValue;
	}
	
	
	
private int[] areFilledDuration = new int [this.nPopulations];
	
	private int drawDurationValueWithSupplyingAllValues(float[] fs,  int popnr) {
		// TODO Auto-generated method stub
		int fillValue=0;
		if (areFilledDuration[popnr]<this.nClasses) {
			fillValue=areFilled[popnr];
			areFilledDuration[popnr]++;
			
			
			}
		else 
		fillValue=DynamoLib.draw(fs, rand3);
		
		
		return fillValue;
	}

	private void setLabelDalyIndividual(Individual dalyIndividual, String riskLabel) {

/* for the one for all population, elements [2] and [3] (=3e+4e)  are used for indicating to and from; here we use 4 and 5 (duration) for the daly info */
		
		String oldLabel=dalyIndividual.getLabel();
		
		 String label = "DALY"; 
		 /* split at the place of the  underscore 			 */
		 /* elements are: "ind", indno, currentriskfactor, duration or to (optional), popnumber */
		 /* for the reference individual continuous risk factor it is only ind, indno en ref */
		 String delims="_";
		 String[] tokens = oldLabel.split(delims);
		 if (tokens[2].equalsIgnoreCase("ref")) label+=tokens[0] + "_"+tokens[1] + "_"+"DALY" + "_"+"cont" + "_";
			/* put together again */
		 else for (int i = 0; i < 4; i++){
			 label += tokens[i] + "_";		}	
			 label += riskLabel;
			 dalyIndividual.setLabel(label);
	}

	private float[][] getPrevalenceTransitionMatrix(int currentscen, int a,
			int g) {
		return this.prevalenceTransitionMatrix[currentscen][a][g];
	}

	private void makeTransitionMatrixForPrevalence(ModelParameters parameters,
			ScenarioInfo scenarioInfo) throws DynamoInconsistentDataException, DynamoConfigurationException {
		int nCat = parameters.getPrevRisk()[0][0].length;
		float[] RR = new float[nCat];
		Arrays.fill(RR, 1);
		this.prevalenceTransitionMatrix = new float[scenarioInfo
				.getNScenarios()][96][2][nCat][nCat];
		int agemax = scenarioInfo.getMaxSimAge();
		if (agemax > 95)
			agemax = 95;

		int agemin = scenarioInfo.getMinSimAge();

		for (int a = agemin; a < agemax + 1; a++)
			for (int g = 0; g < 2; g++)
				for (int s = 0; s < scenarioInfo.getNScenarios(); s++) {
					if (scenarioInfo.getTransitionType()[s]
							&& scenarioInfo.getInitialPrevalenceType()[s]) {
						float oldPrev[] = parameters.getPrevRisk()[a][g];
						float newPrev[] = scenarioInfo.getNewPrevalence()[s][a][g];
						this.prevalenceTransitionMatrix[s][a][g] = NettTransitionRateFactory
								.makeNettTransitionRates(oldPrev, newPrev, 0,
										RR);

					}
				}
	}

	private Population deepCopy(Population population) {

		Population copy = new Population(population.getElementName(),
				population.getLabel());
		String label = "";
		String delims = "[_]";
		String elementName = null;
		for (Individual individual : population) {
			label = individual.getLabel();
			elementName = individual.getElementName();
			/*
			 * if (this.riskType != 2) {
			 * 
			 * String oldLabel = label; label = ""; / split at the place of the
			 * underscore
			 */
			// String[] tokens = label.split(delims);
			/* put together again, but without the last element */
			// for (int i = 0; i < tokens.length - 1; i++)
			// label = label + tokens[i] + "_";
			// int riskValue = ((IntCharacteristicValue) individual.get(3))
			// .getRijtje()[0];
			// label = label + riskValue + "_" + riskValue;
			// }
			Individual currentIndividual = new Individual(elementName, label);
			currentIndividual.setRandomNumberGeneratorSeed(individual
					.getRandomNumberGeneratorSeed());
			currentIndividual.luxeSet(1,
					deepCopy((FloatCharacteristicValue) individual.get(1)));
			currentIndividual.luxeSet(2,
					deepCopy((IntCharacteristicValue) individual.get(2)));
			if (this.riskType == 2)
				currentIndividual.luxeSet(3,
						deepCopy((FloatCharacteristicValue) individual.get(3)));
			if (this.riskType != 2)
				currentIndividual.luxeSet(3,
						deepCopy((IntCharacteristicValue) individual.get(3)));
			int lastIndex = 4;
			if (this.riskType == 3) {
				currentIndividual.luxeSet(4,
						deepCopy((FloatCharacteristicValue) individual.get(4)));
				lastIndex = 5;
			}
			currentIndividual.luxeSet(lastIndex,
					deepCopy((CompoundCharacteristicValue) individual
							.get(lastIndex)));
			copy.addIndividual(currentIndividual);
		}

		return copy;
	}
	
	
	private Individual deepCopy(Individual individual) {

		
		 
			String label = individual.getLabel();
			String elementName = individual.getElementName();
			
			Individual copy = new Individual(elementName, label);
			copy.setRandomNumberGeneratorSeed(individual
					.getRandomNumberGeneratorSeed());
			copy.luxeSet(1,
					deepCopy((FloatCharacteristicValue) individual.get(1)));
			copy.luxeSet(2,
					deepCopy((IntCharacteristicValue) individual.get(2)));
			if (this.riskType == 2)
				copy.luxeSet(3,
						deepCopy((FloatCharacteristicValue) individual.get(3)));
			if (this.riskType != 2)
				copy.luxeSet(3,
						deepCopy((IntCharacteristicValue) individual.get(3)));
			int lastIndex = 4;
			if (this.riskType == 3) {
				copy.luxeSet(4,
						deepCopy((FloatCharacteristicValue) individual.get(4)));
				lastIndex = 5;
			}
			copy.luxeSet(lastIndex,
					deepCopy((CompoundCharacteristicValue) individual
							.get(lastIndex)));
			
		

		return copy;
	}
	
	
	

	private FloatCharacteristicValue deepCopy(FloatCharacteristicValue original) {
		float[] rijtje = original.getRijtje();
		FloatCharacteristicValue copy = new FloatCharacteristicValue(
				rijtje.length - 1, original.getIndex(), rijtje[0]);
		return copy;
	}

	private IntCharacteristicValue deepCopy(IntCharacteristicValue original) {
		int[] rijtje = original.getRijtje();
		IntCharacteristicValue copy = new IntCharacteristicValue(
				rijtje.length - 1, original.getIndex(), rijtje[0]);
		return copy;
	}

	private CompoundCharacteristicValue deepCopy(
			CompoundCharacteristicValue original) {
		float[][] rijtje = original.getRijtje();
		float[] zerovalues = new float[rijtje[0].length];
		for (int i = 0; i < zerovalues.length; i++)
			zerovalues[i] = rijtje[0][i];
		CompoundCharacteristicValue copy = new CompoundCharacteristicValue(
				rijtje.length, original.getIndex(), rijtje[0].length,
				zerovalues);
		return copy;
	}

	// TODO Auto-generated method stub

	/**
	 * @param parameters
	 * @param currentRiskValue
	 * @param a
	 * @param g
	 * @param riskFactorValue
	 * @param currentDurationValue
	 * @param numberOfElements
	 * @return
	 */
	private float[] calculateDiseaseStates(ModelParameters parameters,
			int currentRiskValue, int a, int g, float riskFactorValue,
			float currentDurationValue) {

		float[] CharValues = new float[numberOfElements];
		if (a == 36) {
			int stop = 0;
			stop++;
		}
		int elementIndex = 0;
		for (int cluster = 0; cluster < parameters.getNCluster(); cluster++) {

			/*
			 * first make the logit of the probability for all diseases in this
			 * cluster based only on riskfactor information
			 */
			double[] logitDisease = new double[parameters.getClusterStructure()[cluster]
					.getNInCluster()];
			float[] relativeRisksCont = parameters.getRelRiskContinue(a, g);
			for (int d = 0; d < parameters.getClusterStructure()[cluster]
					.getNInCluster(); d++) {

				/*
				 * for each disease calculate its contribution to the
				 * probability of the combination
				 */
				// zie boven doe eerst een
				// analyse van conditionele
				// onafhankelijkheden
				int dnumber = parameters.getClusterStructure()[cluster]
						.getDiseaseNumber()[d];

				/* only needed if disease is present */
				if (baselineOdds[dnumber] != 0)

				{
					logitDisease[d] = Math.log(baselineOdds[dnumber]);
					if (parameters.getRiskType() == 1)
						logitDisease[d] += Math
								.log(relativeRisksCat[currentRiskValue][dnumber]);
					if (parameters.getRiskType() == 2)
						logitDisease[d] += Math.log(Math.pow(
								relativeRisksCont[dnumber], riskFactorValue
										- parameters.getRefClassCont()));
					if (parameters.getRiskType() == 3) {
						if (currentRiskValue != parameters.getDurationClass())
							logitDisease[d] += Math
									.log(relativeRisksCat[currentRiskValue][dnumber]);
						else
							logitDisease[d] += Math
									.log((relRiskDuurBegin[dnumber] - relRiskDuurEnd[dnumber])
											* Math.exp(-currentDurationValue
													* alphaDuur[dnumber])
											+ relRiskDuurEnd[dnumber]);
					}
				}
			}
			/*
			 * CALCULATE PROBABILITY OF EACH COMBINATION OF DISEASES number of
			 * combinations= 2^Ndiseases
			 */
			int nDiseases = parameters.getClusterStructure()[cluster]
					.getNInCluster();
			int d1number;

			if (!parameters.getClusterStructure()[cluster]
					.isWithCuredFraction())
				for (int combi = 1; combi < Math.pow(2, nDiseases); combi++) {
					/*
					 * loop over diseases in combi; p (D en E en G)=
					 * P(D|E,G)P(E)P(G) (E en G onafhankelijk) log odds
					 * (D=1|E=1,G=1)= logRR(D|E)+logRR(D|
					 * G)+logoddsBaseline(D)--> P(D=1|E=1,G=1)
					 * 
					 * Nu D is gemeenschappelijke oorzaak van E en G p (D en E
					 * en G)= P(E|D)P(G|D)P(D) log odds (E=1|D=1)=
					 * logRR(E|D)+logoddsBaseline(E)--> P(E=1|D=1) log odds
					 * (G=1|D=1)= logRR(G|D)+logoddsBaseline(G)--> P(G=1|D=1)
					 * 
					 * in het algemeen: p-combi= product [P(each dep disease|all
					 * independent diseases)]product[P(independent disease)]
					 * dus: GENERAL: product [p (disease |causes (if any)]
					 */

					double probCombi = 1;

					// TODO: nog checken

					/*
					 * if this is a independent disease then this is all. if
					 * dependent disease, then relative risks should be added
					 * for those causal disease that are equal to 1 in the combi
					 * 
					 * must be: probability conditional on not having disease d
					 * 
					 * we use RRextended and therefore can consider all disease
					 * as causes of each other as RR=1 if this is not the case
					 * // TODO check if RRextended is made OK //
					 */
					/*
					 * now loop throught all diseases in the combi, look if they
					 * are zero or one and calculate the probability given
					 * causal diseases
					 */
					for (int d1 = 0; d1 < nDiseases; d1++) {
						d1number = parameters.getClusterStructure()[cluster]
								.getDiseaseNumber()[d1];
						double probCurrent = 1; /*
												 * probCurrent is the
												 * probability of the current
												 * disease in the combination
												 * (d1)
												 */
						/* look if d1 is one or zero in the combination */
						if ((combi & (1 << d1)) == (1 << d1)) {
							/* is one */
							/* so add prob(=1) given causes */

							if (parameters.getClusterStructure()[cluster]
									.getDependentDisease()[d1]) {
								if (baselineOdds[d1number] != 0) {
									double logitCurrent = logitDisease[d1];

									for (int d2 = 0; d2 < parameters
											.getClusterStructure()[cluster]
											.getNInCluster(); d2++) {
										if ((combi & (1 << d2)) == (1 << d2)) {

											logitCurrent += Math
													.log(RRdiseaseOnDisease[cluster][d2][d1]);
										}
										probCurrent = 1 / (1 + Math
												.exp(-logitCurrent));
									}
								} else
									probCurrent = 0;

							} else
							/* == independent disease */
							if (baselineOdds[d1number] != 0)
								probCurrent = 1 / (1 + Math
										.exp(-logitDisease[d1]));
							else
								probCurrent = 0;

						}/* end if disease d1 ==1 */else {
							/* now if d1 is zero in combination */;
							if (baselineOdds[d1number] != 0) {

								if (parameters.getClusterStructure()[cluster]
										.getDependentDisease()[d1]) {
									double logitCurrent = logitDisease[d1];

									for (int d2 = 0; d2 < parameters
											.getClusterStructure()[cluster]
											.getNInCluster(); d2++)
										if ((combi & (1 << d2)) == (1 << d2))
											logitCurrent += Math
													.log(RRdiseaseOnDisease[cluster][d2][d1]);
									/*
									 * NB now exp(+x) as this is 1-p in stead of
									 * p
									 */
									probCurrent = 1 / (1 + Math
											.exp(logitCurrent));
								} else
									probCurrent = 1 / (1 + Math
											.exp(logitDisease[d1]));
							} else

							{
								/* if baseline prevalence=0 than p-no-disease=1 */
								probCurrent = 1;
							}

						}
						probCombi *= probCurrent;
					} // end loop over d1
					float value = (float) probCombi;

					CharValues[elementIndex] = value;
					elementIndex++;

				} // end loop over combi
			else {/* is withCuredFraction */
				d1number = parameters.getClusterStructure()[cluster]
						.getDiseaseNumber()[0];
				if (baselineOdds[d1number] != 0)
					CharValues[elementIndex] = (float) (1 / (1 + Math
							.exp(-logitDisease[0])));
				else
					CharValues[elementIndex] = 0;
				elementIndex++;
				if (baselineOdds[d1number + 1] != 0)
					CharValues[elementIndex] = (float) (1 / (1 + Math
							.exp(-logitDisease[1])));
				else
					CharValues[elementIndex] = 0;
				elementIndex++;

			}

		} // end loop over clusters

		// tot slot nog characteristiek voor
		// survival;
		CharValues[elementIndex] = 1;
		elementIndex++;

		if (elementIndex != numberOfElements && !incidenceDebug)
			log.warn("number of element written does not"
					+ "fit number calculated, that is : " + elementIndex
					+ " not equal " + numberOfElements);
		return CharValues;
	}

	private void extractDiseaseDataForThisAgeSexGroup(int a, int g) {
		baselineOdds = parameters.getBaselinePrevalenceOdds(a, g);
		relativeRisksCat = parameters.getRelRiskClass(a, g);
		relativeRisksCont = parameters.getRelRiskContinue(a, g);
		relRiskDuurBegin = parameters.getRelRiskDuurBegin(a, g);
		relRiskDuurEnd = parameters.getRelRiskDuurEnd(a, g);
		alphaDuur = parameters.getAlphaDuur(a, g);
		RRdiseaseOnDisease = parameters.getRelRiskDiseaseOnDisease(a, g);
	}

	/**
	 * @param parameters
	 * @return
	 */
	int getNumberOfDiseaseStateElements(ModelParameters parameters) {
		int numberOfElements = 1;

		for (int c = 0; c < parameters.getNCluster(); c++) {
			DiseaseClusterStructure structure = parameters
					.getClusterStructure()[c];
			if (structure.getNInCluster() == 1)
				numberOfElements++;
			else if (structure.isWithCuredFraction())
				numberOfElements += 2;
			else
				numberOfElements += Math.pow(2, structure.getNInCluster()) - 1;
		}
		return numberOfElements;
	}

	public void writeToXMLFile(Population population, int stepNumber,
			File xmlFileName) throws ParserConfigurationException,
			TransformerException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = (DocumentBuilder) dbfac
				.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		String elementName = population.getElementName();
		Element element = document.createElement(population.xmlElementName);
		Element nameElement = document
				.createElement(population.xmlLabelAttributeName);
		nameElement.setTextContent(elementName);
		element.appendChild(nameElement);
		String label = population.getLabel();
		if (label != null && !"".equals(label)) {
			element.setAttribute("lb", label);
		}
		document.appendChild(element);
		Individual individual;
		while ((individual = population.nextIndividual()) != null) {
			DOMIndividualWriter.generateDOM(individual, stepNumber, element);
		}
		boolean isDirectory = xmlFileName.isDirectory();
		boolean canWrite = xmlFileName.canWrite();
		try {
			boolean isNew = xmlFileName.createNewFile();
			if (!isDirectory && (canWrite || isNew)) {
				Source source = new DOMSource(document);
				StreamResult result = new StreamResult(xmlFileName);
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.transform(source, result);
			}
		} catch (IOException e) {
			log.warn("File exception: " + e.getClass().getName() + " message: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void makeProgressBar(int length) {
		// Shell shell = new Shell(parentShell);
		// shell.setText("Construction of initial population ....");
		// shell.setLayout(new FillLayout());
		// shell.setSize(600, 50);
		//
		// this.bar = new ProgressBar(shell, SWT.NULL);
		// this.bar.setBounds(10, 10, 200, 32);
		// this.bar.setMinimum(0);
		//
		// shell.open();
		progressbar = dsi
				.createProgressIndicator("Construction of initial population ....");

		int size = (length);
		int step = 1;
		// this.bar.setMaximum(size / step);
		progressbar.setMaximum(size / step);
		/* initialize populations */
		// this.bar.setSelection(0);
		progressbar.update(0);
	}

	public void updateProgressBar() {

		// int state = this.bar.getSelection();
		int state = progressbar.getPosition();
		state++;
		// this.bar.setSelection(state);
		progressbar.update(state);

	}

	public void closeProgressBar() {
		// this.bar.getShell().close();
		progressbar.dispose();

	}
	/**
	 * @param p
	 * @param sim
	 * @param simName
	 * @param seed
	 * @param newborns
	 */




/** checks if this is a situation where the first n categories are filled in the old prevalence, and the second n in the new prevalence with exactly the same numbers
 * @param oldPrev
 * @param newPrev
 * @return
 */
private boolean isZeroType(float[] oldPrev, float[] newPrev) { 
	boolean isOK=true;
	int ncat=oldPrev.length;
	if (2*Math.floor(ncat/2)!=ncat) isOK=false; //oneven kan niet
	for (int i = 0; i<(ncat/2);i++){		
		if(oldPrev[i] != newPrev[i+(ncat/2)]) isOK=false;}
	
	// TODO Auto-generated method stub
	return isOK;
}
}
