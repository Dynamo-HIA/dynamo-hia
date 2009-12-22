package nl.rivm.emi.dynamo.data.interfaces;


public interface ITabRiskFactorConfiguration {

	public String getName();

	public void setName(String name);

	public String getPrevalenceFileName();

	public void setPrevalenceFileName(String prevalenceFileName);

	public String getTransitionFileName();

	public void setTransitionFileName(String transitionFileName);
}