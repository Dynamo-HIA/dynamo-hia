package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.data.factories.base.IObjectFromXMLFactory;
import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.data.objects.OverallMortalityObject;

public class ExcessMortalityFactory  implements IObjectFromXMLFactory<ExcessMortalityObject>{

	public ExcessMortalityObject constructAllZeroesModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExcessMortalityObject manufactureFromFlatXML(File configurationFile)
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
