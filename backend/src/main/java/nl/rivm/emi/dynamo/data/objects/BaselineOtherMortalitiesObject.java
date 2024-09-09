package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

/**
 * Object to contain the data entered in W12.
 * The Observable contains a nonegative Float with eight decimals.
 */

public class BaselineOtherMortalitiesObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895034975638017919L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public BaselineOtherMortalitiesObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
}
