package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.interfaces.IScenarioConfiguration;

public class ScenarioConfigurationData implements IScenarioConfiguration {
	String name;
	Integer minAge;
	Integer maxAge;
	String gender;
	String altTransitionFileName;
	String altPrevalenceFileName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMinAge() {
		return minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAltTransitionFileName() {
		return altTransitionFileName;
	}

	public void setAltTransitionFileName(String altTransitionFileName) {
		this.altTransitionFileName = altTransitionFileName;
	}

	public String getAltPrevalenceFileName() {
		return altPrevalenceFileName;
	}

	public void setAltPrevalenceFileName(String altPrevalenceFileName) {
		this.altPrevalenceFileName = altPrevalenceFileName;
	}
}
