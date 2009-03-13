package nl.rivm.emi.dynamo.estimation.test;

 
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynamoOutputFactory;
import nl.rivm.emi.dynamo.estimation.ModelParameters;
import nl.rivm.emi.dynamo.estimation.Output_UI;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;
import nl.rivm.emi.dynamo.simulation.DynamoSimulation;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAll {
	Log log = LogFactory.getLog(getClass().getName());
	
	String baseDir = BaseDirectory.getInstance(
			"c:\\hendriek\\java\\dynamohome\\").getBaseDir();

	// NB de directory moet ook worden aangepast in deze file //
	
	String simName = "simulation1";
	String directoryName = baseDir + "Simulations" + File.separator + simName;
	String preCharConfig = directoryName + File.separator + "modelconfiguration"
			+ File.separator +"charconfig.XML";

	String simFileName = directoryName + File.separator+"modelconfiguration"
			+ File.separator+ "simulation" ;// to add " .XML";
	/* String simFileName2 = directoryName + "\\modelconfiguration"
	+ "\\simulation_scen_1.XML";*/
	
	
	ScenarioInfo scen;

	HierarchicalConfiguration simulationConfiguration;
	Simulation sim;
	ModelParameters p;

	@Before
	public void setup() {
		log.fatal("Starting test. ");
      
		System.out.println(preCharConfig);
		try {
			p = new ModelParameters(baseDir);
			scen=p.estimateModelParameters(simName, null);
			log.fatal("ModelParameters estimated and written");
			
			/* Below is obsolete
			 * 			 * 
			 *  InputData i = new InputData();
			i.makeTest2Data();
			// NB is testdata=1 then also scenario info=1: 2x veranderen!!
			//i.makeTest2Data();
			p.estimateModelParameters(100, i); */
			
			/* p.setRiskType(1);
			ScenarioInfo scenInfo=new ScenarioInfo();
			// scenInfo.makeTestData1();
			 
			 scenInfo.makeTestData();
			SimulationConfigurationFactory s = new SimulationConfigurationFactory(
					simName);

			// DynamoConfigurationData d= new
			// DynamoConfigurationData(BaseDirectory.getBaseDir());
			s.manufactureSimulationConfigurationFile(p,scenInfo);
			log.fatal("SimulationConfigurationFile written ");
			s.manufactureCharacteristicsConfigurationFile(p);
			log.fatal("CharacteristicsConfigurationFile written ");
			s.manufactureUpdateRuleConfigurationFiles(p,scenInfo);
			log.fatal("UpdateRuleConfigurationFile written ");*/

		} catch (DynamoConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		
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
	public void runSimulation()  {

		try {
			
			/*
			 * obsolete, already done by estimate parameters
			 * log.fatal("Starting manufacturing initial population.");
			InitialPopulationFactory E2 = new InitialPopulationFactory();
			ScenarioInfo scenInfo=new ScenarioInfo();
			 scenInfo.makeTestData();
			// for testing purposes use only newborns
			E2.writeInitialPopulation(p, 10, simName, 1111,true, scenInfo);
		//	log.fatal("Starting manufacturing newborn population.");
			E2.writeInitialPopulation(p, 10, simName, 1111,false, scenInfo);
			*/
			
			log.fatal("Starting run.");

			
			File multipleCharacteristicsFile = new File(preCharConfig);
			log.fatal("charFile made.");
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			log.fatal("charFile handled.");
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();
			log.fatal("empty charmap made");
			/* array pop contains the stimulated populations for the different scenario's */
			int nLoops=scen.getNScenarios()+1;
			// TODO: number of loops
			if (p.getRiskType() !=2) for (int scennum=1; scennum<scen.getNScenarios();scennum++){
				if  (scen.getInitialPrevalenceType()[scennum]&&  (!scen.getTransitionType()[scennum])) nLoops--;
			}
			
			// Population[] pop=new Population[nLoops];
			// Population [][] newbornPop;
			
			/* 
			 * newborns[0] is null, as newborns at the beginning of the simulation (time=0) are
			 * already included in the population.
			 * for clarity we start numbering at 1
			 */
					
			// if (scen.isWithNewBorns()) newbornPop=new Population[nLoops][scen.getYearsInRun()];
			
			Population[] pop=p.getInitialPopulation();
			for (int scennum=0; scennum<nLoops;scennum++){
			File simulationConfigurationFile;
			if (scennum==0)simulationConfigurationFile= new File(simFileName+".xml");
			else simulationConfigurationFile= new File(simFileName+"_scen_"+scennum+".xml");
			log.fatal("simulationFile made for scenario "+scennum);
			
				assertTrue(CharacteristicsConfigurationMapSingleton
						.getInstance().size() > 1);
				// calculate frequency of risk factor values during simulation
				
				if (simulationConfigurationFile.exists()) {
					simulationConfiguration = new XMLConfiguration(
							simulationConfigurationFile);
					log.fatal("simulationconfuration made for scenario "+scennum);
					
                /* read the configuration file and make populations */
					
					
					sim = SimulationFromXMLFactory
							.manufacture_DOMPopulationTree(simulationConfiguration,false);
					sim.setPopulation(pop[scennum]);
					log.fatal("simulationFile loaded for scenario " + scennum);
					// pop[scennum] = sim.getPopulation();
					if (pop[scennum]==null) throw new CDMConfigurationException("no population found for scenario "+scennum);
					log.fatal("starting run for population "+scennum);
					DynamoSimulation sim2=new DynamoSimulation();
					sim2.runSimulation();
					log.fatal("Run  complete for population "+scennum);
					
					
					
					
				}
			}
				DynamoOutputFactory output = new DynamoOutputFactory(scen,simName);
				Output_UI ui= new Output_UI(null, scen,  simName, pop, baseDir);
				/*
				output.extractArraysFromPopulations(pop);
				output.makeArraysWithNumbers();
			
				output.makePrevalencePlots(0);
				output.makePrevalencePlots(1);
				
				output.makeRiskFactorPlots(0); 
				if (scen.getRiskType()==2) output.makeMeanPlots(0);
				output.makePrevalenceByRiskFactorPlots(0); 
				output.makePrevalenceByRiskFactorPlots(1); 
				JFreeChart chart=output.makeSurvivalPlot(0);
				chart=output.makeSurvivalPlot(1);
				chart=output.makeSurvivalPlot(2);
				output.makeLifeExpectancyPlot(0);*/
				/*
				for (int count = 1; count <= sim.getStepsInRun(); count++) {
					File outFile = new File(baseDir + "out" + count + ".XML");
					DOMPopulationWriter.writeToXMLFile(sim.getPopulation(),
							count, outFile);
				}*/
				log.fatal("Result written.");

					/*
			for (int count = 1; count <= sim.getStepsInRun(); count++) {
				File outFile = new File(baseDir + "out" + count + ".XML");
				DOMPopulationWriter.writeToXMLFile(sim.getPopulation(),
						count, outFile);
			}*/
			log.fatal("Result written.");

					output.writeOutput(scen);
				} catch (XMLStreamException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				
				
		} catch (DynamoConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.fatal(e.getMessage());
			assertNull(e); // Force error.
		} catch (DynamoScenarioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.fatal(e.getMessage());			 				
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.fatal(e.getMessage());
			assertNull(e); // Force error.
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.fatal(e.getMessage());
		}
	
	}
	
	
	/* ***********************************
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
public void writeXMLOutput() throws XMLStreamException, IOException{
	
	
    

	
	
	OutputStream out = new FileOutputStream("c:\\hendriek\\java\\data.xml");
	XMLOutputFactory factory = XMLOutputFactory.newInstance();
	XMLStreamWriter writer = factory.createXMLStreamWriter(out);
	writer.writeStartDocument();
	writer.writeStartElement("greeting");
	writer.writeAttribute("id", "g1"); // voegt toe aan vorig start element
	writer.writeCharacters("Hello StAX");
	writer.writeEndElement();
	writer.writeEndDocument();
	
	writer.flush();
	writer.close();
	out.close();
}
	
	
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.estimation.test.TestAll.class);
	}
}
