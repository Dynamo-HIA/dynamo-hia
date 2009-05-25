package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.DurationDistributionObject;
import nl.rivm.emi.dynamo.data.objects.TransitionMatrixObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DurationDistributionFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public TypedHashMap manufactureObservable(File configurationFile,
			String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true,
				rootElementName);
		DurationDistributionObject result = new DurationDistributionObject(producedMap);
		return (result);
	}

	public TypedHashMap manufacture(File configurationFile, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false, 
				rootElementName);
		DurationDistributionObject result = new DurationDistributionObject(producedMap);
		return (result);
	}

	@Override
	public TypedHashMap manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	@Override
	public TypedHashMap manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}


	public DurationDistributionObject manufactureDefault(boolean makeObservable)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.AGE.getTheType(), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.SEX.getTheType(), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.DURATION.getTheType(), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.PERCENTAGE.getTheType(), null));
		DurationDistributionObject theObject = new DurationDistributionObject(super
				.manufactureDefault(leafNodeList, makeObservable));
		return theObject;
	}
}
