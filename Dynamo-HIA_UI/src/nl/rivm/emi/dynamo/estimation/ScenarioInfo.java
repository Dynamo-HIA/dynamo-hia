/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import java.util.Arrays;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ScenarioInfo.
 *
 * @author Hendriek This Class contains information on scenario's
 */
public class ScenarioInfo {
	
	/** The log. */
	Log log = LogFactory.getLog(getClass().getName());

		/* simulation information */
	/** The with new borns. */
	private boolean withNewBorns;
	
	/** The stepsize. */
	private float stepsize;
	
	/** The sim pop size. */
	private int simPopSize;
	
	/** The max sim age. */
	private int maxSimAge;/* maximum and minimum age in the simulation */
	
	/** The min sim age. */
	private int minSimAge;
	
	/** The years in run. */
	private int yearsInRun;
	
	/** The start year. */
	private int startYear;
	
	/** The newborn start year. */
	private int newbornStartYear;
	
	/** The random seed. */
	private long randomSeed;

	/** The n scenarios. */
	private int nScenarios = 1;
	// nScenarios give the number of alternative scenario's (the baseline
	// scenario not included) !!!!!!!!!!!!!!!!!!!!!!!!!
	/** The scenario names. */
	String[] scenarioNames = null;
	
	/** The is normal. */
	private boolean[] isNormal;
	
	/** The initial prevalence type. */
	private boolean[] initialPrevalenceType = { false };
	
	/** The transition type. */
	private boolean[] transitionType = { false };
	
	/** The netto transition. */
	private boolean[] nettoTransition;

	/**
	 * Gets the netto transition.
	 *
	 * @return the netto transition
	 */
	public boolean[] getNettoTransition() {
		return nettoTransition;
	}

	/**
	 * Gets the netto transition.
	 *
	 * @param i the i
	 * @return the netto transition
	 */
	public boolean getNettoTransition(int i) {
		if (this.nettoTransition == null)
			return false;
		else
			return nettoTransition[i];
	}

	/**
	 * Sets the netto transition.
	 *
	 * @param nettoTransition the new netto transition
	 */
	public void setNettoTransition(boolean[] nettoTransition) {
		this.nettoTransition = nettoTransition;
	}

	/**
	 * set the indicator for calculating an "own" net transition rate for
	 * scenario i.
	 *
	 * @param i            : scenario number (reference scenario not included)
	 * @param netTransition the net transition
	 */
	public void setNettoTransition(int i, boolean netTransition) {
		if (this.nettoTransition == null) {
			this.nettoTransition = new boolean[this.nScenarios];
			for (int j = 1; j < this.nScenarios; j++)
				this.nettoTransition[j] = false;
		}

		this.nettoTransition[i] = netTransition;

	}

	/** The daly type. */
	private boolean[] dalyType = { false };

	/**
	 * Gets the daly type.
	 *
	 * @return the daly type
	 */
	public boolean[] getDalyType() {
		return dalyType;
	}

	/** The number of daly pop for this scenario. */
	private int[] numberOfDalyPopForThisScenario; /*
												 * gives the number of the
												 * population with the DALY for
												 * this scen -1 is no daly
												 * calculated TODO
												 */

	/**
  * Gets the number of daly pop for this scenario.
  *
  * @return the number of daly pop for this scenario
  */
 public int[] getNumberOfDalyPopForThisScenario() {
		return numberOfDalyPopForThisScenario;
	}

	/** the scenario number of the daly-population ; this is -1 for non-daly populations and the one-for-all dalypoopulation (as the latter can belong to multiple scenario's. */
	private int[] scenNumberOfThisDalyPop;/*
										 * give the scenarioNumber belonging to
										 * population
										 */

	/**
 * Gets the scen number of daly4this scen.
 *
 * @return the scen number of daly4this scen
 */
public int[] getScenNumberOfDaly4thisScen() {
		return scenNumberOfThisDalyPop;
	}

	/**
	 * Checks for daly scenarios.
	 *
	 * @return true, if successful
	 */
	public boolean hasDalyScenarios() {
		if (this.nDalyPops > 0)
			return true;
		else
			return false;

	}

	/** The n daly pops. */
	private int nDalyPops = 0;
	
	/** The zero transition. */
	private boolean[] zeroTransition = null;
	
	/** The new mean. */
	private float[][][] newMean; /*
								 * indexes: scenario,age, sex scenario starts at
								 * index 0 with the first alternative scenario
								 * info
								 */
	/** The new std. */
 private float[][][] newStd; /* indexes: scenario,age, sex */
	
	/** The new offset. */
	private float[][][] newOffset; /* indexes: scenario,age, sex */

	/** The old mean. */
	private float[][][] oldMean; /*
								 * indexes: scenario,age, sex scenario starts at
								 * index 0 with the first alternative scenario
								 * info
								 */

	/**
  * Gets the old mean.
  *
  * @return the old mean
  */
 public float[][][] getOldMean() {
		return oldMean;
	}

	/**
	 * Gets the old mean.
	 *
	 * @param scen the scen
	 * @return the old mean
	 */
	public float[][] getOldMean(int scen) {
		return oldMean[scen];
	}

	/**
	 * Sets the old mean.
	 *
	 * @param oldMean the new old mean
	 */
	public void setOldMean(float[][][] oldMean) {
		this.oldMean = oldMean;
	}

	/**
	 * Gets the old std.
	 *
	 * @return the old std
	 */
	public float[][][] getOldStd() {
		return oldStd;
	}

	/**
	 * Gets the old std.
	 *
	 * @param scen the scen
	 * @return the old std
	 */
	public float[][] getOldStd(int scen) {
		return oldStd[scen];
	}

	/**
	 * Sets the old std.
	 *
	 * @param oldStd the new old std
	 */
	public void setOldStd(float[][][] oldStd) {
		this.oldStd = oldStd;
	}

	/**
	 * Gets the old skewness.
	 *
	 * @return the old skewness
	 */
	public float[][][] getOldSkewness() {
		return oldSkewness;
	}

	/**
	 * Gets the old skewness.
	 *
	 * @param scen the scen
	 * @return the old skewness
	 */
	public float[][] getOldSkewness(int scen) {
		return oldSkewness[scen];
	}

	/**
	 * Sets the old skewness.
	 *
	 * @param oldSkewness the new old skewness
	 */
	public void setOldSkewness(float[][][] oldSkewness) {
		this.oldSkewness = oldSkewness;
	}

	/** The old std. */
	private float[][][] oldStd; /* indexes: scenario,age, sex */
	
	/** The old skewness. */
	private float[][][] oldSkewness; /* indexes: scenario,age, sex */

	/** The new prevalence. */
	private float[][][][] newPrevalence; /*
										 * indexes: scenario,age, sex, class
										 * scenario starts at index 0 with the
										 * first alternative scenario info
										 */
	/** The old prevalence. */
 private float[][][] oldPrevalence = null; /*
											 * prevalence of reference
											 * situation; indexes: age sex class
											 */
	/** The old duration classes. */
 private float[][][] oldDurationClasses = null; /*
													 * prevalence of duration
													 * classes in the reference
													 * situation; indexes: age
													 * sex class
													 */

	/** The alternative mean drift. */
 private float[][][] alternativeMeanDrift = null;
	
	/** The alternative offset drift. */
	private float[][][] alternativeOffsetDrift = null;
	
	/** The alternative sd drift. */
	private float[][][] alternativeSDDrift = null;

	/**
	 * Sets the alternative mean drift.
	 *
	 * @param alternativeMeanDrift the new alternative mean drift
	 */
	public void setAlternativeMeanDrift(float[][][] alternativeMeanDrift) {
		this.alternativeMeanDrift = alternativeMeanDrift;
	}

	/**
	 * Sets the alternative mean drift.
	 *
	 * @param alternativeDrift the alternative drift
	 * @param scen the scen
	 */
	public void setAlternativeMeanDrift(float[][] alternativeDrift, int scen) {
		this.alternativeMeanDrift[scen] = alternativeDrift;
	}

	/**
	 * Sets the alternative offset drift.
	 *
	 * @param alternativeDrift the alternative drift
	 * @param scen the scen
	 */
	public void setAlternativeOffsetDrift(float[][] alternativeDrift, int scen) {
		this.alternativeOffsetDrift[scen] = alternativeDrift;
	}

	/**
	 * Sets the alternative sd drift.
	 *
	 * @param alternativeDrift the alternative drift
	 * @param scen the scen
	 */
	public void setAlternativeSDDrift(float[][] alternativeDrift, int scen) {
		this.alternativeSDDrift[scen] = alternativeDrift;
	}

	/**
	 * Gets the alternative mean drift.
	 *
	 * @param scen the scen
	 * @return the alternative mean drift
	 */
	public float[][] getAlternativeMeanDrift(int scen) {
		return alternativeMeanDrift[scen];
	}

	/**
	 * Gets the alternative sd drift.
	 *
	 * @param scen the scen
	 * @return the alternative sd drift
	 */
	public float[][] getAlternativeSDDrift(int scen) {
		return alternativeSDDrift[scen];
	}

	/**
	 * Gets the alternative offset drift.
	 *
	 * @param scen the scen
	 * @return the alternative offset drift
	 */
	public float[][] getAlternativeOffsetDrift(int scen) {
		return alternativeOffsetDrift[scen];
	}

	/**
	 * Gets the alternative mean drift.
	 *
	 * @return the alternative mean drift
	 */
	public float[][][] getAlternativeMeanDrift() {
		return alternativeMeanDrift;
	}

	/* indexes: scenario,age, sex + two dimention for matrix */// TODO volgende
	// 3 inlezen en
	// initialiseren
	/** The alternative transition matrix. */
	private float[][][][][] alternativeTransitionMatrix;

	/** The succesrate. */
	private float[] succesrate = null;
	
	/** The min age. */
	private float[] minAge = null; /*
									 * minimum and maximum target age of
									 * scenarios
									 */
	/** The max age. */
 private float[] maxAge = null;
	
	/** The in men. */
	private boolean[] inMen = null;
	
	/** The in women. */
	private boolean[] inWomen = null;
	
	/** The population size. */
	private float[][] populationSize; // float as no reading method for integers
	// is availlable at the moment
	/** The baseline ability. */
	private double[][] baselineAbility;
	
	/** The disease ability. */
	private double[][][] diseaseAbility;//
	
	/** The rel risk ability cat. */
	private double[][][] relRiskAbilityCat;
	
	/** The rel risk ability cont. */
	private double[][] relRiskAbilityCont;
	
	/** The rel risk ability begin. */
	private double[][] relRiskAbilityBegin;
	
	/** The rel risk ability end. */
	private double[][] relRiskAbilityEnd;
	
	/** The alfa ability. */
	private double[][] alfaAbility;
	
	/** The newborns. */
	private int[] newborns; // index= year (0=startYearnewborns)
	
	/** The start year newborns. */
	private int startYearNewborns;
	
	/** The male female ratio. */
	private float maleFemaleRatio;
	
	/** The risk classnames. */
	private String[] riskClassnames;
	
	/** The reference class. */
	private int referenceClass;
	
	/** The risk type. */
	private int riskType;
	
	/** The structure. */
	private DiseaseClusterStructure[] structure;
	
	/** The index duration class. */
	private int indexDurationClass;
	
	/** The cutoffs. */
	private float[] cutoffs = null;
	
	/** The reference risk factor value. */
	private float referenceRiskFactorValue = 0;

	/**
	 * Gets the reference risk factor value.
	 *
	 * @return the reference risk factor value
	 */
	public float getReferenceRiskFactorValue() {
		return referenceRiskFactorValue;
	}

	/**
	 * Sets the reference risk factor value.
	 *
	 * @param referenceRiskFactorValue the new reference risk factor value
	 */
	public void setReferenceRiskFactorValue(float referenceRiskFactorValue) {
		this.referenceRiskFactorValue = referenceRiskFactorValue;
	}

	/** firstOneForAllPopScenario : -3 if not initialized, and -1 if no OneForAll population is present. */
	private int firstOneForAllPopScenario = -3;
	
	/** The first one for all daly pop. */
	private int firstOneForAllDalyPop = -3;

	/** The this scenario used one for all pop. */
	private boolean[] thisScenarioUsedOneForAllPop;

	/** The pop to scen index. */
	private int[] popToScenIndex;

	/** The is one scen population. */
	private boolean[] isOneScenPopulation;

	/** The n scenarios including dalys. */
	private int nScenariosIncludingDalys;

	/** The n populations. */
	private int nPopulations = -3;

	/** The ref scen name. */
	private String refScenName="reference scenario";

	/**
	 * Gets the ref scen name.
	 *
	 * @return the ref scen name
	 */
	public String getRefScenName() {
		return refScenName;
	}

	/**
	 * Gets the this scenario used one for all pop.
	 *
	 * @return the this scenario used one for all pop
	 */
	public boolean[] getthisScenarioUsedOneForAllPop() {
		/* check if already calculated; if not make it */
		if (this.firstOneForAllPopScenario == -3)
			/*
			 * getNPopulations() also calculates which populations are
			 * oneForAllPopulations
			 */
			this.getNPopulations();
		return thisScenarioUsedOneForAllPop;
	}

	/**
	 * Gets the checks if is first for all pop.
	 *
	 * @return the checks if is first for all pop
	 */
	public boolean[] getIsFirstForAllPop() {
		if (this.firstOneForAllPopScenario == -3)

			/*
			 * getNPopulations() also calculates which populations are
			 * oneForAllPopulations
			 */
			this.getNPopulations();
		return thisScenarioUsedOneForAllPop;
	}

	/**
	 * Gets the first one for all pop scenario.
	 *
	 * @return the first one for all pop scenario
	 */
	public int getFirstOneForAllPopScenario() {
		if (this.firstOneForAllPopScenario == -3)
			/*
			 * getNPopulations() also calculates which populations are
			 * oneForAllPopulations
			 */
			this.getNPopulations();
		return firstOneForAllPopScenario;
	}

	/**
	 * Instantiates a new scenario info.
	 */
	public ScenarioInfo() {

	}

	/**
	 * Make test data.
	 */
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

	/**
	 * Make test data1.
	 */
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
	 * populations needed to simulate for this.
	 *
	 * @param nPopulations the n populations
	 * @return the int
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
			this.setFirstOneForAllDalyPop(-1);
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

				/* continuous riskfactor */
				if (this.riskType == 2) {
					initialPrevalenceType2 = new boolean[extraScens];
					transitionType2 = new boolean[extraScens];
					dalyType2 = new boolean[extraScens];
					popToScenIndex2 = new int[extraScens];
					isOneScenPopulation2 = new boolean[extraScens];
					Arrays.fill(isOneScenPopulation2, false);
					nExtraPop = extraScens;
				} else {
					initialPrevalenceType2 = new boolean[1];
					transitionType2 = new boolean[1];
					dalyType2 = new boolean[1];
					popToScenIndex2 = new int[1];
					isOneScenPopulation2 = new boolean[1];
					isOneScenPopulation2[0] = true;
					popToScenIndex2[0] = this.firstOneForAllPopScenario;
					dalyType2[0] = true;
					nExtraPop = 1;
				}
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
				this.isOneScenPopulation = ArrayUtils.addAll(
						isOneScenPopulation, isOneScenPopulation2);
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

				this.setFirstOneForAllDalyPop(-1);
				if (nExtraPop == 1 && !(this.riskType == 2)) {
					this.setFirstOneForAllDalyPop(this
							.getNPopulationsWithoutDalys());
					numberOfDalyPopForThisScenario[this.firstOneForAllPopScenario] = this
							.getFirstOneForAllDalyPop();
					popToScenIndex2[0] = this.firstOneForAllPopScenario;
				} else if (nExtraPop > 0) {

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
				this.popToScenIndex = ArrayUtils.addAll(popToScenIndex,
						popToScenIndex2);
				this.setNScenariosIncludingDalys(transitionType.length);

			} else {
				for (int scen = 0; scen < nScenarios; scen++)
					numberOfDalyPopForThisScenario[scen] = -1;
			}
		}

		return nExtraPop;

	}

	/**
	 * gets number of simulated populations and sets all the indicator variables.
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
			this.thisScenarioUsedOneForAllPop = new boolean[this
					.getNScenarios()];
			boolean isAtLeastOneAllForOnePopulation = false;
			this.firstOneForAllPopScenario = -1;

			Arrays.fill(thisScenarioUsedOneForAllPop, false);
			if (this.getRiskType() != 2 && this.getNScenarios() > 0)
				for (int scennum = 0; scennum < this.getNScenarios(); scennum++) {
					/*
					 * NB is both prevalence and transitions are identical to
					 * the reference scenario (not very usefull, but users will
					 * try) this is assumed to be a one for all population,
					 * meaning that no extra simulations will be done
					 */
					if ((!this.getTransitionType()[scennum])) {
						this.nPopulations--; /*
											 * remove this scenario from number
											 * than need to be simulated
											 */

						isAtLeastOneAllForOnePopulation = true;
						thisScenarioUsedOneForAllPop[scennum] = true;
						if (this.firstOneForAllPopScenario == -1)
							this.firstOneForAllPopScenario = scennum;

					}
				}
			/* add the one-for-all-scenario that still needs to be simulated */
			if (isAtLeastOneAllForOnePopulation)
				this.nPopulations++;

			/* returns the scenario number belonging with a population */
			this.popToScenIndex = new int[this.nPopulations];
			this.isOneScenPopulation = new boolean[this.nPopulations];
			this.getPopToScenIndex()[0] = 0;
			this.getIsOneScenPopulation()[0] = false;
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

			/* 11-2011: added populations for DALY calculations */
			/*
			 * DALY scenario's are added only in the case of scenario's of
			 * !transitiontype In those cases, there are 2 possibilities:
			 * continous risk factor, or categorical/compound The latter use a
			 * one-for-all population, which means an extra one-for-all
			 * population The former have an extra population for each
			 * !transitiontype scenario
			 * 
			 * The DALY scenarios are added to the end of the scenario's DALY
			 * scenario's are always not transition types and initial prev types
			 */

			nPopulations += addDalyScenarios(nPopulations);
			return nPopulations;
		}

	}

	/**
	 * gets number of simulated populations before DALY scenarios are added.
	 *
	 * @return number of populations
	 */
	public int getNPopulationsWithoutDalys() {

		int nPopulations = this.getNScenarios() + 1;
		boolean isAtLeastOneAllForOnePopulation = false;
		if (this.getRiskType() != 2 && this.getNScenarios() > 0)
			for (int scennum = 0; scennum < this.getNScenarios(); scennum++) {
				/*
				 * NB is both prevalence and transitions are identical to the
				 * reference scenario (not very usefull, but users will try)
				 * this is assumed to be a one for all population, meaning that
				 * no extra simulations will be done
				 */
				if ((!this.getTransitionType()[scennum])) {
					nPopulations--; /*
									 * remove this scenario from number than
									 * need to be simulated
									 */
					isAtLeastOneAllForOnePopulation = true;

				}
			}
		/* add the one-for-all-scenario that still needs to be simulated */
		if (isAtLeastOneAllForOnePopulation)
			nPopulations++;

		return nPopulations;

	}

	/**
	 * Gets the transition matrix.
	 *
	 * @param scen the scen
	 * @return the transition matrix
	 */
	public float[][][][] getTransitionMatrix(int scen) {
		return DynamoLib.deepcopy(alternativeTransitionMatrix[scen]);
	}

	/**
	 * Sets the reference class.
	 *
	 * @param referenceClass the new reference class
	 */
	public void setReferenceClass(int referenceClass) {
		this.referenceClass = referenceClass;
	}

	/**
	 * Gets the reference class.
	 *
	 * @return the reference class
	 */
	public int getReferenceClass() {
		return referenceClass;
	}

	/**
	 * Sets the n scenarios.
	 *
	 * @param nScenarios the new n scenarios
	 */
	public void setNScenarios(int nScenarios) {
		this.nScenarios = nScenarios;
	}

	/**
	 * get number of alternative scenarios (reference scenario not included).
	 *
	 * @return the n scenarios
	 */
	public int getNScenarios() {
		return nScenarios;
	}

	/**
	 * Gets the n scenarios including dalys.
	 *
	 * @return the n scenarios including dalys
	 */
	public int getNScenariosIncludingDalys() {
		return nScenariosIncludingDalys;
	}

	/**
	 * Sets the n scenarios including dalys.
	 *
	 * @param nScen the new n scenarios including dalys
	 */
	public void setNScenariosIncludingDalys(int nScen) {
		this.nScenariosIncludingDalys = nScen;
	}

	/**
	 * Sets the initial prevalence type.
	 *
	 * @param initialPrevalenceType the new initial prevalence type
	 */
	public void setInitialPrevalenceType(boolean[] initialPrevalenceType) {
		this.initialPrevalenceType = initialPrevalenceType;
	}

	/**
	 * Gets the initial prevalence type.
	 *
	 * @return the initial prevalence type
	 */
	public boolean[] getInitialPrevalenceType() {
		return DynamoLib.deepcopy(initialPrevalenceType);
	}

	/**
	 * Sets the transition type.
	 *
	 * @param transitionType the new transition type
	 */
	public void setTransitionType(boolean[] transitionType) {
		this.transitionType = transitionType;
	}

	/**
	 * Gets the transition type.
	 *
	 * @return the transition type
	 */
	public boolean[] getTransitionType() {
		return DynamoLib.deepcopy(transitionType);
	}

	/**
	 * sets transitionType[i] to b.
	 *
	 * @param b            : value to set
	 * @param i            : index
	 */
	public void setTransitionType(boolean b, int i) {
		transitionType[i] = b;

	}

	/**
	 * gets transitionType[i].
	 *
	 * @param i            : index
	 * @return the transition type
	 */
	public boolean getTransitionType(int i) {
		return transitionType[i];

	}

	/**
	 * set InitialPrevalenceType[i] to b.
	 *
	 * @param b            : value to set
	 * @param i            : index
	 */
	public void setInitialPrevalenceType(boolean b, int i) {
		initialPrevalenceType[i] = b;
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the succesrate.
	 *
	 * @return the succesrate
	 */
	public float[] getSuccesrate() {
		return DynamoLib.deepcopy(this.succesrate);
	}

	/**
	 * gets succesrate as double.
	 *
	 * @return succesrate by scenario (double [])
	 */
	public double[] getDoubleSuccesrate() {
		double[] returnArray = new double[this.succesrate.length];
		for (int scen = 0; scen < this.succesrate.length; scen++)
			returnArray[scen] = this.succesrate[scen];

		return returnArray;
	}

	/**
	 * Sets the succesrate.
	 *
	 * @param succesrate the new succesrate
	 */
	public void setSuccesrate(float[] succesrate) {
		this.succesrate = new float[succesrate.length];
		for (int i = 0; i < succesrate.length; i++)
			this.succesrate[i] = succesrate[i];
	}

	/**
	 * Sets the succesrate.
	 *
	 * @param succesrate the succesrate
	 * @param i the i
	 */
	public void setSuccesrate(float succesrate, int i) {
		this.succesrate[i] = succesrate;
	}

	/**
	 * Gets the min age.
	 *
	 * @return the min age
	 */
	public int[] getMinAge() {
		int[] returnarray = new int[minAge.length];
		for (int i = 0; i < minAge.length; i++)
			returnarray[i] = (int) minAge[i];
		return returnarray;
	}

	/**
	 * Sets the min age.
	 *
	 * @param minAge the new min age
	 */
	public void setMinAge(float[] minAge) {
		this.minAge = minAge;
	}

	/**
	 * Sets the min age.
	 *
	 * @param minAge the min age
	 * @param i the i
	 */
	public void setMinAge(int minAge, int i) {
		this.minAge[i] = minAge;
	}

	/**
	 * Gets the max age.
	 *
	 * @return the max age
	 */
	public int[] getMaxAge() {
		int[] returnarray = new int[maxAge.length];
		for (int i = 0; i < maxAge.length; i++)
			returnarray[i] = (int) maxAge[i];
		return returnarray;

	}

	/**
	 * Sets the max age.
	 *
	 * @param maxAge the new max age
	 */
	public void setMaxAge(float[] maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * Sets the max age.
	 *
	 * @param maxAge the max age
	 * @param i the i
	 */
	public void setMaxAge(int maxAge, int i) {
		this.maxAge[i] = maxAge;
	}

	/**
	 * Gets the scenario names.
	 *
	 * @return the scenario names
	 */
	public String[] getScenarioNames() {
		return DynamoLib.deepcopy(scenarioNames);
	}

	/**
	 * Sets the scenario names.
	 *
	 * @param scenarioNames the new scenario names
	 */
	public void setScenarioNames(String[] scenarioNames) {
		this.scenarioNames = scenarioNames;
	}

	/**
	 * Sets the scenario names.
	 *
	 * @param scenarioNames the scenario names
	 * @param i the i
	 */
	public void setScenarioNames(String scenarioNames, int i) {
		this.scenarioNames[i] = scenarioNames;
	}

	/**
	 * Gets the structure.
	 *
	 * @return the structure
	 */
	public DiseaseClusterStructure[] getStructure() {
		return structure;
	}

	/**
	 * Sets the structure.
	 *
	 * @param structure the new structure
	 */
	public void setStructure(DiseaseClusterStructure[] structure) {
		this.structure = structure;
	}

	/**
	 * Sets the structure.
	 *
	 * @param structure the structure
	 * @param i the i
	 */
	public void setStructure(DiseaseClusterStructure structure, int i) {
		this.structure[i] = structure;
	}

	/**
	 * Gets the cutoffs.
	 *
	 * @return the cutoffs
	 */
	public float[] getCutoffs() {
		if (cutoffs == null)
			return cutoffs;
		else
			return DynamoLib.deepcopy(cutoffs);
	}

	/**
	 * Sets the cutoffs.
	 *
	 * @param cutoffs the new cutoffs
	 */
	public void setCutoffs(float[] cutoffs) {
		this.cutoffs = cutoffs;
	}

	/**
	 * Sets the risk type.
	 *
	 * @param riskType the new risk type
	 */
	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	/**
	 * Gets the risk type.
	 *
	 * @return the risk type
	 */
	public int getRiskType() {
		return riskType;
	}

	/**
	 * Sets the newborns.
	 *
	 * @param input the new newborns
	 */
	public void setNewborns(int[] input) {
		this.newborns = input;
	}

	/**
	 * Sets the newborns.
	 *
	 * @param input the input
	 * @param i the i
	 */
	public void setNewborns(int input, int i) {
		this.newborns[i] = input;
	}

	/**
	 * Gets the newborns.
	 *
	 * @return the newborns
	 */
	public int[] getNewborns() {
		return DynamoLib.deepcopy(newborns);
	}

	/**
	 * Sets the zero transition.
	 *
	 * @param zeroTransition the new zero transition
	 */
	public void setZeroTransition(boolean[] zeroTransition) {
		this.zeroTransition = zeroTransition;
	}

	/**
	 * Sets the zero transition.
	 *
	 * @param zeroTransition the zero transition
	 * @param i the i
	 */
	public void setZeroTransition(boolean zeroTransition, int i) {
		this.zeroTransition[i] = zeroTransition;
	}

	/**
	 * Checks if is zero transition.
	 *
	 * @return the boolean[]
	 */
	public boolean[] isZeroTransition() {
		return DynamoLib.deepcopy(zeroTransition);
	}

	/**
	 * Checks if is zero transition.
	 *
	 * @param i the i
	 * @return true, if is zero transition
	 */
	public boolean isZeroTransition(int i) {
		return zeroTransition[i];
	}

	/**
	 * Sets the old prevalence.
	 *
	 * @param oldPrevalence the new old prevalence
	 * @throws DynamoInconsistentDataException the dynamo inconsistent data exception
	 */
	public void setOldPrevalence(float[][][] oldPrevalence)
			throws DynamoInconsistentDataException {
		this.oldPrevalence = checkedRates(oldPrevalence);
	}

	/*  aangepast in maart 2014 waarbij de aanpassing gelijk is gemaakt aan die  voor de oude prevalentie */
	/**
	 * Checked rates.
	 *
	 * @param prevalence the prevalence
	 * @return the float[][][]
	 * @throws DynamoInconsistentDataException the dynamo inconsistent data exception
	 */
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
	
	
	
	
	
	
	

	/**
	 * Gets the old prevalence.
	 *
	 * @return the old prevalence
	 */
	public float[][][] getOldPrevalence() {
		return DynamoLib.deepcopy(oldPrevalence);
	}

	/**
	 * Sets the alternative transition matrix.
	 *
	 * @param alternativeTransitionMatrix the new alternative transition matrix
	 */
	public void setAlternativeTransitionMatrix(
			float[][][][][] alternativeTransitionMatrix) {
		this.alternativeTransitionMatrix = alternativeTransitionMatrix;
	}

	/**
	 * Sets the alternative transition matrix.
	 *
	 * @param scenNumber the scen number
	 * @param age the age
	 * @param sex the sex
	 * @param alternativeTransitionMatrix the alternative transition matrix
	 */
	public void setAlternativeTransitionMatrix(int scenNumber, int age,
			int sex, float[][] alternativeTransitionMatrix) {
		if (this.alternativeTransitionMatrix[scenNumber] == null)
			this.alternativeTransitionMatrix[scenNumber] = new float[96][2][][];
		this.alternativeTransitionMatrix[scenNumber][age][sex] = alternativeTransitionMatrix;
	}

	/**
	 * Sets the alternative transition matrix.
	 *
	 * @param alternativeTransitionMatrix the alternative transition matrix
	 * @param scen the scen
	 * @throws DynamoInconsistentDataException the dynamo inconsistent data exception
	 */
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

	/**
	 * Gets the alternative transition matrix.
	 *
	 * @return the alternative transition matrix
	 */
	public float[][][][][] getAlternativeTransitionMatrix() {
		return alternativeTransitionMatrix;
	}

	/**
	 * Sets the in men.
	 *
	 * @param inMen the new in men
	 */
	public void setInMen(boolean[] inMen) {
		this.inMen = inMen;
	}

	/**
	 * Sets the in men.
	 *
	 * @param inMen the in men
	 * @param i the i
	 */
	public void setInMen(boolean inMen, int i) {
		this.inMen[i] = inMen;
	}

	/**
	 * Gets the in men.
	 *
	 * @return the in men
	 */
	public boolean[] getInMen() {
		boolean[] returnArray = new boolean[inMen.length];
		for (int i = 0; i < inMen.length; i++)
			returnArray[i] = inMen[i];
		return returnArray;
	}

	/**
	 * Sets the in women.
	 *
	 * @param inWomen the new in women
	 */
	public void setInWomen(boolean[] inWomen) {
		this.inWomen = inWomen;
	}

	/**
	 * Sets the in women.
	 *
	 * @param inWomen the in women
	 * @param i the i
	 */
	public void setInWomen(boolean inWomen, int i) {
		this.inWomen[i] = inWomen;
	}

	/**
	 * Gets the in women.
	 *
	 * @return the in women
	 */
	public boolean[] getInWomen() {
		boolean[] returnArray = new boolean[inWomen.length];
		for (int i = 0; i < inWomen.length; i++)
			returnArray[i] = inWomen[i];
		return returnArray;
	}

	/**
	 * Sets the population size.
	 *
	 * @param populationSize the new population size
	 */
	public void setPopulationSize(float[][] populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * Gets the population size.
	 *
	 * @return the population size
	 */
	public float[][] getPopulationSize() {
		return populationSize;
	}

	/**
	 * Sets the male female ratio.
	 *
	 * @param maleFemaleRatio the new male female ratio
	 */
	public void setMaleFemaleRatio(float maleFemaleRatio) {
		this.maleFemaleRatio = maleFemaleRatio;
	}

	/**
	 * Gets the male female ratio.
	 *
	 * @return the male female ratio
	 */
	public float getMaleFemaleRatio() {
		return maleFemaleRatio;
	}

	/**
	 * Sets the risk classnames.
	 *
	 * @param riskClassnames the new risk classnames
	 */
	public void setRiskClassnames(String[] riskClassnames) {
		this.riskClassnames = riskClassnames;
	}

	/**
	 * Gets the risk classnames.
	 *
	 * @return the risk classnames
	 */
	public String[] getRiskClassnames() {
		return riskClassnames;
	}

	/**
	 * Sets the start year.
	 *
	 * @param startYear the new start year
	 */
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	/**
	 * Gets the start year.
	 *
	 * @return the start year
	 */
	public int getStartYear() {
		return startYear;
	}

	/**
	 * Sets the years in run.
	 *
	 * @param yearsInRun the new years in run
	 */
	public void setYearsInRun(int yearsInRun) {
		this.yearsInRun = yearsInRun;
	}

	/**
	 * Gets the years in run.
	 *
	 * @return the years in run
	 */
	public int getYearsInRun() {
		return yearsInRun;
	}

	/**
	 * Sets the mean drift.
	 *
	 * @param drift the new mean drift
	 */
	public void setMeanDrift(float[][][] drift) {
		this.alternativeMeanDrift = drift;
	}

	/**
	 * Sets the mean drift.
	 *
	 * @param drift the drift
	 * @param scen the scen
	 */
	public void setMeanDrift(float[][] drift, int scen) {
		if (this.alternativeMeanDrift == null)
			this.alternativeMeanDrift = new float[this.nScenarios][][];
		this.alternativeMeanDrift[scen] = drift;
	}

	/**
	 * Gets the mean drift.
	 *
	 * @return the mean drift
	 */
	public float[][][] getMeanDrift() {
		return alternativeMeanDrift;
	}

	/**
	 * Gets the mean drift.
	 *
	 * @param i the i
	 * @return the mean drift
	 */
	public float[][] getMeanDrift(int i) {
		return alternativeMeanDrift[i];
	}

	/**
	 * Sets the new prevalence.
	 *
	 * @param newPrevalence the new new prevalence
	 */
	public void setNewPrevalence(float[][][][] newPrevalence) {
		this.newPrevalence = newPrevalence;
	}

	/**
	 * Sets the new prevalence.
	 *
	 * @param inPrevalence the in prevalence
	 * @param i the i
	 * @throws DynamoInconsistentDataException the dynamo inconsistent data exception
	 */
	public void setNewPrevalence(float[][][] inPrevalence, int i)
			throws DynamoInconsistentDataException {

		this.newPrevalence[i] = new float[inPrevalence.length][inPrevalence[0].length][inPrevalence[0][0].length];

		for (int k = 0; k < inPrevalence.length; k++)
			for (int l = 0; l < inPrevalence[0].length; l++)
				for (int j = 0; j < inPrevalence[0][0].length; j++)
					this.newPrevalence[i][k][l][j] = inPrevalence[k][l][j];
		this.newPrevalence[i] = checkedRates(this.newPrevalence[i]);
	}

	/**
	 * Checks if is with initial change.
	 *
	 * @return true, if is with initial change
	 */
	public boolean isWithInitialChange() {
		boolean returnvalue = false;
		for (int i = 0; i < initialPrevalenceType.length; i++)
			if (initialPrevalenceType[i])
				returnvalue = true;
		return returnvalue;
	}

	/**
	 * N with initial change.
	 *
	 * @return the int
	 */
	public int nWithInitialChange() {
		int returnvalue = 0;
		for (int i = 0; i < initialPrevalenceType.length; i++)
			if (initialPrevalenceType[i])
				returnvalue++;
		return returnvalue;
	}

	/**
	 * Gets the n transtion scenarios.
	 *
	 * @return the n transtion scenarios
	 */
	public int getNTranstionScenarios() {
		int returnvalue = 0;
		for (int i = 0; i < transitionType.length; i++)
			if (transitionType[i])
				returnvalue++;
		return returnvalue;
	}

	/**
	 * Gets the new prevalence.
	 *
	 * @return the new prevalence
	 */
	public float[][][][] getNewPrevalence() {
		return newPrevalence;
	}

	/**
	 * Gets the new prevalence.
	 *
	 * @param scen the scen
	 * @return the new prevalence
	 */
	public float[][][] getNewPrevalence(int scen) {
		return newPrevalence[scen];
	}

	/**
	 * this method take a mean, std and skewness and converts it to the
	 * parameters of a lognormal distribution (mu, sigma, offset) before it sets
	 * them as new parameters for the scenario;.
	 *
	 * @param inputMean            (float [scenario][age][sex]) * @param inputSTD (float
	 *            [scenario][age][sex]) * @param inputSkew (float
	 *            [scenario][age][sex])
	 * @param inputSTD the input std
	 * @param inputSkew the input skew
	 * @param scen            : number of scenario;
	 * @throws DynamoInconsistentDataException the dynamo inconsistent data exception
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
	 * Gets the new mean.
	 *
	 * @return the new mean
	 */

	public float[][][] getNewMean() {
		return newMean;
	}

	/**
	 * Gets the new std.
	 *
	 * @return the new std
	 */
	public float[][][] getNewStd() {
		return newStd;
	}

	/**
	 * Gets the new offset.
	 *
	 * @return the new offset
	 */
	public float[][][] getNewOffset() {
		return newOffset;
	}

	/**
	 * Gets the new offset.
	 *
	 * @param scen the scen
	 * @return the new offset
	 */
	public float[][] getNewOffset(int scen) {
		return newOffset[scen];
	}

	/**
	 * Sets the new mean.
	 *
	 * @param newMean the new new mean
	 */
	public void setNewMean(float[][][] newMean) {
		this.newMean = newMean;
	}

	/**
	 * Sets the new std.
	 *
	 * @param newStd the new new std
	 */
	public void setNewStd(float[][][] newStd) {
		this.newStd = newStd;
	}

	/**
	 * Sets the new offset.
	 *
	 * @param newOffset the new new offset
	 */
	public void setNewOffset(float[][][] newOffset) {
		this.newOffset = newOffset;
	}

	/**
	 * Gets the checks if is normal.
	 *
	 * @return the checks if is normal
	 */
	public boolean[] getIsNormal() {
		return isNormal;
	}

	/**
	 * Sets the checks if is normal.
	 *
	 * @param isNormal the new checks if is normal
	 */
	public void setIsNormal(boolean[] isNormal) {
		this.isNormal = isNormal;
	}

	/**
	 * Checks if is with new borns.
	 *
	 * @return true, if is with new borns
	 */
	public boolean isWithNewBorns() {
		return withNewBorns;
	}

	/**
	 * Sets the with new borns.
	 *
	 * @param withNewBorns the new with new borns
	 */
	public void setWithNewBorns(boolean withNewBorns) {
		this.withNewBorns = withNewBorns;
	}

	/**
	 * Gets the stepsize.
	 *
	 * @return the stepsize
	 */
	public float getStepsize() {
		return stepsize;
	}

	/**
	 * Sets the stepsize.
	 *
	 * @param stepsize the new stepsize
	 */
	public void setStepsize(float stepsize) {
		this.stepsize = stepsize;
	}

	/**
	 * Gets the sim pop size.
	 *
	 * @return the sim pop size
	 */
	public int getSimPopSize() {
		return simPopSize;
	}

	/**
	 * Sets the sim pop size.
	 *
	 * @param simPopSize the new sim pop size
	 */
	public void setSimPopSize(int simPopSize) {
		this.simPopSize = simPopSize;
	}

	/**
	 * Gets the max sim age.
	 *
	 * @return the max sim age
	 */
	public int getMaxSimAge() {
		return maxSimAge;
	}

	/**
	 * Sets the max sim age.
	 *
	 * @param maxSimAge the new max sim age
	 */
	public void setMaxSimAge(int maxSimAge) {
		this.maxSimAge = maxSimAge;
	}

	/**
	 * Gets the min sim age.
	 *
	 * @return the min sim age
	 */
	public int getMinSimAge() {
		return minSimAge;
	}

	/**
	 * Sets the min sim age.
	 *
	 * @param minSimAge the new min sim age
	 */
	public void setMinSimAge(int minSimAge) {
		this.minSimAge = minSimAge;
	}

	/**
	 * Gets the random seed.
	 *
	 * @return the random seed
	 */
	public long getRandomSeed() {
		return randomSeed;
	}

	/**
	 * Sets the random seed.
	 *
	 * @param randomSeed the new random seed
	 */
	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	/**
	 * Sets the random seed.
	 *
	 * @param randomSeed the new random seed
	 */
	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	/**
	 * Gets the start year newborns.
	 *
	 * @return the start year newborns
	 */
	public int getStartYearNewborns() {
		return startYearNewborns;
	}

	/**
	 * Sets the start year newborns.
	 *
	 * @param startYearNewborns the new start year newborns
	 */
	public void setStartYearNewborns(int startYearNewborns) {
		this.startYearNewborns = startYearNewborns;
	}

	/**
	 * Gets the zero transition.
	 *
	 * @return the zero transition
	 */
	public boolean[] getZeroTransition() {
		return zeroTransition;
	}

	/**
	 * Gets the old duration classes.
	 *
	 * @return the old duration classes
	 */
	public float[][][] getOldDurationClasses() {

		return oldDurationClasses;
	}

	/**
	 * Sets the old duration classes.
	 *
	 * @param oldDurationClasses the new old duration classes
	 */
	public void setOldDurationClasses(float[][][] oldDurationClasses) {
		this.oldDurationClasses = oldDurationClasses;
	}

	/**
	 * Gets the index duration class.
	 *
	 * @return the index duration class
	 */
	public int getIndexDurationClass() {
		return indexDurationClass;
	}

	/**
	 * Sets the index duration class.
	 *
	 * @param indexDurationClass the new index duration class
	 */
	public void setIndexDurationClass(int indexDurationClass) {
		this.indexDurationClass = indexDurationClass;
	}

	/**
	 * Gets the baseline ability.
	 *
	 * @return the baseline ability
	 */
	public double[][] getBaselineAbility() {
		return DynamoLib.deepcopy(baselineAbility);
	}

	/**
	 * Sets the baseline ability.
	 *
	 * @param ds the new baseline ability
	 */
	public void setBaselineAbility(double[][] ds) {
		this.baselineAbility = ds;
	}

	/**
	 * Gets the disease ability.
	 *
	 * @return the disease ability
	 */
	public double[][][] getDiseaseAbility() {
		return DynamoLib.deepcopy(diseaseAbility);
	}

	/**
	 * Sets the disease ability.
	 *
	 * @param ds the new disease ability
	 */
	public void setDiseaseAbility(double[][][] ds) {
		this.diseaseAbility = ds;
	}

	/**
	 * Gets the rel risk ability cat.
	 *
	 * @return the rel risk ability cat
	 */
	public double[][][] getRelRiskAbilityCat() {
		return DynamoLib.deepcopy(relRiskAbilityCat);
	}

	/**
	 * Sets the rel risk ability cat.
	 *
	 * @param ds the new rel risk ability cat
	 */
	public void setRelRiskAbilityCat(double[][][] ds) {
		this.relRiskAbilityCat = ds;
	}

	/**
	 * Gets the rel risk ability cont.
	 *
	 * @return the rel risk ability cont
	 */
	public double[][] getRelRiskAbilityCont() {
		return DynamoLib.deepcopy(relRiskAbilityCont);
	}

	/**
	 * Sets the rel risk ability cont.
	 *
	 * @param ds the new rel risk ability cont
	 */
	public void setRelRiskAbilityCont(double[][] ds) {
		this.relRiskAbilityCont = ds;
	}

	/**
	 * Gets the rel risk ability begin.
	 *
	 * @return the rel risk ability begin
	 */
	public double[][] getRelRiskAbilityBegin() {
		return DynamoLib.deepcopy(relRiskAbilityBegin);
	}

	/**
	 * Sets the rel risk ability begin.
	 *
	 * @param ds the new rel risk ability begin
	 */
	public void setRelRiskAbilityBegin(double[][] ds) {
		this.relRiskAbilityBegin = ds;
	}

	/**
	 * Gets the rel risk ability end.
	 *
	 * @return the rel risk ability end
	 */
	public double[][] getRelRiskAbilityEnd() {
		return DynamoLib.deepcopy(relRiskAbilityEnd);
	}

	/**
	 * Sets the rel risk ability end.
	 *
	 * @param ds the new rel risk ability end
	 */
	public void setRelRiskAbilityEnd(double[][] ds) {
		this.relRiskAbilityEnd = ds;
	}

	/**
	 * Gets the alfa ability.
	 *
	 * @return the alfa ability
	 */
	public double[][] getAlfaAbility() {
		return DynamoLib.deepcopy(alfaAbility);
	}

	/**
	 * Sets the alpha ability.
	 *
	 * @param ds the new alpha ability
	 */
	public void setAlphaAbility(double[][] ds) {
		this.alfaAbility = ds;
	}

	/**
	 * Gets the newborn start year.
	 *
	 * @return the newborn start year
	 */
	public int getNewbornStartYear() {
		return newbornStartYear;
	}

	/**
	 * Sets the newborn start year.
	 *
	 * @param newbornStartYear the new newborn start year
	 */
	public void setNewbornStartYear(int newbornStartYear) {
		this.newbornStartYear = newbornStartYear;
	}

	/**
	 * Gets the checks if is one scen population.
	 *
	 * @return the checks if is one scen population
	 */
	public boolean[] getIsOneScenPopulation() {
		if (this.firstOneForAllPopScenario == -3)
			this.getNPopulations();
		return isOneScenPopulation;
	}

	/**
	 * Gets the pop to scen index.
	 *
	 * @return the pop to scen index
	 */
	public int[] getPopToScenIndex() {
		if (this.firstOneForAllPopScenario == -3)
			this.getNPopulations();
		return popToScenIndex;
	}

	/**
	 * Sets the first one for all daly pop.
	 *
	 * @param firstOneForAllDalyPop the new first one for all daly pop
	 */
	public void setFirstOneForAllDalyPop(int firstOneForAllDalyPop) {
		this.firstOneForAllDalyPop = firstOneForAllDalyPop;
	}

	/**
	 * Gets the first one for all daly pop.
	 *
	 * @return the first one for all daly pop
	 */
	public int getFirstOneForAllDalyPop() {
		return firstOneForAllDalyPop;
	}

	/**
	 * Sets the ref scen name.
	 *
	 * @param inputString the new ref scen name
	 */
	public void setRefScenName(String inputString) {
		this.refScenName=inputString;
		
	}

}
