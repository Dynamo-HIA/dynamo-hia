/**
 * 
 */
package nl.rivm.emi.dynamo.estimation.test;

import nl.rivm.emi.dynamo.estimation.MTRand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * @author Hendriek
 *
 */
public class testMRRand {




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

MTRand M = new MTRand()	;
long seed=1111;
log.fatal("1000 random numbers starting with random seeds");
for (int i=0;i<1000;i++) log.fatal(M.random());
MTRand M2 = new MTRand(seed)	;
log.fatal("1000 random numbers starting with 1111 seed");
for (int i=0;i<1000;i++) log.fatal(M2.random());


}


}


