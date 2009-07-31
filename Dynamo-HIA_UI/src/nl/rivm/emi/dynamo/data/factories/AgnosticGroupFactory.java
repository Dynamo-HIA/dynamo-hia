package nl.rivm.emi.dynamo.data.factories;

/**
 * Base Factory for not purely hierarchical configuration files.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.dispatch.RootChildDispatchMap;
import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticHierarchicalRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.AgnosticSingleRootChildFactory;
import nl.rivm.emi.dynamo.data.factories.rootchild.RootChildFactory;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractFlexibleUpperLimitInteger;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

abstract public class AgnosticGroupFactory implements RootLevelFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Value used by "FlexibleUpperLimit" types. Limit is that only one
	 * newUpperLimit is present, so it currently not possible to give multiple
	 * wrapped types different upper limits. This is currently YAGNI.
	 */
	private Integer indexLimit = null;
	/**
	 * Data structure to store the parts of the configuration produced by the
	 * rootchild-factories the construction is delegated to.
	 */
	protected HashMap<String, Object> structure = null;

	/**
	 * Interface to be overridden by ChildClass.
	 * 
	 * @param configurationFile
	 * @param rootNodeName
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public LinkedHashMap<String, Object> manufacture(
			File configurationFile, String rootNodeName)
			throws ConfigurationException, DynamoInconsistentDataException;

	/**
	 * Interface to be overridden by ChildClass.
	 * 
	 * @param configurationFile
	 * @param rootNodeName
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public LinkedHashMap<String, Object> manufactureObservable(
			File configurationFile, String rootNodeName)
			throws ConfigurationException, DynamoInconsistentDataException;

	/**
	 * Interface to be overridden by ChildClass.
	 * 
	 * @param configurationFile
	 * @param rootNodeName
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public LinkedHashMap<String, Object> manufactureDefault()
			throws DynamoConfigurationException;

	/**
	 * Interface to be overridden by ChildClass.
	 * 
	 * @param configurationFile
	 * @param rootNodeName
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public LinkedHashMap<String, Object> manufactureObservableDefault()
			throws DynamoConfigurationException;

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @return
	 * @throws DynamoConfigurationException
	 * @throws ConfigurationException
	 */
	protected LinkedHashMap<String, Object> manufactureDefault(
			FileControlEnum fileControl) throws DynamoConfigurationException {
		return manufactureDefault(fileControl, false);
	}

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param fileControl
	 * @return
	 * @throws DynamoConfigurationException
	 */
	protected LinkedHashMap<String, Object> manufactureObservableDefault(
			FileControlEnum fileControl) throws DynamoConfigurationException {
		return manufactureDefault(fileControl, true);
	}

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param configurationFile
	 * @param makeObservable
	 * @param rootElementName
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	@SuppressWarnings("unchecked")
	protected LinkedHashMap<String, Object> manufacture(File configurationFile,
			boolean makeObservable, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		try {
			log.debug(" Starting manufacture.");
			RootChildDispatchMap instance = RootChildDispatchMap.getInstance();
			if (instance == null) {
				log.fatal("RootCildDispatchMap not constructed.");
			}
			LinkedHashMap<String, Object> underConstruction = new LinkedHashMap<String, Object>();
			XMLConfiguration configurationFromFile;
			configurationFromFile = new XMLConfiguration(configurationFile);

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
									equallyNamedRootChildren, makeObservable);
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
						previousRootChildName, equallyNamedRootChildren,
						makeObservable);
				underConstruction.put(previousRootChildName, result);
			} else {
				// The start/first element of the imported file does not match
				// the node name
				throw new DynamoInconsistentDataException(
						//					"The contents of the imported file does not match the node name");
				"The format of the imported file does not match the prescribed format needed by this screen");
			}
			return underConstruction;
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(this.log, "", e,
					configurationFile.getAbsolutePath());
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(), e,
					configurationFile.getAbsolutePath());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Object processPreviousRootChildren(RootChildDispatchMap instance,
			String previousRootChildName,
			List<ConfigurationNode> equallyNamedRootChildren,
			boolean makeObservable) throws ConfigurationException,
			DynamoInconsistentDataException {
		Object result = null;
		Iterator<ConfigurationNode> iterator = equallyNamedRootChildren
				.iterator();
		// The only way to safely get the first element.
		if (iterator.hasNext()) {
			ConfigurationNode firstRootChild = iterator.next();
			String rootChildName = firstRootChild.getName();
			log.debug("rootChildName" + rootChildName);
			RootChildFactory theFactory = instance.get(rootChildName)
					.getTheFactory();
			// 
			if (theFactory != null) {
				if (equallyNamedRootChildren.size() == 1) {
					if (theFactory instanceof AgnosticSingleRootChildFactory) {
						AtomicTypeObjectTuple tuple = null;
						if (makeObservable) {
							tuple = ((AgnosticSingleRootChildFactory) theFactory)
									.manufactureObservable(firstRootChild);
						} else {
							tuple = ((AgnosticSingleRootChildFactory) theFactory)
									.manufacture(firstRootChild);
						}
						result = tuple;
					} else {
						List<ConfigurationNode> childObjects = (List<ConfigurationNode>) firstRootChild
								.getChildren();
						Object modelObject = null;
						if (makeObservable) {
							modelObject = ((AgnosticHierarchicalRootChildFactory) theFactory)
									.manufactureObservable(childObjects);
						} else {
							modelObject = ((AgnosticHierarchicalRootChildFactory) theFactory)
									.manufacture(childObjects);
						}
						result = modelObject;
					}
				} else {
					Object modelObject = null;
					if (makeObservable) {
						modelObject = ((AgnosticHierarchicalRootChildFactory) theFactory)
								.manufactureObservable(equallyNamedRootChildren);
					} else {
						modelObject = ((AgnosticHierarchicalRootChildFactory) theFactory)
								.manufacture(equallyNamedRootChildren);
					}
					result = modelObject;
				}
			} else {
				String message = "No factory found for rootchild: \""
						+ rootChildName + "\"";
				log.fatal(message);
				throw new ConfigurationException(message);
			}
		}
		equallyNamedRootChildren.clear();
		return result;
	}

	private LinkedHashMap<String, Object> manufactureDefault(
			FileControlEnum fileControl, Boolean makeObservable)
			throws DynamoConfigurationException {
		log.debug(" Starting manufacture.");
		LinkedHashMap<String, Object> underConstruction = new LinkedHashMap<String, Object>();
		int numberOfRootChildren = fileControl.getNumberOfRootChildren();
		for (int rootChildCount = 1; rootChildCount <= numberOfRootChildren; rootChildCount++) {
			XMLTagEntity rootChildType = fileControl
					.getParameterType4GroupFactory(rootChildCount);
			log
					.info("Handling rootchild: "
							+ rootChildType.getXMLElementName());
			// A Wrappertype has its own fileControl String-s.
			if (rootChildType instanceof WrapperType) {
				TypedHashMap<?> resultMap = null;
				FileControlSingleton fileControlInstance = FileControlSingleton
						.getInstance();
				String xmlElementName = rootChildType.getXMLElementName();
				FileControlEnum rootChildControlEnum = fileControlInstance
						.get(xmlElementName);
				int level = 1;
				resultMap = handleWrapperType(rootChildType,
						rootChildControlEnum, level, resultMap, makeObservable);
				underConstruction.put(rootChildType.getXMLElementName(),
						resultMap);
			} else {
				if (rootChildType instanceof PayloadType) {
					Object defaultObject = manufactureDefaultSinglePayload(
							(AtomicTypeBase<?>) rootChildType, makeObservable);
					AtomicTypeObjectTuple tuple = new AtomicTypeObjectTuple(
							rootChildType, defaultObject);
					underConstruction.put(rootChildType.getXMLElementName(),
							tuple);
				} else {
					DynamoConfigurationException e = new DynamoConfigurationException(
							"Unexpected type: "
									+ rootChildType.getXMLElementName());
					e.printStackTrace();
					throw e;
				}
			}
		}
		return underConstruction;
	}

	@SuppressWarnings("unchecked")
	private TypedHashMap<?> handleWrapperType(XMLTagEntity wrapperType,
			FileControlEnum rootChildControlEnum, int level,
			TypedHashMap<?> resultMap, Boolean makeObservable)
			throws DynamoConfigurationException {
		if (level > 2) {
			throw new DynamoConfigurationException(
					"More than two Wrapped entities are not supported.");
		}
		XMLTagEntity wrappedEntity = rootChildControlEnum
				.getParameterType4GroupFactory(level);
		if (wrappedEntity instanceof ContainerType) {
			if ((wrappedEntity instanceof NumberRangeTypeBase)
					|| (wrappedEntity instanceof AbstractString)) {
				if (wrappedEntity instanceof AbstractFlexibleUpperLimitInteger) {
					log.debug("Setting MAX_VALUE of type " + wrappedEntity.getXMLElementName() + " to " + getIndexLimit());
					((AbstractFlexibleUpperLimitInteger) wrappedEntity)
							.setMAX_VALUE(getIndexLimit());
				}
				resultMap = new TypedHashMap(wrappedEntity);
				resultMap = makeDefaultPath(resultMap, rootChildControlEnum,
						level, makeObservable);

			} else {
				throw new DynamoConfigurationException(
						"Unsupported ContainerType: "
								+ wrappedEntity.getXMLElementName());
			}
		} else {
			if (wrappedEntity instanceof WrapperType) {
				resultMap = handleWrapperType(wrappedEntity,
						rootChildControlEnum, level + 1, resultMap,
						makeObservable);
			} else {
				throw new DynamoConfigurationException("Unsupported Type: "
						+ wrappedEntity.getXMLElementName());
			}
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private TypedHashMap<?> makeDefaultPath(TypedHashMap<?> priorLevel,
			FileControlEnum fileControl, int currentLevel,
			boolean makeObservable) throws DynamoConfigurationException {
		try {
			AtomicTypeBase<?> testType = (AtomicTypeBase<?>) fileControl
					.getParameterType4GroupFactory(currentLevel);
			if (!(testType instanceof AbstractString)) {
				AtomicTypeBase<Integer> myType = (AtomicTypeBase<Integer>) fileControl
						.getParameterType4GroupFactory(currentLevel);
				log.info("Handling AtomicTypeBase<Integer> ContainerType: "
						+ myType.getXMLElementName());

				int maxValue = ((NumberRangeTypeBase<Integer>) myType)
						.getMaxNumberOfDefaultValues();
				int minValue = ((NumberRangeTypeBase<Integer>) myType)
						.getMIN_VALUE();
				log.debug("Type \"" + myType.getXMLElementName() + "\" minimumValue: " + minValue + " maximumValue: " + maxValue);
				if (minValue < maxValue) {
					for (int value = minValue; value <= maxValue; value++) {
						TypedHashMap<?> pathMap = (TypedHashMap<?>) priorLevel
								.get(value);
						if (pathMap == null) {
							pathMap = new TypedHashMap(
									fileControl
											.getParameterType4GroupFactory(currentLevel + 1));
						}
						log.debug("Adding map at value " + value);
						priorLevel.put(value, pathMap);
						XMLTagEntity levelEntity = fileControl
								.getParameterType4GroupFactory(currentLevel + 1);
						if (levelEntity instanceof ContainerType) {
							makeDefaultPath(pathMap, fileControl,
									currentLevel + 1, makeObservable);

						} else {
							int numberOfPayloadNodes = fileControl
									.getNumberOfParameterTypes4GroupFactory()
									- currentLevel - 1;
							log.debug("Number of payload nodes: "
									+ numberOfPayloadNodes);
							// NB(mondeelr) Difference removed.
							// if (numberOfPayloadNodes == 1) {
							// // Existing functionality.
							// handleSinglePayload(priorLevel, fileControl,
							// currentLevel, makeObservable, value);
							// } else {
							// Extended functionality.
							handleMultiplePayLoads(priorLevel, fileControl,
									currentLevel + 1, makeObservable, value);
							// }
						}
					}
				}
			}
			return priorLevel;
		} catch (ConfigurationException e) {
			throw new DynamoConfigurationException("Rethrowing a "
					+ e.getClass().getName() + " with message: "
					+ e.getMessage());
		}
	}

	private void handleMultiplePayLoads(TypedHashMap<?> priorLevel,
			FileControlEnum fileControl, int payloadStartIndex,
			boolean makeObservable, int value) {
		ArrayList<AtomicTypeObjectTuple> payloadList = new ArrayList<AtomicTypeObjectTuple>();
		for (int count = payloadStartIndex; count < fileControl
				.getNumberOfParameterTypes4GroupFactory(); count++) {
			XMLTagEntity type = fileControl
					.getParameterType4GroupFactory(count);
			Object defaultValue = ((AtomicTypeBase<?>) type).getDefaultValue();
			AtomicTypeObjectTuple modelTuple = null;
			if (!makeObservable) {
				modelTuple = new AtomicTypeObjectTuple(type, defaultValue);
			} else {
				WritableValue observable = new WritableValue(defaultValue,
						defaultValue.getClass());
				modelTuple = new AtomicTypeObjectTuple(type, observable);
			}
			payloadList.add(modelTuple);
		}
		priorLevel.put(value, payloadList);
	}

//	private void handleSinglePayload(TypedHashMap<?> priorLevel,
//			FileControlEnum controlEnum, int currentLevel,
//			boolean makeObservable, int value)
//			throws DynamoConfigurationException {
//		log.debug("controlEnum" + controlEnum);
//		/*
//		 * log.debug("((PayloadType<Number>) leafNodeList.get(currentLevel + 1))"
//		 * + ((PayloadType<Number>) leafNodeList.get( currentLevel + 1)));
//		 */
//		AtomicTypeBase<?> payloadType = (AtomicTypeBase<?>) controlEnum
//				.getParameterType4GroupFactory(currentLevel + 1);
////		Object defaultValue = payloadType.getDefaultValue();
//		Object defaultObjectValue = manufactureDefaultSinglePayload(
//				payloadType, makeObservable);
//		priorLevel.put(value, defaultObjectValue);
//
//	}

	private Object manufactureDefaultSinglePayload(AtomicTypeBase<?> type,
			boolean makeObservable) throws DynamoConfigurationException {
		Object result = null;
		Object defaultValue = type.getDefaultValue();
		if (!makeObservable) {
			log.info("Getting default value " + defaultValue + " for type "
					+ type.getXMLElementName());
			result = defaultValue;
		} else {
			if (defaultValue instanceof Integer) {
				result = new WritableValue(defaultValue, Integer.class);
			} else {
				if (defaultValue instanceof Float) {
					result = new WritableValue(defaultValue, Float.class);
				} else {
					if (defaultValue instanceof String) {
						result = new WritableValue(defaultValue, String.class);
					} else {
						if (defaultValue instanceof Boolean) {
							result = new WritableValue(defaultValue,
									Boolean.class);
						} else {
							DynamoConfigurationException e = new DynamoConfigurationException(
									"Unsupported leafValueType for IObservable-s :"
											+ defaultValue.getClass().getName());
							e.printStackTrace(); // TODO Remove after debugging.
							throw e;
						}
					}
				}
			}
		}
		return result;
	}

	public Integer getIndexLimit() {
		return indexLimit;
	}

	public void setIndexLimit(Integer newIndexLimit) {
		this.indexLimit = newIndexLimit;
	}
}
