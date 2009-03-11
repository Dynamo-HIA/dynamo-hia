package nl.rivm.emi.dynamo.simulation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.DOMCharacteristicValueWriter;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.individual.Individual;
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

public class DynamoSimulation {
	Log log = LogFactory.getLog(getClass().getName());

	// String baseDir="c:/hendriek/java/workspace/dynamo/dynamodata";
	String baseDir = "n:/dynamo-hia/java/workspace/dynamo/dynamodata";

	// String preCharConfig = baseDir+"/charconf_hb.xml";

	String preCharConfig = baseDir + "/charconf02_10.xml";
	// NB de directory moet ook worden aangepast in deze file //
	File simulationConfigurationFile = new File(baseDir + "/simulationRIVM.xml");
	// File simulationConfigurationFile = new File(
	// baseDir+"/simulation02_10_10.xml");

	// File simulationConfigurationFile = new File(
	// baseDir+"/simulationThuis.xml");

	File simOutput = new File(baseDir + "/sim_out.xml");

	XMLConfiguration simulationConfiguration;

	Simulation sim;

	@Before
	public void setup() throws ConfigurationException {
		System.out.println(preCharConfig);
		File multipleCharacteristicsFile = null;
		try {
			multipleCharacteristicsFile = new File(preCharConfig);
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();
			if (simulationConfigurationFile.exists()) {
				this.simulationConfiguration = new XMLConfiguration(
						simulationConfigurationFile);

				// Validate the xml by xsd schema
				// WORKAROUND: clear() is put after the constructor (also calls
				// load()).
				// The config cannot be loaded twice,
				// because the contents will be doubled.
				this.simulationConfiguration.clear();

				// Validate the xml by xsd schema
				this.simulationConfiguration.setValidating(true);
				this.simulationConfiguration.load();

				sim = SimulationFromXMLFactory
						.manufacture_DOMPopulationTree(simulationConfiguration);
			} else {
				throw new ConfigurationException(String.format(
						"Configuration file %1$s does not exist",
						simulationConfigurationFile.getAbsolutePath()));
			}
		} catch (ConfigurationException e) {
			String dynamoErrorMessage = "Reading error encountered when reading file: "
					+ multipleCharacteristicsFile.getAbsolutePath()
					+ " with message: " + e.getMessage();
			ErrorMessageUtil.handleErrorMessage(this.log, dynamoErrorMessage,
					e, simulationConfigurationFile.getAbsolutePath());
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
			for (int count = 1; count < sim.getStepsInRun(); count++) {
				File interOut = new File(baseDir + "/inter" + count + ".xml");
				Population pop = sim.getPopulation();
				Individual ind;
				// count number of characteristics
				// id= 0
				// age = 1
				// gender =2

				double[][][] sum = null;
				int age = 0;
				int sex = 0;
				while ((ind = pop.nextIndividual()) != null) {
					if (sum == null) {
						sum = new double[96][2][ind.size()];

					}
					/* count characteristics to make array */
					/*
					 * int n=1;Iterator<CharacteristicValueBase> charValiterator
					 * = ind.iterator(); while (charValiterator.hasNext()) {n++;
					 * CharacteristicValueBase charVal =
					 * charValiterator.next();}
					 */

					for (CharacteristicValueBase currentChar : ind) {

						if (currentChar instanceof IntCharacteristicValue) {

							IntCharacteristicValue charVal = (IntCharacteristicValue) currentChar;
							if (charVal.getIndex() == 1) {
								age = charVal.getCurrentValue();
							} else if (charVal.getIndex() == 2) {
								sex = charVal.getCurrentValue();
							} else if (charVal.getIndex() != 1)
								sum[age][sex][charVal.getIndex()] += (double) charVal
										.getCurrentValue();

						} else {
							if (currentChar instanceof FloatCharacteristicValue) {
								FloatCharacteristicValue charVal = (FloatCharacteristicValue) currentChar;
								if (charVal.getIndex() == 1) {
									age = Math.round(charVal.getCurrentValue());
								} else if (charVal.getIndex() == 2) {
									sex = Math.round(charVal.getCurrentValue());
									// dit nog aanpassen als age 1-ste
									// characteristiek wordt
								} else if (charVal.getIndex() != 1)
									sum[age][sex][charVal.getIndex()] += (double) charVal
											.getCurrentValue();

							}
						}
					}
					/* add count as first element */
					for (int a = 0; a < 96; a++)
						for (int g = 0; g < 96; g++)
							sum[a][g][0] = count;

					DOMPopulationWriter.writeToXMLFile(sim.getPopulation(),
							count, interOut);
				}
				DOMPopulationWriter.writeToXMLFile(sim.getPopulation(), sim
						.getStepsInRun(), simOutput);
				log.fatal("Result written.");
			}
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

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.iterations.two.test.Test02_10_10.class);
	}
}
