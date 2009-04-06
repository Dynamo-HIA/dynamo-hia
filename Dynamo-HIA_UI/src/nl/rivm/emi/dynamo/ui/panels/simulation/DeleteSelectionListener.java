package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabItem;

public class DeleteSelectionListener extends AbstractLoggingClass 
	implements SelectionListener {

	private TabPlatform tabPlatform = null;
	
	public DeleteSelectionListener(TabPlatform platform) {
		this.tabPlatform = platform;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");		
	}

	public void widgetSelected(SelectionEvent arg0) {		
		// Remove the selected tab
		this.tabPlatform.getTabManager().deleteNestedTab();
	}

}
