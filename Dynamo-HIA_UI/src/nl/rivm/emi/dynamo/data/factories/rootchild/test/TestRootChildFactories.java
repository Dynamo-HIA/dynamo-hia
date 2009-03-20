package nl.rivm.emi.dynamo.data.factories.rootchild.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.RootChildDispatchMap;
import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticSingleRootChildFactory;
import nl.rivm.emi.dynamo.data.types.atomic.HasNewborns;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticGroupWriter;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.exception.NestableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRootChildFactories {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	// @Test
	public void testHasNewBornsDefault() {
		try {
			AgnosticSingleRootChildFactory factory = (AgnosticSingleRootChildFactory) RootChildDispatchMap
					.getInstance().get("hasnewborns").getTheFactory();
			assertNotNull(factory);
			AtomicTypeBase<Boolean> type = new HasNewborns();
			assertNotNull(type);
			AtomicTypeObjectTuple tuple = factory.manufactureDefault(type);
			assertNotNull(tuple);
			assertEquals(type, tuple.getType());
			assertEquals(type.getDefaultValue(), tuple.getValue());
		} catch (DynamoInconsistentDataException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	// @Test
	public void testHasNewBornsWithPrePproduction() {
		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator + "hasnewbornsonly.xml";
			File configurationFile = new File(configurationFilePath);
			AgnosticSingleRootChildFactory factory = (AgnosticSingleRootChildFactory) RootChildDispatchMap
					.getInstance().get("hasnewborns").getTheFactory();
			assertNotNull(factory);
			XMLConfiguration configurationFromFile = new XMLConfiguration(
					configurationFile);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();
			assertEquals(1, rootChildren.size());
			ConfigurationNode onlyChild = rootChildren.get(0);
			Object result = factory.manufacture(onlyChild);
			assertNotNull(result);
		} catch (DynamoInconsistentDataException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (NestableException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	// @Test
	public void testHasNewBornsWithAgnosticGroupFactory() {
//		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator + "hasnewbornsonly.xml";
			File configurationFile = new File(configurationFilePath);
//			AgnosticGroupFactory factory = new AgnosticGroupFactory();
//			assertNotNull(factory);
//			Object result = factory.manufacture(configurationFile,
//					RootElementNamesEnum.SIMULATION.getNodeLabel());
//			assertNotNull(result);
//		} catch (DynamoInconsistentDataException e) {
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		} catch (NestableException e) {
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		}
	}

//	@Test
	public void testGrowingRiskFactorCompoundConfiguration() {
//		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator
					+ "riskfactor_compoundconfig_test.xml";
			String outputFilePath = "data" + File.separator + "development"
					+ File.separator + "riskfactor_compoundconfig_test_rw.xml";
			File configurationFile = new File(configurationFilePath);
			File outputFile = new File(outputFilePath);
//			AgnosticGroupFactory factory = new AgnosticGroupFactory();
//			assertNotNull(factory);
//			HashMap<String, Object> result = factory.manufacture(
//					configurationFile, RootElementNamesEnum.RISKFACTOR_COMPOUND
//							.getNodeLabel());
//			assertNotNull(result);
//			StAXAgnosticGroupWriter.produceFile(
//					RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(),
//					result, outputFile);
//		} catch (DynamoInconsistentDataException e) {
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		} catch (NestableException e) {
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		} catch (XMLStreamException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		} catch (UnexpectedFileStructureException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			assertNull(e); // Force error.
//		}
	}

	@Test
	public void testDefaultRiskFactorCompoundConfiguration() {
		try {
			String outputFilePath = "data" + File.separator + "development"
					+ File.separator
					+ "riskfactor_compoundconfig_default_w.xml";
			File outputFile = new File(outputFilePath);
			RiskFactorCompoundFactory factory = new RiskFactorCompoundFactory();
			assertNotNull(factory);
			HashMap<String, Object> result = factory.manufactureDefault();
			assertNotNull(result);
			StAXAgnosticGroupWriter.produceFile(
					RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(),
					result, outputFile);
		} catch (NestableException e) {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestRootChildFactories.class);
	}
}
