/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.support.RelRisksCollectionForDropdown;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author schutb, adapted by boshuizh
 * 
 */
public class RelativeRisksTabPlatform extends TabPlatform {

	// private Log log = LogFactory.getLog(this.getClass().getName());

	private static final String RELATIVE_RISKS = "Relative Risks";
	// TODO RLM
	private static final String RELATIVE_RISK = "Relative Risk";
	private RelativeRiskTabPlatformDataManager dataManager;

	/**
	 * @param upperTabFolder
	 *            Tabfolder containing this tabPlatform.
	 * @param dynamoSimulationObject
	 *            Reference to the modelObject being edited.
	 * @param selectedNode
	 *            Used for dropdown-management.
	 * @param helpGroup
	 *            Used for the dynamic helptexts.
	 * @throws ConfigurationException
	 */
	public RelativeRisksTabPlatform(TabFolder upperTabFolder,
			DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode, HelpGroup helpGroup)
			throws ConfigurationException {
		super(upperTabFolder, RELATIVE_RISKS, selectedNode,
				dynamoSimulationObject, helpGroup, null);
		createContent();
	}

	public static String getRELATIVE_RISKS() {
		return RELATIVE_RISKS;
	}

	public RelativeRiskTabPlatformDataManager getDataManager()
			throws ConfigurationException {
		if (dataManager == null) {
			dataManager = new RelativeRiskTabPlatformDataManager(selectedNode,
					dynamoSimulationObject, this);
		}
		return dataManager;
	}

	@Override
	public String getNestedTabPrefix() {
		return RelativeRiskTab.RELATIVE_RISK;
	}

	public void removeAndRebuildAllTabs() throws DynamoConfigurationException,
			ConfigurationException {
		dumpTabFolderContent("Starting removeAndRebuildAllTabs: ");
		deleteNestedTabsButNotTheData();
		dumpTabFolderContent("After deleteNestedTabsButNotTheData: ");
		createDefaultTabs_FromManager();
		dumpTabFolderContent("After createDefaultTabs_FromManager: ");
	}

	private void dumpTabFolderContent(String debugInfo) {
		TabItem[] tabItems = tabFolder.getItems();
		StringBuffer tabItemNames = new StringBuffer();
		for(TabItem tabItem:tabItems){
			tabItemNames.append(tabItem.getText() + ", ");
		}
		if(tabItemNames.length() > 2){
		tabItemNames.setLength(tabItemNames.length()-2);
		}
		log
				.fatal(debugInfo + " - TabFolder-identityHash: " + System.identityHashCode(tabFolder) + ", tooltip: "
						+ tabFolder.getToolTipText() + ",\n tabItems: " + tabItemNames.toString()
						);
	}

	public void deleteNestedTabsButNotTheData() throws ConfigurationException {
		tabFolder.removeSelectionListener(listener);
		TabItem[] tabItems = tabFolder.getItems();
		Control[] controlList = tabFolder.getTabList();
		log.debug("Before for: tabItems.length = " + tabItems.length);
		for (int count = 0; count < tabItems.length; count++) {
			TabItem tabItem = tabItems[count];
			NestedTab nestedTab2Remove = nestedTabs.get(tabItem.getText());
			// Remove the data from the data object model
			removeUITabButNotTheData(nestedTab2Remove);
			// tabItem.getControl().dispose();
			tabItem.dispose();
			// 20091005 Zap tabItem.
		//	log.debug("Going to zap TabItem: " + System.identityHashCode(tabItem));
		//	tabItem = null;
		}
		log.debug("After for, tabItems.length = " + tabItems.length);
		// Redraw the tabPlatform
		try {
			this.redraw_FromManager();
		} catch (NoMoreDataException e) {
			// this should not occur
			e.printStackTrace();
		}
	}

	public void removeUITabButNotTheData(NestedTab nestedTab) {
		nestedTab.plotComposite.dispose();
		nestedTabs.remove(nestedTab.getName());
		log.debug("removeUITabButNotTheData, removed: " + nestedTab.getName()
				+ " , number of tabs left: " + nestedTabs.size());
	}

	@Override
	public NestedTab createNestedTab() throws DynamoConfigurationException,
			ConfigurationException {
		log.debug("createNestedTab()");
		return createNestedDefaultTab(null);
	}

	@Override
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections)
			throws ConfigurationException {
		tabFolder.removeSelectionListener(listener);
		Integer newTabNumber = getNumberOfTabs();
		log.debug("createNestedDefaultTab, newTabNumber = " + newTabNumber);
		// return new RelativeRiskTab(defaultSelections, this.getTabManager()
		// .getTabFolder(), tabName, getDynamoSimulationObject(),
		// selectedNode, helpGroup);
		NestedTab relRiskTab = null;
		TabRelativeRiskConfigurationData currentConfiguration = getDataManager().getConfiguration(newTabNumber);
		if (preConstructionCheck(currentConfiguration)) {
			relRiskTab = new RelativeRiskTab(tabFolder, newTabNumber,
					helpGroup, getDataManager(), this);
//			log.debug("relRiskTab, created, tabItemHash: " + System.identityHashCode(relRiskTab.getMyTabItem()));
			
			nestedTabs.put(relRiskTab.getName(), relRiskTab);
		} else {
			getDataManager().removeFromDynamoSimulationObject(currentConfiguration);
		}
		tabFolder.setSelection(newTabNumber);
		tabFolder.addSelectionListener(listener);
		return relRiskTab;
	}

	@Override
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
//			tabItem.dispose();
		}
		log.debug("After removal: " + tabFolder.getItems().length + " Items, "
				+ nestedTabs.size() + " tabs");
		// Renumber the items
//		this.renumberAndRenameItems();
//		log.debug("After renumber: " + tabFolder.getItems().length + " Items, "
//				+ nestedTabs.size() + " tabs");
		// Redraw the tabPlatform
		try {
			this.redraw_FromManager();
		} catch (NoMoreDataException e) {
			// this should not occur

			e.printStackTrace();
		}
	}

	@Override
	public void deleteNestedTabPlusData(NestedTab nestedTab)
			throws ConfigurationException {
		RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;
		relativeRiskTab.removeTabDataObject();
		// Not needed, the rebuild takes care of this....
		// String tabName = relativeRiskTab.getName();
		// nestedTabs.remove(tabName);
		// TabItem[] tabItems = tabFolder.getItems();
		// for (int count = 0; count < tabItems.length; count++) {
		// TabItem currentItem = tabItems[count];
		//
		// if ((tabName != null) && (currentItem != null)
		// && (tabName.equals(currentItem.getText()))) {
		// // The tabItem corresponding to the nestedTab.
		// currentItem.dispose(); // Zap it.
		// }
		// }
	}

	public Set<String> getConfigurations() {
		Map<Integer, TabRelativeRiskConfigurationData> configurations = this
				.getDynamoSimulationObject().getRelativeRiskConfigurations();
		// Conversion into a String keyset: but the contents are still integers
		Set<String> keySet = new LinkedHashSet<String>();
		for (Integer index : configurations.keySet()) {
			keySet.add(index.toString());
		}
		return keySet;
	}

	@Override
	public void refreshNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		if (nestedTab != null) {
			/*
			 * first find the index of the nestedTab, which can be found from
			 * the name (hopefully)
			 */
			RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;

			try {
				relativeRiskTab.refreshSelectionGroup();
			} catch (NoMoreDataException e) {

				Shell messageShell = new Shell(getUpperTabFolder().getDisplay());
				MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
				messageBox.setMessage(e.getMessage() + "\nTab is not made");

				if (messageBox.open() == SWT.OK) {
					messageShell.dispose();
				}

				messageShell.open();

			} catch (DynamoNoValidDataException e) {
				Shell messageShell = new Shell(getUpperTabFolder().getDisplay());
				MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
				messageBox.setMessage(e.getMessage() + "\nTab is deleted");
				// this.tabPlatformManager.deleteNestedTab();
				deleteNestedTab_FromManager();

				if (messageBox.open() == SWT.OK) {
					messageShell.dispose();
					e.printStackTrace();
				}
			}

		}
	}

	/*
	 * organized when updating dynamo-object
	 * 
	 * @Override public void renumberTabObject(NestedTab nestedTab, Integer
	 * index) { if (nestedTab != null) { / first find the index of the
	 * nestedTab, which can be found from the name (hopefully)
	 */
	/*
	 * RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;
	 * RelativeRiskTabDataManager manager= (RelativeRiskTabDataManager)
	 * relativeRiskTab.getDynamoTabDataManager();
	 * TabRelativeRiskConfigurationData singleConfiguration =
	 * manager.getSingleConfiguration(); singleConfiguration.setIndex(index);
	 * manager.setSingleConfiguration(singleConfiguration);
	 * 
	 * } }
	 */

	public void refreshFirstTab() throws ConfigurationException,
			DynamoNoValidDataException {
		/* first update the list of availlable relative risks */

		// RelativeRiskTabPlatformDataManager dataManager =
		// (RelativeRiskTabPlatformDataManager) ((RelativeRiskTab) this
		// .getTabManager().nestedTabs.get(RELATIVE_RISK + "1"))
		// .getDynamoTabDataManager();
		RelativeRiskTabDataManager dataManager = (RelativeRiskTabDataManager) ((RelativeRiskTab) nestedTabs
				.get(RELATIVE_RISK + "1")).getDynamoTabDataManager();
		/*
		 * this is the list of all configured RR's minus the RR's with risk
		 * factors and diseases that have not been choosen in this configuration
		 */
		dataManager.refreshAvaillableRRlist();

		// refreshNestedTab(this.getTabManager().nestedTabs.get(RELATIVE_RISK
		// + "1"));
		refreshNestedTab(nestedTabs.get(RELATIVE_RISK + "1"));
	}

	/*
	 * added by hendriek but not used
	 */
	public void refreshAllTabs() throws ConfigurationException {
		/* first update the list of availlable relative risks */

		// int oldTabNumber = this.getTabManager().getNumberOfTabs();
		int oldTabNumber = getNumberOfTabs();
		if (oldTabNumber > 0) {
/* 20090918 			dataManager.refreshAvaillableRRlist(); */
		}
		int newTabNumber = this.getDynamoSimulationObject()
				.getRelativeRiskConfigurations().size();
		/*
		 * delete tabs if there are more then needed : do not use
		 * "deleteNestedTabs, because this removes the tab data from the
		 * DynamoSimulationObject and that has already been done by the listener
		 * for riskfactor choice or disease choice.
		 */
		for (int i = 0; i < oldTabNumber - newTabNumber; i++) {
			/*
			 * note the the tabfolder in this object is the super tabfolder (of
			 * all groups, riskfactors, diseases etc) while we need the
			 * tabfolder for the relative risks which is found in the Tabmanager
			 */
			// TabItem tabItem = this.tabPlatformManager.getTabFolder().getItem(
			// oldTabNumber - i - 1);
			TabItem tabItem = tabFolder.getItem(oldTabNumber - i - 1);
			tabItem.dispose();
		}

		for (int i = 0; i < newTabNumber; i++) {
			String tabName = RELATIVE_RISK + Integer.toString(i/* + 1 */);
			// RelativeRiskTabPlatformDataManager dataManager =
			// (RelativeRiskTabPlatformDataManager) ((RelativeRiskTab) this
			// .getTabManager().nestedTabs.get(tabName))
			// .getDynamoTabDataManager();
			/*
			 * RelativeRiskTabDataManager dataManager =
			 * (RelativeRiskTabDataManager) ((RelativeRiskTab) nestedTabs
			 * .get(tabName)).getDynamoTabDataManager();
			 */// TODO
			// dataManager.setDynamoSimulationObject(getDynamoSimulationObject());
			// dataManager.reloadConfigurationsFromModelObject();
			// refreshNestedTab(this.getTabManager().nestedTabs.get(tabName));
			refreshNestedTab(nestedTabs.get(tabName));
		}

	}

	private boolean preConstructionCheck(TabRelativeRiskConfigurationData configuration) throws ConfigurationException {
		boolean success = false;
		String defaultFrom = null;
		String defaultTo = null;
		getDataManager();
		RelRisksCollectionForDropdown possibleRelRisksProvider = RelRisksCollectionForDropdown.getInstance(dynamoSimulationObject, dataManager.getTreeLists());
		possibleRelRisksProvider.relRiskRefresh4Init(configuration, dynamoSimulationObject);
		Set<String> fromList = possibleRelRisksProvider.updateFromList();
		if (fromList.size() != 0) {
			defaultFrom = fromList.iterator().next();
			Set<String> toList = possibleRelRisksProvider
					.updateToList(defaultFrom);
			if (toList.size() != 0) {
				defaultTo = toList.iterator().next();
				Set<String> fileNameSet = possibleRelRisksProvider
						.updateRRFileList(defaultFrom, defaultTo);
				if (fileNameSet.size() != 0) {
					success = true;
				}
			}
		}
		if (!success) {
			MessageBox box = new MessageBox(
					this.getUpperTabFolder().getShell(), SWT.ERROR_UNSPECIFIED);
			box.setText("Error creating tab.");
			box.setMessage("No more relative risks avalable for configuring.");
			box.open();
		}
		return success;
	}

	private void handleErrorMessage(Exception e) {
		e.printStackTrace();
		MessageBox box = new MessageBox(this.getUpperTabFolder().getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error while refreshing tab: /n" + e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}
}