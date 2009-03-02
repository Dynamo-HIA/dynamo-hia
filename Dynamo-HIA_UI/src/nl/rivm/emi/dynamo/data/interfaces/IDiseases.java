package nl.rivm.emi.dynamo.data.interfaces;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;

public interface IDiseases {

	public abstract TypedHashMap<IDiseaseConfiguration> getDiseases();

	public abstract void setDiseases(TypedHashMap<IDiseaseConfiguration> diseases);

}