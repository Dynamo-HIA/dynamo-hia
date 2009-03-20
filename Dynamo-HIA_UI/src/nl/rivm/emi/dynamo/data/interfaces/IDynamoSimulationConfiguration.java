package nl.rivm.emi.dynamo.data.interfaces;

public interface IDynamoSimulationConfiguration extends IHasNewborns,
		IDiseases, IMaxAge, IMinAge, INumberOfYears,
		PopulationFileName, ISimPopSize, IRandomSeed, IRelativeRisks,
		IStartingYear, ITimeStep, IResultType, IRiskFactor, IScenarios, IConfigurationCheck {

}