package nl.rivm.emi.dynamo.datahandling;

import java.io.IOException;

import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.dynamo.estimation.DiseaseClusterStructure;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Hendriek Class SimulationConfiguration file contains the simulation
 *         information from window W01 that is needed in order to estimate
 *         parameters information that is needed for running the model or post
 *         processing is not included however it is included in comments for
 *         later use;
 */
public class SimulationConfiguration {

	/** This are the data extracted from W10 */

	// public boolean newborns;
	// public int startingYear;
	// public int numberOfYears;
	// public int simulatedPopulationSize;
	// public int minimmAge;
	// public int maximumAge;
	// public float calculationTimeStep ;
	// public int randomSeed ;
	// public int resultType; // aggregated of separate;
	public String PopulationFileName;

	
	public String[] diseaseName;
	public String[] diseasePrevalenceFile;
	public String[] diseaseIncidenceFile;
	public String[] diseaseExcessMortalityData;
	// public String[] diseaseDalyWeights;
	public String riskFactorName = null;
	public int riskFactorType;
	public String riskFactorPrevalenceFile;
	public String riskFactorTransitionFile;
	public String riskFactorRRforDeathFile;
	public String[] isRRfrom;
	public String[] isRRto;
	public String[] isRRFile;
	// to determine from this 
	
	public int nDiseases = 0;
	public int nClusters = 0;
	public int nRRs;
	public DiseaseClusterStructure[] clusterStructure;

	public SimulationConfiguration readSimulationConfiguration(String baseDir,
			String simulationName) throws DynamoInconsistentDataException {
		/*
		 * TODO read XML configurationfile and put in fields;
		 * count number of RR's and diseases;
		 * 		 
		 **/
		
		/* extract disease structure */
		/* NB cancers must be split up in two diseases */
		/* this can not be determined from the configuration file, but only
		 * from the excess mortality data
		 * this is done elsewhere
		 * 
		 * identification of diseases throughout is through their name
		 * as the order is changed by creating clusters
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
				throw new DynamoInconsistentDataException(
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
// now each cluster has a unique cluster number , but not necessarily aaneensluitend;
		
		// count clusters and make index;
		int clusterIndex []=new int[nDiseases]; // clusterIndex gives for each disease the number of the 
		// cluster it belongs too;
				
		clusterIndex[0]=0;
		int currentIndex=0;
		boolean hasSameNumber=false;
		for (int d = 1; d < nDiseases; d++) {
			for( int d2=0;d2<d;d2++) {
	
			if (clusternumber[d]==clusternumber[d2]) {clusterIndex[d]=clusterIndex[d2]; hasSameNumber=true;break;}
			
		}if (!hasSameNumber) {currentIndex++;clusterIndex[d]=currentIndex;}}
		nClusters=currentIndex;
		/* count number of diseases in each cluster and number of independent (=causal) diseases*/
		int[] nInCluster=new int[nClusters];
		int[] nCausalInCluster=new int[nClusters];
		for (int c=0;c<nClusters;c++){nInCluster[c]=0;
		for (int d = 1; d < nDiseases; d++){
			if (clusterIndex[d]==c) nInCluster[c]++;
			// NB: if disease is not related to any other disease then both causalDisease and dependent disease
			// are false; in this case we make it a causal disease;
		    if (clusterIndex[d]==c && !dependentDisease[d]) nCausalInCluster[c]++;
		}}
		
		
		// make structure class
		clusterStructure=new DiseaseClusterStructure[nClusters];
		int nStart=0;
		for (int c=0;c<nClusters;c++){
			
			String[]DiseaseNamesForCluster=new String [nInCluster[c]];
			int[] indexIndependentDiseasesForCluster=new int[nCausalInCluster[c]];
			/* make array with names of diseases*/
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
			
			/* public DiseaseClusterStructure(String clusterName, int startN, int N,
			String[] diseaseNames, int[] NRIndependent) */
			clusterStructure[c]=new DiseaseClusterStructure("cluster"+c,nStart,nInCluster[c],DiseaseNamesForCluster,
					indexIndependentDiseasesForCluster	);
			nStart+=nInCluster[c];
		}
		
		return this;
	}
	
	/*
	public int readIntegerFromXML(String fileName, String nodeName){
		{
			//get the factory
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			try {

				//Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();

				//parse using builder to get DOM representation of the XML file
				Document dom = db.parse(fileName);

				
					//get the root element
					Element docEle = dom.getDocumentElement();

					//get a nodelist of  elements
					NodeList nl = docEle.getElementsByTagName("Employee");
					if(nl != null && nl.getLength() > 0) {
						for(int i = 0 ; i < nl.getLength();i++) {

							//get the employee element
							Element el = (Element)nl.item(i);

							//get the Employee object
							Employee e = getEmployee(el);

							//add it to list
							myEmpls.add(e);
						}
					}
					
					//for each <employee> element get text or int values of
					//name ,id, age and name
					String name = getTextValue(empEl,"Name");
					int id = getIntValue(empEl,"Id");
					int age = getIntValue(empEl,"Age");

					String type = empEl.getAttribute("type");

					//Create a new Employee with the value read from the xml nodes
					Employee e = new Employee(name,id,age,type);

					/**
					 * I take a xml element and the tag name, look for the tag and get
					 * the text content
					 * i.e for <employee><name>John</name></employee> xml snippet if
					 * the Element points to employee node and tagName is 'name' I will return John
					 */
	
	
	
	/*
	
	
					private String getTextValue(Element ele, String tagName) {
						String textVal = null;
						NodeList nl = ele.getElementsByTagName(tagName);
						if(nl != null && nl.getLength() > 0) {
							Element el = (Element)nl.item(0);
							textVal = el.getFirstChild().getNodeValue();
						}

						return textVal;
					}


					/**
					 * Calls getTextValue and returns a int value
					 */
	
	
	/*
					private int getIntValue(Element ele, String tagName) {
						//in production application you would catch the exception
						return Integer.parseInt(getTextValue(ele,tagName));
					}
					System.out.println("No of Employees '" + myEmpls.size() + "'.");

					Iterator it = myEmpls.iterator();
					while(it.hasNext()) {
						System.out.println(it.next().toString());
					}


				}

			}catch(ParserConfigurationException pce) {
				pce.printStackTrace();
			}catch(SAXException se) {
				se.printStackTrace();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return nClusters;}
*/
}
