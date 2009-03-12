package nl.rivm.emi.dynamo.data.objects;
/**
 *  TODO Refactor to new modelObject.
 *   
 * Model Object for the configuration of a categorical riskfactor.
 */
import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.ReferenceClassObjectImplementation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorCategoricalObject implements IReferenceClass, ICategoricalObject {
	Log log = LogFactory.getLog(this.getClass().getName());

	CategoricalObjectImplementation categoricalObjectImplementation;
	ReferenceClassObjectImplementation referenceCategoryObjectImplementation;

	public RiskFactorCategoricalObject(boolean makeObservable) {
//		super(RootElementNamesEnum.RISKFACTOR_CATEGORICAL, makeObservable);
		categoricalObjectImplementation = new CategoricalObjectImplementation(
				makeObservable);
//		referenceCategoryObjectImplementation = new ReferenceCategoryObjectImplementation(
//				makeObservable);
	}

	public String getCategoryName(Integer index) {
		return categoricalObjectImplementation.getCategoryName(index);
	}

	public WritableValue getObservableCategoryName(Integer index) {
		return categoricalObjectImplementation.getObservableCategoryName(index);
	}

	public int getNumberOfCategories() {
		return categoricalObjectImplementation.getNumberOfCategories();
	}

	/**
	 * NB makeObservable is a don't care here!
	 */
	public Object putCategory(Integer index, String name) {
		return categoricalObjectImplementation.putCategory(index,
				name);
	}

	public Integer getReferenceClass() {
		return referenceCategoryObjectImplementation.getReferenceClass();
	}

	public WritableValue getObservableReferenceCategory() {
//		return referenceCategoryObjectImplementation
//				.getObservableReferenceCategory();
return null;
	}
	
	public Object putReferenceClass(Integer index) {
		return referenceCategoryObjectImplementation
				.putReferenceClass(index);
	}

	public WritableValue getObservableReferenceClass() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Create a modelObject from an XML configurationfile.
	 * 
	 * @param dataFilePath
	 * @return RiskFactorCategoricalObject data representation of the xml
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
}
