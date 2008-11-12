package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.SimulationObject;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;

public class SimulationFactory implements IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker>{

	public SimulationObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public SimulationObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
