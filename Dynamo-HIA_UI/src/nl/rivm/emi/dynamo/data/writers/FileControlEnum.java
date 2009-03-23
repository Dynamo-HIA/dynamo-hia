package nl.rivm.emi.dynamo.data.writers;

import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;

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
	/* W01 */
	DYNAMOSIMULATION(FileControlEnumHelp.dynamoSimulationStrings), //
	/* W11 */
	POPULATIONSIZE(FileControlEnumHelp.populationSizeStrings), //
	/* W12 */
	OVERALLMORTALITY(FileControlEnumHelp.overallMortalityStrings), //
	/* W13 */
	NEWBORNS(FileControlEnumHelp.newbornsStrings), //
	/* W14 */
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
	RELRISKFORDISABILITYCATEGORICAL(
			FileControlEnumHelp.relRiskForDisabilityCatStrings), //
	RELRISKFORDISABILITYCONTINUOUS(
			FileControlEnumHelp.relRiskForDisabilityContStrings), //
	PREVALENCES(FileControlEnumHelp.diseasePrevalencesStrings), //
	INCIDENCES(FileControlEnumHelp.incidencesStrings), //
	RELRISKFROMRISKFACTORCATEGORICAL(
			FileControlEnumHelp.relRiskFromRiskfactorCatStrings), //
	RELRISKFROMRISKFACTORCONTINUOUS(
			FileControlEnumHelp.relRiskFromRiskfactorConStrings), //
	RELRISKFROMOTHERDISEASE(FileControlEnumHelp.relRiskFromDiseaseStrings), //
	RISKFACTORCONTINUOUS(FileControlEnumHelp.riskFactorContinuousStrings), //	
	RISKFACTORCOMPOUND(FileControlEnumHelp.riskFactorCompoundStrings), //
	DALYWEIGHTS(FileControlEnumHelp.dALYWeightsStrings),
	/* W21 */
	EXCESSMORTALITY(FileControlEnumHelp.riskFactorCategoricalStrings), //
	/* Subtrees in the configuration files. */
	CLASSES(FileControlEnumHelp.classesStrings), //
	CUTOFFS(FileControlEnumHelp.cutoffsStrings), //
	MORTALITY(FileControlEnumHelp.mortalityStrings), //
	SCENARIOS(FileControlEnumHelp.scenariosStrings), DISEASES(
			FileControlEnumHelp.diseasesStrings), //
	RISKFACTOR(FileControlEnumHelp.riskfactorStrings), //
	RRS(FileControlEnumHelp.rrsStrings);
	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	String rootElementName;
	String rootChildElementName;
	AtomicTypeBase<Number>[] parameterTypes;
	/**
	 * Array of XMLTagEntities for use in the Group factory.
	 */
	XMLTagEntity[] parameterTypes4GroupFactory;

	private FileControlEnum(String[] elementNames) {
		fillParameterTypes(elementNames);
		fillParameterTypes4GroupFactory(elementNames);
	}

	private void fillParameterTypes(String[] elementNames) {
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
			XMLTagEntitySingleton ats = XMLTagEntitySingleton.getInstance();
			for (int count = 2; count < elementNames.length; count++) {
				XMLTagEntity intermediate = ats.get(elementNames[count]);
				if (intermediate instanceof AtomicTypeBase) {
					Object theValue = ((AtomicTypeBase) intermediate)
							.getValue();
					if (theValue instanceof Number) {
						parameterTypes[count - 2] = (AtomicTypeBase<Number>) ats
								.get(elementNames[count]);
					} else {
						parameterTypes[count - 2] = null;
					}
				} else {
					parameterTypes[count - 2] = null;
				}
			}
		}
	}

	/**
	 * Fill the parameterTypes.
	 * 
	 * @param elementNames
	 */
	private void fillParameterTypes4GroupFactory(String[] elementNames) {
		/* Exceptions not allowed. */
		if (elementNames.length < 2) {
			if (elementNames.length == 0) {
				log
						.error("A FileControlEnum should be based on at least four Strings!");
			} else {
				log.error("Rootelementname: " + elementNames[0]
						+ " This FileControlEnum contains no rootChild-names.");
			}
		} else {
			rootElementName = elementNames[0];
			parameterTypes4GroupFactory = new XMLTagEntity[elementNames.length - 1];
			XMLTagEntitySingleton ats = XMLTagEntitySingleton.getInstance();
			for (int count = 1; count < elementNames.length; count++) {
				XMLTagEntity intermediate = ats.get(elementNames[count]);
				parameterTypes4GroupFactory[count - 1] = intermediate;
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

	public XMLTagEntity getParameterType4GroupFactory(int index) {
		return parameterTypes4GroupFactory[index];
	}

	public int getNumberOfParameterTypes4GroupFactory() {
		return parameterTypes4GroupFactory.length;
	}
}
