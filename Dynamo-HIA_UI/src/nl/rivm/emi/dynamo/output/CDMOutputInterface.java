package nl.rivm.emi.dynamo.output;

public interface CDMOutputInterface {
	
	
	
	
	public int getStepsInRun() ;
		
	public int getNScen() ;

	public int getRiskType(); 
	
	public int getStartYear(); 
	
	public String[] getScenarioNames() ;

	public int getNDiseases();
	
	public int getNDiseaseStates();

	public int getMaxAgeInSimulation() ;

	public int getMinAgeInSimulation();
	
	public boolean isWithNewborns() ;
	
	public int getNDim() ;

	public int getNRiskFactorClasses() ;
	
	public String[] getRiskClassnames() ;

	public String[] getDiseaseNames() ;
	
	public String[] getStateNames() ;

	public double[][][][] getNDiseaseByAge(int d) ;

	public double[][][] getNDiseaseByAge(int d, int year);	

	public double[][][][] getNDiseaseByRiskClassByAge(int disease, int year) ;

	public double[][][][][] getNDiseaseByRiskClassByAge(int d) ;
	
	public double[][][][][][] getNDiseaseByRiskClassByAge() ;

	public double[][][] getNDiseaseByOriAge(int age, int disease);
	
	public double[][][][][] getNDiseaseByOriRiskClassByOriAge(int d);
	
	public double[][][][] getNDisabledByAge() ;

	public double[][][] getNDisabledByAge(int year) ;
	
	public double[][][] getNDisabledByOriAge(int age) ;
	
	public double[][][][] getNDisabledByRiskClassByAge(int year);

	public double[][][][][] getNDisabledByRiskClassByAge() ;

	public double[][][][] getNPopByAge() ;
	
	public double[][][] getNPopByAge(int year) ;
	
	public double[][][][][] getNPopByRiskClassByAge() ;
	
	public double[][][][] getNPopByAgeForRiskclass(int riskClass);

	public double[][][][][] getNPopByOriRiskClassByOriAge() ;
	
	public double[][][][] getMeanRiskByAge() ;
	
	public double[][][][][] getMeanRiskByRiskClassByAge();
	
	public double[][][][][] getMeanRiskByOriRiskClassByOriAge();
	
	

	

	


}
