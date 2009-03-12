package nl.rivm.emi.dynamo.data.objects;

import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IDurationClass;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.DurationClassObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.ReferenceClassObjectImplementation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorCompoundObject implements
		IStaxEventContributor, IReferenceClass, ICategoricalObject,
		IDurationClass {
	Log log = LogFactory.getLog(this.getClass().getName());

	CategoricalObjectImplementation categoricalObjectImplementation;
	ReferenceClassObjectImplementation referenceCategoryObjectImplementation;
	DurationClassObjectImplementation durationClassObjectImplementation;

	public RiskFactorCompoundObject(boolean makeObservable) {
//		super(RootElementNamesEnum.RISKFACTOR_COMPOUND, makeObservable);
		categoricalObjectImplementation = new CategoricalObjectImplementation(
				makeObservable);
		referenceCategoryObjectImplementation = new ReferenceClassObjectImplementation(
				makeObservable);
		durationClassObjectImplementation = new DurationClassObjectImplementation(
				makeObservable);
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
		return categoricalObjectImplementation.putCategory(index, name);
	}

	public Integer getReferenceClass() {
		return referenceCategoryObjectImplementation.getReferenceClass();
	}

	public WritableValue getObservableReferenceClass() {
		return referenceCategoryObjectImplementation
				.getObservableReferenceClass();
	}

	public Object putReferenceClass(Integer index) {
		return referenceCategoryObjectImplementation
				.putReferenceClass(index);
	}

	public Integer getDurationClass() {
		return durationClassObjectImplementation.getDurationClass();
	}

	public WritableValue getObservableDurationClass() {
		return durationClassObjectImplementation.getObservableDurationClass();
	}

	public Object putDurationClass(Integer index) {
		Object result = durationClassObjectImplementation
				.putDurationClass(index);
		return result;
	}

	public void streamEvents(String value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException,
			UnexpectedFileStructureException, IOException {
		// TODO Auto-generated method stub
		
	}

}
