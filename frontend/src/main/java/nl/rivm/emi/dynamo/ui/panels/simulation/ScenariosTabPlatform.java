/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class ScenariosTabPlatform extends TabPlatform {

	// private Log log = LogFactory.getLog(this.getClass().getName());

	private static final String SCENARIOS = "Scenarios";
	private static final String SCENARIO = "Scenario";

	/**
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException
	 */
	public ScenariosTabPlatform(TabFolder upperTabFolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		super(upperTabFolder, SCENARIOS, selectedNode, dynamoSimulationObject,
				helpGroup, dataBindingContext);
		createContent();
		
	}

	

	@Override
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections)
			throws ConfigurationException {
		// int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		tabFolder.removeSelectionListener(listener);
		int newTabNumber = getNumberOfTabs() + 1;
		String tabName = SCENARIO + newTabNumber;
		// return new ScenarioTab(defaultSelections,
		// this.getTabManager().getTabFolder(),
		ScenarioTab scenarioTab = new ScenarioTab(defaultSelections, tabFolder,
				tabName, getDynamoSimulationObject(), dataBindingContext,
				selectedNode, helpGroup);
		if (scenarioTab.getScenarioResultGroup() != null) {
			nestedTabs.put(tabName, scenarioTab);
			tabFolder.setSelection(newTabNumber - 1);
			tabFolder.addSelectionListener(listener);
		} else {
			// does not work, implemented elsewhere
			//ITabScenarioConfiguration singleConfiguration = ((ScenarioTabDataManager) scenarioTab
		//			.getDynamoTabDataManager()).getSingleConfiguration();
		//	if (singleConfiguration != null)
		//		scenarioTab.getDynamoSimulationObject()
		//				.getScenarioConfigurations()
			//			.remove(singleConfiguration);
			scenarioTab = null;
		}
		return scenarioTab;
	}

	@Override
	public String getNestedTabPrefix() {
		return SCENARIO;
	}

	@Override
	public NestedTab createNestedTab() throws DynamoConfigurationException,
			ConfigurationException {
		return createNestedDefaultTab(null);
	}

	@Override
	public void deleteNestedTabPlusData(NestedTab nestedTab)
			throws ConfigurationException {
		ScenarioTab scenarioTab = (ScenarioTab) nestedTab;
		scenarioTab.removeTabDataObject();
	}

	@Override
	public Set<String> getConfigurations() {
		Map<String, ITabScenarioConfiguration> configurations = this
				.getDynamoSimulationObject().getScenarioConfigurations();
		return configurations.keySet();
	}

	@Override
	public void refreshNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		try {
			if (nestedTab != null) {
				ScenarioTab scenarioTab = (ScenarioTab) nestedTab;

				scenarioTab.refreshResultGroup();
			}
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

	public void refreshFirstTab() throws ConfigurationException {
		// refreshNestedTab(this.getTabManager().nestedTabs.get(SCENARIO +
		// "1"));
		refreshNestedTab(nestedTabs.get(SCENARIO + "1"));
	}

	public void refreshAllTabs() throws ConfigurationException {
		int oldTabNumber = getNumberOfTabs();
		int newTabNumber = this.getConfigurations().size();
		/*
		 * if the number of old tabs is larger than the number of new ones,
		 * delete the extra tabs
		 */
		log.info("# oude tabs: " + oldTabNumber + " wordt " + newTabNumber);
		for (int i = 0; i < oldTabNumber - newTabNumber; i++) {
			/*
			 * note the the tabfolder in this object is the super tabfolder (of
			 * all groups, riskfactors, diseases etc) while we need the
			 * tabfolder for the scanarios which is found in the Tabmanager
			 */
			TabItem tabItem = getTabFolder().getItem(oldTabNumber - i - 1);
			tabItem.dispose();
			log.info(" dispose tab " + i);
		}
		int i = 1;/* the first tab is tab 1 */
		/*
		 * now rebuild all the tabs, using fresh information from the
		 * DynamoSimulationObject
		 */
		for (String scenName : this.getConfigurations()) {
			/* get the name of the nested tab */
			String tabName = SCENARIO + Integer.toString(i);
			/* get the datamanager of the nested tab */
			ScenarioTabDataManager dataManager = (ScenarioTabDataManager) ((ScenarioTab) nestedTabs
					.get(tabName)).getDynamoTabDataManager();
			/*
			 * let the datamanage refresh the data of the singleconfiguration
			 * from the DYNAMOsimulation object
			 */
			log.info(" refresh scenario tab " + i);
			dataManager.refreshConfigurations(scenName);
			refreshNestedTab(nestedTabs.get(tabName));
			i++;
		}
	}
}