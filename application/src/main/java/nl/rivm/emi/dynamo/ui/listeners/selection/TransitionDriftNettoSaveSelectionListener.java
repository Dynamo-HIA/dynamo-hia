package nl.rivm.emi.dynamo.ui.listeners.selection;

/**
 * Specialized version, because new datastructure doesn't fit in the regular factories.
 */
//TODO(mondeelr) Hacked to ErrorLessNess.
import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXTransitionDriftNettoWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftNettoModal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

public class TransitionDriftNettoSaveSelectionListener implements SelectionListener {
	protected Log log = LogFactory.getLog(this.getClass().getName());
	TransitionDriftNettoModal modalParent;

	public TransitionDriftNettoSaveSelectionListener(
			TransitionDriftNettoModal modalParent) {
		this.modalParent = modalParent;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		String filePath = modalParent.getConfigurationFilePath();
		File configurationFile = new File(filePath);
		try {
			TransitionDriftNettoObject modelObject = modalParent.getData();
			String rootElementName = (String) modalParent.getRootElementName();
			log.debug("rootElementName" + rootElementName + " modelObject"
					+ modelObject + " configurationFile" + configurationFile);
			FileControlEnum fileControl = FileControlSingleton.getInstance()
					.get(rootElementName);
			StAXTransitionDriftNettoWriter.produceFile(fileControl, modelObject, configurationFile);
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
		} catch (nl.rivm.emi.cdm.exceptions.DynamoConfigurationException e) {
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
