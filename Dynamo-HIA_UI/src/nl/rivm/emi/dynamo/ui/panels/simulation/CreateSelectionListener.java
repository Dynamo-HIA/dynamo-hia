package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

public class CreateSelectionListener extends AbstractLoggingClass
	implements SelectionListener {

	private TabPlatform tabPlatform = null;
	
	public CreateSelectionListener(TabPlatform platform) {
		this.tabPlatform = platform;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub
		try {
			this.tabPlatform.getTabManager().
				createNestedTab();
		} catch (DynamoConfigurationException e) {
			this.handleErrorMessage(e);
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
