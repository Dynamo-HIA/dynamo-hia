package nl.rivm.emi.dynamo.estimation.test;

/**
 * Copy of TestAll.java to avoid development collisions.
 */
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
import nl.rivm.emi.dynamo.estimation.InitialPopulationFactory;
import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.ModelParameters;
import nl.rivm.emi.dynamo.estimation.SimulationConfigurationFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestClusterRun_3Char {
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
	/* LET OP: de factory voor initial population voegt zelf XML toe */
	String popFileName = directoryName + "\\modelconfiguration"
			+ "\\population";

	HierarchicalConfiguration simulationConfiguration;
	ModelParameters p;

	@Before
	public void setup() {
		log.fatal("Starting test. ");

		System.out.println(preCharConfig);
		try {
			p = new ModelParameters();
			InputData i = new InputData();
			// i.makeTest2Data();
			i.makeTest1Data();
			p.estimateModelParameters(100, i);
			log.fatal("ModelParameters estimated ");
			p.setRiskType(1);

			TestClusterRunSimulationConfigurationFactory s = new TestClusterRunSimulationConfigurationFactory(
					simName);

			// DynamoConfigurationData d= new
			// DynamoConfigurationData(BaseDirectory.getBaseDir());
			s.manufactureSimulationConfigurationFile(p,
					"data/development/population_1921i_3c.xml");
			log.fatal("SimulationConfigurationFile written ");
			s.manufactureCharacteristicsConfigurationFile(p);
			log.fatal("CharacteristicsConfigurationFile written ");
			s.manufactureUpdateRuleConfigurationFiles(p);
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
	public void run3CharacteristicSimulation() throws CDMRunException {
		String threeCharConfig = "data/development/charconfig_3.xml";

		try {
			Simulation sim;
			log.fatal("Starting manufacturing initial population.");
			InitialPopulationFactory E2 = new InitialPopulationFactory();
			E2.writeInitialPopulation(p, 10, simName, 1111, false);
			log.fatal("Starting run.");

			File multipleCharacteristicsFile = new File(threeCharConfig);
			log.fatal("charFile made.");
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			log.fatal("charFile handled.");
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();
			log
					.fatal("charFile loaded, " + single.size()
							+ " Characteristics.");
			File simulationConfigurationFile = new File(simFileName);
			log.fatal("simulationFile made.");
			if (simulationConfigurationFile.exists()) {
				simulationConfiguration = new XMLConfiguration(
						simulationConfigurationFile);
				log.fatal("simulationFile read");
				sim = SimulationFromXMLFactory
						.manufacture_DOMPopulationTree(simulationConfiguration);
				log.fatal("simulation manufactured through DOM");
				/*
				 * log.fatal("Starting second, test threaded manufacture.");
				 * Simulation sim2 = ThreadedSimulationFromXMLFactory
				 * .manufacture_DOMPopulationTree(simulationConfiguration); log
				 * .fatal(
				 * "Second, dummy simulation object manufactured with high priority threading."
				 * );
				 */
				log.fatal("starting 3 characteristic run");
				long startTime = System.currentTimeMillis();
				sim.run();
				long endTime = System.currentTimeMillis();
				log.fatal("3 characteristic run complete. Time used: "
						+ (endTime - startTime) + " msec.");
				assertTrue(CharacteristicsConfigurationMapSingleton
						.getInstance().size() > 1);
				// evaluateResult(sim);

			}
		} catch (ParserConfigurationException e) {
			log.fatal("Exception " + e.getClass().getName()
					+ " caught. Message: " + e.getMessage());
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ConfigurationException e) {
			log.fatal("Exception " + e.getClass().getName()
					+ " caught. Message: " + e.getMessage());
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (TransformerException e) {
			log.fatal("Exception " + e.getClass().getName()
					+ " caught. Message: " + e.getMessage());
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	/**
	 * calculate frequency of risk factor values during simulation
	 * 
	 * @param sim
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private void evaluateResult(Simulation sim)
			throws ParserConfigurationException, TransformerException {
		Population pop = sim.getPopulation();
		int sexIndex = 0;
		int ageIndex = 0;
		float simulatedRiskFactorPrevalence[][][][] = new float[sim
				.getStepsInRun()][150][2][9];
		float[][][] simulatedDiseasePrevalence = new float[sim.getStepsInRun()][150][2];
		float[][][] simulatedSurvival = new float[sim.getStepsInRun()][150][2];

		for (int stepCount = 0; stepCount < sim.getStepsInRun(); stepCount++) {
			Iterator<Individual> individualIterator = pop.iterator();
			int[][] nPop = new int[150][2];
			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();
				ageIndex = (int) Math.round(((Float) individual.get(1)
						.getValue(stepCount)));
				sexIndex = (int) (Integer) individual.get(2)
						.getValue(stepCount);
				int riskFactor = (int) (Integer) individual.get(3).getValue(
						stepCount);
				float disease = (float) (Float) individual.get(4).getValue(
						stepCount);
				float survival = (float) (Float) individual.get(7).getValue(
						stepCount);
				simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][riskFactor]++;
				simulatedDiseasePrevalence[stepCount][ageIndex][sexIndex] += disease;
				simulatedSurvival[stepCount][ageIndex][sexIndex] += survival;
				nPop[ageIndex][sexIndex]++;
			}// end loop over individuals

			for (int a = 0; a < 96; a++)
				for (int s = 0; s < 2; s++) {
					for (int r = 0; r < 4; r++) {
						if (nPop[a][s] != 0) {
							simulatedRiskFactorPrevalence[stepCount][a][s][r] = simulatedRiskFactorPrevalence[stepCount][a][s][r]
									/ nPop[a][s];

							log
									.fatal("step "
											+ stepCount
											+ "prev for risk factor class "
											+ r
											+ " age "
											+ a
											+ " sex "
											+ s
											+ " = "
											+ simulatedRiskFactorPrevalence[stepCount][a][s][r]);
						}
					}
					if (nPop[a][s] != 0) {
						simulatedDiseasePrevalence[stepCount][a][s] = simulatedDiseasePrevalence[stepCount][a][s]
								/ nPop[a][s];
						simulatedSurvival[stepCount][a][s] = simulatedSurvival[stepCount][a][s]
								/ nPop[a][s];

						log.fatal("step " + stepCount + "prev disease for age "
								+ a + " sex " + s + " = "
								+ simulatedDiseasePrevalence[stepCount][a][s]);
						log.fatal("step " + stepCount + "survival for age " + a
								+ " sex " + s + " = "
								+ simulatedSurvival[stepCount][a][s]);
					}
				}
		}// end loop over time

		for (int count = 1; count <= sim.getStepsInRun(); count++) {
			File outFile = new File(baseDir + "out" + count + ".XML");
			DOMPopulationWriter.writeToXMLFile(sim.getPopulation(), count,
					outFile);
		}
		log.fatal("Result written.");
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
	 * example.writeToXMLFile(example,f);//E2.writeInitialPopulation(E1,10,
	 * "c:/hendriek/java/workspace/dynamo/dynamoinput/initial"); // test
	 * weighted regression
	 */

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestClusterRun_3Char.class);
	}
}
