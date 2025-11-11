package nl.rivm.emi.cdm.iterations.two.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class Test01_03 {
	Log log = LogFactory.getLog(getClass().getName());

	String existingFileName_MultiChar = "unittestdata/iteration2/test/charconf01_03.xml";

	String multiChar_Report_FileName = "unittestdata/iteration2/test/charconf01_03.txt";

	@Test
	public void parseConfigurationFileMultipleCharacteristics() {
		String currentWorkingDirectory = System.getProperty("user.dir");
		String multipleCharacteristicsFileName = currentWorkingDirectory + "\\"
				+ existingFileName_MultiChar;
		System.out.println(multipleCharacteristicsFileName);
		try {
			File multipleCharacteristicsFile = new File(
					
					multipleCharacteristicsFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();
			assertTrue(single.size() > 1);
			dumpSingleton(single, multiChar_Report_FileName);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNull(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void dumpSingleton(CharacteristicsConfigurationMapSingleton single,
			String fileName) throws IOException {
		File reportFile = new File(fileName);
		FileWriter writer = new FileWriter(reportFile);
		String report = single.humanReadableReport();
		writer.append(report.subSequence(0, report.length()));
		writer.flush();
		writer.close();
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.characteristic.test.TestCharacteristicXMLConfiguration.class);
	}
}