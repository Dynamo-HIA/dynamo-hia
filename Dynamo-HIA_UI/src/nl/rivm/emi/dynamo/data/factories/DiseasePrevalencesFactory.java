package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.DiseasePrevalencesObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiseasePrevalencesFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public DiseasePrevalencesObject manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> manufacturedMap = manufacture(configurationFile, true);
		DiseasePrevalencesObject result = new DiseasePrevalencesObject(manufacturedMap);
		return result;
	}

	public DiseasePrevalencesObject manufacture(
			File configurationFile) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap manufacturedMap = manufacture(configurationFile, false);
		DiseasePrevalencesObject result = new DiseasePrevalencesObject(manufacturedMap);
		return (result); 
	}

	@Override
	public DiseasePrevalencesObject manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	public DiseasePrevalencesObject manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}
	private DiseasePrevalencesObject manufactureDefault(boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("percent"), null));
		TypedHashMap<Age> manufacturedMap = super.manufactureDefault(leafNodeList, makeObservable);
		DiseasePrevalencesObject result = new DiseasePrevalencesObject(manufacturedMap);
			return result;
	}
}
