package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.interfaces.IRiskFactorConfiguration;

public class RiskFactorConfigurationData implements IRiskFactorConfiguration {

	String name;
	String type;
	String prevalenceFileName;
	String transitionFileName;
	String relativeRiskForDeathFileName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
