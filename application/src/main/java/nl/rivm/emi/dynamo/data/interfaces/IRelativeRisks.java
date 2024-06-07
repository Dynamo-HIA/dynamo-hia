package nl.rivm.emi.dynamo.data.interfaces;

import java.util.Map;

import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;

public interface IRelativeRisks {

	public abstract Map<Integer, TabRelativeRiskConfigurationData> getRelativeRiskConfigurations();

	public abstract void setRelativeRiskConfigurations(
			Map<Integer, TabRelativeRiskConfigurationData> relativeRisks);

}