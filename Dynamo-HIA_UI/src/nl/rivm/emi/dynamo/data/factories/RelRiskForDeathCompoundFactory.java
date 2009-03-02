package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskForDeathCompoundObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskForDeathCompoundFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public RelRiskForDeathCompoundObject manufactureObservable(
			File configurationFile) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true);
		return new RelRiskForDeathCompoundObject(producedMap);
	}

	public RelRiskForDeathCompoundObject manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		RelRiskForDeathCompoundObject result = new RelRiskForDeathCompoundObject(
				producedMap);
		return (result);
	}

	@Override
	public RelRiskForDeathCompoundObject manufactureDefault()
			throws ConfigurationException {
		return manufactureDefault(false);
	}

	@Override
	public RelRiskForDeathCompoundObject manufactureObservableDefault()
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
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("cat"), null));
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
