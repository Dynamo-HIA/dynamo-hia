package nl.rivm.emi.dynamo.data.writers;

/**
 * Map to find the type by its corresponding tagname.
 */
import java.util.HashMap;

import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
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
			String typeRootElementName = null;
			if(type != null){
				typeRootElementName = type.getRootElementName();
			}
			log.debug("Getting: " + type + " putting it with name "
					+ typeRootElementName);
			if (type != null) {
				String errorMessage = type.getErrorMessage();
				if ((errorMessage != null) && (errorMessage.length() > 0)) {
					log.debug("Type: " + type.getRootElementName()
							+ " returns errorMessage: " + errorMessage);
					AtomicTypeBase<Number> result = type.getParameterType(0);
					String resultClassName = null;
					if (result != null) {
						resultClassName = result.getClass().getName();
					}
					exceptionMessage.append("While getting: " + resultClassName
							+ " with rootelementname "
							+ type.getRootElementName() + "\n");
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
