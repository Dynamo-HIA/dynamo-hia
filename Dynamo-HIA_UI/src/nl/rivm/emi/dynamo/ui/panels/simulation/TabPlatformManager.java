// OBSOLETE Refactored out.
package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public class TabPlatformManager {

	private Log log = LogFactory.getLog(this.getClass().getName());

	// public Map<String, NestedTab> nestedTabs;

	// private TabFolder tabFolder = null;

	private TabPlatform platform;

	// private SelectionListener listener;

	public TabPlatformManager(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject, HelpGroup helpGroup,
			TabPlatform platform) {

		this.platform = platform;
		//
		// // Create the tabFolder
		// tabFolder = new TabFolder(parent, SWT.FILL);
		// tabFolder.setLayout(new FormLayout());
		//
		// nestedTabs = new LinkedHashMap<String, NestedTab>();
		//
		// FormData formData = new FormData();
		// formData.top = new FormAttachment(0, 6);
		// formData.left = new FormAttachment(0, 5);
		// formData.right = new FormAttachment(100, -5);
		// formData.bottom = new FormAttachment(90, -5);
		// tabFolder.setLayoutData(formData);
		// // this.tabFolder.setBackground(new Color(null, 0xff, 0xff,0xff)); //
		// // white
		//
		// Create a listener that refreshes the nested tabs
		// this.listener = new TabPlatformManagerSelectionListener();
	}

//	private void handleErrorMessage(Exception e) {
//		e.printStackTrace();
//		MessageBox box = new MessageBox(TabPlatformManager.this.getTabFolder()
//				.getShell(), SWT.ERROR_UNSPECIFIED);
//		box.setText("Error occured during creation of a new tab "
//				+ e.getMessage());
//		box.setMessage(e.getMessage());
//		box.open();
//	}

	/**
	 * 
	 * Creates the stored default NestedTabs on this TabFolder
	 * 
	 * @throws ConfigurationException
	 * @throws DynamoNoValidDataException
	 */
	public void createDefaultTabs() throws DynamoConfigurationException,
			ConfigurationException {
//		Set<String> defaultTabKeyValues = platform.getConfigurations();
//		log.debug("defaultTabKeyValues111" + defaultTabKeyValues);
//		// the tab created is given the values of the configuration Map for
//		// which to create the tab
//		for (String defaultTabKeyValue : defaultTabKeyValues) {
//			Set<String> keyValues = new LinkedHashSet<String>();
//			keyValues.add(defaultTabKeyValue);
//			log.debug("defaultTabKeyValue222" + defaultTabKeyValue);
//			NestedTab nestedTab = platform.createNestedDefaultTab(keyValues);
//			log.debug("CREATING DEFAULT NESTEDTABS " + nestedTab);
//			if (nestedTab != null)
//				this.nestedTabs.put(nestedTab.getName(), nestedTab);
//		}
//		try {
//			this.redraw();
//		} catch (NoMoreDataException e) {
//			// should not occur as this should have been spotted earlier
//			e.printStackTrace();
//		}
		platform.createDefaultTabs_FromManager();
	}

	/**
	 * 
	 * Creates a new NestedTab on this TabFolder
	 * 
	 * @throws ConfigurationException
	 * 
	 * @throws ConfigurationException
	 * @throws DynamoNoValidDataException
	 */
	public void createNestedTab() throws ConfigurationException {
//		log.debug("this.listener" + this.listener);
//		this.tabFolder.removeSelectionListener(this.listener);
//
//		NestedTab nestedTab = null;
//		try {
//			nestedTab = this.platform.createNestedTab();
//			// tabName is identifier for the nestedTab to be created
//			log.debug("CREATING NEW NESTEDTAB " + nestedTab);
//			nestedTabs.put(nestedTab.getName(), nestedTab);
//			// select the tab that is created
//			int index = getNumberOfTabs() - 1;
//			log.fatal("index of selectedtabItem: " + index);
//			this.tabFolder.setSelection(index);
//		} catch (ConfigurationException e) {
//			// TODO Auto-generated catch block
//			throw new ConfigurationException(e);
//		} finally {
//			try {
//				this.redraw();
//			} catch (NoMoreDataException e) {
//				// this should not occur
//				e.printStackTrace();
//			}
//		}
		platform.createNestedTab_FromManager();
	}

	public void deleteNestedTab() throws ConfigurationException {
		// this.tabFolder.removeSelectionListener(this.listener);
		// // TODO REMOVE DEBUGGING:
		// TabItem[] tabItems = tabFolder.getItems();
		// log.debug("tabItems.length" + tabItems.length);
		// // TODO REMOVE DEBUGGING
		//
		// // Remove the tab that is selected now
		// int index = tabFolder.getSelectionIndex();
		// log.debug("index" + index);
		// NestedTab removedNestedTab;
		// // Tabs with index -1 or lower do not exist
		// if (index > -1) {
		// log.debug("EXISTING NestedTabs: " + this.nestedTabs);
		// TabItem tabItem = tabFolder.getItem(index);
		// removedNestedTab = nestedTabs.get(tabItem.getText());
		// // Remove the data from the data object model
		// this.platform.deleteNestedTab(removedNestedTab);
		// tabItem.dispose();
		// }
		//
		// // Renumber the items
		// this.renumberAndRenameItems();
		//
		// // Redraw the tabPlatform
		// try {
		// this.redraw();
		// } catch (NoMoreDataException e) {
		// // this should not occur
		//
		// e.printStackTrace();
		// }
		platform.deleteNestedTab_FromManager();
	}

//	private void renumberAndRenameItems() {
//		Map<String, NestedTab> tempNestedTabs = new LinkedHashMap<String, NestedTab>();
//		tempNestedTabs.putAll(this.nestedTabs);
//		log.debug("OLDPRENUMB NestedTabs: " + tempNestedTabs);
//		this.nestedTabs.clear();
//		// Alternative: can we just do a renaming?
//		for (int index = 0; index < this.getTabFolder().getItemCount(); index++) {
//			TabItem item = this.getTabFolder().getItem(index);
//			String oldTabName = item.getText();
//			int newIndexName = index + 1;
//			String tabName = this.platform.getNestedTabPrefix() + newIndexName;
//			item.setText(tabName);
//			log.fatal("OLDTABNAME: " + oldTabName + "  index  " + index);
//			log.fatal("TEMPNESTEDTABS: " + tempNestedTabs);
//			log.fatal("ADDING NESTEDTAB: " + tempNestedTabs.get(oldTabName));
//			// next 3 lines (plus new method) added by Hendriek because did not
//			// work properly when deleting tabs. Refreshes the configuration
//			// from the model object
//			NestedTab tabToRefresh = tempNestedTabs.get(oldTabName);
//			if (tabToRefresh instanceof RelativeRiskTab) {
//				((RelativeRiskTabDataManager) ((RelativeRiskTab) tabToRefresh)
//						.getDynamoTabDataManager())
//						.refreshConfigurations(index);
//			}
//			this.nestedTabs.put(tabName, tabToRefresh);
//		}
//		log.debug("RENUMBERED: " + this.nestedTabs);
//	}

	public void redraw() throws ConfigurationException, NoMoreDataException {
//		try {
//			this.refreshSelectionDropDowns();
//		} catch (DynamoNoValidDataException e) {
//			// should not occur
//			e.printStackTrace();
//		}
//		this.tabFolder.redraw();
//		this.tabFolder.addSelectionListener(this.listener);
platform.redraw_FromManager();
	}

//	private void refreshSelectionDropDowns() throws ConfigurationException,
//			NoMoreDataException, DynamoNoValidDataException {
//		for (int index = 0; index < this.getTabFolder().getItemCount(); index++) {
//			TabItem item = this.getTabFolder().getItem(index);
//			this.platform.refreshNestedTab(this.nestedTabs.get(item.getText()));
//		}
//	}

	public void updateListener() {
		log.debug("HALLO IK BEN HIER");
		// log.debug("Tab Size: " + this.nestedTabs.size());
		// tabFolder.removeListener(SWT.Selection, this.selectionListener);
		// tabFolder.addListener(SWT.Selection, new
		// SelectionListener(nestedTabs));
	}

	public TabFolder getTabFolder() {
		return platform.getTabFolder();
	}

	public int getNumberOfTabs() {
		return platform.getNumberOfTabs();
		// return this.nestedTabs.size();
	}
}
