package nl.rivm.emi.cdm.population.file.csv.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.file.csv.PopulationCsvReader;
import nl.rivm.emi.cdm.population.file.csv.PopulationCsvWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPopulationCsvWriter {

	Log log = LogFactory.getLog(getClass().getName());

	String projectBaseDir = System.getProperty("user.dir");

	String testDataFileBasePath = projectBaseDir + File.separator
			+ "unittestdata" + File.separator + "populationcsvreader"
			+ File.separator;

	private CharacteristicsConfigurationMapSingleton single = null;

	private Population population;

	@Before
	public void setup() {
		String multipleCharacteristicsFileName = testDataFileBasePath
				+ "characteristics_1.xml";
		try {
			File multipleCharacteristicsFile = new File(
					multipleCharacteristicsFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			single = CharacteristicsConfigurationMapSingleton.getInstance();
			assertTrue(single.size() > 1);
			PopulationCsvReader reader;
			reader = new PopulationCsvReader(testDataFileBasePath
					+ "populationfilecorrectheadersoneind.csv");
			boolean result = reader.checkFileAndHeadersAgainstCharacteristics();
			assertTrue(result);
			population = reader.readPopulation(30);
			assertNotNull(population);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e);
		}
	}

	@After
	public void teardown() {
		population = null;
	}

	@Test
	public void createAndTestOnNonExistentFile() {
	}

	@Test
	public void createAndTestOnFileWithCorrectHeadersAndOneIndividual() {
		PopulationCsvWriter writer = new PopulationCsvWriter(
				testDataFileBasePath
				+ "populationfilecorrectheadersoneind_out.csv");
		writer.writePopulation(population);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestPopulationCsvWriter.class);
	}
}
