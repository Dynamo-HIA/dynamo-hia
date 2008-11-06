package nl.rivm.emi.dynamo.data.types.functional;

public interface IScenarioConfiguration {
	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getMinAge();

	public abstract void setMinAge(Integer minAge);

	public abstract Integer getMaxAge();

	public abstract void setMaxAge(Integer maxAge);

	public abstract String getGender();

	public abstract void setGender(String gender);

	public abstract String getAltTransitionFileName();

	public abstract void setAltTransitionFileName(String altTransitionFileName);

	public abstract String getAltPrevalenceFileName();

	public abstract void setAltPrevalenceFileName(String altPrevalenceFileName);

}