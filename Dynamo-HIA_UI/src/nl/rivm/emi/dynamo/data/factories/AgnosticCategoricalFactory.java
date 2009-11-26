package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

/**
 * @author mondeelr
 * 
 *         Extra layer that does the checking of the number of categories of the
 *         riskfactor against that of the configurationfile read (either opened
 *         in place or about to be imported).
 */
public abstract class AgnosticCategoricalFactory extends AgnosticFactory
		implements CategoricalFactory {

	protected Integer numberOfCategories = null;

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.CategoricalFactory#setNumberOfCategories(java.lang.Integer)
	 */
	@Override
	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	/**
	 * Delegates the creation of the Object to the superclass. Afterwards checks
	 * the actual number of classes against the expected number. (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufacture(java.io.File,
	 *      boolean, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TypedHashMap<Age> manufacture(File configurationFile,
			boolean makeObservable, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap<Age> resultObject = super.manufacture(configurationFile,
				makeObservable, rootElementName);
		TypedHashMap<Age> ageMap = (TypedHashMap<Age>) resultObject.get(0);
		TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) ageMap.get(0);
		int numberOfClasses = sexMap.size();
		if ((numberOfCategories != null)
				&& (!numberOfCategories.equals(numberOfClasses))) {
			throw new DynamoInconsistentDataException("Inconsistent data!\n"
					+ "Reading a file based on " + numberOfClasses
					+ " categories, but the Riskfactor has "
					+ numberOfCategories + "\nIf this is an existing file,\n"
					+ "you must delete it\n"
					+ "or it wil cause problems during the simulation.");
		}
		return resultObject;
	}
}
