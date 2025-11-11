package nl.rivm.emi.dynamo.data.objects;
/**
 * Object to contain the data entered in W11.
 * The Observable contains a nonegative Integer.
 */
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class PopulationSizeObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public PopulationSizeObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntityEnum.AGE.getTheType());
			 putAll(manufacturedMap);
		}
}
