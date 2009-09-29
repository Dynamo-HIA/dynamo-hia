package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

public abstract class AgnosticCategoricalGroupFactory extends
		AgnosticGroupFactory implements CategoricalFactory {

	protected Integer numberOfCategories = null;

	@Override
	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	@Override
	public 	LinkedHashMap<String, Object> manufacture(File configurationFile,
			boolean makeObservable, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> resultObject = super.manufacture(configurationFile,
				makeObservable, rootElementName);
		TypedHashMap<Class> classesMap = (TypedHashMap<Class>) resultObject.get(XMLTagEntityEnum.CLASSES.getElementName());
//		TypedHashMap<Sex> sexMap = (TypedHashMap<Sex>) ageMap.get(0);
		int numberOfClasses = classesMap.size();
		if ((numberOfCategories != null)
				&& (!numberOfCategories.equals(numberOfClasses))) {
			throw new DynamoInconsistentDataException("Inconsistent data!\n"
					+ "Reading a file based on " + numberOfClasses
					+ " categories, but the Riskfactor has " + numberOfCategories
					+ "\nIf this is an existing file,\n"
					+ "you must delete it\n"
					+ "or it wil cause problems during the simulation.");
		}
		return resultObject;
	}


}
