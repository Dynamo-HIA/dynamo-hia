package nl.rivm.emi.dynamo.datahandling.test;



	import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.dynamo.datahandling.BaseDirectory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

	public class test {
		
		
		
		
	
		Log log = LogFactory.getLog(getClass().getName());
		
		String baseDir="c:/hendriek/java/workspace/dynamo/dynamodata";
//		String baseDir="n:/dynamo-hia/java/workspace/dynamo";
		
		String preCharConfig = baseDir+"/charconf_hb.xml";
		// NB de directory moet ook worden aangepast in deze file // 
		File simulationConfigurationFile = new File(
				baseDir+"/simulation_hb.xml");

		File simOutput = new File(
				baseDir+"/output/sim_out.xml");
		
		HierarchicalConfiguration simulationConfiguration;


		Simulation sim;

		@Before
			public void setup() throws ConfigurationException {
				System.out.println(preCharConfig);
				try {
					BaseDirectory baseDir=BaseDirectory.getInstance("c:/hendriek/java/workspace/dynamo/dynamodata");
					
					
					
					
					
					
					File multipleCharacteristicsFile = new File(
							preCharConfig);
					CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
							multipleCharacteristicsFile);
					CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
							.getInstance();
					if (simulationConfigurationFile.exists()) {
						simulationConfiguration = new XMLConfiguration(
								simulationConfigurationFile);
						sim = SimulationFromXMLFactory
								.manufacture_DOMPopulationTree(simulationConfiguration);
					} else {
						throw new ConfigurationException(String.format(
								"Configuration file %1$s does not exist",
								simulationConfigurationFile.getAbsolutePath()));
					}
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					assertNull(e); // Force error.
				} catch (ConversionException e1) {
					e1.printStackTrace();
					assertNull(e1); // Force error.
				}
			}

		@After
		public void teardown() {
		}

		@Test
		public void runSimulation() {
			assertTrue(CharacteristicsConfigurationMapSingleton.getInstance()
					.size() > 1);
			try {
				log.fatal("Starting run.");
				sim.run();
				log.fatal("Run complete.");
				// calculate frequency of risk factor values during simulation //
				Population pop = sim.getPopulation();
				int sexIndex=0;
				int ageIndex=0;
				float simulatedRiskFactorPrevalence[][][][]= new float[sim.getStepsInRun()][150][2][9];
				
				for (int stepCount = 0; stepCount < sim.getStepsInRun(); stepCount++) {
					Iterator<Individual> individualIterator = pop.iterator();
					int[][] nPop=new int [150][2];
					while (individualIterator.hasNext()) {
						Individual individual = individualIterator.next();
					ageIndex = (int) Math.round(( (Float)individual.get(1).getValue(stepCount)));
					sexIndex = (int)(Integer) individual.get(2).getValue(stepCount);
					int riskFactor = (int)(Integer) individual.get(3).getValue(stepCount);
					
					simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][riskFactor]++;
					nPop[ageIndex][sexIndex]++;
						
						
					
				}// end loop over individuals
					for (int r=0;r<9;r++){
					simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][r]=simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][r]/nPop[ageIndex][sexIndex];
					log.fatal("step "+stepCount+"prev for risk factor class "+r+" age "+ageIndex+" sex "+ sexIndex + " = "+simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][r]);
					}}// end  loop over time
				
				for (int count=1; count<=sim.getStepsInRun();count++){
					File outFile = new File(baseDir+"out"+count+".XML");
				DOMPopulationWriter.writeToXMLFile(sim.getPopulation(), count, outFile);}
				log.fatal("Result written.");
			} catch (CDMRunException e) {
				// TODO Auto-generated catch block
				log.fatal(e.getMessage());
				log.fatal(e.getLocalizedMessage());
				e.printStackTrace();
				assertNull(e); // Force error.

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				assertNull(e); // Force error.
			}
		}

		public static junit.framework.Test suite() {
			return new JUnit4TestAdapter(
					nl.rivm.emi.cdm.iterations.two.test.Test02_10_10.class);
		}
	}


}
