package nl.rivm.emi.cdm.population.test;

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
import nl.rivm.emi.cdm.population.PopulationFactory;
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

public class TestGenerator {
	Log log = LogFactory.getLog(getClass().getName());

	File testNoFile = new File("bzzzt.xml"); // Do not provide this file :-)

	File testFileNoLabel = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/generatornolabel.xml");

	File testFileNoPopulationSize = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/generatornopopulationsize.xml");

	File testFileNoRngClassName = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/generatornorngclassname.xml");

	File testFileNoRngSeed = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/generatornorngseed.xml");

	File testFileNoCharacteristics = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/generatornocharacteristics.xml");

	File testFileNoCharacteristicId = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/generatornocharacteristicid.xml");

	File testFileInvalidRNGClassName = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/generatorinvalidrngclassname.xml");

	File testFile_OK = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/generator_perfect.xml");

	File testFile_OK_huge = new File(
	"C:/eclipse321/workspace/CZM/unittestdata/generator_hugepop.xml");

	File testFile_OK_huge_mersenne = new File(
	"C:/eclipse321/workspace/CZM/unittestdata/generator_huge_mersenne.xml");

	String existingFileName_MultiChar = "C:/eclipse321/workspace/CZM/unittestdata/iteration2/charconf1.xml";

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
	public void noFile() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testNoFile);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNull(generator); // Force error.
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertEquals(CDMConfigurationException.noFileMessage, e1
					.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void noLabel() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFileNoLabel);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNull(generator);
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertEquals(CDMConfigurationException.noGeneratorLabelMessage, e1
					.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void noPopulationSize() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFileNoPopulationSize);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNull(generator);
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertEquals(
					CDMConfigurationException.noGeneratorPopulationSizeMessage,
					e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void noRngClassName() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFileNoRngClassName);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNull(generator);
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertEquals(
					CDMConfigurationException.noGeneratorRngClassNameMessage,
					e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void noRngSeed() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFileNoRngSeed);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNull(generator);
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertEquals(CDMConfigurationException.noGeneratorRngSeedMessage,
					e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void noCharacteristics() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFileNoCharacteristics);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNull(generator);
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertEquals(
					CDMConfigurationException.noGeneratorCharacteristicsMessage,
					e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void noCharacteristicId() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFileNoCharacteristicId);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNull(generator);
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertEquals(
					CDMConfigurationException.noGeneratorCharacteristicIdMessage,
					e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void invalidRNGClassName() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFileInvalidRNGClassName);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNull(generator.isValid());
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNotNull(e);
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void allOK() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFile_OK);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			generator.generateNewbornsWithAllZeroes();
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void genHuge() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFile_OK_huge);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			log.fatal("Generating 100k population.");
			generator.generateNewbornsWithAllZeroes();
			log.fatal("100k population ready.");
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void genHugeMErsenne() {
		XMLConfiguration configuration;
		try {
			configuration = new XMLConfiguration(testFile_OK_huge_mersenne);
			Generator generator = GeneratorFromXMLFactory
					.manufacture(configuration);
			assertNotNull(generator);
			assertNotNull(generator.isValid());
			log.fatal("Generating 100k population with MersenneTwisterRNG.");
			generator.generateNewbornsWithAllZeroes();
			log.fatal("100k population ready with MersenneTwisterRNG.");
		} catch (CDMConfigurationException e) {
			log.warn(e.getMessage());
			assertNull(e); // Force error.
		} catch (ConfigurationException e1) {
			assertNull(e1); // Force error.
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.population.test.TestGenerator.class);
	}

}
