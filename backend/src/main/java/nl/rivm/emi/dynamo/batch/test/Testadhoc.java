package nl.rivm.emi.dynamo.batch.test;


/**
 * 
 */

/* werkt niet */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import junit.framework.Assert;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.rules.update.dynamo.ArraysFromXMLFactory;
import nl.rivm.emi.dynamo.batch.Runner;
import nl.rivm.emi.dynamo.estimation.InputDataFactory.ScenInfo;
import nl.rivm.emi.dynamo.output.DynamoOutputFactory;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;
import nl.rivm.emi.dynamo.global.ScenarioParameters;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author boshuizh
 * This global test class is there to adapt for adhoc purposes tests for DYNAMO-HIA
 * 
 */

 
public class Testadhoc {
	/**
	 * @throws java.lang.Exception
	 */
	Log log = LogFactory.getLog(getClass().getName());
	String[] files = new String[1];

	/*
	 * 
	 * 
	 * NB the base directory needs to be put in by hand !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * 
	 * 
	 */
	
	
	
	

	

	@Before
	public void setUp() throws Exception {

		// files[0]="C:\\DYNAMO-HIA\\TESTDATA\\temp";

	}

	// this test tests against previous versions of DYNAMO
	// not necessarily correct
	// but so differences will be noted
	
	// in the interface : sullivan oud = 67.832, le is 70.304 = idem aan oude versie
	
	// hier echter 77.31025037823959, 9.476103206219452
	// nee, het is 77.3040717 en 9.4723516298 in nieuwe versie van oud
	
	@Test
	public void test_runbatch() {
		String baseDir = "C:\\DYNAMO-HIA\\testdynamoHIa\\DynamoInput";
		 String batchFileName="simulationnames.txt";
		 String testName=" SmokingCVAdiabetes1" ;
			files[0] = baseDir + File.separator + batchFileName;
			log.fatal(testName + " started ");
			// if the scenario is larger than zero we assume that the output object has already been made
			//!!!!!!!! WARNING: remove if part when first scenario tested is not the reference scenario
		    Runner.main(files);
			log.fatal("runner has run");

	     
	} 
	
	
	/*
	 * this test is  testing if results are the same as in DYNAMO-2. This is meant for future versions, and to check that
	 * bugfixing does not change the results
	 * For testing DYNAMO-2 , this does not have any intrinsic value, as results are taken from the program itself
	 */
	
	
}
