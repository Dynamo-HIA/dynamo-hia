package nl.rivm.emi.cdm.rules.update.dynamo;

import java.io.File;
import java.util.List;
import java.util.Random;

import Jama.Matrix;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TwoPartDiseaseMultiToOneUpdateRule extends DynamoManyToOneUpdateRuleBase
		implements ConfigurationEntryPoint {

	Log log = LogFactory.getLog(this.getClass().getName());
// ???????????????
	String[] requiredTags = { "age", "sex", "characteristicindex", "configfile" };
// parameters that must be read from configuration file
	int riskType = -1;
	int characteristicIndex = -1; 
	int durationClass = -1;  // optional
// parameters that 	must be read from files that are indicated in the configuration file
	float relRiskContinous[][] = null; // for type=2
	float referenceValueContinous;  
	float relRiskCategorical[][][] = null; // for type=1 and 3
	float relRiskEnd[][] = null; // for type=3
	float relRiskBegin[][] = null;
	float alfaDuur[][] = null;
	float attributableMortality[][] = null; // all
	float baselineIncidence[][] = null; //all 
	

	int ageIndex = 1;
	int sex = 2;
	int riskFactorIndex1 = 3;
	int riskFactorIndex2 = 4;
	
// file names to be read from configuration file
	String relRiskCatFileName = null;// for type=1 or 3
	String relRiskContFileName = null;// for type=2
	String relRiskEndFileName = null;// for type=3
	String relRiskBeginFileName = null;// for type=3
	String alfaDuurFileName = null;// for type=3
	String attributableMortalityFileName = null;
	String baselineIncidenceFileName = null;

	

	public TwoPartDiseaseMultiToOneUpdateRule(String configFileName)
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
		if (success);//TODO
		/**
		 * loadIncidence(baselineIncidenceFileName);
		 * loadAttributableMortality(baselineIncidenceFileName); load all
		 * relative risks loadRiskType; load durationClass; load Reference type
		 * continuous
		 */
		// TODO

	}

	public Object update(Object[] currentValues) {

		float newValue = -1;
	
		if ((currentValues[ageIndex] != null) && (currentValues[sex] != null)) {
			int ageValue = -1;
			int sexValue = -1;
			float oldValue = -1;
			if ((currentValues[characteristicIndex] instanceof Float)

			&& (currentValues[ageIndex] instanceof Float)
					&& (currentValues[sex] instanceof Integer)) {
				ageValue = (int) Math.round(((Float) currentValues[ageIndex])
						.floatValue());
				sexValue = ((Integer) currentValues[sex]).intValue();
				oldValue = ((Float) currentValues[characteristicIndex])
						.floatValue();
			}
			double atMort = attributableMortality[ageValue][sexValue];

			double incidence = 0;
			if (riskType == 1) {
				if (currentValues[riskFactorIndex1] instanceof Integer) {
					int riskFactorValue = ((Integer) currentValues[riskFactorIndex1])
							.intValue();
					incidence = baselineIncidence[ageValue][sexValue]
							* relRiskCategorical[ageValue][sexValue][riskFactorValue];
				}
			}
			if (riskType == 2) {
				if (currentValues[riskFactorIndex1] instanceof Float) {
					float riskFactorValue = ((Float) currentValues[riskFactorIndex1])
							.floatValue();
					incidence = baselineIncidence[ageValue][sexValue]
							* Math
									.pow(
											(riskFactorValue - referenceValueContinous),
											relRiskContinous[ageValue][sexValue]);
				}
			}
			if (riskType == 3) {
				if ((currentValues[riskFactorIndex1] instanceof Integer)
						&& (currentValues[riskFactorIndex2] instanceof Integer)) {
					int riskFactorValue = ((Integer) currentValues[riskFactorIndex1])
							.intValue();
					int riskDurationValue = ((Integer) currentValues[riskFactorIndex2])
							.intValue();
					if (durationClass == riskFactorValue)
						incidence = baselineIncidence[ageValue][sexValue]
								* ((relRiskBegin[ageValue][sexValue] - relRiskEnd[ageValue][sexValue])
										* Math.exp(-riskDurationValue
												* alfaDuur[ageValue][sexValue]) + relRiskEnd[ageValue][sexValue]);
					else
						incidence = baselineIncidence[ageValue][sexValue]
								* relRiskCategorical[ageValue][sexValue][riskFactorValue];
				}

				// finci = ((p0 * em - i) * exp((i - em) * time) + i * (1 - p0))
				// / ((p0 * em - i) * exp((i - em) * time) + em * (1 - p0))
				// Cured part of disease:
				Math.exp()
				
				newValue=(
						Math.exp(a) *(-a + itot) *(Math.exp(itot)* itot* pcured - (-1 +Math.exp(itot))*
								icured *(-1 + 
					         ptot)))/(Math.exp(itot)* itot* (itot (-1 + Math.exp(a + m) + 
					          pcured - Math.exp(a) pcured) + (-1 + Math.exp(a))* icured* (-1 + ptot)) + 
					    a (-Math.exp(a + itot + m) *itot - Math.exp(itot)* itot* (pcured - ptot) +
					    		
					    		Math.exp(a)* (icured - itot)* (-1 + ptot) + Math.exp(
					        a + itot) *(icured + itot *pcured - icured* ptot)));
				// fatal part of disease
				// a= attributable mort
				//itot=total incidence
				// icured= cured incidence
				//ptot=total prevalence
				// pcured= cured incidence
				newValue=
				(itot (Math.exp(a)* (icured - itot)* (-1 + ptot) + 
						Math.exp(itot) (icured + itot* (-1 + pcured) - icured *ptot + 
					         a* (-pcured + ptot))))/(-
					        		 Math.exp(itot)* itot *(itot* (-1 + Math.exp(a + m) + 
					          pcured - Math.exp(a )*pcured) + (-1 + 
					        		  Math.exp( a))* icured (-1 + ptot)) + 
					    a (Math.exp(a + itot + m) itot +Math.exp(
					        itot)* itot (pcured - ptot) - Math.exp(
					        a )*(icured - itot) *(-1 + ptot) -Math.exp(
					        a + itot)* (icured + itot *pcured - icured *ptot)));
				
				
			}

		}
		return newValue;

	}

	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return false;
	}
}
