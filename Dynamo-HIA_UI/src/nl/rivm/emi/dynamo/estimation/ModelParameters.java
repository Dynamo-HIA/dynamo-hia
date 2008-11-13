package nl.rivm.emi.dynamo.estimation;

import java.util.Arrays;
import java.util.Random;

import Jama.Matrix;
import nl.rivm.emi.dynamo.datahandling.BaseDirectory;
import nl.rivm.emi.dynamo.datahandling.DynamoConfigurationData;
import nl.rivm.emi.dynamo.datahandling.InputDataFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import javax.management.RuntimeErrorException;

/**
 * @author boshuizh
 * ModelParameters estimates and holds the model parameters
 * method estimateModelParameters(String dir) is called when activating the button "estimate parameters"
 * of the DYNAMO model
 * (or "run" when no parameters have been estimated previously)  
 * it 
 * 1. takes the information in the directory indicated by dir to collect all the input information needed
 * 2. uses this to estimate the model parameters
 * 3. write xml files needed by the simulation module 
 * 4. write the initial population file
 * 5. writes a population of newborns (TODO)
 * 
 */
public class ModelParameters {
	
	// Fields containing the estimated model parameters and other info needed to run the model
	Log log = LogFactory.getLog(getClass().getName());
	public int nSim = 100;
	public int riskType = -1;
	public String RiskTypeDistribution = null;// TODO
	public int durationClass = -1;
	public float refClassCont = -1;
	public int nCluster = -1;
	public DiseaseClusterStructure[] clusterStructure;
	/* relRiskDiseaseOnDisease[][][][][] : third index = cluster nr */
	public float relRiskDiseaseOnDisease[][][][][] = new float[96][2][][][];
	public float baselineIncidence[][][] = new float[96][2][];;
	public float baselineFatalIncidence[][][] = new float[96][2][];;
	public float curedFraction[][][] = new float[96][2][];;
	public double baselinePrevalenceOdds[][][] = new double[96][2][];;
	public float relRiskOtherMort[][][] = new float[96][2][];; // relative
	/* risk for other cause mortality relRiskOtherMort
	 */
	public float relRiskOtherMortCont[][] = new float[96][2];
	public float relRiskOtherMortEnd[][] = new float[96][2];
	public float relRiskOtherMortBegin[][] = new float[96][2];
	public float alfaOtherMort[][] = new float[96][2];
	public float baselineOtherMortality[][] = new float[96][2];
	private float baselineMortality[][] = new float[96][2];
	public float[] attributableMortality[][] = new float[96][2][];
	public float[][] relRiskClass[][] = new float[96][2][][];
	// here the third index is rc {risk factor class}, and the fourth d
	// (disease);
	public float[] [][]relRiskContinue = new float[96][2][];
	public float prevRisk[][][] = new float[96][2][];;
	public float[][] meanRisk = new float[96][2];
	public float[][] stdDevRisk = new float[96][2];
	public float[][] skewnessRisk = new float[96][2];
	public float[][][] relRiskDuurBegin = new float[96][2][];
	public float[][][] relRiskDuurEnd = new float[96][2][];
	public float[][][] alfaDuur = new float[96][2][];
	public float[][][] duurFreq = new float[96][2][];
	public float[][] stdDrift = new float[96][2];// TODO
	public float[][] meanDrift = new float[96][2];// TODO
	public float[][] offsetDrift = new float[96][2];// TODO
	public float[][][][] transitionMatrix = new float[96][2][][];//TODO

	// empty Constructor
	
	
	public ModelParameters() {
	};

	/**
	 * estimateModelParameters is the main method that directs all the others
	 * 
	 * @param nSim number of simulated subjects used in the parameter estimation in case of continuous or compound risk factors
	 * @param inputData Object that holds the input data
	 * @throws DynamoInconsistentDataException which implies that the used should change the input data
	 * @throws Exception
	 */
	public void estimateModelParameters(int nSim, InputData inputData
			) throws DynamoInconsistentDataException{
		
		splitCuredDiseases(inputData)	;
		NettTransitionRates transRates=new NettTransitionRates();
		for (int a=0;a<96;a++)for (int g=0;g<2;g++) {
				
		
		estimateModelParametersForSingleAgeGroup(nSim, inputData,a,g);
		if (a>0){//TODO
			/* nog toevoegen: alleen als nettransition rates geschat moeten worden*/
		if (inputData.getRiskType()!=2 ){
			transRates.makeNettTransitionRates(getPrevRisk()[a-1][g],inputData.getPrevRisk()[a][g],baselineMortality[a-1][g],inputData.relRiskMortCat[a-1][g]);
		float []temp=getPrevRisk()[a-1][g];
		}}}
		
		
		
	};
	
	
	
	/**
	 * @param simulationName : name of the simulation (is used to find the directory of the data and configuration files
	 * and the directory to write the outputfiles too)
	 * @throws DynamoInconsistentDataException, indicating that the user should supply other data
	 * 
	 */
	public void estimateModelParameters(String simulationName
	) throws DynamoInconsistentDataException{
		
		
		/** step 1: build input data from baseline directory */
		/** estimateModelParameters first takes the information in the directory indicated by dir to
		 *  collect all the input information needed
		 *  this is put in two objects: InputData and DynamoConfigurationData
		 *  InputData contains all information needed for the parameter estimation
		 *  DynamoConfigurationData contains the information from the main input screen and is needed 
		 *  for making the simulation files
		 *  
		 */
		
		
		
		
	
        BaseDirectory B=BaseDirectory.getInstance("c:/hendriek/dynamodata");
        String BaseDir=B.getBaseDir();
		InputData inputData=new InputData();
		File simulationConfig=new File(BaseDir+simulationName);
		DynamoConfigurationData config=new DynamoConfigurationData(simulationConfig);
		InputDataFactory.addMortalityToInputData(inputData,config);
		InputDataFactory.addRiskFactorInfoToInputData(inputData,config);
		InputDataFactory.addDiseaseAndRRInfoToInputData(inputData,config);
		/** * 2. uses this to estimate the model parameters*/
        estimateModelParameters(nSim,inputData);
        /** * 3. write xml files needed by the simulation module */
        SimulationConfigurationFactory f1= new SimulationConfigurationFactory() ;
        f1.manufactureSimulationConfigurationFile(this, config);
        f1.manufactureCharacteristicsConfigurationFile(this, config);
        /** * 4. write the initial population file*/
        InitialPopulationFactory f2=new InitialPopulationFactory();
        int seed=config.getSeed;
        f2.manufactureInitialPopulation(this, simulationName, nSim, seed);
        
        /** * 5. writes a population of newborns (TODO)*/
	}
	public void splitCuredDiseases(InputData inputData) throws DynamoInconsistentDataException {
		
		int Nadded=0;
		for (int c=0;c<inputData.nCluster;c++){
			if (inputData.clusterStructure[c].isWithCuredFraction()){
				inputData.clusterStructure[c].nInCluster++;
				int number=inputData.clusterStructure[c].diseaseNumber[0];
				inputData.clusterStructure[c].diseaseNumber= new int [2];
				
				inputData.clusterStructure[c].diseaseNumber[0]=number+Nadded;
				inputData.clusterStructure[c].diseaseNumber[1]=number+Nadded+1;
				Nadded++;
				if (inputData.clusterStructure[c].nInCluster>1) throw new DynamoInconsistentDataException(
						"Error for disease "+inputData.clusterStructure[c].diseaseName.get(0)+". Cured fraction only allowed in diseases not related to other diseases");
				
				inputData.clusterStructure[c].nInCluster++;
				String name=inputData.clusterStructure[c].diseaseName.get(0);
				inputData.clusterStructure[c].diseaseName.set(0,name+"_cured");
				inputData.clusterStructure[c].diseaseName.add(name+"_notcured");
				float halfTime;
				float []incidence=new float [96];
				float[] prevalence=new float [96];
				float []RRcat = null;
				float RRcont;
				float RRduurEnd;
				float RRduurBegin;
				float halftime;
				float[] curedFraction=new float [96];
				float[] totExcess=new float [96];
				
				for (int g=0;g<2;g++){
				for (int a=0;a<96;a++){
					if (inputData.clusterData[a][g][c].caseFatality[0]>0) throw new DynamoInconsistentDataException(
							"Error for disease "+inputData.clusterStructure[c].diseaseName.get(0)+". Both Cured Fraction and Acute Fatality in same disease not allowed");
				/* copy info on prevalence, incidence and mortality to one array containing info of all ages */	
					
				totExcess[a] = inputData.clusterData[a][g][c].excessMortality[0];
				incidence[a]=inputData.clusterData[a][g][c].incidence[0];
				prevalence[a]=inputData.clusterData[a][g][c].prevalence[0];
				curedFraction[a]=inputData.clusterData[a][g][c].curedFraction[0];
	
				
				/* excess mortality goes only to non-cured disease */
				inputData.clusterData[a][g][c].excessMortality=new float [2];
				inputData.clusterData[a][g][c].excessMortality[0]=0;
				inputData.clusterData[a][g][c].excessMortality[1]=totExcess[a];
				/* incidence is split over the diseases */
				inputData.clusterData[a][g][c].incidence=new float [2];
				inputData.clusterData[a][g][c].incidence[0]=curedFraction[a]*incidence[a];
				inputData.clusterData[a][g][c].incidence[1]=(1-curedFraction[a])*incidence[a];
				
					
				/* 
				 * 
				 * copy all info on relative risks to the array with length 2 in stead of 1
				 */
				halftime=inputData.clusterData[a][g][c].halfTime[0];
				inputData.clusterData[a][g][c].halfTime   =new float [2];
				inputData.clusterData[a][g][c].halfTime[0]=halftime;
				inputData.clusterData[a][g][c].halfTime[1]=halftime;
				RRcat = new float [inputData.clusterData[a][g][c].relRiskCat.length];
				
				inputData.clusterData[a][g][c].relRiskCat =new float[RRcat.length][2];
				for (int cat=0;cat<RRcat.length;cat++){
				RRcat[cat]=inputData.clusterData[a][g][c].relRiskCat [cat][0];
				inputData.clusterData[a][g][c].relRiskCat[cat][0]=RRcat[cat];
				inputData.clusterData[a][g][c].relRiskCat[cat][1]=RRcat[cat];}
				
				RRcont=inputData.clusterData[a][g][c].relRiskCont[0] ;
				inputData.clusterData[a][g][c].relRiskCont =new float [2];
				inputData.clusterData[a][g][c].relRiskCont[0]=RRcont;
				inputData.clusterData[a][g][c].relRiskCont[1]=RRcont;
				
				RRduurBegin=inputData.clusterData[a][g][c].relRiskDuurBegin [0] ;
				inputData.clusterData[a][g][c].relRiskDuurBegin=new float [2];
				inputData.clusterData[a][g][c].relRiskDuurBegin[0]=RRduurBegin;
				inputData.clusterData[a][g][c].relRiskDuurBegin[1]=RRduurBegin;
				
				RRduurEnd=inputData.clusterData[a][g][c].relRiskDuurEnd [0] ;
				inputData.clusterData[a][g][c].relRiskDuurEnd =new float [2];
				inputData.clusterData[a][g][c].relRiskDuurEnd[0]=RRduurEnd;
				inputData.clusterData[a][g][c].relRiskDuurEnd[1]=RRduurEnd;
				
				
				
				
				}
				/*
				
				private double[] calculateNotCuredPrevFraction(double inc[], double prev[],
			double excessmort[], double[] curedFraction)
			*/
				double notCured[]=calculateNotCuredPrevalence(incidence,prevalence,
						totExcess, curedFraction);
				
				for (int a=0;a<96;a++){
					inputData.clusterData[a][g][c].prevalence =new float [2];
					inputData.clusterData[a][g][c].prevalence[0]=(float) (prevalence[a]-notCured[a]);
					inputData.clusterData[a][g][c].prevalence[1]=(float) notCured[a];}
					
					
						
					
				}
				
				
				
			}// end if has CuredFraction
			else{
				/* if diseases are split in two, the numbering should be adapted */
				for (int d=0;d<inputData.clusterStructure[c].nInCluster;d++)
			inputData.clusterStructure[c].diseaseNumber[d]=inputData.clusterStructure[c].diseaseNumber[d]+Nadded;
			}
			
			}// end loop over clusters
				
				
				
					} // end method
		
		

	/**
	 * This method estimates the input parameters for a single age and sex group.
	 * Exempt are parameters that can only be estimated from data of more than one age group (nett transition rates; cured prevalence fractions)
	 * @param nSim  number of simulated persons used to estimate the parameters in case of continuous or compound risk factor
	 * @param inputData  object with inputdata
	 * @param age 
	 * @param sex
	 * @throws DynamoInconsistentDataException
	 */
	public void estimateModelParametersForSingleAgeGroup(int nSim, InputData inputData, int age,
			int sex) throws DynamoInconsistentDataException {

		/**
		 * The parameter estimation proceeds using a simulated population that
		 * has a simulated risk factor distribution Per age and gender group at
		 * least 100 persons are generated, in order to let the estimation have
		 * a minimal accuracy. The estimation proceeds in 5 phase. In the first
		 * stage stage of parameter estimation, values of the risk factors are
		 * randomly drawn for each person in the simulated population using a
		 * random generator Based on these simulated values, for each person a
		 * relative risk is calculated for each disease. Baseline prevalence
		 * rates for the independent diseases then can be calculated from this
		 * by calculating the average relative risk and dividing the incidence
		 * or prevalence of this disease by the average relative risks
		 */

		// first initialize the fields that can be directly copied from the
		// input data
		// make rr=1 for the continuous variable if the risk factor is
		// categorical
		// make rr=1 for the class variable if the risk factor is continuous
		/* first copy directly */
		riskType = inputData.getRiskType();
		refClassCont = inputData.refClassCont;
		prevRisk = inputData.getPrevRisk();
		meanRisk = inputData.meanRisk;
		stdDevRisk = inputData.stdDevRisk;
		skewnessRisk = inputData.skewnessRisk;
		duurFreq = inputData.duurFreq;
		nCluster = inputData.nCluster;
		clusterStructure = inputData.clusterStructure;
		durationClass = inputData.indexDuurClass;
		
		int nRiskCat=inputData.getPrevRisk()[age][sex].length;
		int nDiseases = getNDiseases(inputData);
		/* now from DiseaseClusterData */
		relRiskDuurBegin[age][sex] = new float[nDiseases];
		attributableMortality[age][sex] = new float[nDiseases];
		relRiskContinue[age][sex] = new float[nDiseases];
		relRiskClass[age][sex] = new float[nRiskCat][nDiseases];
		relRiskDuurEnd[age][sex]= new float [nDiseases];
		alfaDuur[age][sex] = new float[nDiseases];
		float relRiskMortCont ;
		double log2 = Math.log(2.0); // keep outside loops to prevent
		// recalculation
		/* put prevalence also in a single array for easy access */
		float[][][] diseasePrevalence = new float[96][2][nDiseases];
		relRiskDiseaseOnDisease[age][sex] = new float[nCluster][][];
		float[] relRiskMortCat;
		float[] excessMortality= new float [nDiseases];
		for (int c=0;c<nCluster;c++)
		for (int dc=0;dc<inputData.clusterStructure[c].nInCluster;dc++)
		{
			int dNumber=inputData.clusterStructure[c].diseaseNumber[dc];
		
			 excessMortality[dNumber]=inputData.clusterData[age][sex][c].excessMortality[dc];
			 }
		if (inputData.riskType == 1 || inputData.riskType == 3)
			relRiskContinue[age][sex] = new float[inputData.nDisease];
		if (inputData.riskType == 1 || inputData.riskType == 3) 
			refClassCont = 0;
		for (int c=0;c<nCluster;c++){
			
			
			relRiskDiseaseOnDisease[age][sex][c] = inputData.clusterData[age][sex][c].RRdisExtended;
			
			for (int d=0;d<inputData.clusterStructure[c].nInCluster;d++)
			{				int dNumber=inputData.clusterStructure[c].diseaseNumber[d];
				if (riskType==3){
		    relRiskDuurEnd [age][sex][dNumber]= inputData.clusterData[age][sex][c].relRiskDuurBegin[d];
			if (inputData.clusterData[age][sex][c].halfTime[d] !=0) alfaDuur[age][sex][dNumber] = (float) (log2/inputData.clusterData[age][sex][c].halfTime[d] );
			else alfaDuur[age][sex][dNumber] =999999;}
			diseasePrevalence[age][sex][clusterStructure[c]
			             								.getDiseaseNumber()[d]] = inputData.clusterData[age][sex][c]
			             								.getPrevalence()[d];
			
			

			if (inputData.riskType == 1 || inputData.riskType == 3)
				for (int i = 0; i < nRiskCat; i++)
				relRiskClass[age][sex][i][dNumber] = inputData.clusterData[age][sex][c].relRiskCat[i][d];
			else
				relRiskClass[age][sex][0][dNumber] = 1;
			if (inputData.riskType == 2) {
				
					relRiskClass[age][sex][0][dNumber] = 1;
			}
			if (inputData.riskType == 2)
				relRiskContinue[age][sex][dNumber] = inputData.clusterData[age][sex][c].relRiskCont[d];

			
			// can be changed into variable name of duration
			if (inputData.riskType == 1 || inputData.riskType == 3)
				
					relRiskContinue[age][sex][dNumber] = 1;
			
			}
			
			}
		
		// if not netto transition rates then: transitionMatrix=inputData....
	
								
						
			
		
		
			


		// make declarations for the other fields
		baselineIncidence[age][sex] = new float[inputData.nDisease];
		baselineFatalIncidence[age][sex] = new float[inputData.nDisease];
		baselinePrevalenceOdds[age][sex] = new double[inputData.nDisease];
		relRiskOtherMort[age][sex] = new float[inputData.getPrevRisk()[age][sex].length];
		if (inputData.riskType == 1)
			nSim = inputData.getPrevRisk()[age][sex].length;
		if (inputData.riskType == 3)
			nSim = inputData.getPrevRisk()[age][sex].length
					+ inputData.duurFreq[age][sex].length - 1;
		if (inputData.getRiskType() == 2 && nSim < 1000)
			nSim = 1000;
		// help variables concerning all cause mortality
		baselineMortality[age][sex] = 0; // Baseline mortality

		// now the declation of the arrays that give data per simulated person

		// Disease info

		Morbidity[][] probComorbidity = new Morbidity[nSim][inputData.nCluster];
		// disease cluster c [second index?] for person i [first index]
		// ideas behind dependent diseases
		// there are clusters of dependent diseases
		// in each cluster there are dependent and independent diseases
		// there is a matrix of dimension n-dependent by n-independent that
		// contains the RR's

		double[][] probDisease = new double[nSim][inputData.nDisease];
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
		if (inputData.riskType == 3) {
			double checkSum = 0;

			for (int k = 0; k < inputData.duurFreq[age][sex].length; k++)

				checkSum += inputData.duurFreq[age][sex][k];
			if (checkSum != 1)
				throw new DynamoInconsistentDataException(
						"durations given for compound risk factor class do not sum to 1");
		}
		double weight[] = new double[nSim]; // weight for weighting the
		// prevalences
		if (inputData.riskType == 1) {
			for (int i = 0; i < nSim; i++)
				weight[i] = inputData.getPrevRisk()[age][sex][i];
		}
		if (inputData.riskType == 3) {
			int i = 0;
			for (int k = 0; k < inputData.getPrevRisk()[age][sex].length; k++) {
				weight[i] = inputData.getPrevRisk()[age][sex][k];
				if (inputData.indexDuurClass == k) {
					for (int j = 0; j < inputData.duurFreq[age][sex].length; j++) {
						weight[i] = inputData.getPrevRisk()[age][sex][k]
								* inputData.duurFreq[age][sex][j];
						i++;
					}
				} else
					i++;
			}
		}
		if (inputData.riskType == 2)
			for (int i = 0; i < nSim; i++)
				weight[i] = 1.0 / nSim;
		// relative risks
		double[][] relRisk = new double[nSim][inputData.nDisease];
		// relative risk for person i (first index) on the
		// disease d second index) from risk factors only
		// due to risk factors only (excluding the risks due to other diseases
		double[][] relRiskIncludingDisease = new double[nSim][inputData.nDisease];
		// same as above, but now also including the risk from independent
		// diseases
		double relRiskMort[] = new double[nSim]; // relative risk for person
		// i on all cause
		// mortality

		/* first loop over all individuals in the estimating population */
		/* this gives a first estimator for the baseline prevalence rate */
		// initialize necessary sum-variables etc.
		{
			double[] sumRR = new double[inputData.nDisease]; // sum
			// (index=disease)
			// over all RR's
			// due to
			// riskfactors/classes
			double sumRRm = 0; // sum over all RR's for all cause mortality due
			// to riskfactors/classes
			double relRiskMax[] = new double[inputData.nDisease]; // maximum
			for (int d = 0; d < inputData.nDisease; d++) {
				relRiskMax[d] = 0;
			}
			;

			// now the loop itself
			// index for cumulative probability of risk factor class
			for (int i = 0; i < nSim; i++) {
				/* first draw or initialize risk factors */

				if (inputData.riskType == 1)

				{
					riskfactor[i] = 0;
					riskclass[i] = i;
				}

				if (inputData.riskType == 2) {
					riskclass[i] = 0;
					if (inputData.riskDistribution == "Normal") {
						riskfactor[i] = inputData.meanRisk[age][sex]
								+ inputData.stdDevRisk[age][sex]
								* DynamoLib.normInv((i + 0.5) / nSim);
					} else if (inputData.riskDistribution == "LogNormal") {
						riskfactor[i] = DynamoLib.logNormInv2(
								((i + 0.5) / nSim),
								inputData.skewnessRisk[age][sex],
								inputData.meanRisk[age][sex],
								inputData.stdDevRisk[age][sex]);
					} else
						throw new DynamoInconsistentDataException(" unknown riskfactor distribution "
								+ inputData.riskDistribution);
				}
				if (inputData.riskType == 3) {
					if (i < inputData.indexDuurClass) {
						riskfactor[i] = 0;
						riskclass[i] = i;
					} else if (i >= inputData.indexDuurClass
							&& i < inputData.indexDuurClass
									+ inputData.duurFreq[age][sex].length) {
						riskfactor[i] = i - inputData.indexDuurClass;
						/**
						 * 
						 * duration class starts at 0: this is the first
						 * frequency that should be inputted
						 * 
						 */
						riskclass[i] = inputData.indexDuurClass;
					} else {
						riskfactor[i] = 0;
						riskclass[i] = i - inputData.duurFreq[age][sex].length
								+ 1;
					}
				}

				// Calculate relative risks based on only the riskfactor

				// loop over all clusters of diseases

				for (int d = 0; d < inputData.nDisease; d++) {

					if (inputData.riskType == 3) {
						if (riskclass[i] == inputData.indexDuurClass) {

							relRisk[i][d] = (relRiskDuurBegin[age][sex][d] - relRiskDuurEnd[age][sex][d])
									* Math.exp(- riskfactor[i]
											* alfaDuur[age][sex][d])
									+ relRiskDuurEnd[age][sex][d];

							relRiskMort[i] = (inputData.relRiskDuurMortBegin[age][sex] - inputData.relRiskDuurMortEnd[age][sex])
									* Math.exp(-log2 * riskfactor[i]
											/ inputData.halfTimeMort[age][sex])
									+ inputData.relRiskDuurMortEnd[age][sex];

						} else {
							relRisk[i][d] = relRiskClass[age][sex][riskclass[i]][d];
							
							relRiskMort[i] = inputData.relRiskMortCat[age][sex][riskclass[i]];
						}
					} else

						relRisk[i][d] = Math.pow(relRiskContinue[age][sex][d],
								(riskfactor[i] - inputData.refClassCont))
								* relRiskClass[age][sex][riskclass[i]][d];

					sumRR[d] += relRisk[i][d] * weight[i];
					if (relRiskMax[d] < relRisk[i][d])
						relRiskMax[d] = relRisk[i][d];

				}
				// calculate RR and sum of RR for mortality;
				if (inputData.riskType == 3) {
					if (riskclass[i] == inputData.indexDuurClass) {

						relRiskMort[i] = (inputData.relRiskDuurMortBegin[age][sex] - inputData.relRiskDuurMortEnd[age][sex])
								* Math.exp(-log2 * riskfactor[i]
										/ inputData.halfTimeMort[age][sex])
								+ inputData.relRiskDuurMortEnd[age][sex];

					} else {

						relRiskMort[i] = inputData.relRiskMortCat[age][sex][riskclass[i]];
					}
				} else
					relRiskMort[i] = inputData.relRiskMortCat[age][sex][riskclass[i]]
							* Math.pow(inputData.relRiskMortCont[age][sex],
									(riskfactor[i] - inputData.refClassCont));
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

			calculateBaselinePrev(inputData, age, sex,  sumRR, false);
			calculateBaselineFatalIncidence(inputData, age, sex,  sumRR, false);
			baselineMortality [age][sex]= (float) (inputData.mortTot[age][sex] / sumRRm);
		}

		/* now repeat loop 1 iteratively to estimate the baseline odds
		// loop over all diseases
		 * with the exception of cases where the prevalence == 0; there the baseline odds stays 0
		
		*/
		for (int d = 0; d < inputData.nDisease; d++) {
			int nIter = 0;
			double del = 100;
			if (diseasePrevalence[age][sex][d]==0) del=0;
			/* if disease prevalence == 0 do not do anything but keep baseline odds ==0 */
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
		if (age==0 && sex==0) log.debug("end loop 1");
		// second loop over persons //
		/**
		 * In the second stage of parameter estimation, the RRs are calculated
		 * for the dependent diseases using the probabilities on independent
		 * diseases calculated from the baseline prevalence rates as calculated
		 * in based in the first loop. The RRs are then used to calculate the
		 * baseline prevalence rates of the dependent diseases Also the
		 * prevalences of the independent diseases are used to calculate the
		 * mean relative risk for those persons not having the disease, and
		 * these are used to calculate the baseline incidence rates for the
		 * independent diseases
		 */
		{
			// initialize necessary sum-variables etc.

			double[] sumRR = new double[inputData.nDisease]; // sum
			// (index=disease)
			// over all RR's
			// due to
			// riskfactors/classes
			double[] sumRRinHealth = new double[inputData.nDisease]; // sum
			// (index=disease)
			// over all RR's * (1-probability of disease)
			// due to
			// riskfactors/classes
			
			for (int i = 0; i < nSim; i++) {
				// calculate the probability of each independent disease
				// loop over all clusters and within the clusters over the
				// diseases
				// //
				for (int c = 0; c < inputData.nCluster; c++) {
					for (int dc = 0; dc < inputData.clusterStructure[c].nInCluster; dc++) {
						int d = inputData.clusterStructure[c].diseaseNumber[dc];
						if (!inputData.clusterStructure[c].dependentDisease[dc]) {
							// probability = baseline prevalence * RR
							 probDisease[i][d] = baselinePrevalenceOdds[age][sex][d]
									* relRisk[i][d]
									/ (baselinePrevalenceOdds[age][sex][d]
											* relRisk[i][d] + 1);
							

							sumRRinHealth[d] += weight[i]
									* (1 - probDisease[i][d]) * relRisk[i][d];

						}
					}

					int NdepInCluster = inputData.clusterStructure[c].NDep;
					int NIndepInCluster = inputData.clusterStructure[c].NIndep;
					int NInCluster = inputData.clusterStructure[c].nInCluster;

					// now calculate the sum of RR for each dependent disease
					// loop over clusters and dependent diseases;

					if (inputData.clusterStructure[c].nInCluster > 1)
						for (int dd = 0; dd < NInCluster; dd++) {
							int Ndd = inputData.clusterStructure[c].diseaseNumber[dd];
							// Ndd is disease number belonging to dd ;

							// relRisk[i] already contains the RR due to the
							// risk
							// factors

							// now calculate RR for the dependent disease by
							// multiplying
							// it with the RR due to each independent disease
							relRiskIncludingDisease[i][Ndd] = relRisk[i][Ndd];
							for (int di = 0; di < NInCluster; di++) {
								int Ndi = inputData.clusterStructure[c].diseaseNumber[di];
								// Ndi is disease number belonging to di ;
								// RR due to independent disease= p(di)*RR(di) +
								// 1*(1-p(di)) = p(di)*(RR(di)-1)+1

								relRiskIncludingDisease[i][Ndd] *= (1 + probDisease[i][Ndi]
										* (inputData.clusterData[age][sex][c].RRdisExtended[di][dd] - 1));

							}
							sumRR[Ndd] += weight[i]
									* relRiskIncludingDisease[i][Ndd];

							
							;
						}
				}
			} // end second loop over all persons ( i )
			// calculate Baseline Prevalence and Incidence and mortality for
			// dependent diseases
			if (age==0 && sex==0) log.debug("end loop 2");
			;
			calculateBaselineInc(inputData, age, sex, sumRRinHealth, false);

			calculateBaselinePrev(inputData, age, sex,  sumRR, true);
			calculateBaselineFatalIncidence(inputData, age, sex,  sumRR, true);
			
			/* now calculate Baseline Prevalence Odds for the dependent diseases
			 using an iterative procedure;
			 now repeat loop 1 iteratively to estimate the baseline odds
			 loop over all diseases
			 * 
			 * exception: when input prevalence==0
			 */
			for (int c = 0; c < inputData.nCluster; c++) {
				int NdepInCluster = inputData.clusterStructure[c].NDep;
				int NIndepInCluster = inputData.clusterStructure[c].NIndep;
				int NInCluster = inputData.clusterStructure[c].nInCluster;

				// loop over dependent diseases
				for (int dd = 0; dd < NInCluster; dd++) {
					// need to sum over all combinations of independent diseases
					// is the cluster
					int Ndd = inputData.clusterStructure[c].diseaseNumber[dd];
					// Ndd is disease number belonging to dd ;

					int nIter = 0;
					/* if prevalence = 0 keep baseline odds=0 */
					double del = 100;
					if (diseasePrevalence[age][sex][Ndd]==0) del=0;
					
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
									int Ndi = inputData.clusterStructure[c].diseaseNumber[di];
									// see if disease=1 in the cluster (see if
									// bit =1 at right place)
									if ((combi & (1 << di)) == (1 << di)) {
										RR *= inputData.clusterData[age][sex][c].RRdisExtended[di][dd];
										probCombi *= probDisease[i][Ndi];
									} else
										probCombi *= (1 - probDisease[i][Ndi]);
								}
								// TODO checken of dit (hierboven) nog steeds
								// klopt
								// TODO uitrekenen op logaritmische schaal is
								// waarschijnlijk beter
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
			if (age==0 && sex==0) log.debug("end loop 3");
			/**
			 * In the third stage of parameter estimation, the probabilities on
			 * dependent diseases are calculated from the baseline prevalences
			 * as calculated in stage 2. Also the comorbidities are calculated
			 * from the same baseline prevalences
			 * 
			 * The aim of this stage is 1) estimation of the baseline incidence
			 * of the dependent diseases and 2) the estimation of the
			 * attributable mortality: to be solved with a matrix equation each
			 * row of the equation is for 1 disease. take disease d as the row
			 * disease, and d1 ... dn as other diseases. Then:
			 * 
			 * left hand side of the equation: mtot + (1-p(d))*E(d) -
			 * average(mtot(r)|d) + terms for case fatality
			 * 
			 * Terms for case fatality: this is only relevant when case fatatity
			 * applies to a dependent diseases For the dependent diseases itself
			 * the following terms are added to the lefthand side (one for each
			 * dependent disease, including the disease itself): - average (over
			 * all R) of[fatalbaselineinc-depNO(r)(RRdisNO-1){(P(intermed and
			 * dep given R)-p(intermediate given R)*P(dependent given
			 * R)}]/P(dep) depNO stands for each dependent disease, dep is the
			 * disease of the equation
			 * 
			 * For intermediate diseases the following terms are added to the
			 * lefthand side (with minus sign): - Average(over all R) of
			 * [fatalbaselineinc_dep(R)(RRdis-1)(1-P(intermed given
			 * R))P(intermed given R)]/P(intermed)
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
			 * of weights).
			 * 
			 * The right hand side of the equation: this side has a term for
			 * each disease, and thus is a matrix with the following entries for
			 * row d: For disease d itself it is:
			 * 1-average(probdisease(d,i)^2)/p(d) For disease d1 it is:
			 * average(probdisease (d
			 * &d1,i))/p(d)-average(probdisease(d,i)*probdisease(d1,i))/p(d).
			 * 
			 * This term is zero when two diseases are independent. Also the
			 * term for d alone can be calculated with this formula
			 * 
			 */

			// initialize help variables for calculation of attributable
			// mortality
			{

				double[][] vMat = new double[inputData.nDisease][inputData.nDisease];
				// Vmat is the matrix containing the right hand side terms. Both
				// indexes are disease numbers

				double[] expectedMortality = new double[inputData.nDisease];
				// expected mortality contains the expected mortality for those
				// with
				// a risk
				// factor distribution equal to that of a group of persons with
				// disease d. Index: d
				double[] expectedCF = new double[inputData.nDisease];
				double[] sumForCF = new double[inputData.nDisease];
				Arrays.fill(sumForCF, 0);
				// variable holding the expected mortality from case fatality of
				// dependent diseases
				// index is the disease of the disease line
				double[] sumRRmDisease = new double[inputData.nDisease];
				Arrays.fill(sumRRmDisease, 0);
				// sum over relRiskMort weighted with the probability of having
				// disease d
				// index = d

				// third loop over all persons
				for (int i = 0; i < nSim; i++) {

					for (int c = 0; c < inputData.nCluster; c++) {

						// now calculate comorbidity that contains the
						// probability
						// of
						// each combination of diseases
						// within each cluster
						
						/* make an e=index for the numbers of dependent and
						 * independent diseases
						 */
						int[] indexDep = new int[inputData.clusterStructure[c].NDep];
						int iterDep = 0;
						int iterIndep = 0;
						int[] indexIndep = new int[inputData.clusterStructure[c].NIndep];
						for (int dtemp = 0; dtemp < inputData.clusterStructure[c].nInCluster; dtemp++) {
							if (inputData.clusterStructure[c].dependentDisease[dtemp]) {
								indexDep[iterDep] = dtemp;
								iterDep++;
							} else {
								indexIndep[iterIndep] = dtemp;
								iterIndep++;
							}
						}

						probComorbidity[i][c] = new Morbidity(
								inputData.clusterData[age][sex][c],
								inputData.clusterStructure[c], relRisk[i],
								baselinePrevalenceOdds[age][sex]);
						// extract the probDisease for the dependent diseases
						if (inputData.clusterStructure[c].nInCluster > 1) {
							for (int dd = 0; dd < inputData.clusterStructure[c].NDep; dd++) {
								// make index for numbers of (in)dependent
								// diseases in cluster

								int ndd = indexDep[dd];
								// ndd is number of the dependent disease within
								// the
								// cluster
								int d = inputData.clusterStructure[c].diseaseNumber[ndd];
								probDisease[i][d] = probComorbidity[i][c].prob[ndd][ndd];

								/* now make terms needed to calculate baseline
								* incidence for dependent diseases
								* RR= sum of prob(combi of independent
								* diseases) * RR(combi)
								 * 
								 * if the baseline prevalence odds ==0 then probability should be 0
								 */
								for (int combi = 0; combi < Math.pow(2,
										inputData.clusterStructure[c].NIndep); combi++) {
									double logitDiseasedInCombi=-9999999;
									if (baselinePrevalenceOdds[age][sex][d] !=0)
										logitDiseasedInCombi=   Math
											.log(baselinePrevalenceOdds[age][sex][d])
											+ Math.log(relRisk[i][d]);
									double probCombi = 1;
									// must be: probability conditional on not
									// having disease d
									double RRcombi = 1;
									for (int di = 0; di < inputData.clusterStructure[c].NIndep; di++) {
										int ndi = indexIndep[di];
										if ((combi & (1 << di)) == (1 << di)) {
											probCombi *= probComorbidity[i][c].prob[ndi][ndi];
											logitDiseasedInCombi += Math
													.log(inputData.clusterData[age][sex][c].RRdisExtended[indexIndep[di]][indexDep[dd]]);
											RRcombi *= inputData.clusterData[age][sex][c].RRdisExtended[indexIndep[di]][indexDep[dd]];
										} else
											probCombi *= (1 - probComorbidity[i][c].prob[ndi][ndi]);
									}
									// alternatief
									// RRCombi*=inputData.diseaseData[c].RRdisExtended[ndi][ndd];
									// probability of p(not d^combi)=p(not
									// d|combi)p(combi)=(1-p(d|combi)p(combi)
									// and because combi diseases are mutually
									// independent when not conditioning on d
									// they can be calculated from
									// multiplication
									if (baselinePrevalenceOdds[age][sex][d] !=0) sumRRinHealth[d] += weight[i]
											* (1 - 1 / (1 + Math
													.exp(-logitDiseasedInCombi)))
											* probCombi * RRcombi;
									else 
										sumRRinHealth[d] += weight[i]																
																	* probCombi * RRcombi;
									;
								}
							}
							;

							for (int dc = 0; dc < inputData.clusterStructure[c].nInCluster; dc++) { 
								/* dc = disease number in cluster
								 *  first make the term in case of d is a dependent disease
								 * 
								 */
								int d = inputData.clusterStructure[c].diseaseNumber[dc]; 
								/* d is number disease in total numbering								
								 * 
								 */
								if (inputData.clusterStructure[c].dependentDisease[dc]) { 
									/* we need separate terms if there are more than one causal diseases
									*  to keep things "simple" we therefore
									*  calculate the total of the sums including
									*  the constants that could be added later
									*  we need to circle through the causes, and
									*  for each cause add the terms for all the
									*  resulting dependent diseases thus two loops: 
									*  the first over the causes, the second over the dependent diseases of
									*  these causes
									*/ 
									

									for (int di = 0; di < inputData.clusterStructure[c].NIndep; di++) // loop
									// over
									// causes
									{
										int ndi = indexIndep[di];
										for (int dd = 0; dd < inputData.clusterStructure[c].NDep; dd++) // loop
										// over
										// dependent
										// diseases
										{
											int ndd = indexDep[dd];
											double RRdisC = inputData.clusterData[age][sex][c].RRdisExtended[ndi][ndd];
											double prev = inputData.clusterData[age][sex][c]
													.getPrevalence()[dc];
											if (prev!=0) sumForCF[d] += weight[i]
													* (RRdisC - 1)
													* baselineFatalIncidence[age][sex][d]
													* (probComorbidity[i][c].prob[ndd][ndi] - probComorbidity[i][c].prob[ndd][ndd]
															* probComorbidity[i][c].prob[ndi][ndi])
													/ prev;
											/* if prev ==0 then make sumForCF equal to zero 
											 * basically you cannot calculate the attributable mortality in this case
											 * and so it is made equal to excess mortality?? 
											 * TODO definitief maken en beschrijven*/
										} // end loop over dependent diseases
									}
									;
								} // end loop over causes
								else {
									// here only one loop is needed, as we only
									// need
									// to check of the dependent disease
									// of this intermediate disease

									for (int dd = 0; dd < inputData.clusterStructure[c].NDep; dd++) // loop
									// over
									// dependent
									// diseases
									{
										int ndd = indexDep[dd];
										double RRdisC = inputData.clusterData[age][sex][c].RRdisExtended[dc][ndd];
										double prev = inputData.clusterData[age][sex][c]
												.getPrevalence()[dc];
										if (prev!=0) sumForCF[d] += weight[i]
												* (RRdisC - 1)
												* baselineFatalIncidence[age][sex][d]
												* (1 - probComorbidity[i][c].prob[dc][dc])
												* probComorbidity[i][c].prob[dc][dc]
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
						vMat = probComorbidity[i][c].addBlock(vMat, weight[i],inputData.clusterStructure[c]);
					}

					calculateBaselineInc(inputData, age, sex, sumRRinHealth,
							true);

					// extract the probability of each single disease
					for (int d = 0; d < inputData.nDisease; d++) {

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
				if (age==0 && sex==0) log.debug("end loop 3");
				// now calculate the attributable mortality

				// Divide by Prevalence and nSim;
				for (int d1 = 0; d1 < inputData.nDisease; d1++)
					for (int d2 = 0; d2 < inputData.nDisease; d2++) {
						if (diseasePrevalence[age][sex][d1] != 0)
							vMat[d1][d2] = vMat[d1][d2]
									/ diseasePrevalence[age][sex][d1];
						else
							if (d1==d2) vMat[d1][d2] = 1; else vMat[d1][d2]=0;
					} // TODO: what if prevalence is zero: gaat dit goed? NEE
				/* make vMat into a Matrix */
				Matrix vMatrix = new Matrix(vMat);
				// Invert

				Matrix vInverse = vMatrix.inverse();
				double[] lefthand = new double[inputData.nDisease];
if (age==0 && sex==0) log.debug("matrix is inverted");
				for (int d = 0; d < inputData.nDisease; d++) {
					if (diseasePrevalence[age][sex][d] != 0)
					expectedMortality[d] = baselineMortality [age][sex]* sumRRmDisease[d]
							/ diseasePrevalence[age][sex][d];
					/* if prevalence=0 then attributable mortality is not estimable
					 * in this case attributable mortality should be equal to the excess mortality */
					else expectedMortality[d]=inputData.mortTot[age][sex];
					
					/* mtot + (1-p(d))E(d) - average(mtot(r)|d) */
					lefthand[d] = inputData.mortTot[age][sex]
							+ (1 - diseasePrevalence[age][sex][d])
							* excessMortality[d]
							- expectedMortality[d] - sumForCF[d];
				}
				Matrix LH = new Matrix(lefthand, inputData.nDisease);
				double [] temp =  vInverse.times(LH).getRowPackedCopy();
				if (age==0 && sex==0)		log.debug("attributable mortality calculated");
				for (int d = 0; d < inputData.nDisease; d++) {
				attributableMortality[age][sex][d] = (float) temp[d];}

			}
			if (age==0 && sex==0) log.debug("attributable mortality written");
			for (int d = 0; d < inputData.nDisease; d++) {
				if (Math.abs(attributableMortality[age][sex][d]) < 1e-16)
					attributableMortality[age][sex][d] = 0;
				/* TODO: dit bedenken
				if (diseasePrevalence[age][sex][d]==0)attributableMortality[age][sex][d] = */
			}
			;
			/**
			 * In the fourth stage of parameter estimation, the estimated
			 * attributable mortality is used to calculate the other cause
			 * mortality per simulated person i Then a regression is done of
			 * this other cause mortality on the risk factors yielding relative
			 * risks for other cause mortality
			 */

			if (age==0 && sex==0) log.debug("begin loop 4");
			double sumOtherMort = 0;

			double[] beta;
			double[] RRothermort;

			double otherMort[] = new double[nSim];
			double logOtherMort[] = new double[nSim];

			// make design matrix for regression (including dummy variables
			// for
			// each risk class)
			// TODO: separate for different risk factor types::
			// testing for type 2 and 3
			double[][] xMatrix = new double[nSim][2];

			if (inputData.riskType == 1 || inputData.riskType == 3)
				xMatrix = new double[nSim][inputData.getPrevRisk()[age][sex].length];
			// fourth loop over all persons i: fill the design matrix

			for (int i = 0; i < nSim; i++) {

				// add intercept
				xMatrix[i][0] = 1.0;
				// add dummies except for the first class = reference
				// category
				if (inputData.riskType == 1 || inputData.riskType == 3) {
					for (int rc = 1; rc < inputData.getPrevRisk()[age][sex].length; rc++) {
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
				if (inputData.riskType == 2) {
					xMatrix[i][xMatrix[i].length - 1] = riskfactor[i]
							- inputData.refClassCont;
				}
				otherMort[i] = relRiskMort[i] * baselineMortality[age][sex];
				for (int d = 0; d < inputData.nDisease; d++) {
					otherMort[i] -= attributableMortality[age][sex][d]
							* probDisease[i][d];
				}
				;
				sumOtherMort += weight[i] * otherMort[i];
				if (otherMort[i] > 0)
					logOtherMort[i] = Math.log(otherMort[i]);
				else {
					System.out.println("negative other mortality  = "
							+ otherMort[i] + " for person  " + i
							+ " for riskclass " + riskclass[i]
							+ " and for riskfactor " + riskfactor[i]);
					logOtherMort[i] = -999999;
				}
			}
			// end of fourth loop over all persons i
			if (age==0 && sex==0) log.debug("end loop 4");
			// TODO warnings for other mortality lower then 0

			// carry out the regression of log other mortality on the risk
			// factors;
			try {
				beta = weightedRegression(logOtherMort, xMatrix, weight);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.fatal(e.getMessage());
				throw new RuntimeException(e.getMessage());
			}
			if (age==0 && sex==0) log.debug(" beta 0 and 1 :" + beta[0] + beta[1]);
			// calculate relative risks from the regression coefficients
			// TODO: for compound type other form is needed

			// first class has relative risk of 1
			relRiskOtherMort[age][sex][0] = 1;
			if (inputData.riskType == 1 || inputData.riskType == 3)
				for (int j = 1; j < beta.length; j++)
				// calculate the relative risk relative to the first
				// risk
				// class
				// //
				{
					relRiskOtherMort[age][sex][j] = (float) Math.exp(beta[j]);
					// in case of duration class set rr to 1;
					if (inputData.riskType == 3
							&& inputData.indexDuurClass == j)
						relRiskOtherMort[age][sex][j] = 1;
				}

			// last beta is the coefficient for the continuous risk factor
			// //
			relRiskOtherMortCont[age][sex] = (float) Math.exp(beta[beta.length - 1]);
			if (inputData.riskType == 1 || inputData.riskType == 3)
				relRiskOtherMortCont[age][sex] = 1;

			baselineOtherMortality[age][sex] = (float) Math.exp(beta[0]);
			/**
			 * in the fifth stage the sum of the RR's on other cause mortalities
			 * is calculated in order to estimate the baseline other cause
			 * mortality This could also be derived from the regression
			 * (intercept)
			 * 
			 */
			if (inputData.riskType == 3) { // now do time dependent part;

				// first anker the RRbegin and RRend if those are ankered for
				// all cause mortality
				double endRR = -1;
				double beginRR = -1;
				for (int rc = 0; rc < inputData.getPrevRisk()[age][sex].length; rc++) {
					if (inputData.relRiskDuurMortBegin[age][sex] == inputData.relRiskMortCat[age][sex][rc]
							&& rc != inputData.indexDuurClass)
						beginRR = relRiskOtherMort[age][sex][rc];
					if (inputData.relRiskDuurMortEnd[age][sex] == inputData.relRiskMortCat[age][sex][rc]
							&& rc != inputData.indexDuurClass)
						endRR = relRiskOtherMort[age][sex][rc];
				}
				// select only the data for the duration class;
				double ydata[] = new double[inputData.duurFreq[age][sex].length];
				double xdata[] = new double[inputData.duurFreq[age][sex].length];
				double weightdata[] = new double[inputData.duurFreq[age][sex].length];
				int index = 0;
				for (int i = 1; i < nSim; i++) {
					if (riskclass[i] == inputData.indexDuurClass) {
						ydata[index] = otherMort[i];
						xdata[index] = riskfactor[i];
						weightdata[index] = weight[i];
						index++;
					}

				}
				try {
					beta = nonLinearDurationRegression(ydata, xdata, weightdata,
							endRR, beginRR, baselineOtherMortality[age][sex]);
				} catch (Exception e) {
				log.fatal(e.getMessage());
				e.printStackTrace();
				throw new  RuntimeException(e.getMessage());
					
					
				}
				relRiskOtherMortBegin[age][sex] = (float) beta[0];
				relRiskOtherMortEnd[age][sex] = (float) beta[1];
				alfaOtherMort[age][sex] = (float) beta[2];

			}

			{
			}
			if (age==0 && sex==0) log.debug("begin loop 5");
			// fifth loop over all persons i to calculate sum of RR other
			// mortality to check baselineOtherMortality
			// only temporary to check method
			double baselineOtherMortality2;
			double sumRROtherMort = 0;
			for (int i = 0; i < nSim; i++) {
				if (inputData.riskType == 3) {
					if (riskclass[i] == inputData.indexDuurClass) {

						sumRROtherMort += weight[i]
								* (relRiskOtherMortBegin[age][sex] - relRiskOtherMortEnd[age][sex])
								* Math.exp(alfaOtherMort[age][sex]
										* riskfactor[i])
								+ relRiskOtherMortEnd[age][sex];

					} else {

						sumRROtherMort += weight[i]
								* relRiskOtherMort[age][sex][riskclass[i]];
					}
				} else
					sumRROtherMort += weight[i]
							* relRiskOtherMort[age][sex][riskclass[i]]
							* Math.pow(relRiskOtherMortCont[age][sex],
									riskfactor[i] - inputData.refClassCont);
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
			if (age==0 && sex==0 && baselineOtherMortality2 != baselineOtherMortality[age][sex])
			log.debug("different baseline mortalities calculated nl "
					+ baselineOtherMortality2
					+ " after calibration and  "
					+ baselineOtherMortality[age][sex]+" before");
			if(baselineOtherMortality2!=0)
				if (Math.abs(baselineOtherMortality2 - baselineOtherMortality[age][sex])/baselineOtherMortality2>0.01)
				log.fatal("different baseline mortalities calculated after calibration nl "
								+ baselineOtherMortality2
								+ " after calibration while  "
								+ baselineOtherMortality[age][sex]+" before.");
			baselineOtherMortality[age][sex] = (float) baselineOtherMortality2;

			if (age==0 && sex==0) log.debug("end loop 5");
		}

	}

	/**
	 * @param inputData
	 * @return number of diseases (int)
	 */
	public int getNDiseases(InputData inputData) {
		int nDiseases=0;
		for (int c=0;c<nCluster;c++){nDiseases+=inputData.clusterStructure[c].nInCluster;}
		return nDiseases;
	}
	/**
	 * 
	 * @return number of diseases (int)
	 */
	public int getNDiseases() {
		int nDiseases=0;
		for (int c=0;c<nCluster;c++){nDiseases+=clusterStructure[c].nInCluster;}
		return nDiseases;
	}

	// end constructor
	/**the method calculates the baseline incidence rate  
	 * @param InputData: Object with input data
	 * @param age
	 * @param sex
	 * @param meanRR: mean Relative Risk <b> in health persons </b> in the simulated population
	 * @param isDependent: indicates if this should be calculated for dependent diseases
	 */
	private void calculateBaselineInc(InputData InputData, int age, int sex,
			double[] meanRR, boolean isDependent) {
		// loops over the diseases within clusters
		for (int c = 0; c < InputData.nCluster; c++) {
			for (int dc = 0; dc < InputData.clusterStructure[c].nInCluster; dc++) {
				// this is done either for the independent diseases or the
				// dependent diseases;
				if (InputData.clusterStructure[c].dependentDisease[dc] == isDependent) {
					int d = InputData.clusterStructure[c].diseaseNumber[dc];
					baselineIncidence[age][sex][d] = (float) (InputData.clusterData[age][sex][c].incidence[dc]*
							(1-	InputData.clusterData[age][sex][c].caseFatality[dc])
							/ meanRR[d]
							/ (1 - InputData.clusterData[age][sex][c].prevalence[dc]));

				}
			}
		} // end loops over diseases within cluster
	}
	/**the method calculates the initial estimate for the baseline odds assuming that the prevalence relative risk is equal to
	 * the incidence relative risk 
	 * @param InputData: Object with input data
	 * @param age
	 * @param sex
	 * @param meanRR: mean Relative Risk in the simulated population
	 * @param isDependent: indicates if this should be calculated for dependent diseases
	 */
	private void calculateBaselinePrev(InputData InputData, int age, int sex,
			 double[] meanRR, boolean isDependent) {
		// loops over the diseases within clusters
		for (int c = 0; c < InputData.nCluster; c++) {
			for (int dc = 0; dc < InputData.clusterStructure[c].nInCluster; dc++) {
				// this is done either for the independent diseases or the
				// dependent diseases;
				if (InputData.clusterStructure[c].dependentDisease[dc] == isDependent) {
					int d = InputData.clusterStructure[c].diseaseNumber[dc];

					baselinePrevalenceOdds[age][sex][d] = InputData.clusterData[age][sex][c].prevalence[dc]
							/ meanRR[d];
					
				}

			}

		} // end loops over diseases within cluster

	}
	
	
	/**the method calculates the baseline fatal incidence rate 
	 * @param InputData: Object with input data
	 * @param age
	 * @param sex
	 * @param meanRR: mean Relative Risk in the simulated population
	 * @param isDependent: indicates if this should be calculated for dependent diseases
	 */
	private void calculateBaselineFatalIncidence(InputData InputData, int age, int sex,
			 double[] meanRR, boolean isDependent) {
		// loops over the diseases within clusters
		for (int c = 0; c < InputData.nCluster; c++) {
			for (int dc = 0; dc < InputData.clusterStructure[c].nInCluster; dc++) {
				// this is done either for the independent diseases or the
				// dependent diseases;
				if (InputData.clusterStructure[c].dependentDisease[dc] == isDependent) {
					int d = InputData.clusterStructure[c].diseaseNumber[dc];

					baselineFatalIncidence[age][sex][d] =(float)( InputData.clusterData[age][sex][c].incidence[dc]*
						(	InputData.clusterData[age][sex][c].caseFatality[dc])
							/ meanRR[d]);
					
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
	 * @param y_array: array containing the y values
	 * @param x_array: matrix containing the x values
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
	 * this methods does a non linear regression fitting the model : RR=(RRbegin-RRend)exp(-alfa*time)+RRend.
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
	 * @param BaselineMort: value of the baseline Mortality. The depedent variable of the regression is y_array/BaselineMort 
	 * @return a array with three values: <ls> <le>[0] = estimate of RRbegin;</le>
		<le>[1] = estimate of RRend;</le>
		<le>[2] = estimate of Alfa;</le> </ls>
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

	/**this method does a weighted regression
	 * @param y_array
	 *            array with values of the dependent variable
	 * @param x_array
	 *            matrix with values of the independent variable. First index
	 *            gives datapoint (record) number, second index is for the variable
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
	 * calculatatCuredPrevFraction calculates the fraction of cured disease in
	 * the initial prevalence assuming that past incidence and cure rates were
	 * the same as current incidence and cure rates
	 * 
	 * obsolete as calculateNonCuredPrevFraction is used
	 * 
	 * @param inc
	 *            double[] incidence by age
	 * @param prev
	 *            double[] prevalence by age
	 * @param curedFraction
	 *            [] curedFraction (of newly diagnosed cases) by age
	 * @return percentage of prevalent cases that belongs to the group of cured
	 *         patients
	 */

	private double[] calculateCuredPrevFraction(double inc[], double prev[],
			double[] curedFraction) {
		int n = inc.length;
		double[] curedPrev = new double[n];
		double[] curedPrevFraction = new double[n];
		curedPrev[0] = curedFraction[0] * prev[0];
		// TODO juist formule overnemen uit estimation stuk;
		for (int a = 1; a < n; a++) {
			curedPrev[a] = curedPrev[a - 1] + (1 - Math.exp(-inc[a - 1]))
					* curedFraction[a - 1];
			if (prev[a] != 0)
				curedPrevFraction[a] = curedPrev[a] / prev[a];
			else
				curedPrevFraction[a] = 0;
			if (curedPrevFraction[a] > 1) {
				curedPrevFraction[a] = 1.0;
				curedPrev[a] = prev[a];
			}
		}
		return curedPrev;
	}

	/**This method calculates the prevalence of not cured disease, 
	 * by back-calculating the survivors from earlier agegroups
	 * 
	 * @param inc: array [age][sex] with total incidence of the disease (cured+non cured)
	 * @param prev: array [age][sex] with total prevalence of the disease (cured+non cured) 
	 * @param excessmort: array [age][sex]with excess mortality of the non cured patients
	 * @param curedFraction:array [age][sex]of the fraction of incident cases that can be considered cured 
	 * @return not cured prevalence rate; this is forced to lie between 0 and the input prevalence rate
	 */
	private double[] calculateNotCuredPrevalence(float inc[], float prev[],
			float excessmort[], float[] curedFraction) {
		int n = inc.length;
		double[] notCuredPrev = new double[n];
		double[] CuredPrevFraction = new double[n];
		double expDifferenceIncExcessMort;
		notCuredPrev[0] = (1 - curedFraction[0]) * prev[0];
		// TODO juist formule overnemen uit estimation stuk;
		for (int a = 1; a < n; a++) {

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
		return relRiskDiseaseOnDisease;
	}

	public void setRelRiskDiseaseOnDisease(
			float[][][][][] relRiskDiseaseOnDisease) {
		this.relRiskDiseaseOnDisease = relRiskDiseaseOnDisease;
	}

	public float[][][] getBaselineIncidence() {
		return baselineIncidence;
	}

	public void setBaselineIncidence(float[][][] baselineIncidence) {
		this.baselineIncidence = baselineIncidence;
	}

	public double[][][] getBaselinePrevalenceOdds() {
		return baselinePrevalenceOdds;
	}

	public void setBaselinePrevalenceOdds(double[][][] baselinePrevalenceOdds) {
		this.baselinePrevalenceOdds = baselinePrevalenceOdds;
	}

	public float[][][] getRelRiskOtherMort() {
		return relRiskOtherMort;
	}

	public void setRelRiskOtherMort(float[][][] relRiskOtherMort) {
		this.relRiskOtherMort = relRiskOtherMort;
	}

	public float[][] getRelRiskOtherMortCont() {
		return relRiskOtherMortCont;
	}

	public void setRelRiskOtherMortCont(float[][] relRiskOtherMortCont) {
		this.relRiskOtherMortCont = relRiskOtherMortCont;
	}

	public float[][] getRelRiskOtherMortEnd() {
		return relRiskOtherMortEnd;
	}

	public void setRelRiskOtherMortEnd(float[][] relRiskOtherMortEnd) {
		this.relRiskOtherMortEnd = relRiskOtherMortEnd;
	}

	public float[][] getRelRiskOtherMortBegin() {
		return relRiskOtherMortBegin;
	}

	public void setRelRiskOtherMortBegin(float[][] relRiskOtherMortBegin) {
		this.relRiskOtherMortBegin = relRiskOtherMortBegin;
	}

	public float[][] getAlfaOtherMort() {
		return alfaOtherMort;
	}

	public void setAlfaOtherMort(float[][] alfaOtherMort) {
		this.alfaOtherMort = alfaOtherMort;
	}

	public float[][] getBaselineOtherMortality() {
		return baselineOtherMortality;
	}

	public void setBaselineOtherMortality(float[][] baselineOtherMortality) {
		this.baselineOtherMortality = baselineOtherMortality;
	}

	public float[][][] getAttributableMortality() {
		return attributableMortality;
	}

	public void setAttributableMortality(float[][][] attributableMortality) {
		this.attributableMortality = attributableMortality;
	}

	public float[][][][] getRelRiskClass() {
		return relRiskClass;
	}

	public void setRelRiskClass(float[][][][] relRiskClass) {
		this.relRiskClass = relRiskClass;
	}

	public float[][][] getRelRiskContinue() {
		return relRiskContinue;
	}

	public void setRelRiskContinue(float[][][] relRiskContinue) {
		this.relRiskContinue = relRiskContinue;
	}

	public float[][][] getPrevRisk() {
		return prevRisk;
	}

	public void setPrevRisk(float[][][] prevRisk) {
		this.prevRisk = prevRisk;
	}

	public float[][] getMeanRisk() {
		return meanRisk;
	}

	public void setMeanRisk(float[][] meanRisk) {
		this.meanRisk = meanRisk;
	}

	public float[][] getStdDevRisk() {
		return stdDevRisk;
	}

	public void setStdDevRisk(float[][] stdDevRisk) {
		this.stdDevRisk = stdDevRisk;
	}

	public float[][] getSkewnessRisk() {
		return skewnessRisk;
	}

	public void setSkewnessRisk(float[][] skewnessRisk) {
		this.skewnessRisk = skewnessRisk;
	}

	public float[][][] getRelRiskDuurBegin() {
		return relRiskDuurBegin;
	}

	public void setRelRiskDuurBegin(float[][][] relRiskDuurBegin) {
		this.relRiskDuurBegin = relRiskDuurBegin;
	}

	public float[][][] getRelRiskDuurEnd() {
		return relRiskDuurEnd;
	}

	public void setRelRiskDuurEnd(float[][][] relRiskDuurEnd) {
		this.relRiskDuurEnd = relRiskDuurEnd;
	}

	public float[][][] getAlfaDuur() {
		return alfaDuur;
	}

	public void setAlfaDuur(float[][][] alfaDuur) {
		this.alfaDuur = alfaDuur;
	}

	public float[][][] getDuurFreq() {
		return duurFreq;
	}

	public void setDuurFreq(float[][][] duurFreq) {
		this.duurFreq = duurFreq;
	}

	public float[][] getStdDrift() {
		return stdDrift;
	}

	public void setStdDrift(float[][] stdDrift) {
		this.stdDrift = stdDrift;
	}

	public float[][] getMeanDrift() {
		return meanDrift;
	}

	public void setMeanDrift(float[][] meanDrift) {
		this.meanDrift = meanDrift;
	}

	public float[][] getOffsetDrift() {
		return offsetDrift;
	}

	public void setOffsetDrift(float[][] offsetDrift) {
		this.offsetDrift = offsetDrift;
	}

	public float[][][][] getTransitionMatrix() {
		return transitionMatrix;
	}

	public void setTransitionMatrix(float[][][][] transitionMatrix) {
		this.transitionMatrix = transitionMatrix;
	}

	public float[][][] getCuredFraction() {
		return curedFraction;
	}

	public void setCuredFraction(float[][][] curedFraction) {
		this.curedFraction = curedFraction;
	}

	public float[][][] getBaselineFatalIncidence() {
		return baselineFatalIncidence;
	}

	public void setBaselineFatalIncidence(float[][][] baselineFatalIncidence) {
		this.baselineFatalIncidence = baselineFatalIncidence;
	}

}
