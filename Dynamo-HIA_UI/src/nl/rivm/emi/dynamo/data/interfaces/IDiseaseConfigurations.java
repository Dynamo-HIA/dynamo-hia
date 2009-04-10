package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;

/**
 * Interface for use by the User Interface and the Storage Interface.
 * 
 * @author mondeelr
 * @deprecated
 */
public interface IDiseaseConfigurations {
	public abstract TabDiseaseConfigurationData getDiseaseConfigurationData(String name);

	public abstract TabDiseaseConfigurationData setDiseaseConfiguration(TabDiseaseConfigurationData theData);
}