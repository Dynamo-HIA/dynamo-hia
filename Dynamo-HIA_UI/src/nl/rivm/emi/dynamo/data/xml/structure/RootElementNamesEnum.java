package nl.rivm.emi.dynamo.data.xml.structure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Enum containing all possible rootelementnames in the dynamo-HIA configuration
 * files.
 * 
 * This Enum was created later on during development and has not been factored
 * in pervasively. All literal references to rootelementnames should eventually
 * be changed to run through this Enum. This will greatly decrease the
 * vulnerability to refactoring resulting in inconsistent names across the codebase.
 * 
 * @author mondeelr
 * 
 */

public enum RootElementNamesEnum {

	SIMULATION("simulation"), // Comment to block reformatting.
	POPULATIONSIZE("populationsize"), //
	OVERALLMORTALITY("overallmortality"), //
	NEWBORNS("newborns"), //
	OVERALLDALYWEIGHTS("overalldalyweights"), RISKFACTOR_CATEGORICAL(
			"riskfactor_categorical"), //
	RISKFACTOR_CONTINUOUS("riskfactor_continuous"), //
	RISKFACTOR_COMPOUND("riskfactor_compound"), //
	TRANSITIONMATRIX("transitionmatrix"), //
	TRANSITIONMATRIX_ZERO("transitionmatrix_zero"), //
	TRANSITIONMATRIX_NETTO("transitionmatrix_netto"), //
	TRANSITIONDRIFT("transitiondrift"), //
	TRANSITIONDRIFT_ZERO("transitiondrift_zero"), //
	TRANSITIONDRIFT_NETTO("transitiondrift_netto"), //
	RISKFACTORPREVALENCES_CATEGORICAL("riskfactorprevalences_categorical"), //
	RISKFACTORPREVALENCES_CONTINUOUS("riskfactorprevalences_continuous"), //
	RISKFACTORPREVALENCES_DURATION("riskfactorprevalences_duration"), //
	RELATIVERISKSFORDEATH_CATEGORICAL("relativerisksfordeath_categorical"), //
	RELATIVERISKSFORDEATH_CONTINUOUS("relativerisksfordeath_continuous"), //
	RELATIVERISKSFORDEATH_COMPOUND("relativerisksfordeath_compound"), //
	DISEASEPREVALENCES("diseaseprevalences"), //
	DISEASEINCIDENCES("diseaseincidences"), //
	EXCESSMORTALITY("excessmortality"), //
	RELATIVERISKSFROMRISKFACTOR_CATEGORICAL(
			"relativerisksfromriskfactor_categorical"), //
	RELATIVERISKSFROMRISKFACTOR_CONTINUOUS(
			"relativerisksfromriskfactor_continuous"), //
	RELATIVERISKSFROMRISKFACTOR_COMPOUND("relativerisksfromriskfactor_compound"), //
	RELATIVERISKSFROMDISEASES("relativerisksfromdiseases"), //
	DALYWEIGHTS("dalyweights");

	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	private String rootElementName;

	private RootElementNamesEnum(String initialRootElementName) {
		this.rootElementName = initialRootElementName;
	}

	public String getNodeLabel() {
		return rootElementName;
	}
}
