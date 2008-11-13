package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.Random;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.simulation.Simulation;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Hendriek
 * reading part only implemented for categorical risk factors
 *layout of configurationfile:
 <?xml version="1.0" encoding="UTF-8"?>
<updateRuleConfiguration>
<charID>4</charID>
<name>Dis1</name>
<baselineIncidenceFile>incidenceDis1.xml</baselineIncidenceFile>
<attributableMortFile>catMortDis1.xml</attributableMortFile>
<relativeRiskFile>relriskDis1.xml</relativeRiskFile>
</updateRuleConfiguration>
 */
/*
 * Let op bij schrijven van update rules: er moet altijd een constructor aanwezig zijn
 * zonder argumenten anders werkt de rule niet;
 * ook moeten ze worden geimporteert in het simulatie programma
 */
public class SingleDiseaseMultiToOneUpdateRule extends DynamoManyToOneUpdateRuleBase
		implements ConfigurationEntryPoint {

	Log log = LogFactory.getLog(this.getClass().getName());

	String[] requiredTags = {"updateRuleConfiguration", "age", "sex", "charID" };
	
	/* data needed in update rule */
	protected   float referenceValueContinous;
	
	private   float relRiskContinous[][] = null;
	private  float relRiskCategorical[][][] = null;
	private   float relRiskEnd[][] = null;
	private   float relRiskBegin[][] = null;
	private  float alfaDuur[][] = null;
	private  float attributableMortality[][] = null;
	private float baselineIncidence[][] = null;
	/* the following fields are needed in inherited types */
	protected float baselineOtherMort [][]=null; //(OM alleen nodig voor inherited types)
	protected float relRiskOtherMortCategorical[][][] = null;// kan dus evt weg
	protected float relRiskOtherMortEnd[][] = null;
	protected float relRiskOtherMortBegin[][] = null;
	protected float alfaDuurOtherMort[][] = null;
	protected float relRiskOtherMortContinous[][] = null;
	int riskType = 0;
	int nCat =0;
	/* filenames */
	private String  relRiskCatFileName = null;
	private String  relRiskContFileName = null;
	private String  relRiskEndFileName = null;
	private String  relRiskBeginFileName = null;
	private String alfaDuurFileName = null;
	private String attributableMortalityFileName = null;
	private String baselineIncidenceFileName =null;
	private String baselineOtherMortFileName =null;
	private String relRiskOtherMortCatFileName=null;
	
	/* xml tags for filenames */
	// TODO voor andere typen riskfactors 
	static protected String relRiskContFileNameLabel = null;
	static protected String relRiskEndFileNameLabel = null;
	static protected String relRiskBeginFileNameLabel = null;
	static protected String alfaDuurFileNameLabel = null;
	static protected String referenceValueContinousLabel=null;
	static protected  String riskTypeLabel = "riskType";
	static protected  String nCatLabel = "nCat";
	static protected String relRiskCatFileNameLabel ="relativeRiskFile";
	static protected String baselineIncidenceFileNameLabel ="baselineIncidenceFile" ;
	static protected String attributableMortalityFileNameLabel = "attributableMortFile";
	static protected String baselineOtherMortFileLabel=  "baselineOtherMortFile";
	static protected String relativeRiskOtherMortFileLabel=  "relativeRiskOtherMortFile";

	public SingleDiseaseMultiToOneUpdateRule() throws ConfigurationException, CDMUpdateRuleException {
		// constructor fills the parameters
		// temporary;
		super();
        
		
	}
	
	
	public SingleDiseaseMultiToOneUpdateRule(String configFileName)
			throws ConfigurationException {
		// constructor fills the parameters with data
		// filenames of the parameter files are given in the file name with
		// configFileName
		relRiskCatFileName = null; // ConfigurationFactory.getRiskCatFileName(configFileName);
		relRiskContFileName = null;
		relRiskEndFileName = null;
		relRiskBeginFileName = null;
		alfaDuurFileName = null;
		attributableMortalityFileName = null;
		baselineIncidenceFileName = null;
		
		File configFile = new File(configFileName);
		boolean success = loadConfigurationFile(configFile);
		if (!success) throw new ConfigurationException("loading of configuration file failed for updateRule SingleDiseaseMultiToOneUpdateRule");
		

	}

	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		float newValue = -1;
	
		
		try {
			int ageValue = (int) getFloat(currentValues, ageIndex);
			int sexValue = getInteger(currentValues, sexIndex);
			if (ageValue>95) ageValue=95;
			float oldValue;
			
			
				oldValue = getFloat(currentValues, characteristicIndex);
			
			double atMort = attributableMortality[ageValue][sexValue];

			double incidence = calculateIncidence(currentValues, ageValue,
					sexValue);

				// finci = ((p0 * em - i) * exp((i - em) * time) + i * (1 - p0))
				// / ((p0 * em - i) * exp((i - em) * time) + em * (1 - p0))
if (Math.abs(incidence-atMort)>1E-15) 
				newValue = (float) (((oldValue * atMort - incidence)
						* Math.exp((incidence - atMort) * timeStep) + incidence
						* (1 - (double) oldValue)) / ((oldValue * atMort - incidence)
						* Math.exp((incidence - atMort) * timeStep) + atMort
						* (1 - (double) oldValue)));
else newValue = (float)(1-(1-oldValue)/(1+incidence*(1-oldValue)*timeStep));
				/* if incidence equal to attributable mortality, the denominator becomes zero and we need another formula
				 * */
				
				 


		
		return newValue;
		} 
			catch (CDMUpdateRuleException e) {log.fatal(e.getMessage());
			log.fatal("this message was issued by SingleDiseaseMultiToOneUpdateRule"+
			 " when updating characteristic number "+"characteristicIndex");
			e.printStackTrace();
			throw e;
		}
	
	}

	private double calculateIncidence(Object[] currentValues, int ageValue,
			int sexValue) throws CDMUpdateRuleException {
		double incidence = 0;
		if (riskType == 1) {
			
				int riskFactorValue =  getInteger(currentValues, riskFactorIndex1);
				incidence = baselineIncidence[ageValue][sexValue]
						* relRiskCategorical[ageValue][sexValue][riskFactorValue];
			}
		
		if (riskType == 2) {
			
				float  riskFactorValue =  getFloat(currentValues, riskFactorIndex1);
				incidence = baselineIncidence[ageValue][sexValue]
						* Math
								.pow(
										(riskFactorValue - referenceValueContinous),
										relRiskContinous[ageValue][sexValue]);
			
		}
		if (riskType == 3) {

			int riskFactorValue =  getInteger(currentValues, riskFactorIndex1);
			
			
				if (durationClass == riskFactorValue){
					float  riskDurationValue =  getFloat(currentValues, riskFactorIndex2);
				
					incidence = baselineIncidence[ageValue][sexValue]
							* ((relRiskBegin[ageValue][sexValue] - relRiskEnd[ageValue][sexValue])
									* Math.exp(-riskDurationValue
											* alfaDuur[ageValue][sexValue]) + relRiskEnd[ageValue][sexValue]);
				}
					else
					incidence = baselineIncidence[ageValue][sexValue]
							* relRiskCategorical[ageValue][sexValue][riskFactorValue];
			}
		return incidence;
	}


	public boolean loadConfigurationFile(File configurationFile)
	throws ConfigurationException{
 boolean success = false;
 
XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
		configurationFile);

handleCharID(configurationFileConfiguration);
handleRiskType(configurationFileConfiguration);
handleNCat(configurationFileConfiguration);
handleAttributableMortality(configurationFileConfiguration);
handleBaselineIncidence(configurationFileConfiguration);

if (riskType==1){
	handleRelRiskCat(configurationFileConfiguration);
	
	
	
}else throw new ConfigurationException("risktype 2 and 3 not yet implemented in update Rule for diseases");
                       
                       
                    
success=true;
return success;
}
	

	protected  void handleNCat(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			int nCat   = simulationConfiguration.getInt(nCatLabel);
			log.debug("Setting number of categories to " + nCat);
			setNCat(nCat);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationTimestepMessage);
		}
	}	
	

	protected  void handleRiskType(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			int riskType   = simulationConfiguration.getInt(riskTypeLabel);
			log.debug("Setting number of categories to " + riskType);
			setRiskType(riskType);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noSimulationTimestepMessage);
		}
	}	

/* temporary version that loads fill into the file */
	protected  void loadOneDimData(
			String XMLFileName, String dataName, float filldata) throws ConfigurationException {
		try {
			
	/*		XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					XMLFileName);
	*/		log.debug("Setting "+ dataName );
/* temporary to fill by hand for testing */
	float [][] inputData =getData(filldata);
	
			if (dataName=="attributableMortality") 
						setAttributableMortality(inputData);
			if (dataName=="baselineIncidence") 
				setBaselineIncidence(inputData);
			
		
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}
	
	
/* real version that reads from XML */

	/**
	 * 
	 * this method reads the xml files for the relative risks and 
	 * other parameter data stored in three dimensional arrays
	 * @param XMLFileName: name of the file to read from (entire path)
	 * @param dataName: name of the type of input 
	 * (currently implemented are: "baselineOtherMortality", "attributableMortality" 
	 * and "baselineIncidence")
	 * 
	 *
	 * @throws ConfigurationException
	 */
	protected   void loadOneDimData(
			String XMLFileName, String dataName) throws ConfigurationException {
		try {
			
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					XMLFileName);
			log.debug("Setting "+ dataName );
			float [][] inputData=new float [96][2];
			
		    ArraysFromXMLFactory factory=new ArraysFromXMLFactory();
		
		    if (dataName==" baselineOtherMortality") {
				
				inputData=factory.manufactureOneDimArray( baselineOtherMortFileName, "baselineOtherMortalities","baselineOtherMortality");
			  setAttributableMortality(inputData);} 
			
			if (dataName=="attributableMortality") {
						
				inputData=factory.manufactureOneDimArray(baselineIncidenceFileName, "baselineIncidences","baselineIncidence");
			  setAttributableMortality(inputData);} 
			
			if (dataName=="baselineIncidence") 
			{ inputData= factory.manufactureOneDimArray(attributableMortalityFileName, "attributableMortalities","attributableMortality")  ; 
				
				setBaselineIncidence(inputData);}
			
		
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}
	


	/* temporary version that loads fill into the file */
	protected  void loadTwoDimData(
				String XMLFileName, String dataName, float [] filldata) throws ConfigurationException {
			try {
				
		/*		XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
						XMLFileName);
		*/		log.debug("Setting "+ dataName );
				float [][][] inputData =getDataTwoDim(filldata);
//				later: getData( attributableMortalityFileName); 
				if (dataName=="relRiskCat") 
							setRelRiskCategorical(inputData);
					
				if (dataName=="relRiskCatOtherMort") 
					setRelRiskOtherMortCategorical(inputData);
				
			
			} catch (NoSuchElementException e) {
				throw new ConfigurationException(
						CDMConfigurationException.noFileMessage);
			}
		}
		
	


	
	/** this method reads the xml files for the relative risks and 
	 * other parameter data stored in three dimensional arrays
	 * @param XMLFileName: name of the file to read from (entire path)
	 * @param dataName: name of the type of input 
	 * (currently implemented are: "relRiskCat", "relRiskCatOtherMort")
	 * 
	 * @throws ConfigurationException
	 */
	protected  void loadTwoDimData(
				String XMLFileName, String dataName) throws ConfigurationException {
			try {
				
		/*		XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
						XMLFileName);
		*/		log.debug("Setting "+ dataName );
				float [][][] inputData ;
				 ArraysFromXMLFactory factory=new ArraysFromXMLFactory();
				    
					
//				later: getData( attributableMortalityFileName); 
				if (dataName=="relRiskCat") {
					inputData= factory.manufactureTwoDimArray(relRiskCatFileName, "relativeRisks","relativeRisk")  ; 
						
							setRelRiskCategorical(inputData);}
				if (dataName=="relRiskCatOtherMort") {
					
					inputData= factory.manufactureTwoDimArray(relRiskOtherMortCatFileName, "relativeRisks","relativeRisk")  ; 
					
					setRelRiskOtherMortCategorical(inputData);}
				
			
			} catch (NoSuchElementException e) {
				throw new ConfigurationException(
						CDMConfigurationException.noFileMessage);
			}
		}
		
	
	
	
	/* temporary for testing */
	/* should be reading from XML */
	/* in that case argument should be string (filename) */
	
	protected   float [][] getData(float fill) throws ConfigurationException
			 {
		try {
			float [][] data=new float[96][2];
			for (int a=0;a<96;a++) for(int g=0;g<2;g++){data[a][g]=fill;};
			return data;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}
	

	protected   float [][] getData(String XMLFileName) throws ConfigurationException
			 {
		try {
			
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					XMLFileName);
			log.debug("Reading XML file "+XMLFileName );
			float [][] data=new float[96][2];
			// TODO: read from file
			return data;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected   float [][][] getDataTwoDim(float fill[]) throws ConfigurationException
	 {
try {
	int nCat=fill.length;
	float [][] []data=new float[96][2][nCat];
	for (int a=0;a<96;a++) for(int g=0;g<2;g++){data[a][g]=fill;};
	return data;
} catch (NoSuchElementException e) {
	throw new ConfigurationException(
			CDMConfigurationException.noFileMessage);
}
}


	protected   float [][][] getDataTwoDim(String XMLFileName) throws ConfigurationException
	 {
try {
	
	XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
			XMLFileName);
	log.debug("Reading XML file "+XMLFileName );
	float [][][] data=new float[96][2][];
	// TODO: read from file
	return data;
} catch (NoSuchElementException e) {
	throw new ConfigurationException(
			CDMConfigurationException.noFileMessage);
}
}

	
	


	protected   void handleAttributableMortality(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			String attributableMortalityFileName   = simulationConfiguration.getString(attributableMortalityFileNameLabel);
			log.debug("Setting AttributableMortalityFilename to: " + attributableMortalityFileName );
			setAttributableMortalityFileName(attributableMortalityFileName);
			loadOneDimData( attributableMortalityFileName,"attributableMortality",0.01F);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}
	
	protected   void handleBaselineIncidence(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			String FileName   = simulationConfiguration.getString(attributableMortalityFileNameLabel);
			log.debug("Setting BaselineIncidenceFilename to: " + FileName );
			setBaselineIncidenceFileName(FileName);
			loadOneDimData( baselineIncidenceFileName,"baselineIncidence",0.01F);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}
	
	


	

	
	protected   void handleRelRiskCat(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			
			// relRiskCatFileName 
			String FileName   = simulationConfiguration.getString(relRiskCatFileNameLabel);
			log.debug("Setting RelativeRiskFilename to: " + FileName );
			setRelRiskCatFileNameLabel(FileName);
			float[] fill={1.0F,1.2F,1.5F,2F};
			loadTwoDimData( relRiskCatFileName,"relRiskCat",fill);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}
	


	
	protected   void handleRelRiskOtherMortCat(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			
			// relRiskCatFileName 
			String FileName   = simulationConfiguration.getString(relativeRiskOtherMortFileLabel);
			log.debug("Setting relativeRiskOtherMortFile to: " + FileName );
			setRelativeRiskOtherMortFileName(FileName);
			float[] fill={1.0F,1.2F,1.5F,2F};
			loadTwoDimData( relRiskCatFileName,"relRiskCatOtherMort",fill);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}


	private void setRelativeRiskOtherMortFileName(String fileName) {
		// TODO Auto-generated method stub
		
	}


	public String getBaselineIncidenceFileName() {
		return baselineIncidenceFileName;
	}

	public void setBaselineIncidenceFileName(String baselineIncidenceFileName) {
		this.baselineIncidenceFileName = baselineIncidenceFileName;
	}

	

	public float[][] getRelRiskContinous() {
		return relRiskContinous;
	}

	public void setRelRiskContinous(float[][] relRiskContinous) {
		this.relRiskContinous = relRiskContinous;
	}

	public float getReferenceValueContinous() {
		return referenceValueContinous;
	}

	public void setReferenceValueContinous(float referenceValueContinous) {
		this.referenceValueContinous = referenceValueContinous;
	}

	public float[][][] getRelRiskCategorical() {
		return relRiskCategorical;
	}

	public void setRelRiskCategorical(float[][][] relRiskCategorical) {
		this.relRiskCategorical = relRiskCategorical;
	}

	public float[][] getRelRiskEnd() {
		return relRiskEnd;
	}

	public void setRelRiskEnd(float[][] relRiskEnd) {
		this.relRiskEnd = relRiskEnd;
	}

	public float[][] getRelRiskBegin() {
		return relRiskBegin;
	}

	public void setRelRiskBegin(float[][] relRiskBegin) {
		this.relRiskBegin = relRiskBegin;
	}

	public float[][] getAlfaDuur() {
		return alfaDuur;
	}

	public void setAlfaDuur(float[][] alfaDuur) {
		this.alfaDuur = alfaDuur;
	}

	public float[][] getAttributableMortality() {
		return attributableMortality;
	}

	public void setAttributableMortality(float[][] attributableMortality) {
		this.attributableMortality = attributableMortality;
	}

	public float[][] getBaselineIncidence() {
		return baselineIncidence;
	}

	public void setBaselineIncidence(float[][] baselineIncidence) {
		this.baselineIncidence = baselineIncidence;
	}

	public int getRiskType() {
		return riskType;
	}

	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	public int getDurationClass() {
		return durationClass;
	}

	public void setDurationClass(int durationClass) {
		this.durationClass = durationClass;
	}

	public int getAgeIndex() {
		return ageIndex;
	}

	public void setAgeIndex(int ageIndex) {
		this.ageIndex = ageIndex;
	}

	public int getSexIndex() {
		return sexIndex;
	}

	public void setSexIndex(int sexIndex) {
		this.sexIndex = sexIndex;
	}

	public int getRiskFactorIndex1() {
		return riskFactorIndex1;
	}

	public void setRiskFactorIndex1(int riskFactorIndex1) {
		this.riskFactorIndex1 = riskFactorIndex1;
	}

	public int getRiskFactorIndex2() {
		return riskFactorIndex2;
	}

	public void setRiskFactorIndex2(int riskFactorIndex2) {
		this.riskFactorIndex2 = riskFactorIndex2;
	}

	public int getCharacteristicIndex() {
		return characteristicIndex;
	}

	public void setCharacteristicIndex(int characteristicIndex) {
		this.characteristicIndex = characteristicIndex;
	}

	public String getRelRiskCatFileName() {
		return relRiskCatFileName;
	}

	public void setRelRiskCatFileName(String relRiskCatFileName) {
		this.relRiskCatFileName = relRiskCatFileName;
	}

	public String getRelRiskContFileName() {
		return relRiskContFileName;
	}

	public void setRelRiskContFileName(String relRiskContFileName) {
		this.relRiskContFileName = relRiskContFileName;
	}

	public String getRelRiskEndFileName() {
		return relRiskEndFileName;
	}

	public void setRelRiskEndFileName(String relRiskEndFileName) {
		this.relRiskEndFileName = relRiskEndFileName;
	}

	public String getRelRiskBeginFileName() {
		return relRiskBeginFileName;
	}

	public void setRelRiskBeginFileName(String relRiskBeginFileName) {
		this.relRiskBeginFileName = relRiskBeginFileName;
	}

	public String getAlfaDuurFileName() {
		return alfaDuurFileName;
	}

	public void setAlfaDuurFileName(String alfaDuurFileName) {
		this.alfaDuurFileName = alfaDuurFileName;
	}

	public String getAttributableMortalityFileName() {
		return attributableMortalityFileName;
	}

	public void setAttributableMortalityFileName(
			String attributableMortalityFileName) {
		this.attributableMortalityFileName = attributableMortalityFileName;
	}
	public int getNCat() {
		return nCat;
	}
	public static String getRelativeRiskOtherMortFileLabel() {
		return relativeRiskOtherMortFileLabel;
	}


	public static void setRelativeRiskOtherMortFileLabel(
			String relativeRiskOtherMortFileLabel) {
		SingleDiseaseMultiToOneUpdateRule.relativeRiskOtherMortFileLabel = relativeRiskOtherMortFileLabel;
	}



	public void setNCat(int cat) {
		nCat = cat;
	}


	public static String getRelRiskCatFileNameLabel() {
		return relRiskCatFileNameLabel;
	}


	public static void setRelRiskCatFileNameLabel(String relRiskCatFileNameLabel) {
		SingleDiseaseMultiToOneUpdateRule.relRiskCatFileNameLabel = relRiskCatFileNameLabel;
	}


	public float[][][] getRelRiskOtherMortCategorical() {
		return relRiskOtherMortCategorical;
	}


	public void setRelRiskOtherMortCategorical(
			float[][][] relRiskOtherMortCategorical) {
		this.relRiskOtherMortCategorical = relRiskOtherMortCategorical;
	}


	public float[][] getRelRiskOtherMortBegin() {
		return relRiskOtherMortBegin;
	}


	public void setRelRiskOtherMortBegin(float[][] relRiskOtherMortBegin) {
		this.relRiskOtherMortBegin = relRiskOtherMortBegin;
	}


	public float[][] getRelRiskOtherMortContinous() {
		return relRiskOtherMortContinous;
	}


	public void setRelRiskOtherMortContinous(float[][] relRiskOtherMortContinous) {
		this.relRiskOtherMortContinous = relRiskOtherMortContinous;
	}




	public void setRelRiskOtherMortCatFileName(
			String relRiskOtherMortCatFileName) {
		this.relRiskOtherMortCatFileName = relRiskOtherMortCatFileName;
	}


	public String getRelRiskOtherMortCatFileName() {
		return relRiskOtherMortCatFileName;
	}

	
	}

