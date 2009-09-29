package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

public class RiskFactorCategoricalFactory extends AgnosticCategoricalGroupFactory{
//	private Log log = LogFactory.getLog(this.getClass().getName());

	FileControlEnum myEnum = FileControlEnum.RISKFACTORCATEGORICAL;

	public RiskFactorCategoricalObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(configurationFile,false,
				rootNodeName);
		return new RiskFactorCategoricalObject(modelMap);
	}

	public RiskFactorCategoricalObject manufactureObservable(
			File configurationFile, String rootNodeName)
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return new RiskFactorCategoricalObject(modelMap);
	}

	public RiskFactorCategoricalObject manufactureDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super.manufactureDefault(myEnum);
		return new RiskFactorCategoricalObject(modelMap);
	}

	public RiskFactorCategoricalObject manufactureObservableDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super
				.manufactureObservableDefault(myEnum);
		return new RiskFactorCategoricalObject(modelMap);
	}
}
