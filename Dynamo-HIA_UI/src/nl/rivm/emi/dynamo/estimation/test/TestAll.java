package nl.rivm.emi.dynamo.estimation.test;

 
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.dynamo.datahandling.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynamoOutputFactory;
import nl.rivm.emi.dynamo.estimation.InitialPopulationFactory;
import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.ModelParameters;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;
import nl.rivm.emi.dynamo.estimation.SimulationConfigurationFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAll {
	Log log = LogFactory.getLog(getClass().getName());
	
	String baseDir = BaseDirectory.getInstance(
			"c:\\hendriek\\java\\dynamohome\\").getBaseDir();

	// NB de directory moet ook worden aangepast in deze file //
	File simulationConfigurationFile = new File(baseDir + "simulation_hb.xml");

	File simOutput = new File(baseDir + "\\output\\sim_out.xml");
	String simName = "testsimulation";
	String directoryName = baseDir + "Simulations\\" + simName;
	String preCharConfig = directoryName + "\\modelconfiguration"
			+ "\\charconfig.XML";

	String simFileName = directoryName + "\\modelconfiguration"
			+ "\\simulation.XML";
	String simFileName2 = directoryName + "\\modelconfiguration"
	+ "\\simulation_scen_1.XML";
	/* LET OP: de factory voor initial population voegt zelf XML toe */
	String popFileName = directoryName + "\\modelconfiguration"
			+ "\\population";
	String popFileName2 = directoryName + "\\modelconfiguration"
	+ "\\population_scen_1";

	HierarchicalConfiguration simulationConfiguration;
	Simulation sim;
	ModelParameters p;

	@Before
	public void setup() {
		log.fatal("Starting test. ");

		System.out.println(preCharConfig);
		try {
			p = new ModelParameters();
			InputData i = new InputData();
			i.makeTest1Data();
			// NB is testdata=1 then also scenario info=1: 2x veranderen!!
			//i.makeTest2Data();
			p.estimateModelParameters(100, i);
			log.fatal("ModelParameters estimated ");
			p.setRiskType(1);
			ScenarioInfo scenInfo=new ScenarioInfo();
			// scenInfo.makeTestData();
			 
			 scenInfo.makeTestData1();
			SimulationConfigurationFactory s = new SimulationConfigurationFactory(
					simName);

			// DynamoConfigurationData d= new
			// DynamoConfigurationData(BaseDirectory.getBaseDir());
			s.manufactureSimulationConfigurationFile(p,scenInfo);
			log.fatal("SimulationConfigurationFile written ");
			s.manufactureCharacteristicsConfigurationFile(p);
			log.fatal("CharacteristicsConfigurationFile written ");
			s.manufactureUpdateRuleConfigurationFiles(p,scenInfo);
			log.fatal("UpdateRuleConfigurationFile written ");

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ConversionException e1) {
			e1.printStackTrace();
			log.fatal(e1.getMessage());
			assertNull(e1); // Force error.
		} catch (DynamoInconsistentDataException e2) {
			// TODO Auto-generated catch block
			log.fatal(e2.getMessage());
			e2.printStackTrace();
			assertNull(e2); // Force error.
		}
	}

	@After
	public void teardown() {
		log.fatal("Test completed ");
	}

	@Test
	public void runSimulation() throws CDMRunException {

		try {
			
			log.fatal("Starting manufacturing initial population.");
			InitialPopulationFactory E2 = new InitialPopulationFactory();
			ScenarioInfo scenInfo=new ScenarioInfo();
			 scenInfo.makeTestData1();
			// for testing purposes use only newborns
			E2.writeInitialPopulation(p, 10, simName, 1111,true, scenInfo);
		//	log.fatal("Starting manufacturing newborn population.");
			E2.writeInitialPopulation(p, 10, simName, 1111,false, scenInfo);
			
			log.fatal("Starting run.");

			
			File multipleCharacteristicsFile = new File(preCharConfig);
			log.fatal("charFile made.");
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			log.fatal("charFile handled.");
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();
			log.fatal("empty charmap made");
			File simulationConfigurationFile = new File(simFileName);
			File simulationConfigurationFile2 = new File(simFileName2);
			log.fatal("simulationFile made.");
			
				assertTrue(CharacteristicsConfigurationMapSingleton
						.getInstance().size() > 1);
				// calculate frequency of risk factor values during simulation
				// //
				Population pop2=null; ;
				Population pop1=null; ;
				if (simulationConfigurationFile.exists()) {
					simulationConfiguration = new XMLConfiguration(
							simulationConfigurationFile);
					log.fatal("simulationconfuration made");
					sim = SimulationFromXMLFactory
							.manufacture_DOMPopulationTree(simulationConfiguration);
					log.fatal("simulationFile loaded");
					
					log.fatal("starting run ");
					sim.run();
					log.fatal("Run  complete.");
					
					pop1 = sim.getPopulation();
					
					if (simulationConfigurationFile2.exists()) {
						simulationConfiguration = new XMLConfiguration(
								simulationConfigurationFile2);
						log.fatal("simulationconfuration made");
						sim = SimulationFromXMLFactory
								.manufacture_DOMPopulationTree(simulationConfiguration);
						log.fatal("simulationFile2 loaded");
						
						log.fatal("starting run 2");
						sim.run();
						log.fatal("Run 2 complete.");
						
						pop2 = sim.getPopulation();}	
				//public DynamoOutputFactory(int nScen,int riskType, int nRiskFactorClasses, int stepsInRun,DiseaseClusterStructure [] structure) {
				int nScen=1;// number of alternative scenarios
				DynamoOutputFactory output = new DynamoOutputFactory(nScen,p.riskType,p.prevRisk[0][0].length, sim.getStepsInRun(), p.clusterStructure);
				Population[] pop=new Population[2];
				pop[0]=pop1;
				pop[1]=pop2;
				output.makeOutput(pop);
				JFreeChart chart=output.makeSurvivalPlot("testplot",0);
				chart=output.makeSurvivalPlot("testplot",1);
				ChartFrame frame1 = new ChartFrame("Survival Chart", chart);
				frame1.setVisible(true);
				frame1.setSize(300, 300);
				output.makeLifeExpectancyPlot();
				output.makePrevalencePlots(0);
				output.makePrevalencePlots(1);
				output.makePopulationPyramidPlot(0,0);
				output.makeRiskFactorPlots(0); 
				output.makePrevalenceByRiskFactorPlots(0); 
				output.makePrevalenceByRiskFactorPlots(1); 
				try {

					ChartUtilities.saveChartAsJPEG(new File(
							"C:\\hendriek\\java\\chart.jpg"), chart, 500, 300);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("Problem occurred creating chart.");
				}
				/*
				for (int count = 1; count <= sim.getStepsInRun(); count++) {
					File outFile = new File(baseDir + "out" + count + ".XML");
					DOMPopulationWriter.writeToXMLFile(sim.getPopulation(),
							count, outFile);
				}*/
				log.fatal("Result written.");

			}

			/*
			for (int count = 1; count <= sim.getStepsInRun(); count++) {
				File outFile = new File(baseDir + "out" + count + ".XML");
				DOMPopulationWriter.writeToXMLFile(sim.getPopulation(),
						count, outFile);
			}*/
			log.fatal("Result written.");

		
			
			
			
			
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	/*
	 * InitialPopulationFactory e2=new InitialPopulationFactory(); File f=new
	 * File("c:/hendriek/java/workspace/dynamo/dynamoinput/test.xml");
	 * 
	 * XMLBaseElement element=new XMLBaseElement(); ConfigurationFileData
	 * example = new ConfigurationFileData();
	 * example.setXmlGlobalTagName("example"); for(int i = 0; i < 7; i++){
	 * element=new XMLBaseElement(); element.setTag("Tag"+i);
	 * element.setValue(i); example.add(element);}
	 * 
	 * example.writeToXMLFile(example,f);
	 * //E2.writeInitialPopulation(E1,10,"c:/hendriek/java/workspace/dynamo/dynamoinput/initial"); //
	 * test weighted regression
	 * 
	 */

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.estimation.test.TestAll.class);
	}
}
