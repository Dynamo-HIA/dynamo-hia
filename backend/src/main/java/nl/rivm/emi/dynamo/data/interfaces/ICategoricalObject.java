package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

public interface ICategoricalObject {

	public abstract Object putCategory(Integer index, String name);

	public abstract String getCategoryName(Integer index);

	public abstract WritableValue getObservableCategoryName(Integer index);
	
	public abstract int getNumberOfCategories();
}