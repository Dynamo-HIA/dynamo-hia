package nl.rivm.emi.dynamo.data.interfaces;

public interface IRiskFactor {

	public abstract ISimulationRiskFactorConfiguration getRiskFactor();

	public abstract void setRiskFactor(ISimulationRiskFactorConfiguration riskFactor);

}