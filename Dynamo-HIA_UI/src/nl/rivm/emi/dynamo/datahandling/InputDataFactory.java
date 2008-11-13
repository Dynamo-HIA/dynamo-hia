package nl.rivm.emi.dynamo.datahandling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.estimation.DiseaseClusterData;
import nl.rivm.emi.cdm.estimation.DiseaseClusterStructure;
import nl.rivm.emi.cdm.estimation.DynamoConfigurationData;
import nl.rivm.emi.cdm.estimation.DynamoSimulationConfiguration;
import nl.rivm.emi.cdm.estimation.InconsistentDataException;
import nl.rivm.emi.cdm.estimation.InputData;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.dynamo.data.types.functional.DynamoSimulationConfiguration.DiseaseConfigurationData;
import nl.rivm.emi.dynamo.datahandling.DynamoSimulationConfiguration.RelativeRiskConfigurationData;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * @author boshuizh
 *
 */
public  class InputDataFactory {
	
	
	private int conversionTable [];
	/* the conversion table gives for the old number of the disease (as in the user input)
	 * the number of the disease as used in the parameter estimation;
	 * In the simulation, we use again another numbering, as age, sex and risk factor info are added at the 
	 * beginning of the numbering, and also some diseases are split in a cured and not cured part and for
	 * clusters combinations of diseases are numbered in stead of the diseases; 
	 */

	

	public static void addMortalityToInputData(InputData inputData, DynamoConfigurationData W01 ){
		
		String popFileName = W01.getPopulationFileName();
		
		HierarchicalConfiguration w12=new XMLConfiguration(popFileName+"overallmortality.xml");
	
		inputData.mortTot=w12.getAgeSexArray("value");// value is the tagname
		

		
		
	
	};
		
	

	public static void addRiskFactorInfoToInputData(InputData inputData,DynamoConfigurationData W01){
		// add risktype
		
		String riskFileName = W01.getRiskFactor().prevalenceFileName;
		inputData.riskFactorName=getName(W01,"riskFactorName");
		HierarchicalConfiguration w20=new XMLConfiguration(riskFileName+"configuration.xml");
		String riskTypeString =  W01.getRiskFactor().getType();
		if (riskTypeString=="categorical") 	inputData.setRiskType(1);
		if (riskTypeString=="continuous") 	inputData.setRiskType(2);
		if (riskTypeString=="compound") 	inputData.setRiskType(3);
		int riskType=inputData.getRiskType();
		
		
		if (riskType==1){
			inputData.setPrevRisk( W01....getPrevalenceData);
			inputData.setTransitionMatrix( W01....getPrevalenceData);
			
			// TODO: add to inputData
		}
			
		if (riskType==2){
			
			inputData.setRefClassCont(W01.getRiskFactor().getClass()refClassCont);
			inputData.setMeanRisk( W01....getPrevalenceData);
			inputData.setStdRisk( W01....getPrevalenceData);
			inputData.setSkewRisk( W01....getPrevalenceData);
			inputData.setRiskDistribution( W01....getTypeDistribution);
		}
		
		if (riskType==3){
			inputData.setTransitionMatrix( W01....getPrevalenceData);
			
		inputData.setPrevRisk( W01....getPrevalenceData);
		inputData.setIndexDuurClass( W01....getDurationClass);
		inputData.setDuurFreq(W01....getDurationFreq);
		
		
		}
	
}

protected class DiseaseData{
	
	String name;
	boolean causal=false;
	boolean dependent=false;
	protected DiseaseData(String name,boolean causal){
		setName(name);
		setCausal(causal);
	};
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isCausal() {
		return causal;
	}
	public void setCausal(boolean causal) {
		causal = causal;
	}
	public boolean isDependent() {
		return dependent;
	}
	public void setDependent(boolean dependent) {
		this.dependent = dependent;
	}
	
};



public static void addDiseaseAndRRInfoToInputData(InputData inputData, DynamoConfigurationData W01){
	
	
	int riskType=inputData.getRiskType();
	// loop through diseases
	
	
	ArrayList<DiseaseData> diseasesInfo;
	ArrayList<DiseaseConfigurationData> diseases=W01.getDiseases();
	 Iterator<DiseaseConfigurationData> diseaseIterator = diseases.iterator();
	 while (diseaseIterator.hasNext())
	diseasesInfo.add(new DiseaseData(diseases.getName(),false));
	
	 ArrayList<RelativeRiskConfigurationData> relativeRisks=W01.getRelativeRisks();
	 Iterator<RelativeRiskConfigurationData> RRIterator = relativeRisks.iterator();
	 while (RRIterator.hasNext()) {
		 RelativeRiskConfigurationData RR = RRIterator.next();
		 String CausalDiseaseName=RR.getFrom();
		 String DependentDiseaseName=RR.getTo();
/*		 Find the name in diseaseInfo and set to causal or dependent */
		 
		 Iterator it = diseasesInfo.iterator();
		 while (it.hasNext())
		 {DiseaseData info=(DiseaseData)it.next();{
			 if (CausalDiseaseName==info.getName())
				 info.setCausal(true);
			 if (DependentDiseaseName==info.getName())
 				 info.setDependent(true);
		  
		 }}
		 
		

		 
		 
		 
	 }
	 
	
				
	/* extract disease structure 
	 * NB cancers must be split up in two diseases but this will be done after parameter estimation 
	 * 
	 * identification of diseases throughout is through their name
	 * as the order is changed by creating clusters
	 */
	 
	 /* put data into arrays for better random access and index manipulation */
	int nDiseases=diseasesInfo.size();
	boolean[] causalDisease = new boolean[nDiseases];
	boolean[] dependentDisease = new boolean[nDiseases];
	String[] diseaseName = new String[nDiseases];
	Iterator it = diseasesInfo.iterator();
	int i=0;
	 while (it.hasNext())
	 {DiseaseData info=(DiseaseData)it.next();
		 if (info.isCausal()) causalDisease[i]=true; else causalDisease[i]=false;
		 if (info.isDependent()) dependentDisease[i]=true; else dependentDisease[i]=false;
		 diseaseName[i]=info.getName();
		// check if not both causal and dependent disease
		if (causalDisease[i] && dependentDisease[i])
					throw new InconsistentDataException(
							"Disease "
									+ diseaseName[i]
									+ " is both a cause of another disease and is caused itself by another disease. This is not allowed. Please change this");
			
		 
		i++;	 
	  
	 }
	
	

	ArrayList<DiseaseClusterStructure> clusterInfo;
	
	for (int d = 0; d < nDiseases; d++)
		clusterInfo.add(new DiseaseClusterStructure(diseaseName[d],d));
	// check which diseases are causal or dependent (=RR present);
	
	
	// now determine clusters ;

	int clusterSum = 0;
	int prevClusterSum = 10000;
	int niter = 0;
	
	while (clusterSum != prevClusterSum && niter <= nDiseases) {
		prevClusterSum = clusterInfo.size();
	/* loop through all pairs of diseases and see if there is a RR from d1 to d2;*/	
		for (int d1 = 0; d1 < nDiseases; d1++) {
			for (int d2 = 0; d2 < nDiseases; d2++) {
			Iterator<RelativeRiskConfigurationData> RRIterator2 = relativeRisks.iterator();
			   while (RRIterator2.hasNext()) {
				 RelativeRiskConfigurationData RR = RRIterator2.next();
				  if (RR.getFrom() == diseaseName[d1] && RR.getTo() == diseaseName[d2]){
		/* now give dependent and causal disease the  same  (lowest) cluster number; 
		look through the list of diseases, and find the first disease of the two in the list
							 */
					  int numberD1=-1;
					  int numberD2=-1;
			/* find the numbers of the clusters where both diseases are */		  
			for (int c=0;c<clusterInfo.size();c++){
			ArrayList<String> currentNames=clusterInfo.get(c).getDiseaseName();
			for (int d=0;d<currentNames.size();d++){
				if (currentNames.get(d)==diseaseName[d1] ) numberD1=c;
				if (currentNames.get(d)==diseaseName[d2] ) numberD2=c;
			}}
			if (numberD1>numberD2){
				/* combine D1 and D2 in D2 remove D1 */
				DiseaseClusterStructure cluster1 = clusterInfo.get(numberD1);
				DiseaseClusterStructure cluster2 = clusterInfo.get(numberD2);
				cluster2.addDiseaseName(cluster1.getDiseaseName());
				cluster2.setNinCluster(cluster1.getNinCluster()+cluster2.getNinCluster());
				clusterInfo.set(numberD2,cluster2);
				clusterInfo.remove(numberD1);}
			if (numberD2>numberD1){
				/* combine D1 and D2 in D2 remove D1 */
				DiseaseClusterStructure cluster1 = clusterInfo.get(numberD1);
				DiseaseClusterStructure cluster2 = clusterInfo.get(numberD2);
				cluster1.addDiseaseName(cluster2.getDiseaseName());
				cluster1.setNinCluster(cluster1.getNinCluster()+cluster2.getNinCluster());
				clusterInfo.set(numberD1,cluster1);
				clusterInfo.remove(numberD2);}
			
				  }}}}}
	
			
				
				
				
				
	;
	// TODO relative risks should be added to diseaseClusterData 
	/* now make diseasecluster structure and diseasecluster data */
	int nClusters=clusterInfo.size();
	DiseaseClusterData [][][] clusterData=new DiseaseClusterData[96][2][nClusters];
	/* count number of diseases and number of independent (=causal) diseases in each cluster
	 *  and make RRextended 
	 *  */
	int dStart=0;
	for (int c=0;c<nClusters;c++){
		
		DiseaseClusterStructure cluster = clusterInfo.get(c); 
		int [] diseaseNumbers=new int [cluster.getNinCluster()];
		diseaseNumbers[0]=dStart;
		dStart++;
		if (cluster.getNinCluster()>1) {
			boolean[] dependent= new boolean[cluster.getNinCluster()];
			int nIndep=0;
			int nDep=0;
			for (int d=0;d<cluster.getNinCluster();d++){
				int index=getDiseaseIndex(diseaseName, cluster.getDiseaseName().get(d));
				diseaseNumbers[d]=dStart;
				dStart++;
			if (diseasesInfo.get(index).isCausal()){ nIndep++;dependent[d]=false;}
			else {nDep++;dependent[d]=true;};
			
			}
			cluster.setNIndep(nIndep);
			cluster.setNDep(nDep);
			cluster.setDependentDisease(dependentDisease);
			cluster.setDiseaseNumber(diseaseNumbers);
			}else {diseaseNumbers[0]=dStart;
			dStart++;} 
		}
	
	
	for (int c=0;c<nClusters;c++){
		DiseaseClusterStructure cluster = clusterInfo.get(c); 
		if (cluster.getNinCluster()==1) {
			int index=getDiseaseIndex(diseaseName, cluster.getDiseaseName().get(0));
			float [][]prevalence=W01.getDiseases(index).getPrevalence();
			float [][]incidence=W01.getDiseases(index).getIncidence();
			float [][]excessMort=W01.getDiseases(index).getExcessMortality();
			
			for (int age=0;age<96;age++) for (int g=0;g<2;g++)
			{
			clusterData[age][g][c].setIncidence(incidence[age][g]);
			clusterData[age][g][c].setPrevalence(prevalence[age][g]);
			clusterData[age][g][c].setRRdisExtended(1.0F);
			clusterData[age][g][c].setExcessMortality(excessMort[age][g]);
			continue; // no further action needed;
		}}
		for (int age=0;age<96;age++) for (int g=0;g<2;g++){
		float [][] RRdis=new float[cluster.getNinCluster()][cluster.getNinCluster()];
		float [] prevalence=new float [cluster.getNinCluster()];
		float [] incidence=new float [cluster.getNinCluster()];
		float [] excessMort=new float [cluster.getNinCluster()];
		for (int d1=0;d1<cluster.getNinCluster();d1++){
			int index=getDiseaseIndex(diseaseName, cluster.getDiseaseName().get(d1));
			prevalence[d1]=W01.getDiseases(index).getPrevalence()[age][g];
			incidence[d1]=W01.getDiseases(index).getIncidence()[age][g];
			excessMort[d1]=W01.getDiseases(index).getExcessMortality()[age][g];
			
			
			if (!diseasesInfo.get(index).isCausal())
			for (int d2=0;d2<cluster.getNinCluster();d2++) clusterData[age][g][c].setRRdisExtended(1.0F,d1,d2);
			else for (int d2=0;d2<cluster.getNinCluster();d2++){
				int index2=getDiseaseIndex(diseaseName, cluster.getDiseaseName().get(d2));
				Iterator<RelativeRiskConfigurationData> RRIterator2 = relativeRisks.iterator();
				 while (RRIterator2.hasNext()) {
					 RelativeRiskConfigurationData RR = RRIterator.next();
					 if(RR.getFrom()==cluster.getDiseaseName().get(d1)&&
					      RR.getTo()==cluster.getDiseaseName().get(d2))	
					 {RR.getDataFileName(); //... haal hieruit [age][sex]RR waarde en stop die in
						 // RRdis[d1][d2]
						 
					 }
					 else RRdis[d1][d2]=1.0F;
				 } //end loop over all rr's
				 
				 
				 }}
// TODO also add cured fraction + caseFatality
			clusterData[age][g][c].setRRdisExtended(RRdis);
			clusterData[age][g][c].setIncidence(incidence);
		    clusterData[age][g][c].setPrevalence(prevalence);
		    clusterData[age][g][c].setExcessMortality(excessMort);
	}
	}
		
	

	
	return this;
}
	
	
	
		
		

	
	
	
		


public int getDiseaseIndex(String [] array, String name){
	int returnValue=0;
	for (returnValue=0;returnValue<array.length;returnValue++) if (name==array[returnValue]) break;
	return returnValue;
}



public static void addDiseaseAndRRInfoToInputData(InputData inputData,
		DynamoConfigurationData config) {
	// TODO Auto-generated method stub
	
};

}





