/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

/**
 * @author Hendriek
 * This Class contains information on scenario's
 */
public class ScenarioInfo {

	
	private int nScenarios=1; 
// nScenarios give the number of alternative scenario's (the baseline scenario not included)
	String [] scenarioNames=null;
	private boolean [] initialPrevalenceType ={false};
	
	private boolean[] transitionType ={false};
	boolean[] zeroTransition=null;
	float[][][][] newPrevalence; /* indexes: scenario,age, sex, class
	scenario starts at index?? */
	float [][][] oldPrevalence=null; /* prevalenc of reference situation; indexes: age sex class */
	float [][][] drift=null;    /* indexes: scenario,age, sex */// TODO volgende 3 inlezen en initialiseren
	float [][][] stdDrift=null;    /* indexes: scenario,age, sex */
	float [][][] offsetDrift=null; /*indexes: scenario,age, sex */
	float [][][][] [] alternativeTransitionMatrix;
	float [] succesrate = null;
	float [] minAge = null;
	float [] maxAge = null;
	boolean[] inMen = null;
	boolean[] inWomen = null;
	public float[][] populationSize; // float as no reading method for integers is availlable at the moment
	public float[][] overallDalyWeight;
	int [][] newborns;
	float maleFemaleRatio;
	public String[] riskClassnames;
	private int referenceClass;
	public int riskType;
	public DiseaseClusterStructure[] structure;
	public int startYear;
	public int yearsInRun;
	public boolean details=false;
	
	/**
	 * TODO: zorgen dat newPrevalence [0] de oude prevalences bevat
	 */
	public ScenarioInfo() {
		// TODO Auto-generated constructor stub
	}
	public void makeTestData (){
		
		setInitialPrevalenceType(new boolean[1]);
		getInitialPrevalenceType()[0]=true;
		
		newPrevalence=new float [1][96][2][2];
		
		for (int a = 0; a < 96; a++) {
			for (int g = 0; g < 2; g++) {
				
				newPrevalence[0][a][g][0]=0.5F;
				newPrevalence[0][a][g][1]=0.5F;
		;}

}}
public void makeTestData1 (){
		
		setInitialPrevalenceType(new boolean[1]);
		getInitialPrevalenceType()[0]=true;
		
		newPrevalence=new float [1][96][3][3];
		
		for (int a = 0; a < 96; a++) {
			for (int g = 0; g < 2; g++) {
				newPrevalence[0][a][g][0]=0.0F;
				newPrevalence[0][a][g][1]=0.5F;
				newPrevalence[0][a][g][2]=0.5F;
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
	}}
