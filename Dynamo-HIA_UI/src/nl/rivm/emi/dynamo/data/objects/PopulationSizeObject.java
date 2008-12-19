package nl.rivm.emi.dynamo.data.objects;
/**
 * Object to contain the data entered in W11.
 * The Observable contains a nonegative Integer.
 */
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

import org.eclipse.core.databinding.observable.IObservable;

public class PopulationSizeObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public PopulationSizeObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)AtomicTypesSingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
}
