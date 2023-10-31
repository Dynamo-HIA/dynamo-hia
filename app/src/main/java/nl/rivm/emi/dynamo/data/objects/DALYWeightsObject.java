package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class DALYWeightsObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = 664454848388037813L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public DALYWeightsObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
		}
