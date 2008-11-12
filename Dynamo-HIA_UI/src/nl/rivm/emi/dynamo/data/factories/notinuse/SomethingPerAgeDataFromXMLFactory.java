package nl.rivm.emi.dynamo.data.factories.notinuse;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;

public class SomethingPerAgeDataFromXMLFactory {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.factories.IntegerPerAgeDataFromXMLFactory");

	public static AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> manufacture(
			File configurationFile) {
		AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> outerContainer = null;
		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
			List<SubnodeConfiguration> snConf = configurationFromFile
					.configurationsAt(AgeSteppedContainer.ageSteppedContainerTagName);
			if (snConf == null || snConf.isEmpty()) {
				log
						.error("No configuration or one without \"agesteppedcontainer\" tag found.");
			} else {
				if (snConf.size() > 1) {
					log
							.error("Configuration with more than one \"agesteppedcontainer\" tags found.");
				} else {
					SubnodeConfiguration tagConf = snConf.get(0);
					ConfigurationNode confNode = tagConf.getRootNode();
					float ageStepSize = getAndDecodeAgeStep(confNode);
					int numSteps = getAndDecodeNumberOfSteps(confNode);
					outerContainer = new AgeSteppedContainer<BiGenderSteppedContainer<IObservable>>(
							ageStepSize, numSteps);
					// TODO Maybe make configurable.
					handleAgeTags(confNode, outerContainer);
				}
			}
			return outerContainer;
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return outerContainer;
		}
	}

	private static void handleAgeTags(ConfigurationNode confNode,
			AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> outerContainer) throws ConfigurationException {
		float expectedAge = 0;
		int step = 0;
		List theChildren = confNode.getChildren();
		for (Object child : theChildren) {
			ConfigurationNode castedChild = (ConfigurationNode) child;
			if (AgeSteppedContainer.ageWrapperTagName
					.equals(castedChild.getName())) {
				float ageValue = getAndDecodeAgeValue(castedChild);
				if (expectedAge == ageValue) {
					log.fatal("Age value " + ageValue
							+ " as expected.");
					outerContainer.put(step, GenderSteppedIntegersFromXMLFactory.manufacture(castedChild));
				} else {
					throw new ConfigurationException("Age value is \"" + ageValue
							+ "\" expected \"" + expectedAge + "\"");
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
		log.fatal("AgeSteppedContainer<BiGenderSteppedContainer<IObservable>> contains " + outerContainer.size() + " units.");
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
