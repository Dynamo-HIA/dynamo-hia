package nl.rivm.emi.dynamo.estimation;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.rules.update.UpdateRules4SimulationFromXMLFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationDataW01 {
	

	
	
		static private Log log = LogFactory.getLog("nl.rivm.emi.dynamo.estimation.ConfigurationFromXMLFactory");

		/**
		 * Currently implemented structure:
		 * 
		 * <?xml version="1.0" encoding="UTF-8"?> <newborns>no</newborns>
		 * <startingYear>2008</startingYear> <numberOfYears>10</numberOfYears>
		 * <simPopSize> 1000</simPopSize> <minAge> 0</minAge> <maxAge>100
		 * </maxAge> <timeStep>1 </timeStep> <randomSeed>12345 </randomSeed>
		 * <resultType>aggregated</resultType> <popFileName>NL-2008
		 * </popFileName> <scenarios> <scenario label="1"> <scenName>scenario 1
		 * </scenName> <successRate> 100 </successRate> <targetMinAge>0</targetMinAge><targetMaxAge>100</targetMaxAge>
		 * <targetGender> 2</targetGender> <alternativeTransFile>none</alternativeTransFile>
		 * <alternativePrevFile>Prev-scen1 </alternativePrevFile> </scenario>
		 * <scenario label="2"> <scenName>scenario 2 </scenName> <successRate>
		 * 50 </successRate> <targetMinAge>0</targetMinAge><targetMaxAge>100</targetMaxAge>
		 * <targetGender> 2</targetGender> <alternativeTransFile>none</alternativeTransFile>
		 * <alternativePrevFile>Prev-scen1 </alternativePrevFile> </scenario>
		 * </scenarios> <diseases> <disease label="1"> <diseaseName> lung cancer</diseaseName>
		 * <diseasePrevFile>lung cancer-prev-NL</diseasePrevFile>
		 * <diseaseIncFile>lung cancer-inc-NL</diseaseIncFile>
		 * <diseaseExcessMortFile>lungcancer-excess-mort</diseaseExcessMortFile>
		 * <diseaseDalyWeights>lungcancer-Daly weights </diseaseDalyWeights>
		 * </disease > <disease label="2"> <diseaseName> HVD</diseaseName>
		 * <diseasePrevFile>HVD-prev-NL</diseasePrevFile>
		 * <diseaseIncFile>HVD-inc-NL</diseaseIncFile>
		 * <diseaseExcessMortFile>HVD-excess-mort</diseaseExcessMortFile>
		 * <diseaseDalyWeights>HVD-Daly weights </diseaseDalyWeights> </disease >
		 * <disease label="3"> <diseaseName> diabetes</diseaseName>
		 * <diseasePrevFile>diabetes-prev-NL</diseasePrevFile>
		 * <diseaseIncFile>diabetes-inc-NL</diseaseIncFile>
		 * <diseaseExcessMortFile>diabetes-excess-mort</diseaseExcessMortFile>
		 * <diseaseDalyWeights>diabetes-Daly weights </diseaseDalyWeights>
		 * </disease > </diseases> <riskfactor> <riskFactorName>BMI
		 * </riskFactorName> <riskFactorType>compound</riskFactorType>
		 * <riskFactorPrevFile>BMI-NL</riskFactorPrevFile>;
		 * <riskFactorTransFile> </riskFactorTransFile>
		 * <riskFactorRRforDeathFile></riskFactorRRforDeathFile>; </riskfactor>
		 * <RRs> <RR label="1">
		 * 
		 * <isRRfrom>CVD</isRRfrom> <isRRto>lung cancer</isRRto>
		 * <isRRFile>RR-CVD-lung cancer</isRRFile> </RR > <RR label="2">
		 * 
		 * <isRRfrom>diabetes</isRRfrom> <isRRto>CVD</isRRto>
		 * <isRRFile>RR-CVD-lung cancer</isRRFile> </RR > </RR> </RRs> </xml>
		 * 
		 */
	/* Field containing the name of the base directory */	
		
		public String baseDir;
		
	/*
	 * fields describing the labels of the XML configuration file (as made by
	 * window W01)
	 */	
		public static final String newbornLabel = "newborns";
		public static final String startingYearLabel = "startingYear";
		public static final String numberOfYearsLabel = "numberOfYears";
		public static final String simPopSize = "simPopSize";
		public static final String minAgeLabel = "minAge";
		public static final String maxAgeLabel = "maxAge";
		public static final String timeStepLabel = "timeStep";
		public static final String randomSeedLabel = "randomSeed";
		public static final String resultTypeLabel = "resultType";
		public static final String popFileNameLabel = "popFileName";
		public static final String diseasesLabel = "diseases";
		public static final String singleDiseaseLabel = "disease";
		public static final String diseaseNameLabel = "diseaseName";
		public static final String diseasePrevFileLabel = "diseasePrevFile";
		public static final String diseaseIncFileLabel = "diseaseIncFile";
		public static final String diseaseExcessMortFileLabel = "diseaseExcessMortFile";
		public static final String diseaseIncFile = "timeStep";
		public static final String DalyWeightLabel = "diseaseDalyWeights";
		public static final String riskFactorLabel = "riskfactor";
		public static final String riskFactorNameLabel = "riskfactor";
		public static final String riskFactorTypeLabel = "riskFactorType";
		public static final String riskFactorPrevFileLabel = "riskFactorPrevFile";
		public static final String riskFactorTransFileLabel = "riskFactorTransFile";
		public static final String riskFactorRRforDeathFileLabel = "riskFactorRRforDeathFile";
		public static final String RRseriesLabel = "RRs";
		public static final String singleRRLabel = "RR";
		public static final String isRRfromLabel = "isRRfrom";
		public static final String isRRtoLabel = "isRRto";
		public static final String isRRfileLabel = "isRRfile";
		public static final String scenarioSeriesLabel = "scenarios";
		public static final String scenarioLabel = "scenario";
		public static final String scenarioNameLabel = "scenName";
		public static final String targetMinAgeLabel = "targetMinAge";
		public static final String targetMaxAgeLabel = "targetMaxAge";
		public static final String targetGenderLabel = "targetGender";
		public static final String alternativeTransFileLabel = "alternativeTransFile";
		public static final String alternativePrevFileLabel = "alternativePrevFile";
	/* the object containing the content of the XML configuration file */ 
		private  HierarchicalConfiguration configuration;
	/*
	 * the names of the directories containing the information on resp.
	 * riskfactors, diseases and relative risks
	 */
		public static final String riskFactorDir = "/riskFactors";
		
		
		ConfigurationDataW01(String inputBaseDir,String fileName) throws ConfigurationException{
			HierarchicalConfiguration  configuration = new XMLConfiguration(inputBaseDir+fileName);
			baseDir=inputBaseDir;}

		
		
		public String getPopFileName()
				
				throws ConfigurationException {
			
					try {
						String populationFileName = configuration
								.getString(popFileNameLabel);
						if (populationFileName == null) {
							throw new ConfigurationException(
									"no population file name in XML file from window 1");
						}
						return populationFileName;
					} catch (NoSuchElementException e) {
						throw new ConfigurationException(
								"no population file name in XML file from window 1");
					}
				}
		
		public String getRiskFactorPrevFileName()
		
		throws ConfigurationException {
	
			try {
				String fileName = configuration
						.getString(riskFactorPrevFileLabel);
				if (fileName == null) {
					throw new ConfigurationException(
							"no risk factor prevalence file name in XML file from window 1");
				}
				return fileName;
			} catch (NoSuchElementException e) {
				throw new ConfigurationException(
						"no risk factor prevalence file name in XML file from window 1");
			}
		}

		public String getRiskFactorTransFileName()
		
		throws ConfigurationException {
			
			try {
				String fileName = configuration
						.getString(riskFactorTransFileLabel);
				if (fileName == null) {
					throw new ConfigurationException(
							"no risk factor transition file name in XML file from window 1");
				}
				return fileName;
			} catch (NoSuchElementException e) {
				throw new ConfigurationException(
						"no risk factor transition file name in XML file from window 1");
			}
			
		
		}
		public void addMortalityInfoToInputData(InputData inputData){
			getPopFileName();
			// TODO inlezen van populatie data en localiseren van plek sterfte
			// data
			// TODO inlezen van mortTot naar inputData}
		}
		public void addRiskFactorInfoToInputData(InputData inputData){
			// add risktype
			int riskType;
			try {
				 riskType = configuration
						.getInt(riskFactorTypeLabel);
				if (riskType != 1 && riskType !=2 && riskType !=3 ) 
					throw new ConfigurationException(
							"no valid risk factor type information in XML file from window 1, info is: "+riskType);
				
				inputData.riskType=riskType;
			} catch (NoSuchElementException e) {
				throw new ConfigurationException(
						"no risk factor type information in XML file from window 1");}
			// add riskfactor prevalence
			String fileNamePrev=getRiskFactorPrevFileName();
			if (riskType==1){
			HierarchicalConfiguration  riskFactorPrevConfiguration = new XMLConfiguration(baseDir+riskFactorDir+fileNamePrev);}
			// TODO inlezen + in inputdata stoppen zie onder;
			inputData.prevRisk[0][0][0]=4.0;
			
			}
			
			// TODO inlezen info voor continue: refClassCont,
			// riskDistribution,meanRisk,stdDevRisk,skewnessRisk
		   // TODO inlezen info voor compound : indexDuurClass, prevRisk
		}


public void addDiseaseInfoToInputData(InputData inputData){
	// first read necessary info :
	// TODO: read RR of disease on disease:
	// TODO: read diseaseNames

				
				
				
	/* extract disease structure */
	/* NB cancers must be split up in two diseases */
	/*
	 * this can not be determined from the configuration file, but only from the
	 * excess mortality data this is done elsewhere
	 * 
	 * identification of diseases throughout is through their name as the order
	 * is changed by creating clusters
	 */
	boolean[] causalDisease = new boolean[nDiseases];
	boolean[] dependentDisease = new boolean[nDiseases];

	int[] clusternumber = new int[nDiseases];
	for (int d = 0; d < nDiseases; d++)
		clusternumber[d] = d;
	// check which diseases are causal or dependent (=RR present);
	for (int d = 0; d < nDiseases; d++)
		for (int rr = 0; rr < nRRs; rr++) {
			if (isRRfrom[rr] == diseaseName[d]) {
				causalDisease[d] = true;
				for (int d2 = 0; d2 < nDiseases; d2++) {
					if (isRRto[rr] == diseaseName[d2]) {
						dependentDisease[d2] = true;
						// now give dependent and causal disease the same
						// (lowest) cluster number;
						if (clusternumber[d] < clusternumber[d2])
							clusternumber[d2] = clusternumber[d];
						if (clusternumber[d2] < clusternumber[d])
							clusternumber[d] = clusternumber[d2];
					}
				}
			}
		}
	// check if not both causal and dependent disease
	for (int d = 0; d < nDiseases; d++) {
		if (causalDisease[d] || dependentDisease[d])
			throw new InconsistentDataException(
					"Disease "
							+ diseaseName[d]
							+ " is both a cause of another disease and is caused itself by another disease. This is not allowed. Please change this");
	}// now determine clusters ;

	int clusterSum = 0;
	int prevClusterSum = 10000;
	int niter = 0;
	while (clusterSum != prevClusterSum && niter <= nDiseases) {
		prevClusterSum = 0;
		clusterSum = 0;
		for (int d = 0; d < nDiseases; d++) {
			prevClusterSum += clusternumber[d];
			for (int rr = 0; rr < nRRs; rr++) {
				if (isRRfrom[rr] == diseaseName[d]) {
					for (int d2 = 0; d2 < nDiseases; d2++) {
						if (isRRto[rr] == diseaseName[d2]) {
							// now give dependent and causal disease the
							// same
							// (lowest) cluster number;
							if (clusternumber[d] < clusternumber[d2])
								clusternumber[d2] = clusternumber[d];
							if (clusternumber[d2] < clusternumber[d])
								clusternumber[d] = clusternumber[d2];
						}
					}
				}
			}// end loop over all rr's related to d
			clusterSum += clusternumber[d];
		}
	}
	;
// now each cluster has a unique cluster number , but not necessarily
// aaneensluitend;
	
	// count clusters and make index;
	int clusterIndex []=new int[nDiseases]; // clusterIndex gives for each
											// disease the number of the
	// cluster it belongs too;
			
	clusterIndex[0]=0;
	int currentIndex=0;
	boolean hasSameNumber=false;
	for (int d = 1; d < nDiseases; d++) {
		for( int d2=0;d2<d;d2++) {

		if (clusternumber[d]==clusternumber[d2]) {clusterIndex[d]=clusterIndex[d2]; hasSameNumber=true;break;}
		
	}if (!hasSameNumber) {currentIndex++;clusterIndex[d]=currentIndex;}}
	nClusters=currentIndex;
	/*
	 * count number of diseases in each cluster and number of independent
	 * (=causal) diseases
	 */
	int[] nInCluster=new int[nClusters];
	int[] nCausalInCluster=new int[nClusters];
	for (int c=0;c<nClusters;c++){nInCluster[c]=0;
	for (int d = 1; d < nDiseases; d++){
		if (clusterIndex[d]==c) nInCluster[c]++;
		// NB: if disease is not related to any other disease then both
		// causalDisease and dependent disease
		// are false; in this case we make it a causal disease;
	    if (clusterIndex[d]==c && !dependentDisease[d]) nCausalInCluster[c]++;
	}}
	
	
	// make structure class
	clusterStructure=new DiseaseClusterStructure[nClusters];
	int nStart=0;
	for (int c=0;c<nClusters;c++){
		
		String[]DiseaseNamesForCluster=new String [nInCluster[c]];
		int[] indexIndependentDiseasesForCluster=new int[nCausalInCluster[c]];
		/* make array with names of diseases */
		int withinClusterNumber=0;
        int withinClusterIndependentNumber=0;
		{for (int d = 0; d < nDiseases; d++)
			if (clusterIndex[d]==c) {
			DiseaseNamesForCluster[withinClusterNumber]=diseaseName[d];
			if (!dependentDisease[d]) 
			{indexIndependentDiseasesForCluster[withinClusterNumber]=withinClusterIndependentNumber;
			withinClusterIndependentNumber++; }
			withinClusterNumber++;
		}}
		
		/*
		 * public DiseaseClusterStructure(String clusterName, int startN, int N,
		 * String[] diseaseNames, int[] NRIndependent)
		 */
		clusterStructure[c]=new DiseaseClusterStructure("cluster"+c,nStart,nInCluster[c],DiseaseNamesForCluster,
				indexIndependentDiseasesForCluster	);
		nStart+=nInCluster[c];
	}
	
	return this;
}
	
	}
	
	// TODO inlezen info voor continue: refClassCont,
	// riskDistribution,meanRisk,stdDevRisk,skewnessRisk
   // TODO inlezen info voor compound : indexDuurClass, prevRisk
}
		
		
		
		
		
}
