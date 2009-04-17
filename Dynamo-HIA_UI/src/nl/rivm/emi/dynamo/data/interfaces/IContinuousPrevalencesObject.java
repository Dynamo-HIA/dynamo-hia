package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public interface IContinuousPrevalencesObject {
	public abstract TypedHashMap<Age> getPrevalences();

}
