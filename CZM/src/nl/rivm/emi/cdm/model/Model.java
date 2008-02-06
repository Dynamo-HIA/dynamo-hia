package nl.rivm.emi.cdm.model;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import nl.rivm.emi.cdm.CZMRunException;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMap;
import nl.rivm.emi.cdm.simulation.CZMConfigurationException;
import nl.rivm.emi.cdm.simulation.Simulation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class Model {

	Log log = LogFactory.getLog(getClass().getName());

	CharacteristicsConfigurationMap characteristicsConfiguration;

	Simulation simulation;

	public Model() {
		log.info("Instantiating Model.");
		simulation = new Simulation("@@@@@@", 1);
	}

	public static void main(String[] args) {
		try {
			int numberOfSteps = 1;
			File populationFile = new File(
					"C:/eclipse321/workspace/CZM/data/population.xml");
			// Preliminary checks.
			if (populationFile.exists() && populationFile.isFile()
					&& populationFile.canRead()) {
				Simulation simulation = new Simulation("AlphaOne", numberOfSteps, populationFile);
//				simulation.run();
			}
		} catch (ParserConfigurationException e) {
			System.out.println(e.getClass().getName() + " " + e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getClass().getName() + " " + e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + " " + e.getMessage());
		} catch (CZMConfigurationException e) {
			System.out.println(e.getClass().getName() + " " + e.getMessage());
		} catch (CZMRunException e) {
			System.out.println(e.getClass().getName() + " " + e.getMessage());
		}
	}
}
