package nl.rivm.emi.cdm.simulation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;

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
	
	String baseDir="c:/hendriek/java/workspace/dynamo/dynamodata";
//	String baseDir="n:/dynamo-hia/java/workspace/dynamo";
	
	String preCharConfig = baseDir+"/charconf02_10.xml";
	// NB de directory moet ook worden aangepast in deze file // 
	File simulationConfigurationFile = new File(
			baseDir+"/simulation02_10_10.xml");

	File simOutput = new File(
			baseDir+"/sim02_10_10_out.xml");
	
	XMLConfiguration simulationConfiguration;


	Simulation sim;

	@Before
		public void setup() throws ConfigurationException {
			System.out.println(preCharConfig);
			try {
				File multipleCharacteristicsFile = new File(
						preCharConfig);
				CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
						multipleCharacteristicsFile);
				CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
						.getInstance();
				if (simulationConfigurationFile.exists()) {
					this.simulationConfiguration = new XMLConfiguration(
							simulationConfigurationFile);
					
					// Validate the xml by xsd schema
					// WORKAROUND: clear() is put after the constructor (also calls load()). 
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
				ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(), e, 
						this.simulationConfigurationFile.getAbsolutePath());
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
			for (int count=1; count<=sim.getStepsInRun();count++){
				File outFile = new File(baseDir+"out"+count+".XML");
			DOMPopulationWriter.writeToXMLFile(sim.getPopulation(), count, outFile);}
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

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.iterations.two.test.Test02_10_10.class);
	}
}
