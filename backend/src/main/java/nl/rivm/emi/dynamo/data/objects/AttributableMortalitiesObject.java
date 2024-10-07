package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

/**
 * Object to contain the data entered in W12.
 * The Observable contains a nonegative Float with eight decimals.
 */

public class AttributableMortalitiesObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = -1973812253427654652L;
	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public AttributableMortalitiesObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
}
