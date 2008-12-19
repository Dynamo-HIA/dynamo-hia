package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.PrevalencesCategoricalObject;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Category;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PrevalencesCategoricalFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public PrevalencesCategoricalObject manufactureObservable(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true);
		PrevalencesCategoricalObject result = new PrevalencesCategoricalObject(
				producedMap);
		return (result);
	}

	public PrevalencesCategoricalObject manufacture(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		PrevalencesCategoricalObject result = new PrevalencesCategoricalObject(
				producedMap);
		return (result);
	}

	public PrevalencesCategoricalObject manufactureDefault(
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
		Integer oldMaxValue = category.setMAX_VALUE(numberOfCategories); // The loop has <=, so 6 results in 7 categories.
		leafNodeList.add(new AtomicTypeObjectTuple(category, null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("percent"), null));
		PrevalencesCategoricalObject defaultObject = new PrevalencesCategoricalObject(
				super.manufactureDefault(leafNodeList, false));
		category.setMAX_VALUE(oldMaxValue);
		return defaultObject;
	}

	/**
	 * TODO Change
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 */
	// public float[][] manufactureArrayFromFlatXML(File configurationFile)
	// throws ConfigurationException {
	// float[][] theArray = null;
	// AgeMap<SexMap<Float>> theMap = manufacture(configurationFile);
	// int ageDim = theMap.size();
	// SexMap<Float> sexMap = theMap.get(new Float(0));
	// int sexDim = sexMap.size();
	// theArray = new float[ageDim][sexDim];
	// Float theFloat = null;
	// log.debug("Array sizes: age " + ageDim + " sex: " + sexDim);
	// for (int ageCount = 0; ageCount < ageDim; ageCount++) {
	// sexMap = theMap.get(new Float(ageCount));
	// if (sexMap == null) {
	// throw new ConfigurationException(
	// "Incomplete set of sexes for age " + ageCount);
	// }
	// for (int sexCount = 0; sexCount < sexDim; sexCount++) {
	// theFloat = sexMap.get(new Float(sexCount));
	// if (theFloat != null) {
	// log.debug("Putting value " + theFloat + " for age "
	// + ageCount + " sex: " + sexCount);
	// theArray[ageCount][sexCount] = theFloat;
	// } else {
	// throw new ConfigurationException(
	// "Incomplete set of values for age " + ageCount
	// + ",sex " + sexCount);
	// }
	// }
	// }
	// return theArray;
	// }
}
