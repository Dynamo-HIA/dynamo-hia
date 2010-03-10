/**
 * 
 */
package nl.rivm.emi.dynamo.batch.test;

/* werkt niet */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import junit.framework.Assert;

import nl.rivm.emi.dynamo.batch.Runner;
import nl.rivm.emi.dynamo.output.DynamoOutputFactory;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

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
	String[] files = new String[1];

	@Before
	public void setUp() throws Exception {

		// files[0]="C:\\DYNAMO-HIA\\TESTDATA\\temp";

	}

	@Test
	public void test() {

	//	runTest("run_test_1", "test01", 11.83285, 6.95871, 10.064339, 7.263274,
	//			0, 0);
	//	runTest("run_test_2", "test02", 11.84868, 7.33809, 10.064933, 7.517412,
	//			0, 0);
	//	runTest("run_test_3", "test03", 11.62399547, 8.426732, 10.05240022, 7.25465746,
	//			10.449351, 9.047160194);
	//	runTest("run_test_4", "test04", 11.64373307, 8.627173, 10.05454054, 7.509649749,
	//			10.430651, 9.04908649);
	//	runTest("run_test_5", "test05", 11.86262859, 10.323828, 10.06424551, 9.057820083,
	//			10.544173, 9.057820963);
	//	runTest("run_test_6", "test06", 10.94401469, 7.098233, 10.44058873, 7.572985171,
	//			9.666941, 9.414778818);
	//	runTest("run_test_7", "test07", 11.02311413, 7.387165, 10.44525728, 7.846585711,
	//			9.721771, 9.420975178);
	//	runTest("run_test_8", "test08", 11.6627042, 7.363732, 11.37419777, 8.37765101,
	//						10.225445, 10.3010073);
	//	runTest("run_test_9", "test09", 11.70370647, 8.183877, 11.3731399, 8.667465314,
	//			10.469886, 10.30456292);
		runTest("run_test_10", "test10", 11.09527441, 9.680941, 10.43985847, 9.447335957,
				9.871448, 9.415172457);
	}

	/** test comparing health and life expectancies of the test with
	 * the expected values; expected value=0 means that no test is carried out
	 * for this value
	 * @param batchFileName Name of the batchfile
	 * @param testName testName as found in the batchfile
	 * @param LEcohort
	 * @param HLEcohort
	 * @param LEPeriod
	 * @param HLESullivan
	 * @param DALYcohort
	 * @param DALYSullivan
	 */
	private void runTest(String batchFileName, String testName,
			double LEcohort, double HLEcohort, double LEPeriod,
			double HLESullivan, double DALYcohort, double DALYSullivan) {
		/* test 01 */

		String baseDir = "C:\\DYNAMO-HIA\\TESTDATA";
		files[0] = baseDir + File.separator + batchFileName;
		log.fatal(testName + " started ");
		Runner.main(files);
		DynamoOutputFactory  output =null;
		output = getOutput(baseDir, testName);
		ScenarioParameters scenParms = getScenParms(baseDir, testName);
		DynamoPlotFactory factory = new DynamoPlotFactory(output, scenParms);
		if (HLEcohort != 0){double[][] hle = factory.calculateCohortHealthExpectancy(0, 0, -1);
		Assert.assertEquals(" LE cohort is "+hle[0][0]+ "but should be "+LEcohort,true,
				Math.abs(hle[0][0] - LEcohort) < 1E-4);
		Assert.assertEquals(" HLE cohort is "+(hle[0][0]- hle[0][1])+ "but should be "+HLEcohort,true,
				Math.abs(hle[0][0] - hle[0][1] - HLEcohort) < 1E-4);}
		if (HLESullivan != 0){
		double[][] hle2 = factory.calculateSullivanLifeExpectancy(0, 0, -1, 0);
		Assert.assertTrue(" LE-Period is "+hle2[0][0]+ "but should be "+LEPeriod,Math.abs(hle2[0][0] - LEPeriod) < 1E-4);
		Assert.assertEquals(" HLE-Period is "+(hle2[0][0]-hle2[0][1])+ "but should be "+HLESullivan,true, 
				  Math.abs(hle2[0][0] - hle2[0][1]
				- HLESullivan) < 1E-4);
		}
		
		if (DALYcohort != 0) {
			double[][] hle3 = factory.calculateCohortHealthExpectancy(0, 0, -2);
				Assert.assertEquals(" LE-cohort is "+hle3[0][0]+ "but should be "+LEcohort,
					true, Math.abs(hle3[0][0] - LEcohort) < 1E-4);
			Assert.assertEquals(" DALE-cohort is "+(hle3[0][0]-hle3[0][1])+ "but should be "+DALYcohort,
					true, Math.abs(hle3[0][0]-hle3[0][1] - DALYcohort) < 1E-4);
		}
		if (DALYSullivan != 0) {
			double[][] hle4 = factory.calculateSullivanLifeExpectancy(0, 0, -2,
					0);
			Assert.assertEquals(" LE-Period is "+hle4[0][0]+ "but should be "+LEPeriod,
					true, Math.abs(hle4[0][0] - LEPeriod) < 1E-4);
			Assert.assertEquals(" DALE-Period is "+(hle4[0][0]-hle4[0][1])+ "but should be "+DALYSullivan,
					true,
					Math.abs(hle4[0][0] -hle4[0][1] - DALYSullivan) < 1E-4);
		}
		log.fatal(testName+" successfully completed ");
	}

	@After
	public void teardown() {
		log.fatal("Test completed ");
	}

	public DynamoOutputFactory getOutput(String baseDir, String simName) {

		String resultFileName = baseDir + File.separator + "Simulations"
				+ File.separator + simName + File.separator + "results"
				+ File.separator + "resultsObject.obj";
		File resultFile = new File(resultFileName);
		DynamoOutputFactory output = null;
		if (resultFile.exists()) {

			FileInputStream resultFileStream;
			try {
				resultFileStream = new FileInputStream(resultFileName);
				ObjectInputStream inputStream = new ObjectInputStream(
						resultFileStream);
				output = (DynamoOutputFactory) inputStream.readObject();
				return output;
			} catch (FileNotFoundException e1) {

			} catch (IOException e2) {

				e2.printStackTrace();
			} catch (ClassNotFoundException e3) {

			}
		}
		return output;

	}

	public ScenarioParameters getScenParms(String baseDir, String simName) {
		String parmsFileName = baseDir
				+ File.separator
				+ "Simulations"
				+ File.separator
				+ simName
				+ File.separator
				+ StandardTreeNodeLabelsEnum.RESULTS.getNodeLabel()
				+ File.separator
				+ StandardTreeNodeLabelsEnum.SCENARIOPARMSOBJECTFILE
						.getNodeLabel() + ".obj";
		File parmsFile = new File(parmsFileName);
		ScenarioParameters scenParms = null;

		if (parmsFile.exists()) {

			FileInputStream parmsFileStream;

			try {
				parmsFileStream = new FileInputStream(parmsFileName);
				ObjectInputStream inputStream = new ObjectInputStream(
						parmsFileStream);
				scenParms = (ScenarioParameters) inputStream.readObject();
			} catch (FileNotFoundException e1) {
				// new ErrorMessageWindow(
				// "Error message while reading the resulst object with message: "
				// + e1.getMessage(), parentShell);

				e1.printStackTrace();
			} catch (IOException e2) {
				// new ErrorMessageWindow(
				// "Error message while reading the results object with message: "
				// + e2.getMessage(), parentShell);

				e2.printStackTrace();
			} catch (ClassNotFoundException e3) {
				// new ErrorMessageWindow(
				// "Error message while reading the results object with message: "
				// + e3.getMessage(), parentShell);

				e3.printStackTrace();
			}

		}
		return scenParms;
	}
}
