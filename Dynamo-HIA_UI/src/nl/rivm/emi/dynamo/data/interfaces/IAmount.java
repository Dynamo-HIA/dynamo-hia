package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * @author mondeelr
 *
 */
public interface IAmount {

	public abstract Object putNumber(Integer index, Integer number);

	public abstract Integer getNumber(Integer index);

	public abstract WritableValue getObservableNumber(Integer index);
	
	public abstract int getNumberOfAmounts();
}