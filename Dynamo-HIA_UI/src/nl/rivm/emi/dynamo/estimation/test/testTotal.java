/**
 * 
 */
package nl.rivm.emi.dynamo.estimation.test;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author boshuizh
 *
 */


public class testTotal {
Log log = LogFactory.getLog(getClass().getName());
	
	String baseDir = BaseDirectory.getInstance(
			"C:\\dynamo\\eclipse_workspaces\\dynamo\\DynamoDataSet").getBaseDir();

	// NB de directory moet ook worden aangepast in deze file //
	
	String simName = "simulation1";

	@Before
	public void setup() {}
	
	/**
	 * 
	 */
	@After
	public void teardown() {
		log.fatal("Test completed ");
	}
	
	@Test
	public void runSimulation()  {

		Display display= new Display();
		Shell shell=new Shell(display);
		DynamoSimulationRunnable R= new DynamoSimulationRunnable(shell, simName,baseDir);
		 R.run();
		 while (!shell.isDisposed()) { if (!display.readAndDispatch()) { display.sleep();}}
		 display.dispose();
		
			
	
}
		}
