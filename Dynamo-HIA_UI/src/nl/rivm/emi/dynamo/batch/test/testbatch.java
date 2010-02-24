/**
 * 
 */
package nl.rivm.emi.dynamo.batch.test;

/* werkt niet */
import nl.rivm.emi.dynamo.batch.Runner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author boshuizh
 *
 */
public class testbatch {
	/**
	 * @throws java.lang.Exception
	 */
	Log log = LogFactory.getLog(getClass().getName());
	String [] files=new String [2];
	
	@Before
	public void setUp() throws Exception {
	
		files[0]="C:\\DYNAMO-HIA\\TESTDATA\\test";
    	
        
	}
	

	@Test
	public void test() {
		files[0]="C:\\DYNAMO-HIA\\TESTDATA\\test";
   
    log.fatal(files[0]+" is read ");
	Runner.main(files); }

	@After
	public void teardown() {
		log.fatal("Test completed ");
	}
}
