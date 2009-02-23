package nl.rivm.emi.dynamo.estimation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import Jama.Matrix;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.dynamo.datahandling.DynamoConfigurationData;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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

	private String RiskTypeDistribution = null;// TODO
	private int durationClass = -1;
	private float refClassCont = -1;
	private int nCluster = -1;
	private DiseaseClusterStructure[] clusterStructure;
	/* relRiskDiseaseOnDisease[][][][][] : third index = cluster nr */
	private float relRiskDiseaseOnDisease[][][][][] = new float[96][2][][][];
	private float baselineIncidence[][][] = new float[96][2][];;
	private float baselineFatalIncidence[][][] = new float[96][2][];;
	private float curedFraction[][][] = new float[96][2][];;
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
	private float[][][] diseaseDisabilityOR = new float[96][2][];
	private float[][] baselineDisability = new float[96][2];
	/* for disability we do not have a duration option */
	private float[][][] riskFactorDisabilityORcat = new float[96][2][];
	private float[][] riskFactorDisabilityORcont = new float[96][2];
	private float[][][] relRiskDuurBegin = new float[96][2][];
	private float[][][] relRiskDuurEnd = new float[96][2][];
	private float[][][] alfaDuur = new float[96][2][];
	private float[][][] duurFreq = new float[96][2][];
	private float[][] meanDrift = new float[96][2];// TODO
	private float[][] stdDrift = new float[96][2];// TODO
	private float[][] offsetDrift = new float[96][2];// TODO
	private float[][][][] transitionMatrix = new float[96][2][][];
	private boolean zeroTransition;// TODO
    private Population [] initialPopulation;
	// empty Constructor

	public ModelParameters() {
	};

	/**
	 * 
	 * @param nSim
	 *            number of simulated subjects used in the parameter estimation
	 *            in case of continuous or compound risk factors
	 * @param inputData
	 *            Object that holds the input data
	 * @throws DynamoInconsistentDataException
	 * 
	 * @throws Exception
	 *             : not yet handled (from net transition rates: to do
	 * 
	 * @returns ScenarioInfo: an object containing information that is needed
	 *          for postprocessing
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
		 * primitive types TODO deep copy for arrays
		 */
		riskType = inputData.getRiskType();
		RiskTypeDistribution = inputData.getRiskDistribution();
		refClassCont = inputData.getRefClassCont();
		if (riskType != 2)
			prevRisk = inputData.getPrevRisk();
		if (riskType == 3)
			duurFreq = inputData.getDuurFreq();
		if (RiskTypeDistribution == "Normal") {
			meanRisk = inputData.getMeanRisk();
			stdDevRisk = inputData.getStdDevRisk();
			zeroTransition = (inputData.getTransType() == 0);
			offsetRisk = null;
		} else {

			/*
			 * NB: same calculation is in the setMeanSTD method of scenarioInfo,
			 * so if there are errors here they should also be corrected there
			 */
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++) {

					try {
						float skew = inputData.getSkewnessRisk()[a][g];
						stdDevRisk[a][g] = (float) DynamoLib.findSigma(skew);

						meanRisk[a][g] = (float) (0.5 * (Math.log(skew * skew)
								- Math.log(Math.exp(stdDevRisk[a][g]
										* stdDevRisk[a][g]) - 1) - stdDevRisk[a][g]
								* stdDevRisk[a][g]));
						offsetRisk[a][g] = (float) (inputData.getMeanRisk()[a][g] - Math
								.exp(meanRisk[a][g] + 0.5 * stdDevRisk[a][g]
										* stdDevRisk[a][g]));
					} catch (Exception e) {

						log
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

		nCluster = inputData.getNCluster();
		clusterStructure = inputData.clusterStructure;
		durationClass = inputData.getIndexDuurClass();
		int nRiskClasses;
		if (riskType != 2)
			nRiskClasses = inputData.getPrevRisk()[0][0].length;
		else
			nRiskClasses = 1;
		log.fatal("before split");
		splitCuredDiseases(inputData);
		log.fatal("after split");
		if (inputData.getRiskType() != 2)
			transitionMatrix = new float[96][2][nRiskClasses][inputData
					.getPrevRisk()[0][0].length];
		NettTransitionRateFactory factory = new NettTransitionRateFactory();
		/* set up progress bar for this part of the calculations */
		Display display = new Display();
		Shell shell = new Shell(display);
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
				log.fatal("before first estimate");
				estimateModelParametersForSingleAgeGroup(nSim, inputData, a, g);
				log.fatal("parameters estimated for age " + a + " and gender "
						+ g);
				if (a > 0) {
					// TODO
					/*
					 * nog toevoegen: alleen als nettransition rates geschat
					 * moeten worden
					 */
					if (inputData.getRiskType() != 2) {
						if (inputData.getTransType() == 1) { /*
															 * nett transition
															 * rates
															 */
							transitionMatrix[a - 1][g] = factory
									.makeNettTransitionRates(
											getPrevRisk()[a - 1][g],
											inputData.getPrevRisk()[a][g],
											baselineMortality[a - 1][g],
											inputData.getRelRiskMortCat()[a - 1][g]);
						} else if (inputData.getTransType() == 2)
							transitionMatrix[a - 1][g] = inputData
									.getTransitionMatrix(a - 1, g);
						else if (inputData.getTransType() == 1)
							transitionMatrix[a - 1][g] = inputData
									.getTransitionMatrix(a - 1, g);
						else if (inputData.getTransType() == 0) {
							/*
							 * this matrix is not really used, but implemented
							 * all the same in case a future programmer needs it
							 */
							float mat[][] = new float[nRiskClasses][nRiskClasses];
							for (int r1 = 0; r1 < nRiskClasses; r1++)
								for (int r2 = 0; r2 < nRiskClasses; r2++) {
									if (r1 == r2)
										mat[r1][r2] = 1;
									else
										mat[r1][r2] = 0;
								}
							transitionMatrix[a - 1][g] = mat;

						}
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
						.getSkewnessRisk(), baselineMortality, inputData
						.getRelRiskMortCont(), refClassCont, inputData
						.getMeanDrift());
			}
			if (inputData.getTransType() == 1) /* netto transitionrates */{
				drift = factory.makeNettTransitionRates(
						inputData.getMeanRisk(), inputData.getStdDevRisk(),
						inputData.getSkewnessRisk(), baselineMortality,
						inputData.getRelRiskMortCont(), refClassCont);
			}
			meanDrift = drift[0];
			stdDrift = drift[1];
			offsetDrift = drift[2];

			if (inputData.getTransType() == 0) /*
												 * zero transitionrates; not
												 * really used but in case
												 * someone expects the data
												 */{
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++) {
						meanDrift[a][g] = 0;
						stdDrift[a][g] = 0;
						offsetDrift[a][g] = 0;
					}
			}
		}
		bar.setSelection(97);
		/*
		 * while (!shell.isDisposed ()) { if (!display.readAndDispatch ())
		 * display.sleep (); }
		 */
		shell.close();
		display.dispose();

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
	 * @returns ScenarioInfo: an object with information needed in
	 *          postprocessing
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
	public ScenarioInfo estimateModelParameters(String simulationName)
			throws DynamoInconsistentDataException,
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

		BaseDirectory B = BaseDirectory
				.getInstance("c:\\hendriek\\java\\dynamohome");
		String BaseDir = B.getBaseDir();
		InputDataFactory config = new InputDataFactory(simulationName);
		InputData inputData = new InputData();

		ScenarioInfo scenInfo = new ScenarioInfo();
		log.fatal("overall configuration read");
		config
				.addPopulationInfoToInputData(simulationName, inputData,
						scenInfo);
		log.fatal("population info added");
		config.addRiskFactorInfoToInputData(inputData, scenInfo);
		log.fatal("risk factor info added");
		config.addDiseaseInfoToInputData(inputData, scenInfo);
		config.addScenarioInfoToScenarioData(simulationName, scenInfo);

		log.fatal("disease info added");

		/** * 2. uses the inputdata to estimate the model parameters */
		estimateModelParameters(nSim, inputData);
		/** * 3. write xml files needed by the simulation module */

		SimulationConfigurationFactory s = new SimulationConfigurationFactory(
				simulationName);
		s.manufactureSimulationConfigurationFile(this, scenInfo);
		log.debug("SimulationConfigurationFile written ");
		s.manufactureCharacteristicsConfigurationFile(this);
		log.debug("CharacteristicsConfigurationFile written ");
		s.manufactureUpdateRuleConfigurationFiles(this, scenInfo);
		log.debug("UpdateRuleConfigurationFile written ");

		/** * 4. write the initial population file for all scenarios */

		InitialPopulationFactory popFactory = new InitialPopulationFactory();
		int seed = config.getRandomSeed();
		int nSim = config.getSimPopSize();
		
		initialPopulation = popFactory.manufactureInitialPopulation(this,
				simulationName, nSim, seed, false, scenInfo);
		/*: obsolete: write
		popFactory.writeInitialPopulation(this, nSim, simulationName, seed,
				false, scenInfo);
  */
		/** * 5. writes a population of newborns  */
		
		if (scenInfo.isWithNewBorns()){
		Population[] newborns = popFactory.manufactureInitialPopulation(this,
				simulationName, nSim, seed, true, scenInfo);
		
  /*: obsolete: write
		if (scenInfo.isWithNewBorns())
			popFactory.writeInitialPopulation(this, nSim, simulationName, seed,
					true, scenInfo); */
		for (int p=0; p<initialPopulation.length;p++){
			newborns[p].addAll(initialPopulation[p]);
		initialPopulation[p]=newborns[p];
		// initialPopulation[p].addAll(newborns[p]);
		}}
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
				if (inputData.clusterStructure[c].getNInCluster() > 1)
					throw new DynamoInconsistentDataException(
							"Error for disease "
									+ inputData.clusterStructure[c]
											.getDiseaseName().get(0)
									+ ". Cured fraction only allowed in diseases not related to other diseases");

				inputData.clusterStructure[c]
						.setNInCluster(inputData.clusterStructure[c]
								.getNInCluster() + 1);
				String name = inputData.clusterStructure[c].getDiseaseName()
						.get(0);
				DiseaseClusterStructure newStructure = inputData.clusterStructure[c];
				newStructure.setDiseaseName(name + "_cured", 0);
				newStructure.setDiseaseName(name + "_notcured", 1);
				inputData.setClusterStructure(newStructure, c);
				float halfTime;
				float[] incidence = new float[96];
				float[] prevalence = new float[96];
				float[] RRcat = null;
				float RRcont;
				float RRduurEnd;
				float RRduurBegin;
				float halftime;
				float[] curedFraction = new float[96];
				float[] totExcess = new float[96];
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
						prevalence[a] = inputData.getClusterData()[a][g][c]
								.getPrevalence()[0];
						curedFraction[a] = inputData.getClusterData()[a][g][c]
								.getCuredFraction()[0];

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
						RRcat = new float[inputData.getClusterData()[a][g][c]
								.getRelRiskCat().length];

						newdata.setRelRiskCat(new float[RRcat.length][2]);
						for (int cat = 0; cat < RRcat.length; cat++) {
							RRcat[cat] = inputData.getClusterData()[a][g][c]
									.getRelRiskCat()[cat][0];
							newdata.setRelRiskCat(RRcat[cat], cat, 0);
							newdata.setRelRiskCat(RRcat[cat], cat, 1);
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
		if (riskType != 2)
			nRiskCat = inputData.getPrevRisk()[0][0].length;
		int nDiseases = getNDiseases(inputData);

		/* now copy data directly from DiseaseClusterData to parameter fields */

		attributableMortality[age][sex] = new float[nDiseases];
		relRiskContinue[age][sex] = new float[nDiseases];
		relRiskClass[age][sex] = new float[nRiskCat][nDiseases];
		relRiskDuurBegin[age][sex] = new float[nDiseases];
		relRiskDuurEnd[age][sex] = new float[nDiseases];
		alfaDuur[age][sex] = new float[nDiseases];
		float[] disability = new float[nDiseases];
		float relRiskMortCont;
		double log2 = Math.log(2.0); // keep outside loops to prevent
		// recalculation
		/* put prevalence also in a single array for easy access */
		float[][][] diseasePrevalence = new float[96][2][nDiseases];
		relRiskDiseaseOnDisease[age][sex] = new float[nCluster][][];
		float[] relRiskMortCat;
		float[] excessMortality = new float[nDiseases];
		for (int c = 0; c < nCluster; c++)
			for (int dc = 0; dc < inputData.clusterStructure[c].getNInCluster(); dc++) {
				int dNumber = inputData.clusterStructure[c].getDiseaseNumber()[dc];

				excessMortality[dNumber] = inputData.getClusterData()[age][sex][c]
						.getExcessMortality()[dc];
				disability[dNumber] = inputData.getClusterData()[age][sex][c]
						.getDisability()[dc];
			}
		if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
			relRiskContinue[age][sex] = new float[inputData.getNDisease()];
		if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
			refClassCont = 0;
		for (int c = 0; c < nCluster; c++) {

			relRiskDiseaseOnDisease[age][sex][c] = inputData.getClusterData()[age][sex][c]
					.getRRdisExtended();

			for (int d = 0; d < inputData.clusterStructure[c].getNInCluster(); d++) {
				int dNumber = inputData.clusterStructure[c].getDiseaseNumber()[d];
				if (riskType == 3) {
					relRiskDuurEnd[age][sex][dNumber] = inputData
							.getClusterData()[age][sex][c].getRelRiskDuurEnd()[d];
					relRiskDuurBegin[age][sex][dNumber] = inputData
							.getClusterData()[age][sex][c]
							.getRelRiskDuurBegin()[d];
					alfaDuur[age][sex][dNumber] = inputData.getClusterData()[age][sex][c]
							.getAlpha()[d];

				}
				diseasePrevalence[age][sex][clusterStructure[c]
						.getDiseaseNumber()[d]] = inputData.getClusterData()[age][sex][c]
						.getPrevalence()[d];

				if (inputData.getRiskType() == 1
						|| inputData.getRiskType() == 3)
					for (int i = 0; i < nRiskCat; i++)
						relRiskClass[age][sex][i][dNumber] = inputData
								.getClusterData()[age][sex][c].getRelRiskCat()[i][d];
				else
					relRiskClass[age][sex][0][dNumber] = 1;
				if (inputData.getRiskType() == 2) {

					relRiskClass[age][sex][0][dNumber] = 1;
				}
				if (inputData.getRiskType() == 2)
					relRiskContinue[age][sex][dNumber] = inputData
							.getClusterData()[age][sex][c].getRelRiskCont()[d];

				// can be changed into variable name of duration
				if (inputData.getRiskType() == 1
						|| inputData.getRiskType() == 3)

					relRiskContinue[age][sex][dNumber] = 1;

			}

		}

		// if not netto transition rates then: transitionMatrix=inputData....

		// make declarations for the other fields
		baselineIncidence[age][sex] = new float[inputData.getNDisease()];
		baselineFatalIncidence[age][sex] = new float[inputData.getNDisease()];
		baselinePrevalenceOdds[age][sex] = new double[inputData.getNDisease()];
		relRiskOtherMort[age][sex] = new float[nRiskCat];
		if (inputData.getRiskType() == 1)
			nSim = nRiskCat;
		if (inputData.getRiskType() == 3)
			nSim = nRiskCat + inputData.getDuurFreq()[age][sex].length - 1;
		if (inputData.getRiskType() == 2 && nSim < 1000)
			nSim = 1000;
		// help variables concerning all cause mortality
		baselineMortality[age][sex] = 0; // Baseline mortality

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

		double[][] probDisease = new double[nSim][inputData.getNDisease()];
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
		if (inputData.getRiskType() == 3) {
			double checkSum = 0;
			float a;

			/*
			 * despite trying this not only changes the duurFreq, but also the
			 * original version!
			 */
			for (int k = 0; k < inputData.getDuurFreq()[age][sex].length; k++) {

				checkSum += duurFreq[age][sex][k];
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
					for (int j = 0; j < duurFreq[age][sex].length; j++) {
						weight[i] = inputData.getPrevRisk()[age][sex][k]
								* duurFreq[age][sex][j];
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
		double[][] relRisk = new double[nSim][inputData.getNDisease()];
		// relative risk for person i (first index) on the
		// disease d second index) from risk factors only
		// due to risk factors only (excluding the risks due to other diseases
		double[][] relRiskIncludingDisease = new double[nSim][inputData
				.getNDisease()];
		// same as above, but now also including the risk from independent
		// diseases
		double relRiskMort[] = new double[nSim]; // relative risk for person
		// i on all cause
		// mortality

		/* first loop over all individuals in the estimating population */
		/* this gives a first estimator for the baseline prevalence rate */
		// initialize necessary sum-variables etc.
		{
			double[] sumRR = new double[inputData.getNDisease()]; // sum
			// (index=disease)
			// over all RR's
			// due to
			// riskfactors/classes
			double sumRRm = 0; // sum over all RR's for all cause mortality due
			// to riskfactors/classes
			double relRiskMax[] = new double[inputData.getNDisease()]; // maximum
			for (int d = 0; d < inputData.getNDisease(); d++) {
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
					if (RiskTypeDistribution == "Normal") {
						riskfactor[i] = meanRisk[age][sex]
								+ stdDevRisk[age][sex]
								* DynamoLib.normInv((i + 0.5) / nSim);
					} else if (RiskTypeDistribution == "LogNormal") {
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
									+ duurFreq[age][sex].length) {
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
						riskclass[i] = i - duurFreq[age][sex].length + 1;
					}
				}

				// Calculate relative risks based on only the riskfactor

				// loop over all clusters of diseases

				for (int d = 0; d < inputData.getNDisease(); d++) {

					if (inputData.getRiskType() == 3) {
						if (riskclass[i] == inputData.getIndexDuurClass()) {

							relRisk[i][d] = (relRiskDuurBegin[age][sex][d] - relRiskDuurEnd[age][sex][d])
									* Math.exp(-riskfactor[i]
											* alfaDuur[age][sex][d])
									+ relRiskDuurEnd[age][sex][d];

							relRiskMort[i] = (inputData
									.getRelRiskDuurMortBegin()[age][sex] - inputData
									.getRelRiskDuurMortEnd()[age][sex])
									* Math
											.exp(-riskfactor[i]
													* inputData
															.getRrAlphaMort()[age][sex])
									+ inputData.getRelRiskDuurMortEnd()[age][sex];

						} else {
							relRisk[i][d] = relRiskClass[age][sex][riskclass[i]][d];

							relRiskMort[i] = inputData.getRelRiskMortCat()[age][sex][riskclass[i]];
						}
					} else

						relRisk[i][d] = Math.pow(relRiskContinue[age][sex][d],
								(riskfactor[i] - inputData.getRefClassCont()))
								* relRiskClass[age][sex][riskclass[i]][d];

					sumRR[d] += relRisk[i][d] * weight[i];
					if (relRiskMax[d] < relRisk[i][d])
						relRiskMax[d] = relRisk[i][d];

				}
				// calculate RR and sum of RR for mortality;
				if (inputData.getRiskType() == 3) {
					if (riskclass[i] == inputData.getIndexDuurClass()) {

						relRiskMort[i] = (inputData.getRelRiskDuurMortBegin()[age][sex] - inputData
								.getRelRiskDuurMortEnd()[age][sex])
								* Math.exp(-riskfactor[i]
										* inputData.getAlphaMort()[age][sex])
								+ inputData.getRelRiskDuurMortEnd()[age][sex];

					} else {

						relRiskMort[i] = inputData.getRelRiskMortCat()[age][sex][riskclass[i]];
					}
				} else
					relRiskMort[i] = inputData.getRelRiskMortCat()[age][sex][riskclass[i]]
							* Math.pow(
									inputData.getRelRiskMortCont()[age][sex],
									(riskfactor[i] - inputData
											.getRefClassCont()));
				sumRRm += relRiskMort[i] * weight[i]; // sum of relRiskMort
				// over all
				// persons

			} // end first loop over all individuals

			// calculate a first estimate of baseline prevalence for the
			// independent
			// diseases and baseline all cause mortality
			// the false indicates that this should be done for independent
			// diseases

			;

			calculateBaselinePrev(inputData, age, sex, sumRR, false);
			calculateBaselineFatalIncidence(inputData, age, sex, sumRR, false);
			baselineMortality[age][sex] = (float) (inputData.getMortTot()[age][sex] / sumRRm);
		}

		/*
		 * now repeat loop 1 iteratively to estimate the baseline odds // loop
		 * over all diseases with the exception of cases where the prevalence ==
		 * 0; there the baseline odds stays 0
		 */
		for (int d = 0; d < inputData.getNDisease(); d++) {
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
							* baselinePrevalenceOdds[age][sex][d]
							/ (1 + relRisk[i][d]
									* baselinePrevalenceOdds[age][sex][d]);
					sumDerivativePrevCurrent += weight[i]
							* relRisk[i][d]
							/ Math.pow((1 + relRisk[i][d]
									* baselinePrevalenceOdds[age][sex][d]), 2);
				}// end loop over all individuals
				double oldValue = baselinePrevalenceOdds[age][sex][d];
				baselinePrevalenceOdds[age][sex][d] = oldValue
						- (sumPrevCurrent - diseasePrevalence[age][sex][d])
						/ sumDerivativePrevCurrent;
				del = Math.abs(baselinePrevalenceOdds[age][sex][d] - oldValue);
				++nIter;
			}// end iterative procedure for disease
		} // end loop over diseases

		// //////////////////////////////////////////////einde first loop
		// /////////////////////////////
		if (age == 0 && sex == 0)
			log.debug("end loop 1");
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
		{
			// initialize necessary sum-variables etc.

			double[] sumRR = new double[inputData.getNDisease()]; // sum
			// (index=disease)
			// over all RR's
			// due to
			// riskfactors/classes
			double[] sumRRinHealth = new double[inputData.getNDisease()]; // sum
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
						int d = inputData.clusterStructure[c]
								.getDiseaseNumber()[dc];
						if (!inputData.clusterStructure[c]
								.getDependentDisease()[dc]) {
							// probability = baseline prevalence * RR
							probDisease[i][d] = baselinePrevalenceOdds[age][sex][d]
									* relRisk[i][d]
									/ (baselinePrevalenceOdds[age][sex][d]
											* relRisk[i][d] + 1);

							sumRRinHealth[d] += weight[i]
									* (1 - probDisease[i][d]) * relRisk[i][d];

						}
					}

					int NdepInCluster = inputData.clusterStructure[c].getNDep();
					int NIndepInCluster = inputData.clusterStructure[c]
							.getNIndep();
					int NInCluster = inputData.clusterStructure[c]
							.getNInCluster();

					// now calculate the sum of RR for each dependent disease
					// loop over clusters and dependent diseases;

					if (inputData.clusterStructure[c].getNInCluster() > 1)
						for (int dd = 0; dd < NInCluster; dd++) {
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
				log.debug("end loop 2");
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
				int NdepInCluster = inputData.clusterStructure[c].getNDep();
				int NIndepInCluster = inputData.clusterStructure[c].getNIndep();
				int NInCluster = inputData.clusterStructure[c].getNInCluster();

				// loop over dependent diseases
				for (int dd = 0; dd < NInCluster; dd++) {
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
										* baselinePrevalenceOdds[age][sex][Ndd]
										/ (1 + RR
												* baselinePrevalenceOdds[age][sex][Ndd]);
								sumDerivativePrevCurrent += weight[i]
										* probCombi
										* RR
										/ Math
												.pow(
														(1 + RR
																* baselinePrevalenceOdds[age][sex][Ndd]),
														2);
							}
						}

						// end loop over all individuals
						double oldValue = baselinePrevalenceOdds[age][sex][Ndd];
						baselinePrevalenceOdds[age][sex][Ndd] = oldValue
								- (sumPrevCurrent - diseasePrevalence[age][sex][Ndd])
								/ sumDerivativePrevCurrent;
						del = Math.abs(baselinePrevalenceOdds[age][sex][Ndd]
								- oldValue);
						++nIter;
					}// end iterative procedure for disease
				} // end loop over diseases

			}// end loop over clusters
			if (age == 0 && sex == 0)
				log.debug("end loop 3");
			/**
			 * <br>
			 * Phase 3.<br>
			 * In the third stage of parameter estimation, the probabilities on
			 * dependent diseases are calculated from the baseline prevalences
			 * as calculated in phase 2. Also the comorbidities are calculated
			 * from the same baseline prevalences
			 * 
			 * The aim of this stage is: <br>
			 * 1) estimation of the baseline incidence of the dependent diseases
			 * and <br>
			 * 2) estimation of the OR's and baseline disability odds<br>
			 * 3) the estimation of the attributable mortality: to be solved
			 * with a matrix equation each row of the equation is for 1 disease.
			 * take disease d as the row disease, and d1 ... dn as other
			 * diseases. See the description of calculation document for a
			 * description of the calculations. <br>
			 * In short:
			 * 
			 * <br>
			 * left hand side of the equation: <br>
			 * when other mortality depends on the riskfactor: <br>
			 * mtot + (1-p(d))*E(d) - average(mtot(r)|d) + terms for case
			 * fatality <br>
			 * when other mortality does not depend on the riskfactor: <br>
			 * (1-p(d))*E(d) + terms for case fatality <br>
			 * <br>
			 * Terms for case fatality: <br>
			 * this is only relevant when case fatatity applies to a dependent
			 * diseases. For the dependent diseases itself the following terms
			 * are added to the lefthand side (one for each dependent disease,
			 * including the disease itself): <br>
			 * - average (over all R)
			 * of[fatalbaselineinc-depNO(r)(RRdisNO-1){(P(intermed and dep given
			 * R)-p(intermediate given R)*P(dependent given R)}]/P(dep) <br>
			 * depNO stands for each dependent disease, <br>
			 * dep is the disease of the equation. <br>
			 * <br>
			 * 
			 * For intermediate diseases the following terms are added to the
			 * lefthand side (with minus sign): <br>
			 * - Average(over all R) of
			 * [fatalbaselineinc_dep(R)(RRdis-1)(1-P(intermed given
			 * R))P(intermed given R)]/P(intermed) <br>
			 * <br>
			 * 
			 * 
			 * The last term gives the mortality of a population in which the
			 * distribution of risk factors is equal to that with disease d. To
			 * calculate this, we reason that each person i in our estimation
			 * population has a probability probdisease(d) to have the disease.
			 * So to obtain the distribution of riskfactors as in disease d, we
			 * will have to weight each person with weight probdisease(d). So
			 * this can be found by taking probdisease(d,i)*probmort(i) and
			 * average over the population. probmort(i) is here RRm(i)*baseline
			 * mortality. Thus we take the sum over RRm(i)*baseline mortality*
			 * probdisease(d,i), divided by the sum over probdisease(d,i) (=sum
			 * of weights). <br>
			 * <br>
			 * The right hand side of the equation: this side has a term for
			 * each disease, and thus is a matrix with the following entries for
			 * row d: For disease d itself it is: <br>
			 * 1-average(probdisease(d,i)^2)/p(d) For disease d1 it is:
			 * average(probdisease (d
			 * &d1,i))/p(d)-average(probdisease(d,i)*probdisease(d1,i))/p(d). <br>
			 * <br>
			 * This term is zero when two diseases are independent. Also the
			 * term for d alone can be calculated with this formula <br>
			 */

			// initialize help variables for calculation of attributable
			// mortality
			{

				double[][] vMat = new double[inputData.getNDisease()][inputData
						.getNDisease()];
				// Vmat is the matrix containing the right hand side terms. Both
				// indexes are disease numbers

				double[] expectedMortality = new double[inputData.getNDisease()];
				// expected mortality contains the expected mortality for those
				// with
				// a risk
				// factor distribution equal to that of a group of persons with
				// disease d. Index: d
				double[] expectedCF = new double[inputData.getNDisease()];
				double[] sumForCF = new double[inputData.getNDisease()];
				Arrays.fill(sumForCF, 0);
				// variable holding the expected mortality from case fatality of
				// dependent diseases
				// index is the disease of the disease line
				double[] sumRRmDisease = new double[inputData.getNDisease()];
				Arrays.fill(sumRRmDisease, 0);
				// sum over relRiskMort weighted with the probability of having
				// disease d
				// index = d

				// third loop over all persons
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
								baselinePrevalenceOdds[age][sex]);
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
								probDisease[i][d] = probComorbidity[i][c]
										.getProb()[ndd][ndd];
								// d is the number of this disease in the whole
								// set of diseases (also outside the cluster)

								/*
								 * 
								 * 
								 * 
								 * Make terms needed to calculate baseline
								 * incidence for dependent diseases RR= sum of
								 * prob(1-p|combi of independent diseases)
								 * RR(combi)
								 * 
								 * if the baseline prevalence odds ==0 then
								 * probability should be 0
								 */
								for (int combi = 0; combi < Math.pow(2,
										inputData.clusterStructure[c]
												.getNIndep()); combi++) {
									// double logitDiseasedInCombi=-9999999;

									// if (baselinePrevalenceOdds[age][sex][d]
									// !=0)
									double oddsDiseasedInCombi = baselinePrevalenceOdds[age][sex][d]
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
											RRcombi *= inputData
													.getClusterData()[age][sex][c]
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
									if (baselinePrevalenceOdds[age][sex][d] != 0)
										sumRRinHealth[d] += weight[i]
												* relRisk[i][d]
												* (1 / (1 + oddsDiseasedInCombi))
												* RRcombi * probCombi;
									else
										sumRRinHealth[d] += weight[i]
												* probCombi * RRcombi;
									;
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
									 * we need separate terms if there are more
									 * than one causal diseases. to keep things
									 * "simple" we therefore calculate the total
									 * of the sums including the constants that
									 * could be added later. we need to circle
									 * through the causes, and for each cause
									 * add the terms for all the resulting
									 * dependent diseases. thus two loops: the
									 * first over the causes, the second over
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
															* baselineFatalIncidence[age][sex][d]
															* (probComorbidity[i][c]
																	.getProb()[ndd][ndi] - probComorbidity[i][c]
																	.getProb()[ndd][ndd]
																	* probComorbidity[i][c]
																			.getProb()[ndi][ndi])
															/ prev;
												else
													// TODO deze formule
													// uitwerken
													sumForCF[d] += weight[i]
															* (RRdisC - 1)
															* baselineFatalIncidence[age][sex][d]
															* ((probComorbidity[i][c]
																	.getProb()[ndd][ndi] - probComorbidity[i][c]
																	.getProb()[ndd][ndd]
																	* probComorbidity[i][c]
																			.getProb()[ndi][ndi])
																	/ prev - (probComorbidity[i][c]
																	.getProb()[ndd][ndi] - probComorbidity[i][c]
																	.getProb()[ndd][ndd]
																	* probComorbidity[i][c]
																			.getProb()[ndi][ndi])
																	/ (1 - prev));

											} else
												sumForCF[d] = 0;
											/*
											 * if prev ==0 then make sumForCF
											 * equal to zero basically you
											 * cannot calculate the attributable
											 * mortality in this case and so it
											 * is made equal to excess
											 * mortality.
											 */
										} // end loop over dependent diseases
									}
									;
								} // end loop over causes
								else {
									// here only one loop is needed, as we only
									// need
									// to check of the dependent disease
									// of this intermediate disease

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
												.getRRdisExtended()[dc][ndd];
										double prev = inputData
												.getClusterData()[age][sex][c]
												.getPrevalence()[dc];
										if (prev != 0)
											if (inputData
													.isWithRRForMortality())
												sumForCF[d] += weight[i]
														* (RRdisC - 1)
														* baselineFatalIncidence[age][sex][d]
														* (1 - probComorbidity[i][c]
																.getProb()[dc][dc])
														* probComorbidity[i][c]
																.getProb()[dc][dc]
														/ prev;
											else
												// TODO nakijken of klopt
												sumForCF[d] += weight[i]
														* (RRdisC - 1)
														* baselineFatalIncidence[age][sex][d]
														* probComorbidity[i][c]
																.getProb()[dc][dc]
														/ prev;
										;
									} // end loop over dependent diseases

								}

							}
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
						}
						;
						vMat = probComorbidity[i][c].addBlock(vMat, weight[i],
								inputData.clusterStructure[c], inputData
										.isWithRRForMortality(),
								diseasePrevalence[age][sex]);
					}

					// extract the probability of each single disease
					for (int d = 0; d < inputData.getNDisease(); d++) {

						/*
						 * this part is redundant as this was already calculated
						 * earlier probDisease[i][d +
						 * probComorbidity[i][c].dStart] =
						 * probComorbidity[i][c].prob[d][d]; // calculate the
						 * contribution to the sum of the relative // mortality
						 * given the risk factor // distribution of each disease
						 */
						sumRRmDisease[d] += probDisease[i][d] * relRiskMort[i]
								* weight[i];
						// sumExpectedCF[d]+=
					}
					// calculate matrix V=
					// average(probdisease (d1 &d2,i))/p(d1)-
					// * average(probdisease(d1,i)*probdisease(d2,i))/p(d1)

				}// end third loop over all persons i

				calculateBaselineInc(inputData, age, sex, sumRRinHealth, true);
				if (age == 0 && sex == 0)
					log.debug("end loop 3");
				// now calculate the attributable mortality

				double[] lefthand = new double[inputData.getNDisease()];
				for (int d = 0; d < inputData.getNDisease(); d++) {
					if (diseasePrevalence[age][sex][d] != 0)
						expectedMortality[d] = baselineMortality[age][sex]
								* sumRRmDisease[d]
								/ diseasePrevalence[age][sex][d];

					else
						expectedMortality[d] = inputData.getMortTot()[age][sex];

					/* mtot + (1-p(d))E(d) - average(mtot(r)|d) */
					if (inputData.isWithRRForMortality())
						lefthand[d] = inputData.getMortTot()[age][sex]
								+ (1 - diseasePrevalence[age][sex][d])
								* excessMortality[d] - expectedMortality[d]
								- sumForCF[d];
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
						log.debug("matrix is inverted");

					Matrix LH = new Matrix(lefthand, inputData.getNDisease());
					double[] temp = vInverse.times(LH).getRowPackedCopy();
					if (age == 0 && sex == 0)
						log.debug("attributable mortality calculated");
					for (int d = 0; d < inputData.getNDisease(); d++) {
						attributableMortality[age][sex][d] = (float) temp[d];
					}

					if (age == 0 && sex == 0)
						log.debug("attributable mortality written");

					for (int d = 0; d < inputData.getNDisease(); d++) {
						if (Math.abs(attributableMortality[age][sex][d]) < 1e-16)
							attributableMortality[age][sex][d] = 0;
						if (attributableMortality[age][sex][d] < 0) {
							negativeAM = true;

							/*
							 * exclude this disease from the calculations and
							 * make AM zero for this disease
							 * 
							 * This will decrease the AM of other diseases, so
							 * they might get negative; therefore this should be
							 * repeated until no negative AM's are left
							 */

							/*
							 * this is done by setting the rows and columns of
							 * vMat to zero, and the diagonal to 1
							 */
							for (int d1 = 0; d1 < inputData.getNDisease(); d1++) {
								vMat[d1][d] = 0;
								vMat[d][d1] = 0;
							}
							vMat[d][d] = 1;

						}

					}
					niter++;

				} // einde herhaling schatting van Attributable mortality
				if (niter == 10)
					log.fatal("g=negative attributable mortality estimated");
				// TODO throw exception

			}

			/*
			 * estimate disability OR and baseline OR for disability using
			 * probDisease[i][d]
			 */

			/* start with initial estimates of or-1 and baseline = overall */
			/*
			 * double [] RRdisability = new double [nRiskCat];
			 * baselineDisability[age][sex] =
			 * inputData.getOverallDalyWeight()[age][sex];
			 * Arrays.fill(RRdisability,1); for (int i = 0; i < nSim; i++) { for
			 * (int d=0;d<inputData.getNDisease();d++)
			 * 
			 * probDisease[i][d] } //TODO disability rr berekenen
			 */

			;
			/**
			 * <br>
			 * Phase 4 <br>
			 * In the fourth stage of parameter estimation, the estimated
			 * attributable mortality is used to calculate the other cause
			 * mortality per simulated person i Then a regression is done of
			 * this other cause mortality on the risk factors yielding relative
			 * risks for other cause mortality <br>
			 */

			if (age == 0 && sex == 0)
				log.debug("begin loop 4");
			double sumOtherMort = 0;

			double[] beta;
			double[] RRothermort;

			double otherMort[] = new double[nSim];
			double logOtherMort[] = new double[nSim];
			double nNegativeOtherMort = 0;

			// make design matrix for regression (including dummy variables
			// for
			// each risk class)

			double[][] xMatrix = new double[nSim][2];

			if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
				xMatrix = new double[nSim][nRiskCat];
			// fourth loop over all persons i: fill the design matrix

			for (int i = 0; i < nSim; i++) {

				// add intercept
				xMatrix[i][0] = 1.0;
				// add dummies except for the first class = reference
				// category
				if (inputData.getRiskType() == 1
						|| inputData.getRiskType() == 3) {
					for (int rc = 1; rc < nRiskCat; rc++) {
						if (riskclass[i] == rc)
							xMatrix[i][rc] = 1.0;
						else
							xMatrix[i][rc] = 0.0;
					}
				}
				;
				// add continuous risk factor only for type=2
				// for type=3 the compound part is dealt with separately
				// for type=3 we here calculate the categorical part taking the
				// duration part
				// as a separate entity (taken together)
				if (inputData.getRiskType() == 2) {
					xMatrix[i][xMatrix[i].length - 1] = riskfactor[i]
							- inputData.getRefClassCont();
				}
				otherMort[i] = relRiskMort[i] * baselineMortality[age][sex];
				for (int d = 0; d < inputData.getNDisease(); d++) {
					otherMort[i] -= attributableMortality[age][sex][d]
							* probDisease[i][d];
				}
				;
				sumOtherMort += weight[i] * otherMort[i];
				if (otherMort[i] > 0)
					logOtherMort[i] = Math.log(otherMort[i]);
				else {
					log.warn("negative other mortality  = " + otherMort[i]
							+ " for person  " + i + " for riskclass "
							+ riskclass[i] + " and for riskfactor "
							+ riskfactor[i]);
					logOtherMort[i] = -999999;
					nNegativeOtherMort += weight[i];
				}
			}

			// end of fourth loop over all persons i
			if (age == 0 && sex == 0)
				log.debug("end loop 4");
			if (nNegativeOtherMort > 0.1) {
				// TODO warnings for other mortality lower then 0 in window for
				// user
				log.fatal("negative other mortality  in  "
						+ (nNegativeOtherMort * 100) + " % of simulated cases");
			}
			if (nNegativeOtherMort > 0.4)
				throw new DynamoInconsistentDataException(
						"Other mortality becomes negative in"
								+ " more than 40% ( "
								+ (nNegativeOtherMort * 100)
								+ " %) of cases. The amount of disease specific mortality given to the model"
								+ " exceeds the overall mortality give to the model.  Please lower excess mortality rates or"
								+ " disease prevalence rates, or increase total mortality rates");
			// carry out the regression of log other mortality on the risk
			// factors;
			try {
				beta = weightedRegression(logOtherMort, xMatrix, weight);
			} catch (Exception e) {

				e.printStackTrace();
				log
						.fatal("runtime error while estimating model parameters. e.getMessage()"
								+ " for age is " + age + "and sex is " + sex);
				throw new RuntimeException(e.getMessage());
			}
			if (age == 0 && sex == 0)
				log.debug(" beta 0 and 1 :" + beta[0] + beta[1]);
			// calculate relative risks from the regression coefficients

			// first class has relative risk of 1
			relRiskOtherMort[age][sex][0] = 1;
			if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
				for (int j = 1; j < beta.length; j++)
				// calculate the relative risk relative to the first
				// risk
				// class
				// //
				{
					relRiskOtherMort[age][sex][j] = (float) Math.exp(beta[j]);
					// in case of duration class set rr to 1;
					if (inputData.getRiskType() == 3
							&& inputData.getIndexDuurClass() == j)
						relRiskOtherMort[age][sex][j] = 1;
				}

			// last beta is the coefficient for the continuous risk factor
			// //
			relRiskOtherMortCont[age][sex] = (float) Math
					.exp(beta[beta.length - 1]);
			if (inputData.getRiskType() == 1 || inputData.getRiskType() == 3)
				relRiskOtherMortCont[age][sex] = 1;

			baselineOtherMortality[age][sex] = (float) Math.exp(beta[0]);
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
						beginRR = relRiskOtherMort[age][sex][rc];
					if (inputData.getRelRiskDuurMortEnd()[age][sex] == inputData
							.getRelRiskMortCat()[age][sex][rc]
							&& rc != inputData.getIndexDuurClass())
						endRR = relRiskOtherMort[age][sex][rc];
				}
				// select only the data for the duration class;
				double ydata[] = new double[duurFreq[age][sex].length];
				double xdata[] = new double[duurFreq[age][sex].length];
				double weightdata[] = new double[duurFreq[age][sex].length];
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
							baselineOtherMortality[age][sex]);
				} catch (Exception e) {
					log.fatal(e.getMessage());
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());

				}
				relRiskOtherMortBegin[age][sex] = (float) beta[0];
				relRiskOtherMortEnd[age][sex] = (float) beta[1];
				alfaOtherMort[age][sex] = (float) beta[2];

			}

			{
			}
			if (age == 0 && sex == 0)
				log.debug("begin loop 5");
			// fifth loop over all persons i to calculate sum of RR other
			// mortality to check baselineOtherMortality
			// only temporary to check method
			double baselineOtherMortality2;
			double sumRROtherMort = 0;
			for (int i = 0; i < nSim; i++) {
				if (inputData.getRiskType() == 3) {
					if (riskclass[i] == inputData.getIndexDuurClass()) {

						sumRROtherMort += weight[i]
								* ((relRiskOtherMortBegin[age][sex] - relRiskOtherMortEnd[age][sex])
										* Math.exp(-alfaOtherMort[age][sex]
												* riskfactor[i]) + relRiskOtherMortEnd[age][sex]);

					} else {

						sumRROtherMort += weight[i]
								* relRiskOtherMort[age][sex][riskclass[i]];
					}
				} else
					sumRROtherMort += weight[i]
							* relRiskOtherMort[age][sex][riskclass[i]]
							* Math
									.pow(relRiskOtherMortCont[age][sex],
											riskfactor[i]
													- inputData
															.getRefClassCont());
				/*
				 * double pred=
				 * baselineOtherMortalityrelRiskOtherMort[riskclass[i]]
				 * Math.pow(relRiskOtherMortCont,(riskfactor[i] -
				 * inputData.refClassCont)); double
				 * logpred=beta[0]+beta[1]xMatrix[i][1]+beta[2]xMatrix[i][2]
				 * +beta[3]xMatrix[i][3]; System.out .println("predicted " +
				 * pred + " from " + otherMort[i]); System.out .println("log
				 * predicted " + logpred + " from " + logOtherMort[i]);
				 */
			}
			baselineOtherMortality2 = sumOtherMort / sumRROtherMort;
			if (age == 0
					&& sex == 0
					&& baselineOtherMortality2 != baselineOtherMortality[age][sex])
				log.debug("different baseline mortalities calculated nl "
						+ baselineOtherMortality2 + " after calibration and  "
						+ baselineOtherMortality[age][sex] + " before");
			if (baselineOtherMortality2 != 0)
				if (Math.abs(baselineOtherMortality2
						- baselineOtherMortality[age][sex])
						/ baselineOtherMortality2 > 0.01)
					log
							.fatal("different baseline mortalities calculated after calibration nl "
									+ baselineOtherMortality2
									+ " after calibration while  "
									+ baselineOtherMortality[age][sex]
									+ " before.");
			baselineOtherMortality[age][sex] = (float) baselineOtherMortality2;

			if (age == 0 && sex == 0)
				log.debug("end loop 5");
		}

	}

	/**
	 * @param inputData
	 * @return number of diseases (int)
	 */
	public int getNDiseases(InputData inputData) {
		int nDiseases = 0;
		for (int c = 0; c < nCluster; c++) {
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
		for (int c = 0; c < nCluster; c++) {
			nDiseases += clusterStructure[c].getNInCluster();
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
					baselineIncidence[age][sex][d] = (float) (InputData
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

					baselinePrevalenceOdds[age][sex][d] = InputData
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

					baselineFatalIncidence[age][sex][d] = (float) (InputData
							.getClusterData()[age][sex][c].getIncidence()[dc]
							* (InputData.getClusterData()[age][sex][c]
									.getCaseFatality()[dc]) / meanRR[d]);

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
				log.debug(" lambda " + lambda + " halvingsteps = " + iter2);
			}

			log
					.debug(" non-linear regression other cause mortality: iteration "
							+ iter + " criterium = " + Criterium);
			log.debug(" lambda " + lambda + " halvingsteps = " + iter2);
			log.debug("alfa " + currentAlfa + " RR end " + currentRRend
					+ " RR begin " + currentRRbegin);
			if (lambda > 1)
				lambda = lambda / 2;
			if (Math.abs(old1 - currentAlfa) / old1 < 0.001
					&& Math.abs(old2 - currentRRend) / old2 < 0.001
					&& Math.abs(old3 - currentRRbegin) / old3 < 0.001)
				break;
			else if (iter == 499)
				log
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
			notCuredPrev[a] = ((notCuredPrev[a - 1] * excessmort[a - 1] - (1 - curedFraction[a - 1])
					* inc[a - 1])
					* expDifferenceIncExcessMort + (1 - curedFraction[a - 1])
					* inc[a - 1] * (1 - notCuredPrev[a - 1]))
					/ ((notCuredPrev[a - 1] * excessmort[a - 1] - (1 - curedFraction[a - 1])
							* inc[a - 1])
							* expDifferenceIncExcessMort + excessmort[a - 1]
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

	private float[][] deepcopy(float[][] inarray) {
		float[][] returnarray = new float[inarray.length][inarray[0].length];
		for (int i = 0; i < inarray.length; i++)
			System.arraycopy(inarray[i], 0, returnarray[i], 0,
					inarray[0].length);
		return returnarray;

	}

	private float[][][] deepcopy(float[][][] inarray) {
		float[][][] returnarray = new float[inarray.length][inarray[0].length][inarray[0][0].length];
		for (int i = 0; i < inarray.length; i++)
			for (int j = 0; j < inarray[0].length; j++)
				System.arraycopy(inarray[i][j], 0, returnarray[i][j], 0,
						inarray[0][0].length);
		return returnarray;

	}

	private double[][][] deepcopy(double[][][] inarray) {
		double[][][] returnarray = new double[inarray.length][inarray[0].length][inarray[0][0].length];
		for (int i = 0; i < inarray.length; i++)
			for (int j = 0; j < inarray[0].length; j++)
				System.arraycopy(inarray[i][j], 0, returnarray[i][j], 0,
						inarray[0][0].length);
		return returnarray;

	}

	private float[][][][] deepcopy(float[][][][] inarray) {
		float[][][][] returnarray = new float[inarray.length][inarray[0].length][inarray[0][0].length][inarray[0][0][0].length];
		for (int i = 0; i < inarray.length; i++)
			for (int j = 0; j < inarray[0].length; j++)
				for (int k = 0; k < inarray[0][0].length; k++)
					System.arraycopy(inarray[i][j][k], 0, returnarray[i][j][k],
							0, inarray[0][0][0].length);
		return returnarray;

	}

	private float[][][][][] deepcopy(float[][][][][] inarray) {
		float[][][][][] returnarray = new float[inarray.length][inarray[0].length][inarray[0][0].length][inarray[0][0][0].length][inarray[0][0][0][0].length];
		for (int i = 0; i < inarray.length; i++)
			for (int j = 0; j < inarray[0].length; j++)
				for (int k = 0; k < inarray[0][0].length; k++)
					for (int l = 0; l < inarray[0][0][0].length; l++)
						System.arraycopy(inarray[i][j][k][l], 0,
								returnarray[i][j][k][l], 0,
								inarray[0][0][0][0].length);
		return returnarray;

	}

	public int getRiskType() {
		return riskType;
	}

	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	public String getRiskTypeDistribution() {
		return RiskTypeDistribution;
	}

	public void setRiskTypeDistribution(String riskTypeDistribution) {
		RiskTypeDistribution = riskTypeDistribution;
	}

	public int getDurationClass() {
		return durationClass;
	}

	public void setDurationClass(int durationClass) {
		this.durationClass = durationClass;
	}

	public float getRefClassCont() {
		return refClassCont;
	}

	public void setRefClassCont(float refClassCont) {
		this.refClassCont = refClassCont;
	}

	public int getNCluster() {
		return nCluster;
	}

	public void setNCluster(int cluster) {
		nCluster = cluster;
	}

	public DiseaseClusterStructure[] getClusterStructure() {
		return clusterStructure;
	}

	public void setClusterStructure(DiseaseClusterStructure[] clusterStructure) {
		this.clusterStructure = clusterStructure;
	}

	public float[][][][][] getRelRiskDiseaseOnDisease() {
		return deepcopy(relRiskDiseaseOnDisease);
	}

	public void setRelRiskDiseaseOnDisease(
			float[][][][][] relRiskDiseaseOnDisease) {
		this.relRiskDiseaseOnDisease = relRiskDiseaseOnDisease;
	}

	public float[][][] getBaselineIncidence() {
		return deepcopy(baselineIncidence);
	}

	public void setBaselineIncidence(float[][][] baselineIncidence) {
		this.baselineIncidence = baselineIncidence;
	}

	public double[][][] getBaselinePrevalenceOdds() {
		return deepcopy(baselinePrevalenceOdds);
	}

	public void setBaselinePrevalenceOdds(double[][][] baselinePrevalenceOdds) {
		this.baselinePrevalenceOdds = baselinePrevalenceOdds;
	}

	public float[][][] getRelRiskOtherMort() {
		return deepcopy(relRiskOtherMort);
	}

	public void setRelRiskOtherMort(float[][][] relRiskOtherMort) {
		this.relRiskOtherMort = relRiskOtherMort;
	}

	public float[][] getRelRiskOtherMortCont() {
		return deepcopy(relRiskOtherMortCont);
	}

	public void setRelRiskOtherMortCont(float[][] relRiskOtherMortCont) {
		this.relRiskOtherMortCont = relRiskOtherMortCont;
	}

	public float[][] getRelRiskOtherMortEnd() {
		return deepcopy(relRiskOtherMortEnd);
	}

	public void setRelRiskOtherMortEnd(float[][] relRiskOtherMortEnd) {
		this.relRiskOtherMortEnd = relRiskOtherMortEnd;
	}

	public float[][] getRelRiskOtherMortBegin() {
		return deepcopy(relRiskOtherMortBegin);
	}

	public void setRelRiskOtherMortBegin(float[][] relRiskOtherMortBegin) {
		this.relRiskOtherMortBegin = relRiskOtherMortBegin;
	}

	public float[][] getAlfaOtherMort() {
		return deepcopy(alfaOtherMort);
	}

	public void setAlfaOtherMort(float[][] alfaOtherMort) {
		this.alfaOtherMort = alfaOtherMort;
	}

	public float[][] getBaselineOtherMortality() {
		return deepcopy(baselineOtherMortality);
	}

	public void setBaselineOtherMortality(float[][] baselineOtherMortality) {
		this.baselineOtherMortality = baselineOtherMortality;
	}

	public float[][][] getAttributableMortality() {
		return deepcopy(attributableMortality);
	}

	public void setAttributableMortality(float[][][] attributableMortality) {
		this.attributableMortality = attributableMortality;
	}

	public float[][][][] getRelRiskClass() {
		return deepcopy(relRiskClass);
	}

	public void setRelRiskClass(float[][][][] relRiskClass) {
		this.relRiskClass = relRiskClass;
	}

	public float[][][] getRelRiskContinue() {
		return deepcopy(relRiskContinue);
	}

	public void setRelRiskContinue(float[][][] relRiskContinue) {
		this.relRiskContinue = relRiskContinue;
	}

	public float[][][] getPrevRisk() {
		return deepcopy(prevRisk);
	}

	public void setPrevRisk(float[][][] prevRisk) {
		this.prevRisk = prevRisk;
	}

	public float[][] getMeanRisk() {
		return deepcopy(meanRisk);
	}

	public void setMeanRisk(float[][] meanRisk) {
		this.meanRisk = meanRisk;
	}

	public float[][] getStdDevRisk() {
		return deepcopy(stdDevRisk);
	}

	public void setStdDevRisk(float[][] stdDevRisk) {
		this.stdDevRisk = stdDevRisk;
	}

	public float[][] getOffsetRisk() {
		return deepcopy(offsetRisk);
	}

	public void setOffsetRisk(float[][] input) {
		this.offsetRisk = input;
	}

	public float[][][] getRelRiskDuurBegin() {
		return deepcopy(relRiskDuurBegin);
	}

	public void setRelRiskDuurBegin(float[][][] relRiskDuurBegin) {
		this.relRiskDuurBegin = relRiskDuurBegin;
	}

	public float[][][] getRelRiskDuurEnd() {
		return deepcopy(relRiskDuurEnd);
	}

	public void setRelRiskDuurEnd(float[][][] relRiskDuurEnd) {
		this.relRiskDuurEnd = relRiskDuurEnd;
	}

	public float[][][] getAlfaDuur() {
		return deepcopy(alfaDuur);
	}

	public void setAlfaDuur(float[][][] alfaDuur) {
		this.alfaDuur = alfaDuur;
	}

	public float[][][] getDuurFreq() {
		return deepcopy(duurFreq);
	}

	public void setDuurFreq(float[][][] duurFreq) {
		this.duurFreq = duurFreq;
	}

	public float[][] getMeanDrift() {
		return deepcopy(meanDrift);
	}

	public void setMeanDrift(float[][] meanDrift) {
		this.meanDrift = meanDrift;
	}

	public float[][][][] getTransitionMatrix() {
		return deepcopy(transitionMatrix);
	}

	public void setTransitionMatrix(float[][][][] transitionMatrix) {
		this.transitionMatrix = transitionMatrix;
	}

	public float[][][] getCuredFraction() {
		return deepcopy(curedFraction);
	}

	public void setCuredFraction(float[][][] curedFraction) {
		this.curedFraction = curedFraction;
	}

	public float[][][] getBaselineFatalIncidence() {
		return deepcopy(baselineFatalIncidence);
	}

	public void setBaselineFatalIncidence(float[][][] baselineFatalIncidence) {
		this.baselineFatalIncidence = baselineFatalIncidence;
	}

	public boolean isZeroTransition() {
		return zeroTransition;
	}

	public void setZeroTransition(boolean zeroTransition) {
		this.zeroTransition = zeroTransition;
	}

	public float[][] getStdDrift() {
		return stdDrift;
	}

	public void setStdDrift(float[][] stdDrift) {
		this.stdDrift = stdDrift;
	}

	public float[][] getOffsetDrift() {
		return offsetDrift;
	}

	public void setOffsetDrift(float[][] offsetDrift) {
		this.offsetDrift = offsetDrift;
	}

	public Population[] getInitialPopulation() {
		return initialPopulation;
	}

	public void setInitialPopulation(Population[] initialPopulation) {
		this.initialPopulation = initialPopulation;
	}

}
