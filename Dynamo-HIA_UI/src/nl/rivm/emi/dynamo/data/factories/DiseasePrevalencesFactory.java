package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.DiseasePrevalencesObject;
import nl.rivm.emi.dynamo.data.objects.ObservableObjectMarker;
import nl.rivm.emi.dynamo.data.objects.StandardObjectMarker;

import org.apache.commons.configuration.ConfigurationException;

public class DiseasePrevalencesFactory  implements IObjectFromXMLFactory<StandardObjectMarker, ObservableObjectMarker>{

	public DiseasePrevalencesObject constructObservableAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public DiseasePrevalencesObject manufactureObservable(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
