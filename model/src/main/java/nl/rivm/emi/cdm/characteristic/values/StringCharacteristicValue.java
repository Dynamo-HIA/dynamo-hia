package nl.rivm.emi.cdm.characteristic.values;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.exceptions.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringCharacteristicValue extends CharacteristicValueBase
		implements Serializable {
	private static final long serialVersionUID = 1L;

	Log log = LogFactory.getLog(getClass().getName());

	String[] rijtje;

	int numberFilled = 0;

	/**
	 * Multiple steps possible. Number is fixed after instantiation.
	 * 
	 * @param numSteps
	 * @param index
	 * @param value
	 */
	public StringCharacteristicValue(int numSteps, int index) {
		super("ch", index);
		rijtje = new String[numSteps + 1];

	}

	/**
	 * Multiple steps possible. Number is fixed after instantiation.
	 * 
	 * @param numSteps
	 *            Number of steps to be preallocated for this value.
	 * @param index
	 *            The index of the Characteristic the value belongs to.
	 * @param value
	 *            The value for the characteristic to be stored in the first
	 *            step (at index 0).
	 */
	public StringCharacteristicValue(int numSteps, int index, String value) {
		super("ch", index);
		rijtje = new String[numSteps + 1];
		rijtje[0] = value;
		/* next line added by Hendriek as it otherwise does not run */
		numberFilled = 1;
	}

	/*
	 * added by Hendriek in order to stop the simulation of those older than 105
	 */
	public boolean isFull() throws CDMRunException {
		boolean full = false;
		if (numberFilled == rijtje.length)
			full = true;
		return full;
	}

	/*
	 * end addition hendriek
	 */

	public String getValue() {
		return rijtje[0];
	}

	public Integer getValue(int step) {
		return new Integer(rijtje[step]);
	}

	public String[] getRijtje() {
		return rijtje;
	}

	/*
	 * added by Hendriek in order to initialize newborns
	 * 
	 * not needed public void shiftFirstValue(int i) throws CDMRunException { if
	 * (numberFilled > 0) { int current = rijtje[numberFilled - 1];
	 * rijtje[i]=current; numberFilled=i+1; for (int j=0;j<i;j++) rijtje[j]=-1;
	 * 
	 * } else { log.warn("Steps are empty!"); throw new
	 * CDMRunException("Step storage is empty, no values available."); } }
	 */

	public void appendValue(String value) throws CDMRunException {
		if (numberFilled < rijtje.length) {
			rijtje[numberFilled] = value;
			numberFilled++;
		} else {
			log.warn("Steps are full!");
			throw new CDMRunException("Step storage (size " + rijtje.length
					+ " ) overflow, cannot append value.");
		}
	}

	public String getCurrentValue() throws CDMRunException {
		if (numberFilled > 0) {
			return rijtje[numberFilled - 1];
		} else {
			log.warn("Steps are empty!");
			throw new CDMRunException(
					"Step storage is empty, no newest value available.");
		}
	}

	/* added by hendriek */
	public String getPreviousValue() throws CDMRunException {
		if (numberFilled > 1) {
			return rijtje[numberFilled - 2];
		} else {
			log.warn("Previous Steps are empty!");
			throw new CDMRunException(
					"previous Step storage is empty, no newest value available.");
		}
	}

	public boolean appendStringValue(String stringValue) throws CDMRunException {
		appendValue(stringValue);
		return true;
	}

}
