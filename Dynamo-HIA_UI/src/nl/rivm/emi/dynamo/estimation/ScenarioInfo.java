/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

/**
 * @author Hendriek
 * This Class contains information on scenario's
 */
public class ScenarioInfo {

	
	int nScenarios=1; // nScenarios give the number of alternative scenario's (the baseline scenario not included)
	boolean [] initialPrevalenceType ={false};
	boolean[] transitionType ={false};
	float[][][][] newPrevalence=null; /* indexes: scenario,age, sex, class */
	float [][][] drift=null;    /* indexes: scenario,age, sex */
	float [][][] stdDrift=null;    /* indexes: scenario,age, sex */
	float [][][] offsetDrift=null; /*indexes: scenario,age, sex */
	float succesrate = 0.5F;
	float minAge = 20;
	float maxAge = 50;
	boolean inMen = true;
	boolean inWomen = true;
	
	/**
	 * 
	 */
	public ScenarioInfo() {
		// TODO Auto-generated constructor stub
	}
	public void makeTestData (){
		
		initialPrevalenceType = new boolean[1];
		initialPrevalenceType[0]=true;
		
		newPrevalence=new float [1][96][2][2];
		
		for (int a = 0; a < 96; a++) {
			for (int g = 0; g < 2; g++) {
				
				newPrevalence[0][a][g][0]=0.5F;
				newPrevalence[0][a][g][1]=0.5F;
		;}

}}
public void makeTestData1 (){
		
		initialPrevalenceType = new boolean[1];
		initialPrevalenceType[0]=true;
		
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
	}}
