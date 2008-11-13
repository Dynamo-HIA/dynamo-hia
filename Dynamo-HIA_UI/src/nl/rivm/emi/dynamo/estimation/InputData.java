package nl.rivm.emi.dynamo.estimation;

/**
 * @author Hendriek
 * This class contains the input data for a particular reference simulation that are needed to estimate
 * the parameters
 * 
 *  
 */
public class InputData {

	// first the general data (the same for each age and gender */
	public int riskType = -1; /* 1=categorical 2=continuous, 3=compound */
	public int nCluster = -1;
	public int nDisease = -1;
	public float refClassCont = -1;
	public int indexDuurClass = -1;
	public String [] riskFactorClassNames;
	public String  riskFactorName;
	/* class number of compound class with duration */
	String riskDistribution = "Normal";;
	DiseaseClusterStructure[] clusterStructure;
	
	// now data per age and gender
	// index 1 always age, 2 always gender
	public DiseaseClusterData[][][] clusterData= new DiseaseClusterData [96][2][];
	public float[][] mortTot = new float [96][2];
	// third index is for risk factor class, fourth for disease //
	
//	public float relRiskCont[][][]= new float [96][2][];
//	public float relRiskMortCat[][][]= new float [96][2][];
//	public float relRiskMortCont[][]= new float [96][2];
//	public float excessMortality[][][]= new float [96][2][];
//	public float caseFatality[][][]= new float [96][2][];
//	public float[][][] relRiskDuurBegin= new float [96][2][];
//	public float[][][] relRiskDuurEnd= new float[96][2][];
//	public float[][][] halfTime= new float [96][2][];
	
	private float prevRisk[][][]= new float [96][2][];;
	public float[][] meanRisk= new float [96][2];
	public float[][] stdDevRisk= new float [96][2];
	public float[][] skewnessRisk= new float [96][2];
	public float[][] relRiskDuurMortBegin= new float[96][2];
	public float[][] relRiskDuurMortEnd= new float [96][2];
	public float[][][] duurFreq= new float [96][2][];
	public float[][] halfTimeMort= new float[96][2];
	public float[][][] relRiskMortCat = new float [96][2][];
	public float[][] relRiskMortCont= new float [96][2];
    
	// for the one dependent cluster gives the numbers of the independent
	// diseases

	// Matrix [] RRdis;

	// RRdis [i-cluster][d-independent][d-dependent]

	// 
	// is an array of RR's

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

				mortTot[age][g] = (float) 0.01;
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
				
				halfTimeMort[age][g] = 5;
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
						clusterData[age][g][0].relRiskCont[0] = 1.1F;
						clusterData[age][g][0].excessMortality[0] = 0.01F;
						clusterData[age][g][0].caseFatality [0]= 0.1F;
						clusterData[age][g][0].relRiskDuurBegin[0]=2;
						clusterData[age][g][0].relRiskDuurEnd[0] = 1.1F;
						clusterData[age][g][0].halfTime[0] =5;

						float[][] hulp = { { 1 },
								{  1.2F },
								{ 1.5F } };
						clusterData[age][g][0].relRiskCat = hulp;
					}
						break;
					case 1: {
						clusterStructure[1] = new DiseaseClusterStructure(
								"ziekte 2", 1);
						clusterData[age][g][1] = new DiseaseClusterData();
						
						clusterData[age][g][1].relRiskCont[0] = 1.1F;
						clusterData[age][g][1].excessMortality[0] = 0.01F;
						clusterData[age][g][1].caseFatality [0]= 0.1F;
						clusterData[age][g][1].relRiskDuurBegin[0]=2;
						clusterData[age][g][1].relRiskDuurEnd[0] = 1.1F;
						clusterData[age][g][1].halfTime[0] =5;
						
						float[][] hulp = { { 1 },
								{  1.2F },
								{ 1.5F } };
						clusterData[age][g][1].relRiskCat = hulp;
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
								clusterStructure[2], RRdis);
						float[] hulp1 = { 1.1F, 1.1F, 1.1F, 1.1F, 1.1F, 1.1F, 1.1F };
						clusterData[age][g][2].relRiskCont = hulp1;
						float[] hulp2 = { 0.01F, 0.01F, 0.01F, 0.01F, 0.01F, 0.01F, 0.01F };
						clusterData[age][g][2].excessMortality = hulp2;
						float[] hulp3 = { 0, 0, 0, 0.1F, 0.1F, 0, 0.1F };
						clusterData[age][g][2].caseFatality = hulp3;
						float[] hulp5 = { 2, 2, 2, 2, 2, 2, 2 };
						clusterData[age][g][2].relRiskDuurBegin= hulp5;
						float[] hulp6 = { 1.1F, 1.1F, 1.1F, 1.1F, 1.1F, 1.1F, 1.1F };
						clusterData[age][g][2].relRiskDuurEnd = hulp6;
						float[] hulp8 = { 5, 5, 5, 5, 5, 5, 5 };
						clusterData[age][g][2].halfTime = hulp8;
						float[][] hulp = { {  1, 1, 1, 1, 1 },
								{   1.2F, 1.2F, 1.2F, 1.2F, 1.2F },
								{ 1.5F, 1.5F, 1.5F, 1.5F, 1.5F } };
						clusterData[age][g][2].relRiskCat = hulp;
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
						clusterData[age][g][1].setIncidence(0.01F);
						clusterData[age][g][1].setPrevalence(0.1F);

					}
						break;
					case 2: {
						float[] Istring = { 0.01F, 0.01F, 0.01F, 0.01F, 0.01F };
						float[] Pstring = { 0.1F, 0.1F, 0.1F, 0.1F, 0.1F };
						clusterData[age][g][2].incidence = Istring;
						clusterData[age][g][2].prevalence = Pstring;
					}
						break;
					}
				}

				// count number of diseases;
				nDisease = 0;
				for (int c = 0; c < nCluster; c++) {
					nDisease += clusterStructure[c].nInCluster;
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
		String[] nameString = { "ziekte1", "ziekte2" };
		clusterStructure =new DiseaseClusterStructure [nCluster] ;
		clusterStructure[0] = new DiseaseClusterStructure(
				"cluster1", 0, 2, nameString, nrIndep);
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
								clusterStructure[0], RRdis);
						
						
						clusterData[age][g][0].relRiskCont = new float[2];// not used
						clusterData[age][g][0].relRiskCat = new float[2][2];
						for (int i = 0; i < 2; i++) {
// i = disease nr ;
		// risk=0	
							clusterData[age][g][0].relRiskCat[0][i] = 1;
	// risk=1					
							clusterData[age][g][0].relRiskCat[1][i] = 2;
						}
						
						clusterData[age][g][0].excessMortality= new float[2];
						clusterData[age][g][0].excessMortality[0] = 0.01F;
						clusterData[age][g][0].excessMortality[1] = 0.01F;

						clusterData[age][g][0].caseFatality = new float[2];
						clusterData[age][g][0].caseFatality[0] = 0;
						clusterData[age][g][0].caseFatality[1] = 0;
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
						float[] Istring = { 0.01F, 0.01F };
						float[] Pstring = { 0.1F, 0.1F };
						clusterData[age][g][0].incidence = Istring;
						clusterData[age][g][0].prevalence = Pstring;
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
					nDisease += clusterStructure[c].nInCluster;
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
		return mortTot;
	}

	public void setMortTot(float[][] mortTot) {
		this.mortTot = mortTot;
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

	public float[][] getRelRiskDuurMortBegin() {
		return relRiskDuurMortBegin;
	}

	public void setRelRiskDuurMortBegin(float[][] relRiskDuurMortBegin) {
		this.relRiskDuurMortBegin = relRiskDuurMortBegin;
	}

	public float[][] getRelRiskDuurMortEnd() {
		return relRiskDuurMortEnd;
	}

	public void setRelRiskDuurMortEnd(float[][] relRiskDuurMortEnd) {
		this.relRiskDuurMortEnd = relRiskDuurMortEnd;
	}

	public float[][][] getDuurFreq() {
		return duurFreq;
	}

	public void setDuurFreq(float[][][] duurFreq) {
		this.duurFreq = duurFreq;
	}



	public float[][] getHalfTimeMort() {
		return halfTimeMort;
	}

	public void setHalfTimeMort(float[][] halfTimeMort) {
		this.halfTimeMort = halfTimeMort;
	}

	}

