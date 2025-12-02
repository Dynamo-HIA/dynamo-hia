/**
 * 
 */
package nl.rivm.emi.dynamo.estimation.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FilenameFilter;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynSimRunPRInterface;
import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;
import nl.rivm.emi.dynamo.estimation.LoggingDynSimRunPR;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mondeelr<br/>
 *         Test for run of a single simulation without a graphical
 *         userinterface. The output that is normally presented onscreen is now
 *         output into the log-file.<br/>
 *         Of course the log4j parameters must have been set correctly.
 * 
 */

public class TestNonGraphicalSimulationRun {
	Log log = LogFactory.getLog(getClass().getName());
	/**
	 * The BaseDirectory needs to be set manually for this test.
	 */
	@SuppressWarnings("static-access")
	final String baseDir = BaseDirectory.getInstance(
			"D:\\DynamoHIA_testspaces\\Netherlands_scmeeting").getBaseDir();
	/**
	 * This name must be the name of a directory below the "Simulations"
	 * directory.<br/>
	 * It must contain a valid simulation-configuration for this test to
	 * succeed.
	 */
	final String simName = "NL_BMI_cat3";
	File baseDirFile = null;
	File simulationsDirFile = null;
	File simulationDirFile = null;
	File simulationConfigurationFile = null;

	/**
	 * This setup is not strictly nescessary, but it provides some entities for
	 * tests to make sure the configuration fields above are valid/up-to-date.
	 */
	@Before
	public void setup() {
		baseDirFile = new File(baseDir);
		if (baseDirFile.exists() && baseDirFile.isDirectory()) {
			String[] simulationsDirectoryNameArray = baseDirFile
					.list(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							return StandardTreeNodeLabelsEnum.SIMULATIONS
									.getNodeLabel().equals(name);
						}
					});
			if (simulationsDirectoryNameArray.length == 1) {
				simulationsDirFile = new File(baseDirFile.getAbsolutePath()
						+ File.separator + simulationsDirectoryNameArray[0]);
				if (simulationsDirFile.exists()
						&& simulationsDirFile.isDirectory()) {
					String[] simulationDirectoryNameArray = simulationsDirFile
							.list(new FilenameFilter() {

								@Override
								public boolean accept(File dir, String name) {
									return simName.equals(name);
								}
							});
					if (simulationDirectoryNameArray.length == 1) {
						simulationDirFile = new File(simulationsDirFile
								.getAbsolutePath()
								+ File.separator
								+ simulationDirectoryNameArray[0]);
						if (simulationDirFile.exists()
								&& simulationDirFile.isDirectory()) {
							String[] simulationConfigurationNameArray = simulationDirFile
									.list(new FilenameFilter() {

										@Override
										public boolean accept(File dir,
												String name) {
											return (StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
													.getNodeLabel() + ".xml")
													.equals(name);
										}
									});
							if (simulationConfigurationNameArray.length == 1) {
								simulationConfigurationFile = new File(
										simulationDirFile.getAbsolutePath()
												+ File.separator
												+ simulationConfigurationNameArray[0]);
								if (!(simulationConfigurationFile.exists() && simulationConfigurationFile
										.isFile())) {
									simulationConfigurationFile = null;
								}
							}
						} else {
							simulationDirFile = null;
						}

					}
				} else {
					simulationsDirFile = null;
				}
			}
		} else {
			baseDirFile = null;
		}
	}

	/**
	 * 
	 */
	@After
	public void teardown() {
		log.fatal("Test completed ");
	}

	@Test
	public void runSimulation() throws DynamoInconsistentDataException {
		assertNotNull(baseDirFile);
		assertNotNull(simulationsDirFile);
		assertNotNull(simulationDirFile);
		assertNotNull(simulationConfigurationFile);
		DynSimRunPRInterface dsi = new LoggingDynSimRunPR();
		DynamoSimulationRunnable R = new DynamoSimulationRunnable(dsi, simName,
				baseDir);
		R.run();
	}
}
