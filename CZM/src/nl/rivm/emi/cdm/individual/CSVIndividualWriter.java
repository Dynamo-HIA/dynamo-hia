package nl.rivm.emi.cdm.individual;

import java.util.Iterator;

import nl.rivm.emi.cdm.characteristic.values.CSVCharacteristicValueWriter;
import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.population.CSVPopulationWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author boshuizh This class houses a method that generates a CSV String for
 *         an individual in a particular step
 * 
 */
public class CSVIndividualWriter {

	Log log = LogFactory.getLog(getClass().getName());

	public CSVIndividualWriter() {
		super();
	}

	public static String generateCSVrecord(Individual individual, int stepNumber) {
		String individualRecord = "";

		String elementName = individual.getElementName();
		individualRecord += elementName + /*";"*/ CSVPopulationWriter.SEPARATOR;
		/* elementname in Dynamo contains "scen"+ scenarioNumber */
		String label = individual.getLabel();

		if (label == null)
			label = "";
		individualRecord += label ;

		Iterator<CharacteristicValueBase> iterator = individual.iterator();
		while (iterator.hasNext()) {
			individualRecord+=/*";"*/ CSVPopulationWriter.SEPARATOR;
			CharacteristicValueBase charVal = iterator.next();
			if (charVal instanceof IntCharacteristicValue) {
				individualRecord += CSVCharacteristicValueWriter
						.generateString((IntCharacteristicValue) charVal,
								stepNumber);
			} else {
				if (charVal instanceof FloatCharacteristicValue) {
					individualRecord += CSVCharacteristicValueWriter.generateString(
							(FloatCharacteristicValue) charVal, stepNumber);
				} else if (charVal instanceof CompoundCharacteristicValue) {

					individualRecord += CSVCharacteristicValueWriter.generateString(
							(CompoundCharacteristicValue) charVal, stepNumber);
				}
			}
		}
		return individualRecord;

	}
}