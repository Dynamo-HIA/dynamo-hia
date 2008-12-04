package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskForDeathCategoricalObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Category;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskForDeathCategoricalFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public RelRiskForDeathCategoricalObject manufactureObservable(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		return new RelRiskForDeathCategoricalObject(manufacture(
				configurationFile, true));
	}

	public RelRiskForDeathCategoricalObject manufacture(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		RelRiskForDeathCategoricalObject result = new RelRiskForDeathCategoricalObject(
				producedMap);
		return (result);
	}

	public RelRiskForDeathCategoricalObject manufactureDefault(
			int numberOfCategories) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		Category category = (Category) AtomicTypesSingleton.getInstance().get(
				"cat");
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxValue = category.setMAX_VALUE(numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(category, null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("value"), null));
		return new RelRiskForDeathCategoricalObject(super.manufactureDefault(
				leafNodeList, false));
	}

}
