package nl.rivm.emi.dynamo.data.xml.structure;

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
 * 20090313 mondeelr Bij "*fromriskfactor*" en "*fromdisease*" "relative"prefix 
 * teruggebracht naar "rel".
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum RootElementNamesEnum {

	/**
	 * This represents the list of root element names,
	 * used in the xml, xsd, and application
	 */
	
	SIMULATION("simulation", null), // Comment to block reformatting.
	POPULATIONSIZE("populationsize", null), //
	OVERALLMORTALITY("overallmortality", null), //
	NEWBORNS("newborns", null), //
	OVERALLDALYWEIGHTS("overalldalyweights", null), //
	RISKFACTOR_CATEGORICAL("riskfactor_categorical", "Categorical"), //
	RISKFACTOR_CONTINUOUS("riskfactor_continuous", "Continuous"), //
	RISKFACTOR_COMPOUND("riskfactor_compound", "Compound"), //
	TRANSITIONMATRIX("transitionmatrix", null), //
	TRANSITIONMATRIX_ZERO("transitionmatrix_zero", null), //
	TRANSITIONMATRIX_NETTO("transitionmatrix_netto", null), //
	TRANSITIONDRIFT("transitiondrift", null), //
	TRANSITIONDRIFT_ZERO("transitiondrift_zero", null), //
	TRANSITIONDRIFT_NETTO("transitiondrift_netto", null), //
	RISKFACTORPREVALENCES_CATEGORICAL("riskfactorprevalences_categorical", null), //
	RISKFACTORPREVALENCES_CONTINUOUS("riskfactorprevalences_continuous", null), //
	RISKFACTORPREVALENCES_DURATION("riskfactorprevalences_duration", null), //
	RELATIVERISKSFORDEATH_CATEGORICAL("relrisksfordeath_categorical", null), //
	RELATIVERISKSFORDEATH_CONTINUOUS("relrisksfordeath_continuous", null), //
	RELATIVERISKSFORDEATH_COMPOUND("relrisksfordeath_compound", null), //
	RELATIVERISKSFORDISABILITY_CATEGORICAL("relrisksfordisability_categorical", null), //
	RELATIVERISKSFORDISABILITY_CONTINUOUS("relrisksfordisability_continuous", null), //
	RELATIVERISKSFORDISABILITY_COMPOUND("relrisksfordisability_compound", null), //
	DISEASEPREVALENCES("diseaseprevalences", null), //
	DISEASEINCIDENCES("diseaseincidences", null), //
	EXCESSMORTALITY("excessmortality", null), //
	RELATIVERISKSFROMRISKFACTOR_CATEGORICAL(
			"relrisksfromriskfactor_categorical", null), //
	RELATIVERISKSFROMRISKFACTOR_CONTINUOUS(
			"relrisksfromriskfactor_continuous", null), //
	RELATIVERISKSFROMRISKFACTOR_COMPOUND("relrisksfromriskfactor_compound", null), //
	RELATIVERISKSFROMDISEASES("relrisksfromdiseases", null), //
	DALYWEIGHTS("dalyweights", null);

	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	private String rootElementName;
	private String displayName;

	private RootElementNamesEnum(String theRootElementName, String theDisplayName) {
		this.rootElementName = theRootElementName;
		this.displayName = theDisplayName;
	}

	public String getNodeLabel() {
		return rootElementName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
