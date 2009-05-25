package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class DurationDistributionObject extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = 559843387625531336L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public DurationDistributionObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
}
