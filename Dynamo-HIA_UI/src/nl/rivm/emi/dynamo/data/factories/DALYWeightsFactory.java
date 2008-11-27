package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.DALYWeightsObject;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.OverallDALYWeightsObject;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypesSingleton;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

public class DALYWeightsFactory extends AgnosticFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	public DALYWeightsObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		log.debug("Starting manufacture.");
		return (DALYWeightsObject) manufacture(configurationFile, true);
	}

	public DALYWeightsObject manufacture(
			File configurationFile) throws ConfigurationException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false);
		DALYWeightsObject result = new DALYWeightsObject(producedMap);
		return (result); 
	}
	public DALYWeightsObject manufactureDefault() throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("age"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("sex"), null));
		leafNodeList.add(new AtomicTypeObjectTuple(AtomicTypesSingleton
				.getInstance().get("percent"), null));
		return new DALYWeightsObject(super.manufactureDefault(leafNodeList, false));
	}
}
