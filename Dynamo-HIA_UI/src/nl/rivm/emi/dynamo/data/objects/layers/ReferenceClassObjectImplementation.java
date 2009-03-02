package nl.rivm.emi.dynamo.data.objects.layers;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.interfaces.IFactoryContributor;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;

import org.eclipse.core.databinding.observable.value.WritableValue;

public class ReferenceClassObjectImplementation extends DualModeObjectBase
		implements IReferenceClass, IStaxEventContributor, IFactoryContributor {

	final XMLTagEntity myType = XMLTagEntityEnum.REFERENCECLASS.getTheType();

	private Object referenceClass = null;

	public ReferenceClassObjectImplementation(boolean makeObservable) {
		super(makeObservable, "bogus");
	}

	public Integer putReferenceCategory(Integer index) {
		Integer oldReferenceCategory = null;
		if (observable) {
			if (referenceClass != null) {
				oldReferenceCategory = (Integer) ((WritableValue) referenceClass)
						.doGetValue();
				((WritableValue) referenceClass).doSetValue(index);
			} else {
				referenceClass = new WritableValue(index, Integer.class);
			}
		} else {
			if (referenceClass != null) {
				oldReferenceCategory = (Integer) referenceClass;
			}
			referenceClass = index;
		}
		return oldReferenceCategory;
	}

	public Integer getReferenceCategory() {
		Integer resultClass = null;
		if (referenceClass instanceof Integer) {
			resultClass = (Integer) referenceClass;
		} else {
			resultClass = (Integer) ((WritableValue) referenceClass)
					.doGetValue();
		}
		return resultClass;
	}

	public WritableValue getObservableReferenceClass() {
		WritableValue resultCategory = null;
		if (referenceClass instanceof WritableValue) {
			resultCategory = (WritableValue) referenceClass;
		}
		return resultCategory;
	}

	public void streamEvents(String value, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "", myType
				.getXMLElementName());
		writer.add(event);
		event = eventFactory
				.createCharacters(getReferenceClass().toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", myType
				.getXMLElementName());
		writer.add(event);
	}

	public void manufactureDefault() {
		putReferenceClass(1);
	}

}
