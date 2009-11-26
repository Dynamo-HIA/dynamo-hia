package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.TransitionMatrixObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransitionMatrixFactory extends AgnosticCategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureObservable(java.io.File, java.lang.String)
	 */
	public TypedHashMap<Age> manufactureObservable(File configurationFile,
			String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true,
				rootElementName);
		TransitionMatrixObject result = new TransitionMatrixObject(producedMap);
		return (result);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufacture(java.io.File, java.lang.String)
	 */
	public TypedHashMap<Age> manufacture(File configurationFile,
			String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false,
				rootElementName);
		TransitionMatrixObject result = new TransitionMatrixObject(producedMap);
		return (result);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureDefault()
	 */
	@Override
	public TypedHashMap<Age> manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureObservableDefault()
	 */
	@Override
	public TypedHashMap<Age> manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}

	/**
	 * Manufactures a new Object from scratch. The AtomicTypeObjectTuples in the
	 * leafNodeList determine the structure and defaultvalues.
	 * 
	 * @param makeObservable
	 *            Flag indicating the Object must contain WritableValues at the
	 *            lowest level.
	 * @return The manufactured default Object.
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("unchecked")
	public TransitionMatrixObject manufactureDefault(boolean makeObservable)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		TransitionSource source = (TransitionSource) XMLTagEntitySingleton
				.getInstance().get("from");
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxSource = source.setMAX_VALUE(numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(source, null));
		TransitionDestination destination = (TransitionDestination) XMLTagEntitySingleton
				.getInstance().get(TransitionDestination.XMLElementName);
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxDestination = destination
				.setMAX_VALUE(numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(destination, null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.PERCENTAGE
				.getTheType(), null));
		TransitionMatrixObject theObject = new TransitionMatrixObject(super
				.manufactureDefault(leafNodeList, makeObservable));
		source.setMAX_VALUE(oldMaxSource);
		destination.setMAX_VALUE(oldMaxDestination);
		return theObject;
	}
}
