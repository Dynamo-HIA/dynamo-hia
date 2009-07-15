package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
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
 * Tab that shows the NestedTab children
 * NestedTab children are handled by the TabManager
 * Shows New buttons and Delete buttons,
 * of which the actions, are also handled by the TabManager
 * 
 * @author schutb
 *
 */
public abstract class TabPlatform extends Tab {

	private Log log = LogFactory.getLog(this.getClass().getName()); 
	
	protected TabManager tabManager;
	/* tabFolder is the parent folder of all high level tabs */
	private TabFolder tabFolder;
	
	public TabPlatform(TabFolder tabFolder, String tabName, 
			BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,			
			HelpGroup helpGroup) throws ConfigurationException {
		super(null, tabFolder, tabName, 
				dynamoSimulationObject, 
			    dataBindingContext, 
				selectedNode, helpGroup);
	}

	/**
	 * makes the tabfolder
	 * @throws ConfigurationException 
	 */
	public void makeIt() throws ConfigurationException{			

		// Create the tabManager, it handles the subtabs
		this.tabManager = 
			new TabManager(this.plotComposite, this.selectedNode, 
					this.getDynamoSimulationObject(), this.dataBindingContext, 
					this.helpGroup, this);	
		this.tabManager.createDefaultTabs();
		
		// Create the create and delete buttons and their listeners:
		// add the tabManager methods		
		TabPlatformButtonPanel buttonPanel 
			= new TabPlatformButtonPanel(plotComposite, this.tabManager.getTabFolder(), helpGroup);
		((TabPlatformButtonPanel) buttonPanel)
			.setSelectionListeners((TabPlatform) this);
	}	

	public TabManager getTabManager() {
		return this.tabManager;
	}

	// Create a new abstract tab
	public abstract NestedTab createNestedTab() throws DynamoConfigurationException, ConfigurationException;

	public abstract String getNestedTabPrefix();

	public abstract NestedTab createNestedDefaultTab(Set<String> defaultSelections) throws ConfigurationException;

	/**
	 * @return Set<Set<String>> Set of tabs that each contain the (Set of composed) primary key values
	 */
	public abstract Set<String> getConfigurations();

	// Delete an abstract tab
	public abstract void deleteNestedTab(NestedTab removedNestedTab) throws ConfigurationException;

	public abstract void refreshNestedTab(NestedTab nestedTab) throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException;

	public void setTabFolder(TabFolder tabFolder) {
		this.tabFolder = tabFolder;
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}
			

	
}
