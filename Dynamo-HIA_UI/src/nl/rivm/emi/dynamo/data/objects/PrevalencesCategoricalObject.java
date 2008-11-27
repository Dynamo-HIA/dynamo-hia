package nl.rivm.emi.dynamo.data.objects;
/**
 * Wrapper Class to clarify the relations in the sourcecode.
 * Object to contain the data entered in W22 for categorical 
 * and the categorical part of the compound prevalences.
 * 
 * @author mondeelr
 *
 */
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;

public class PrevalencesCategoricalObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = -3022294026093628326L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public PrevalencesCategoricalObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)AtomicTypesSingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
		}
