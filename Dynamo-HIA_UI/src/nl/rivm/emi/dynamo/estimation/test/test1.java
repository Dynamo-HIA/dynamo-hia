package nl.rivm.emi.dynamo.estimation.test;

import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.ModelParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class test1 {
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

	// main conducts a set of tests for testing the different parts

	// data to test the regression
	double y[] = { 1, 2, 3, 4, 5, 7 };
	double w[] = { 1, 1, 1, 1, 1, 1 };
	double w2[] = { 2, 2, 2, 2, 2, 2 };
	double x[][] = { { 1, 2, 3, 4 }, { 1, 3, 5, 5 }, { 1, 4, 7, 2 },
			{ 1, 5, 8, 3 }, { 1, 6, 7, 5 }, { 1, 7, 8, 9 } };
	double coef[] = { 0, 0, 0 };
	double coef2[] = { 0, 0, 0 };
	InputData Testdata = new InputData();

	try {

		// first test categorical variables

		ModelParameters E1 = new ModelParameters();

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
		System.out.println("test resultaten regressie: " + coef[0]
				+ "= -2.02354? " + coef[1] + " = 0.88230? " + coef[2]
				+ " = 0.14077 ? " + coef[3] + " = 0.17750?");

		coef2 = E1.weightedRegression(y, x, w2);
		// print the comparision with what should come out of this
		System.out.println("test resultaten regressie met w2: " + coef[0]
				+ "= -2.02354? " + coef[1] + " = 0.88230? " + coef[2]
				+ " = 0.14077 ? " + coef[3] + " = 0.17750?");

		// test continuous risk factor
		Testdata.riskType = 2;
/*		ModelParameters E2 = new ModelParameters(100, Testdata); */
		// test compound risk factor
		Testdata.riskType = 3;
	/*	ModelParameters E3 = new ModelParameters(100, Testdata);*/
		/* next test: no mortality through other diseases;
		 * then other cause = total cause
		 */
		
		for (int i=0;i<7;i++){
		Testdata.excessMortality[0][0][i]=0;
		Testdata.caseFatality[0][0][i]=0;
		Testdata.relRiskDuurBegin[0][0][i]=1;
		Testdata.relRiskDuurEnd[0][0][i]=1;
		Testdata.relRiskDuurMortEnd[0][0]=1.1; // also=1 for testing that end = fixed value
		Testdata.relRiskDuurMortBegin[0][0]=1.1;  // also=1 for testing that begin=fixed value
		Testdata.relRiskCat[0][0][1][i]=1;
		Testdata.relRiskCat[0][0][2][i]=1;}
		ModelParameters E3a = new ModelParameters();
		E3a.estimateModelParameters(100, Testdata,0,0);
		log.debug("relRiskOtherMortEnd "+E3a.relRiskOtherMortEnd[0][0]+" relRiskOtherMortBegin "
				+E3a.relRiskOtherMortBegin
				+" alfaOtherMort "+E3a.alfaOtherMort[0][0]);
		
		InputData Testdata2 = new InputData();
		Testdata2.makeTest2();
		ModelParameters E4 = new ModelParameters();
		estimateModelParameters(100, Testdata2,0,0);

		// print comparison with what should come out of it
		System.out.println("test resultaten 2 ziekte zonder casefat: "
				+ E4.baselineIncidence[0][0][0] + "= ? "
				+ E4.baselineIncidence[0][0][1] + "= ? "
				+ E4.baselinePrevalenceOdds[0][0][0] + "=0.117647 ? "
				+ E4.baselinePrevalenceOdds[0][0][1] + "=0.117647 ? "
				+ E4.relRiskOtherMort[0][0][0] + "= ? " + E4.relRiskOtherMort[0][0][1]
				+ "= ? " + E4.baselineOtherMortality + "= ? "
				+ E4.attributableMortality[0][0][0] + "= ? "
				+ E4.attributableMortality[0][0][1] + "= ? ");
		Testdata2.caseFatality[0][0][0] = 0;
		Testdata2.caseFatality[0][0][1] = 0.5;
		ModelParameters E5 = new ModelParameters();
		estimateModelParameters(100, Testdata2,0,0);
		// print comparison with what should come out of it
		System.out.println("test resultaten 2 ziekte with casefat: "
				+ E4.baselineIncidence[0][0][0] + "= ? "
				+ E4.baselineIncidence[0][0][1] + "= ? "
				+ E4.baselinePrevalenceOdds[0][0][0] + "=0.117647 ? "
				+ E4.baselinePrevalenceOdds[0][0][1] + "=0.117647 ? "
				+ E4.relRiskOtherMort[0][0][0] + "= ? " + E4.relRiskOtherMort[0][0][1]
				+ "= ? " + E4.baselineOtherMortality + "= ? "
				+ E4.attributableMortality[0][0][0] + "= ? "
				+ E4.attributableMortality[0][0][1] + "= ? ");

	} catch (Exception e) {
		System.err.println(e.getMessage());

	}
}


}