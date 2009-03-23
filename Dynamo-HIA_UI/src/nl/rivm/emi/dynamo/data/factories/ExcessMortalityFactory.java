package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExcessMortalityFactory extends AgnosticGroupFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	Integer numberOfMortalities = null;
	FileControlEnum myEnum = FileControlEnum.EXCESSMORTALITY;

	public void setNumberOfMortalities(Integer numberOfMortalities) {
		this.numberOfMortalities = numberOfMortalities;
	}

	public ExcessMortalityObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(configurationFile,false,
				rootNodeName);
		return new ExcessMortalityObject(modelMap);
	}

	public ExcessMortalityObject manufactureObservable(
			File configurationFile, String rootNodeName)
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return new ExcessMortalityObject(modelMap);
	}

	public ExcessMortalityObject manufactureDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super.manufactureDefault(myEnum);
		return new ExcessMortalityObject(modelMap);
	}

	public ExcessMortalityObject manufactureObservableDefault()
			throws DynamoConfigurationException {
		LinkedHashMap<String, Object> modelMap = super
				.manufactureObservableDefault(myEnum);
		return new ExcessMortalityObject(modelMap);
	}
}
