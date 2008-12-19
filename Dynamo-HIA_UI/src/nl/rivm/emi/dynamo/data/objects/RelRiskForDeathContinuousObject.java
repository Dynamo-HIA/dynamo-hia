package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class RelRiskForDeathContinuousObject extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = -4041827797520696115L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RelRiskForDeathContinuousObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)AtomicTypesSingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
}
