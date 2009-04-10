package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;

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

	public String getCurrentValue(String dropDownName);
	
	public void updateObjectState(String name, String selectedValue) throws ConfigurationException;
	
	public void createInDynamoSimulationObject();
	
	public void removeFromDynamoSimulationObject(String selectedValue);
}
