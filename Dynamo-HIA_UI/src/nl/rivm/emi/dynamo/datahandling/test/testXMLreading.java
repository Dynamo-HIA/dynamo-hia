package nl.rivm.emi.dynamo.datahandling.test;

import java.util.ArrayList;

import junit.framework.Assert;
import nl.rivm.emi.cdm.rules.update.dynamo.ArraysFromXMLFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class testXMLreading {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
		log.fatal("Starting test. ");
	}

	@After
	public void teardown() {
		log.fatal("Test completed ");
	}

	@Test
	public void test() {

		// main conducts a set of tests for testing the different parts

		// data to test the regression
		String f = "c:/hendriek/java/workspace/dynamo/dynamoinput/test.xml";
		String ff = "c:/hendriek/java/workspace/dynamo/dynamoinput/relrisktest.xml";
		float[][] incidence;
		ArrayList<ArrayList<ArrayList<Float>>> relrisk;
		try {
			ArraysFromXMLFactory factory= new ArraysFromXMLFactory();
			incidence = factory.manufactureOneDimArray(f,
					"incidences", "incidence");

			for (int age = 0; age < 96; age++)
				for (int sex = 0; sex < 2; sex++)
					Assert.assertEquals(incidence[age][sex] == 0.1F, true);
			log.fatal("Test 1 OK ");
// TODO			relrisk=factory.manufactureTwoDimArray(ff,"relrisks", "relrisk");
			
			
		} catch (Exception e) {
			if (e.getMessage() == null)
				System.err.println(" some form of unknown exception trown");
			else
				System.err.println(e.getMessage());

		}
	}

}
