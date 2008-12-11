package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.objects.DALYWeightsObject;

import org.apache.commons.configuration.ConfigurationException;

public class DummyPlaceholderFactory extends AgnosticFactory {

	public DALYWeightsObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
	throw new ConfigurationException("This Factory has not been implemented yet.");
	}

	public DALYWeightsObject manufacture(
			File configurationFile) throws ConfigurationException {
		throw new ConfigurationException("This Factory has not been implemented yet.");
	}
	public DALYWeightsObject manufactureDefault() throws ConfigurationException {
		throw new ConfigurationException("This Factory has not been implemented yet.");
	}
}
