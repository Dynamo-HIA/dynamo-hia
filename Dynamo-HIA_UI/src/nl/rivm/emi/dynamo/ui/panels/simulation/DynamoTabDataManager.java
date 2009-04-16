package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.observable.value.WritableValue;

import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

/**
 * 
 * Handles the data management of contents drop downs 
 * 
 * @author schutb
 *
 */
public interface DynamoTabDataManager {
	
	public DropDownPropertiesSet getDropDownSet(String name, String selection) throws ConfigurationException;
	
	public Set<String> getContents(String name, String chosenDiseaseName) throws ConfigurationException;

	public String getCurrentValue(String dropDownName) throws ConfigurationException;
	
	public void updateObjectState(String name, String selectedValue) throws ConfigurationException;

	public void updateDynamoSimulationObject();
	
	public DropDownPropertiesSet getRefreshedDropDownSet(String label) throws ConfigurationException;

	public void removeFromDynamoSimulationObject() throws ConfigurationException;

	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException;

	public void removeOldDefaultValue(String label) throws ConfigurationException;

	public WritableValue getCurrentWritableValue(String successRate);

}
