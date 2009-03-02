package nl.rivm.emi.dynamo.data.objects;
/**
 * Model Object for the configuration of a categorical riskfactor.
 */
import java.io.File;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.factories.XMLHandlingEntryPoint;
import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceClass;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.ReferenceClassObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.StaxWriterEntryPoint;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorCategoricalObject extends XMLHandlingEntryPoint implements
		/* IXMLHandlingLayer, */ IReferenceClass, ICategoricalObject {
	Log log = LogFactory.getLog(this.getClass().getName());

	CategoricalObjectImplementation categoricalObjectImplementation;
	ReferenceClassObjectImplementation referenceCategoryObjectImplementation;

	public RiskFactorCategoricalObject(boolean makeObservable) {
		super(RootElementNamesEnum.RISKFACTOR_CATEGORICAL, makeObservable);
		categoricalObjectImplementation = new CategoricalObjectImplementation(
				makeObservable);
		referenceCategoryObjectImplementation = new ReferenceCategoryObjectImplementation(
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
		return categoricalObjectImplementation.putCategory(index,
				name);
	}

	public Integer getReferenceCategory() {
		return referenceCategoryObjectImplementation.getReferenceCategory();
	}

	public WritableValue getObservableReferenceCategory() {
		return referenceCategoryObjectImplementation
				.getObservableReferenceCategory();
	}
	
	public Object putReferenceCategory(Integer index) {
		return referenceCategoryObjectImplementation
				.putReferenceCategory(index);
	}

	/**
	 * Create a modelObject from an XML configurationfile.
	 * 
	 * @param dataFilePath
	 * @return RiskFactorCategoricalObject data representation of the xml
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	@Override
	public RiskFactorCategoricalObject manufacture(String dataFilePath)
	 throws ConfigurationException, DynamoInconsistentDataException {
		this.log.debug("Starting manufacture.");
		manufacture(this, dataFilePath);
	 return this;
	 }
	protected ConfigurationObjectBase handleRootChildren(
			ConfigurationObjectBase modelObject,
			List<ConfigurationNode> rootChildren) throws ConfigurationException {
	if(rootChildren != null){
		for (ConfigurationNode rootChild : rootChildren) {
			String childName = rootChild.getName();
			log.debug("Handle rootChild: " + childName);
			XMLTagEntity entity = XMLTagEntitySingleton.getInstance().get(
					childName);
			if ((entity != null) && (entity instanceof IXMLHandlingLayer)) {
				modelObject = ((IXMLHandlingLayer) entity).handle(modelObject,
						rootChild);
			} else {
				throw new ConfigurationException("Unhandled rootChild element: " + childName);
			}
		}
	} else {
		categoricalObjectImplementation.manufactureDefault();
		referenceCategoryObjectImplementation.manufactureDefault();
	}
		return modelObject;
	}

	// write
	public void streamEvents(String value, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
		event = eventFactory.createStartElement("", "", rootElement
				.getNodeLabel());
		writer.add(event);
		categoricalObjectImplementation.streamEvents(writer, eventFactory);
		referenceCategoryObjectImplementation
				.streamEvents(null, writer, eventFactory);
		event = eventFactory.createEndElement("", "", rootElement
				.getNodeLabel());
		writer.add(event);
		event = eventFactory.createEndDocument();
		writer.add(event);
	}

	public Float handle(ConfigurationNode node) throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDefault() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fillHandlers(boolean observable) {
		// TODO Auto-generated method stub
		
	}

}
