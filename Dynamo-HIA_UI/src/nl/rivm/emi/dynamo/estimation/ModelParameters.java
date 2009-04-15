package nl.rivm.emi.dynamo.estimation;

import java.util.Arrays;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import Jama.Matrix;

/**
 * @author Hendriek Boshuizen. ModelParameters estimates and holds the model
 *         parameters method estimateModelParameters(String name_of_simulation)
 *         is called when activating the button "estimate parameters" of the
 *         DYNAMO model (or "run" when no parameters have been estimated
 *         previously) it 1. takes the information in the directory indicated by
 *         dir to collect all the input information needed 2. uses this to
 *         estimate the model parameters 3. write xml files needed by the
 *         simulation module 4. write the initial population file 5. writes a
 *         population of newborns
 * 
 * 
 */

public class ModelParameters {

	// Fields containing the estimated model parameters and other info needed to
	// run the model
	Log log = LogFactory.getLog(getClass().getName());
	private int nSim = 100;
	private int riskType = -1;
    private int nWarningsDisability=0;
    private int nWarningsMort=0;
    
	private String RiskTypeDistribution = null;
	private int durationClass = -1;
	private float refClassCont = -1;
	private int nCluster = -1;
	private DiseaseClusterStructure[] clusterStructure;
	/* relRiskDiseaseOnDisease[][][][][] : third index = cluster nr */
	private float relRiskDiseaseOnDisease[][][][][] = new float[96][2][][][];
	private float baselineIncidence[][][] = new float[96][2][];;
	private float baselineFatalIncidence[][][] = new float[96][2][];;
	// private float curedFraction[][][] = new float[96][2][];;
	private double baselinePrevalenceOdds[][][] = new double[96][2][];;
	private float relRiskOtherMort[][][] = new float[96][2][];;
	/*
	 * relative risk for other cause mortality relRiskOtherMort
	 */
	private float relRiskOtherMortCont[][] = new float[96][2];
	private float relRiskOtherMortEnd[][] = new float[96][2];
	private float relRiskOtherMortBegin[][] = new float[96][2];
	private float alfaOtherMort[][] = new float[96][2];
	private float baselineOtherMortality[][] = new float[96][2];
	private float baselineMortality[][] = new float[96][2]; // geen setter maken
	private float[] attributableMortality[][] = new float[96][2][];
	private float[][] relRiskClass[][] = new float[96][2][][];
	// here the third index is rc {risk factor class}, and the fourth d
	// (disease);
	private float[][][] relRiskContinue = new float[96][2][];
	private float prevRisk[][][] = new float[96][2][];;
	private float[][] meanRisk = new float[96][2];
	private float[][] stdDevRisk = new float[96][2];
	private float[][] offsetRisk = new float[96][2];
	/*
	 * disease Ability is defined as 1 - fraction with disability (due to the
	 * diseases) It can also contain the daly weight (where 1 = perfect health,
	 * 0= like being death
	 */
	private float[][][] diseaseAbility = new float[96][2][];
	private float[][] baselineAbility = new float[96][2];
	/* for disability we do not have a duration option */
	private float[][][] riskFactorDisabilityRRcat = new float[96][2][];
	private float[][] riskFactorDisabilityRRcont = new float[96][2];
	private float[][] riskFactorDisabilityRRend = new float[96][2];
	private float[][] riskFactorDisabilityRRbegin = new float[96][2];
	private float[][] riskFactorDisabilityalfa = new float[96][2];

	private float[][][] relRiskDuurBegin = new float[96][2][];
	private float[][][] relRiskDuurEnd = new float[96][2][];
	private float[][][] alfaDuur = new float[96][2][];
	private float[][][] duurFreq = new float[96][2][];
	private float[][] meanDrift = new float[96][2];
	private float[][] stdDrift = new float[96][2];
	private float[][] offsetDrift = new float[96][2];
	private float[][][][] transitionMatrix = new float[96][2][][];
	private boolean zeroTransition;
	// TODO
	private Population[] initialPopulation;
	// empty Constructor
	private Shell parentShell;
	private String globalBaseDir;
	

	/**
	 * contructor sets the baseDir;
	 * 
	 * @param baseDir
	 */
	public ModelParameters(String baseDir) {
		this.globalBaseDir = baseDir;
	}

	/**
	 * 
	 * @param nSim
	 *            number of simulated subjects used in the parameter estimation
	 *            in case of continuous or compound risk factors
	 * @param inputData
	 *            Object that holds the input data
	 * 
	 * @param parentShell
	 *            : the shell that is the parent of the progress bar
	 * 
	 * 
	 * @returns ScenarioInfo: an object containing information that is needed
	 *          for postprocessing
	 * @throws DynamoInconsistentDataException
	 */
	public void estimateModelParameters(int nSim, InputData inputData,
			Shell parentShell) throws DynamoInconsistentDataException {

		// first initialize the fields that can be directly copied from the
		// input data
		// make rr=1 for the continuous variable if the risk factor is
		// categorical
		// make rr=1 for the class variable if the risk factor is continuous
		/* first copy directly */
		/*
		 * NB this is not very safe, as copying like this means that changing
		 * the object here will change it also in inputData So only possible for
		 * primitive types TODO check if everywhere are deep copy for arrays
		 */
		this.riskType = inputData.getRiskType();
		this.RiskTypeDistribution = inputData.getRiskDistribution();
		this.refClassCont = inputData.getRefClassCont();
		if (this.riskType != 2)
			this.prevRisk = inputData.getPrevRisk();
		if (this.riskType == 3)
			this.duurFreq = inputData.getDuurFreq();
		if (this.RiskTypeDistribution == "Normal") {
			this.meanRisk = inputData.getMeanRisk();
			this.stdDevRisk = inputData.getStdDevRisk();
			this.zeroTransition = (inputData.getTransType() == 0);
			this.offsetRisk = null;
		} else {

			/*
			 * NB: same calculation is in the setMeanSTD method of scenarioInfo,
			 * so if there are errors here they should also be corrected there
			 */
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++) {

					try {
						float skew = inputData.getSkewnessRisk()[a][g];
						this.stdDevRisk[a][g] = (float) DynamoLib
								.findSigma(skew);

						this.meanRisk[a][g] = (float) (0.5 * (Math.log(skew
								* skew)
								- Math.log(Math.exp(this.stdDevRisk[a][g]
										* stdDevRisk[a][g]) - 1) - this.stdDevRisk[a][g]
								* this.stdDevRisk[a][g]));
						this.offsetRisk[a][g] = (float) (inputData
								.getMeanRisk()[a][g] - Math
								.exp(this.meanRisk[a][g] + 0.5
										* this.stdDevRisk[a][g]
										* this.stdDevRisk[a][g]));
					} catch (Exception e) {

						this.log
								.fatal("skewness of lognormal variable "
										+ "has a value that is not possible for a lognormal distribution  "
										+ " at age " + a + " and gender " + g
										+ ". Problematic skewness = "
										+ inputData.getSkewnessRisk()[a][g]);
						e.printStackTrace();
						throw new DynamoInconsistentDataException(
								"skewness of lognormal variable "
										+ "has a value that is not possible for a lognormal distribution  "
										+ " at age " + a + " and gender " + g
										+ ". Problematic skewness = "
										+ inputData.getSkewnessRisk()[a][g]);

					}
				}

		}
		;
		this.parentShell = parentShell;
		this.nCluster = inputData.getNCluster();
		this.clusterStructure = inputData.clusterStructure;
		this.durationClass = inputData.getIndexDuurClass();
		int nRiskClasses;
		if (this.riskType != 2)
			nRiskClasses = inputData.getPrevRisk()[0][0].length;
		else
			nRiskClasses = 1;
		this.log.fatal("before split");
		splitCuredDiseases(inputData);
		this.log.fatal("after split");
		if (inputData.getRiskType() != 2)
			this.transitionMatrix = new float[96][2][nRiskClasses][inputData
					.getPrevRisk()[0][0].length];
		NettTransitionRateFactory factory = new NettTransitionRateFactory();
		/* set up progress bar for this part of the calculations */

		Shell shell = new Shell(parentShell);
		shell.setText("Parameter estimation in progress .......");
		shell.setLayout(new FillLayout());
		shell.setSize(600, 50);

		ProgressBar bar = new ProgressBar(shell, SWT.NULL);
		bar.setBounds(10, 10, 200, 32);
		bar.setMinimum(0);

		shell.open();
		bar.setMaximum(100);

		for (int a = 0; a < 96; a++) {
			bar.setSelection(a);

			for (int g = 0; g < 2; g++) {
				this.log.debug("before first estimate");
				estimateModelParametersForSingleAgeGroup(nSim, inputData, a, g);
				this.log.debug("parameters estimated for age " + a
						+ " and gender " + g);

				int anext = a + 1;
				if (a == 95)
					anext = 95;

				// TODO
				/*
				 * nog testen
				 */
				if (inputData.getRiskType() != 2) {
					if (inputData.getTransType() == 1) { /*
														 * nett transition rates
														 */
						this.transitionMatrix[a][g] = NettTransitionRateFactory
								.makeNettTransitionRates(getPrevRisk()[a][g],
										inputData.getPrevRisk()[anext][g],
										this.baselineMortality[a][g], inputData
												.getRelRiskMortCat()[a][g]);
					} else if (inputData.getTransType() == 2)
						this.transitionMatrix[a][g] = inputData
								.getTransitionMatrix(a, g);
					else if (inputData.getTransType() == 1)
						this.transitionMatrix[a][g] = inputData
								.getTransitionMatrix(a, g);
					else if (inputData.getTransType() == 0) {
						/*
						 * this matrix is not really used, but implemented all
						 * the same in case a future programmer needs it
						 */
						float mat[][] = new float[nRiskClasses][nRiskClasses];
						for (int r1 = 0; r1 < nRiskClasses; r1++)
							for (int r2 = 0; r2 < nRiskClasses; r2++) {
								if (r1 == r2)
									mat[r1][r2] = 1;
								else
									mat[r1][r2] = 0;
							}
						this.transitionMatrix[a][g] = mat;

					}
				}
			}
		}

		bar.setSelection(96);
		if (inputData.getRiskType() == 2) {
			float drift[][][] = new float[3][96][2];
			if (inputData.getTransType() == 2) { /* inputted rates */
				drift = factory.makeUserGivenTransitionRates(inputData
						.getMeanRisk(), inputData.getStdDevRisk(), inputData
						.getSkewnessRisk(), this.baselineMortality, inputData
						.getRelRiskMortCont(), this.refClassCont, inputData
						.getMeanDrift());
			}
			if (inputData.getTransType() == 1) /* netto transitionrates */{
				drift = factory.makeNettTransitionRates(
						inputData.getMeanRisk(), inputData.getStdDevRisk(),
						inputData.getSkewnessRisk(), this.baselineMortality,
						inputData.getRelRiskMortCont(), this.refClassCont);
			}
			this.meanDrift = drift[0];
			this.stdDrift = drift[1];
			this.offsetDrift = drift[2];

			if (inputData.getTransType() == 0) /*
												 * zero transitionrates; not
												 * really used but in case
												 * someone expects the data
												 */{
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++) {
						this.meanDrift[a][g] = 0;
						this.stdDrift[a][g] = 0;
						this.offsetDrift[a][g] = 0;
					}
			}
		}
		bar.setSelection(97);
		/*
		 * while (!shell.isDisposed ()) { if (!display.readAndDispatch ())
		 * display.sleep (); }
		 */
		shell.close();

	};

	/**
	 * 
	 * estimateModelParameters(simulationname) is the main method between the
	 * user-interface and the simulation-module (the SOR program) it directs all
	 * the other action. It orders the following actions: - read the xml files
	 * produced by the userinterface (using only the simulationname) - put them
	 * into objects InputData (data that are needed only for construction of
	 * model parameters) and ScenarioInfo (data that are needed also for
	 * postprocessing) - estimate modelparameters and put them in the current
	 * object ModelParameters - write initial population xml files - write
	 * newborn xml file - write all configuration xml files for the SOR module
	 * 
	 * @param simulationName
	 *            : name of the simulation (is used to find the directory of the
	 *            data and configuration files and the directory to write the
	 *            outputfiles too)
	 * @param parentShell
	 *            the parentShell to which the output windows are attached
	 * 
	 * @return ScenarioInfo: an object with information needed in postprocessing
	 * @throws DynamoInconsistentDataException
	 *             , indicating that the user should supply other data
	 * @throws DynamoConfigurationException
	 * 
	 *             both identical implementation showing that something got
	 *             wrong reading the user data if the user interface is
	 *             implemented well, this should not occur in practise, but
	 *             could occur when users enter data directly in XML files.
	 * 
	 */
	public ScenarioInfo estimateModelParameters(String simulationName,
			Shell parentShell) throws DynamoInconsistentDataException,
			DynamoConfigurationException {

		/** step 1: build input data from baseline directory */

		/**
		 * estimateModelParameters first takes the information in the directory
		 * indicated by dir to collect all the input information needed this is
		 * put in two objects: InputData and DynamoConfigurationData InputData
		 * contains all information needed for the parameter estimation
		 * DynamoConfigurationData contains the information from the main input
		 * screen and is needed for making the simulation files
		 * 
		 */

		// BaseDirectory B = BaseDirectory
		// .getInstance("c:\\");
		// String BaseDir = B.getBaseDir();
		InputDataFactory config = new InputDataFactory(simulationName,
				this.globalBaseDir);
		InputData inputData = new InputData();

		ScenarioInfo scenInfo = new ScenarioInfo();
		this.log.debug("overall configuration read");
		config
				.addPopulationInfoToInputData(simulationName, inputData,
						scenInfo);
		this.log.debug("population info added");
		config.addRiskFactorInfoToInputData(inputData, scenInfo);
		this.log.debug("risk factor info added");
		config.addDiseaseInfoToInputData(inputData, scenInfo);
		config.addScenarioInfoToScenarioData(simulationName, scenInfo);

		this.log.debug("disease info added");

		/** * 2. uses the inputdata to estimate the model parameters */
		estimateModelParameters(this.nSim, inputData, parentShell);
		/** * 3. write xml files needed by the simulation module */

		SimulationConfigurationFactory s = new SimulationConfigurationFactory(
				simulationName);
		s.manufactureSimulationConfigurationFile(this, scenInfo);
		this.log.debug("SimulationConfigurationFile written ");
		s.manufactureCharacteristicsConfigurationFile(this);
		this.log.debug("CharacteristicsConfigurationFile written ");
		s.manufactureUpdateRuleConfigurationFiles(this, scenInfo);
		this.log.debug("UpdateRuleConfigurationFile written ");

		/** * 4. write the initial population file for all scenarios */

		InitialPopulationFactory popFactory = new InitialPopulationFactory(
				this.globalBaseDir);
		int seed = config.getRandomSeed();
		int nSim = config.getSimPopSize();

		this.initialPopulation = popFactory.manufactureInitialPopulation(this,
				simulationName, nSim, seed, false, scenInfo);
		/*
		 * : obsolete: write popFactory.writeInitialPopulation(this, nSim,
		 * simulationName, seed, false, scenInfo);
		 */
		/** * 5. writes a population of newborns */

		if (scenInfo.isWithNewBorns()) {
			Population[] newborns = popFactory.manufactureInitialPopulation(
					this, simulationName, nSim, seed, true, scenInfo);

			/*
			 * : obsolete: write if (scenInfo.isWithNewBorns())
			 * popFactory.writeInitialPopulation(this, nSim, simulationName,
			 * seed, true, scenInfo);
			 */
			for (int p = 0; p < this.initialPopulation.length; p++) {
				newborns[p].addAll(this.initialPopulation[p]);
				this.initialPopulation[p] = newborns[p];
				// initialPopulation[p].addAll(newborns[p]);
			}
		}
		return scenInfo;

	}

	/**
	 * 
	 * The method splits a disease with a cured fraction in two separate
	 * diseases for simulation The first is the "cured" disease, the second the
	 * "not cured" disease.
	 * 
	 * @param inputData
	 *            : object with input data
	 * @throws DynamoInconsistentDataException
	 */
	public void splitCuredDiseases(InputData inputData)
			throws DynamoInconsistentDataException {

		int Nadded = 0;
		for (int c = 0; c < inputData.getNCluster(); c++) {
			if (inputData.clusterStructure[c].isWithCuredFraction()) {
				if (inputData.clusterStructure[c].getNInCluster() > 1)
					throw new DynamoInconsistentDataException(
							"Error for disease "
									+ inputData.clusterStructure[c]
											.getDiseaseName().get(0)
									+ ". Cured fraction only allowed in diseases not related to other diseases");

				inputData.clusterStructure[c]
						.setNInCluster(inputData.clusterStructure[c]
								.getNInCluster() + 1);
				int number = inputData.clusterStructure[c].getDiseaseNumber()[0];
				inputData.clusterStructure[c].setDiseaseNumber(new int[2]);

				inputData.clusterStructure[c].setDiseaseNumber(number + Nadded,
						0);
				inputData.clusterStructure[c].setDiseaseNumber(number + Nadded
						+ 1, 1);
				Nadded++;

				String name = inputData.clusterStructure[c].getDiseaseName()
						.get(0);
				inputData.clusterStructure[c]
						.setDependentDisease(new boolean[2]);
				inputData.clusterStructure[c].setDependentDisease(false, 0);
				inputData.clusterStructure[c].setDependentDisease(false, 1);
				inputData.clusterStructure[c]
						.setIndexIndependentDiseases(new int[2]);
				inputData.clusterStructure[c].setIndexIndependentDiseases(0, 0);
				inputData.clusterStructure[c].setIndexIndependentDiseases(1, 1);
				inputData.clusterStructure[c].setNIndep(2);

				DiseaseClusterStructure newStructure = inputData.clusterStructure[c];
				newStructure.setDiseaseName(name + "_cured", 0);
				newStructure.setDiseaseName(name + "_notcured", 1);
				inputData.setClusterStructure(newStructure, c);

				float[] incidence = new float[96];
				float[] prevalence = new float[96];
				float[] ability = new float[96];
				float[] casefat = new float[96];
				float[][] RRcat = null;
				float RRcont;
				float RRduurEnd;
				float RRduurBegin;
				float halftime;
				float[] curedFraction = new float[96];
				float[] totExcess = new float[96];
				/* put the data in newdata */
				DiseaseClusterData newdata;
				for (int g = 0; g < 2; g++) {
					for (int a = 0; a < 96; a++) {
						if (inputData.getClusterData()[a][g][c]
								.getCaseFatality()[0] > 0)
							throw new DynamoInconsistentDataException(
									"Error for disease "
											+ inputData.clusterStructure[c]
													.getDiseaseName().get(0)
											+ ". Both Cured Fraction and Acute Fatality in same disease not allowed");
						/*
						 * copy info on prevalence, incidence and mortality to
						 * one array containing info of all ages
						 */

						totExcess[a] = inputData.getClusterData()[a][g][c]
								.getExcessMortality()[0];
						incidence[a] = inputData.getClusterData()[a][g][c]
								.getIncidence()[0];

						curedFraction[a] = inputData.getClusterData()[a][g][c]
								.getCuredFraction()[0];
						ability[a] = inputData.getClusterData()[a][g][c]
								.getAbility()[0];
						casefat[a] = inputData.getClusterData()[a][g][c]
								.getCaseFatality()[0];
						/* excess mortality goes only to non-cured disease */
						newdata = inputData.getClusterData()[a][g][c];
						newdata.setExcessMortality(new float[2]);
						newdata.setExcessMortality(0, 0);
						newdata.setExcessMortality(totExcess[a], 1);
						/* incidence is split over the diseases */
						newdata.setIncidence(new float[2]);
						newdata
								.setIncidence(curedFraction[a] * incidence[a],
										0);
						newdata.setIncidence((1 - curedFraction[a])
								* incidence[a], 1);
						newdata.setAbility(new float[2]);
						newdata.setAbility(ability[a], 0);
						newdata.setAbility(ability[a], 1);

						newdata.setCaseFatality(new float[2]);
						newdata.setCaseFatality(casefat[a], 0);
						newdata.setCaseFatality(casefat[a], 1);

						/*
						 * 
						 * copy all info on relative risks to the array with
						 * length 2 in stead of 1
						 */
						halftime = inputData.getClusterData()[a][g][c]
								.getAlpha()[0];
						newdata.setRrAlpha(new float[2]);
						newdata.setRrAlpha(halftime, 0);
						newdata.setRrAlpha(halftime, 1);
						RRcat = inputData.getClusterData()[a][g][c]
								.getRelRiskCat();

						newdata.setRelRiskCat(new float[RRcat.length][2]);
						for (int cat = 0; cat < RRcat.length; cat++) {
							;
							newdata.setRelRiskCat(RRcat[cat][0], cat, 0);
							newdata.setRelRiskCat(RRcat[cat][0], cat, 1);
						}

						RRcont = inputData.getClusterData()[a][g][c]
								.getRelRiskCont()[0];
						newdata.setRelRiskCont(new float[2]);
						newdata.setRelRiskCont(RRcont, 0);
						newdata.setRelRiskCont(RRcont, 1);

						RRduurBegin = inputData.getClusterData()[a][g][c]
								.getRelRiskDuurBegin()[0];
						newdata.setRelRiskDuurBegin(new float[2]);
						newdata.setRelRiskDuurBegin(RRduurBegin, 0);
						newdata.setRelRiskDuurBegin(RRduurBegin, 1);

						RRduurEnd = inputData.getClusterData()[a][g][c]
								.getRelRiskDuurEnd()[0];
						newdata.setRelRiskDuurEnd(new float[2]);
						newdata.setRelRiskDuurEnd(RRduurEnd, 0);
						newdata.setRelRiskDuurEnd(RRduurEnd, 1);
						inputData.setClusterData(newdata, a, g, c);
						prevalence[a] = inputData.getClusterData()[a][g][c]
								.getPrevalence()[0];
					}
					/*
					 * 
					 * private double[] calculateNotCuredPrevFraction(double
					 * inc[], double prev[], double excessmort[], double[]
					 * curedFraction)
					 */
					double notCured[] = calculateNotCuredPrevalence(incidence,
							prevalence, totExcess, curedFraction);

					for (int a = 0; a < 96; a++) {
						newdata = inputData.getClusterData()[a][g][c];
						prevalence[a] = inputData.getClusterData()[a][g][c]
								.getPrevalence()[0];
						newdata.setPrevalence(new float[2]);
						newdata.setPrevalence(
								(float) (prevalence[a] - notCured[a]), 0);
						newdata.setPrevalence((float) notCured[a], 1);
						inputData.setClusterData(newdata, a, g, c);
					}

				}

			}// end if has CuredFraction
			else {
				DiseaseClusterStructure newStructure = inputData.clusterStructure[c];
				/* if diseases are split in two, the numbering should be adapted */
				for (int d = 0; d < inputData.clusterStructure[c]
						.getNInCluster(); d++)
					newStructure.setDiseaseNumber(inputData.clusterStructure[c]
							.getDiseaseNumber()[d]
							+ Nadded, d);
				inputData.setClusterStructure(newStructure, c);
			}

		}// end loop over clusters
		inputData.setNDisease(inputData.getNDisease() + Nadded);
	} // end method

	/**
	 * This method estimates the input parameters for a single age and sex
	 * group. Exempt are parameters that can only be estimated from data of more
	 * than one age group (nett transition rates; cured prevalence fractions)
	 * 
	 * @param nSim
	 *            number of simulated persons used to estimate the parameters in
	 *            case of continuous or compound risk factor
	 * @param inputData
	 *            object with inputdata
	 * @param age
	 * @param sex
	 * @throws DynamoInconsistentDataException
	 */
	public void estimateModelParametersForSingleAgeGroup(int nSim,
			InputData inputData, int age, int sex)
			throws DynamoInconsistentDataException {

		/**
		 * The parameter estimation proceeds using a simulated population that
		 * has a simulated risk factor distribution. Per age and gender group at
		 * least 100 persons are generated, in order to let the estimation have
		 * a minimal accuracy. The estimation proceeds in 5 phases. Phase 1. In
		 * the first phase of parameter estimation, values of the risk factors
		 * are randomly drawn for each person in the simulated population using
		 * a random generator. Based on these simulated values, for each person
		 * a relative risk is calculated for each disease. Baseline prevalence
		 * rates for the independent diseases then can be calculated from this
		 * by calculating the average relative risk and dividing the incidence
		 * or prevalence of this disease by the average relative risks
		 */

		int nRiskCat = 1;
		if (this.riskType != 2)
			nRiskCat = inputData.getPrevRisk()[0][0].length;
		int nDiseases = getNDiseases(inputData);

		/* now copy data directly from DiseaseClusterData to parameter fields */

		this.attributableMortality[age][sex] = new float[nDiseases];
		this.relRiskContinue[age][sex] = new float[nDiseases];
		this.relRiskClass[age][sex] = new float[nRiskCat][nDiseases];
		this.relRiskDuurBegin[age][sex] = new float[nDiseases];
		this.relRiskDuurEnd[age][sex] = new float[nDiseases];
		this.alfaDuur[age][sex] = new float[nDiseases];
		this.diseaseAbility[age][sex] = new float[nDiseases];
		boolean withRRmort=inputData
		.isWithRRForMortality();
		boolean withRRdisability=inputData
		.isWithRRForDisability();
		double log2 = Math.log(2.0); // keep outside loops to prevent
		// recalculation
		/* put prevalence also in a single array for easy access */
		float[][][] diseasePrevalence = new float[96][2][nDiseases];
		this.relRiskDiseaseOnDisease[age][sex] = new float[this.nCluster][][];
		
		float[] excessMortality = new float[nDiseases];
		for (int c = 0; c < this.nCluster; c++)
			for (int dc = 0; dc < this.clusterStructure[c].getNInCluster(); dc++) {
				int dNumber = this.clusterStructure[c].getDiseaseNumber()[dc];

				excessMortality[dNumber] = inputData.getClusterData()[age][sex][c]
						.getExcessMortality()[dc];
				this.diseaseAbility[age][sex][dNumber] = 1 - inputData
						.getClusterData()[age][sex][c].getAbility()[dc];
			}
		if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
			this.relRiskContinue[age][sex] = new float[nDiseases];
		if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
			this.refClassCont = 0;
		for (int c = 0; c < this.nCluster; c++) {

			this.relRiskDiseaseOnDisease[age][sex][c] = inputData
					.getClusterData()[age][sex][c].getRRdisExtended();

			for (int d = 0; d < inputData.clusterStructure[c].getNInCluster(); d++) {
				int dNumber = inputData.clusterStructure[c].getDiseaseNumber()[d];
				if (this.riskType == 3) {
					this.relRiskDuurEnd[age][sex][dNumber] = inputData
							.getClusterData()[age][sex][c].getRelRiskDuurEnd()[d];
					this.relRiskDuurBegin[age][sex][dNumber] = inputData
							.getClusterData()[age][sex][c]
							.getRelRiskDuurBegin()[d];
					this.alfaDuur[age][sex][dNumber] = inputData
							.getClusterData()[age][sex][c].getAlpha()[d];

				}
				diseasePrevalence[age][sex][this.clusterStructure[c]
						.getDiseaseNumber()[d]] = inputData.getClusterData()[age][sex][c]
						.getPrevalence()[d];

				if (inputData.getRiskType() == 1
						|| inputData.getRiskType() == 3)
					for (int i = 0; i < nRiskCat; i++)
						this.relRiskClass[age][sex][i][dNumber] = inputData
								.getClusterData()[age][sex][c].getRelRiskCat()[i][d];
				else
					this.relRiskClass[age][sex][0][dNumber] = 1;
				if (inputData.getRiskType() == 2) {

					this.relRiskClass[age][sex][0][dNumber] = 1;
				}
				if (inputData.getRiskType() == 2)
					this.relRiskContinue[age][sex][dNumber] = inputData
							.getClusterData()[age][sex][c].getRelRiskCont()[d];

				// can be changed into variable name of duration
				if (inputData.getRiskType() == 1
						|| inputData.getRiskType() == 3)
					this.relRiskContinue[age][sex][dNumber] = 1;

			}

		}

		// if not netto transition rates then: transitionMatrix=inputData....

		// make declarations for the other fields
		this.baselineIncidence[age][sex] = new float[nDiseases];
		this.baselineFatalIncidence[age][sex] = new float[nDiseases];
		this.baselinePrevalenceOdds[age][sex] = new double[nDiseases];
		this.relRiskOtherMort[age][sex] = new float[nRiskCat];
		this.riskFactorDisabilityRRcat[age][sex]=new float[nRiskCat];
		if (inputData.getRiskType() == 1)
			nSim = nRiskCat;
		if (inputData.getRiskType() == 3)
			nSim = nRiskCat + inputData.getDuurFreq()[age][sex].length - 1;
		if (inputData.getRiskType() == 2 && nSim < 1000)
			nSim = 1000;
		// help variables concerning all cause mortality
		this.baselineMortality[age][sex] = 0; // Baseline mortality

		// now the declation of the arrays that give data per simulated person

		// Disease info

		Morbidity[][] probComorbidity = new Morbidity[nSim][inputData
				.getNCluster()];
		// disease cluster c [second index?] for person i [first index]
		// ideas behind dependent diseases
		// there are clusters of dependent diseases
		// in each cluster there are dependent and independent diseases
		// there is a matrix of dimension n-dependent by n-independent that
		// contains the RR's

		double[][] probDisease = new double[nSim][nDiseases];
		// for person i and disease d gives the probability of the disease
		// this is more or less redundant as this info is also part of the
		// object probComorbidity,
		// but this info is needed first in order to calculate probComorbidity

		// risk factor info
		int riskclass[] = new int[nSim];// gives riskclass (=categorical
		// variable)number for each simulated
		// person i
		double riskfactor[] = new double[nSim];// gives riskfactor (=
		// continuous variable) for each
		// simulated person i
		
		
		float [] abilityFromRiskFactor = new float [nSim];
		float [] abilityFromDiseases = new float [nSim];
		double totalAbilityFromRiskFactor=0;
		
		if (inputData.getRiskType() == 3) {
			double checkSum = 0;

			/*
			 * despite trying this not only changes the duurFreq, but also the
			 * original version!
			 */
			for (int k = 0; k < inputData.getDuurFreq()[age][sex].length; k++) {

				checkSum += this.duurFreq[age][sex][k];
			}
			if (Math.abs(checkSum - 1) > 0.0001)
				throw new DynamoInconsistentDataException(
						"durations given for compound risk factor class do not sum to 100 but to "
								+ checkSum);
		}
		double weight[] = new double[nSim]; // weight for weighting the
		// prevalences
		if (inputData.getRiskType() == 1) {
			for (int i = 0; i < nSim; i++)
				weight[i] = inputData.getPrevRisk()[age][sex][i];
		}
		if (inputData.getRiskType() == 3) {
			int i = 0;
			for (int k = 0; k < nRiskCat; k++) {
				weight[i] = inputData.getPrevRisk()[age][sex][k];
				if (inputData.getIndexDuurClass() == k) {
					for (int j = 0; j < this.duurFreq[age][sex].length; j++) {
						weight[i] = inputData.getPrevRisk()[age][sex][k]
								* this.duurFreq[age][sex][j];
						i++;
					}
				} else
					i++;
			}
		}
		if (inputData.getRiskType() == 2)
			for (int i = 0; i < nSim; i++)
				weight[i] = 1.0 / nSim;
		// relative risks
		double[][] relRisk = new double[nSim][nDiseases];
		// relative risk for person i (first index) on the
		// disease d second index) from risk factors only
		// due to risk factors only (excluding the risks due to other diseases
		double[][] relRiskIncludingDisease = new double[nSim][nDiseases];
		// same as above, but now also including the risk from independent
		// diseases
		double relRiskMort[] = new double[nSim]; // relative risk for person
		// i on all cause
		// mortality

		/* first loop over all individuals in the estimating population */
		/* this gives a first estimator for the baseline prevalence rate */
		// initialize necessary sum-variables etc.
		/* also it calculates the disability based on the riskfactor status */

		{
			double[] sumRR = new double[nDiseases]; // sum
			// (index=disease)
			// over all RR's
			// due to
			// riskfactors/classes
			double sumRRm = 0; // sum over all RR's for all cause mortality due
			// to riskfactors/classes
			double relRiskMax[] = new double[nDiseases]; // maximum
			for (int d = 0; d < nDiseases; d++) {
				relRiskMax[d] = 0;
			}
			;

			// now the loop itself
			// index for cumulative probability of risk factor class
			for (int i = 0; i < nSim; i++) {
				/* first draw or initialize risk factors */

				if (inputData.getRiskType() == 1)

				{
					riskfactor[i] = 0;
					riskclass[i] = i;
				}

				if (inputData.getRiskType() == 2) {
					riskclass[i] = 0;
					if (this.RiskTypeDistribution == "Normal") {
						riskfactor[i] = this.meanRisk[age][sex]
								+ this.stdDevRisk[age][sex]
								* DynamoLib.normInv((i + 0.5) / nSim);
					} else if (this.RiskTypeDistribution == "LogNormal") {
						riskfactor[i] = DynamoLib.logNormInv2(
								((i + 0.5) / nSim),
								inputData.getSkewnessRisk()[age][sex],
								inputData.getMeanRisk()[age][sex], inputData
										.getStdDevRisk()[age][sex]);
					} else
						throw new DynamoInconsistentDataException(
								" unknown riskfactor distribution "
										+ inputData.riskDistribution);
				}
				if (inputData.getRiskType() == 3) {
					if (i < inputData.getIndexDuurClass()) {
						riskfactor[i] = 0;
						riskclass[i] = i;
					} else if (i >= inputData.getIndexDuurClass()
							&& i < inputData.getIndexDuurClass()
									+ this.duurFreq[age][sex].length) {
						riskfactor[i] = i - inputData.getIndexDuurClass();
						/**
						 * 
						 * duration class starts at 0: this is the first
						 * frequency that should be inputted
						 * 
						 */
						riskclass[i] = inputData.getIndexDuurClass();
					} else {
						riskfactor[i] = 0;
						riskclass[i] = i - this.duurFreq[age][sex].length + 1;
					}
				}
				// Calculate disability based on only the riskfactor
				
				// first calculate the relative risk for this person/group
				double relRiskDis=1;
				if (inputData.getRiskType() == 1 && withRRdisability)
					relRiskDis = inputData.getRRforDisabilityCat()[age][sex][i];
				if (inputData.getRiskType() == 2 && withRRdisability)
					
				relRiskDis = Math.pow(
						inputData.getRRforDisabilityCont()[age][sex],
						(riskfactor[i] - inputData.getRefClassCont()));

				if (inputData.getRiskType() == 3 && withRRdisability)

					relRiskDis = (inputData.getRRforDisabilityBegin()[age][sex] - inputData
							.getRRforDisabilityEnd()[age][sex])
							* Math
									.exp(-riskfactor[i]
											* inputData.getAlfaForDisability()[age][sex])
							+ inputData.getRRforDisabilityEnd()[age][sex];
				
				
				abilityFromRiskFactor[i] = (float)	 ( Math.exp(-relRiskDis));
				totalAbilityFromRiskFactor+=weight[i]
				             						* ( Math.exp(-relRiskDis));

				// Calculate relative risks based on only the riskfactor

				// loop over all clusters of diseases

				for (int d = 0; d < nDiseases; d++) {

					if (inputData.getRiskType() == 3) {
						if (riskclass[i] == inputData.getIndexDuurClass()) {

							relRisk[i][d] = (this.relRiskDuurBegin[age][sex][d] - this.relRiskDuurEnd[age][sex][d])
									* Math.exp(-riskfactor[i]
											* this.alfaDuur[age][sex][d])
									+ this.relRiskDuurEnd[age][sex][d];

							if (inputData.isWithRRForMortality())
								relRiskMort[i] = (inputData
										.getRelRiskDuurMortBegin()[age][sex] - inputData
										.getRelRiskDuurMortEnd()[age][sex])
										* Math
												.exp(-riskfactor[i]
														* inputData
																.getRrAlphaMort()[age][sex])
										+ inputData.getRelRiskDuurMortEnd()[age][sex];

						} else {
							relRisk[i][d] = this.relRiskClass[age][sex][riskclass[i]][d];

							if (inputData.isWithRRForMortality())
								relRiskMort[i] = inputData.getRelRiskMortCat()[age][sex][riskclass[i]];
						}
					} else

						relRisk[i][d] = Math.pow(
								this.relRiskContinue[age][sex][d],
								(riskfactor[i] - inputData.getRefClassCont()))
								* this.relRiskClass[age][sex][riskclass[i]][d];

					sumRR[d] += relRisk[i][d] * weight[i];
					if (relRiskMax[d] < relRisk[i][d])
						relRiskMax[d] = relRisk[i][d];

				}
				// calculate RR and sum of RR for mortality;
				if (inputData.getRiskType() == 3) {
					if (riskclass[i] == inputData.getIndexDuurClass()) {

						if (inputData.isWithRRForMortality())
							relRiskMort[i] = (inputData
									.getRelRiskDuurMortBegin()[age][sex] - inputData
									.getRelRiskDuurMortEnd()[age][sex])
									* Math
											.exp(-riskfactor[i]
													* inputData.getAlphaMort()[age][sex])
									+ inputData.getRelRiskDuurMortEnd()[age][sex];

					} else {

						if (inputData.isWithRRForMortality())
							relRiskMort[i] = inputData.getRelRiskMortCat()[age][sex][riskclass[i]];
					}
				} else if (inputData.isWithRRForMortality())
					relRiskMort[i] = inputData.getRelRiskMortCat()[age][sex][riskclass[i]]
							* Math.pow(
									inputData.getRelRiskMortCont()[age][sex],
									(riskfactor[i] - inputData
											.getRefClassCont()));
				if (inputData.isWithRRForMortality())
					sumRRm += relRiskMort[i] * weight[i];
				// sum of relRiskMort
				// over all
				// persons
				else
					relRiskMort[i] = 1; // if not RR for mortality, this is 1;
			} // end first loop over all individuals

			// calculate a first estimate of baseline prevalence for the
			// independent
			// diseases and baseline all cause mortality
			// the false indicates that this should be done for independent
			// diseases

			;

			calculateBaselinePrev(inputData, age, sex, sumRR, false);
			calculateBaselineFatalIncidence(inputData, age, sex, sumRR, false);
			if (inputData.isWithRRForMortality())
				this.baselineMortality[age][sex] = (float) (inputData
						.getMortTot()[age][sex] / sumRRm);
			else
				this.baselineMortality[age][sex] = (float) (inputData
						.getMortTot()[age][sex]);
		}

		/*
		 * now repeat loop 1 iteratively to estimate the baseline odds // loop
		 * over all diseases with the exception of cases where the prevalence ==
		 * 0; there the baseline odds stays 0
		 */
		for (int d = 0; d < nDiseases; d++) {
			int nIter = 0;
			double del = 100;
			if (diseasePrevalence[age][sex][d] == 0)
				del = 0;
			/*
			 * if disease prevalence == 0 do not do anything but keep baseline
			 * odds ==0
			 */
			double sumPrevCurrent = 0;
			double sumDerivativePrevCurrent = 0;
			while (del > 0.00001 && nIter < 10) {
				sumPrevCurrent = 0;
				sumDerivativePrevCurrent = 0;
				for (int i = 0; i < nSim; i++) {
					sumPrevCurrent += weight[i]
							* relRisk[i][d]
							* this.baselinePrevalenceOdds[age][sex][d]
							/ (1 + relRisk[i][d]
									* this.baselinePrevalenceOdds[age][sex][d]);
					sumDerivativePrevCurrent += weight[i]
							* relRisk[i][d]
							/ Math
									.pow(
											(1 + relRisk[i][d]
													* this.baselinePrevalenceOdds[age][sex][d]),
											2);
				}// end loop over all individuals
				double oldValue = this.baselinePrevalenceOdds[age][sex][d];
				this.baselinePrevalenceOdds[age][sex][d] = oldValue
						- (sumPrevCurrent - diseasePrevalence[age][sex][d])
						/ sumDerivativePrevCurrent;
				del = Math.abs(this.baselinePrevalenceOdds[age][sex][d]
						- oldValue);
				++nIter;
			}// end iterative procedure for disease
		} // end loop over diseases

		// //////////////////////////////////////////////einde first loop
		// /////////////////////////////
		if (age == 0 && sex == 0)
			this.log.debug("end loop 1");
		// second loop over persons //
		/**
		 * Phase 2. In the second stage of parameter estimation, the RRs are
		 * calculated for the dependent diseases using the probabilities on
		 * independent diseases calculated from the baseline prevalence rates as
		 * calculated in based in the first loop. The RRs are then used to
		 * calculate the baseline prevalence rates of the dependent diseases
		 * Also the prevalences of the independent diseases are used to
		 * calculate the mean relative risk for those persons not having the
		 * disease, and these are used to calculate the baseline incidence rates
		 * for the independent diseases
		 */

		// initialize necessary sum-variables etc.
		double[] sumRR = new double[nDiseases]; // sum
		// (index=disease)
		// over all RR's
		// due to
		// riskfactors/classes
		double[] sumRRinHealth = new double[nDiseases]; // sum
		// (index=disease)
		// over all RR's * (1-probability of disease)
		// due to
		// riskfactors/classes

		for (int i = 0; i < nSim; i++) {
			// calculate the probability of each independent disease
			// loop over all clusters and within the clusters over the
			// diseases
			// //
			for (int c = 0; c < inputData.getNCluster(); c++) {
				for (int dc = 0; dc < inputData.clusterStructure[c]
						.getNInCluster(); dc++) {
					int d = inputData.clusterStructure[c].getDiseaseNumber()[dc];
					if (!inputData.clusterStructure[c].getDependentDisease()[dc]) {
						// probability = baseline prevalence * RR
						probDisease[i][d] = this.baselinePrevalenceOdds[age][sex][d]
								* relRisk[i][d]
								/ (this.baselinePrevalenceOdds[age][sex][d]
										* relRisk[i][d] + 1);

						sumRRinHealth[d] += weight[i] * (1 - probDisease[i][d])
								* relRisk[i][d];

					}
				}

				int NInCluster = inputData.clusterStructure[c].getNInCluster();

				// now calculate the sum of RR for each dependent disease
				// loop over clusters and dependent diseases;

				if (inputData.clusterStructure[c].getNInCluster() > 1)
					for (int dd = 0; dd < NInCluster; dd++)
						if (inputData.clusterStructure[c].getDependentDisease()[dd]) {
							int Ndd = inputData.clusterStructure[c]
									.getDiseaseNumber()[dd];
							// Ndd is disease number belonging to dd ;

							// relRisk[i] already contains the RR due to the
							// risk
							// factors

							// now calculate RR for the dependent disease by
							// multiplying
							// it with the RR due to each independent disease
							relRiskIncludingDisease[i][Ndd] = relRisk[i][Ndd];
							for (int di = 0; di < NInCluster; di++) {
								int Ndi = inputData.clusterStructure[c]
										.getDiseaseNumber()[di];
								// Ndi is disease number belonging to di ;
								// RR due to independent disease= p(di)*RR(di) +
								// 1*(1-p(di)) = p(di)*(RR(di)-1)+1

								relRiskIncludingDisease[i][Ndd] *= (1 + probDisease[i][Ndi]
										* (inputData.getClusterData()[age][sex][c]
												.getRRdisExtended()[di][dd] - 1));

							}
							sumRR[Ndd] += weight[i]
									* relRiskIncludingDisease[i][Ndd];

							;
						}
			}
		} // end second loop over all persons ( i )
		// calculate Baseline Prevalence and Incidence and mortality for
		// dependent diseases
		if (age == 0 && sex == 0)
			this.log.debug("end loop 2");
		;
		calculateBaselineInc(inputData, age, sex, sumRRinHealth, false);

		calculateBaselinePrev(inputData, age, sex, sumRR, true);
		calculateBaselineFatalIncidence(inputData, age, sex, sumRR, true);

		/*
		 * now calculate Baseline Prevalence Odds for the dependent diseases
		 * using an iterative procedure; now repeat loop 1 iteratively to
		 * estimate the baseline odds loop over all diseases
		 * 
		 * exception: when input prevalence==0
		 */
		for (int c = 0; c < inputData.getNCluster(); c++) {
			int NInCluster = inputData.clusterStructure[c].getNInCluster();

			// loop over dependent diseases
			for (int dd = 0; dd < NInCluster; dd++)
				if (this.clusterStructure[c].getDependentDisease()[dd]) {
					// need to sum over all combinations of independent diseases
					// is the cluster
					int Ndd = inputData.clusterStructure[c].getDiseaseNumber()[dd];
					// Ndd is disease number belonging to dd ;

					int nIter = 0;
					/* if prevalence = 0 keep baseline odds=0 */
					double del = 100;
					if (diseasePrevalence[age][sex][Ndd] == 0)
						del = 0;

					double sumPrevCurrent = 0;
					double sumDerivativePrevCurrent = 0;
					double RR = 1;
					while (del > 0.00001 && nIter < 10) {
						sumPrevCurrent = 0;
						sumDerivativePrevCurrent = 0;
						for (int i = 0; i < nSim; i++) {

							for (int combi = 0; combi < Math.pow(NInCluster, 2); combi++) {
								// calculate RR for this combination //
								RR = relRisk[i][Ndd];
								double probCombi = 1;
								for (int di = 0; di < NInCluster; di++) {
									int Ndi = inputData.clusterStructure[c]
											.getDiseaseNumber()[di];
									// see if disease=1 in the cluster (see if
									// bit =1 at right place)
									if ((combi & (1 << di)) == (1 << di)) {
										RR *= inputData.getClusterData()[age][sex][c]
												.getRRdisExtended()[di][dd];
										probCombi *= probDisease[i][Ndi];
									} else
										probCombi *= (1 - probDisease[i][Ndi]);
								}

								sumPrevCurrent += weight[i]
										* probCombi
										* RR
										* this.baselinePrevalenceOdds[age][sex][Ndd]
										/ (1 + RR
												* this.baselinePrevalenceOdds[age][sex][Ndd]);
								sumDerivativePrevCurrent += weight[i]
										* probCombi
										* RR
										/ Math
												.pow(
														(1 + RR
																* this.baselinePrevalenceOdds[age][sex][Ndd]),
														2);
							}
						}

						// end loop over all individuals
						double oldValue = this.baselinePrevalenceOdds[age][sex][Ndd];
						this.baselinePrevalenceOdds[age][sex][Ndd] = oldValue
								- (sumPrevCurrent - diseasePrevalence[age][sex][Ndd])
								/ sumDerivativePrevCurrent;
						del = Math
								.abs(this.baselinePrevalenceOdds[age][sex][Ndd]
										- oldValue);
						++nIter;
					}// end iterative procedure for disease
				} // end loop over diseases

		}// end loop over clusters
		if (age == 0 && sex == 0)
			this.log.debug("end loop 3");
		/**
		 * <br>
		 * Phase 3.<br>
		 * In the third stage of parameter estimation, the probabilities on
		 * dependent diseases are calculated from the baseline prevalences as
		 * calculated in phase 2. Also the comorbidities are calculated from the
		 * same baseline prevalences
		 * 
		 * The aim of this stage is: <br>
		 * 1) estimation of the baseline incidence of the dependent diseases and <br>
		 * 2) estimation of the baseline disability "hazard"<br>
		 * 3) the estimation of the attributable mortality: to be solved with a
		 * matrix equation each row of the equation is for 1 disease. take
		 * disease d as the row disease, and d1 ... dn as other diseases. See
		 * the description of calculation document for a description of the
		 * calculations. <br>
		 * 
		 */
		/*
		 * <br> In short: left hand side of the equation: <br> when other
		 * mortality depends on the riskfactor: <br> mtot + (1-p(d))E(d) -
		 * average(mtot(r)|d) + terms for case fatality <br> when other
		 * mortality does not depend on the riskfactor: <br> (1-p(d))E(d) +
		 * terms for case fatality <br> <br> Terms for case fatality: <br> this
		 * is only relevant when case fatatity applies to a dependent diseases.
		 * For the dependent diseases itself the following terms are added to
		 * the lefthand side (one for each dependent disease, including the
		 * disease itself): <br> - average (over all R)
		 * of[fatalbaselineinc-depNO(r)(RRdisNO-1){(P(intermed and dep given
		 * R)-p(intermediate given R)P(dependent given R)}]/P(dep) <br> depNO
		 * stands for each dependent disease, <br> dep is the disease of the
		 * equation. <br> <br>
		 * 
		 * For intermediate diseases the following terms are added to the
		 * lefthand side (with minus sign): <br> - Average(over all R) of
		 * [fatalbaselineinc_dep(R)(RRdis-1)(1-P(intermed given R))P(intermed
		 * given R)]/P(intermed) <br> <br>
		 * 
		 * 
		 * The last term gives the mortality of a population in which the
		 * distribution of risk factors is equal to that with disease d. To
		 * calculate this, we reason that each person i in our estimation
		 * population has a probability probdisease(d) to have the disease. So
		 * to obtain the distribution of riskfactors as in disease d, we will
		 * have to weight each person with weight probdisease(d). So this can be
		 * found by taking probdisease(d,i)probmort(i) and average over the
		 * population. probmort(i) is here RRm(i)baseline mortality. Thus we
		 * take the sum over RRm(i)baseline mortality probdisease(d,i), divided
		 * by the sum over probdisease(d,i) (=sum of weights). <br> <br> The
		 * right hand side of the equation: this side has a term for each
		 * disease, and thus is a matrix with the following entries for row d:
		 * For disease d itself it is: <br> 1-average(probdisease(d,i)^2)/p(d)
		 * For disease d1 it is: average(probdisease (d
		 * &d1,i))/p(d)-average(probdisease(d,i)probdisease(d1,i))/p(d). <br>
		 * <br> This term is zero when two diseases are independent. Also the
		 * term for d alone can be calculated with this formula <br>
		 */

		// initialize help variables for calculation of attributable
		// mortality
		/*
		 * prevalenceDiseaseStates gives the prevalence of each diseaseState
		 * (averaged over the population)
		 */
		double[][] prevalenceDiseaseStates = new double[this.nCluster][];
		double[][] prevalenceDiseaseStatesForI = new double[this.nCluster][];
		{

			double[][] vMat = new double[nDiseases][nDiseases];
			// Vmat is the matrix containing the right hand side terms. Both
			// indexes are disease numbers

			double[] expectedMortality = new double[nDiseases];
			// expected mortality contains the expected mortality for those
			// with
			// a risk
			// factor distribution equal to that of a group of persons with
			// disease d. Index: d

			double[] sumForCF = new double[nDiseases];
			Arrays.fill(sumForCF, 0);
			// variable holding the expected mortality from case fatality of
			// dependent diseases
			// index is the disease of the disease line
			double[] sumRRmDisease = new double[nDiseases];
			Arrays.fill(sumRRmDisease, 0);
			// sum over relRiskMort weighted with the probability of having
			// disease d
			// index = d
			boolean[] isCuredDisease = new boolean[nDiseases];
			Arrays.fill(isCuredDisease, false);

			// third loop over all persons

			for (int i = 0; i < nSim; i++) {

				for (int c = 0; c < inputData.getNCluster(); c++) {
					prevalenceDiseaseStates[c] = new double[(int) Math.pow(2,
							clusterStructure[c].getNInCluster())];
					prevalenceDiseaseStatesForI[c] = new double[(int) Math.pow(2,
							clusterStructure[c].getNInCluster())];
					if (this.clusterStructure[c].isWithCuredFraction()) {
						isCuredDisease[this.clusterStructure[c]
								.getDiseaseNumber()[0]] = true;
						prevalenceDiseaseStates[c] = new double[3];
						prevalenceDiseaseStatesForI[c] = new double[3];

					}

					// now calculate comorbidity that contains the
					// probability
					// of
					// each combination of diseases
					// within each cluster

					probComorbidity[i][c] = new Morbidity(inputData
							.getClusterData()[age][sex][c],
							inputData.clusterStructure[c], relRisk[i],
							this.baselinePrevalenceOdds[age][sex]);

					if (inputData.clusterStructure[c].getNInCluster() == 1) {
						prevalenceDiseaseStatesForI[c][1] = probComorbidity[i][c].getProb()[0][0];
						prevalenceDiseaseStatesForI[c][0] = (1 - probComorbidity[i][c].getProb()[0][0]);
						prevalenceDiseaseStates[c][1] += weight[i]
						        								* prevalenceDiseaseStatesForI[c][1];
						prevalenceDiseaseStates[c][0] += weight[i]*prevalenceDiseaseStatesForI[c][0]
						        								;

					}
					if (this.clusterStructure[c].isWithCuredFraction()) {
						prevalenceDiseaseStatesForI[c][1] = 
								 probComorbidity[i][c].getProb()[0][0];
						prevalenceDiseaseStatesForI[c][2] = 
								 probComorbidity[i][c].getProb()[1][1];
						prevalenceDiseaseStatesForI[c][0] = 
								 (1 - probComorbidity[i][c].getProb()[0][0] - probComorbidity[i][c]
										.getProb()[1][1]);
						prevalenceDiseaseStates[c][1] += weight[i]*prevalenceDiseaseStatesForI[c][1];
						prevalenceDiseaseStates[c][0] += weight[i]*prevalenceDiseaseStatesForI[c][0];
						prevalenceDiseaseStates[c][2] += weight[i]*prevalenceDiseaseStatesForI[c][2];
						                                                  					

					}

					// extract the probDisease for the dependent diseases
					if (inputData.clusterStructure[c].getNInCluster() > 1) {
						for (int dd = 0; dd < inputData.clusterStructure[c]
								.getNDep(); dd++) {
							// make index for numbers of (in)dependent
							// diseases in cluster

							int ndd = inputData.clusterStructure[c]
									.getIndexDependentDiseases()[dd];
							// ndd is number of the dependent disease within
							// the
							// cluster
							int d = inputData.clusterStructure[c]
									.getDiseaseNumber()[ndd];
							probDisease[i][d] = probComorbidity[i][c].getProb()[ndd][ndd];
							// d is the number of this disease in the whole
							// set of diseases (also outside the cluster)

							/*
							 * 
							 * 
							 * 
							 * Make terms needed to calculate baseline incidence
							 * for dependent diseases RR= sum of prob(1-p|combi
							 * of independent diseases) RR(combi)
							 * 
							 * if the baseline prevalence odds ==0 then
							 * probability should be 0
							 */
							for (int combi = 0; combi < Math.pow(2,
									inputData.clusterStructure[c].getNIndep()); combi++) {
								// double logitDiseasedInCombi=-9999999;

								// if (baselinePrevalenceOdds[age][sex][d]
								// !=0)
								double oddsDiseasedInCombi = this.baselinePrevalenceOdds[age][sex][d]
										* relRisk[i][d];
								// logitDiseasedInCombi= Math
								// .log(baselinePrevalenceOdds[age][sex][d])
								// + Math.log(relRisk[i][d]);
								double probCombi = 1;
								// must be: probability conditional on not
								// having disease d
								double RRcombi = 1;
								for (int di = 0; di < inputData.clusterStructure[c]
										.getNIndep(); di++) {
									int ndi = inputData.clusterStructure[c]
											.getIndexIndependentDiseases()[di];
									if ((combi & (1 << di)) == (1 << di)) {
										probCombi *= probComorbidity[i][c]
												.getProb()[ndi][ndi];
										oddsDiseasedInCombi *= inputData
												.getClusterData()[age][sex][c]
												.getRRdisExtended()[inputData.clusterStructure[c]
												.getIndexIndependentDiseases()[di]][inputData.clusterStructure[c]
												.getIndexDependentDiseases()[dd]];
										RRcombi *= inputData.getClusterData()[age][sex][c]
												.getRRdisExtended()[inputData.clusterStructure[c]
												.getIndexIndependentDiseases()[di]][inputData.clusterStructure[c]
												.getIndexDependentDiseases()[dd]];
									} else
										probCombi *= (1 - probComorbidity[i][c]
												.getProb()[ndi][ndi]);
								}
								// alternatief
								// inputData.clusterData[age][sex][c].RRdisExtended[0][1]
								// RRCombi*=inputData.diseaseData[c].RRdisExtended[ndi][ndd];
								// probability of p(not d^combi)=p(not
								// d|combi)p(combi)=(1-p(d|combi)p(combi)
								// and because combi diseases are mutually
								// independent when not conditioning on d
								// they can be calculated from
								// multiplication

								// below: w * RR * prob(dep|indep,
								// r)*prob(combi)
								// RR is the thing we sum
								if (this.baselinePrevalenceOdds[age][sex][d] != 0)
									sumRRinHealth[d] += weight[i]
											* relRisk[i][d]
											* (1 / (1 + oddsDiseasedInCombi))
											* RRcombi * probCombi;
								else
									sumRRinHealth[d] += weight[i] * probCombi
											* RRcombi;
								;

								prevalenceDiseaseStatesForI[c][combi] = probCombi;
								prevalenceDiseaseStates[c][combi] += weight[i]*prevalenceDiseaseStatesForI[c][combi];
								
							}
						}
						;
						
						

						for (int dc = 0; dc < inputData.clusterStructure[c]
								.getNInCluster(); dc++) {
							/*
							 * dc = disease number in cluster first make the
							 * term in case of d is a dependent disease
							 */
							int d = inputData.clusterStructure[c]
									.getDiseaseNumber()[dc];
							/*
							 * d is number disease in total numbering
							 */
							if (inputData.clusterStructure[c]
									.getDependentDisease()[dc]) {
								/*
								 * we need separate terms if there are more than
								 * one causal diseases. to keep things "simple"
								 * we therefore calculate the total of the sums
								 * including the constants that could be added
								 * later. we need to circle through the causes,
								 * and for each cause add the terms for all the
								 * resulting dependent diseases. thus two loops:
								 * the first over the causes, the second over
								 * the dependent diseases of these causes
								 */

								for (int di = 0; di < inputData.clusterStructure[c]
										.getNIndep(); di++) // loop
								// over
								// causes
								{
									int ndi = inputData.clusterStructure[c]
											.getIndexIndependentDiseases()[di];
									for (int dd = 0; dd < inputData.clusterStructure[c]
											.getNDep(); dd++) // loop
									// over
									// dependent
									// diseases
									{
										int ndd = inputData.clusterStructure[c]
												.getIndexDependentDiseases()[dd];
										double RRdisC = inputData
												.getClusterData()[age][sex][c]
												.getRRdisExtended()[ndi][ndd];
										double prev = inputData
												.getClusterData()[age][sex][c]
												.getPrevalence()[dc];
										if (prev != 0 && prev != 1) {
											if (inputData
													.isWithRRForMortality())
												sumForCF[d] += weight[i]
														* (RRdisC - 1)
														* this.baselineFatalIncidence[age][sex][d]
														* (probComorbidity[i][c]
																.getProb()[ndd][ndi] - probComorbidity[i][c]
																.getProb()[ndd][ndd]
																* probComorbidity[i][c]
																		.getProb()[ndi][ndi])
														/ prev;
											else
												// // alleen de eerste term, de laatste wordt verderop toegevoegd
												// 
												sumForCF[d] += weight[i]
														* (RRdisC - 1)
														* this.baselineFatalIncidence[age][sex][d]
														* probComorbidity[i][c]
																.getProb()[ndd][ndi]/prev 												
																;

										} else
											sumForCF[d] = 0;
										/*
										 * if prev ==0 then make sumForCF equal
										 * to zero basically you cannot
										 * calculate the attributable mortality
										 * in this case and so it is made equal
										 * to excess mortality.
										 */
									} // end loop over dependent diseases
								}
								;
							} // end loop over causes
							
							else {
								// independent disease:
								// here only one loop is needed, as we only
								// need
								// to check if there are dependent diseases
								// of this intermediate disease

								for (int dd = 0; dd < inputData.clusterStructure[c]
										.getNDep(); dd++) // loop
								// over
								// dependent
								// diseases
								{
									int ndd = inputData.clusterStructure[c]
											.getIndexDependentDiseases()[dd];
									double RRdisC = inputData.getClusterData()[age][sex][c]
											.getRRdisExtended()[dc][ndd];
									double prev = inputData.getClusterData()[age][sex][c]
											.getPrevalence()[dc];
									if (prev != 0)
										if (inputData.isWithRRForMortality())
											sumForCF[d] += weight[i]
													* (RRdisC - 1)
													* this.baselineFatalIncidence[age][sex][d]
													* (1 - probComorbidity[i][c]
															.getProb()[dc][dc])
													* probComorbidity[i][c]
															.getProb()[dc][dc]
													/ prev;
										else
											// alleen de eerste term, de laatste wordt verderop toegevoegd
											sumForCF[d] += weight[i]
													* (RRdisC - 1)
													* this.baselineFatalIncidence[age][sex][d]
													* probComorbidity[i][c]
															.getProb()[dc][dc]
													/ prev;
									;
								} // end loop over dependent diseases

							}
							if (!inputData
									.isWithRRForMortality()) sumForCF[d] -=inputData.getClusterData()[age][sex][c].getIncidence()[dc]*
							inputData.getClusterData()[age][sex][c].getCaseFatality()[dc];

						}
						/*
						 * - average (over all R) of
						 * [fatalbaselineinc-depNO(r)(RRdisNO-1){(P(intermed and
						 * dep given R)-p(intermediate given R)P(dependent given
						 * R)}]/P(dep) depNO stands for each dependent disease,
						 * dep is the disease of the equation
						 * 
						 * For intermediate diseases the following terms are
						 * added to the lefthand side (with minus sign): -
						 * Average(over all R) of
						 * [fatalbaselineinc_dep(R)(RRdis-1)(1-P(intermed given
						 * R))P(intermed given R)]/P(intermed)
						 */
					}
					;
					vMat = probComorbidity[i][c].addBlock(vMat, weight[i],
							inputData.clusterStructure[c], inputData
									.isWithRRForMortality(),
							diseasePrevalence[age][sex]);
				}

				// extract the probability of each single disease
				for (int d = 0; d < nDiseases; d++) {

					/*
					 * this part is redundant as this was already calculated
					 * earlier probDisease[i][d + probComorbidity[i][c].dStart]
					 * = probComorbidity[i][c].prob[d][d];
					 * 
					 * 
					 * calculate the contribution to the sum of the relative
					 * risk for mortality given the risk factor distribution of
					 * each disease
					 */
					sumRRmDisease[d] += probDisease[i][d] * relRiskMort[i]
							* weight[i];
					// sumExpectedCF[d]+=
				}
				// calculate matrix V=
				// average(probdisease (d1 &d2,i))/p(d1)-
				// * average(probdisease(d1,i)*probdisease(d2,i))/p(d1)

				
				
				/*
				 * Calculate the prevalence of disability for group/subject i due to diseases
				 */
				if (withRRdisability){
				abilityFromDiseases[i] = 0;

				for (int diseaseCombi = 0; diseaseCombi < Math.pow(2, nDiseases); diseaseCombi++) {
					/* use logaritmes to prevent numerical problems */
					double logProbCombi = 0;
					double abilityCombi = 1;
					for (int c = 0; c < nCluster; c++) {
						/* filter the clusterpart out of the diseaseCombi */
						/* filter heeft enen op de plek van het cluster en elders nullen */
						int filter = (int) Math.round(Math.pow(2, clusterStructure[c]
								.getNInCluster()) - 1);
						filter = filter << clusterStructure[c].getDiseaseNumber()[0];
						// TODO nakijken of dit wel nullen rechts toevoegd
						/* apply filter to get the diseasestate within the cluster */
						int stateInCluster = filter
								& diseaseCombi >> clusterStructure[c]
										.getDiseaseNumber()[0];
						logProbCombi += Math
								.log(prevalenceDiseaseStatesForI[c][stateInCluster]);

						for (int d = 0; d < clusterStructure[c].getNInCluster(); d++) {
							if ((stateInCluster & (1 << d)) == (1 << d))
								abilityCombi *= inputData.getClusterData()[age][sex][c]
										.getAbility()[d];

						}

					}
					abilityFromDiseases[i] += abilityCombi * Math.exp(logProbCombi);
					double abilityFromOtherCauses=abilityFromRiskFactor[i]/abilityFromDiseases[i];
					if (riskType==1){
						if( abilityFromOtherCauses<1){ 
						if (i==0){	this.baselineAbility[age][sex]=(float) -Math.log(1-abilityFromOtherCauses);
			              	this.riskFactorDisabilityRRcat[age][sex][0]=1;}
			              	else
			              	this.riskFactorDisabilityRRcat[age][sex][i]=(float) -Math.log(1-abilityFromOtherCauses)/this.baselineAbility[age][sex];
			              	
					}
						else {
							String label="";
							if (nWarningsDisability==2) label=" NO MORE WARNINGS OF THIS TYPE WILL BE ISSUED FOR"+
							" OTHER AGE/SEX GROUPS";
							if (nWarningsDisability<3) displayWarningMessage("the disability given for riskfactor group " +i+
									" in age "+age+" and sex "+sex+ " can " +
									"be explained completely by differences in disease prevalences due to the riskfactor. " +
									"Therefore disability for this group will be calculated solely on disease status and not on risk " +
									"factor status"+label);
							withRRdisability=false;
							nWarningsDisability++;
							
							
						}
						//TODO other risktypes
				}}}
			
				
				
				
				
				
			}// end third loop over all persons i

			calculateBaselineInc(inputData, age, sex, sumRRinHealth, true);
			if (age == 0 && sex == 0)
				this.log.debug("end loop 3");
			// now calculate the attributable mortality

			double[] lefthand = new double[nDiseases];
			for (int d = 0; d < nDiseases; d++) {
				if (diseasePrevalence[age][sex][d] != 0)
					expectedMortality[d] = this.baselineMortality[age][sex]
							* sumRRmDisease[d] / diseasePrevalence[age][sex][d];

				else
					expectedMortality[d] = inputData.getMortTot()[age][sex];

				/* mtot + (1-p(d))E(d) - average(mtot(r)|d) */
				if (inputData.isWithRRForMortality() && !isCuredDisease[d])
					lefthand[d] = inputData.getMortTot()[age][sex]
							+ (1 - diseasePrevalence[age][sex][d])
							* excessMortality[d] - expectedMortality[d]
							- sumForCF[d];
				/* in cured diseases the attributable mortality is 0 */
				else if (isCuredDisease[d])
					lefthand[d] = 0;
				else
					// TODO nog checken
					lefthand[d] = (1 - diseasePrevalence[age][sex][d])
							* excessMortality[d] - sumForCF[d];
			}

			boolean negativeAM = true;
			int niter = 0;
			while (negativeAM && niter < 10) {
				/* make vMat into a Matrix */
				negativeAM = false;
				Matrix vMatrix = new Matrix(vMat);
				// Invert

				Matrix vInverse = vMatrix.inverse();

				if (age == 0 && sex == 0)
					this.log.debug("matrix is inverted");

				Matrix LH = new Matrix(lefthand, nDiseases);
				double[] temp = vInverse.times(LH).getRowPackedCopy();
				if (age == 0 && sex == 0)
					this.log.debug("attributable mortality calculated");
				for (int d = 0; d < nDiseases; d++) {
					this.attributableMortality[age][sex][d] = (float) temp[d];
				}

				if (age == 0 && sex == 0)
					this.log.debug("attributable mortality written");

				for (int d = 0; d < nDiseases; d++) {
					if (Math.abs(this.attributableMortality[age][sex][d]) < 1e-16)
						this.attributableMortality[age][sex][d] = 0;
					if (this.attributableMortality[age][sex][d] < 0) {
						negativeAM = true;

						/*
						 * exclude this disease from the calculations and make
						 * AM zero for this disease
						 * 
						 * This will decrease the AM of other diseases, so they
						 * might get negative; therefore this should be repeated
						 * until no negative AM's are left
						 */

						/*
						 * this is done by setting the rows and columns of vMat
						 * to zero, and the diagonal to 1
						 */
						for (int d1 = 0; d1 < nDiseases; d1++) {
							vMat[d1][d] = 0;
							vMat[d][d1] = 0;
						}
						vMat[d][d] = 1;

					}

				}
				niter++;

			} // einde herhaling schatting van Attributable mortality
			if (niter == 10)
				this.log
						.fatal(" negative attributable mortality estimated after 10 iterations!!");
			// TODO throw exception

		}

		/*
		 * estimate disability other cause disability using
		 * prevalenceDiseaseStates[c][state] for the case that there is no RR
		 * disability
		 * 
		 * to do so, loop over all possible disease combinations and calculate
		 * the disease-cause disability, and sum these to obtain the total
		 * disability caused by disease the fraction disability from other
		 * causes then is: (total disability- disability caused by diseases)/
		 * total disability
		 * 
		 * In case there is also disability based on risk factors:
		 * 
		 * disabilityFromRiskFactor
		 */
		double ability = 0;
        if (!withRRdisability){
		for (int diseaseCombi = 0; diseaseCombi < Math.pow(2, nDiseases); diseaseCombi++) {
			/* use logaritmes to prevent numerical problems */
			double logProbCombi = 0;
			double abilityCombi = 1;
			for (int c = 0; c < nCluster; c++) {
				/* filter the clusterpart out of the diseaseCombi */
				/* filter heeft enen op de plek van het cluster en elders nullen */
				int filter = (int) Math.round(Math.pow(2, clusterStructure[c]
						.getNInCluster()) - 1);
				filter = filter << clusterStructure[c].getDiseaseNumber()[0];
				// TODO nakijken of dit wel nullen rechts toevoegd
				/* apply filter to get the diseasestate within the cluster */
				int stateInCluster = filter
						& diseaseCombi >> clusterStructure[c]
								.getDiseaseNumber()[0];
				logProbCombi += Math
						.log(prevalenceDiseaseStates[c][stateInCluster]);

				for (int d = 0; d < clusterStructure[c].getNInCluster(); d++) {
					if ((stateInCluster & (1 << d)) == (1 << d))
						abilityCombi *= inputData.getClusterData()[age][sex][c]
								.getAbility()[d];

				}

			}
			ability += abilityCombi * Math.exp(logProbCombi);

		}
		float overallAbility = inputData.getOverallDalyWeight()[age][sex];
		this.baselineAbility[age][sex] = (float) (overallAbility / ability);
		for (int i=0;i<nRiskCat;i++)
			                            this.riskFactorDisabilityRRcat[age][sex][i]=1;
		this.riskFactorDisabilityRRcont[age][sex]=1;
		this.riskFactorDisabilityRRend[age][sex]=1;
		this.riskFactorDisabilityRRbegin[age][sex]=1;
		this.riskFactorDisabilityalfa[age][sex]=1;
			                            
		if (this.baselineAbility[age][sex] > 1) {
			String label="";
			if (nWarningsDisability==2) label=" NO MORE WARNINGS OF THIS TYPE WILL BE ISSUED FOR"+
			" OTHER AGE/SEX GROUPS";
			
			if (nWarningsDisability<3) displayWarningMessage("Overall dalyweight/disability is smaller than dalyweight/disability due "
					+ "to diseases for age "
					+ age
					+ " and gender "
					+ sex
					+ " : disability due to diseases: "
					+ (1 - ability)
					+ " and overall: "
					+ (1 - overallAbility)
					+ " . Other cause disability is set" + " to zero."+label);
			nWarningsDisability++;
			this.baselineAbility[age][sex] = 1;

		}}

	 	

		;
		/**
		 * <br>
		 * Phase 4 <br>
		 * In the fourth stage of parameter estimation, the estimated
		 * attributable mortality is used to calculate the other cause mortality
		 * per simulated person i Then a regression is done of this other cause
		 * mortality on the risk factors yielding relative risks for other cause
		 * mortality <br>
		 */

		if (age == 0 && sex == 0)
			this.log.debug("begin loop 4");
		double sumOtherMort = 0;

		double[] beta;
		double otherMort[] = new double[nSim];
		double logOtherMort[] = new double[nSim];
		double nNegativeOtherMort = 0;

		// make design matrix for regression (including dummy variables
		// for
		// each risk class)

		/*
		 * in case there are zero prevalences, this point should be excluded the
		 * indexDat translate the indexes of the regression data to the original
		 * classes
		 */

		double[][] xMatrix = new double[nSim][2];
		double[] wVector = weight;
		/* count number of valid categories */
		int nValidCategories = 0;
		int[] indexForCategories = new int[nRiskCat];
		for (int i = 0; i < nRiskCat; i++) {
			indexForCategories[nValidCategories] = i;
			if (inputData.getPrevRisk()[age][sex][i] > 0)
				nValidCategories++;

			;
		}
		/* count number of valid rows */
		int nValidRows = 0;
		int[] indexForRows = new int[nSim];
		for (int i = 0; i < nSim; i++) {
			indexForRows[nValidRows] = i;
			if (weight[i] > 0)
				nValidRows++;
		}
		if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
			xMatrix = new double[nValidRows][nValidCategories];
		wVector = new double[nValidRows];
		// fourth loop over all persons i: fill the design matrix
		int nrow = 0;
		for (int i = 0; i < nSim; i++) {

			// add intercept

			// add dummies except for the first class = reference
			// category
			if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3) {
				if (weight[i] > 0) {
					xMatrix[nrow][0] = 1.0;
					wVector[nrow] = weight[i];
					for (int rc = 1; rc < nValidCategories; rc++) {
						if (riskclass[indexForCategories[i]] == rc)
							xMatrix[nrow][rc] = 1.0;
						else
							xMatrix[nrow][rc] = 0.0;

					}
					nrow++;
				}
			}
			;
			// add continuous risk factor only for type=2
			// for type=3 the compound part is dealt with separately
			// here nrow==i;
			if (inputData.getRiskType() == 2) {
				xMatrix[i][xMatrix[i].length - 1] = riskfactor[i]
						- inputData.getRefClassCont();

			}
			otherMort[i] = relRiskMort[i] * this.baselineMortality[age][sex];
			for (int d = 0; d < nDiseases; d++) {
				otherMort[i] -= this.attributableMortality[age][sex][d]
						* probDisease[i][d];
			}
			;
			sumOtherMort += weight[i] * otherMort[i];
			if (otherMort[i] > 0)
				logOtherMort[i] = Math.log(otherMort[i]);
			else {
				this.log
						.warn("negative other mortality  = " + otherMort[i]
								+ " for person  " + i + " for riskclass "
								+ riskclass[i] + " and for riskfactor "
								+ riskfactor[i]);
				logOtherMort[i] = -999999;
				nNegativeOtherMort += weight[i];
			}
		}
		double[] yValue = logOtherMort;
		if (nValidRows < nSim) {
			yValue = new double[nValidRows];
			for (int i = 0; i < nValidRows; i++)
				yValue[i] = logOtherMort[indexForRows[i]];

		}
		// end of fourth loop over all persons i
		if (age == 0 && sex == 0)
			this.log.debug("end loop 4");
		if (nNegativeOtherMort > 0.1 && inputData.isWithRRForMortality()) {

			displayWarningMessage("negative other mortality  in  "
					+ (nNegativeOtherMort * 100) + " % of simulated cases");
		}
		if (nNegativeOtherMort > 0.3 && inputData.isWithRRForMortality())
			throw new DynamoInconsistentDataException(
					"Other mortality becomes negative in"
							+ " more than 30% ( "
							+ (nNegativeOtherMort * 100)
							+ " %) of cases. The amount of disease specific mortality given to the model"
							+ " exceeds the overall mortality given to the model.  Please lower excess mortality rates or"
							+ " case fatality rates or disease prevalence rates, or increase total mortality rates");

		if (sumOtherMort < 0)
			throw new DynamoInconsistentDataException(
					"Attributable Mortality from diseases exceeds the overall mortality. "
							+ "  Please lower excess mortality rates or"
							+ " case fatality rates or disease prevalence rates, or increase total mortality rates");

		// carry out the regression of log other mortality on the risk
		// factors;
		if (inputData.isWithRRForMortality()) {
			try {
				beta = weightedRegression(yValue, xMatrix, wVector);
			} catch (Exception e) {

				e.printStackTrace();
				this.log
						.fatal("runtime error while estimating model parameters. e.getMessage()"
								+ " for age is " + age + "and sex is " + sex);
				throw new RuntimeException(e.getMessage());
			}
			if (age == 0 && sex == 0 && beta.length > 1)
				this.log.debug(" beta 0 and 1 :" + beta[0] + beta[1]);
			// calculate relative risks from the regression coefficients

			// first class has relative risk of 1; also default value for all
			// other
			// categories
			Arrays.fill(this.relRiskOtherMort[age][sex], 1);

			if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3) {
				for (int j = 1; j < beta.length; j++)
				// calculate the relative risk relative to the first
				// risk
				// class
				// //
				{
					this.relRiskOtherMort[age][sex][indexForCategories[j]] = (float) Math
							.exp(beta[j]);
					// in case of duration class set rr to 1;
					if (inputData.getRiskType() == 3
							&& inputData.getIndexDuurClass() == j)
						this.relRiskOtherMort[age][sex][j] = 1;
				}

				// last beta is the coefficient for the continuous risk factor
				// //
				this.relRiskOtherMortCont[age][sex] = (float) Math
						.exp(beta[beta.length - 1]);
			}
			if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
				this.relRiskOtherMortCont[age][sex] = 1;

			this.baselineOtherMortality[age][sex] = (float) Math.exp(beta[0]);
			/**
			 * in the fifth stage the sum of the RR's on other cause mortalities
			 * is calculated in order to estimate the baseline other cause
			 * mortality This could also be derived from the regression
			 * (intercept)
			 * 
			 */
			if (inputData.getRiskType() == 3) { // now do time dependent part;

				// first anker the RRbegin and RRend if those are ankered for
				// all cause mortality
				double endRR = -1;
				double beginRR = -1;
				for (int rc = 0; rc < nRiskCat; rc++) {
					if (inputData.getRelRiskDuurMortBegin()[age][sex] == inputData
							.getRelRiskMortCat()[age][sex][rc]
							&& rc != inputData.getIndexDuurClass())
						beginRR = this.relRiskOtherMort[age][sex][rc];
					if (inputData.getRelRiskDuurMortEnd()[age][sex] == inputData
							.getRelRiskMortCat()[age][sex][rc]
							&& rc != inputData.getIndexDuurClass())
						endRR = this.relRiskOtherMort[age][sex][rc];
				}
				// select only the data for the duration class;
				double ydata[] = new double[this.duurFreq[age][sex].length];
				double xdata[] = new double[this.duurFreq[age][sex].length];
				double weightdata[] = new double[this.duurFreq[age][sex].length];
				int index = 0;
				for (int i = 0; i < nSim; i++) {
					if (riskclass[i] == inputData.getIndexDuurClass()) {
						ydata[index] = otherMort[i];
						xdata[index] = riskfactor[i];
						weightdata[index] = weight[i];
						index++;
					}

				}
				try {
					beta = nonLinearDurationRegression(ydata, xdata,
							weightdata, endRR, beginRR,
							this.baselineOtherMortality[age][sex]);
				} catch (Exception e) {
					this.log.fatal(e.getMessage());
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());

				}
				this.relRiskOtherMortBegin[age][sex] = (float) beta[0];
				this.relRiskOtherMortEnd[age][sex] = (float) beta[1];
				this.alfaOtherMort[age][sex] = (float) beta[2];

			}
		} else /* if not rr for other mortality present */
		{
			Arrays.fill(this.relRiskOtherMort[age][sex], 1);
			this.relRiskOtherMortCont[age][sex] = 1;
			this.relRiskOtherMortBegin[age][sex] = 1;
			this.relRiskOtherMortEnd[age][sex] = 1;
			this.alfaOtherMort[age][sex] = 1; // does not really matter
		}

		if (age == 0 && sex == 0)
			this.log.debug("begin loop 5");
		// fifth loop over all persons i to calculate sum of RR other
		// mortality to check baselineOtherMortality
		// only temporary to check method
		double baselineOtherMortality2;
		double sumRROtherMort = 0;
		for (int i = 0; i < nSim; i++) {
			if (inputData.getRiskType() == 3) {
				if (riskclass[i] == inputData.getIndexDuurClass()) {

					sumRROtherMort += weight[i]
							* ((this.relRiskOtherMortBegin[age][sex] - this.relRiskOtherMortEnd[age][sex])
									* Math.exp(-this.alfaOtherMort[age][sex]
											* riskfactor[i]) + this.relRiskOtherMortEnd[age][sex]);

				} else {

					sumRROtherMort += weight[i]
							* this.relRiskOtherMort[age][sex][riskclass[i]];
				}
			} else
				sumRROtherMort += weight[i]
						* this.relRiskOtherMort[age][sex][riskclass[i]]
						* Math.pow(this.relRiskOtherMortCont[age][sex],
								riskfactor[i] - inputData.getRefClassCont());
			/*
			 * double pred= baselineOtherMortalityrelRiskOtherMort[riskclass[i]]
			 * Math.pow(relRiskOtherMortCont,(riskfactor[i] -
			 * inputData.refClassCont)); double
			 * logpred=beta[0]+beta[1]xMatrix[i][1]+beta[2]xMatrix[i][2]
			 * +beta[3]xMatrix[i][3]; System.out .println("predicted " + pred +
			 * " from " + otherMort[i]); System.out .println("log predicted
			 * " + logpred + " from " + logOtherMort[i]);
			 */
		}
		baselineOtherMortality2 = sumOtherMort / sumRROtherMort;
		if (age == 0
				&& sex == 0
				&& baselineOtherMortality2 != this.baselineOtherMortality[age][sex])
			this.log.debug("different baseline mortalities calculated nl "
					+ baselineOtherMortality2 + " after calibration and  "
					+ this.baselineOtherMortality[age][sex] + " before");
		if (baselineOtherMortality2 != 0)
			if (Math.abs(baselineOtherMortality2
					- this.baselineOtherMortality[age][sex])
					/ baselineOtherMortality2 > 0.01)
				this.log
						.fatal("different baseline mortalities calculated after calibration nl "
								+ baselineOtherMortality2
								+ " after calibration while  "
								+ this.baselineOtherMortality[age][sex]
								+ " before.");
		this.baselineOtherMortality[age][sex] = (float) baselineOtherMortality2;

		if (age == 0 && sex == 0)
			this.log.debug("end loop 5");

	}

	/**
	 * @param inputData
	 * @return number of diseases (int)
	 */
	public int getNDiseases(InputData inputData) {
		int nDiseases = 0;
		for (int c = 0; c < this.nCluster; c++) {
			nDiseases += inputData.clusterStructure[c].getNInCluster();
		}
		return nDiseases;
	}

	/**
	 * 
	 * @return number of diseases (int)
	 */
	public int getNDiseases() {
		int nDiseases = 0;
		for (int c = 0; c < this.nCluster; c++) {
			nDiseases += this.clusterStructure[c].getNInCluster();
		}
		return nDiseases;
	}

	// end constructor
	/**
	 * the method calculates the baseline incidence rate
	 * 
	 * @param InputData
	 *            : Object with input data
	 * @param age
	 * @param sex
	 * @param meanRR
	 *            : mean Relative Risk <b> in health persons </b> in the
	 *            simulated population
	 * @param isDependent
	 *            : indicates if this should be calculated for dependent
	 *            diseases
	 */
	private void calculateBaselineInc(InputData InputData, int age, int sex,
			double[] meanRR, boolean isDependent) {
		// loops over the diseases within clusters
		for (int c = 0; c < InputData.getNCluster(); c++) {
			for (int dc = 0; dc < InputData.clusterStructure[c].getNInCluster(); dc++) {
				// this is done either for the independent diseases or the
				// dependent diseases;
				if (InputData.clusterStructure[c].getDependentDisease()[dc] == isDependent) {
					int d = InputData.clusterStructure[c].getDiseaseNumber()[dc];
					this.baselineIncidence[age][sex][d] = (float) (InputData
							.getClusterData()[age][sex][c].getIncidence()[dc]
							* (1 - InputData.getClusterData()[age][sex][c]
									.getCaseFatality()[dc])

							* (1 - InputData.getClusterData()[age][sex][c]
									.getPrevalence()[dc]) / meanRR[d]);

				}
			}
		} // end loops over diseases within cluster
	}

	/**
	 * the method calculates the initial estimate for the baseline odds assuming
	 * that the prevalence relative risk is equal to the incidence relative risk
	 * 
	 * @param InputData
	 *            : Object with input data
	 * @param age
	 * @param sex
	 * @param meanRR
	 *            : mean Relative Risk in the simulated population
	 * @param isDependent
	 *            : indicates if this should be calculated for dependent
	 *            diseases
	 */
	private void calculateBaselinePrev(InputData InputData, int age, int sex,
			double[] meanRR, boolean isDependent) {
		// loops over the diseases within clusters
		for (int c = 0; c < InputData.getNCluster(); c++) {
			for (int dc = 0; dc < InputData.clusterStructure[c].getNInCluster(); dc++) {
				// this is done either for the independent diseases or the
				// dependent diseases;
				if (InputData.clusterStructure[c].getDependentDisease()[dc] == isDependent) {
					int d = InputData.clusterStructure[c].getDiseaseNumber()[dc];

					this.baselinePrevalenceOdds[age][sex][d] = InputData
							.getClusterData()[age][sex][c].getPrevalence()[dc]
							/ meanRR[d];

				}

			}

		} // end loops over diseases within cluster

	}

	/**
	 * the method calculates the baseline fatal incidence rate
	 * 
	 * @param InputData
	 *            : Object with input data
	 * @param age
	 * @param sex
	 * @param meanRR
	 *            : mean Relative Risk in the simulated population
	 * @param isDependent
	 *            : indicates if this should be calculated for dependent
	 *            diseases
	 */
	private void calculateBaselineFatalIncidence(InputData InputData, int age,
			int sex, double[] meanRR, boolean isDependent) {
		// loops over the diseases within clusters
		for (int c = 0; c < InputData.getNCluster(); c++) {
			for (int dc = 0; dc < InputData.clusterStructure[c].getNInCluster(); dc++) {
				// this is done either for the independent diseases or the
				// dependent diseases;
				if (InputData.clusterStructure[c].getDependentDisease()[dc] == isDependent) {
					int d = InputData.clusterStructure[c].getDiseaseNumber()[dc];

					if (meanRR[d] != 0)
						this.baselineFatalIncidence[age][sex][d] = (float) (InputData
								.getClusterData()[age][sex][c].getIncidence()[dc]
								* (InputData.getClusterData()[age][sex][c]
										.getCaseFatality()[dc]) / meanRR[d]);
					else
						this.baselineFatalIncidence[age][sex][d] = 0;
					/*
					 * if the sum of all RR's ==0, then the incidence must be 0
					 * also
					 */

				}

			}

		} // end loops over diseases within cluster

	}

	// Constructor for only continuous (2 double)

	// Constructor for only class (int + double array)

	// Constructor for class with duration (int + double array + ??)

	/* method regression(y,x) does a regression of array y on matrix x */
	/* no checking of dimensions is done */
	/**
	 * the method does multiple linear regression (least squares method)
	 * 
	 * @param y_array
	 *            : array containing the y values
	 * @param x_array
	 *            : matrix containing the x values
	 * @return array of regression coefficients
	 */
	private double[] regression(double[] y_array, double[][] x_array) {
		Matrix X = new Matrix(x_array);
		Matrix Y = new Matrix(y_array, y_array.length);
		Matrix XT = X.transpose();
		Matrix XX = XT.times(X);
		Matrix inverseXX = XX.inverse();
		Matrix XY = XT.times(Y);
		// beta are the regression coefficients;
		Matrix Beta = inverseXX.times(XY);
		double coef[] = Beta.getColumnPackedCopy();
		return coef;
	};

	/**
	 * this methods does a non linear regression fitting the model :
	 * RR=(RRbegin-RRend)exp(-alfa*time)+RRend.
	 * 
	 * @param y_array
	 *            array with values of the dependent variable (=other cause
	 *            mortality, not the RR itself)
	 * @param x_array
	 *            array with values of the independent variable (time)
	 * @param endRR
	 *            Gives the value of the end RR (assuming it fixed). If -1 it is
	 *            fitted
	 * @param beginRR
	 *            Gives the value of the begin RR (assuming it fixed). If -1 it
	 *            is fitted
	 * @param BaselineMort
	 *            : value of the baseline Mortality. The depedent variable of
	 *            the regression is y_array/BaselineMort
	 * @return a array with three values: <ls> <le>[0] = estimate of
	 *         RRbegin;</le> <le>[1] = estimate of RRend;</le> <le>[2] =
	 *         estimate of Alfa;</le> </ls>
	 * @throws Exception
	 */
	private double[] nonLinearDurationRegression(double[] y_array,
			double[] x_array, double[] W, double endRR, double beginRR,
			double baselineMort) throws Exception

	{
		int nParam = 1;
		if (endRR == -1)
			++nParam;
		if (beginRR == -1)
			++nParam;

		double result[] = new double[3];
		double resultReg[] = new double[nParam];
		double jMat[][] = new double[x_array.length][nParam];
		double currentRRbegin;
		double currentRRend;
		double currentAlfa;
		// starting values assume RRbegin and RRend equal to first and last
		// value and alfa * t = 3 for t is time between first and last moment;
		if (beginRR == -1)
			currentRRbegin = y_array[0] / baselineMort;
		else
			currentRRbegin = beginRR;
		if (endRR == -1)
			currentRRend = y_array[y_array.length - 1] / baselineMort;
		else
			currentRRend = endRR;
		currentAlfa = 3 / (x_array[x_array.length - 1] - x_array[0]);

		double delRRbegin[] = new double[x_array.length];
		double delRRend[] = new double[x_array.length];
		double delAlfa[] = new double[x_array.length];
		double delY[] = new double[x_array.length];
		double fitted[] = new double[x_array.length];
		double Ydata[] = new double[x_array.length];
		double trace[] = new double[nParam];
		double old1 = 100;
		double old2 = 100;
		double old3 = 100;
		if (endRR == beginRR && endRR != -1) {
			result[0] = beginRR;
			result[1] = endRR;
			result[2] = 0;
			return result;
		}
		;
		double lambda = 1; // lambda is de Marquard parameter;
		// regressie is op de relative risico's
		for (int iter = 0; iter < 500; iter++) {

			double Criterium = 0;
			double oldCriterium = 100;
			for (int j = 0; j < nParam; j++)
				trace[j] = 0;
			for (int i = 0; i < x_array.length; i++) {

				Ydata[i] = y_array[i] / baselineMort;
				fitted[i] = (currentRRbegin - currentRRend)
						* Math.exp(-currentAlfa * x_array[i]) + currentRRend;
				delRRbegin[i] = Math.exp(-currentAlfa * x_array[i]);
				delRRend[i] = 1 - Math.exp(-currentAlfa * x_array[i]);
				delAlfa[i] = -x_array[i] * (currentRRbegin - currentRRend)
						* Math.exp(-currentAlfa * x_array[i]);
				delY[i] = Ydata[i] - fitted[i];
				Criterium += (delY[i] * delY[i]) * W[i];
				trace[0] += delAlfa[i];
				if (nParam == 2 && beginRR == -1)
					trace[1] += delRRend[i] * delRRend[i] * W[i];
				if (nParam == 2 && endRR == -1)
					trace[1] += delRRbegin[i] * delRRbegin[i] * W[i];
				if (nParam == 3)
					trace[2] += delRRend[i] * delRRend[i] * W[i];
			}
			if (nParam == 1)
				for (int k = 0; k < x_array.length; k++) {

					jMat[k][0] = delAlfa[k];
				}
			if (nParam == 2 && endRR == -1)
				for (int k = 0; k < x_array.length; k++) {

					jMat[k][0] = delAlfa[k];
					jMat[k][1] = delRRend[k];
				}
			if (nParam == 2 && beginRR == -1)
				for (int k = 0; k < x_array.length; k++) {

					jMat[k][0] = delAlfa[k];
					jMat[k][1] = delRRbegin[k];
				}
			if (nParam == 3)
				for (int k = 0; k < x_array.length; k++) {

					jMat[k][0] = delAlfa[k];
					jMat[k][1] = delRRbegin[k];
					jMat[k][2] = delRRend[k];
				}
			resultReg = weightedRegression(delY, jMat, W);
			oldCriterium = Criterium;
			old1 = currentAlfa;
			old2 = currentRRend;
			old3 = +currentRRbegin;
			int iter2 = 0;
			if (lambda > 10)
				lambda = 8;
			while (Criterium >= oldCriterium && iter2 < 50) {
				Criterium = 0;
				++iter2;
				currentAlfa = old1 + resultReg[0] / lambda;
				if (endRR == -1 && nParam == 2)
					currentRRend = old2 + resultReg[1] / lambda;
				if (endRR == -1 && nParam == 3)
					currentRRend = old2 + resultReg[2] / lambda;
				if (beginRR == -1)
					currentRRbegin = old3 + resultReg[1] / lambda;
				if (currentRRend < 0 && endRR == -1)
					currentRRend = 0.001;
				if (currentRRbegin < 0 && beginRR == -1)
					currentRRbegin = 0.001;
				if (currentAlfa < 0.001)
					currentAlfa = 0.001; /*
										 * this forces a linear model with time
										 * in case of inconsistent data that is
										 * data for which the time dependency
										 * does not fit the model
										 * (r1-r2)exp(-alfat) +r2
										 */

				for (int i = 0; i < x_array.length; i++) {
					delY[i] = Ydata[i]
							- ((currentRRbegin - currentRRend)
									* Math.exp(-currentAlfa * x_array[i]) + currentRRend);
					Criterium += (delY[i] * delY[i]) * W[i];
				}
				if (Criterium >= oldCriterium)
					lambda = lambda * 2;
				this.log
						.debug(" lambda " + lambda + " halvingsteps = " + iter2);
			}

			this.log
					.debug(" non-linear regression other cause mortality: iteration "
							+ iter + " criterium = " + Criterium);
			this.log.debug(" lambda " + lambda + " halvingsteps = " + iter2);
			this.log.debug("alfa " + currentAlfa + " RR end " + currentRRend
					+ " RR begin " + currentRRbegin);
			if (lambda > 1)
				lambda = lambda / 2;
			if (Math.abs(old1 - currentAlfa) / old1 < 0.001
					&& Math.abs(old2 - currentRRend) / old2 < 0.001
					&& Math.abs(old3 - currentRRbegin) / old3 < 0.001)
				break;
			else if (iter == 499)
				this.log
						.fatal(" non-linear regression other cause mortality did not converge in 500 iterations "
								+ " results: alfa "
								+ currentAlfa
								+ " RR end "
								+ currentRRend
								+ " RR begin "
								+ currentRRbegin
								+ " criterium = " + Criterium);
		}
		result[0] = currentRRbegin;
		result[1] = currentRRend;
		result[2] = currentAlfa;

		return result;
	}

	/**
	 * this method does a weighted regression
	 * 
	 * @param y_array
	 *            array with values of the dependent variable
	 * @param x_array
	 *            matrix with values of the independent variable. First index
	 *            gives datapoint (record) number, second index is for the
	 *            variable
	 * @param w
	 *            array with values of the weight variable
	 * @return fitted regression coefficients
	 * @throws Exception
	 *             if dimensions do not match
	 */
	public double[] weightedRegression(double[] y_array, double[][] x_array,
			double[] w) throws Exception {

		// check dimensions //
		if (y_array.length != w.length)
			throw new Exception(
					" Array lengths of y and weights differ in method weighted regression");
		if (x_array.length != w.length)
			throw new Exception(
					" Array lengths of x and weights differ in method weighted regression");
		if (y_array.length != x_array.length)
			throw new Exception(
					" Array lengths of x and y differ in method weighted regression");

		for (int i = 0; i < w.length; i++) {
			double weight = Math.sqrt(w[i]);
			y_array[i] = weight * y_array[i];
			for (int j = 0; j < x_array[0].length; j++)
				x_array[i][j] = x_array[i][j] * weight;
		}
		;

		Matrix X = new Matrix(x_array);
		Matrix Y = new Matrix(y_array, y_array.length);
		Matrix XT = X.transpose();
		Matrix XX = XT.times(X);
		Matrix inverseXX = XX.inverse();
		Matrix XY = XT.times(Y);
		// beta are the regression coefficients;
		Matrix Beta = inverseXX.times(XY);
		double coef[] = Beta.getColumnPackedCopy();
		return coef;
	};

	/**
	 * This method calculates the prevalence of not cured disease, by
	 * back-calculating the survivors from earlier agegroups
	 * 
	 * @param inc
	 *            : array [age][sex] with total incidence of the disease
	 *            (cured+non cured)
	 * @param prev
	 *            : array [age][sex] with total prevalence of the disease
	 *            (cured+non cured)
	 * @param excessmort
	 *            : array [age][sex]with excess mortality of the non cured
	 *            patients
	 * @param curedFraction
	 *            :array [age][sex]of the fraction of incident cases that can be
	 *            considered cured
	 * @return not cured prevalence rate; this is forced to lie between 0 and
	 *         the input prevalence rate
	 */
	private double[] calculateNotCuredPrevalence(float inc[], float prev[],
			float excessmort[], float[] curedFraction) {
		int n = inc.length;
		double[] notCuredPrev = new double[n];
		double[] CuredPrevFraction = new double[n];
		double expDifferenceIncExcessMort;
		notCuredPrev[0] = (1 - curedFraction[0]) * prev[0];
		for (int a = 1; a < n; a++) {
			// TODO Testen van deze methode (kloppen berekeningen)
			// finci = ((p0 * em - i) * exp((i - em) * time) + i * (1 - p0)) /
			// ((p0 * em - i) * exp((i - em) * time) + em * (1 - p0)) + p0 - p5
			expDifferenceIncExcessMort = Math.exp((1 - curedFraction[a - 1])
					* inc[a - 1] - excessmort[a - 1]);
			if ((1 - curedFraction[a - 1]) * inc[a - 1] != excessmort[a - 1])

				notCuredPrev[a] = ((notCuredPrev[a - 1] * excessmort[a - 1] - (1 - curedFraction[a - 1])
						* inc[a - 1])
						* expDifferenceIncExcessMort + (1 - curedFraction[a - 1])
						* inc[a - 1] * (1 - notCuredPrev[a - 1]))
						/ ((notCuredPrev[a - 1] * excessmort[a - 1] - (1 - curedFraction[a - 1])
								* inc[a - 1])
								* expDifferenceIncExcessMort + excessmort[a - 1]
								* (1 - notCuredPrev[a - 1]));
			/*
			 * this should be checked on whether this is OK! moet formule worden
			 * voor em=inc. hieronder is i=not cured
			 * 
			 * (p0 + i t - i p0 t)/(1 + i t - i p0 t) TODO
			 */
			else
				notCuredPrev[a] = (notCuredPrev[a - 1] + (1 - curedFraction[a - 1])
						* inc[a - 1] * (1 - notCuredPrev[a - 1]))
						/ (1 + (1 - curedFraction[a - 1]) * inc[a - 1]
								* (1 - notCuredPrev[a - 1]));
			;
			if (prev[a] != 0)
				CuredPrevFraction[a] = (prev[a] - notCuredPrev[a]) / prev[a];
			else
				CuredPrevFraction[a] = 0;
			if (CuredPrevFraction[a] > 1) {
				CuredPrevFraction[a] = 1.0;
				notCuredPrev[a] = 0;

			}
			if (CuredPrevFraction[a] < 0) {
				CuredPrevFraction[a] = 0.0;
				notCuredPrev[a] = prev[a];

			}
		}
		return notCuredPrev;
	}

	/**
	 * @param e
	 */
	private void displayWarningMessage(String message) {
		Shell shell = new Shell(this.parentShell);
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox.setMessage(message);
		messageBox.open();

	}

	/**
	 * @return riskType (1=categorical, 2=continuous, 3=compound
	 */
	public int getRiskType() {
		return this.riskType;
	}

	/**
	 * @param riskType
	 */
	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	/**
	 * @return risktype distribution (normal or lognormal)
	 */
	public String getRiskTypeDistribution() {
		return this.RiskTypeDistribution;
	}

	/**
	 * @param riskTypeDistribution
	 */
	public void setRiskTypeDistribution(String riskTypeDistribution) {
		this.RiskTypeDistribution = riskTypeDistribution;
	}

	/**
	 * @return index of the class with the duration attached to it
	 */
	public int getDurationClass() {
		return this.durationClass;
	}

	/**
	 * @param durationClass
	 */
	public void setDurationClass(int durationClass) {
		this.durationClass = durationClass;
	}

	/**
	 * @return reference value for the continuous risk factor (value for which
	 *         the RR = 1)
	 */
	public float getRefClassCont() {
		return this.refClassCont;
	}

	/**
	 * @param refClassCont
	 */
	public void setRefClassCont(float refClassCont) {
		this.refClassCont = refClassCont;
	}

	/**
	 * @return number of diseases in the cluster
	 */
	public int getNCluster() {
		return this.nCluster;
	}

	/**
	 * @param cluster
	 */
	public void setNCluster(int cluster) {
		this.nCluster = cluster;
	}

	/**
	 * @return object containing the structure of the disease cluster
	 */
	public DiseaseClusterStructure[] getClusterStructure() {
		return this.clusterStructure;
	}

	/**
	 * @param clusterStructure
	 */
	public void setClusterStructure(DiseaseClusterStructure[] clusterStructure) {
		this.clusterStructure = clusterStructure;
	}

	/**
	 * @return relative risks for diseases on other diseases . NB this is an
	 *         irregular array indexes: age, gender, cluster, from, to
	 */
	public float[][][][][] getRelRiskDiseaseOnDisease() {
		return DynamoLib.deepcopy(this.relRiskDiseaseOnDisease);
	}

	/**
	 * @param relRiskDiseaseOnDisease
	 */
	public void setRelRiskDiseaseOnDisease(
			float[][][][][] relRiskDiseaseOnDisease) {
		this.relRiskDiseaseOnDisease = relRiskDiseaseOnDisease;
	}

	/**
	 * @return baselineincidence for all diseases. indexes are age, sex, disease
	 */
	public float[][][] getBaselineIncidence() {
		return DynamoLib.deepcopy(this.baselineIncidence);
	}

	/**
	 * @param baselineIncidence
	 */
	public void setBaselineIncidence(float[][][] baselineIncidence) {
		this.baselineIncidence = baselineIncidence;
	}

	/**
	 * @return baseline prevalence odds. Indexes are age, sex, diseasenumber
	 */
	public double[][][] getBaselinePrevalenceOdds() {
		return DynamoLib.deepcopy(this.baselinePrevalenceOdds);
	}

	/**
	 * @param baselinePrevalenceOdds
	 */
	public void setBaselinePrevalenceOdds(double[][][] baselinePrevalenceOdds) {
		this.baselinePrevalenceOdds = baselinePrevalenceOdds;
	}

	/**
	 * @return relative risks on other mortality for a categorical riskfactor.
	 *         Indexes are age, sex, riskfactor category
	 */
	public float[][][] getRelRiskOtherMort() {
		return DynamoLib.deepcopy(this.relRiskOtherMort);
	}

	/**
	 * @param relRiskOtherMort
	 */
	public void setRelRiskOtherMort(float[][][] relRiskOtherMort) {
		this.relRiskOtherMort = relRiskOtherMort;
	}

	/**
	 * @return relative risks for other cause mortality for a continuous risk
	 *         factor. Indexes are age, sex
	 */
	public float[][] getRelRiskOtherMortCont() {
		return DynamoLib.deepcopy(this.relRiskOtherMortCont);
	}

	/**
	 * @param relRiskOtherMortCont
	 */
	public void setRelRiskOtherMortCont(float[][] relRiskOtherMortCont) {
		this.relRiskOtherMortCont = relRiskOtherMortCont;
	}

	/**
	 * @return relative risk on other mortality in the duration class at time
	 *         infinity (compound risk factor)
	 */
	public float[][] getRelRiskOtherMortEnd() {
		return DynamoLib.deepcopy(this.relRiskOtherMortEnd);
	}

	/**
	 * @param relRiskOtherMortEnd
	 */
	public void setRelRiskOtherMortEnd(float[][] relRiskOtherMortEnd) {
		this.relRiskOtherMortEnd = relRiskOtherMortEnd;
	}

	/**
	 * @return relative risk on other mortality in the duration class at time
	 *         zero (compound risk factor)
	 */
	public float[][] getRelRiskOtherMortBegin() {
		return DynamoLib.deepcopy(this.relRiskOtherMortBegin);
	}

	/**
	 * @param relRiskOtherMortBegin
	 */
	public void setRelRiskOtherMortBegin(float[][] relRiskOtherMortBegin) {
		this.relRiskOtherMortBegin = relRiskOtherMortBegin;
	}

	/**
	 * @return the alfa coefficient (decrease of relative risk with duration)
	 *         for other cause mortality. indexes are age and sex
	 */
	public float[][] getAlfaOtherMort() {
		return DynamoLib.deepcopy(this.alfaOtherMort);
	}

	/**
	 * @param alfaOtherMort
	 */
	public void setAlfaOtherMort(float[][] alfaOtherMort) {
		this.alfaOtherMort = alfaOtherMort;
	}

	/**
	 * @return baseline other cause mortality rate. indexes are age and gender
	 */
	public float[][] getBaselineOtherMortality() {
		return DynamoLib.deepcopy(this.baselineOtherMortality);
	}

	/**
	 * @param baselineOtherMortality
	 */
	public void setBaselineOtherMortality(float[][] baselineOtherMortality) {
		this.baselineOtherMortality = baselineOtherMortality;
	}

	/**
	 * @return attributable mortality rate. indexes are age, gender,
	 *         diseasenumber
	 */
	public float[][][] getAttributableMortality() {
		return DynamoLib.deepcopy(this.attributableMortality);
	}

	/**
	 * @param attributableMortality
	 */
	public void setAttributableMortality(float[][][] attributableMortality) {
		this.attributableMortality = attributableMortality;
	}

	/**
	 * @return relative risk for categorical risk factor. Indexes are age, sex,
	 *         from, to=diseasenumber
	 */
	public float[][][][] getRelRiskClass() {
		return DynamoLib.deepcopy(this.relRiskClass);
	}

	/**
	 * @param relRiskClass
	 */
	public void setRelRiskClass(float[][][][] relRiskClass) {
		this.relRiskClass = relRiskClass;
	}

	/**
	 * @return relative risk for a continuous risk factor. indexes are age, sex
	 *         and diseaseNumber
	 */
	public float[][][] getRelRiskContinue() {
		return DynamoLib.deepcopy(this.relRiskContinue);
	}

	/**
	 * @param relRiskContinue
	 */
	public void setRelRiskContinue(float[][][] relRiskContinue) {
		this.relRiskContinue = relRiskContinue;
	}

	/**
	 * @return prevalence rate of the riskfactor. indexes are age sex and
	 *         riskfactor class
	 */
	public float[][][] getPrevRisk() {
		return DynamoLib.deepcopy(this.prevRisk);
	}

	/**
	 * @param prevRisk
	 */
	public void setPrevRisk(float[][][] prevRisk) {
		this.prevRisk = prevRisk;
	}

	/**
	 * @return mean value of the risk factor. Indexes are age and sex.
	 */
	public float[][] getMeanRisk() {
		return DynamoLib.deepcopy(this.meanRisk);
	}

	/**
	 * @param meanRisk
	 */
	public void setMeanRisk(float[][] meanRisk) {
		this.meanRisk = meanRisk;
	}

	/**
	 * @return standard deviation of the continous riskfactor. indexes age, sex
	 */
	public float[][] getStdDevRisk() {
		return DynamoLib.deepcopy(this.stdDevRisk);
	}

	/**
	 * @param stdDevRisk
	 */
	public void setStdDevRisk(float[][] stdDevRisk) {
		this.stdDevRisk = stdDevRisk;
	}

	/**
	 * @return offset of a lognormally distributed continuous riskfactor.
	 *         indexes are age and gender
	 */
	public float[][] getOffsetRisk() {
		return DynamoLib.deepcopy(this.offsetRisk);
	}

	/**
	 * @param input
	 */
	public void setOffsetRisk(float[][] input) {
		this.offsetRisk = input;
	}

	/**
	 * @return relative risk at the beginning of the duration in the duration
	 *         class of a compound riskfactor indexes are age, sex, and disease
	 */
	public float[][][] getRelRiskDuurBegin() {
		return DynamoLib.deepcopy(this.relRiskDuurBegin);
	}

	/**
	 * @param relRiskDuurBegin
	 */
	public void setRelRiskDuurBegin(float[][][] relRiskDuurBegin) {
		this.relRiskDuurBegin = relRiskDuurBegin;
	}

	/**
	 * @return relative risk at the end of the duration in the duration class of
	 *         a compound riskfactor indexes are age, sex, and disease
	 */
	public float[][][] getRelRiskDuurEnd() {
		return DynamoLib.deepcopy(this.relRiskDuurEnd);
	}

	/**
	 * @param relRiskDuurEnd
	 *            relative risk at time plus infinity in the duration class
	 */
	public void setRelRiskDuurEnd(float[][][] relRiskDuurEnd) {
		this.relRiskDuurEnd = relRiskDuurEnd;
	}

	/**
	 * @return the alfa value for decreasing of the relative risk with duration
	 */
	public float[][][] getAlfaDuur() {
		return DynamoLib.deepcopy(this.alfaDuur);
	}

	/**
	 * @param alfaDuur
	 */
	public void setAlfaDuur(float[][][] alfaDuur) {
		this.alfaDuur = alfaDuur;
	}

	/**
	 * @return initial frequency of durations in the duration class of a
	 *         compound risk factor
	 */
	public float[][][] getDuurFreq() {
		return DynamoLib.deepcopy(this.duurFreq);
	}

	/**
	 * @param duurFreq
	 */
	public void setDuurFreq(float[][][] duurFreq) {
		this.duurFreq = duurFreq;
	}

	/**
	 * @return mean Drift for a continuous risk factor
	 */
	public float[][] getMeanDrift() {
		return DynamoLib.deepcopy(this.meanDrift);
	}

	/**
	 * @param meanDrift
	 */
	public void setMeanDrift(float[][] meanDrift) {
		this.meanDrift = meanDrift;
	}

	/**
	 * @return transition matrix for a categorical risk factor. indexes are age,
	 *         sex, from, to
	 */
	public float[][][][] getTransitionMatrix() {
		return DynamoLib.deepcopy(this.transitionMatrix);
	}

	/**
	 * @param transitionMatrix
	 */
	public void setTransitionMatrix(float[][][][] transitionMatrix) {
		this.transitionMatrix = transitionMatrix;
	}

	// public float[][][] getCuredFraction() {
	// return DynamoLib.deepcopy(curedFraction);
	// }

	// public void setCuredFraction(float[][][] curedFraction) {
	// this.curedFraction = curedFraction;
	// }

	/**
	 * @return baseline fatal incidences. indexes are age sex and diseasenumber
	 */
	public float[][][] getBaselineFatalIncidence() {
		return DynamoLib.deepcopy(this.baselineFatalIncidence);
	}

	/**
	 * @param baselineFatalIncidence
	 */
	public void setBaselineFatalIncidence(float[][][] baselineFatalIncidence) {
		this.baselineFatalIncidence = baselineFatalIncidence;
	}

	/**
	 * @return boolean whether the riskfactor has a zero transition
	 */
	public boolean isZeroTransition() {
		return this.zeroTransition;
	}

	/**
	 * @param zeroTransition
	 */
	public void setZeroTransition(boolean zeroTransition) {
		this.zeroTransition = zeroTransition;
	}

	/**
	 * @return standarddeviation drift for a continuous riskfactor
	 */
	public float[][] getStdDrift() {
		return this.stdDrift;
	}

	/**
	 * @param stdDrift
	 *            standarddeviation drift for a continuous riskfactor
	 */
	public void setStdDrift(float[][] stdDrift) {
		this.stdDrift = stdDrift;
	}

	/**
	 * @return offset drift for a longnormally distributed continuous riskfactor
	 */
	public float[][] getOffsetDrift() {
		return this.offsetDrift;
	}

	/**
	 * @param offsetDrift
	 */
	public void setOffsetDrift(float[][] offsetDrift) {
		this.offsetDrift = offsetDrift;
	}

	/**
	 * @return array with initial population (one for each scenario)
	 */
	public Population[] getInitialPopulation() {
		return this.initialPopulation;
	}

	/**
	 * @param initialPopulation
	 */
	public void setInitialPopulation(Population[] initialPopulation) {
		this.initialPopulation = initialPopulation;
	}

}
