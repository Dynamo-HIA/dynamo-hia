/*
 * 
 */
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

// TODO: Auto-generated Javadoc
/**
 * A factory for creating InitialPopulation objects.
 */
public class InitialPopulationFactory {
	
	/** The log. */
	Log log = LogFactory.getLog(this.getClass().getName());
	
	/** The number of elements. */
	private int numberOfElements;
	
	/** The global base dir. */
	private String globalBaseDir;
	
	/** The incidence debug. */
	private boolean incidenceDebug = true;
	// private Shell parentShell;
	/** The dsi. */
	private DynSimRunPRInterface dsi = null;
	// private ProgressBar bar = null;
	/** The progressbar. */
	private ProgressIndicatorInterface progressbar = null;
	
	/** The prevalence transition matrix. */
	private float[][][][][] prevalenceTransitionMatrix = null;
	
	/** The risk type. */
	private int riskType = 0;
	
	/** The n classes. */
	private int nClasses = 0;

	/** The n sim. */
	int nSim = 50;

	/* in case of debugging, more elements are needed */

	/** The n duur classes. */
	int[][] nDuurClasses = new int[96][2];

	/** The n sim new. */
	int[][] nSimNew = new int[96][2];

	/** The rand. */
	Random rand; // used to draw the initial population
	
	/** The rand2. */
	MTRand rand2; /*
				 * used to generate seeds which are entered as information with
				 * each generated individual, and will be used by the update
				 * rules // TODO inlezen baseDir aanpassen aan userinterface
				 */
	/** The rand3. */
 Random rand3; // used to draw the scenario population
	// here the seed is reset by hand, so it does not matter that it used
	// the same seed here
	/* make a table of point from the inverse normal distribution */
	/** The n populations. */
	private int nPopulations;
	
	/** The number of the one for all pop. */
	private int numberOfTheOneForAllPop;
	
	/** The is at least one all for one population. */
	private boolean isAtLeastOneAllForOnePopulation;
	
	/** The has daly scenarios. */
	private boolean hasDalyScenarios;
	
	/** The number of dalypopulation. */
	private int[] numberOfDalypopulation;
	
	/** The should change into. */
	private boolean[][][][] shouldChangeInto;
	
	/** The individual number. */
	private int individualNumber;
	
	/** The is one for all population. */
	private boolean[] isOneForAllPopulation;
	
	/** The parameters. */
	private ModelParameters parameters;
	
	/** The scenario info. */
	private ScenarioInfo scenarioInfo;
	
	/** The baseline odds. */
	private double[] baselineOdds;
	
	/** The rel risk duur begin. */
	private float[] relRiskDuurBegin;
	
	/** The rel risk duur end. */
	private float[] relRiskDuurEnd;
	
	/** The relative risks cat. */
	private float[][] relativeRisksCat;
	
	/** The relative risks cont. */
	private float[] relativeRisksCont;
	
	/** The alpha duur. */
	private float[] alphaDuur;
	
	/** The R rdisease on disease. */
	private float[][][] RRdiseaseOnDisease;

	/* indexes are: scenario, age ,sex, transitionmatrix */

	/* number of disease states */

	/**
	 * Instantiates a new initial population factory.
	 *
	 * @author Boshuizh This class generates the initial population and
	 *         populations of newborns
	 * @param params the params
	 * @param scenInfo the scen info
	 * @param dsi the dsi
	 * @param minage the minage
	 * @param maxage the maxage
	 * @param ming the ming
	 * @param maxg the maxg
	 * @throws DynamoInconsistentDataException the dynamo inconsistent data exception
	 * @throws DynamoConfigurationException the dynamo configuration exception
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

	ScenarioInfo scenInfo, DynSimRunPRInterface dsi, int minage, int maxage, int ming, int maxg)
			throws DynamoInconsistentDataException, DynamoConfigurationException {

		initializeInitialPopulationFactory(params,

		scenInfo, minage,maxage,ming,maxg);
		// this.parentShell = shell;
		this.dsi = dsi;
	}

	/**
	 * OBSOLETE and does not work anymore.
	 *
	 * @param parameters the parameters
	 * @param nSim the n sim
	 * @param simulationName the simulation name
	 * @param seed the seed
	 * @param newborns            : boolean: should this give an intial population or newborns
	 * @param scenarioInfo            : information on scenario
	 * @throws DynamoConfigurationException the dynamo configuration exception
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
	 * @param params the params
	 * @param scenInfo the scen info
	 * @param minage the minage
	 * @param maxage the maxage
	 * @param ming the ming
	 * @param maxg the maxg
	 * @throws DynamoInconsistentDataException the dynamo inconsistent data exception
	 * @throws DynamoConfigurationException the dynamo configuration exception
	 */
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

	ScenarioInfo scenInfo, int minage, int maxage, int ming, int maxg) throws DynamoInconsistentDataException, DynamoConfigurationException {

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
		this.numberOfTheOneForAllPop = scenarioInfo
				.getFirstOneForAllPopScenario() + 1;

		this.isAtLeastOneAllForOnePopulation = false;
		if (numberOfTheOneForAllPop > 0)
			isAtLeastOneAllForOnePopulation = true;
		isOneForAllPopulation = scenarioInfo.getthisScenarioUsedOneForAllPop();

		/* add the one-for-all-scenario that still needs to be simulated */
		if (scenarioInfo.getNTranstionScenarios() != 0
				&& scenarioInfo.getRiskType() != 2)
			makeTransitionMatrixForPrevalence(parameters, scenarioInfo);

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


		if (isAtLeastOneAllForOnePopulation) {
			int nCat = parameters.getPrevRisk()[0][0].length;
			float[] RR = new float[nCat];

			// initialize arrays;
			Arrays.fill(RR, 1);

			this.shouldChangeInto = new boolean[agemax + 1][2][nCat][nCat];
			for (int a = agemin; a < agemax + 1; a++)
				for (int g = ming; g <= maxg; g++)
					for (int r1 = 0; r1 < nCat; r1++)
						for (int r2 = 0; r2 < nCat; r2++)
							this.shouldChangeInto[a][g][r1][r2] = false;

			for (int a = agemin; a < agemax + 1; a++)
				for (int g = ming; g <=maxg; g++)
					for (int s = 0; s < scenarioInfo.getNScenarios(); s++) {
						if (isOneForAllPopulation[s]) {
							float oldPrev[] = parameters.getPrevRisk()[a][g];
							float newPrev[] = scenarioInfo.getNewPrevalence()[s][a][g];
							float[][] trans =null;
							try {
							trans = NettTransitionRateFactory
									.makeNettTransitionRates(oldPrev, newPrev,
											0, RR);}
							catch (DynamoConfigurationException E){
								
								if (  E.getMessage().equals("Error in simplex algorithm: Bad input tableau in simplx.") && isZeroType(oldPrev, newPrev)) { 
									/* one possibility for this error is that the user has different categories for the old and the new situation
									 * below we give a solution for this case, provided that the categories are ordered: first all old situation
									 * categories, and second all new situation categories
									 * Also, each old categories should correspond to a new category in the same order and prevalences should stay the same
									 */
									
									trans = new float [nCat][nCat];
									for (int r1 = 0; r1<nCat;r1++){
										for (int r2 = 0; r2<nCat;r2++){
											if (r1<(nCat/2) && r2==r1+nCat/2)
										     trans[r1][r2]=1;
									}}
										
									
									
									
									}
								else if (  E.getMessage().equals("Error in simplex algorithm: Bad input tableau in simplx.")) throw new DynamoConfigurationException(E.getMessage()+ " for age "+ a+
										" gender "+ g +
										 "\nOne possible reason might be that there are multiple riskfactor classes with a prevalence of zero, where it is not clear how to change the original prevalence into " +
										 "the scenario prevalence. Also this error is known to occur when negative prevalence rates are present in the data. " 
										);
								else throw new DynamoConfigurationException(E.getMessage());
									}
							
							
							for (int r1 = 0; r1 < nCat; r1++)
								for (int r2 = 0; r2 < nCat; r2++) {
									if (r1 != r2 && trans[r1][r2] > 0)
										shouldChangeInto[a][g][r1][r2] = true;
								}
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
	 * Manufacture initial population.
	 *
	 * @param agemin            ; 0 for newborns
	 * @param agemax            ; 0 for newborns
	 * @param gmin            : minimum gender index (0 or 1)
	 * @param gmax            : maximum gender index (0 or 1)
	 * @param generationMin            for newborns: 1 <= generationMin <=
	 *            scenarioInfo.getYearsInRun()
	 * @param generationMax            for newborns: 1 <= generationMax <=
	 *            scenarioInfo.getYearsInRun(); Not newborns: both are 1;
	 * @param newborns the newborns
	 * @return the population[]
	 */
	public Population[] manufactureInitialPopulation(int agemin, int agemax,
			int gmin, int gmax,

			int generationMin, int generationMax, boolean newborns) {

		Population[] initialPopulation = new Population[nPopulations];

		for (int scen = 0; scen < nPopulations; scen++) {
			initialPopulation[scen] = new Population("Population nr " + scen,
					null);
		}

		int[] cumulativeNSimPerClass;
		int[] nSimPerClass;
		int[] nSimPerDurationClass;
		int[] cumulativeNSimPerDurationClass;
		float[] rest;
		float[] restDuration;
		
		
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
				nSimNew[a][g] = nSim;
				/*
				 * if (Math.abs(Math.round(a / 10) - a / 10) < 0.01 && g == 1)
				 * 
				 * updateProgressBar();
				 */

				// calculate the number of persons in each risk factor class
				cumulativeNSimPerClass = new int[nClasses];
				cumulativeNSimPerDurationClass = new int[nDuurClasses[a][g]];
				rest = new float[nClasses];
				restDuration = new float[nDuurClasses[a][g]];
				if (parameters.getRiskType() == 1) {
					// NB all statements here are copied to the next part
					// (risktype==3)
					// so any fault found here should be mended there too.
					nSimPerClass = new int[nClasses];

					int c = 0;

					for (c = 0; c < nClasses; c++) {
						nSimPerClass[c] = (int) Math.floor(parameters
								.getPrevRisk()[a][g][c]
								* nSimNew[a][g]);
						/*
						 * if zero case in a class, add a person to the
						 * simulated population, in case such persons exist in
						 * the population
						 */
						if (nSimPerClass[c] == 0
								&& parameters.getPrevRisk()[a][g][c] > 0) {
							nSimPerClass[c] = 1;
							nSimNew[a][g]++;

							/*
							 * in that case, the rest from other categories only
							 * needs to be distributed differently over the
							 * other categories, as this categorie already has a
							 * large part of the total as needed
							 * 
							 * We therefore recalculate the earlier
							 */

						}

						if (c > 0)
							cumulativeNSimPerClass[c] = nSimPerClass[c]
									+ cumulativeNSimPerClass[c - 1];
						else
							cumulativeNSimPerClass[c] = nSimPerClass[c];
					}
					/*
					 * calculated the parts of nSim that have not yet been
					 * allocated
					 */
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
								/ (float) nSimNew[a][g];
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

					for (c = 0; c < nClasses; c++) {
						rest[c] = rest[c] / totrestWithoutNegatives;
					}

					;
					if (cumulativeNSimPerClass[nClasses-1]>nSimNew[a][g]) nSimNew[a][g]=cumulativeNSimPerClass[nClasses-1];

				}
				
				
				/*
				 * repeat this for categorical riskfactors with duration, where
				 * the same "trick" is applied both to the categories, and the
				 * duration within categories
				 */
				if (parameters.getRiskType() == 3) {
					// NB all statements here are copied from (risktype==1)
					// so any fault found here should be mended there too.
					nSimPerClass = new int[nClasses];
					nSimPerDurationClass = new int[nDuurClasses[a][g]];
					int c = 0;

					for (c = 0; c < nClasses; c++) {
						nSimPerClass[c] = (int) Math.floor(parameters
								.getPrevRisk()[a][g][c]
								* nSimNew[a][g]);
						/*
						 * if zero case in a class, add a person to the
						 * simulated population but not if such persons do not
						 * exist in the population
						 */
						if (nSimPerClass[c] == 0
								&& parameters.getPrevRisk()[a][g][c] > 0) {
							nSimPerClass[c] = 1;
							nSimNew[a][g]++;

							/*
							 * in that case, the rest from other categories only
							 * needs to be distributed differently over the
							 * other categories, as this category already has a
							 * large part of the total as needed
							 * 
							 * We therefore recalculate the earlier
							 */

						}

						if (c > 0)
							cumulativeNSimPerClass[c] = nSimPerClass[c]
									+ cumulativeNSimPerClass[c - 1];
						else
							cumulativeNSimPerClass[c] = nSimPerClass[c];
					}
					/*
					 * calculated the parts of nSim that have not yet been
					 * allocated
					 */
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
								/ (float) nSimNew[a][g];
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

					for (c = 0; c < nClasses; c++) {
						rest[c] = rest[c] / totrestWithoutNegatives;
					}

					;

					for (c = 0; c < nDuurClasses[a][g]; c++) {
						nSimPerDurationClass[c] = (int) Math.floor(parameters
								.getDuurFreq()[a][g][c]
								* nSimPerClass[parameters.getDurationClass()]);
						/*
						 * if no cases in duration class, add an extra case in
						 * case such persons exist
						 */
						if (nSimPerDurationClass[c] == 0
								&& nSimPerClass[parameters.getDurationClass()] > 0
								&& parameters.getDuurFreq()[a][g][c] > 0) {
							nSimPerDurationClass[c] = 1;
							nSimNew[a][g]++;

						}
						restDuration[c] = parameters.getDuurFreq()[a][g][c]
								- (float) nSimPerDurationClass[c]
								/ (float) nSimPerClass[parameters
										.getDurationClass()];
						if (c > 0)
							cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c]
									+ cumulativeNSimPerDurationClass[c - 1];
						else
							cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c];
					}
					totrest = 0;
					totrestWithoutNegatives = 0;
					for (int c2 = 0; c2 < nDuurClasses[a][g]; c2++) {
						totrest += restDuration[c2];
						if (restDuration[c2] < 0)
							restDuration[c2] = 0;
						else
							totrestWithoutNegatives += restDuration[c2];
					}

					for (int c2 = 0; c2 < nDuurClasses[a][g]; c2++) {
						restDuration[c2] = restDuration[c2]
								/ totrestWithoutNegatives;
					}
					/*
					 * if extra points have been added (because otherwise
					 * durations would not have been present), adjust the arrays
					 * for the overall classes
					 */
					if (cumulativeNSimPerDurationClass[nDuurClasses[a][g] - 1] > nSimPerClass[parameters
							.getDurationClass()]) {
						nSimPerClass[parameters.getDurationClass()] = cumulativeNSimPerDurationClass[nDuurClasses[a][g] - 1];
						for (c = 0; c < nClasses; c++) {

							if (c > 0)
								cumulativeNSimPerClass[c] = nSimPerClass[c]
										+ cumulativeNSimPerClass[c - 1];
							else
								cumulativeNSimPerClass[c] = nSimPerClass[c];
						}
					}
					if (cumulativeNSimPerClass[nClasses-1]>nSimNew[a][g]) nSimNew[a][g]=cumulativeNSimPerClass[nClasses-1];
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
						 * This is done for 4 cases:
						 * 
						 * 1.- different initial risk factor prevalence, not
						 * handled through one-for-all-population
						 * 
						 * 2.- different initial risk factor prevalence handled
						 * by one-for-all-population
						 * 
						 * 3.- same initial prevalence: just copy reference
						 * population
						 * 
						 * 4. - daly scenarios: just copy reference population,
						 * but with different label, and different health status for newborns
						 * 
						 * 3. is done in a separate loop (not for this
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
						 * / 1.
						 * 
						 * 
						 * GENERATE SCENARIO POPULATIONS WITH DIFFERENT INITIAL
						 * PREVALENCE DISTRIBUTIONS (but not an all-for-one
						 * population)
						 */

						/*
						 * 
						 * There are two cases for which this needs to be done:
						 * 1. for continuous riskfactors with different initial
						 * populations and 2. categorical where both initial
						 * population and transition rates are changed
						 * 
						 * (if there exist oneForAll populations and there are
						 * only 2 populations, this means no such situation
						 * exists and than this part can be skipped)
						 */
						int newRiskValue = -1;
						float newDurationValue=-1;
						float newRiskFactorValue=-1;
						int currentscen = 0;
						int currentpop = 1; // population 0 is the reference
						// population
						boolean moreScenarios = true;
						if ((isAtLeastOneAllForOnePopulation && nPopulations == 2)
								|| (isAtLeastOneAllForOnePopulation
										&& nPopulations == 3 && this.hasDalyScenarios))
							moreScenarios = false;

						while ((currentpop < nPopulations) && moreScenarios) {

							/*
							 * look for next scenario that has an different
							 * initial prevalence (initialprevalencetype) and is
							 * not an all for One Population type
							 */
							boolean found = false;
							for (int i1 = currentscen; i1 < scenarioInfo
									.getNScenarios(); i1++) {
								/*
								 * find the population number that belongs to
								 * the current scenario
								 */
								/*
								 * isOneForAllPopulation indicates that this
								 * scenario uses a one-for-all-pop
								 */
								/*
								 * if this scenario is the first one for all
								 * scenario, then making this population should
								 * not be done here in this loop, so increase
								 * the population number
								 */
								if (numberOfTheOneForAllPop - 1 == i1) /*
																		 * this
																		 * tests
																		 * whether
																		 * this
																		 * scenario
																		 * is
																		 * the
																		 * first
																		 * scenario
																		 * that
																		 * uses
																		 * a one
																		 * for
																		 * all
																		 * population
																		 */
									currentpop++;
								/*
								 * find the next scenario that is not a 1 for
								 * all scenario, and also not a scenario that is
								 * copied later
								 */
								if (!isOneForAllPopulation[i1]
										&& scenarioInfo
												.getInitialPrevalenceType()[i1]) {
									currentscen = i1;
									found = true;
									break;
								}
								/*
								 * if scenario has same initial pop, it is just
								 * copied from the reference scenario, This is
								 * done later, so we have to skip filling the
								 * population here
								 */
								if (!isOneForAllPopulation[i1]
										&& !scenarioInfo
												.getInitialPrevalenceType()[i1])
									currentpop++;

								/* if no such scenario is found */
							

							}
							if (!found)
								moreScenarios = false;
							/* now give the values to the individual */
							/*
							 * first check if there is a population to be made
							 */

							if (moreScenarios) {
									
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
									/* get transition matrix */

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

									rand3.setSeed(seed2 + 5);
									newRiskValue = DynamoLib.draw(
											trans[currentRiskValue], rand3);
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
								
			

								currentscen++;
								currentpop++;

							}
						} // end loop over populations of this individual

						/*
						 * 
						 * 2.
						 * 
						 * 2. generation of SCENARIO POPULATION for the
						 * one-for-all population
						 */

						/*
						 * 
						 * 
						 * for categorical covariates, but not for newborns and
						 * 0 year old (=newborns in the existing population) as
						 * the latter will start "clean", that is without having
						 * disease prevalences based on old history
						 * 
						 * the label is an indicator of scenario for the
						 * "all in one" scenario population for categorical
						 * riskfactors. This gives the old (baseline) value of
						 * the individual (0-9) plus the new value (0-9) for
						 * categorical data
						 */

						if (parameters.getRiskType() != 2
								&& shouldChangeInto != null && a != 0
								&& !newborns) {
							for (int r = 0; r < shouldChangeInto[a][g].length; r++) {
								if (shouldChangeInto[a][g][currentRiskValue][r]) {
									currentIndividualS = new Individual("scen"
											+ numberOfTheOneForAllPop, "ind_"
											+ individualNumber + "_"
											+ currentRiskValue + "_" + r);
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
									
									
									initialPopulation[numberOfTheOneForAllPop]
											.addIndividual(currentIndividualS);
									
									
									
									
									/* also add the daly-scenarios */
									/* daly is een copy van the one for all
									 * but all riskfactors back to the reference value 
									  */
									if (this.scenarioInfo.getFirstOneForAllDalyPop()>-1) {
										Individual dalyIndividual=deepCopy(currentIndividualS);
										
										dalyIndividual
										.luxeSet(
												3,
												new IntCharacteristicValue(
														stepsInSimulation,
														3, currentRiskValue));
										if (riskType==3) dalyIndividual
										.luxeSet(
												4,
												new FloatCharacteristicValue(
														stepsInSimulation,
														4, currentDurationValue));
											
										String riskLabel="";
										/* durations needs no labeling, as it is always zero after change */
										/* yes they do as the category itself needs changing back */
										if (riskType==1 || riskType==3) riskLabel= Integer.toString(r);
										setLabelDalyIndividual(dalyIndividual,riskLabel);
									
										initialPopulation[this.scenarioInfo.getFirstOneForAllDalyPop()]
															.addIndividual(dalyIndividual);
										// end daly addition
									}
								}
							}

						}
						/*
						 * now the one year olds and newborns
						 */
						else if (parameters.getRiskType() != 2
								&& shouldChangeInto != null
								&& (a == 0 || newborns))

							for (int r = 0; r < shouldChangeInto[a][g].length; r++) {
								if (shouldChangeInto[a][g][currentRiskValue][r]) {
									currentIndividualS = new Individual("scen"
											+ numberOfTheOneForAllPop, "ind_"
											+ individualNumber + "_"
											+ currentRiskValue + "_" + r);
									currentIndividualS
											.setRandomNumberGeneratorSeed(seed2);
									currentIndividualS.luxeSet(1,
											new FloatCharacteristicValue(
													stepsInSimulation, 1,
													agestart));
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
												parameters, r, a, g,
												riskFactorValue,
												currentDurationValue);
									currentIndividualS.luxeSet(
											characteristicIndex,
											new CompoundCharacteristicValue(
													stepsInSimulation,
													characteristicIndex,
													numberOfElements,
													CharValues));
									initialPopulation[numberOfTheOneForAllPop]
											.addIndividual(currentIndividualS);
									
									// add daly individual: this starts with having  reference values 
									
									if (this.scenarioInfo.getFirstOneForAllDalyPop()>-1) {
										Individual dalyIndividual=deepCopy(currentIndividualS);
										
										dalyIndividual
										.luxeSet(
												3,
												new IntCharacteristicValue(
														stepsInSimulation,
														3, currentRiskValue));
										if (riskType==3) dalyIndividual
										.luxeSet(
												4,
												new FloatCharacteristicValue(
														stepsInSimulation,
														4, currentDurationValue));
											
										String riskLabel="";
										if (riskType==1 || riskType==3) riskLabel= Integer.toString(r);
										setLabelDalyIndividual(dalyIndividual,riskLabel);
									
										initialPopulation[this.scenarioInfo.getFirstOneForAllDalyPop()]
															.addIndividual(dalyIndividual);
									}
							}
					}
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

		if (parameters.getRiskType() == 2
				|| !(isAtLeastOneAllForOnePopulation && nPopulations == 2)) {

			for (int i1 = 1; i1 < nPopulations; i1++) {
				int scenNum = scenarioInfo.getPopToScenIndex()[i1];
				if (!scenarioInfo.getInitialPrevalenceType()[scenNum]
						&& !isOneForAllPopulation[scenNum]) {
					initialPopulation[i1] = deepCopy(initialPopulation[0]);

				}

			}
		}
		/*
		 * if (newborns || !scenarioInfo.isWithNewBorns()) closeProgressBar();
		 */
		return initialPopulation;
	}

	/**
	 * Sets the label daly individual.
	 *
	 * @param dalyIndividual the daly individual
	 * @param riskLabel the risk label
	 */
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

	/**
	 * Gets the prevalence transition matrix.
	 *
	 * @param currentscen the currentscen
	 * @param a the a
	 * @param g the g
	 * @return the prevalence transition matrix
	 */
	private float[][] getPrevalenceTransitionMatrix(int currentscen, int a,
			int g) {
		return this.prevalenceTransitionMatrix[currentscen][a][g];
	}

	/**
	 * Make transition matrix for prevalence.
	 *
	 * @param parameters the parameters
	 * @param scenarioInfo the scenario info
	 * @throws DynamoInconsistentDataException the dynamo inconsistent data exception
	 * @throws DynamoConfigurationException the dynamo configuration exception
	 */
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

	/**
	 * Deep copy.
	 *
	 * @param population the population
	 * @return the population
	 */
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
	
	
	/**
	 * Deep copy.
	 *
	 * @param individual the individual
	 * @return the individual
	 */
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
	
	
	

	/**
	 * Deep copy.
	 *
	 * @param original the original
	 * @return the float characteristic value
	 */
	private FloatCharacteristicValue deepCopy(FloatCharacteristicValue original) {
		float[] rijtje = original.getRijtje();
		FloatCharacteristicValue copy = new FloatCharacteristicValue(
				rijtje.length - 1, original.getIndex(), rijtje[0]);
		return copy;
	}

	/**
	 * Deep copy.
	 *
	 * @param original the original
	 * @return the int characteristic value
	 */
	private IntCharacteristicValue deepCopy(IntCharacteristicValue original) {
		int[] rijtje = original.getRijtje();
		IntCharacteristicValue copy = new IntCharacteristicValue(
				rijtje.length - 1, original.getIndex(), rijtje[0]);
		return copy;
	}

	/**
	 * Deep copy.
	 *
	 * @param original the original
	 * @return the compound characteristic value
	 */
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
	 * Calculate disease states.
	 *
	 * @param parameters the parameters
	 * @param currentRiskValue the current risk value
	 * @param a the a
	 * @param g the g
	 * @param riskFactorValue the risk factor value
	 * @param currentDurationValue the current duration value
	 * @return the float[]
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

	/**
	 * Extract disease data for this age sex group.
	 *
	 * @param a the a
	 * @param g the g
	 */
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
	 * Gets the number of disease state elements.
	 *
	 * @param parameters the parameters
	 * @return the number of disease state elements
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

	/**
	 * Write to xml file.
	 *
	 * @param population the population
	 * @param stepNumber the step number
	 * @param xmlFileName the xml file name
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws TransformerException the transformer exception
	 */
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

	/**
	 * Make progress bar.
	 *
	 * @param length the length
	 */
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

	/**
	 * Update progress bar.
	 */
	public void updateProgressBar() {

		// int state = this.bar.getSelection();
		int state = progressbar.getPosition();
		state++;
		// this.bar.setSelection(state);
		progressbar.update(state);

	}

	/**
	 * Close progress bar.
	 */
	public void closeProgressBar() {
		// this.bar.getShell().close();
		progressbar.dispose();

	}
	
	/**
	 * Checks if is zero type.
	 *
	 * @param oldPrev the old prev
	 * @param newPrev the new prev
	 * @return true, if is zero type
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
