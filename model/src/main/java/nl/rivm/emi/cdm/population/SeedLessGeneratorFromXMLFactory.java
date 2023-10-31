package nl.rivm.emi.cdm.population;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class SeedLessGeneratorFromXMLFactory {
	public static final String labelLabel = "lb";

	public static final String populationSizeLabel = "populationsize";

	public static final String characteristicsLabel = "characteristics";

	public static final String characteristicIndexLabel = "id";

	public static SeedLessGenerator manufacture(
			HierarchicalConfiguration generatorConfiguration)
			throws ConfigurationException {
		int childrenCount = generatorConfiguration.getRootNode()
				.getChildrenCount();
		if (childrenCount == 0) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
		SeedLessGenerator generator = new SeedLessGenerator();
		try {
			String label = generatorConfiguration.getString(labelLabel);
			if (label == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noGeneratorLabelMessage);
			}
			generator.setLabel(label);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicLabelMessage);
		}
		try {
			int populationSize = generatorConfiguration
					.getInt(populationSizeLabel);
			generator.setPopulationSize(populationSize);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noGeneratorPopulationSizeMessage);
		}
		addCharacteristicsIds(generatorConfiguration, generator);
		return generator;
	}

	private static void addCharacteristicsIds(
			HierarchicalConfiguration generatorConfiguration,
			SeedLessGenerator generator) throws ConfigurationException {
		List<SubnodeConfiguration> characteristicsConfigurations = generatorConfiguration
				.configurationsAt(characteristicsLabel);
		if (characteristicsConfigurations.size() == 1) {
			SubnodeConfiguration characteristicsConfiguration = characteristicsConfigurations
					.get(0);
			List<SubnodeConfiguration> characteristicIdConfigurations = characteristicsConfiguration
					.configurationsAt(characteristicIndexLabel);
			if (characteristicIdConfigurations.size() > 0) {
				ArrayList<Integer> characteristicIds = new ArrayList<Integer>();
				Iterator<SubnodeConfiguration> iterator = characteristicIdConfigurations
						.iterator();
				while (iterator.hasNext()) {
					SubnodeConfiguration currentCharacteristicIdNode = iterator
							.next();
					Object value = currentCharacteristicIdNode.getRoot()
							.getValue();
					characteristicIds.add(Integer.valueOf((String) value));
				}
				generator.setCharacteristicIds(characteristicIds);
			} else {
				throw new ConfigurationException(
						CDMConfigurationException.noGeneratorCharacteristicIdMessage);
			}
		} else {
			throw new ConfigurationException(
					CDMConfigurationException.noGeneratorCharacteristicsMessage);
		}
	}
}
