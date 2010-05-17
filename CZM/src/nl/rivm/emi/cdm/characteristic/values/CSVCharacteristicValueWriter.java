package nl.rivm.emi.cdm.characteristic.values;

import nl.rivm.emi.cdm.csv.file.population.CSVPopulationWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CSVCharacteristicValueWriter {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.characteristic.values.CSVCharacteristicValueWriter");

	public static String generateString(
			IntCharacteristicValue characteristicValue, int stepNumber) {
		String returnString = "";

		if (stepNumber >= characteristicValue.getRijtje().length) {
			log.fatal("Attemp to get a value (arraysize: "
					+ characteristicValue.getRijtje().length
					+ ") from a nonexistent location: " + stepNumber);
			return returnString;
		} else {
			int value = characteristicValue.getValue(stepNumber);
			returnString = ((Integer) value).toString();
			return returnString;
		}
	}

	public static String generateString(
			FloatCharacteristicValue characteristicValue, int stepNumber) {
		String returnString = "";
		if (stepNumber >= characteristicValue.getRijtje().length) {
			log.fatal("Attemp to get a value (arraysize: "
					+ characteristicValue.getRijtje().length
					+ ") from a nonexistent location: " + stepNumber);
			return returnString;
		} else {
			float value = characteristicValue.getValue(stepNumber);
			returnString = ((Float) value).toString();
			return returnString;
		}
	}

	public static String generateString(
			CompoundCharacteristicValue characteristicValue, int stepNumber) {
		String returnString = "";
		if (stepNumber >= characteristicValue.getRijtje().length) {
			log.fatal("Attemp to get a value (arraysize: "
					+ characteristicValue.getRijtje().length
					+ ") from a nonexistent location: " + stepNumber);
			return returnString;
		} else {
			float[] value = characteristicValue.getUnwrappedValue(stepNumber);
			returnString = "";
			for (int i = 0; i < value.length; i++) {
				if (i == 0)
					returnString = ((Float) value[i]).toString();
				else
					returnString += /*";"*/ CSVPopulationWriter.SEPARATOR + ((Float) value[i]).toString();
			}
			return returnString;
		}
	}
}
