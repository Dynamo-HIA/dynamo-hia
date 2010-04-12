package nl.rivm.emi.dynamo.estimation.test;


import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.ModelParameters;
import nl.rivm.emi.dynamo.estimation.SimulationConfigurationFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class testXMLwriter {
	Log log = LogFactory.getLog(getClass().getName());
	
	String baseDir;
	
@Before
	public void setup() {
	log.fatal("Starting test. ");
	baseDir = BaseDirectory.
		getInstance("c:\\hendriek\\java\\dynamohome\\").getBaseDir();
}

@After
public void teardown() {
	log.fatal("Test completed ");
}

@Test
public void test() {

	// main conducts a set of tests for testing the different parts

	// data to test the regression
	

	try {
		ModelParameters p=new ModelParameters(baseDir);
		InputData i=new InputData();
		i.makeTest1Data();
// 20100412		p.estimateModelParameters(100, i, null);
		p.estimateModelParameters(100, i);
		log.fatal("ModelParameters estimated ");
		p.setRiskType(1);
		
		String simName="testsimulation";
		SimulationConfigurationFactory s=new SimulationConfigurationFactory(simName);
		
	   // DynamoConfigurationData d= new DynamoConfigurationData(BaseDirectory.getBaseDir());
		s.manufactureSimulationConfigurationFile(p, null);
		log.fatal("SimulationConfigurationFile written ");
		s.manufactureCharacteristicsConfigurationFile(p)  ;
		log.fatal("CharacteristicsConfigurationFile written ");
		s.manufactureUpdateRuleConfigurationFiles(p, null);
		log.fatal("UpdateRuleConfigurationFile written ");
		/*
		InitialPopulationFactory e2=new InitialPopulationFactory();
		File f=new File("c:/hendriek/java/workspace/dynamo/dynamoinput/test.xml");
		
		XMLBaseElement element=new XMLBaseElement();
		ConfigurationFileData  example = new ConfigurationFileData();
		example.setXmlGlobalTagName("example");
	    for(int i = 0; i < 7; i++){
	    	element=new XMLBaseElement();
	    	element.setTag("Tag"+i);
	        element.setValue(i);
	        example.add(element);}

		example.writeToXMLFile(example,f);
		//E2.writeInitialPopulation(E1,10,"c:/hendriek/java/workspace/dynamo/dynamoinput/initial");
		// test weighted regression

		*/
	} catch (Exception e) {
		if (e.getMessage()==null)
		System.err.println(" some form of unknown exception thrown");
		else System.err.println(e.getMessage());

	}
}


}


