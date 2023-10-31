package nl.rivm.emi.cdm.population.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.PopulationFromDomFactory;
import nl.rivm.emi.cdm.simulation.Simulation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class TestPopulationFromDOMFactory {
	Log log = LogFactory.getLog(getClass().getName());

	File testFileNOK = new File(
			"C:/eclipse321/workspace/CZM/data/populationtestNOK.xml");

	File testFileNOK2 = new File(
			"C:/eclipse321/workspace/CZM/data/populationtestNOK2.xml");

	File testFileOK = new File(
			"C:/eclipse321/workspace/CZM/data/populationtestOK.xml");

	File testFileOK2 = new File(
			"C:/eclipse321/workspace/CZM/data/populationtestOK2.xml");

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void list_ThatIsNOK() {
log.info("<<<<<<<<<<<<Starting test>>>>>>>>>>");
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder docBuilder = (DocumentBuilder) dbfac
					.newDocumentBuilder();
			Document document = docBuilder.parse(testFileNOK);
			Node rootNode = document.getFirstChild();
			Simulation simulation = new Simulation("Label", 1);
			assertNotNull(simulation);
			PopulationFromDomFactory factory = new PopulationFromDomFactory("pop");
			Population population = factory.makeItFromDOM(rootNode, 1);
			assertNull(population);
		} catch (CDMConfigurationException e) {
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

	@Test
	public void list_ThatIsNOK2() {
		log.info("<<<<<<<<<<<<Starting test>>>>>>>>>>");
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = (DocumentBuilder) dbfac
					.newDocumentBuilder();
			Document document = docBuilder.parse(testFileNOK2);
			Node rootNode = document.getFirstChild();
			Simulation simulation = new Simulation("Label", 1);
			assertNotNull(simulation);
			PopulationFromDomFactory factory = new PopulationFromDomFactory("pop");
			Population population = factory.makeItFromDOM(rootNode, 1);
			assertNull(population);
		} catch (CDMConfigurationException e) {
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

	@Test
	public void list_ThatIsOK() {
		log.info("<<<<<<<<<<<<Starting test>>>>>>>>>>");
		// DocumentBuilderFactory dbfac =
		// DocumentBuilderFactoryImpl.newInstance();
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			// dbfac.setFeature("http://apache.org/xml/features/dom/defer-node-expansion",
			// false);
			// DocumentBuilderImpl docBuilder =
			// (DocumentBuilderImpl)dbfac.newDocumentBuilder();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document document = docBuilder.parse(testFileOK);
			Node rootNode = document.getFirstChild();
			Simulation simulation = new Simulation("Label", 1);
			assertNotNull(simulation);
			PopulationFromDomFactory factory = new PopulationFromDomFactory("pop");
			Population population = factory.makeItFromDOM(rootNode, 1);
			assertNotNull(population);
		} catch (CDMConfigurationException e) {
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

	@Test
	public void list_ThatIsOK2() {
		log.info("<<<<<<<<<<<<Starting test>>>>>>>>>>");
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = (DocumentBuilder) dbfac
					.newDocumentBuilder();
			Document document = docBuilder.parse(testFileOK2);
			Node rootNode = document.getFirstChild();
			Simulation simulation = new Simulation("Label", 1);
			assertNotNull(simulation);
			PopulationFromDomFactory factory = new PopulationFromDomFactory("pop");
			Population population = factory.makeItFromDOM(rootNode, 1);
			assertNotNull(population);
		} catch (CDMConfigurationException e) {
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
								+ ((IntCharacteristicValue)indy.get(charCount)).getValue());
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
