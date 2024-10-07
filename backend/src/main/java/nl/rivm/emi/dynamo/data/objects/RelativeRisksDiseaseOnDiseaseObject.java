package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

/**
 * Object to contain the data of a file with estimated parameters.
 */

public class RelativeRisksDiseaseOnDiseaseObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = -8392472633288157632L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RelativeRisksDiseaseOnDiseaseObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
}
