package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class DiseasePrevalencesObject extends TypedHashMap<Age> implements
		StandardObjectMarker {
	/**
	 * Initialize self and copy content.
	 * 
	 * @param manufacturedMap
	 */
	public DiseasePrevalencesObject(TypedHashMap<Age> manufacturedMap) {
		super((Age)XMLTagEntityEnum.AGE.getTheType());
		putAll(manufacturedMap);
	}

}
