package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.OverallMortalityObject;
import nl.rivm.emi.dynamo.data.objects.RelRiskForRiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.RelRiskForRiskFactorContinuousObject;
import nl.rivm.emi.dynamo.data.objects.RRiskForRiskFactorObject;
import nl.rivm.emi.dynamo.data.objects.RelRiskFromOtherDiseaseObject;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Category;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

public class RelRiskForRiskFactorCategoricalFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public RelRiskForRiskFactorCategoricalObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public RelRiskForRiskFactorCategoricalObject manufactureObservable(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		return new RelRiskForRiskFactorCategoricalObject(manufacture(
				configurationFile, true));
	}

	public RelRiskForRiskFactorCategoricalObject manufacture(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		RelRiskForRiskFactorCategoricalObject result = new RelRiskForRiskFactorCategoricalObject(
				producedMap);
		return (result);
	}

	public RelRiskForRiskFactorCategoricalObject manufactureDefault(
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
		return new RelRiskForRiskFactorCategoricalObject(super
				.manufactureDefault(leafNodeList, false));
	}

}
