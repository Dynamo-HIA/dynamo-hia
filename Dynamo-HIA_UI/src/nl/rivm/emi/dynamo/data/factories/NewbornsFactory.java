package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

public class NewbornsFactory extends AgnosticGroupFactory {
//		private Log log = LogFactory.getLog(this.getClass().getName());

		FileControlEnum myEnum = FileControlEnum.NEWBORNS;

		public NewbornsObject manufacture(File configurationFile,
				String rootNodeName) throws ConfigurationException,
				DynamoInconsistentDataException {
			LinkedHashMap<String, Object> modelMap = super.manufacture(configurationFile,false,
					rootNodeName);
			return new NewbornsObject(modelMap);
		}

		public NewbornsObject manufactureObservable(
				File configurationFile, String rootNodeName)
				throws ConfigurationException, DynamoInconsistentDataException {
			LinkedHashMap<String, Object> modelMap = super.manufacture(
					configurationFile, true, rootNodeName);
			return new NewbornsObject(modelMap);
		}

		public NewbornsObject manufactureDefault()
				throws DynamoConfigurationException {
			LinkedHashMap<String, Object> modelMap = super.manufactureDefault(myEnum);
			return new NewbornsObject(modelMap);
		}

		public NewbornsObject manufactureObservableDefault()
				throws DynamoConfigurationException {
			LinkedHashMap<String, Object> modelMap = super
					.manufactureObservableDefault(myEnum);
			return new NewbornsObject(modelMap);
		}
	}
