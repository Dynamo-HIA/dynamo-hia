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

public class DiseaseTab extends NestedTab {
	
	private Log log = LogFactory.getLog("DiseaseTab");
	
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
	public DiseaseTab(TabFolder tabfolder, String tabName,
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
	 */	
	@Override
	public void makeIt(){		
		DiseaseSelectionGroup diseaseSelectionGroup =
			new DiseaseSelectionGroup(this.plotComposite,
					selectedNode, helpGroup);
		
		DiseaseResultGroup DiseaseResultGroup =
			new DiseaseResultGroup(this.plotComposite,
					selectedNode, helpGroup,
					diseaseSelectionGroup.group,
					diseaseSelectionGroup.getDropDownModifyListener());
	}	
}