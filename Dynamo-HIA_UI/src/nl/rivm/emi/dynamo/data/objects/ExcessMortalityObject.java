package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class ExcessMortalityObject extends TypedHashMap<Age> implements
		StandardObjectMarker {

	/**
	 * Initialize self and copy content.
	 * 
	 * @param manufacturedMap
	 */
	public ExcessMortalityObject(TypedHashMap<Age> manufacturedMap) {
		super((Age) XMLTagEntitySingleton.getInstance().get(
				Age.getElementName()));
		putAll(manufacturedMap);
	}
}
