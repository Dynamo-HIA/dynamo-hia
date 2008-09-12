package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.List;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.transition.DestinationsByOriginMap;
import nl.rivm.emi.dynamo.data.transition.ValueByDestinationMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FourDimFloatArrayFromFlatXMLFactory {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.factories.IntegerPerAgeDataFromFlatXMLFactory");

	/**
	 * 
	 * @param configurationFile
	 * @return
	 */
	public static float[][][][] manufactureArray(File configurationFile) {
		float[][][][] theArray = null;
		AgeMap<SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>>> theMap = manufacture(configurationFile);
		int ageDim = theMap.size();
		SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>> sexMap = theMap
				.get(new Integer(0));
		int sexDim = sexMap.size();
		DestinationsByOriginMap<ValueByDestinationMap<Float>> originMap = sexMap
				.get(new Integer(0));
		int originDim = originMap.size();
		ValueByDestinationMap<Float> destinyMap = originMap.get(0);
		int destinyDim = destinyMap.size();
		theArray = new float[ageDim][sexDim][originDim][destinyDim];
		Float theFloat = null;
		log.debug("Array sizes: age " + ageDim + " sex: " + sexDim + " from: "
				+ originDim + " to: " + destinyDim);
		for (int ageCount = 0; ageCount < ageDim; ageCount++) {
			sexMap = theMap.get(new Integer(ageCount));
			for (int sexCount = 0; sexCount < sexDim; sexCount++) {
				originMap = sexMap.get(new Integer(sexCount));
				for (int originCount = 0; originCount < originDim; originCount++) {
					destinyMap = originMap.get(new Integer(originCount));
					for (int destinyCount = 0; destinyCount < destinyDim; destinyCount++) {
						theFloat = destinyMap.get(new Integer(destinyCount));
						if (theFloat != null) {
							log.debug("Putting value " + theFloat + " for age "
									+ ageCount + " sex: " + sexCount
									+ " from: " + originCount + " to: "
									+ destinyCount);
							theArray[ageCount][sexCount][originCount][destinyCount] = theFloat
									.floatValue();
						} else {
							theArray[ageCount][sexCount][originCount][destinyCount] = 0F;
							log.info("Adding a value 0 for age " + ageCount
									+ " sex: " + sexCount + " from: "
									+ originCount + " to: " + destinyCount);
						}
					}
				}
			}
		}
		return theArray;
	}

	public static AgeMap<SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>>> manufacture(
			File configurationFile) {
		log.debug("Starting manufacture.");
		AgeMap<SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>>> outerContainer = null;
		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();
			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				outerContainer = handleRootChild(rootChild, outerContainer);
			} // for rootChildren
			return outerContainer;
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return outerContainer;
		}
	}

	private static AgeMap<SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>>> handleRootChild(
			ConfigurationNode rootChild,
			AgeMap<SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>>> ageMap)
			throws ConfigurationException {
		String rootChildName = rootChild.getName();
		Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Integer from = null;
		Integer to = null;
		Float value = null;

		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();
			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {
					if (age == null) {
						age = Integer.parseInt(valueString);
					} else {
						throw new ConfigurationException("Double age tag.");
					}
				} else {
					if ("sex".equalsIgnoreCase(leafName)) {
						if (sex == null) {
							sex = Integer.parseInt(valueString);
						} else {
							throw new ConfigurationException("Double sex tag.");
						}
					} else {
						if ("from".equalsIgnoreCase(leafName)) {
							if (from == null) {
								from = Integer.parseInt(valueString);
							} else {
								throw new ConfigurationException(
										"Double from tag.");
							}
						} else {
							if ("to".equalsIgnoreCase(leafName)) {
								if (to == null) {
									to = Integer.parseInt(valueString);
								} else {
									throw new ConfigurationException(
											"Double to tag.");
								}
							} else {
								if ("value".equalsIgnoreCase(leafName)) {
									if (value == null) {
										value = Float.parseFloat(valueString);
									} else {
										throw new ConfigurationException(
												"Double value tag.");
									}
								} else {
									throw new ConfigurationException(
											"Unexpected tag: " + leafName);
								}
							}
						}
					}
				}
			} else {
				throw new ConfigurationException("Value is no String!");
			}
		} // for leafChildren
		SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>> sexMap = null;
		DestinationsByOriginMap<ValueByDestinationMap<Float>> originMap = null;
		ValueByDestinationMap<Float> destinyMap = null;
		Float storedValue = null;
		boolean newBranch = false;
		if (ageMap == null) {
			newBranch = true;
			ageMap = new AgeMap<SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>>>();
		} else {
			sexMap = ageMap.get(age);
		}
		if (sexMap == null) {
			newBranch = true;
			sexMap = new SexMap<DestinationsByOriginMap<ValueByDestinationMap<Float>>>();
			ageMap.put(age, sexMap);
		} else {
			originMap = sexMap.get(from);
		}
		if (originMap == null) {
			originMap = new DestinationsByOriginMap<ValueByDestinationMap<Float>>();
			sexMap.put(sex, originMap);
		} else {
			destinyMap = originMap.get(from);
		}
		if (destinyMap == null) {
			destinyMap = new ValueByDestinationMap<Float>();
			originMap.put(from, destinyMap);
		} else {
			storedValue = destinyMap.get(to);
		}
		if (storedValue != null) {
			throw new ConfigurationException("Duplicate value for age: " + age
					+ " sex: " + sex + " from: " + from + " to: " + to
					+ "\nPresentValue: " + storedValue + " newValue: " + value);
		}
		destinyMap.put(to, value);
		log.debug("Processed value for age: " + age + " sex: " + sex
				+ " from: " + from + " to: " + to + " value: " + value);
		return ageMap;
	}

	private static void handleAgeTags(
			ConfigurationNode confNode,
			AgeSteppedContainer<BiGenderSteppedContainer<Integer>> outerContainer)
			throws ConfigurationException {
		float expectedAge = 0;
		int step = 0;
		List theChildren = confNode.getChildren();
		for (Object child : theChildren) {
			ConfigurationNode castedChild = (ConfigurationNode) child;
			if (AgeSteppedContainer.ageWrapperTagName.equals(castedChild
					.getName())) {
				float ageValue = getAndDecodeAgeValue(castedChild);
				if (expectedAge == ageValue) {
					log.fatal("Age value " + ageValue + " as expected.");
					outerContainer.put(step,
							GenderSteppedIntegersFromXMLFactory
									.manufacture(castedChild));
				} else {
					throw new ConfigurationException("Age value is \""
							+ ageValue + "\" expected \"" + expectedAge + "\"");
				}
				expectedAge = expectedAge + outerContainer.getAgeStepSize();
				step++;
			} else {
				throw new ConfigurationException("\""
						+ AgeSteppedContainer.ageWrapperTagName
						+ "\" tag expected at this point, \""
						+ castedChild.getName() + "\" tag found.");
			}
		}
		log
				.fatal("AgeSteppedContainer<BiGenderSteppedContainer<Integer>> contains "
						+ outerContainer.size() + " units.");
	}

	private static int getAndDecodeNumberOfSteps(ConfigurationNode confNode) {
		List numStepsAttributes = confNode
				.getAttributes(AgeSteppedContainer.numberOfStepsAttributeName);
		ConfigurationNode numStepsNode = (ConfigurationNode) numStepsAttributes
				.get(0);
		String numStepsString = (String) numStepsNode.getValue();
		int numSteps = Integer.parseInt(numStepsString);
		return numSteps;
	}

	private static float getAndDecodeAgeStep(ConfigurationNode confNode) {
		List ageStepAttributes = confNode
				.getAttributes(AgeSteppedContainer.ageStepAttributeName);
		ConfigurationNode ageStepNode = (ConfigurationNode) ageStepAttributes
				.get(0);
		String ageStepString = (String) ageStepNode.getValue();
		float ageStep = Float.parseFloat(ageStepString);
		return ageStep;
	}

	private static float getAndDecodeAgeValue(ConfigurationNode confNode) {
		List ageStepAttributes = confNode
				.getAttributes(AgeSteppedContainer.ageValueAttributeName);
		ConfigurationNode ageValueAttributeNode = (ConfigurationNode) ageStepAttributes
				.get(0);
		String ageValueString = (String) ageValueAttributeNode.getValue();
		float ageValue = Float.parseFloat(ageValueString);
		return ageValue;
	}
}
