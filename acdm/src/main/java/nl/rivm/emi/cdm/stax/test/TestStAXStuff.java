package nl.rivm.emi.cdm.stax.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.population.UnexpectedFileStructureException;
import nl.rivm.emi.cdm.stax.StAXEntryPoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStAXStuff {
	Log log = LogFactory.getLog(getClass().getName());
final String projectRoot = "D:/eclipse_workspaces/KISFromSVN/";
	File absentFile = new File(
			projectRoot + "CZM/unittestdata/thisfilemustnotexist.xml");

	File noRootElement = new File(
			projectRoot + "CZM/unittestdata/norootelement.xml");

	File nonPopRootElement = new File(
			projectRoot + "CZM/unittestdata/nonpoprootelementonly.xml");

	File popRootElementOnly = new File(
			projectRoot + "CZM/unittestdata/poprootelementonly.xml");

	File sepIndOK = new File(
			projectRoot + "CZM/unittestdata/separateindividual4stax.xml");

	File sepIntCharValOK = new File(
			projectRoot + "CZM/unittestdata/separateintcharvalue4stax.xml");

	File sepFloatCharValOK = new File(
			projectRoot + "CZM/unittestdata/separatefloatcharvalue4stax.xml");

	File testFileNOK = new File(
			projectRoot + "CZM/unittestdata/xmlstructures/populationtestNOK.xml");

	File testFileNOK2 = new File(
			projectRoot + "CZM/unittestdata/xmlstructures/populationtestNOK2.xml");

	File testFileOK = new File(
			projectRoot + "CZM/unittestdata/xmlstructures/populationtestOK.xml");

	File testFileOKOutput = new File(
	projectRoot + "CZM/unittestdata/xmlstructures/populationtestOK_output.xml");

	File testFileOKStAXOutput = new File(
	projectRoot + "CZM/unittestdata/xmlstructures/populationtestOK_StAX_output.xml");

	File recycledStAXOutput = new File(
	projectRoot + "CZM/unittestdata/xmlstructures/recycled_population_StAX_output.xml");

	File testFileOK2 = new File(
			projectRoot + "CZM/unittestdata/xmlstructures/populationtestOK2.xml");

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

//	@Test
	public void noFile() {
		try {
			log.info("<<<<<<<<<<<< Test without file. >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(absentFile);
			assertNotNull(result);
			assertTrue(result instanceof Exception);
			log.trace("", (Exception) result);
			log.debug("XMLEvent.ATTRIBUTE " + XMLEvent.ATTRIBUTE);
			log.debug("XMLEvent.CDATA " + XMLEvent.CDATA);
			log.debug("XMLEvent.CHARACTERS " + XMLEvent.CHARACTERS);
			log.debug("XMLEvent.COMMENT " + XMLEvent.COMMENT);
			log.debug("XMLEvent.DTD " + XMLEvent.DTD);
			log.debug("XMLEvent.END_DOCUMENT " + XMLEvent.END_DOCUMENT);
			log.debug("XMLEvent.END_ELEMENT " + XMLEvent.END_ELEMENT);
			log.debug("XMLEvent.ENTITY_DECLARATION "
					+ XMLEvent.ENTITY_DECLARATION);
			log.debug("XMLEvent.ENTITY_REFERENCE " + XMLEvent.ENTITY_REFERENCE);
			log.debug("XMLEvent.NAMESPACE " + XMLEvent.NAMESPACE);
			log.debug("XMLEvent.NOTATION_DECLARATION "
					+ XMLEvent.NOTATION_DECLARATION);
			log.debug("XMLEvent.PROCESSING_INSTRUCTION "
					+ XMLEvent.PROCESSING_INSTRUCTION);
			log.debug("XMLEvent.SPACE " + XMLEvent.SPACE);
			log.debug("XMLEvent.START_DOCUMENT " + XMLEvent.START_DOCUMENT);
			log.debug("XMLEvent.START_ELEMENT " + XMLEvent.START_ELEMENT);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void noRootElement() {
		try {
			log.info("<<<<<<<<<<<< Test file without rootelement. >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(noRootElement);
			assertNotNull(result);
			assertTrue(result instanceof Exception);
			log.trace("", (Exception) result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void nonPopRootElement() {
		try {
			log
					.info("<<<<<<<<<<<< Test file with non-pop rootelement. >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(nonPopRootElement);
			assertNotNull(result);
			assertTrue(result instanceof Exception);
			log.trace("", (Exception) result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void sepCharVal4Stax() {
		try {
			log
					.info("<<<<<<<<<<<< Starting test separate integer characteristicvalue toplevelreader >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(sepIntCharValOK);
			assertNotNull(result);
			assertTrue(result instanceof Exception);
			log.trace("", (Exception) result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void sepCharVal4Stax2() {
		try {
			log
					.info("<<<<<<<<<<<< Starting test separate float characteristicvalue toplevelreader >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(sepFloatCharValOK);
			assertNotNull(result);
			assertTrue(result instanceof Exception);
			log.trace("", (Exception) result);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void popRootElementOnly() {
		try {
			log
					.info("<<<<<<<<<<<< Test file with pop rootelement only. >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(popRootElementOnly);
			assertNotNull(result);
			assertTrue(result instanceof Population);
		} catch (FileNotFoundException e) {
			assertNull(e); // Force error.
		} catch (XMLStreamException e) {
			assertNull(e); // Force error.
		} catch (UnexpectedFileStructureException e) {
			assertNotNull(e); 
		}
	}

//	@Test
	public void testFileNOK() {
		try {
			log.info("<<<<<<<<<<<< Test testFileNOK. >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(testFileNOK);
			assertNotNull(result);
			assertTrue(result instanceof Exception);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void testFileNOK2() {
		try {
			log.info("<<<<<<<<<<<< Test testFileNOK2. >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(testFileNOK2);
			assertNotNull(result);
			assertTrue(result instanceof Exception);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testFileOK() {
		try {
			log.info("<<<<<<<<<<<< Test testFileOK. >>>>>>>>>>");
			Object result = StAXEntryPoint.processFile(testFileOK);
			assertNotNull(result);
			assertTrue(result instanceof Population);
			DOMPopulationWriter.writeToXMLFile((Population)result, 0, testFileOKOutput); 
			StAXEntryPoint.produceFile((Population)result, testFileOKStAXOutput); 
/* Recycle */
			result = StAXEntryPoint.processFile(testFileOKStAXOutput);
			assertNotNull(result);
			assertTrue(result instanceof Population);
			StAXEntryPoint.produceFile((Population)result, recycledStAXOutput); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

//	@Test
	public void testFileOK2() {
		try {
			log.info("<<<<<<<<<<<< Test testFileOK2. >>>>>>>>>>");
			Object result;
			result = StAXEntryPoint.processFile(testFileOK2);
			assertNotNull(result);
			assertTrue(result instanceof Population);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedFileStructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.stax.test.TestStAXStuff.class);
	}

}
