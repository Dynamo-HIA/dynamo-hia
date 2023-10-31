package nl.rivm.emi.cdm.characteristic.test;

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

public class TestCharacteristicXMLConfiguration {
	Log log = LogFactory.getLog(getClass().getName());

	String nonExistentFileName = "xxx.xml";

	String existingFileName_WithoutCharacteristic = "unittestdata\\charconf_nocharacteristic.xml";

	String existingFileName_NoIndex = "unittestdata\\charconf_noindex.xml";

	String existingFileName_NoLabel = "unittestdata\\charconf_nolabel.xml";

	String existingFileName_NoType = "unittestdata\\charconf_notype.xml";

	String existingFileName_NoPossibleValues = "unittestdata\\charconf_nopossiblevalues.xml";

	String existingFileName_NoValues = "unittestdata\\charconf_novalues.xml";

	String existingFileName_SingleChar = "unittestdata\\charconf_onechar.xml";

	String singleChar_Report_FileName = "unittestdata\\charconf_singlechar.txt";

	String existingFileName_MultiChar = "unittestdata\\charconf_multichar.xml";

	String multiChar_Report_FileName = "unittestdata\\charconf_multichar.txt";

	@Test
	public void parseAbsentConfigurationFile() {
		try {
			String currentWorkingDirectory = System.getProperty("user.dir");
			System.out.println(currentWorkingDirectory);
			File nonExistentFile = new File(nonExistentFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					nonExistentFile);
			assertNotNull(null); // An exception should be thrown.
		} catch (ConfigurationException e) {
			assertEquals(CDMConfigurationException.noFileMessage, e
					.getMessage());
			log.debug(e.getMessage() + " " + nonExistentFileName);
		}
	}

	@Test
	public void parseConfigurationFileWithoutCharacteristic() {
		String currentWorkingDirectory = System.getProperty("user.dir");

		String withoutCharacteristicFileName = currentWorkingDirectory + "\\"
				+ existingFileName_WithoutCharacteristic;
		System.out.println(withoutCharacteristicFileName);
		try {
			File withoutCharacteristicFile = new File(
					withoutCharacteristicFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					withoutCharacteristicFile);
			assertNotNull(null); // An exception should be thrown.
		} catch (ConfigurationException e) {
			assertEquals(CDMConfigurationException.noCharacteristicMessage, e
					.getMessage());
			log.debug(e.getMessage() + withoutCharacteristicFileName);
		}
	}

	@Test
	public void parseConfigurationFileWithoutIndex() {
		String currentWorkingDirectory = System.getProperty("user.dir");

		String withoutCharacteristicFileName = currentWorkingDirectory + "\\"
				+ existingFileName_NoIndex;
		System.out.println(withoutCharacteristicFileName);
		try {
			File withoutCharacteristicFile = new File(
					withoutCharacteristicFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					withoutCharacteristicFile);
			assertNotNull(null); // An exception should be thrown.
		} catch (ConfigurationException e) {
			assertEquals(
					CDMConfigurationException.noCharacteristicIndexMessage, e
							.getMessage());
			log.debug(e.getMessage() + withoutCharacteristicFileName);
		}
	}

	@Test
	public void parseConfigurationFileWithoutLabel() {
		String currentWorkingDirectory = System.getProperty("user.dir");
		String withoutLabelFileName = currentWorkingDirectory + "\\"
				+ existingFileName_NoLabel;
		System.out.println(withoutLabelFileName);
		try {
			File withoutLabelFile = new File(withoutLabelFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					withoutLabelFile);
			assertNotNull(null); // An exception should be thrown.
		} catch (ConfigurationException e) {
			assertEquals(
					CDMConfigurationException.noCharacteristicLabelMessage, e
							.getMessage());
			log.debug(e.getMessage() + withoutLabelFileName);
		}
	}

	@Test
	public void parseConfigurationFileWithoutType() {
		String currentWorkingDirectory = System.getProperty("user.dir");
		String withoutTypeFileName = currentWorkingDirectory + "\\"
				+ existingFileName_NoType;
		System.out.println(withoutTypeFileName);
		try {
			File withoutLabelFile = new File(withoutTypeFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					withoutLabelFile);
			assertNotNull(null); // An exception should be thrown.
		} catch (ConfigurationException e) {
			assertEquals(CDMConfigurationException.noCharacteristicTypeMessage,
					e.getMessage());
			log.debug(e.getMessage() + withoutTypeFileName);
		}
	}

	@Test
	public void parseConfigurationFileWithoutPossibleValues() {
		String currentWorkingDirectory = System.getProperty("user.dir");
		String withoutPossibleValuesFileName = currentWorkingDirectory + "\\"
				+ existingFileName_NoPossibleValues;
		System.out.println(withoutPossibleValuesFileName);
		try {
			File withoutLabelFile = new File(withoutPossibleValuesFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					withoutLabelFile);
			assertNotNull(null); // An exception should be thrown.
		} catch (ConfigurationException e) {
			assertEquals(
					CDMConfigurationException.noCharacteristicPossibleValuesMessage,
					e.getMessage());
			log.debug(e.getMessage() + withoutPossibleValuesFileName);
		}
	}

	@Test
	public void parseConfigurationFileWithoutValues() {
		String currentWorkingDirectory = System.getProperty("user.dir");
		String withoutValueFileName = currentWorkingDirectory + "\\"
				+ existingFileName_NoValues;
		System.out.println(withoutValueFileName);
		try {
			File withoutLabelFile = new File(withoutValueFileName);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					withoutLabelFile);
			assertNotNull(null); // An exception should be thrown.
		} catch (ConfigurationException e) {
			assertEquals(
					CDMConfigurationException.noCharacteristicPossibleValueValueMessage,
					e.getMessage());
			log.debug(e.getMessage() + withoutValueFileName);
		}
	}

//	@Test
//	public void parseConfigurationFileSingleCharacteristic() {
//		String currentWorkingDirectory = System.getProperty("user.dir");
//		String singleCharacteristicFileName = currentWorkingDirectory + "\\"
//				+ existingFileName_SingleChar;
//		System.out.println(singleCharacteristicFileName);
//		try {
//			File singleCharacteristicFile = new File(
//					singleCharacteristicFileName);
//			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
//					singleCharacteristicFile);
//			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
//					.getInstance();
//			assertTrue(CharacteristicsConfigurationMapSingleton.getInstance()
//					.size() == 1);
//			dumpSingleton(single, singleChar_Report_FileName);
//		} catch (ConfigurationException e) {
//			assertNull(e);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

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