package nl.rivm.emi.cdm.characteristic;

/**
 * It is serialized to XML format: 
 * <characteristics>
 *  <characteristic>
 *   <index> </index>
 *   <label> </label>
 *  </characteristic>
 * </characteristics>
 */
import java.io.File;
import java.util.Iterator;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CharacteristicsXMLConfiguration extends XMLConfiguration {

	/**
	 * serial id not used
	 */
	private static final long serialVersionUID = 22L;

	Log log = LogFactory.getLog(getClass().getName());

	static final String containerTag = "characteristics";

	static final String characteristicTag = "ch";

	static final String indexTag = "id";

	static final String typeTag = "type";

	static final String possibleValuesTag = "possiblevalues";
	/* added by Hendriek for compound type*/
	
	static final String numberOfValuesTag = "numberofvalues";

	static final String valueTag = "vl";

	public CharacteristicsXMLConfiguration(String configurationFileName,
			String schemaFileName) throws ConfigurationException {
		super(configurationFileName);
		populateSingleton();
	}

	public CharacteristicsXMLConfiguration(File configurationFile)
			throws ConfigurationException {
		super(configurationFile);
		if (!configurationFile.exists() || !configurationFile.canRead()
				|| !configurationFile.isFile()) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
		populateSingleton();
	}

	/**
	 * When the characteristics-configurationfile supplied in the constructor is
	 * valid this method clears the CharacteristicsConfigurationSingleton and
	 * fills it from the characteristics-configurationfile. Does nothing
	 * otherwise.
	 * 
	 * @return null if the configuration was not changed.
	 * @throws ConfigurationException
	 */
	private CharacteristicsConfigurationMapSingleton populateSingleton()
			throws ConfigurationException {
		CharacteristicsConfigurationMapSingleton singleton = null;
		
		@SuppressWarnings("unchecked")
		List<HierarchicalConfiguration> characteristicConfigurations = configurationsAt(characteristicTag);
		if (characteristicConfigurations.size() > 0) {
			// The XML contains at least one Characteristic-tag.
			singleton = CharacteristicsConfigurationMapSingleton.getInstance();
			singleton.clear();
			if (singleton.isEmpty()) {
				Iterator<HierarchicalConfiguration> characteristicConfigurationsIterator = characteristicConfigurations
						.iterator();
				while (characteristicConfigurationsIterator.hasNext()) {
					HierarchicalConfiguration characteristicConfiguration = characteristicConfigurationsIterator
							.next();
					Characteristic charInstance = CharacteristicFromXMLFactory
							.manufacture(characteristicConfiguration);
					singleton.put(charInstance.getIndex(), charInstance);
				}
			}
		} else {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicMessage);
		}
		return singleton;
	}

	/**
	 * When the characteristics-configurationfile supplied in the constructor is
	 * valid this method clears the CharacteristicsConfigurationSingleton and
	 * fills it from the characteristics-configurationfile. Does nothing
	 * otherwise.
	 * 
	 * @return null if the configuration was not changed.
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("unused")
	private CharacteristicsConfigurationMapSingleton rePopulateSingleton()
			throws ConfigurationException {
		CharacteristicsConfigurationMapSingleton singleton = null;
		@SuppressWarnings("unchecked")
		List<HierarchicalConfiguration> characteristicConfigurations = configurationsAt(characteristicTag);
		if (characteristicConfigurations.size() > 0) {
			// The XML contains at least one Characteristic-tag.
			singleton = CharacteristicsConfigurationMapSingleton.getInstance();
			singleton.clear();
			Iterator<HierarchicalConfiguration> characteristicConfigurationsIterator = characteristicConfigurations
					.iterator();
			while (characteristicConfigurationsIterator.hasNext()) {
				HierarchicalConfiguration characteristicConfiguration = characteristicConfigurationsIterator
						.next();
				Characteristic charInstance = CharacteristicFromXMLFactory
						.manufacture(characteristicConfiguration);
				singleton.put(charInstance.getIndex(), charInstance);
			}
		} else {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicMessage);
		}
		return singleton;
	}
}
