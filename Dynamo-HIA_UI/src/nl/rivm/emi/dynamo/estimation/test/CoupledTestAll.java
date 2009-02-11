package nl.rivm.emi.dynamo.estimation.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.CDMRunException;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.CharacteristicsXMLConfiguration;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.simulation.Simulation;
import nl.rivm.emi.cdm.simulation.SimulationFromXMLFactory;
import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynamoOutputFactory;
import nl.rivm.emi.dynamo.estimation.InitialPopulationFactory;
import nl.rivm.emi.dynamo.estimation.InputData;
import nl.rivm.emi.dynamo.estimation.ModelParameters;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;
import nl.rivm.emi.dynamo.estimation.SimulationConfigurationFactory;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.MessageBox;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoupledTestAll {
	Log log = LogFactory.getLog(getClass().getName());

	// String baseDir = BaseDirectory.getInstance(
	// "c:\\hendriek\\java\\dynamohome\\").getBaseDir();
	//
	// // NB de directory moet ook worden aangepast in deze file //
	//
	// String simName = "simulation1";
	// String directoryName = baseDir + "Simulations" + File.separator +
	// simName;
	// String preCharConfig = directoryName + File.separator
	// + "modelconfiguration" + File.separator + "charconfig.XML";
	//
	// String simFileName = directoryName + File.separator +
	// "modelconfiguration"
	// + File.separator + "simulation";// to add " .XML";
	/*
	 * String simFileName2 = directoryName + "\\modelconfiguration" +
	 * "\\simulation_scen_1.XML";
	 */

	ScenarioInfo scen;

	HierarchicalConfiguration simulationConfiguration;
	Simulation sim;
	ModelParameters p;

	public void entryPoint(String baseDirectoryPath, String simulationName) throws CDMRunException {
			log.fatal("Starting coupled test. ");

			// System.out.println(preCharConfig);
			log.fatal("entryPoint() BaseDirectoryPath: " + baseDirectoryPath
					+ ", simulationName: " + simulationName);
			estimateModelParameters(baseDirectoryPath, simulationName);
			runSimulation(baseDirectoryPath, simulationName);
			log.fatal("Test completed without Exception");
	}

	public void runSimulation(String baseDirectoryPath, String simulationName)
			throws CDMRunException {

		try {
			log.fatal("Starting run.");
			String simulationBasePath = baseDirectoryPath + File.separator
					+ "Simulations" + File.separator + simulationName;
			String preCharConfig = simulationBasePath + File.separator
					+ "modelconfiguration" + File.separator + "charconfig.XML";
			String simulationConfigPath = simulationBasePath + File.separator
					+ "modelconfiguration" + File.separator + "simulation";
			File multipleCharacteristicsFile = new File(preCharConfig);
			log.fatal("charFile made.");
			CharacteristicsXMLConfiguration handler = new CharacteristicsXMLConfiguration(
					multipleCharacteristicsFile);
			log.fatal("charFile handled.");
			CharacteristicsConfigurationMapSingleton single = CharacteristicsConfigurationMapSingleton
					.getInstance();
			log.fatal("empty charmap made");
			/*
			 * array pop contains the stimulated populations for the different
			 * scenario's
			 */
			int nLoops = scen.getNScenarios() + 1;
			// TODO: number of loops
			for (int scennum = 1; scennum < scen.getNScenarios(); scennum++) {
				if (scen.getInitialPrevalenceType()[scennum]
						&& (!scen.getTransitionType()[scennum]))
					nLoops--;
			}

			Population[] pop = new Population[nLoops];
			for (int scennum = 0; scennum < nLoops; scennum++) {
				File simulationConfigurationFile;
				if (scennum == 0)
					simulationConfigurationFile = new File(simulationConfigPath + ".xml");
				else
					simulationConfigurationFile = new File(simulationConfigPath
							+ "_scen_" + scennum + ".xml");
				log.fatal("simulationFile made for scenario " + scennum);

				assertTrue(CharacteristicsConfigurationMapSingleton
						.getInstance().size() > 1);
				// calculate frequency of risk factor values during simulation
				// //

				if (simulationConfigurationFile.exists()) {
					simulationConfiguration = new XMLConfiguration(
							simulationConfigurationFile);
					log.fatal("simulationconfuration made for scenario "
							+ scennum);
					sim = SimulationFromXMLFactory
							.manufacture_DOMPopulationTree(simulationConfiguration);
					log.fatal("simulationFile loaded for scenario " + scennum);

					log.fatal("starting run for scenario " + scennum);
					sim.run();
					log.fatal("Run  complete for scenario " + scennum);

					pop[scennum] = sim.getPopulation();

				}
			}
			DynamoOutputFactory output = new DynamoOutputFactory(scen, simulationName);

			// output.makeOutput(pop);
			output.extractArraysFromPopulations(pop);
			JFreeChart chart = output
					.makeSurvivalPlot("survival scenario 0", 0);
			chart = output.makeSurvivalPlot("survival scenario 1", 1);
			ChartFrame frame1 = new ChartFrame("Survival Chart", chart);
			frame1.setVisible(true);
			frame1.setSize(300, 300);
			output.makeLifeExpectancyPlot();
			output.makePrevalencePlots(0);
			output.makePrevalencePlots(1);
			output.makePopulationPyramidPlot(0, 0);
			output.makeRiskFactorPlots(0);
			output.makePrevalenceByRiskFactorPlots(0);
			output.makePrevalenceByRiskFactorPlots(1);
			try {
				output.writeOutput(scen);
			} catch (XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {

				ChartUtilities.saveChartAsJPEG(new File(
						"C:\\hendriek\\java\\chart.jpg"), chart, 500, 300);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Problem occurred creating chart.");
			}
			/*
			 * for (int count = 1; count <= sim.getStepsInRun(); count++) { File
			 * outFile = new File(baseDir + "out" + count + ".XML");
			 * DOMPopulationWriter.writeToXMLFile(sim.getPopulation(), count,
			 * outFile); }
			 */
			log.fatal("Result written.");

			/*
			 * for (int count = 1; count <= sim.getStepsInRun(); count++) { File
			 * outFile = new File(baseDir + "out" + count + ".XML");
			 * DOMPopulationWriter.writeToXMLFile(sim.getPopulation(), count,
			 * outFile); }
			 */
			log.fatal("Result written.");

		} catch (DynamoScenarioException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (DynamoConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e); // Force error.
		}
	}

	private void estimateModelParameters(String baseDirectoryPath, String simulationName) {
		try {
			p = new ModelParameters();
			scen = p.estimateModelParameters(simulationName);
			log.fatal("ModelParameters estimated and written");
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
	public void writeXMLOutput() throws XMLStreamException, IOException {

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
}
