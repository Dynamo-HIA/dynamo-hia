package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.List;

import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.RRiskFromDiseaseObject;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RRiskFromDiseaseFactory implements
		IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker> {
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 */
	public float[][] manufactureArrayFromFlatXML(File configurationFile)
			throws ConfigurationException {
		float[][] theArray = null;
		AgeMap<SexMap<IObservable>> theMap = manufactureObservable(configurationFile);
		int ageDim = theMap.size();
		SexMap<IObservable> sexMap = theMap.get(new Float(0));
		int sexDim = sexMap.size();
		theArray = new float[ageDim][sexDim];
		IObservable theObservable = null;
		log.debug("Array sizes: age " + ageDim + " sex: " + sexDim);
		for (int ageCount = 0; ageCount < ageDim; ageCount++) {
			sexMap = theMap.get(new Float(ageCount));
			if (sexMap == null) {
				throw new ConfigurationException(
						"Incomplete set of sexes for age " + ageCount);
			}
			for (int sexCount = 0; sexCount < sexDim; sexCount++) {
				theObservable = sexMap.get(new Float(sexCount));
				if (theObservable != null) {
					log.debug("Putting value " + theObservable + " for age "
							+ ageCount + " sex: " + sexCount);
					theArray[ageCount][sexCount] = ((Float) ((WritableValue) theObservable)
							.doGetValue()).floatValue();
				} else {
					throw new ConfigurationException(
							"Incomplete set of values for age " + ageCount
									+ ",sex " + sexCount);
				}
			}
		}
		return theArray;
	}

	public RRiskFromDiseaseObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		RRiskFromDiseaseObject outerContainer = null;
		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);
			ConfigurationNode rootNode = configurationFromFile.getRootNode();
			List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
					.getChildren();
			for (ConfigurationNode rootChild : rootChildren) {
				log.debug("Handle rootChild: " + rootChild.getName());
				outerContainer = handleRootChild(rootChild, outerContainer);
			} // for rootChildren
			return outerContainer;
		} catch (ConfigurationException e) {
			log.error("Caught Exception of type: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception exception) {
			log.error("Caught Exception of type: "
					+ exception.getClass().getName() + " with message: "
					+ exception.getMessage());
			exception.printStackTrace();
			return null;
		}
	}

	private RRiskFromDiseaseObject handleRootChild(ConfigurationNode rootChild,
			RRiskFromDiseaseObject ageMap) throws ConfigurationException {
		// String rootChildName = rootChild.getName();
		// Object rootChildValueObject = rootChild.getValue();
		Integer age = null;
		Integer sex = null;
		Float value = null;

		List<ConfigurationNode> leafChildren = (List<ConfigurationNode>) rootChild
				.getChildren();
		for (ConfigurationNode leafChild : leafChildren) {
			log.debug("Handle leafChild: " + leafChild.getName());
			String leafName = leafChild.getName();
			Object valueObject = leafChild.getValue();
			if (valueObject instanceof String) {
				String valueString = (String) valueObject;
				if ("age".equalsIgnoreCase(leafName)) {
					if (age == null) {
						age = Integer.parseInt(valueString);
					} else {
						throw new ConfigurationException("Double age tag.");
					}
				} else {
					if ("sex".equalsIgnoreCase(leafName)) {
						if (sex == null) {
							sex = Integer.parseInt(valueString);
						} else {
							throw new ConfigurationException("Double sex tag.");
						}
					} else {
						if ("value".equalsIgnoreCase(leafName)) {
							if (value == null) {
								value = Float.parseFloat(valueString);
							} else {
								throw new ConfigurationException(
										"Double value tag.");
							}
						} else {
							throw new ConfigurationException("Unexpected tag: "
									+ leafName);
						}
					}
				}
			} else {
				throw new ConfigurationException("Value is no String!");
			}
		} // for leafChildren
		SexMap<IObservable> sexMap = null;
		IObservable storedValue = null;
		boolean newBranch = false;
		if (ageMap == null) {
			newBranch = true;
			ageMap = new RRiskFromDiseaseObject();
		} else {
			sexMap = ageMap.get(age);
		}
		if (sexMap == null) {
			newBranch = true;
			sexMap = new SexMap<IObservable>();
			ageMap.put(age, sexMap);
		} else {
			storedValue = sexMap.get(sex);
		}
		if (storedValue != null) {
			throw new ConfigurationException("Duplicate value for age: " + age
					+ " sex: " + sex + "\nPresentValue: "
					+ ((WritableValue) storedValue).doGetValue()
					+ " newValue: " + value);
		}
		log.debug("Processing value for age: " + age + " sex: " + sex
				+ " value: " + value);
		IObservable obsValue = new WritableValue(value, value);
		sexMap.put(sex, obsValue);
		log.debug("Processed value for age: " + age + " sex: " + sex
				+ " value: " + value);
		return ageMap;
	}

	public RRiskFromDiseaseObject constructObservableAllZeroesModel() {
		log.debug("Starting construction of empty model.");
		RRiskFromDiseaseObject theModel = new RRiskFromDiseaseObject();
		for (int ageCount = Age.MIN_VALUE; ageCount <= Age.MAX_VALUE; ageCount++) {
			theModel.put(new Integer(ageCount), constructAllZeroesSexMap());
		}
		return theModel;
	}

	private SexMap<IObservable> constructAllZeroesSexMap() {
		SexMap<IObservable> theSexMap = new SexMap<IObservable>();
		Float nul = new Float(0F);
		for (int sexCount = Sex.MIN_VALUE; sexCount <= Sex.MAX_VALUE; sexCount++) {
			theSexMap.put(new Integer(sexCount), new WritableValue(nul, nul
					.getClass()));
		}
		return theSexMap;
	}
}