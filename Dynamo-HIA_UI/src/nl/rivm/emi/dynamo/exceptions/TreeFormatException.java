package nl.rivm.emi.dynamo.exceptions;

/**
 * 
 * Handles DynamoScenario Exceptions
 * 
 * @author schutb
 *
 */
public class TreeFormatException extends Exception {

	/**
	 * Needed to serialize
	 */
	private static final long serialVersionUID = -6872755950180376130L;

	/**
	 * @param message
	 */
	public TreeFormatException(String message) {
		super(message);
	}

}
