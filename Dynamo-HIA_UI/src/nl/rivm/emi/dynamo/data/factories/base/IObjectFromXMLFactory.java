package nl.rivm.emi.dynamo.data.factories.base;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;

public interface IObjectFromXMLFactory<S, O> {

	public abstract S manufacture(
			File configurationFile) throws ConfigurationException;

	public abstract O manufactureObservable(
			File configurationFile) throws ConfigurationException;
}