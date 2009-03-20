package nl.rivm.emi.dynamo.data.objects;
/**
 * Object to contain the data entered in W14.
 * The Observable contains a nonnegative Integer.
 */

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Year;

/**
 * 
 * Construction Object for Year of Newborns
 * 
 * @author schutb
 *
 */
public class NewbornsObject  extends TypedHashMap<Year> implements StandardObjectMarker{
	private static final long serialVersionUID = -1973812253427654652L;
	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public NewbornsObject(TypedHashMap<Year> manufacturedMap) {
			 super((Year)XMLTagEntitySingleton.getInstance().get(((Year)XMLTagEntityEnum.YEAR.getTheType()).getElementName()));
			 putAll(manufacturedMap);
		}
}
