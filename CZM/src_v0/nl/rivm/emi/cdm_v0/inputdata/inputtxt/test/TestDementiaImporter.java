package nl.rivm.emi.cdm_v0.inputdata.inputtxt.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm_v0.inputdata.cbs.MortalityCSVImporter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class TestDementiaImporter {
	Log logger = LogFactory.getLog(getClass().getName());

	@Test
	public void parseAbsentMortalityFile() {
		// Import a file that isn't there.
		try {
			MortalityCSVImporter.importFile("");
		} catch (IOException e) {
			logger.info("Exception caught.");
			assertNotNull(e);
		}
	}

	@Test
	public void parsePresentMortalityFile() {
		try {
			MortalityCSVImporter.importFile("C:\\eclipse321\\workspace\\CZM\\data\\CBS_download_sterftequotienten-leeftijd-geslacht.csv");
		} catch (IOException e) {
		assertFalse(true);	// Should not come here.
		}
	}

	@Test
	public void parsePresentMortalityFile2TweakedMantissas() {
		try {
			MortalityCSVImporter.importFile2IntegerChances("C:\\eclipse321\\workspace\\CZM\\data\\CBS_download_sterftequotienten-leeftijd-geslacht.csv");
		} catch (IOException e) {
		assertFalse(true);	// Should not come here.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm_v0.inputdata.cbs.test.TestMortalityCSVImporter.class);
	}

}
