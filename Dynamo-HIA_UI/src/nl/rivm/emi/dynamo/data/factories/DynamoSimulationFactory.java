package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * @author mondeelr
 *
 */
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

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufacture(java.io.File, java.lang.String)
	 */
	public DynamoSimulationObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, false, rootNodeName);
		
		return new DynamoSimulationObject(modelMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureObservable(java.io.File, java.lang.String)
	 */
	public DynamoSimulationObject manufactureObservable(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		
		
		// added by Hendriek april 2013 in order to add the new tag to old configuration files
		if (!modelMap.containsKey("refScenarioName")){
			
			XMLTagEntity name = XMLTagEntitySingleton.getInstance().get("refScenarioName");
			WritableValue value=new WritableValue();
			value.doSetValue(new String("Reference Scenario"));
			AtomicTypeObjectTuple toAdd = new AtomicTypeObjectTuple (name,value);
			
			// needs to be put at the right place after timeStep otherwise things go wrong
			// this is not the solution, maybe the schema
			LinkedHashMap<String, Object> newModelMap =  new LinkedHashMap<String, Object>();
			for (Entry<String, Object> entry:modelMap.entrySet()){
			String key = entry.getKey();
		    Object value2 = entry.getValue();
		    newModelMap.put(key,value2);
		    if (key.equalsIgnoreCase("timeStep"))
		    	newModelMap.put("refScenarioName",toAdd);	
			}
			modelMap=newModelMap;
		}

		
		
		
		return new DynamoSimulationObject(modelMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureDefault()
	 */
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

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureObservableDefault()
	 */
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
