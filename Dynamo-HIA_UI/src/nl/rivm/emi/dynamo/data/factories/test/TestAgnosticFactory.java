package nl.rivm.emi.dynamo.data.factories.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.HashMap;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.writers.AgnosticWriter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAgnosticFactory {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testManufacturing() {
		String configurationFilePath = "data" + File.separator + "development"
				+ File.separator + "incidence1.xml";
		File configurationFile = new File(configurationFilePath);
		log.debug(configurationFile.getAbsolutePath());
		try {
			Object result = new AgnosticFactory().manufacture(
					configurationFile, false);
			assertNotNull(result);
			AtomicTypesSingleton single = AtomicTypesSingleton.getInstance();
			AtomicTypeBase<Number> age = single.get("age");
			AtomicTypeBase<Number> sex = single.get("sex");
			AtomicTypeBase<Number> value = single.get("value");
			AtomicTypeBase[] types = new AtomicTypeBase[3];
			types[0] = age;
			types[1] = sex;
			types[2] = value;
			AgnosticWriter.logContent((AtomicTypeBase<Number>[]) types,
					(HashMap) result);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestAgnosticFactory.class);
	}
}
