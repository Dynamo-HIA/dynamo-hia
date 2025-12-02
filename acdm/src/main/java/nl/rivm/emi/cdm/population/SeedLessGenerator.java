package nl.rivm.emi.cdm.population;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.prngutil.RandomConstants;

public class SeedLessGenerator {

	private Log log = LogFactory.getLog(getClass().getName());

	private String label = null;

	private int populationSize = -1;

	private ArrayList<Integer> characteristicIds = null;

	public SeedLessGenerator() {
		super();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public ArrayList<Integer> getCharacteristicIds() {
		return characteristicIds;
	}

	public void setCharacteristicIds(ArrayList<Integer> characteristicIds) {
		this.characteristicIds = characteristicIds;
	}

	/**
	 * Check whether the Characteristic instance has a minimal set of values.
	 * 
	 * @return The this reference when this test is OK, null otherwise. 
	 *         This test must be kept in sync with the structure of the XML
	 *         configuration.
	 * @throws CDMConfigurationException
	 */
	public SeedLessGenerator isValid() throws CDMConfigurationException {
		SeedLessGenerator thisWhenValid = null;
		if (label != null) {
			if (populationSize > 0) {
				if ((characteristicIds != null)
						&& (characteristicIds.size() > 0)) {
					thisWhenValid = this;
				}
			}
		}
		return thisWhenValid;
	}

	public File generateNewbornsWithAllOnes() throws CDMConfigurationException {
		CharacteristicsConfigurationMapSingleton charConf = CharacteristicsConfigurationMapSingleton
				.getInstance();
		if (charConf.isEmpty()) {
			throw new CDMConfigurationException(
					CDMConfigurationException.characteristicsConfigurationNotInitializedMessage);
		}
		File newBornCohort = null;
		int count = 1;
		String fileName;
		do {
			String countString = String.format("%1$03d", count);
			fileName = label + countString + ".xml";
			newBornCohort = new File(fileName);
			count++;
		} while (newBornCohort.exists() && (count < 1000));
		if (count <= 1000) {
			try {
				log.info("Generating " + newBornCohort);
				newBornCohort.createNewFile();
				FileOutputStream fOS = new FileOutputStream(newBornCohort);
				OutputStreamWriter writer = new OutputStreamWriter(fOS, "UTF-8");

				XMLOutputFactory factory = XMLOutputFactory.newInstance();
				XMLStreamWriter output = factory.createXMLStreamWriter(writer);
				writeDocument(output);
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new CDMConfigurationException(String.format(
						Population.populationFileNotWriteableMsg, fileName));
			} catch (XMLStreamException e) {
				e.printStackTrace();
				throw new CDMConfigurationException(String.format(
						Population.populationFileNotWriteableMsg, fileName));
			}
		}
		return newBornCohort;
	}

	private void writeDocument(XMLStreamWriter output)
			throws XMLStreamException, CDMConfigurationException {
		output.writeStartDocument("UTF-8", "1.0");
		output.writeCharacters("\n");
		writePopulation(output);
		output.writeEndDocument();
	}

	private void writePopulation(XMLStreamWriter output)
			throws XMLStreamException, CDMConfigurationException {
		output.writeStartElement(Population.xmlElementName);
//		output.writeCharacters("\n");
//		output.writeStartElement(Population.xmlLabelElementName);
//		output.writeCharacters(label);
//		output.writeEndElement();
		output.writeAttribute(Population.xmlLabelAttributeName,label);
// ~
		output.writeCharacters("\n");
		for (int count = 1; count <= populationSize; count++) {
			writeIndividual(output, count);
		}
		output.writeEndElement();
	}

	private void writeIndividual(XMLStreamWriter output, int count)
			throws XMLStreamException, CDMConfigurationException {
		output.writeStartElement(Individual.xmlElementName);
		output.writeAttribute("lb", "ind_" + count);
		output.writeCharacters("\n");
		output.writeStartElement(RandomConstants.xmlElementName);
		output.writeCharacters("1");
		output.writeEndElement();
		output.writeCharacters("\n");

		for (int cCount = 0; cCount < characteristicIds.size(); cCount++) {
			writeCharacteristicWithValueOne(output, cCount);
		}
		output.writeEndElement();
		output.writeCharacters("\n");
	}

	private void writeCharacteristicWithValueOne(XMLStreamWriter output, int count)
			throws XMLStreamException {
		output.writeEmptyElement(Characteristic.xmlElementName);
		output.writeAttribute("id", characteristicIds.get(count).toString());
		output.writeAttribute("vl", "1");
		output.writeCharacters("\n");
	}
}