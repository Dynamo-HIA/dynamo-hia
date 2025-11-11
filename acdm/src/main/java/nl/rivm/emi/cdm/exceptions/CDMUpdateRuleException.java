package nl.rivm.emi.cdm.exceptions;

public class CDMUpdateRuleException extends Exception {

	/**
	 * serial id not used
	 */
	private static final long serialVersionUID = 3L;

	public static final String wrongParameterMessage = "This updaterule cannot be used with parameter of type %1$s.";

	public static final String finalParameterMessage = "Updaterule %1$s: Parameter %2$s cannot be changed.";

	public CDMUpdateRuleException(String message) {
		super(message);
	}

	public CDMUpdateRuleException(String updateRuleName,
			String parameterClassName) {
		super(String.format(
				"Updaterule %1$s cannot be used on parameter of type %2$s.",
				updateRuleName, parameterClassName));
	}
}
