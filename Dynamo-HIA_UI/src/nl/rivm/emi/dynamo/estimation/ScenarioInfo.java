/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

/**
 * @author Hendriek This Class contains information on scenario's
 */
public class ScenarioInfo {
	Log log = LogFactory.getLog(getClass().getName());

	/* simulation information */
	private boolean withNewBorns;
	private float stepsize;
	private int simPopSize;
	private int maxSimAge;/* maximum and minimum age in the simulation */
	private int minSimAge;
	private int yearsInRun;
	private int startYear;
	private int newbornStartYear;
	private int randomSeed;

	private int nScenarios = 1;
	// nScenarios give the number of alternative scenario's (the baseline
	// scenario not included)
	String[] scenarioNames = null;
	private boolean[] isNormal;
	private boolean[] initialPrevalenceType = { false };
	private boolean[] transitionType = { false };
	private boolean[] zeroTransition = null;
	private float[][][] newMean; /*
								 * indexes: scenario,age, sex scenario starts at
								 * index 0 with the first alternative scenario
								 * info
								 */
	private float[][][] newStd; /* indexes: scenario,age, sex */
	private float[][][] newOffset; /* indexes: scenario,age, sex */
	private float[][][][] newPrevalence; /*
										 * indexes: scenario,age, sex, class
										 * scenario starts at index 0 with the
										 * first alternative scenario info
										 */
	private float[][][] oldPrevalence = null; /*
											 * prevalence of reference
											 * situation; indexes: age sex class
											 */
	private float[][][] oldDurationClasses = null; /*
													 * prevalence of duration
													 * classes in the reference
													 * situation; indexes: age
													 * sex class
													 */

	private float[][][] alternativeMeanDrift = null;
	/* indexes: scenario,age, sex + two dimention for matrix */// TODO volgende
																// 3 inlezen en
																// initialiseren
	private float[][][][][] alternativeTransitionMatrix;
	
	
	private float[] succesrate = null;
	private float[] minAge = null; /*
									 * minimum and maximum target age of
									 * scenarios
									 */
	private float[] maxAge = null;
	private boolean[] inMen = null;
	private boolean[] inWomen = null;
	private float[][] populationSize; // float as no reading method for integers
										// is availlable at the moment
	private float[][] baselineAbility;
	private float[][][] diseaseAbility;//
	private float[][][] relRiskAbilityCat;
	private float[][] relRiskAbilityCont;
	private float[][] relRiskAbilityBegin;
	private float[][] relRiskAbilityEnd;
	private float[][] alfaAbility;
	private int[] newborns; // index= year (0=startYearnewborns)
	private int startYearNewborns;
	private float maleFemaleRatio;
	private String[] riskClassnames;
	private int referenceClass;
	private int riskType;
	private DiseaseClusterStructure[] structure;
	private int indexDurationClass;
	private boolean details = false;
	private float[] cutoffs = null;

	private int firstOneForAllPopScenario = -3;
	private boolean[] thisScenarioUsedOneForAllPop;

	private int[] popToScenIndex;

	private boolean[] isOneScenPopulation;

	/**
	 * @return
	 */
	public boolean[] getthisScenarioUsedOneForAllPop() {
		/* check if already calculated; if not make it */
		if (this.firstOneForAllPopScenario == -3)
			this.getNPopulations();
		return thisScenarioUsedOneForAllPop;
	}

	/**
	 * @return
	 */
	public boolean[] getIsFirstForAllPop() {
		if (this.firstOneForAllPopScenario == -3)
			this.getNPopulations();
		return thisScenarioUsedOneForAllPop;
	}

	/**
	 * @return
	 */
	public int getFirstOneForAllPopScenario() {
		if (this.firstOneForAllPopScenario == -3)
			this.getNPopulations();
		return firstOneForAllPopScenario;
	}

	/*
	 * TODO: zorgen dat newPrevalence [0] de oude prevalences bevat
	 */
	public ScenarioInfo() {
		// TODO Auto-generated constructor stub
	}

	public void makeTestData() {

		setInitialPrevalenceType(new boolean[1]);
		getInitialPrevalenceType()[0] = true;

		setNewPrevalence(new float[1][96][2][2]);

		for (int a = 0; a < 96; a++) {
			for (int g = 0; g < 2; g++) {

				getNewPrevalence()[0][a][g][0] = 0.5F;
				getNewPrevalence()[0][a][g][1] = 0.5F;
				;
			}

		}
	}

	public void makeTestData1() {

		setInitialPrevalenceType(new boolean[1]);
		getInitialPrevalenceType()[0] = true;

		setNewPrevalence(new float[1][96][3][3]);

		for (int a = 0; a < 96; a++) {
			for (int g = 0; g < 2; g++) {
				getNewPrevalence()[0][a][g][0] = 0.0F;
				getNewPrevalence()[0][a][g][1] = 0.5F;
				getNewPrevalence()[0][a][g][2] = 0.5F;
				;
			}

		}
	}

	/**
	 * 
	 * @return number of populations
	 */
	public int getNPopulations() {

		int nPopulations = this.getNScenarios() + 1;
		this.thisScenarioUsedOneForAllPop = new boolean[this.getNScenarios()];
		boolean isAtLeastOneAllForOnePopulation = false;
		this.firstOneForAllPopScenario = -1;
		Arrays.fill(thisScenarioUsedOneForAllPop, false);
		if (this.getRiskType() != 2 && this.getNScenarios() > 0)
			for (int scennum = 0; scennum < this.getNScenarios(); scennum++) {
				/* NB is both prevalence and transitions are identical to the reference 
				 * scenario (not very usefull, but users will try) 
				 * this is assumed to be a one for all population, meaning that no extra simulations 
				 * will be done
				 */
				if ((!this.getTransitionType()[scennum])) {
					nPopulations--; /*
									 * remove this scenario from number than
									 * need to be simulated
									 */

					isAtLeastOneAllForOnePopulation = true;
					thisScenarioUsedOneForAllPop[scennum] = true;
					if (this.firstOneForAllPopScenario == -1)
						this.firstOneForAllPopScenario = scennum;
				}
			}
		/* add the one-for-all-scenario that still needs to be simulated */
		if (isAtLeastOneAllForOnePopulation)
			nPopulations++;
     
		/* returns the scenario number belonging with a population */
		this.popToScenIndex =new int[nPopulations];
		this.isOneScenPopulation=new boolean[nPopulations];
		this.getPopToScenIndex()[0] = 0;
		this.getIsOneScenPopulation()[0]=false;
		int currentPop = 1;

		/* look which populations are one-for-all */
		/*
		 * make an indicator array to "translate" the other populations into
		 * scenarionumbers
		 */
		for (int i = 0; i < this.nScenarios; i++) {
			if (i == this.firstOneForAllPopScenario) {
				this.getPopToScenIndex()[currentPop] = i;
				this.getIsOneScenPopulation()[currentPop] = true;
				currentPop++;
			}
			if (!this.thisScenarioUsedOneForAllPop[i]) {
				this.getPopToScenIndex()[currentPop] = i;
				currentPop++;
				
			}
		}

		return nPopulations;

	}

	public float[][][][] getTransitionMatrix(int scen) {
		return DynamoLib.deepcopy(alternativeTransitionMatrix[scen]);
	}

	public void setReferenceClass(int referenceClass) {
		this.referenceClass = referenceClass;
	}

	public int getReferenceClass() {
		return referenceClass;
	}

	public void setNScenarios(int nScenarios) {
		this.nScenarios = nScenarios;
	}

	public int getNScenarios() {
		return nScenarios;
	}

	public void setInitialPrevalenceType(boolean[] initialPrevalenceType) {
		this.initialPrevalenceType = initialPrevalenceType;
	}

	public boolean[] getInitialPrevalenceType() {
		return DynamoLib.deepcopy(initialPrevalenceType);
	}

	public void setTransitionType(boolean[] transitionType) {
		this.transitionType = transitionType;
	}

	public boolean[] getTransitionType() {
		return DynamoLib.deepcopy(transitionType);
	}

	/**
	 * sets transitionType[i] to b
	 * 
	 * @param b
	 *            : value to set
	 * @param i
	 *            : index
	 */
	public void setTransitionType(boolean b, int i) {
		transitionType[i] = b;

	}

	/**
	 * gets transitionType[i]
	 * 
	 * @param b
	 *            : value to set
	 * @param i
	 *            : index
	 */
	public boolean getTransitionType(int i) {
		return transitionType[i];

	}

	/**
	 * set InitialPrevalenceType[i] to b
	 * 
	 * @param b
	 *            : value to set
	 * @param i
	 *            : index
	 */
	public void setInitialPrevalenceType(boolean b, int i) {
		initialPrevalenceType[i] = b;
		// TODO Auto-generated method stub

	}

	public float[] getSuccesrate() {
		return DynamoLib.deepcopy(succesrate);
	}

	public void setSuccesrate(float[] succesrate) {
		this.succesrate = new float[succesrate.length];
		for (int i = 0; i < succesrate.length; i++)
			this.succesrate[i] = succesrate[i];
	}

	public void setSuccesrate(float succesrate, int i) {
		this.succesrate[i] = succesrate;
	}

	public int[] getMinAge() {
		int[] returnarray = new int[minAge.length];
		for (int i = 0; i < minAge.length; i++)
			returnarray[i] = (int) minAge[i];
		return returnarray;
	}

	public void setMinAge(float[] minAge) {
		this.minAge = minAge;
	}

	public void setMinAge(int minAge, int i) {
		this.minAge[i] = minAge;
	}

	public int[] getMaxAge() {
		int[] returnarray = new int[maxAge.length];
		for (int i = 0; i < maxAge.length; i++)
			returnarray[i] = (int) maxAge[i];
		return returnarray;

	}

	public void setMaxAge(float[] maxAge) {
		this.maxAge = maxAge;
	}

	public void setMaxAge(int maxAge, int i) {
		this.maxAge[i] = maxAge;
	}

	public String[] getScenarioNames() {
		return DynamoLib.deepcopy(scenarioNames);
	}

	public void setScenarioNames(String[] scenarioNames) {
		this.scenarioNames = scenarioNames;
	}

	public void setScenarioNames(String scenarioNames, int i) {
		this.scenarioNames[i] = scenarioNames;
	}

	public DiseaseClusterStructure[] getStructure() {
		return structure;
	}

	public void setStructure(DiseaseClusterStructure[] structure) {
		this.structure = structure;
	}

	public void setStructure(DiseaseClusterStructure structure, int i) {
		this.structure[i] = structure;
	}

	public float[] getCutoffs() {
		if (cutoffs == null)
			return cutoffs;
		else
			return DynamoLib.deepcopy(cutoffs);
	}

	public void setCutoffs(float[] cutoffs) {
		this.cutoffs = cutoffs;
	}

	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	public int getRiskType() {
		return riskType;
	}

	public void setNewborns(int[] input) {
		this.newborns = input;
	}

	public void setNewborns(int input, int i) {
		this.newborns[i] = input;
	}

	public int[] getNewborns() {
		return DynamoLib.deepcopy(newborns);
	}

	public void setZeroTransition(boolean[] zeroTransition) {
		this.zeroTransition = zeroTransition;
	}

	public void setZeroTransition(boolean zeroTransition, int i) {
		this.zeroTransition[i] = zeroTransition;
	}

	public boolean[] isZeroTransition() {
		return DynamoLib.deepcopy(zeroTransition);
	}

	public boolean isZeroTransition(int i) {
		return zeroTransition[i];
	}

	public void setOldPrevalence(float[][][] oldPrevalence) {
		this.oldPrevalence = oldPrevalence;
	}

	public float[][][] getOldPrevalence() {
		return DynamoLib.deepcopy(oldPrevalence);
	}

	public void setAlternativeTransitionMatrix(
			float[][][][][] alternativeTransitionMatrix) {
		this.alternativeTransitionMatrix = alternativeTransitionMatrix;
	}
	
	public void setAlternativeTransitionMatrix(
			float[][][][] alternativeTransitionMatrix, int scen) {
		this.alternativeTransitionMatrix[scen] = alternativeTransitionMatrix;
	}

	public float[][][][][] getAlternativeTransitionMatrix() {
		return alternativeTransitionMatrix;
	}

	public void setInMen(boolean[] inMen) {
		this.inMen = inMen;
	}

	public void setInMen(boolean inMen, int i) {
		this.inMen[i] = inMen;
	}

	public boolean[] getInMen() {
		boolean[] returnArray = new boolean[inMen.length];
		for (int i = 0; i < inMen.length; i++)
			returnArray[i] = inMen[i];
		return returnArray;
	}

	public void setInWomen(boolean[] inWomen) {
		this.inWomen = inWomen;
	}

	public void setInWomen(boolean inWomen, int i) {
		this.inWomen[i] = inWomen;
	}

	public boolean[] getInWomen() {
		boolean[] returnArray = new boolean[inWomen.length];
		for (int i = 0; i < inWomen.length; i++)
			returnArray[i] = inWomen[i];
		return returnArray;
	}

	public void setPopulationSize(float[][] populationSize) {
		this.populationSize = populationSize;
	}

	public float[][] getPopulationSize() {
		return populationSize;
	}

	public void setMaleFemaleRatio(float maleFemaleRatio) {
		this.maleFemaleRatio = maleFemaleRatio;
	}

	public float getMaleFemaleRatio() {
		return maleFemaleRatio;
	}

	public void setRiskClassnames(String[] riskClassnames) {
		this.riskClassnames = riskClassnames;
	}

	public String[] getRiskClassnames() {
		return riskClassnames;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setYearsInRun(int yearsInRun) {
		this.yearsInRun = yearsInRun;
	}

	public int getYearsInRun() {
		return yearsInRun;
	}

	public void setDetails(boolean details) {
		this.details = details;
	}

	public boolean isDetails() {
		return details;
	}

	public void setMeanDrift(float[][][] drift) {
		this.alternativeMeanDrift = drift;
	}
	public void setMeanDrift(float[][] drift, int scen) {
		if (this.alternativeMeanDrift==null) this.alternativeMeanDrift=new float [this.nScenarios][][];
		this.alternativeMeanDrift[scen] = drift;
	}


	public float[][][] getMeanDrift() {
		return alternativeMeanDrift;
	}

	public float[][] getMeanDrift(int i) {
		return alternativeMeanDrift[i];
	}

	public void setNewPrevalence(float[][][][] newPrevalence) {
		this.newPrevalence = newPrevalence;
	}

	public void setNewPrevalence(float[][][] inPrevalence, int i
		) {
		
			this.newPrevalence[i] = new float[inPrevalence.length][inPrevalence[0].length][inPrevalence[0][0].length];

			for (int k = 0; k < inPrevalence.length; k++)
				for (int l = 0; l < inPrevalence[0].length; l++)
					for (int j = 0; j < inPrevalence[0][0].length; j++)
						this.newPrevalence[i][k][l][j] = inPrevalence[k][l][j] ;

		
	}

	public boolean isWithInitialChange() {
		boolean returnvalue = false;
		for (int i = 0; i < initialPrevalenceType.length; i++)
			if (initialPrevalenceType[i])
				returnvalue = true;
		return returnvalue;
	}

	public int nWithInitialChange() {
		int returnvalue = 0;
		for (int i = 0; i < initialPrevalenceType.length; i++)
			if (initialPrevalenceType[i])
				returnvalue++;
		return returnvalue;
	}

	public int getNTranstionScenarios() {
		int returnvalue = 0;
		for (int i = 0; i < transitionType.length; i++)
			if (transitionType[i])
				returnvalue++;
		return returnvalue;
	}

	public float[][][][] getNewPrevalence() {
		return newPrevalence;
	}

	/**
	 * this method take a mean, std and skewness and converts it to the
	 * parameters of a lognormal distribution (mu, sigma, offset) before it sets
	 * them as new parameters for the scenario;
	 * 
	 * @param inputMean
	 *            (float [scenario][age][sex]) * @param inputSTD (float
	 *            [scenario][age][sex]) * @param inputSkew (float
	 *            [scenario][age][sex])
	 * @param i
	 *            : number of scenario;
	 * @param isLognormal
	 *            (boolean)
	 * @throws DynamoInconsistentDataException
	 */
	public void setNewMeanSTD(float[][] inputMean, float[][] inputSTD,
			float[][] inputSkew, int i) throws DynamoInconsistentDataException {
		boolean isLognormal = false;
		for (int a = 0; a < 96; a++)
			for (int g = 0; g < 2; g++)
				if (inputSkew[a][g] > 0)
					isLognormal = true;
		if (isLognormal)
			isNormal[i] = false;
		else
			isNormal[i] = true;
		if (isLognormal) {

			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++) {

					try {
						newStd[i][a][g] = (float) DynamoLib
								.findSigma(inputSkew[a][g]);
					} catch (Exception e) {

						log
								.fatal("skewness of lognormal variable "
										+ "has a value that is not possible for a lognormal distribution for scenario "
										+ i + " at age " + a + " and gender "
										+ g + ". Problematic skewness = "
										+ inputSkew[a][g]);
						e.printStackTrace();
						throw new DynamoInconsistentDataException(
								"skewness of lognormal variable "
										+ "has a value that is not possible for a lognormal distribution for scenario "
										+ i + " at age " + a + " and gender "
										+ g + ". Problematic skewness = "
										+ inputSkew[a][g]);

					}
					newMean[i][a][g] = (float) (0.5 * (Math.log(inputSkew[a][g]
							* inputSkew[a][g])
							- Math.log(Math.exp(newStd[i][a][g]
									* newStd[i][a][g]) - 1) - newStd[i][a][g]
							* newStd[i][a][g]));
					newOffset[i][a][g] = (float) (inputMean[a][g] - Math
							.exp(newMean[i][a][g] + 0.5 * newStd[i][a][g]
									* newStd[i][a][g]));

				}
		} else {
			this.newMean[i] = inputMean;
			this.newStd[i] = inputSTD;
			this.newOffset[i] = null;
		}
	}

	/**
	 * @param newvalue
	 *            : new value
	 */

	public float[][][] getNewMean() {
		return newMean;
	}

	public float[][][] getNewStd() {
		return newStd;
	}

	public float[][][] getNewOffset() {
		return newStd;
	}

	public void setNewMean(float[][][] newMean) {
		this.newMean = newMean;
	}

	public void setNewStd(float[][][] newStd) {
		this.newStd = newStd;
	}

	public void setNewOffset(float[][][] newOffset) {
		this.newOffset = newOffset;
	}

	public boolean[] getIsNormal() {
		return isNormal;
	}

	public void setIsNormal(boolean[] isNormal) {
		this.isNormal = isNormal;
	}

	public boolean isWithNewBorns() {
		return withNewBorns;
	}

	public void setWithNewBorns(boolean withNewBorns) {
		this.withNewBorns = withNewBorns;
	}

	public float getStepsize() {
		return stepsize;
	}

	public void setStepsize(float stepsize) {
		this.stepsize = stepsize;
	}

	public int getSimPopSize() {
		return simPopSize;
	}

	public void setSimPopSize(int simPopSize) {
		this.simPopSize = simPopSize;
	}

	public int getMaxSimAge() {
		return maxSimAge;
	}

	public void setMaxSimAge(int maxSimAge) {
		this.maxSimAge = maxSimAge;
	}

	public int getMinSimAge() {
		return minSimAge;
	}

	public void setMinSimAge(int minSimAge) {
		this.minSimAge = minSimAge;
	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	public int getStartYearNewborns() {
		return startYearNewborns;
	}

	public void setStartYearNewborns(int startYearNewborns) {
		this.startYearNewborns = startYearNewborns;
	}

	public boolean[] getZeroTransition() {
		return zeroTransition;
	}

	public float[][][] getOldDurationClasses() {

		return oldDurationClasses;
	}

	public void setOldDurationClasses(float[][][] oldDurationClasses) {
		this.oldDurationClasses = oldDurationClasses;
	}

	public int getIndexDurationClass() {
		return indexDurationClass;
	}

	public void setIndexDurationClass(int indexDurationClass) {
		this.indexDurationClass = indexDurationClass;
	}

	public float[][] getBaselineAbility() {
		return DynamoLib.deepcopy(baselineAbility);
	}

	public void setBaselineAbility(float[][] overallAbility) {
		this.baselineAbility = overallAbility;
	}

	public float[][][] getDiseaseAbility() {
		return DynamoLib.deepcopy(diseaseAbility);
	}

	public void setDiseaseAbility(float[][][] diseaseAbility) {
		this.diseaseAbility = diseaseAbility;
	}

	public float[][][] getRelRiskAbilityCat() {
		return DynamoLib.deepcopy(relRiskAbilityCat);
	}

	public void setRelRiskAbilityCat(float[][][] input) {
		this.relRiskAbilityCat = input;
	}

	public float[][] getRelRiskAbilityCont() {
		return DynamoLib.deepcopy(relRiskAbilityCont);
	}

	public void setRelRiskAbilityCont(float[][] input) {
		this.relRiskAbilityCont = input;
	}

	public float[][] getRelRiskAbilityBegin() {
		return DynamoLib.deepcopy(relRiskAbilityBegin);
	}

	public void setRelRiskAbilityBegin(float[][] relRiskAbilityBegin) {
		this.relRiskAbilityBegin = relRiskAbilityBegin;
	}

	public float[][] getRelRiskAbilityEnd() {
		return DynamoLib.deepcopy(relRiskAbilityEnd);
	}

	public void setRelRiskAbilityEnd(float[][] relRiskAbilityEnd) {
		this.relRiskAbilityEnd = relRiskAbilityEnd;
	}

	public float[][] getAlfaAbility() {
		return DynamoLib.deepcopy(alfaAbility);
	}

	public void setAlfaAbility(float[][] alfaAbility) {
		this.alfaAbility = alfaAbility;
	}

	public int getNewbornStartYear() {
		return newbornStartYear;
	}

	public void setNewbornStartYear(int newbornStartYear) {
		this.newbornStartYear = newbornStartYear;
	}

	

	public boolean[] getIsOneScenPopulation() {
		if (this.firstOneForAllPopScenario == -3)
			this.getNPopulations();
		return isOneScenPopulation;
	}

	

	public int[] getPopToScenIndex() {
		if (this.firstOneForAllPopScenario == -3)
			this.getNPopulations();
		return popToScenIndex;
	}

}
