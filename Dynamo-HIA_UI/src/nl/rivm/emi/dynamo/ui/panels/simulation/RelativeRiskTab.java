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

public class RelativeRiskTab extends NestedTab {
	
	private Log log = LogFactory.getLog("RelativeRiskTab");
	private RelativeRiskSelectionGroup relativeRiskSelectionGroup;
	private DynamoTabDataManager dynamoTabDataManager;
	
	/**
	 * @param selectedRelativeRisk 
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException 
	 */
	public RelativeRiskTab(Set<String> selectedRelativeRisk, TabFolder tabfolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup
			) throws ConfigurationException {
		super(selectedRelativeRisk, tabfolder, tabName,
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
			new RelativeRiskTabDataManager(selectedNode, 
					dynamoSimulationObject,
					this.selections);
		
		this.relativeRiskSelectionGroup =
			new RelativeRiskSelectionGroup(tabName,
					this.selections, 
					this.plotComposite,
					selectedNode, helpGroup,
					dynamoTabDataManager);
		
		RelativeRiskResultGroup RelativeRiskResultGroup =
			new RelativeRiskResultGroup(this.selections, 
					this.plotComposite,
					selectedNode, helpGroup,
					relativeRiskSelectionGroup,
					dynamoTabDataManager);
	}
	
	
	public void refreshSelectionGroup() throws ConfigurationException {
		this.relativeRiskSelectionGroup.refreshSelectionDropDown();
	}

	public void removeTabDataObject() throws ConfigurationException {
		this.dynamoTabDataManager.removeFromDynamoSimulationObject();
	}
}