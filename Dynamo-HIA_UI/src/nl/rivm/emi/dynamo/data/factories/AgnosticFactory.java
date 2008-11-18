package nl.rivm.emi.dynamo.data.factories;

/**
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 * 20081111 Implementation from HashMapto LinkedHashMap to preserve ordering of the elements.
 * 20081117 Constructing of IObservables added.
 */
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.objects.IncidencesObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.ContainerType;
import nl.rivm.emi.dynamo.data.types.atomic.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.util.NameObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param makeObservable
	 *            TODO
	 */
	synchronized public Object manufacture(File configurationFile,
			boolean makeObservable) throws ConfigurationException {
		log.debug(this.getClass().getName() + " Starting manufacture.");
		HashMap underConstruction = null;
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
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private HashMap handleRootChild(HashMap originalObject,
			ConfigurationNode rootChild, boolean makeObservable)
			throws ConfigurationException {
		HashMap resultMap = null;
		LeafNodeList leafNodeList = new LeafNodeList();
		int theLastContainer = leafNodeList.fill(rootChild);
		if (theLastContainer == 0) {
			throw new DynamoConfigurationException(
					"Supporting only XML with at least one dimension (eg. age) for now. LastContainer "
							+ theLastContainer + leafNodeList.report());
		} else {
			if (theLastContainer != leafNodeList.size() - 1) {
				throw new DynamoConfigurationException(
						"Supporting XML with single value only for now. LastContainer "
								+ theLastContainer + leafNodeList.report());
			} else {
				log.debug("Handling rootchild. LastContainer "
						+ theLastContainer + leafNodeList.report());
				resultMap = buildObject(originalObject, leafNodeList,
						theLastContainer, makeObservable);
			}
		}
		return resultMap;
	}

	/**
	 * @param leafNodeList
	 * @param theLastContainer
	 * @param makeObservable
	 *            TODO
	 * @return
	 * @throws DynamoConfigurationException
	 */
	@SuppressWarnings("unchecked")
	private HashMap buildObject(HashMap originalObject,
			ArrayList<NameObjectTuple> leafNodeList, int theLastContainer,
			boolean makeObservable) throws DynamoConfigurationException {
		int currentLevel = 0;
		HashMap resultMap = makePath(originalObject, leafNodeList,
				theLastContainer, currentLevel, makeObservable);
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
	private HashMap makePath(HashMap priorLevel,
			ArrayList<NameObjectTuple> leafNodeList, int theLastContainer,
			int currentLevel, boolean makeObservable)
			throws DynamoConfigurationException {
		if (priorLevel == null) {
			priorLevel = new LinkedHashMap();
		}
		Integer currentLevelValue = (Integer) (leafNodeList.get(currentLevel)
				.getValue());
		log.debug("Recursing, currentLevel " + currentLevel + " value "
				+ currentLevelValue);
		if (currentLevel < theLastContainer - 1) {
			HashMap pathMap = (HashMap) priorLevel.get(currentLevelValue);
			if (pathMap == null) {
				pathMap = new LinkedHashMap();
			}
			makePath(pathMap, leafNodeList, theLastContainer, currentLevel + 1,
					makeObservable);
			priorLevel.put(currentLevelValue, pathMap);
		} else {
			Number leafValue = (Number) (leafNodeList.get(currentLevel + 1)
					.getValue());
			if (!makeObservable) {
				priorLevel.put(currentLevelValue, leafValue);
			} else {
				if (leafValue instanceof Integer) {
					WritableValue writableValue = new WritableValue(leafValue, Integer.class);  
					priorLevel.put(currentLevelValue, writableValue);
				} else {
					if (leafValue instanceof Float) {
						WritableValue writableValue = new WritableValue(leafValue, Float.class);
						priorLevel.put(currentLevelValue, writableValue);
					} else {
						throw new DynamoConfigurationException(
								"Unsupported leafValueType for IObservable-s :"
										+ leafValue.getClass().getName());
					}
				}
				// TODO if()
			}
		}
		return priorLevel;
	}

	private class LeafNodeList extends ArrayList<NameObjectTuple> {
		private static final long serialVersionUID = 4381230502193758915L;

		private int fill(ConfigurationNode rootChild)
				throws ConfigurationException {
			int theLastContainer = 0;
			List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
					.getChildren();
			AtomicTypesSingleton atomicTypesSingleton = AtomicTypesSingleton
					.getInstance();
			for (ConfigurationNode leafChild : leafChildren) {
				log.debug("Handle leafChild: " + leafChild.getName());
				String leafName = leafChild.getName();
				String valueString = (String) leafChild.getValue();
				AtomicTypeBase leafAtomicType = atomicTypesSingleton
						.get(leafName);
				if (leafAtomicType != null) {
					Object leafDataType = leafAtomicType.getType();
					Number valueNumber;
					if (leafDataType.equals(Integer.class)) {
						valueNumber = Integer.parseInt(valueString);
					} else {
						if (leafDataType.equals(Float.class)) {
							valueNumber = Float.parseFloat(valueString);
						} else {
							throw new ConfigurationException(
									"Unsupported data type "
											+ leafDataType.getClass().getName());
						}
					}
					if (leafAtomicType instanceof ContainerType) {
						theLastContainer++;
					}
					add(new NameObjectTuple(leafName, valueNumber));
				} else {
					throw new ConfigurationException("Unexpected tag: "
							+ leafName);
				}

			} // for leafChildren
			return theLastContainer;
		}

		public String report() {
			StringBuffer resultStringBuffer = new StringBuffer(", listlength "
					+ size() + " Values ");
			for (int count = 0; count < size(); count++) {
				resultStringBuffer.append((get(count)).getValue() + " * ");
			}
			return resultStringBuffer.toString();
		}
	}

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param makeObservable
	 *            TODO
	 */
	public Object manufactureZeroes(AtomicTypeBase<Number>[] types,
			boolean makeObservable) throws ConfigurationException {
		log.debug(this.getClass().getName() + " Starting manufacture.");
		Object underConstruction = null;
		HashMap theObject = null;
		int theLastContainer = 0;
		for (; types[theLastContainer] instanceof ContainerType; theLastContainer++) {
		}
		if (theLastContainer == 0) {
			log
					.error("Supporting only configurations with at least one dimension (eg. age) for now. LastContainer "
							+ theLastContainer + ", listlength " + types.length);
		} else {
			if (theLastContainer != types.length - 1) {
				log
						.error("Supporting configuration with single value only for now. LastContainer "
								+ theLastContainer
								+ ", listlength "
								+ types.length);
			} else {
				log.debug("Handling rootchild. LastContainer "
						+ theLastContainer + ", listlength " + types.length);
				int currentLevel = 0;
				underConstruction = makeZeroesPath(theObject, types,
						theLastContainer, currentLevel, makeObservable);
			}
		}
		return underConstruction;
	}

	@SuppressWarnings("unchecked")
	private HashMap makeZeroesPath(HashMap priorLevel,
			AtomicTypeBase<Number>[] types, int theLastContainer,
			int currentLevel, boolean makeObservable) {
		if (priorLevel == null) {
			priorLevel = new HashMap();
		}
		AtomicTypeBase<Number> currentType = types[currentLevel];
		if ((currentType instanceof NumberRangeTypeBase)
				&& (currentType.getType() instanceof Integer)) {
			int lowerLimit = ((Integer) ((NumberRangeTypeBase<Number>) currentType)
					.getMIN_VALUE()).intValue();
			int upperLimit = ((Integer) ((NumberRangeTypeBase<Number>) currentType)
					.getMAX_VALUE()).intValue();
			for (int count = lowerLimit; count < upperLimit; count++) {
				log.debug("Recursing, currentLevel " + currentLevel);
				HashMap pathMap = new HashMap();
				if (currentLevel < theLastContainer - 1) {
					priorLevel.put(count, pathMap);
					makeZeroesPath(pathMap, types, theLastContainer,
							currentLevel + 1, makeObservable);
				} else {
					Number pathValue = null;
					if (currentType.getType() instanceof Integer) {
						if (!makeObservable) {
							pathValue = new Integer(0);
						} else {
							// TODO
						}
					} else {
						if (currentType.getType() instanceof Float) {
							if (!makeObservable) {
								pathValue = new Float(0F);
							} else {
								// TODO
							}
						} else {
							log.error("Unsupported type "
									+ pathValue.getClass().getName());
						}
					}
					priorLevel.put(count, pathValue);
				}
			}
		}
		return null;
	}

	private Number getIndex(ArrayList<NameObjectTuple> theList,
			int currentElement) {
		return (Number) ((NameObjectTuple) theList.get(currentElement)
				.getValue()).getValue();
	}

	private Number getLeaf(ArrayList<NameObjectTuple> theList) {
		return (Number) ((NameObjectTuple) theList.get(theList.size())
				.getValue()).getValue();
	}

	private Set<String> createMethodNamesSet(Object leafDataType) {
		Method[] methods = leafDataType.getClass().getMethods();
		HashSet<String> methodNames = new HashSet<String>();
		for (int methodCount = 0; methodCount < methods.length; methodCount++) {
			methodNames.add(methods[methodCount].getName());
		}
		return methodNames;
	}

	public IncidencesObject constructAllZeroesModel() {
		log.debug("Starting construction of empty model.");
		IncidencesObject theModel = new IncidencesObject();
		AtomicTypesSingleton atomicTypesSingleton = AtomicTypesSingleton
				.getInstance();
		Age age = (Age) atomicTypesSingleton.get("age");
		int minAge = age.getMIN_VALUE().intValue();
		int maxAge = age.getMAX_VALUE().intValue();
		for (int ageCount = minAge; ageCount <= maxAge; ageCount++) {
			theModel.put(new Integer(ageCount), constructAllZeroesSexMap());
		}
		return theModel;
	}

	private SexMap<Float> constructAllZeroesSexMap() {
		SexMap<Float> theSexMap = new SexMap<Float>();
		Float nul = new Float(0F);
		AtomicTypesSingleton atomicTypesSingleton = AtomicTypesSingleton
				.getInstance();
		Sex sex = (Sex) atomicTypesSingleton.get("sex");
		int minSex = sex.getMIN_VALUE().intValue();
		int maxSex = sex.getMAX_VALUE().intValue();
		for (int sexCount = minSex; sexCount <= maxSex; sexCount++) {
			theSexMap.put(new Integer(sexCount), nul);
		}
		return theSexMap;
	}

}
