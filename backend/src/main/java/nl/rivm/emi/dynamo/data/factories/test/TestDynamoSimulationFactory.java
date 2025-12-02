package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.DynamoSimulationFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.DispatchMap;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticGroupWriter;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDynamoSimulationFactory {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

//	@Test
	@SuppressWarnings("unchecked")
	public void testDynamoSimulationDefault() {
		try {
			String outputFilePath = "data" + File.separator + "development"
					+ File.separator + "dynamosimulation_default_normal.xml";
			File outputFile = new File(outputFilePath);
			DispatchMap rootLevelDispatchMap = DispatchMap.getInstance();
			assertNotNull(rootLevelDispatchMap);
			DynamoSimulationFactory theFactory = (DynamoSimulationFactory) rootLevelDispatchMap
					.get(RootElementNamesEnum.SIMULATION.getNodeLabel())
					.getTheFactory();
			assertNotNull(theFactory);
			Object result = theFactory.manufactureDefault();
			assertNotNull(result);
			StAXAgnosticGroupWriter.produceFile(RootElementNamesEnum.SIMULATION
					.getNodeLabel(), (HashMap<String, Object>) result,
					outputFile);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (UnexpectedFileStructureException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (IOException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (DynamoConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (Exception e) {
			log.fatal(e.getClass().getName() + " cause: " + e.getCause());
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDynamoSimulationConstruction() {
		try {
			String configurationFilePath = "data" + File.separator
					+ "development" + File.separator
					+ "testset20090330_simulation1_configuration.xml";
			File configurationFile = new File(configurationFilePath);
			String outputFilePath = "data" + File.separator + "development"
					+ File.separator + "dynamosimulation_default_normal.xml";
			File outputFile = new File(outputFilePath);
			DispatchMap rootLevelDispatchMap = DispatchMap.getInstance();
			assertNotNull(rootLevelDispatchMap);
			DynamoSimulationFactory theFactory = (DynamoSimulationFactory) rootLevelDispatchMap
					.get(RootElementNamesEnum.SIMULATION.getNodeLabel())
					.getTheFactory();
			assertNotNull(theFactory);
			Object result = theFactory.manufacture(configurationFile, RootElementNamesEnum.SIMULATION.getNodeLabel());
			assertNotNull(result);
			StAXAgnosticGroupWriter.produceFile(RootElementNamesEnum.SIMULATION
					.getNodeLabel(), (HashMap<String, Object>) result,
					outputFile);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (UnexpectedFileStructureException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (IOException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (DynamoConfigurationException e) {
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (Exception e) {
			log.fatal(e.getClass().getName() + " cause: " + e.getCause());
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestDynamoSimulationFactory.class);
	}
}
