package nl.rivm.emi.dynamo.data.objects;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

public class RRiskFromDiseaseObject  extends TypedHashMap<Age> implements StandardObjectMarker{
	private static final long serialVersionUID = 5813196917287594782L;

	/**
	 * Initialize self and copy content.
	 * @param manufacturedMap
	 */
		public RRiskFromDiseaseObject(TypedHashMap<Age> manufacturedMap) {
			 super((Age)XMLTagEntitySingleton.getInstance().get(Age.getElementName()));
			 putAll(manufacturedMap);
		}
		}
