package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class DiseaseIncidencesObject extends TypedHashMap<Age> implements
		StandardObjectMarker {
	/**
	 * Initialize self and copy content.
	 * 
	 * @param manufacturedMap
	 */
	public DiseaseIncidencesObject(TypedHashMap<Age> manufacturedMap) {
		super((Age) AtomicTypesSingleton.getInstance()
				.get(Age.getElementName()));
		putAll(manufacturedMap);
	}

}