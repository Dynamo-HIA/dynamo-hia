package nl.rivm.emi.dynamo.data.objects;

import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IMortalityObject;
import nl.rivm.emi.dynamo.data.interfaces.IUnitTypeObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class ExcessMortalityObject extends GroupConfigurationObjectServiceLayer 
	  implements IMortalityObject, IUnitTypeObject {

	/**
	 * Initialize self and copy content.
	 * 
	 * @param manufacturedMap
	 */
	
	// TODO: Stepwise implementation: first as RiskfactorCategoricalFactory
	// ClassDefinitionsDataPanel.java, FileControlEnum, FileControlEnumHelp
	// RiskFactorCategoricalObject, ICategoricalObject
	
	/*
	public ExcessMortalityObject(TypedHashMap<Age> manufacturedMap) {
		super((Age)XMLTagEntityEnum.AGE.getTheType());
		putAll(manufacturedMap);
	}*/
	
	
	Log log = LogFactory.getLog(this.getClass().getName());

	public ExcessMortalityObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
	}

	
	/*
	public String getCategoryName(Integer index) {
		TypedHashMap<Index> wrappedObject = (TypedHashMap<Index>) get(XMLTagEntityEnum.CLASSES
				.getElementName());
		Object categoryNameObject = (String) wrappedObject.get(index);
		String categoryName = "Empty";
		if (categoryNameObject instanceof WritableValue) {
			categoryName = (String) ((WritableValue) categoryNameObject)
					.doGetValue();
		} else {
			categoryName = (String) categoryNameObject;
		}
		return categoryName;
	}

	public WritableValue getObservableCategoryName(Integer index) {
		TypedHashMap<Index> wrappedObject = (TypedHashMap<Index>) get(XMLTagEntityEnum.CLASSES
				.getElementName());
		Object categoryNameObject = wrappedObject.get(index);
		WritableValue writableCategoryName = null;
		if (categoryNameObject instanceof WritableValue) {
			writableCategoryName = (WritableValue) categoryNameObject;
		}
		return writableCategoryName;
	}

	public int getNumberOfCategories() {
		return ((TypedHashMap<Index>) get(XMLTagEntityEnum.CLASSES
				.getElementName())).size();
	}

	public Object putCategory(Integer index, String name) {
		TypedHashMap<Index> wrappedObject = (TypedHashMap<Index>) get(XMLTagEntityEnum.CLASSES
				.getElementName());
		WritableValue newName = new WritableValue(name, name.getClass());
		return wrappedObject.put(index, newName);
	}
*/
	
	
	@Override
	public String getUnitType() {
		return getSingleRootChildStringValue(XMLTagEntityEnum.UNITTYPE
				.getElementName());
	}

	@Override
	public Object putUnitType(String unitType) {
		return putSingleRootChildStringValue(XMLTagEntityEnum.UNITTYPE
				.getElementName(), unitType);
	}


	@Override
	public int getNumberOfMortalities() {		
		return ((TypedHashMap<Age>) get(XMLTagEntityEnum.MORTALITY
				.getElementName())).size();
	}


	@Override
	public WritableValue getObservableUnit(int count) {
		return getSingleRootChildWritableValue(XMLTagEntityEnum.REFERENCECLASS
				.getElementName());
	}

	@Override	
	public Object putMortality(Integer index, String value) {
		TypedHashMap<Index> wrappedObject = (TypedHashMap<Index>) get(XMLTagEntityEnum.MORTALITY
				.getElementName());
		WritableValue newValue = new WritableValue(value, value.getClass());
		return wrappedObject.put(index, newValue);
	}


}
