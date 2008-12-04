package nl.rivm.emi.dynamo.data.factories;

/**
 * This is the new factory based on the AgnosticFactory.
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 */
import java.io.File;
import java.util.List;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.containers.AgeMap;
import nl.rivm.emi.dynamo.data.containers.SexMap;
import nl.rivm.emi.dynamo.data.objects.IncidencesObject;
import nl.rivm.emi.dynamo.data.objects.ObservableIncidencesObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class IncidencesFactory extends AgnosticFactory {
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
//		IncidencesObject theMap = manufacture(configurationFile, false);
//		int ageDim = theMap.size();
//		SexMap<Float> sexMap = theMap.get(new Float(0));
//		int sexDim = sexMap.size();
//		theArray = new float[ageDim][sexDim];
//		Float theFloat = null;
//		log.debug("Array sizes: age " + ageDim + " sex: " + sexDim);
//		for (int ageCount = 0; ageCount < ageDim; ageCount++) {
//			sexMap = theMap.get(new Float(ageCount));
//			if (sexMap == null) {
//				throw new ConfigurationException(
//						"Incomplete set of sexes for age " + ageCount);
//			}
//			for (int sexCount = 0; sexCount < sexDim; sexCount++) {
//				theFloat = sexMap.get(new Float(sexCount));
//				if (theFloat != null) {
//					log.debug("Putting value " + theFloat + " for age "
//							+ ageCount + " sex: " + sexCount);
//					theArray[ageCount][sexCount] = theFloat;
//				} else {
//					throw new ConfigurationException(
//							"Incomplete set of values for age " + ageCount
//									+ ",sex " + sexCount);
//				}
//			}
//		}
		return theArray;
	}

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 */
	public IncidencesObject manufactureObservable(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true);
		IncidencesObject result = new IncidencesObject(producedMap);
		return (result); 
	}

	public IncidencesObject manufacture(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		IncidencesObject result = new IncidencesObject(producedMap);
		return (result); 
	}
	public IncidencesObject manufactureDefault() throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("value"), null));
		return (IncidencesObject) super.manufactureDefault(leafNodeList, false);
	}
}
