package nl.rivm.emi.dynamo.data.factories.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeDataFromXMLFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestPopulationSizeDataFromXMLFactory {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testManufacturing() {
		String configurationFilePath = "datatemplates" + File.separator + "5agestep_2gender_popsize.xml";
		File configurationFile = new File(configurationFilePath);
		log.fatal(configurationFile.getAbsolutePath());
		AgeSteppedContainer<BiGenderSteppedContainer<Integer>> testContainer = 
			PopulationSizeDataFromXMLFactory.manufacture(configurationFile);
		assertNotNull(testContainer);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.data.factories.test.TestPopulationSizeDataFromXMLFactory.class);
	}
}





