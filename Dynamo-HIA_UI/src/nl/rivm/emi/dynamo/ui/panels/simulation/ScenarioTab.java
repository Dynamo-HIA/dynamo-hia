/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * Defines the nested scenario tab 
 * 
 * @author schutb
 *
 */
public class ScenarioTab extends NestedTab {
	
	private Log log = LogFactory.getLog("ScenarioTab");
	
	private ScenarioSelectionGroup scenarioSelectionGroup;
	private ScenarioResultGroup scenarioResultGroup;
	private DynamoTabDataManager dynamoTabDataManager;

	

	public DynamoTabDataManager getDynamoTabDataManager() {
		return dynamoTabDataManager;
	}

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
				selectedNode, 
				helpGroup, dataBindingContext, null);
	}
	
	/**
	 * Create the active contents of this tab
	 * @throws ConfigurationException 
	 * @throws NoMoreDataException 
	 */	
	@Override
	public void makeIt() throws ConfigurationException, NoMoreDataException{
		
		this.dynamoTabDataManager =
			new ScenarioTabDataManager(selectedNode, 
					getDynamoSimulationObject(),
					this.selections);
		
		try {
			this.scenarioSelectionGroup =
				new ScenarioSelectionGroup(tabName, this.selections, 
						this.plotComposite,
						selectedNode, helpGroup,
						dynamoTabDataManager,
						dataBindingContext,
						this.getDynamoSimulationObject()
						);
		
		this.scenarioResultGroup =
			new ScenarioResultGroup(selections, this.plotComposite,
					selectedNode, helpGroup,
					scenarioSelectionGroup.scenarioDefGroup,
					scenarioSelectionGroup.getDropDownModifyListener(),
					dynamoTabDataManager
					);
		} catch (DynamoNoValidDataException e) {
			this.dynamoTabDataManager.removeFromDynamoSimulationObject();
			throw new NoMoreDataException(e.getMessage());
			
		}
	}	
	
	public void refreshSelectionGroup() throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException {
		this.scenarioSelectionGroup.refreshSelectionDropDown();
	}

	public void removeTabDataObject() throws ConfigurationException {
		this.dynamoTabDataManager.removeFromDynamoSimulationObject();
	}

	public void refreshResultGroup() throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException {
		this.scenarioResultGroup.refreshGroupDropDown();
		
	}
	
	
}