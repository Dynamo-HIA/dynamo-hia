/**
 * 
 */
package nl.rivm.emi.cdm.characteristic.values;

/**
 * @author Hendriek
 *
 */
import java.io.Serializable;

import nl.rivm.emi.cdm.characteristic.types.CompoundCharacteristicType;
import nl.rivm.emi.cdm.exceptions.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CompoundCharacteristicValue extends CharacteristicValueBase implements Serializable{
	private static final long serialVersionUID = 1150635092009180305L;

	Log log = LogFactory.getLog(getClass().getName());

	float[][] rijtje;

	private int numberFilled = 0;

	private int nElementFilled=0;

	/**
	 * Multiple steps possible. Number is fixed after instantiation. Also the
	 * number of sub-characteristics in fixed after instantion
	 * 
	 * @param numSteps
	 *            : number of steps in the simulation
	 * @param index
	 *            : index of the characteristic in the individual
	 * @param nChar
	 *             : number of elements in the compound characteristic 
	 
	 */
	public CompoundCharacteristicValue(int numSteps, int index, int nChar) {
		super("ch", index);
		rijtje = new float[numSteps + 1][nChar];
	}

	/**
	 * Multiple steps possible. Number is fixed after instantiation.
	 * 
	 * @param numSteps
	 *            Number of steps to be preallocated for this value.
	 * @param index
	 *            The index of the Characteristic the value belongs to.
	 * @param value
	 *            The array with values for the characteristic to be stored in
	 *            the first step (at index 0).
	 * 
	 * 
	 * @param nChar The number of sub-characteristics
	 *             
	 */
	public CompoundCharacteristicValue(int numSteps, int index, int nChar,
			float[] value) {
		super("ch", index);
		if (value.length>nChar) nChar=value.length;
		this.rijtje = new float[numSteps + 1][nChar];
		/* do deep copy for safety */
		for (int i=0;i<value.length;i++)
		this.rijtje[0][i] = value[i];
		
		numberFilled=1;;
	}
	
	/**
	 * @return
	 */
	public float[] getValue() {
		return this.rijtje[0];
	}
	
	
		public void shiftFirstValue(int i) throws CDMRunException {
			if (getNumberFilled() > 0) {
				float [] current = rijtje[getNumberFilled() - 1];
				rijtje[i]=current;
				numberFilled=(i+1);
				for (int j=0;j<i;j++)
					for (int ichar=0;ichar<rijtje.length;ichar++)
					rijtje[j][ichar]=-1;
				
			} else {
				log.warn("Steps are empty!");
				throw new CDMRunException("Step storage is empty, no values available.");
			}
		}

	public Float[] getValue(int step) {
	Float[]	returnArray= new Float[rijtje[step].length];
        for (int i=0;i<rijtje[step].length;i++) returnArray[i]=rijtje[step][i];
		return returnArray;
	}
	
	
	public float[] getUnwrappedValue(int step) {
			return rijtje[step];
		}

	public float[][] getRijtje() {
		return rijtje;
	}

	public void appendValue(float [] value) throws CDMRunException {
		if (getNumberFilled() < rijtje.length) {
			rijtje[getNumberFilled()] = value;
			numberFilled=(getNumberFilled() + 1);
		} else {
			log.warn("Steps are full!");
			throw new CDMRunException("Step storage (size " + rijtje.length
					+ " ) overflow, cannot append value.");
		}
	}
	
	
	public void appendInitialValue(float  value) throws CDMRunException {
		if (nElementFilled < rijtje[0].length) {
			rijtje[0][nElementFilled] = value;
			numberFilled=1;
			nElementFilled++;
		} else {
			log.warn("Steps are full!");
			throw new CDMRunException("Step storage (size " + rijtje.length
					+ " ) overflow, cannot append value.");
		}
	}
	
	public Float[] getCurrentValue() throws CDMRunException {
		if (getNumberFilled() > 0) {
			
			
			Float[]	returnArray= new Float[rijtje[getNumberFilled() - 1].length];
	        for (int i=0;i<rijtje[getNumberFilled() - 1].length;i++) returnArray[i]=rijtje[getNumberFilled() - 1][i];
			return returnArray;
			
		} else {
			log.warn("Steps are empty!");
			throw new CDMRunException(
					"Step storage is empty, no newest value available.");
		}
	}
	
	
	public Float[] getPreviousValue() throws CDMRunException {
		if (getNumberFilled() > 1) {
			
			
			Float[]	returnArray= new Float[rijtje[getNumberFilled() - 2].length];
	        for (int i=0;i<rijtje[getNumberFilled() - 2].length;i++) returnArray[i]=rijtje[getNumberFilled() -2][i];
			return returnArray;
			
		} else {
			log.warn("previous Steps are empty!");
			throw new CDMRunException(
					"previous Step storage is empty, no newest value available.");
		}
	}
	
	
	
	

	/** This method was added as a possibly more speedy version of getCurrentValue
	 * as profiling showed that much time was spend in the method "getCurrentValue of FloatCharacterValue
	 * @return
	 * @throws CDMRunException
	 */
	public float[] getCurrentWrapperlessValue() throws CDMRunException {
		if (getNumberFilled() > 0) {
			
			
			return rijtje[getNumberFilled() - 1];
			
		} else {
			log.warn("Steps are empty!");
			throw new CDMRunException(
					"Step storage is empty, no newest value available.");
		}
	}
	/* added by Hendriek
	
	/**
	 * @param compoundCharacteristicValue
	 */
	public void add(CompoundCharacteristicType compoundCharacteristicValue) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return
	 */
	public float getLastValue() {
		// TODO Auto-generated method stub
		
		return rijtje[getNumberFilled()-1][nElementFilled-1];
	}

	

	public int getNumberFilled() {
		return numberFilled;
	}
}
