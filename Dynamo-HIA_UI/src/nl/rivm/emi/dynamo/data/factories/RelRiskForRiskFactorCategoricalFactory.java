package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskForRiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Category;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskForRiskFactorCategoricalFactory extends AgnosticFactory
		implements CategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	private Integer numberOfCategories = null;

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	public RelRiskForRiskFactorCategoricalObject manufactureObservable(
			File configurationFile) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		return new RelRiskForRiskFactorCategoricalObject(manufacture(
				configurationFile, true));
	}

	public RelRiskForRiskFactorCategoricalObject manufacture(
			File configurationFile) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		RelRiskForRiskFactorCategoricalObject result = new RelRiskForRiskFactorCategoricalObject(
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

	private RelRiskForRiskFactorCategoricalObject manufactureDefault(
			boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		Category category = (Category) XMLTagEntitySingleton.getInstance().get(
				"cat");
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxValue = category.setMAX_VALUE(numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(category, null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("value"), null));
		return new RelRiskForRiskFactorCategoricalObject(super
				.manufactureDefault(leafNodeList, makeObservable));
	}

}