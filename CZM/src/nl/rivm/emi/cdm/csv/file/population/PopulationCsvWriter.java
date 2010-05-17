package nl.rivm.emi.cdm.csv.file.population;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csvreader.CsvWriter;

public class PopulationCsvWriter {

	Log log = LogFactory.getLog(getClass().getName());

	private String filePath = null;

	private CsvWriter writer;

	String[] headers;

	private int numFields = 0;

	CharacteristicsConfigurationMapSingleton charSingle = CharacteristicsConfigurationMapSingleton
			.getInstance();

	public PopulationCsvWriter(String filePath) {
		super();
		this.filePath = filePath;
		numFields = charSingle.size() + 2;
	}

	public boolean writeHeaders(CsvWriter writer) {
		boolean result = false;
		try {
			headers = new String[numFields];
			headers[0] = "individualname";
			headers[1] = "Seed";
			for (int count = 0; count < charSingle.size(); count++) {
				String characteristicName = (charSingle
						.getCharacteristic(count)).getLabel();
				headers[count + 2] = characteristicName;
				log.debug("Setting header: " + characteristicName
						+ " at index: " + count);
			}
			writer.writeRecord(headers);
			writer.endRecord();
		} catch (FileNotFoundException e) {
			log.error("File at: " + filePath + " not found.");
		} catch (IOException e) {
			log.error("Cannot read file at: " + filePath + ".");
		}
		return result;
	}

	public void writePopulation(Population population) {
		try {
		writer = new CsvWriter(filePath);
		writer.setDelimiter(CSVPopulationWriter.SEPARATOR.charAt(0));
		writeHeaders(writer);
		String fileNameBody = filePath.substring(Math.max(0, filePath
				.lastIndexOf(File.separator) + 1), filePath.indexOf(".csv"));
		log.debug("Population name derived from filePath: " + filePath
				+ " result: " + fileNameBody);
		Iterator<Individual> iterator = population.iterator();
		String[] values = new String[numFields];
		while (iterator.hasNext()) {
			Individual individual = iterator.next();
			writeIndividual(values, individual);
		}
		writer.flush();
		writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeIndividual(String[] values, Individual individual)
			throws IOException {
		values[0] = individual.getLabel();
		values[1] = Long
				.toString(individual.getRandomNumberGeneratorSeed());
		for (int count = 2; count < numFields; count++) {
			CharacteristicValueBase value = individual.get(count - 2);
			String valueAsString = (value.getValue(0)).toString();
			values[count] = valueAsString;
		}
			writer.writeRecord(values);
			writer.endRecord();
	}
}
