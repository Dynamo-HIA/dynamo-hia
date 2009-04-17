/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

public class ScenariosTab extends TabPlatform {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String SCENARIOS = "Scenarios";
	private static final String SCENARIO = "Scenario";
		
	/**
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException 
	 */
	public ScenariosTab(TabFolder tabFolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		super(tabFolder, SCENARIOS, selectedNode, dynamoSimulationObject, dataBindingContext, helpGroup);
	}
	
	@Override
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections)
			throws ConfigurationException {
		int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		String tabName = SCENARIO + newTabNumber;		
		return new ScenarioTab(defaultSelections, this.getTabManager().getTabFolder(), 
				tabName, dynamoSimulationObject, 
				dataBindingContext, selectedNode, helpGroup);
	}
	
	@Override
	public String getNestedTabPrefix() {
		return SCENARIO;
	}

	@Override
	public NestedTab createNestedTab() throws DynamoConfigurationException,
			ConfigurationException {
		return createNestedDefaultTab(null);
	}

	@Override
	public void deleteNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		ScenarioTab scenarioTab = (ScenarioTab) nestedTab;
		scenarioTab.removeTabDataObject();
	}

	@Override
	public Set<String> getConfigurations() {
		Map<String, ITabScenarioConfiguration> configurations =
		this.dynamoSimulationObject.getScenarioConfigurations();
		return configurations.keySet();
	}

	@Override
	public void refreshNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		ScenarioTab scenarioTab = (ScenarioTab) nestedTab;
		//scenarioTab.refreshSelectionGroup();
		scenarioTab.refreshResultGroup();
	}
	
	public void refreshFirstTab() throws ConfigurationException {
		refreshNestedTab(this.getTabManager().nestedTabs.get(SCENARIO + "1"));
	}	
}