package nl.rivm.emi.dynamo.data.objects;

/**
 * Object to contain the data entered in W14.
 * The Observable contains a nonnegative Integer.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IAmount;
import nl.rivm.emi.dynamo.data.interfaces.ISexRatio;
import nl.rivm.emi.dynamo.data.interfaces.IStartingYear;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Year;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * 
 * Object for Newborns.
 * 
 * @author mondeelr
 * 
 */
public class NewbornsObject extends GroupConfigurationObjectServiceLayer
		implements ISexRatio, IStartingYear, IAmount {
	private static final long serialVersionUID = -1973812253427654652L;
	Log log = LogFactory.getLog(this.getClass().getName());

	public NewbornsObject(LinkedHashMap<String, Object> manufacturedMap) {
		super();
		super.putAll(manufacturedMap);
	}

	public WritableValue getObservableSexRatio() {
		WritableValue value = getSingleRootChildWritableValue(XMLTagEntityEnum.SEXRATIO
				.getElementName());
		return value;
	}

	public Float getSexRatio() {
		Float value = getSingleRootChildFloatValue(XMLTagEntityEnum.SEXRATIO
				.getElementName());
		return value;
	}

	public void setSexratio(Float sexRatio) {
		putSingleRootChildFloatValue(
				XMLTagEntityEnum.SEXRATIO.getElementName(), sexRatio);

	}

	public WritableValue getObservableStartingYear() {
		WritableValue value = getSingleRootChildWritableValue(XMLTagEntityEnum.STARTINGYEAR
				.getElementName());
		return value;
	}

	public Integer getStartingYear() {
		Integer value = getSingleRootChildIntegerValue(XMLTagEntityEnum.STARTINGYEAR
				.getElementName());
		return value;
	}

	public void setStartingYear(Integer startingYear) {
		putSingleRootChildIntegerValue(XMLTagEntityEnum.STARTINGYEAR
				.getElementName(), startingYear);
	}

	public Integer getNumber(Integer index) {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName());
		Object numberObject = wrappedObject.get(index);
		Integer number = -1;
		if (numberObject instanceof WritableValue) {
			number = (Integer) ((WritableValue) numberObject).doGetValue();
		} else {
			number = (Integer) numberObject;
		}
		return number;
	}

	public int getNumberOfNumbers() {
		log.debug("getNumberOfNumbers() about to return "
				+ ((TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
						.getElementName())).size());
		return ((TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName())).size();
	}

	public WritableValue getObservableNumber(Integer index) {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName());
		ArrayList<AtomicTypeObjectTuple> numberTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
				.get(index);
		log.debug("index:: " + index 
				+ "numberTupleList:: " + numberTupleList);
		Object numberObject = numberTupleList.get(0).getValue();
		WritableValue writableNumber = null;
		if (numberObject instanceof WritableValue) {
			writableNumber = (WritableValue) numberObject;
		}
		return writableNumber;
	}

	
	/**
	 * 
	 * Replaces/changes an existing Number with a new Number for the given year
	 * 
	 * @see nl.rivm.emi.dynamo.data.interfaces.IAmount#putNumber(java.lang.Integer, java.lang.Integer)
	 */
	public Object putNumber(Integer index, Integer number) {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName());
		ArrayList<AtomicTypeObjectTuple> numberTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
				.get(index);
		AtomicTypeObjectTuple numberTuple = numberTupleList.get(0);
		Object currentNumber = numberTuple.getValue();
		if (currentNumber == null) {
			log
					.fatal("!!!!!!!!!!putNumber() may not be used to add numbers!!!!!!!!!!!!");
		}
		// Assumption, always writable.
		WritableValue newNumber = new WritableValue(number, number.getClass());
		numberTuple.setValue(newNumber);
		numberTupleList.remove(0);
		numberTupleList.add(0, numberTuple);
		Object displacedObject = wrappedObject.put(index, numberTupleList);
		return displacedObject;
	}
		

	/**
	 * 
	 * Adds a new Number for the given year
	 * 
	 * @param index The given Year
	 * @param number The Number
	 * @param prefix true if the new value is added as first value
	 *          of the TypedHashMap, false if the new value
	 *          is added at the end of the TypedHashMap.
	 *          This is needed because TypedHashMap is a
	 *          LinkedHashMap, so the order of addition is relevant.
	 *          
	 * @return
	 */
	public Object addNumber(Integer index, Integer number, boolean prefix) {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName());
		ArrayList<AtomicTypeObjectTuple> numberTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
				.get(index);
		
		if (numberTupleList == null) {
			numberTupleList = new ArrayList<AtomicTypeObjectTuple>();
			WritableValue newNumber = new WritableValue(number, number.getClass());
			AtomicTypeObjectTuple numberTuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.NUMBER.getTheType(), newNumber);			
			numberTupleList.add(0, numberTuple);
		}
		Object displacedObject = null;
		if (!prefix) {
			displacedObject = wrappedObject.put(index, numberTupleList);	
		} else {
			TypedHashMap<Year> tempMap = 
				new TypedHashMap(XMLTagEntityEnum.YEAR.getTheType());			
			tempMap.putAll(wrappedObject);
			wrappedObject.clear();
			displacedObject = wrappedObject.put(index, numberTupleList);
			wrappedObject.putAll(tempMap);
		}		
		return displacedObject;
	}
	
	
	/**
	 * 
	 * Removes a Number for the given year
	 * 
	 * @param index
	 * @param number
	 * @return
	 */
	public Object removeNumber(Integer index) {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName());
		ArrayList<AtomicTypeObjectTuple> numberTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
				.get(index);
		Object displacedObject = null;
		if (numberTupleList != null) {
			AtomicTypeObjectTuple numberTuple = numberTupleList.get(0);
			Object currentNumber = numberTuple.getValue();
			if (currentNumber == null) {
				log.fatal("!!!!!!!!!!removeNumber() may not be used to add numbers!!!!!!!!!!!!");
			}
			numberTupleList.remove(0); // Not really needed
			displacedObject = wrappedObject.remove(index);
		}
		// Else nothing can be removed
		return displacedObject;
	}

	public int getPreviousStartingYear() {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName());
		return ((Integer) wrappedObject.keySet().iterator().next()).intValue();
	}

	public boolean isContainsPostfixZeros() {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName());
		int startingYear = ((Integer) getObservableStartingYear().doGetValue()).intValue(); 
		for (int index = (startingYear + 
				 this.getNumberOfNumbers() - 1); index >= startingYear; index-- ) {
			ArrayList<AtomicTypeObjectTuple> numberTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
			.get(index);
			if (numberTupleList != null) {
				AtomicTypeObjectTuple numberTuple = numberTupleList.get(0);
				Integer currentNumber = (Integer) ((WritableValue) numberTuple.getValue()).doGetValue();
				if (index == (startingYear + 
						 this.getNumberOfNumbers() - 1) && currentNumber.intValue() != 0) {
					// No postfix 0s exist
					return false;
				}
				if (currentNumber.intValue() == 0) {
					return true;
				}
			}			
		}
		return false;
	}

	public boolean isContainsZeros() {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNTS
				.getElementName());
		Set keys = wrappedObject.keySet();		
		for (Integer key : (Set<Integer>) keys) {
			ArrayList<AtomicTypeObjectTuple> numberTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
			.get(key);
			if (numberTupleList != null) {
				AtomicTypeObjectTuple numberTuple = numberTupleList.get(0);
				Integer currentNumber = (Integer) ((WritableValue) numberTuple.getValue()).doGetValue();				
				if (currentNumber.intValue() == 0) {
					return true;
				}
			}
		}
		return false;
	}	
	
	
}
