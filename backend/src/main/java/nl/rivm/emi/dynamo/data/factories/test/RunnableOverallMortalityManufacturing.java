package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;

public class RunnableOverallMortalityManufacturing implements Runnable {
	Log log = LogFactory.getLog(getClass().getName());

	@SuppressWarnings("unchecked")
	public void run() {
		@SuppressWarnings("unused")
		DataBindingContext dbc = new DataBindingContext();
		String rootElementName = FileControlEnum.OVERALLMORTALITY
				.getRootElementName();
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Overall mortality_corrected.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_observable_overall_mortality_.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new OverallMortalityFactory()
					.manufactureObservable(configurationFile, rootElementName);
			assertNotNull(result);
			StAXAgnosticWriter.produceFile((FileControlSingleton.getInstance())
					.get(rootElementName), (HashMap<Integer, Object>) result,
					outputFile);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (UnexpectedFileStructureException e) {
			e.printStackTrace();
			assertNull(e);
			// Force error.
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (DynamoInconsistentDataException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
			}
	}

}
