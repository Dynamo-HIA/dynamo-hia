package nl.rivm.emi.dynamo.data.interfaces;

public interface IRiskFactor {

	public abstract IRiskFactorConfiguration getRiskFactor();

	public abstract void setRiskFactor(IRiskFactorConfiguration riskFactor);

}