package nl.rivm.emi.dynamo.data.objects;
// TODO Hacked to ErrorLessNess.
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceValue;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.objects.layers.ReferenceValueObjectImplementation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorContinuousObject implements
		IStaxEventContributor, IReferenceValue {
	Log log = LogFactory.getLog(this.getClass().getName());

	ReferenceValueObjectImplementation referenceValueObjectImplementation;

	public RiskFactorContinuousObject(boolean makeObservable) {
//		super(RootElementNamesEnum.RISKFACTOR_CONTINUOUS, makeObservable);
		referenceValueObjectImplementation = new ReferenceValueObjectImplementation(
				makeObservable);
	}

	public Float getReferenceValue() {
		return referenceValueObjectImplementation.getReferenceValue();
	}

	public WritableValue getObservableReferenceValue() {
		return referenceValueObjectImplementation
				.getObservableReferenceValue();
	}

	public Object putReferenceValue(Float value) {
		return referenceValueObjectImplementation.putReferenceValue(value);
	}

	public void streamEvents(String value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException,
			UnexpectedFileStructureException, IOException {
		// TODO Auto-generated method stub
		
	}
}
