package nl.rivm.emi.cdm.population.file.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.rivm.emi.cdm.characteristic.Characteristic;
import nl.rivm.emi.cdm.characteristic.CharacteristicsConfigurationMapSingleton;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csvreader.CsvReader;

public class PopulationCsvReader {

	Log log = LogFactory.getLog(getClass().getSimpleName());

	private String filePath = null;

	private CsvReader reader;

	String[] headers;
	CharacteristicsConfigurationMapSingleton charSingle = CharacteristicsConfigurationMapSingleton
			.getInstance();

	public PopulationCsvReader(String filePath) {
		super();
		this.filePath = filePath;
	}

	public PopulationCsvReader(File populationFile) {
		super();
		this.filePath = populationFile.getAbsolutePath();
	}

	public boolean checkFileAndHeadersAgainstCharacteristics() {
		log.debug("Entering: checkFileAndHeadersAgainstCharacteristics()");
		boolean result = false;
		try {
			reader = new CsvReader(filePath);
			reader.setDelimiter(CSVPopulationWriter.SEPARATOR.charAt(0));
			reader.readHeaders();
			headers = reader.getHeaders();
			log.debug("Read headers: " + reader.getHeaders());
			log.debug("Headers.length: " + headers.length);
			if (headers.length > 0) {
				if (headers.length == charSingle.size()
						+ CSVPopulationWriter.CHARACTERISTICSOFFSET) {
					int headerCount = 0;
					for (String header : headers) {
						log.debug("Iterating, count: " + headerCount
								+ " header: " + header);
						if (headerCount > 1) {
							String characteristicName = charSingle
									.getCharacteristic(headerCount - 1)
									.getLabel();
							log.debug("Comparing: " + characteristicName
									+ " and: " + header);
							if (!characteristicName.equals(header)) {
								log
										.fatal("Aborting check: Characteristic name: "
												+ characteristicName
												+ " and header: "
												+ header
												+ " do not match.");
								break;
							}
						}
						headerCount++;
					}
					if (headerCount == headers.length) {
						result = true;
					}
				} else {
					log
							.fatal("- Failure - Population file has "
									+ headers.length
									+ " header(s), expected are "
									+ (charSingle.size() + CSVPopulationWriter.CHARACTERISTICSOFFSET));
				}
			} else {
				log.fatal("- Failure - Population file has no headers.");
			}
		} catch (FileNotFoundException e) {
			log.error("File at: " + filePath + " not found.");
		} catch (IOException e) {
			log.error("Cannot read file at: " + filePath + ".");
		}
		return result;
	}

	public String[] getHeaders() throws IOException {
		return (reader.getHeaders());
	}

	public Population readPopulation(int stepsInRun) {
		String fileNameBody = filePath.substring(Math.max(0, filePath
				.lastIndexOf(File.separator) + 1), filePath.indexOf(".csv"));
		log.debug("Population name derived from filePath: " + filePath
				+ " result: " + fileNameBody);
		Population population = new Population("population", fileNameBody);
		Individual individual = null;
		int individualCount = 0;
		do {
			individual = readIndividual(stepsInRun);
			if (individual != null) {
				population.add(individual);
				individualCount++;
			}
		} while (individual != null);
		if (individualCount == 0) {
			population = null;
			log.fatal("Population construction aborted, no individuals found.");
		}
		return population;
	}

	private Individual readIndividual(int stepsInRun) {
		Individual individual = null;
		try {
			reader.readRecord();
			{
				int charUserIndex = 1;
				for (String header : headers) {
					String recordContent = reader.get(header);
					if ("".equals(recordContent)) {
						if (charUserIndex > charSingle.size()) {
							// Don't panic, just leftovers at the end of the
							// record.
							break;
						} else {
							// Not so good.
							log
									.debug("Aborting readIndividual: Empty column found at index: "
											+ charUserIndex);
							break;
						}
					}
					if ("individualname".equalsIgnoreCase(header)) {
						String name = recordContent;
						individual = new Individual("individual", "name");
						log.debug("Created individual, name: " + name);
					} else {
						if ("Seed".equals(header)) {
							long randomNumberGeneratorSeed = Long
									.parseLong(recordContent);
							individual
									.setRandomNumberGeneratorSeed(randomNumberGeneratorSeed);
							log.debug("Set randomNumberGeneratorSeed to: "
									+ randomNumberGeneratorSeed);
						} else {
							// Characteristics - columns.
							log.debug("Header: " + header + " at index: "
									+ charUserIndex);
							String characteristicValueAsString = recordContent;
							Characteristic characteristic = charSingle
									.getCharacteristic(charUserIndex);
							charUserIndex++;
							if (characteristic == null) {
								throw (new CDMConfigurationException(
										"CSV header:"
												+ header
												+ " doesn't have a matching characteristic."));
							}
							String characteristicName = characteristic
									.getLabel();
							if (!header.equals(characteristicName)) {
								throw (new CDMConfigurationException(
										"CSV header:"
												+ header
												+ " doesn't match characteristic label: "
												+ characteristicName));
							}
							AbstractCharacteristicType type = characteristic
									.getType();
							CharacteristicValueBase value2Set = (CharacteristicValueBase) type
									.convertFromString(
											characteristicValueAsString,
											charUserIndex - 1);
							if (value2Set != null) {
								individual.add(value2Set);
								log.debug("Added value: "
										+ value2Set.getCurrentValue()
												.toString() + " for: "
										+ characteristic.getLabel());
							} else {
								log.error("- Aborting - Value: "
										+ characteristicValueAsString
										+ " could not be converted by: "
										+ characteristicName);
								individual = null;
								break;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			log.fatal("Individual construction aborted, Exception: "
					+ e.getClass().getSimpleName() + " with message: "
					+ e.getMessage());
			individual = null;
		} catch (CDMConfigurationException e) {
			log.fatal("Individual construction aborted, Exception: "
					+ e.getClass().getSimpleName() + " with message: "
					+ e.getMessage());
			individual = null;
		} catch (CDMRunException e) {
			log.fatal("Individual construction aborted, Exception: "
					+ e.getClass().getSimpleName() + " with message: "
					+ e.getMessage());
			individual = null;
			e.printStackTrace();
		}
		return individual;
	}
}
