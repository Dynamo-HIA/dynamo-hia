package nl.rivm.emi.dynamo.data.types.functional;

import java.util.ArrayList;

import nl.rivm.emi.cdm.simulation.Simulation;

public class DynamoSimulationConfiguration extends Simulation implements IDynamoSimulationConfiguration {

	boolean newborns;
	Integer startingYear;
	Integer numberOfYears;
	Integer populationSize;
	Integer minimumAge;
	Integer maximumAge;
	Integer timeStep;
	Float randomSeed;
	String resultType;
	String populationFileName;
	ArrayList<DiseaseConfigurationData> diseases;
	IRiskFactorConfiguration riskFactor;

	ArrayList<RelativeRiskConfigurationData> relativeRisks;

	ArrayList<ScenarioConfigurationData> scenarios;

	public boolean isNewborns() {
		return newborns;
	}
	public void setNewborns(boolean newborns) {
		this.newborns = newborns;
	}
	public Integer getStartingYear() {
		return startingYear;
	}
	public void setStartingYear(Integer startingYear) {
		this.startingYear = startingYear;
	}
	public Integer getNumberOfYears() {
		return numberOfYears;
	}
	public void setNumberOfYears(Integer numberOfYears) {
		this.numberOfYears = numberOfYears;
	}
	public Integer getPopulationSize() {
		return populationSize;
	}
	public void setPopulationSize(Integer populationSize) {
		this.populationSize = populationSize;
	}
	public Integer getMinimumAge() {
		return minimumAge;
	}
	public void setMinimumAge(Integer minimumAge) {
		this.minimumAge = minimumAge;
	}
	public Integer getMaximumAge() {
		return maximumAge;
	}
	public void setMaximumAge(Integer maximumAge) {
		this.maximumAge = maximumAge;
	}
	public Integer getTimeStep() {
		return timeStep;
	}
	public void setTimeStep(Integer timeStep) {
		this.timeStep = timeStep;
	}
	public Float getRandomSeed() {
		return randomSeed;
	}
	public void setRandomSeed(Float randomSeed) {
		this.randomSeed = randomSeed;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getPopulationFileName() {
		return populationFileName;
	}
	public void setPopulationFileName(String populationFileName) {
		this.populationFileName = populationFileName;
	}
	public ArrayList<DiseaseConfigurationData> getDiseases() {
		return diseases;
	}
	public void setDiseases(ArrayList<DiseaseConfigurationData> diseases) {
		this.diseases = diseases;
	}
	public IRiskFactorConfiguration getRiskFactor() {
		return riskFactor;
	}
	public void setRiskFactor(IRiskFactorConfiguration riskFactor) {
		this.riskFactor = riskFactor;
	}
	public ArrayList<RelativeRiskConfigurationData> getRelativeRisks() {
		return relativeRisks;
	}
	public void setRelativeRisks(ArrayList<RelativeRiskConfigurationData> relativeRisks) {
		this.relativeRisks = relativeRisks;
	}
	public ArrayList<ScenarioConfigurationData> getScenarios() {
		return scenarios;
	}
	public void setScenarios(ArrayList<ScenarioConfigurationData> scenarios) {
		this.scenarios = scenarios;
	}
	public class DiseaseConfigurationData implements IDiseaseConfiguration {
		String name;
		String prevalenceFileName;
		String incidenceFileName;
		String excessMortalityFileName;
		String dalyWeightsFileName;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPrevalenceFileName() {
			return prevalenceFileName;
		}
		public void setPrevalenceFileName(String prevalenceFileName) {
			this.prevalenceFileName = prevalenceFileName;
		}
		public String getIncidenceFileName() {
			return incidenceFileName;
		}
		public void setIncidenceFileName(String incidenceFileName) {
			this.incidenceFileName = incidenceFileName;
		}
		public String getExcessMortalityFileName() {
			return excessMortalityFileName;
		}
		public void setExcessMortalityFileName(String excessMortalityFileName) {
			this.excessMortalityFileName = excessMortalityFileName;
		}
		public String getDalyWeightsFileName() {
			return dalyWeightsFileName;
		}
		public void setDalyWeightsFileName(String dalyWeightsFileName) {
			this.dalyWeightsFileName = dalyWeightsFileName;
		}
	}
	public class RiskFactorConfigurationData implements IRiskFactorConfiguration  {

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

	public class RelativeRiskConfigurationData implements IRelativeRiskConfiguration {
		String from; // TODO
		String to;  // TODO
		String dataFileName;
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getTo() {
			return to;
		}
		public void setTo(String to) {
			this.to = to;
		}
		public String getDataFileName() {
			return dataFileName;
		}
		public void setDataFileName(String dataFileName) {
			this.dataFileName = dataFileName;
		}
	}

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

}
