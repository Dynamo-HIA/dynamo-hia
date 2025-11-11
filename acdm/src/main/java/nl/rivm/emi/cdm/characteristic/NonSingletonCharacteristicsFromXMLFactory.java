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

public class NonSingletonCharacteristicsFromXMLFactory{

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	Log log = LogFactory.getLog(getClass().getName());

	static final String containerTag = "characteristics";

	static final String characteristicTag = "ch";

	static final String indexTag = "id";

	static final String typeTag = "type";

	static final String possibleValuesTag = "possiblevalues";
	/* added by Hendriek for compound type */

	static final String numberOfValuesTag = "numberofvalues";

	static final String valueTag = "vl";

	/**
	 * When the characteristics-configurationfile supplied in the constructor is
	 * valid this method clears the CharacteristicsConfigurationSingleton and
	 * fills it from the characteristics-configurationfile. Does nothing
	 * otherwise.
	 * 
	 * @return null if the configuration was not changed.
	 * @throws ConfigurationException
	 */
	static public CharacteristicsConfigurationMap createConfigurationObject(
			File configurationFile) throws ConfigurationException {
		if (!configurationFile.exists() || !configurationFile.canRead()
				|| !configurationFile.isFile()) {
			throw new ConfigurationException(
					CDMConfigurationException.noFileMessage);
		}
		XMLConfiguration characteristicsXMLConfiguration = new XMLConfiguration(
				configurationFile);

		CharacteristicsConfigurationMap characteristicsConfigurationObject = null;

		@SuppressWarnings("unchecked")
		List<HierarchicalConfiguration> characteristicConfigurations = characteristicsXMLConfiguration
				.configurationsAt(characteristicTag);
		if (characteristicConfigurations.size() > 0) {
			// The XML contains at least one Characteristic-tag.
			characteristicsConfigurationObject = new CharacteristicsConfigurationMap();
			Iterator<HierarchicalConfiguration> characteristicConfigurationsIterator = characteristicConfigurations
					.iterator();
			while (characteristicConfigurationsIterator.hasNext()) {
				HierarchicalConfiguration characteristicConfiguration = characteristicConfigurationsIterator
						.next();
				Characteristic charInstance = CharacteristicFromXMLFactory
						.manufacture(characteristicConfiguration);
				characteristicsConfigurationObject.put(charInstance.getIndex(),
						charInstance);
			}
		} else {
			throw new ConfigurationException(
					CDMConfigurationException.noCharacteristicMessage);
		}
		return characteristicsConfigurationObject;
	}
}
