/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

public class DiseaseTab extends NestedTab {
	
	private Log log = LogFactory.getLog("DiseaseTab");
	private DiseaseSelectionGroup diseaseSelectionGroup;
	private DynamoTabDataManager dynamoTabDataManager;

	/** 
	 * @param defaultDisease 
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException 
	 */
	public DiseaseTab(Set<String> selectedDisease,  
			TabFolder tabfolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		super(selectedDisease, tabfolder, tabName,
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
			new DiseaseTabDataManager(selectedNode, 
					dynamoSimulationObject,
					this.selections);
		
		this.diseaseSelectionGroup =
			new DiseaseSelectionGroup(tabName, this.selections, this.plotComposite,
					selectedNode, helpGroup,
					dynamoTabDataManager
					);
		
		DiseaseResultGroup diseaseResultGroup =
			new DiseaseResultGroup(this.selections, this.plotComposite,					
					selectedNode, helpGroup,
					diseaseSelectionGroup.group,
					diseaseSelectionGroup.getDropDownModifyListener(),
					dynamoTabDataManager
					);
	}
	
	public void refreshSelectionGroup() throws ConfigurationException {
		this.diseaseSelectionGroup.refreshSelectionDropDown();
	}

	public void removeTabDataObject() throws ConfigurationException {
		this.dynamoTabDataManager.removeFromDynamoSimulationObject();
	}
}