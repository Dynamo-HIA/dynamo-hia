package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;

public class DynamoSimulationHeaderDataManager implements DynamoTabDataManager {

	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject configuration;

	public DynamoSimulationHeaderDataManager(BaseNode selectedNode, 
			DynamoSimulationObject configuration
			) throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.configuration = configuration;
	}
	
	@Override
	public Set<String> getContents(String name, String chosenName) {
		Set<String> contents = new LinkedHashSet<String>();
		if (DynamoHeaderDataPanel.POP_FILE_NAME.equals(name)) {
			contents = this.treeLists.getPopulations();
		}
		return contents;
	}

	@Override
	public String getCurrentValue(String dropDownName) {		
		String value = null;
		if (DynamoHeaderDataPanel.POP_FILE_NAME.equals(dropDownName)) {			
			value = configuration.getPopulationFileName();
		}
		return value;
	}

	@Override
	public DropDownPropertiesSet getDropDownSet(String name, String selection) {
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		set.addAll(this.getContents(name, selection));
		return set;
	}

	@Override
	public void createInDynamoSimulationObject() {
		// Will not be used		
	}

	@Override
	public void removeFromDynamoSimulationObject(String selectedValue) {
		// Will not be used		
	}

	@Override
	public void updateObjectState(String name, String selectedValue) {
		if (DynamoHeaderDataPanel.POP_FILE_NAME.equals(name)) {			
			configuration.setPopulationFileName(selectedValue);
		}
	}

	@Override
	public DropDownPropertiesSet getRefreshedDropDownSet(String label)
			throws ConfigurationException {
		// Will not be used
		return null;
	}

}
