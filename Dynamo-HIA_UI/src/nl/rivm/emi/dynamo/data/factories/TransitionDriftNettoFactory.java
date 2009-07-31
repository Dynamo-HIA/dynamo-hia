package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransitionDriftNettoFactory extends AgnosticGroupFactory implements
		RootLevelFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());
	FileControlEnum myFileControlEnum = FileControlEnum.TRANSITIONDRIFT_NETTO;

	@Override
	public LinkedHashMap<String, Object> manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, false, rootNodeName);
		return modelMap;
	}

	@Override
	public LinkedHashMap<String, Object> manufactureDefault() throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super.manufactureDefault(myFileControlEnum);
		return modelMap;
	}

	@Override
	public LinkedHashMap<String, Object> manufactureObservable(
			File configurationFile, String rootNodeName)
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return modelMap;
	}

	@Override
	public LinkedHashMap<String, Object> manufactureObservableDefault() throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super.manufactureObservableDefault(myFileControlEnum);
		return modelMap;
	}
}
