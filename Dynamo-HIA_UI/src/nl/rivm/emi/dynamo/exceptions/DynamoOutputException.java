package nl.rivm.emi.dynamo.exceptions;

/**
 * 
 * Exception thrown in case of an error in DynamoOutput
 * 
 * @author schutb
 *
 */
public class DynamoOutputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4663216515410602278L;

	/**
	 * @param message
	 */
	public DynamoOutputException(String message) {
		super(message);
	}
	
}
