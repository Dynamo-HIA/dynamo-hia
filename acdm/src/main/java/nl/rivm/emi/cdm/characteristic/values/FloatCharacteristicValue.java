package nl.rivm.emi.cdm.characteristic.values;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.exceptions.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FloatCharacteristicValue extends CharacteristicValueBase implements Serializable {
	private static final long serialVersionUID = -484554629969040878L;

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
		/* next line added by Hendriek as it otherwise does not run */
		numberFilled=1;
		
	}
	
	
	

	public float getValue() {
		return rijtje[0];
	}

	public Float getValue(int step) {
		return Float.valueOf(rijtje[step]);
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
	
	/* added by Hendriek */
	
	public Float getPreviousValue() throws CDMRunException {
		if (numberFilled > 1) {
			return rijtje[numberFilled - 2];
		} else {
			log.warn("previous Steps are empty!");
			throw new CDMRunException("Previous Step storage is empty, no newest value available.");
		}
	}
/*
 * added by Hendriek in order to initialize newborns
 * but not needed
 
	public void shiftFirstValue(int i) throws CDMRunException {
		if (numberFilled > 0) {
			float current = rijtje[numberFilled - 1];
			rijtje[i]=current;
			numberFilled=i+1;
			for (int j=0;j<i;j++)
				rijtje[j]=-1;
			
		} else {
			log.warn("Steps are empty!");
			throw new CDMRunException("Step storage is empty, no values available.");
		}
	}
	* */
	/*
	 * added by Hendriek in order to stop the simulation of those older than 105
	 * 
	 * */
	public boolean isFull() throws CDMRunException {
		boolean full=false;
		if (numberFilled == rijtje.length) full=true;
		return full;
	}
	
	/* added by hendriek */
	public void setFirstValue(float value)  {
		 rijtje[0]=value;
		
	}
	/*
	 * end addition hendriek
	 */
	
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
