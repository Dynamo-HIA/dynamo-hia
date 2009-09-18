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
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

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
		int newTabNumber = getNumberOfTabs() + 1;
		String tabName = SCENARIO + newTabNumber;
		// return new ScenarioTab(defaultSelections,
		// this.getTabManager().getTabFolder(),
		return new ScenarioTab(defaultSelections, tabFolder, tabName,
				getDynamoSimulationObject(), dataBindingContext, selectedNode,
				helpGroup);
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

}