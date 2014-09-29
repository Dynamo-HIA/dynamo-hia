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

	int[][] nDuurClasses = new int[96][2];

	int[][] nSimNew = new int[96][2];

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
	private boolean[] isOneForAllPopulation;
	private ModelParameters parameters;
	private ScenarioInfo scenarioInfo;
	private double[] baselineOdds;
	private float[] relRiskDuurBegin;
	private float[] relRiskDuurEnd;
	private float[][] relativeRisksCat;
	private float[] relativeRisksCont;
	private float[] alphaDuur;
	private float[][][] RRdiseaseOnDisease;
	private float[][][][][] transitionMatrix;
	private int minage;
	private int ming;

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
	 * @param newborns
	 * @param maxGeneration
	 * @param minGeneration
	 * @param maxg
	 * @param ming
	 * @param maxage
	 * @param minage
	 * @throws DynamoInconsistentDataException
	 * @throws DynamoConfigurationException
	 */
	public InitialPopulationFactory(ModelParameters params,

	ScenarioInfo scenInfo, DynSimRunPRInterface dsi, int minage, int maxage,
			int ming, int maxg) throws DynamoInconsistentDataException,
			DynamoConfigurationException {

		initializeInitialPopulationFactory(params,

		scenInfo, minage, maxage, ming, maxg);
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
					log.warn("trying to write non existing initial population for scenario "
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
	 * @param newborns
	 * @param maxGeneration
	 * @param minGeneration
	 * @param maxg
	 * @param ming
	 * @param maxage
	 * @param minage
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

	ScenarioInfo scenInfo, int minage, int maxage, int ming, int maxg)
			throws DynamoInconsistentDataException,
			DynamoConfigurationException {

		/*
		 * at this moment:
		 * 
		 * the method should be run twice: once for the existing population, and
		 * once for the newborns
		 */
		/* Make some indexes that are needed */
		this.parameters = params;
		this.scenarioInfo = scenInfo;
		this.minage = minage;
		this.ming = ming;
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

		rand = new Random(seed + minage * 2510689 + ming * 753209); // used to
																	// draw the
																	// initial
																	// population
		rand2 = new MTRand(seed + 111777444 + minage * 237 + ming * 39927); /*
																			 * used
																			 * to
																			 * generate
																			 * seeds
																			 * which
																			 * are
																			 * entered
																			 * as
																			 * information
																			 * with
																			 * each
																			 * generated
																			 * individual
																			 * ,
																			 * and
																			 * will
																			 * be
																			 * used
																			 * by
																			 * the
																			 * update
																			 * rules
																			 * /
																			 * /
																			 * TODO
																			 * inlezen
																			 * baseDir
																			 * aanpassen
																			 * aan
																			 * userinterface
																			 */
		rand3 = new Random(seed + 2945032 + minage * 2057 + ming * 89452); // used
																			// to
																			// draw
																			// the
																			// scenario
		// population
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

		/* initialize the ranges for the populations to generate */

		/*
		 * see which riskfactor classes should be changed into another class in
		 * the one-for-all scenario population
		 */
		int agemax = maxage;
		if (agemax > 95)
			agemax = 95;
		int agemin = minage;

		// voor newborns is age 0 included

		if (riskType != 2) {
			int nCat = parameters.getPrevRisk()[0][0].length;
			float[] RR = new float[nCat];

			// initialize arrays;
			Arrays.fill(RR, 1);

			this.shouldChangeInto = new boolean[agemax + 1][2][nCat][nCat];
			this.transitionMatrix = new float[scenInfo.getNScenarios()][agemax + 1][2][nCat][nCat];
			for (int a = agemin; a < agemax + 1; a++)
				for (int g = ming; g <= maxg; g++)
					for (int r1 = 0; r1 < nCat; r1++)
						for (int r2 = 0; r2 < nCat; r2++)
							this.shouldChangeInto[a][g][r1][r2] = false;

			for (int a = agemin; a < agemax + 1; a++)
				for (int g = ming; g <= maxg; g++)
					for (int s = 0; s < scenarioInfo.getNScenarios(); s++) {

						float oldPrev[] = parameters.getPrevRisk()[a][g];
						float newPrev[] = scenarioInfo.getNewPrevalence()[s][a][g];

						try {
							this.transitionMatrix[s][a][g] = NettTransitionRateFactory
									.makeNettTransitionRates(oldPrev, newPrev,
											0, RR);
						} catch (DynamoConfigurationException E) {
							throw new DynamoConfigurationException(
									E.getMessage()
											+ "\nThis error occurs when making the initial population");

						}

						for (int r1 = 0; r1 < nCat; r1++)
							for (int r2 = 0; r2 < nCat; r2++) {
								if (r1 != r2
										&& this.transitionMatrix[s][a][g][r1][r2] > 0)
									shouldChangeInto[a][g][r1][r2] = true;
							}

					}
		}

		/*
		 * a new cohort of newborns is generated for each step in the
		 * simulation, the existing population needs to be generated only once
		 */

		/* count the number of duration categories in each age class */

		if (parameters.getRiskType() == 3) {
			for (int a = agemin; a <= agemax; a++)
				for (int g = ming; g <= maxg; g++) {
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
	 * @throws DynamoConfigurationException
	 */
	public Population[] manufactureInitialPopulation(int agemin, int agemax,
			int gmin, int gmax,

			int generationMin, int generationMax, boolean newborns)
			throws DynamoConfigurationException {

		Population[] initialPopulation = new Population[nPopulations];

		for (int scen = 0; scen < nPopulations; scen++) {
			initialPopulation[scen] = new Population("Population nr " + scen,
					null);
		}

		int[] cumulativeNSimPerClass;
		int[] nSimPerClass;
		int[] nSimPerDurationClass = null;
		int[] cumulativeNSimPerDurationClass = null;
		int[][] cumulativeNSimPerScenarioClass = new int[this.scenarioInfo
				.getNScenarios()][nClasses];
		int numberInClass = -10000; // gives the individual number within the
									// same riskfactor class
		// initialize to a value that will give problems if accidentally used
		int[][] nSimPerScenarioClass = new int[this.scenarioInfo
				.getNScenarios()][nClasses];
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
				/*
				 * this is changed in dynamo-3 as we no longer do a one-for-all
				 * scenario in stead the idea of upweighting the scenarios is
				 * applied to all scenarios this means that we are going to
				 * simulate all possible combinations of changes from the
				 * reference to the alternative scenario
				 */

				/*
				 * if (Math.abs(Math.round(a / 10) - a / 10) < 0.01 && g == 1)
				 * 
				 * updateProgressBar();
				 */

				// calculate the number of persons in each risk factor class
				nSimPerClass = new int[nClasses];
				cumulativeNSimPerClass = new int[nClasses];

				if (parameters.getRiskType() == 1) {
					int[] minimumPerClass = getMinimumPerClass(a, g);
					nSimPerClass = makeNperClass(
							parameters.getPrevRisk()[a][g], minimumPerClass,
							nSim);
					cumulativeNSimPerClass = getCumulative(nSimPerClass);

				}

				// for duration class, everyone new goes to duration = 0 in the
				// scenario
				// so enough individuals are simulated for the other classes
				// everyone old , however, should be Nduur as many, where Nduur
				// is the number of
				// duration-times that are filled
				int nDuur = 0;

				if (parameters.getRiskType() == 3) {
					int[] minimumPerClass = getMinimumPerClass(a, g);
					nDuur = getNinDurationClass(a, g);
					minimumPerClass[parameters.getDurationClass()] *= nDuur;
					nSimPerClass = makeNperClass(
							parameters.getPrevRisk()[a][g], minimumPerClass,
							nSim);
					cumulativeNSimPerClass = getCumulative(nSimPerClass);
					// for the durationclass, the minimum is 1 for each existing
					// duration
					int[] nMinimumDuur = getNMinimumDurationClass(a, g);
					nSimPerDurationClass = makeNperClass(
							parameters.getDuurFreq()[a][g], nMinimumDuur,
							nSimPerClass[parameters.getDurationClass()]);
					;
					cumulativeNSimPerDurationClass = getCumulative(nSimPerDurationClass);
				}
				nSimNew[a][g] = cumulativeNSimPerClass[nClasses - 1];
				/*
				 * repeat this for categorical riskfactors with duration, where
				 * the same "trick" is applied both to the categories, and the
				 * duration within categories
				 */

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
				int currentRiskValue = -1;
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
						Individual currentIndividualS = null; /*
															 * to be used in the
															 * scenario
															 * -populations
															 */

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
						
						if (parameters.getRiskType() == 1
								|| parameters.getRiskType() == 3) {

							int c = getCurrentValue(cumulativeNSimPerClass, i);

							// we have to keep track if which number the
							// individual
							// has within the risk class, because of the new
							// structuring of the
							// scenario populations

							if (c > currentRiskValue) {
								numberInClass = 0;
								int[][] minimumScenPerScenarioClass = getMinimumPerScenarioTransition(
										a, g, c );

								for (int scen = 0; scen < this.scenarioInfo
										.getNScenarios(); scen++) {
									nSimPerScenarioClass[scen] = makeNperClass(
											this.transitionMatrix[scen][a][g][c],
											minimumScenPerScenarioClass[scen],
											nSimPerClass[c]);
									cumulativeNSimPerScenarioClass[scen] = getCumulative(nSimPerScenarioClass[scen]);
									currentRiskValue = c;
								}
							}

							else
								numberInClass++;

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
								c = getCurrentValue(
										cumulativeNSimPerDurationClass,
										relativeI);

								currentDurationValue = c;

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
						 * 1.- different initial risk factor prevalence, not
						 * handled through one-for-all-population
						 * 
						 * * 2 (old 3).- same initial prevalence: just copy
						 * reference population
						 * 
						 * 3 (old 4). - daly scenarios: just copy reference
						 * population, but with different label, and different
						 * health status for newborns
						 * 
						 * 2. is done in a separate loop (not for this
						 * individual).
						 * 
						 * 
						 * The numbering of the population is in the order of
						 * the scenario's. So if the one-for-all scenario is
						 * only the 4th scenario (=scennum=3), population 4 will
						 * be the one-for-all population (population 0 is
						 * reference population)
						 * 
						 * 
						 * /
						 * 
						 * 
						 * / 1a.
						 * 
						 * 
						 * GENERATE SCENARIO POPULATIONS WITH DIFFERENT INITIAL
						 * PREVALENCE DISTRIBUTIONS for continuous (basically
						 * old code)
						 */

						/*
						 * 
						 * There are two cases for which this needs to be done:
						 * 1a. for continuous riskfactors with different initial
						 * populations a
						 */
						int newRiskValue = -1;
						float newDurationValue = -1;
						float newRiskFactorValue = -1;
						int currentscen = 0;
						int currentpop = 1; // population 0 is the reference
						// population
						// in this new version currentpop is simply
						// currentscen+1 (at least for the
						// non daly scenario's

						while (currentpop < scenarioInfo
								.getNPopulationsWithoutDalys()
								&& scenarioInfo.getInitialPrevalenceType()[currentscen]) {

							if (riskType == 2) {

								/*
								 * look for next scenario that has an different
								 * initial prevalence (initialprevalencetype)
								 * and is not an all for One Population type
								 */
								// bereken verdeling aantal individuen over de
								// nieuwe risicofactor klassen bij dit scenario
								// en risicofactor
								// the minimum number for each change is 1,
								// except when there are no changes
								int[] minimumN = new int[shouldChangeInto[a][g].length];
								for (int r = 0; r < shouldChangeInto[a][g].length; r++) {
									if (shouldChangeInto[a][g][currentRiskValue][r]) {
										minimumN[r] = 1;
									} else
										minimumN[r] = 0;
									if (parameters.getRiskType() == 3)
										minimumN[r] *= nDuur;
								}
								int[] nIndividualsPerNewClass = makeNperClass(
										transitionMatrix[currentscen][a][g][currentRiskValue],
										minimumN,
										nSimPerClass[currentRiskValue]);

								for (int i1 = currentscen; i1 < scenarioInfo
										.getNScenarios(); i1++) {

									String riskFactorLevel = "";

									riskFactorLevel = Float
											.toString(riskFactorValue);

									currentIndividualS = new Individual("scen"
											+ (currentscen + 1), "ind_"
											+ individualNumber + "_"
											+ riskFactorLevel + "_"
											+ currentpop);
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
									 * for continuous risk factor we just give
									 * the new distribution, meaning that
									 * everyone maintains his/her old ranking in
									 * the population
									 */

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

									/*
									 * newborns and zero year olds are not
									 * bothered disease histories, so their
									 * diseases are recalculated from the new
									 * risk factor state but not the others:
									 * they keep the disease probabilities based
									 * on their old risk factors (before
									 * intervention)
									 */
									if (newborns || a == 0)

										CharValues = calculateDiseaseStates(
												parameters, newRiskValue, a, g,
												newRiskFactorValue,
												newDurationValue);

									currentIndividualS.luxeSet(
											characteristicIndex,
											new CompoundCharacteristicValue(
													stepsInSimulation,
													characteristicIndex,
													numberOfElements,
													CharValues));

									initialPopulation[currentpop]
											.addIndividual(currentIndividualS);

									/* also add the daly-scenarios */
									/*
									 * daly is een copy van het
									 * referentiescenario with the exception of
									 * the health state of newborns
									 */

									if (this.scenarioInfo
											.getNumberOfDalyPopForThisScenario()[currentscen] > -1) {
										Individual dalyIndividual = deepCopy(currentIndividual);
										if (newborns || a == 0)
											dalyIndividual
													.luxeSet(
															characteristicIndex,
															new CompoundCharacteristicValue(
																	stepsInSimulation,
																	characteristicIndex,
																	numberOfElements,
																	CharValues));

										String riskLabel = "";
										riskLabel = Float
												.toString(newRiskFactorValue);
										/*
										 * durations needs no labeling, as it is
										 * always zero after change
										 */
										/*
										 * but the category itself needs
										 * labeling
										 */
										// if (riskType==3)
										// riskLabel=Integer.toString(newRiskValue)+"_dur_"+
										// Float.toString(newDurationValue);

										setLabelDalyIndividual(dalyIndividual,
												riskLabel);
										initialPopulation[this.scenarioInfo
												.getNumberOfDalyPopForThisScenario()[currentscen]]
												.addIndividual(dalyIndividual);
									}

									currentscen++;
									currentpop++;

								}
							} // end loop over this pop continuous risk factor

							/*
							 * 
							 * 1b: for categorical/compound risk factors This is
							 * a new variant on the one-for-all, but now no
							 * longer one for all
							 */

							/*
							 * 
							 * 
							 * for categorical covariates, but not for newborns
							 * and 0 year old (=newborns in the existing
							 * population) as the latter will start "clean",
							 * that is without having disease prevalences based
							 * on old history
							 * 
							 * the label is an indicator of scenario for the
							 * "all in one" scenario population for categorical
							 * riskfactors. This gives the old (baseline) value
							 * of the individual (0-9) plus the new value (0-9)
							 * for categorical data
							 */
							if (parameters.getRiskType() != 2
									&& shouldChangeInto == null)
								throw new DynamoConfigurationException(
										"ERROR: "
												+ "no change information where this should be availlable ");
							if (parameters.getRiskType() != 2) {
								/*
								 * in de oude situatie werden er voor ieder
								 * referentie individual meerdere scenario
								 * individuals toegevoegd, nl 1 voor iedere
								 * mogelijke verandereing in de nieuwe situatie
								 * wordt er maar 1 toegevoegd, maar moeten we
								 * dus bijhouden welke dat is Dus geen loop meer
								 * maar aantallen berekenen bij verandering van
								 */

								//
								int r = getCurrentValue(
										cumulativeNSimPerScenarioClass[currentscen],
										numberInClass);
								// if transitions = 0 we skip making this
								// individual as its weight will
								// be zero;
								if (currentRiskValue==3 || currentscen==3 || r ==3 )
								{
								int kk=0;
								kk++;	
								}	
								if (transitionMatrix[currentscen][a][g][currentRiskValue][r] > 0) {

									currentIndividualS = new Individual("scen"
											+ currentpop, "ind_"
											+ individualNumber + "_"
											+ currentRiskValue + "_" + r);
									// hier krijgen alle individueen hetzelfde
									// seed

									currentIndividualS
											.setRandomNumberGeneratorSeed(seed2);
									currentIndividualS.luxeSet(1,
											new FloatCharacteristicValue(
													stepsInSimulation, 1,
													(float) agestart));
									currentIndividualS.luxeSet(2,
											new IntCharacteristicValue(
													stepsInSimulation, 2, g));
									if (parameters.getRiskType() == 1
											|| parameters.getRiskType() == 3)
										currentIndividualS
												.luxeSet(
														3,
														new IntCharacteristicValue(
																stepsInSimulation,
																3, r));
									// duration = 0, both for just stopped, and
									// for
									// other categories
									characteristicIndex = 4;
									if (parameters.getRiskType() == 3) {
										currentIndividualS
												.luxeSet(
														4,
														new FloatCharacteristicValue(
																stepsInSimulation,
																4, 0));
										characteristicIndex = 5;
									}
									/*
									 * newborns and zero year olds are not
									 * bothered disease histories, so their
									 * diseases are recalculated from the new
									 * risk factor state but not the others:
									 * they keep the disease probabilities based
									 * on their old risk factors (before
									 * intervention)
									 */
									if (newborns || a == 0)

										CharValues = calculateDiseaseStates(
												parameters, currentRiskValue,
												a, g, riskFactorValue,
												currentDurationValue);

									currentIndividualS.luxeSet(
											characteristicIndex,
											new CompoundCharacteristicValue(
													stepsInSimulation,
													characteristicIndex,
													numberOfElements,
													CharValues));

									initialPopulation[currentpop]
											.addIndividual(currentIndividualS);

									/* also add the daly-scenarios */
									/*
									 * daly is een copy van the one for all but
									 * all riskfactors back to the reference
									 * value
									 */
									if (this.scenarioInfo
											.getNumberOfDalyPopForThisScenario()[currentscen] > -1) {

										Individual dalyIndividual = deepCopy(currentIndividualS);

										dalyIndividual.luxeSet(3,
												new IntCharacteristicValue(
														stepsInSimulation, 3,
														currentRiskValue));
										if (riskType == 3)
											dalyIndividual
													.luxeSet(
															4,
															new FloatCharacteristicValue(
																	stepsInSimulation,
																	4,
																	currentDurationValue));

										String riskLabel = "";
										/*
										 * durations needs no labeling, as it is
										 * always zero after change
										 */
										/*
										 * yes they do as the category itself
										 * needs changing back
										 */
										if (riskType == 1 || riskType == 3)
											riskLabel = Integer.toString(r);
										setLabelDalyIndividual(dalyIndividual,
												riskLabel);

										initialPopulation[this.scenarioInfo
												.getNumberOfDalyPopForThisScenario(currentscen)]
												.addIndividual(dalyIndividual);
										// end daly addition
									}

								}

								currentscen++;
								currentpop++;

							} // risktype!=2
							/*
							 * the one year olds and newborns loop weggehaald
							 * want niet duidelijk waarom dat dit anders moet
							 */

						} // end scenario loop

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

		// deze scenarios hebben geen daly populations

		for (int i1 = 1; i1 < nPopulations; i1++) {
			int scenNum = i1 - 1;
			if (!scenarioInfo.getInitialPrevalenceType()[scenNum]) {
				initialPopulation[i1] = deepCopy(initialPopulation[0]);

			}

		}

		/*
		 * if (newborns || !scenarioInfo.isWithNewBorns()) closeProgressBar();
		 */
		return initialPopulation;
	}

	private int getCurrentValue(int[] cumulativeN, int i) {
		int c;
		for (c = 0; c < cumulativeN.length; c++) {
			if (i < cumulativeN[c])
				break;
		}
		return c;
	}

	private int getNinDurationClass(int a, int g) {
		float[] duurFreq = parameters.getDuurFreq()[a][g];
		int n = 0;
		for (int c = 0; c < duurFreq.length; c++) {
			if (duurFreq[c] > 0)
				n++;
		}

		return n;
	}

	private int[] getNMinimumDurationClass(int a, int g) {
		float[] duurFreq = parameters.getDuurFreq()[a][g];
		int[] n = new int[duurFreq.length];
		for (int c = 0; c < duurFreq.length; c++) {
			if (duurFreq[c] > 0)
				n[c] = 1;
			else
				n[c] = 0;

		}

		return n;
	}

	private int[] getMinimumPerClass(int a, int g) {

		int[] minimumN = new int[nClasses];

		// ***
		boolean onlyTransitionScens = true;
		for (int scen = 0; scen < this.scenarioInfo.getNScenarios(); scen++)
			if (this.scenarioInfo.getInitialPrevalenceType()[scen])
				onlyTransitionScens = false;

		if (onlyTransitionScens)
			Arrays.fill(minimumN, 1);
		else
			for (int c1 = 0; c1 < nClasses; c1++) {
				for (int c2 = 0; c2 < nClasses; c2++) {
					if (this.shouldChangeInto[a][g][c1][c2])
						minimumN[c1]++;
				}

			}
		return minimumN;
	}

	/** calculates the mimum number of inidividuals with the current age/sex/riskfactor group
	 * that needs to be changed to each scenario risk factor class
	 * @param a
	 * @param g
	 * @return numbers by scenario and riskfactorclass to go to
	 */
	private int[][] getMinimumPerScenarioTransition(int a, int g, int currentRiskClass) {

		int[][] minimumN = new int[this.scenarioInfo.getNScenarios()][nClasses];

		// *** bereken eerst het totaal aantal dat nodig is;
		for (int scen = 0; scen < this.scenarioInfo.getNScenarios(); scen++)
			if (this.scenarioInfo.getInitialPrevalenceType()[scen])
				for (int rToGo= 0; rToGo < nClasses; rToGo++) {
					
						if (this.transitionMatrix[scen][a][g][currentRiskClass][rToGo] > 0)
							minimumN[scen][rToGo]++;
					
					if (riskType == 3
							&& rToGo == this.scenarioInfo.getIndexDurationClass())
						minimumN[scen][rToGo] *= getNinDurationClass(a, g);

				}
			else {
				for (int rToGo = 0; rToGo < nClasses; rToGo++)
					minimumN[scen][rToGo] = 1;

			}

		return minimumN;
	}

	/** Makes the number of individuals to simulate by riskclass 
	 * @param targetDistribution: the distribution that needs to be approximated
	 * @param minimumN minimum numbers per category
	 * @param nTot total numbers to simulate (if larger than implied in minimumN, than numbers are added; otherwise minimumN is returned
	 * @return
	 */
	private int[] makeNperClass(float[] targetDistribution, int[] minimumN,
			int nTot) {
		// ** bereken dan wat nog over is (tov minimumN)
		// ** en bereken hoe de overige het best verdeelt moeten worden
		// als niet over==> klaar
		// er is geen poging gedaan aantallen te minimaliseren
		// kan zijn dat er maar 1 over is om toe te voegen, maar aantallen
		// vooral in de verkeerde klasse zitten
		// in dat geval worden er met dit algoritme grote aantallen toegevoegd
		// aan de andere klassen
		// dat zorgt wel voor betere verdeling in de simulatie
		// dus laten we zo

		int[] nSimPerClass = new int[nClasses];

		int sumMinimum = 0;
		for (int c1 = 0; c1 < nClasses; c1++)
			sumMinimum += minimumN[c1];

		if (sumMinimum >= this.nSim)
			nSimPerClass = minimumN;
		else {
			float[] prevToAdd = new float[nClasses];
			float sumToAdd = 0;
			int nAssigned = 0;

			for (int c1 = 0; c1 < nClasses; c1++) {
				if (targetDistribution[c1] * nTot > minimumN[c1]) {
					nSimPerClass[c1] = (int) Math.floor(targetDistribution[c1]
							* nTot);
					prevToAdd[c1] = targetDistribution[c1] * nTot
							- nSimPerClass[c1];
					sumToAdd += prevToAdd[c1];

				} else
					nSimPerClass[c1] = minimumN[c1];

				nAssigned += nSimPerClass[c1];

			}
			if (nAssigned < nTot) {

				for (int c1 = 0; c1 < nClasses; c1++)
					prevToAdd[c1] /= (nTot - nAssigned);
				for (int i = 0; i < nTot - nAssigned; i++)// rest random
															// toevoegen
				{
					int newRiskValue = DynamoLib.draw(prevToAdd, rand3);
					nSimPerClass[newRiskValue]++;
				}

			}
		}

		return nSimPerClass;

	}

	// bereken de cumulatieve som
	private int[] getCumulative(int[] array) {
		int[] cumulativeN = new int[array.length];
		for (int c1 = 0; c1 < nClasses; c1++) {

			if (c1 > 0)
				cumulativeN[c1] = array[c1] + cumulativeN[c1 - 1];
			else
				cumulativeN[c1] = array[c1];
		}

		return cumulativeN;

	}

	private void setLabelDalyIndividual(Individual dalyIndividual,
			String riskLabel) {

		/*
		 * for the one for all population, elements [2] and [3] (=3e+4e) are
		 * used for indicating to and from; here we use 4 and 5 (duration) for
		 * the daly info
		 */

		String oldLabel = dalyIndividual.getLabel();

		String label = "DALY";
		/* split at the place of the underscore */
		/*
		 * elements are: "ind", indno, currentriskfactor, duration or to
		 * (optional), popnumber
		 */
		/*
		 * for the reference individual continuous risk factor it is only ind,
		 * indno en ref
		 */
		String delims = "_";
		String[] tokens = oldLabel.split(delims);
		if (tokens[2].equalsIgnoreCase("ref"))
			label += tokens[0] + "_" + tokens[1] + "_" + "DALY" + "_" + "cont"
					+ "_";
		/* put together again */
		else
			for (int i = 0; i < 4; i++) {
				label += tokens[i] + "_";
			}
		label += riskLabel;
		dalyIndividual.setLabel(label);
	}

	private float[][] getPrevalenceTransitionMatrix(int currentscen, int a,
			int g) {
		return this.prevalenceTransitionMatrix[currentscen][a][g];
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
		copy.luxeSet(1, deepCopy((FloatCharacteristicValue) individual.get(1)));
		copy.luxeSet(2, deepCopy((IntCharacteristicValue) individual.get(2)));
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

	/**
	 * checks if this is a situation where the first n categories are filled in
	 * the old prevalence, and the second n in the new prevalence with exactly
	 * the same numbers
	 * 
	 * @param oldPrev
	 * @param newPrev
	 * @return
	 */
	private boolean isZeroType(float[] oldPrev, float[] newPrev) {
		boolean isOK = true;
		int ncat = oldPrev.length;
		if (2 * Math.floor(ncat / 2) != ncat)
			isOK = false; // oneven kan niet
		for (int i = 0; i < (ncat / 2); i++) {
			if (oldPrev[i] != newPrev[i + (ncat / 2)])
				isOK = false;
		}

		// TODO Auto-generated method stub
		return isOK;
	}
}
