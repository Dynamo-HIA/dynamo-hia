package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskForDeathCompoundObject;
import nl.rivm.emi.dynamo.data.objects.RelRiskForDisabilityCompoundObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Begin;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.types.atomic.End;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelRiskForDisabilityCompoundFactory extends AgnosticFactory implements
		CategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	Integer numberOfCategories = null;

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	public TypedHashMap manufactureObservable(
			File configurationFile, String rootElementName) throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true, rootElementName);
		return new RelRiskForDisabilityCompoundObject(producedMap);
	}

	public TypedHashMap manufacture(File configurationFile, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false, rootElementName);
		RelRiskForDisabilityCompoundObject result = new RelRiskForDisabilityCompoundObject(
				producedMap);
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

	private RelRiskForDisabilityCompoundObject manufactureDefault(
			boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.AGE.getTheType(), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.SEX.getTheType(), null));
//		XMLTagEntitySingleton xmlTagEntitySingleton = XMLTagEntitySingleton.getInstance();
		CatContainer category = (CatContainer) XMLTagEntityEnum.CAT.getTheType();
		// TODO Clone to make threadsafe. Category clone = category.
		Integer oldMaxValue = category.setMAX_VALUE(numberOfCategories);
		log.debug("Set Category. MAX_VALUE to: " + numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(category, null));
		//leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.BEGIN.getTheType(), null));
		Begin customBegin = new Begin();
		customBegin.setDefaultValue(1F);
		leafNodeList.add(new AtomicTypeObjectTuple(customBegin, null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.ALFA.getTheType(), null));
//		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.END.getTheType(), null));
		End customEnd = new End();
		customEnd.setDefaultValue(1F);
		leafNodeList.add(new AtomicTypeObjectTuple(customEnd, null));
		TypedHashMap<Age> manufacturedMap = super.manufactureDefault(
				leafNodeList, makeObservable);
		RelRiskForDisabilityCompoundObject result = new RelRiskForDisabilityCompoundObject(
				manufacturedMap);
		return result;
	}

}
