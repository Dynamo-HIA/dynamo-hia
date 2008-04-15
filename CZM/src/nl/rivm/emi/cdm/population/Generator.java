package nl.rivm.emi.cdm.population;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.prngutil.RandomConstants;

public class Generator {

	private String label = null;

	private int populationSize = -1;

	private String rngClassName = null;

	private Class rngClass = null;

	private float rngSeed = -1;

	private ArrayList<Integer> characteristicIds = null;

	public Generator() {
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

	public String getRngClassName() {
		return rngClassName;
	}

	public void setRngClassName(String rngClassName) {
		this.rngClassName = rngClassName;
	}

	public float getRngSeed() {
		return rngSeed;
	}

	public void setRngSeed(float rngSeed) {
		this.rngSeed = rngSeed;
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
	 * @return The this reference when this test is OK, null otherwise. TODO
	 *         This test must be kept in sync with the structure of the XML
	 *         configuration.
	 * @throws CDMConfigurationException
	 */
	@SuppressWarnings("finally")
	public Generator isValid() throws CDMConfigurationException {
		Generator thisWhenValid = null;
		try {
			if (label != null) {
				if (populationSize > 0) {
					if (rngClassName != null) {
						boolean success = loadAndCheckRNGClass();
						if (success) {
							if (rngSeed != -1) {
								if ((characteristicIds != null)
										&& (characteristicIds.size() > 0)) {
									thisWhenValid = this;
								}
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			throw new CDMConfigurationException(
					CDMConfigurationException.invalidGeneratorRngClassNameMessage);
		}
		return thisWhenValid;
	}

	private boolean loadAndCheckRNGClass() throws ClassNotFoundException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		Class randomNumberGenerator = classLoader.loadClass(rngClassName);
		Class javaUtilRandom = classLoader.loadClass("java.util.Random");
		boolean success = (randomNumberGenerator.asSubclass(javaUtilRandom) != null);
		if (success) {
			rngClass = randomNumberGenerator;
		}
		return success;
	}

	public File generateNewbornsWithAllZeroes()
			throws CDMConfigurationException {
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
		output.writeCharacters("\n");
		output.writeStartElement(Population.xmlLabelElementName);
		output.writeCharacters(label);
		output.writeEndElement();
		output.writeCharacters("\n");

		try {
			if (rngClass == null) {

				loadAndCheckRNGClass();
			}
			if (rngClass != null) {
				Random rngInstance = (Random) rngClass.newInstance();
				rngInstance.setSeed((long) rngSeed);
				for (int count = 1; count <= populationSize; count++) {
					writeIndividual(output, count, rngInstance.nextLong());
				}
			}
		} catch (ClassNotFoundException e) {
			throw new CDMConfigurationException(String.format(
					RandomConstants.classNotLoadableMsg, rngClassName));
		} catch (InstantiationException e) {
			throw new CDMConfigurationException(String.format(
					RandomConstants.classNotInstantiableMsg, rngClassName));
		} catch (IllegalAccessException e) {
			throw new CDMConfigurationException(String.format(
					RandomConstants.classNotAccessibleMsg, rngClassName));
		}
		output.writeEndElement();
	}

	private void writeIndividual(XMLStreamWriter output, int count, long rngSeed)
			throws XMLStreamException, CDMConfigurationException {
		output.writeStartElement(Individual.xmlElementName);
		output.writeAttribute("lb", "ind_" + count);
		output.writeCharacters("\n");
		output.writeStartElement(RandomConstants.xmlElementName);
		output.writeCharacters(String.valueOf(rngSeed));
		output.writeEndElement();
		output.writeCharacters("\n");
		for (int cCount = 0; cCount < characteristicIds.size(); cCount++) {
			writeCharacteristic(output, cCount);
		}
		output.writeEndElement();
		output.writeCharacters("\n");
	}

	private void writeCharacteristic(XMLStreamWriter output, int count)
			throws XMLStreamException {
		output.writeEmptyElement(Characteristic.xmlElementName);
		output.writeAttribute("lb", "char_" + characteristicIds.get(count));
		output.writeAttribute("vl", "0");
		output.writeCharacters("\n");
	}
}