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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPopulationCsvReader {

	Log log = LogFactory.getLog(getClass().getName());

	String projectBaseDir = System.getProperty("user.dir");

	String testDataFileBasePath = projectBaseDir + File.separator
			+ "unittestdata" + File.separator + "populationcsvreader"
			+ File.separator;
	private CharacteristicsConfigurationMapSingleton single = null;

	@Before
	public void setup() {
		String multipleCharacteristicsFileName = testDataFileBasePath
				+ "characteristics_1.xml";
		try {
			File multipleCharacteristicsFile = new File(
					multipleCharacteristicsFileName);
			@SuppressWarnings("unused")
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			single = CharacteristicsConfigurationMapSingleton.getInstance();
			assertTrue(single.size() > 1);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e);
		}
	}

	@After
	public void teardown() {
		single = null;
	}

	@Test
	public void createAndTestOnNonExistentFile() {
		PopulationCsvReader reader;
		reader = new PopulationCsvReader(testDataFileBasePath
				+ "nonexistentpopulation.csv");
		boolean result = reader.checkFileAndHeadersAgainstCharacteristics();
		assertFalse(result);
	}

	@Test
	public void createAndTestOnEmptyFile() {
		PopulationCsvReader reader;
		reader = new PopulationCsvReader(testDataFileBasePath
				+ "emptypopulationfile.csv");
		boolean result = reader.checkFileAndHeadersAgainstCharacteristics();
		assertFalse(result);
	}

	@Test
	public void createAndTestOnFileWithIncorrectHeadersOnly() {
		PopulationCsvReader reader;
		reader = new PopulationCsvReader(testDataFileBasePath
				+ "populationfileincorrectheaders.csv");
		boolean result = reader.checkFileAndHeadersAgainstCharacteristics();
		assertFalse(result);
	}

	@Test
	public void createAndTestOnFileWithCorrectHeadersOnly() {
		PopulationCsvReader reader;
		reader = new PopulationCsvReader(testDataFileBasePath
				+ "populationfilecorrectheaders.csv");
		boolean result = reader.checkFileAndHeadersAgainstCharacteristics();
		assertTrue(result);
		Population population = reader.readPopulation(30);
		// Empty configuration should not produce a population.
		assertNull(population);
	}

	@Test
	public void createAndTestOnFileWithCorrectHeadersAndOneIndividual() {
		PopulationCsvReader reader;
		reader = new PopulationCsvReader(testDataFileBasePath
				+ "populationfilecorrectheadersoneind.csv");
		boolean result = reader.checkFileAndHeadersAgainstCharacteristics();
		assertTrue(result);
		Population population = reader.readPopulation(30);
		// Empty configuration should not produce a population.
		assertNotNull(population);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.population.file.csv.test.TestPopulationCsvReader.class);
	}
}
