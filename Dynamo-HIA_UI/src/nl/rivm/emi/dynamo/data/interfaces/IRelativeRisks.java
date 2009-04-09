package nl.rivm.emi.dynamo.data.interfaces;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;

public interface IRelativeRisks {

	public abstract TypedHashMap<TabRelativeRiskConfigurationData> getRelativeRisks();

	public abstract void setRelativeRisks(
			TypedHashMap<TabRelativeRiskConfigurationData> relativeRisks);

}