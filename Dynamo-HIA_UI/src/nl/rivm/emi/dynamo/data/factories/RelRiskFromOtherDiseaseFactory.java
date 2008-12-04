package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskFromOtherDiseaseObject;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskFromOtherDiseaseFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public RelRiskFromOtherDiseaseObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public RelRiskFromOtherDiseaseObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		return  new RelRiskFromOtherDiseaseObject( manufacture(configurationFile, true));
	}

	public RelRiskFromOtherDiseaseObject manufacture(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		RelRiskFromOtherDiseaseObject result = new RelRiskFromOtherDiseaseObject(producedMap);
		return (result); 
	}
	public RelRiskFromOtherDiseaseObject manufactureDefault() throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("value"), null));
		return new RelRiskFromOtherDiseaseObject(super.manufactureDefault(leafNodeList, false));
	}


//	/**
//	 * 
//	 * @param configurationFile
//	 * @return
//	 * @throws ConfigurationException
//	 */
//	public float[][] manufactureArrayFromFlatXML(File configurationFile)
//			throws ConfigurationException {
//		float[][] theArray = null;
//		RelRiskFromOtherDiseaseObject theMap = manufactureObservable(configurationFile);
//		int ageDim = theMap.size();
//		SexMap<IObservable> sexMap = theMap.get(new Float(0));
//		int sexDim = sexMap.size();
//		theArray = new float[ageDim][sexDim];
//		IObservable theObservable = null;
//		log.debug("Array sizes: age " + ageDim + " sex: " + sexDim);
//		for (int ageCount = 0; ageCount < ageDim; ageCount++) {
//			sexMap = theMap.get(new Float(ageCount));
//			if (sexMap == null) {
//				throw new ConfigurationException(
//						"Incomplete set of sexes for age " + ageCount);
//			}
//			for (int sexCount = 0; sexCount < sexDim; sexCount++) {
//				theObservable = sexMap.get(new Float(sexCount));
//				if (theObservable != null) {
//					log.debug("Putting value " + theObservable + " for age "
//							+ ageCount + " sex: " + sexCount);
//					theArray[ageCount][sexCount] = ((Float) ((WritableValue) theObservable)
//							.doGetValue()).floatValue();
//				} else {
//					throw new ConfigurationException(
//							"Incomplete set of values for age " + ageCount
//									+ ",sex " + sexCount);
//				}
//			}
//		}
//		return theArray;
//	}
}
