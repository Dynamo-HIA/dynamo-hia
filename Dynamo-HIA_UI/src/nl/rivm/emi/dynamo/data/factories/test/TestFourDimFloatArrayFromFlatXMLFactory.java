package nl.rivm.emi.dynamo.data.factories.test;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.notinuse.FourDimFloatArrayFromFlatXMLFactory;
import nl.rivm.emi.dynamo.data.transition.DestinationsByOriginMap;
import nl.rivm.emi.dynamo.data.transition.ValueByDestinationMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestFourDimFloatArrayFromFlatXMLFactory {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testManufacturing() {
		String configurationFilePath = "datadiscussion" + File.separator + "transitions.xml";
		File configurationFile = new File(configurationFilePath);
		log.debug(configurationFile.getAbsolutePath());
		AgeMap<SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>>> testContainer = 
			FourDimFloatArrayFromFlatXMLFactory.manufacture(configurationFile);
		assertNotNull(testContainer);

	}


//	@Test
//	public void testArrayManufacturing() {
//		String configurationFilePath = "datadiscussion" + File.separator + "transitions.xml";
//		File configurationFile = new File(configurationFilePath);
//		log.debug(configurationFile.getAbsolutePath());
//		float[][][][] theArray =  FourDimFloatArrayFromFlatXMLFactory.manufactureArray(configurationFile);
//		assertNotNull(theArray);
//
//	}

	@Test
	public void testArrayManufacturing() {
		String configurationFilePath = "datadiscussion" + File.separator + "transitions.xml";
		File configurationFile = new File(configurationFilePath);
		log.debug(configurationFile.getAbsolutePath());
		float[][][][] theArray =  FourDimFloatArrayFromFlatXMLFactory.manufactureArray(configurationFile);
		assertNotNull(theArray);

	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				TestFourDimFloatArrayFromFlatXMLFactory.class);
	}
}





