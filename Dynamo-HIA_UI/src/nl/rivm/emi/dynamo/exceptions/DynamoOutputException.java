package nl.rivm.emi.dynamo.exceptions;

/**
 * 
 * Exception thrown in case of an error in DynamoOutput
 * 
 * @author schutb
 *
 */
@SuppressWarnings("serial")
public class DynamoOutputException extends Exception {

	/**
	 * @param string
	 */
	public DynamoOutputException(String string) {
		super(string);
	}
	
}
