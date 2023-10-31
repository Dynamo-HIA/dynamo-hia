package nl.rivm.emi.dynamo.ui.main.structure;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.main.ExcessMortalityModal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Enum containing all possible  bullet button names in the DYNAMO-HIA configuration
 * files: InputBulletsFreeXMLFileAction, InputBulletsTrialog
 * 
 * @author mondeelr
 * 
 */

public enum BulletButtonNamesEnum {

	TRANSITION_ZERO("Zero", null),
	TRANSITION_USER_SPECIFIED("User specified", null), //
	TRANSITION_NETTO("Nett", null), //
	DURATION_USER_SPECIFIED("User specified", "riskfactorprevalences_duration"), //
	DURATION_UNIFORM("Uniform", "riskfactorprevalences_duration_uniform"), //
	ACUTELY_FATAL(ExcessMortalityModal.ParameterTypeHelperClass.ACUTELY_FATAL,RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel()), //
	CURED_FRACTION(ExcessMortalityModal.ParameterTypeHelperClass.CURED_FRACTION,RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel());

	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	private String bulletButtonName;
	private String rootElementName;

	private BulletButtonNamesEnum(String bulletButtonName, String rootElementName) {
		this.bulletButtonName = bulletButtonName;
		this.rootElementName = rootElementName;
	}

	public String getBulletButtonName() {
		return bulletButtonName;
	}

	public String getRootElementName() {
		return rootElementName;
	}
}
