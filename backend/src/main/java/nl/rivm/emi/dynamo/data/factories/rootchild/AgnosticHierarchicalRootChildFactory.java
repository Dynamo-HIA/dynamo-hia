package nl.rivm.emi.dynamo.data.factories.rootchild;

import java.util.ArrayList;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
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

/**
 * @author mondeelr
 * 
 *         Base Factory for hierarchic sub-configurations. This is a modified
 *         copy of AgnosticFactory that has about the same functionality but
 *         processes a whole configurationfile.
 */
public class AgnosticHierarchicalRootChildFactory implements RootChildFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());

	public AtomicTypeObjectTuple manufacture(ConfigurationNode node)
			throws ConfigurationException, DynamoInconsistentDataException {
		throw new ConfigurationException(this.getClass().getName()
				+ " is the wrong factory type for a single Node.");
	}

	/**
	 * Manufactures a Map modelobject-part based on the passed List of nodes
	 * from the configurationfile.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public TypedHashMap<?> manufacture(List<ConfigurationNode> node)
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap<?> result = manufacture(node, false);
		return result;
	}

	/**
	 * Manufactures a Map modelobject-part that can be used for databinding
	 * based on the passed List of nodes from the configurationfile.
	 * 
	 * @param node
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public AtomicTypeObjectTuple manufactureObservable(ConfigurationNode node)
			throws ConfigurationException, DynamoInconsistentDataException {
		throw new ConfigurationException(this.getClass().getName()
				+ " is the wrong factory type for a single Node.");
	}

	/**
	 * Manufactures a Map of modelobject-parts that contain default values and
	 * can be used for databinding. The parts are based on the passed List of
	 * nodes.
	 * 
	 * @param nodeList
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public TypedHashMap<?> manufactureObservable(
			List<ConfigurationNode> nodeList) throws ConfigurationException,
			DynamoInconsistentDataException {
		TypedHashMap<?> result = manufacture(nodeList, true);
		return result;
	}

	/**
	 * Manufactures a Map of modelobject-parts that are based on the passed List
	 * of nodes.
	 * 
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param nodeList
	 *            List of nodes that are derived from an actual
	 *            configurationfile.
	 * @param makeObservable
	 *            governs whether the resulting map can be used for databinding.
	 * @return TypedHashMap HashMap that contains the data of the given file and
	 *         the type of the data
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	private TypedHashMap<?> manufacture(List<ConfigurationNode> nodeList,
			boolean makeObservable) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug(this.getClass().getName() + " Starting manufacture.");
		TypedHashMap<?> underConstruction = null;
		for (ConfigurationNode rootChild : nodeList) {
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
	 * Recurses down to the leafnodes.
	 * 
	 * @param myContainer
	 * @param priorLevelValue
	 * @param leafNodeList
	 * @param theLastContainer
	 * @param currentLevel
	 * @param makeObservable
	 * @return
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
			// 20090331 mondeelr UniqueNames can be keys as well now.
			// Integer currentLevelValue = (Integer) (leafNodeList
			// .get(currentLevel).getValue());
			Object currentLevelValue = (leafNodeList.get(currentLevel)
					.getValue());
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

	/**
	 * Handles the leaflevel of the configurationtree.
	 * 
	 * @param leafNodeList
	 * @param currentLevel
	 * @param makeObservable
	 * @return
	 * @throws DynamoConfigurationException
	 */
	private Object handlePayLoadType(LeafNodeList leafNodeList,
			int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		Object result = null;
		// if (leafNodeList.size() - leafNodeList.getTheLastContainer() == 1) {
		// result = handleSinglePayLoadType(leafNodeList, currentLevel,
		// makeObservable);
		// } else {
		result = handleAggregatePayLoadTypes(leafNodeList, currentLevel,
				makeObservable);
		// throw new
		// DynamoConfigurationException("handleAggregatePayLoadType not yet implemented here.");
		// }
		return result;
	}

	/**
	 * Handles compound payload types.
	 * 
	 * 
	 * @param leafNodeList
	 *            list of payloadnodes to process.
	 * @param currentLevel
	 *            indicates which elements of the List must be processed.
	 * @param makeObservable
	 *            indicates whether the result must be useable for databinding.
	 * @return the result Object.
	 * @throws DynamoConfigurationException
	 */
	private Object handleAggregatePayLoadTypes(LeafNodeList leafNodeList,
			int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		// Integer indexInContainer = (Integer) (leafNodeList
		// .get(currentLevel - 1).getValue());
		// Remove ContainerTypes.
		for (int count = 0; count < currentLevel; count++) {
			leafNodeList.remove(0);
		}
		// Transform to Observables.
		if (makeObservable) {
			for (AtomicTypeObjectTuple tuple : leafNodeList) {
				Object theValue = tuple.getValue();
				@SuppressWarnings({ "rawtypes", "unchecked" })
				WritableValue observableValue = new WritableValue(theValue,
						theValue.getClass());
				tuple.setValue(observableValue);
			}
		}
		return leafNodeList;
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
	protected TypedHashMap<AtomicTypeBase<?>> manufactureDefault(
			LeafNodeList leafNodeList, boolean makeObservable)
			throws ConfigurationException {
		log.debug("manufactureDefault()");
		int theLastContainer = leafNodeList.checkContents();
		int currentLevel = 0;
		AtomicTypeBase<?> type = (AtomicTypeBase<?>) leafNodeList.get(
				currentLevel).getType();
		TypedHashMap<AtomicTypeBase<?>> resultMap = new TypedHashMap<AtomicTypeBase<?>>(
				type);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TypedHashMap<?> makeDefaultPath(TypedHashMap<?> priorLevel,
			LeafNodeList leafNodeList, int theLastContainer, int currentLevel,
			boolean makeObservable) throws DynamoConfigurationException {
		try {
			log.debug("Recursing, making default Object, currentLevel "
					+ currentLevel);
			AtomicTypeBase<Integer> myType = (AtomicTypeBase<Integer>) leafNodeList
					.get(currentLevel).getType();
			int maxValue = ((NumberRangeTypeBase<Integer>) myType)
					.getMaxNumberOfDefaultValues();
			int minValue;
			minValue = ((NumberRangeTypeBase<Integer>) leafNodeList.get(
					currentLevel).getType()).getMIN_VALUE();
			log.debug("Type \"" + myType.getXMLElementName()
					+ "\" minimumValue: " + minValue + " maximumValue: "
					+ maxValue);
			if (minValue < maxValue) {
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
						makeDefaultPath(pathMap, leafNodeList,
								theLastContainer, currentLevel + 1,
								makeObservable);

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
			}
			return priorLevel;
		} catch (ConfigurationException e) {
			throw new DynamoConfigurationException("Rethrowing a "
					+ e.getClass().getName() + "with message; "
					+ e.getMessage());
		}
	}

	/**
	 * Process a solitary payload element from the configurationfile.
	 * 
	 * @param priorLevel
	 * @param leafNodeList
	 * @param currentLevel
	 * @param makeObservable
	 * @param value
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings("unchecked")
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
				@SuppressWarnings("rawtypes")
				WritableValue writableValue = new WritableValue(defaultValue,
						Integer.class);
				log.debug("Adding default Writable Integer leaf "
						+ defaultValue + " at value " + value);
				priorLevel.put(value, writableValue);
			} else {
				if (defaultValue instanceof Float) {
					@SuppressWarnings("rawtypes")
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
