package nl.rivm.emi.dynamo.output;

import java.io.Serializable;

public abstract class CDMOutputFactory implements CDMOutputInterface, Serializable {

	
	
	

	/* simple fields and getters */
	
	/**
	 * percentage disability (or daly) by age and riskclass at start of
	 * simulation
	 */
	protected double[][][][][] pDisabilityByRiskClassByAge;
	protected double[][][][][] pDisabilityByOriRiskClassByOriAge;
	
	
	public double[][][][][] getPDisabilityByOriRiskClassByOriAge() {
		return pDisabilityByOriRiskClassByOriAge;
	}

	/**
	 * number of new cases in the next year by age and riskclass at start of
	 * simulation
	 * indexes: scenario, year in run, disease, riskclass, age, sex ;
	 */
	
	protected double[][][][][][] newCasesByRiskClassByAge;
	
	public synchronized double getNewCasesByOriRiskClassByOriAge(int scen, int step,int d, int r, int a, int g) {
		return newCasesByOriRiskClassByOriAge[scen][step][d][r][a][g];
	}


	protected double[][][][][][] newCasesByOriRiskClassByOriAge;
	
	public synchronized double[][][][][][] getNewCasesByOriRiskClassByOriAge() {
		return newCasesByOriRiskClassByOriAge;
	}


	
	public synchronized double[][][][][][] getNewCasesByRiskClassByAge() {
		return newCasesByRiskClassByAge;
	}
	
	/** number of new deaths in the next year by age and riskclass 
	 * indexes: scenario, year in run, riskclass, age, sex ;
	 */
	
	
	protected double[][][][][] mortalityByRiskClassByAge;
	public synchronized double getMortalityByRiskClassByAge(int scen, int step, int r, int a, int g) {
		return mortalityByRiskClassByAge[scen][step][r][a][g];
	}
	public synchronized double [][][][][] getMortalityByRiskClassByAge() {
		return mortalityByRiskClassByAge;
	}

	public synchronized double getMortalityByOriRiskClassByOriAge(int scen, int step, int r, int a, int g) {
		return mortalityByOriRiskClassByOriAge[scen][step][r][a][g];
	}
	/** number of new deaths in the next year by age and riskclass at the start of simulation
	 * indexes: scenario, year in run, riskclass, age, sex ;
	 */
	protected double[][][][][] mortalityByOriRiskClassByOriAge;
	public synchronized double[][][][][] getMortalityByOriRiskClassByOriAge() {
		return mortalityByOriRiskClassByOriAge;
	}

	public synchronized double getPDisabilityByOriRiskClassByOriAge(int scen, int step, int r, int a, int g) {
		return pDisabilityByOriRiskClassByOriAge[scen][step][r][a][g];
	}

	public synchronized double getPDisabilityByRiskClassByAge(int scen, int step, int r, int a, int g) {
		return pDisabilityByRiskClassByAge[scen][step][r][a][g];
	}
	
    /**
	 * 
	 */
	
	protected double[][][][][] pTotalDiseaseByRiskClassByAge;

	public double[][][][][] getPTotalDiseaseByRiskClassByAge() {
		return pTotalDiseaseByRiskClassByAge;
	}

	

	/**
	 * percentage of total diseases by age and riskclass at start of simulation
	 */
	protected double[][][][][] pTotalDiseaseByOriRiskClassByOriAge;

	/*
	 * temporary arrays that contain simulated survival by risk class by Age;
	 * indexes are: scenario, time, risk class, age, and sex NB this array is
	 * reused, so it contains something different before and after applying
	 * method makeSummaryArrays
	 */

	public double[][][][][] getPTotalDiseaseByOriRiskClassByOriAge() {
		return pTotalDiseaseByOriRiskClassByOriAge;
	}

	public synchronized double getPTotalDiseaseByRiskClassByAge(int scen, int step, int r, int a, int g) {
		return pTotalDiseaseByRiskClassByAge[scen][step][r][a][g];
	}

	public synchronized double getPTotalDiseaseByOriRiskClassByOriAge(int scen, int step, int r, int a, int g) {
		return pTotalDiseaseByOriRiskClassByOriAge[scen][step][r][a][g];
	}
	private static final long serialVersionUID = 1L;
	protected int stepsInRun;
    
	/**  
	 * @return stepsInRun, the number of years of simulation
	 */
	public int getStepsInRun() {
		return stepsInRun;
	}
	
	protected int nScen;
    
	/**
	 * @return nScen, the number of scenario's (excluding the reference scenario)
	 */
	public int getNScen() {
		
		return nScen;
	}

	protected int  riskType;
	/**
	 * @return riskfactor Type: 1=categorical, 2= continuous, 3=compound
	 */
	public int getRiskType() {
		
		return riskType;
	}

	
	protected int startYear;
	
	/**
	 * @return starting year of simulation
	 */
	public int getStartYear() {
		
		return startYear;
	}

	protected String[] diseaseNames;
	/**
	 * @return String [] diseaseNames
	 */
	public String[] getDiseaseNames() {
		return this.diseaseNames;
	}
	
	protected String[] stateNames;

	//protected boolean categorized;
	///**
	// * @return boolean for categorized
	// */
	//public boolean getCategorized() {
		
	//	return categorized;
	//}

	public String[] getStateNames() {
		return stateNames;
	}

	protected String [] scenarioNames;
	/**
	 * @return String [] scenarioNames
	 */
	public String[] getScenarioNames() {
		
		return scenarioNames;
	}
	protected int nDiseases;
	public int getNDiseases() {
		
		return nDiseases;
	}

	protected int maxAgeInSimulation;
	/**
	 * @return maximum age of the simulated population (at the start of the simulation)
	 */
	public int getMaxAgeInSimulation() {
		return this.maxAgeInSimulation;
	}
	/**
	 * @return minimum age of the simulated population (at the start of the simulation, and >=0)
	 */
	
	protected int minAgeInSimulationAtStart;
	public int getMinAgeInSimulationAtStart() {
		return minAgeInSimulationAtStart;
	}

	

	/**
	 * @return minimum age of the simulated population at the start of the simulation and can be lower than 0
	 */
	
	
	protected int minAgeInSimulation;
	/**
	 * @return minimum age of the simulated population
	 */
	
	public int getMinAgeInSimulation() {
		
		return minAgeInSimulation;
	}
	
	protected boolean withNewborns;


	
	public boolean isWithNewborns() {
	
		return  withNewborns;
	}

	protected int nDim;
	public int getNDim() {
	
		return nDim;
	}
	protected int nRiskFactorClasses;
	public int getNRiskFactorClasses() {
		
		return nRiskFactorClasses;
	}

	protected String[] riskClassnames;
	/**
	 * @return names of riskclasses
	 */
	public String[] getRiskClassnames() {
		
		return riskClassnames;
	}
	

	/* getters of arrays with data */
	
	/**
	 * average value of risk class by Age; indexes are: scenario time risk
	 * class, age, and sex
	 * 
	 * 
	 */
	protected double[][][][][] meanRiskByRiskClassByAge;
	
	public synchronized  double getMeanRiskByRiskClassByAge(int scen, int step, int r, int a, int g) {
		return meanRiskByRiskClassByAge[scen][step][r][a][g];
	}

	/**
	 * average value of risk class by the age at the start of simulation within
	 * a category of the riskclass; indexes are: scenario time risk class, age,
	 * and sex
	 * 
	 * 
	 */
	protected double[][][][][] meanRiskByOriRiskClassByOriAge;
	
	
	public synchronized double getMeanRiskByOriRiskClassByOriAge(int scen, int step, int r, int a, int g) {
		return meanRiskByOriRiskClassByOriAge[scen][step][r][a][g];
	}

	/**
	 * number in risk class by Age; indexes are: scenario, time risk class age
	 * and sex
	 */
	protected double[][][][][] nPopByRiskClassByAge;
	/**
	 * number in risk class by Age; indexes are: scenario, time ,risk class ,age
	 * and sex
	 */
	
	protected double[][][][][] nPopByOriRiskClassByOriAge;

	public boolean categorized;


	/**************** detailed disease information ****************/
	/* these are used only for writing to excel readable XML file */
	
	/**
	 * number of survivors in each diseaseState by scenario, time, disease
	 * state, risk class, age and sex
	 */
	protected double nDiseaseStateByRiskClassByAge[][][][][][];
	public synchronized double getNDiseaseStateByRiskClassByAge(int scen, int step,int s, int r, int a, int g) {
		return nDiseaseStateByRiskClassByAge[scen][step][s][r][a][g];
	}

	/**
	 * number of survivors in scenario and each diseaseState. Indexes: scenario,
	 * time, diseaseState, riskclass at beginning of simulation, age at
	 * beginning of simulation and sex
	 * 
	 */

	protected double nDiseaseStateByOriRiskClassByOriAge[][][][][][];

	public double[][][][][][] getNDiseaseStateByOriRiskClassByOriAge() {
		return nDiseaseStateByOriRiskClassByOriAge;
	}

	public int nDiseaseStates;
	
	double[][][][][] makeDiseaseArray(double[][][][][][] stateArray,
			int disease){
		double[][][][][] returnarray = null;
		return returnarray;}
	

	
	public int getNDiseaseStates() {
		return nDiseaseStates;
	}

	public double getNDiseaseStateByOriRiskClassByOriAge(int scen, int step,int s, int r, int a, int g) {
		return nDiseaseStateByOriRiskClassByOriAge[scen][step][s][r][a][g];
	}
	
	
	public double [][][][][][]getNDiseasesByOriRiskClassByOriAge() {
		return null;
	}
/* ******************  POPULATION NUMBERS  ************************************************************/
	/*  methods returning population number information */
	
	
	/**
	 * @return number in population by scenario, year, age and gender
	 * dimensions: scenario, year , age, gender
	 */
	public double[][][][] getNPopByAge() {
		
			double [][][][] nPopByAge=new double[this.getNScen() + 1][this.getStepsInRun()+1][96][2];
			for (int scen = 0; scen < this.getNScen(); scen++)
				for (int year = 0; year < this.getStepsInRun(); year++)
					for (int a = 0; a < 96; a++)
						for (int s = 0; s < 2; s++)
							for (int r = 0;r<this.getNRiskFactorClasses(); r++)
							nPopByAge[scen][year][a][s]+=this.getNPopByRiskClassByAge()[scen][year][r][a][s];
			
			return nPopByAge;
		}
	
	/**
	 * @return number in population by age in year year
	 * dimensions: scenario, age, gender
	 */
	
	public double[][][] getNPopByAge(int year) {
		double [][][] nPopByAge=new double[this.getNScen() + 1][96][2];
		for (int scen = 0; scen < this.getNScen(); scen++)
				for (int a = 0; a < 96; a++)
					for (int s = 0; s < 2; s++)
						for (int r = 0;r<this.getNRiskFactorClasses(); r++)
						nPopByAge[scen][a][s]+=this.getNPopByRiskClassByAge()[scen][year][r][a][s];
		
		return nPopByAge;
	}
	/**
	 * @return number in population by riskclass and age
	 */
	public double[][][][][] getNPopByRiskClassByAge() {
		return this.nPopByRiskClassByAge;
	}
	/**
	 * @return number in population by age in riskFactorClass riskClass
	 * dimensions scenario, year, age, gender
	 */

	public double[][][][] getNPopByAgeForRiskclass(int riskClass) {
		double[][][][] nPopByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int stepcount = 0; stepcount < this.stepsInRun + 1; stepcount++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nPopByAge[scen][stepcount][a][g] += this.nPopByRiskClassByAge[scen][stepcount][riskClass][a][g];
		return nPopByAge;
	}
	public double[][][][][] getNTotalDiseaseByRiskClassByAge() {

		double[][][][][] NDisease = new double[this.nScen + 1][this.stepsInRun + 1][nRiskFactorClasses][96 + this.stepsInRun][2];
		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
							NDisease[scen][stepCount][r][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
									* pTotalDiseaseByRiskClassByAge[scen][stepCount][r][a][g];

		return NDisease;
	}

	public double[][][][][] getNPopByOriRiskClassByOriAge() {
		
		return this.nPopByOriRiskClassByOriAge;
	}

	public double[][][] getNTotFutureDiseaseByOriAge(int age) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[][][][] getNIncidenceByAge(int d) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[][][][] getIncidenceByRiskClassByAge(int year) {
		// TODO Auto-generated method stub
		return null;
	}
	public double[][][][][] getNewCasesByRiskClassByAge(int d) {

			return null;
	}public double[][][][] getNewCasesByRiskClassByAge(int d, int year) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[][][][] getNewCasesByAge(int d) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[][][] getNewCasesByAge(int d, int year) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	
	
	public double[][][] getPopDALY() {
		return popDALY;
	}

	public void setPopDALY(double[][][] popDALY) {
		this.popDALY = popDALY;
	}

	public double[][][] getTotDiseaseDALY() {
		return totDiseaseDALY;
	}

	public void setTotDiseaseDALY(double[][][] totDiseaseDALY) {
		this.totDiseaseDALY = totDiseaseDALY;
	}

	public double[][][][] getDiseaseStateDALY() {
		return diseaseStateDALY;
	}

	public void setDiseaseStateDALY(double[][][][] diseaseStateDALY) {
		this.diseaseStateDALY = diseaseStateDALY;
	}

	public double[][][] getDisabilityDALY() {
		return disabilityDALY;
	}

	public void setDisabilityDALY(double[][][] disabilityDALY) {
		this.disabilityDALY = disabilityDALY;
	}

	protected double[][][] popDALY;

	protected double[][][] totDiseaseDALY;
 /* indexes: scenario, diseasestate, age, gender */
	protected double[][][][] diseaseStateDALY;
	
	protected double[][][][] diseaseDALY;

	protected double[][][] disabilityDALY;
	public boolean[] scenTrans;

	public boolean[] getScenTrans() {
		return scenTrans;
	}

	public double[][][] getDiseaseDALY(int disease){
		return null;
	}
//	public double getMortalityByOriRiskClassByOriAge(int scen, int step, int r, int a, int g) {
//		return mortalityByOriRiskClassByOriAge[scen][step][r][a][g]=value;
//	}
	public synchronized void setMaxAgeInSimulation(int maxAgeInSimulation) {
		this.maxAgeInSimulation = maxAgeInSimulation;
	}

	public synchronized void setMinAgeInSimulation(int minAgeInSimulation) {
		this.minAgeInSimulation = minAgeInSimulation;
	}

	public synchronized void setPDisabilityByRiskClassByAge(int scen, int step, int r, int a, int g,double value
			) {
		pDisabilityByRiskClassByAge [scen][step][r][a][g]=value;
	}

	public synchronized  void setPTotalDiseaseByRiskClassByAge(int scen, int step, int r, int a, int g,double value
		) {
		pTotalDiseaseByRiskClassByAge [scen][step][r][a][g]=value;
	}

	public  synchronized  void  setMortalityByRiskClassByAge(int scen, int step, int r, int a, int g,double value
			) {
		this.mortalityByRiskClassByAge [scen][step][r][a][g]=value;
	}

	public synchronized void setPDisabilityByOriRiskClassByOriAge(int scen, int step, int r, int a, int g,double value
			) {
		pDisabilityByOriRiskClassByOriAge [scen][step][r][a][g]=value;
	}

	public synchronized void setPTotalDiseaseByOriRiskClassByOriAge(int scen, int step, int r, int a, int g,double value
			) {
		pTotalDiseaseByOriRiskClassByOriAge [scen][step][r][a][g]=value;
	}

	public synchronized void setMortalityByOriRiskClassByOriAge(int scen, int step, int r, int a, int g,double value
			) {
		this.mortalityByOriRiskClassByOriAge [scen][step][r][a][g]=value;
	}

	public synchronized void setMeanRiskByRiskClassByAge(int scen, int step, int r, int a, int g,double value
		) {
		this.meanRiskByRiskClassByAge [scen][step][r][a][g]=value;
	}

	public synchronized void setMeanRiskByOriRiskClassByOriAge(int scen, int step, int r, int a, int g,double value
			) {
		this.meanRiskByOriRiskClassByOriAge [scen][step][r][a][g]=value;
	}

	public double[][][] getNDiseaseByAgeInYear(int disease, int year) {
		// TODO Auto-generated method stub
		return null;
	}



	public double[][][][] getNDisabledByOriRiskClassByOriAge(int ageAtStart) {
		// TODO Auto-generated method stub
		return null;
	}



	public double[][][][] getNTotalDiseaseByOriRiskClassByOriAge(int ageAtStart) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * get the array of diseaseNumbers by age at start of simulation and
	 * riskclass at start of simulation for disease d.
	 * 
	 * @param d
	 *            : diseasenumber of disease to return
	 * @return: array with diseaseNumbers. indexes scenario, year of follow-up,
	 *          risk factorclass, age, sex
	 */
	
}
	
	

	/* **************   mortality methods ****************************************/	

	/* not present as mortality is calculated from population data in the plot object */
	
	
	/* **************   riskfactor methods ****************************************/
//	public double[][][][] getMeanRiskByAge() {
//		// TODO Auto-generated method stub
//		return null;
//	}
/* **************   disease methods ****************************************/
//	public double[][][] getNDiseaseByAge(int d, int year) {
		// TODO Auto-generated method stub
//		return null;
//	}
//	public double[][][][] getNDiseaseByAge(int d) {
		// TODO Auto-generated method stub
//		return null;
		
//	}
//		public double[][][] getNDiseaseByOriAge(int age, int i) {
			// TODO Auto-generated method stub
//			return null;
//		}
	
	//public double[][][][][] getNDiseaseByRiskClassByAge(int d) {
		// TODO Auto-generated method stub
		//return null;
//	}
//	public double[][][][] getNDiseaseByRiskClassByAge(int d, int steps) {
		// TODO Auto-generated method stub
//		return null;
		
		
	//	}
	
	/*  ************************ disability methods************************/
	
	
	//public double[][][][] getNDisabledByAge() {
		// TODO Auto-generated method stub
		//return null;
//	}

	//public double[][][] getNDisabledByAge(int year) {
		// TODO Auto-generated method stub
		//return null;
//	}

	

//	public double[][][][] getNDisabledByRiskClassByAge(int steps) {
		// TODO Auto-generated method stub
	//	return null;
//	}

//	public double[][][][][] getNDisabledByRiskClassByAge() {
		// TODO Auto-generated method stub
	//	return null;
//	}

	

//	public double[][][] getNDisabledByOriAge(int age) {
		// TODO Auto-generated method stub
	//	return null;
//	}

	


	


