package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAgnosticFactory_MK2PlusChildren {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testRiskFactorCategorical() {
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "riskfactor_categorical_config1.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "riskfactor_categorical_config1_after.xml";
		@SuppressWarnings("unused")
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
	}

//	@Test
	public void testRiskFactorCategorical_copy() {
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "riskfactor_categorical_config1.xml";
		File configurationFile = new File(configurationFilePath);
//		String outputFilePath = "data" + File.separator + "development"
//				+ File.separator + "stax_population_size.xml";
//		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			RelRiskFromRiskFactorCategoricalFactory theFactory = new RelRiskFromRiskFactorCategoricalFactory(); 
			Object result = theFactory.manufacture(configurationFile, "riskfactor_categorical");
			assertNotNull(result);
//			try {
//				StAXAgnosticWriter.produceFile((FileControlSingleton
//						.getInstance()).get(rootElementName),
//						(HashMap<Integer, Object>) result, outputFile);
//			} catch (XMLStreamException e) {
//				e.printStackTrace();
//				assertNull(e); // Force error.
//			} catch (UnexpectedFileStructureException e) {
//				e.printStackTrace();
//				assertNull(e); // Force error.
//			} catch (IOException e) {
//				e.printStackTrace();
//				assertNull(e); // Force error.
//			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		} catch (DynamoInconsistentDataException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestAgnosticFactory_MK2PlusChildren.class);
	}
}
