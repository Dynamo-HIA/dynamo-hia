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
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.rules.update.dynamo.ArraysFromXMLFactory;
import nl.rivm.emi.dynamo.batch.Runner;
import nl.rivm.emi.dynamo.output.DynamoOutputFactory;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;
import nl.rivm.emi.dynamo.global.ScenarioParameters;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author boshuizh
 * 
 */
public class testbatch_II {
	/**
	 * @throws java.lang.Exception
	 */
	Log log = LogFactory.getLog(getClass().getName());
	String[] files = new String[1];
	String baseDir = "C:\\DYNAMO-HIA\\TESTDATA";
	ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

	@Before
	public void setUp() throws Exception {

		// files[0]="C:\\DYNAMO-HIA\\TESTDATA\\temp";

	}

	@Test
	public void test_1() {

	     runTest("run_test_1", "test01", 11.83285, 6.95871, 10.064339,
		 7.263274,
		 0, 0);
	}

	@Test
	public void test_1a() {
	     runTest("run_test_1a", "test01a", 11.83285, 6.95871, 10.064339,
			 7.263274,
			 0, 0);
	}

	@Test
	public void test_2() {
		 runTest("run_test_2", "test02", 11.84868, 7.33809, 10.064933,
		 7.517412,
		 0, 0);
	}

	@Test
	public void test_3() {
		 runTest("run_test_3", "test03", 11.62399547, 8.426732, 10.05240022,
		 7.25465746,
		 10.449351, 9.047160194);
	}

	@Test
	public void test_4() {
		 runTest("run_test_4", "test04", 11.64373307, 8.627173, 10.05454054,
		 7.509649749,
		 10.430651, 9.04908649);
	}

	@Test
	public void test_5() {
		runTest("run_test_5", "test05", 11.86143824, 10.319618, 10.06416548,
				 9.057748055,
				 10.541925, 9.057748935); 
	}

	@Test
	public void test_6() {
		 runTest("run_test_6", "test06", 10.94401469, 7.098233, 10.44058873,
		 7.572985171,
		 9.666941, 9.414778818); 
	}

	@Test
	public void test_7() {
		 runTest("run_test_7", "test07", 11.02311413, 7.387165, 10.44525728,
		 7.846585711,
		 9.721771, 9.420975178);
	}

	@Test
	public void test_8() {
		 runTest("run_test_8", "test08", 11.6627042, 7.363732, 11.37419777,
		 8.37765101,
		 10.225445, 10.3010073);
	}

	@Test
	public void test_9() {
		 runTest("run_test_9", "test09", 11.70370647, 8.183877, 11.3731399,
		 8.667465314,
		 10.469886, 10.30456292);
	}

	@Test
	public void test_10() {
		  runTest("run_test_10", "test10", 11.09238588, 9.669386, 10.4396114,
					 9.447112375,
					 9.865490, 9.414949636);
	}

	@Test
	public void test_11() {
		 runTest("run_test_11", "test11", 11.62399547, 8.426732, 10.05240022,
		 7.25465746,
		 10.449351, 9.047160194);
	}

	@Test
	public void test_11a() {
		 runTest("run_test_11a", "test11a", 11.62399547, 8.426732, 10.05240022,
				 7.25465746,
				 10.449351, 9.047160194);
	}

	@Test
	public void test_12() {
		 runTest("run_test_12", "test12", 11.64373307, 8.627173, 10.05454054,
		 7.509649749,
		 10.430651, 9.04908649); 
	}
	@Test
	public void test_12a() {
		checkParameters("test12", 0.0035560585F, 0.004974321F, 0.0039015845F,
				0.043575432F, 2.4666753F, 2.4666753F, 0.5F, 0.039915062F, true); 
	}
	
	@Test
	public void test_13() {
	 runTest("run_test_13", "test13", 11.86143824, 10.319618, 10.06416548,
			 9.057748055,
			 10.541925, 9.057748935); 
	}
	@Test
	public void test_14() {
		 runTest("run_test_14", "test14", 9.955002415, 7.037181, 10.00184631,
				 7.218174589,
				 8.903619, 9.001661675); 
	}

	@Test
	public void test_15() {
		 runTest("run_test_15", "test15", 9.957494346, 7.259818, 10.00333218,
				 7.47140266,
				 8.895412, 9.002998958); 
	}
	
	@Test
	public void test_17() {
		 runTest("run_test_17", "test17", 11.62399547, 8.426732, 10.05240022,
				 7.25465746,
				 10.449351, 9.047160194);
	}
	@Test
	public void test_20() {
		 runTest("run_test_20", "test20", 11.62399547, 8.426732, 10.05240022,
				 7.25465746,
				 10.449351, 9.047160194);
	}
	


	private void checkParameters(String simName, float baselineIncidenceA,
			float baselineIncidenceB, float atmort1, float atmort2,
			float rrOMbegin, float rrOMend, float alphaOM,
			float baselineOtherMort, boolean dependent) {

		String parameterDir = baseDir + "Simulations" + File.separator
				+ simName + File.separator + "parameters" + File.separator;
		try {
			float atmortA_model = factory.manufactureOneDimArray(parameterDir
					+ "attributableMort_0_0_diseaseA.xml",
					"attributableMortalities", "attributableMortality", false)[0][0];
			float incA = factory.manufactureOneDimArray(parameterDir
					+ "baselineIncidence_0_0_diseaseA.xml",
					"baselineIncidences", "baselineIncidence", false)[0][0];
			
			
			float incB = 0;
			float atmortB_model =0;
			if (dependent) {
				
				incB = factory.manufactureOneDimArray(parameterDir
						+ "baselineIncidence_0_1_diseaseB.xml",
						"baselineIncidences", "baselineIncidence", false)[0][0];
				 atmortB_model = factory.manufactureOneDimArray(parameterDir
						+ "attributableMort_0_1_diseaseB.xml",
						"attributableMortalities", "attributableMortality", false)[0][0];
			} else {
				
				incB = factory.manufactureOneDimArray(parameterDir
						+ "baselineIncidence_1_0_diseaseB.xml",
						"baselineIncidences", "baselineIncidence", false)[0][0];
				 atmortB_model = factory.manufactureOneDimArray(parameterDir
						+ "attributableMort_1_0_diseaseB.xml",
						"attributableMortalities", "attributableMortality", false)[0][0];
			}

			float RRbegin = factory.manufactureOneDimArray(parameterDir
					+ "beginRelativeRisk_OtherMort.xml",
					"relativerisks_othermort_begin", "relativerisk", false)[0][0];
			float RRend = factory.manufactureOneDimArray(parameterDir
					+ "endRelativeRisk_OtherMort.xml",
					"relativerisks_othermort_end", "relativerisk", false)[0][0];
			float alpha = factory.manufactureOneDimArray(parameterDir
					+ "alpha_OtherMort.xml", "alphasothermortality", "alpha",
					false)[0][0];
			float baselineOM = factory.manufactureOneDimArray(parameterDir
					+ "baselineOtherMort.xml", "baselineOtherMortalities",
					"baselineOtherMortality", false)[0][0];

			Assert.assertTrue(" attributable mortality disease A is "
					+ atmortA_model + "but should be " + atmort1, Math
					.abs(atmortA_model - atmort1) < 1E-5);
			Assert.assertTrue(" attributable mortality disease B is "
					+ atmortB_model + "but should be " + atmort2, Math
					.abs(atmortB_model - atmort2) < 1E-5);
			Assert.assertTrue(" baseline incidence disease A is " + incA
					+ "but should be " + baselineIncidenceA, Math
					.abs(baselineIncidenceA - incA) < 1E-5);
			Assert.assertTrue(" baseline incidence disease B is " + incB
					+ "but should be " + baselineIncidenceB, Math
					.abs(baselineIncidenceB - incB) < 1E-5);
			Assert.assertTrue(" RR otherMort begin is " + RRbegin
					+ "but should be " + rrOMbegin, Math.abs(rrOMbegin
					- RRbegin) < 1E-5);
			Assert.assertTrue(" RR otherMort end is " + RRend
					+ "but should be " + rrOMend,
					Math.abs(rrOMend - RRend) < 1E-5);
			Assert.assertTrue(" baseline otherMort is " + baselineOM
					+ "but should be " + baselineOtherMort, Math
					.abs(baselineOtherMort - baselineOM) < 1E-5);

		} catch (DynamoConfigurationException e) {

			e.printStackTrace();
		}

	}

	/**
	 * test comparing health and life expectancies of the test with the expected
	 * values; expected value=0 means that no test is carried out for this value
	 * 
	 * @param batchFileName
	 *            Name of the batchfile
	 * @param testName
	 *            testName as found in the batchfile
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

	//	String baseDir = "C:\\DYNAMO-HIA\\TESTDATA";
	//	String baseDir = "C:\\DYNAMO-HIA\\Sweden_SL";
		files[0] = baseDir + File.separator + batchFileName;
		log.fatal(testName + " started ");
		Runner.main(files);
		log.fatal("runner has run");
		DynamoOutputFactory output = null;
		output = getOutput(baseDir, testName);
		ScenarioParameters scenParms = getScenParms(baseDir, testName);
		int nScenarios=scenParms.getSuccesPercentage().length;
		log.fatal("starting plotfactory");
		DynamoPlotFactory factory = new DynamoPlotFactory(output, scenParms);
		log.fatal("plotfactory ready");
		for (int scen=0; scen<nScenarios+1; scen++){
			log.fatal( "checking scenario "+scen);
		if (HLEcohort != 0) {
			double[][] hle = factory.calculateCohortHealthExpectancy(0, scen, -1);
			Assert.assertEquals(" LE cohort is " + hle[0][0] + "but should be "
					+ LEcohort, true, Math.abs(hle[0][0] - LEcohort) < 1E-4);
			Assert.assertEquals(" HLE cohort is " + (hle[0][0] - hle[0][1])
					+ "but should be " + HLEcohort, true, Math.abs(hle[0][0]
					- hle[0][1] - HLEcohort) < 1E-4);
		}
		log.fatal(" HLE cohort is OK ");
		if (HLESullivan != 0) {
			double[][] hle2 = factory.calculateSullivanLifeExpectancy(0, 0, -1,
					scen);
			Assert.assertTrue(" LE-Period is " + hle2[0][0] + "but should be "
					+ LEPeriod, Math.abs(hle2[0][0] - LEPeriod) < 1E-4);
			Assert.assertEquals(" HLE-Period is " + (hle2[0][0] - hle2[0][1])
					+ "but should be " + HLESullivan, true, Math.abs(hle2[0][0]
					- hle2[0][1] - HLESullivan) < 1E-4);
		}
		log.fatal(" HLE Sullivan is OK ");
		if (DALYcohort != 0) {
			double[][] hle3 = factory.calculateCohortHealthExpectancy(0, scen, -2);
			Assert.assertEquals(" LE-cohort is " + hle3[0][0]
					+ "but should be " + LEcohort, true, Math.abs(hle3[0][0]
					- LEcohort) < 1E-4);
			Assert.assertEquals(" DALE-cohort is " + (hle3[0][0] - hle3[0][1])
					+ "but should be " + DALYcohort, true, Math.abs(hle3[0][0]
					- hle3[0][1] - DALYcohort) < 1E-4);
		}
		log.fatal(" DALY cohort is OK ");
		if (DALYSullivan != 0) {
			double[][] hle4 = factory.calculateSullivanLifeExpectancy(0, 0, -2,
					scen);
			Assert.assertEquals(" LE-Period is " + hle4[0][0]
					+ "but should be " + LEPeriod, true, Math.abs(hle4[0][0]
					- LEPeriod) < 1E-4);
			Assert.assertEquals(" DALE-Period is " + (hle4[0][0] - hle4[0][1])
					+ "but should be " + DALYSullivan, true, Math
					.abs(hle4[0][0] - hle4[0][1] - DALYSullivan) < 1E-4);
		}
		log.fatal(" Daly sullivan is OK ");
		}
		log.fatal(testName + " successfully completed ");
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
