package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.support.ChoosableDiseases;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class TabManager {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public Map<String, NestedTab> nestedTabs;
	
	private TabFolder tabFolder = null;

	private SelectionListener selectionListener;

	private TabPlatform platform;

	private SelectionListener listener;
	
	public TabManager(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,			
			HelpGroup helpGroup, TabPlatform platform) {

		this.platform = platform;
		
		// Create the tabFolder
		this.tabFolder = new TabFolder(parent, SWT.FILL);
		this.tabFolder.setLayout(new FormLayout());
		
		nestedTabs = new LinkedHashMap<String, NestedTab>();
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(90, -5);
		this.tabFolder.setLayoutData(formData);
		//this.tabFolder.setBackground(new Color(null, 0xff, 0xff,0xff)); // white

		this.listener = 
			new SelectionListener() {
		      public void widgetSelected(SelectionEvent e) {
		        log.debug("Selected item index = " + tabFolder.getSelectionIndex());
		        log.debug("Selected item = " + (tabFolder.getSelection() == null ? "null" : tabFolder.getSelection()[0].toString()));
		        
	            try {
	            	int selected = tabFolder.getSelectionIndex();
	            	if (selected > -1) {
	            		
	            		//TabManager.this.reCreateNestedTab(TabManager.this.deleteNestedTab());
	            		TabItem item = TabManager.this.getTabFolder().getItem(selected);
	            		log.debug("item.getText()" + item.getText());
	            		log.debug("TabManager.this.nestedTabs.size()" + TabManager.this.nestedTabs.size());
	            		log.debug("TabManager.this.nestedTabs" + TabManager.this.nestedTabs);
	            		TabManager.this.platform.
	            			refreshNestedTab(TabManager.this.nestedTabs.
	            					get(item.getText()));
						//tabFolder.setSelection(selected);
						
	            	}
				} catch (ConfigurationException ce) {
					// TODO Auto-generated catch block
					ce.printStackTrace();
				}		        
		      }

		      public void widgetDefaultSelected(SelectionEvent e) {
		        //widgetSelected(e);
		      }
		    };
		    
		//new DiseaseTab(this.getTabFolder(), "", null, dataBindingContext, selectedNode, helpGroup);
		
		// Initially, there are no nested tabs
	//	this.selectionListener = new SelectionListener(nestedTabs);		
	}
/*
	protected void reCreateNestedTab(Map<String, String> removedDisease) throws ConfigurationException {
		this.platform.createtNestedDefaultTab(null, removedDisease);		
	}*/

	/**
	 * 
	 * Creates the stored default NestedTabs on this TabFolder
	 * 
	 * @throws ConfigurationException
	 */
	public void createDefaultTabs() throws DynamoConfigurationException, ConfigurationException {
		Set<String> defaultTabKeyValues = this.platform.getConfigurations();
		log.debug("defaultTabKeyValues111" + defaultTabKeyValues);
		for (String defaultTabKeyValue : defaultTabKeyValues) {
			Set<String> keyValues = new LinkedHashSet<String>();
			keyValues.add(defaultTabKeyValue);
			log.debug("defaultTabKeyValue222" + defaultTabKeyValue);
			NestedTab nestedTab = this.platform.createNestedDefaultTab(keyValues);
			log.debug("CREATING DEFAULT NESTEDTABS " + nestedTab);
			this.nestedTabs.put(nestedTab.getName(), nestedTab);
		}		
		this.redraw();
	}		
	
	/**
	 * 
	 * Creates a new NestedTab on this TabFolder
	 * @throws ConfigurationException 
	 * 
	 * @throws ConfigurationException
	 */
	public void createNestedTab() throws ConfigurationException {
		this.tabFolder.removeSelectionListener(this.listener);
		
		NestedTab nestedTab = null;
		try {
			nestedTab = this.platform.createNestedTab();
			// tabName is identifier for the nestedTab to be created
			log.debug("CREATING NEW NESTEDTAB " + nestedTab);
			nestedTabs.put(nestedTab.getName(), nestedTab);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			throw new ConfigurationException(e);
		} finally {
			this.redraw();	
		}		
		// Create an unique number
		//int tabNumber = this.nestedTabs.size();
		// Create a new NestedTab on the TabPlatform		
		//this.nestedTabs.put(nestedTab.getName() + tabNumber, nestedTab);
		//this.nestedTabs.put(tabNumber, nestedTab);
		//this.updateListener();
		// Redraw the tabPlatform
		
	}
	
	public void deleteNestedTab() throws ConfigurationException {
		this.tabFolder.removeSelectionListener(this.listener);
		// TODO REMOVE DEBUGGING:
		TabItem[] tabItems = tabFolder.getItems();
		log.debug("tabItems.length" + tabItems.length);		
		// TODO REMOVE DEBUGGING

		// Remove the tab that is selected now		
		int index = tabFolder.getSelectionIndex();
		log.debug("index" + index);
		NestedTab removedNestedTab;
		// Tabs with index -1 or lower do not exist
		if (index > -1) {
			log.debug("EXISTING NestedTabs: " + this.nestedTabs);
			TabItem tabItem = tabFolder.getItem(index);
			removedNestedTab = nestedTabs.get(tabItem.getText());
			// Remove the data from the data object model
			this.platform.deleteNestedTab(removedNestedTab);		
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
		Map<String, NestedTab> tempNestedTabs = new LinkedHashMap<String, NestedTab>();
		tempNestedTabs.putAll(this.nestedTabs);
		log.debug("OLDPRENUMB NestedTabs: " + tempNestedTabs);
		this.nestedTabs.clear();
		// Alternative: can we just do a renaming?
		for (int index = 0; index < this.getTabFolder().getItemCount(); index++) {			
			TabItem item = this.getTabFolder().getItem(index);
			String oldTabName = item.getText(); 
			int newIndexName = index + 1;
			String tabName = this.platform.getNestedTabPrefix() + newIndexName;
			item.setText(tabName);
			log.debug("OLDTABNAME: " + oldTabName);
			log.debug("TEMPNESTEDTABS: " + tempNestedTabs);
			log.debug("ADDING NESTEDTAB: " + tempNestedTabs.get(oldTabName));
			this.nestedTabs.put(tabName, tempNestedTabs.get(oldTabName));
		}
		log.debug("RENUMBERED: " + this.nestedTabs);
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

	public void redraw() throws ConfigurationException {
		this.refreshSelectionDropDowns();
		this.tabFolder.redraw();
		this.tabFolder.addSelectionListener(		
				this.listener);		
	}	
	
	private void refreshSelectionDropDowns() throws ConfigurationException {
		for (int index = 0; index < this.getTabFolder().getItemCount(); index++) {			
			TabItem item = this.getTabFolder().getItem(index);
			this.platform.
			refreshNestedTab(this.nestedTabs.
					get(item.getText()));			
		}
	}

	public void updateListener() {
		log.debug("HALLO IK BEN HIER");
		//log.debug("Tab Size: " + this.nestedTabs.size());
		//tabFolder.removeListener(SWT.Selection, this.selectionListener);		
		//tabFolder.addListener(SWT.Selection, new SelectionListener(nestedTabs));
	}

	public TabFolder getTabFolder() {
		return this.tabFolder;
	}

	public int getNumberOfTabs() {
		return this.tabFolder.getItemCount();
		//return this.nestedTabs.size();
	}
	
}
