/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

public class ScenarioTab extends NestedTab {
	
	private Log log = LogFactory.getLog("ScenarioTab");
	
	private DynamoSimulationObject modelObject;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup helpGroup;
	private BaseNode selectedNode;
	
	private TabFolder tabFolder;

	/**
	 * @param tabfolder
	 * @param output
	 * @throws DynamoConfigurationException 
	 */
	public ScenarioTab(TabFolder tabfolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) throws DynamoConfigurationException {
		super(tabfolder, tabName,
				dynamoSimulationObject,
				dataBindingContext, 
				selectedNode,
				helpGroup);
	}
	
	/**
	 * Create the active contents of this tab
	 * @throws DynamoConfigurationException 
	 */	
	@Override
	public void makeIt() throws DynamoConfigurationException{		
		ScenarioSelectionGroup scenarioSelectionGroup =
			new ScenarioSelectionGroup(this.plotComposite,
					modelObject, selectedNode, helpGroup);
		
		ScenarioResultGroup ScenarioResultGroup =
			new ScenarioResultGroup(this.plotComposite,
					selectedNode, helpGroup,
					scenarioSelectionGroup.group,
					scenarioSelectionGroup.getDropDownModifyListener());
	}	
}