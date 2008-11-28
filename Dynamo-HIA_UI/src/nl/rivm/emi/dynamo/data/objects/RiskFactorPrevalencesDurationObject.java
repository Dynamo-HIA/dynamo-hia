package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;

public class RiskFactorPrevalencesDurationObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = 8666339552453771280L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RiskFactorPrevalencesDurationObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)AtomicTypesSingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
}
