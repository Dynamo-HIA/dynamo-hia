package nl.rivm.emi.dynamo.ui.panels.button;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.notinuse.IncidenceIntegerFactory;
import nl.rivm.emi.dynamo.data.writers.StAXWriterEntryPoint;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class SaveSelectionListener extends AbstractLoggingClass implements
		SelectionListener {
	DataAndFileContainer modalParent;

	public SaveSelectionListener(DataAndFileContainer modalParent) {
		this.modalParent = modalParent;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDefaultSelected callback.");
	}

	public void widgetSelected(SelectionEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetSelected callback.");
		File configurationFile = new File(modalParent.getFilePath());
		try {
			StAXWriterEntryPoint.produceFile(
					(AgeMap<SexMap<IObservable>>) modalParent.getData(),
					configurationFile);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
