package nl.rivm.emi.dynamo.estimation.test;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.estimation.Simplx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSimplex {	Log log = LogFactory.getLog(getClass().getName());

@Before
	public void setup() {
	log.fatal("Starting test. ");
}

@After
public void teardown() {
	log.fatal("Test completed ");
}

@Test
public void test() throws DynamoConfigurationException {
	//test: this should give the solution: eerste var: 15, tweede 22.5. maximum = -45
	//	double[][] augmented = { { 2.5, 5 ,1 ,0,150 }, {5,2,0,1,120 },{-1.5,-1,0,0,0 } };
	//	double [][]augmented = { {-1.5,-1,0,0,0 },{ 2.5, 5 ,1 ,0, 150 }, {5,2,0,1,120 } }; 
	//	double [][]augmented = { {0,-1.5,-1,0,0 },{ 150,2.5, 5 ,1 ,0 }, {120,5,2,0,1 } }; 
	
		// voorbeeld uit NR
		
		
		/*float [][]augmented = {{0,1,1,3,-0.5f,0,0,0},
				                {740,-1,0,-2,0,-1,0,0},
				                {0,0,-2,0,7,0,-1,0},
				                {0.5f,0,-1,1,-2,0,0,1},
				                {9,-1,-1,-1,-1,0,0,0}
		};
		// outcome should be:
		// z 17.03 -0.95 -0.05 -1.05 ...
		// x2 3.33 -0.35 -0.15 0.35 ...
		// x3 4.73 -0.55 0.5 -0.45 ...
		// x4 0.95 -.10 0.10 0.10 ...
		// y1 730.55 0.10 -0.10 0.90 ...
		Simplx result= new Simplx(augmented,4,4,2,1,1);
		/* m,n,m1,m2,m3 */
		double[][]augmented = {{0,2,-4},{2,-6,1},{8,3,-4}};
		// outcome should be x1=0 x2=3.33 x4=4.73 en x4=0.95 */
		
		Simplx result= new Simplx(augmented,2,2,0,0,2);
		System.out
		.println(result.getA()[1][1]+" "+ result.getA()[1][2]+" "+result.getA()[1][3]+" "+result.getA()[2][1]+" "+
				result.getA()[2][2]+" "+result.getA()[2][3]+" "+
				result.getA()[3][1]+" "+result.getA()[3][2]+" "+result.getA()[3][3]);
		
		// iposv geeft de nummers van variabelen die niet nul zijn in de oplossing
		// bij gehorende antwoord staat in a[j+1][1] (+1 vanwege z in eerste rij)
// izrow geeft variabelen die 0 zijn geworden //
	//	De nummering is daarbij: eerst variabelen (boven aan de tabel) , daarna constraints (rijen)
		
		System.out
		.println(" var zrow " + result.getIzrov()[1] + " var zrow " + result.getIzrov()[2] 
			+	"var posv " + result.getIposv()[1]+"var posv " + result.getIposv()[2] );
		
		/* test transitiekansen 
		a = [0.7304662, 0.2169622, 0.05257154]
				b = [0.7189476, 0.2255193, 0.05553312] */
		float [] poud={0.7304662F, 0.2169622F, 0.05257154F} ;
		float [] pnew={0.7189476F, 0.2255193F, 0.05553312F} ;
		@SuppressWarnings("unused")
		float [] hulp = new float[3];
		
		double[][]augmented2 = {{0,        3,  2,  0,  2,  3,  2,  0,  2,  3},
		                       {poud[0], -1, -1, -1,  0,  0,  0,  0,  0,  0},
		                       {poud[1],  0,  0,  0, -1, -1, -1,  0,  0,  0},
		                       {poud[2],  0,  0,  0,  0,  0,  0, -1, -1, -1},
		                       {pnew[0], -1,  0,  0, -1,  0,  0, -1,  0,  0},
		                       {pnew[1],  0, -1,  0,  0, -1,  0,  0, -1,  0},
		                       {pnew[2],  0,  0, -1,  0,  0, -1,  0,  0, -1}};
		Simplx result2= new Simplx(augmented2,6,9,0,0,6);
		System.out
		.println(" var posv " + result2.getIposv()[1] + " var posv " + result2.getIposv()[2] 
		      +	"var posv " + result2.getIposv()[3]+"var posv " + result2.getIposv()[4] 
			+	"var posv " + result2.getIposv()[5]+"var posv " + result2.getIposv()[6] ); 
		System.out
		.println(" results  1 " + result2.getA()[2][1] + " 2 " + result2.getA()[3][1]
		      +	" 3 " + result2.getA()[4][1]+" 4 " + result2.getA()[5][1]
			+	" 5 " + result2.getA()[6][1]+" 6 " + result2.getA()[7][1] ); 

}
}