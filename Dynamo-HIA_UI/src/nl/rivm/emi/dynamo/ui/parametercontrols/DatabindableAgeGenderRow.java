package nl.rivm.emi.dynamo.ui.parametercontrols;

import nl.rivm.emi.dynamo.data.values.AgeWritableValue;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class DatabindableAgeGenderRow {
//	Log log = LogFactory.getLog(this.getClass());
	private IObservable ageValue = new AgeWritableValue();
	private IObservable femaleValue = new WritableValue(null, Float.class);
	private IObservable maleValue = new WritableValue(null, Float.class);
	
	public DatabindableAgeGenderRow() {
		super();
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
