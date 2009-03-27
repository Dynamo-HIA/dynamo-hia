package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalPrevalencesObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RiskFactorPrevalencesCategoricalFactory extends AgnosticFactory
		implements CategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	private Integer numberOfCategories = null;

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	public TypedHashMap manufactureObservable(
			File configurationFile, String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		return new RiskFactorCategoricalPrevalencesObject(manufacture(
				configurationFile, true, rootElementName));
	}

	public TypedHashMap manufacture(
			File configurationFile, String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false, rootElementName);
		RiskFactorCategoricalPrevalencesObject result = new RiskFactorCategoricalPrevalencesObject(
				producedMap);
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

	private RiskFactorCategoricalPrevalencesObject manufactureDefault(
			boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		CatContainer category = (CatContainer) XMLTagEntitySingleton.getInstance().get(
				"cat");
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxValue = category.setMAX_VALUE(numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(category, null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("percent"), null));
		return new RiskFactorCategoricalPrevalencesObject(super
				.manufactureDefault(leafNodeList, makeObservable));
	}
}