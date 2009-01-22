package nl.rivm.emi.dynamo.estimation.test;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import nl.rivm.emi.dynamo.datahandling.BaseDirectory;
import nl.rivm.emi.dynamo.datahandling.ConfigurationFileData;
import nl.rivm.emi.dynamo.datahandling.DynamoConfigurationData;
import nl.rivm.emi.dynamo.estimation.DynamoLib;
import nl.rivm.emi.dynamo.estimation.InitialPopulationFactory;
import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.ModelParameters;
import nl.rivm.emi.dynamo.estimation.SimulationConfigurationFactory;
import nl.rivm.emi.dynamo.estimation.XMLBaseElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestXMLwriter {
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
	

	try {

		

	
		ModelParameters p=new ModelParameters();
		InputData i=new InputData();
		i.makeTest1Data();
		p.estimateModelParameters(100, i);
		log.fatal("ModelParameters estimated ");
		p.setRiskType(1);
		String baseDir=BaseDirectory.getInstance("c:\\hendriek\\java\\dynamohome\\").getBaseDir();
		String simName="testsimulation";
		SimulationConfigurationFactory s=new SimulationConfigurationFactory(simName);
		
	   // DynamoConfigurationData d= new DynamoConfigurationData(BaseDirectory.getBaseDir());
		s.manufactureSimulationConfigurationFile(p);
		log.fatal("SimulationConfigurationFile written ");
		s.manufactureCharacteristicsConfigurationFile(p)  ;
		log.fatal("CharacteristicsConfigurationFile written ");
		s.manufactureUpdateRuleConfigurationFiles(p);
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


