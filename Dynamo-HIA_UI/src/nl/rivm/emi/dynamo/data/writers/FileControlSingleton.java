package nl.rivm.emi.dynamo.data.writers;

/**
 * Map to find the type by its corresponding tagname.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileControlSingleton extends HashMap<String, FileControlEnum> {
	Log log = LogFactory.getLog(this.getClass().getName());
	private static FileControlSingleton instance = null;

	private FileControlSingleton() throws DynamoConfigurationException {
		super();
		StringBuffer exceptionMessage = new StringBuffer();
		Object test = FileControlEnum.values();
		for (FileControlEnum type : FileControlEnum.values()) {
			log.debug("Getting: " + type + " putting it with name " + type.getRootElementName());
			if (type != null) {
				String errorMessage = type.getErrorMessage();
				if ((errorMessage != null) && (errorMessage.length() > 0)) {
					exceptionMessage.append("While getting: " + type.getParameterType(0).getClass().getName() 
							+ " with rootelementname " + type.getRootElementName() + "\n");
					exceptionMessage.append(errorMessage + "\n");
				}
				put(type.getRootElementName(), type);
			}
		}
		if (exceptionMessage.length() > 0) {
			throw new DynamoConfigurationException(exceptionMessage.toString());
		}

	}

	synchronized static public FileControlSingleton getInstance()
			throws DynamoConfigurationException {
		if (instance == null) {
			instance = new FileControlSingleton();
		}
		return instance;
	}
}
