package nl.rivm.emi.dynamo.ui.panels.simulation.listeners;

import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.panels.simulation.TabPlatform;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

/**
 * 
 * Listener that creates a new nested tab
 * 
 * @author schutb
 * 
 */
public class CreateSelectionListener extends AbstractLoggingClass implements
		SelectionListener {

	private TabPlatform tabPlatform = null;
	private DataAndFileContainer theModal = null;

	public CreateSelectionListener(TabPlatform platform,
			DataAndFileContainer theModal) {
		this.tabPlatform = platform;
		this.theModal = theModal;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		try {
//			this.tabPlatform.getTabManager().createNestedTab();
			tabPlatform.createNestedTab();
			theModal.setChanged(true);
		} catch (DynamoConfigurationException dce) {
			this.handleErrorMessage(dce);
		} catch (ConfigurationException ce) {
			this.handleErrorMessage(ce);
		}
	}

	private void handleErrorMessage(Exception e) {
		this.log.fatal(e);
		e.printStackTrace();
//		MessageBox box = new MessageBox(this.tabPlatform.getTabManager()
//				.getTabFolder().getParent().getShell(), SWT.ERROR_UNSPECIFIED);
		MessageBox box = new MessageBox(tabPlatform
				.getTabFolder().getParent().getShell(), SWT.ERROR_UNSPECIFIED);
		box.setText("Error occurred during creation of a new tab "
				+ e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}

}