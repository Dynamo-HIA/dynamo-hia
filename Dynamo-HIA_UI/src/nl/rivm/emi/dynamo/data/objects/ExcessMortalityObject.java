package nl.rivm.emi.dynamo.data.objects;

import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IMortalityObject;
import nl.rivm.emi.dynamo.data.interfaces.IUnitTypeObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class ExcessMortalityObject extends GroupConfigurationObjectServiceLayer
		implements IMortalityObject, IUnitTypeObject {
	private static final long serialVersionUID = 3681891491526136721L;
	/**
	 * Initialize self and copy content.
	 * 
	 * @param manufacturedMap
	 */

	Log log = LogFactory.getLog(this.getClass().getName());

	public ExcessMortalityObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
	}

	public String getUnitType() {
		return getSingleRootChildStringValue(XMLTagEntityEnum.UNITTYPE
				.getElementName());
	}

	public WritableValue getObservableUnitType() {
		return getSingleRootChildWritableValue(XMLTagEntityEnum.UNITTYPE
				.getElementName());
	}

	public Object putUnitType(String unitType) {
		return putSingleRootChildStringValue(XMLTagEntityEnum.UNITTYPE
				.getElementName(), unitType);
	}

	@SuppressWarnings("unchecked")
	public TypedHashMap<Age> getMortalities() {
		TypedHashMap<Age> wrappedObject = (TypedHashMap<Age>) get(
		/*
		 * Alternative, non-conforming version. */ XMLTagEntityEnum.MORTALITIES
		  .getElementName()
		 /*
		XMLTagEntityEnum.MORTALITY.getElementName() */ );
		return wrappedObject;
	}
}
