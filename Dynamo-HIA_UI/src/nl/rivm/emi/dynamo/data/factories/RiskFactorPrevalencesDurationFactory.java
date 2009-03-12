package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.PrevalencesCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorPrevalencesDurationObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RiskFactorPrevalencesDurationFactory extends AgnosticFactory implements CategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

Integer numberOfCategories = null;

public void setNumberOfCategories(Integer numberOfCategories) {
this.numberOfCategories = numberOfCategories;
}

	public RiskFactorPrevalencesDurationObject manufactureObservable(
			File configurationFile, String rootElementName) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true, rootElementName);
		RiskFactorPrevalencesDurationObject result = new RiskFactorPrevalencesDurationObject(
				producedMap);
		return (result);
	}

	public RiskFactorPrevalencesDurationObject manufacture(File configurationFile,
			String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false, rootElementName);
		RiskFactorPrevalencesDurationObject result = new RiskFactorPrevalencesDurationObject(
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

	public PrevalencesCategoricalObject manufactureDefault(
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
		Integer oldMaxValue = category.setMAX_VALUE(numberOfCategories); // The loop has <=, so 6 results in 7 categories.
		leafNodeList.add(new AtomicTypeObjectTuple(category, null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("percent"), null));
		PrevalencesCategoricalObject defaultObject = new PrevalencesCategoricalObject(
				super.manufactureDefault(leafNodeList, makeObservable));
		category.setMAX_VALUE(oldMaxValue);
		return defaultObject;
	}
}
