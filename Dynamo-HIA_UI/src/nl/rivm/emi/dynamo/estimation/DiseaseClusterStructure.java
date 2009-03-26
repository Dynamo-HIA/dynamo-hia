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
		this.diseaseName = new ArrayList<String>();
		for (String names: diseaseNames){this.diseaseName.add(names);};
		this.diseaseNumber = new int[N];
		this.dependentDisease = new boolean[N];
		this.NIndep = NRIndependent.length;
		this.NDep = N - this.NIndep;
		

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
				this.dependentDisease[d] = false;
			    if (di<NRIndependent.length-1) di++;/* if di indicates the last element the array
			    gets out of bounds. In this case keep the last value, as d will increase, and things will go OK */
			} else {
				
				this.dependentDisease[d] = true;
				
			}
			this.diseaseNumber[d] = startN + d;

		}
		
		/*
		 * make an index for the numbers of dependent and
		 * independent diseases within the cluster
		 */
		setIndexDependentDiseases(new int[this.NDep]);
		int iterDep = 0;
		int iterIndep = 0;
		setIndexIndependentDiseases(new int[this.NIndep]);
		for (int dtemp = 0; dtemp < getNInCluster(); dtemp++) {
			if (this.dependentDisease[dtemp]) {
				getIndexDependentDiseases()[iterDep] = dtemp;
				iterDep++;
			} else {
				getIndexIndependentDiseases()[iterIndep] = dtemp;
				iterIndep++;
			}
		}

	}

	/**
	 * @param dName
	 *            : Name of the disease structure
	 * @param startN
	 *            : number of the first disease of this cluster within the total
	 *            number of diseases
	 */

	public DiseaseClusterStructure(String dName, int startN) {
		this.clusterName = dName;

		this.dependentDisease = new boolean[1];
		this.dependentDisease[0] = false;
		this.diseaseName = new ArrayList<String>();
		addDiseaseName(dName);
		this.diseaseNumber = new int[1];
		this.diseaseNumber[0] = startN;
		this.NDep = 0;
		this.NIndep = 1;
		setNInCluster(1);
		setIndexDependentDiseases(new int[0]);
		setIndexIndependentDiseases(new int[1]);
		getIndexIndependentDiseases()[0]=0;
	}

	/**
	 * @return array with booleans indicating whether the disease is a dependent disease
	 */
	public boolean[] getDependentDisease() {
		return this.dependentDisease;
	}

	/**
	 * @param dep
	 */
	public void setDependentDisease(boolean[] dep) {
		this.dependentDisease = dep;
	}

	/**
	 * @return array of diseaseNumbers; the numbering is over all diseases, not over only the diseases in this cluster
	 * diseaseNumber[0] gives the startnumber of the cluster 
	 */
	public int[] getDiseaseNumber() {
		return this.diseaseNumber;
	}

	/**
	 * @param diseaseNr
	 */
	public void setDiseaseNumber(int[] diseaseNr) {
		this.diseaseNumber = diseaseNr;
	}

	/**
	 * @return arraylist of the names of the diseases in the cluster
	 */
	public ArrayList<String> getDiseaseName() {
		return this.diseaseName;
	}

	/**
	 * @param nameArray
	 */
	public void setDiseaseName(String[] nameArray) {
		this.diseaseName=null;
		
		for (String name:nameArray){this.diseaseName.add(name);};
	}
	
	/**
	 * @param newNames
	 */
	public void addDiseaseName(ArrayList<String> newNames){
		this.diseaseName.addAll(newNames);
		
	};
	

	/**
	 * @param newName
	 */
	public void addDiseaseName(String newName){
		this.diseaseName.add(newName);
		
	};
	
	

	/**
	 * @return the name of the disease cluster
	 */
	public String getClusterName() {
		return this.clusterName;
	}

	/**
	 * @param clusterName
	 */
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	/**
	 * @return boolean indicating whether the disease cluster represents a disease with a cured fraction
	 */
	public boolean isWithCuredFraction() {
		return this.withCuredFraction;
	}

	/**
	 * @param withCuredFraction
	 */
	public void setWithCuredFraction(boolean withCuredFraction) {
		this.withCuredFraction = withCuredFraction;
	}

	

	

	
	/**
	 * @return number of dependent diseases in the cluster
	 */
	public int getNDep() {
		return this.NDep;
	}

	/**
	 * @param dep
	 */
	public void setNDep(int dep) {
		this.NDep = dep;
	}

	/**
	 * @return number of independent diseases in this cluster
	 */
	public int getNIndep() {
		return this.NIndep;
	}

	/**
	 * @param indep
	 */
	public void setNIndep(int indep) {
		this.NIndep = indep;
	}

	/**
	 * @param nInCluster
	 */
	public void setNInCluster(int nInCluster) {
		this.nInCluster = nInCluster;
	}

	/**
	 * @return number of diseases in this cluster
	 */
	public int getNInCluster() {
		return this.nInCluster;
	}

	/**
	 * @param indexIndependentDiseases
	 */
	public void setIndexIndependentDiseases(int[] indexIndependentDiseases) {
		this.indexIndependentDiseases = indexIndependentDiseases;
	}

	/**
	 * @return array with the index numbers of the independent diseases in this cluster
	 */
	public int[] getIndexIndependentDiseases() {
		return this.indexIndependentDiseases;
	}

	/**
	 * @param i
	 * @param d
	 */
	public void setDiseaseNumber(int i, int d) {
		this.diseaseNumber[d]=i;
		
	}

	/**
	 * @param indexDependentDiseases
	 */
	public void setIndexDependentDiseases(int[] indexDependentDiseases) {
		this.indexDependentDiseases = indexDependentDiseases;
	}

	/**
	 * @return array with the index numbers of the dependent diseases in this cluster
	 */
	public int[] getIndexDependentDiseases() {
		return this.indexDependentDiseases;
	}

	/**
	 * @param string
	 * @param i
	 */
	public void setDiseaseName(String string, int i) {
		if (i<this.diseaseName.size())
		this.diseaseName.set(i,string);
		else this.diseaseName.add(i,string);
		
	}

	/**
	 * @param b
	 * @param i
	 */
	public void setDependentDisease(boolean b, int i) {
		this.dependentDisease[i]=b;
		
	}

	/**set indexIndependentDiseases[j] to i
	 * @param i
	 * @param j
	 */
	public void setIndexIndependentDiseases(int i, int j) {
		this.indexIndependentDiseases[j]=i;
		
	}


}
