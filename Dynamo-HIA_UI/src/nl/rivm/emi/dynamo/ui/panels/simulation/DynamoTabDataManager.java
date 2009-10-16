package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * 
 * Defines an interface for the management of 
 * the simulation object data of all tabs  
 * 
 * @author schutb
 *
 */
public interface DynamoTabDataManager {
	
	//RelRisksCollectionForDropdown availlableRRs = null;
	//TabRelativeRiskConfigurationData singleConfiguration = null;
	//Map<Integer, TabRelativeRiskConfigurationData> configurations = null;

	public DropDownPropertiesSet getDropDownSet(String name, String selection) throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException;
	
	public Set<String> getContents(String name, String chosenDiseaseName) throws ConfigurationException, NoMoreDataException;

	public String getValueFromSingleConfiguration(String dropDownName) throws ConfigurationException;
	
	public void updateObjectState(String name, String selectedValue) throws ConfigurationException, NoMoreDataException;

	public void updateDynamoSimulationObject() throws ConfigurationException;
	
	public DropDownPropertiesSet getRefreshedDropDownSet(String label) throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException;

	public void removeFromDynamoSimulationObject() throws ConfigurationException;

	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException;

	public void removeOldDefaultValue(String label) throws ConfigurationException;

	public WritableValue getCurrentWritableValue(String successRate);

	public DynamoSimulationObject getDynamoSimulationObject();
}
