package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Precondition is that a dispatcher has chosen this factory based on the
 * root-tagname.
 */
public class TransitionDriftFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public TypedHashMap manufactureObservable(File configurationFile, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true, rootElementName);
		TransitionDriftObject result = new TransitionDriftObject(producedMap);
		return (result); 
	}

	public TypedHashMap manufacture(
			File configurationFile, String rootElementName) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false, rootElementName);
		TransitionDriftObject result = new TransitionDriftObject(producedMap);
		return (result); 
	}
	
	public TypedHashMap manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	public TypedHashMap manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}

	private TransitionDriftObject manufactureDefault(boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("mean"), null));
		TypedHashMap<Age> producedMap = super.manufactureDefault(leafNodeList, makeObservable);
		TransitionDriftObject result = new TransitionDriftObject(producedMap);
		return result; 
	}
}
