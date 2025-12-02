package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorContinuousFactory;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;

public class RunnableRelRiskForRiskFactorContinuousManufacturing implements Runnable {
	Log log = LogFactory.getLog(getClass().getName());

	@SuppressWarnings("unchecked")
	public void run() {
		@SuppressWarnings("unused")
		DataBindingContext dbc = new DataBindingContext();
		String rootElementName = FileControlEnum.RELRISKFROMRISKFACTORCONTINUOUS.getRootElementName();
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "relriskforriskfactorcontinuous_default.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator
				+ "relriskforriskfactorcontinuous_read_written.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			// Create XML-file with defaults and write it to disk.
			RelRiskFromRiskFactorContinuousFactory theFactory = new RelRiskFromRiskFactorContinuousFactory();
			Object defaultResult = theFactory.manufactureDefault();
			try {
				FileControlSingleton instance = FileControlSingleton
						.getInstance();
				FileControlEnum myEnum = instance.get(rootElementName);
				StAXAgnosticWriter.produceFile(myEnum,
						(HashMap<Integer, Object>) defaultResult,
						configurationFile);
				// Read defaults file and write it out again.
				Object result = theFactory.manufactureObservable(configurationFile, rootElementName);
				assertNotNull(result);
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get(rootElementName),
						(HashMap<Integer, Object>) result, outputFile);
				// assertEquals( 0, outputFile.compareTo(configurationFile));
			} catch (XMLStreamException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (DynamoInconsistentDataException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}
}
