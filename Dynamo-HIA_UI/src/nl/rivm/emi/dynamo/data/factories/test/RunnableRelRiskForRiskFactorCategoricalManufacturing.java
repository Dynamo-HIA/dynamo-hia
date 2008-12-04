package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.PrevalencesCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForRiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesDurationFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;

public class RunnableRelRiskForRiskFactorCategoricalManufacturing implements Runnable {
	Log log = LogFactory.getLog(getClass().getName());

	public void run() {
		DataBindingContext dbc = new DataBindingContext();
		String rootElementName = FileControlEnum.RELRISKFORRISKFACTORCATEGORICAL.getRootElementName();
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + rootElementName +"_default.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator
				+  rootElementName +"_read_written.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			// Create XML-file with defaults and write it to disk.
			RelRiskForRiskFactorCategoricalFactory theFactory = new RelRiskForRiskFactorCategoricalFactory();
			int numCategories = 6;
			Object defaultResult = theFactory.manufactureDefault(numCategories);
			try {
				FileControlSingleton instance = FileControlSingleton
						.getInstance();
				FileControlEnum myEnum = instance.get( rootElementName);
				StAXAgnosticWriter.produceFile(myEnum,
						(HashMap<Integer, Object>) defaultResult,
						configurationFile);
				// Read defaults file and write it out again.
				Object result = theFactory.manufactureObservable(configurationFile);
				assertNotNull(result);
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get( rootElementName),
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
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}
}
