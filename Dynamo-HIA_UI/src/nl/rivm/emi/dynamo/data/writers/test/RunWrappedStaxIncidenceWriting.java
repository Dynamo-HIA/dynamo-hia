package nl.rivm.emi.dynamo.data.writers.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.notinuse.IncidenceIntegerFactory;
import nl.rivm.emi.dynamo.data.writers.obsolete.StAXWriterEntryPoint;
import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.prototype.SingleAgeGenderComposite;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class RunWrappedStaxIncidenceWriting implements Runnable {
	Log log = LogFactory.getLog(this.getClass().getName());

	public void run() {
		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator
					+ "staxwriterincidenceoutput.xml";
			File configurationFile = new File(configurationFilePath);
			log.debug(configurationFile.getAbsolutePath());
			AgeMap<SexMap<IObservable>> testContainer = IncidenceIntegerFactory
					.constructAllZeroesModel();
			StAXWriterEntryPoint.produceFile(testContainer, configurationFile);
			assertNotNull(testContainer);
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
