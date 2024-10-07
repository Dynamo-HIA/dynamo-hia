package nl.rivm.emi.dynamo.data.interfaces;

import java.util.Map;

public interface IDiseases {

	public abstract Map<String,ITabDiseaseConfiguration> getDiseaseConfigurations();

	public abstract void setDiseaseConfigurations(Map<String, ITabDiseaseConfiguration> diseases);

}