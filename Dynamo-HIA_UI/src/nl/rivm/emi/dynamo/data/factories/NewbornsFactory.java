package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Year;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NewbornsFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public TypedHashMap manufactureObservable(File configurationFile,
			String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Year> manufacturedMap = manufacture(configurationFile, true, rootElementName);
		NewbornsObject result = new NewbornsObject(manufacturedMap);
		return result;
	}

	public TypedHashMap manufacture(
			File configurationFile, String rootElementName) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap manufacturedMap = manufacture(configurationFile, false, rootElementName);
		NewbornsObject result = new NewbornsObject(manufacturedMap);
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
	private NewbornsObject manufactureDefault(boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("year"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("number"), null));
		log.debug("leafNodeList" + leafNodeList);
		TypedHashMap<Year> manufacturedMap = super.manufactureDefault(leafNodeList, makeObservable);
		log.debug("manufacturedMap" + manufacturedMap);
		NewbornsObject result = new NewbornsObject(manufacturedMap);
		log.debug("result" + result);
			return result;
	}
}
