package nl.rivm.emi.dynamo.data.interfaces;

import java.util.Map;

import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;

public interface IRiskFactor {

	public  Map<String, TabRiskFactorConfigurationData> getRiskFactorConfigurations();

	public abstract void setRiskFactorConfigurations( Map<String, TabRiskFactorConfigurationData> riskFactor);

}