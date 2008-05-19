package nl.rivm.emi.cdm.population.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.stax.StAXEntryPoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPopulationThroughSTaxFactory {
	Log log = LogFactory.getLog(getClass().getName());

	File testFileNOK = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/xmlstructures/populationtestNOK.xml");

	File testFileNOK2 = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/xmlstructures/populationtestNOK2.xml");

	File testFileOK = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/xmlstructures/populationtestOK.xml");

	File testFileOK2 = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/xmlstructures/populationtestOK2.xml");

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void list_ThatIsNOK() {
		log.info("<<<<<<<<<<<<Starting test NOK>>>>>>>>>>");
		Population population;
		try {
			population = (Population) StAXEntryPoint
					.processFile(testFileNOK);
			assertNull(population);
		} catch (CDMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void list_ThatIsNOK2() {
		log.info("<<<<<<<<<<<<Starting test NOK2>>>>>>>>>>");
		Population population;
		try {
			population = (Population) StAXEntryPoint
					.processFile(testFileNOK2);
			assertNull(population);
		} catch (CDMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void list_ThatIsOK() {
		log.info("<<<<<<<<<<<<Starting test OK>>>>>>>>>>");
		Population population;
		try {
			population = (Population) StAXEntryPoint
					.processFile(testFileOK);
			assertNotNull(population);
		} catch (CDMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@Test
	public void list_ThatIsOK2() {
		log.info("<<<<<<<<<<<<Starting test OK2>>>>>>>>>>");
		Population population;
		try {
			population = (Population) StAXEntryPoint
					.processFile(testFileOK2);
			assertNotNull(population);
		} catch (CDMConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
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
						log
								.debug("CharacteristicValue at index "
										+ charCount
										+ " has value "
										+ ((IntCharacteristicValue) indy
												.get(charCount)).getValue());
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
				nl.rivm.emi.cdm.population.test.TestPopulationThroughSTaxFactory.class);
	}

}
