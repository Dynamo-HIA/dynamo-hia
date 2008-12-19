package nl.rivm.emi.dynamo.data.objects;
/**
 * Object to contain the data entered in W14.
 * The Observable contains a nonnegative Integer.
 */

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class OverallDALYWeightsObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = -1973812253427654652L;
	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public OverallDALYWeightsObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)AtomicTypesSingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
		}
