package nl.rivm.emi.dynamo.data.factories.notinuse;

import java.util.List;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class GenderSteppedIntegersFromXMLFactory {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.data.factories.BiGenderSteppedContainer<Integer>");

	public static BiGenderSteppedContainer<IObservable> manufacture(
			ConfigurationNode configurationNode) throws ConfigurationException {
		BiGenderSteppedContainer<IObservable> innerContainer = new BiGenderSteppedContainer<IObservable>();
		float expectedGender = 0;
		List theChildren = configurationNode.getChildren();
		for (Object child : theChildren) {
			ConfigurationNode castedChild = (ConfigurationNode) child;
			if (BiGenderSteppedContainer.genderTagName.equals(castedChild
					.getName())) {
				int genderValue = getAndDecodeGenderValue(castedChild);
				if (expectedGender == genderValue) {
					log.debug("Gender value " + genderValue + " as expected.");
					String byGenderValueString = (String) castedChild
							.getValue();
					int byGenderValue = Integer.parseInt(byGenderValueString);
					Integer genderValueInteger = new Integer(genderValue);
					Integer byGenderValueInteger = new Integer(byGenderValue);
					IObservable byGenderValueWritable = new WritableValue(byGenderValueInteger, byGenderValueInteger);
					log.fatal("Gender value " + genderValueInteger + " byGenderValue " + ((WritableValue)byGenderValueWritable).doGetValue());
					innerContainer.put(genderValueInteger, byGenderValueWritable);
				} else {
					throw new ConfigurationException("Gender value is \""
							+ genderValue + "\" expected \"" + expectedGender
							+ "\"");
				}
				expectedGender = expectedGender + 1;
			} else {
				throw new ConfigurationException("\""
						+ BiGenderSteppedContainer.genderTagName
						+ "\" tag expected at this point, \""
						+ castedChild.getName() + "\" tag found.");
			}
		}
		return innerContainer;
	}

	private static int getAndDecodeGenderValue(ConfigurationNode confNode) {
		List ageStepAttributes = confNode
				.getAttributes(AgeSteppedContainer.ageValueAttributeName);
		ConfigurationNode ageValueAttributeNode = (ConfigurationNode) ageStepAttributes
				.get(0);
		String ageValueString = (String) ageValueAttributeNode.getValue();
		int ageValue = Integer.parseInt(ageValueString);
		return ageValue;
	}
}
