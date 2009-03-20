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

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureObservable(java.io.File)
	 */
	@Override
	public TypedHashMap manufactureObservable(File configurationFile,
			String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> manufacturedMap = manufacture(configurationFile, true, rootElementName);
		DiseasePrevalencesObject result = new DiseasePrevalencesObject(manufacturedMap);
		return result;
	}

	
	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufacture(java.io.File)
	 */
	@Override
	public TypedHashMap manufacture(
			File configurationFile, String rootElementName) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap manufacturedMap = manufacture(configurationFile, false, rootElementName);
		DiseasePrevalencesObject result = new DiseasePrevalencesObject(manufacturedMap);
		return (result); 
	}

	@Override
	public TypedHashMap manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	public TypedHashMap manufactureObservableDefault()
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
