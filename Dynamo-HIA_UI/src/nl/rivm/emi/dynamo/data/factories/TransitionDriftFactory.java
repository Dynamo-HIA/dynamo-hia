package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.OverallMortalityObject;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftObject;

public class TransitionDriftFactory  implements IObjectFromXMLFactory<TransitionDriftObject>{

	public TransitionDriftObject constructAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public TransitionDriftObject manufactureFromFlatXML(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
