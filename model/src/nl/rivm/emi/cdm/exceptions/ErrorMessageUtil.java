package nl.rivm.emi.cdm.exceptions;

import org.apache.commons.logging.Log;

/**
 * 
 * Handles the error messages, in case the root cause 
 * has to be shown
 * 
 * @author schutb
 *
 */
public class ErrorMessageUtil {

	
	/**
	 * 
	 * Handles the error messages provided, in case the root cause 
	 * has to be shown
	 * 
	 * @param log
	 * @param cdmErrorMessage
	 * @param e
	 * @param fileName
	 * @throws DynamoConfigurationException
	 */
	public static void handleErrorMessage(Log log, String cdmErrorMessage,
			Exception e, String fileName) throws DynamoConfigurationException {
		e.printStackTrace();
		// Show the error message and the nested cause of the error
		String errorMessage = "";		
		if (e.getCause() != null) {
			if (!e.getCause().getMessage().contains(":")) {
				errorMessage = "An error occured: " + e.getMessage() + "\n"
						+ "Cause: " + e.getCause().getMessage();
			} else {
				errorMessage = "An error occured: " + e.getMessage() + "\n"
						+ "Cause: ";				
				String[] splits = e.getCause().getMessage().split(":"); 				
				for (int i = 1; i < splits.length; i++) {
					errorMessage += splits[i];
				}
			}			
			errorMessage += " related to file: " + fileName;
		} else {
			errorMessage = cdmErrorMessage;
		}
		log.error(errorMessage);
		throw new DynamoConfigurationException(errorMessage);
	}	
}
