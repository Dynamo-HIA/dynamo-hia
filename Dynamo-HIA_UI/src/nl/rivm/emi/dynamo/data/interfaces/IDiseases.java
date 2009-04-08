package nl.rivm.emi.dynamo.data.interfaces;

import java.util.ArrayList;
import java.util.Map;

import nl.rivm.emi.dynamo.data.TypedHashMap;

public interface IDiseases {

	public abstract Map<String,IDiseaseConfiguration> getDiseaseConfigurations();

	public abstract void setDiseaseConfigurations(Map<String, IDiseaseConfiguration> diseases);

}