package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfigurationToo;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * @author mondeelr
 * 
 *         Base Factory for hierarchic configuration files. Current limitations:
 *         Simple Object (Integer, Float) at the deepest level.
 * 
 *         20080918 Agestep fixed at 1. Ages are Integers. 20081111
 *         Implementation changed from HashMap to LinkedHashMap to preserve
 *         ordering of the elements. 20081117 Constructing of IObservables
 *         added. 20081120 Made class abstract and external interface protected
 *         to force inheritance.
 */
abstract public class AgnosticFactory implements RootLevelFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Abstract method to force overriding.
	 * 
	 * @param configurationFile
	 * @return the produced TypedHashMap
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public TypedHashMap<Age> manufacture(File configurationFile,
			String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException;

	/**
	 * Abstract method to force overriding.
	 * 
	 * @param configurationFile
	 * @return the produced TypedHashMap
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public TypedHashMap<Age> manufactureObservable(
			File configurationFile, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException;

	/**
	 * Abstract method to force overriding.
	 * 
	 * @return the produced TypedHashMap
	 * @throws ConfigurationException
	 */
	abstract public TypedHashMap<Age> manufactureDefault()
			throws ConfigurationException;

	/**
	 * Abstract method to force overriding.
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	abstract public TypedHashMap<Age> manufactureObservableDefault()
			throws ConfigurationException;

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
	@SuppressWarnings("unchecked")
	public TypedHashMap<Age> manufacture(File configurationFile,
			boolean makeObservable, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap<Age> underConstruction = null;
		XMLConfigurationToo configurationFromFile;
		try {
			configurationFromFile = new XMLConfigurationToo(configurationFile);

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
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
				List<Object> list = rootNode.getChildren();
				List<ConfigurationNode> rootChildren = new LinkedList<ConfigurationNode>();
				for (Object childObject : list) {
					rootChildren.add((ConfigurationNode) childObject);
				}
				for (ConfigurationNode rootChild : rootChildren) {
					log.info("Handle rootChild: " + rootChild.getName());
					underConstruction = handleRootChild(underConstruction,
							rootChild, makeObservable);
				} // for rootChildren
			} else {
				// The start/first element of the imported file does not match
				// the node name
				throw new DynamoInconsistentDataException(
				// "The contents of the imported file does not match the node name");
						"The format of the imported file does not match the prescribed format needed by this screen");
			}
			return underConstruction;
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(), e,
					configurationFile.getAbsolutePath());
			return underConstruction;
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
	private TypedHashMap<Age> handleRootChild(TypedHashMap<?> originalObject,
			ConfigurationNode rootChild, boolean makeObservable)
			throws ConfigurationException {
		LeafNodeList leafNodeList = new LeafNodeList();
		int theLastContainer = leafNodeList.fill(rootChild);
		log.debug("Handling rootchild. LastContainer " + theLastContainer
				+ leafNodeList.report());
		int currentLevel = 0;
		TypedHashMap<Age> updatedObject = (TypedHashMap<Age>) makePath(
				originalObject, leafNodeList, theLastContainer, currentLevel,
				makeObservable);
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
			handleAggregatePayLoadTypes(priorLevel, leafNodeList, currentLevel,
					makeObservable, currentLevelValue);
		}
		return priorLevel;
	}

	/**
	 * Processes an intermediate level.
	 * 
	 * @param priorLevel
	 * @param leafNodeList
	 * @param theLastContainer
	 * @param currentLevel
	 * @param makeObservable
	 * @param currentLevelValue
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings("unchecked")
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

//	/**
//	 * Processes simple payload types.
//	 * 
//	 * @param priorLevel
//	 * @param leafNodeList
//	 * @param currentLevel
//	 * @param makeObservable
//	 * @param currentLevelValue
//	 * @throws DynamoConfigurationException
//	 */
//	private void handlePayLoadType(TypedHashMap<?> priorLevel,
//			ArrayList<AtomicTypeObjectTuple> leafNodeList, int currentLevel,
//			boolean makeObservable, Integer currentLevelValue)
//			throws DynamoConfigurationException {
//		Number leafValue = (Number) (leafNodeList.get(currentLevel + 1)
//				.getValue());
//		if (!makeObservable) {
//			priorLevel.put(currentLevelValue, leafValue);
//		} else {
//			if (leafValue instanceof Integer) {
//				WritableValue writableValue = new WritableValue(leafValue,
//						Integer.class);
//				priorLevel.put(currentLevelValue, writableValue);
//			} else {
//				if (leafValue instanceof Float) {
//					WritableValue writableValue = new WritableValue(leafValue,
//							Float.class);
//					priorLevel.put(currentLevelValue, writableValue);
//				} else {
//					throw new DynamoConfigurationException(
//							"Unsupported leafValueType for IObservable-s :"
//									+ leafValue.getClass().getName());
//				}
//			}
//		}
//	}

	/**
	 * Handles compound payload types.
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
	 * Recurses through the leafnodes.
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
	private TypedHashMap<?> makeDefaultPath(TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		try {
			log.debug("leafNodeList" + leafNodeList);
			log.debug("Recursing, making default Object, currentLevel "
					+ currentLevel);
			AtomicTypeBase<Integer> myType = (AtomicTypeBase<Integer>) leafNodeList
					.get(currentLevel).getType();
			int maxValue = ((NumberRangeTypeBase<Integer>) myType)
					.getMaxNumberOfDefaultValues();
			int minValue;
			minValue = ((NumberRangeTypeBase<Integer>) leafNodeList.get(
					currentLevel).getType()).getMIN_VALUE();
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
						ArrayList<AtomicTypeObjectTuple> payloadList = handleConsecutivePayLoadElements(
								priorLevel, leafNodeList, theLastContainer,
								makeObservable, value);
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

	@SuppressWarnings("unchecked")
	private ArrayList<AtomicTypeObjectTuple> handleConsecutivePayLoadElements(
			TypedHashMap<?> priorLevel,
			ArrayList<AtomicTypeObjectTuple> leafNodeList,
			int theLastContainer, boolean makeObservable, int value) {
		log.debug("Number of payload nodes: "
				+ (leafNodeList.size() - theLastContainer));
		ArrayList<AtomicTypeObjectTuple> payloadList = new ArrayList<AtomicTypeObjectTuple>();
		for (int count = theLastContainer; count < leafNodeList.size(); count++) {
			AtomicTypeObjectTuple leafNodeTuple = leafNodeList.get(count);
			XMLTagEntity type = leafNodeTuple.getType();
			log.debug("Handling payloadSubType: " + type.getXMLElementName());
			Object defaultValue = ((PayloadType) type).getDefaultValue();
			AtomicTypeObjectTuple modelTuple = null;
			if (!makeObservable) {
				modelTuple = new AtomicTypeObjectTuple(type, defaultValue);
			} else {
				WritableValue observable = new WritableValue(defaultValue,
						defaultValue.getClass());
				modelTuple = new AtomicTypeObjectTuple(type, observable);
			}
			payloadList.add(modelTuple);
			// }
		}
		return payloadList;
	}

	// /**
	// * Handles a single payload.
	// *
	// * @param priorLevel
	// * @param leafNodeList
	// * @param currentLevel
	// * @param makeObservable
	// * @param value
	// * @throws DynamoConfigurationException
	// */
	// private void handleSinglePayload(TypedHashMap<?> priorLevel,
	//
	// ArrayList<AtomicTypeObjectTuple> leafNodeList, int currentLevel,
	// boolean makeObservable, int value)
	// throws DynamoConfigurationException {
	// log.debug("leafNodeList" + leafNodeList);/*
	// * log.debug("((PayloadType<Number>) leafNodeList.get(currentLevel + 1))"
	// * + ((PayloadType<Number>)
	// * leafNodeList.get(
	// * currentLevel + 1)));
	// */
	// Number defaultValue = ((PayloadType<Number>) leafNodeList.get(
	// currentLevel + 1).getType()).getDefaultValue();
	// if (!makeObservable) {
	// log.debug("Adding default leaf " + defaultValue + " at value "
	// + value);
	// priorLevel.put(value, defaultValue);
	// } else {
	// if (defaultValue instanceof Integer) {
	// WritableValue writableValue = new WritableValue(defaultValue,
	// Integer.class);
	// log.debug("Adding default Writable Integer leaf "
	// + defaultValue + " at value " + value);
	// priorLevel.put(value, writableValue);
	// } else {
	// if (defaultValue instanceof Float) {
	// WritableValue writableValue = new WritableValue(
	// defaultValue, Float.class);
	// log.debug("Adding default Writable Float leaf "
	// + defaultValue + " at value " + value);
	// priorLevel.put(value, writableValue);
	// } else {
	// throw new DynamoConfigurationException(
	// "Unsupported leafValueType for IObservable-s :"
	// + defaultValue.getClass().getName());
	// }
	// }
	// }
	// }
}
