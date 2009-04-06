package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class TabManager {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private Map<Integer, NestedTab> nestedTabs = 
		new LinkedHashMap<Integer, NestedTab>();
	
	private TabFolder tabFolder = null;

	private SelectionListener selectionListener;

	private TabPlatform platform;
	
	public TabManager(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,			
			HelpGroup helpGroup, TabPlatform platform) {

		this.platform = platform;
		
		// Create the tabFolder
		this.tabFolder = new TabFolder(parent, SWT.FILL);
		this.tabFolder.setLayout(new FormLayout());
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(90, -5);
		this.tabFolder.setLayoutData(formData);
		//this.tabFolder.setBackground(new Color(null, 0xff, 0xff,0xff)); // white		
		
		
		//new DiseaseTab(this.getTabFolder(), "", null, dataBindingContext, selectedNode, helpGroup);
		
		// Initially, there are no nested tabs
	//	this.selectionListener = new SelectionListener(nestedTabs);		
	}

	// tabName is identifier for the nestedTab to be created
	public void createNestedTab() throws DynamoConfigurationException {
		NestedTab nestedTab = this.platform.getNestedTab();
		///this.platform.getNestedTab();
		log.debug("nestedTab.getName()" + nestedTab.getName());
		// Create an unique number
		//int tabNumber = this.nestedTabs.size();
		// Create a new NestedTab on the TabPlatform		
		//this.nestedTabs.put(nestedTab.getName() + tabNumber, nestedTab);
		//this.nestedTabs.put(tabNumber, nestedTab);
		//this.updateListener();
		// Redraw the tabPlatform
		this.redraw();
	}
	
	public void deleteNestedTab() {
		// TODO REMOVE DEBUGGING:
		TabItem[] tabItems = tabFolder.getItems();
		log.debug("tabItems.length" + tabItems.length);		
		// TODO REMOVE DEBUGGING
		
		// Remove the tab that is selected now		
		int index = tabFolder.getSelectionIndex();
		log.debug("index" + index);
		// Tabs with index -1 or lower do not exist
		if (index > -1) {
			TabItem tabItem = tabFolder.getItem(index);
			tabItem.dispose();
		}		

		// Remove the nestedTab from the TabPlatform		
		//this.nestedTabs.remove(index);
		//this.updateListener();
		
		// Renumber the items
		this.renumberAndRenameItems();
		
		// Redraw the tabPlatform
		this.redraw();
	}
	
	
	private void renumberAndRenameItems() {
		// Alternative: can we just do a renaming?
		for (int index = 0; index < this.getTabFolder().getItemCount(); index++) {
			TabItem item = this.getTabFolder().getItem(index);
			int newIndexName = index + 1;
			String tabName = this.platform.getNestedTabPrefix() + newIndexName;
			item.setText(tabName);
		}	
		
		/*
		TabItem[] tabItems = this.tabFolder.getItems();
		
        Set<Integer> keys = nestedTabs.keySet();
        log.debug("nestedTabs.size()" + nestedTabs.size());
        Map<Integer, NestedTab> tempNestedTabs = new LinkedHashMap<Integer, NestedTab>();
        int newIndex = 0;
        for (Integer key :  keys) {
        	log.debug("key" + key);
        	tempNestedTabs.put(newIndex, nestedTabs.get(key));
        	
        	tempTabItems[newIndex] = oldTabItems[key];
        	this.tabFolder.
        	newIndex++;
        } 
        this.nestedTabs = tempNestedTabs;
        */        
	}

	public void redraw() {
		this.tabFolder.redraw();
	}	
	
	public void updateListener() {
		log.debug("HALLO IK BEN HIER");
		//log.debug("Tab Size: " + this.nestedTabs.size());
		//tabFolder.removeListener(SWT.Selection, this.selectionListener);		
		//tabFolder.addListener(SWT.Selection, new SelectionListener(nestedTabs));
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public int getNumberOfTabs() {
		return tabFolder.getItemCount();
		//return this.nestedTabs.size();
	}
	
}
