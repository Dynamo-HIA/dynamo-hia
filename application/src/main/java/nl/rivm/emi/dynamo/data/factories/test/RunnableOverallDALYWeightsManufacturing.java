package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;

public class RunnableOverallDALYWeightsManufacturing implements Runnable {
	Log log = LogFactory.getLog(getClass().getName());

	public void run() {
		DataBindingContext dbc = new DataBindingContext();
		String rootElementName = FileControlEnum.OVERALLDALYWEIGHTS.getRootElementName();
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Overall daly weights.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_overall_daly_weights.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new OverallDALYWeightsFactory()
					.manufactureObservable(configurationFile, rootElementName);
			assertNotNull(result);
			try {
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get(rootElementName),
						(HashMap<Integer, Object>) result, outputFile);
			} catch (XMLStreamException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		} catch (DynamoInconsistentDataException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}

}