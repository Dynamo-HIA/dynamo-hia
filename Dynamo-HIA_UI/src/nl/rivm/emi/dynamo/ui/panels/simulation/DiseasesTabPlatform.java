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
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.support.ChoosableDiseaseNameManager;
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

	private final ChoosableDiseaseNameManager choosableDiseaseNameManager;
	private Boolean listenerWorking = false;

	public Boolean getListenerWorking() {
		return listenerWorking;
	}

	synchronized public void setListenerWorking(Boolean listenerWorking) {
		this.listenerWorking = listenerWorking;
	}

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
		log.debug("Constructed super, now some local thingies, my instance: "
				+ this);
		choosableDiseaseNameManager = new ChoosableDiseaseNameManager(this);
		createContent();
	}

	@Override
	public NestedTab createNestedTab() throws ConfigurationException {
		return createNestedDefaultTab(null);
	}

	/**
	 * @param defaultSelections
	 *            For diseases this Set contains either a null value for a new
	 *            tab to be created or a single name when the tab is created for
	 *            a configuration from the modelobject.
	 */
	@Override
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections)
			throws ConfigurationException {
		synchronized (tabFolder) {
			tabFolder.removeSelectionListener(listener);
			int newTabNumber = getNumberOfTabs() + 1;
			String tabName = DISEASE + newTabNumber;
			DiseaseTab newTab = new DiseaseTab(defaultSelections, tabFolder,
					tabName, getDynamoSimulationObject(), selectedNode,
					helpGroup, this);
			nestedTabs.put(tabName, newTab);
			tabFolder.addSelectionListener(listener);
			return newTab;
		}
	}

	public ChoosableDiseaseNameManager getChoosableDiseaseNameManager() {
		return choosableDiseaseNameManager;
	}

	@Override
	public String getNestedTabPrefix() {
		return DISEASE;
	}

	/**
	 * 20091012 mondeelr Pulled down from TabPlatform to make it more specific.
	 */
	@Override
	public void createDefaultTabs_FromManager()
			throws DynamoConfigurationException, ConfigurationException {
		Set<String> cleanedConfigurationDiseaseNames = choosableDiseaseNameManager
				.getAndCleanDiseaseNames((LinkedHashMap<String, ITabDiseaseConfiguration>) getDynamoSimulationObject()
						.getDiseaseConfigurations());
		// Some debugging stuff.
		StringBuffer concatDefTabKeyVals = new StringBuffer();
		for (String keyValue : cleanedConfigurationDiseaseNames) {
			concatDefTabKeyVals.append(keyValue + ", ");
		}
		log.debug("cleanedDiseaseNamesFromModelObject: " + concatDefTabKeyVals);
		// Debugging stuff ends.

		for (String defaultTabKeyValue : cleanedConfigurationDiseaseNames) {
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
				choosableDiseaseNameManager.setChosenDiseaseName(
						defaultTabKeyValue, nestedTab.getName());
			}
		} // for ends.
		try {
			this.redraw_FromManager();
		} catch (NoMoreDataException e) {
			// should not occur as this should have been spotted earlier
			e.printStackTrace();
		}
	}

	/**
	 * Not used for diseases.
	 */
	@Override
	public Set<String> getConfigurations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteNestedTabPlusData(NestedTab nestedTab)
			throws ConfigurationException {
		DiseaseTab diseaseTab = (DiseaseTab) nestedTab;
		diseaseTab.removeTabDataObject();
		/* also remove in the other disease tabs */
//		Map<String, ITabDiseaseConfiguration> newConfigurations = ((DiseaseTabDataManager) ((DiseaseTab) diseaseTab)
//				.getDynamoTabDataManager()).getConfigurations();
		// for (String tabName :this.getTabManager().nestedTabs.keySet()){
		for (String tabName : nestedTabs.keySet()) {
			DiseaseTabDataManager dataManager = (DiseaseTabDataManager)
			((DiseaseTab) nestedTabs.get(tabName)).getDynamoTabDataManager();
//			dataManager.setConfigurations(newConfigurations);
			dataManager.touchConfigurations();
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

	public BaseNode getSelectedNode() {
		return selectedNode;
	}

}