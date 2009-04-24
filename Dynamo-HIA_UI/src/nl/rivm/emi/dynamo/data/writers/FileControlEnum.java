package nl.rivm.emi.dynamo.data.writers;

import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;
import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

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
 * 20090325 mondeelr REfactored "old" functionality to use the new typearray.
 * 
 * @author mondeelr
 * 
 */

public enum FileControlEnum {
	/* W01 */
	DYNAMOSIMULATION(FileControlEnumHelp.dynamoSimulationStrings, true, false), //
	/* W11 */
	POPULATIONSIZE(FileControlEnumHelp.populationSizeStrings, false, false), //
	/* W12 */
	OVERALLMORTALITY(FileControlEnumHelp.overallMortalityStrings, false, false), //
	/* W13 */
	NEWBORNS(FileControlEnumHelp.newbornsStrings, false, false), //
	/* W14 */
	OVERALLDALYWEIGHTS(FileControlEnumHelp.overallDALYWeightsStrings, false,
			false), //
	TRANSITIONDRIFT(FileControlEnumHelp.transitionDriftStrings, false, false), //
	TRANSITIONDRIFTZERO(FileControlEnumHelp.transitionDriftZeroStrings,
			false, false), //
			TRANSITIONDRIFTNETTO(FileControlEnumHelp.transitionDriftNettoStrings,
					false, false), //
	TRANSITIONMATRIX(FileControlEnumHelp.transitionMatrixStrings, false, false), //
	TRANSITIONMATRIX_ZERO(FileControlEnumHelp.transitionMatrixZeroStrings, false, false), //
	TRANSITIONMATRIX_NETTO(FileControlEnumHelp.transitionMatrixNettoStrings, false, false), //
	RISKFACTORPREVALENCESCATEGORICAL(
			FileControlEnumHelp.riskFactorPrevalenceCatStrings, false, false), //
	RISKFACTORPREVALENCESCONTINUOUS(
					FileControlEnumHelp.riskFactorPrevalenceConStrings, true, false), //
	RISKFACTORPREVALENCESDURATION(
			FileControlEnumHelp.riskFactorPrevalenceDurStrings, false, false), //
	RELRISKFORDEATHCATEGORICAL(FileControlEnumHelp.relRiskForDeathCatStrings,
			false, false), //
	RELRISKFORDEATHCONTINUOUS(FileControlEnumHelp.relRiskForDeathContStrings,
			false, false), //
	RELRISKFORDEATHCOMPOUND(FileControlEnumHelp.relRiskForDeathCompStrings,
			false, false), //
	RISKFACTORCATEGORICAL(FileControlEnumHelp.riskFactorCategoricalStrings,
			true, false), //
	RELRISKFORDISABILITYCATEGORICAL(
			FileControlEnumHelp.relRiskForDisabilityCatStrings, false, false), //
	RELRISKFORDISABILITYCONTINUOUS(
			FileControlEnumHelp.relRiskForDisabilityContStrings, false, false), //
	PREVALENCES(FileControlEnumHelp.diseasePrevalencesStrings, false, false), //
	INCIDENCES(FileControlEnumHelp.incidencesStrings, false, false), //
	RELRISKFROMRISKFACTORCATEGORICAL(
			FileControlEnumHelp.relRiskFromRiskfactorCatStrings, false, false), //
	RELRISKFROMRISKFACTORCONTINUOUS(
			FileControlEnumHelp.relRiskFromRiskfactorConStrings, false, false), //
	RELRISKFROMOTHERDISEASE(FileControlEnumHelp.relRiskFromDiseaseStrings,
			false, false), //
	RISKFACTORCONTINUOUS(FileControlEnumHelp.riskFactorContinuousStrings, true,
			false), //	
	RISKFACTORCOMPOUND(FileControlEnumHelp.riskFactorCompoundStrings, true,
			false), //
	DALYWEIGHTS(FileControlEnumHelp.dALYWeightsStrings, false, false),
	/* W21 */
	EXCESSMORTALITY(FileControlEnumHelp.excessMortalityStrings, true, false), //
	/*
	 * Subtrees in the configuration files. Should have the second parameter
	 * flag set to true.
	 */
	AMOUNT(FileControlEnumHelp.amountStrings, false, true),
	CLASSES(FileControlEnumHelp.classesStrings, false, true), //
	CUTOFFS(FileControlEnumHelp.cutoffsStrings, false, true), //
	MORTALITIES(FileControlEnumHelp.mortalitiesStrings, false, true), //
	MORTALITY(FileControlEnumHelp.mortalityStrings, false, true), //
	SCENARIOS(FileControlEnumHelp.scenariosStrings, false, true), //
	DISEASES(FileControlEnumHelp.diseasesStrings, false, true), //
	PREVALENCESCONTINUOUS(FileControlEnumHelp.prevalencesContinuousStrings, false, true), //
	RISKFACTORS(FileControlEnumHelp.riskfactorsStrings, false, true), //
	RRS(FileControlEnumHelp.rrsStrings, false, true);
	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	private boolean isGroupEnum;
	private boolean isRootChildEnum;
	// "Old" functionality, refactored out for a more logical setup.
	// String rootElementName;
	// String rootChildElementName;
	// AtomicTypeBase<Number>[] parameterTypes;
	/**
	 * Array of XMLTagEntities for use in the Group factory.
	 */
	private XMLTagEntity[] parameterTypes4GroupFactory;
	private int rootElementCount = 0;
	private int wrapperCount = 0;
	private int containerCount = 0;
	private int payloadCount = 0;
	/**
	 * ErrorMessage to display at first access.
	 */
	StringBuffer errorMessage = new StringBuffer();

	/**
	 * 
	 * @param elementNames
	 * @param isGroupEnum
	 *            This type must be processed with an AgnosticGroupFactory. When
	 *            this flag is false the AgnosticFactory must be used.
	 * @param isRootChildEnum
	 *            This type is used
	 */

	private FileControlEnum(String[] elementNames, boolean isGroupEnum,
			boolean isRootChildEnum) {
		this.isGroupEnum = isGroupEnum;
		this.isRootChildEnum = isRootChildEnum;
		// fillParameterTypes(elementNames);
		fillParameterTypes4GroupFactory(elementNames);
	}

	/**
	 * Fill the parameterTypes.
	 * 
	 * @param elementNames
	 */
	private void fillParameterTypes4GroupFactory(String[] elementNames) {
		/* Exceptions not allowed. */
		if (elementNames.length == 0) {
			log
					.fatal("A FileControlEnum should be based on at least one String!");
		} else {
			// rootElementName = elementNames[0];
			parameterTypes4GroupFactory = new XMLTagEntity[elementNames.length];
			XMLTagEntitySingleton ats = XMLTagEntitySingleton.getInstance();
			for (int count = 0; count < elementNames.length; count++) {
				log.debug("Getting XMLTagEntity for name: "
						+ elementNames[count]);
				XMLTagEntity intermediate = ats.get(elementNames[count]);
				log.debug("Result: " + intermediate);
				// Always fill the counters.
				boolean syntaxIsWrong = syntaxIsWrong(elementNames, count,
						intermediate);
				if (isRootChildEnum && syntaxIsWrong) {
					break;
				}
				parameterTypes4GroupFactory[count] = intermediate;
			}
		}
	}

	/**
	 * Check Crooked construction because constructors of Objects used in
	 * Enumerations are not allowed to throw Exceptions.
	 * 
	 * @param elementNames
	 * @param count
	 * @param intermediate
	 * @return
	 */
	private boolean syntaxIsWrong(String[] elementNames, int count,
			XMLTagEntity intermediate) {
		boolean doBreak = false;
		int previousTotal = rootElementCount + wrapperCount + containerCount
				+ payloadCount;
		if (intermediate == null) {
			errorMessage.append("FileControl: " + elementNames[0]
					+ " is not an XMLTagEntity.\n");
			doBreak = true;
		} else {
			// / RootElementTypes should always be single and first.
			if (intermediate instanceof RootElementType) {
				rootElementCount++;
				if ((rootElementCount > 1) || (wrapperCount != 0)
						|| (containerCount != 0) || (payloadCount != 0)) {
					errorMessage.append("FileControl strings for "
							+ elementNames[0]
							+ " have wrong structure at zero based index: "
							+ count + ", type: "
							+ intermediate.getClass().getName() + "\n"
							+ " rootElementCount: " + rootElementCount
							+ " wrapperCount: " + wrapperCount
							+ " containerCount: " + containerCount
							+ " payloadCount: " + payloadCount + "\n");
					doBreak = true;
				}
			}
			if (intermediate instanceof WrapperType) {
				wrapperCount++;
				// if (payloadCount != 0) {
				// errorMessage.append("FileControl strings for "
				// + elementNames[0]
				// + " have wrong structure at zero based index: "
				// + count + ", type: "
				// + intermediate.getClass().getName() + "\n"
				// + " rootElementCount: " + rootElementCount
				// + " wrapperCount: " + wrapperCount
				// + " containerCount: " + containerCount
				// + " payloadCount: " + payloadCount + "\n");
				// doBreak = true;
				// }
			}
			if (intermediate instanceof ContainerType) {
				containerCount++;
				// if (payloadCount != 0) {
				// errorMessage.append("FileControl strings for "
				// + elementNames[0]
				// + " have wrong structure at zero based index: "
				// + count + ", type: "
				// + intermediate.getClass().getName() + "\n"
				// + " rootElementCount: " + rootElementCount
				// + " wrapperCount: " + wrapperCount
				// + " containerCount: " + containerCount
				// + " payloadCount: " + payloadCount + "\n");
				// doBreak = true;
				// }
			}
			if (intermediate instanceof PayloadType) {
				payloadCount++;
			}
			int newTotal = rootElementCount + wrapperCount + containerCount
					+ payloadCount;
			if ((newTotal - previousTotal) != 1) {
				errorMessage
						.append("FileControl strings for "
								+ elementNames[0]
								+ " should have either a wrapper-, container- or patloadType at zero based index: "
								+ count + ", type: "
								+ intermediate.getClass().getName() + "\n"
								+ " rootElementCount: " + rootElementCount
								+ " wrapperCount: " + wrapperCount
								+ " containerCount: " + containerCount
								+ " payloadCount: " + payloadCount + "\n");
				doBreak = true;
			}
		}
		return doBreak;
	}

	/**
	 * "Old" functionality. Refactored to work with the new parameterarray.
	 * 
	 * @return
	 */
	public String getRootElementName() {
		String result = null;
		result = parameterTypes4GroupFactory[0].getXMLElementName();
		return result;
	}

	public String getRootChildElementName() {
		// return rootChildElementName;
		String result = null;
//		if (!isRootChildEnum) {
			result = parameterTypes4GroupFactory[1].getXMLElementName();
//		}
		return result;
	}

	/**
	 * "Old" functionality refactored to work with the new array.
	 */
	public AtomicTypeBase<Number> getParameterType(int index) {
		AtomicTypeBase<Number> result = null;
		if (!((index + 2) >= parameterTypes4GroupFactory.length)) {
		if (parameterTypes4GroupFactory[index + 2] instanceof AtomicTypeBase) {
			result = (AtomicTypeBase<Number>) parameterTypes4GroupFactory[index + 2];
			log.debug("AtomicTypeBase: " + result.getXMLElementName() +" found at index: " + (index +2) + " in the FileControlEnum for " + parameterTypes4GroupFactory[0]);
		} else {
			log.fatal("No AtomicTypeBase found at index: " + (index +2) + " in the FileControlEnum for " + parameterTypes4GroupFactory[0]);
		}
		} else {
			log.fatal("Index: " + (index + 2) + " is past the end of the tags given for: " + parameterTypes4GroupFactory[0]);
		}
		return result;
	}

	public XMLTagEntity getParameterType4GroupFactory(int index) {
		XMLTagEntity result = null;
		if (isGroupEnum || isRootChildEnum) {
			result = parameterTypes4GroupFactory[index];
		} else {
			// Refer for "old"configurationfiles.
			getParameterType(index);
		}
		return result;
	}

	public int getNumberOfParameterTypes4GroupFactory() {
		return parameterTypes4GroupFactory.length;
	}

	public String getErrorMessage() {
		return errorMessage.toString();
	}

	public boolean isRootChildEnum() {
		return isRootChildEnum;
	}

	public int getWrapperCount() {
		return wrapperCount;
	}

	public int getContainerCount() {
		return containerCount;
	}

	public int getPayloadCount() {
		return payloadCount;
	}

	/**
	 * First attempt to return sensible numbers for different cases.
	 * 
	 * @return
	 */
	public int getNumberOfRootChildren() {
		int result = 0;
		if (!isGroupEnum && !isRootChildEnum) {
			result = 1;
		} else {
			if (isGroupEnum) {
				result = parameterTypes4GroupFactory.length - 1;
			}
		}
		return result;
	}

	public boolean isGroupEnum() {
		return isGroupEnum;
	}
}
