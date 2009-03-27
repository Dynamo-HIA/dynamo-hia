package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

public class DynamoSimulationFactory extends AgnosticGroupFactory {
	// private Log log = LogFactory.getLog(this.getClass().getName());

	FileControlEnum myEnum = null;

	/**
	 * This constructor has been added to force error-detection in the
	 * FileControlEnum entries.
	 * 
	 * @throws DynamoConfigurationException
	 */
	public DynamoSimulationFactory(){
		super();
	}

	public DynamoSimulationObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, false, rootNodeName);
		return new DynamoSimulationObject(modelMap);
	}

	public DynamoSimulationObject manufactureObservable(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return new DynamoSimulationObject(modelMap);
	}

	public DynamoSimulationObject manufactureDefault()
			throws DynamoConfigurationException {
		if(myEnum==null){
			myEnum = FileControlSingleton.getInstance().get(
					RootElementNamesEnum.SIMULATION.getNodeLabel());
		}
		LinkedHashMap<String, Object> modelMap = super
				.manufactureDefault(myEnum);
		return new DynamoSimulationObject(modelMap);
	}

	public DynamoSimulationObject manufactureObservableDefault()
			throws DynamoConfigurationException {
		if(myEnum==null){
			myEnum = FileControlSingleton.getInstance().get(
					RootElementNamesEnum.SIMULATION.getNodeLabel());
		}
		LinkedHashMap<String, Object> modelMap = super
				.manufactureObservableDefault(myEnum);
		return new DynamoSimulationObject(modelMap);
	}
}
