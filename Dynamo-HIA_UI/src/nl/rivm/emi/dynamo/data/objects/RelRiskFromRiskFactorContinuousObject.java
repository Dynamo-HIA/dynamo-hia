package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.AbstractAge;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class RelRiskFromRiskFactorContinuousObject   extends TypedHashMap<Age> implements StandardObjectMarker{

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RelRiskFromRiskFactorContinuousObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}

}
