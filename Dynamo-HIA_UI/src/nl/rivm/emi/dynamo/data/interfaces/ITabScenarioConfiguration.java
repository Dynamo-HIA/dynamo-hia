package nl.rivm.emi.dynamo.data.interfaces;

public interface ITabScenarioConfiguration {
	public String getName();

	public void setName(String name);

	public Integer getSuccessRate();

	public void setSuccessRate(Integer successRate);
	
	public Integer getMinAge();

	public void setMinAge(Integer minAge);

	public Integer getMaxAge();

	public void setMaxAge(Integer maxAge);

	public Integer getTargetSex();

	public void setTargetSex(Integer targetSex);

	public abstract String getAltTransitionFileName();

	public abstract void setAltTransitionFileName(String altTransitionFileName);

	public abstract String getAltPrevalenceFileName();

	public abstract void setAltPrevalenceFileName(String altPrevalenceFileName);

}