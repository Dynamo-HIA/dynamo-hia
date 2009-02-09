package nl.rivm.emi.dynamo.ui.listeners.selection;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.objects.layers.StaxWriterEntryPoint;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticTypedHashMapWriter;
import nl.rivm.emi.dynamo.ui.listeners.for_test.AbstractLoggingClass;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;

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
		String filePath = modalParent.getFilePath();
		File configurationFile = new File(filePath);
		try {
			Object modelObject = modalParent
					.getData();
			if(!(modelObject instanceof IStaxEventContributor)){
				Object rootElementName = modalParent.getRootElementName();
			StAXAgnosticTypedHashMapWriter.produceFile(FileControlSingleton
					.getInstance().get(rootElementName),
					(TypedHashMap) modelObject, configurationFile);
			} else {
				((StaxWriterEntryPoint)modelObject).writeToFile(configurationFile);
			}
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
