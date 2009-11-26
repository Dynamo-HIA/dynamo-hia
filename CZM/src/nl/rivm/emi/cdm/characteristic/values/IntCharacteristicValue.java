package nl.rivm.emi.cdm.characteristic.values;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.exceptions.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IntCharacteristicValue extends CharacteristicValueBase implements Serializable{
	private static final long serialVersionUID = 1071047282957867095L;

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
		/* next line added by Hendriek as it otherwise does not run */
		numberFilled=1;
	}
	
	

	/*
	 * added by Hendriek in order to stop the simulation of those older than 105
	 * 
	 * */
	public boolean isFull() throws CDMRunException {
		boolean full=false;
		if (numberFilled == rijtje.length) full=true;
		return full;
	}
	/*
	 * end addition hendriek
	 */

	public int getValue() {
		return rijtje[0];
	}

	public Integer getValue(int step) {
		return new Integer(rijtje[step]);
	}

	public int[] getRijtje() {
		return rijtje;
	}

	/*
	 * added by Hendriek in order to initialize newborns
	 * 
	 * not needed 
		public void shiftFirstValue(int i) throws CDMRunException {
			if (numberFilled > 0) {
				int current = rijtje[numberFilled - 1];
				rijtje[i]=current;
				numberFilled=i+1;
				for (int j=0;j<i;j++)
					rijtje[j]=-1;
				
			} else {
				log.warn("Steps are empty!");
				throw new CDMRunException("Step storage is empty, no values available.");
			}
		}
		*/
	
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
/* added by hendriek */
	public Integer getPreviousValue() throws CDMRunException {
		if (numberFilled > 1) {
			return rijtje[numberFilled - 2];
		} else {
			log.warn("Previous Steps are empty!");
			throw new CDMRunException("previous Step storage is empty, no newest value available.");
		}
	}

	
	public boolean appendIntegerValue(String stringValue) throws CDMRunException {
		String intRegex = "[0-9]+";
		Pattern pattern = Pattern.compile(intRegex);
		Matcher matcher = pattern.matcher(stringValue);
		boolean success = matcher.matches();
		try {
			if (success) {
				int numberToSet = Integer.valueOf(stringValue);
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
