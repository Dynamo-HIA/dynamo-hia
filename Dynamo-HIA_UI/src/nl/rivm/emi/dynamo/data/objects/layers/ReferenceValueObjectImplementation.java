package nl.rivm.emi.dynamo.data.objects.layers;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.interfaces.IFactoryContributor;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceValue;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;

import org.eclipse.core.databinding.observable.value.WritableValue;

public class ReferenceValueObjectImplementation extends DualModeObjectBase
		implements IReferenceValue, IStaxEventContributor, IFactoryContributor {

	private Object referenceValue = null;

	public ReferenceValueObjectImplementation(boolean makeObservable) {
		super(makeObservable, "bogus");
	}

	public Float putReferenceValue(Float value) {
		Float oldReferenceValue = null;
		if (observable) {
			if (referenceValue != null) {
				oldReferenceValue = (Float) ((WritableValue) referenceValue)
						.doGetValue();
				((WritableValue) referenceValue).doSetValue(value);
			} else {
				referenceValue = new WritableValue(value, Float.class);
			}
		} else {
			if (referenceValue != null) {
				oldReferenceValue = (Float) referenceValue;
			}
			referenceValue = value;
		}
		return oldReferenceValue;
	}

	public Float getReferenceValue() {
		Float resultValue = null;
		if (referenceValue instanceof Integer) {
			resultValue = (Float) referenceValue;
		} else {
			resultValue = (Float) ((WritableValue) referenceValue).doGetValue();
		}
		return resultValue;
	}

	public WritableValue getObservableReferenceValue() {
		WritableValue resultCategory = null;
		if (referenceValue instanceof WritableValue) {
			resultCategory = (WritableValue) referenceValue;
		}
		return resultCategory;
	}

	public void streamEvents(String value, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "",
				"referencevalue");
		writer.add(event);
		event = eventFactory.createCharacters(getReferenceValue().toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", "referencevalue");
		writer.add(event);
	}

	public void manufactureDefault() {
		putReferenceValue(0F);
	}

}
