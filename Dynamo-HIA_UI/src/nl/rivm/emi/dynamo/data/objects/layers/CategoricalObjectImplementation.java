package nl.rivm.emi.dynamo.data.objects.layers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.eclipse.core.databinding.observable.value.WritableValue;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IFactoryContributor;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.types.atomic.Classes;
import nl.rivm.emi.dynamo.data.types.markers.IHandlerType;

public class CategoricalObjectImplementation extends DualModeObjectBase
		implements IStaxEventContributor, ICategoricalObject,
		IFactoryContributor {

	LinkedHashMap<Integer, Object> theClasses = new LinkedHashMap<Integer, Object>();

	public CategoricalObjectImplementation(boolean makeObservable) {
		super(makeObservable);
	}

	public Object putCategory(Integer index, String name) {
		Object result = null;
		if (!observable) {
			result = theClasses.put(index, name);
		} else {
			WritableValue value = new WritableValue(name, String.class);
			result = theClasses.put(index, value);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectInterface#
	 * getCategoryName(java.lang.Integer)
	 */
	public String getCategoryName(Integer index) {
		String result = null;
		Object valueObject = theClasses.get(index);
		if (valueObject != null) {
			if (valueObject instanceof String) {
				result = (String) valueObject;
			} else {
				result = (String) ((WritableValue) valueObject).doGetValue();
			}
		}
		return result;
	}

	public WritableValue getObservableCategoryName(Integer index) {
		WritableValue result = null;
		Object valueObject = theClasses.get(index);
		if ((valueObject != null) && (valueObject instanceof WritableValue)) {
			result = (WritableValue) valueObject;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectInterface#
	 * getNumberOfCategories()
	 */
	public int getNumberOfCategories() {
		return theClasses.size();
	}

	// public ConfigurationObjectBase handle(ConfigurationObjectBase
	// modelObject,
	// ConfigurationNode node) throws ConfigurationException {
	// return xmlTagHandler.handle(modelObject, node);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectInterface#
	 * streamReferenceCategoryEvents(javax.xml.stream.XMLEventWriter,
	 * javax.xml.stream.XMLEventFactory)
	 */
	public void streamEvents(XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "", "classes");
		writer.add(event);
		int found = 0;
		for (int count = 1; (found < theClasses.size()) && (count <= theClasses.size()); count++) {
			Integer index = new Integer(count);
			Object value = theClasses.get(index);
			String name = null;
			name = getCategoryName(index);
			if ((name != null)&&(!name.equals(""))) {
				found++;
				event = eventFactory.createStartElement("", "", "class");
				writer.add(event);
				event = eventFactory.createStartElement("", "", "index");
				writer.add(event);
				event = eventFactory.createCharacters(index.toString());
				writer.add(event);
				event = eventFactory.createEndElement("", "", "index");
				writer.add(event);
				event = eventFactory.createStartElement("", "", "name");
				writer.add(event);
				event = eventFactory.createCharacters(name);
				writer.add(event);
				event = eventFactory.createEndElement("", "", "name");
				writer.add(event);
				event = eventFactory.createEndElement("", "", "class");
				writer.add(event);
			} else {
				continue;
			}
		}
		event = eventFactory.createEndElement("", "", "classes");
		writer.add(event);
	}

	public void manufactureDefault() {
		for (int count = 1; count < 10; count++) {
			putCategory(count, "");
		}
	}
}