package nl.rivm.emi.dynamo.data.objects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.cdm.exceptions.UnexpectedFileStructureException;
import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceCategory;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.objects.layers.ReferenceCategoryObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.StaxWriterEntryPoint;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.markers.IHandlerType;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

public class RiskFactorCategoricalObject extends StaxWriterEntryPoint implements
		IStaxEventContributor, IReferenceCategory, ICategoricalObject {
	Log log = LogFactory.getLog(this.getClass().getName());

	CategoricalObjectImplementation categoricalObjectImplementation;
	ReferenceCategoryObjectImplementation referenceCategoryObjectImplementation;

	public RiskFactorCategoricalObject(boolean makeObservable) {
		super(RootElementNamesEnum.RISKFACTOR_CATEGORICAL, makeObservable);
		categoricalObjectImplementation = new CategoricalObjectImplementation(makeObservable);
		referenceCategoryObjectImplementation = new ReferenceCategoryObjectImplementation(makeObservable);
	}

	public String getCategoryName(Integer index) {
		return categoricalObjectImplementation.getCategoryName(index);
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

	public Integer getReferenceCategory() {
		return referenceCategoryObjectImplementation.getReferenceCategory();
	}

	public Object putReferenceCategory(Integer index) {
		return referenceCategoryObjectImplementation.putReferenceCategory(
				index);
	}

	/**
	 * Create a modelObject from an XML configurationfile.
	 * 
	 * @param modelObject
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	// public RiskFactorCategoricalObject manufacture(
	// RiskFactorCategoricalObject modelObject, File configurationFile)
	// throws ConfigurationException, DynamoInconsistentDataException {
	// log.debug("Starting manufacture.");
	// modelObject =
	// categoricalObjectImplementation.(RiskFactorCategoricalObject)
	// manufacture(modelObject,
	// configurationFile);
	// return modelObject;
	// }
	protected ConfigurationObjectBase handleRootChildren(
			ConfigurationObjectBase modelObject,
			List<ConfigurationNode> rootChildren) throws ConfigurationException {
		for (ConfigurationNode rootChild : rootChildren) {
			String childName = rootChild.getName();
			log.debug("Handle rootChild: " + childName);
			XMLTagEntity entity = XMLTagEntitySingleton.getInstance().get(
					childName);
			if ((entity != null) && (entity instanceof IHandlerType)) {
				modelObject = ((IHandlerType) entity).handle(modelObject,
						rootChild);
			}
		}
		return modelObject;
	}

	@Override
	public ConfigurationObjectBase manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		return manufacture(this, configurationFile);
	}

	@Override
	public ConfigurationObjectBase manufactureDefault()
			throws ConfigurationException {
		return null;
	}

	// write
	public void streamEvents(XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		event = eventFactory.createStartElement("", "", rootElement
				.getNodeLabel());
		writer.add(event);
		categoricalObjectImplementation.streamEvents(writer, eventFactory);
		referenceCategoryObjectImplementation
				.streamEvents(writer, eventFactory);
		event = eventFactory.createEndElement("", "", rootElement
				.getNodeLabel());
		writer.add(event);
		event = eventFactory.createEndDocument();
		writer.add(event);
	}
}
