package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * Tab that shows the NestedTab children
 * NestedTab children are handled by the TabManager
 * Shows New buttons and Delete buttons,
 * of which the actions, are also handled by the TabManager
 * 
 * @author schutb
 *
 */
public abstract class TabPlatform extends Tab {

	protected DynamoSimulationObject dynamoSimulationObject;
	private Composite myParent = null;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup theHelpGroup;
	private BaseNode selectedNode;
	protected TabManager tabManager;
	
	public TabPlatform(TabFolder tabFolder, String tabName, 
			BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,			
			HelpGroup helpGroup) {
		super(tabFolder, tabName);
		this.myParent = tabFolder;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.selectedNode = selectedNode;
		
		// TODO: Create a Singleton here, there can be only one
		this.tabManager = new TabManager(this.plotComposite, selectedNode, this.dynamoSimulationObject, dataBindingContext, this.theHelpGroup);
	}

	/**
	 * makes the tabfolder
	 */
	public void makeIt(){			
		// Create the tabManager, it handles the subtabs
		// TODO: add this.tabFolder???
		
		// Create the create and delete buttons and their listeners:
		// add the tabManager methods		
		TabPlatformButtonPanel buttonPanel 
			= new TabPlatformButtonPanel(this.plotComposite);
		((TabPlatformButtonPanel) buttonPanel)
			.setSelectionListeners((TabPlatform) this);
	}	
	
	public TabManager getTabManager() {
		return this.tabManager;
	}

	public abstract NestedTab getNestedTab();

	
}
