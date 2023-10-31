package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

/**
 * @author mondeelr
 *
 */
public class NewbornsFactory extends AgnosticGroupFactory {
//		private Log log = LogFactory.getLog(this.getClass().getName());

		FileControlEnum myEnum = FileControlEnum.NEWBORNS;

		/* (non-Javadoc)
		 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufacture(java.io.File, java.lang.String)
		 */
		public NewbornsObject manufacture(File configurationFile,
				String rootNodeName) throws ConfigurationException,
				DynamoInconsistentDataException {
			LinkedHashMap<String, Object> modelMap = super.manufacture(configurationFile,false,
					rootNodeName);
			return new NewbornsObject(modelMap);
		}

		/* (non-Javadoc)
		 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureObservable(java.io.File, java.lang.String)
		 */
		public NewbornsObject manufactureObservable(
				File configurationFile, String rootNodeName)
				throws ConfigurationException, DynamoInconsistentDataException {
			LinkedHashMap<String, Object> modelMap = super.manufacture(
					configurationFile, true, rootNodeName);
			return new NewbornsObject(modelMap);
		}

		/* (non-Javadoc)
		 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureDefault()
		 */
		public NewbornsObject manufactureDefault()
				throws DynamoConfigurationException {
			LinkedHashMap<String, Object> modelMap = super.manufactureDefault(myEnum);
			return new NewbornsObject(modelMap);
		}

		/* (non-Javadoc)
		 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureObservableDefault()
		 */
		public NewbornsObject manufactureObservableDefault()
				throws DynamoConfigurationException {
			LinkedHashMap<String, Object> modelMap = super
					.manufactureObservableDefault(myEnum);
			return new NewbornsObject(modelMap);
		}
	}
