package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;

import org.apache.commons.configuration.ConfigurationException;

/**
 * @author mondeelr<br/>
 * 
 *        Factory that does nothing but extending AgnosticFactory. Used for
 *         putting in the DispatchMap as a placeholder during development.
 */
public class DummyPlaceholderFactory extends AgnosticFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureObservable
	 * (java.io.File, java.lang.String)
	 */
	public TypedHashMap<Age> manufactureObservable(File configurationFile,
			String rootElementName) throws ConfigurationException {
		throw new ConfigurationException(
				"This Factory has not been implemented yet.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufacture(java.io
	 * .File, java.lang.String)
	 */
	@Override
	public TypedHashMap<Age> manufacture(File configurationFile,
			String rootElementName) throws ConfigurationException {
		throw new ConfigurationException(
				"This Factory has not been implemented yet.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureDefault()
	 */
	public TypedHashMap<Age> manufactureDefault() throws ConfigurationException {
		throw new ConfigurationException(
				"This Factory has not been implemented yet.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.data.factories.AgnosticFactory#
	 * manufactureObservableDefault()
	 */
	@Override
	public TypedHashMap<Age> manufactureObservableDefault()
			throws ConfigurationException {
		return null;
	}
}
