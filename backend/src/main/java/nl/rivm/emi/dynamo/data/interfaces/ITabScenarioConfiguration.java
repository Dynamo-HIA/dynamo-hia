package nl.rivm.emi.dynamo.data.interfaces;

import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * Interface to implement for Objects that shuttle information between the
 * Simulation configuration screen and the DynamoSimulationObject.
 * 
 * @author mondeelr
 * 
 */
public interface ITabScenarioConfiguration {

	public String getName();

	public void setName(String name);

	@SuppressWarnings("rawtypes")
	public WritableValue getObservableName();

	public void setObservableName(@SuppressWarnings("rawtypes") WritableValue name);

	@SuppressWarnings("rawtypes")
	public WritableValue getObservableSuccessRate();

	public void setObservableSuccessRate(@SuppressWarnings("rawtypes") WritableValue successRate);

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


	public void setSuccessRate(Integer successRate);

}