package nl.rivm.emi.cdm.characteristic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class CharacteristicFromXMLFactory {
	public static final String indexLabel = "id";

	public static final String labelLabel = "lb";

	public static final String typeLabel = "type";

	public static final String possibleValuesLabel = "possiblevalues";

	public static final String valueLabel = "vl";

	public static Characteristic manufacture(
			HierarchicalConfiguration characteristicConfiguration)
			throws ConfigurationException {
		Characteristic characteristic = new Characteristic();
		try {
			int index = characteristicConfiguration.getInt(indexLabel);
			characteristic.setIndex(index);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicIndexMessage);
		}
		try {
			String label = characteristicConfiguration.getString(labelLabel);
			if (label == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicLabelMessage);
			}
			characteristic.setLabel(label);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicLabelMessage);
		}
		try {
			String type = characteristicConfiguration.getString(typeLabel);
			if (type == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicTypeMessage);
			}
			characteristic.setType(type);
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicTypeMessage);
		}
		if (!CharacteristicType.continuousTypeString.equals(characteristic
				.getType())) {
			List<SubnodeConfiguration> possibleValuesConfigurations = characteristicConfiguration
					.configurationsAt(possibleValuesLabel);
			if (possibleValuesConfigurations.size() == 1) {
				SubnodeConfiguration possibleValuesConfiguration = possibleValuesConfigurations
						.get(0);
				List<SubnodeConfiguration> valueConfigurations = possibleValuesConfiguration
						.configurationsAt(valueLabel);
				if (valueConfigurations.size() > 0) {
					Iterator<SubnodeConfiguration> iterator = valueConfigurations
							.iterator();
					while (iterator.hasNext()) {
						SubnodeConfiguration currentValueNode = iterator.next();
						String value = currentValueNode.getString(valueLabel);
					}

				} else {
					throw new ConfigurationException(
							CDMConfigurationException.noCharacteristicValueMessage);
				}
			} else {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicPossibleValuesMessage);
			}
		}
		return characteristic;
	}
}
