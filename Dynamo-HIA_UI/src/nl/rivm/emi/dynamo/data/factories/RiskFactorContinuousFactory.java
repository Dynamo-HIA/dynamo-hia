package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.RiskFactorContinuousObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

public class RiskFactorContinuousFactory extends AgnosticGroupFactory {
//	private Log log = LogFactory.getLog(this.getClass().getName());

	Integer numberOfCategories = null;
	FileControlEnum myEnum = FileControlEnum.RISKFACTORCONTINUOUS;

	public RiskFactorContinuousObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(configurationFile,false,
				rootNodeName);
		return new RiskFactorContinuousObject(modelMap);
	}

	public RiskFactorContinuousObject manufactureObservable(
			File configurationFile, String rootNodeName)
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return new RiskFactorContinuousObject(modelMap);
	}

	public RiskFactorContinuousObject manufactureDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super.manufactureDefault(myEnum);
		return new RiskFactorContinuousObject(modelMap);
	}

	public RiskFactorContinuousObject manufactureObservableDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super
				.manufactureObservableDefault(myEnum);
		return new RiskFactorContinuousObject(modelMap);
	}
}
