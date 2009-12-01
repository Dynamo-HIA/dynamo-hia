package nl.rivm.emi.dynamo.ui.panels.simulation.listeners;

import nl.rivm.emi.dynamo.ui.main.base.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.panels.simulation.TabPlatform;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

/**
 * 
 * Listener that deletes an existing nested tab
 * 
 * @author schutb
 * 
 */
public class DeleteSelectionListener implements SelectionListener {
	protected Log log = LogFactory.getLog(this.getClass().getName());

	private TabPlatform tabPlatform = null;
	private DataAndFileContainer theModal;

	public DeleteSelectionListener(TabPlatform platform,
			DataAndFileContainer theModal) {
		this.tabPlatform = platform;
		this.theModal = theModal;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		// Remove the selected tab
		try {
			// this.tabPlatform.getTabManager().deleteNestedTab();
			tabPlatform.deleteNestedTab_FromManager();
			theModal.setChanged(true);
		} catch (ConfigurationException ce) {
			this.handleErrorMessage(ce);
		}
	}

	private void handleErrorMessage(Exception e) {
		this.log.fatal(e);
		e.printStackTrace();
		// MessageBox box = new MessageBox(this.tabPlatform.getTabManager()
		// .getTabFolder().getParent().getShell(), SWT.ERROR_UNSPECIFIED);
		MessageBox box = new MessageBox(tabPlatform.getTabFolder().getParent()
				.getShell(), SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during creation of a new tab "
				+ e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}
}
