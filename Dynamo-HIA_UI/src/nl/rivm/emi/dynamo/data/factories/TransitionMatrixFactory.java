package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.TransitionMatrixObject;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransitionMatrixFactory extends AgnosticFactory implements
		CategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	private Integer numberOfCategories = null;

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	public TransitionMatrixObject manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true);
		TransitionMatrixObject result = new TransitionMatrixObject(producedMap);
		return (result);
	}

	public TransitionMatrixObject manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		TransitionMatrixObject result = new TransitionMatrixObject(producedMap);
		return (result);
	}

	@Override
	public TypedHashMap manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	@Override
	public TypedHashMap manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}


	public TransitionMatrixObject manufactureDefault(boolean makeObservable)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		TransitionSource source = (TransitionSource) AtomicTypesSingleton
				.getInstance().get(TransitionSource.getElementName());
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxSource = source.setMAX_VALUE(numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(source, null));
		TransitionDestination destination = (TransitionDestination) AtomicTypesSingleton
				.getInstance().get(TransitionDestination.getElementName());
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxDestination = destination
				.setMAX_VALUE(numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(destination, null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("value"), null));
		TransitionMatrixObject theObject = new TransitionMatrixObject(super
				.manufactureDefault(leafNodeList, makeObservable));
		source.setMAX_VALUE(oldMaxSource);
		destination.setMAX_VALUE(oldMaxDestination);
		return theObject;
	}
}
