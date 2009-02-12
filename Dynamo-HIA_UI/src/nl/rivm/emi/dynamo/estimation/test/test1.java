package nl.rivm.emi.dynamo.estimation.test;

import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.InputDataFactory;
import nl.rivm.emi.dynamo.estimation.ModelParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/*  test 1 tests several components of the program */

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

		
	/* to test the calculation of rates from median survival */
		
		InputDataFactory E2 = new InputDataFactory ("simulation1");
		float [][] rate;
		float [][] medianSurvival = new float [96][2];
		for (int g = 0; g < 2; g++)
		for (int a = 0; a <96; a++)
		{ medianSurvival[a][g]=1.4835F ;}
		medianSurvival[94][0]=1.5344F;
		medianSurvival[93][0]=2.4375F;
		medianSurvival[92][0]=3.395F;
	   rate=E2.excessRate(medianSurvival, null);
		System.out.println("test resultaten rate calculation" + rate[92][0]
		   				+ "= 0.019859172 " + rate[93][0] + " = 0.045275774 " + rate[94][0]
		                                               				+ " = 0.4434549 " + rate[95][0] + " =  0.46723774");
		
		
	} catch (Exception e) {
		System.err.println(e.getMessage());

	}
}


}
