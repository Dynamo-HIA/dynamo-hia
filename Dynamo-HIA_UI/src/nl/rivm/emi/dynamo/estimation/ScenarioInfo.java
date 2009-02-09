/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

/**
 * @author Hendriek
 * This Class contains information on scenario's
 */
public class ScenarioInfo {
	Log log = LogFactory.getLog(getClass().getName());
	
	/* simulation information */
	private boolean withNewBorns;
	private float stepsize;
	private int simPopSize;
	private int maxSimAge;
	private int minSimAge;
	private int yearsInRun;
	private int startYear;
	private int randomSeed;
	
	private int nScenarios=1; 
// nScenarios give the number of alternative scenario's (the baseline scenario not included)
	String [] scenarioNames=null;
	private boolean[] isNormal;
	private boolean [] initialPrevalenceType ={false};	
	private boolean[] transitionType ={false};
	private boolean[] zeroTransition=null;
	private float[][][] newMean; /* indexes: scenario,age, sex
	scenario starts at index 0 with the first alternative scenario info */
	private float[][][] newStd; /* indexes: scenario,age, sex */
	private float[][][] newOffset; /* indexes: scenario,age, sex */
	private float[][][][] newPrevalence; /* indexes: scenario,age, sex, class
	scenario starts at index 0 with the first alternative scenario info */
	private float [][][] oldPrevalence=null; /* prevalence of reference situation; indexes: age sex class */
	private float [][][] meanDrift=null;   
	/* indexes: scenario,age, sex */// TODO volgende 3 inlezen en initialiseren
	private float [][][][] [] alternativeTransitionMatrix;
	private float [] succesrate = null;
	private float [] minAge = null;
	private float [] maxAge = null;
	private boolean[] inMen = null;
	private boolean[] inWomen = null;
	private  float[][] populationSize; // float as no reading method for integers is availlable at the moment
	private  float[][] overallDalyWeight;
	private int[] newborns; // index= year (0=startYearnewborns)
	private int startYearNewborns;
	private float maleFemaleRatio;
	private  String[] riskClassnames;
	private int referenceClass;
	private int riskType;
	private DiseaseClusterStructure[] structure;
	
	private boolean details=false;
	private float[] cutoffs=null;
	/**
	 * TODO: zorgen dat newPrevalence [0] de oude prevalences bevat
	 */
	public ScenarioInfo() {
		// TODO Auto-generated constructor stub
	}
	public void makeTestData (){
		
		setInitialPrevalenceType(new boolean[1]);
		getInitialPrevalenceType()[0]=true;
		
		setNewPrevalence(new float [1][96][2][2]);
		
		for (int a = 0; a < 96; a++) {
			for (int g = 0; g < 2; g++) {
				
				getNewPrevalence()[0][a][g][0]=0.5F;
				getNewPrevalence()[0][a][g][1]=0.5F;
		;}

}}
public void makeTestData1 (){
		
		setInitialPrevalenceType(new boolean[1]);
		getInitialPrevalenceType()[0]=true;
		
		setNewPrevalence(new float [1][96][3][3]);
		
		for (int a = 0; a < 96; a++) {
			for (int g = 0; g < 2; g++) {
				getNewPrevalence()[0][a][g][0]=0.0F;
				getNewPrevalence()[0][a][g][1]=0.5F;
				getNewPrevalence()[0][a][g][2]=0.5F;
		;}

}}
	
	/**
	 * @param scen
	 * @return
	 */
	public float[][][][] getTransitionMatrix(int scen) {
		// TODO Auto-generated method stub
		return null;
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
	public void setInitialPrevalenceType(boolean [] initialPrevalenceType) {
		this.initialPrevalenceType = initialPrevalenceType;
	}
	public boolean [] getInitialPrevalenceType() {
		return initialPrevalenceType;
	}
	public void setTransitionType(boolean[] transitionType) {
		this.transitionType = transitionType;
	}
	public boolean[] getTransitionType() {
		return transitionType;
	}
	/** sets transitionType[i] to b
	 * @param b: value to set 
	 * @param i: index
	 */
	public void setTransitionType(boolean b, int i) {
		transitionType[i]=b;
		
	}
	/** gets transitionType[i] 
	 * @param b: value to set 
	 * @param i: index
	 */
	public boolean getTransitionType( int i) {
		return transitionType[i];
		
	}
	/**set InitialPrevalenceType[i] to b
	 * @param b: value to set 
	 * @param i: index
	 */
	public void setInitialPrevalenceType(boolean b, int i) {
		initialPrevalenceType[i]=b;
		// TODO Auto-generated method stub
		
	}
	public float[] getSuccesrate() {
		return succesrate;
	}
	public void setSuccesrate(float[] succesrate) {
		this.succesrate = succesrate;
	}
	public float[] getMinAge() {
		return minAge;
	}
	public void setMinAge(float[] minAge) {
		this.minAge = minAge;
	}
	public float[] getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(float[] maxAge) {
		this.maxAge = maxAge;
	}
	public String[] getScenarioNames() {
		return scenarioNames;
	}
	public void setScenarioNames(String[] scenarioNames) {
		this.scenarioNames = scenarioNames;
	}
	public DiseaseClusterStructure[] getStructure() {
		return structure;
	}
	public void setStructure(DiseaseClusterStructure[] structure) {
		this.structure = structure;
	}
	public float[] getCutoffs() {
		return cutoffs;
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
	public int[] getNewborns() {
		return newborns;
	}
	public void setOverallDalyWeight(float[][] overallDalyWeight) {
		this.overallDalyWeight = overallDalyWeight;
	}
	public float[][] getOverallDalyWeight() {
		return overallDalyWeight;
	}
	public void setZeroTransition(boolean[] zeroTransition) {
		this.zeroTransition = zeroTransition;
	}
	
	public void setZeroTransition(boolean zeroTransition,int i) {
		this.zeroTransition[i] = zeroTransition;
	}
	public boolean [] isZeroTransition() {
		return zeroTransition;
	}
	public boolean  isZeroTransition(int i) {
		return zeroTransition[i];
	}
	public void setOldPrevalence(float [][][] oldPrevalence) {
		this.oldPrevalence = oldPrevalence;
	}
	public float [][][] getOldPrevalence() {
		return oldPrevalence;
	}
	
	
	
	public void setAlternativeTransitionMatrix(
			float [][][][] [] alternativeTransitionMatrix) {
		this.alternativeTransitionMatrix = alternativeTransitionMatrix;
	}
	public float [][][][] [] getAlternativeTransitionMatrix() {
		return alternativeTransitionMatrix;
	}
	public void setInMen(boolean[] inMen) {
		this.inMen = inMen;
	}
	public boolean[] getInMen() {
		return inMen;
	}
	public void setInWomen(boolean[] inWomen) {
		this.inWomen = inWomen;
	}
	public boolean[] getInWomen() {
		return inWomen;
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
	public void setMeanDrift(float [][][] drift) {
		this.meanDrift = drift;
	}
	public float [][][] getMeanDrift() {
		return meanDrift;
	}
	public float [][] getMeanDrift(int i) {
		return meanDrift[i];
	}
	public void setNewPrevalence(float[][][][] newPrevalence) {
		this.newPrevalence = newPrevalence;
	}
	public void setNewPrevalence(float[][][] newPrevalence,int i) {
		
		this.newPrevalence[i] = newPrevalence;
	}
	
	public boolean isWithInitialChange() {
		boolean returnvalue=false;
		for (int i=0;i<initialPrevalenceType.length;i++)
			if (initialPrevalenceType[i])returnvalue=true;
		return returnvalue;
	}
	
	public int nWithInitialChange() {
		int returnvalue=0;
		for (int i=0;i<initialPrevalenceType.length;i++)
			if (initialPrevalenceType[i])returnvalue++;
		return returnvalue;
	}
	
	
	public int getNTranstionScenarios() {
		int returnvalue=0;
		for (int i=0;i<transitionType.length;i++)
			if (transitionType[i]) returnvalue++;
		return returnvalue;
	}
	
	public float[][][][] getNewPrevalence() {
		return newPrevalence;
	}
	
	
	
	
	/**this method take a mean, std and skewness and converts it to the parameters of a
	 *  lognormal distribution (mu, sigma, offset)
	 * before it sets them as new parameters for the scenario;
	 * 
	 * @param inputMean (float [scenario][age][sex])
	 * * @param inputSTD (float [scenario][age][sex])
	 * * @param inputSkew (float [scenario][age][sex])
	 * @param i: number of scenario;
	 * @param isLognormal (boolean)
	 * @throws DynamoInconsistentDataException 
	 */
	public void setNewMeanSTD(float[][] inputMean,float[][] inputSTD,float[][] inputSkew,int i) throws DynamoInconsistentDataException {
		 boolean isLognormal=false;
		 for (int a=0;a<96;a++)
				for (int g=0;g<2;g++)
					if (inputSkew[a][g]>0) isLognormal=true;
		 if (isLognormal) isNormal[i]=false; else isNormal[i]=true;
		if (isLognormal){
			
			
			
				
				for (int a=0;a<96;a++)
					for (int g=0;g<2;g++){
						
						try {
							newStd[i][a][g]=(float) DynamoLib.findSigma(inputSkew[a][g]);
						} catch (Exception e) {
							
							log.fatal("skewness of lognormal variable " +
									"has a value that is not possible for a lognormal distribution for scenario "+i+
									" at age " +a+ " and gender "+g+ ". Problematic skewness = "+inputSkew[a][g]);
								e.printStackTrace();
							throw new DynamoInconsistentDataException("skewness of lognormal variable " +
									"has a value that is not possible for a lognormal distribution for scenario "+i+
									" at age " +a+ " and gender "+g+ ". Problematic skewness = "+inputSkew[a][g]);
							
						}
						newMean[i][a][g]= (float) (0.5 * (Math.log(inputSkew[a][g] * inputSkew[a][g])
								- Math.log(Math.exp(newStd[i][a][g] * newStd[i][a][g]) - 1) - newStd[i][a][g] * newStd[i][a][g]));
						newOffset [i][a][g]= (float) (inputMean[a][g] - Math.exp(newMean[i][a][g] + 0.5 * newStd[i][a][g] * newStd[i][a][g]));

					}		
		}
		else {
		this.newMean[i] = inputMean;
		this.newStd[i] = inputSTD;
		this.newOffset[i] = null;}
	}
	/**
	 * @param newvalue: new value
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
	


}
