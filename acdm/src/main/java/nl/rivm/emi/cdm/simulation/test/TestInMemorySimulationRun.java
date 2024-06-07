package nl.rivm.emi.cdm.simulation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.characteristic.types.IntegerCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.model.DOMBootStrap;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRuleRepository;
import nl.rivm.emi.cdm.simulation.RunModes;
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
import org.xml.sax.SAXException;

public class TestInMemorySimulationRun {
	Log log = LogFactory.getLog(getClass().getName());

	String projectBaseDir = System.getProperty("user.dir");

	String outputBasePath = projectBaseDir + File.separator
			+ "unittestdata" + File.separator + "iteration4"
			+ File.separator;

	File simulationConfiguration1 = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/simulation1.xml");

	File sim1Output = new File(
			"C:/eclipse321/workspace/CZM/unittestdata/iteration2/sim1output.xml");

	String existingFileName_MultiChar = "C:/eclipse321/workspace/CZM/unittestdata/iteration2/charconf1.xml";

	HierarchicalConfiguration simulationConfiguration;

	Simulation sim1;

	@Before
	public void setup() throws ConfigurationException {

//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//
//		IWorkspaceRoot root = workspace.getRoot();
//
//
//		// To get to the directory of your Workspace
//
//		IPath path = root.getLocation().;
//
//		String stringPath = path.toString();
//
//
//		// To get a handle to your project
//
//		IProject project = root.getProject("MyNewProject");
//
		// The above creates dependencies on Eclipse, not a good idea.
		

		try {
			CharacteristicsConfigurationMapSingleton singleton = CharacteristicsConfigurationMapSingleton
					.getInstance();
			singleton.clear();
			Characteristic ageCharacteristic = new Characteristic(1, "Age");
			IntegerCategoricalCharacteristicType icct = new IntegerCategoricalCharacteristicType();
			for (int count = 0; count < 96; count++) {
				icct.addPossibleValue(count);
			}
			ageCharacteristic.setType(icct);
			singleton.putCharacteristic(ageCharacteristic);

			sim1 = new Simulation("InMemorySim", 1);
			sim1.setTimeStep(1f);
			sim1.setStepsBetweenSaves(1);
			Individual indie1 = new Individual("bogus", "indie1");
			CharacteristicValueBase cvb = new IntCharacteristicValue(10, 1);
			((IntCharacteristicValue) cvb).appendValue(47);
			indie1.add(cvb);
			Population pop1 = new Population("pop","population1");
			pop1.add(indie1);
			sim1.setPopulation(pop1);
			sim1.setRunMode(RunModes.LONGITUDINAL);
		} catch (CDMRunException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}

	@After
	public void teardown() {
	}

	@Test
	public void runSimulation1() {
		assertTrue(CharacteristicsConfigurationMapSingleton.getInstance()
				.size() > 0);
		try {
			log.fatal("Starting run.");
			sim1.run();
			log.fatal("Run complete.");
			File outputFile = new File(outputBasePath + "sim1Output.xml");
			DOMPopulationWriter.writeToXMLFile(sim1.getPopulation(), sim1
					.getStepsInRun(), outputFile );
			log.fatal("Result written.");
		} catch (CDMRunException e) {
			// TODO Auto-generated catch block
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

	// @Test
	// public void runSimulation2() {
	// assertTrue(CharacteristicsConfigurationMapSingleton.getInstance()
	// .size() > 1);
	// try {
	// log.fatal("Starting run.");
	// sim2.run();
	// log.fatal("Run complete.");
	// PopulationWriter.writeToXMLFile(sim2.getPopulation(), sim2
	// .getStepsInRun(), sim2Output);
	// log.fatal("Result written.");
	// } catch (CDMRunException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// assertNull(e); // Force error.
	//
	// } catch (ParserConfigurationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// assertNull(e); // Force error.
	// } catch (TransformerException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// assertNull(e); // Force error.
	// }
	// }

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestInMemorySimulationRun.class);
	}

}
