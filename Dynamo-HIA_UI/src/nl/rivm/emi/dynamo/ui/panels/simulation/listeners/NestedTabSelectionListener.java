package nl.rivm.emi.dynamo.ui.panels.simulation.listeners;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.simulation.DiseaseTab;
import nl.rivm.emi.dynamo.ui.panels.simulation.DiseasesTabPlatform;
import nl.rivm.emi.dynamo.ui.panels.simulation.NestedTab;
import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskTab;
import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRisksTabPlatform;
import nl.rivm.emi.dynamo.ui.panels.simulation.TabPlatform;
import nl.rivm.emi.dynamo.ui.panels.util.NestedTabsMap;
import nl.rivm.emi.dynamo.ui.support.RelRisksCollectionForDropdown;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabItem;

public class NestedTabSelectionListener implements SelectionListener {

	Log log = LogFactory.getLog(this.getClass().getName());

	TabPlatform myTabPlatform = null;

	public NestedTabSelectionListener(TabPlatform myTabPlatform) {
		super();
		this.myTabPlatform = myTabPlatform;
		log.debug("Constructed for: " + myTabPlatform.getName());
	}

	public void widgetSelected(SelectionEvent e) {
		if(myTabPlatform instanceof DiseasesTabPlatform){
			((DiseasesTabPlatform) myTabPlatform).setListenerWorking(true);
		}
		log.debug("TabPlatform : " + myTabPlatform.getClass().getSimpleName() + " Selected item index = "
				+ myTabPlatform.getTabFolder().getSelectionIndex());
		log.debug("Selected item = "
				+ (myTabPlatform.getTabFolder().getSelection() == null ? "null"
						: myTabPlatform.getTabFolder().getSelection()[0]
								.toString()));

		int selected = myTabPlatform.getTabFolder().getSelectionIndex();
		if (selected > -1) {
			TabItem item = myTabPlatform.getTabFolder().getItem(selected);
			try {
				log.debug("item.getText() >" + item.getText() + "<");
				log.debug("nestedTabs.size()"
						+ myTabPlatform.getNestedTabs().size());
				log.debug("nestedTabs" + myTabPlatform.getNestedTabs());
				NestedTab tab = myTabPlatform.getNestedTabs().get(
						item.getText());
				if (tab != null) {
					if (tab instanceof RelativeRiskTab) {
						DynamoSimulationObject dynSimObj = myTabPlatform
								.getDynamoSimulationObject();
						int tabIndex = ((RelativeRiskTab) tab).getTabIndex();
						TabRelativeRiskConfigurationData configuration = dynSimObj
								.getRelativeRiskConfigurations().get(tabIndex);
						RelRisksCollectionForDropdown possibleRelRisksProvider = ((RelativeRisksTabPlatform) myTabPlatform)
								.getDataManager()
								.getRelRisksCollectionForDropdown();
						possibleRelRisksProvider.relRiskRefresh4Init(
								configuration, dynSimObj);
					} else {
						 if (!(tab instanceof DiseaseTab)) {
						myTabPlatform.refreshNestedTab(tab);
						 }
					}
				} else {
					NestedTabsMap nestedTabs = myTabPlatform.getNestedTabs();
					StringBuffer nestedTabNames = new StringBuffer(
					"Nested Tab names: ");
					Set<String> keySet = nestedTabs.keySet();
					for (String nestedTabName : keySet) {
						nestedTabNames.append(nestedTabName + ", ");
					}
					TabItem[] tabItems = item.getParent().getItems();
					StringBuffer tabItemNames = new StringBuffer(
							"Sibling tabItem names: ");
					for (TabItem tabItem : tabItems) {
						tabItemNames.append(tabItem.getText() + ", ");
					}
					tabItemNames.setLength(tabItemNames.length() - 2);
					log
							.warn("Orphaned widget: "
									+ item.getClass().getSimpleName()
									+ ", "
									+ System.identityHashCode(item)
									+ " has no corresponding NestedTab, parent-tooltip: "
									+ item.getParent().getToolTipText()
									+ ",\n parent-instance: "
									+ System.identityHashCode(item.getParent())
									+ " >>> " + tabItemNames.toString());
				}
			} catch (ConfigurationException ce) {
				handleErrorMessage(ce);
			} catch (NoMoreDataException e1) {
				// added by Hendriek: removes the item in case of a no data
				// situation
				handleErrorMessage(e1);
				int index = myTabPlatform.getTabFolder().getSelectionIndex();
				TabItem tabItem = myTabPlatform.getTabFolder().getItem(index);
				NestedTab removedNestedTab = myTabPlatform.getNestedTabs().get(
						tabItem.getText());
				// Remove the data from the data object model
				try {
					myTabPlatform.deleteNestedTabPlusData(removedNestedTab);
				} catch (ConfigurationException e2) {
					// TODO Auto-generated catch block
					handleErrorMessage(e2);
				}
				tabItem.dispose();
			} catch (DynamoNoValidDataException e3) {
				// added by Hendriek: removes the item in case of a no data
				// situation
				handleErrorMessage(e3);
				int index = myTabPlatform.getTabFolder().getSelectionIndex();
				TabItem tabItem = myTabPlatform.getTabFolder().getItem(index);
				NestedTab removedNestedTab = myTabPlatform.getNestedTabs().get(
						tabItem.getText());
				// Remove the data from the data object model
				try {
					myTabPlatform.deleteNestedTabPlusData(removedNestedTab);
				} catch (ConfigurationException e2) {
					// TODO Auto-generated catch block
					handleErrorMessage(e2);
				}
				tabItem.dispose();
			}
		}
		if(myTabPlatform instanceof DiseasesTabPlatform){
			((DiseasesTabPlatform) myTabPlatform).setListenerWorking(false);
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private void handleErrorMessage(Exception e) {
		e.printStackTrace();
		MessageBox box = new MessageBox(
				myTabPlatform.getTabFolder().getShell(), SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during creation of a new tab "
				+ e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}
}
