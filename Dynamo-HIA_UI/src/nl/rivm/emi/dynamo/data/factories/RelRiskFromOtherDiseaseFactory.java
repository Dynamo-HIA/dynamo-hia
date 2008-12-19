package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskFromOtherDiseaseObject;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskFromOtherDiseaseFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public RelRiskFromOtherDiseaseObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public RelRiskFromOtherDiseaseObject manufactureObservable(
			File configurationFile) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		return new RelRiskFromOtherDiseaseObject(manufacture(configurationFile,
				true));
	}

	public RelRiskFromOtherDiseaseObject manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		RelRiskFromOtherDiseaseObject result = new RelRiskFromOtherDiseaseObject(
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

	public RelRiskFromOtherDiseaseObject manufactureDefault(
			boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("value"), null));
		return new RelRiskFromOtherDiseaseObject(super.manufactureDefault(
				leafNodeList, makeObservable));
	}
}
