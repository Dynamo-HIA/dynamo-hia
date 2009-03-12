package nl.rivm.emi.dynamo.data.objects.layers;

/**
 * Base Factory for hierarchic configuration files.
 * Current limitations: Simple Object (Integer, Float) at the deepest level.
 * 
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 * 20081111 Implementation from HashMapto LinkedHashMap to preserve ordering of the elements.
 * 20081117 Constructing of IObservables added.
 * 20081120 Made class abstract and external interface protected to force inheritance. 
 */
import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

abstract public class RecursiveXMLHandlingLayer  {
	private Log log = LogFactory.getLog(this.getClass().getName());
	
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
	public TypedHashMap<?> handle(TypedHashMap<?> originalObject,
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
			if(leafNodeList.size() - theLastContainer == 1){
			handlePayLoadType(priorLevel, leafNodeList, currentLevel,
					makeObservable, currentLevelValue);
			} else {
				handleMultiplePayLoadTypes(priorLevel, leafNodeList, currentLevel,
						makeObservable, currentLevelValue);
				}
		}
		return priorLevel;
	}

	private void handleContainerType(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, int currentLevel, boolean makeObservable,
			Integer currentLevelValue) throws DynamoConfigurationException {
		TypedHashMap<?> pathMap = (TypedHashMap<?>) priorLevel.get(currentLevelValue);
		if (pathMap == null) {
			pathMap = new TypedHashMap(leafNodeList.get(currentLevel + 1)
					.getType());
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
	private void handleMultiplePayLoadTypes(TypedHashMap<?> priorLevel,
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
	public TypedHashMap<?> manufactureDefault(LeafNodeList leafNodeList,
			boolean makeObservable) throws ConfigurationException {
		int theLastContainer = leafNodeList.checkContents();
		int currentLevel = 0;
		AtomicTypeBase type = (AtomicTypeBase) leafNodeList.get(currentLevel).getType();
		TypedHashMap<?> resultMap = (TypedHashMap<?>)new TypedHashMap(type);
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
	 * @throws ConfigurationException 
	 */
	private TypedHashMap<?> makeDefaultPath(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, int currentLevel, boolean makeObservable)
			throws ConfigurationException {
		log.debug("Recursing, making default Object, currentLevel "
				+ currentLevel);
		AtomicTypeBase<Integer> myType = (AtomicTypeBase<Integer>) leafNodeList.get(
				currentLevel).getType();
		int maxValue = ((NumberRangeTypeBase<Integer>)myType ).getMAX_VALUE();
		for (int value = ((NumberRangeTypeBase<Integer>) leafNodeList.get(
				currentLevel).getType()).getMIN_VALUE(); value <= maxValue; value++) {
			TypedHashMap<?> pathMap = (TypedHashMap<?>) priorLevel.get(value);
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
				Number defaultValue = ((PayloadType<Number>) leafNodeList.get(
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
