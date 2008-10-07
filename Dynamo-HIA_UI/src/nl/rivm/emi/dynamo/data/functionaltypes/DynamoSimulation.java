package nl.rivm.emi.dynamo.data.functionaltypes;

import java.util.ArrayList;

import nl.rivm.emi.cdm.simulation.Simulation;

public class DynamoSimulation extends Simulation {

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
	ArrayList<DiseaseData> diseases;

	public class DiseaseData {
		String name;
		String prevalenceFile;
		String incidenceFile;
		String excessMortalityFile;
		String dalyWeightsFile;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPrevalenceFile() {
			return prevalenceFile;
		}
		public void setPrevalenceFile(String prevalenceFile) {
			this.prevalenceFile = prevalenceFile;
		}
		public String getIncidenceFile() {
			return incidenceFile;
		}
		public void setIncidenceFile(String incidenceFile) {
			this.incidenceFile = incidenceFile;
		}
		public String getExcessMortalityFile() {
			return excessMortalityFile;
		}
		public void setExcessMortalityFile(String excessMortalityFile) {
			this.excessMortalityFile = excessMortalityFile;
		}
		public String getDalyWeightsFile() {
			return dalyWeightsFile;
		}
		public void setDalyWeightsFile(String dalyWeightsFile) {
			this.dalyWeightsFile = dalyWeightsFile;
		}
	}

	RiskFactorData riskFactor;

	public class RiskFactorData {
		String name;
		String type;
		String prevalenceFile;
		String transitionFile;
		String relativeRiskForDeathFile;
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
		public String getPrevalenceFile() {
			return prevalenceFile;
		}
		public void setPrevalenceFile(String prevalenceFile) {
			this.prevalenceFile = prevalenceFile;
		}
		public String getTransitionFile() {
			return transitionFile;
		}
		public void setTransitionFile(String transitionFile) {
			this.transitionFile = transitionFile;
		}
		public String getRelativeRiskForDeathFile() {
			return relativeRiskForDeathFile;
		}
		public void setRelativeRiskForDeathFile(String relativeRiskForDeathFile) {
			this.relativeRiskForDeathFile = relativeRiskForDeathFile;
		}
	}

	ArrayList<RelativeRiskData> relativeRisks;

	public class RelativeRiskData {
		String from;
		String to;
		String dataFile;
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
		public String getDataFile() {
			return dataFile;
		}
		public void setDataFile(String dataFile) {
			this.dataFile = dataFile;
		}
	}

	ArrayList<ScenarioData> scenarios;

	public class ScenarioData {
		String name;
		String minAge;
		String maxAge;
		String gender;
		String altTransitionFile;
		String altPrevalenceFile;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMinAge() {
			return minAge;
		}
		public void setMinAge(String minAge) {
			this.minAge = minAge;
		}
		public String getMaxAge() {
			return maxAge;
		}
		public void setMaxAge(String maxAge) {
			this.maxAge = maxAge;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public String getAltTransitionFile() {
			return altTransitionFile;
		}
		public void setAltTransitionFile(String altTransitionFile) {
			this.altTransitionFile = altTransitionFile;
		}
		public String getAltPrevalenceFile() {
			return altPrevalenceFile;
		}
		public void setAltPrevalenceFile(String altPrevalenceFile) {
			this.altPrevalenceFile = altPrevalenceFile;
		}
	}

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

	public ArrayList<DiseaseData> getDiseases() {
		return diseases;
	}

	public void setDiseases(ArrayList<DiseaseData> diseases) {
		this.diseases = diseases;
	}

	public RiskFactorData getRiskFactor() {
		return riskFactor;
	}

	public void setRiskFactor(RiskFactorData riskFactor) {
		this.riskFactor = riskFactor;
	}

	public ArrayList<RelativeRiskData> getRelativeRisks() {
		return relativeRisks;
	}

	public void setRelativeRisks(ArrayList<RelativeRiskData> relativeRisks) {
		this.relativeRisks = relativeRisks;
	}

	public ArrayList<ScenarioData> getScenarios() {
		return scenarios;
	}

	public void setScenarios(ArrayList<ScenarioData> scenarios) {
		this.scenarios = scenarios;
	}
}
