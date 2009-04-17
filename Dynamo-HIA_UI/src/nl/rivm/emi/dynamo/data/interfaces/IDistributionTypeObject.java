package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface IDistributionTypeObject {

	Object putDistributionType(String unitType);

	String getDistributionType();

	public WritableValue getObservableDistributionType();

}
