package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.parts.RelativeRiskConfigurationData;

public interface IRelativeRisks {

	public abstract TypedHashMap<RelativeRiskConfigurationData> getRelativeRisks();

	public abstract void setRelativeRisks(
			TypedHashMap<RelativeRiskConfigurationData> relativeRisks);

}