package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;

public class DALYWeightsObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = 664454848388037813L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public DALYWeightsObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)AtomicTypesSingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
		}
