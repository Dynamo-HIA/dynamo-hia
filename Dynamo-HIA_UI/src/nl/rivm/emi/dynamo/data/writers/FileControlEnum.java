package nl.rivm.emi.dynamo.data.writers;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;

/**
 * Class that for the moment contains items nescessary to be able to write out
 * XML files containing only repetative parameters contained below a rootelement
 * and a rootchildelement only.
 * 
 * Example: <rootelement> <rootchild> <age>0</age> <sex>0</sex> <value>4.5</value>
 * </rootchild> </rootelement>
 * 
 * The parameters each have a type that contains among others its elementname.
 * 
 * @author mondeelr
 * 
 */

public enum FileControlEnum {
	POPULATIONSIZE(FileControlEnumHelp.populationSizeStrings),
	OVERALLMORTALITY(FileControlEnumHelp.overallMortalityStrings),
	OVERALLDALYWEIGHTS(FileControlEnumHelp.overallDALYWeightsStrings),
	TRANSITIONMATRIX(FileControlEnumHelp.transitionMatrixStrings),
	RISKFACTORPREVALENCESCATEGORICAL(FileControlEnumHelp.riskFactorPrevalenceCatStrings),
	RISKFACTORPREVALENCESDURATION(FileControlEnumHelp.riskFactorPrevalenceDurStrings),
	RELRISKFORDEATHCATEGORICAL(FileControlEnumHelp.relRiskForDeathCatStrings),
	RELRISKFORDEATHCONTINUOUS(FileControlEnumHelp.relRiskForDeathContStrings),
	PREVALENCES(FileControlEnumHelp.diseasePrevalencesStrings),
	INCIDENCES(FileControlEnumHelp.incidencesStrings),
	RELRISKFORRISKFACTORCATEGORICAL(FileControlEnumHelp.relRiskForRiskfactorCatStrings),
	RELRISKFORRISKFACTORCONTINUOUS(FileControlEnumHelp.relRiskForRiskfactorConStrings),
	RELRISKFROMOTHERDISEASE(FileControlEnumHelp.relRiskFromDiseaseStrings),
	DALYWEIGHTS(FileControlEnumHelp.dALYWeightsStrings);
	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	String rootElementName;
	String rootChildElementName;
	AtomicTypeBase<Number>[] parameterTypes;

	private FileControlEnum(String[] elementNames){
		/* Exceptions not allowed. */
		if (elementNames.length < 4) {
			if(elementNames.length == 0){
			log.error(
					"A FileControlEnum should be based on at least four Strings!");
			} else {
				log.error(
				"Rootelementname: " + elementNames[0] + " A FileControlEnum should be based on at least four Strings!");
			}
		} else{
		rootElementName = elementNames[0];
		rootChildElementName = elementNames[1];
		parameterTypes = new AtomicTypeBase[elementNames.length - 2];
		AtomicTypesSingleton ats = AtomicTypesSingleton.getInstance();
		for (int count = 2; count < elementNames.length ; count++) {
			parameterTypes[count - 2] = ats.get(elementNames[count]);
		}
		}
	}

	public String getRootElementName() {
		return rootElementName;
	}

	public String getRootChildElementName() {
		return rootChildElementName;
	}

	public AtomicTypeBase<Number> getParameterType(int index) {
		return parameterTypes[index];
	}
}
