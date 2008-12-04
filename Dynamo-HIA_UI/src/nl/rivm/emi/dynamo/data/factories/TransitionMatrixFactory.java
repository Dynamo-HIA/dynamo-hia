package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskForDeathCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.RelRiskForDeathContinuousObject;
import nl.rivm.emi.dynamo.data.objects.TransitionMatrixObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Category;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransitionMatrixFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public TransitionMatrixObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true);
		TransitionMatrixObject result = new TransitionMatrixObject(producedMap);
		return (result);
	}

	public TransitionMatrixObject manufacture(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		TransitionMatrixObject result = new TransitionMatrixObject(producedMap);
		return (result);
	}

	public TransitionMatrixObject manufactureDefault(int numberOfCategories)
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
				.manufactureDefault(leafNodeList, false));
		source.setMAX_VALUE(oldMaxSource);
		destination.setMAX_VALUE(oldMaxDestination);
		return theObject;
	}

}
