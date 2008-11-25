package nl.rivm.emi.dynamo.estimation.test;

import junit.framework.Assert;
import nl.rivm.emi.dynamo.datahandling.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynamoLib;
import nl.rivm.emi.dynamo.estimation.InitialPopulationFactory;
import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.ModelParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class test2_initialpop {
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
	

	try {

		
		BaseDirectory baseDir=BaseDirectory.getInstance("c:\\hendriek\\test");
		ModelParameters E1 = new ModelParameters();
		InputData testdata = new InputData();
		testdata.makeTest1Data();
		E1.estimateModelParameters(100,testdata);
		InitialPopulationFactory E2=new InitialPopulationFactory();
		E2.manufactureInitialPopulation(E1,"simname",10, 111,true);
		// test weighted regression

		
	} catch (Exception e) {
		if (e.getMessage()==null)
		System.err.println(" some form of unknown exception trown");
		else System.err.println(e.getMessage());

	}
}


}
