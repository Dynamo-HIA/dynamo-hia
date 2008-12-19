package nl.rivm.emi.dynamo.data.objects;
/**
 * Wrapper Class to clarify the relations in the sourcecode.
 * Object to contain the data entered in W32.
 */
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class IncidencesObject extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = -1973812253427654652L;
/**
 * Initialize self and copy content.
 * @param manufacturedMap
 */
	public IncidencesObject(TypedHashMap<Age> manufacturedMap) {
		 super((Age)AtomicTypesSingleton.getInstance().get(Age.getElementName()));
		 putAll(manufacturedMap);
	}
	
}
