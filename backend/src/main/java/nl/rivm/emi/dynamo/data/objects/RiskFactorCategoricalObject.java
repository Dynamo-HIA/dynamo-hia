package nl.rivm.emi.dynamo.data.objects;

/**
 * 
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.FlexDex;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorCategoricalObject extends
		GroupConfigurationObjectServiceLayer implements ICategoricalObject,
		IReferenceClass {
	/**
	     * 
	     */
	    private static final long serialVersionUID = 1L;
	Log log = LogFactory.getLog(this.getClass().getName());

	public RiskFactorCategoricalObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
	}

	@SuppressWarnings("rawtypes")
	public String getCategoryName(Integer index) {
		@SuppressWarnings("unchecked")
		TypedHashMap<FlexDex> wrappedObject = (TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CLASSES
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

	@SuppressWarnings("rawtypes")
	public WritableValue getObservableCategoryName(Integer index) {
		@SuppressWarnings("unchecked")
		TypedHashMap<FlexDex> wrappedObject = (TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CLASSES
				.getElementName());
		@SuppressWarnings("unchecked")
		ArrayList<AtomicTypeObjectTuple> categoryNameTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
				.get(index);
		Object categoryNameObject = categoryNameTupleList.get(0).getValue();
		
		WritableValue writableCategoryName = null;
		if (categoryNameObject instanceof WritableValue) {
			writableCategoryName = (WritableValue) categoryNameObject;
		}
		return writableCategoryName;
	}

	@SuppressWarnings("unchecked")
	public int getNumberOfCategories() {
		log.debug("getNumberOfClasses() about to return " + ((TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CLASSES
				.getElementName())).size());
		return ((TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CLASSES
				.getElementName())).size();
	}

	public Object putCategory(Integer index, String name) {
		@SuppressWarnings("unchecked")
		TypedHashMap<FlexDex> wrappedObject = (TypedHashMap<FlexDex>) get(XMLTagEntityEnum.CLASSES
				.getElementName());
		@SuppressWarnings("unchecked")
		ArrayList<AtomicTypeObjectTuple> categoryNameTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject
		.get(index);
		AtomicTypeObjectTuple currentCategoryTuple = categoryNameTupleList.get(0);
		Object currentCategory = currentCategoryTuple.getValue();
		if (currentCategory == null) {
			log
					.warn("!!!!!!!!!!putCategory() may not be used to add categories!!!!!!!!!!!!");
		}
		// Assumption, always writable.
		@SuppressWarnings({ "unchecked", "rawtypes" })
		WritableValue newName = new WritableValue(name, name.getClass());
		currentCategoryTuple.setValue(newName);
		categoryNameTupleList.remove(0);
		categoryNameTupleList.add(0, currentCategoryTuple);
		Object displacedObject = wrappedObject.put(index, categoryNameTupleList);
		
		return displacedObject;
	}

	public Integer getReferenceClass() {
		return getSingleRootChildIntegerValue(XMLTagEntityEnum.REFERENCECLASS
				.getElementName());
	}

	@SuppressWarnings("rawtypes")
	public WritableValue getObservableReferenceClass() throws DynamoConfigurationException {
		
		WritableValue value=null;
		try {
			 value = getSingleRootChildWritableValue(XMLTagEntityEnum.REFERENCECLASS
					.getElementName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	public Object putReferenceClass(Integer index) {
		return putSingleRootChildIntegerValue(XMLTagEntityEnum.REFERENCECLASS
				.getElementName(), index);
	}
}
