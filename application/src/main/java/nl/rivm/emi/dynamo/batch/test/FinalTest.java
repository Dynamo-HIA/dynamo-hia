package nl.rivm.emi.dynamo.batch.test;


/**
 * 
 */

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
import nl.rivm.emi.dynamo.estimation.InputDataFactory.ScenInfo;
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
 * This unit test contains the complete battery of tests for DYNAMO-HIA
 * 
 */

 
public class FinalTest {
	/**
	 * @throws java.lang.Exception
	 */
	Log log = LogFactory.getLog(getClass().getName());
	String[] files = new String[1];

	/*
	 * 
	 * 
	 * NB the base directory needs to be put in by hand !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * 
	 * 
	 */
	
	
	
	String baseDir = "C:\\HENDRIEK\\TESTDATA voor versie 2";

	ArraysFromXMLFactory factory = new ArraysFromXMLFactory();

	@Before
	public void setUp() throws Exception {

		// files[0]="C:\\DYNAMO-HIA\\TESTDATA\\temp";

	}

	// this test tests against previous versions of DYNAMO
	// not necessarily correct
	// but so differences will be noted
	
	// in the interface : sullivan oud = 67.832, le is 70.304 = idem aan oude versie
	
	// hier echter 77.31025037823959, 9.476103206219452
	// nee, het is 77.3040717 en 9.4723516298 in nieuwe versie van oud
	
	
	@Test
	public void test_compareOld_1() {
		 baseDir = "C:\\Hendriek\\Tutorial_DATA";
		 runTest("run_test_1","tutorial_3", 77.90907195, 68.7166646, 77.3040717,
				 67.83172007,	71.615427227, 70.85643596, 0, false);
		 runTest("run_test_1","tutorial_3", 77.64293668, 68.050949, 77.251881858,
				 67.7928535, 71.24306821,70.81152774,1, false);
		
		 

	     
	} 
	
	
	/*
	 * this test is  testing if results are the same as in DYNAMO-2. This is meant for future versions, and to check that
	 * bugfixing does not change the results
	 * For testing DYNAMO-2 , this does not have any intrinsic value, as results are taken from the program itself
	 */
	
	@Test
	public void test_compareOld_2() {
		 baseDir = "C:\\Hendriek\\Tutorial_DATA";
	     runTest("run_test_1","tutorial_3", 77.90907195, 68.7166646,  77.3040717,
		 68.0727927,
		 71.615427227, 70.96645352, 0, true);
		 runTest("run_test_1","tutorial_3", 77.64293668, 68.050949, 77.251881858,
				 68.03363493, 71.24306821,70.92141455,1, true);


	     
	} 
	
	
	
	
	/*
	 * this test is the same as the previous version, but with larger number in the simulation, so it will test the other running method
	 */
	
	@Test
	public void test_compareOld_3() {
		 baseDir = "C:\\Hendriek\\Tutorial_DATA";
	     runTest("run_test_2","tutorial_3", 77.90907195, 68.7166646,  77.3040717,
		 68.0727927,
		 71.615427227, 70.96645352, 0, true);
		 runTest("run_test_2","tutorial_3", 77.64293668, 68.050949, 77.251881858,
				 68.03363493, 71.24306821,70.92141455,1, true);


	     
	} 
	
	
	
	/*
	/* Oorsprong van deze test is niet helemaal duidelijk
	 Hij werkt niet meer omdat de data zoek zijn

	 */
	
	/*
	@Test
	public void test_0() {
		 baseDir = "C:\\DYNAMO-HIA\\Country_data\\Netherlands";
	     runTest("simulation1.txt", "simulation1", 76.309470706, 76.309470706, 77.4402140007,77.4402140007,		 
	    		 68.678526364837,  77.4402140007-7.7440186306, -1, true);
	
	} 
	*/
	
	@Test
	public void test_1() {
		 baseDir = "C:\\HENDRIEK\\TESTDATA voor versie 2";
	     runTest("run_test_1", "test01", 11.83285, 6.95871, 10.064339,
		 7.263274,
		 0, 0, -1, true);
	} 

	@Test
	public void test_1a() {
	    runTest("run_test_1a", "test01a", 11.83285, 6.95871, 10.064339,
			 7.263274,
			 0, 0, 0, true);
	}

	@Test
	public void test_2() {
		runTest("run_test_2", "test02", 11.84868, 7.33809, 10.064933,
		 7.517412,
		 0, 0,-1, true);
	}
	

	@Test
	public void test_3() {
		runTest("run_test_3", "test03", 11.62399547, 8.426732, 10.05240022,
		 7.25465746,
		 10.449351, 9.047160194, -1, true);
		 checkIncidence("test03",0.008453573,0.015406758);
	
	}
	
	/* deze test met grotere aantallen zodat de andere runmethode wordt gebruikt */

	@Test
	public void test_3a() {
		runTest("run_test_3a", "test03a", 11.62399547, 8.426732, 10.05240022,
		 7.25465746,
		 10.449351, 9.047160194, -1, true);
		
	
	}



//11.64373307	8.627173	10.510574	10.430651
//10.05454054	7.509649749	9.04174418	9.04908649

	@Test
	public void test_4() {
		 runTest("run_test_4", "test04", 11.64373307, 8.627173, 10.05454054,
		 7.509649749,
		 10.430651, 9.04908649, -1, true);
		 checkIncidence("test04",0.008454043,0.01543499);
		
	}

	@Test
	public void test_5() {
		runTest("run_test_5", "test05", 11.86143824, 10.319618, 10.06416548,
				 9.057748055,
				 10.541925, 9.057748935, -1, true); 
		checkIncidence("test05",0.004851236,0.003234157);
		//0.003234157	0.004851236

	}

	@Test
	public void test_6() {
		runTest("run_test_6", "test06", 10.94401469, 7.098233, 10.44058873,
		 7.572985171,
		 9.666941, 9.414778818, -1, true); 
		checkIncidence("test06",0.008081647,0.015395727);
	}

	@Test
	public void test_7() {
		 runTest("run_test_7", "test07", 11.02311413, 7.387165, 10.44525728,
		 7.846585711,
		 9.721771, 9.420975178, -1, true);
		// 11.02311413	7.387165	9.750942	9.721771;
		// 10.44525728	7.846585711	9.416530981	9.420975178;

		 checkIncidence("test07",0.008081923,0.015208242);

	}

	@Test
	public void test_8() {
		runTest("run_test_8", "test08", 11.6627042, 7.363732, 11.37419777,
		 8.37765101,
		 10.225445, 10.3010073, -1, true);
		 checkIncidence("test08",0.007381388,	0.014992776);

	}

	@Test
	public void test_9() {
		runTest("run_test_9", "test09", 11.70370647, 8.183877, 11.3731399,
		 8.667465314,
		 10.469886, 10.30456292, -1, true);
		// als test 6,7,en 8 werken dan zal de incidentie voor 9 ook goed worden berekend
		// daarom niet extra getest
	}

	@Test
	public void test_10() {
		  runTest("run_test_10", "test10", 11.09238588, 9.669386, 10.4396114,
					 9.447112375,
					 9.865490, 9.414949636, -1, true);
		  // volgorde ziekten net andersom als in excel sheet
		  checkIncidence("test10", 	0.004684144 , 0.003122763);

	}

	@Test
	public void test_11() {
		 runTest("run_test_11", "test11", 11.62399547, 8.426732, 10.05240022,
		 7.25465746,
		 10.449351, 9.047160194, -1, true);
		// volgorde andersom als in excel 
		 checkIncidence("test11",  0.015406758,0.008453573	);

	}

	@Test
	public void test_11a() {
		 runTest("run_test_11a", "test11a", 11.62399547, 8.426732, 10.05240022,
				 7.25465746,
				 10.449351, 9.047160194, -1, true);
		 
		 //0.008453573	0.015406758;
// volgorde andersom als in excel 
		 checkIncidence("test11a",  0.015406758,0.008453573	);
	}

	@Test
	public void test_12() {
		 runTest("run_test_12", "test12", 11.64373307, 8.627173, 10.05454054,
		 7.509649749,
		 10.430651, 9.04908649, -1, true); 
		// volgorde andersom als in excel 
		// 0.008454043	0.01543499
  // is 0.015886
		 checkIncidence("test12", 	0.01543499,0.008454043);

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
			 10.541925, 9.057748935,-1, true); 
	 
	 checkIncidence("test13",0.004851234,	0.003234156	);

	}
	@Test
	public void test_14() {
		runTest("run_test_14", "test14", 9.955002415, 7.037181, 10.00184631,
				 7.218174589,
				 8.903619, 9.001661675, -1, true); 
	}

	@Test
	public void test_15() {
		 runTest("run_test_15", "test15", 9.957494346, 7.259818, 10.00333218,
				 7.47140266,
				 8.895412, 9.002998958, -1, true); 
	}
	
	
	
	

	@Test
	public void test_16() {
		 runTest("run_test_16", "test16",  11.64373307, 8.627173, 10.05454054,
				 7.509649749,
				 10.430651, 9.04908649, 0, true);
		 runTest("run_test_16", "test16",   19.93239363,16.297425,18.69126678	,14.01561423,
							 18.278857, 16.8382229, 1, true);
		 runTest("run_test_16", "test16",  19.93239363, 16.297425,18.69126678	,14.01561423,					
				 18.278857, 16.8382229, 2, true);
		//11.64373307	8.627173	10.516401	10.430651
		// 10.05454054	7.509649749	9.04908678	9.04908649




//19.93239363	16.297425	18.761242	18.278857
//18.69126678	14.01561423	17.28258941	16.8382229



		 
	}
	
	
	@Test
	public void test_16RR() {
		 runTest("run_test_16RR", "test16RR", 11.64373307, 8.627173,10.05454054,
				 7.509649749,10.516401,
				 9.04908649, 0, true); 
		 runTest("run_test_16RR", "test16RR",  19.93239363, 16.297425,18.69126678	,14.01561423,	
				
				 18.7612428, 17.28258941, 1,true);
		 runTest("run_test_16RR", "test16RR",  19.93239363, 16.297425,18.69126678	,14.01561423,	
					
				 18.7612428, 17.28258941, 2, true);
		 

	}
	
	@Test
	public void test_17() {
		runTest("run_test_17", "test17", 11.62399547, 8.426732, 10.05240022,
				 7.25465746,
				 10.449351, 9.047160194, -1, true);
	}
	@Test
	public void test_20() {
		runTest("run_test_20", "test20", 11.62399547, 8.426732, 10.05240022,
				 7.25465746,
				 10.449351, 9.047160194, -1, true);
	}
	

	
	/* this tests has only a single age, so no life expectancy measures */
	//private void checkDALY( String testName, double PYLL,double diseaseDaly,
		//	double disabilityDaly )
	
	
		@Test
	public void test_daly() { 
	 runTest("run_test_daly", "testDALY", 11.64373307, 8.627173, 0,
		 0,  10.430651, 0, 0, true);
		for (int scen=1; scen<3; scen++) runTest("run_test_daly", "testDALY", 19.93239363, 16.297425, 0,
				 0,  18.278857, 0, scen, true);
		 checkDALY("testDALY",672.01336,1229.3108,820.18175);
	}
	
/*  NB bij de RR testen is in de excel spreadsheets de situatie waarbij de RR leidt tot baseline ability>1 
 * niet altijd goed afgehandeld
 * wel in test 4 en 3 en 16
 *  */

	
	//11.62399399	8.426734	10.528738	10.449349
	//10.05240015	7.254658498	9.047160422	9.047160132


	@Test
	public void test_3RR() {
		 runTest("run_test_3RR", "test03RR", 11.62399547, 8.426732, 10.05240022,
		 7.25465746,
		 10.528738, 9.047160422, -1, true);
		 checkIncidence("test03RR",0.008453573,0.015406758);
	}
	
	
	  //11.64373307	8.627173	10.516401	10.430651
	//10.05454054	7.509649749	9.04908678	9.04908649
	@Test
	public void test_4RR() {
		runTest("run_test_4RR", "test04RR", 11.64373307, 8.627173,10.05454054,
		 7.509649749,10.516401,
		 9.04908678, -1, true);
		 checkIncidence("test04RR",0.008454043,0.01543499);
	}
	
	
	
	//11.64373307	8.627192	10.506169	10.430651
	//10.05454054	7.509649749	9.049086176	9.04908649

	@Test
	public void test_4RRa() {
		runTest("run_test_4RRa", "test04RRa", 11.64373307, 8.627173,10.05454054,
		 7.509649749,10.506169,
		 9.049086176, -1, true);
		 checkIncidence("test04RR",0.008454043,0.01543499);
	}
	

	@Test
	public void test_5RR() {
		runTest("run_test_5RR", "test05RR", 11.86143824, 10.319618, 10.06416548,
				 9.057748055,
				 10.704893, 9.057695401, -1, true); 
		checkIncidence("test05RR",0.004851236,0.003234157);
	}

	@Test
	public void test_6RR() {
		 runTest("run_test_6RR", "test06RR", 10.94401469, 7.098233, 10.44058873,
		 7.572985171,
		 9.696873, 9.410217584, -1, true); 
	}

	@Test
	public void test_7RR() {
		runTest("run_test_7RR", "test07RR", 11.02311413, 7.387165, 10.44525728,
		 7.846585711,
		 9.750942, 9.416530981, -1, true);
	}

	
	
	
	//11.6627043	7.363732	10.304471	10.225445
	//11.37419777	8.37765101	10.34283372	10.3010073

	@Test
	public void test_8RR() {
		runTest("run_test_8RR", "test08RR", 11.6627042, 7.363732, 11.37419777,
		 8.37765101,
		 10.304471, 10.34283372, -1, true);
	}

	@Test
	public void test_9RR() {
		runTest("run_test_9RR", "test09RR", 11.70370647, 8.183877, 11.3731399,
		 8.667465314,
		 10.545963, 10.3437661, -1, true);
	}

	@Test
	public void test_10RR() {
		 runTest("run_test_10RR", "test10RR", 11.09238588, 9.669386, 10.4396114,
					 9.447112375,
					 9.908268, 9.409510732, -1, true);
	}

	
	/* nb these simulations should give identical results as those for 
	 * test 3,4 and 5 , with exception of dale with RR for disability, as the RR is 2.5 here in stead of 3.5*/ 

	@Test
	public void test_11RR() {
		runTest("run_test_11RR", "test11RR", 11.62399399, 8.426751, 10.05240015,
		 7.254658498,
		 10.499432, 9.047160639, -1, true);
	}

	

	@Test
	public void test_12RR() {
		runTest("run_test_12RR", "test12RR", 11.64373307, 8.627193, 10.05454054,
				 7.509649749,
				 10.481500, 9.049086998, -1, true); 
	}
	
	
	@Test
	public void test_13RR() {
	runTest("run_test_13RR", "test13RR", 11.86143824, 10.319618, 10.06416548,
			 9.057748055,
			 10.660396, 9.057749524, -1, true); 
	}


	/* de volgende testen hebben een OR disability van 10, waardoor other
	 * disability boven de 1 komt in klasse 2, en daarvoor correctie nodig is
	 */

	@Test
	public void test_5RRa() {
		runTest("run_test_5RRa", "test05RRa", 11.86143824, 10.319618, 10.06416548,
				 9.057748055,
				 10.759947, 9.057688705, -1, true); 
		
	}
	
	@Test
	public void test_13RRa() {
	runTest("run_test_13RRa", "test13RRa", 11.86143824, 10.319618, 10.06416548,
			 9.057748055,
			 10.759947, 9.057688705, -1, true); 
	}
	


	private void checkParameters(String simName, float baselineIncidenceA,
			float baselineIncidenceB, float atmort1, float atmort2,
			float rrOMbegin, float rrOMend, float alphaOM,
			float baselineOtherMort, boolean dependent) {

		String parameterDir = baseDir + File.separator + "Simulations" + File.separator
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
					.abs(baselineIncidenceA - incA) < 1E-6);
			Assert.assertTrue(" baseline incidence disease B is " + incB
					+ "but should be " + baselineIncidenceB, Math
					.abs(baselineIncidenceB - incB) < 1E-6);
			Assert.assertTrue(" RR otherMort begin is " + RRbegin
					+ "but should be " + rrOMbegin, Math.abs(rrOMbegin
					- RRbegin) < 1E-5);
			Assert.assertTrue(" RR otherMort end is " + RRend
					+ "but should be " + rrOMend,
					Math.abs(rrOMend - RRend) < 1E-5);
			Assert.assertTrue(" baseline otherMort is " + baselineOM
					+ "but should be " + baselineOtherMort, Math
					.abs(baselineOtherMort - baselineOM) < 1E-6);

		} catch (DynamoConfigurationException e) {

			e.printStackTrace();
		}

	}
	
	
	
	
	private void checkIncidence( String testName, double annualIncidenceA,
			double annualIncidenceB) {


		
		DynamoOutputFactory output = getOutput(baseDir, testName);
		
	for (int scen=0;scen<=output.getNScen();scen++)	{	
	double incA = output.getNewCasesByAge(1)[scen][0][0][0]/output.getNPopByAge()[0][0][0][0];
	double incB = output.getNewCasesByAge(0)[scen][0][0][0]/output.getNPopByAge()[0][0][0][0];
	//double incA1 = output.getNewCasesByRiskClassByAge(1)[scen][0][1][0][0]/output.getNPopByRiskClassByAge()[0][0][1][0][0];
	//double incA2 = output.getNewCasesByRiskClassByAge(1)[scen][0][2][0][0]/output.getNPopByRiskClassByAge()[0][0][2][0][0];
	
			

			Assert.assertTrue(" incidence disease A for scenario "+scen+" is "
					+ incA + "but should be " + annualIncidenceA, Math
					.abs(incA - annualIncidenceA) < 1E-8);
			if (Math.abs(incA - annualIncidenceA) < 1E-8) log.fatal(" incidence A OK");
			Assert.assertTrue(" incidence disease B for scenario "+scen+" is "
					+ incB + "but should be " + annualIncidenceB, Math
					.abs(incB - annualIncidenceB) < 1E-8);
			if (Math.abs(incB - annualIncidenceB) < 1E-8) log.fatal(" incidence B OK");

	

	}}

	
	private void checkDALY( String testName, double PYLL,double diseaseDaly,
			double disabilityDaly ) {

		DynamoOutputFactory output = getOutput(baseDir, testName);
		
		
	double[][] dataScenDisab;
	double [][] dataRefDisab= output.getDisabilityDALY()[0];	;
	double [][] dataScenDisease;
	double [][] dataRefDisease=output.getTotDiseaseDALY()[0];;
	double[][] dataScenPYL;
	double[][] dataRefPYL=output.getPopDALY()[0];
	
	for (int scen=1;scen<=output.getNScen();scen++)	if (!output.getScenTrans()[scen-1]){	
		
		   double delDisease = 0;double delDisability = 0;double delPYL = 0;
			
		    dataScenPYL = output.getPopDALY()[scen];
			dataScenDisease = output.getTotDiseaseDALY()[scen];		
			dataScenDisab = output.getDisabilityDALY()[scen]; 
		/* the daly arrays contain the differences in disease years, not in diseasefree years */	
			
	         delDisease+=dataScenDisease[0][0]-dataRefDisease[0][0];
	         delDisability+=dataScenDisab[0][0]-dataRefDisab[0][0];
	         delPYL+=dataScenPYL[0][0]-dataRefPYL[0][0];

	         /* daly= [le-disle]  (scen) -[ le-disle] (ref scen]
	          * 
	          * = daly3 - dalyx
	          */
	         /* the numerical inaccuracy is in the 7th digit, so with values around 1000 5E-4 is an expected error
	          * for disability the error is even larger, maybe because of extra errors in the calculation of the disability figures 
	          * error in disability figures is 4E-9  because of more use of double 
	          * still total relative error is E-7 so acceptable */
	         
			Assert.assertTrue(" disease daly for scenario "+scen+" is "
					+ (delPYL-delDisease) + "but should be " + diseaseDaly, Math
					.abs( delPYL-delDisease- diseaseDaly) < 5E-5);
			Assert.assertTrue(" PYLL for scenario "+scen+" is "
					+ delPYL + "but should be " + PYLL, Math
					.abs( delPYL- PYLL) < 5E-4);
			Assert.assertTrue(" disability daly for scenario "+scen+" is "
					+ (delPYL-delDisability) + "but should be " + disabilityDaly, Math
					.abs( delPYL-delDisability- disabilityDaly) < 1E-4);
			
			
			
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

	 * @param DALEcohort
	 * @param DALESullivan
	 * @param scenno : scenario number to test; -1 = test all scenarios (should have the same values)

	 */
	private void runTest(String batchFileName, String testName,
			double LEcohort, double HLEcohort, double LEPeriod,

			double HLESullivan, double DALEcohort, double DALESullivan, int scenno, boolean newversion) {

		/* test 01 */

	//	String baseDir = "C:\\DYNAMO-HIA\\TESTDATA";
	//	String baseDir = "C:\\DYNAMO-HIA\\Sweden_SL";
		files[0] = baseDir + File.separator + batchFileName;
		log.fatal(testName + " started ");
		// if the scenario is larger than zero we assume that the output object has already been made
		//!!!!!!!! WARNING: remove if part when first scenario tested is not the reference scenario
	if (scenno <1)	Runner.main(files);
		log.fatal("runner has run");
		DynamoOutputFactory output = null;
		output = getOutput(baseDir, testName);
		ScenarioParameters scenParms = getScenParms(baseDir, testName);
		int nScenarios=scenParms.getSuccesPercentage().length;
		log.fatal("starting plotfactory");
		DynamoPlotFactory factory = new DynamoPlotFactory(output, scenParms);
		log.fatal("plotfactory ready");
		int beginscen=scenno;
		int endscen=scenno+1;
		if (scenno==-1) {beginscen=0; endscen=nScenarios;}
		 for (int scen=beginscen; scen<endscen; scen++){
			log.fatal( "checking scenario "+scen);
		if (HLEcohort != 0) {
			log.fatal("voor old");
		//	double[][] hle2 = factory.calculateCohortHealthExpectancyOld(0, scen, -1);
			log.fatal("na old");
			if (scen == 1 ){
				
				int i=0;
				i++;
			}
			double[][] hle = factory.calculateCohortHealthExpectancy(0, scen, -1);
			log.fatal("na new");
			Assert.assertEquals(" LE cohort for scenario "+scen+" is " + hle[0][0] + "but should be "
					+ LEcohort, true, Math.abs(hle[0][0] - LEcohort) < 1E-4);
			Assert.assertEquals(" HLE cohort for scenario "+scen+" is " + (hle[0][0] - hle[0][1])
					+ "but should be " + HLEcohort, true, Math.abs(hle[0][0]
					- hle[0][1] - HLEcohort) < 1E-4);
			
			
//			Assert.assertEquals(" LE cohort for scenario "+scen+" is " + hle[0][0] + " and fast is "
//					+ hle2[0][0], true, Math.abs(hle[0][0] - hle2[0][0]) < 5E-5);
//			Assert.assertEquals(" HLE cohort for scenario "+scen+" is " + hle[0][1] + " and fast is "
//					+ hle2[0][1], true, Math.abs(hle[0][1] - hle2[0][1]) < 5E-5);
			
			
			Assert.assertEquals(" HLE cohort for scenario "+scen+" is " + (hle[0][0] - hle[0][1])
					+ "but should be " + HLEcohort, true, Math.abs(hle[0][0]
					- hle[0][1] - HLEcohort) < 1E-4);
			
			 


			
		}
		log.fatal(" HLE cohort is OK ");
		if (HLESullivan != 0) {
			double[][] hle2 ;
			if (newversion)
			 hle2 = factory.calculateSullivanLifeExpectancy(0, 0, -1,
					scen);
			else hle2 = factory.calculateSullivanLifeExpectancy_old(0, 0, -1,
					scen);
	//		double[][] hle3 = factory.calculateSullivanLifeExpectancy_old(0, 0, -1,
	//				scen);
	//		double[][] hle4 = factory.calculateSullivanLifeExpectancy_old2(0, 0, -1,
	//				scen);
			Assert.assertTrue(" LE-Period for scenario "+scen+" is " + hle2[0][0] + "but should be "
					+ LEPeriod, Math.abs(hle2[0][0] - LEPeriod) < 1E-4);
			Assert.assertEquals(" HLE-Period for scenario "+scen+" is " + (hle2[0][0] - hle2[0][1])
					+ "but should be " + HLESullivan, true, Math.abs(hle2[0][0]
					- hle2[0][1] - HLESullivan) < 1E-4);
		}
		log.fatal(" HLE Sullivan is OK ");
		if (DALEcohort != 0) {
			double[][] hle3 = factory.calculateCohortHealthExpectancy(0, scen, -2);
			Assert.assertEquals(" LE-cohort for scenario "+scen+" is " + hle3[0][0]
					+ "but should be " + LEcohort, true, Math.abs(hle3[0][0]
					- LEcohort) < 1E-4);

			Assert.assertEquals(" DALE-cohort for scenario "+scen+" is " + (hle3[0][0] - hle3[0][1])
					+ "but should be " + DALEcohort, true, Math.abs(hle3[0][0]
					- hle3[0][1] - DALEcohort) < 1E-4);

		}
		log.fatal(" DALE cohort is OK ");
		if (DALESullivan != 0) {double[][] hle4 ;
			if (newversion)  hle4 = factory.calculateSullivanLifeExpectancy(0, 0, -2,
					scen);
			else hle4 = factory.calculateSullivanLifeExpectancy_old(0, 0, -2,
					scen);
			Assert.assertEquals(" LE-Period for scenario "+scen+" is " + hle4[0][0]
					+ "but should be " + LEPeriod, true, Math.abs(hle4[0][0]
					- LEPeriod) < 1E-4);

			Assert.assertEquals(" DALE-Period for scenario "+scen+" is " + (hle4[0][0] - hle4[0][1])
					+ "but should be " + DALESullivan, true, Math
					.abs(hle4[0][0] - hle4[0][1] - DALESullivan) < 1E-4);

		}
		log.fatal(" Dale sullivan is OK ");
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
