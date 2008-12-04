package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.OverallMortalityObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Precondition is that a dispatcher has chosen this factory based on the
 * root-tagname.
 */
public class OverallMortalityFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public OverallMortalityObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public OverallMortalityObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true);
		OverallMortalityObject result = new OverallMortalityObject(producedMap);
		return (result); 
	}

	public OverallMortalityObject manufacture(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		OverallMortalityObject result = new OverallMortalityObject(producedMap);
		return (result); 
	}
	public OverallMortalityObject manufactureDefault() throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("value"), null));
		return (OverallMortalityObject) super.manufactureDefault(leafNodeList, false);
	}

}
