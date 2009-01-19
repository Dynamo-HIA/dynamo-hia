package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.OverallDALYWeightsObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OverallDALYWeightsFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public OverallDALYWeightsObject manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> manufacturedMap = manufacture(configurationFile, true);
		OverallDALYWeightsObject result = new OverallDALYWeightsObject(manufacturedMap);
		return result;
	}

	public OverallDALYWeightsObject manufacture(
			File configurationFile) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap manufacturedMap = manufacture(configurationFile, false);
		OverallDALYWeightsObject result = new OverallDALYWeightsObject(manufacturedMap);
		return (result); 
	}

	@Override
	public OverallDALYWeightsObject manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	public OverallDALYWeightsObject manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}
	private OverallDALYWeightsObject manufactureDefault(boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("percent"), null));
		TypedHashMap<Age> manufacturedMap = super.manufactureDefault(leafNodeList, makeObservable);
		OverallDALYWeightsObject result = new OverallDALYWeightsObject(manufacturedMap);
			return result;
	}
}
