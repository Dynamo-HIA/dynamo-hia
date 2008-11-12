package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.DALYWeightsObject;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;

public class DALYWeightsFactory   implements IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker>{

	public DALYWeightsObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public DALYWeightsObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	public StandardObjectMarker constructAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public StandardObjectMarker manufacture(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
