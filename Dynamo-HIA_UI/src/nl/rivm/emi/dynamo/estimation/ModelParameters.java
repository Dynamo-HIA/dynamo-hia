package nl.rivm.emi.dynamo.estimation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	static public final int CATEGORICAL = 1;
	static public final int CONTINUOUS = 2;
	static public final int COMPOUND = 3;
	static public final String SEPARATOR = ";";
	// Fields containing the estimated model parameters and other info needed to
	// run the model
	Log log = LogFactory.getLog(getClass().getName());
	DynSimRunPRInterface dsi;
	private int nSim = 100;
	private int riskType = -1;
	private int nWarningsDisability = 0;
	private int nWarningsMort = 0;
	private int nWarningRRdis = 0;
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
	private float alphaOtherMort[][] = new float[96][2];
	private float baselineOtherMortality[][] = new float[96][2];
	private float baselineMortality[][] = new float[96][2]; // geen setter maken
	private float[] attributableMortality[][] = new float[96][2][];
	private float[][] relRiskClass[][] = new float[96][2][][];
	// here the third index is rc {risk factor class}, and the fourth d
	// (disease);
	private float[][][] relRiskContinue = new float[96][2][];
	private float prevRisk[][][] = new float[96][2][];;

	final StringBuilder toCVS = new StringBuilder("");
	/**
	 * mean of the risk factor, for the lognormal distribution this is the mean
	 * on the logscale
	 */
	private float[][] meanRisk = new float[96][2];

	/**
	 * standard deviation of the risk factor, for the lognormal distribution
	 * this is the mean on the logscale
	 */
	private float[][] stdDevRisk = new float[96][2];
	private float[][] offsetRisk = new float[96][2];
	private String[] diseaseNames;
	/*
	 * disease Ability is defined as 1 - fraction with disability (due to the
	 * diseases) It can also contain the daly weight (where 1 = perfect health,
	 * 0= like being death
	 */
	private float[][][] diseaseAbility = new float[96][2][];
	private float[][] baselineAbility = new float[96][2];
	/* for disability we do not have a duration option */
	private float[][][] riskFactorAbilityRRcat = new float[96][2][];
	private float[][] riskFactorAbilityRRcont = new float[96][2];
	private float[][] riskFactorAbilityRRend = new float[96][2];
	private float[][] riskFactorAbilityRRbegin = new float[96][2];
	private float[][] riskFactorAbilityAlpha = new float[96][2];
	private float[][][] relRiskDuurBegin = new float[96][2][];
	private float[][][] relRiskDuurEnd = new float[96][2][];
	private float[][][] alphaDuur = new float[96][2][];
	private float[][][] duurFreq = new float[96][2][];
	private float[][] meanDrift = new float[96][2];
	private float[][] stdDrift = new float[96][2];
	private float[][] offsetDrift = new float[96][2];
	private float[][][][] transitionMatrix = new float[96][2][][];
	private boolean zeroTransition;
	// TODO
	private Population[] initialPopulation;
	// empty Constructor
	// private Shell parentShell;
	// private DynSimRunPRInterface dsi = null;
	private String globalBaseDir;
	private boolean warningflag2 = true;
	private boolean warningflag3 = true;
	private boolean warningflag4 = true;
	private boolean warningflag5 = true;
	private boolean warningflag6 = true;
	private boolean negativeMortality = false;

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
	 * @throws IOException
	 * 
	 */
	public ScenarioInfo estimateModelParameters(String simulationName
	/* Shell parentShell */, DynSimRunPRInterface dsi)
			throws DynamoInconsistentDataException,
			DynamoConfigurationException, IOException {

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
		this.dsi = dsi;
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

		estimateModelParameters(this.nSim, inputData);
		/* write the fitted other mortality to a file for further inspection */

		if (!this.toCVS.toString().equals("")) {
			String dirName = this.globalBaseDir + File.separator
					+ "Simulations" + File.separator + simulationName
					+ File.separator + "parameters";
			File dir = new File(dirName);
			if (!dir.exists())
				dir.mkdir();
			FileWriter writer = new FileWriter(dirName + File.separator
					+ "otherMortData.csv");
			writer.append(this.toCVS.toString());
			writer.flush();
			writer.close();
		}
		/* put estimated daly parameters in scenarioInfo */
		scenInfo.setBaselineAbility(this.baselineAbility);
		if (this.getNDiseases() > 0)
			scenInfo.setDiseaseAbility(this.diseaseAbility);
		else
			scenInfo.setDiseaseAbility(null);
		scenInfo.setRelRiskAbilityCat(this.riskFactorAbilityRRcat);
		scenInfo.setRelRiskAbilityCont(this.riskFactorAbilityRRcont);
		scenInfo.setRelRiskAbilityBegin(this.riskFactorAbilityRRbegin);
		scenInfo.setRelRiskAbilityEnd(this.riskFactorAbilityRRend);
		scenInfo.setAlphaAbility(this.riskFactorAbilityAlpha);
		/** * 3. write xml files needed by the simulation module */

		SimulationConfigurationFactory s = new SimulationConfigurationFactory(
				simulationName);
		s.manufactureSimulationConfigurationFile(this, scenInfo);
		this.log.debug("SimulationConfigurationFile written ");
		s.manufactureCharacteristicsConfigurationFile(this);
		this.log.debug("CharacteristicsConfigurationFile written ");
		s.manufactureUpdateRuleConfigurationFiles(this, scenInfo);
		this.log.debug("UpdateRuleConfigurationFile written ");

		return scenInfo;

	}

	/**
	 * estimateModelParameters (int nSim, InputData inputData, Shell
	 * parentShell) estimates the ModelParameters from the object inputData
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
	public void estimateModelParameters(int nSim, InputData inputData)
			throws DynamoInconsistentDataException {

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
						/*
						 * NB stdDevRisk in inputData is the standard deviation
						 * on the measured scale, while this.stdDevRisk is the
						 * standard deviation on the log-scale!
						 */
						this.meanRisk[a][g] = (float) (0.5 * (Math.log(Math
								.pow((double) inputData.getStdDevRisk()[a][g],
										2))
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
		// this.parentShell = parentShell;
		this.nCluster = inputData.getNCluster();
		this.clusterStructure = inputData.clusterStructure;
		this.durationClass = inputData.getIndexDuurClass();
		int nRiskClasses;
		if (this.riskType != 2)
			nRiskClasses = inputData.getPrevRisk()[0][0].length;
		else
			nRiskClasses = 1;
		this.log.debug("before split");
		splitCuredDiseases(inputData);
		this.log.debug("after split");
		/* now extract the names */
		makeDiseaseNames(inputData.getClusterStructure());
		if (inputData.getRiskType() != 2)
			this.transitionMatrix = new float[96][2][nRiskClasses][inputData
					.getPrevRisk()[0][0].length];
		NettTransitionRateFactory factory = new NettTransitionRateFactory();
		/* set up progress bar for this part of the calculations */

		// Shell shell = new Shell(parentShell);
		// shell.setText("Parameter estimation in progress .......");
		// shell.setLayout(new FillLayout());
		// shell.setSize(600, 50);
		//
		// ProgressBar bar = new ProgressBar(shell, SWT.NULL);
		// bar.setBounds(10, 10, 200, 32);
		// bar.setMinimum(0);
		//
		// shell.open();
		ProgressIndicatorInterface pii = dsi
				.createProgressIndicator("Parameter estimation in progress .......");
		// bar.setMaximum(100);
		pii.setMaximum(100);

		for (int a = 0; a < 96; a++) {
			// bar.setSelection(a);
			pii.update(a);
			for (int g = 0; g < 2; g++) {
				this.log.debug("before first estimate");
				estimateModelParametersForSingleAgeGroup(nSim, inputData, a, g,
						dsi);

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
						if (a == 79 && g == 1) {

							int stop = 0;
							stop++;

						}

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

		// bar.setSelection(96);
		pii.update(96);
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
						inputData.getRelRiskMortCont(), this.refClassCont,
						inputData.getTrendInDrift());
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
		// bar.setSelection(97);
		pii.update(97);
		/*
		 * while (!shell.isDisposed ()) { if (!display.readAndDispatch ())
		 * display.sleep (); }
		 */
		// shell.close();
		pii.dispose();
	};

	private void makeDiseaseNames(DiseaseClusterStructure[] clusterStructure2) {

		this.diseaseNames = new String[getNDiseases()];
		int currentDisease = 0;
		if (clusterStructure != null)
			for (int c = 0; c < clusterStructure.length; c++) {

				ArrayList<String> currentNames = clusterStructure[c]
						.getDiseaseName();
				for (String name : currentNames) {
					this.diseaseNames[currentDisease] = name;
					currentDisease++;
				}

			}

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
						float[][] temp = new float[RRcat.length][2];
						for (int cat = 0; cat < RRcat.length; cat++) {
							temp[cat][0] = RRcat[cat][0];
							temp[cat][1] = RRcat[cat][0];
						}
						newdata.setRelRiskCat(temp);

						RRcont = inputData.getClusterData()[a][g][c]
								.getRelRiskCont()[0];
						float[] temp2 = new float[2];

						temp2[0] = RRcont;
						temp2[1] = RRcont;
						newdata.setRelRiskCont(temp2);

						temp2 = new float[2];
						RRduurBegin = inputData.getClusterData()[a][g][c]
								.getRelRiskDuurBegin()[0];
						temp2[0] = RRduurBegin;
						temp2[1] = RRduurBegin;
						newdata.setRelRiskDuurBegin(temp2);
						temp2 = new float[2];
						RRduurEnd = inputData.getClusterData()[a][g][c]
								.getRelRiskDuurEnd()[0];
						temp2[0] = RRduurEnd;
						temp2[1] = RRduurEnd;
						newdata.setRelRiskDuurEnd(temp2);
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
	 * @param dsi
	 *            TODO
	 * @throws DynamoInconsistentDataException
	 */
	public void estimateModelParametersForSingleAgeGroup(int nSim,
			InputData inputData, int age, int sex, DynSimRunPRInterface dsi)
			throws DynamoInconsistentDataException {

		/**
		 * The parameter estimation proceeds using a simulated population that
		 * has a simulated risk factor distribution. Per age and gender group at
		 * least 100 persons are generated, in order to let the estimation have
		 * a minimal accuracy. The estimation proceeds in 5 phases.
		 * <p>
		 * Phase 1. In the first phase of parameter estimation, values of the
		 * risk factors are randomly drawn for each person in the simulated
		 * population using a random generator. Based on these simulated values,
		 * for each person a relative risk is calculated for each disease.
		 * Baseline prevalence rates for the independent diseases then can be
		 * calculated from this by calculating the average relative risk and
		 * dividing the incidence or prevalence of this disease by the average
		 * relative risks
		 * </p>
		 */

		int nRiskCat = 1;
		if (this.riskType != 2)
			nRiskCat = inputData.getPrevRisk()[0][0].length;
		int nDiseases = getNDiseases(inputData);

		/* now copy data directly from DiseaseClusterData to parameter fields */
		if (nDiseases > 0) {
			this.attributableMortality[age][sex] = new float[nDiseases];
			this.relRiskContinue[age][sex] = new float[nDiseases];
			this.relRiskClass[age][sex] = new float[nRiskCat][nDiseases];
			this.relRiskDuurBegin[age][sex] = new float[nDiseases];
			this.relRiskDuurEnd[age][sex] = new float[nDiseases];
			this.alphaDuur[age][sex] = new float[nDiseases];
			this.diseaseAbility[age][sex] = new float[nDiseases];
			this.relRiskDiseaseOnDisease[age][sex] = new float[this.nCluster][][];

		}
		boolean withRRmort = inputData.isWithRRForMortality();
		boolean withRRdisability = inputData.isWithRRForDisability();
		if (withRRdisability && nWarningRRdis == 0) {
			displayWarningMessage(
					"WARNING:\nRR for disability is not (yet) implemented so RR for disability is ignored",
					dsi);
			nWarningRRdis++;
		}
		withRRdisability = false;
		double log2 = Math.log(2.0); // keep outside loops to prevent
		// recalculation
		/* put prevalence also in a single array for easy access */
		float[] excessMortality = new float[nDiseases];
		float[] diseasePrevalence = new float[nDiseases];
		if (nDiseases > 0)
			for (int c = 0; c < this.nCluster; c++)
				for (int dc = 0; dc < this.clusterStructure[c].getNInCluster(); dc++) {
					int dNumber = this.clusterStructure[c].getDiseaseNumber()[dc];

					excessMortality[dNumber] = inputData.getClusterData()[age][sex][c]
							.getExcessMortality()[dc];
					this.diseaseAbility[age][sex][dNumber] = inputData
							.getClusterData()[age][sex][c].getAbility()[dc];
				}
		if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
			this.relRiskContinue[age][sex] = new float[nDiseases];
		if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
			this.refClassCont = 0;
		if (nDiseases > 0)
			for (int c = 0; c < this.nCluster; c++) {

				this.relRiskDiseaseOnDisease[age][sex][c] = inputData
						.getClusterData()[age][sex][c].getRRdisExtended();

				for (int d = 0; d < inputData.clusterStructure[c]
						.getNInCluster(); d++) {
					int dNumber = inputData.clusterStructure[c]
							.getDiseaseNumber()[d];
					if (this.riskType == 3) {
						this.relRiskDuurEnd[age][sex][dNumber] = inputData
								.getClusterData()[age][sex][c]
								.getRelRiskDuurEnd()[d];
						this.relRiskDuurBegin[age][sex][dNumber] = inputData
								.getClusterData()[age][sex][c]
								.getRelRiskDuurBegin()[d];
						this.alphaDuur[age][sex][dNumber] = inputData
								.getClusterData()[age][sex][c].getAlpha()[d];

					}
					diseasePrevalence[this.clusterStructure[c]
							.getDiseaseNumber()[d]] = inputData
							.getClusterData()[age][sex][c].getPrevalence()[d];

					if (inputData.getRiskType() == 1
							|| inputData.getRiskType() == 3)
						for (int i = 0; i < nRiskCat; i++)
							this.relRiskClass[age][sex][i][dNumber] = inputData
									.getClusterData()[age][sex][c]
									.getRelRiskCat()[i][d];
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
		this.riskFactorAbilityRRcat[age][sex] = new float[nRiskCat];
		this.riskFactorAbilityRRcont[age][sex] = 1;
		this.riskFactorAbilityRRbegin[age][sex] = 1;
		this.riskFactorAbilityRRend[age][sex] = 1;
		this.riskFactorAbilityAlpha[age][sex] = 0;
		double baselineDisabilityOdds = 0;

		if (inputData.getRiskType() == 1)
			nSim = nRiskCat;
		if (inputData.getRiskType() == 3)
			nSim = nRiskCat + inputData.getDuurFreq()[age][sex].length - 1;
		if (inputData.getRiskType() == 2 && nSim < 100)
			nSim = 100;
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

		double[] abilityFromRiskFactor = new double[nSim];
		double[] abilityFromDiseases = new double[nSim];
		double totalAbilityFromRiskFactor = 0;
		boolean oneDuration = false; /*
									 * one Duration is a flag that sees whether
									 * there is only a single duration present
									 */
		if (inputData.getRiskType() == 3) {
			double checkSum = 0;
			int nFilled = 0;
			/*
			 * despite trying this not only changes the duurFreq, but also the
			 * original version!
			 */
			for (int k = 0; k < inputData.getDuurFreq()[age][sex].length; k++) {

				checkSum += this.duurFreq[age][sex][k];
				if (this.duurFreq[age][sex][k] > 0)
					nFilled++;
			}
			if (nFilled == 1)
				oneDuration = true;
			/* there is a tolerance of 2% */
			if (Math.abs(checkSum - 1) > 0.01)
				throw new DynamoInconsistentDataException(
						"durations given for compound risk factor class do not sum to 100% but to "
								+ 100 * checkSum + "%");
			/* if there is a small deviation, normalize */
			if (Math.abs(checkSum - 1) > 0.00001) {
				for (int k = 0; k < inputData.getDuurFreq()[age][sex].length; k++)

					this.duurFreq[age][sex][k] = (float) (this.duurFreq[age][sex][k] / checkSum);

			}
		}
		if (oneDuration
				&& warningflag6
				&& (withRRdisability || withRRmort)
				&& inputData.getPrevRisk()[age][sex][inputData
						.getIndexDuurClass()] != 0) {
			warningflag6 = false;

			displayWarningMessage(
					"WARNING: \n100% of the initial population has the same duration"
							+ ". \nTherefore"
							+ " it is not possible to estimate a time dependent other mortality or "
							+ "other disability."
							+ "\nIn case other mortality/disability is requested, those relative risks will be made constant over time"
							+ "\nThis warning is give for age "
							+ age
							+ " and gender "
							+ sex
							+ "\nNo more warnings of this kind will be generated for "
							+ "other age and gender groups", dsi);
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
		double relRiskDisability[] = new double[nSim];
		/*
		 * odds ratio given by the user for person i on all disability,
		 * calculated for person i
		 */

		/* first loop over all individuals in the estimating population */
		/* this gives a first estimator for the baseline prevalence rate */
		/*
		 * also it calculates the first estimator of the baseline disability
		 * based on the riskfactor status and an odds ratio model for disability
		 */

		/* initialize necessary sum-variables etc. */

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
			// auxilary variable to calculate the baselineOdds of disability
			double sumRRDisability = 0;

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
				if (!withRRdisability)
					relRiskDisability[i] = 1;
				if (inputData.getRiskType() == 1 && withRRdisability)
					relRiskDisability[i] = inputData.getRRforDisabilityCat()[age][sex][i];
				if (inputData.getRiskType() == 2 && withRRdisability)

					relRiskDisability[i] = Math.pow(inputData
							.getRRforDisabilityCont()[age][sex],
							(riskfactor[i] - inputData.getRefClassCont()));

				if (inputData.getRiskType() == 3 && withRRdisability)

					relRiskDisability[i] = (inputData.getRRforDisabilityBegin()[age][sex] - inputData
							.getRRforDisabilityEnd()[age][sex])
							* Math
									.exp(-riskfactor[i]
											* inputData.getAlphaForDisability()[age][sex])
							+ inputData.getRRforDisabilityEnd()[age][sex];
				sumRRDisability += weight[i] * relRiskDisability[i];

				// Calculate relative risks based on only the riskfactor

				// loop over all clusters of diseases

				if (nDiseases > 0)
					for (int d = 0; d < nDiseases; d++) {

						if (inputData.getRiskType() == 3) {
							if (riskclass[i] == inputData.getIndexDuurClass()) {

								relRisk[i][d] = (this.relRiskDuurBegin[age][sex][d] - this.relRiskDuurEnd[age][sex][d])
										* Math.exp(-riskfactor[i]
												* this.alphaDuur[age][sex][d])
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
									relRiskMort[i] = inputData
											.getRelRiskMortCat()[age][sex][riskclass[i]];
							}
						} else

							relRisk[i][d] = Math.pow(
									this.relRiskContinue[age][sex][d],
									(riskfactor[i] - inputData
											.getRefClassCont()))
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
				/* if not RR for mortality, this is 1 */
				if (!inputData.isWithRRForMortality())
					relRiskMort[i] = 1;

				sumRRm += relRiskMort[i] * weight[i];
				/* sum of relRiskMort over all persons */

			} // end first loop over all individuals

			// calculate a first estimate of baseline prevalence for the
			// independent
			// diseases and baseline all cause mortality
			// the false indicates that this should be done for independent
			// diseases only

			;

			if (nDiseases > 0) {
				calculateBaselinePrev(inputData, age, sex, sumRR, false);
				calculateBaselineFatalIncidence(inputData, age, sex, sumRR,
						false);
			}
			if (inputData.isWithRRForMortality())
				this.baselineMortality[age][sex] = (float) (inputData
						.getMortTot()[age][sex] / sumRRm);
			else
				this.baselineMortality[age][sex] = (float) (inputData
						.getMortTot()[age][sex]);
			/* first estimate of baselineDisabilityOdds */
			baselineDisabilityOdds = (float) (inputData.getOverallDalyWeight()[age][sex] / sumRRDisability);

		}

		/*
		 * now repeat loop 1 iteratively to estimate the baseline odds for
		 * disease (prevalence) and disability. loop over all diseases with the
		 * exception of cases where the prevalence == 0; there the baseline odds
		 * stays 0
		 */

		/*
		 * first for diseases
		 */
		if (nDiseases > 0)
			for (int d = 0; d < nDiseases; d++) {
				int nIter = 0;
				double del = 100;
				if (diseasePrevalence[d] == 0)
					del = 0;
				/*
				 * if disease prevalence == 0 do not do anything but keep
				 * baseline odds ==0
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
							- (sumPrevCurrent - diseasePrevalence[d])
							/ sumDerivativePrevCurrent;
					del = Math.abs(this.baselinePrevalenceOdds[age][sex][d]
							- oldValue);
					++nIter;
				}// end iterative procedure for disease
			} // end loop over diseases

		/* repeat for disability */
		/* is inside {} as some variable names are used later on again */

		{
			int nIter = 0;
			double del = 100;
			if (age == 50) {
				int kk = 0;
				kk++;

			}
			double disabilityPrevalence = inputData.getOverallDalyWeight()[age][sex];
			/*
			 * if disability prevalence == 0 do not do anything but keep
			 * baseline odds ==0
			 */
			double sumPrevCurrent = 0;
			double sumDerivativePrevCurrent = 0;
			while (del > 0.00001 && nIter < 10) {
				sumPrevCurrent = 0;
				sumDerivativePrevCurrent = 0;
				for (int i = 0; i < nSim; i++) {
					sumPrevCurrent += weight[i]
							* relRiskDisability[i]
							* baselineDisabilityOdds
							/ (1 + relRiskDisability[i]
									* baselineDisabilityOdds);
					sumDerivativePrevCurrent += weight[i]
							* relRiskDisability[i]
							/ Math.pow((1 + relRiskDisability[i]
									* baselineDisabilityOdds), 2);
				}// end loop over all individuals
				double oldValue = baselineDisabilityOdds;
				baselineDisabilityOdds = oldValue
						- (sumPrevCurrent - disabilityPrevalence)
						/ sumDerivativePrevCurrent;
				del = Math.abs(baselineDisabilityOdds - oldValue);
				++nIter;
			}// end iterative procedure for disability
		} /* end of this part (temporary variables can be discarded */
		/*
		 * calculate the ability from the riskFactor for each person
		 */
		for (int i = 0; i < nSim; i++) {

			abilityFromRiskFactor[i] = (float) (1 - relRiskDisability[i]
					* baselineDisabilityOdds
					/ (1 + relRiskDisability[i] * baselineDisabilityOdds));

			/* this is to check results */
			totalAbilityFromRiskFactor += weight[i] * abilityFromRiskFactor[i];

		}
		if (Math.abs(totalAbilityFromRiskFactor
				+ inputData.getOverallDalyWeight()[age][sex] - 1) > 0.0001)
			log
					.fatal(" bug in program when calculating baseline disablity odds. Total disability given"
							+ " bij user = "
							+ inputData.getOverallDalyWeight()[age][sex]
							+ " but calculated"
							+ "from baselineDisabilityOdds = "
							+ (1 - totalAbilityFromRiskFactor));

		// //////////////////////////////////////////////einde first loop
		// /////////////////////////////

		// / start loop 2 //

		// ///////////////////////////////////////////////////////////////////////////////////////////

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

		if (nDiseases > 0)
			for (int i = 0; i < nSim; i++) {
				// calculate the probability of each independent disease
				// loop over all clusters and within the clusters over the
				// diseases
				// //
				for (int c = 0; c < inputData.getNCluster(); c++) {
					if (inputData.clusterStructure[c].isWithCuredFraction()) {

						/*
						 * diseases with cured fraction are seen as separate
						 * diseases, but have the same population at risk, and
						 * relative risk so the mean RR in the population at
						 * risk is the same
						 */
						int d = inputData.clusterStructure[c]
								.getDiseaseNumber()[0];
						probDisease[i][d] = this.baselinePrevalenceOdds[age][sex][d]
								* relRisk[i][d]
								/ (this.baselinePrevalenceOdds[age][sex][d]
										* relRisk[i][d] + 1);
						probDisease[i][d + 1] = this.baselinePrevalenceOdds[age][sex][d + 1]
								* relRisk[i][d + 1]
								/ (this.baselinePrevalenceOdds[age][sex][d + 1]
										* relRisk[i][d + 1] + 1);
						/*
						 * relative risk is identical for cured and not cured
						 * diseases
						 */
						sumRRinHealth[d] += weight[i]
								* (1 - probDisease[i][d] - probDisease[i][d + 1])
								* relRisk[i][d];
						sumRRinHealth[d + 1] = sumRRinHealth[d];
					} else {

						for (int dc = 0; dc < inputData.clusterStructure[c]
								.getNInCluster(); dc++) {
							int d = inputData.clusterStructure[c]
									.getDiseaseNumber()[dc];
							if (!inputData.clusterStructure[c]
									.getDependentDisease()[dc]) {
								// probability = baseline prevalence * RR
								probDisease[i][d] = this.baselinePrevalenceOdds[age][sex][d]
										* relRisk[i][d]
										/ (this.baselinePrevalenceOdds[age][sex][d]
												* relRisk[i][d] + 1);

								sumRRinHealth[d] += weight[i]
										* (1 - probDisease[i][d])
										* relRisk[i][d];

							}
						}

						int NInCluster = inputData.clusterStructure[c]
								.getNInCluster();

						// now calculate the sum of RR for each dependent
						// disease
						// loop over clusters and dependent diseases;

						if (inputData.clusterStructure[c].getNInCluster() > 1)
							for (int dd = 0; dd < NInCluster; dd++)
								if (inputData.clusterStructure[c]
										.getDependentDisease()[dd]) {
									int Ndd = inputData.clusterStructure[c]
											.getDiseaseNumber()[dd];
									// Ndd is disease number belonging to dd ;

									// relRisk[i] already contains the RR due to
									// the
									// risk
									// factors

									// now calculate RR for the dependent
									// disease by
									// multiplying
									// it with the RR due to each independent
									// disease
									relRiskIncludingDisease[i][Ndd] = relRisk[i][Ndd];
									for (int di = 0; di < NInCluster; di++)
										/*
										 * if independent disease multiply rr
										 * with the rr due to the presence of
										 * this disease
										 */
										if (!inputData.clusterStructure[c]
												.getDependentDisease()[di]) {
											int Ndi = inputData.clusterStructure[c]
													.getDiseaseNumber()[di];
											// Ndi is disease number belonging
											// to di
											// ;
											// RR due to independent disease=
											// p(di)*RR(di) +
											// 1*(1-p(di)) = p(di)*(RR(di)-1)+1

											relRiskIncludingDisease[i][Ndd] *= (1 + probDisease[i][Ndi]
													* (inputData
															.getClusterData()[age][sex][c]
															.getRRdisExtended()[di][dd] - 1));

										}
									sumRR[Ndd] += weight[i]
											* relRiskIncludingDisease[i][Ndd];

									;
								}
					}
				}
			} // end second loop over all persons ( i )
		// calculate Baseline Prevalence and Incidence and mortality for
		// dependent diseases
		if (age == 0 && sex == 0)
			this.log.debug("end loop 2");
		;
		if (nDiseases > 0) {

			/*
			 * incidence for independet diseases, but prevalence/ fatal
			 * incidence for the dependent diseases (fatal incidence does not
			 * depend on having the disease already, so this info is not needed
			 * (as it is with incidence) so can be estimated sooner
			 */
			calculateBaselineInc(inputData, age, sex, sumRRinHealth, false);
			calculateBaselinePrev(inputData, age, sex, sumRR, true);
			calculateBaselineFatalIncidence(inputData, age, sex, sumRR, true);
			if (age == 0)
				for (int d = 0; d < nDiseases; d++)
					log.debug("or trial disease" + d + " = "
							+ this.baselinePrevalenceOdds[age][sex][d]);
		}

		// //////////////////////////////////////////////einde second loop
		// /////////////////////////////

		// / start iterations for prevalence odds dependent diseases /// //

		// ///////////////////////////////////////////////////////////////////////////////////////////

		/*
		 * now calculate Baseline Prevalence Odds for the dependent diseases
		 * using an iterative procedure; now repeat loop 1 iteratively to
		 * estimate the baseline odds loop over all diseases
		 * 
		 * exception: when input prevalence==0
		 */
		if (nDiseases > 0)
			for (int c = 0; c < inputData.getNCluster(); c++) {
				int NInCluster = inputData.clusterStructure[c].getNInCluster();
				int NIndep = inputData.clusterStructure[c].getNIndep();
				int[] indexIndependent = inputData.clusterStructure[c]
						.getIndexIndependentDiseases();
				// loop over dependent diseases
				for (int dd = 0; dd < NInCluster; dd++)
					if (this.clusterStructure[c].getDependentDisease()[dd]) {
						// need to sum over all combinations of independent
						// diseases
						// is the cluster
						int Ndd = inputData.clusterStructure[c]
								.getDiseaseNumber()[dd];
						// Ndd is disease number belonging to dd ;

						int nIter = 0;
						/* if prevalence = 0 keep baseline odds=0 */
						double del = 100;
						if (diseasePrevalence[Ndd] == 0)
							del = 0;

						double sumPrevCurrent = 0;
						double sumDerivativePrevCurrent = 0;
						double RR = 1;
						while (del > 0.00001 && nIter < 10) {
							sumPrevCurrent = 0;
							sumDerivativePrevCurrent = 0;
							for (int i = 0; i < nSim; i++) {

								for (int combi = 0; combi < Math.pow(2, NIndep); combi++) {
									// calculate RR for this combination //
									RR = relRisk[i][Ndd];
									double probCombi = 1;
									for (int di = 0; di < NIndep; di++) {
										int Ndi = inputData.clusterStructure[c]
												.getDiseaseNumber()[indexIndependent[di]];
										// see if disease=1 in the cluster (see
										// if
										// bit =1 at right place)
										if ((combi & (1 << di)) == (1 << di)) {
											RR *= inputData.getClusterData()[age][sex][c]
													.getRRdisExtended()[indexIndependent[di]][dd];
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
									if (age == 0)
										log
												.debug(" cat i= "
														+ i
														+ " disease "
														+ Ndd
														+ " after combi "
														+ combi
														+ "  prevalence d = "
														+ (weight[i]
																* probCombi
																* RR
																* this.baselinePrevalenceOdds[age][sex][Ndd] / (1 + RR
																* this.baselinePrevalenceOdds[age][sex][Ndd])));
								}
								if (age == 0)
									log.debug("or ct i= " + i + " RR = " + RR
											+ "  sumprevalence  until now: "
											+ sumPrevCurrent);

							}

							// end loop over all individuals
							double oldValue = this.baselinePrevalenceOdds[age][sex][Ndd];
							if (age == 0)
								log.debug("or loop " + nIter
										+ " sumPrevCurrent = " + sumPrevCurrent
										+ "  oldvalue: " + oldValue);
							this.baselinePrevalenceOdds[age][sex][Ndd] = oldValue
									- (sumPrevCurrent - diseasePrevalence[Ndd])
									/ sumDerivativePrevCurrent;
							del = Math
									.abs(this.baselinePrevalenceOdds[age][sex][Ndd]
											- oldValue);
							++nIter;
						}// end iterative procedure for disease
					} // end loop over diseases

			}// end loop over clusters
		if (age == 0 && sex == 0)
			this.log.debug("end loop iterations after loop 2");

		// //////////////////////////////////////////////einde second loop
		// /////////////////////////////

		// / start loop 3 (for AM and (non-fatal) incidence dep diseases /// //

		// ///////////////////////////////////////////////////////////////////////////////////////////

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
		 * 3) to make the matrix needed for the estimation of the attributable
		 * mortality: to be solved with a matrix equation each row of the
		 * equation is for 1 disease. take disease d as the row disease, and d1
		 * ... dn as other diseases. See the description of calculation document
		 * for a description of the calculations. <br>
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

		if (nDiseases > 0)
			for (int i = 0; i < nSim; i++) {

				for (int c = 0; c < inputData.getNCluster(); c++) {

					// now calculate comorbidity that contains the
					// probability
					// of
					// each combination of diseases
					// within each cluster

					probComorbidity[i][c] = new Morbidity(inputData
							.getClusterData()[age][sex][c],
							inputData.clusterStructure[c], relRisk[i],
							this.baselinePrevalenceOdds[age][sex]);

					// extract the probDisease for the dependent diseases
					if (inputData.clusterStructure[c].getNInCluster() > 1
							&& !this.clusterStructure[c].isWithCuredFraction()) {
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

							}
						}
						;

					}
					;
					vMat = probComorbidity[i][c].addBlock(vMat, weight[i],
							inputData.clusterStructure[c], inputData
									.isWithRRForMortality(), diseasePrevalence);
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

				}

				// if without RR for mortality, calculate the contribution to
				// matrix V of the off-diagonal cluster blocks:
				// average(probdisease (d1 &d2,i))/p(d1)-
				// * average(probdisease(d1,i)*probdisease(d2,i))/p(d1)
				// = (as independent given i)
				// 1/p(d1) * [ sum probdisease(d1,i)*probdisease(d2,i)*weigth(i)
				// - p(d1)*p(d2)]
				// =
				// 1/p(d1) * sum probdisease(d1,i)*probdisease(d2,i)*weigth(i) -
				// p(d2)
				// =
				// sum [probdisease(d1,i)*probdisease(d2,i)*weigth(i)/p(d1) ] -
				// sum [p(d2,i)*weight(i)]
				// =
				// sum [{probdisease(d1,i)*probdisease(d2,i)*weigth(i)/p(d1) -
				// p(d2,i)}*weight(i)]

				// if RR for mortality is present, the off diagonal element are
				// zero, so nothing
				// needs to be done
				//
				//
				if (!withRRmort)
					for (int c1 = 0; c1 < nCluster; c1++)
						for (int c2 = 0; c2 < nCluster; c2++)
							if (c1 != c2)
								for (int dd = 0; dd < inputData.clusterStructure[c1]
										.getNInCluster(); dd++)
									for (int ee = 0; ee < inputData.clusterStructure[c2]
											.getNInCluster(); ee++) {
										int d1 = inputData.clusterStructure[c1]
												.getDiseaseNumber()[dd];
										int d2 = inputData.clusterStructure[c2]
												.getDiseaseNumber()[ee];
										vMat[d1][d2] += weight[i]
												* (probDisease[i][d1]
														* probDisease[i][d2]
														/ diseasePrevalence[d1] - diseasePrevalence[d2]);
									}

			}// end third loop over all persons i

		if (nDiseases > 0) {
			/*
			 * this is only needed for incidence; this needs two steps before:
			 * one for estimating prevalence odds of disease, and thus make the
			 * distribution of independent disease within the population. This
			 * is needed because only healthy persons can get the disease
			 * 
			 * So only in the second step (when it is known who are healthy or
			 * not we can calculate the baseline incidence for independent
			 * diseases;
			 * 
			 * For dependent diseases this can be done only in the next step, as
			 * the healthy for those diseases are only known there
			 * 
			 * For fatal diseases we do not need to know the percentage healthy,
			 * so this is not needed
			 */
			calculateBaselineInc(inputData, age, sex, sumRRinHealth, true);

		}
		if (age == 0 && sex == 0)
			this.log.debug("end loop 3");

		/*
		 * fourth loop calculates the contribution of the fatal incidence to the
		 * lefthand side of the equation for solving attributable mortality This
		 * can be carried out only after baseline fatal incidence has been
		 * calculated
		 * 
		 * Also it calculates the probability of all diseasestates necessary for
		 * the calculation of the disability/daly in other diseases
		 */

		double[][] fatalIncidence = new double[nSim][nDiseases];
		double[][] fatalIncidenceEgivenD = new double[nDiseases][nDiseases];

		double[][] prevalenceDiseaseStates = new double[this.nCluster][];
		double[][] prevalenceDiseaseStatesForI = new double[this.nCluster][];
		if (nDiseases > 0)
			for (int c = 0; c < inputData.getNCluster(); c++) {
				prevalenceDiseaseStates[c] = new double[(int) Math.pow(2,
						clusterStructure[c].getNInCluster())];
				prevalenceDiseaseStatesForI[c] = new double[(int) Math.pow(2,
						clusterStructure[c].getNInCluster())];
				if (this.clusterStructure[c].isWithCuredFraction()) {
					isCuredDisease[this.clusterStructure[c].getDiseaseNumber()[0]] = true;
					prevalenceDiseaseStates[c] = new double[3];
					prevalenceDiseaseStatesForI[c] = new double[3];
				}
			}
		double[] abilityFromOtherCauses = new double[nSim];
		double sumAbilityFromDiseases = 0;
		if (nDiseases > 0)
			for (int i = 0; i < nSim; i++) {
				if (age == 70) {
					int ii = 0;
					ii++;
				}

				for (int c = 0; c < inputData.getNCluster(); c++) {
					int dStart = inputData.clusterStructure[c]
							.getDiseaseNumber()[0];
					/* independent diseases (single disease cluster) */

					if (inputData.clusterStructure[c].getNInCluster() == 1) {
						prevalenceDiseaseStatesForI[c][1] = probComorbidity[i][c]
								.getProb()[0][0];
						prevalenceDiseaseStatesForI[c][0] = (1 - probComorbidity[i][c]
								.getProb()[0][0]);
						prevalenceDiseaseStates[c][1] += weight[i]
								* prevalenceDiseaseStatesForI[c][1];
						prevalenceDiseaseStates[c][0] += weight[i]
								* prevalenceDiseaseStatesForI[c][0];
						fatalIncidence[i][dStart] = this.baselineFatalIncidence[age][sex][dStart]
								* relRisk[i][dStart];
						/* within cluster fatalIncidenceGivenThisDisease */
						if (diseasePrevalence[dStart] != 0)
							fatalIncidenceEgivenD[dStart][dStart] += weight[i]
									* probDisease[i][dStart]
									* fatalIncidence[i][dStart]
									/ diseasePrevalence[dStart];
						else
							fatalIncidenceEgivenD[dStart][dStart] += 0;

					}
					if (this.clusterStructure[c].isWithCuredFraction()) {
						prevalenceDiseaseStatesForI[c][1] = probComorbidity[i][c]
								.getProb()[0][0];
						prevalenceDiseaseStatesForI[c][2] = probComorbidity[i][c]
								.getProb()[1][1];
						prevalenceDiseaseStatesForI[c][0] = (1 - probComorbidity[i][c]
								.getProb()[0][0] - probComorbidity[i][c]
								.getProb()[1][1]);
						prevalenceDiseaseStates[c][1] += weight[i]
								* prevalenceDiseaseStatesForI[c][1];
						prevalenceDiseaseStates[c][0] += weight[i]
								* prevalenceDiseaseStatesForI[c][0];
						prevalenceDiseaseStates[c][2] += weight[i]
								* prevalenceDiseaseStatesForI[c][2];
						fatalIncidence[i][dStart] = this.baselineFatalIncidence[age][sex][dStart]
								* relRisk[i][dStart];
						fatalIncidence[i][dStart + 1] = this.baselineFatalIncidence[age][sex][dStart + 1]
								* relRisk[i][dStart + 1];
						/*
						 * within cluster fatalIncidenceGivenThisDisease
						 * superfluous as no fatalIncidence is allowed at this
						 * moment for diseases with cured fractions, but this
						 * might work in case it is allowed
						 */
						if (diseasePrevalence[dStart] != 0)
							fatalIncidenceEgivenD[dStart][dStart] += weight[i]
									* probDisease[i][dStart]
									* fatalIncidence[i][dStart]
									/ diseasePrevalence[dStart];
						else
							fatalIncidenceEgivenD[dStart][dStart] += 0;
						if (diseasePrevalence[dStart + 1] != 0)
							fatalIncidenceEgivenD[dStart + 1][dStart + 1] += weight[i]
									* probDisease[i][dStart + 1]
									* fatalIncidence[i][dStart + 1]
									/ diseasePrevalence[dStart + 1];
						else
							fatalIncidenceEgivenD[dStart + 1][dStart + 1] += 0;
						if (diseasePrevalence[dStart] != 0)
							fatalIncidenceEgivenD[dStart + 1][dStart] += weight[i]
									* probDisease[i][dStart]
									* fatalIncidence[i][dStart + 1]
									/ diseasePrevalence[dStart];
						else
							fatalIncidenceEgivenD[dStart + 1][dStart] += 0;
						if (diseasePrevalence[dStart + 1] != 0)
							fatalIncidenceEgivenD[dStart][dStart + 1] += weight[i]
									* probDisease[i][dStart + 1]
									* fatalIncidence[i][dStart]
									/ diseasePrevalence[dStart + 1];
						else
							fatalIncidenceEgivenD[dStart][dStart + 1] += 0;

					}
					if (inputData.clusterStructure[c].getNInCluster() > 1
							&& !this.clusterStructure[c].isWithCuredFraction())
						for (int combi = 0; combi < Math.pow(2,
								inputData.clusterStructure[c].getNInCluster()); combi++) {
							/*
							 * - average (over all R) of
							 * [fatalbaselineinc-depNO(r)(RRdisNO-1){(P(intermed
							 * and dep given R)-p(intermediate given
							 * R)P(dependent given R)}]/P(dep) depNO stands for
							 * each dependent disease, dep is the disease of the
							 * equation
							 * 
							 * For intermediate diseases the following terms are
							 * added to the lefthand side (with minus sign): -
							 * Average(over all R) of
							 * [fatalbaselineinc_dep(R)(RRdis-1)(1-P(intermed
							 * given R))P(intermed given R)]/P(intermed)
							 */

							/*
							 * calculate the contribution of each disease to the
							 * probability of the combination (p(disease state d
							 * |other disease states in combi)
							 */
							double probcombi = 1;
							double RRfromDiseases[] = new double[this.clusterStructure[c]
									.getNInCluster()];
							Arrays.fill(RRfromDiseases, 1);
							// RRcombi is relative risk due to the independent
							// diseases

							for (int d = 0; d < this.clusterStructure[c]
									.getNInCluster(); d++) {
								int absDiseaseNumber = this.clusterStructure[c]
										.getDiseaseNumber()[d];

								if (!clusterStructure[c].getDependentDisease()[d]) { // independent
									// disease
									if ((combi & (1 << d)) == (1 << d))

										probcombi *= probDisease[i][absDiseaseNumber];
									else
										probcombi *= (1 - probDisease[i][absDiseaseNumber]);
								} else { /* dependent disease */

									/*
									 * to calculate: probability d given
									 * independent diseases= odds = baseline
									 * odds rr based on [riskfactor] + rr based
									 * on diseasestates
									 */
									double oddsDiseasedInCombi = this.baselinePrevalenceOdds[age][sex][absDiseaseNumber]
											* relRisk[i][absDiseaseNumber];
									for (int di = 0; di < inputData.clusterStructure[c]
											.getNIndep(); di++) {
										int ndi = inputData.clusterStructure[c]
												.getIndexIndependentDiseases()[di];
										if ((combi & (1 << ndi)) == (1 << ndi)) {
											/*
											 * if the causal disease is present,
											 * the odds and RR should be
											 * multiplied with the rr ,
											 */
											oddsDiseasedInCombi *= inputData
													.getClusterData()[age][sex][c]
													.getRRdisExtended()[ndi][d];

											RRfromDiseases[d] *= inputData
													.getClusterData()[age][sex][c]
													.getRRdisExtended()[ndi][d];
										}
										double pDisease = oddsDiseasedInCombi
												/ (oddsDiseasedInCombi + 1);
										if ((combi & (1 << d)) == (1 << d))

											probcombi *= pDisease;
										else
											probcombi *= (1 - pDisease);

									}

								} // end if dependent disease

							}// end loop over d
							prevalenceDiseaseStatesForI[c][combi] = probcombi;
							prevalenceDiseaseStates[c][combi] += weight[i]
									* probcombi;
							for (int d = 0; d < this.clusterStructure[c]
									.getNInCluster(); d++) {
								int absDiseaseNumber = this.clusterStructure[c]
										.getDiseaseNumber()[d];

								fatalIncidence[i][absDiseaseNumber] += probcombi
										* this.baselineFatalIncidence[age][sex][absDiseaseNumber]
										* RRfromDiseases[d]
										* relRisk[i][absDiseaseNumber];

								/*
								 * here we calculate the fatalincidincidence in
								 * one disease only in those persons where the
								 * other (given) disease is present
								 * 
								 * We can here already divide by prevalence of
								 * d2 (=D) as this is a constant
								 */
								for (int d2 = 0; d2 < this.clusterStructure[c]
										.getNInCluster(); d2++) {
									if ((combi & (1 << d2)) == (1 << d2))
										if (diseasePrevalence[d2 + dStart] > 0)
											fatalIncidenceEgivenD[dStart + d][dStart
													+ d2] += weight[i]
													* probcombi
													* this.baselineFatalIncidence[age][sex][absDiseaseNumber]
													* RRfromDiseases[d]
													* relRisk[i][absDiseaseNumber]
													/ diseasePrevalence[d2
															+ dStart];

								}
								/*
								 * calculate the amount of fatal disease in
								 * those with disease d
								 * 
								 * this is the amount over all diseases
								 */

							}

						}// end loop over combinations

					/*
					 * now calculate fatal incidence given other disease for
					 * diseases from other clusters (between clusters): as these
					 * are independent, this are dependent only through the
					 * riskfactors
					 */

					for (int d = 0; d < this.clusterStructure[c]
							.getNInCluster(); d++) {
						// Only do this between clusters
						int dEnd = this.clusterStructure[c].getDiseaseNumber()[this.clusterStructure[c]
								.getNInCluster() - 1];
						// d2 is a number over all diseases , d only within the
						// current cluster diseases
						// so we need to add dStart to d, but not to d2

						for (int d2 = 0; d2 < nDiseases; d2++) {
							if (d2 < dStart || d2 > dEnd)
								// if d2 has zero prevalence, this entity
								// does not make sense
								// we make it zero so it disappears from the
								// calculations
								if (diseasePrevalence[d2] != 0)
									fatalIncidenceEgivenD[d + dStart][d2] += weight[i]
											* probDisease[i][d2]
											* fatalIncidence[i][d + dStart]
											/ diseasePrevalence[d2];
								else
									fatalIncidenceEgivenD[d + dStart][d2] += 0;

						}

					}

				} // end loop over clusters

				/*
				 * calculate the ability from diseases for person i, and in case
				 * of risktype==1 this directly give the relative hazards for
				 * other disease, as well as the baseline hazard for other
				 * diseases
				 */

				abilityFromDiseases[i] = calculateAbilityFromDiseases(inputData
						.getClusterData()[age][sex], nDiseases,
						prevalenceDiseaseStatesForI);

				/*
				 * in case 100% of the population has disease, and the disease
				 * also causes 100% diability, the ability from diseases will be
				 * 0 In that case it is impossible to calculate the amount of
				 * disability in those without the disease Therefore a warning
				 * message is given, and abilityFrom other causes is set to 1
				 */

				if (abilityFromDiseases[i] == 0) {
					abilityFromOtherCauses[i] = 1;

					if (warningflag3) {
						warningflag3 = false;

						displayWarningMessage(
								"WARNING:"
										+ "\n100% of the initial population has disability due to at least one disease. \nTherefore"
										+ " it is not possilible to estimate the disability from other (not modelled) diseases."
										+ "\nThis is made 0 (no disability from other diseases"
										+ "\nThis warning is give for age "
										+ age
										+ " and gender "
										+ sex
										+ " and riskgroup "
										+ i
										+ "\nNo more warnings of this kind will be generated for"
										+ " other risk, age and gender groups",
								dsi);
					}
				}

				else

				if (!withRRdisability) {

					/*
					 * here we just need the calculate the sum of the ability
					 * from diseases,
					 */

					sumAbilityFromDiseases += weight[i]
							* abilityFromDiseases[i];

				} else {
					abilityFromOtherCauses[i] = abilityFromRiskFactor[i]
							/ abilityFromDiseases[i];

					if (riskType == 1) {
						if (abilityFromOtherCauses[i] < 1) {
							/*
							 * take i=1 as the (temporary) reference category
							 * this might be another category as the category
							 * that is the baseline category given by the user.
							 */
							if (i == 0) {
								this.baselineAbility[age][sex] = (float) abilityFromOtherCauses[i];
								this.riskFactorAbilityRRcat[age][sex][i] = 1;
							} else
							/*
							 * in case there is 100% disability in a particular
							 * riskfactor group (user a=has given an
							 * oddsratio=0) then the ability should become 0%,
							 * thus RR should also be 0; As log(RR) then is
							 * log(0) the standard formula does not work, so we
							 * have to calculate this directly
							 */

							if (abilityFromOtherCauses[i] > 0)

								/*
								 * model for ability is: ability = exp( coef1 D
								 * + coef2R + log(baseline)) As parameter we use
								 * the RR = exp(coef2)
								 */

								this.riskFactorAbilityRRcat[age][sex][i] = (float) (abilityFromOtherCauses[i] / this.baselineAbility[age][sex]);
							else
								this.riskFactorAbilityRRcat[age][sex][i] = 0;

						} else if (abilityFromOtherCauses[i] == 1) {
							/*
							 * here other causes do not cause disability, and we
							 * set the R's to 1
							 */
							if (i == 0) {
								this.baselineAbility[age][sex] = 1;
								this.riskFactorAbilityRRcat[age][sex][i] = 1;
							} else
								this.riskFactorAbilityRRcat[age][sex][i] = 1;

						} else {

							if (i == 0) {
								String label = "";

								if (this.nWarningsDisability == 2)

									label = " NO MORE WARNINGS OF THIS TYPE WILL BE ISSUED FOR"
											+ " OTHER AGE/SEX GROUPS";
								if (this.nWarningsDisability < 3)
									displayWarningMessage(
											"WARNING:"
													+ "\nthe disability given for riskfactor group "
													+ i
													+ " in age "
													+ age
													+ " and sex "
													+ sex
													+ " can "
													+ "be explained completely by differences in disease prevalences due to the riskfactor. "
													+ "\nTherefore disability for this group will be calculated solely on disease status and not on risk "
													+ "factor status" + label,
											dsi);

								this.baselineAbility[age][sex] = 1;
								this.riskFactorAbilityRRcat[age][sex][i] = 1;

								this.nWarningsDisability++;
							} else
								this.riskFactorAbilityRRcat[age][sex][i] = 1;

						}
					} // end risktype == 1

					// other risktype are done later on (together with
					// mortality)

				} // end part on disability

			}// end loop over i

		if (!withRRdisability) {
			float overallAbility = 1 - inputData.getOverallDalyWeight()[age][sex];
			if (nDiseases > 0)
				this.baselineAbility[age][sex] = (float) (overallAbility / sumAbilityFromDiseases);
			else
				this.baselineAbility[age][sex] = (float) overallAbility;
			for (int i = 0; i < nRiskCat; i++)
				this.riskFactorAbilityRRcat[age][sex][i] = 1;
			this.riskFactorAbilityRRcont[age][sex] = 1;
			this.riskFactorAbilityRRend[age][sex] = 1;
			this.riskFactorAbilityRRbegin[age][sex] = 1;
			this.riskFactorAbilityAlpha[age][sex] = 1;

			if (this.baselineAbility[age][sex] > 1) {
				warnForAbilityGreaterOne(age, sex, sumAbilityFromDiseases,
						overallAbility, dsi);

			}
		}
		// TODO other risktypes!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// TODO fatal diseases

		/* calculate the mortality from acutely fatal diseases */
		/*
		 * this has two parts:
		 * 
		 * part 1: the casefatality (summed over all the (fatal) diseases) given
		 * the index disease D thus for each D (=row in matrix) the sum over
		 * fatalIncidence E given D
		 * 
		 * part 2: in case of RRothermort: we subtract from the matrix all the
		 * rows for risk factors, each row multiplied by p(r|d) A single row has
		 * terms CF given r, thus the sum is the sum over all CFs for a single
		 * riskfactor level i.
		 * 
		 * In terms over variables: sum over all diseases e of fatalIncidence
		 * [e]; the multiplication factor p(r|d)= p(d|r)p(r)/p(d) In variables:
		 * p(r)=weigth p(d)=prevalence of d and p(d|r)=probDisease[i][d]
		 */

		if (nDiseases > 0)
			for (int c = 0; c < inputData.getNCluster(); c++) {

				/*
				 * d is within cluster number, but d2 and d1 are over all
				 * diseases
				 */
				for (int d = 0; d < this.clusterStructure[c].getNInCluster(); d++) {
					int d1 = this.clusterStructure[c].getDiseaseNumber()[d];
					{
						for (int d2 = 0; d2 < nDiseases; d2++) {
							sumForCF[d1] += fatalIncidenceEgivenD[d2][d1];
							/* e= E, d2=D */
							if (inputData.isWithRRForMortality()) {
								/*
								 * with rr this is sum of CF given d - sum over
								 * I of all CF|r.p(r|d)= cf(r)p(d|r)p(r)/p(d) =
								 * cf(i)p(d,i)weight(i)/p(d) as p(d) does not
								 * depend on i, it can be applied outside the
								 * loop
								 */

								double sum = 0;

								// NB e and d are just the other way round here
								// as
								// they were used above
								for (int i = 0; i < nSim; i++) {
									sum += fatalIncidence[i][d2]
											* probDisease[i][d1] * weight[i];
								}
								if (diseasePrevalence[d1] != 0)
									sumForCF[d1] -= sum / diseasePrevalence[d1];

								// TODO checken of klopt ; is al wel een extra
								// keer gechecked

							} else { // without rr:
								/*
								 * without RR (see later comments when
								 * calculating lefthand):
								 * 
								 * sum of CF given d -sum over I of all CF
								 */

								for (int i = 0; i < nSim; i++) {
									sumForCF[d1] -= fatalIncidence[i][d2]
											* weight[i];
								}

								;

							}
						}

					}
				}
			}

		for (int i = 0; i < nSim; i++) {
			double sumFatalIncidence = 0;
			for (int d = 0; d < nDiseases; d++) {
				sumFatalIncidence += fatalIncidence[i][d];
			}
			if (sumFatalIncidence > relRiskMort[i]
					* baselineMortality[age][sex])
				throw new DynamoInconsistentDataException(
						" Mortality from Case Fatality is larger than total"
								+ " mortality for age "
								+ age
								+ " and gender "
								+ sex
								+ " and riskgroup "
								+ i
								+ "\nProgram will not run unless this problem is solved first"
								+ "\nPlease change the input");
		}

		/*
		 * Calculate the prevalence of disability for group/subject i due to
		 * diseases
		 */

		// now calculate the attributable mortality
		double[] lefthand = new double[nDiseases];
		if (nDiseases > 0)
			for (int d = 0; d < nDiseases; d++) {
				if (diseasePrevalence[d] != 0)
					expectedMortality[d] = this.baselineMortality[age][sex]
							* sumRRmDisease[d] / diseasePrevalence[d];

				else
					expectedMortality[d] = inputData.getMortTot()[age][sex];
				// with RR for mortality lefthand is md- sum cf terms -
				/* mtot + (1-p(d))E(d) - average(mtot(r)|d) */
				if (inputData.isWithRRForMortality() && !isCuredDisease[d])
					lefthand[d] = inputData.getMortTot()[age][sex]
							+ (1 - diseasePrevalence[d]) * excessMortality[d]
							- expectedMortality[d] - sumForCF[d];
				/* in cured diseases the attributable mortality is 0 */
				else if (isCuredDisease[d])
					/*
					 * for cured disease the attributable mortality is by
					 * definition zero we signal this by making the lefthand
					 * equal to zero In that case a negative am is needed to
					 * compensate any other positive am's for other diseases and
					 * in case of negative am this is made zero
					 */
					lefthand[d] = 0;
				else
				// no RR for mortality
				/*
				 * in this case the lefthand is equal to: mortality from disease
				 * D - (othermortality excl attributable mortality terms) - sum
				 * of CF given this disease mortality from disease D = mort-tot
				 * + ExcessMort(1-prevalence(D))
				 * 
				 * other mort = morttot - attributable terms - sum over i of all
				 * CF TODO nog checken
				 * 
				 * than the lefthand becomes: excessmort(1-prevalence(D)+ sum
				 * over I of all CF - sum of CF given d
				 * 
				 * 
				 * basically, the expectedMortality based on riskfactor
				 * distribution here is equal to total mortality, as riskfactors
				 * do not influence total mortality other than through disease
				 * and thus both terms disappear
				 */
				{
					lefthand[d] = (1 - diseasePrevalence[d])
							* excessMortality[d] - sumForCF[d];
					/*
					 * due to rounding, lefthand is not zero in the next case,
					 * which it should be
					 */

					if (excessMortality[d] == 0 && sumForCF[d] == 0)
						lefthand[d] = 0;
				}
				/*
				 * and similarly in case this numerical problem also affects the
				 * other calculations
				 */
				if (lefthand[d] < 0 && Math.abs(lefthand[d]) < 0.000001)
					lefthand[d] = 0;

			}

		boolean negativeAM = true;

		/*
		 * IN CASE EXCESS MORTALITY IS ZERO: exclude this disease from the
		 * calculations and make AM zero for this disease
		 */
		boolean[] AMsetToZero = new boolean[nDiseases];
		Arrays.fill(AMsetToZero, false);

		for (int d = 0; d < nDiseases; d++) {

			if (excessMortality[d] == 0 || diseasePrevalence[d] == 0) {
				for (int d1 = 0; d1 < nDiseases; d1++) {
					/*
					 * this is done by setting the rows and columns of vMat to
					 * zero, and the diagonal to 1 Values of lefthand do not
					 * matter in this case
					 */
					vMat[d1][d] = 0;
					vMat[d][d1] = 0;
				}
				vMat[d][d] = 1;
				AMsetToZero[d] = true;
				if (warningflag5 && Math.abs(diseasePrevalence[d]) > 0.00001) {
					/* displayWarningMessage */
					log
							.fatal("WARNING:\nExcess mortality of disease "
									+ diseaseNames[d]
									+ " is zero for age "
									+ age
									+ " and gender "
									+ sex
									+ "\nThe program assumes a zero attributable mortality for this disease"
									+ "\nNo more warning messages of this kind are given for other age/gender/disease groups"
							/* ,dsi */);
					warningflag5 = false;
				}

			}
		}

		if (age == 46) {

			int i = 0;
			i++;

		}
		int niter = 0;
		while (negativeAM && niter < 10 && nDiseases > 0) {
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
				if (!AMsetToZero[d])
					this.attributableMortality[age][sex][d] = (float) temp[d];
				else
					this.attributableMortality[age][sex][d] = 0;
			}

			if (age == 0 && sex == 0)
				this.log.debug("attributable mortality written");

			for (int d = 0; d < nDiseases; d++) {
				if (Math.abs(this.attributableMortality[age][sex][d]) < 1e-16)
					this.attributableMortality[age][sex][d] = 0;

				if (this.attributableMortality[age][sex][d] < 0) {
					negativeAM = true;

					/*
					 * exclude this disease from the calculations and make AM
					 * zero for this disease
					 * 
					 * This will decrease the AM of other diseases, so they
					 * might get negative; therefore this should be repeated
					 * until no negative AM's are left
					 */

					/*
					 * this is done by setting the rows and columns of vMat to
					 * zero, and the diagonal to 1
					 */
					for (int d1 = 0; d1 < nDiseases; d1++) {
						vMat[d1][d] = 0;
						vMat[d][d1] = 0;
					}
					vMat[d][d] = 1;
					AMsetToZero[d] = true;

				}

			}
			niter++;

		}
		for (int d = 0; d < nDiseases; d++)
			if (excessMortality[d] > 0 && diseasePrevalence[d] == 0) {
				log
						.fatal("WARNING:\nExcess mortality of disease "
								+ diseaseNames[d]
								+ " is not zero for age "
								+ age
								+ " and gender "
								+ sex
								+ " while prevalence is zero. In this situation no attributable mortality can be calculated."
								+ "\nThe program assumes a attributable mortality for this disease that is equal to the input excess mortality"
						/* ,dsi */);
				this.attributableMortality[age][sex][d] = excessMortality[d];

			}

		// einde herhaling schatting van Attributable mortality
		if (niter == 10)
			throw new DynamoInconsistentDataException(
					" negative attributable mortality estimated after 10 iterations!! \n"
							+ "Message given for age "
							+ age
							+ " and gender "
							+ sex
							+ "\n Most common reason is that"
							+ " the mortality from acutely fatal disease is larger than excess mortality. ");
		// TODO throw exception

		;
		/**
		 * <br>
		 * Phase 5 <br>
		 * In the fifth stage of parameter estimation, the estimated
		 * attributable mortality is used to calculate the other cause mortality
		 * per simulated person i Then a regression is done of this other cause
		 * mortality on the risk factors yielding relative risks for other cause
		 * mortality <br>
		 */

		if (age == 0 && sex == 0)
			this.log.debug("begin loop 4");
		double sumOtherMort = 0;

		double[] beta = null;

		double otherMort[] = new double[nSim];
		double mortalityFromDisease[] = new double[nSim];
		double logOtherMort[] = new double[nSim];

		double logAbilityFromOtherCauses[] = new double[nSim];
		double nNegativeOtherMort = 0;
		
		if (age == 57 ) {

			int stop = 0;
			stop++;

		}
		for (int i = 0; i < nSim; i++) {

			otherMort[i] = relRiskMort[i] * this.baselineMortality[age][sex];
			// for riskType==1 the relative risks have already been made above
			if (withRRdisability && riskType != 1) {

				// abilityFromOtherCauses[i] = 1;
				logAbilityFromOtherCauses[i] = 0;

				if (abilityFromRiskFactor[i] == 0) {
					abilityFromOtherCauses[i] = 0.00000;
					logAbilityFromOtherCauses[i] = -14;
					if (this.warningflag2)
						displayWarningMessage(
								"WARNING:\n100% disability calculated for riskclass "
										+ i
										+ ". \nAs this gives"
										+ " numerical problems disability is made slightly less then 100%.",
								dsi);
					this.warningflag2 = false;
				} else {
					logAbilityFromOtherCauses[i] = Math
							.log(abilityFromOtherCauses[i]);

				}
			}

			if (nDiseases > 0)
				for (int d = 0; d < nDiseases; d++) {
					otherMort[i] -= this.attributableMortality[age][sex][d]
							* probDisease[i][d] + fatalIncidence[i][d];
					mortalityFromDisease[i] += this.attributableMortality[age][sex][d]
							* probDisease[i][d] + fatalIncidence[i][d];
				}

			sumOtherMort += weight[i] * otherMort[i];
			if (otherMort[i] > 0)
				logOtherMort[i] = Math.log(otherMort[i]);
			else {
				this.log.fatal("negative other mortality  = " + otherMort[i]
						+ " for person  " + i + " for riskclass "
						+ riskclass[i] + " and for riskfactor " + riskfactor[i]
						+ " age: " + age + " sex: " + sex);
				logOtherMort[i] = (1.0 / 0.0) * 0.0; /*
													 * This gives NaN
													 */
				// if (riskType==1 || (riskType==3 &&
				// riskclass[i]!=durationClass))
				nNegativeOtherMort += weight[i];
			}

		}
		// make design matrix for regression (including dummy variables
		// for
		// each risk class)

		/*
		 * in case there are zero prevalences, this point should be excluded the
		 * indexDat translate the indexes of the regression data to the original
		 * classes
		 */

		double[][] xMatrix = new double[nSim][2];

		/* count number of valid categories */

		int[] indexForCategories = new int[nRiskCat];
		Arrays.fill(indexForCategories, -1);
		int nValidCategories = 0;
		if (riskType != 2)
			for (int i = 0; i < nRiskCat; i++) {

				if (inputData.getPrevRisk()[age][sex][i] > 0) {
					indexForCategories[nValidCategories] = i;
					nValidCategories++;
				}

				;
			}

		/* count number of valid rows */
		int nValidRows = 0;

		int[] indexForRows = new int[nSim];
		Arrays.fill(indexForRows, -1);
		/*
		 * nValidRiskClass contains the renumbered riskfactor classes: so if
		 * there are 6 riskclasses, 0,1,2,3,4,5 but 4 is empty, the new numbers
		 * are 0,1,2,3,4 were 5 is renumbered to 4
		 */
		int[] validRiskClass = new int[nSim];
		Arrays.fill(validRiskClass, -1);
		if (riskType != 3)
			for (int i = 0; i < nSim; i++) {
				if (weight[i] > 0 ) {
					indexForRows[nValidRows] = i;
					validRiskClass[i] = nValidRows;
					nValidRows++;

				}
			}
		else {
			nValidRows = 0;
			int currentCat = -1;
			boolean workingOnDuration = false;
			for (int i = 0; i < nSim; i++) {
				if (weight[i] > 0) {
					/*
					 * increase currentCat unless the last valid riskclass was
					 * also a durationclass
					 */
					if (!workingOnDuration
							|| riskclass[i] != inputData.getIndexDuurClass())
						currentCat++;
					if (riskclass[i] == inputData.getIndexDuurClass())
						workingOnDuration = true;
					else
						workingOnDuration = false;
					indexForRows[nValidRows] = i;
					validRiskClass[i] = currentCat;
					nValidRows++;

				}
			}
		}
		if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
			xMatrix = new double[nValidRows][nValidCategories];
		double[] wVector = new double[nValidRows];
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

						if (validRiskClass[i] == rc)
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
				xMatrix[i][0] = 1.0;
				wVector[i] = weight[i];
				xMatrix[i][xMatrix[i].length - 1] = riskfactor[i]
						- inputData.getRefClassCont();

			}
		}
		double[] yValue = new double[nSim];
		if (riskType == 2)
			yValue = logOtherMort;
		else for (int i = 0; i < nSim; i++)
			
			/* make negative values a small positive value, small is relative to all cause mortality at this age */
			/* if this happens to the reference value, relative risks will be in the order of 1000 */
			/* for other categories also zero could have been used, but then changing the category order would
			 * give different results, so this is not done
			 */
			if (otherMort[i]<=0) 
				yValue[i]=baselineMortality[age][sex]/1000;
			else yValue[i]=otherMort[i];
		if (nValidRows < nSim) {
			yValue = new double[nValidRows];
			if (riskType == 2) {
				for (int i = 0; i < nValidRows; i++)
					yValue[i] = logOtherMort[indexForRows[i]];
			} else {
				for (int i = 0; i < nValidRows; i++)
					yValue[i] = otherMort[indexForRows[i]];
			}

		}
		double[] y2Value = logAbilityFromOtherCauses;
		if (nValidRows < nSim) {
			y2Value = new double[nValidRows];
			for (int i = 0; i < nValidRows; i++)
				y2Value[i] = logAbilityFromOtherCauses[indexForRows[i]];

		}
		// end of fourth loop over all persons i
		if (age == 0 && sex == 0)
			this.log.debug("end loop 4");
		if (nNegativeOtherMort > 0) negativeMortality=true; 
		if (nNegativeOtherMort > 0.3 && inputData.isWithRRForMortality()
				&& (warningflag4)) {
			warningflag4 = false;
			
			displayWarningMessage(
					"WARNING: \nnegative other mortality  in  "
							+ (nNegativeOtherMort * 100)
							+ " % of simulated cases"
							+ " for age "
							+ age
							+ " and gender "
							+ sex
							+ "\nData for further inspection will be written to the file parameters/otherMortData.csv"
							+ "\nno more warnings of this kind will be generated for "
							+ "other age and gender groups", dsi);
		}
		/*
		 * if (nNegativeOtherMort > 0.3 && inputData.isWithRRForMortality())
		 * throw new DynamoInconsistentDataException(
		 * "FATAL ERROR:\nOther mortality becomes negative in" +
		 * " more than 30% ( " + (nNegativeOtherMort 100) + " %) of cases. " +
		 * " for age " + age + " and gender " + sex +
		 * "The amount of disease specific mortality given to the model" +
		 * " exceeds the overall mortality given to the model.  Please lower excess mortality rates or"
		 * +
		 * " case fatality rates or disease prevalence rates, or increase total mortality rates"
		 * );
		 */
		if (sumOtherMort < 0)
			// TODO add more info on mortality per disease
			throw new DynamoInconsistentDataException(
					"FATAL ERROR: Attributable Mortality from diseases exceeds the overall mortality for age "
							+ age
							+ " and sex = "
							+ sex

							+ "./N  Please lower excess mortality rates or"
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

			// first class has relative risk of 1; also default value for
			// all
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
					// old version on log scale
					// this.relRiskOtherMort[age][sex][indexForCategories[j]] =
					// (float) Math
					// .exp(beta[j]);
					if (beta[0] > 0)
						this.relRiskOtherMort[age][sex][indexForCategories[j]] = (float) ((beta[j]+beta[0]) / beta[0]);
					else {
						log.fatal("zero or negative baseline other mortality: "
								+ beta[0] + " therefore RR set to 1");
						this.relRiskOtherMort[age][sex][indexForCategories[j]] = 0;
					}
					// in case of duration class set rr to 1;
					if (inputData.getRiskType() == 3
							&& inputData.getIndexDuurClass() == j)
						this.relRiskOtherMort[age][sex][j] = 1;
				}

				// last beta is the coefficient for the continuous risk
				// factor
				// //
				// this.relRiskOtherMortCont[age][sex] = (float) Math
				// .exp(beta[beta.length - 1]);
			}

			if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
				this.relRiskOtherMortCont[age][sex] = 1;
			else
				this.relRiskOtherMortCont[age][sex] = (float) Math.exp(beta[1]);
			if (inputData.getRiskType() == 2)
				this.baselineOtherMortality[age][sex] = (float) Math
						.exp(beta[0]);
			else
				this.baselineOtherMortality[age][sex] = (float) beta[0];
			if (this.baselineOtherMortality[age][sex] == 0)
				this.relRiskOtherMortCont[age][sex] = 1;
			/**
			 * in the fifth stage the sum of the RR's on other cause mortalities
			 * is calculated in order to estimate the baseline other cause
			 * mortality This could also be derived from the regression
			 * (intercept)
			 * 
			 */
			if (inputData.getRiskType() == 3) { // now do time dependent
				// part;

				// first anker the RRbegin and RRend if those are ankered
				// for
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
				double sumDif = 0; /*
									 * sumDif is used to check if all y-values
									 * are the same
									 */
				for (int i = 0; i < nSim; i++) {
					if (riskclass[i] == inputData.getIndexDuurClass()) {
						ydata[index] = otherMort[i];
						xdata[index] = riskfactor[i];
						weightdata[index] = weight[i];
						sumDif += ydata[index] - ydata[0];
						index++;
					}

				}
				try {

					double RRdurationclass = 1;
					for (int j = 0; j < nRiskCat; j++)
						if (indexForCategories[j] == inputData
								.getIndexDuurClass())

							RRdurationclass = Math.exp(beta[j]);
					/*
					 * if RR average outside the area between RRend and RRbegin,
					 * the model can not be fitted anchored on both of them In
					 * this case we only anchor the end RR
					 */
					if ((endRR >= RRdurationclass && beginRR >= RRdurationclass)
							|| (endRR <= RRdurationclass
									&& beginRR <= RRdurationclass && endRR != -1))
						beginRR = -1;
					if (!oneDuration && Math.abs(sumDif) > 0.00001) {
						log.fatal("age: " + age + " sex: " + sex);
						try {

							beta = nonLinearDurationRegression(ydata, xdata,
									weightdata, endRR, beginRR,
									this.baselineOtherMortality[age][sex]);
						} catch (DynamoInconsistentDataException e) {
							boolean success = false;

							if (e.getMessage().equals("Repeat")) {
								endRR = -1;
								beginRR = -1;
								try {
									beta = nonLinearDurationRegression(
											ydata,
											xdata,
											weightdata,
											endRR,
											beginRR,
											this.baselineOtherMortality[age][sex]);
									success = true;
								} catch (DynamoInconsistentDataException e2) {
									success = false;
								}
							}

							if (!success) {
								displayWarningMessage(
										"WARNING: \n"
												+ e.getMessage()
												+ ". Average RR for the durationclass is "
												+ RRdurationclass
												+ " for age "
												+ age
												+ " and sex "
												+ sex
												+ "\nThe RR for other mortality is put to "
												+ RRdurationclass
												+ " for all durations"
												+ "\nNote that incidence of "
												+ "diseases is still duration dependent as specified",
										dsi);
								beta = new double[3];
								beta[0] = RRdurationclass;
								beta[1] = RRdurationclass;
								beta[2] = 0;
							}
						}
					} else if (!oneDuration) {
						beta = new double[3];
						beta[0] = ydata[0]
								/ this.baselineOtherMortality[age][sex]; /* RRbegin */
						beta[1] = beta[0]; /* RRend */
						beta[2] = 0.5; /*
										 * with RRbegin=RRend alfa does not
										 * matter, so take 0.5
										 */
					} else { /*
							 * now beta is still the old beta from the previous
							 * loop
							 */

						// in case of duration class set rr to 1;

						/* put this in all the new beta's */
						beta = new double[3];
						beta[0] = RRdurationclass;
						beta[1] = RRdurationclass;
						beta[2] = 0;
					}
				} catch (Exception e) {
					this.log.fatal(e.getMessage());
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());

				}
				this.relRiskOtherMortBegin[age][sex] = (float) beta[0];
				this.relRiskOtherMortEnd[age][sex] = (float) beta[1];
				this.alphaOtherMort[age][sex] = (float) beta[2];

			}
		} else /* if not rr for other mortality present */
		{
			this.baselineOtherMortality[age][sex] = (float) sumOtherMort;
			Arrays.fill(this.relRiskOtherMort[age][sex], 1);
			this.relRiskOtherMortCont[age][sex] = 1;
			this.relRiskOtherMortBegin[age][sex] = 1;
			this.relRiskOtherMortEnd[age][sex] = 1;
			this.alphaOtherMort[age][sex] = 1; // does not really matter
		}

		/*
		 * now repeat for disability, but this has already been done for
		 * riskType==1
		 */
		if (withRRdisability && riskType != 1) {
			try {
				beta = weightedRegression(y2Value, xMatrix, wVector);
			} catch (Exception e) {

				e.printStackTrace();
				this.log
						.fatal("runtime error while estimating model parameters. e.getMessage()"
								+ " for age is " + age + "and sex is " + sex);
				throw new RuntimeException(e.getMessage());
			}
			if (age == 0 && sex == 0 && beta.length > 1)
				this.log
						.debug(" disability beta 0 and 1 :" + beta[0] + beta[1]);
			// calculate relative risks from the regression coefficients

			// first class has relative risk of 1; also default value for
			// all
			// other
			// categories
			Arrays.fill(this.riskFactorAbilityRRcat[age][sex], 1);

			if (riskType == 3) {
				for (int j = 1; j < beta.length; j++)
				// calculate the relative risk relative to the first
				// risk
				// class
				// //
				{
					this.riskFactorAbilityRRcat[age][sex][indexForCategories[j]] = (float) Math
							.exp(beta[j]);
					// in case of duration class set rr to 1;
					if (inputData.getIndexDuurClass() == j)
						this.riskFactorAbilityRRcat[age][sex][j] = 1;
				}

				// last beta is the coefficient for the continuous risk
				// factor
				// //
				this.riskFactorAbilityRRcont[age][sex] = (float) Math
						.exp(beta[beta.length - 1]);
			}

			if (inputData.getRiskType() == 3)
				this.riskFactorAbilityRRcont[age][sex] = 1;

			this.baselineAbility[age][sex] = (float) Math.exp(beta[0]);
			if (this.baselineAbility[age][sex] > 1)
				warnForAbilityGreaterOne(age, sex,
						this.baselineAbility[age][sex], 1 - inputData
								.getOverallDalyWeight()[age][sex], dsi);
			/**
			 * in the fifth stage the sum of the RR's on other cause mortalities
			 * is calculated in order to estimate the baseline other cause
			 * mortality This could also be derived from the regression
			 * (intercept)
			 * 
			 */
			if (inputData.getRiskType() == 3) { // now do time dependent
				// part;

				// first anker the RRbegin and RRend if those are ankered
				// for
				// all cause mortality

				double endRR = -1;
				double beginRR = -1;
				for (int rc = 0; rc < nRiskCat; rc++) {
					if (inputData.getRRforDisabilityBegin()[age][sex] == inputData
							.getRRforDisabilityCat()[age][sex][rc]
							&& rc != inputData.getIndexDuurClass())
						beginRR = this.riskFactorAbilityRRcat[age][sex][rc];
					if (inputData.getRRforDisabilityEnd()[age][sex] == inputData
							.getRRforDisabilityCat()[age][sex][rc]
							&& rc != inputData.getIndexDuurClass())
						endRR = this.riskFactorAbilityRRcat[age][sex][rc];
				}
				// select only the data for the duration class;
				double y2data[] = new double[this.duurFreq[age][sex].length];
				double x2data[] = new double[this.duurFreq[age][sex].length];
				double weightdata[] = new double[this.duurFreq[age][sex].length];
				int index = 0;
				double sumDif = 0;
				for (int i = 0; i < nSim; i++) {
					if (riskclass[i] == inputData.getIndexDuurClass()) {
						y2data[index] = abilityFromOtherCauses[i];
						x2data[index] = riskfactor[i];
						weightdata[index] = weight[i];
						sumDif += y2data[index] - y2data[0];
						index++;
					}

				}
				double RRdurationclass = 1;
				for (int j = 0; j < nRiskCat; j++)
					if (indexForCategories[j] == inputData.getIndexDuurClass())

						RRdurationclass = Math.exp(beta[j]);

				/*
				 * if RR average outside the area between RRend and RRbegin, the
				 * model can not be fitted anchored on both of them In this case
				 * we only anchor the end RR
				 */
				if ((endRR >= RRdurationclass && beginRR >= RRdurationclass)
						|| (endRR <= RRdurationclass
								&& beginRR <= RRdurationclass && endRR != -1))
					beginRR = -1;
				if (!oneDuration && Math.abs(sumDif) > 0.00001)
					try {
						beta = nonLinearDurationRegression(y2data, x2data,
								weightdata, endRR, beginRR,
								this.baselineAbility[age][sex]);
					} catch (Exception e) {
						boolean success = false;

						if (e.getMessage().equals("Repeat")) {
							endRR = -1;
							beginRR = -1;
							try {
								beta = nonLinearDurationRegression(y2data,
										x2data, weightdata, endRR, beginRR,
										this.baselineAbility[age][sex]);
								success = true;
							} catch (DynamoInconsistentDataException e2) {
								success = false;
							}
						}

						if (!success) {

							this.log.fatal(e.getMessage());
							displayWarningMessage(
									"WARNING: \n"
											+ e.getMessage()
											+ " Average RR is "
											+ RRdurationclass
											+ " for age: "
											+ age
											+ " and sex "
											+ sex
											+ "\nTherefore other disability is assumed not to "
											+ "dependent on duration of exposure in this age/sex combination. Note that incidence of "
											+ "diseases is still duration dependent as specified",
									dsi);

							beta = new double[3];
							beta[0] = RRdurationclass;
							beta[1] = RRdurationclass;
							beta[2] = 0;

						}
					}
				else if (!oneDuration) {
					beta = new double[3];
					beta[0] = y2data[0] / this.baselineAbility[age][sex]; /* RRbegin */
					beta[1] = beta[0]; /* RRend */
					beta[2] = 0.5; /*
									 * with RRbegin=RRend alfa does not matter,
									 * so take 0.5
									 */
				} else { /*
						 * now beta is still the old beta from the previous loop
						 */

					/* put this in all the new beta's */
					beta = new double[3];
					beta[0] = RRdurationclass;
					beta[1] = RRdurationclass;
					beta[2] = 0;
				}

				this.riskFactorAbilityRRbegin[age][sex] = (float) beta[0];
				this.riskFactorAbilityRRend[age][sex] = (float) beta[1];
				this.riskFactorAbilityAlpha[age][sex] = (float) beta[2];

			}
		}
		if (this.baselineAbility[age][sex] > 1 || !withRRdisability) {
			/* baseline Ability for this case has been calculated earlier */
			Arrays.fill(this.riskFactorAbilityRRcat[age][sex], 1);
			this.riskFactorAbilityRRcont[age][sex] = 1;
			this.riskFactorAbilityRRbegin[age][sex] = 1;
			this.riskFactorAbilityRRend[age][sex] = 1;
			this.riskFactorAbilityAlpha[age][sex] = 1; // does not really matter
		}

		if (age == 0 && sex == 0)
			this.log.debug("begin loop 5");
		if (age == 0 && sex == 0) {
			this.log.debug("\nbaseline ability for age 0, sex 0 : "
					+ this.baselineAbility[age][sex]);
			if (this.riskType != 2)
				this.log.debug("\nand  RR ability for category 2 : "
						+ this.riskFactorAbilityRRcat[age][sex][1]);
			else
				this.log.debug("\nand RR ability :"
						+ this.riskFactorAbilityRRcont[age][sex]);
		}
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
									* Math.exp(-this.alphaOtherMort[age][sex]
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

		}
		baselineOtherMortality2 = sumOtherMort / sumRROtherMort;
		/* check on RR's total mortality */
		double RRmortNew[] = new double[nSim];

		for (int i = 0; i < nSim; i++) {
			if (age == 55 && sex == 1 && i > 22) {

				int stop = 0;
				stop++;
			}
			if (inputData.getRiskType() == 1)
				RRmortNew[i] = mortalityFromDisease[i]
						+ baselineOtherMortality2
						* this.relRiskOtherMort[age][sex][riskclass[i]];
			if (inputData.getRiskType() == 2)
				RRmortNew[i] = mortalityFromDisease[i]
						+ baselineOtherMortality2
						* Math.pow(this.relRiskOtherMortCont[age][sex],
								riskfactor[i] - inputData.getRefClassCont());
			if (inputData.getRiskType() == 3)
				RRmortNew[i] = mortalityFromDisease[i]
						+ baselineOtherMortality2
						* ((this.relRiskOtherMortBegin[age][sex] - this.relRiskOtherMortEnd[age][sex])
								* Math.exp(-this.alphaOtherMort[age][sex]
										* riskfactor[i]) + this.relRiskOtherMortEnd[age][sex]);
			/*
			 * we take the old baseline mortality as reference, and calculate
			 * all RR relative to that
			 */
			/*
			 * this might mean that there are no categories with RR=1 , or that
			 * the RR is not 1 for RefClassCont()
			 */
			double RRmort = RRmortNew[i] / this.baselineMortality[age][sex];

			if (Math.abs(relRiskMort[i] - RRmort) > 0.1)
				this.log
						.fatal("WARNING: RR used for mortality differs from RR given for age: "
								+ age
								+ " sex: "
								+ sex
								+ " riskclass: "
								+ i
								+ " old RR: "
								+ relRiskMort[i]
								+ " new RR: "
								+ RRmort
								+ "\nMortality from diseases: "
								+ mortalityFromDisease[i]
								+ " totalMort: "
								+ RRmortNew[i]);
			RRmortNew[i] = RRmort;
		}

		if (negativeMortality || (riskType == 2 && withRRmort)
				|| (riskType == 3 && withRRmort)) {
			if (this.toCVS.toString().equals(""))
				this.toCVS.append("age" + SEPARATOR + "sex" + SEPARATOR
						+ "riskfactor" + SEPARATOR + "otherMort" + SEPARATOR
						+ "otherMortFitted" + SEPARATOR + "diseaseMort"
						+ SEPARATOR + "totalMort" + "\n");
			for (int i = 0; i < nSim; i++) {
				double fitted = 0;
				if (inputData.getRiskType() == 1)
					fitted = baselineOtherMortality2
							* this.relRiskOtherMort[age][sex][riskclass[i]];
				if (inputData.getRiskType() == 2)
					fitted = baselineOtherMortality2
							* Math
									.pow(this.relRiskOtherMortCont[age][sex],
											riskfactor[i]
													- inputData
															.getRefClassCont());
				if (inputData.getRiskType() == 3)
					fitted = baselineOtherMortality2
							* ((this.relRiskOtherMortBegin[age][sex] - this.relRiskOtherMortEnd[age][sex])
									* Math.exp(-this.alphaOtherMort[age][sex]
											* riskfactor[i]) + this.relRiskOtherMortEnd[age][sex]);
				String data = age + SEPARATOR + sex + SEPARATOR + riskfactor[i]
						+ SEPARATOR + otherMort[i] + SEPARATOR + fitted
						+ SEPARATOR + mortalityFromDisease[i] + SEPARATOR
						+ relRiskMort[i] * this.baselineMortality[age][sex]
						+ "\n";
				this.toCVS.append(data);

			}
		}
		;
		if (age == 0
				&& sex == 0
				&& baselineOtherMortality2 != this.baselineOtherMortality[age][sex])
			this.log.debug("different baseline mortalities calculated nl "
					+ baselineOtherMortality2 + " after calibration and  "
					+ this.baselineOtherMortality[age][sex] + " before");
		if (baselineOtherMortality2 != 0)
			if ((Math.abs(baselineOtherMortality2
					- this.baselineOtherMortality[age][sex])
					/ baselineOtherMortality2 > 0.001))
				this.log
						.debug("different baseline mortalities calculated after calibration nl "
								+ baselineOtherMortality2
								+ " after calibration while  "
								+ this.baselineOtherMortality[age][sex]
								+ " before (age=" + age + "; sex=" + sex + ").");
		this.baselineOtherMortality[age][sex] = (float) baselineOtherMortality2;

		if (age == 0 && sex == 0)
			this.log.debug("end loop 6");

	}

	private boolean constantY(double[] value) {
		boolean constantY = true;
		for (int i = 0; i < value.length; i++)
			if (value[i] != value[0])
				constantY = false;
		return constantY;
	}

	private double calculateAbilityFromDiseases(DiseaseClusterData[] inputData,
			int nDiseases, double[][] prevalenceDiseaseStates) {

		/*
		 * if disease clusters are independent, then we can multiply the average
		 * ability in each cluster to get the average population ability
		 * 
		 * With a single disease, the ability in a cluster is 1-p+pA, where 1-p
		 * is the contribution of ability of those without the disease
		 * (ability=1 for this group) and pA the ability from those with the
		 * disease
		 * 
		 * 
		 * to calculate within the cluster, loop over all possible disease
		 * combinations and calculate the disease-cause disability, and sum
		 * these to obtain the total disability caused by disease
		 */

		double abilityFromDiseases = 1;

		if (nDiseases > 0)
			for (int c = 0; c < nCluster; c++) {

				if (clusterStructure[c].isWithCuredFraction()) {
					double probState = prevalenceDiseaseStates[c][1]
							+ prevalenceDiseaseStates[c][2];
					abilityFromDiseases *= (1 - probState + probState
							* inputData[c].getAbility()[0]);

				} else {

					/*
					 * calculate the ability in the clusters as the average of
					 * the ability of each state, that is the sum of
					 * probability(state) ability(state)
					 */
					double abilityFromCluster = 0;
					double probState;
					double abilityState;
					for (int stateInCluster = 0; stateInCluster < Math.pow(2,
							clusterStructure[c].getNInCluster()); stateInCluster++) {

						probState = prevalenceDiseaseStates[c][stateInCluster];
						abilityState = 1;
						for (int d = 0; d < clusterStructure[c].getNInCluster(); d++) {
							/*
							 * in case the disease=0, the ability is not
							 * affected so no action is needed in case the
							 * disease=1, multiply with the ability of the
							 * disease
							 */
							if ((stateInCluster & (1 << d)) == (1 << d))
								abilityState *= inputData[c].getAbility()[d];
						}

						abilityFromCluster += abilityState * probState;
					}
					/*
					 * the total ability from disease is derived by multiplying
					 * the abilities for each cluster
					 */
					abilityFromDiseases *= abilityFromCluster;
				}
			} // end loop over clusters

		return abilityFromDiseases;
	}

	private void warnForAbilityGreaterOne(int age, int sex, double ability,
			float overallAbility, DynSimRunPRInterface dsi) {
		String label = "";
		if (nWarningsDisability == 2)
			label = " NO MORE WARNINGS OF THIS TYPE WILL BE ISSUED FOR"
					+ " OTHER AGE/SEX GROUPS";

		if (nWarningsDisability < 3)
			displayWarningMessage(
					"WARNING:\nOverall dalyweight/disability is smaller than dalyweight/disability due "
							+ "to diseases for age "
							+ age
							+ " and gender "
							+ sex
							+ " : disability due to diseases: "
							+ (1 - ability)
							+ " and overall: "
							+ (1 - overallAbility)
							+ " . Other cause disability is set"
							+ " to zero."
							+ label, dsi);
		nWarningsDisability++;
		this.baselineAbility[age][sex] = 1;
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
	private void calculateBaselineInc(InputData inputData, int age, int sex,
			double[] meanRR, boolean isDependent) {

		for (int c = 0; c < inputData.getNCluster(); c++) {
			if (inputData.clusterStructure[c].isWithCuredFraction()) {
				int d = inputData.clusterStructure[c].getDiseaseNumber()[0];
				this.baselineIncidence[age][sex][d] = (float) (inputData
						.getClusterData()[age][sex][c].getIncidence()[d]
						* (1 - inputData.getClusterData()[age][sex][c]
								.getCaseFatality()[d])

						* (1 - inputData.getClusterData()[age][sex][c]
								.getPrevalence()[d] - inputData
								.getClusterData()[age][sex][c].getPrevalence()[d + 1]) / meanRR[d]);
				this.baselineIncidence[age][sex][d + 1] = (float) (inputData
						.getClusterData()[age][sex][c].getIncidence()[d + 1]
						* (1 - inputData.getClusterData()[age][sex][c]
								.getCaseFatality()[d + 1])

						* (1 - inputData.getClusterData()[age][sex][c]
								.getPrevalence()[d] - inputData
								.getClusterData()[age][sex][c].getPrevalence()[d + 1]) / meanRR[d + 1]);
			}

			else {// loop over the diseases within clusters
				for (int dc = 0; dc < inputData.clusterStructure[c]
						.getNInCluster(); dc++) {
					// this is done either for the independent diseases or the
					// dependent diseases;
					if (inputData.clusterStructure[c].getDependentDisease()[dc] == isDependent) {
						int d = inputData.clusterStructure[c]
								.getDiseaseNumber()[dc];
						this.baselineIncidence[age][sex][d] = (float) (inputData
								.getClusterData()[age][sex][c].getIncidence()[dc]
								* (1 - inputData.getClusterData()[age][sex][c]
										.getCaseFatality()[dc])

								* (1 - inputData.getClusterData()[age][sex][c]
										.getPrevalence()[dc]) / meanRR[d]);

					}
				}
			} // end loops over diseases within cluster
		}
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
	 * RR=(RRbegin-RRend)exp(-alpha*time)+RRend.
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
	 *         estimate of Alpha;</le> </ls>
	 * @throws Exception
	 */
	private double[] nonLinearDurationRegression(double[] y_array,
			double[] x_array, double[] W, double endRR, double beginRR,
			double baselineMort) throws DynamoInconsistentDataException

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
		double currentAlpha;
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
		currentAlpha = 3 / (x_array[x_array.length - 1] - x_array[0]);
		if ((new Double(currentRRbegin)).isNaN())
			currentRRbegin = 0.1;
		if ((new Double(currentRRend)).isNaN())
			currentRRend = 0.1;

		double delRRbegin[] = new double[x_array.length];
		double delRRend[] = new double[x_array.length];
		double delAlpha[] = new double[x_array.length];
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
						* Math.exp(-currentAlpha * x_array[i]) + currentRRend;
				delRRbegin[i] = Math.exp(-currentAlpha * x_array[i]);
				delRRend[i] = 1 - Math.exp(-currentAlpha * x_array[i]);
				delAlpha[i] = -x_array[i] * (currentRRbegin - currentRRend)
						* Math.exp(-currentAlpha * x_array[i]);
				delY[i] = Ydata[i] - fitted[i];
				if (!Double.isNaN(Ydata[i])) {
					Criterium += (delY[i] * delY[i]) * W[i];
					trace[0] += delAlpha[i];
					if (nParam == 2 && beginRR == -1)
						trace[1] += delRRend[i] * delRRend[i] * W[i];
					if (nParam == 2 && endRR == -1)
						trace[1] += delRRbegin[i] * delRRbegin[i] * W[i];
					if (nParam == 3)
						trace[2] += delRRend[i] * delRRend[i] * W[i];
				}
			}
			if (nParam == 1)
				for (int k = 0; k < x_array.length; k++) {

					jMat[k][0] = delAlpha[k];
				}
			if (nParam == 2 && endRR == -1)
				for (int k = 0; k < x_array.length; k++) {

					jMat[k][0] = delAlpha[k];
					jMat[k][1] = delRRend[k];
				}
			if (nParam == 2 && beginRR == -1)
				for (int k = 0; k < x_array.length; k++) {

					jMat[k][0] = delAlpha[k];
					jMat[k][1] = delRRbegin[k];
				}
			if (nParam == 3)
				for (int k = 0; k < x_array.length; k++) {

					jMat[k][0] = delAlpha[k];
					jMat[k][1] = delRRbegin[k];
					jMat[k][2] = delRRend[k];
				}
			int iter2 = 0;
			if (checkX(jMat)) {
				try {
					resultReg = weightedRegression(delY, jMat, W);
				} catch (Exception e) {
					if (nParam == 3)
						throw new DynamoInconsistentDataException(
								"Regression for other mortality failed");
					else
						throw new DynamoInconsistentDataException("Repeat");
				}

				oldCriterium = Criterium;
				old1 = currentAlpha;
				old2 = currentRRend;
				old3 = +currentRRbegin;

				if (lambda > 10)
					lambda = 8;
				while (Criterium >= oldCriterium && iter2 < 50) {
					Criterium = 0;
					++iter2;
					currentAlpha = old1 + resultReg[0] / lambda;
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
					if (currentAlpha < 0.001)
						currentAlpha = 0.001; /*
											 * this forces a linear model with
											 * time in case of inconsistent data
											 * that is data for which the time
											 * dependency does not fit the model
											 * (r1-r2)exp(-alphat) +r2
											 */
					if (currentAlpha > 35)
						currentAlpha = 35;
					/* if alfa is larger than 35, it causes numerical problems */
					for (int i = 0; i < x_array.length; i++) {
						delY[i] = Ydata[i]
								- ((currentRRbegin - currentRRend)
										* Math.exp(-currentAlpha * x_array[i]) + currentRRend);
						if (!Double.isNaN(Ydata[i]))
							Criterium += (delY[i] * delY[i]) * W[i];
					}
					if (Criterium >= oldCriterium)
						lambda = lambda * 2;
					this.log.debug(" lambda " + lambda + " halvingsteps = "
							+ iter2);
				}
			} else {
				throw new DynamoInconsistentDataException(
						"No duration dependence can be estimate for other mortality as data do not"
								+ " support the assumed model, which assumes an RR between "
								+ beginRR + " and " + endRR);
			}

			this.log
					.debug(" non-linear regression other cause mortality: iteration "
							+ iter + " criterium = " + Criterium);
			this.log.debug(" lambda " + lambda + " halvingsteps = " + iter2);
			this.log.debug("alpha " + currentAlpha + " RR end " + currentRRend
					+ " RR begin " + currentRRbegin);
			if (lambda > 1)
				lambda = lambda / 2;
			if (Math.abs(old1 - currentAlpha) / old1 < 0.001
					&& Math.abs(old2 - currentRRend) / old2 < 0.001
					&& Math.abs(old3 - currentRRbegin) / old3 < 0.001)
				break;
			else if (iter == 499)
				this.log
						.fatal("ERROR: non-linear regression other cause mortality did not converge in 500 iterations "
								+ " results: alpha "
								+ currentAlpha
								+ " RR end "
								+ currentRRend
								+ " RR begin "
								+ currentRRbegin + " criterium = " + Criterium);
		}
		result[0] = currentRRbegin;
		result[1] = currentRRend;
		result[2] = currentAlpha;

		return result;
	}

	private boolean checkX(double[][] mat) {
		double first = mat[0][0];
		boolean equal = true;
		for (int i = 0; i < mat.length; i++)
			for (int j = 0; j < mat[i].length; j++)
				if (mat[i][j] != first)
					equal = false;
		return !equal;
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
	public double[] weightedRegression(double[] y_array_input, double[][] x_array_input,
			double[] w_input) throws DynamoInconsistentDataException {

		// check dimensions //

		if (y_array_input.length != w_input.length)
			throw new DynamoInconsistentDataException(
					" Array lengths of y and weights differ in method weighted regression");
		if (x_array_input.length != w_input.length)
			throw new DynamoInconsistentDataException(
					" Array lengths of x and weights differ in method weighted regression");
		if (y_array_input.length != x_array_input.length)
			throw new DynamoInconsistentDataException(
					" Array lengths of x and y differ in method weighted regression");

		/* remove NAN values by making the weight equal to 0 */
		/*
		 * use new arrays, in order not to overwrite the old array that might be used
		 * again in the main program
		 */
		double[] w = new double[w_input.length];
		double[] y_array = new double [w_input.length];
		double[][] x_array= new double [w_input.length][x_array_input[0].length];
		for (int i = 0; i < w_input.length; i++) {
			w[i] = w_input[i];
			y_array[i]=y_array_input[i];
			if ((new Double(y_array[i])).isNaN())
				w[i] = 0;
			for (int j = 0; j < x_array[0].length; j++){
				if ((new Double(x_array[i][j])).isNaN())
					w[i] = 0;
			x_array[i][j]=x_array_input[i][j];
		}}
		double[] coef = null;
		/* check if all Y-values are the same */
		if (!constantY(y_array)) {

			/*
			 * throw out data with zero weight by first shifting all data to the
			 * front and then truncating the arrays to this part
			 */
			int numberOfValidDataPoints = shiftZeroWeights(y_array, x_array, w);
			if (numberOfValidDataPoints != w.length) {
				x_array = truncate(numberOfValidDataPoints, x_array);
				y_array = truncate(numberOfValidDataPoints, y_array);
				w = truncate(numberOfValidDataPoints, w);
			}

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
			try {
				Matrix inverseXX = XX.inverse();

				Matrix XY = XT.times(Y);
				// beta are the regression coefficients;

				Matrix Beta = inverseXX.times(XY);
				coef = Beta.getColumnPackedCopy();
			} catch (Exception e) {
				log.fatal(e.getMessage() + "  y + x = ");
				for (int i = 0; i < y_array.length; i++) {
					String data = y_array[i] + " ";
					for (int j = 0; j < x_array[0].length; j++)
						data += x_array[i][j] + " ";
					log.fatal(data);
					throw new DynamoInconsistentDataException(e.getMessage());
				}
			}
		} else {
			/* if all Y-values are the same, fill the coefficient by hand */
			coef = new double[x_array[0].length];
			Arrays.fill(coef, 0);
			coef[0] = y_array[0];
		}
		return coef;
	};

	/**
	 * Returns an array with only the first toN elements of the inputArray
	 * 
	 * @param toN
	 * @param inputArray
	 * @return
	 */
	private double[] truncate(int toN, double[] inputArray) {
		double[] returnArray = new double[toN];
		for (int i = 0; i < toN; i++) {
			returnArray[i] = inputArray[i];
		}
		return returnArray;
	}

	/**
	 * Returns an array with only the first toN elements of the inputArray
	 * 
	 * @param toN
	 * @param inputArray
	 * @return
	 */
	private double[][] truncate(int toN, double[][] inputArray) {
		double[][] returnArray = new double[toN][];
		for (int i = 0; i < toN; i++) {
			returnArray[i] = inputArray[i];
		}

		return returnArray;
	}

	private int shiftZeroWeights(double[] y_array, double[][] x_array,
			double[] w) {

		int numberOfZeroWeights = 0;
		for (int i = 0; i < w.length; i++) {
			if (w[i] == 0)
				numberOfZeroWeights++;
		}
		int newLength = w.length - numberOfZeroWeights;
		if (numberOfZeroWeights > 0) {

			int currentPointer = 0;
			int step = 0;
			while (step < w.length) {
				step++;
				if (w[currentPointer] == 0) /* shift all data one to the front */
					for (int i = currentPointer; i < w.length - 1; i++) {
						w[i] = w[i + 1];
						y_array[i] = y_array[i + 1];
						x_array[i] = x_array[i + 1];
					}
				else
					currentPointer++;

			}
		}
		return newLength;
	}

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
			/*
			 * (E^(at t) inotcured (-1 + p) + E^(inotcured t) (inotcured - at
			 * p))/( at E^(at t) (-1 + p) + E^(inotcured t) (inotcured - at p))
			 * is equal to next formula
			 */
			// finci = ((p0 * em - i) * exp((i - em) * time) + i * (1 - p0)) /
			// ((p0 * em - i) * exp((i - em) * time) + em * (1 - p0))
			/*
			 * 
			 * next prevalence not cured as function of total prevalence :
			 * ((icured + inotcured) (E^at inotcured (-1 + p1 + p2) + E^(icured
			 * + inotcured) (inotcured - inotcured p1 + (-at + icured) p2)))/(at
			 * E^ at inotcured (-1 + p1 + p2) + E^(icured + inotcured) (-E^ at
			 * (-at + icured + inotcured) (-inotcured p1 + icured (-1 + p2)) +
			 * (icured + inotcured) (inotcured - inotcured p1 + (-at + icured)
			 * p2)))
			 * 
			 * This is too complicated, and we therefore ignore the cured cases,
			 * and proceed as if they are still at risk
			 * 
			 * Then we get next not cured prevalence as function of last (p):
			 * (E^at inotcured (-1 + p) + E^inotcured (inotcured - at p))/( at
			 * E^at (-1 + p) + E^inotcured (inotcured - at p)) where inc is
			 * incidence of not cured, and at the excess mortality
			 * 
			 * Multiply with -E^-at /-E^-at: nominator: inotcured (1-p ) + E^( i
			 * - at)(at p - i) denominator: (1-p)at + E^(i - at)(at p - i)
			 */
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
			 * (p0 + i t - i p0 t)/(1 + i t - i p0 t)
			 * 
			 * CHECKED =OK
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
	 * @param pri
	 *            TODO
	 * @param e
	 */
	private void displayWarningMessage(String message, DynSimRunPRInterface dsi) {
		// Shell shell = new Shell(this.parentShell);
		// MessageBox messageBox = new MessageBox(shell, SWT.OK);
		// messageBox.setMessage(message);
		// messageBox.open();
		dsi.usedToBeErrorMessageWindow(message);
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
	 * @return relative risks for diseases on other diseases for age a and sex s
	 *         . NB this is an irregular array indexes: age, gender, cluster,
	 *         from, to
	 */
	public float[][][] getRelRiskDiseaseOnDisease(int a, int s) {
		return DynamoLib.deepcopy(this.relRiskDiseaseOnDisease[a][s]);
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
	 * @return baseline prevalence odds for age and sex. Index is diseasenumber
	 */
	public double[] getBaselinePrevalenceOdds(int age, int sex) {
		return DynamoLib.deepcopy(this.baselinePrevalenceOdds[age][sex]);
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
	 * @return the alpha coefficient (decrease of relative risk with duration)
	 *         for other cause mortality. indexes are age and sex
	 */
	public float[][] getAlphaOtherMort() {
		return DynamoLib.deepcopy(this.alphaOtherMort);
	}

	/**
	 * @param alphaOtherMort
	 */
	public void setAlfaOtherMort(float[][] alphaOtherMort) {
		this.alphaOtherMort = alphaOtherMort;
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
	 * @return relative risk for categorical risk factor for age and sex.
	 *         Indexes are: from, to=diseasenumber
	 */
	public float[][] getRelRiskClass(int age, int sex) {
		return DynamoLib.deepcopy(this.relRiskClass[age][sex]);
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
	 * @return relative risk for a continuous risk factor for age and sex. Index
	 *         is and diseaseNumber
	 */
	public float[] getRelRiskContinue(int age, int sex) {
		return DynamoLib.deepcopy(this.relRiskContinue[age][sex]);
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
	 * @throws DynamoInconsistentDataException
	 */
	public void setPrevRisk(float[][][] prevRisk)
			throws DynamoInconsistentDataException {
		this.prevRisk = prevRisk;
		for (int a = 0; a < 96; a++)
			for (int s = 0; s < 2; s++) {

				float sumP = 0;
				for (float prev : prevRisk[a][s])
					sumP += prev;
				if (Math.abs(sumP - 1.0) > 1E-3)
					throw new DynamoInconsistentDataException(
							"Risk factor prevalence does not sum "
									+ "to 100% but to " + 100 * sumP + "%"
									+ " for age " + a + " and gender " + s);
				else if (Math.abs(sumP - 1.0) > 1E-8)
					prevRisk[a][s][prevRisk[a][s].length - 1] += 1 - sumP;
			}

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
	 * @return relative risk at the beginning of the duration in the duration
	 *         class of a compound riskfactor for age age and sex sex,. Index is
	 *         disease
	 */
	public float[] getRelRiskDuurBegin(int age, int sex) {
		return DynamoLib.deepcopy(this.relRiskDuurBegin[age][sex]);
	}

	/**
	 * @return relative risk at the end of the duration in the duration class of
	 *         a compound riskfactor for age age and sex sex,. Index is disease
	 */
	public float[] getRelRiskDuurEnd(int age, int sex) {
		return DynamoLib.deepcopy(this.relRiskDuurEnd[age][sex]);
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
	 * @return the alpha value for decreasing of the relative risk with duration
	 */
	public float[][][] getAlphaDuur() {
		return DynamoLib.deepcopy(this.alphaDuur);
	}

	/**
	 * @return the alpha value for decreasing of the relative risk with duration
	 */
	public float[] getAlphaDuur(int age, int sex) {
		return DynamoLib.deepcopy(this.alphaDuur[age][sex]);
	}

	/**
	 * @param alphaDuur
	 */
	public void setAlfaDuur(float[][][] alphaDuur) {
		this.alphaDuur = alphaDuur;
	}

	/**
	 * @return initial frequency of durations in the duration class of a
	 *         compound risk factor
	 */
	public float[][][] getDuurFreq() {
		return DynamoLib.deepcopy(this.duurFreq);
	}

	/**
	 * @return initial frequency of durations in the duration class of a
	 *         compound risk factor
	 */
	public float[] getDuurFreq(int age, int sex) {
		return DynamoLib.deepcopy(this.duurFreq[age][sex]);
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
