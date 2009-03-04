package nl.rivm.emi.dynamo.data.factories;

/**
 * Base Factory for non-hierarchic configuration files.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;


abstract public class AgnosticGroupFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public HashMap<String, ?> manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException;

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public HashMap<String,?> manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException;

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	abstract public HashMap<String, ?> manufactureDefault()
			throws ConfigurationException;

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	abstract public HashMap<String,?> manufactureObservableDefault()
			throws ConfigurationException;

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param makeObservable
	 * 
	 * @return TypedHashMap HashMap that contains the data of the given file and the type of the data
	 * @throws ConfigurationException 
	 * @throws DynamoInconsistentDataException 
	 */
	public HashMap<String, ?> manufacture(File configurationFile,
			boolean makeObservable, String rootElementName) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug(this.getClass().getName() + " Starting manufacture.");
		HashMap<String, ?> underConstruction = new LinkedHashMap<String, Object>();
		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
			// Validate the xml by xsd schema
			configurationFromFile.setValidating(true);			
			configurationFromFile.load();			
			
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			
			// Check if the name of the first element of the file
			// is the same as that of the node name where the file is processes
			if (rootNode.getName() != null && rootNode.getName().equalsIgnoreCase(rootElementName)) {
				List<?> list = rootNode.getChildren();
				List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) list;
				
				for (ConfigurationNode rootChild : rootChildren) {
					String rootChildName = rootChild.getName();
					log.debug("Handle rootChild: " + rootChildName);
					/* TODO: Under construction as of 3-3-2009
					RootChildSubFactoryEnum.
					underConstruction = handleRootChild(underConstruction,
							rootChild, makeObservable);
							*/
				} // for rootChildren				
			} else {
				// The start/first element of the imported file does not match the node name
				throw new DynamoInconsistentDataException("The contents of the imported file does not match the node name"); 
			}
			return underConstruction;
		} catch (ConfigurationException e) {
			String errorMessageLogFile = "Caught Exception of type: " + e.getClass().getName()
			+ " with message: " + e.getMessage() 
			+ " Cause" + e.getCause();			
			log.error(errorMessageLogFile);
			e.printStackTrace();
			// Show the error message and the nested cause of the error
			String errorMessage;
			if (!e.getCause().getMessage().contains(":")) {
				errorMessage = "An error occured: " + e.getMessage() + "\n" 
				+ "Cause: " + e.getCause().getMessage();
			} else {
				errorMessage = "An error occured: " + e.getMessage() + "\n" 
				+ "Cause: " + e.getCause().getMessage().split(":")[1];
			}
				
			throw new ConfigurationException(errorMessage);
		}
	}

	/**
	 * Currently each rootchild contains a group of XML-elements (leafnodes) of
	 * which all but the last must be "container" datatypes. The result of
	 * successfull processing is the addition of a single Object (wrapped in a
	 * WritableValue when makeObservable is true) contained in a number of
	 * levels of TypedHashMap.
	 * 
	 * @param originalObject
	 * @param rootChild
	 * @param makeObservable
	 * @return
	 * @throws ConfigurationException
	 */
	private TypedHashMap<?> handleRootChild(TypedHashMap<?> originalObject,
			ConfigurationNode rootChild, boolean makeObservable)
			throws ConfigurationException {
		LeafNodeList leafNodeList = new LeafNodeList();
		int theLastContainer = leafNodeList.fill(rootChild);
		log.debug("Handling rootchild. LastContainer " + theLastContainer
				+ leafNodeList.report());
		int currentLevel = 0;
		TypedHashMap<?> updatedObject = makePath(originalObject, leafNodeList,
				theLastContainer, currentLevel, makeObservable);
		return updatedObject;
	}

	/**
	 * Recurse through the leafnodes.
	 * 
	 * 
	 * @param priorLevel
	 * @param leafNodeList
	 * @param theLastContainer
	 * @param currentLevel
	 * @param makeObservable
	 * @return
	 * @throws DynamoConfigurationException
	 */
	private TypedHashMap<?> makePath(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		if (priorLevel == null) {
			priorLevel = new TypedHashMap(leafNodeList.get(currentLevel)
					.getType());
		}
		Integer currentLevelValue = (Integer) (leafNodeList.get(currentLevel)
				.getValue());
		log.debug("Recursing, currentLevel " + currentLevel + " value "
				+ currentLevelValue);
		if (currentLevel < theLastContainer - 1) {
			handleContainerType(priorLevel, leafNodeList, theLastContainer,
					currentLevel, makeObservable, currentLevelValue);
		} else {
			if (leafNodeList.size() - theLastContainer == 1) {
				handlePayLoadType(priorLevel, leafNodeList, currentLevel,
						makeObservable, currentLevelValue);
			} else {
				handleAggregatePayLoadTypes(priorLevel, leafNodeList,
						currentLevel, makeObservable, currentLevelValue);
			}
		}
		return priorLevel;
	}

	private void handleContainerType(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, int currentLevel, boolean makeObservable,
			Integer currentLevelValue) throws DynamoConfigurationException {
		TypedHashMap<?> pathMap = (TypedHashMap<?>) priorLevel
				.get(currentLevelValue);
		if (pathMap == null) {
			XMLTagEntity theType = leafNodeList.get(currentLevel + 1).getType();
			pathMap = new TypedHashMap(theType);
		}
		makePath(pathMap, leafNodeList, theLastContainer, currentLevel + 1,
				makeObservable);
		priorLevel.put(currentLevelValue, pathMap);
	}

	private void handlePayLoadType(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList, int currentLevel,
			boolean makeObservable, Integer currentLevelValue)
			throws DynamoConfigurationException {
		Number leafValue = (Number) (leafNodeList.get(currentLevel + 1)
				.getValue());
		if (!makeObservable) {
			priorLevel.put(currentLevelValue, leafValue);
		} else {
			if (leafValue instanceof Integer) {
				WritableValue writableValue = new WritableValue(leafValue,
						Integer.class);
				priorLevel.put(currentLevelValue, writableValue);
			} else {
				if (leafValue instanceof Float) {
					WritableValue writableValue = new WritableValue(leafValue,
							Float.class);
					priorLevel.put(currentLevelValue, writableValue);
				} else {
					throw new DynamoConfigurationException(
							"Unsupported leafValueType for IObservable-s :"
									+ leafValue.getClass().getName());
				}
			}
		}
	}

	/**
	 * Method for handling compound payload types.
	 * 
	 * @param priorLevel
	 * @param leafNodeList
	 * @param currentLevel
	 * @param makeObservable
	 * @param currentLevelValue
	 * @throws DynamoConfigurationException
	 */
	private void handleAggregatePayLoadTypes(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList, int currentLevel,
			boolean makeObservable, Integer currentLevelValue)
			throws DynamoConfigurationException {
		// Remove ContainerTypes.
		for (int count = 0; count <= currentLevel; count++) {
			leafNodeList.remove(0);
		}
		// Transform to Observables.
		if (makeObservable) {
			for (AtomicTypeObjectTuple tuple : leafNodeList) {
				Object theValue = tuple.getValue();
				WritableValue observableValue = new WritableValue(theValue,
						theValue.getClass());
				tuple.setValue(observableValue);
			}
		}
		priorLevel.put(currentLevelValue, leafNodeList);
	}

	/**
	 * Creates an "empty" Object with all values of the ContainerTypes in their
	 * respective ranges and the default values for the LeafTypes. Level 1:
	 * Initial checks and recursion startup.
	 * 
	 * @param leafNodeList
	 * @param makeObservable
	 * @return the Object filled with default values.
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings("unchecked")
	protected TypedHashMap manufactureDefault(LeafNodeList leafNodeList,
			boolean makeObservable) throws ConfigurationException {
		int theLastContainer = leafNodeList.checkContents();
		int currentLevel = 0;
		AtomicTypeBase type = (AtomicTypeBase) leafNodeList.get(currentLevel)
				.getType();
		TypedHashMap resultMap = new TypedHashMap(type);
		makeDefaultPath(resultMap, leafNodeList, theLastContainer,
				currentLevel, makeObservable);
		return resultMap;
	}

	/**
	 * Recurse through the leafnodes.
	 * 
	 * 
	 * @param priorLevel
	 * @param leafNodeList
	 * @param theLastContainer
	 * @param currentLevel
	 * @param makeObservable
	 * @return
	 * @throws DynamoConfigurationException
	 */
	private TypedHashMap<?> makeDefaultPath(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		try {
			log.debug("Recursing, making default Object, currentLevel "
					+ currentLevel);
			AtomicTypeBase<Integer> myType = (AtomicTypeBase<Integer>) leafNodeList
					.get(currentLevel).getType();
			int maxValue = ((NumberRangeTypeBase<Integer>) myType)
					.getMAX_VALUE();
			int minValue;
			minValue = ((NumberRangeTypeBase<Integer>) leafNodeList.get(
					currentLevel).getType()).getMIN_VALUE();
			for (int value = minValue; value <= maxValue; value++) {
				TypedHashMap<?> pathMap = (TypedHashMap<?>) priorLevel
						.get(value);
				if (pathMap == null) {
					pathMap = new TypedHashMap(leafNodeList.get(
							currentLevel + 1).getType());
				}
				log.debug("Adding map at value " + value);
				priorLevel.put(value, pathMap);
				if (currentLevel < theLastContainer - 1) {
					makeDefaultPath(pathMap, leafNodeList, theLastContainer,
							currentLevel + 1, makeObservable);

				} else {
					log.debug("Number of payload nodes: "
							+ (leafNodeList.size() - theLastContainer));
					if ((leafNodeList.size() - theLastContainer) == 1) { 
						// Existing functionality.
						handleSinglePayload(priorLevel, leafNodeList,
								currentLevel, makeObservable, value);
					} else {
						// Extended functionality.
						ArrayList<AtomicTypeObjectTuple> payloadList = new ArrayList<AtomicTypeObjectTuple>();
						for (int count = theLastContainer; count < leafNodeList
								.size(); count++) {
							AtomicTypeObjectTuple tuple = leafNodeList
									.get(count);
							XMLTagEntity type = tuple.getType();
							Object defaultValue = ((PayloadType) type)
									.getDefaultValue();
							tuple.setValue(defaultValue);
							payloadList.add(tuple);
						}
						priorLevel.put(value, payloadList);
					}
				}
			}
			return priorLevel;
		} catch (ConfigurationException e) {
			throw new DynamoConfigurationException("Rethrowing a "
					+ e.getClass().getName() + "with message; "
					+ e.getMessage());
		}
	}

	private void handleSinglePayload(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList, int currentLevel,
			boolean makeObservable, int value)
			throws DynamoConfigurationException {
		Number defaultValue = ((PayloadType<Number>) leafNodeList.get(
				currentLevel + 1).getType()).getDefaultValue();
		if (!makeObservable) {
			log.debug("Adding default leaf " + defaultValue + " at value "
					+ value);
			priorLevel.put(value, defaultValue);
		} else {
			if (defaultValue instanceof Integer) {
				WritableValue writableValue = new WritableValue(defaultValue,
						Integer.class);
				log.debug("Adding default Writable Integer leaf "
						+ defaultValue + " at value " + value);
				priorLevel.put(value, writableValue);
			} else {
				if (defaultValue instanceof Float) {
					WritableValue writableValue = new WritableValue(
							defaultValue, Float.class);
					log.debug("Adding default Writable Float leaf "
							+ defaultValue + " at value " + value);
					priorLevel.put(value, writableValue);
				} else {
					throw new DynamoConfigurationException(
							"Unsupported leafValueType for IObservable-s :"
									+ defaultValue.getClass().getName());
				}
			}
		}
	}

}
