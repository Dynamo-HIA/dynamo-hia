package nl.rivm.emi.cdm.rules.update.dynamo.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DynamoSimulationUpdateRuleTest {
	Log log = LogFactory.getLog(getClass().getName());
	
	String baseDir="c:/hendriek/java/workspace/dynamo/dynamodata/";
//	String baseDir="n:/dynamo-hia/java/workspace/dynamo/dynamodata/";
	
	String preCharConfig = baseDir+"/charconf_hb.xml";
	// NB de directory moet ook worden aangepast in deze file // 
	File simulationConfigurationFile = new File(
			baseDir+"simulation_hb.xml");

	File simOutput = new File(
			baseDir+"output/sim_out.xml");
	
	HierarchicalConfiguration simulationConfiguration;


	Simulation sim;

	@Before
		public void setup() throws ConfigurationException {
			System.out.println(preCharConfig);
			try {
				//CategoricalRiskFactorManyToOneUpdateRule rule4=new CategoricalRiskFactorManyToOneUpdateRule(baseDir+"/rule2.xml");
				
				
				File multipleCharacteristicsFile = new File(
						preCharConfig);
				@SuppressWarnings("unused")
				CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
						multipleCharacteristicsFile);
				@SuppressWarnings("unused")
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
			float[][][] simulatedDiseasePrevalence= new float[sim.getStepsInRun()][150][2];
			float[][][]simulatedSurvival= new float[sim.getStepsInRun()][150][2];
			
				
				
			for (int stepCount = 0; stepCount < sim.getStepsInRun(); stepCount++) {
				Iterator<Individual> individualIterator = pop.iterator();
				int[][]nPop=new int[150][2];
				while (individualIterator.hasNext()) {
					Individual individual = individualIterator.next();
				ageIndex = (int) Math.round(( (Float)individual.get(1).getValue(stepCount)));
				sexIndex = (int)(Integer) individual.get(2).getValue(stepCount);
				int riskFactor = (int)(Integer) individual.get(3).getValue(stepCount);
				float disease =(float)(Float)individual.get(4).getValue(stepCount);
				float survival =(float)(Float)individual.get(10).getValue(stepCount);
				simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][riskFactor]++;
				simulatedDiseasePrevalence[stepCount][ageIndex][sexIndex]+=disease;
				simulatedSurvival[stepCount][ageIndex][sexIndex]+=survival;
				nPop[ageIndex][sexIndex]++;
			}// end loop over individuals
					
			
			
			for (int a=0;a<96;a++)for (int s=0;s<2;s++){
			for (int r=0;r<4;r++){
				if (nPop[a][s]!=0){
					simulatedRiskFactorPrevalence[stepCount][a][s][r]=simulatedRiskFactorPrevalence[stepCount][a][s][r]/nPop[a][s];
					
					log.fatal("step "+stepCount+"prev for risk factor class "+r+" age "+a+" sex "+ s + " = "+simulatedRiskFactorPrevalence[stepCount][a][s][r]);
				}}
				   if (nPop[a][s]!=0){
					 simulatedDiseasePrevalence[stepCount][a][s]=simulatedDiseasePrevalence[stepCount][a][s]/nPop[a][s];
						simulatedSurvival[stepCount][a][s]=simulatedSurvival[stepCount][a][s]/nPop[a][s];
 
				log.fatal("step "+stepCount+"prev disease for age "+a+" sex "+ s + " = "+simulatedDiseasePrevalence[stepCount][a][s]);
				log.fatal("step "+stepCount+"survival for age "+a+" sex "+ s + " = "+simulatedSurvival[stepCount][a][s]);}
			}
			}// end  loop over time
			
			
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
