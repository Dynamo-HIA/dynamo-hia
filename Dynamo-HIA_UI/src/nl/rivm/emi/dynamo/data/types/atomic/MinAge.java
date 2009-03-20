package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class MinAge extends AbstractAge implements PayloadType<Integer> {
	static final protected String XMLElementName = "minage";

	public MinAge() {
		super(XMLElementName);
	}
}
