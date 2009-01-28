package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class RelRiskFromRiskFactorCategoricalObject extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = 7531350021240209294L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RelRiskFromRiskFactorCategoricalObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntitySingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
}
