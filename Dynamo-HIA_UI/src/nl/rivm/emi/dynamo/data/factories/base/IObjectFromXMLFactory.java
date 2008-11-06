package nl.rivm.emi.dynamo.data.factories.base;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;

public interface IObjectFromXMLFactory<T> {

	public abstract T manufactureFromFlatXML(
			File configurationFile) throws ConfigurationException;

	public abstract T constructAllZeroesModel();

}