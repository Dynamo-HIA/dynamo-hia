package nl.rivm.emi.dynamo.data.writers;

import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class that for the moment contains items nescessary to be able to write out
 * XML files containing only repetative parameters contained below a rootelement
 * and a rootchildelement only.
 * 
 * Example: <rootelement> <rootchild> <age>0</age> <sex>0</sex>
 * <value>4.5</value> </rootchild> </rootelement>
 * 
 * The parameters each have a type that contains among others its elementname.
 * 
 * 20090107 RLM Started adding support for different fileformats through
 * entities postfixed with "_MK2".
 * 
 * @author mondeelr
 * 
 */

public enum FileControlEnum {
	POPULATIONSIZE(FileControlEnumHelp.populationSizeStrings), //
	OVERALLMORTALITY(FileControlEnumHelp.overallMortalityStrings), //
	NEWBORNS(FileControlEnumHelp.newbornsStrings), //
	OVERALLDALYWEIGHTS(FileControlEnumHelp.overallDALYWeightsStrings), //
	TRANSITIONDRIFT(FileControlEnumHelp.transitionDriftStrings), //
	TRANSITIONDRIFTNETTO(FileControlEnumHelp.transitionDriftNettoStrings), //
	TRANSITIONMATRIX(FileControlEnumHelp.transitionMatrixStrings), //
	RISKFACTORPREVALENCESCATEGORICAL(
			FileControlEnumHelp.riskFactorPrevalenceCatStrings), //
	RISKFACTORPREVALENCESDURATION(
			FileControlEnumHelp.riskFactorPrevalenceDurStrings), //
	RELRISKFORDEATHCATEGORICAL(FileControlEnumHelp.relRiskForDeathCatStrings), //
	RELRISKFORDEATHCONTINUOUS(FileControlEnumHelp.relRiskForDeathContStrings), //
	RELRISKFORDEATHCOMPOUND(FileControlEnumHelp.relRiskForDeathCompStrings), //
	RISKFACTORCATEGORICAL(FileControlEnumHelp.riskFactorCategoricalStrings), //
	RELRISKFORDISABILITYCATEGORICAL(FileControlEnumHelp.relRiskForDisabilityCatStrings), //
	RELRISKFORDISABILITYCONTINUOUS(FileControlEnumHelp.relRiskForDisabilityContStrings), //
	PREVALENCES(FileControlEnumHelp.diseasePrevalencesStrings), //
	INCIDENCES(FileControlEnumHelp.incidencesStrings), //
	RELRISKFORRISKFACTORCATEGORICAL(
			FileControlEnumHelp.relRiskForRiskfactorCatStrings), //
	RELRISKFORRISKFACTORCONTINUOUS(
			FileControlEnumHelp.relRiskForRiskfactorConStrings), //
	RELRISKFROMOTHERDISEASE(FileControlEnumHelp.relRiskFromDiseaseStrings), //
	RISKFACTORCOMPOUND(
			FileControlEnumHelp.riskFactorCompoundStrings), //
	DALYWEIGHTS(FileControlEnumHelp.dALYWeightsStrings),
	/* Subtrees in the configuration files. */
	CLASSES(FileControlEnumHelp.classesStrings);
	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	String rootElementName;
	String rootChildElementName;
	AtomicTypeBase<Number>[] parameterTypes;
	XMLTagEntity[] parameterTypes_MK2;

	private FileControlEnum(String[] elementNames) {
		/* Exceptions not allowed. */
		if (elementNames.length < 4) {
			if (elementNames.length == 0) {
				log
						.error("A FileControlEnum should be based on at least four Strings!");
			} else {
				log
						.error("Rootelementname: "
								+ elementNames[0]
								+ " A FileControlEnum should be based on at least four Strings!");
			}
		} else {
			rootElementName = elementNames[0];
			rootChildElementName = elementNames[1];
			parameterTypes = new AtomicTypeBase[elementNames.length - 2];
			parameterTypes_MK2 = new XMLTagEntity[elementNames.length - 1];
			XMLTagEntitySingleton ats = XMLTagEntitySingleton.getInstance();
			for (int count = 2; count < elementNames.length; count++) {
				XMLTagEntity intermediate = ats.get(elementNames[count]);
				if (intermediate instanceof AtomicTypeBase) {
					Object theValue = ((AtomicTypeBase) intermediate)
							.getValue();
					if (theValue instanceof Number) {
						parameterTypes[count - 2] = (AtomicTypeBase<Number>) ats
								.get(elementNames[count]);
						parameterTypes_MK2[count - 1] = null;
					} else {
						parameterTypes[count - 2] = null;
						parameterTypes_MK2[count - 1] = ats
								.get(elementNames[count]);
					}
				} else {
					parameterTypes[count - 2] = null;
					parameterTypes_MK2[count - 1] = ats
							.get(elementNames[count]);
				}
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

	public XMLTagEntity getParameterType_MK2(int index) {
		return parameterTypes_MK2[index];
	}
}
