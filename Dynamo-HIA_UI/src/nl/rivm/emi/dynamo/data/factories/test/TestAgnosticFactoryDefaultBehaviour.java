package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.writers.StAXAgnosticWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAgnosticFactoryDefaultBehaviour {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	/**
	 * This test must not finish normally because of unexpected "male"and
	 * "female" tags instead of "sex".
	 */
	@Test
	public void testManufactureDefaultObject() {
		String outputFilePath = "data" + File.separator + "development"
				+ File.separator + "stax_disease_incidence_default.xml";
		File outputFile = new File(outputFilePath);
		try {
			LeafNodeList theList = new LeafNodeList();
			theList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
					.getInstance().get("age"), null));
			theList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
					.getInstance().get("sex"), null));
			theList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
					.getInstance().get("value"), null));
			Object result = new AgnosticFactory().manufactureDefault(theList,
					false);
			assertNotNull(result);
			try {
				StAXAgnosticWriter.produceFile((FileControlSingleton
						.getInstance()).get("incidences"),
						(HashMap<Integer, Object>) result, outputFile);
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
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNotNull(e); // Force error.
		}
	}


	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestAgnosticFactoryDefaultBehaviour.class);
	}
}
