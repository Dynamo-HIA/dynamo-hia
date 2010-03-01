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
	
		files[0]="C:\\DYNAMO-HIA\\TESTDATA\\tests_to_run";
	//	files[0]="C:\\DYNAMO-HIA\\TESTDATA\\temp";
    	
        
	}
	

	@Test
	public void test() {
		
   
   
	Runner.main(files); }

	@After
	public void teardown() {
		log.fatal("Test completed ");
	}
}
