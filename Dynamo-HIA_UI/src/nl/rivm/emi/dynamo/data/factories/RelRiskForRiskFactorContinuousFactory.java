package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskForRiskFactorContinuousObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskForRiskFactorContinuousFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public RelRiskForRiskFactorContinuousObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public RelRiskForRiskFactorContinuousObject manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		return  new RelRiskForRiskFactorContinuousObject( manufacture(configurationFile, true));
	}

	public RelRiskForRiskFactorContinuousObject manufacture(
			File configurationFile) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		RelRiskForRiskFactorContinuousObject result = new RelRiskForRiskFactorContinuousObject(producedMap);
		return (result); 
	}

	@Override
	public TypedHashMap manufactureDefault()
			throws ConfigurationException {
		return manufactureDefault(false);
	}

	@Override
	public TypedHashMap manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}
	public RelRiskForRiskFactorContinuousObject manufactureDefault(boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntitySingleton
				.getInstance().get("value"), null));
		return new RelRiskForRiskFactorContinuousObject(super.manufactureDefault(leafNodeList, makeObservable));
	}
}