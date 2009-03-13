/**
 * 
 */
package nl.rivm.emi.dynamo.estimation.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynamoOutputFactory;

import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;
import nl.rivm.emi.dynamo.estimation.ModelParameters;
import nl.rivm.emi.dynamo.estimation.Output_UI;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
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
			"c:\\hendriek\\java\\dynamohome\\").getBaseDir();

	// NB de directory moet ook worden aangepast in deze file //
	
	String simName = "simulation1";

	@Before
	public void setup() {}
	
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
