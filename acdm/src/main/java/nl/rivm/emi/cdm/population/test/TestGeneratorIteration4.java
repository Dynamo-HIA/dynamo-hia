package nl.rivm.emi.cdm.population.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.population.Generator;
import nl.rivm.emi.cdm.population.GeneratorFromXMLFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGeneratorIteration4 {
	Log log = LogFactory.getLog(getClass().getSimpleName());

	String projectBaseDir = System.getProperty("user.dir");

	String characteristicsConfigFileName = projectBaseDir + File.separator
			+ "unittestdata" + File.separator + "iteration4" + File.separator
			+ "characteristics_1.xml";

	String generatorConfigFileName = projectBaseDir + File.separator
			+ "unittestdata" + File.separator + "iteration4" + File.separator
			+ "generatorconfig_1.xml";

	@Before
	synchronized public void setup() throws ConfigurationException {
		log.info("Characteristics configuration filename: "
				+ characteristicsConfigFileName);
		File multipleCharacteristicsFile = new File(
				characteristicsConfigFileName);
		@SuppressWarnings("unused")
		CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
				multipleCharacteristicsFile);
	}

	@After
	public void teardown() {
	}

	@Test
	public void genMultiChar() {
		XMLConfiguration configuration;
		try {
			String multipleCharacteristicsFileName = generatorConfigFileName;
			log.info(multipleCharacteristicsFileName);
			File multipleCharacteristicsFile = new File(
					multipleCharacteristicsFileName);
			configuration = new XMLConfiguration(multipleCharacteristicsFile);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			log
					.fatal("Generating 10k population with MersenneTwisterRNG and multiple characteristics");
			generator.generateNewborns();
			log.fatal("Population ready.");
		} catch (CDMConfigurationException e) {
			log.fatal("Exception caught: " + e.getClass().getSimpleName()
					+ " with message: " + e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			log.fatal("Exception caught: " + e1.getClass().getSimpleName()
					+ " with message: " + e1.getMessage());
			assertNull(e1); // Force error.
		} catch (Exception e) {
			log.fatal("Exception caught: " + e.getClass().getSimpleName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGeneratorIteration4.class);
	}

}
