package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.HasNewbornsObject;
import nl.rivm.emi.dynamo.data.objects.OverallMortalityObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.HasNewborns;
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
public class HasNewbornsFactory extends AgnosticRootChildFactoryBase {
	private Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public TypedHashMap<?> manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedHashMap<?> manufactureDefault() throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedHashMap<?> manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedHashMap<?> manufactureObservableDefault()
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}
	
//	public HasNewbornsObject manufactureObservable(File configurationFile)
//			throws ConfigurationException, DynamoInconsistentDataException {
//		log.debug("Starting manufacture.");
//		TypedHashMap<HasNewborns> producedMap = manufacture(configurationFile, true);
//		HasNewbornsObject result = new HasNewbornsObject(producedMap);
//		return (result); 
//	}
//
//	public HasNewbornsObject manufacture(
//			File configurationFile) throws ConfigurationException, DynamoInconsistentDataException {
//		log.debug("Starting manufacture.");
//		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
//		HasNewbornsObject result = new HasNewbornsObject(producedMap);
//		return (result); 
//	}
//	
//	public HasNewbornsObject manufactureDefault() throws ConfigurationException {
//		return manufactureDefault(false);
//	}
//
//	public HasNewbornsObject manufactureObservableDefault()
//			throws ConfigurationException {
//		return manufactureDefault(true);
//	}
//
//	private HasNewbornsObject manufactureDefault(boolean makeObservable) throws ConfigurationException {
//		log.debug("Starting manufacture.");
//		LeafNodeList leafNodeList = new LeafNodeList();
//		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
//				.getInstance().get("age"), null));
//		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
//				.getInstance().get("sex"), null));
//		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
//				.getInstance().get("value"), null));
//		TypedHashMap<Age> producedMap = super.manufactureDefault(leafNodeList, makeObservable);
//		HasNewbornsObject result = new HasNewbornsObject(producedMap);
//		return result; 
//	}
}
