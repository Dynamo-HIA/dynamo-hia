package nl.rivm.emi.dynamo.data.factories;

/**
 * 
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 * 20081111 Implementation from HashMapto LinkedHashMap to preserve ordering of the elements.
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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
		ArrayList<NameObjectTuple> leafNodeList = new ArrayList<NameObjectTuple>();
		int theLastContainer = fillList(rootChild, leafNodeList);
		if (theLastContainer == 0) {
			log
					.error("Supporting only XML with at least one dimension (eg. age) for now. LastContainer "
							+ theLastContainer
							+ ", listlength "
							+ leafNodeList.size());
		} else {
			if (theLastContainer != leafNodeList.size() - 1) {
				log
						.error("Supporting XML with single value only for now. LastContainer "
								+ theLastContainer
								+ ", listlength "
								+ leafNodeList.size());
			} else {
				StringBuffer values = new StringBuffer(" Values ");
				for (int count = 0; count < leafNodeList.size(); count++) {
					values.append((leafNodeList.get(count)).getValue() + " * ");
				}
				log.debug("Handling rootchild. LastContainer "
						+ theLastContainer + ", listlength "
						+ leafNodeList.size() + values);
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
	 */
	@SuppressWarnings("unchecked")
	private HashMap buildObject(HashMap originalObject,
			ArrayList<NameObjectTuple> leafNodeList, int theLastContainer,
			boolean makeObservable) {
		int currentLevel = 0;
		HashMap resultMap = makePath(originalObject, leafNodeList,
				theLastContainer, currentLevel, makeObservable);
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private HashMap makePath(HashMap priorLevel,
			ArrayList<NameObjectTuple> leafNodeList, int theLastContainer,
			int currentLevel, boolean makeObservable) {
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
			Number pathValue = (Number) (leafNodeList.get(currentLevel + 1)
					.getValue());
			if (!makeObservable) {
				priorLevel.put(currentLevelValue, pathValue);
			} else {
				// TODO if()
			}
		}
		return priorLevel;
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

	// private Object addLayer(HashMap containingMap,
	// ArrayList<NameObjectTuple> theList, int theLastContainer,
	// int currentElement) {
	// if (currentElement < theLastContainer) {
	// Number containerIndex = getIndex(theList, currentElement);
	// HashMap actualMap = (HashMap) containingMap.get(containerIndex);
	// if (actualMap == null) {
	// actualMap = new HashMap();
	// containingMap.put(((NameObjectTuple) theList
	// .get(currentElement).getValue()).getValue(), actualMap);
	// }
	// if (theLastContainer != 0) {
	// containingLevel = new HashMap();
	// } else {
	// containingLevel = theList.get(0);
	// }
	// // } else {
	// // sexMap = underConstruction.get(age);
	// } else {
	//
	// }
	// // if (sexMap == null) {
	// // newBranch = true;
	// // sexMap = new SexMap<Float>();
	// // underConstruction.put(age, sexMap);
	// // } else {
	// // storedValue = sexMap.get(sex);
	// // }
	// // if (storedValue != null) {
	// // throw new ConfigurationException("Duplicate value for age: " + age
	// // + " sex: " + sex + "\nPresentValue: " + storedValue
	// // + " newValue: " + value);
	// // }
	// // log.debug("Processing value for age: " + age + " sex: " + sex
	// // + " value: " + value);
	// // sexMap.put(sex, value);
	// // log.debug("Processed value for age: " + age + " sex: " + sex
	// // + " value: " + value);
	// return containingLevel;
	// }

	private Number getIndex(ArrayList<NameObjectTuple> theList,
			int currentElement) {
		return (Number) ((NameObjectTuple) theList.get(currentElement)
				.getValue()).getValue();
	}

	private Number getLeaf(ArrayList<NameObjectTuple> theList) {
		return (Number) ((NameObjectTuple) theList.get(theList.size())
				.getValue()).getValue();
	}

	private int fillList(ConfigurationNode rootChild,
			ArrayList<NameObjectTuple> theList) throws ConfigurationException {
		int theLastContainer = 0;
		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		AtomicTypesSingleton atomicTypesSingleton = AtomicTypesSingleton
				.getInstance();
		for (ConfigurationNode leafChild : leafChildren) {
			log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			String valueString = (String) leafChild.getValue();
			AtomicTypeBase leafAtomicType = atomicTypesSingleton.get(leafName);
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
				theList.add(new NameObjectTuple(leafName, valueNumber));
			} else {
				throw new ConfigurationException("Unexpected tag: " + leafName);
			}

		} // for leafChildren
		return theLastContainer;
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

	/**
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 */
	// public float[][] manufactureArrayFromFlatXML(File configurationFile)
	// throws ConfigurationException {
	// float[][] theArray = null;
	// Object theMap = manufacture(configurationFile);
	// int ageDim = theMap.size();
	// SexMap<Float> sexMap = theMap.get(new Float(0));
	// int sexDim = sexMap.size();
	// theArray = new float[ageDim][sexDim];
	// Float theFloat = null;
	// log.debug("Array sizes: age " + ageDim + " sex: " + sexDim);
	// for (int ageCount = 0; ageCount < ageDim; ageCount++) {
	// sexMap = theMap.get(new Float(ageCount));
	// if (sexMap == null) {
	// throw new ConfigurationException(
	// "Incomplete set of sexes for age " + ageCount);
	// }
	// for (int sexCount = 0; sexCount < sexDim; sexCount++) {
	// theFloat = sexMap.get(new Float(sexCount));
	// if (theFloat != null) {
	// log.debug("Putting value " + theFloat + " for age "
	// + ageCount + " sex: " + sexCount);
	// theArray[ageCount][sexCount] = theFloat;
	// } else {
	// throw new ConfigurationException(
	// "Incomplete set of values for age " + ageCount
	// + ",sex " + sexCount);
	// }
	// }
	// }
	// return theArray;
	// }
	// /**
	// * Precondition is that a dispatcher has chosen this factory based on the
	// * root-tagname.
	// */
	// public ObservableIncidencesObject manufactureObservable(
	// File configurationFile) throws ConfigurationException {
	// log.debug("Starting manufacture.");
	// ObservableIncidencesObject outerContainer = null;
	// XMLConfiguration configurationFromFile;
	// try {
	// configurationFromFile = new XMLConfiguration(configurationFile);
	// ConfigurationNode rootNode = configurationFromFile.getRootNode();
	// List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
	// .getChildren();
	// for (ConfigurationNode rootChild : rootChildren) {
	// log.debug("Handle rootChild: " + rootChild.getName());
	// outerContainer = handleRootChild(rootChild, outerContainer);
	// } // for rootChildren
	// return outerContainer;
	// } catch (ConfigurationException e) {
	// log.error("Caught Exception of type: " + e.getClass().getName()
	// + " with message: " + e.getMessage());
	// e.printStackTrace();
	// throw e;
	// } catch (Exception exception) {
	// log.error("Caught Exception of type: "
	// + exception.getClass().getName() + " with message: "
	// + exception.getMessage());
	// exception.printStackTrace();
	// return null;
	// }
	// }
	//
	// private ObservableIncidencesObject handleRootChild(
	// ConfigurationNode rootChild, ObservableIncidencesObject ageMap)
	// throws ConfigurationException {
	// // String rootChildName = rootChild.getName();
	// // Object rootChildValueObject = rootChild.getValue();
	// Integer age = null;
	// Integer sex = null;
	// Float value = null;
	//
	// List<ConfigurationNode> leafChildren = (List<ConfigurationNode>)
	// rootChild
	// .getChildren();
	// for (ConfigurationNode leafChild : leafChildren) {
	// log.debug("Handle leafChild: " + leafChild.getName());
	// String leafName = leafChild.getName();
	// Object valueObject = leafChild.getValue();
	// if (valueObject instanceof String) {
	// String valueString = (String) valueObject;
	// if ("age".equalsIgnoreCase(leafName)) {
	// if (age == null) {
	// age = Integer.parseInt(valueString);
	// } else {
	// throw new ConfigurationException("Double age tag.");
	// }
	// } else {
	// if ("sex".equalsIgnoreCase(leafName)) {
	// if (sex == null) {
	// sex = Integer.parseInt(valueString);
	// } else {
	// throw new ConfigurationException("Double sex tag.");
	// }
	// } else {
	// if ("value".equalsIgnoreCase(leafName)) {
	// if (value == null) {
	// value = Float.parseFloat(valueString);
	// } else {
	// throw new ConfigurationException(
	// "Double value tag.");
	// }
	// } else {
	// throw new ConfigurationException("Unexpected tag: "
	// + leafName);
	// }
	// }
	// }
	// } else {
	// throw new ConfigurationException("Value is no String!");
	// }
	// } // for leafChildren
	// SexMap<IObservable> sexMap = null;
	// IObservable storedValue = null;
	// boolean newBranch = false;
	// if (ageMap == null) {
	// newBranch = true;
	// ageMap = new ObservableIncidencesObject();
	// } else {
	// sexMap = ageMap.get(age);
	// }
	// if (sexMap == null) {
	// newBranch = true;
	// sexMap = new SexMap<IObservable>();
	// ageMap.put(age, sexMap);
	// } else {
	// storedValue = sexMap.get(sex);
	// }
	// if (storedValue != null) {
	// throw new ConfigurationException("Duplicate value for age: " + age
	// + " sex: " + sex + "\nPresentValue: "
	// + ((WritableValue) storedValue).doGetValue()
	// + " newValue: " + value);
	// }
	// log.debug("Processing value for age: " + age + " sex: " + sex
	// + " value: " + value);
	// IObservable obsValue = new WritableValue(value, value);
	// sexMap.put(sex, obsValue);
	// log.debug("Processed value for age: " + age + " sex: " + sex
	// + " value: " + value);
	// return ageMap;
	// }
	//
	// public ObservableIncidencesObject constructObservableAllZeroesModel() {
	// log.debug("Starting construction of empty model.");
	// ObservableIncidencesObject theModel = new ObservableIncidencesObject();
	// AtomicTypesSingleton atomicTypesSingleton = AtomicTypesSingleton
	// .getInstance();
	// Age age = (Age) atomicTypesSingleton.get("age");
	// int minAge = age.getMIN_VALUE().intValue();
	// int maxAge = age.getMAX_VALUE().intValue();
	// for (int ageCount = minAge; ageCount <= maxAge; ageCount++) {
	// theModel.put(new Integer(ageCount),
	// constructObservableAllZeroesSexMap());
	// }
	// return theModel;
	// }
	//
	// private SexMap<IObservable> constructObservableAllZeroesSexMap() {
	// SexMap<IObservable> theSexMap = new SexMap<IObservable>();
	// Float nul = new Float(0F);
	// AtomicTypesSingleton atomicTypesSingleton = AtomicTypesSingleton
	// .getInstance();
	// Sex sex = (Sex) atomicTypesSingleton.get("sex");
	// int minSex = sex.getMIN_VALUE().intValue();
	// int maxSex = sex.getMAX_VALUE().intValue();
	// for (int sexCount = minSex; sexCount <= maxSex; sexCount++) {
	// theSexMap.put(new Integer(sexCount), new WritableValue(nul, nul
	// .getClass()));
	// }
	// return theSexMap;
	// }
}
