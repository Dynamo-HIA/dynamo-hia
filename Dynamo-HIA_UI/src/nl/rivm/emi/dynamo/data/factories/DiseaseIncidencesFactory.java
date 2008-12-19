package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.DiseaseIncidencesObject;
import nl.rivm.emi.dynamo.data.types.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiseaseIncidencesFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public DiseaseIncidencesObject manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> manufacturedMap = manufacture(configurationFile, true);
		DiseaseIncidencesObject result = new DiseaseIncidencesObject(manufacturedMap);
		return result;
	}

	public DiseaseIncidencesObject manufacture(
			File configurationFile) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap manufacturedMap = manufacture(configurationFile, false);
		DiseaseIncidencesObject result = new DiseaseIncidencesObject(manufacturedMap);
		return (result); 
	}

	@Override
	public DiseaseIncidencesObject manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	public DiseaseIncidencesObject manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}
	private DiseaseIncidencesObject manufactureDefault(boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("value"), null));
		TypedHashMap<Age> manufacturedMap = super.manufactureDefault(leafNodeList, makeObservable);
		DiseaseIncidencesObject result = new DiseaseIncidencesObject(manufacturedMap);
			return result;
	}
}
