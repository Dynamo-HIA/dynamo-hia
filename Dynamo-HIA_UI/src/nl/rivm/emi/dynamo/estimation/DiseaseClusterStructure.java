package nl.rivm.emi.dynamo.estimation;

import java.util.ArrayList;


/**
 * @author Hendriek Boshuizen
 * 
 */


public class DiseaseClusterStructure {

	/**
	 * DiseaseClusterStructure contains general info on the disease structure
	 * within a cluster
	 * 
	 */

	/**
	 * @dependentDisease (boolean[]) indicates whether a disease is a dependent
	 *                   disease or not
	 */
	private boolean[] dependentDisease;
	
	

	private int[] diseaseNumber; /*
						 * the overall disease number (in the entire model) of
						 * the diseases
						 */

	private ArrayList<String> diseaseName;
	private String clusterName;
	private boolean withCuredFraction=false;
	private int nInCluster = 1;
	
	private int[] indexDependentDiseases;
	/**
	 * @indexDependentDiseases Index giving the numbers of the dependent
	 *                         diseases within this cluster
	 * 
	 */
	private int[] indexIndependentDiseases;
	/**
	 * @indexIndependentDiseases Index giving the numbers of the independent
	 *                           diseases within this cluster
	 * 
	 */

	/**
	 * @NDep number of dependent diseases within this cluster
	 * @NIndep number of independent diseases within this cluster
	 * 
	 */

	private int NDep; /* number of dependent diseases within this cluster */
	private int NIndep;/* number of independent diseases within this cluster */

	/**
	 * @param clusterName
	 *            : Name of the disease structure
	 * @param startN
	 *            : number of the first disease of this cluster within the total
	 *            number of diseases
	 * @param N
	 *            : number of diseases within this cluster
	 * @param diseaseNames
	 *            : names of the diseases within the cluster
	 * @param NRIndependent
	 *            : number of independent diseases within this cluster
	 * 
	 */
	public DiseaseClusterStructure(String clusterName, int startN, int N,
			String[] diseaseNames, int[] NRIndependent) {
		this.clusterName = clusterName;
		diseaseName = new ArrayList<String>();
		for (String names: diseaseNames){this.diseaseName.add(names);};
		diseaseNumber = new int[N];
		dependentDisease = new boolean[N];
		NIndep = NRIndependent.length;
		NDep = N - NIndep;
		

		int dd = 0;// dd contains number of current dependent disease;
		int di = 0;// di contains number of current independent disease;

		this.setNInCluster(N);
		
		for (int d = 0; d < N; d++) /*
									 * go through all diseases and if disease
									 * number is not in the array with
									 * independent diseases it should go in the
									 * array with indexes of dependent diseases
									 */
		{
			if (NRIndependent[di] == d) {
				dependentDisease[d] = false;
			    if (di<NRIndependent.length-1) di++;/* if di indicates the last element the array
			    gets out of bounds. In this case keep the last value, as d will increase, and things will go OK */
			} else {
				
				dependentDisease[d] = true;
				
			}
			diseaseNumber[d] = startN + d;

		}
		
		/*
		 * make an index for the numbers of dependent and
		 * independent diseases within the cluster
		 */
		setIndexDependentDiseases(new int[NDep]);
		int iterDep = 0;
		int iterIndep = 0;
		setIndexIndependentDiseases(new int[NIndep]);
		for (int dtemp = 0; dtemp < getNInCluster(); dtemp++) {
			if (dependentDisease[dtemp]) {
				getIndexDependentDiseases()[iterDep] = dtemp;
				iterDep++;
			} else {
				getIndexIndependentDiseases()[iterIndep] = dtemp;
				iterIndep++;
			}
		}

	}

	/**
	 * @param diseaseName
	 *            : Name of the disease structure
	 * @param startN
	 *            : number of the first disease of this cluster within the total
	 *            number of diseases
	 */

	public DiseaseClusterStructure(String dName, int startN) {
		clusterName = dName;

		dependentDisease = new boolean[1];
		dependentDisease[0] = false;
		diseaseName = new ArrayList<String>();
		addDiseaseName(dName);
		diseaseNumber = new int[1];
		diseaseNumber[0] = startN;
		NDep = 0;
		NIndep = 1;
		setNInCluster(1);
		setIndexDependentDiseases(new int[0]);
		setIndexIndependentDiseases(new int[1]);
		getIndexIndependentDiseases()[0]=0;
	}

	public boolean[] getDependentDisease() {
		return dependentDisease;
	}

	public void setDependentDisease(boolean[] dep) {
		dependentDisease = dep;
	}

	public int[] getDiseaseNumber() {
		return diseaseNumber;
	}

	public void setDiseaseNumber(int[] diseaseNr) {
		diseaseNumber = diseaseNr;
	}

	public ArrayList<String> getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String[] nameArray) {
		diseaseName=null;
		
		for (String name:nameArray){diseaseName.add(name);};
	}
	
	public void addDiseaseName(ArrayList<String> newNames){
		diseaseName.addAll(newNames);
		
	};

	public void addDiseaseName(String newName){
		diseaseName.add(newName);
		
	};
	
	

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		clusterName = clusterName;
	}

	public boolean isWithCuredFraction() {
		return withCuredFraction;
	}

	public void setWithCuredFraction(boolean withCuredFraction) {
		this.withCuredFraction = withCuredFraction;
	}

	

	

	
	public int getNDep() {
		return NDep;
	}

	public void setNDep(int dep) {
		NDep = dep;
	}

	public int getNIndep() {
		return NIndep;
	}

	public void setNIndep(int indep) {
		NIndep = indep;
	}

	public void setNInCluster(int nInCluster) {
		this.nInCluster = nInCluster;
	}

	public int getNInCluster() {
		return nInCluster;
	}

	public void setIndexIndependentDiseases(int[] indexIndependentDiseases) {
		this.indexIndependentDiseases = indexIndependentDiseases;
	}

	public int[] getIndexIndependentDiseases() {
		return indexIndependentDiseases;
	}

	/**
	 * @param i
	 * @param d
	 */
	public void setDiseaseNumber(int i, int d) {
		diseaseNumber[d]=i;
		
	}

	public void setIndexDependentDiseases(int[] indexDependentDiseases) {
		this.indexDependentDiseases = indexDependentDiseases;
	}

	public int[] getIndexDependentDiseases() {
		return indexDependentDiseases;
	}

	/**
	 * @param string
	 * @param i
	 */
	public void setDiseaseName(String string, int i) {
		diseaseName.set(i,string);
		
	}


}
