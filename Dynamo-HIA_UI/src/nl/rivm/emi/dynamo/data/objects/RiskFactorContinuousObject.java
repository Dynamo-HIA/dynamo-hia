package nl.rivm.emi.dynamo.data.objects;

/**
 * The putters are designed to insert Observables only!!!!!!
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ICutoffs;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceValue;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.FlexDex;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorContinuousObject extends
		GroupConfigurationObjectServiceLayer implements ICutoffs, IReferenceValue {
	private static final long serialVersionUID = -3767037849437274799L;

	Log log = LogFactory.getLog(this.getClass().getName());

	public RiskFactorContinuousObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
	}

	public Float getReferenceValue() {
		return getSingleRootChildFloatValue(XMLTagEntityEnum.REFERENCEVALUE
				.getElementName());
	}

	public WritableValue getObservableReferenceValue() {
		return getSingleRootChildWritableValue(XMLTagEntityEnum.REFERENCEVALUE
				.getElementName());
	}

	public Object putReferenceValue(Float value) {
		return putSingleRootChildFloatValue(XMLTagEntityEnum.REFERENCEVALUE
				.getElementName(), value);
	}

	public Float getCutoffValue(Integer index) {
		TypedHashMap<FlexDex> wrappedObject = (TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CUTOFFS
				.getElementName());
		Object cutoffValueObject = (String) wrappedObject.get(index);
		Float cutoffValue = null;
		if (cutoffValueObject instanceof WritableValue) {
			cutoffValue = (Float) ((WritableValue) cutoffValueObject)
					.doGetValue();
		} else {
			cutoffValue = (Float) cutoffValueObject;
		}
		return cutoffValue;
	}

	public int getNumberOfCutoffs() {
		TypedHashMap<FlexDex> cutoffs =((TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CUTOFFS
				.getElementName()));
		int numberOfCutoffs = ((cutoffs == null)? 0 : cutoffs.size());
		log.debug("getNumberOfCutoffs() about to return " + numberOfCutoffs);
		return numberOfCutoffs;
}

	public WritableValue getObservableCutoffValue(Integer index) {
		TypedHashMap<FlexDex> wrappedObject = (TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CUTOFFS
				.getElementName());
		ArrayList<AtomicTypeObjectTuple> cutoffValuesTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
				.get(index);
		Object cutoffValueObject = cutoffValuesTupleList.get(0).getValue();
		WritableValue writableCutoffValue = null;
		if (cutoffValueObject instanceof WritableValue) {
			writableCutoffValue = (WritableValue) cutoffValueObject;
		}
		return writableCutoffValue;
}

	public Object putCutoff(Integer index, Float value) {
		TypedHashMap<FlexDex> wrappedObject = (TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CUTOFFS
				.getElementName());
		ArrayList<AtomicTypeObjectTuple> cutoffValueTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
		.get(index);
		AtomicTypeObjectTuple currentCutoffValueTuple = cutoffValueTupleList.get(0);
		Object currentCutoffValue = currentCutoffValueTuple.getValue();
		if (currentCutoffValue == null) {
			log
					.warn("!!!!!!!!!!putCutoff() may not be used to add cutoffs!!!!!!!!!!!!");
		}
		// Assumption, always writable.
		WritableValue newValue = new WritableValue(value, value.getClass());
		currentCutoffValueTuple.setValue(newValue);
		cutoffValueTupleList.remove(0);
		cutoffValueTupleList.add(0, currentCutoffValueTuple);
		Object displacedObject = wrappedObject.put(index, cutoffValueTupleList);
		return displacedObject;
	}
}
