package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.dynamo.data.objects.parts.DiseaseConfigurationData;

/**
 * Interface for use by the User Interface and the Storage Interface.
 * 
 * @author mondeelr
 * 
 */
public interface IDiseaseConfigurations {
	public abstract DiseaseConfigurationData getDiseaseConfigurationData(String name);

	public abstract DiseaseConfigurationData setDiseaseConfiguration(DiseaseConfigurationData theData);
}