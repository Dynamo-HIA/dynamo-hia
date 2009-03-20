package nl.rivm.emi.dynamo.data.factories;

import java.io.File;


import nl.rivm.emi.dynamo.data.TypedHashMap;

import org.apache.commons.configuration.ConfigurationException;

public class DummyPlaceholderFactory extends AgnosticFactory {

	public TypedHashMap manufactureObservable(File configurationFile, String rootElementName)
			throws ConfigurationException {
	throw new ConfigurationException("This Factory has not been implemented yet.");
	}
	@Override
	public TypedHashMap manufacture(
			File configurationFile, String rootElementName) throws ConfigurationException {
		throw new ConfigurationException("This Factory has not been implemented yet.");
	}
	public TypedHashMap manufactureDefault() throws ConfigurationException {
		throw new ConfigurationException("This Factory has not been implemented yet.");
	}

	@Override
	public TypedHashMap manufactureObservableDefault()
			throws ConfigurationException {
		return null;
	}
}
