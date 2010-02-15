package nl.rivm.emi.dynamo.estimation.test;

import junit.framework.Assert;
import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynSimRunPRInterface;
import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.LoggingDynSimRunPR;
import nl.rivm.emi.dynamo.estimation.ModelParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestEstimation {
	Log log = LogFactory.getLog(getClass().getName());

	String baseDir;
	DynSimRunPRInterface dsi = null;

	@Before
	public void setup() {
		log.fatal("Starting test. ");
		baseDir = BaseDirectory.getInstance("c:\\hendriek\\java\\dynamohome\\")
				.getBaseDir();
		dsi = new LoggingDynSimRunPR();

	}

	@After
	public void teardown() {
		log.fatal("Test completed ");
	}

	@Test
	public void test() {

		// main conducts a set of tests for testing the different parts

		// data to test the regression
		double y[] = { 1, 2, 3, 4, 5, 7 };
		double w[] = { 1, 1, 1, 1, 1, 1 };
		double w2[] = { 2, 2, 2, 2, 2, 2 };
		double x[][] = { { 1, 2, 3, 4 }, { 1, 3, 5, 5 }, { 1, 4, 7, 2 },
				{ 1, 5, 8, 3 }, { 1, 6, 7, 5 }, { 1, 7, 8, 9 } };
		double coef[] = { 0, 0, 0 };
		double coef2[] = { 0, 0, 0 };
		InputData testdata = new InputData();

		try {

			// first test categorical variables

			ModelParameters E1 = new ModelParameters(baseDir);

			// test weighted regression

			coef = E1.weightedRegression(y, x, w);

			/*
			 * Data set y + 3 x-en: 1 2 3 4 2 3 5 5 3 4 7 2 4 5 8 3 5 6 7 5 7 7
			 * 8 9 Hoort als output te geven
			 * 
			 * Parameter Estimates
			 * 
			 * Parameter Standard Variable DF Estimate Error t Value Pr > |t|
			 * 
			 * Intercept 1 -2.02354 0.55475 -3.65 0.0676 x1 1 0.88230 0.21837
			 * 4.04 0.0561 x2 1 0.14077 0.17352 0.81 0.5024 x3 1 0.17750
			 */
			// 
			// print the comparision with what should come out of this
			Assert.assertEquals(Math.abs(coef[0] + 2.02354) < 1E-4, true);
			Assert.assertEquals(Math.abs(coef[1] - 0.88230) < 1E-4, true);
			Assert.assertEquals(Math.abs(coef[2] - 0.14077) < 1E-4, true);
			Assert.assertEquals(Math.abs(coef[3] - 0.17750) < 1E-4, true);

			coef2 = E1.weightedRegression(y, x, w2);
			// print the comparision with what should come out of this

			Assert.assertEquals(Math.abs(coef[0] + 2.02354) < 1E-4, true);
			Assert.assertEquals(Math.abs(coef[1] - 0.88230) < 1E-4, true);
			Assert.assertEquals(Math.abs(coef[2] - 0.14077) < 1E-4, true);
			Assert.assertEquals(Math.abs(coef[3] - 0.17750) < 1E-4, true);
			log.fatal("end test regression ");

			E1.estimateModelParametersForSingleAgeGroup(100, testdata, 0, 0,
					dsi);
			log.fatal("end estimation categorical risk factor ");

			// test continuous risk factor
			testdata.setRiskType(2);
			ModelParameters E2 = new ModelParameters(baseDir);
			E2.estimateModelParametersForSingleAgeGroup(100, testdata, 0, 0,
					dsi);

			log.fatal("end estimation continuous risk factor ");

			float[] hulp = { 0, 0, 0, 0, 0 };
			testdata.getClusterData()[0][0][2].setPrevalence(hulp);
			E1.estimateModelParametersForSingleAgeGroup(100, testdata, 0, 0,
					dsi);
			log.fatal("end estimation factor with all prevalence disease=0 ");
			/*
			 * // test compound risk factor testdata.riskType = 3;
			 * ModelParameters E3 = new ModelParameters();
			 * E3.estimateModelParameters(100,testdata,0,0); / next test: no
			 * mortality through other diseases; then other cause = total cause
			 */
			/*
			 * log.fatal("end estimation compound risk factor ");
			 */

			testdata.getClusterData()[0][0][0].getExcessMortality()[0] = 0;
			testdata.getClusterData()[0][0][1].getExcessMortality()[0] = 0;
			testdata.getClusterData()[0][0][0].getCaseFatality()[0] = 0;
			testdata.getClusterData()[0][0][1].getCaseFatality()[0] = 0;
			testdata.getClusterData()[0][0][0].getRelRiskDuurBegin()[0] = 1;
			testdata.getClusterData()[0][0][1].getRelRiskDuurBegin()[0] = 1;
			testdata.getClusterData()[0][0][0].getRelRiskDuurEnd()[0] = 1;
			testdata.getClusterData()[0][0][1].getRelRiskDuurEnd()[0] = 1;
			testdata.getClusterData()[0][0][0].getRelRiskCat()[1][0] = 1;
			testdata.getClusterData()[0][0][0].getRelRiskCat()[2][0] = 1;
			testdata.getClusterData()[0][0][1].getRelRiskCat()[1][0] = 1;
			testdata.getClusterData()[0][0][1].getRelRiskCat()[2][0] = 1;

			for (int i = 0; i < 5; i++) {
				testdata.getClusterData()[0][0][2].getExcessMortality()[i] = 0;
				testdata.getClusterData()[0][0][2].getCaseFatality()[i] = 0;
				testdata.getClusterData()[0][0][2].getRelRiskDuurBegin()[i] = 1;
				testdata.getClusterData()[0][0][2].getRelRiskDuurEnd()[i] = 1;
				testdata.getRelRiskDuurMortEnd()[0][0] = 1.1F; // also=1 for
																// testing that
																// end = fixed
																// value
				testdata.getRelRiskDuurMortBegin()[0][0] = 1.1F; // also=1 for
																	// testing
																	// that
																	// begin=fixed
																	// value
				testdata.getClusterData()[0][0][2].getRelRiskCat()[1][i] = 1;
				testdata.getClusterData()[0][0][2].getRelRiskCat()[2][i] = 1;
			}
			ModelParameters E3a = new ModelParameters(baseDir);
			// E3a.estimateModelParameters(100, testdata,0,0);
			log.debug("relRiskOtherMortEnd "
					+ E3a.getRelRiskOtherMortEnd()[0][0]
					+ " relRiskOtherMortBegin "
					+ E3a.getRelRiskOtherMortBegin() + " alfaOtherMort "
					+ E3a.getAlphaOtherMort()[0][0]);

			testdata.getClusterData()[0][0][0].setPrevalence(0);
			// E1.estimateModelParameters(100,testdata,0,0);
			log.fatal("end estimation with prevalence disease 0 =0 ");

			testdata.getClusterData()[0][0][1].setPrevalence(0);

			InputData Testdata2 = new InputData();
			Testdata2.makeTest2Data();
			ModelParameters E4 = new ModelParameters(baseDir);
			E4.estimateModelParametersForSingleAgeGroup(100, Testdata2, 0, 0,
					dsi);

			// print comparison with what should come out of it
			System.out.println("test resultaten 2 ziekte zonder casefat: "
					+ E4.getBaselineIncidence()[0][0][0] + "= ? "
					+ E4.getBaselineIncidence()[0][0][1] + "= ? "
					+ E4.getBaselinePrevalenceOdds()[0][0][0] + "=0.117647 ? "
					+ E4.getBaselinePrevalenceOdds()[0][0][1] + "=0.117647 ? "
					+ E4.getRelRiskOtherMort()[0][0][0] + "= ? "
					+ E4.getRelRiskOtherMort()[0][0][1] + "= ? "
					+ E4.getBaselineOtherMortality() + "= ? "
					+ E4.getAttributableMortality()[0][0][0] + "= ? "
					+ E4.getAttributableMortality()[0][0][1] + "= ? ");
			Testdata2.getClusterData()[0][0][0].getCaseFatality()[0] = 0;
			Testdata2.getClusterData()[0][0][0].getCaseFatality()[1] = 0.5F;
			ModelParameters E5 = new ModelParameters(baseDir);
			E5.estimateModelParametersForSingleAgeGroup(100, Testdata2, 0, 0,
					dsi);
			// print comparison with what should come out of it
			System.out.println("test resultaten 2 ziekte with casefat: "
					+ E4.getBaselineIncidence()[0][0][0] + "= ? "
					+ E4.getBaselineIncidence()[0][0][1] + "= ? "
					+ E4.getBaselinePrevalenceOdds()[0][0][0] + "=0.117647 ? "
					+ E4.getBaselinePrevalenceOdds()[0][0][1] + "=0.117647 ? "
					+ E4.getRelRiskOtherMort()[0][0][0] + "= ? "
					+ E4.getRelRiskOtherMort()[0][0][1] + "= ? "
					+ E4.getBaselineOtherMortality() + "= ? "
					+ E4.getAttributableMortality()[0][0][0] + "= ? "
					+ E4.getAttributableMortality()[0][0][1] + "= ? ");

		} catch (Exception e) {
			if (e.getMessage() == null)
				System.err.println(e.getClass().getName()
						+ " trown without message.");
			else
				System.err.println(e.getClass().getName()
						+ " trown with message: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

}
