/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import java.util.Arrays;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private long randomSeed;

	private int nScenarios = 1;
	// nScenarios give the number of alternative scenario's (the baseline
	// scenario not included) !!!!!!!!!!!!!!!!!!!!!!!!!
	String[] scenarioNames = null;
	private boolean[] isNormal;
	private boolean[] initialPrevalenceType = { false };
	private boolean[] transitionType = { false };
	private boolean[] nettoTransition;

	public boolean[] getNettoTransition() {
		return nettoTransition;
	}

	public boolean getNettoTransition(int i) {
		if (this.nettoTransition == null)
			return false;
		else
			return nettoTransition[i];
	}

	public void setNettoTransition(boolean[] nettoTransition) {
		this.nettoTransition = nettoTransition;
	}

	/**
	 * set the indicator for calculating an "own" net transition rate for
	 * scenario i
	 * 
	 * @param i
	 *            : scenario number (reference scenario not included)
	 * @param nettoTransition
	 *            boolean
	 */
	public void setNettoTransition(int i, boolean netTransition) {
		if (this.nettoTransition == null) {
			this.nettoTransition = new boolean[this.nScenarios];
			for (int j = 1; j < this.nScenarios; j++)
				this.nettoTransition[j] = false;
		}

		this.nettoTransition[i] = netTransition;

	}

	private boolean[] dalyType = { false };

	public boolean[] getDalyType() {
		return dalyType;
	}

	private int[] numberOfDalyPopForThisScenario; /*
												 * gives the number of the
												 * population with the DALY for
												 * this scen -1 is no daly
												 * calculated TODO
												 */

	public int[] getNumberOfDalyPopForThisScenario() {
		return numberOfDalyPopForThisScenario;
	}

	/**
	 * the scenario number of the daly-population ; this is -1 for non-daly
	 * populations and the one-for-all dalypoopulation (as the latter can belong
	 * to multiple scenario's
	 */
	private int[] scenNumberOfThisDalyPop;/*
										 * give the scenarioNumber belonging to
										 * population
										 */

	public int[] getScenNumberOfDaly4thisScen() {
		return scenNumberOfThisDalyPop;
	}

	public boolean hasDalyScenarios() {
		if (this.nDalyPops > 0)
			return true;
		else
			return false;

	}

	private int nDalyPops = 0;
	private boolean[] zeroTransition = null;
	private float[][][] newMean; /*
								 * indexes: scenario,age, sex scenario starts at
								 * index 0 with the first alternative scenario
								 * info
								 */
	private float[][][] newStd; /* indexes: scenario,age, sex */
	private float[][][] newOffset; /* indexes: scenario,age, sex */

	private float[][][] oldMean; /*
								 * indexes: scenario,age, sex scenario starts at
								 * index 0 with the first alternative scenario
								 * info
								 */

	public float[][][] getOldMean() {
		return oldMean;
	}

	public float[][] getOldMean(int scen) {
		return oldMean[scen];
	}

	public void setOldMean(float[][][] oldMean) {
		this.oldMean = oldMean;
	}

	public float[][][] getOldStd() {
		return oldStd;
	}

	public float[][] getOldStd(int scen) {
		return oldStd[scen];
	}

	public void setOldStd(float[][][] oldStd) {
		this.oldStd = oldStd;
	}

	public float[][][] getOldSkewness() {
		return oldSkewness;
	}

	public float[][] getOldSkewness(int scen) {
		return oldSkewness[scen];
	}

	public void setOldSkewness(float[][][] oldSkewness) {
		this.oldSkewness = oldSkewness;
	}

	private float[][][] oldStd; /* indexes: scenario,age, sex */
	private float[][][] oldSkewness; /* indexes: scenario,age, sex */

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
	private float[][][] alternativeOffsetDrift = null;
	private float[][][] alternativeSDDrift = null;

	public void setAlternativeMeanDrift(float[][][] alternativeMeanDrift) {
		this.alternativeMeanDrift = alternativeMeanDrift;
	}

	public void setAlternativeMeanDrift(float[][] alternativeDrift, int scen) {
		this.alternativeMeanDrift[scen] = alternativeDrift;
	}

	public void setAlternativeOffsetDrift(float[][] alternativeDrift, int scen) {
		this.alternativeOffsetDrift[scen] = alternativeDrift;
	}

	public void setAlternativeSDDrift(float[][] alternativeDrift, int scen) {
		this.alternativeSDDrift[scen] = alternativeDrift;
	}

	public float[][] getAlternativeMeanDrift(int scen) {
		return alternativeMeanDrift[scen];
	}

	public float[][] getAlternativeSDDrift(int scen) {
		return alternativeSDDrift[scen];
	}

	public float[][] getAlternativeOffsetDrift(int scen) {
		return alternativeOffsetDrift[scen];
	}

	public float[][][] getAlternativeMeanDrift() {
		return alternativeMeanDrift;
	}

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
	private double[][] baselineAbility;
	private double[][][] diseaseAbility;//
	private double[][][] relRiskAbilityCat;
	private double[][] relRiskAbilityCont;
	private double[][] relRiskAbilityBegin;
	private double[][] relRiskAbilityEnd;
	private double[][] alfaAbility;
	private int[] newborns; // index= year (0=startYearnewborns)
	private int startYearNewborns;
	private float maleFemaleRatio;
	private String[] riskClassnames;
	private int referenceClass;
	private int riskType;
	private DiseaseClusterStructure[] structure;
	private int indexDurationClass;
	private float[] cutoffs = null;
	private float referenceRiskFactorValue = 0;

	public float getReferenceRiskFactorValue() {
		return referenceRiskFactorValue;
	}

	public void setReferenceRiskFactorValue(float referenceRiskFactorValue) {
		this.referenceRiskFactorValue = referenceRiskFactorValue;
	}

	/**
	 * firstOneForAllPopScenario : -3 if not initialized, and -1 if no OneForAll
	 * population is present
	 */
	
		

	
	private int nScenariosIncludingDalys;

	private int nPopulations = -3;

	private String refScenName="reference scenario";

	public String getRefScenName() {
		return refScenName;
	}

	


	public ScenarioInfo() {

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
	 * adds information on DALY scenario's and returns the number of extra
	 * populations needed to simulate for this
	 * 
	 * @param nPopulations
	 * 
	 */
	public int addDalyScenarios(int nPopulations) {
		/* For safety: only do this when no dalyScenarios have yet been added */
		int nExtraPop = this.nDalyPops;
		if (!hasDalyScenarios()) {
			/*
			 * first count the number of scenarios that needs to be added and
			 * fill the dalyType array: This indicates for each population that
			 * is a dalytype population
			 */
			dalyType = new boolean[nPopulations];
			Arrays.fill(dalyType, false);
			
			int extraScens = 0;

			for (int scennum = 0; scennum < this.nScenarios; scennum++) {
				if (!transitionType[scennum]) {
					extraScens++;
				}

			}
			this.setNScenariosIncludingDalys(extraScens + this.getNScenarios());
			this.numberOfDalyPopForThisScenario = new int[this.nScenarios];
			if (extraScens > 0) {

				/* make arrays with properties for the new part of the arrays */
				boolean[] initialPrevalenceType2 = null;
				boolean[] transitionType2 = null;
				boolean[] dalyType2 = null;
				boolean[] isOneScenPopulation2 = null;
				int[] popToScenIndex2 = null;

				
					initialPrevalenceType2 = new boolean[extraScens];
					transitionType2 = new boolean[extraScens];
					dalyType2 = new boolean[extraScens];
					popToScenIndex2 = new int[extraScens];
					isOneScenPopulation2 = new boolean[extraScens];
					Arrays.fill(isOneScenPopulation2, false);
					nExtraPop = extraScens;
				
				this.nDalyPops = nExtraPop;
				for (int scennum2 = 0; scennum2 < nExtraPop; scennum2++) {
					initialPrevalenceType2[scennum2] = true;
					transitionType2[scennum2] = false;
					dalyType2[scennum2] = true;
				}
				this.initialPrevalenceType = ArrayUtils.addAll(
						initialPrevalenceType, initialPrevalenceType2);
				this.transitionType = ArrayUtils.addAll(transitionType,
						transitionType2);
				this.dalyType = ArrayUtils.addAll(dalyType, dalyType2);
				
				/*
				 * now calculate the number of population needed: if categorical
				 * or compound risk factor then this is one extra, that is an
				 * extra one for all population if continous risk factor, than
				 * this is equal to the number of extraSens
				 */

				/**
				 * the scenario number of the daly-population ; this is -1 for
				 * non-daly populations and the one-for-all dalypoopulation (as
				 * the latter can belong to multiple scenario's
				 */
				this.scenNumberOfThisDalyPop = new int[this.nPopulations
						+ nExtraPop];
				Arrays.fill(this.scenNumberOfThisDalyPop, -1);

				Arrays.fill(this.numberOfDalyPopForThisScenario, -1);

				
				 if (nExtraPop > 0) {

					int index = 0;
					Arrays.fill(scenNumberOfThisDalyPop, -1);
					/*
					 * in this case the number of populations is equal to that
					 * of the number of populations: no, nscenarios in exclusive
					 * of the reference scenario
					 */
					for (int scennum = 0; scennum < this.nScenarios; scennum++) {
						if (!transitionType[scennum]) {
							numberOfDalyPopForThisScenario[scennum] = index
									+ this.getNPopulationsWithoutDalys();
							scenNumberOfThisDalyPop[this
									.getNPopulationsWithoutDalys()
									+ index] = scennum; /*
														 * this is the same
														 * value as
														 * popToScenIndex
														 */
							popToScenIndex2[index] = scennum; /*
															 * scenario
															 * numbering is
															 * always without
															 * the reference
															 * scen
															 */
							index++;
						} else {
							numberOfDalyPopForThisScenario[scennum] = -1;
						}
					}
				}
				
				this.setNScenariosIncludingDalys(transitionType.length);

			} else {
				for (int scen = 0; scen < nScenarios; scen++)
					numberOfDalyPopForThisScenario[scen] = -1;
			}
		}

		return nExtraPop;

	}

	/**
	 * gets number of simulated populations and sets all the indicator variables
	 * 
	 * @return number of populations
	 */
	public int getNPopulations() {

		/*
		 * only do the calculation the first time necessary as the addDALY
		 * method also works the first time only, so the adjustments of this
		 * methods are not carried out if you rerun this method
		 */
		if (this.nPopulations > -1)
			return this.nPopulations;
		else {
			this.nPopulations = this.getNScenarios() + 1;
						

			nPopulations += addDalyScenarios(nPopulations);
			return nPopulations;
		}

	}

	/**
	 * gets number of simulated populations before DALY scenarios are added
	 * 
	 * @return number of populations
	 */
	public int getNPopulationsWithoutDalys() {

		int nPopulations = this.getNScenarios() + 1;
		
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

	/**
	 * get number of alternative scenarios (reference scenario not included)
	 * 
	 * @return
	 */
	public int getNScenarios() {
		return nScenarios;
	}

	public int getNScenariosIncludingDalys() {
		return nScenariosIncludingDalys;
	}

	public void setNScenariosIncludingDalys(int nScen) {
		this.nScenariosIncludingDalys = nScen;
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
		return DynamoLib.deepcopy(this.succesrate);
	}

	/**
	 * gets succesrate as double
	 * 
	 * @return succesrate by scenario (double [])
	 */
	public double[] getDoubleSuccesrate() {
		double[] returnArray = new double[this.succesrate.length];
		for (int scen = 0; scen < this.succesrate.length; scen++)
			returnArray[scen] = this.succesrate[scen];

		return returnArray;
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

	public void setOldPrevalence(float[][][] oldPrevalence)
			throws DynamoInconsistentDataException {
		this.oldPrevalence = checkedRates(oldPrevalence);
	}

	/*  aangepast in maart 2014 waarbij de aanpassing gelijk is gemaakt aan die  voor de oude prevalentie */
	public float[][][] checkedRates(float[][][] prevalence)
			throws DynamoInconsistentDataException {
		for (int a = 0; a < 96; a++)

			for (int s = 0; s < 2; s++) {

				float sumP = 0;
				for (float prev : prevalence[a][s])
					sumP += prev;
				if (Math.abs(sumP - 1.0) > 0.02)
					throw new DynamoInconsistentDataException(
							"Risk factor prevalence does not sum "
									+ "to 100% but to " + 100 * sumP + "%"
									+ " for age " + a + " and gender " + s);
				else if (Math.abs(sumP - 1.0) > 0.00001)
					for (int r = 0; r < prevalence[a][s].length; r++)
					prevalence[a][s][r] = (float) prevalence[a][s][r] /sumP;
			}
		return prevalence;
	}
	
	
	
	
	
	
	

	public float[][][] getOldPrevalence() {
		return DynamoLib.deepcopy(oldPrevalence);
	}

	public void setAlternativeTransitionMatrix(
			float[][][][][] alternativeTransitionMatrix) {
		this.alternativeTransitionMatrix = alternativeTransitionMatrix;
	}

	public void setAlternativeTransitionMatrix(int scenNumber, int age,
			int sex, float[][] alternativeTransitionMatrix) {
		if (this.alternativeTransitionMatrix[scenNumber] == null)
			this.alternativeTransitionMatrix[scenNumber] = new float[96][2][][];
		this.alternativeTransitionMatrix[scenNumber][age][sex] = alternativeTransitionMatrix;
	}

	public void setAlternativeTransitionMatrix(
			float[][][][] alternativeTransitionMatrix, int scen)
			throws DynamoInconsistentDataException {
		int dim1 = alternativeTransitionMatrix.length;
		int dim2 = alternativeTransitionMatrix[0].length;
		int dim3 = alternativeTransitionMatrix[0][0].length;
		int dim4 = alternativeTransitionMatrix[0][0][0].length;
		for (int i = 0; i < dim1; i++)
			for (int i1 = 0; i1 < dim2; i1++)
				for (int i11 = 0; i11 < dim3; i11++) {
					float sum = 0;
					for (int i111 = 0; i111 < dim4; i111++)
						sum += alternativeTransitionMatrix[i][i1][i11][i111];
					if (Math.abs(sum - 1) > 0.001)
						throw new DynamoInconsistentDataException(
								"transitionrates for scenario " + (scen + 1)
										+ " from category " + (i11 + 1)
										+ " do not sum to 100% for age " + i
										+ " and gender " + i1);

				}
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

	public void setMeanDrift(float[][][] drift) {
		this.alternativeMeanDrift = drift;
	}

	public void setMeanDrift(float[][] drift, int scen) {
		if (this.alternativeMeanDrift == null)
			this.alternativeMeanDrift = new float[this.nScenarios][][];
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

	public void setNewPrevalence(float[][][] inPrevalence, int i)
			throws DynamoInconsistentDataException {

		this.newPrevalence[i] = new float[inPrevalence.length][inPrevalence[0].length][inPrevalence[0][0].length];

		for (int k = 0; k < inPrevalence.length; k++)
			for (int l = 0; l < inPrevalence[0].length; l++)
				for (int j = 0; j < inPrevalence[0][0].length; j++)
					this.newPrevalence[i][k][l][j] = inPrevalence[k][l][j];
		this.newPrevalence[i] = checkedRates(this.newPrevalence[i]);
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

	public float[][][] getNewPrevalence(int scen) {
		return newPrevalence[scen];
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
	 * @param scen
	 *            : number of scenario;
	 * @param isLognormal
	 *            (boolean)
	 * @throws DynamoInconsistentDataException
	 */
	public void setNewMeanSTD(float[][] inputMean, float[][] inputSTD,
			float[][] inputSkew, int scen)
			throws DynamoInconsistentDataException {
		boolean isLognormal = false;
		if (this.oldMean == null) {

			this.oldMean = new float[this.nScenarios][][];
			this.oldStd = new float[this.nScenarios][][];
			this.oldSkewness = new float[this.nScenarios][][];
		}
		this.oldMean[scen] = inputMean;
		this.oldStd[scen] = inputSTD;
		this.oldSkewness[scen] = inputSkew;

		for (int a = 0; a < 96; a++)
			for (int g = 0; g < 2; g++)
				if (inputSkew[a][g] > 0)
					isLognormal = true;
		if (isLognormal)
			isNormal[scen] = false;
		else
			isNormal[scen] = true;
		if (isLognormal) {

			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++) {

					try {
						newStd[scen][a][g] = (float) DynamoLib
								.findSigma(inputSkew[a][g]);
					} catch (Exception e) {

						log
								.fatal("skewness of lognormal variable "
										+ "has a value that is not possible for a lognormal distribution for scenario "
										+ scen + " at age " + a
										+ " and gender " + g
										+ ". Problematic skewness = "
										+ inputSkew[a][g]);
						e.printStackTrace();
						throw new DynamoInconsistentDataException(
								"skewness of lognormal variable "
										+ "has a value that is not possible for a lognormal distribution for scenario "
										+ scen + " at age " + a
										+ " and gender " + g
										+ ". Problematic skewness = "
										+ inputSkew[a][g]);

					}
					newMean[scen][a][g] = (float) (0.5 * (Math
							.log(inputSTD[a][g] * inputSTD[a][g])
							- Math.log(Math.exp(newStd[scen][a][g]
									* newStd[scen][a][g]) - 1) - newStd[scen][a][g]
							* newStd[scen][a][g]));
					newOffset[scen][a][g] = (float) (inputMean[a][g] - Math
							.exp(newMean[scen][a][g] + 0.5 * newStd[scen][a][g]
									* newStd[scen][a][g]));

				}
		} else {
			this.newMean[scen] = inputMean;
			this.newStd[scen] = inputSTD;
			this.newOffset[scen] = null;
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
		return newOffset;
	}

	public float[][] getNewOffset(int scen) {
		return newOffset[scen];
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

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
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

	public double[][] getBaselineAbility() {
		return DynamoLib.deepcopy(baselineAbility);
	}

	public void setBaselineAbility(double[][] ds) {
		this.baselineAbility = ds;
	}

	public double[][][] getDiseaseAbility() {
		return DynamoLib.deepcopy(diseaseAbility);
	}

	public void setDiseaseAbility(double[][][] ds) {
		this.diseaseAbility = ds;
	}

	public double[][][] getRelRiskAbilityCat() {
		return DynamoLib.deepcopy(relRiskAbilityCat);
	}

	public void setRelRiskAbilityCat(double[][][] ds) {
		this.relRiskAbilityCat = ds;
	}

	public double[][] getRelRiskAbilityCont() {
		return DynamoLib.deepcopy(relRiskAbilityCont);
	}

	public void setRelRiskAbilityCont(double[][] ds) {
		this.relRiskAbilityCont = ds;
	}

	public double[][] getRelRiskAbilityBegin() {
		return DynamoLib.deepcopy(relRiskAbilityBegin);
	}

	public void setRelRiskAbilityBegin(double[][] ds) {
		this.relRiskAbilityBegin = ds;
	}

	public double[][] getRelRiskAbilityEnd() {
		return DynamoLib.deepcopy(relRiskAbilityEnd);
	}

	public void setRelRiskAbilityEnd(double[][] ds) {
		this.relRiskAbilityEnd = ds;
	}

	public double[][] getAlfaAbility() {
		return DynamoLib.deepcopy(alfaAbility);
	}

	public void setAlphaAbility(double[][] ds) {
		this.alfaAbility = ds;
	}

	public int getNewbornStartYear() {
		return newbornStartYear;
	}

	public void setNewbornStartYear(int newbornStartYear) {
		this.newbornStartYear = newbornStartYear;
	}

	
	public void setRefScenName(String inputString) {
		this.refScenName=inputString;
		
	}

}
