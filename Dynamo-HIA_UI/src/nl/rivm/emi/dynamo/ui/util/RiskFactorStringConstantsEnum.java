package nl.rivm.emi.dynamo.ui.util;
/**
 * Constants used for communication in the menu and action mechanism.
 * @author mondeelr
 *
 */
public enum RiskFactorStringConstantsEnum {
	RISKFACTORPREVALENCES("riskfactorprevalences", "risk factor prevalences file"),
	RISKFACTORRELATIVERISKSFORDEATH("riskfactorrelativerisksfordeath", "risk factor relative risks for death file"),
	RISKFACTORRELATIVERISKSFORDISABILITY("riskfactorrelativerisksfordisability", "risk factor relative risks for disability file");
	
	final private String myContent;
	final private String myPR;

	private RiskFactorStringConstantsEnum(String myContent, String myPR) {
		this.myContent = myContent;
		this.myPR = myPR;
	}
/**
 * No comment needed. 
 *  
 * @return
 */
	public String getMyContent() {
		return myContent;
	}
public String getMyPR() {
	return myPR;
}

}
