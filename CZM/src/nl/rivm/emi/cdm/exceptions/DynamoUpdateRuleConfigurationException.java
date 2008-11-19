package nl.rivm.emi.cdm.exceptions;

public class DynamoUpdateRuleConfigurationException extends Exception  {
	
	
	public static final String wrongParameterMessage = "This updaterule cannot be used with parameter of type %1$s.";

	public static final String finalParameterMessage = "Updaterule %1$s: Parameter %2$s cannot be changed.";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	public DynamoUpdateRuleConfigurationException(String message) {super(message);
		// TODO Auto-generated constructor stub
	}

}
