package nl.rivm.emi.cdm.characteristic.values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.CDMRunException;

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
	 * @param numSteps Number of steps to be preallocated for this value.
	 * @param index The index of the Characteristic the value belongs to.
	 * @param value The value for the characteristic to be stored in the first step (at index 0).
	 */
	public IntCharacteristicValue(int numSteps, int index, int value) {
		super("ch", index );
		rijtje = new int[numSteps+1];
		rijtje[0] = value;
	}
	
	/* extra constructor added by hendriek */
	/**
	 * Initiation of characteristic for newborns, where
	 * the first steps are empty.
	 * Multiple steps possible. Number is fixed after instantiation.
	 * 
	 * @param numSteps Number of steps to be preallocated for this value.
	 * @param index The index of the Characteristic the value belongs to.
	 * @param startstep The number of the first step to be filled.
	 * @param value The value for the characteristic to be stored in the first step (at index 0).
	
	 */
	public IntCharacteristicValue(int numSteps, int index, int startStep, int value) {
		super("ch", index );
		rijtje = new int[numSteps+1];
		rijtje[startStep] = value;
		numberFilled = startStep;
	}

	public int getValue() {
		return rijtje[0];
	}

	public Integer getValue(int step) {
		return new Integer(rijtje[step]);
	}

	public int[] getRijtje() {
		return rijtje;
	}

	public void appendValue(int value) throws CDMRunException {
		if (numberFilled < rijtje.length) {
			rijtje[numberFilled] = value;
			numberFilled++;
		} else {
			log.warn("Steps are full!");
			throw new CDMRunException("Step storage (size " + rijtje.length + " ) overflow, cannot append value.");
		}
	}

	public Integer getCurrentValue() throws CDMRunException {
		if (numberFilled > 0) {
			return rijtje[numberFilled - 1];
		} else {
			log.warn("Steps are empty!");
			throw new CDMRunException("Step storage is empty, no newest value available.");
		}
	}

	public boolean appendIntegerValue(String stringValue) throws CDMRunException {
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
