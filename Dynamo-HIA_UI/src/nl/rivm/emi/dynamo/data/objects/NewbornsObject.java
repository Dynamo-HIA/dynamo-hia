package nl.rivm.emi.dynamo.data.objects;

/**
 * Object to contain the data entered in W14.
 * The Observable contains a nonnegative Integer.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNT
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
				+ ((TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNT
						.getElementName())).size());
		return ((TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNT
				.getElementName())).size();
	}

	public WritableValue getObservableNumber(Integer index) {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNT
				.getElementName());
		ArrayList<AtomicTypeObjectTuple> numberTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
				.get(index);
		Object numberObject = numberTupleList.get(0).getValue();
		WritableValue writableNumber = null;
		if (numberObject instanceof WritableValue) {
			writableNumber = (WritableValue) numberObject;
		}
		return writableNumber;
	}

	public Object putNumber(Integer index, Integer number) {
		TypedHashMap<Year> wrappedObject = (TypedHashMap<Year>) get(XMLTagEntityEnum.AMOUNT
				.getElementName());
		ArrayList<AtomicTypeObjectTuple> numberTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
				.get(index);
		AtomicTypeObjectTuple numberTuple = numberTupleList.get(0);
		Object currentNumber = numberTuple.getValue();
		if (currentNumber == null) {
			log
					.fatal("!!!!!!!!!!putCategory() may not be used to add categories!!!!!!!!!!!!");
		}
		// Assumption, always writable.
		WritableValue newNumber = new WritableValue(number, number.getClass());
		numberTuple.setValue(newNumber);
		numberTupleList.remove(0);
		numberTupleList.add(0, numberTuple);
		Object displacedObject = wrappedObject.put(index, numberTupleList);
		return displacedObject;
	}
}
