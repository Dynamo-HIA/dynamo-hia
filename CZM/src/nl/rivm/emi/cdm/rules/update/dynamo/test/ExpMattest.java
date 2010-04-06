package nl.rivm.emi.cdm.rules.update.dynamo.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.dynamo.MatrixExponential;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;


public class ExpMattest {
	Log log = LogFactory.getLog(getClass().getName());
	@Before
	public void setup() {
		log.fatal("Starting test. ");
	}
	
	@After
	
	public void teardown() {
		log.fatal("Starting test. ");
	}
	
	@Test
	public void test() {
		
		double [][] inArray={{-0.2,-0,0,0},{0.1,-0.11,0,0},{0,0.1,-0.11,0},{0,0.1,0.1,-0.02}};
		MatrixExponential m=new MatrixExponential();
		double[][] outArray;
		try {
			outArray = m.exponentiateMatrix(inArray);
		
		for (int i=0; i<4;i++)
		log.fatal("outArray "+i+" : "+outArray[i][0]+" : "+outArray[i][1]+" : "+outArray[i][2]+" : "+outArray[i][3] );
		/* the answer should be:
		{{0.818731, 0., 0., 0.}, {0.0856704, 0.895834, 0., 0.}, {0.00434777, 
			  0.0895834, 0.895834, 0.}, {0.00463156, 0.098355, 0.0937384, 
			  0.980199}}
			  */
		
		double [][] inArray2={{-0.045, 0, 0, 0}, {0.017, -0.058, 0, 0}, {0.028, 0, -0.0506, 0}, {0,
			   0.028, 0.0306, -0.05}};
		
		outArray=m.exponentiateMatrix(inArray2);
		for (int i=0; i<4;i++)
			log.fatal("outArray "+i+" : "+outArray[i][0]+" : "+outArray[i][1]+" : "+outArray[i][2]+" : "+outArray[i][3] );
		for (int i=1; i<11;i++){
		m.setNIterations(i);
		outArray=m.exponentiateMatrix(inArray2);
		log.fatal("n iterations in matrix exponential"+i);
		for (int k=0; k<4;k++)
			log.fatal("outArray "+k+" : "+outArray[k][0]+" : "+outArray[k][1]+" : "+outArray[k][2]+" : "+outArray[k][3] );
		}
		
		} catch (CDMUpdateRuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
