/**
 * 
 */
package nl.rivm.emi.dynamo.estimation.test;

import java.io.File;

import nl.rivm.emi.dynamo.estimation.MTRand;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

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
String basedir = new String("\\\\alt.rivm.nl\\d1\\d2\\Simulations\\d4");
log.fatal("original string = "+basedir);
String basedir2 = new String(basedir.substring(0));
log.fatal("new string = "+basedir2);
String baseDirectoryPath = basedir.substring(0,
		basedir.lastIndexOf(File.separator
				+ StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()
				+ File.separator));
log.fatal("new string als in runselectionlisterner= "+baseDirectoryPath );
}


}


