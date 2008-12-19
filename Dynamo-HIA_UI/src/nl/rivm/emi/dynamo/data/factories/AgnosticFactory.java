package nl.rivm.emi.dynamo.data.factories;

/**
 * Base Factory for hierarchic configuration files.
 * Current limitations: Simple Object (Integer, Float) at the deepest level.
 * 
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 * 20081111 Implementation from HashMapto LinkedHashMap to preserve ordering of the elements.
 * 20081117 Constructing of IObservables added.
 * 20081120 Made class abstract and external interface protected to force inheritance. 
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.PopulationSizeObject;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.LeafType;
import nl.rivm.emi.dynamo.data.types.atomic.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

abstract public class AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException 
	 */
	abstract public TypedHashMap manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException;

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException 
	 */
	abstract public TypedHashMap manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException;

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	abstract public TypedHashMap manufactureDefault()
			throws ConfigurationException;

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	abstract public TypedHashMap manufactureObservableDefault()
			throws ConfigurationException;

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param makeObservable
	 *            TODO
	 * @throws DynamoInconsistentDataException 
	 */
	protected TypedHashMap manufacture(File configurationFile,
			boolean makeObservable) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug(this.getClass().getName() + " Starting manufacture.");
		TypedHashMap underConstruction = null;
		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();
			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				underConstruction = handleRootChild(underConstruction,
						rootChild, makeObservable);
			} // for rootChildren
			return underConstruction;
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage());
			exception.printStackTrace();
			throw new DynamoInconsistentDataException("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage() + " inside " + this.getClass().getName());
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
	@SuppressWarnings("unchecked")
	private TypedHashMap handleRootChild(TypedHashMap originalObject,
			ConfigurationNode rootChild, boolean makeObservable)
			throws ConfigurationException {
		LeafNodeList leafNodeList = new LeafNodeList();
		int theLastContainer = leafNodeList.fill(rootChild);
		log.debug("Handling rootchild. LastContainer " + theLastContainer
				+ leafNodeList.report());
		int currentLevel = 0;
		TypedHashMap updatedObject = makePath(originalObject, leafNodeList,
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
	@SuppressWarnings("unchecked")
	private TypedHashMap makePath(TypedHashMap priorLevel,
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
			handlePayLoadType(priorLevel, leafNodeList, currentLevel,
					makeObservable, currentLevelValue);
		}
		return priorLevel;
	}

	private void handleContainerType(TypedHashMap priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, int currentLevel, boolean makeObservable,
			Integer currentLevelValue) throws DynamoConfigurationException {
		TypedHashMap pathMap = (TypedHashMap) priorLevel.get(currentLevelValue);
		if (pathMap == null) {
			pathMap = new TypedHashMap(leafNodeList.get(currentLevel + 1)
					.getType());
		}
		makePath(pathMap, leafNodeList, theLastContainer, currentLevel + 1,
				makeObservable);
		priorLevel.put(currentLevelValue, pathMap);
	}

	private void handlePayLoadType(TypedHashMap priorLevel,
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
		AtomicTypeBase type = leafNodeList.get(currentLevel).getType();
		TypedHashMap resultMap = new TypedHashMap(type);
		int currentIndex = 0;
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
	@SuppressWarnings("unchecked")
	private TypedHashMap makeDefaultPath(TypedHashMap priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		log.debug("Recursing, making default Object, currentLevel "
				+ currentLevel);
		int maxValue = ((NumberRangeTypeBase<Integer>) leafNodeList.get(
				currentLevel).getType()).getMAX_VALUE();
		for (int value = ((NumberRangeTypeBase<Integer>) leafNodeList.get(
				currentLevel).getType()).getMIN_VALUE(); value <= maxValue; value++) {
			TypedHashMap pathMap = (TypedHashMap) priorLevel.get(value);
			if (pathMap == null) {
				pathMap = new TypedHashMap(leafNodeList.get(currentLevel + 1)
						.getType());
			}
			log.debug("Adding map at value " + value);
			priorLevel.put(value, pathMap);
			if (currentLevel < theLastContainer - 1) {
				makeDefaultPath(pathMap, leafNodeList, theLastContainer,
						currentLevel + 1, makeObservable);

			} else {
				Number defaultValue = ((LeafType<Number>) leafNodeList.get(
						currentLevel + 1).getType()).getDefaultValue();
				if (!makeObservable) {
					log.debug("Adding default leaf " + defaultValue
							+ " at value " + value);
					priorLevel.put(value, defaultValue);
				} else {
					if (defaultValue instanceof Integer) {
						WritableValue writableValue = new WritableValue(
								defaultValue, Integer.class);
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
		return priorLevel;
	}

}
