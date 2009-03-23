package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Trend;
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
public class TransitionDriftNettoFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public TransitionDriftNettoObject manufactureObservable(File configurationFile, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Trend> producedMap = manufacture(configurationFile, true, rootElementName);
		TransitionDriftNettoObject result = new TransitionDriftNettoObject(producedMap);
		return (result); 
	}

	public TransitionDriftNettoObject manufacture(
			File configurationFile, String rootElementName) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Trend> producedMap = manufacture(configurationFile, false, rootElementName);
		TransitionDriftNettoObject result = new TransitionDriftNettoObject(producedMap);
		return (result); 
	}
	
	public TransitionDriftNettoObject manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	public TransitionDriftNettoObject manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}

	private TransitionDriftNettoObject manufactureDefault(boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("trend"), null));
		TypedHashMap<Trend> producedMap = super.manufactureDefault(leafNodeList, makeObservable);
		TransitionDriftNettoObject result = new TransitionDriftNettoObject(producedMap);
		return result; 
	}
}
