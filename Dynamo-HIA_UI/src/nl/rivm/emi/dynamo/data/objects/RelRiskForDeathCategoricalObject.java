package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class RelRiskForDeathCategoricalObject extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = 2491255194115037128L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RelRiskForDeathCategoricalObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
}
