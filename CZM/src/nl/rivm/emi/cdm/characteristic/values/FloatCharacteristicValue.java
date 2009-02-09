package nl.rivm.emi.cdm.characteristic.values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FloatCharacteristicValue extends CharacteristicValueBase {
	Log log = LogFactory.getLog(getClass().getName());

	float[] rijtje;

	int numberFilled = 0;

	/**
	 * Multiple steps possible. Number is fixed after instantiation.
	 * 
	 * @param numSteps
	 * @param index
	 * @param value
	 */
	public FloatCharacteristicValue(int numSteps, int index) {
		super("ch", index );
		rijtje = new float[numSteps+1];
	}

	/**
	 * Multiple steps possible. Number is fixed after instantiation.
	 * 
	 * @param numSteps Number of steps to be preallocated for this value.
	 * @param index The index of the Characteristic the value belongs to.
	 * @param value The value for the characteristic to be stored in the first step (at index 0).
	 */
	public FloatCharacteristicValue(int numSteps, int index, float value) {
		super("ch", index );
		rijtje = new float[numSteps+1];
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
	public FloatCharacteristicValue(int numSteps, int index, int startStep, float value) {
		super("ch", index );
		rijtje = new float[numSteps+1];
		rijtje[startStep] = value;
		numberFilled = startStep;
	}
	
	
	

	public float getValue() {
		return rijtje[0];
	}

	public Float getValue(int step) {
		return new Float(rijtje[step]);
	}

	public float[] getRijtje() {
		return rijtje;
	}

	public void appendValue(float value) throws CDMRunException {
		if (numberFilled < rijtje.length) {
			rijtje[numberFilled] = value;
			numberFilled++;
		} else {
			log.warn("Steps are full!");
			throw new CDMRunException("Step storage (size " + rijtje.length + " ) overflow, cannot append value.");
		}
	}

	public Float getCurrentValue() throws CDMRunException {
		if (numberFilled > 0) {
			return rijtje[numberFilled - 1];
		} else {
			log.warn("Steps are empty!");
			throw new CDMRunException("Step storage is empty, no newest value available.");
		}
	}

	public boolean appendFloatValue(String stringValue) throws CDMRunException {
		String floatRegex = "^\\d++\\.?\\d*$";
		Pattern pattern = Pattern.compile(floatRegex);
		Matcher matcher = pattern.matcher(stringValue);
		boolean success = matcher.matches();
		try {
			if (success) {
				float numberToSet = Float.parseFloat(stringValue);
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
