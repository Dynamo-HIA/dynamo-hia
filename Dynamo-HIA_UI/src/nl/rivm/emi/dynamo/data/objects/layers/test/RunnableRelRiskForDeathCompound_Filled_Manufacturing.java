package nl.rivm.emi.dynamo.data.objects.layers.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.PrevalencesCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesDurationFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;

public class RunnableRelRiskForDeathCompound_Filled_Manufacturing implements
		Runnable {
	Log log = LogFactory.getLog(getClass().getName());

	public void run() {
		DataBindingContext dbc = new DataBindingContext();
		String rootElementName = FileControlEnum.RELRISKFORDEATHCOMPOUND
				.getRootElementName();
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + rootElementName + "_filled.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + rootElementName
				+ "_filled_observablemodelled_written.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			// Create XML-file with defaults and write it to disk.
			AgnosticFactory theFactory = FactoryProvider
					.getRelevantFactoryByRootNodeName(rootElementName);
			Object modelObject = theFactory
					.manufactureObservable(configurationFile, "relriskfordeathcompound");
			FileControlSingleton instance = FileControlSingleton.getInstance();
			FileControlEnum myEnum = instance.get(rootElementName);
			StAXAgnosticWriter.produceFile(myEnum,
					(HashMap<Integer, Object>) modelObject, outputFile);
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
		// catch (DynamoInconsistentDataException e) {
		// e.printStackTrace();
		// assertNull(e); // Force error.
		// }
		catch (ConfigurationException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		} catch (DynamoInconsistentDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}

	}
}
