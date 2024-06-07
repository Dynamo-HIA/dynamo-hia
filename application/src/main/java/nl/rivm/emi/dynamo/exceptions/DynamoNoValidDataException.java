package nl.rivm.emi.dynamo.exceptions;


/**
 * Handles DynamoInconsistentData Exceptions
 */

/**
 * @author boshuizh
 *
 */
public class DynamoNoValidDataException extends Exception {
	 static final long serialVersionUID=1 ;
	/**
	 * Default exception constructor
	 * Exception for XML-files that contain arguments that are not valid.
	 * Used to throw out tabs with invalid data
	 */
	public DynamoNoValidDataException() {
		// Continue with no message
	}

	/**
	 * @param message
	 */
	public DynamoNoValidDataException(String message) {
		super(message);
	}

	/**
	 * @param message
	 */
	public DynamoNoValidDataException(Throwable message) {
		super(message);
	}

	/**
	 * @param message
	 * @param throwable exception
	 */
	public DynamoNoValidDataException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
