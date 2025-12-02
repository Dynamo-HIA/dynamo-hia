package nl.rivm.emi.dynamo.estimation.test;

import nl.rivm.emi.dynamo.estimation.NettTransitionRateFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class testnettransitions {
	Log log = LogFactory.getLog(getClass().getName());
	String baseDir;
	
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
	try {
	float [] oldprev = { 0.2F, 0.3F,0.2F,0.3F,0,0, 0 ,0};
	float [] newprev = { 0,0,0,0,0.2F,0.3F,0.2F,0.3F};

	float[][] result = NettTransitionRateFactory.makeNettTransitionRates(oldprev, newprev);
	System.err.println(result[0][0]+" "+result[1][0]+" "+result[0][1]+" "+result[1][1]);
	

		
	} catch (Exception e) {
		System.err.println(e.getMessage());

	}
}


}
