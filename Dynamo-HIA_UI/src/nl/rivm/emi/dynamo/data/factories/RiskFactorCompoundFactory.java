package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.GroupConfigurationObjectServiceLayer;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCompoundObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RiskFactorCompoundFactory extends AgnosticGroupFactory implements CategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	FileControlEnum myEnum = FileControlEnum.RISKFACTORCOMPOUND;

	// String rootNodeName =
	// RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel();

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.setIndexLimit(numberOfCategories);
	}

	public RiskFactorCompoundObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(configurationFile,false,
				rootNodeName);
		return new RiskFactorCompoundObject(modelMap);
	}

	public RiskFactorCompoundObject manufactureObservable(
			File configurationFile, String rootNodeName)
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return new RiskFactorCompoundObject(modelMap);
	}

	public RiskFactorCompoundObject manufactureDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super.manufactureDefault(myEnum);
		return new RiskFactorCompoundObject(modelMap);
	}

	public RiskFactorCompoundObject manufactureObservableDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super
				.manufactureObservableDefault(myEnum);
		return new RiskFactorCompoundObject(modelMap);
	}
}
