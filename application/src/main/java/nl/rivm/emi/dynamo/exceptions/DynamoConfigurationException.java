package nl.rivm.emi.dynamo.exceptions;

import org.apache.commons.configuration.ConfigurationException;

/**
 * 
Exception thrown in case of an error in DynamoConfiguration
 * 
 * @author schutb
 *
 */
public class DynamoConfigurationException extends ConfigurationException {

	/**
	 * Needed to serialize
	 */
	private static final long serialVersionUID = 452761140447015751L;

	/**
	 * @param message
	 */
	public DynamoConfigurationException(String message) {
		super(message);
	}

}
