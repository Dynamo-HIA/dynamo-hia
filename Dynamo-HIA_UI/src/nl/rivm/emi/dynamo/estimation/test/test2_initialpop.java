package nl.rivm.emi.dynamo.estimation.test;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;
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
	String baseDir;
	
@Before
	public void setup() {
	log.fatal("Starting test. ");
	baseDir = BaseDirectory.
	getInstance("c:\\hendriek\\java\\dynamohome\\").getBaseDir();
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

		

		ModelParameters E1 = new ModelParameters(baseDir);
		InputData testdata = new InputData();
		testdata.makeTest1Data();
		E1.estimateModelParameters(100,testdata, null);
		InitialPopulationFactory E2=new InitialPopulationFactory(baseDir, null);
		E2.manufactureInitialPopulation(E1,"simname",10, 111,true, null);
		// test weighted regression

		
	} catch (Exception e) {
		if (e.getMessage()==null)
		System.err.println(" some form of unknown exception trown");
		else System.err.println(e.getMessage());

	}
}


}
