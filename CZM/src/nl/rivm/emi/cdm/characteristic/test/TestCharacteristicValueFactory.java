package nl.rivm.emi.cdm.characteristic.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.JUnit4TestAdapter;

import nl.rivm.emi.cdm.characteristic.CharacteristicValueFactory;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;
import nl.rivm.emi.cdm.util.StringInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class TestCharacteristicValueFactory {
	Log logger = LogFactory.getLog(getClass().getName());

//	String testXML = "<?xml version=\"1.0\" standalone=\"yes\" ?>\n"
//			+ "<charval>blabla</charval>\n";
	// Not an Integer.
	File testFileNOK = new File(
	"C:/eclipse321/workspace/CZM/data/charvaltestNOK.xml");
	// No String.
	File testFileNOK2 = new File(
	"C:/eclipse321/workspace/CZM/data/charvaltestNOK2.xml");
	// Integer 42
	File testFileOK = new File(
	"C:/eclipse321/workspace/CZM/data/charvaltestOK.xml");


	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void list_ThatIsNOK() {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			// Doesn't work for some reason @#$%
//			InputStream theStream = (InputStream) new StringInputStream(testXML);
//			Document document = docBuilder.parse(theStream);
			Document document = docBuilder.parse(testFileNOK);
			Node rootNode = document.getFirstChild();
			Individual individual = new Individual("Pietje");
			assertNotNull(individual);
			CharacteristicValueFactory factory = new CharacteristicValueFactory(
					"charval");
			boolean success = factory.makeIt(rootNode, individual);
			assertFalse(success);
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
		} catch (CZMConfigurationException e) {
			e.printStackTrace();
			assertNull(e);
		} catch (Exception e){
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void list_ThatIsNOK2() {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			// Doesn't work for some reason @#$%
//			InputStream theStream = (InputStream) new StringInputStream(testXML);
//			Document document = docBuilder.parse(theStream);
			Document document = docBuilder.parse(testFileNOK2);
			Node rootNode = document.getFirstChild();
			Individual individual = new Individual("Pietje");
			assertNotNull(individual);
			CharacteristicValueFactory factory = new CharacteristicValueFactory(
					"charval");
			boolean success = factory.makeIt(rootNode, individual);
			assertFalse(success);
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
		} catch (CZMConfigurationException e) {
			e.printStackTrace();
			assertNull(e);
		} catch (Exception e){
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void list_ThatIsOK() {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			// Doesn't work for some reason @#$%
//			InputStream theStream = (InputStream) new StringInputStream(testXML);
//			Document document = docBuilder.parse(theStream);
			Document document = docBuilder.parse(testFileOK);
			Node rootNode = document.getFirstChild();
			Individual individual = new Individual("Pietje");
			assertNotNull(individual);
			CharacteristicValueFactory factory = new CharacteristicValueFactory(
					"charval");
			boolean success = factory.makeIt(rootNode, individual);
			assertTrue(success);
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
		} catch (CZMConfigurationException e) {
			e.printStackTrace();
			assertNull(e);
		} catch (Exception e){
			e.printStackTrace();
			assertNull(e);
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.characteristic.test.TestCharacteristicValueFactory.class);
	}

}
