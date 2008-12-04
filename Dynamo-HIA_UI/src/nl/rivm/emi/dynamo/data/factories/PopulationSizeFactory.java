package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.PopulationSizeObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PopulationSizeFactory extends AgnosticFactory{
	private Log log = LogFactory.getLog(this.getClass().getName());

	public PopulationSizeObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public PopulationSizeObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap =  manufacture(configurationFile, true);
		PopulationSizeObject result = new PopulationSizeObject(producedMap);
		return result;
	}

	public PopulationSizeObject manufacture(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		PopulationSizeObject result = new PopulationSizeObject(producedMap);
		return (result); 
	}
	public PopulationSizeObject manufactureDefault() throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("value"), null));
		return (PopulationSizeObject) super.manufactureDefault(leafNodeList, false);
	}

}
