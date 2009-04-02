/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public class DiseaseTab extends NestedTab {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private DynamoSimulationObject modelObject;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup helpGroup;
	private BaseNode selectedNode;
	
	private TabFolder tabFolder;
	private Composite plotComposite;

	/**
	 * @param tabfolder
	 * @param output
	 */
	public DiseaseTab(TabFolder tabfolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) {
		super(tabfolder, tabName,
				dynamoSimulationObject,
				dataBindingContext, 
				selectedNode,
				helpGroup);
	}
	
	/**
	 * Create the active contents of this tab
	 */
	public void makeIt(){	
		/* TODO Add the active contents of the tab
		DiseaseSelectionGroup diseaseSelectionGroup =
			new DiseaseSelectionGroup(this.plotComposite,
					selectedNode, helpGroup);*/
										
	}	
	
}