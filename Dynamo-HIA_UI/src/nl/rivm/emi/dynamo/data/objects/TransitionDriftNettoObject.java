package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Trend;

/**
 * Object to contain the data entered in W12.
 * The Observable contains a nonegative Float with eight decimals.
 */

public class TransitionDriftNettoObject  extends TypedHashMap<Trend> implements StandardObjectMarker{
	private static final long serialVersionUID = -1973812253427654652L;
	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
	public TransitionDriftNettoObject(TypedHashMap<Trend> manufacturedMap) {
		 super((Trend)XMLTagEntitySingleton.getInstance().get(XMLTagEntityEnum.TREND.getElementName()));
		 putAll(manufacturedMap);
	}
}
