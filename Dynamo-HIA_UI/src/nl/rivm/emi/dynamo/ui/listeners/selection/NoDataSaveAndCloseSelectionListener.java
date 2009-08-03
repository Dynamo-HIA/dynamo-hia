package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticGroupWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.AbstractHelplessModal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class NoDataSaveAndCloseSelectionListener extends AbstractLoggingClass
		implements SelectionListener {
	AbstractHelplessModal modalParent;
	Shell shell2Handle;

	public NoDataSaveAndCloseSelectionListener(Shell shell, AbstractHelplessModal modalParent) {
		shell2Handle = shell;
		this.modalParent = modalParent;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	synchronized public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		String filePath = modalParent.getConfigurationFilePath();
		File configurationFile = new File(filePath);
		try {
			String rootElementName = (String) modalParent.getRootElementName();
			log.debug("rootElementName: " + rootElementName
					+ " , no modelObject, " + " configurationFile: "
					+ configurationFile);
					StAXAgnosticGroupWriter.produceFile(rootElementName,
							null,
							configurationFile);
					shell2Handle.dispose();
		} catch (XMLStreamException e) {
			this.handleErrorMessage(e);
		} catch (UnexpectedFileStructureException e) {
			this.handleErrorMessage(e);
		} catch (IOException e) {
			this.handleErrorMessage(e);
		} catch (DynamoOutputException e) {
			this.handleErrorMessage(e);
		} catch (DynamoConfigurationException e) {
			this.handleErrorMessage(e);
		}
	}

	private void handleErrorMessage(Exception e) {
		this.log.fatal(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(this.modalParent.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during save " + e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}
}
