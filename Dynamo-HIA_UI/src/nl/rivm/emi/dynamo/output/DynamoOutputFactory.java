/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.estimation.DiseaseClusterStructure;
import nl.rivm.emi.dynamo.estimation.NettTransitionRateFactory;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author boshuizh
 * 
 *         DynamoOutputFactory takes the populations from the simulation and
 *         makes aggregated data-arrays The object is stored for later retrieval
 *         it implements the CZMOutputInterface, needed for making the plots in
 *         the output interface it extends the abstract CDMoutputFactory, that
 *         implements the general methods and fields
 * 
 */
public class DynamoOutputFactory extends CDMOutputFactory implements
		Serializable, CDMOutputInterface {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.output.DynamoOutputFactory");

	/*
	 * different arrays with summation data to keep things overzichtelijk, most
	 * summary arrays are recalculated each time they are needed, using a getXXX
	 * method, and are no longer fields only the most detailed arrays are kept
	 * as fields
	 */
	/* all are by sex, timestep and scenario */
	/* some are by risk or by age or both (indicated by their names) */
	/*
	 * p=proportion (of age/risk/sex class): these are temporary arrays not
	 * intended for the users, as for proportions it is always difficult to know
	 * what the denominator is
	 */
	/* n= total number with particular characterisitics in population */
	/*
	 * nInSimulation= total numbers with the characteristics in the simulation
	 * the ones with JAVADOC documentation are suitable for outside use and have
	 * getters / sequence of indexes: scenario, time , disease, risk class, age,
	 * and sex
	 */
	/**
	 * summary array of simulated numbers: index=sex
	 */
	transient int[] nInSimulation = new int[2]; /* index: sex */
	/**
	 * summary array of simulated numbers: index=age, sex
	 */
	transient int[][] nInSimulationByAge;

	/**
	 * summary array of simulated numbers: scenario number (0=first alternative
	 * scenario) , from, to, age, sex,
	 */
	transient int[][][][][] nInSimulationByFromByTo;
	/**
	 * summary array of simulated numbers of newborns: index=age at the end of
	 * simulation, sex
	 */
	transient int[][] nNewBornsInSimulationByAge;
	/**
	 * summary array of simulated numbers: index=riskclass, age, sex
	 */
	transient int[][][] nInSimulationByRiskClassByAge;
	/**
	 * summary array of simulated numbers: index=riskclass, duration class, age,
	 * sex
	 */
	transient int[][][][] nInSimulationByRiskClassAndDurationByAge;

	/* these are a temporary array for making nDiseaseStateByRiskClassByAge */
	/*
	 * NB this array is reused, so it contains something different before and
	 * after applying method makeSummaryArrays
	 */
	transient private double pDiseaseStateByRiskClassByAge[][][][][][];
	/*
	 * indexes are scenario, step, disease state, original riskclass , original
	 * age , sex
	 */

	transient private double pDiseaseStateByOriRiskClassByAge[][][][][][];

	transient private double[][][][][] pSurvivalByRiskClassByAge;

	/*
	 * this summary array is an exception to the rule that only detailed arrays
	 * are fields as for riskfactortype=2 means can more easily be calculated
	 * from crude data
	 */
	/**
	 * average value of riskvalue by Age; indexes are: scenario, time age, and
	 * sex
	 */

	private double[][][][] meanRiskByAge;

	public double[][][][] getMeanRiskByAge() {
		return meanRiskByAge;
	}

	/* these data are copied by the constructor from the object "scenario.info" */
	DiseaseClusterStructure[] structure;
	private int nPopulations;

	/**
	 * details indicates whether information should be written on all disease
	 * combinations (true) or only per disease (marginals, when false
	 */

	// private boolean details;
	private boolean[] scenInitial;
	private boolean[] scenTrans;
	// private float[] cutoffs;
	private int durationClass;
	// private boolean withNewborns;
	/*
	 * this array indicates how a population should be handled false= direct ;
	 * true= as a one-simulation for all scenarios population
	 */
	private boolean[] isOneScenPopulation;
	private boolean oneScenPopulation;

	private float[][] populationSize;
	private int[] newborns;
	private float mfratio;
	// String[] riskClassnames;
	/**
	 * for each scenario the new prevalence rates
	 */
	private float[][][][] newPrevalence;
	/**
	 * the prevalence rates of the reference scenario
	 */
	private float[][][] oldPrevalence;
	/**
	 * the prevalence rates of the durationclasses in the reference scenario
	 */
	private float[][][] oldDurationNumbers;

	// private String[] diseaseNames;
	// private String[] scenarioNames;
	// private int nDim = 106;
	private float[][] baselineAbility;
	private float[][][] diseaseAbility;
	private float[][][] relRiskAbilityCat;
	private float[][] relRiskAbilityCont;
	private float[][] relRiskAbilityBegin;
	private float[][] relRiskAbilityEnd;
	private float[][] alfaAbility;
	private float referenceRiskFactorValue = 0;
	/**
	 * succesrate is the successrate of the intervention can be reset at a
	 * different value for obtaining new results without having to redo the
	 * simulation. Set this value to the succesrate in percent (value between 0
	 * and 100)
	 */
	// private float[] succesrate = null;
	/**
	 * minAge is the lower bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	// private int[] minAge = null;
	/**
	 * in Men indicates if the intervention is applied to men. It can be reset
	 * at a different value for obtaining new results without having to redo the
	 * simulation
	 */

	// private boolean[] inWomen = null;
	/**
	 * in Men indicates if the intervention is applied to men. It can be reset
	 * at a different value for obtaining new results without having to redo the
	 * simulation
	 */

	// private boolean[] inMen = null;
	/**
	 * minAge is the lower bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	// private int[] maxAge = null;
	/*
	 * popToScenIndex indicates the first scenario that is simulated by a
	 * particular population
	 */
	// private int minAgeInSimulation = 100;
	/**
	 * minAge is the lower bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	// private int maxAgeInSimulation = 0;
	/*
	 * popToScenIndex indicates the first scenario that is simulated by a
	 * particular population
	 */
	// private int[] popToScenIndex;
	/*
	 * categorized indicates whether the continuous variable is categorized in
	 * the output
	 */

	transient private double[][][][][] pSurvivalByOriRiskClassByOriAge;

	private float[] cutoffs;

	private int[] popToScenIndex;

	/**
	 * The constructor initializes the fields (arrays with all values==0), and
	 * copies the information from the scenarioInfo object to the fields in this
	 * object. It also copies the scenario name, as it will write results to the
	 * results directory Secondly, it takes the simulated population and makes
	 * this into summary arrays
	 * 
	 * @param scenInfo
	 * @param pop
	 *            simulated population
	 * @throws DynamoScenarioException
	 * @throws DynamoOutputException
	 *             when newborns are not present with the right starting year
	 */
	public DynamoOutputFactory(ScenarioInfo scenInfo)
			throws DynamoScenarioException, DynamoOutputException {
		super();
		/*
		 * copy the information from scenInfo into the current object (as
		 * fields)
		 */
		this.nDim = 106;
		initializeClassInfo(scenInfo);
		// extractArraysFromPopulations(pop);
		// makeArraysWithNumbers();

	}

	/*
	 * code for setting line width XYLineAndShapeRenderer renderer = new
	 * XYLineAndShapeRenderer(); renderer.setSeriesStroke(number of series, new
	 * BasicStroke(2.0f)). plot.setRenderer(renderer);
	 */

	/**
	 * @param scenInfo
	 * @param simName
	 * @throws DynamoOutputException
	 */
	private void initializeClassInfo(ScenarioInfo scenInfo)
			throws DynamoOutputException {

		this.riskType = (scenInfo.getRiskType());
		this.nScen = (scenInfo.getNScenarios());
		this.durationClass = scenInfo.getIndexDurationClass();
		this.scenInitial = scenInfo.getInitialPrevalenceType();
		this.scenTrans = scenInfo.getTransitionType();
		this.withNewborns = scenInfo.isWithNewBorns();
		/*
		 * this is an indicator that indicates whether one-for-all populations
		 * exist at all
		 */
		this.oneScenPopulation = false;
		this.isOneScenPopulation = scenInfo.getIsOneScenPopulation();
		for (int i = 0; i < this.isOneScenPopulation.length; i++)
			if (this.isOneScenPopulation[i])
				this.oneScenPopulation = true;
		this.popToScenIndex = scenInfo.getPopToScenIndex();
		// setSuccesrate(scenInfo.getSuccesrate());
		// this.inMen = scenInfo.getInMen();
		// this.inWomen = scenInfo.getInWomen();
		// this.minAge = scenInfo.getMinAge();
		// this.maxAge = scenInfo.getMaxAge();
		this.scenarioNames = new String[scenInfo.getScenarioNames().length + 1];
		this.scenarioNames[0] = "reference scenario";

		for (int i = 1; i <= scenInfo.getScenarioNames().length; i++)
			this.scenarioNames[i] = scenInfo.getScenarioNames()[i - 1];
		this.cutoffs = scenInfo.getCutoffs();

		this.nPopulations = scenInfo.getNPopulations();

		/* calculate the number of one-population-for-all-scenarios situation */

		this.stepsInRun = (scenInfo.getYearsInRun());
		setStructure(scenInfo.getStructure());
		setNDiseases(scenInfo.getStructure());
		setNDiseaseStates(scenInfo.getStructure());

		this.baselineAbility = scenInfo.getBaselineAbility();
		this.diseaseAbility = scenInfo.getDiseaseAbility();
		this.relRiskAbilityCat = scenInfo.getRelRiskAbilityCat();
		this.relRiskAbilityCont = scenInfo.getRelRiskAbilityCont();
		this.relRiskAbilityBegin = scenInfo.getRelRiskAbilityBegin();
		this.relRiskAbilityEnd = scenInfo.getRelRiskAbilityEnd();
		this.alfaAbility = scenInfo.getAlfaAbility();
		this.referenceRiskFactorValue = scenInfo.getReferenceRiskFactorValue();

		this.stateNames = new String[this.nDiseaseStates];
		this.diseaseNames = new String[this.nDiseases];
		int currentDis = 0;
		int currentState = 0;
		if (this.nDiseases > 0)
			for (int c = 0; c < this.structure.length; c++) {
				if (this.structure[c].getNInCluster() > 1
						&& !this.structure[c].isWithCuredFraction())
					for (int i = 1; i < Math.pow(2,this.structure[c]
							.getNInCluster()); i++) {
						this.stateNames[currentState] = "";
						for (int d1 = 0; d1 < this.structure[c].getNInCluster(); d1++) {

							if ((i & (1 << d1)) == (1 << d1))
								if (this.stateNames[currentState] == "")
									this.stateNames[currentState] = this.structure[c]
											.getDiseaseName().get(d1);
								else
									this.stateNames[currentState] = this.stateNames[currentState]
											+ "+"
											+ this.structure[c]
													.getDiseaseName().get(d1);
						}
						currentState++;
					}

				for (int d = 0; d < this.structure[c].getNInCluster(); d++) {
					this.diseaseNames[currentDis] = this.structure[c]
							.getDiseaseName().get(d);
					currentDis++;
					if (this.structure[c].getNInCluster() == 1
							|| this.structure[c].isWithCuredFraction()) {

						this.stateNames[currentState] = this.structure[c]
								.getDiseaseName().get(d);
						currentState++;
					}

				}
			}

		this.startYear = scenInfo.getStartYear();
		this.populationSize = scenInfo.getPopulationSize();
		int newbornStart = scenInfo.getNewbornStartYear();
		if (newbornStart == this.startYear)
			this.newborns = scenInfo.getNewborns();
		else if (newbornStart < this.startYear && this.withNewborns) {
			int nYears = scenInfo.getNewborns().length;
			int difference = this.startYear - newbornStart;
			this.newborns = new int[nYears - difference];
			int[] oldData = scenInfo.getNewborns();
			for (int year = 0; year < scenInfo.getNewborns().length
					- difference; year++)
				this.newborns[year] = oldData[year + difference];
		} else if (newbornStart > this.startYear && this.withNewborns) {
			throw new DynamoOutputException(
					"first year with newborns is larger than first year in simulation");
		}
		/*
		 * in case the length of newborns is shorter than the number of newborn
		 * generations needed, use the last newborn number for all further
		 * generations
		 */
		if (this.withNewborns && this.stepsInRun > this.newborns.length) {
			int[] temp = new int[this.stepsInRun];
			for (int y = 0; y < this.newborns.length; y++)
				temp[y] = this.newborns[y];
			for (int y = this.newborns.length; y < this.stepsInRun; y++)
				temp[y] = this.newborns[this.newborns.length - 1];
			this.newborns = temp;
		}
		this.mfratio = scenInfo.getMaleFemaleRatio();
		this.riskClassnames = scenInfo.getRiskClassnames();
		if (this.riskType != 2) {
			this.oldPrevalence = scenInfo.getOldPrevalence();
			this.newPrevalence = scenInfo.getNewPrevalence();
		}
		this.oldDurationNumbers = scenInfo.getOldDurationClasses();

		// TODO remove this temporary solution
		if (this.newPrevalence == null) {
			this.newPrevalence = new float[1][][][];
			this.newPrevalence[0] = this.oldPrevalence;
		}

		if (getRiskType() == 1 || getRiskType() == 3)
			this.nRiskFactorClasses = scenInfo.getRiskClassnames().length;
		else {

			if (this.cutoffs == null)
				this.nRiskFactorClasses = 5;
			/*
			 * NB the names and cutoffs are taken from the data, this is part of
			 * the method extractArraysFroPopulations
			 */
			else {
				this.nRiskFactorClasses = this.cutoffs.length + 1;
				this.riskClassnames = new String[getNRiskFactorClasses()];
				for (int i = 0; i < this.cutoffs.length; i++) {
					if (i > 0)
						this.riskClassnames[i] = this.cutoffs[i - 1] + "-"
								+ this.cutoffs[i];
				}
				this.riskClassnames[0] = "<" + this.cutoffs[0];
				this.riskClassnames[this.cutoffs.length] = ">"
						+ this.cutoffs[this.cutoffs.length - 1];

			}
			;
		}
		/*
		 * as the starting situation is also part of the results, the dimension
		 * of the arrays should be stepsInRun+1
		 */
		int nClasses = getNRiskFactorClasses();
		this.meanRiskByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];
		this.pSurvivalByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		this.pSurvivalByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];
		this.pDisabilityByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		this.pDisabilityByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];
		this.pTotalDiseaseByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		this.pTotalDiseaseByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];

		this.nPopByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		this.nPopByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];
		this.meanRiskByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];
		if (this.riskType == 2)
			this.meanRiskByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		if (this.riskType == 3)
			this.meanRiskByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];

		/*
		 * NB the dimension can be nClasses (nClasses-1) but this makes life
		 * more difficult for now we suppose we have enough room for doing it
		 * this way
		 */
		this.pDiseaseStateByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseaseStates][nClasses][96 + this.stepsInRun][2];
		this.nDiseaseStateByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseaseStates][nClasses][96 + this.stepsInRun][2];
		this.pDiseaseStateByOriRiskClassByAge = new double[this.nScen + 1][this.nDim][this.nDiseaseStates][nClasses][96][2];
		this.nDiseaseStateByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][this.nDiseaseStates][nClasses][96][2];
		this.minAgeInSimulation = 100;
		this.minAgeInSimulationAtStart = 100;
		this.maxAgeInSimulation = 0;
		this.nInSimulation = new int[2];
		this.nInSimulationByAge = new int[this.nDim][2];
		this.nNewBornsInSimulationByAge = new int[this.stepsInRun][2];
		this.nInSimulationByRiskClassByAge = new int[this.nRiskFactorClasses][this.nDim][2];
		this.nInSimulationByRiskClassAndDurationByAge = new int[this.nRiskFactorClasses][100][this.nDim][2];

	}

	/**
	 * this method extracts summary arrays from the simulated population the
	 * arrays are fields of the object DynamoOutPutFactory, named
	 * pPopByRiskClassByAge, pDiseaseStateByRiskClassByAge
	 * pDiseaseByRiskClassByAge and meanRiskByRiskClassByAge. The first three
	 * contain the proportion of the initial population (of the particular age
	 * and ses) that is resp. - still alive and in a particular riskclasse -
	 * still alive and in a particular riskclass and in a particular diseases
	 * state, or - still alive and in a particular riskclass and has a
	 * particular disease the last one give the average riskfactor state of the
	 * persons still alive (with a particular age and sex and possibly
	 * riskfactor state )
	 * 
	 * @param pop
	 *            simulated population
	 * 
	 * 
	 * @throws DynamoScenarioException
	 * @throws DynamoInconsistentDataException 
	 * 
	 */
	public void extractNumbersFromPopulation(Population[] pop)
			throws DynamoScenarioException, DynamoInconsistentDataException {

		// TODO newborns weighting
		// TODO weighting of initial scenario that is not a one-for-all scenario
		/*
		 * check if all the bookkeeping with number of populations agrees with
		 * the real number of populations Is redundant if correct, but just to
		 * be sure
		 */
		if (pop.length != this.nPopulations)
			throw new DynamoScenarioException(
					"something goes wrong "
							+ " in calculating the number of simulated populations. \nThere are "
							+ this.nPopulations
							+ " populations expected but only " + pop.length
							+ " found");

		if (this.riskType == 2 && this.cutoffs == null)
			setCutoffs(pop);

		/* set dimension of arrays */

		/* intialized arrays */
		/* these arrays will contain the results of the all-for-one population */
		/*
		 * plus the data from the reference population (when to (index2) equal
		 * to from (index3)) the latter are the data for individual with
		 * riskfactor to=from at the start of the simulation
		 * 
		 * indexes are: simulation year,[diseasestate] riskfactor clas
		 * from(=orginal), riskfactor class to (=in scenario),current riskfactor
		 * class, age, sex
		 */

		int sexIndex = 0;
		int ageIndex = 0;

		double weight[][][] = new double[this.nRiskFactorClasses][96][2];
		double[][][] weight2 = new double[100][96][2];
		double weightNewborns[][] = new double[this.nRiskFactorClasses][2];
		double[][] weight2Newborns = new double[100][2];

		float[] compoundData;
		float survival;

		/*
		 * for categorical / compound variables: get information on the number
		 * of simulation subjects per risk class and use these to calculate
		 * weighting factors for individuals in order to calculated all outcomes
		 * valid for a population with the distribution of risk factors given in
		 * oldPrevalence
		 * 
		 * 
		 * 
		 * for all riskfactors: get the numbers of simulated persons
		 */

		int[] minMaxData = extractNumberInSimulationFromPopulation(pop);
		if (minMaxData[0] == 100)
			throw new DynamoScenarioException(
					" empty populations in simulation");
		int minimumAge = Math.max(0, minMaxData[0]);
		int minimumGender = minMaxData[2];
		int numberOfAgesInPop = minMaxData[1] - minimumAge + 1;
		int numberOfGendersInPop = minMaxData[3] - minimumGender + 1;
		
		/* next statement is nice to see howfar the program has progressed, but not used in final release 
		
		log.debug("minimum age " + minimumAge + " maximum age " + minMaxData[1]
				+ "\nminimum sex " + minimumGender + " maximum sex "
				+ minMaxData[3]);

		/*
		 * these arrays are kept as small as possible by letting starting age
		 * start at zero (ori arrays only) and gender always at zero
		 */
		/* this requires accurate bookkeeping */

		/*
		 * in the arrays the riskfactor at start is the one that is changed,
		 * while the riskfactor during follow-up is different, therefor three
		 * indexes for riskfactor are required: from, to, and current
		 */
		double[][][][][][] pSurvivalByRiskClassByAge_scen = null;
		if (this.oneScenPopulation)
			pSurvivalByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][numberOfGendersInPop];
		double[][][][][][] pDisabilityByRiskClassByAge_scen = null;
		if (this.oneScenPopulation)
			pDisabilityByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][numberOfGendersInPop];
		double[][][][][][] pTotalDiseaseByRiskClassByAge_scen = null;
		if (this.oneScenPopulation)
			pTotalDiseaseByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][numberOfGendersInPop];

		double[][][][][][][] pDiseaseStateByRiskClassByAge_scen = null;
		if (this.oneScenPopulation)

			pDiseaseStateByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nDiseaseStates][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][numberOfGendersInPop];
		/* in the ori-version the current riskclass is not needed */
		double[][][][][] pSurvivalByOriRiskClassByAge_scen = null;
		if (this.oneScenPopulation)
			pSurvivalByOriRiskClassByAge_scen = new double[this.nDim][this.nRiskFactorClasses][this.nRiskFactorClasses][numberOfAgesInPop][numberOfGendersInPop];
		double[][][][][] pDisabilityByOriRiskClassByAge_scen = null;
		if (this.oneScenPopulation)
			pDisabilityByOriRiskClassByAge_scen = new double[this.nDim][this.nRiskFactorClasses][this.nRiskFactorClasses][numberOfAgesInPop][numberOfGendersInPop];
		double[][][][][] pTotalDiseaseByOriRiskClassByAge_scen = null;
		if (this.oneScenPopulation)
			pTotalDiseaseByOriRiskClassByAge_scen = new double[this.nDim][this.nRiskFactorClasses][this.nRiskFactorClasses][numberOfAgesInPop][numberOfGendersInPop];

		double[][][][][][] pDiseaseStateByOriRiskClassByAge_scen = null;
		if (this.oneScenPopulation)
			pDiseaseStateByOriRiskClassByAge_scen = new double[this.nDim][this.nDiseaseStates][this.nRiskFactorClasses][this.nRiskFactorClasses][numberOfAgesInPop][numberOfGendersInPop];

		double[][][][][][] MeanRiskByRiskClassByAge_scen = null;
		double[][][][][] MeanRiskByOriRiskClassByAge_scen = null;
		if (this.riskType == 3 && this.oneScenPopulation) {
			MeanRiskByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][numberOfGendersInPop];
			MeanRiskByOriRiskClassByAge_scen = new double[this.nDim][this.nRiskFactorClasses][this.nRiskFactorClasses][numberOfAgesInPop][numberOfGendersInPop];
		}

		/*
		 * calculated weights : weighting factors for individuals in order to
		 * calculated all outcomes valid for a population with the distribution
		 * of risk factors given in oldPrevalence
		 * 
		 * the numbers sum up to nSim= nInSimulationByAge at the start of
		 * simulation
		 */

		for (int s = minMaxData[2]; s <= minMaxData[3]; s++)
			for (int age = Math.max(0, minMaxData[0]); age <= minMaxData[1]; age++) {

				for (int r = 0; r < this.nRiskFactorClasses; r++)
					if (this.riskType != 2) {/*
											 * hier gaat het fout out of bounds
											 * -20
											 */
						/*
						 * NB if there are newborns but no zero-year olds at the
						 * start of the simulation, the zero year olds will get
						 * a weight of zero
						 */

						if (this.nInSimulationByRiskClassByAge[r][age][s] > 0)
							weight[r][age][s] = this.oldPrevalence[age][s][r]
									* this.nInSimulationByAge[age][s]
									/ this.nInSimulationByRiskClassByAge[r][age][s];
						else
							weight[r][age][s] = 0;
						if (weight[r][age][s] == 0
								&& this.oldPrevalence[age][s][r] != 0
								&& this.minAgeInSimulationAtStart >= age
								&& this.maxAgeInSimulation <= age)
							throw new DynamoScenarioException(
									" no simulated individuals with riskclass "
											+ this.riskClassnames[r]
											+ " in simulation, while the prevalence of this riskclass is "
											+ this.oldPrevalence[age][s][r]
											+ " for age " + age
											+ " and gender " + s);
						if (this.riskType == 3 && r == this.durationClass)
							for (int duur = 0; duur < this.oldDurationNumbers[age][s].length; duur++) {
								if (this.nInSimulationByRiskClassAndDurationByAge[r][duur][age][s] > 0)
									weight2[duur][age][s] = this.oldPrevalence[age][s][r]
											* this.oldDurationNumbers[age][s][duur]
											* this.nInSimulationByAge[age][s]
											/ this.nInSimulationByRiskClassAndDurationByAge[r][duur][age][s];
								else
									weight2[duur][age][s] = 0;
								if (weight2[duur][age][s] == 0
										&& (this.oldPrevalence[age][s][r] * this.oldDurationNumbers[age][s][duur]) != 0
										&& this.minAgeInSimulationAtStart >= age
										&& this.maxAgeInSimulation <= age)
									throw new DynamoScenarioException(
											" no simulated individuals with riskclass "
													+ this.riskClassnames[r]
													+ " and duration "
													+ duur
													+ " in simulation, while the prevalence of this riskclass is "
													+ this.oldPrevalence[age][s][r]
													* this.oldDurationNumbers[age][s][duur]
													+

													" for age " + age
													+ " and gender " + s);
							}

					} else {
						weight[r][age][s] = 1;
						weightNewborns[r][s] = 1;
					}

			}

		/*
		 * 
		 * 
		 * extract the information from the simulated populations
		 */

		int from = 0;
		int to = 0;
		String indLabel;
		String delims = "[_]";
		String[] tokens = new String[4];

		double[] weightOfReferenceIndividual = new double[pop[0].size()];

		for (int thisPop = 0; thisPop < this.nPopulations; thisPop++) {

			int currentIndividualNo = -1;
			Iterator<Individual> individualIterator = pop[thisPop].iterator();

			/*
			 * make detailed arrays summing the data for sex/age/year/risk class
			 * combinations
			 */

			/* start with reading the data from the population */
			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();
				currentIndividualNo++;
				double weightOfIndividual = 1;
				if (thisPop == 0)
					weightOfReferenceIndividual[currentIndividualNo] = 1;

				int riskClassAtStart = -1;

				int ageAtStart = (int) Math.round(((Float) individual.get(1)
						.getValue(0)));
				int ageAtStartRelativeToMinimum = ageAtStart - minimumAge;

				int nSteps = 105 - ageAtStart + 1;
				if (ageAtStart < 0)
					nSteps = this.stepsInRun + 1;

				if (ageAtStart == 27) {
					int ii = 0;
					ii++;
				}

				for (int stepCount = 0; stepCount < nSteps; stepCount++) {
					/*
					 * get the information of this individual at the stepCount
					 * step for the simulation
					 */
					ageIndex = (int) Math.round(((Float) individual.get(1)
							.getValue(stepCount)));
					sexIndex = (int) (Integer) individual.get(2).getValue(
							stepCount);
					int ageIndexRelativeToMinimum = ageIndex - minimumAge;
					int sexIndexRelativeToMinimum = sexIndex - minimumGender;

					if (ageIndex >= 0) {
						int riskFactor = 0;
						float riskValue = 0;
						int riskDurationValue = 0;

						if (this.riskType != 2) {
							riskFactor = (int) (Integer) individual.get(3)
									.getValue(stepCount);

						} else {
							riskValue = (float) (Float) individual.get(3)
									.getValue(stepCount);
							int i = 0;
							if (this.riskClassnames.length > 1) {
								if (riskValue <= this.cutoffs[0])
									riskFactor = 0;

								else {
									for (i = 1; i < this.cutoffs.length; i++) {
										if (riskValue <= this.cutoffs[i]
												&& riskValue > this.cutoffs[i - 1])
											break;
									}

									riskFactor = i;
									/* just to be sure that it goes OK: */
									if (riskValue > this.cutoffs[this.cutoffs.length - 1])
										riskFactor = this.cutoffs.length;
								}
							} else { /*
									 * only one single value present for
									 * riskValue
									 */
								riskFactor = 0;

							}
						}

						if (this.riskType == 3)

							riskDurationValue = Math
									.round((float) (Float) individual.get(4)
											.getValue(stepCount));

						/*
						 * calculate the age and riskclass at start NB: this
						 * will be missing for newborns; they will not be
						 * included in the summary arrays for calculating
						 * life-expectancy
						 */

						if (stepCount == 0) {
							ageAtStart = ageIndex;
							ageAtStartRelativeToMinimum = ageIndexRelativeToMinimum;

						}
						/* this will also work for newborns */
						if (riskClassAtStart == -1) {
							riskClassAtStart = riskFactor;

						}

						/*
						 * the weighting is meant to make the distribution of
						 * the categorical risk factor in the reference scenario
						 * equal to what is given as distribution (despite the
						 * fact that because of a finite number of simulated
						 * cases, the distribution in the simulated population
						 * is different. the weight of an individual should be
						 * the same in each scenario. So the weight is
						 * determined by the value of the riskfactor in the
						 * reference scenario
						 */
						/*
						 * In changing initial prevalence scenarios this is the
						 * value of "from". In changing transition rate
						 * scenarios the initial distribution is the same in the
						 * reference population and the scenario population
						 */

						if (stepCount == 0 && this.riskType != 2) {
							if (ageIndex > 100)
								log.debug(stepCount + " " + riskFactor + " "
										+ ageIndex + " " + sexIndex);
						}
						/* is start year for this individual */
						if ((stepCount == 0 || ageIndex == 0)
								&& this.riskType != 2) {
							if (thisPop == 0)
								weightOfReferenceIndividual[currentIndividualNo] = weight[riskFactor][ageIndex][sexIndex];
							if (!this.isOneScenPopulation[thisPop])
								weightOfIndividual = weightOfReferenceIndividual[currentIndividualNo];

						}
						if ((stepCount == 0 || ageIndex == 0)
								&& this.riskType == 3
								&& riskFactor == this.durationClass) {
							if (thisPop == 0)
								weightOfReferenceIndividual[currentIndividualNo] = weight2[riskDurationValue][ageIndex][sexIndex];
							if (!this.isOneScenPopulation[thisPop])
								weightOfIndividual = weightOfReferenceIndividual[currentIndividualNo];
						}
						// log.fatal(ageIndex+" =age; stepCount="+stepCount);
						if (this.riskType == 3)
							compoundData = ((CompoundCharacteristicValue) individual
									.get(5)).getUnwrappedValue(stepCount);

						else
							compoundData = ((CompoundCharacteristicValue) individual
									.get(4)).getUnwrappedValue(stepCount);
						if (ageIndex == 57 && stepCount == 1 && sexIndex == 1) {

							int stop = 1;
							stop++;
						}
						survival = compoundData[getNDiseaseStates() - 1];
						double daly = makeDaly(compoundData, riskFactor,
								riskValue, riskDurationValue, ageIndex,
								sexIndex);
						double totalDisease = makeTotalDisease(compoundData);
						/*
						 * if the population is a one-for-all population, read
						 * the from (risk factor without the scenario change)
						 * and to (with the scenario change) from the label of
						 * the individual
						 */

						if (thisPop > 0)
							if (this.isOneScenPopulation[thisPop]) {
								indLabel = individual.getLabel();
								tokens = indLabel.split(delims);
								from = Integer.parseInt(tokens[2]);
								to = Integer.parseInt(tokens[3]);
								/*
								 * overwrite the weight with the weight of the
								 * reference population
								 */
								if (stepCount == 0 || ageIndex == 0) {

									weightOfIndividual = weight[from][ageIndex][sexIndex];

								}
								/*
								 * if (scenInitial[thisPop - 1] &&
								 * scenTrans[thisPop - 1]) { / dit was een
								 * poging to wegen zodat ook referentie scenario
								 * gelijk wordt dat is echter problematisch
								 * wanneer lege cellen, daarom moet daarvoor
								 * eerst de initial population worden aangepast
								 * voorlopig op verlanglijst.
								 * 
								 * indLabel = individual.getLabel(); tokens =
								 * indLabel.split(delims); from =
								 * Integer.parseInt(tokens[2]); float[] dummy =
								 * new float[this.nRiskFactorClasses];
								 * Arrays.fill(dummy, 1); toChange =
								 * NettTransitionRateFactory
								 * .makeNettTransitionRates(
								 * this.oldPrevalence[0][sexIndex],
								 * this.newPrevalence[thisPop - 1][0][sexIndex],
								 * 0, dummy); weightOfIndividual = / target
								 *//*
									 * toChange[from][riskFactor]
									 * this.oldPrevalence[0][sexIndex][from] /
									 * 1/sampled
									 *//**
								 * nInSimulationByAge[ageIndex][sexIndex]
								 * / nInSimulationByFromByTo[thisPop -
								 * 1][from][riskFactor][ageIndex][sexIndex]; }
								 */
							}

						if (ageAtStart == 0 && sexIndex == 0 && stepCount < 3
								&& thisPop > 0)
							log
									.debug("a,g,step,r: " + ageIndex + " "
											+ sexIndex + " " + stepCount + " "
											+ riskFactor + " weight "
											+ weightOfIndividual + " surv: "
											+ survival);

						/*
						 * add the data read to the summary arrays
						 * 
						 * start with adding survival to the summary survival
						 * arrays
						 */
						/*
						 * for a scenario population
						 */
						if (thisPop > 0) {

							/*
							 * for a all-for-one population
							 */

							if (this.isOneScenPopulation[thisPop]) {

								if (stepCount <= this.stepsInRun) {
									pSurvivalByRiskClassByAge_scen[stepCount][from][to][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival;
									pDisabilityByRiskClassByAge_scen[stepCount][from][to][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival * daly;
									pTotalDiseaseByRiskClassByAge_scen[stepCount][from][to][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival * totalDisease;
								}
								if (ageAtStart >= 0) {
									pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival;
									pDisabilityByOriRiskClassByAge_scen[stepCount][from][to][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival * daly;
									pTotalDiseaseByOriRiskClassByAge_scen[stepCount][from][to][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival * totalDisease;
								}
								if (this.riskType == 3) {
									if (stepCount <= this.stepsInRun)
										MeanRiskByRiskClassByAge_scen[stepCount][from][to][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										MeanRiskByOriRiskClassByAge_scen[stepCount][from][to][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
												* riskDurationValue * survival;
								}

								/* for a "one-for-one" scenario */

							} else {
								if (stepCount <= this.stepsInRun) {
									this.pSurvivalByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* survival;
									this.pDisabilityByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* daly * survival;
									this.pTotalDiseaseByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* totalDisease * survival;

								}
								if (ageAtStart >= 0) {
									this.pSurvivalByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* survival;
									this.pDisabilityByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* daly * survival;
									this.pTotalDiseaseByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* totalDisease * survival;
								}
								if (this.riskType == 2) {
									if (stepCount <= this.stepsInRun)
										this.meanRiskByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* riskValue * survival;
									if (ageAtStart >= 0)
										this.meanRiskByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* riskValue * survival;

									if (stepCount <= this.stepsInRun)
										this.meanRiskByAge[this.popToScenIndex[thisPop] + 1][stepCount][ageIndex][sexIndex] += weightOfIndividual
												* riskValue * survival;
									if (stepCount <= this.stepsInRun) if (Double.isNaN(this.meanRiskByAge[this.popToScenIndex[thisPop] + 1][stepCount][ageIndex][sexIndex])){
										int stop=0;
										stop++;
										
									}
								}
								if (this.riskType == 3) {
									if (stepCount <= this.stepsInRun)
										this.meanRiskByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										this.meanRiskByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;

								}
							}
						}

						/*
						 * for the reference scenario
						 */
						else {
							if (stepCount <= this.stepsInRun) {
								this.pSurvivalByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* survival;
								this.pDisabilityByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* survival * daly;
								this.pTotalDiseaseByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* survival * totalDisease;
							}
							if (ageAtStart >= 0) {
								this.pSurvivalByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
										* survival;
								this.pDisabilityByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
										* survival * daly;
								this.pTotalDiseaseByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
										* survival * totalDisease;
							}
							if (this.riskType == 2) {
								if (stepCount <= this.stepsInRun)
									this.meanRiskByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* riskValue * survival;
								if (stepCount <= this.stepsInRun)
									this.meanRiskByAge[0][stepCount][ageIndex][sexIndex] += weightOfIndividual
											* riskValue * survival;
								if (ageAtStart >= 0)
									this.meanRiskByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* riskValue * survival;

								if (stepCount <= this.stepsInRun) if (Double.isNaN(this.meanRiskByAge[0][stepCount][ageIndex][sexIndex])){
									int stop=0;
									stop++;
									
								}
								
								
							}
							if (this.riskType == 3) {
								if (stepCount <= this.stepsInRun)
									this.meanRiskByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* riskDurationValue * survival;
								if (ageAtStart >= 0)
									this.meanRiskByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* riskDurationValue * survival;

							}

							/*
							 * in case there is a one-for-all scenario, the
							 * reference scenario should be added to the
							 * summary-array of this scenario, because
							 * not-changing is also a part of possible scenarios
							 */

							if (this.oneScenPopulation) {
								if (stepCount <= this.stepsInRun) {
									/*
									 * the "from" value is identical to the
									 * riskClassAtStart in this case
									 */
									pSurvivalByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival;
									pDisabilityByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival * daly;
									pTotalDiseaseByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival * totalDisease;
								}
								if (ageAtStart >= 0) {
									pSurvivalByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival;
									pDisabilityByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival * daly;
									pTotalDiseaseByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
											* survival * totalDisease;
								}
								if (this.riskType == 3) {
									if (stepCount <= this.stepsInRun)
										MeanRiskByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										MeanRiskByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
												* riskDurationValue * survival;
								}
							}
						}
						if (ageAtStart == 3 && sexIndex == 0 && stepCount < 5
								&& stepCount < this.stepsInRun && thisPop > 0)
							log
									.debug("pSurvival reference for riskfactor "
											+ riskFactor
											+ " = "
											+ this.pSurvivalByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex]);

						if (ageAtStart == 3 && sexIndex == 0 && stepCount < 5
								&& stepCount < this.stepsInRun && thisPop > 0
								&& this.oneScenPopulation)
							log
									.debug("pSurvival_scen voor stepcount "
											+ stepCount
											+ " from=to "
											+ riskFactor
											+ " age "
											+ ageIndex
											+ " = "
											+ pSurvivalByRiskClassByAge_scen[stepCount][riskFactor][riskFactor][riskFactor][ageIndex][sexIndexRelativeToMinimum]);
						/*
						 * add disease states to disease state arrays in a
						 * similar fashion
						 */
						for (int s = 0; s < this.nDiseaseStates; s++) {

							if (thisPop > 0)
								if (this.isOneScenPopulation[thisPop]) {
									if (stepCount <= this.stepsInRun)
										pDiseaseStateByRiskClassByAge_scen[stepCount][s][from][to][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
												* compoundData[s] * survival;
									if (ageAtStart >= 0)
										pDiseaseStateByOriRiskClassByAge_scen[stepCount][s][from][to][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
												* compoundData[s] * survival;

								} else {
									if (stepCount <= this.stepsInRun)
										this.pDiseaseStateByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][s][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;
									if (ageAtStart >= 0)
										this.pDiseaseStateByOriRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][s][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;
								}
							if (thisPop == 0) {
								/*
								 * riskClassAtStart plays role of both from and
								 * to here
								 */

								if (this.oneScenPopulation) {
									if (stepCount <= this.stepsInRun)
										pDiseaseStateByRiskClassByAge_scen[stepCount][s][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndexRelativeToMinimum] += weightOfIndividual
												* compoundData[s] * survival;
									if (ageAtStart >= 0)
										pDiseaseStateByOriRiskClassByAge_scen[stepCount][s][riskClassAtStart][riskClassAtStart][ageAtStartRelativeToMinimum][sexIndexRelativeToMinimum] += weightOfIndividual
												* compoundData[s] * survival;
								}

								if (stepCount <= this.stepsInRun)
									this.pDiseaseStateByRiskClassByAge[0][stepCount][s][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* compoundData[s] * survival;
								if (ageAtStart >= 0)
									this.pDiseaseStateByOriRiskClassByAge[0][stepCount][s][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* compoundData[s] * survival;

							}

						}
						if (ageAtStart == 0 && sexIndex == 0 && stepCount < 2
								&& stepCount < this.stepsInRun)
							log
									.debug("pDiseaseState "
											+ this.pDiseaseStateByRiskClassByAge[0][stepCount][sexIndex][riskFactor][ageIndex][sexIndex]
											+ " riskfactor " + riskFactor
											+ " weight " + weightOfIndividual);

						// TODO for with cured fraction
						// float [] disease = (float[]) individual.get(4)
						// .getValue(stepCount);

						// simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][riskFactor]++;
						// simulatedDiseasePrevalence[stepCount][ageIndex][sexIndex]
						// +=
						// disease[0];
						// simulatedSurvival[stepCount][ageIndex][sexIndex] +=
						// disease[3];
					} // end if age>=0
				}// end over stepCount
			}// end loop over individuals
		}// end loop populations

		/*
		 * 
		 * 
		 * combine data from the one-population-for-all-scenarios to separate
		 * scenario's
		 * 
		 * first for the arrays based on current age and current risk
		 * factorstatus
		 */
		if (this.oneScenPopulation) {
			float[] dummy = new float[this.nRiskFactorClasses];
			Arrays.fill(dummy, 1);
			float[][] toChange;

			int minSimAge = Math.max(0, minMaxData[0]);
			int maxSimAge = minMaxData[1];

			for (int scen = 0; scen < this.nScen; scen++)

				if (this.scenInitial[scen] && !this.scenTrans[scen]) {
					/*
					 * calculate the transitions needed from old to new
					 * prevalence
					 */
					for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++) {
						int amax = Math.min(maxSimAge + 1 + stepCount, nDim);
						/* only do for ages that are in simulation */
						int minimumForLoop = minSimAge + stepCount;
						/* if there are newborns, then always start at zero */
						if (this.minAgeInSimulation < 0)
							minimumForLoop = 0;
						for (int a = minimumForLoop; a < amax; a++)
							for (int s = minMaxData[2]; s <= minMaxData[3]; s++)

							{
								/*
								 * for safety, initialize arrays for (int r = 0;
								 * r < this.nRiskFactorClasses; r++) {
								 * this.pSurvivalByRiskClassByAge[scen +
								 * 1][stepCount][r][a][s] = 0;
								 * this.pDisabilityByRiskClassByAge[scen +
								 * 1][stepCount][r][a][s] = 0; for (int state =
								 * 0; state < this.nDiseaseStates; state++)
								 * this.pDiseaseStateByRiskClassByAge[scen +
								 * 1][stepCount][state][r][a][s] = 0; }
								 */
								
								if (a >= stepCount){
									toChange = NettTransitionRateFactory
											.makeNettTransitionRates(
													this.oldPrevalence[a
															- stepCount][s],
													this.newPrevalence[scen][a
															- stepCount][s], 0,
													dummy);
									}
								else{
									/* for newborns */
									toChange = NettTransitionRateFactory
											.makeNettTransitionRates(
													this.oldPrevalence[0][s],
													this.newPrevalence[scen][0][s],
													0, dummy);}

								for (from = 0; from < this.nRiskFactorClasses; from++)
									for (to = 0; to < this.nRiskFactorClasses; to++) {
										for (int r = 0; r < this.nRiskFactorClasses; r++) {

											for (int state = 0; state < this.nDiseaseStates; state++)
												if (pDiseaseStateByRiskClassByAge_scen[stepCount][state][from][to][r][a][s
														- minimumGender] != 0)
													this.pDiseaseStateByRiskClassByAge[scen + 1][stepCount][state][r][a][s] += toChange[from][to]
															* pDiseaseStateByRiskClassByAge_scen[stepCount][state][from][to][r][a][s
																	- minimumGender];
											if (pSurvivalByRiskClassByAge_scen[stepCount][from][to][r][a][s
													- minimumGender] != 0)
												this.pSurvivalByRiskClassByAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* pSurvivalByRiskClassByAge_scen[stepCount][from][to][r][a][s
																- minimumGender];
											if (pDisabilityByRiskClassByAge_scen[stepCount][from][to][r][a][s
													- minimumGender] != 0)
												this.pDisabilityByRiskClassByAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* pDisabilityByRiskClassByAge_scen[stepCount][from][to][r][a][s
																- minimumGender];
											if (pTotalDiseaseByRiskClassByAge_scen[stepCount][from][to][r][a][s
													- minimumGender] != 0)

												this.pTotalDiseaseByRiskClassByAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* pTotalDiseaseByRiskClassByAge_scen[stepCount][from][to][r][a][s
																- minimumGender];

											if (this.riskType > 1
													&& MeanRiskByRiskClassByAge_scen[stepCount][from][to][r][a][s
															- minimumGender] != 0)
												this.meanRiskByRiskClassByAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* MeanRiskByRiskClassByAge_scen[stepCount][from][to][r][a][s
																- minimumGender];
										}
										/*
										 * check if there are the required
										 * persons in the "one-for-all-scenario"
										 * situation do this only at step 0, as
										 * they might legitimately be zero at
										 * later times
										 */
										if (stepCount == 0
												&& (minimumAge != minMaxData[1] || minMaxData[0] >= 0)) {
											double sum = 0;
											for (int r = 0; r < this.nRiskFactorClasses; r++)
												sum += pSurvivalByRiskClassByAge_scen[stepCount][from][to][r][a][s
														- minimumGender];
											
											boolean zeroToPrevalence=false;
											boolean zeroFromPrevalence=false;
											 if (a >= stepCount) {
												if( newPrevalence[scen][a - stepCount][s][to]==0) zeroToPrevalence=true;
												if( oldPrevalence[a	- stepCount][s][to]==0) zeroFromPrevalence=true;
											 }
												
									else {
										/* for newborns */ 
										if( newPrevalence[scen][0][s][to]==0) zeroToPrevalence=true;
										if( oldPrevalence[0][s][to]==0) zeroFromPrevalence=true;
										}
											 /* if from prevalence is zero, than for [to=from] the change is made 1 
											  * this should not given an error */
											 /* zeroToPrevalence is probably redundant as than the toChange is also 0 */
											if (sum == 0
													&& toChange[from][to] > 0 && (!zeroToPrevalence) && ( !(zeroFromPrevalence && to==from)) ) {
												log
														.fatal(" not enough simulated information to calculate scenario "
																+ scen
																+ ". No simulated information on changing riskfactor class "
																+ from
																+ " into class "
																+ to
																+ " for age "
																+ a);
												throw new DynamoScenarioException(
														" not enough simulated information to calculate scenario "
																+ scen
																+ ". No simulated information on changing riskfactor class "
																+ from
																+ " into class "
																+ to
																+ " for age "
																+ a);

											}
										}
									}// end to-from loop

							}
					}// end age , sex and stepCount loop
					/*
					 * repeat
					 * 
					 * 
					 * for the arrays based on original age and riskfactors
					 */

					for (int stepCount = 0; stepCount < this.nDim; stepCount++)
						for (int a = minSimAge; a < maxSimAge + 1; a++)
							for (int s = minMaxData[2]; s <= minMaxData[3]; s++)

							{

								/* for safety, initialize arrays *//*
																	 * for (int
																	 * r = 0; r
																	 * <this.
																	 * nRiskFactorClasses
																	 * ; r++) {
																	 * this.
																	 * pSurvivalByOriRiskClassByOriAge
																	 * [scen +
																	 * 1]
																	 * [stepCount
																	 * ]
																	 * [r][a][s]
																	 * = 0;
																	 * this.
																	 * pDisabilityByOriRiskClassByOriAge
																	 * [scen +
																	 * 1]
																	 * [stepCount
																	 * ]
																	 * [r][a][s]
																	 * = 0; for
																	 * (int
																	 * state =
																	 * 0; state
																	 * <this.
																	 * nDiseaseStates
																	 * ;
																	 * state++)
																	 * this.
																	 * pDiseaseStateByOriRiskClassByAge
																	 * [scen +
																	 * 1]
																	 * [stepCount
																	 * ]
																	 * [state][r
																	 * ][a][s] =
																	 * 0; }
																	 */

								toChange = NettTransitionRateFactory
										.makeNettTransitionRates(
												this.oldPrevalence[a][s],
												this.newPrevalence[scen][a][s],
												0, dummy);

								for (from = 0; from < this.nRiskFactorClasses; from++)
									for (to = 0; to < this.nRiskFactorClasses; to++) {
										{

											for (int state = 0; state < this.nDiseaseStates; state++)

												if (pDiseaseStateByOriRiskClassByAge_scen[stepCount][state][from][to][a
														- minimumAge][s
														- minimumGender] > 0)
													this.pDiseaseStateByOriRiskClassByAge[scen + 1][stepCount][state][to][a][s] += toChange[from][to]
															* pDiseaseStateByOriRiskClassByAge_scen[stepCount][state][from][to][a
																	- minimumAge][s
																	- minimumGender];
											if (a == 0 && s == 0
													&& stepCount == 50)
												log
														.debug("scen "
																+ scen
																+ 1
																+ " from "
																+ from
																+ " to "
																+ to
																+ " change "
																+ toChange[from][to]
																+ " scen var "
																+ pDiseaseStateByOriRiskClassByAge_scen[stepCount][0][from][to][a
																		- minimumAge][s
																		- minimumGender]
																+ " results in "
																+ this.pDiseaseStateByOriRiskClassByAge[scen + 1][stepCount][0][to][a][s]);
											if (pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][a
													- minimumAge][s
													- minimumGender] > 0)
												this.pSurvivalByOriRiskClassByOriAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
														* pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][a
																- minimumAge][s
																- minimumGender];
											if (pDisabilityByOriRiskClassByAge_scen[stepCount][from][to][a
													- minimumAge][s
													- minimumGender] > 0)
												this.pDisabilityByOriRiskClassByOriAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
														* pDisabilityByOriRiskClassByAge_scen[stepCount][from][to][a
																- minimumAge][s
																- minimumGender];
											if (pTotalDiseaseByOriRiskClassByAge_scen[stepCount][from][to][a
													- minimumAge][s
													- minimumGender] > 0)
												this.pTotalDiseaseByOriRiskClassByOriAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
														* pTotalDiseaseByOriRiskClassByAge_scen[stepCount][from][to][a
																- minimumAge][s
																- minimumGender];

											if (this.riskType > 1
													&& MeanRiskByOriRiskClassByAge_scen[stepCount][from][to][a
															- minimumAge][s
															- minimumGender] > 0)
												this.meanRiskByOriRiskClassByOriAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
														* MeanRiskByOriRiskClassByAge_scen[stepCount][from][to][a
																- minimumAge][s
																- minimumGender];
										}
										/*
										 * look if data are availlable for the
										 * first step, except when the
										 * population
										 * 
										 * TODO: make this checking also for the
										 * excluded situation
										 */
										if (stepCount == 0
												&& (minimumAge != minMaxData[1] || minMaxData[0] >= 0)) {
											double sum = 0;
											for (int r = 0; r < this.nRiskFactorClasses; r++)
												sum += pSurvivalByRiskClassByAge_scen[stepCount][from][to][r][a][s
														- minimumGender];
											
											boolean zeroToPrevalence=false;
											boolean zeroFromPrevalence=false;
											 
												if( newPrevalence[scen][a ][s][to]==0) zeroToPrevalence=true;
												if( oldPrevalence[a	][s][to]==0) zeroFromPrevalence=true;
											 

											if (sum == 0
													&& toChange[from][to] > 0 && ( !(zeroFromPrevalence && to==from))) {
												log
														.fatal(" not enough simulated information to calculate scenario "
																+ scen
																+ ". No simulated information on changing riskfactor class "
																+ from
																+ " into class "
																+ to);
												throw new DynamoScenarioException(
														" not enough simulated information to calculate scenario "
																+ scen
																+ ". No simulated information on changing riskfactor class "
																+ from
																+ " into class "
																+ to);
											}
										}
									}// end to-from loop

							}// end age , sex and stepCount loop

				}// end loop for scenario

		}

		/*
		 * arrays for average riskfactor value now contain the sum of value
		 * times survival This should be divided by survival
		 */
		if (riskType == 2) {
			int minSimAge = Math.max(0, minMaxData[0]);
			int maxSimAge = minMaxData[1];
			for (int popnr = 0; popnr <= this.nScen; popnr++) {
				for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++) {
					int amax = Math.min(maxSimAge + 1 + stepCount, nDim);
					/* only do for ages that are in simulation */
					int minimumForLoop = minSimAge + stepCount;
					/* if there are newborns, then always start at zero */
					if (this.minAgeInSimulation < 0)
						minimumForLoop = 0;
					for (int a = minimumForLoop; a < amax; a++)
						for (int s = minMaxData[2]; s <= minMaxData[3]; s++) {
							double survtot = 0;
							for (int r = 0; r < this.nRiskFactorClasses; r++) {
								survtot += pSurvivalByRiskClassByAge[popnr][stepCount][r][a][s];
								this.meanRiskByRiskClassByAge[popnr][stepCount][r][a][s] = this.meanRiskByRiskClassByAge[popnr][stepCount][r][a][s]
										/ this.pSurvivalByRiskClassByAge[popnr][stepCount][r][a][s];
							}
							this.meanRiskByAge[popnr][stepCount][a][s] = this.meanRiskByAge[popnr][stepCount][a][s]
									/ survtot;
							if (a == 0)
								log
										.debug("meanriskbyage"
												+ this.meanRiskByAge[popnr][stepCount][a][s]
												+ " survtot " + survtot);
						}
				}
				/*
				 * and also for the arrays with based on original age and risk
				 * factor
				 */
				for (int stepCount = 0; stepCount < this.nDim; stepCount++)
					for (int a = minSimAge; a < maxSimAge + 1; a++)
						for (int s = minMaxData[2]; s <= minMaxData[3]; s++) {
							for (int r = 0; r < this.nRiskFactorClasses; r++)
								this.meanRiskByOriRiskClassByOriAge[popnr][stepCount][r][a][s] = this.meanRiskByOriRiskClassByOriAge[popnr][stepCount][r][a][s]
										/ this.pSurvivalByOriRiskClassByOriAge[popnr][stepCount][r][a][s];
						}
			}
		}

		/* and similarly for the duration value for risk type=3 */
		if (riskType == 3) {
			int minSimAge = Math.max(0, minMaxData[0]);
			int maxSimAge = minMaxData[1];
			for (int popnr = 0; popnr <= this.nScen; popnr++) {
				for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++) {
					int amax = Math.min(maxSimAge + 1 + stepCount, nDim);
					/* only do for ages that are in simulation */
					int minimumForLoop = minSimAge + stepCount;
					/* if there are newborns, then always start at zero */
					if (this.minAgeInSimulation < 0)
						minimumForLoop = 0;
					for (int a = minimumForLoop; a < amax; a++)
						for (int s = minMaxData[2]; s <= minMaxData[3]; s++) {
							int r = this.durationClass;

							double survtot = pSurvivalByRiskClassByAge[popnr][stepCount][r][a][s];
							
							this.meanRiskByRiskClassByAge[popnr][stepCount][r][a][s] = this.meanRiskByRiskClassByAge[popnr][stepCount][r][a][s]
									/ this.pSurvivalByRiskClassByAge[popnr][stepCount][r][a][s];

							this.meanRiskByAge[popnr][stepCount][a][s] = this.meanRiskByAge[popnr][stepCount][a][s]
									/ survtot;
							if (a == 0)
								log
										.debug("meanriskbyage"
												+ this.meanRiskByAge[popnr][stepCount][a][s]
												+ " survtot " + survtot);
						}
				}
				/*
				 * and also for the arrays with based on original age and risk
				 * factor
				 * Here we can have also mean durations in other classes 
				 */
				for (int stepCount = 0; stepCount < this.nDim; stepCount++)
					for (int a = minSimAge; a < maxSimAge + 1; a++)
						for (int s = minMaxData[2]; s <= minMaxData[3]; s++) {
							for (int r = 0; r < this.nRiskFactorClasses; r++)
								this.meanRiskByOriRiskClassByOriAge[popnr][stepCount][r][a][s] = this.meanRiskByOriRiskClassByOriAge[popnr][stepCount][r][a][s]
										/ this.pSurvivalByOriRiskClassByOriAge[popnr][stepCount][r][a][s];
						}
			}
		}

	}

	private double makeTotalDisease(float[] compoundData) {
		double totalDisease = 0;
		double healthy = 1;

		/*
		 * general idea: calculate the disease per cluster by adding up the
		 * diseasestates in the cluster Diseasefree= product of all disease free
		 * proportions with disease = 1-diseasefree
		 */

		int currentClusterStart = 0;
		/*
		 * if there are no diseases in the model, structure is null
		 */

		if (this.structure != null) {
			for (int c = 0; c < this.structure.length; c++) {
				double probWithDisease = 0;

				if (!this.structure[c].isWithCuredFraction()) {
					for (int state = 0; state < (Math.pow(2, this.structure[c]
							.getNInCluster()) - 1); state++) {

						probWithDisease += compoundData[currentClusterStart
								+ state];
					}
					currentClusterStart += Math.pow(2, this.structure[c]
							.getNInCluster()) - 1;
				} else {
					/* with cured fraction */
					probWithDisease = compoundData[currentClusterStart]
							+ compoundData[currentClusterStart + 1];

					currentClusterStart += 2;
				}

				healthy *= (1 - probWithDisease);

			}
		}
		totalDisease = 1 - healthy;

		return totalDisease;
	}

	/**
	 * returns a Daly value for a particular age, gender, riskfactorstate
	 * combination
	 * 
	 * @param compoundData
	 *            : disease data as in Dynamo-simulation object
	 * @param riskFactor
	 *            : riskfactor category
	 * @param riskValue
	 *            : continuous risk factor state
	 * @param riskDurationValue
	 *            : duration state
	 * @param ageIndex
	 *            : age
	 * @param sexIndex
	 *            : gender
	 * @return daly-value (double)
	 */
	private double makeDaly(float[] compoundData, int riskFactor,
			float riskValue, int riskDurationValue, int age, int sex) {

		int age2 = Math.min(95, age);
		int currentClusterStart = 0;
		double ability = 1;
		if (this.nDiseases > 0)
			for (int c = 0; c < this.structure.length; c++) {
				if (!this.structure[c].isWithCuredFraction()) {
					double clusterAbility = 0;
					for (int state = 0; state < Math.pow(2, this.structure[c]
							.getNInCluster()); state++) {
						double probState = 0;
						if (state == 0) {
							probState = 1;
							for (int state2 = 0; state2 < (Math.pow(2,
									this.structure[c].getNInCluster()) - 1); state2++)
								probState -= compoundData[currentClusterStart
										+ state2];
						} else
							probState = compoundData[currentClusterStart
									+ state - 1];
						double abilityState = 1;
						for (int d = 0; d < this.structure[c].getNInCluster(); d++) {

							if ((state & (1 << d)) == (1 << d))
								abilityState *= diseaseAbility[age2][sex][this.structure[c]
										.getDiseaseNumber()[d]];
						}
						clusterAbility += abilityState * probState;

					}
					currentClusterStart += Math.pow(2, this.structure[c]
							.getNInCluster()) - 1;
					ability *= clusterAbility;
				} else {
					/* with cured fraction */

					double probDisease = compoundData[currentClusterStart]
							+ compoundData[currentClusterStart + 1];
					ability *= (1 - probDisease + probDisease
							* diseaseAbility[age2][sex][this.structure[c]
									.getDiseaseNumber()[0]]);
					currentClusterStart += 2;
				}

			}

		// TODO including riskfactor
		double daly = 0;
		if (this.riskType == 1)
			daly = (1 - this.baselineAbility[age2][sex]
					* this.relRiskAbilityCat[age2][sex][riskFactor] * ability);
		if (this.riskType == 2)
			daly = (1 - this.baselineAbility[age2][sex]
					* Math.pow(this.relRiskAbilityCont[age2][sex], riskValue
							- this.referenceRiskFactorValue) * ability);
		if (this.riskType == 3) {
			double RR = 1;
			if (riskFactor == this.durationClass)
				RR = (this.relRiskAbilityBegin[age2][sex] - this.relRiskAbilityEnd[age2][sex])
						* Math.exp(-riskDurationValue
								* this.alfaAbility[age2][sex])
						+ this.relRiskAbilityEnd[age2][sex];
			else
				RR = this.relRiskAbilityCat[age2][sex][riskFactor];
			daly = (1 - this.baselineAbility[age2][sex] * RR * ability);
			if (this.alfaAbility[age2][sex] < 0)
				log.fatal("!!!! NEGATIVE alfa-ability");
		}
		if (age == 0) {
			int b = 0;
			b++;

		}

		return daly;
	}

	/**
	 * The array takes a 6-dimensional array with disease state, and returns a 6
	 * dimensional array in which the diseasestates are summed to diseases Is
	 * internal for the DYNAMO-output
	 * 
	 * @param stateArray
	 *            : 6-dimensional array where the third index indicates the
	 *            disease state = combination of diseases
	 * @return 6-dimensional array where the third index indicates the disease
	 */
	private double[][][][][][] makeDiseaseArray(double[][][][][][] stateArray) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim1 = stateArray.length;
		int dim2 = stateArray[0].length;

		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		int dim6 = stateArray[0][0][0][0][0].length;

		double diseaseArray[][][][][][] = new double[dim1][dim2][this.nDiseases][dim4][dim5][dim6];

		if (this.structure != null) {
			for (int c = 0; c < this.structure.length; c++) {

				if (!this.structure[c].isWithCuredFraction()) {
					for (int d = 0; d < this.structure[c].getNInCluster(); d++) {

						for (int state = 1; state < Math.pow(2,
								this.structure[c].getNInCluster()); state++) {

							if ((state & (1 << d)) == (1 << d)) {
								/*
								 * pDisease[thisScen][stepCount][currentDisease
								 * + d][sexIndex] += compoundData[currentState +
								 * s - 1] survival weight[riskFactor][ageIndex][
								 * sexIndex]; if (details)
								 */
								for (int scen = 0; scen < dim1; scen++)
									for (int a = 0; a < dim5; a++)
										for (int g = 0; g < dim6; g++)
											for (int r = 0; r < dim4; r++)
												for (int stepCount = 0; stepCount < dim2; stepCount++)
													diseaseArray[scen][stepCount][currentDisease][r][a][g] += stateArray[scen][stepCount][currentClusterStart
															+ state - 1][r][a][g];
							}
						}
						currentDisease++;
					}
					currentClusterStart += Math.pow(2, this.structure[c]
							.getNInCluster()) - 1;
				} else {
					/* with cured fraction */
					for (int scen = 0; scen < dim1; scen++)
						for (int a = 0; a < dim5; a++)
							for (int g = 0; g < dim6; g++)
								for (int r = 0; r < dim4; r++)
									for (int stepCount = 0; stepCount < dim2; stepCount++) {
										diseaseArray[scen][stepCount][currentDisease][r][a][g] += stateArray[scen][stepCount][currentClusterStart][r][a][g];

										diseaseArray[scen][stepCount][currentDisease + 1][r][a][g] += stateArray[scen][stepCount][currentClusterStart + 1][r][a][g];

									}
					currentDisease += 2;
					currentClusterStart += 2;
				}

			}
		} else {
			diseaseArray = null;
		}

		return diseaseArray;
	}

	/**
	 * Makes an array with diseaseNumbers from the array with diseasestates
	 * 
	 * @param stateArray
	 * @param scen
	 * @param stepCount
	 * @param g
	 * @return array [][][] of diseases, riskclass, age for the case
	 *         scenario=scen, year=stepcount and gender=g
	 */
	private double[][][] makeDiseaseArray(double[][][][][][] stateArray,
			int scen, int stepCount, int g) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		double diseaseArray[][][] = new double[this.nDiseases][dim4][dim5];

		for (int c = 0; c < this.structure.length; c++) {
			if (!this.structure[c].isWithCuredFraction()) {
				for (int d = 0; d < this.structure[c].getNInCluster(); d++) {

					for (int state = 1; state < Math.pow(2, this.structure[c]
							.getNInCluster()); state++) {

						if ((state & (1 << d)) == (1 << d)) {
							/*
							 * pDisease[thisScen][stepCount][currentDisease +
							 * d][sexIndex] += compoundData[currentState + s -
							 * 1] survival weight[riskFactor][ageIndex][
							 * sexIndex]; if (details)
							 */
							for (int r = 0; r < dim4; r++)
								for (int a = 0; a < dim5; a++)
									diseaseArray[currentDisease][r][a] += stateArray[scen][stepCount][currentClusterStart
											+ state - 1][r][a][g];
						}
					}
					currentDisease++;
				}
				currentClusterStart += Math.pow(2, this.structure[c]
						.getNInCluster()) - 1;
			} else {
				for (int r = 0; r < dim4; r++)
					for (int a = 0; a < dim5; a++) {
						diseaseArray[currentDisease][r][a] += stateArray[scen][stepCount][currentClusterStart][r][a][g];
						diseaseArray[currentDisease + 1][r][a] += stateArray[scen][stepCount][currentClusterStart + 1][r][a][g];
						currentDisease += 2;
					}
				currentClusterStart += 2;

			}
		}
		return diseaseArray;
	}

	/**
	 * combines from the stateArray the contributions for disease with
	 * diseasenumber disease , and selects only those data from the simulated
	 * year given by stepCount
	 * 
	 * @param stateArray
	 * @param disease
	 *            : disease number
	 * @param stepCount
	 * @return array [][][][] of scenario, riskclass, age, sex for the case
	 *         year=stepcount and diseasenumber=disease
	 */

	private double[][][][] makeDiseaseArray(double[][][][][][] stateArray,
			int stepCount, int disease) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim6 = stateArray[0][0][0][0][0].length;
		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		double diseaseArray[][][][] = new double[stateArray.length][dim4][dim5][dim6];
		boolean diseaseFound = false;
		/*
		 * no check on whether structure exists, as there will not be a state
		 * array without diseases
		 */
		for (int c = 0; c < this.structure.length; c++) {
			if (!this.structure[c].isWithCuredFraction()) {
				for (int d = 0; d < this.structure[c].getNInCluster(); d++) {
					if (this.structure[c].getDiseaseNumber()[d] == disease) {
						diseaseFound = true;

						for (int state = 1; state < Math.pow(2,
								this.structure[c].getNInCluster()); state++) {

							if ((state & (1 << d)) == (1 << d)) {
								/*
								 * pDisease[thisScen][stepCount][currentDisease
								 * + d][sexIndex] += compoundData[currentState +
								 * s - 1] survival weight[riskFactor][ageIndex][
								 * sexIndex]; if (details)
								 */
								for (int scen = 0; scen < stateArray.length; scen++)
									for (int r = 0; r < dim4; r++)
										for (int a = 0; a < dim5; a++)
											for (int g = 0; g < dim6; g++)
												diseaseArray[scen][r][a][g] += stateArray[scen][stepCount][currentClusterStart
														+ state - 1][r][a][g];
							}
						}
						break;
					}
					currentDisease++;
				}
				currentClusterStart += Math.pow(2, this.structure[c]
						.getNInCluster()) - 1;
				if (diseaseFound)
					break;
			} else {

				if (this.structure[c].getDiseaseNumber()[0] == disease) {
					diseaseFound = true;

					for (int scen = 0; scen < stateArray.length; scen++)
						for (int r = 0; r < dim4; r++)
							for (int a = 0; a < dim5; a++)
								for (int g = 0; g < dim6; g++)
									diseaseArray[scen][r][a][g] = stateArray[scen][stepCount][currentClusterStart][r][a][g];
				}
				if (this.structure[c].getDiseaseNumber()[1] == disease) {
					diseaseFound = true;

					for (int scen = 0; scen < stateArray.length; scen++)
						for (int r = 0; r < dim4; r++)
							for (int a = 0; a < dim5; a++)
								for (int g = 0; g < dim6; g++)
									diseaseArray[scen][r][a][g] = stateArray[scen][stepCount][currentClusterStart + 1][r][a][g];
				}

				currentDisease += 2;

				currentClusterStart += 2;
				if (diseaseFound)
					break;
			} // end if with cured fraction
		}// end loop over clusters
		return diseaseArray;
	}

	/**
	 * combines from the stateArray the contributions for disease with
	 * diseasenjumber disease ,
	 * 
	 * @param stateArray
	 * @param disease
	 *            : disease number
	 * 
	 * @return array [][][][][] of scenario,year riskclass, age, sex for the
	 *         case diseasenumber=disease
	 */

	private double[][][][][] makeDiseaseArray(double[][][][][][] stateArray,
			int disease) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim6 = stateArray[0][0][0][0][0].length;
		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		int dim2 = stateArray[0].length;
		double diseaseArray[][][][][] = new double[stateArray.length][dim2][dim4][dim5][dim6];
		boolean diseaseFound = false;

		/*
		 * no check on whether structure exists is necessary as there will not
		 * be a statearray without
		 */

		for (int c = 0; c < this.structure.length; c++) {
			if (!this.structure[c].isWithCuredFraction()) {
				for (int d = 0; d < this.structure[c].getNInCluster(); d++) {
					if (this.structure[c].getDiseaseNumber()[d] == disease) {
						diseaseFound = true;

						for (int state = 1; state < Math.pow(2,
								this.structure[c].getNInCluster()); state++) {

							if ((state & (1 << d)) == (1 << d)) {
								/*
								 * if d =1 in the state, add this state to the
								 * disease
								 */
								for (int scen = 0; scen < stateArray.length; scen++)
									for (int r = 0; r < dim4; r++)
										for (int stepCount = 0; stepCount < dim2; stepCount++)
											for (int a = 0; a < dim5; a++)
												for (int g = 0; g < dim6; g++)
													diseaseArray[scen][stepCount][r][a][g] += stateArray[scen][stepCount][currentClusterStart
															+ state - 1][r][a][g];
							}
						}
						break;
					}
					currentDisease++;
				}
				currentClusterStart += Math.pow(2, this.structure[c]
						.getNInCluster()) - 1;
				if (diseaseFound)
					break;
			} else {

				if (this.structure[c].getDiseaseNumber()[0] == disease) {
					diseaseFound = true;

					for (int scen = 0; scen < stateArray.length; scen++)
						for (int r = 0; r < dim4; r++)
							for (int stepCount = 0; stepCount < dim2; stepCount++)
								for (int a = 0; a < dim5; a++)
									for (int g = 0; g < dim6; g++)
										diseaseArray[scen][stepCount][r][a][g] = stateArray[scen][stepCount][currentClusterStart][r][a][g];
				}
				if (this.structure[c].getDiseaseNumber()[1] == disease) {
					diseaseFound = true;

					for (int scen = 0; scen < stateArray.length; scen++)
						for (int r = 0; r < dim4; r++)
							for (int stepCount = 0; stepCount < dim2; stepCount++)
								for (int a = 0; a < dim5; a++)
									for (int g = 0; g < dim6; g++)
										diseaseArray[scen][stepCount][r][a][g] = stateArray[scen][stepCount][currentClusterStart + 1][r][a][g];
				}

				currentDisease += 2;

				currentClusterStart += 2;
				if (diseaseFound)
					break;
			} // end if with cured fraction
		}// end loop over clusters

		return diseaseArray;
	}

	/**
	 * The array returns a 5 dimensional array (scenario, time, age, sex) of
	 * healthy persons in the population
	 * 
	 * 
	 * 
	 * @return 4-dimensional array of healthy persons in the population by
	 *         scenario, time, age and sex
	 */
	private double[][][][] getNumberOfDiseasedPersons() {
		double diseasedPersons[][][][] = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int r = 0; r < this.nRiskFactorClasses; r++) {
							diseasedPersons[scen][stepCount][a][g] += pTotalDiseaseByRiskClassByAge[scen][stepCount][r][a][g]
									*

									this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
						}
		return diseasedPersons;
		/*
		 * 
		 * verouderd, dit kan alleen zo worden berekend bij homogene groepen,
		 * dat is bij zelfde risicofactor klasse niet altijd het geval Daarom
		 * moet dit op persoonsbasis worden berekend bij inlezen en daarna
		 * bewaard.
		 */
		/*
		 * double healthyPersonsByRiskClass[][][][][] = new double[this.nScen +
		 * 1][this.stepsInRun + 1][this.nRiskFactorClasses][96 +
		 * this.stepsInRun][2]; double diseasedPersons[][][][] = new
		 * double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];
		 * 
		 * for (int scen = 0; scen < this.nScen + 1; scen++) for (int stepCount
		 * = 0; stepCount < this.stepsInRun + 1; stepCount++) for (int a = 0; a
		 * < 96 + this.stepsInRun; a++) for (int g = 0; g < 2; g++) {
		 * 
		 * for (int r = 0; r < this.nRiskFactorClasses; r++) {
		 * healthyPersonsByRiskClass[scen][stepCount][r][a][g] =
		 * this.nPopByRiskClassByAge[scen][stepCount][r][a][g]; int
		 * currentClusterStart = 0; / if there are no diseases in the model,
		 * structure is null
		 *//*
			 * if (this.structure != null) { for (int c = 0; c <
			 * this.structure.length; c++) { double nWithDisease = 0;
			 * 
			 * if (!this.structure[c] .isWithCuredFraction()) { for (int state =
			 * 0; state < (Math.pow( 2, this.structure[c] .getNInCluster()) -
			 * 1); state++) {
			 * 
			 * nWithDisease +=
			 * this.nDiseaseStateByRiskClassByAge[scen][stepCount
			 * ][currentClusterStart + state][r][a][g]; } currentClusterStart +=
			 * Math.pow(2, this.structure[c] .getNInCluster()) - 1; } else { /
			 * with cured fraction
			 *//*
				 * nWithDisease =
				 * this.nDiseaseStateByRiskClassByAge[scen][stepCount
				 * ][currentClusterStart][r][a][g] +
				 * this.nDiseaseStateByRiskClassByAge
				 * [scen][stepCount][currentClusterStart + 1][r][a][g];
				 * 
				 * currentClusterStart += 2; } if
				 * (this.nPopByRiskClassByAge[scen][stepCount][r][a][g] > 0)
				 * healthyPersonsByRiskClass[scen][stepCount][r][a][g] *=
				 * (this.nPopByRiskClassByAge[scen][stepCount][r][a][g] -
				 * nWithDisease) /
				 * this.nPopByRiskClassByAge[scen][stepCount][r][a][g]; else
				 * healthyPersonsByRiskClass[scen][stepCount][r][a][g] = 0; }
				 * diseasedPersons[scen][stepCount][a][g] +=
				 * this.nPopByRiskClassByAge[scen][stepCount][r][a][g] -
				 * healthyPersonsByRiskClass[scen][stepCount][r][a][g]; } else {
				 * / if no diseases in the model
				 */
		/*
		 * diseasedPersons[scen][stepCount][a][g] = 0; } } }
		 */

	}

	/**
	 * returns the array if future numbers of total disease numbers for the
	 * group of persons with age age at the start of simulation
	 * 
	 * @param age
	 *            age
	 * @return array (indexes; scenario, timesteps (= up to very old age as
	 *         given by nDim) sex
	 */
	private double[][][] getNumberOfOriDiseasedPersons(int age) {

		double diseasedPersons[][][] = new double[this.nScen + 1][this.nDim][2];

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int steps = 0; steps < this.stepsInRun + 1; steps++)
				for (int g = 0; g < 2; g++)
					for (int r = 0; r < this.nRiskFactorClasses; r++) {
						diseasedPersons[scen][steps][g] += pTotalDiseaseByOriRiskClassByOriAge[scen][steps][r][age][g]
								* this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g];
					}
		return diseasedPersons;

		/* zie getNumberOf DiseasesPersons voor toelichting op weglaten vervolg */
		/*
		 * double healthyPersons[][][][] = new double[this.nScen +
		 * 1][this.nDim][this.nRiskFactorClasses][2];
		 * 
		 * for (int scen = 0; scen < this.nScen + 1; scen++) for (int steps = 0;
		 * steps < this.nDim; steps++)
		 * 
		 * for (int g = 0; g < 2; g++) {
		 * 
		 * for (int r = 0; r < this.nRiskFactorClasses; r++) {
		 * 
		 * healthyPersons[scen][steps][r][g] +=
		 * this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g]; int
		 * currentClusterStart = 0; if (this.structure != null) { for (int c =
		 * 0; c < this.structure.length; c++) { double nWithDisease = 0; if
		 * (!this.structure[c].isWithCuredFraction()) { for (int state = 0;
		 * state < (Math.pow(2, this.structure[c].getNInCluster()) - 1);
		 * state++) {
		 * 
		 * / pDisease[thisScen][stepCount][currentDisease + d][sexIndex] +=
		 * compoundData[currentState + s - 1] survival
		 * weight[riskFactor][ageIndex][ sexIndex]; if (details)
		 */

		/*
		 * nWithDisease +=
		 * this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][
		 * currentClusterStart + state][r][age][g]; } currentClusterStart +=
		 * Math.pow(2, this.structure[c].getNInCluster()) - 1; } else { / with
		 * cured fraction
		 *//*
			 * nWithDisease =
			 * this.nDiseaseStateByOriRiskClassByOriAge[scen][steps
			 * ][currentClusterStart][r][age][g] +
			 * this.nDiseaseStateByOriRiskClassByOriAge
			 * [scen][steps][currentClusterStart + 1][r][age][g];
			 * 
			 * currentClusterStart += 2; } if
			 * (this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g] > 0)
			 * healthyPersons[scen][steps][r][g] *=
			 * (this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g] -
			 * nWithDisease) /
			 * this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g]; else
			 * healthyPersons[scen][steps][r][g] = 0; if (steps == 70) { log
			 * .debug("population " +
			 * this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g]); log
			 * .debug("c " + c + " healthy " +
			 * healthyPersons[scen][steps][r][g]); // volgen log
			 * .debug("nwithDisease " +
			 * this.nDiseaseStateByOriRiskClassByOriAge[
			 * scen][steps][currentClusterStart - 1][r][age][g]);
			 * 
			 * log.debug("n with Disease new " + nWithDisease); log
			 * .debug("prob disease " +
			 * (this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g] -
			 * nWithDisease) /
			 * this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g]); }
			 * 
			 * } diseasedPersons[scen][steps][g] +=
			 * this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g] -
			 * healthyPersons[scen][steps][r][g];
			 * 
			 * } else { / no diseases in the model
			 */

		/*
		 * diseasedPersons[scen][steps][g] = 0;
		 * 
		 * } } // end loop over r } return diseasedPersons;
		 */
	}

	/**
	 * returns the array if future numbers of total disabled numbers for the
	 * group of persons with age age at the start of simulation
	 * 
	 * @param age
	 *            age
	 * @return array (indexes; scenario, timesteps (= up to very old age as
	 *         given by nDim) sex
	 */
	@Override
	public double[][][] getNDisabledByOriAge(int age) {

		double[][][] nDisabled = new double[this.nScen + 1][this.nDim][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)

				for (int g = 0; g < 2; g++)
					for (int stepCount = 0; stepCount < this.nDim; stepCount++)

						nDisabled[scen][stepCount][g] += nPopByOriRiskClassByOriAge[scen][stepCount][r][age][g]
								* pDisabilityByOriRiskClassByOriAge[scen][stepCount][r][age][g];

		return nDisabled;

	}

	/**
	 * returns the array if future numbers of total disease numbers for the
	 * group of persons with age age at the start of simulation
	 * 
	 * @param age
	 *            age
	 * @return array (indexes; scenario, timesteps (= up to very old age as
	 *         given by nDim) sex
	 */
	@Override
	public double[][][] getNTotDiseaseByOriAge(int age) {

		double[][][] nDisease = new double[this.nScen + 1][this.nDim][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)

				for (int g = 0; g < 2; g++)
					for (int stepCount = 0; stepCount < this.nDim; stepCount++)

						nDisease[scen][stepCount][g] += nPopByOriRiskClassByOriAge[scen][stepCount][r][age][g]
								* pTotalDiseaseByOriRiskClassByOriAge[scen][stepCount][r][age][g];

		return nDisease;

	}

	/**
	 * @param pop
	 */
	/**
	 * @param pop
	 * @return array [4] which contains the minimum+ maximum age, and minimum
	 *         and maximum gender in the population
	 */
	private int[] extractNumberInSimulationFromPopulation(Population[] pop) {

		int sexIndex;
		int ageIndex;

		int[] results = new int[4];
		int minage = 100;
		int maxage = 0;
		int mingender = 1;
		int maxgender = 0;
		/* look at general numbers in the population in the reference population */

		Iterator<Individual> individualIterator1 = pop[0].iterator();

		while (individualIterator1.hasNext()) {
			Individual individual = individualIterator1.next();

			ageIndex = (int) Math
					.round(((Float) individual.get(1).getValue(0)));
			sexIndex = (int) (Integer) individual.get(2).getValue(0);
			if (sexIndex == 1)
				maxgender = 1;
			if (sexIndex == 0)
				mingender = 0;
			if (ageIndex > maxage)
				maxage = ageIndex;
			if (ageIndex < minage)
				minage = ageIndex;
			/* look for maximum and minimum age in simulation */
			if (ageIndex > this.maxAgeInSimulation)
				this.maxAgeInSimulation = ageIndex;
			if (ageIndex < this.minAgeInSimulation)
				this.minAgeInSimulation = ageIndex;
			if (ageIndex < this.minAgeInSimulationAtStart && ageIndex >= 0)
				this.minAgeInSimulationAtStart = ageIndex;

			if (ageIndex < 0)
				this.nNewBornsInSimulationByAge[ageIndex + this.stepsInRun][sexIndex]++;
			else {
				this.nInSimulation[sexIndex]++;
				this.nInSimulationByAge[ageIndex][sexIndex]++;

				float riskValue;
				int durationValue;
				if (this.riskType != 2) {
					int riskFactor = (int) (Integer) individual.get(3)
							.getValue(0);
					this.nInSimulationByRiskClassByAge[riskFactor][ageIndex][sexIndex]++;
					if (this.riskType == 3) {
						durationValue = Math.round((float) (Float) individual
								.get(4).getValue(0));
						this.nInSimulationByRiskClassAndDurationByAge[riskFactor][durationValue][ageIndex][sexIndex]++;
					}
				} else {
					riskValue = (float) (Float) individual.get(3).getValue(0);
					int riskFactor;
					int i = 0;
					if (this.riskClassnames.length > 1) {
						if (riskValue <= this.cutoffs[0])
							riskFactor = 0;

						else {
							for (i = 1; i < this.cutoffs.length; i++) {
								if (riskValue <= this.cutoffs[i]
										&& riskValue > this.cutoffs[i - 1])
									break;
							}

							riskFactor = i;
							/* just to be sure that it goes OK: */
							if (riskValue > this.cutoffs[this.cutoffs.length - 1])
								riskFactor = this.cutoffs.length;
						}
					} else { /* only one single value present for riskValue */
						riskFactor = 0;
					}
					this.nInSimulationByRiskClassByAge[riskFactor][ageIndex][sexIndex]++;

				}
			}

		}
		results[0] = minage;
		results[1] = maxage;
		results[2] = mingender;
		results[3] = maxgender;
		return results;

		/*
		 * now count in the scenarios with categorical (compound) riskfactors
		 * which simultaneously differ in both scenario prevalence and in
		 * scenario transitions the number of particular from-to combinations;
		 *//*
			 * not yet used so commented out (gave errors) String delims =
			 * "[_]"; if (pop.length > 1 && this.riskType != 2) for (int scen =
			 * 0; scen < pop.length - 1; scen++) if (scenInitial[scen] &&
			 * scenTrans[scen]) { Iterator<Individual> individualIterator2 =
			 * pop[scen + 1] .iterator();
			 * 
			 * while (individualIterator2.hasNext()) { Individual individual =
			 * individualIterator2.next();
			 * 
			 * ageIndex = (int) Math.round(((Float) individual.get(1)
			 * .getValue(0))); sexIndex = (int) (Integer) individual.get(2)
			 * .getValue(0); int to = (int) (Integer)
			 * individual.get(3).getValue(0);
			 * 
			 * String indLabel = individual.getLabel(); String[] tokens =
			 * indLabel.split(delims); int from = Integer.parseInt(tokens[2]);
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * this.nInSimulationByFromByTo[scen][from][to][ageIndex][sexIndex]++
			 * ;
			 * 
			 * } }
			 */

	}

	/**
	 * @param pop
	 */
	private void setCutoffs(Population[] pop) {
		float maxRisk = -1000000000;
		float minRisk = 1000000000;

		/*
		 * for continuous risk factor without defined cutoffs, define cutoffs
		 * based on 10% percentiles
		 */
		/* for this, first find minimum and maximum values */
		{
			for (int ipop = 0; ipop < this.nPopulations; ipop++) {
				Iterator<Individual> individualIterator2 = pop[ipop].iterator();

				while (individualIterator2.hasNext()) {
					Individual individual = individualIterator2.next();

					float riskValue;

					riskValue = (float) (Float) individual.get(3).getValue(0);
					if (riskValue > maxRisk)
						maxRisk = riskValue;
					if (riskValue < minRisk)
						minRisk = riskValue;
				}
			}

			if (maxRisk > minRisk) {
				this.nRiskFactorClasses = 5;
				this.cutoffs = new float[4];
				this.riskClassnames = new String[5];
				for (int i = 1; i < 5; i++) {
					this.cutoffs[i - 1] = minRisk + i * (maxRisk - minRisk)
							* 0.2F;
					/*
					 * names can be made only if both cutoffs are already
					 * calculated
					 */
					if (i != 1)
						this.riskClassnames[i - 1] = this.cutoffs[i - 2] + "-"
								+ this.cutoffs[i - 1];

				}
				this.riskClassnames[0] = "<" + this.cutoffs[0];
				this.riskClassnames[4] = ">" + this.cutoffs[3];
			} else if (maxRisk == minRisk) {
				this.riskClassnames = new String[1];
				this.nRiskFactorClasses = 1;
				this.riskClassnames[0] = ((Float) maxRisk).toString();
			}

		}

	}

	/**
	 * This method takes the detailed arrays (...byRiskClassByAge) and makes
	 * arrays of number of persons in the simulated population by applying the
	 * population numbers to them
	 * 
	 */
	public void makeArraysWithPopulationNumbers() {

		/*
		 * 
		 * Make absolute numbers, using the population size at the start of the
		 * simulation
		 */
		for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++) {

			float originalNumber = 0;
			float ratio = 0;

			for (int s = 0; s < 2; s++) {
				for (int a = 0; a < Math.min(this.nDim, (96 + stepCount)); a++) {
					/*
					 * get original number of persons in this birthcohort at
					 * time=zero
					 */
					if (a - stepCount >= 0) {
						/* for those in the initial cohort */
						originalNumber = this.populationSize[a - stepCount][s];
						if (this.nInSimulationByAge[a - stepCount][s] != 0)
							ratio = originalNumber
									/ this.nInSimulationByAge[a - stepCount][s];
						else
							ratio = 0;
					} else if (this.withNewborns) {
						/* for newborns (born during simulation ) */
						if (s == 0) /* males */
							originalNumber = this.newborns[stepCount - 1 - a]
									* this.mfratio / (1 + this.mfratio);
						else
							/* females */
							originalNumber = this.newborns[stepCount - 1 - a]
									* (1 - this.mfratio / (1 + this.mfratio));
						if (this.nNewBornsInSimulationByAge[stepCount - 1][s] != 0)
							ratio = originalNumber
									/ this.nNewBornsInSimulationByAge[stepCount - 1][s];
						else
							ratio = 0;
						// if
						// (s==0)log.fatal("step: "+stepCount+" age "+a+" basic pop taken "+
						// this.newborns[stepCount - 1-a]);
					} else
						ratio = 0;

					// TODO hierboven gok nog nagaan en zorgen dat alles goed
					// geinitialiseerd is

					/*
					 * nb: scen is the scenario number starting with scen =0 is
					 * the reference scenario. in arrays with scenario info the
					 * first (0) element refers to the first alternative
					 * scenario
					 */

					for (int scen = 0; scen <= this.nScen; scen++) {

						for (int r = 0; r < this.nRiskFactorClasses; r++) {

							this.nPopByRiskClassByAge[scen][stepCount][r][a][s] = ratio
									* this.pSurvivalByRiskClassByAge[scen][stepCount][r][a][s];
							if (this.pSurvivalByRiskClassByAge[scen][stepCount][r][a][s] != 0)
								this.pDisabilityByRiskClassByAge[scen][stepCount][r][a][s] = this.pDisabilityByRiskClassByAge[scen][stepCount][r][a][s]
										/ this.pSurvivalByRiskClassByAge[scen][stepCount][r][a][s];
							if (this.pSurvivalByRiskClassByAge[scen][stepCount][r][a][s] != 0)
								this.pTotalDiseaseByRiskClassByAge[scen][stepCount][r][a][s] = this.pTotalDiseaseByRiskClassByAge[scen][stepCount][r][a][s]
										/ this.pSurvivalByRiskClassByAge[scen][stepCount][r][a][s];
							for (int state = 0; state < this.nDiseaseStates; state++) {

								this.nDiseaseStateByRiskClassByAge[scen][stepCount][state][r][a][s] = ratio
										* this.pDiseaseStateByRiskClassByAge[scen][stepCount][state][r][a][s];

							}
						}
					}
				}// end loop age
			} // end loop sex
		} // end loop stepcount
		/*
		 * repeat for the cohort-based arrays based on original age and original
		 * riskclass
		 */
		/* a = original age */
		for (int a = 0; a < 96; a++) {
			for (int stepCount = 0; stepCount < this.nDim - a; stepCount++) {
				float ratio = 0;
				for (int s = 0; s < 2; s++) {

					/*
					 * get original number of persons in this birthcohort at
					 * time=zero
					 */
					if (this.nInSimulationByAge[a][s] != 0)
						ratio = this.populationSize[a][s]
								/ this.nInSimulationByAge[a][s];
					/* newborns are not included in the cohort based arrays */

					for (int scen = 0; scen <= this.nScen; scen++) {
						for (int r = 0; r < this.nRiskFactorClasses; r++) {
							this.nPopByOriRiskClassByOriAge[scen][stepCount][r][a][s] = ratio
									* this.pSurvivalByOriRiskClassByOriAge[scen][stepCount][r][a][s];
							if (pSurvivalByOriRiskClassByOriAge[scen][stepCount][r][a][s] != 0)
								pDisabilityByOriRiskClassByOriAge[scen][stepCount][r][a][s] = pDisabilityByOriRiskClassByOriAge[scen][stepCount][r][a][s]
										/ pSurvivalByOriRiskClassByOriAge[scen][stepCount][r][a][s];
							if (pSurvivalByOriRiskClassByOriAge[scen][stepCount][r][a][s] != 0)
								pTotalDiseaseByOriRiskClassByOriAge[scen][stepCount][r][a][s] = pTotalDiseaseByOriRiskClassByOriAge[scen][stepCount][r][a][s]
										/ pSurvivalByOriRiskClassByOriAge[scen][stepCount][r][a][s];
							for (int state = 0; state < this.nDiseaseStates; state++) {
								this.nDiseaseStateByOriRiskClassByOriAge[scen][stepCount][state][r][a][s] = ratio
										* this.pDiseaseStateByOriRiskClassByAge[scen][stepCount][state][r][a][s];
								/*
								 * if (s == 0 && a == 50) log .("scenario "
								 * + scen + "step " + stepCount + " state " +
								 * state + " r " + r + "pdis " +
								 * this.pDiseaseStateByOriRiskClassByAge
								 * [scen][stepCount][state][r][a][s] + "ndis " +
								 * this
								 * .nDiseaseStateByOriRiskClassByOriAge[scen]
								 * [stepCount][state][r][a][s]);
								 */}
						}
					}
				}// end loop sex

			} // end loop stepcount
		} // end loop age

		/*
		 * uptill now all arrays contain numbers of simulated population in the
		 * category. The next part changes those into percentages (or means) by
		 * dividing by the right denominator (indicated by the name, and also by
		 * sex and timeStep (stepCount) and scenario
		 * 
		 * denominator: -- for survival the numbers in the initial population
		 * (stepcount=0) in the particular group -- for disease(state) : the
		 * fraction surviving (pop(stepcount))
		 */

	}

	/**
	 * 
	 */
	public void writeDataToDisc(String dataName) {
		final String dataFile = dataName;

		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(dataFile)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int scen = 0; scen < this.nScen + 1; scen++)

			for (int r = 0; r < this.nRiskFactorClasses; r++)
				for (int age = 0; age < 96; age++)
					for (int s = 0; s < 2; s++) {
						int yearsleft = this.nDim - age;

						for (int steps = 0; steps < yearsleft; steps++) {
							try {
								out
										.writeDouble(this.nPopByOriRiskClassByOriAge[scen][steps][r][age][s]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int d = 0; d < this.nDiseaseStates; d++)
								try {
									out
											.writeDouble(this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][d][r][age][s]);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							;
						}
					}

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int steps = 0; steps < this.stepsInRun + 1; steps++)
				for (int r = 0; r < this.nRiskFactorClasses; r++)
					for (int age = 0; age < 96 + this.stepsInRun; age++)
						for (int s = 0; s < 2; s++) {
							try {
								out
										.writeDouble(this.nPopByRiskClassByAge[scen][steps][r][age][s]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int d = 0; d < this.nDiseaseStates; d++)
								try {
									out
											.writeDouble(this.nDiseaseStateByRiskClassByAge[scen][steps][d][r][age][s]);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}

		// inlezen: price = in.readDouble();catch (EOFException e) {}
		//

		// TODO exception afhandelen + toevoegen wegschrijven van mean van
		// riskfactor

	}

	public void readDataFromDisc(String dataName) {
		final String dataFile = dataName;

		DataInputStream indata = null;
		try {
			indata = new DataInputStream(new BufferedInputStream(
					new FileInputStream(dataFile)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int scen = 0; scen < this.nScen + 1; scen++)

			for (int r = 0; r < this.nRiskFactorClasses; r++)
				for (int age = 0; age < 96; age++)
					for (int s = 0; s < 2; s++) {
						int yearsleft = this.nDim - age;

						for (int steps = 0; steps < yearsleft; steps++) {
							try {
								this.nPopByOriRiskClassByOriAge[scen][steps][r][age][s] = indata
										.readDouble();
							} catch (EOFException e) {
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int d = 0; d < this.nDiseaseStates; d++)
								try {
									this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][d][r][age][s] = indata
											.readDouble();
								} catch (EOFException e) {
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							;
						}
					}

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int steps = 0; steps < this.stepsInRun + 1; steps++)
				for (int r = 0; r < this.nRiskFactorClasses; r++)
					for (int age = 0; age < 96 + this.stepsInRun; age++)
						for (int s = 0; s < 2; s++) {
							try {
								this.nPopByRiskClassByAge[scen][steps][r][age][s] = indata
										.readDouble();
							} catch (EOFException e) {
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int d = 0; d < this.nDiseaseStates; d++)
								try {
									this.nDiseaseStateByRiskClassByAge[scen][steps][d][r][age][s] = indata
											.readDouble();
								} catch (EOFException e) {
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}

		//

		// TODO exception afhandelen + toevoegen wegschrijven van mean van
		// riskfactor

	}

	/**
	 * @return array with objects (one for each disease cluster) containing the
	 *         information in each cluster
	 */
	public DiseaseClusterStructure[] getStructure() {
		return this.structure;
	}

	/**
	 * @param structure
	 */
	public void setStructure(DiseaseClusterStructure[] structure) {
		this.structure = structure;
	}

	/**
	 * @param structure2
	 */
	private void setNDiseases(DiseaseClusterStructure[] s) {
		this.nDiseases = 0;
		// if no diseases are present, DiseaseClusterStructure=null
		if (s != null)
			for (int i = 0; i < s.length; i++) {
				this.nDiseases += s[i].getNInCluster();

			}

	}

	/**
	 * /**
	 * 
	 * @param s
	 */
	private void setNDiseaseStates(DiseaseClusterStructure[] s) {
		this.nDiseaseStates = 1;
		// if no diseases are present, s==null
		if (s != null)
			for (int i = 0; i < s.length; i++) {
				if (s[i].getNInCluster() == 1)
					this.nDiseaseStates++;
				else if (s[i].isWithCuredFraction())
					this.nDiseaseStates += 2;
				else
					this.nDiseaseStates += Math.pow(2, s[i].getNInCluster()) - 1;

			}
	}

	/**
	 * @return number of newborns in the simulation by age (=year when added)
	 *         and gender
	 */
	public int[][] getNNewBornsInSimulationByAge() {
		return this.nNewBornsInSimulationByAge;
	}

	/**
	 * @return
	 */
	public int[][][] getNInSimulationByRiskClassByAge() {
		return nInSimulationByRiskClassByAge;
	}

	/**
	 * @return number of persons with the disease by riskclass
	 */
	/* this method is never used?? */
	public double[][][][][] getNDiseaseByRiskClass() {
		double[][][][][] nDiseaseByRiskClass = new double[nScen + 1][stepsInRun + 1][nDiseases][this.nRiskFactorClasses][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)

								nDiseaseByRiskClass[scen][stepCount][d][r][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByRiskClass;
	}

	/**
	 * @return number of persons with the disease
	 */
	/* never used */
	public double[][][][] makeNDisease() {
		double[][][][] nDisease = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseases][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < this.nDiseases; d++)
							for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)
								nDisease[scen][stepCount][d][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDisease;
	}

	/**
	 * @return number of persons with the disease by age index : scenario,
	 *         stepInRun, diseaseNumber, age, sex
	 */
	/* never used */
	public double[][][][][] getNDiseaseByAge() {
		double[][][][][] nDiseaseByAge = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseases][96 + this.stepsInRun][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < this.nDiseases; d++)
							for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
								nDiseaseByAge[scen][stepCount][d][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByAge;
	}

	/**
	 * gets number of persons with disease
	 * 
	 * @param disease
	 *            : diseasenumber (-1= all diseases together)
	 * @return array [][][][] with number of persons with this disease (index:
	 *         scenario, year of follow-up (simulation), age, sex)
	 */
	@Override
	public double[][][][] getNDiseaseByAge(int disease) {
		double[][][][] nDiseaseByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];
		;
		if (disease >= 0) {
			double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);
			for (int r = 0; r < this.nRiskFactorClasses; r++)

				for (int scen = 0; scen < this.nScen + 1; scen++)
					for (int a = 0; a < 96 + this.stepsInRun; a++)
						for (int g = 0; g < 2; g++)
							for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
								nDiseaseByAge[scen][stepCount][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][disease][r][a][g];
		} else
			nDiseaseByAge = getNumberOfDiseasedPersons();

		return nDiseaseByAge;
	}

	/**
	 * gets number of persons with disease
	 * 
	 * @param disease
	 *            : diseasenumber
	 * @return array [][][] with number of persons with this disease (index:
	 *         scenario, age,sex)
	 */
	@Override
	public double[][][] getNDiseaseByAge(int disease, int stepCount) {
		double[][][] nDiseaseByAge = new double[this.nScen + 1][96 + this.stepsInRun][2];
		;

		double[][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(
				this.nDiseaseStateByRiskClassByAge, stepCount, disease);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nDiseaseByAge[scen][a][g] += nDiseaseByRiskClassByAge[scen][r][a][g];
		return nDiseaseByAge;
	}

	/**
	 * 
	 * @return array [][][][] with numbers of disabled persons by scenario,
	 *         years in follow-uo , age and sex
	 */
	@Override
	public double[][][][] getNDisabledByAge() {

		double[][][][] nDisabledByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)

							nDisabledByAge[scen][stepCount][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
									* pDisabilityByRiskClassByAge[scen][stepCount][r][a][g];

		return nDisabledByAge;
	}

	/**
	 * @ param stepCount Year for which to get the
	 * 
	 * @return array [][][] with numbers of disabled persons by scenario, , age
	 *         and sex
	 */
	@Override
	public double[][][] getNDisabledByAge(int stepCount) {

		double[][][] nDisabledByAge = new double[this.nScen + 1][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nDisabledByAge[scen][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
								* pDisabilityByRiskClassByAge[scen][stepCount][r][a][g];

		return nDisabledByAge;
	}

	/**
	 * 
	 * @return array [][][][][] with numbers of disabled persons by scenario,
	 *         years in follow-uo ,riskclass, age and sex
	 */
	@Override
	public double[][][][][] getNDisabledByRiskClassByAge() {

		double[][][][][] nDisabledByAge = new double[this.nScen + 1][this.stepsInRun + 1][nRiskFactorClasses][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)

							nDisabledByAge[scen][stepCount][r][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
									* pDisabilityByRiskClassByAge[scen][stepCount][r][a][g];

		return nDisabledByAge;
	}

	/**
	 * 
	 * @return array [][][][] with numbers of disabled persons by scenario,
	 *         ,riskclass, age and sex , selected for year=stepCount
	 */
	@Override
	public double[][][][] getNDisabledByRiskClassByAge(int stepCount) {

		double[][][][] nDisabledByAge = new double[this.nScen + 1][nRiskFactorClasses][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nDisabledByAge[scen][r][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
								* pDisabilityByRiskClassByAge[scen][stepCount][r][a][g];

		return nDisabledByAge;
	}

	/**
	 * @param scen
	 * @param stepCount
	 * @param g
	 * @return nm
	 */
	/* never used */
	public double[][] getNDiseaseByAge(int scen, int stepCount, int g) {
		double[][] nDiseaseByAge = new double[this.nDiseases][96 + this.stepsInRun];
		;
		double[][][] nDiseaseByRiskClassByAge = makeDiseaseArray(
				this.nDiseaseStateByRiskClassByAge, scen, stepCount, g);
		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int a = 0; a < 96 + this.stepsInRun; a++)
				for (int d = 0; d < this.nDiseases; d++)
					nDiseaseByAge[d][a] += nDiseaseByRiskClassByAge[d][r][a];
		return nDiseaseByAge;
	}

	/**
	 * @return
	 */

	public double[][][][][] getNDiseaseByOriAge() {
		double[][][][][] nDiseaseByAge = new double[nScen + 1][nDim][nDiseases][96][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByOriRiskClassByOriAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < nDim; stepCount++)
								nDiseaseByAge[scen][stepCount][d][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByAge;
	}

	/**
	 * @param age
	 * @param d
	 *            disease number , -1=all diseases together
	 * @return
	 */
	@Override
	public double[][][] getNDiseaseByOriAge(int age, int d) {
		double[][][] nDiseaseByAge = new double[this.nScen + 1][this.nDim - age][2];
		;
		if (d >= 0) {
			double[][][][][][] nDiseaseByOriRiskClassByOriAge = makeDiseaseArray(this.nDiseaseStateByOriRiskClassByOriAge);
			for (int r = 0; r < this.nRiskFactorClasses; r++)

				for (int scen = 0; scen < this.nScen + 1; scen++)

					for (int g = 0; g < 2; g++)

						for (int stepCount = 0; stepCount < this.nDim - age; stepCount++) {
							nDiseaseByAge[scen][stepCount][g] += nDiseaseByOriRiskClassByOriAge[scen][stepCount][d][r][age][g];
							if (scen == 1 && g == 0)
								log.debug(" stepcount " + stepCount
										+ " NDisease "
										+ nDiseaseByAge[scen][stepCount][g]);
						}
		} else
			nDiseaseByAge = getNTotDiseaseByOriAge(age);
		return nDiseaseByAge;
	}

	/**
	 * get the array of diseaseNumbers for disease d for the year year.
	 * 
	 * @param d
	 *            : diseasenumber of disease to return
	 * @return: array with diseaseNumbers. indexes scenario, risk factorclass,
	 *          age, sex
	 */
	@Override
	public double[][][][] getNDiseaseByRiskClassByAge(int d, int year) {

		double[][][][] returnArray = makeDiseaseArray(
				this.nDiseaseStateByRiskClassByAge, year, d);

		return returnArray;
	}

	/**
	 * get the array of diseaseNumbers .
	 * 
	 * 
	 * @return: array with diseaseNumbers. indexes: scenario, year of follow-up,
	 *          risk factorclass, age, sex
	 */
	@Override
	public double[][][][][][] getNDiseaseByRiskClassByAge() {

		double[][][][][][] returnArray = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);

		return returnArray;
	}

	/**
	 * get the array of diseaseNumbers for disease d.
	 * 
	 * @param d
	 *            : diseasenumber of disease to return
	 * @return: array with diseaseNumbers. indexes: scenario, year of follow-up,
	 *          risk factorclass, age, sex
	 */
	@Override
	public double[][][][][] getNDiseaseByRiskClassByAge(int d) {

		double[][][][][] returnArray = makeDiseaseArray(
				this.nDiseaseStateByRiskClassByAge, d);

		return returnArray;
	}

	/**
	 * get the array of diseaseNumbers by age at start of simulation and
	 * riskclass at start of simulation for disease d.
	 * 
	 * @param d
	 *            : diseasenumber of disease to return
	 * @return: array with diseaseNumbers. indexes scenario, year of follow-up,
	 *          risk factorclass, age, sex
	 */
	@Override
	public double[][][][][] getNDiseaseByOriRiskClassByOriAge(int d) {
		double[][][][][][] nDiseaseByOriRiskClassByOriAge = makeDiseaseArray(this.nDiseaseStateByOriRiskClassByOriAge);
		double[][][][][] returnArray = new double[this.nScen + 1][this.nDim][this.nRiskFactorClasses][96][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int g = 0; g < 2; g++)
					for (int a = 0; a < 96; a++)
						for (int stepCount = 0; stepCount < this.nDim - a; stepCount++)
							returnArray[scen][stepCount][r][a][g] = nDiseaseByOriRiskClassByOriAge[scen][stepCount][d][r][a][g];

		return returnArray;
	}

	/**
	 * @return
	 */
	@Override
	public double[][][][][] getMeanRiskByRiskClassByAge() {
		return this.meanRiskByRiskClassByAge;
	}

	/**
	 * @return number in population by age
	 */
	@Override
	public double[][][][] getNPopByAge() {

		double[][][][] nPopByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
							nPopByAge[scen][stepCount][a][g] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	/**
	 * @return number in population by age in year stepCount
	 */
	@Override
	public double[][][] getNPopByAge(int stepCount) {

		double[][][] nPopByAge = new double[this.nScen + 1][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nPopByAge[scen][a][g] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	/**
	 * @return number in population by age in riskFactorClass riskClass
	 */
	@Override
	public double[][][][] getNPopByAgeForRiskclass(int riskClass) {

		double[][][][] nPopByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int stepcount = 0; stepcount < this.stepsInRun + 1; stepcount++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nPopByAge[scen][stepcount][a][g] += this.nPopByRiskClassByAge[scen][stepcount][riskClass][a][g];
		return nPopByAge;

	}

	/**
	 * @param scen
	 * @param stepCount
	 * @param g
	 * @return
	 */
	/* never used */
	public double[] getNPopByAge(int scen, int stepCount, int g) {
		double[] nPopByAge = new double[96 + this.stepsInRun];
		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int a = 0; a < 96 + this.stepsInRun; a++)
				nPopByAge[a] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPopByAge;
	}

	/**
	 * @return
	 */
	/* never used */
	public double[][][] getNPop() {

		double[][][] nPop = new double[this.nScen + 1][this.stepsInRun + 1][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < this.nDiseases; d++)
							for (int stepCount = 0; stepCount < this.nDim; stepCount++)
								nPop[scen][stepCount][g] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPop;

	}

	/**
	 * @return
	 */
	/* never used */
	public double[][][][] getNPopByOriAge() {

		double[][][][] nPopByAge = new double[this.nScen + 1][this.nDim][96][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++)

						for (int stepCount = 0; stepCount < this.nDim; stepCount++)
							nPopByAge[scen][stepCount][a][g] += this.nPopByOriRiskClassByOriAge[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	/* never used */
	public double[][][][] getNPopByOriAge(int riskClass) {

		double[][][][] nPopByAge = new double[this.nScen + 1][this.nDim][96][2];

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++)
					for (int stepCount = 0; stepCount < this.nDim; stepCount++)
						nPopByAge[scen][stepCount][a][g] = this.nPopByOriRiskClassByOriAge[scen][stepCount][riskClass][a][g];
		return nPopByAge;

	}

	public void setCategorized(boolean categorized) {
		this.categorized = categorized;
	}

	public boolean isCategorized() {
		return categorized;
	}

}
