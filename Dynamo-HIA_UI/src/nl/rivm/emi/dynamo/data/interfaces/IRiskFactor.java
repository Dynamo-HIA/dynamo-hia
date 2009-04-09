package nl.rivm.emi.dynamo.data.interfaces;

import java.util.HashMap;

import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;

public interface IRiskFactor {

	public  HashMap<String, TabRiskFactorConfigurationData> getRiskFactorConfigurations();

	public abstract void setRiskFactorConfigurations( HashMap<String, TabRiskFactorConfigurationData> riskFactor);

}