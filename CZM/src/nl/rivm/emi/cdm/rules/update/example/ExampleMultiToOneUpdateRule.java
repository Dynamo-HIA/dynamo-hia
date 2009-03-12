package nl.rivm.emi.cdm.rules.update.example;

import java.io.File;
import java.util.List;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation, must be generified later to UpdateRuleEntryLayer.
 * 
 * @author mondeelr
 * 
 */
public class ExampleMultiToOneUpdateRule extends ManyToOneUpdateRuleBase
		implements ConfigurationEntryPoint {

	Log log = LogFactory.getLog(this.getClass().getName());

	String[] requiredTags = { "characteristicindex1", "parameter1",
			"characteristicindex2", "parameter2" };

	int characteristicIndex1 = -1;

	int parameter1 = -1;

	int characteristicIndex2 = -1;

	int parameter2 = -1;

	//@Override
	/*
	public Object update(Object[] currentValues) {
		Integer newValue = null;
		if ((currentValues[characteristicIndex1] != null)
				&& (currentValues[characteristicIndex2] != null)) {
			if ((currentValues[characteristicIndex1] instanceof Integer)
					&& (currentValues[characteristicIndex2] instanceof Integer)) {
				int currentValue1 = ((Integer) currentValues[characteristicIndex1])
						.intValue();
				newValue = currentValue1 + parameter1;
				int currentValue2 = ((Integer) currentValues[characteristicIndex2])
						.intValue();
				newValue = currentValue2 + parameter2 + newValue.intValue();
			}
		}
		return newValue;
	}
	*/

	/**
	 */
	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		try {
			boolean success = false;
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					configurationFile);
			
			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFileConfiguration.clear();
			
			// Validate the xml by xsd schema
			configurationFileConfiguration.setValidating(true);			
			configurationFileConfiguration.load();
			
			List<SubnodeConfiguration> snConf = configurationFileConfiguration
					.configurationsAt("param");
			if (!((snConf == null) || (snConf.isEmpty() || (snConf.size() > 1)))) {
				SubnodeConfiguration tagConf = snConf.get(0);
				ConfigurationNode confNode = tagConf.getRootNode();
				List children = confNode.getChildren();
				if (children.size() == 4) {
					for (int innerdex = 0; innerdex < children.size(); innerdex++) {
						ConfigurationNode childNode = (ConfigurationNode) children
								.get(innerdex);
						String value = (String) childNode.getValue();
						Integer intValue = Integer.parseInt(value);
						switch (innerdex) {
						case 0:
							characteristicIndex1 = intValue;
							break;
						case 1:
							parameter1 = intValue;
							break;
						case 2:
							characteristicIndex2 = intValue;
							break;
						case 3:
							parameter2 = intValue;
							break;
						}
					}
				}
			} else {
				throw new ConfigurationException(
						String
								.format(
										CDMConfigurationException.invalidUpdateRuleConfigurationFileFormatMessage,
										configurationFile.getName(), this
												.getClass().getSimpleName()));
			}
			return (false);			
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(log, "", e, configurationFile.getAbsolutePath());
		}						
		return (false);
	}
	
	@Override
	public Object update(Object[] currentValues, Long seed)
			throws CDMUpdateRuleException, CDMUpdateRuleException {
		Integer newValue = null;
		if ((currentValues[characteristicIndex1] != null)
				&& (currentValues[characteristicIndex2] != null)) {
			if ((currentValues[characteristicIndex1] instanceof Integer)
					&& (currentValues[characteristicIndex2] instanceof Integer)) {
				int currentValue1 = ((Integer) currentValues[characteristicIndex1])
						.intValue();
				newValue = currentValue1 + parameter1;
				int currentValue2 = ((Integer) currentValues[characteristicIndex2])
						.intValue();
				newValue = currentValue2 + parameter2 + newValue.intValue();
			}
		}
		return newValue;

	}
}
