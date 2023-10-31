package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

/**
 * @author mondeelr
 *
 */
public class ExcessMortalityFactory extends AgnosticGroupFactory {
	// private Log log = LogFactory.getLog(this.getClass().getName());

	FileControlEnum myEnum = null;

	/**
	 * This constructor has been added to force error-detection in the
	 * FileControlEnum entries.
	 * 
	 * @throws DynamoConfigurationException
	 */
	public ExcessMortalityFactory(){
		super();
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufacture(java.io.File, java.lang.String)
	 */
	public ExcessMortalityObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, false, rootNodeName);
		return new ExcessMortalityObject(modelMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureObservable(java.io.File, java.lang.String)
	 */
	public ExcessMortalityObject manufactureObservable(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return new ExcessMortalityObject(modelMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureDefault()
	 */
	public ExcessMortalityObject manufactureDefault()
			throws DynamoConfigurationException {
		if(myEnum==null){
			myEnum = FileControlSingleton.getInstance().get(
					RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel());
		}
		LinkedHashMap<String, Object> modelMap = super
				.manufactureDefault(myEnum);
		return new ExcessMortalityObject(modelMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureObservableDefault()
	 */
	public ExcessMortalityObject manufactureObservableDefault()
			throws DynamoConfigurationException {
		if(myEnum==null){
			myEnum = FileControlSingleton.getInstance().get(
					RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel());
		}
		LinkedHashMap<String, Object> modelMap = super
				.manufactureObservableDefault(myEnum);
		return new ExcessMortalityObject(modelMap);
	}
}
