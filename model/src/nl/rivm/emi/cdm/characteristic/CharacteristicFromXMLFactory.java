package nl.rivm.emi.cdm.characteristic;

/**
 * Class for manufacturing a <code>Characteristic</code> <code>Object</code> 
 * from an XML element. 
 */
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.characteristic.types.AbstractCategoricalCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractCompoundCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.AbstractContinuousCharacteristicType;
import nl.rivm.emi.cdm.characteristic.types.CharacteristicTypesContainer;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

public class CharacteristicFromXMLFactory {
	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains the index of the <Code>Characteristic</code>.
	 */
	public static final String indexLabel = "id";

	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains the label of the <Code>Characteristic</code>.
	 */
	public static final String labelLabel = "lb";

	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains the type of the <Code>Characteristic</code>.
	 */
	public static final String typeLabel = "type";

	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains the possible values of the <Code>Characteristic</code>.
	 */
	public static final String possibleValuesLabel = "possiblevalues";

	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains a value for the <Code>Characteristic</code>.
	 */
	public static final String valueLabel = "vl";
	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains the limits of the <Code>Characteristic</code>.
	 */
	
	/* added by Hendriek */
	public static final String numberOfElementsLabel = "numberofelements";
	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains the limits of the <Code>Characteristic</code>.
	 */
	public static final String limitsLabel = "limits";

	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains the lower limit of the <Code>Characteristic</code>.
	 */
	public static final String lowerLimitLabel = "lower";

	/**
	 * <code>String</code> containing the element in the configuration file
	 * that contains the upper limit of the <Code>Characteristic</code>.
	 */
	public static final String upperLimitLabel = "upper";

	/**
	 * Method that creates a <code>Characteristic</code> Object from the
	 * (sub)configuration passed to it.
	 * 
	 * @param characteristicConfiguration
	 *            (sub)configuration containing data for a
	 *            <code>Characteristic</code>.
	 * @return A <code>Characteristic</code> Object if successfull.
	 * @throws ConfigurationException
	 *             When the supplied configuration does not conform to the
	 *             expected configuration structure.
	 */
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
			/* added by Hendriek */	
			if (type.isCompoundType()) {try{
				int numberOfElements = characteristicConfiguration.getInt(numberOfElementsLabel);
					characteristic.setNumberOfElements(numberOfElements);
			} catch (NoSuchElementException e) {
				throw new ConfigurationException(
						CDMConfigurationException.noCharacteristicLabelMessage);
			}
				/* fillLimits is not yet implemented for this type */
				fillLimits(characteristicConfiguration,
						(AbstractCompoundCharacteristicType) type);
			}
			else{
			fillLimits(characteristicConfiguration,
					(AbstractContinuousCharacteristicType) type);
		}
		
		}
		
		characteristic.setType(type);
		return characteristic;
	}

	/**
	 * @param characteristicConfiguration
	 * @param type
	 */
	private static void fillLimits(
			HierarchicalConfiguration characteristicConfiguration,
			AbstractCompoundCharacteristicType type) {
		// TODO Auto-generated method stub
		// Not yet implemented
		
	}

	/**
	 * Method that creates a <code>CharacteristicType</code> Object from the
	 * (sub)configuration passed to it.
	 * 
	 * @param characteristicTypeConfiguration
	 *            (sub)configuration containing data for a
	 *            <code>AbstractCharacteristicType</code>.
	 * @return A <code>CharacteristicType</code> Object if successfull.
	 * @throws ConfigurationException
	 *             When the supplied configuration does not conform to the
	 *             expected configuration structure.
	 */
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

	/**
	 * Method that instantiates a <code>CharacteristicType</code> Object from
	 * the type<code>Class</code> passed to it.
	 * 
	 * @param typeClass
	 *            <code>Class</code> to be instantiated.
	 * @return A <code>AbstractCharacteristicType</code> Object if
	 *         successfull.
	 * @throws ConfigurationException
	 *             When the supplied <Code>Class</code> cannot be
	 *             instantiated.
	 */
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

	/**
	 * Method that creates values and puts them in the possiblevalues of the
	 * categorical <code>Characteristic</code> from the (sub)configuration
	 * passed to it.
	 * 
	 * @param characteristicConfiguration
	 *            (sub)configuration containing data for the possible values.
	 * @throws ConfigurationException
	 *             When the supplied configuration does not conform to the
	 *             expected configuration structure.
	 */
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

	/**
	 * Method that creates values and puts them in the limits of the continuous
	 * <code>Characteristic</code> from the (sub)configuration passed to it.
	 * 
	 * @param characteristicConfiguration
	 *            (sub)configuration containing data for the possible values.
	 * @param catType
	 *            The <code>CharacteristicType</code> Object that should be
	 *            filled.
	 * @throws ConfigurationException
	 *             When the supplied configuration does not conform to the
	 *             expected configuration structure.
	 */
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
