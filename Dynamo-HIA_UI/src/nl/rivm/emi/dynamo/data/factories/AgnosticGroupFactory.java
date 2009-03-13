package nl.rivm.emi.dynamo.data.factories;

/**
 * Base Factory for not purely hierarchical configuration files.
 */
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.data.factories.dispatch.RootChildDispatchMap;
import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticHierarchicalRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticSingleRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.RootChildFactory;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgnosticGroupFactory implements RootLevelFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Data structure to store the parts of the configuration produced by the
	 * rootchild-factories the construction is delegated to.
	 */
	protected HashMap<String, Object> structure = null;

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 * @throws ConfigurationException
	 */
	public HashMap<String, Object> manufacture(File configurationFile,
			String rootNodeName) throws DynamoInconsistentDataException,
			ConfigurationException {
		return manufacture(configurationFile, false, rootNodeName);
	}

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 * @throws ConfigurationException
	 */
	public HashMap<String, Object> manufactureObservable(File configurationFile,
			String rootNodeName) throws DynamoInconsistentDataException,
			ConfigurationException {
		return manufacture(configurationFile, true, rootNodeName);
	}

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	public HashMap<String, Object> manufactureDefault() {
		return manufactureDefault(structure, false);
	}

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	public HashMap<String, Object> manufactureObservableDefault() {
		return manufactureDefault(structure, true);
	}

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param makeObservable
	 * 
	 * @return TypedHashMap HashMap that contains the data of the given file and
	 *         the type of the data
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public HashMap<String, Object> manufacture(File configurationFile,
			boolean makeObservable, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		try {
		log.debug(" Starting manufacture.");
			RootChildDispatchMap instance = RootChildDispatchMap.getInstance();
			if (instance == null) {
				log.fatal("RootCildDispatchMap not constructed.");
			}
			HashMap<String, Object> underConstruction = new LinkedHashMap<String, Object>();
			XMLConfiguration configurationFromFile;
			configurationFromFile = new XMLConfiguration(configurationFile);
		
			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFromFile.clear();
			
			// Validate the xml by xsd schema
			configurationFromFile.setValidating(true);
			configurationFromFile.load();
			
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			// Check if the name of the first element of the file
			// is the same as that of the node name where the file is processes
			if (rootNode.getName() != null
					&& rootNode.getName().equalsIgnoreCase(rootElementName)) {
				List<?> list = rootNode.getChildren();
				List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) list;
				// Iteration-control entities.
				String previousRootChildName = "";
				List<ConfigurationNode> equallyNamedRootChildren = new LinkedList<ConfigurationNode>();
				String rootChildName = null;
				for (ConfigurationNode rootChild : rootChildren) {
					rootChildName = rootChild.getName();
					log.debug("Processing rootchild \"" + rootChildName + "\"");
					if ("".equals(previousRootChildName)
							|| previousRootChildName.equals(rootChildName)) {
						equallyNamedRootChildren.add(rootChild);
						// Only really usefull the first time.
						previousRootChildName = rootChildName;
					} else {
						if (equallyNamedRootChildren.size() > 0) {
							Object result = processPreviousRootChildren(
									instance, previousRootChildName,
									equallyNamedRootChildren);
							underConstruction
									.put(previousRootChildName, result);
							equallyNamedRootChildren.clear();
						}
						equallyNamedRootChildren.add(rootChild);
						previousRootChildName = rootChildName;
					}
				}
				log.debug("Handle rootChild: " + rootChildName);
				Object result = processPreviousRootChildren(instance,
						previousRootChildName, equallyNamedRootChildren);
				underConstruction.put(previousRootChildName, result);
			} else {
				// The start/first element of the imported file does not match
				// the node name
				throw new DynamoInconsistentDataException(
						"The contents of the imported file does not match the node name");
			}
			return underConstruction;
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(),
					e, configurationFile.getAbsolutePath());
			return null;
		}
	}

	private Object processPreviousRootChildren(RootChildDispatchMap instance,
			String previousRootChildName,
			List<ConfigurationNode> equallyNamedRootChildren)
			throws ConfigurationException, DynamoInconsistentDataException {
		Object result = null;
		Iterator<ConfigurationNode> iterator = equallyNamedRootChildren
				.iterator();
		// The only way to safely get the first element.
		if (iterator.hasNext()) {
			ConfigurationNode firstRootChild = iterator.next();
			String rootChildName = firstRootChild.getName();
			RootChildFactory theFactory = instance.get(rootChildName)
					.getTheFactory();
			if (equallyNamedRootChildren.size() == 1) {
				if (theFactory != null) {
					if (theFactory instanceof AgnosticSingleRootChildFactory) {
						AtomicTypeObjectTuple tuple = ((AgnosticSingleRootChildFactory) theFactory)
								.manufacture(firstRootChild);
						result = tuple;
					} else {
						List<ConfigurationNode> childObjects = (List<ConfigurationNode>)firstRootChild.getChildren();
						Object modelObject = ((AgnosticHierarchicalRootChildFactory) theFactory)
								.manufacture(childObjects);
						result = modelObject;
					}
				} else {
				String message = "No factory found for rootchild: \"" + rootChildName + "\"";
					log
							.fatal(message);
					throw new ConfigurationException(message);
				}
			} else {
				String message = "Can't handle multiple RootChildren with the same name";
				log
						.fatal(message);
				throw new ConfigurationException( message);
			}
		}
		equallyNamedRootChildren.clear();
		return result;
	}

	protected HashMap<String, Object> manufactureDefault(
			HashMap<String, Object> structure, Boolean makeObservable) {
		return null; // TODO Implement.
	}
}
