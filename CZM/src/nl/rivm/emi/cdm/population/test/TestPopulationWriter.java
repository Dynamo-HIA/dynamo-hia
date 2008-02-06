package nl.rivm.emi.cdm.population.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationFactory;
import nl.rivm.emi.cdm.population.PopulationWriter;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;
import nl.rivm.emi.cdm.simulation.Simulation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class TestPopulationWriter {
	Log log = LogFactory.getLog(getClass().getName());

	File testFileOK = new File(
			"C:/eclipse321/workspace/CZM/data/populationtestOK.xml");

	File testWriterOutput = new File(
	"C:/eclipse321/workspace/CZM/data/populationWriterOutput.xml");

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void rewriteFile() {
		log.info("<<<<<<<<<<<<Starting test>>>>>>>>>>");
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document document = docBuilder.parse(testFileOK);
			Node rootNode = document.getFirstChild();
			Simulation simulation = new Simulation("Label", 1);
			assertNotNull(simulation);
			PopulationFactory factory = new PopulationFactory("pop");
			boolean success = factory.makeIt(rootNode, simulation);
			assertTrue(success);
			PopulationWriter.writeToXMLFile(simulation.getPopulation(), 0, testWriterOutput);
		} catch (CZMConfigurationException e) {
			// Is not an error perse.
			log.warn(e.getMessage());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			// Make junit fail. TODO Make something nicer.
			assertNull(e);
		} catch (SAXException e) {
			e.printStackTrace();
			assertNull(e);
		} catch (IOException e) {
			e.printStackTrace();
			assertNull(e);
		} catch (Exception e) {
			e.printStackTrace();
			assertNull(e);
		}
	}

	private void checkResult(Population population) {
		log.debug("Checking Object Tree");
		try {
			if (population != null) {
				log.debug("Population present, contains " + population.size()
						+ " individual(s).");
				for (int count = 0; count < population.size(); count++) {
					Individual indy = population.nextIndividual();
					log.debug("Individual contains " + indy.size()
							+ " CharacteristicValue(s).");
					for (int charCount = 0; charCount < indy.size(); charCount++) {
						log.debug("CharacteristicValue at index " + charCount
								+ " has value "
								+ indy.get(charCount).getValue());
					}
				}
			}
		} catch (Exception e) {
			log.warn("Something blew up in checkResult. Exc: "
					+ e.getClass().getName() + " message: " + e.getMessage());
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.characteristic.test.TestCharacteristicValueFactory.class);
	}

}
