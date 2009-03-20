package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskForDeathCompoundObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskForDeathCompoundFactory extends AgnosticFactory implements
		CategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	Integer numberOfCategories = null;

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	public TypedHashMap manufactureObservable(
			File configurationFile, String rootElementName) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true, rootElementName);
		return new RelRiskForDeathCompoundObject(producedMap);
	}

	public TypedHashMap manufacture(File configurationFile, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false, rootElementName);
		RelRiskForDeathCompoundObject result = new RelRiskForDeathCompoundObject(
				producedMap);
		return (result);
	}

	@Override
	public TypedHashMap manufactureDefault()
			throws ConfigurationException {
		return manufactureDefault(false);
	}

	@Override
	public TypedHashMap manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}

	private RelRiskForDeathCompoundObject manufactureDefault(
			boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		XMLTagEntitySingleton xmlTagEntitySingleton = XMLTagEntitySingleton.getInstance();
		CatContainer category = (CatContainer) xmlTagEntitySingleton.get("cat");
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxValue = category.setMAX_VALUE(numberOfCategories-1);
		leafNodeList.add(new AtomicTypeObjectTuple(category, null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("begin"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("alpha"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("end"), null));
		TypedHashMap<Age> manufacturedMap = super.manufactureDefault(
				leafNodeList, makeObservable);
		RelRiskForDeathCompoundObject result = new RelRiskForDeathCompoundObject(
				manufacturedMap);
		return result;
	}

}
