/**
 * 
 */
package nl.rivm.emi.dynamo.estimation.test;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynSimRunPRInterface;
import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;
import nl.rivm.emi.dynamo.estimation.GraphicalDynSimRunPR;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

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
	
	String simName = "simulation5";

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
	public void runSimulation() throws DynamoInconsistentDataException  {

		Display display= new Display();
		Shell shell=new Shell(display);
		DynSimRunPRInterface dsi = new GraphicalDynSimRunPR(shell);
		DynamoSimulationRunnable R= new DynamoSimulationRunnable(dsi, simName,baseDir);
		 R.run();
		 while (!shell.isDisposed()) { if (!display.readAndDispatch()) { display.sleep();}}
		 display.dispose();
		
			
	
}
		}
