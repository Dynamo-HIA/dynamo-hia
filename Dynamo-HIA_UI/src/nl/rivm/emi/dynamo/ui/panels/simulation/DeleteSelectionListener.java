package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
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
		try {
			this.tabPlatform.getTabManager().deleteNestedTab();
		} catch (ConfigurationException ce) {
			this.handleErrorMessage(ce);
		}
	}

	private void handleErrorMessage(Exception e) {
		this.log.fatal(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(this.tabPlatform.getTabManager().getTabFolder().getParent().getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during creation of a new tab " + e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}
}
