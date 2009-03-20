package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.AbstractAge;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class RiskFactorCategoricalPrevalencesObject extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = -3275109890758726865L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RiskFactorCategoricalPrevalencesObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
}
