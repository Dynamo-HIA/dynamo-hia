package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;

public class RelRiskFromOtherDiseaseObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RelRiskFromOtherDiseaseObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)AtomicTypesSingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
		}
