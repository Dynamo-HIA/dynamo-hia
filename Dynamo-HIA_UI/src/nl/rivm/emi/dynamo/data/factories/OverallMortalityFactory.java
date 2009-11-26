package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.OverallMortalityObject;
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
public class OverallMortalityFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureObservable(java.io.File, java.lang.String)
	 */
	public TypedHashMap<Age> manufactureObservable(File configurationFile,
			String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true,
				rootElementName);
		OverallMortalityObject result = new OverallMortalityObject(producedMap);
		return (result);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufacture(java.io.File, java.lang.String)
	 */
	public TypedHashMap<Age> manufacture(File configurationFile,
			String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false,
				rootElementName);
		OverallMortalityObject result = new OverallMortalityObject(producedMap);
		return (result);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureDefault()
	 */
	public TypedHashMap<Age> manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureObservableDefault()
	 */
	public TypedHashMap<Age> manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}

	/**
	 * Manufactures a new Object from scratch. The AtomicTypeObjectTuples in the
	 * leafNodeList determine the structure and defaultvalues.
	 * 
	 * @param makeObservable
	 *            Flag indicating the Object must contain WritableValues at the
	 *            lowest level.
	 * @return The manufactured default Object.
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("unchecked")
	private OverallMortalityObject manufactureDefault(boolean makeObservable)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("value"), null));
		TypedHashMap<Age> producedMap = super.manufactureDefault(leafNodeList,
				makeObservable);
		OverallMortalityObject result = new OverallMortalityObject(producedMap);
		return result;
	}
}
