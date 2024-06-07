package nl.rivm.emi.cdm.fromdynamo;


/**
 * Handles DynamoInconsistentData Exceptions
 */

/**
 * @author boshuizh
 *
 */
public class DynamoInconsistentDataException extends Exception {
	 static final long serialVersionUID=1 ;
	/**
	 * Default exception constructor
	 */
	public DynamoInconsistentDataException() {
		// Continue with no message
	}

	/**
	 * @param message
	 */
	public DynamoInconsistentDataException(String message) {
		super(message);
	}

	/**
	 * @param message
	 */
	public DynamoInconsistentDataException(Throwable message) {
		super(message);
	}

	/**
	 * @param message
	 * @param throwable exception
	 */
	public DynamoInconsistentDataException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
