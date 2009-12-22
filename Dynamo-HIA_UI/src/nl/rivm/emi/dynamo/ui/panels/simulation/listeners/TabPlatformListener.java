package nl.rivm.emi.dynamo.ui.panels.simulation.listeners;

import nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabsDataPanel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabItem;

public class TabPlatformListener implements Listener {
	Log log = LogFactory.getLog(this.getClass().getSimpleName());
	DynamoTabsDataPanel myTabsDataPanel;

	public TabPlatformListener(DynamoTabsDataPanel myTabsDataPanel) {
		super();
		this.myTabsDataPanel = myTabsDataPanel;
		log.debug("Constructed for: "
				+ myTabsDataPanel.getClass().getSimpleName());
	}

	@Override
	public void handleEvent(Event event) {
		TabItem item = (TabItem) event.item;
		String tabId = item.getText();
		log.debug("HandleEvent for: " + tabId);
		try {
			if (tabId == "Risk Factor")
				myTabsDataPanel.getRiskFactorTab().redraw();
			if (tabId == "Diseases"){
				/*1-11-2009 next line added by Hendriek to make dropdown list present at startup */
				myTabsDataPanel.getDiseasesTabPlatform().refreshAllTabs();
				myTabsDataPanel.getDiseasesTabPlatform().redraw();}
			if (tabId == "Relative Risks") {

				// changed by Hendriek
				/*
				 * this refreshes the list with RR availlable from new disease
				 * or riskfactor choices
				 */
				/*
				 * 20090918 Rene Trying to keep refreshing to an absolute
				 * minimum to solve concurrency problems.
				 */
				// RelRisksCollectionForDropdown.getInstance(myTabsDataPanel
				// .getDynamoSimulationObject(), myTabsDataPanel
				// .getSelectedNode());
				myTabsDataPanel.getRelativeRisksTabPlatform().refreshAllTabs();
				myTabsDataPanel.getRelativeRisksTabPlatform().redraw();
				// tab2.refreshFirstTab();
				// tab2.redraw();
			}
			if (tabId == "Scenarios") {
				// myTabsDataPanel.getScenariosTabPlatform().refreshFirstTab();
				myTabsDataPanel.getScenariosTabPlatform().refreshAllTabs();
				myTabsDataPanel.getScenariosTabPlatform().redraw();
			}
		} catch (ConfigurationException ce) {
			handleErrorMessage(ce);
		}
	}

	private void handleErrorMessage(Exception e) {
		e.printStackTrace();
		MessageBox box = new MessageBox(myTabsDataPanel.getMyParent()
				.getShell(), SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during creation of a new tab "
				+ e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}
}
