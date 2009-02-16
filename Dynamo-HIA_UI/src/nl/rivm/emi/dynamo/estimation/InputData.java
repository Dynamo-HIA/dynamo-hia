package nl.rivm.emi.dynamo.estimation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

/**
 * @author Hendriek
 * This class contains the input data for the reference simulation.
 * These data are needed to estimate the parameters
 * 
 *  
 */
public class InputData {
	Log log = LogFactory.getLog(getClass().getName());


	// first the general data (the same for each age and gender */
	private int  riskType = -1; /* 1=categorical 2=continuous, 3=compound */
	private int nCluster = -1;
	private int nDisease = -1;
	private float refClassCont = -1;
	private int indexDuurClass = -1;
	private String [] riskFactorClassNames;
	private String  riskFactorName;
	/* class number of compound class with duration */
	String riskDistribution = "Normal";
	/* index = cluster number */
	DiseaseClusterStructure[] clusterStructure;
	/* index = age, sex, clusternumber */
	private DiseaseClusterData[][][] clusterData= new DiseaseClusterData [96][2][];
	private float[][] mortTot = new float [96][2];
	// third index is for risk factor class //

	private float prevRisk[][][]= new float [96][2][];;
	private float[][] meanRisk= new float [96][2];
	private float[][] stdDevRisk= new float [96][2];
	private float[][] skewnessRisk= new float [96][2];
	private float[][] relRiskDuurMortBegin= new float[96][2];
	private float[][] relRiskDuurMortEnd= new float [96][2];
	private float[][][] duurFreq= new float [96][2][];
	private float[][] alphaMort= new float[96][2];
	private float[][][] relRiskMortCat = new float [96][2][];
	private float[][] relRiskMortCont= new float [96][2];
    
	// transition data
	private int transType=-1; 
	/* transtype: 0= null, 1= netto, 2= as inputted */
	// now data per age and gender
	// index 1 always age, 2 always gender
	private float[][] stdDrift = new float[96][2];
	private  float[][] meanDrift = new float[96][2];
	private float[][] offsetDrift = new float[96][2];
	private float[][][][] transitionMatrix = new float[96][2][][];
    private boolean withRRForMortality=true;
    private boolean withRRForDisability=true;
    private float [][] overallDalyWeight = new float [92][2];
	private float [][][] ORforDisabilityCat =   new float [92][2][];
	private float [][] ORforDisabilityCont =   new float [92][2];
	//TODO: filling the disability arrays
	/**
	 * the constructor () fills the fields with test data
	 */
	
	
	public InputData() {};
	public void makeTest1Data() {

		// fill RRdis only for clusters with more than one disease;
		// otherwise it is not possible to use a matrix (not defined for 1 by 1
		// matrix) ;

		// calculate number of independent diseases;
		riskType = 1; /* 1=categorical 2=continuous, 3=compound */
		nCluster = 3;
		nDisease = 7;
		refClassCont = 1;
		indexDuurClass = 2;
		int[] indexIndependentDiseases = { 1, 4 };
		for (int age = 0; age < 96; age++)
			for (int g = 0; g < 2; g++) {

				mortTot[age][g] = (float) 0.05;
				// first index is for risk factor class, second for disease //
				
				
				float[] hulp4 = { 0.2F, 0.3F, 0.5F };
				prevRisk[age][g] = hulp4;
				meanRisk[age][g] = 25;
				stdDevRisk[age][g] = 3;
				skewnessRisk[age][g] = 1;
				
				relRiskDuurMortBegin[age][g] = 2;
				relRiskDuurMortEnd[age][g] = 1;
				float[] hulp7 = { 0.2F, 0.2F, 0.2F, 0.1F, 0.1F, 0.1F, 0.1F };
				duurFreq[age][g] = hulp7;
				
				alphaMort[age][g] = 5;
				float[] hulp11 = { 1, (float) 1.1, (float) 1.2 };
			    relRiskMortCat [age][g]= hulp11;
				relRiskMortCont [age][g]= (float) 1.1;
				

				// for the one dependent cluster gives the numbers of the
				// independent
				// diseases

				// Matrix [] RRdis;

				clusterData[age][g] = new DiseaseClusterData[nCluster];
				clusterStructure = new DiseaseClusterStructure[nCluster];
				for (int c = 0; c < nCluster; c++) {
					switch (c) {
					case 0: {
						clusterStructure[0] = new DiseaseClusterStructure(
								"ziekte 1", 0);
						clusterData[age][g][0] = new DiseaseClusterData();
						clusterData[age][g][0].getRelRiskCont()[0] = 1.1F;
						clusterData[age][g][0].getExcessMortality()[0] = 0.01F;
						clusterData[age][g][0].getCaseFatality() [0]= 0.0F;
						clusterData[age][g][0].getRelRiskDuurBegin()[0]=2;
						clusterData[age][g][0].getRelRiskDuurEnd()[0] = 1.1F;
						clusterData[age][g][0].getAlpha()[0] =0.15F;

						float[][] hulp = { { 1 },
								{  1.2F },
								{ 1.5F } };
						clusterData[age][g][0].setRelRiskCat ( hulp);
					}
						break;
					case 1: {
						clusterStructure[1] = new DiseaseClusterStructure(
								"ziekte 2", 1);
						clusterData[age][g][1] = new DiseaseClusterData();
						
						clusterData[age][g][1].setRelRiskCont(1.2F,0);
						clusterData[age][g][1].setExcessMortality(0.02F,0);
						clusterData[age][g][1].setCaseFatality(0.02F,0) ;
						clusterData[age][g][1].setRelRiskDuurBegin(2,0);
						clusterData[age][g][1].setRelRiskDuurEnd(1.2F,0);
						clusterData[age][g][1].setRrAlpha(15F,0);
						
						float[][] hulp = { { 1 },
								{  1.2F },
								{ 1.5F } };
						clusterData[age][g][1].setRelRiskCat ( hulp);
						// here the first index is rc {risk factor class}, and the
						// second d (disease);
						
					}
						;
						break;

					case 2: {

						String[] Namestring = { "ziekte3", "ziekte4",
								"ziekte5", "ziekte6", "ziekte7" };
						float[][] RRdis = { { 2, 2, 1 }, { 1, 2, 2 } };
						int[] nrIndep = { 1, 4 };
						clusterStructure[2] = new DiseaseClusterStructure(
								"cluster1", 2, 5, Namestring, nrIndep);
						clusterData[age][g][2] = new DiseaseClusterData(
								clusterStructure[2], 3,RRdis);
						float[] hulp1 = { 1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F };
						clusterData[age][g][2].setRelRiskCont(hulp1) ;
						float[] hulp2 = { 0.01F, 0.02F, 0.03F, 0.04F, 0.05F, 0.06F, 0.07F };
						clusterData[age][g][2].setExcessMortality (hulp2);
						float[] hulp3 = { 0, 0, 0, 0.01F, 0.02F, 0, 0.03F };
						clusterData[age][g][2].setCaseFatality (hulp3);
						float[] hulp5 = { 2, 2, 2, 2, 2, 2, 2 };
						clusterData[age][g][2].setRelRiskDuurBegin(hulp5);
						float[] hulp6 = { 1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F };
						clusterData[age][g][2].setRelRiskDuurEnd (hulp6);
						float[] hulp8 = { 0.15F,  0.015F,  0.15F,  0.015F,  0.15F,  0.015F,  0.15F };
						clusterData[age][g][2].setRrAlpha( hulp8);
						float[][] hulp = { {  1, 1, 1, 1, 1 },
								{   1.2F, 1.4F, 1.6F, 1.8F, 2.2F },
								{ 1.5F, 1.7F, 1.9F, 2.1F, 2.5F } };
						clusterData[age][g][2].setRelRiskCat( hulp);
						// here the first index is rc {risk factor class}, and the
						// second d (disease);
						
						
					}
						break;
					}
					// fill with data

					switch (c) {
					case 0: {
						clusterData[age][g][0].setIncidence(0.01F);
						clusterData[age][g][0].setPrevalence(0.1F);
					}
						break;

					case 1: {
						clusterData[age][g][1].setIncidence(0.02F);
						clusterData[age][g][1].setPrevalence(0.2F);

					}
						break;
					case 2: {
						float[] Istring = { 0.01F, 0.02F, 0.03F, 0.04F, 0.05F };
						float[] Pstring = { 0.1F, 0.12F, 0.15F, 0.2F, 0.25F };
						clusterData[age][g][2].setIncidence(Istring);
						clusterData[age][g][2].setPrevalence(Pstring);
					}
						break;
					}
				}

				// count number of diseases;
				nDisease = 0;
				for (int c = 0; c < nCluster; c++) {
					nDisease += clusterStructure[c].getNInCluster();
				}
				// make an array with all prevalences
				
			}
	}

	/**
	 * makeTest2() fills it with simple test data conform excel-spreadsheet
	 * this is not needed for a final version
	 */
	public void makeTest2Data() {

		// this method files the object with test data for test2
		nDisease = 2;
		riskType = 1; /* 1=categorical 2=continuous, 3=compound */
		nCluster = 1;
		
		
		indexDuurClass = 2; /* class number of compound class with duration */
		int[] nrIndep = { 0 };
		String[] nameString = { "disease1", "disease2" };
		clusterStructure =new DiseaseClusterStructure [nCluster] ;
		clusterStructure[0] = new DiseaseClusterStructure(
				"cluster0", 0, 2, nameString, nrIndep);
		clusterStructure[0].setWithCuredFraction(false);
		
		for (int age = 0; age < 96; age++)
			for (int g = 0; g < 2; g++) {
				mortTot[age][g] = 0.1F;
				// first index is for risk factor class, second for disease //
				
				// here the first index is rc {risk factor class}, and the
				// second d (disease);
				
				prevRisk[age][g] = new float[2];
				prevRisk[age][g][0] = 0.3F;
				prevRisk[age][g][1] = 0.7F;
				relRiskMortCat[age][g] = new float[2];
				relRiskMortCat[age][g][0] = 1;
				relRiskMortCat[age][g][1] = 2;
				relRiskMortCont[age][g]= 1;
				
				int[] indexIndependentDiseases = new int[1];
				indexIndependentDiseases[0] = 1;
				// for the one dependent cluster gives the numbers of the
				// independent
				// diseases

				// Matrix [] RRdis;

				clusterData[age][g] = new DiseaseClusterData[nCluster];
				for (int c = 0; c < nCluster; c++) {
					switch (c) {
					case 0: {
						
						
						
						
						
						
						
						
						
						float[][] RRdis =new float [2][2];
						RRdis[0][0]=1;
						RRdis[0][1]=2;
						RRdis[1][0]=1;
						RRdis[1][1]=1;
						
						clusterData[age][g][0] = new DiseaseClusterData(
								clusterStructure[0],2, RRdis);
						
						
						clusterData[age][g][0].setRelRiskCont( new float[2]);// not used
						clusterData[age][g][0].setRelRiskCat( new float[2][2]);
						for (int i = 0; i < 2; i++) {
// i = disease nr ;
		// risk=0	
							clusterData[age][g][0].getRelRiskCat()[0][i] = 1;
	// risk=1					
							clusterData[age][g][0].getRelRiskCat()[1][i] = 2;
						}
						
						clusterData[age][g][0].setExcessMortality( new float[2]);
						clusterData[age][g][0].setExcessMortality( 0.01F,0);
						clusterData[age][g][0].setExcessMortality( 0.01F,1);

						clusterData[age][g][0].setCaseFatality ( new float[2]);
						clusterData[age][g][0].setCaseFatality(0,0);
						clusterData[age][g][0].setCaseFatality(0,1);
						// public DiseaseClusterData(String Name, int StartN,
						// int N, String[] Name2,
						// int[] NRIndependent, double[][] RRdis )
						// NRIndependent = number of independent diseases ,
						// starting at 0
					}
						break;
					case 1: {

						// DiseaseClusterData(String Name, int StartN)
					}
						break;

					case 2: {

					}
						break;
					}
					// fill with data

					switch (c) {
					case 0: {
						float[] Istring = { 0.01F, 0.02F };
						float[] Pstring = { 0.1F, 0.2F };
						clusterData[age][g][0].setIncidence(Istring);
						clusterData[age][g][0].setPrevalence(Pstring);
					}
						break;

					case 1: {
						clusterData[age][g][1].setIncidence(0.01F);
						clusterData[age][g][1].setPrevalence(0.1F);

					}
						break;
					case 2: {

					}
					}
				}

				// count number of diseases;
				nDisease = 0;
				for (int c = 0; c < nCluster; c++) {
					nDisease += clusterStructure[c].getNInCluster();
				}
				// make an array with all prevalences
				
				
			}
	};
	public void readData(String baseDir, String simulationName){//TODO
		readSimulationConfiguration(baseDir,simulationName);};
		
		
	public void readSimulationConfiguration(String baseDir, String simulationName){//TODO
			readSimulationConfiguration(baseDir,simulationName);}

	public int getRiskType() {
		return riskType;
	}

	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	public int getNCluster() {
		return nCluster;
	}

	public void setNCluster(int cluster) {
		nCluster = cluster;
	}

	public int getNDisease() {
		return nDisease;
	}

	public void setNDisease(int disease) {
		nDisease = disease;
	}

	public float getRefClassCont() {
		return refClassCont;
	}

	public void setRefClassCont(float refClassCont) {
		this.refClassCont = refClassCont;
	}

	public int getIndexDuurClass() {
		return indexDuurClass;
	}

	public void setIndexDuurClass(int indexDuurClass) {
		this.indexDuurClass = indexDuurClass;
	}

	public String[] getRiskFactorClassNames() {
		return riskFactorClassNames;
	}

	public void setRiskFactorClassNames(String[] riskFactorClassNames) {
		this.riskFactorClassNames = riskFactorClassNames;
	}

	public String getRiskFactorName() {
		return riskFactorName;
	}

	public void setRiskFactorName(String riskFactorName) {
		this.riskFactorName = riskFactorName;
	}

	

	public String getRiskDistribution() {
		return riskDistribution;
	}

	public void setRiskDistribution(String riskDistribution) {
		this.riskDistribution = riskDistribution;
	}

	public DiseaseClusterStructure[] getClusterStructure() {
		return clusterStructure;
	}

	public void setClusterStructure(DiseaseClusterStructure[] clusterStructure) {
		this.clusterStructure = clusterStructure;
	}

	public DiseaseClusterData[][][] getClusterData() {
		return clusterData;
	}

	public void setClusterData(DiseaseClusterData[][][] clusterData) {
		this.clusterData = clusterData;
	}

	public float[][] getMortTot() {
		return deepcopy(mortTot);
	}

	public void setMortTot(float[][] mortTot) {
		this.mortTot = mortTot;
	}

	


	public float[][][] getPrevRisk() {
		return deepcopy(prevRisk);
	}

	public void setPrevRisk(float[][][] prevRisk) {
		this.prevRisk = prevRisk;
	}

	public float[][] getMeanRisk() {
		return meanRisk.clone();
	}

	public void setMeanRisk(float[][] inputMean){
		this.meanRisk = inputMean;
	}
  private float [][] deepcopy( float [][] inarray)
  {float [][] returnarray= new float [inarray.length][inarray[0].length];
for (int i=0;i<inarray.length;i++) System.arraycopy(inarray[i],0,returnarray[i],0,inarray[0].length) ;
return returnarray;
  
	  
  }
  private float [][][] deepcopy( float [][] []inarray)
  {float [][][] returnarray= new float [inarray.length][inarray[0].length][inarray[0][0].length];
  for (int i=0;i<inarray.length;i++)
for (int j=0;j<inarray[0].length;j++) System.arraycopy(inarray[i][j],0,returnarray[i][j],0,inarray[0][0].length) ;
return returnarray;
  
	  
  }
  

  private float [][][][] deepcopy( float [][][][]inarray)
  {float [][][][] returnarray= new float [inarray.length][inarray[0].length][inarray[0][0].length][inarray[0][0][0].length];
  for (int i=0;i<inarray.length;i++)
for (int j=0;j<inarray[0].length;j++)
	for (int k=0;k<inarray[0][0].length;k++)System.arraycopy(inarray[i][j][k],0,returnarray[i][j][k],0,inarray[0][0][0].length) ;
return returnarray;
  
	  
  }
	
	
	
	public float[][] getStdDevRisk() {
		return deepcopy(stdDevRisk);
	}

	public void setStdDevRisk(float[][] stdDevRisk) {
		this.stdDevRisk = deepcopy(stdDevRisk);
	}

	public float[][] getSkewnessRisk() {
		return deepcopy(skewnessRisk);
	}

	public void setSkewnessRisk(float[][] skewnessRisk) {
		this.skewnessRisk = skewnessRisk;
	}

	public float[][] getRelRiskDuurMortBegin() {
		return deepcopy(relRiskDuurMortBegin);
	}

	public void setRelRiskDuurMortBegin(float[][] relRiskDuurMortBegin) {
		this.relRiskDuurMortBegin = deepcopy(relRiskDuurMortBegin);
	}

	public float[][] getRelRiskDuurMortEnd() {
		return relRiskDuurMortEnd;
	}

	public void setRelRiskDuurMortEnd(float[][] relRiskDuurMortEnd) {
		this.relRiskDuurMortEnd = relRiskDuurMortEnd;
	}

	public float[][][] getDuurFreq() {
		return deepcopy(duurFreq);
	}
	/**
	 * @param input: array (float[][][]) of prevalence of risk factor (indexes: age, sex, risk category)
	 * @param percent: boolean telling whether input is in percent
	 */

	public void setDuurFreq(float[][][] input, boolean percent) {
		
		if (percent){
			this.duurFreq=new float[96][2][input[0][0].length];
		for (int a=0;a<96;a++)
			for (int g=0;g<2;g++)
				for (int r=0;r<input[0][0].length;r++)
				this.duurFreq[a][g][r]=input[a][g][r]/100;}
		else this.duurFreq=input;
	}



	public float[][] getRrAlphaMort() {
		return deepcopy(alphaMort);
	}

	public void setRrAplhaMort(float[][] halfTimeMort) {
		this.alphaMort = halfTimeMort;
	}
	public float[][][] getRelRiskMortCat() {
		return deepcopy(relRiskMortCat);
	}
	public void setRelRiskMortCat(float[][][] relRiskMortCat) {
		this.relRiskMortCat = relRiskMortCat;
	}
	public float[][] getRelRiskMortCont() {
		return deepcopy(relRiskMortCont);
	}
	public void setRelRiskMortCont(float[][] relRiskMortCont) {
		this.relRiskMortCont = relRiskMortCont;
	}
	/**
	 * @param input: array (float[][][]) of prevalence of risk factor (indexes: age, sex, risk category)
	 * @param percent: boolean telling whether input is in percent
	 */
	public void setPrevRisk(float[][][] input, boolean percent) {
		if (percent){
			this.prevRisk=new float[96][2][input[0][0].length];
		for (int a=0;a<96;a++)
			for (int g=0;g<2;g++)
				for (int r=0;r<input[0][0].length;r++)
				this.prevRisk[a][g][r]=input[a][g][r]/100;}
		else this.prevRisk=input;
		
		
	}
	public int getTransType() {
		return transType;
	}
	public void setTransType(int transType) {
		this.transType = transType;
	}
	public float[][] getStdDrift() {
		return deepcopy(stdDrift);
	}
	public void setStdDrift(float[][] stdDrift) {
		this.stdDrift = stdDrift;
	}
	public float[][] getMeanDrift() {
		return deepcopy(meanDrift);
	}
	public void setMeanDrift(float[][] meanDrift) {
		this.meanDrift = meanDrift;
	}
	public float[][] getOffsetDrift() {
		return deepcopy(offsetDrift);
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
	public boolean isWithRRForMortality() {
		return withRRForMortality;
	}
	public void setWithRRForMortality(boolean withRRForMortality) {
		this.withRRForMortality = withRRForMortality;
	}
	public void setAlphaMort(float[][] input) {
		this.alphaMort = input;
	}
	
	public float[][] getAlphaMort() {
		return this.alphaMort;
	}
	/**
	 * @param newdata
	 * @param a
	 * @param g
	 * @param c
	 */
	public void setClusterData(DiseaseClusterData newdata, int a, int g, int c) {
		clusterData[a][g][c]=newdata;
		
	}
	/**
	 * @param newStructure
	 * @param c
	 */
	public void setClusterStructure(DiseaseClusterStructure newStructure, int c) {
		clusterStructure[c]=newStructure;
		
	}
	public float[][] getOverallDalyWeight() {
		return overallDalyWeight;
	}
	public void setOverallDalyWeight(float[][] overallDalyWeight) {
		this.overallDalyWeight = overallDalyWeight;
	}
	public boolean isWithRRForDisability() {
		return withRRForDisability;
	}
	public void setWithRRForDisability(boolean withRRForDisability) {
		this.withRRForDisability = withRRForDisability;
	}
	public float[][][] getORforDisabilityCat() {
		return ORforDisabilityCat;
	}
	public void setORforDisabilityCat(float[][][] rforDisabilityCat) {
		ORforDisabilityCat = rforDisabilityCat;
	}
	public float[][] getORforDisabilityCont() {
		return ORforDisabilityCont;
	}
	public void setORforDisabilityCont(float[][] rforDisabilityCont) {
		ORforDisabilityCont = rforDisabilityCont;
	}

	}

