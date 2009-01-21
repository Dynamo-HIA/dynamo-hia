package nl.rivm.emi.dynamo.data.objects.layers;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.interfaces.IFactoryContributor;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceCategory;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceClass;
import nl.rivm.emi.dynamo.data.types.markers.IHandlerType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class ReferenceCategoryObjectImplementation extends DualModeObjectBase implements
		IReferenceCategory, IStaxEventContributor, IFactoryContributor {

	private Object referenceCategory = null;

	public ReferenceCategoryObjectImplementation(boolean makeObservable) {
		super(makeObservable);
	}

	public Integer putReferenceCategory(Integer index) {
		Integer oldReferenceCategory = null;
		if (observable) {
			if (referenceCategory != null) {
				oldReferenceCategory = (Integer) ((WritableValue) referenceCategory)
						.doGetValue();
				((WritableValue) referenceCategory).doSetValue(index);
			} else {
				referenceCategory = new WritableValue(index, Integer.class);
			}
		} else {
			if (referenceCategory != null) {
				oldReferenceCategory = (Integer) referenceCategory;
			}
			referenceCategory = index;
		}
		return oldReferenceCategory;
	}

	public Integer getReferenceCategory() {
		Integer resultCategory = null;
		if (referenceCategory instanceof Integer) {
			resultCategory = (Integer) referenceCategory;
		} else {
			resultCategory = (Integer) ((WritableValue) referenceCategory)
					.doGetValue();
		}
		return resultCategory;
	}

	public WritableValue getObservableReferenceCategory() {
		WritableValue resultCategory = null;
		if (referenceCategory instanceof WritableValue) {
			resultCategory = (WritableValue) referenceCategory;
			}
		return resultCategory;
	}

	public void streamEvents(XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "",
				"referenceclass");
		writer.add(event);
		event = eventFactory.createCharacters(getReferenceCategory().toString());
		writer.add(event);
		event = eventFactory.createEndElement("", "", "referenceclass");
		writer.add(event);
	}

	public void manufactureDefault() {
putReferenceCategory(0);
	}

}
