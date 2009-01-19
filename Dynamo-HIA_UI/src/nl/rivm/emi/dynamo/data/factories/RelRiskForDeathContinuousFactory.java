package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskForDeathContinuousObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskForDeathContinuousFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public RelRiskForDeathContinuousObject manufactureObservable(
			File configurationFile) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true);
		return new RelRiskForDeathContinuousObject(producedMap);
	}

	public RelRiskForDeathContinuousObject manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		RelRiskForDeathContinuousObject result = new RelRiskForDeathContinuousObject(
				producedMap);
		return (result);
	}

	@Override
	public RelRiskForDeathContinuousObject manufactureDefault()
			throws ConfigurationException {
		return manufactureDefault(false);
	}

	@Override
	public RelRiskForDeathContinuousObject manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}

	private RelRiskForDeathContinuousObject manufactureDefault(
			boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("value"), null));
		TypedHashMap<Age> manufacturedMap = super.manufactureDefault(
				leafNodeList, makeObservable);
		RelRiskForDeathContinuousObject result = new RelRiskForDeathContinuousObject(
				manufacturedMap);
		return result;
	}

}
