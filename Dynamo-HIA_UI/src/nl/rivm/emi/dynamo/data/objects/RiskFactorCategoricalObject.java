package nl.rivm.emi.dynamo.data.objects;

/**
 * 
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Index;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorCategoricalObject extends GroupConfigurationObjectServiceLayer
		implements ICategoricalObject, IReferenceClass {
		Log log = LogFactory.getLog(this.getClass().getName());

		public RiskFactorCategoricalObject(LinkedHashMap<String, Object> content) {
			super();
			super.putAll(content);
		}
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
			ArrayList<AtomicTypeObjectTuple> categoryNameTupleList = (ArrayList<AtomicTypeObjectTuple>) wrappedObject.get(index);
			Object categoryNameObject = categoryNameTupleList.get(0).getValue();
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

		public Integer getReferenceClass() {
			return getSingleRootChildIntegerValue(XMLTagEntityEnum.REFERENCECLASS
					.getElementName());
		}

		public WritableValue getObservableReferenceClass() {
			return getSingleRootChildWritableValue(XMLTagEntityEnum.REFERENCECLASS
					.getElementName());
		}

		public Object putReferenceClass(Integer index) {
			return putSingleRootChildIntegerValue(XMLTagEntityEnum.REFERENCECLASS
					.getElementName(), index);
		}
	}
