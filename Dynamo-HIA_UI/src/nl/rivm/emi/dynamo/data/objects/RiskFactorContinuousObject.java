package nl.rivm.emi.dynamo.data.objects;

import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.dynamo.data.factories.XMLHandlingEntryPoint;
import nl.rivm.emi.dynamo.data.interfaces.IReferenceValue;
import nl.rivm.emi.dynamo.data.interfaces.IStaxEventContributor;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.objects.layers.ReferenceValueObjectImplementation;
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

public class RiskFactorContinuousObject extends XMLHandlingEntryPoint implements
		IStaxEventContributor, IReferenceValue {
	Log log = LogFactory.getLog(this.getClass().getName());

	ReferenceValueObjectImplementation referenceValueObjectImplementation;

	public RiskFactorContinuousObject(boolean makeObservable) {
		super(RootElementNamesEnum.RISKFACTOR_CONTINUOUS, makeObservable);
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

	/**
	 * Create a modelObject from an XML configurationfile.
	 * 
	 * @param dataFilePath
	 * @return RiskFactorCategoricalObject data representation of the xml
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	@Override
	public void manufacture(String dataFilePath)
			throws ConfigurationException, DynamoInconsistentDataException {
		this.log.debug("Starting manufacture.");
		super.manufacture( dataFilePath);
	}

	protected ConfigurationObjectBase handleRootChildren(
			ConfigurationObjectBase modelObject,
			List<ConfigurationNode> rootChildren) throws ConfigurationException {
		if (rootChildren != null) {
			for (ConfigurationNode rootChild : rootChildren) {
				String childName = rootChild.getName();
				log.debug("Handle rootChild: " + childName);
				XMLTagEntity entity = XMLTagEntitySingleton.getInstance().get(
						childName);
//				if ((entity != null) && (entity instanceof IHandlerType)) {
//					modelObject = ((IHandlerType) entity).handle(modelObject,
//							rootChild);
//				} else {
//					throw new ConfigurationException(
//							"Unhandled rootChild element: " + childName);
//				}
			}
		} else {
			referenceValueObjectImplementation.manufactureDefault();
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
		referenceValueObjectImplementation
				.streamEvents(null, writer, eventFactory);
		event = eventFactory.createEndElement("", "", rootElement
				.getNodeLabel());
		writer.add(event);
		event = eventFactory.createEndDocument();
		writer.add(event);
	}

	@Override
	protected void fillHandlers(boolean observable)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleRootChildren(List<ConfigurationNode> rootChildren)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		
	}

}
