package nl.rivm.emi.dynamo.ui.parametercontrols;

import nl.rivm.emi.dynamo.data.values.AgeWritableValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class DatabindableAgeGenderRow {
//	Log log = LogFactory.getLog(this.getClass());
	IObservable ageValue = new AgeWritableValue();
	IObservable femaleValue = new WritableValue(null, Float.class);
	IObservable maleValue = new WritableValue(null, Float.class);
	
	public DatabindableAgeGenderRow() {
		super();
	}

	public Integer getIntegerAgeValue() {
//		log.debug("Getting ageValue "
//				+ ((Integer)((WritableValue)ageValue).doGetValue()).toString());
		return (Integer) ((WritableValue)ageValue).doGetValue();
	}

	public void setIntegerAgeValue(Integer value) {
//		log.debug("Setting ageValue to "
//				+ value);
	 ((WritableValue) ageValue).doSetValue(value);
	}

	public IObservable getAgeValue() {
		return ageValue;
	}

	public void setAgeValue(IObservable ageValue) {
		this.ageValue = ageValue;
	}

	public IObservable getFemaleValue() {
		return femaleValue;
	}

	public void setFemaleValue(IObservable femaleValue) {
		this.femaleValue = femaleValue;
	}

	public IObservable getMaleValue() {
		return maleValue;
	}

	public void setMaleValue(IObservable maleValue) {
		this.maleValue = maleValue;
	}
}
