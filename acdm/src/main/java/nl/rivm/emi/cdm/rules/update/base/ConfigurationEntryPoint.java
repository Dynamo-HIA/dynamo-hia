package nl.rivm.emi.cdm.rules.update.base;

import java.io.File;


import org.apache.commons.configuration.ConfigurationException;

/**
 * Marker interface that every update rule must "implement".
 * @author mondeelr
 *
 */
public interface ConfigurationEntryPoint extends ConfigurationLevel {

	public boolean loadConfigurationFile(File configurationFile) throws ConfigurationException;
}
