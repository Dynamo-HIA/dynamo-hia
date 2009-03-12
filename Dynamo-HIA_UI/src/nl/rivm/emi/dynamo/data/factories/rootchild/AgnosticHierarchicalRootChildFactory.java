package nl.rivm.emi.dynamo.data.factories.rootchild;

/**
 * Base Factory for hierarchic sub-configurations.
 * This is a modified copy of AgnosticFactory that has about the 
 * same functionality but processes a whole configurationfile.
 */
import java.util.ArrayList;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class AgnosticHierarchicalRootChildFactory implements RootChildFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());

	public AtomicTypeObjectTuple manufacture(ConfigurationNode node)
			throws ConfigurationException, DynamoInconsistentDataException {
		throw new ConfigurationException(this.getClass().getName()
				+ " is the wrong factory type for a single Node.");
	}

	/**
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public TypedHashMap<?> manufacture(List<ConfigurationNode> node)
			throws ConfigurationException, DynamoInconsistentDataException {
		return manufacture(node, false);
	}

	public AtomicTypeObjectTuple manufactureObservable(ConfigurationNode node)
			throws ConfigurationException, DynamoInconsistentDataException {
		throw new ConfigurationException(this.getClass().getName()
				+ " is the wrong factory type for this type.");
	}

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public TypedHashMap<?> manufactureObservable(List<ConfigurationNode> node)
			throws ConfigurationException, DynamoInconsistentDataException {
		return manufacture(node, true);
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
	private TypedHashMap manufacture(List<ConfigurationNode> list,
			boolean makeObservable) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug(this.getClass().getName() + " Starting manufacture.");
		TypedHashMap<?> underConstruction = null;
		for (ConfigurationNode rootChild : list) {
			log.debug("Handle rootChild: " + rootChild.getName());
			underConstruction = handleRootChild(underConstruction, rootChild,
					makeObservable);
		} // for rootChildren
		return underConstruction;
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
	 */
	/**
	 * @param myContainer
	 * @param priorLevelValue
	 * @param leafNodeList
	 * @param theLastContainer
	 * @param currentLevel
	 * @param makeObservable
	 * @return
	 * @throws DynamoConfigurationException
	 */
	private TypedHashMap<?> makePath(TypedHashMap<?> myContainer,
			LeafNodeList leafNodeList, int theLastContainer, int currentLevel,
			boolean makeObservable) throws DynamoConfigurationException {
		log.debug("Recursing, currentLevel " + currentLevel + " value "
				+ leafNodeList.get(currentLevel).getValue());
		XMLTagEntity currentLevelType = leafNodeList.get(currentLevel)
				.getType();
		if (myContainer == null) {
			myContainer = new TypedHashMap(currentLevelType);
		}
		if (currentLevelType instanceof ContainerType) {
			Integer currentLevelValue = (Integer) (leafNodeList
					.get(currentLevel).getValue());
			TypedHashMap<?> myObject = (TypedHashMap<?>) myContainer
					.get(currentLevelValue);
			if (currentLevel + 1 < leafNodeList.size()) {
				XMLTagEntity nextLevelType = leafNodeList.get(currentLevel + 1)
						.getType();
				if (nextLevelType instanceof ContainerType) {
					TypedHashMap<?> myUpdatedObject = makePath(myObject,
							leafNodeList, theLastContainer, currentLevel + 1,
							makeObservable);
					myContainer.put(currentLevelValue, myUpdatedObject);
				} else {
					Object result = handlePayLoadType(leafNodeList,
							currentLevel + 1, makeObservable);
					myContainer.put(currentLevelValue, result);
				}
			} else {
			}
		}
		return myContainer;
	}

	private Object handlePayLoadType(LeafNodeList leafNodeList,
			int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		Object result = null;
		if (leafNodeList.size() - leafNodeList.getTheLastContainer() == 1) {
			result = handleSinglePayLoadType(leafNodeList, currentLevel,
					makeObservable);
		} else {
			// result = handleAggregatePayLoadTypes(leafNodeList, currentLevel,
			// makeObservable);
			throw new DynamoConfigurationException("handleAggregatePayLoadType not yet implemented here.");
		}
		return result;
	}

	private Object handleSinglePayLoadType(LeafNodeList leafNodeList,
			int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		Object result = null;
		try {
			XMLTagEntity theType = leafNodeList.get(currentLevel).getType();
			Object theTypeType = ((AtomicTypeBase<?>) theType).getDefaultValue();
			
			if (theTypeType instanceof Number) {
				result = handleNumberPayloadType(leafNodeList, currentLevel,
						makeObservable);
			} else {
//				if (theTypeType instanceof String) {
				if ("java.lang.String".equals(theTypeType.getClass().getName())) {
									result = handleStringPayloadType(leafNodeList,
							currentLevel, makeObservable);
				}
			}
			return result;
		} catch (Exception e) {
			throw new DynamoConfigurationException("handlePayLoadType blew up.");
		}
	}

	private Object handleStringPayloadType(LeafNodeList leafNodeList,
			int currentLevel, boolean makeObservable) {
		Object result = null;
		String leafValue = (String) (leafNodeList.get(currentLevel).getValue());
		if (!makeObservable) {
			result = leafValue;
		} else {
			WritableValue writableValue = new WritableValue(leafValue,
					String.class);
			result = writableValue;
		}
		return result;
	}

	private Object handleNumberPayloadType(LeafNodeList leafNodeList,
			int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		Object result = null;
		Number leafValue = (Number) (leafNodeList.get(currentLevel).getValue());
		if (!makeObservable) {
			result = leafValue;
		} else {
			if (leafValue instanceof Integer) {
				WritableValue writableValue = new WritableValue(leafValue,
						Integer.class);
				result = writableValue;
			} else {
				if (leafValue instanceof Float) {
					WritableValue writableValue = new WritableValue(leafValue,
							Float.class);
					result = writableValue;
				} else {
					throw new DynamoConfigurationException(
							"Unsupported leafValueType for IObservable-s :"
									+ leafValue.getClass().getName());
				}
			}
		}
		return result;
	}

	/**
	 * Method for handling compound payload types.
	 * 
	 * @param priorLevel
	 * @param leafNodeList
	 * @param currentLevel
	 * @param makeObservable
	 * @throws DynamoConfigurationException
	 */
	private TypedHashMap<?> handleAggregatePayLoadTypes(
			TypedHashMap<?> priorLevel, LeafNodeList leafNodeList,
			int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		Integer indexInContainer = (Integer) (leafNodeList
				.get(currentLevel - 1).getValue());
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
		priorLevel.put(indexInContainer, leafNodeList);
		return priorLevel;
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
			LeafNodeList leafNodeList, int theLastContainer, int currentLevel,
			boolean makeObservable) throws DynamoConfigurationException {
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
			LeafNodeList leafNodeList, int currentLevel,
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
