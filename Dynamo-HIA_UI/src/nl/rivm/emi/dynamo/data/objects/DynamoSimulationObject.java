package nl.rivm.emi.dynamo.data.objects;

import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IConfigurationCheck;
import nl.rivm.emi.dynamo.data.interfaces.IDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IDiseases;
import nl.rivm.emi.dynamo.data.interfaces.IHasNewborns;
import nl.rivm.emi.dynamo.data.interfaces.IMaxAge;
import nl.rivm.emi.dynamo.data.interfaces.IMinAge;
import nl.rivm.emi.dynamo.data.interfaces.INumberOfYears;
import nl.rivm.emi.dynamo.data.interfaces.IRandomSeed;
import nl.rivm.emi.dynamo.data.interfaces.IRelativeRisks;
import nl.rivm.emi.dynamo.data.interfaces.IResultType;
import nl.rivm.emi.dynamo.data.interfaces.IRiskFactor;
import nl.rivm.emi.dynamo.data.interfaces.IRiskFactorConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IScenarios;
import nl.rivm.emi.dynamo.data.interfaces.ISimPopSize;
import nl.rivm.emi.dynamo.data.interfaces.IStartingYear;
import nl.rivm.emi.dynamo.data.interfaces.ITimeStep;
import nl.rivm.emi.dynamo.data.interfaces.PopulationFileName;
import nl.rivm.emi.dynamo.data.objects.parts.RelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.parts.ScenarioConfigurationData;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;

import org.eclipse.core.databinding.observable.value.WritableValue;

public class DynamoSimulationObject extends
		GroupConfigurationObjectServiceLayer implements IHasNewborns,
		IMaxAge, IMinAge, INumberOfYears, PopulationFileName,
		ISimPopSize, IRandomSeed,  IStartingYear, ITimeStep,
		IResultType, IRiskFactor, IDiseases, IRelativeRisks, IScenarios, IConfigurationCheck {

	public DynamoSimulationObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
	}

	public boolean isHasNewborns() {
				return getSingleRootChildBooleanValue(XMLTagEntityEnum.HASNEWBORNS.getElementName());
	}

	public WritableValue getObservableHasNewborns() {
		return getSingleRootChildWritableValue(XMLTagEntityEnum.HASNEWBORNS.getElementName());
}

	public void setHasNewborns(boolean newborns) {
putSingleRootChildBooleanValue(XMLTagEntityEnum.HASNEWBORNS.getElementName(), newborns);
	}

	public Integer getMaxAge() {
		return getSingleRootChildIntegerValue(XMLTagEntityEnum.MAXAGE.getElementName());
	}

	public WritableValue getObservableMaxAge() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMaxAge(Integer maxAge) {
		// TODO Auto-generated method stub

	}

	public Integer getMinAge() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableMinAge() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setMinAge(Integer minAge) {
		// TODO Auto-generated method stub

	}

	public Integer getNumberOfYears() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableNumberOfYears() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setNumberOfYears(Integer numberOfYears) {
		// TODO Auto-generated method stub

	}

	public String getPopulationFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservablePopulationFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPopulationFileName(String populationFileName) {
		// TODO Auto-generated method stub

	}

	public Integer getSimPopSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableSimPopSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSimPopSize(Integer simPopSize) {
		// TODO Auto-generated method stub

	}

	public Float getRandomSeed() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableRandomSeed() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRandomSeed(Float randomSeed) {
		// TODO Auto-generated method stub

	}

	public Integer getStartingYear() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableStartingYear() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStartingYear(Integer startingYear) {
		// TODO Auto-generated method stub

	}

	public Float getTimeStep() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableTimeStep() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTimeStep(Float timeStep) {
		// TODO Auto-generated method stub

	}

	public String getResultType() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableResultType() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResultType(String resultType) {
		// TODO Auto-generated method stub

	}

	public IRiskFactorConfiguration getRiskFactor() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRiskFactor(IRiskFactorConfiguration riskFactor) {
		// TODO Auto-generated method stub

	}

	public TypedHashMap<IDiseaseConfiguration> getDiseases() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDiseases(TypedHashMap<IDiseaseConfiguration> diseases) {
		// TODO Auto-generated method stub

	}

	public TypedHashMap<RelativeRiskConfigurationData> getRelativeRisks() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRelativeRisks(
			TypedHashMap<RelativeRiskConfigurationData> relativeRisks) {
		// TODO Auto-generated method stub

	}

	public TypedHashMap<ScenarioConfigurationData> getScenarios() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setScenarios(TypedHashMap<ScenarioConfigurationData> scenarios) {
		// TODO Auto-generated method stub

	}

	public boolean isConfigurationOK() {
		// TODO Auto-generated method stub
		return false;
	}
}
