package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

/**
 * @author mondeelr
 * 
 *         Factory that creates a LinkedHashMap based on a configurationfile
 *         with multiple rootchildren and a categorical part.
 *         
 */
public abstract class AgnosticCategoricalGroupFactory extends
		AgnosticGroupFactory implements CategoricalFactory {

	protected Integer numberOfCategories = null;

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.CategoricalFactory#setNumberOfCategories(java.lang.Integer)
	 */
	@Override
	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufacture(java.io.File, boolean, java.lang.String)
	 */
	@Override
	public LinkedHashMap<String, Object> manufacture(File configurationFile,
			boolean makeObservable, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> resultObject = super.manufacture(
				configurationFile, makeObservable, rootElementName);
		TypedHashMap<Class> classesMap = (TypedHashMap<Class>) resultObject
				.get(XMLTagEntityEnum.CLASSES.getElementName());
		// TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) ageMap.get(0);
		int numberOfClasses = classesMap.size();
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
