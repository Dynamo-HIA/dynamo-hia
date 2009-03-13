package nl.rivm.emi.dynamo.estimation.test;



	import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.InputDataFactory;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

	public class TestInputDataFactory {
		
		
		
		
	
		Log log = LogFactory.getLog(getClass().getName());
		
		String baseDir; //="c:/hendriek/java/dynamohome";
		
		String simulationName="simulation1";

		@Before
			public void setup() throws DynamoConfigurationException {
			baseDir = BaseDirectory.
			getInstance("c:\\hendriek\\java\\dynamohome\\").getBaseDir();
			}

		@After
		public void teardown() {
		}

		@Test
		public void runConfiguration() {
			
			try {
				log.fatal("Starting test");

					
					InputDataFactory config= new InputDataFactory(simulationName, 
							baseDir);
					InputData i=new InputData();
					InputData i2=new InputData();
					i2.makeTest2Data();
					ScenarioInfo s =new ScenarioInfo();
					log.fatal("overall configuration read");
					config.addPopulationInfoToInputData(simulationName,i,s);
					log.fatal("population info added");
					config.addRiskFactorInfoToInputData(i,s);
					log.fatal("risk factor info added");
					config.addDiseaseInfoToInputData(i,s);

					log.fatal("disease info added");
				
						// add risktype
					
					log.fatal("test complete");
					
					assertSame(i.getRiskType(),i2.getRiskType());
					log.fatal("risk factor riskType OK");
					
					assertSame(i.getClusterStructure(),i2.getClusterStructure());
					log.fatal("clusterStructure OK");
					
					assertSame(i.getPrevRisk(),i2.getPrevRisk());
					log.fatal("risk factor prevalence OK");
					
					assertSame(i.getClusterData(),i2.getClusterData());
					log.fatal("clusterdata OK");
					assertSame(i.getPrevRisk(),i2.getPrevRisk());
					log.fatal("risk factor prevalence OK");
					assertSame(i.getIndexDuurClass(),i2.getIndexDuurClass());
					log.fatal("indexDuurClass OK");
					assertSame(i.getMeanRisk(),i2.getMeanRisk());
					log.fatal("risk factor mean OK");
					
					// TODO test also other elements of input data and test scenario
					
					
				    
					
					
					
					
				} catch (DynamoConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					assertNull(e); // Force error.
				} catch (Exception e1) {
					e1.printStackTrace();
					assertNull(e1); // Force error.
				}
		

		
	}


}
