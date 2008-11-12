package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.OverallMortalityObject;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftObject;

public class TransitionDriftFactory  implements IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker>{

	public TransitionDriftObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public TransitionDriftObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
