package nl.rivm.emi.cdm.characteristic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.rivm.emi.cdm.characteristic.types.AbstractCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractContinuousCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.CharacteristicTypesContainer;
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

	public static final String limitsLabel = "limits";

	public static final String lowerLimitLabel = "lower";

	public static final String upperLimitLabel = "upper";

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
		AbstractCharacteristicType type = handleConfigurationType(characteristicConfiguration);
		if (type == null) {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicTypeMessage);
		}
		if (type.isCategoricalType()) {
			fillPossibleValues(characteristicConfiguration,
					(AbstractCategoricalCharacteristicType) type);
		} else {
			fillLimits(characteristicConfiguration,
					(AbstractContinuousCharacteristicType) type);
		}
		characteristic.setType(type);
		return characteristic;
	}

	private static AbstractCharacteristicType handleConfigurationType(
			HierarchicalConfiguration characteristicConfiguration)
			throws ConfigurationException {
		AbstractCharacteristicType type = null;
		try {
			String typeLabel = characteristicConfiguration
					.getString(CharacteristicFromXMLFactory.typeLabel);
			if (typeLabel == null) {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicTypeMessage);
			}
			CharacteristicTypesContainer container = CharacteristicTypesContainer
					.getInstance();
			Class typeClass = container.get(typeLabel);
			if (typeClass == null) {
				throw new ConfigurationException(String.format(
						"Class for typeLabel  %1$s could not be found.",
						typeLabel));
			}
			type = instantiateTypeClass(typeClass);
			return type;
		} catch (NoSuchElementException e) {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicTypeMessage);
		}
	}

	private static AbstractCharacteristicType instantiateTypeClass(
			Class typeClass) throws ConfigurationException {
		AbstractCharacteristicType type;
		try {
			type = (AbstractCharacteristicType) typeClass.newInstance();
		} catch (InstantiationException e) {
			throw new ConfigurationException(String.format(
					"Class %1$s could not be instantiated.", typeClass
							.getName()));
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(String.format(
					"Illegal access for Class %1$s.", typeClass.getName()));
		}
		if ((type == null) || !(type instanceof AbstractCharacteristicType)) {
			throw new ConfigurationException(
					"Unexpected Class in CharacteristicTypesContainer.");
		}
		return type;
	}

	private static void fillPossibleValues(
			HierarchicalConfiguration characteristicConfiguration,
			AbstractCategoricalCharacteristicType catType)
			throws ConfigurationException {
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
					Object valueObject = currentValueNode.getRootNode()
							.getValue();
					String value = null;
					if (valueObject instanceof String) {
						value = (String) valueObject;
					}
					catType.addPossibleValue(value);
				}
			} else {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicPossibleValueValueMessage);
			}
		} else {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicPossibleValuesMessage);
		}
	}

	public static void fillLimits(
			HierarchicalConfiguration characteristicConfiguration,
			AbstractContinuousCharacteristicType catType)
			throws ConfigurationException {
		List<SubnodeConfiguration> possibleValuesConfigurations = characteristicConfiguration
				.configurationsAt(limitsLabel);
		if (possibleValuesConfigurations.size() == 1) {
			SubnodeConfiguration limitsConfiguration = possibleValuesConfigurations
					.get(0);
			List<SubnodeConfiguration> valueConfigurations;
			for (int count = 0; count < 2; count++) {
				switch (count) {
				case 0:
					String lowerLimit = null;
					valueConfigurations = limitsConfiguration
							.configurationsAt(lowerLimitLabel);
					if (valueConfigurations.size() == 1) {
						SubnodeConfiguration currentValueNode = valueConfigurations
								.get(0);
						Object valueObject = currentValueNode.getRootNode()
								.getValue();
						String value = null;
						if (valueObject instanceof String) {
							value = (String) valueObject;
						}
						lowerLimit = value;
					} else {
						throw new ConfigurationException(
								CDMConfigurationException.noCharacteristicPossibleValueValueMessage);
					}
					if (lowerLimit != null) {
						try {
							catType.setLowerLimit(lowerLimit);
						} catch (NumberFormatException e) {
							throw new ConfigurationException(
									CDMConfigurationException.wrongCharacteristicLowerLimitConfigurationFormatMessage);
						}
					} else {
						throw new ConfigurationException(
								CDMConfigurationException.noCharacteristicLimitsValueMessage);
					}
					break;
				case 1:
					String upperLimit = null;
					valueConfigurations = limitsConfiguration
							.configurationsAt(upperLimitLabel);
					if (valueConfigurations.size() == 1) {
						SubnodeConfiguration currentValueNode = valueConfigurations
								.get(0);
						Object valueObject = currentValueNode.getRootNode()
								.getValue();
						String value = null;
						if (valueObject instanceof String) {
							value = (String) valueObject;
						}
						upperLimit = value;
					} else {
						throw new ConfigurationException(
								CDMConfigurationException.noCharacteristicPossibleValueValueMessage);
					}
					if (upperLimit != null) {
						try {
							catType.setUpperLimit(upperLimit);
						} catch (NumberFormatException e) {
							throw new ConfigurationException(
									CDMConfigurationException.wrongCharacteristicUpperLimitConfigurationFormatMessage);
						}
					} else {
						throw new ConfigurationException(
								CDMConfigurationException.noCharacteristicLimitsValueMessage);
					}
					break;
				default:
					throw new ConfigurationException(
							CDMConfigurationException.noCharacteristicPossibleValueValueMessage);
				}
			}
		} else {
			// No limits in configuration, configure defaults.
			catType.setLimits(null, null);
		}
	}
}
