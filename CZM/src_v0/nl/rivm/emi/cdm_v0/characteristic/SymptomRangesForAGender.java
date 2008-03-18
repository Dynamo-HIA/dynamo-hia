package nl.rivm.emi.cdm_v0.characteristic;

import java.util.HashMap;

import nl.rivm.emi.cdm_v0.exceptions.RangePropertiesAtWrongIndexException;

/**
 * Class containing the properties governing the self-controlled behaviour of a
 * characteristic. External influences are modelled elsewhere.
 * 
 * @author mondeelr
 * 
 */
public class SymptomRangesForAGender extends HashMap<Integer, SymptomRange>{

	private static final long serialVersionUID = -7384437482694252231L;
	/**
	 * The PR name of the characteristic.
	 */
	String name;

	public SymptomRangesForAGender() {
		super();
	}

	/**
	 * Add the ClassProperties to the ArrayList. One for each class the
	 * characteristic can fall in.
	 * 
	 * @param newSymptomRange
	 * @throws RangePropertiesAtWrongIndexException
	 */
	public void addSymptomRange(SymptomRange newSymptomRange){
		put(new Integer(newSymptomRange.getRangeIndex()), newSymptomRange);
	}

}
