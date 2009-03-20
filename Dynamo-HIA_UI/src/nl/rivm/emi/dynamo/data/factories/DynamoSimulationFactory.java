package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.GroupConfigurationObjectServiceLayer;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCompoundObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DynamoSimulationFactory extends AgnosticGroupFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	Integer numberOfCategories = null;
	FileControlEnum myEnum = FileControlEnum.DYNAMOSIMULATION;

	public DynamoSimulationObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(configurationFile,false,
				rootNodeName);
		return new DynamoSimulationObject(modelMap);
	}

	public DynamoSimulationObject manufactureObservable(
			File configurationFile, String rootNodeName)
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return new DynamoSimulationObject(modelMap);
	}

	public DynamoSimulationObject manufactureDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super.manufactureDefault(myEnum);
		return new DynamoSimulationObject(modelMap);
	}

	public DynamoSimulationObject manufactureObservableDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super
				.manufactureObservableDefault(myEnum);
		return new DynamoSimulationObject(modelMap);
	}
}
