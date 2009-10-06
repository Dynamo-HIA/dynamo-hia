package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.NestedTabSelectionListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 * Tab that shows the NestedTab children NestedTab children are handled by the
 * TabManager Shows New buttons and Delete buttons, of which the actions, are
 * also handled by the TabManager
 * 
 * @author schutb
 * 
 */
public abstract class TabPlatform extends Tab {

	// private Log log = LogFactory.getLog(this.getClass().getName());

	protected TabFolder tabFolder = null;

	protected Map<String, NestedTab> nestedTabs;

	protected SelectionListener listener;

	// protected TabPlatformManager tabPlatformManager;

	/* tabFolder is the parent folder of all high level tabs */
	// private TabFolder tabFolder;
	public TabPlatform(TabFolder upperTabFolder, String tabName,
			BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject, HelpGroup helpGroup,
			DataBindingContext dataBindingContext)
			throws ConfigurationException {
		super(upperTabFolder, tabName, dynamoSimulationObject, selectedNode,
				helpGroup, dataBindingContext);
		makeIt();

		// The TabItem can only be created AFTER makeIt()
		TabItem item = new TabItem(upperTabFolder, SWT.NONE);
		item.setText(tabName);
		item.setControl(plotComposite);
		item.addListener(SWT.SELECTED, new Listener() {
			public void handleEvent(Event event) {
				TabItem item = (TabItem) event.item;
				String tabId = item.getText();
				log.debug("THIS TAB IS SELECTED" + tabId);
			}
		});
	}

	/**
	 * makes the tabfolder
	 * 
	 * @throws ConfigurationException
	 */
	public void makeIt() throws ConfigurationException {
		// Moved here from the constructor, because this method is called from a
		// superclass and this line would always be called too late.
		this.listener = (SelectionListener) new NestedTabSelectionListener(this);
	}

	public Map<String, NestedTab> getNestedTabs() {
		return nestedTabs;
	}

	public void createContent() throws ConfigurationException {
		createLowerTabFolder();
		createDefaultTabs_FromManager();
		// Create the create and delete buttons and their listeners:
		// add the tabManager methods
		TabPlatformButtonPanel buttonPanel = new TabPlatformButtonPanel(
		// plotComposite, this.tabPlatformManager.getTabFolder(),
				plotComposite, getTabFolder(), helpGroup);
		((TabPlatformButtonPanel) buttonPanel)
				.setSelectionListeners((TabPlatform) this);
	}

	synchronized private void createLowerTabFolder() {
		if (tabFolder == null) {
			// In some cases there is appearently a race condition, don't do it
			// twice.
			tabFolder = new TabFolder(plotComposite, SWT.FILL);
			tabFolder.setLayout(new FormLayout());

			nestedTabs = new LinkedHashMap<String, NestedTab>();

			FormData formData = new FormData();
			formData.top = new FormAttachment(0, 6);
			formData.left = new FormAttachment(0, 5);
			formData.right = new FormAttachment(100, -5);
			formData.bottom = new FormAttachment(90, -5);
			tabFolder.setLayoutData(formData);
			// this.tabFolder.setBackground(new Color(null, 0xff, 0xff,0xff));
			// //
			// white
		}
	}

	// public TabPlatformManager getTabManager() {
	// return this.tabPlatformManager;
	// }

	public void createNestedTab_FromManager() throws ConfigurationException {
		log.debug("this.listener" + this.listener);
		this.tabFolder.removeSelectionListener(this.listener);

		NestedTab nestedTab = null;
		try {
			nestedTab = createNestedTab();
			// tabName is identifier for the nestedTab to be created
			log.debug("Adding new nestedTab: " + nestedTab.getName());
			nestedTabs.put(nestedTab.getName(), nestedTab);
			// select the tab that is created
			int index = getNumberOfTabs() - 1;
			log.debug("index of selectedtabItem: " + index);
			this.tabFolder.setSelection(index);
			log.debug("selectedtabItem.getText(): "
					+ tabFolder.getItem(index).getText());

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			throw new ConfigurationException(e);
		} finally {
			try {
				this.redraw_FromManager();
			} catch (NoMoreDataException e) {
				// this should not occur
				e.printStackTrace();
			}
		}
	}

	// Create a new abstract tab
	public abstract NestedTab createNestedTab()
			throws DynamoConfigurationException, ConfigurationException;

	public abstract String getNestedTabPrefix();

	public void createDefaultTabs_FromManager()
			throws DynamoConfigurationException, ConfigurationException {
		Set<String> defaultTabKeyValues = getConfigurations();
		// Some debugging stuff.
		StringBuffer concatDefTabKeyVals = new StringBuffer();
		for (String keyValue : defaultTabKeyValues) {
			concatDefTabKeyVals.append(keyValue + ", ");
		}
		log.debug("concatDefTabKeyVals: " + concatDefTabKeyVals);
		// Debugging stuff ends.
		// the tab created is given the values of the configuration Map for
		// which to create the tab
		for (String defaultTabKeyValue : defaultTabKeyValues) {
			Set<String> keyValues = new LinkedHashSet<String>();
			keyValues.add(defaultTabKeyValue);
			log.debug("defaultTabKeyValue: " + defaultTabKeyValue);
			if (nestedTabs == null) {
				createLowerTabFolder();
			}
			NestedTab nestedTab = createNestedDefaultTab(keyValues);
			if (nestedTab != null) {
				log.debug("Created nestedTab: " + nestedTab.getName());
				// In the relativeRisksPlatform the nestedtab is directly placed
				// in
				// the collection.
				if (!nestedTabs.containsKey(nestedTab.getName())) {
					this.nestedTabs.put(nestedTab.getName(), nestedTab);
				}
			}
		} // for ends.
		try {
			this.redraw_FromManager();
		} catch (NoMoreDataException e) {
			// should not occur as this should have been spotted earlier
			e.printStackTrace();
		}
	}

	public abstract NestedTab createNestedDefaultTab(
			Set<String> defaultSelections) throws ConfigurationException;

	/**
	 * @return Set<Set<String>> Set of tabs that each contain the (Set of
	 *         composed) primary key values
	 */
	public abstract Set<String> getConfigurations();

	public void deleteNestedTab_FromManager() throws ConfigurationException {
		tabFolder.removeSelectionListener(listener);
		// TODO REMOVE DEBUGGING:
		TabItem[] tabItems = tabFolder.getItems();
		log.debug("tabItems.length" + tabItems.length);
		// TODO REMOVE DEBUGGING

		// Remove the tab that is selected now
		int index = tabFolder.getSelectionIndex();
		log.debug("Going to remove tab at tabItem index" + index);
		NestedTab removedNestedTab;
		// Tabs with index -1 or lower do not exist
		if (index > -1) {
			log.debug("EXISTING NestedTabs: " + this.nestedTabs);
			TabItem tabItem = tabFolder.getItem(index);
			removedNestedTab = nestedTabs.get(tabItem.getText());
			log.debug("TabItem text: " + tabItem.getText() + " tab: "
					+ removedNestedTab);
			// Remove the data from the data object model
			deleteNestedTabPlusData(removedNestedTab);
			tabItem.dispose();
		}
		log.debug("After removal: " + tabFolder.getItems().length + " Items, "
				+ nestedTabs.size() + " tabs");
		// Renumber the items
		this.renumberAndRenameItems();
		log.debug("After renumber: " + tabFolder.getItems().length + " Items, "
				+ nestedTabs.size() + " tabs");
		// Redraw the tabPlatform
		try {
			this.redraw_FromManager();
		} catch (NoMoreDataException e) {
			// this should not occur

			e.printStackTrace();
		}
	}

	// Delete an abstract tab
	public abstract void deleteNestedTabPlusData(NestedTab removedNestedTab)
			throws ConfigurationException;

	private void renumberAndRenameItems() {
		Map<String, NestedTab> tempNestedTabs = new LinkedHashMap<String, NestedTab>();
		tempNestedTabs.putAll(this.nestedTabs);
		log.debug("OLDPRENUMB NestedTabs: " + tempNestedTabs);
		this.nestedTabs.clear();
		// Alternative: can we just do a renaming?
		for (int index = 0; index < tabFolder.getItemCount(); index++) {
			TabItem item = tabFolder.getItem(index);
			String oldTabName = item.getText();
			int newIndexName = index + 1;
			String tabName = getNestedTabPrefix() + newIndexName;
			item.setText(tabName);
			log.fatal("OLDTABNAME: " + oldTabName + "  index  " + index);
			log.fatal("TEMPNESTEDTABS: " + tempNestedTabs);
			log.fatal("ADDING NESTEDTAB: " + tempNestedTabs.get(oldTabName));
			// next 3 lines (plus new method) added by Hendriek because did not
			// work properly when deleting tabs. Refreshes the configuration
			// from the model object
			NestedTab tabToRefresh = tempNestedTabs.get(oldTabName);
			if (tabToRefresh instanceof RelativeRiskTab) {
				((RelativeRiskTabDataManager) ((RelativeRiskTab) tabToRefresh)
						.getDynamoTabDataManager())
						.reloadConfigurationsFromModelObject();
			}
			this.nestedTabs.put(tabName, tabToRefresh);
		}
		log.debug("RENUMBERED: " + this.nestedTabs);
	}

	public abstract void refreshNestedTab(NestedTab nestedTab)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException;

	// public void setTabFolder(TabFolder tabFolder) {
	// this.tabFolder = tabFolder;
	// }

	public TabFolder getUpperTabFolder() {
		return (TabFolder) plotComposite.getParent();
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public int getNumberOfTabs() {
		return tabFolder.getItemCount();
		// return this.nestedTabs.size();
	}

	public void redraw_FromManager() throws ConfigurationException,
			NoMoreDataException {
		try {
			refreshSelectionDropDowns();
		} catch (DynamoNoValidDataException e) {
			// should not occur
			e.printStackTrace();
		}
		tabFolder.redraw();
		tabFolder.addSelectionListener(this.listener);
	}

	private void refreshSelectionDropDowns() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		for (int index = 0; index < tabFolder.getItemCount(); index++) {
			TabItem item = tabFolder.getItem(index);
			refreshNestedTab(this.nestedTabs.get(item.getText()));
		}
	}

	private void handleErrorMessage(Exception e) {
		e.printStackTrace();
		MessageBox box = new MessageBox(tabFolder.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during creation of a new tab "
				+ e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}

}
