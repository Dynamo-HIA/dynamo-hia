package nl.rivm.emi.dynamo.data.objects.layers.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Test_RecursiveXMLHandlingLayer {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	// @Test
	public void leafNodeListTest1() {
		XMLConfiguration configurationFromFile;
		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator + "leafnodelisttest1.xml";
			configurationFromFile = new XMLConfiguration(configurationFilePath);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();
			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				LeafNodeList leafNodeList = new LeafNodeList();
				int theLastContainer = leafNodeList.fill(rootChild);
			}
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			assertNull(e);
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage());
			exception.printStackTrace();
			assertNull(exception);
		}
	}

	// @Test
	public void leafNodeListTest2() {
		XMLConfiguration configurationFromFile;
		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator + "leafnodelisttest2.xml";
			configurationFromFile = new XMLConfiguration(configurationFilePath);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();
			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				LeafNodeList leafNodeList = new LeafNodeList();
				int theLastContainer = leafNodeList.fill(rootChild);
			}
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			assertNull(e);
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage());
			exception.printStackTrace();
			assertNull(exception);
		}
	}

	// @Test
	public void relRisksForDeath_dev1() {
		XMLConfiguration configurationFromFile;
		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator
					+ "relrisksfordeath_dev1.xml";
			configurationFromFile = new XMLConfiguration(configurationFilePath);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();
			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				LeafNodeList leafNodeList = new LeafNodeList();
				int theLastContainer = leafNodeList.fill(rootChild);
			}
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			assertNull(e);
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage());
			exception.printStackTrace();
			assertNull(exception);
		}
	}

	// @Test
	public void relRisksForDeath_dev1_viaFactory() {
		try {
			XMLConfiguration configurationFromFile;
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator
					+ "relrisksfordeath_dev1.xml";
			configurationFromFile = new XMLConfiguration(configurationFilePath);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			String rootNodeName = configurationFromFile.getRootElementName();
			AgnosticFactory factory = FactoryProvider
					.getRelevantFactoryByRootNodeName(rootNodeName);
			TypedHashMap<Age> model = (TypedHashMap<Age>) factory
					.manufacture(new File(configurationFilePath));
			assertNotNull(model);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		} catch (DynamoInconsistentDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void relRisksForDeath_default_viaFactory() {
		String rootElementName = FileControlEnum.RELRISKFORDEATHCOMPOUND
				.getRootElementName();
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + rootElementName + "_default.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + rootElementName + "_read_written.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			// Create XML-file with defaults and write it to disk.
			AgnosticFactory theFactory = FactoryProvider
					.getRelevantFactoryByRootNodeName(rootElementName);
			Object defaultResult = theFactory.manufactureDefault();
			try {
				FileControlSingleton instance = FileControlSingleton
						.getInstance();
				FileControlEnum myEnum = instance.get(rootElementName);
				StAXAgnosticWriter.produceFile(myEnum,
						(HashMap<Integer, Object>) defaultResult,
						configurationFile);
				// Read defaults file and write it out again.
				Object result = theFactory.manufacture(configurationFile);
				assertNotNull(result);
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get(rootElementName),
						(HashMap<Integer, Object>) result, outputFile);
				// assertEquals( 0, outputFile.compareTo(configurationFile));
			} catch (XMLStreamException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (UnexpectedFileStructureException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (IOException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (DynamoInconsistentDataException e) {
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}

	@Test
	public void relRisksForDeath_dev1_observable_viaFactory() {
		// try {
		RunnableRelRiskForDeathCompound_Default_Manufacturing rund = new RunnableRelRiskForDeathCompound_Default_Manufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				rund);
	}

	@Test
	public void relRisksForDeath_compound_filled_viaFactory() {
		String rootElementName = FileControlEnum.RELRISKFORDEATHCOMPOUND
				.getRootElementName();
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + rootElementName + "_filled.xml";
		File configurationFile = new File(configurationFilePath);
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + rootElementName
				+ "_filled_modelled_written.xml";
		File outputFile = new File(outputFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			// Create XML-file with defaults and write it to disk.
			AgnosticFactory theFactory = FactoryProvider
					.getRelevantFactoryByRootNodeName(rootElementName);
			Object modelObject = theFactory.manufacture(configurationFile);
			FileControlSingleton instance = FileControlSingleton.getInstance();
			FileControlEnum myEnum = instance.get(rootElementName);
			StAXAgnosticWriter.produceFile(myEnum,
					(HashMap<Integer, Object>) modelObject, outputFile);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (UnexpectedFileStructureException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (IOException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
		// catch (DynamoInconsistentDataException e) {
		// e.printStackTrace();
		// assertNull(e); // Force error.
		// }
		catch (ConfigurationException e) {
			e.printStackTrace();
			assertNotNull(e); // Force error.
		} catch (DynamoInconsistentDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	@Test
	public void relRisksForDeath_compound_filled_observable_viaFactory() {
		RunnableRelRiskForDeathCompound_Filled_Manufacturing rund = new RunnableRelRiskForDeathCompound_Filled_Manufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				rund);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(Test_RecursiveXMLHandlingLayer.class);
	}
}
