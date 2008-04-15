package nl.rivm.emi.cdm.exceptions;

public class WrongUpdateRuleException extends Exception {
	public WrongUpdateRuleException(String updateRuleName, String parameterClassName) {
		super(String.format(
				"Updaterule %1$s cannot be used on parameter of type %2$s.",
				updateRuleName, parameterClassName));
	}
}
