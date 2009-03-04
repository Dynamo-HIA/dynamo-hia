package nl.rivm.emi.dynamo.exceptions;

import org.apache.commons.configuration.ConfigurationException;

/**
 * 
 * Handles DynamoScenario Exceptions
 * 
 * @author schutb
 *
 */
public class DynamoScenarioException extends ConfigurationException {

	
	private static final long serialVersionUID = 4543192747231032340L;

	/**
	 * @param message
	 */
	public DynamoScenarioException(String message) {
		super(message);
	}

}
