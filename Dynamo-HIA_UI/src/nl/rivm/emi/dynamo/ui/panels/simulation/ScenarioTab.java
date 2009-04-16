/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

public class ScenarioTab extends NestedTab {
	
	private Log log = LogFactory.getLog("ScenarioTab");
	
	private ScenarioSelectionGroup scenarioSelectionGroup;
	private DynamoTabDataManager dynamoTabDataManager;

	/**
	 * @param defaultSelections 
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException 
	 */
	public ScenarioTab(Set<String> selectedScenario, TabFolder tabfolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		super(selectedScenario, tabfolder, tabName,
				dynamoSimulationObject,
				dataBindingContext, 
				selectedNode,
				helpGroup);
	}
	
	/**
	 * Create the active contents of this tab
	 * @throws ConfigurationException 
	 */	
	@Override
	public void makeIt() throws ConfigurationException{
		
		this.dynamoTabDataManager =
			new ScenarioTabDataManager(selectedNode, 
					dynamoSimulationObject,
					this.selections);
		
		this.scenarioSelectionGroup =
			new ScenarioSelectionGroup(tabName, this.selections, 
					this.plotComposite,
					selectedNode, helpGroup,
					dynamoTabDataManager,
					dataBindingContext,
					this.dynamoSimulationObject
					);
		
		ScenarioResultGroup ScenarioResultGroup =
			new ScenarioResultGroup(selections, this.plotComposite,
					selectedNode, helpGroup,
					scenarioSelectionGroup.scenarioDefGroup,
					scenarioSelectionGroup.getDropDownModifyListener(),
					dynamoTabDataManager
					);
	}	
	
	
	public void refreshSelectionGroup() throws ConfigurationException {
		this.scenarioSelectionGroup.refreshSelectionDropDown();
	}

	public void removeTabDataObject() throws ConfigurationException {
		this.dynamoTabDataManager.removeFromDynamoSimulationObject();
	}	
}