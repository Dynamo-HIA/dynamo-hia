package nl.rivm.emi.cdm.characteristic;

import static org.junit.Assert.assertFalse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.CZMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IntCharacteristicValue extends CharacteristicValueBase {
	Log log = LogFactory.getLog(getClass().getName());

	int[] rijtje;

	int numberFilled = 0;

	/**
	 * Multiple steps possible. Number is fixed after instantiation.
	 * 
	 * @param numSteps
	 * @param index
	 * @param value
	 */
	public IntCharacteristicValue(int numSteps, int index) {
		super("ch", index );
		rijtje = new int[numSteps+1];
	}

	/**
	 * Multiple steps possible. Number is fixed after instantiation.
	 * 
	 * @param numSteps
	 * @param index
	 * @param value
	 */
	public IntCharacteristicValue(int numSteps, int index, int value) {
		super("ch", index );
		rijtje = new int[numSteps+1];
		rijtje[0] = value;
	}

	public int getValue() {
		return rijtje[0];
	}

	public int getValue(int step) {
		return rijtje[step];
	}

	public void appendValue(int value) throws CZMRunException {
		if (numberFilled < rijtje.length) {
			rijtje[numberFilled] = value;
			numberFilled++;
		} else {
			log.warn("Steps are full!");
			throw new CZMRunException("Step storage (size " + rijtje.length + " ) overflow, cannot append value.");
		}
	}

	public int getCurrentValue() throws CZMRunException {
		if (numberFilled > 0) {
			return rijtje[numberFilled - 1];
		} else {
			log.warn("Steps are empty!");
			throw new CZMRunException("Step storage is empty, no newest value available.");
		}
	}

	public boolean appendIntegerValue(String stringValue) throws CZMRunException {
		String intRegex = "[0-9]+";
		Pattern pattern = Pattern.compile(intRegex);
		Matcher matcher = pattern.matcher(stringValue);
		boolean success = matcher.matches();
		try {
			if (success) {
				int numberToSet = Integer.decode(stringValue);
				appendValue(numberToSet);
			}
			return success;
			// The Regex should prevent this from happening.
		} catch (NumberFormatException e) {
			success = false;
			return success;
		}
	}
}
