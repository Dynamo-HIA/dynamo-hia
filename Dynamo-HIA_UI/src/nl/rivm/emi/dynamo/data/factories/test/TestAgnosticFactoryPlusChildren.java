package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.DALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.IncidencesFactory;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.PrevalencesCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForRiskFactorContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromOtherDiseaseFactory;
import nl.rivm.emi.dynamo.data.objects.RelRiskFromOtherDiseaseObject;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAgnosticFactoryPlusChildren {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	/**
	 * This test must not finish normally because of unexpected "male"and
	 * "female" tags instead of "sex".
	 */
	@Test
	public void testPopulationSize() {
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Population size.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_population_size.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new PopulationSizeFactory()
					.manufacture(configurationFile);
			assertNotNull(result);
			try {
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("populationsize"),
						(HashMap<Integer, Object>) result, outputFile);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}

	@Test
	public void testCorrectedPopulationSize() {
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Population size_corrected.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_population_size_corrected.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new PopulationSizeFactory()
					.manufacture(configurationFile);
			assertNotNull(result);
			try {
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("populationsize"),
						(HashMap<Integer, Object>) result, outputFile);
			} catch (XMLStreamException e) { // TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) { // TODO
				// Auto-generated
				// catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) { // TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	// This test must not finish normally because of unexpected "male"and
	// "female" tags instead of "sex".

	@Test
	public void testOverallMortality() {
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Overall mortality.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_overall_mortality.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new OverallMortalityFactory()
					.manufacture(configurationFile);
			assertNotNull(result);
			try {
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("overallmortality"),
						(HashMap<Integer, Object>) result, outputFile);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}

	@Test
	public void testCorrectedOverallMortality() {
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Overall mortality_corrected.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_overall_mortality_corrected.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new OverallMortalityFactory()
					.manufacture(configurationFile);
			assertNotNull(result);
			StAXAgnosticWriter.produceFile((FileControlSingleton.getInstance())
					.get("overallmortality"),
					(HashMap<Integer, Object>) result, outputFile);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
			// Force error.
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void testOverallDALYWeights() {
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Overall daly weights.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_overall_daly_weights.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new OverallDALYWeightsFactory()
					.manufacture(configurationFile);
			assertNotNull(result);
			try {
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("overalldalyweights"),
						(HashMap<Integer, Object>) result, outputFile);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}

	// This test must not finish normally because of a comma in a value field.
	@Test
	public void testOriginalPrevalence() {
		String configurationFilePath

		= "data" + File.separator + "Mehdi" + File.separator
				+ "Disease prevalence.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_disease_prevalence.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new PrevalencesCategoricalFactory()
					.manufacture(configurationFile);
			assertNotNull(result);
			StAXAgnosticWriter.produceFile((FileControlSingleton.getInstance())
					.get("diseaseprevalences"),
					(HashMap<Integer, Object>) result, outputFile);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ConfigurationException e) {
			assertNotNull(e); // Force error.
		}
	}

	@Test
	public void testCorrectedPrevalence() {
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Disease prevalence_corrected.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_disease_prevalence_corrected.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new PrevalencesCategoricalFactory()
					.manufacture(configurationFile);
			assertNotNull(result);
			try {
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("diseaseprevalences"),
						(HashMap<Integer, Object>) result, outputFile);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) { // TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void testDiseaseIncidence() {
		String configurationFilePath = "data" + File.separator + "Mehdi"
				+ File.separator + "Disease incidence.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_disease_incidence.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new IncidencesFactory()
					.manufacture(configurationFile);
			// manufacture(configurationFile, false);
			assertNotNull(result);
			try {
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("incidences"),
						(HashMap<Integer, Object>) result, outputFile);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void testRelRiskForRiskFactorContinuous() {
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
			RelRiskForRiskFactorContinuousFactory theFactory = new RelRiskForRiskFactorContinuousFactory();
			Object defaultResult = theFactory.manufactureDefault();
			try {
				FileControlSingleton instance = FileControlSingleton
						.getInstance();
				FileControlEnum myEnum = instance.get("rrisksforriskfactor");
				StAXAgnosticWriter.produceFile(myEnum,
						(HashMap<Integer, Object>) defaultResult,
						configurationFile);
				// Read defaults file and write it out again.
				Object result = theFactory.manufacture(configurationFile);
				assertNotNull(result);
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("rrisksforriskfactor"),
						(HashMap<Integer, Object>) result, outputFile);
				// assertEquals( 0, outputFile.compareTo(configurationFile));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}

	@Test
	public void testRelRiskFromOtherDisease() {
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "relriskfromdisease_default.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "relriskfromdisease_read_written.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			// Create XML-file with defaults and write it to disk.
			RelRiskFromOtherDiseaseFactory theFactory = new RelRiskFromOtherDiseaseFactory();
			Object defaultResult = theFactory.manufactureDefault();
			try {
				FileControlSingleton instance = FileControlSingleton
						.getInstance();
				FileControlEnum myEnum = instance.get("rrisksfromdisease");
				StAXAgnosticWriter.produceFile(myEnum,
						(HashMap<Integer, Object>) defaultResult,
						configurationFile);
				// Read defaults file and write it out again.
				Object result = theFactory.manufacture(configurationFile);
				assertNotNull(result);
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("rrisksfromdisease"),
						(HashMap<Integer, Object>) result, outputFile);
				// assertEquals( 0, outputFile.compareTo(configurationFile));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}

	@Test
	public void testDALYWeights() {
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "dalyweights_default.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "dalyweights_read_written.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			// Create XML-file with defaults and write it to disk.
			DALYWeightsFactory theFactory = new DALYWeightsFactory();
			Object defaultResult = theFactory.manufactureDefault();
			try {
				FileControlSingleton instance = FileControlSingleton
						.getInstance();
				FileControlEnum myEnum = instance.get("dalyweights");
				StAXAgnosticWriter.produceFile(myEnum,
						(HashMap<Integer, Object>) defaultResult,
						configurationFile);
				// Read defaults file and write it out again.
				Object result = theFactory.manufacture(configurationFile);
				assertNotNull(result);
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("dalyweights"),
						(HashMap<Integer, Object>) result, outputFile);
				// assertEquals( 0, outputFile.compareTo(configurationFile));
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestAgnosticFactoryPlusChildren.class);
	}
}
