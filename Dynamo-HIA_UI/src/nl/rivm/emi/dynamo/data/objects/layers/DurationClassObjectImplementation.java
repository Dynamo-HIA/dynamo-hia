package nl.rivm.emi.dynamo.data.objects.layers;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.interfaces.IDurationClass;
import nl.rivm.emi.dynamo.data.interfaces.IFactoryContributor;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceCategory;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceClass;
import nl.rivm.emi.dynamo.data.types.markers.IHandlerType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class DurationClassObjectImplementation extends DualModeObjectBase
		implements IDurationClass, IStaxEventContributor, IFactoryContributor {

	private Object durationClass = null;

	public DurationClassObjectImplementation(boolean makeObservable) {
		super(makeObservable);
	}

	public Object putDurationClass(Integer index) {
		Integer oldReferenceCategory = null;
		if (observable) {
			if (durationClass != null) {
				oldReferenceCategory = (Integer) ((WritableValue) durationClass)
						.doGetValue();
				((WritableValue) durationClass).doSetValue(index);
			} else {
				durationClass = new WritableValue(index, Integer.class);
			}
		} else {
			if (durationClass != null) {
				oldReferenceCategory = (Integer) durationClass;
			}
			durationClass = index;
		}
		return oldReferenceCategory;
	}

	public Integer getDurationClass() {
		Integer resultCategory = null;
		if (durationClass instanceof Integer) {
			resultCategory = (Integer) durationClass;
		} else {
			resultCategory = (Integer) ((WritableValue) durationClass)
					.doGetValue();
		}
		return resultCategory;
	}

	public WritableValue getObservableDurationClass() {
		WritableValue resultCategory = null;
		if (durationClass instanceof WritableValue) {
			resultCategory = (WritableValue) durationClass;
		}
		return resultCategory;
	}

	public void streamEvents(XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "",
				"durationclass");
		writer.add(event);
		event = eventFactory.createCharacters(getDurationClass().toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", "durationclass");
		writer.add(event);
	}

	public void manufactureDefault() {
		putDurationClass(0);
	}

}
