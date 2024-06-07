package nl.rivm.emi.cdm.iterations.two.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Generator;
import nl.rivm.emi.cdm.population.GeneratorFromXMLFactory;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationFromDomFactory;
import nl.rivm.emi.cdm.population.SeedLessGenerator;
import nl.rivm.emi.cdm.population.SeedLessGeneratorFromXMLFactory;
import nl.rivm.emi.cdm.simulation.Simulation;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalXMLConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import static org.junit.Assert.assertEquals;

public class TestSeedLessGenerator02_10 {
	Log log = LogFactory.getLog(getClass().getName());

	File testFile_100indi = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/seedlessgenerator_100.xml");
	
	File testFile_1kindi = new File(
	"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/seedlessgenerator_1k.xml");
	
	File testFile_10kindi = new File(
	"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/seedlessgenerator_10k.xml");

	File testFile_100kindi = new File(
	"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/seedlessgenerator_100k.xml");
	
	File testFile_1Mindi = new File(
	"C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/seedlessgenerator_1M.xml");

	String existingFileName_MultiChar = "C:/eclipse321/workspace/CZM/unittestdata/iteration2/test/charconf02_10.xml";

@Before
	synchronized public void setup() throws ConfigurationException {
		String multipleCharacteristicsFileName = existingFileName_MultiChar;
		System.out.println(multipleCharacteristicsFileName);
		File multipleCharacteristicsFile = new File(
				multipleCharacteristicsFileName);
		CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
				multipleCharacteristicsFile);
	}

	@After
	public void teardown() {
	}

	@Test
	public void oneHundred() {
		XMLConfiguration configuration;
		try {
			log.info("Entering oneHundred.");
			configuration = new XMLConfiguration(testFile_100indi);
			SeedLessGenerator generator = SeedLessGeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			generator.generateNewbornsWithAllOnes();
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
finally{
	log.info("Exiting oneHundred.");
}
}

	@Test
	public void oneThousand() {
		XMLConfiguration configuration;
		try {
			log.info("Entering oneThousand.");
			configuration = new XMLConfiguration(testFile_1kindi);
			SeedLessGenerator generator = SeedLessGeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			generator.generateNewbornsWithAllOnes();
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
		finally{
			log.info("Exiting oneThousand.");
		}
	}
	@Test
	public void tenThousand() {
		XMLConfiguration configuration;
		try {
			log.info("Entering tenThousand.");
			configuration = new XMLConfiguration(testFile_10kindi);
			SeedLessGenerator generator = SeedLessGeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			generator.generateNewbornsWithAllOnes();
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
		finally{
			log.info("Exiting tenThousand.");
		}
	}
	@Test
	public void oneHundredThousand() {
		XMLConfiguration configuration;
		try {
			log.info("Entering oneHundredThousand.");
			configuration = new XMLConfiguration(testFile_100kindi);
			SeedLessGenerator generator = SeedLessGeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			generator.generateNewbornsWithAllOnes();
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
		finally{
			log.info("Exiting oneHundredThousand.");
		}
	}
	@Test
	public void oneMillion() {
		XMLConfiguration configuration;
		try {
			log.info("Entering oneMillion.");
			configuration = new XMLConfiguration(testFile_1Mindi);
			SeedLessGenerator generator = SeedLessGeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			generator.generateNewbornsWithAllOnes();
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
		finally{
			log.info("Exiting oneMillion.");
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.iterations.two.test.TestSeedLessGenerator02_10.class);
	}

}
