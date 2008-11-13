package nl.rivm.emi.cdm.rules.update.dynamo;


import java.io.File;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
/**
 * 
 * 
 * 
 * 
 * @author boshuizh
 * configuration file should look like:
 * 
 * obsolete
 *<?xml version="1.0" encoding="UTF-8"?>
<updateRuleConfiguration>
<charID>9</charID>
<name>Dis6</name>
<riskType>1</riskType>
<nCat>4</nCat>
<baselineOtherMortFile>otherMort.xml</baselineOtherMortFile>
<relativeRiskOtherMortFile>relriskDis1.xml</relativeOtherMortRiskFile>

<disease>
<baselineIncidenceFile>incidenceDis1.xml</baselineIncidenceFile>
<attributableMortFile>catMortDis1.xml</attributableMortFile>
<relativeRiskFile>relriskDis1.xml</relativeRiskFile>
</disease>
<disease>
<baselineIncidenceFile>incidenceDis1.xml</baselineIncidenceFile>
<attributableMortFile>catMortDis1.xml</attributableMortFile>
<relativeRiskFile>relriskDis1.xml</relativeRiskFile>
</disease>
<disease>
<baselineIncidenceFile>incidenceDis1.xml</baselineIncidenceFile>
<attributableMortFile>catMortDis1.xml</attributableMortFile>
<relativeRiskFile>relriskDis1.xml</relativeRiskFile>
</disease>
<disease>
<baselineIncidenceFile>incidenceDis1.xml</baselineIncidenceFile>
<attributableMortFile>catMortDis1.xml</attributableMortFile>
<relativeRiskFile>relriskDis1.xml</relativeRiskFile>
</disease>

</updateRuleConfiguration>

 */
public class SurvivalMultiToOneUpdateRule extends SingleDiseaseMultiToOneUpdateRule{
	
	
	
	
	/* data needed in update rule */
	
	
	private   float relRiskContinous[][][] = null;
	private  float relRiskCategorical[][][][] = null;
	private   float relRiskEnd[][] []= null;
	private   float relRiskBegin[][][] = null;
	private  float alfaDuur[][][] = null;
	private  float attributableMortality[][][] = null;
	private float baselineIncidence[][][] = null;
	private float baselineFatalIncidence[][][] = null;
	private int nClusters=1;
	private int [] NinCluster={2};
	private int [] firstCharID={4};
	/* filenames */
	private String [] relRiskCatFileName = null;
	private String [] relRiskContFileName = null;
	private String [] relRiskEndFileName = null;
	private String [] relRiskBeginFileName = null;
	private String [] alfaDuurFileName = null;
	private String [] attributableMortalityFileName = null;
	private String [] baselineIncidenceFileName =null;

	private int nDiseases=0;
	static protected  String nDiseasesLabel = "nDiseases";
	public SurvivalMultiToOneUpdateRule(String configFileName)
			throws ConfigurationException {
		super(configFileName);
		// TODO also set number of diseases OK
		// TODO Auto-generated constructor stub
	}
	
	
	public SurvivalMultiToOneUpdateRule() throws ConfigurationException, CDMUpdateRuleException{super();};
	public Object update(Object[] currentValues) throws CDMUpdateRuleException {

		float newValue = -1;
		
		try {
			int ageValue = (int) getFloat(currentValues, ageIndex);
			int sexValue = getInteger(currentValues, sexIndex);
			if (ageValue>95) ageValue=95;
			float oldValue = getFloat(currentValues, characteristicIndex);
			
			float[] atMort = attributableMortality[ageValue][sexValue];
			double otherMort = calculateOtherCauseMortality(currentValues, ageValue, sexValue);
			double[] incidence =new double [nDiseases];
			double[] fatalIncidence =new double [nDiseases];
			for (int d=0;d<nDiseases;d++){
			incidence[d]=calculateIncidence(currentValues, ageValue,
					sexValue,d);
			fatalIncidence[d]=incidence[d]*baselineFatalIncidence[ageValue][sexValue][d]/
			baselineIncidence[ageValue][sexValue][d];}
			double[] currentDiseaseValue;
			if (riskType==1 || riskType==2) 
			 currentDiseaseValue=getCurrentDiseaseValues(currentValues, ageValue, sexValue,riskFactorIndex1+1,nDiseases);
			else currentDiseaseValue=getCurrentDiseaseValues(currentValues, ageValue, sexValue,riskFactorIndex2+1,nDiseases);
			double survivalFraction=Math.exp(-otherMort*timeStep);
			for (int c = 0; c < nClusters; c++){
			if (NinCluster[c]==1)			
			{survivalFraction*=(atMort[firstCharID[c]]*(1-currentDiseaseValue[firstCharID[c]])*Math.exp(-timeStep*incidence[firstCharID[c]])
				+(atMort[firstCharID[c]]*currentDiseaseValue[firstCharID[c]]-incidence[firstCharID[c]])*Math.exp(-timeStep*atMort[firstCharID[c]]))/
				(atMort[firstCharID[c]]-incidence[firstCharID[c]]);
	                
			}//TODO voor cluster ziekten
			}
				newValue = (float) survivalFraction;

			

		
	return newValue;} 
	
			catch (CDMUpdateRuleException e) {log.fatal(e.getMessage());
	log.fatal("this message was issued by SurvivalMultiToOneUpdateRule"+
	 " when updating characteristic number "+"characteristicIndex");
	e.printStackTrace();
	throw e;

	}
	}
	private double[] getCurrentDiseaseValues(Object[] currentValues,
			int ageValue, int sexValue, int i, int diseases) {
		// TODO Auto-generated method stub
		
		
		
		return null;
	}
	public int getNDiseases() {
		return nDiseases;
	}
	public void setNDiseases(int diseases) {
		nDiseases = diseases;
	}
	private double calculateIncidence(Object[] currentValues, int ageValue,
			int sexValue, int diseaseNumber) throws CDMUpdateRuleException {
		double incidence = 0;
		if (riskType == 1) {
			
				int riskFactorValue =  getInteger(currentValues, riskFactorIndex1);
				incidence = baselineIncidence[ageValue][sexValue][diseaseNumber]
						* relRiskCategorical[ageValue][sexValue][riskFactorValue][diseaseNumber];
			}
		
		if (riskType == 2) {
			
				float  riskFactorValue =  getFloat(currentValues, riskFactorIndex1);
				incidence = baselineIncidence[ageValue][sexValue][diseaseNumber]
						* Math
								.pow(
										(riskFactorValue - referenceValueContinous),
										relRiskContinous[ageValue][sexValue][diseaseNumber]);
			
		}
		if (riskType == 3) {

			int riskFactorValue =  getInteger(currentValues, riskFactorIndex1);
			
			
				if (durationClass == riskFactorValue){
					float  riskDurationValue =  getFloat(currentValues, riskFactorIndex2);
				
					incidence = baselineIncidence[ageValue][sexValue][diseaseNumber]
							* ((relRiskBegin[ageValue][sexValue][diseaseNumber] - relRiskEnd[ageValue][sexValue][diseaseNumber])
									* Math.exp(-riskDurationValue
											* alfaDuur[ageValue][sexValue][diseaseNumber]) + relRiskEnd[ageValue][sexValue][diseaseNumber]);
				}
					else
					incidence = baselineIncidence[ageValue][sexValue][diseaseNumber]
							* relRiskCategorical[ageValue][sexValue][riskFactorValue][diseaseNumber];
			}
		return incidence;
	}

	


	private double calculateOtherCauseMortality(Object[] currentValues, int ageValue,
			int sexValue) throws CDMUpdateRuleException {
		double otherCauseMortality = 0;
		if (riskType == 1) {
			
				int riskFactorValue =  getInteger(currentValues, riskFactorIndex1);
				otherCauseMortality =baselineOtherMort[ageValue][sexValue]
						*  relRiskOtherMortCategorical[ageValue][sexValue][riskFactorValue];
			}
		
		if (riskType == 2) {
			
				float  riskFactorValue =  getFloat(currentValues, riskFactorIndex1);
				otherCauseMortality = baselineOtherMort[ageValue][sexValue]
						* Math
								.pow(
										(riskFactorValue - referenceValueContinous),
										relRiskOtherMortContinous[ageValue][sexValue]);
			
		}
		if (riskType == 3) {

			int riskFactorValue =  getInteger(currentValues, riskFactorIndex1);
			
			
				if (durationClass == riskFactorValue){
					float  riskDurationValue =  getFloat(currentValues, riskFactorIndex2);
				
					otherCauseMortality =baselineOtherMort[ageValue][sexValue]
							* ((relRiskOtherMortBegin[ageValue][sexValue] - relRiskOtherMortEnd[ageValue][sexValue])
									* Math.exp(-riskDurationValue
											* alfaDuurOtherMort[ageValue][sexValue]) + relRiskOtherMortEnd[ageValue][sexValue]);
				}
					else
						otherCauseMortality = baselineOtherMort[ageValue][sexValue]
							* relRiskOtherMortCategorical[ageValue][sexValue][riskFactorValue];
			}
		return otherCauseMortality;
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
handleBaselineOtherMort(configurationFileConfiguration);
//TODO overige info inlezen

if (riskType==1){
	handleRelRiskCat(configurationFileConfiguration);
	handleRelRiskOtherMortCat(configurationFileConfiguration);
	
	
}else throw new ConfigurationException("risktype 2 and 3 not yet implemented in update Rule for diseases");
                       
                       
                    
success=true;
return success;
}

	protected  void handleNDiseases(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			int nDiseases   = simulationConfiguration.getInt(nDiseasesLabel);
			log.debug("Setting number of diseases to " + nDiseases);
			setNDiseases(nDiseases);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noConfigurationTagMessage+nDiseasesLabel);
		}
	}	


	protected   void handleAttributableMortality(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			/* TODO String attributableMortalityFileName   = simulationConfiguration.getString(attributableMortalityFileNameLabel);
			log.debug("Setting AttributableMortalityFilename to: " + attributableMortalityFileName );
			setAttributableMortalityFileName(attributableMortalityFileName);*/
			
			 String attributableMortalityFileName="not given";
			 attributableMortality=new float [96][2][6];
			 for (int d=0;d<6;d++){for (int a=0;a<96;a++) for(int g=0;g<2;g++)
			 {attributableMortality[a][g][d]=0.01F;
			 
			 };
			 }
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
			baselineIncidence=new float [96][2][6];
		
			 for (int d=0;d<6;d++)for (int a=0;a<96;a++) for(int g=0;g<2;g++)
			 {baselineIncidence[a][g][d]=0.01F;
			 
			 };
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected   void handleBaselineOtherMort(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			String FileName   = simulationConfiguration.getString(attributableMortalityFileNameLabel);
			log.debug("Setting BaselineIncidenceFilename to: " + FileName );
			setBaselineIncidenceFileName(FileName);
			baselineOtherMort=new float [96][2];
		
			for (int a=0;a<96;a++) for(int g=0;g<2;g++)
			 {baselineOtherMort[a][g]=0.01F;
			 
			 };
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

	protected   void handleRelativeRisks(
			HierarchicalConfiguration simulationConfiguration) throws ConfigurationException {
		try {
			// String FileName   = simulationConfiguration.getString(attributableMortalityFileNameLabel);
			//log.debug("Setting BaselineIncidenceFilename to: " + FileName );
			//setattributableMortalityFileName(FileName);
			String attributableMortalityFileName="not given";
			relRiskCategorical= new float [96][2][nDiseases][nCat];
			float[] fill={1,1.1F,1.2F,1.5F};
			 for (int d=0;d<6;d++)for (int a=0;a<96;a++) for(int g=0;g<2;g++)
			 { relRiskCategorical[a][g][d]=fill;
			 
			 };
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
	}

}
	