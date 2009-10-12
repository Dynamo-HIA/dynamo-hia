/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * Handles all actions of the nested disease tabs
 * 
 * @author schutb
 * 
 *         20091006 mondeelr The nameSet returned by getConfigurations() no
 *         longer is produced directly from the configurationObject. It is now
 *         filtered to contain only diseaseNames that are still available. This
 *         extra functionality is nescessary because diseases can now be
 *         deleted....
 */

public class DiseasesTabPlatform extends TabPlatform {

	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	private static final String DISEASES = "Diseases";
	private static final String DISEASE = "Disease";

	private TreeAsDropdownLists treeLists;

	/**
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException
	 */
	public DiseasesTabPlatform(TabFolder upperTabFolder,
			DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode, HelpGroup helpGroup)
			throws ConfigurationException {
		super(upperTabFolder, DISEASES, selectedNode, dynamoSimulationObject,
				helpGroup, null);
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		createContent();
	}

	@Override
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections)
			throws ConfigurationException {
		tabFolder.removeSelectionListener(listener);
		// int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		int newTabNumber = getNumberOfTabs() + 1;
		String tabName = DISEASE + newTabNumber;
		// return new DiseaseTab(defaultSelections,
		// this.getTabManager().getTabFolder(),
		DiseaseTab newTab = new DiseaseTab(defaultSelections, tabFolder,
				tabName, getDynamoSimulationObject(), selectedNode, helpGroup);
		nestedTabs.put(tabName, newTab);
		tabFolder.setSelection(newTabNumber-1);
		tabFolder.addSelectionListener(listener);
		
	
		return newTab;
	}

	@Override
	public NestedTab createNestedTab() throws ConfigurationException {
		return createNestedDefaultTab(null);
	}

	@Override
	public String getNestedTabPrefix() {
		return DISEASE;
	}

	/**
	 * The name didn't cover the functionality, the method returns a Set of
	 * diseasenames. 
	 * 
	 * Now it also checks these names against the diseases with a
	 * valid configuration and removes diseases that have for instance been
	 * deleted from the simulation configuration.
	 * 
	 * This upfront filtering was chosen because the constructing of the
	 * disease-tabs fails on absent diseases.
	 * 
	 * Consistency-check methods scattered throughout the simulationscreen
	 * functionality may try to salvage derived configurations by changing them
	 * from disappeared diseases to other items.
	 */
	@Override
	public Set<String> getConfigurations() {
		LinkedHashMap<String, ITabDiseaseConfiguration> configurations = (LinkedHashMap<String, ITabDiseaseConfiguration>) this
				.getDynamoSimulationObject().getDiseaseConfigurations();
		Set<String> configuredDiseaseNames = configurations.keySet();
		Set<String> validDiseaseNames = treeLists.getValidDiseaseNames();
		Set<String> approvedDiseaseNames = new LinkedHashSet<String>();
		Set<String> disApprovedDiseaseNames = new LinkedHashSet<String>();
		for (String diseaseName : configuredDiseaseNames) {
			if (validDiseaseNames.contains(diseaseName)) {
				approvedDiseaseNames.add(diseaseName);
			} else {
				disApprovedDiseaseNames.add(diseaseName);
			}
		}
		if (disApprovedDiseaseNames.size() != 0) {
			for (String disApprovedDiseaseName : disApprovedDiseaseNames) {
				configurations.remove(disApprovedDiseaseName);
			}
			getDynamoSimulationObject()
					.setDiseaseConfigurations(configurations);
		}
		return approvedDiseaseNames;
	}

	@Override
	public void deleteNestedTabPlusData(NestedTab nestedTab)
			throws ConfigurationException {
		DiseaseTab diseaseTab = (DiseaseTab) nestedTab;
		diseaseTab.removeTabDataObject();
		/* also remove in the other disease tabs */
		Map<String, ITabDiseaseConfiguration> newConfigurations = ((DiseaseTabDataManager) ((DiseaseTab) diseaseTab)
				.getDynamoTabDataManager()).getConfigurations();
		// for (String tabName :this.getTabManager().nestedTabs.keySet()){
		for (String tabName : nestedTabs.keySet()) {
			DiseaseTabDataManager dataManager = (DiseaseTabDataManager)
			// ((DiseaseTab)this.getTabManager().nestedTabs.get(tabName)).getDynamoTabDataManager();
			((DiseaseTab) nestedTabs.get(tabName)).getDynamoTabDataManager();
			dataManager.setConfigurations(newConfigurations);
		}
	}

	@Override
	public void refreshNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		DiseaseTab diseaseTab = (DiseaseTab) nestedTab;
		try {
			diseaseTab.refreshSelectionGroup();
		}

		catch (NoMoreDataException e) {

			Shell messageShell = new Shell(getUpperTabFolder().getDisplay());
			MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
			messageBox.setMessage("WARNING:\n" + e.getMessage()
					+ "\nTab is not made");
			messageBox.setText("WARNING");
			if (messageBox.open() == SWT.OK) {
				messageShell.dispose();
			}

			messageShell.open();

		} catch (DynamoNoValidDataException e) {
			Shell messageShell = new Shell(getUpperTabFolder().getDisplay());
			MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
			messageBox.setText("WARNING");
			messageBox.setMessage("WARNING:\n" + e.getMessage()
					+ "\nTab is deleted");
			// this.tabPlatformManager.deleteNestedTab();
			deleteNestedTab_FromManager();

			if (messageBox.open() == SWT.OK) {
				messageShell.dispose();
				e.printStackTrace();
			}
		}
	}

}