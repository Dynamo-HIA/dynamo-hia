package nl.rivm.emi.dynamo.datahandling;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject.DiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject.RelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject.ScenarioConfigurationData;

public interface IDynamoSimulationConfiguration {

	public abstract boolean isNewborns();

	public abstract void setNewborns(boolean newborns);

	public abstract Integer getStartingYear();

	public abstract void setStartingYear(Integer startingYear);

	public abstract Integer getNumberOfYears();

	public abstract void setNumberOfYears(Integer numberOfYears);

	public abstract Integer getPopulationSize();

	public abstract void setPopulationSize(Integer populationSize);

	public abstract Integer getMinimumAge();

	public abstract void setMinimumAge(Integer minimumAge);

	public abstract Integer getMaximumAge();

	public abstract void setMaximumAge(Integer maximumAge);

	public abstract Integer getTimeStep();

	public abstract void setTimeStep(Integer timeStep);

	public abstract Float getRandomSeed();

	public abstract void setRandomSeed(Float randomSeed);

	public abstract String getResultType();

	public abstract void setResultType(String resultType);

	public abstract String getPopulationFileName();

	public abstract void setPopulationFileName(String populationFileName);

	public abstract ArrayList<DiseaseConfigurationData> getDiseases();

	public abstract void setDiseases(
			ArrayList<DiseaseConfigurationData> diseases);

	public abstract IRiskFactorConfiguration getRiskFactor();

	public abstract void setRiskFactor(IRiskFactorConfiguration riskFactor);

	public abstract ArrayList<RelativeRiskConfigurationData> getRelativeRisks();

	public abstract void setRelativeRisks(
			ArrayList<RelativeRiskConfigurationData> relativeRisks);

	public abstract ArrayList<ScenarioConfigurationData> getScenarios();

	public abstract void setScenarios(
			ArrayList<ScenarioConfigurationData> scenarios);

}