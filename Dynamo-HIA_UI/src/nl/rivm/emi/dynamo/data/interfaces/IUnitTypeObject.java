package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IUnitTypeObject {

	Object putUnitType(String unitType);

	String getUnitType();

	public WritableValue getObservableUnitType();

}
