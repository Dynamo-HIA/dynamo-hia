package nl.rivm.emi.dynamo.data.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
import nl.rivm.emi.dynamo.data.interfaces.IScenarios;
import nl.rivm.emi.dynamo.data.interfaces.ISimPopSize;
import nl.rivm.emi.dynamo.data.interfaces.ISimulationRiskFactorConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IStartingYear;
import nl.rivm.emi.dynamo.data.interfaces.ITimeStep;
import nl.rivm.emi.dynamo.data.interfaces.PopulationFileName;
import nl.rivm.emi.dynamo.data.objects.parts.DiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.parts.RelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.parts.ScenarioConfigurationData;
import nl.rivm.emi.dynamo.data.objects.parts.SimulationRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.DALYWeightsFileName;
import nl.rivm.emi.dynamo.data.types.atomic.ExessMortFileName;
import nl.rivm.emi.dynamo.data.types.atomic.IncFileName;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class DynamoSimulationObject extends
		GroupConfigurationObjectServiceLayer implements IHasNewborns, IMaxAge,
		IMinAge, INumberOfYears, PopulationFileName, ISimPopSize, IRandomSeed,
		IStartingYear, ITimeStep, IResultType, IRiskFactor, IDiseases,
		IRelativeRisks, IScenarios, IConfigurationCheck {

	Log log = LogFactory.getLog(this.getClass().getName());

	public DynamoSimulationObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
	}

	public boolean isHasNewborns() {
		return getSingleRootChildBooleanValue(XMLTagEntityEnum.HASNEWBORNS
				.getElementName());
	}

	public WritableValue getObservableHasNewborns() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.HASNEWBORNS
				.getElementName());
		return result;
	}

	public void setHasNewborns(boolean newborns) {
		putSingleRootChildBooleanValue(XMLTagEntityEnum.HASNEWBORNS
				.getElementName(), newborns);
	}

	public Integer getMaxAge() {
		return getSingleRootChildIntegerValue(XMLTagEntityEnum.MAXAGE
				.getElementName());
	}

	public WritableValue getObservableMaxAge() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.MAXAGE
				.getElementName());
		return result;
	}

	public void setMaxAge(Integer maxAge) {
		// TODO Auto-generated method stub

	}

	public Integer getMinAge() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableMinAge() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.MINAGE
				.getElementName());
		return result;
	}

	public void setMinAge(Integer minAge) {
		// TODO Auto-generated method stub

	}

	public Integer getNumberOfYears() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableNumberOfYears() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.NUMBEROFYEARS
				.getElementName());
		return result;
	}

	public void setNumberOfYears(Integer numberOfYears) {
		// TODO Auto-generated method stub

	}

	public String getPopulationFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservablePopulationFileName() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.POPFILENAME
				.getElementName());
		return result;
	}

	public void setPopulationFileName(String populationFileName) {
		// TODO Auto-generated method stub

	}

	public Integer getSimPopSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableSimPopSize() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.SIMPOPSIZE
				.getElementName());
		return result;
	}

	public void setSimPopSize(Integer simPopSize) {
		// TODO Auto-generated method stub

	}

	public Float getRandomSeed() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableRandomSeed() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.RANDOMSEED
				.getElementName());
		return result;
	}

	public void setRandomSeed(Float randomSeed) {
		// TODO Auto-generated method stub

	}

	public Integer getStartingYear() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableStartingYear() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.STARTINGYEAR
				.getElementName());
		return result;
	}

	public void setStartingYear(Integer startingYear) {
		// TODO Auto-generated method stub

	}

	public Float getTimeStep() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableTimeStep() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.TIMESTEP
				.getElementName());
		return result;
	}

	public void setTimeStep(Float timeStep) {
		// TODO Auto-generated method stub

	}

	public String getResultType() {
		// TODO Auto-generated method stub
		return null;
	}

	public WritableValue getObservableResultType() {
		WritableValue result = getSingleRootChildWritableValue(XMLTagEntityEnum.RESULTTYPE
				.getElementName());
		return result;
	}

	public void setResultType(String resultType) {
		// TODO Auto-generated method stub

	}

	public ISimulationRiskFactorConfiguration getRiskFactor() {
		TypedHashMap<UniqueName> riskFactorMap = (TypedHashMap<UniqueName>) get(XMLTagEntityEnum.RISKFACTORS
				.getElementName());
		SimulationRiskFactorConfigurationData data = new SimulationRiskFactorConfigurationData(
				riskFactorMap);
		return data;
	}

	public void setRiskFactor(ISimulationRiskFactorConfiguration riskFactor) {
		TypedHashMap<UniqueName> riskFactorMap = riskFactor
				.createTypedHashMap();
		put(XMLTagEntityEnum.RISKFACTORS.getElementName(), riskFactorMap);
	}

	public Map<String, IDiseaseConfiguration> getDiseaseConfigurations() {
		TypedHashMap<UniqueName> diseasesMap = (TypedHashMap<UniqueName>) get(XMLTagEntityEnum.DISEASES
				.getElementName());
		Map<String, IDiseaseConfiguration> resultMap = new HashMap<String, IDiseaseConfiguration>();
		Set<Object> keySet = diseasesMap.keySet();
		for (Object key : keySet) {
			String name = (String) key;
			DiseaseConfigurationData data = new DiseaseConfigurationData();
			data.setName(name);
			ArrayList<AtomicTypeObjectTuple> diseaseModelData = (ArrayList<AtomicTypeObjectTuple>) diseasesMap
					.get(key);
			for (AtomicTypeObjectTuple tuple : diseaseModelData) {
				XMLTagEntity type = tuple.getType();
				if (type instanceof PrevFileName) {
					data.setPrevalenceFileName( (String) ((WritableValue) 
							tuple.getValue()).getValue());
				} else {
					if (type instanceof IncFileName) {
						data.setIncidenceFileName( (String) ((WritableValue) 
								tuple.getValue()).getValue());
					} else {
						if (type instanceof ExessMortFileName) {
							data.setExcessMortalityFileName( (String) ((WritableValue) 
									tuple.getValue()).getValue());
						} else {
							if (type instanceof DALYWeightsFileName) {
								data.setDalyWeightsFileName( (String) ((WritableValue) 
										tuple.getValue()).getValue());
							} else {
								log.fatal("Unexpected type \""
										+ type.getXMLElementName()
										+ "\" in getDiseasesConfigurations()");
							}
						}
					}
				}
			}
			resultMap.put(name, data);
		}
		return resultMap;
	}

	public void setDiseaseConfigurations(
			Map<String, IDiseaseConfiguration> diseaseConfigurations) {
		TypedHashMap<UniqueName> diseasesMap = new TypedHashMap<UniqueName>();
		Set<String> nameSet = diseaseConfigurations.keySet();
		for (String name : nameSet) {
			DiseaseConfigurationData data = (DiseaseConfigurationData) diseaseConfigurations
					.get(name);
			ArrayList<AtomicTypeObjectTuple> diseaseModelData = new ArrayList<AtomicTypeObjectTuple>();
			AtomicTypeObjectTuple tuple = new AtomicTypeObjectTuple(
					XMLTagEntityEnum.PREVFILENAME.getTheType(),
					new WritableValue(data.getPrevalenceFileName(),
							String.class));
			diseaseModelData.add(tuple);
			tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.INCFILENAME
					.getTheType(), new WritableValue(data
					.getIncidenceFileName(), String.class));
			diseaseModelData.add(tuple);
			tuple = new AtomicTypeObjectTuple(
					XMLTagEntityEnum.EXESSMORTFILENAME.getTheType(),
					new WritableValue(data.getExcessMortalityFileName(),
							String.class));
			diseaseModelData.add(tuple);
			tuple = new AtomicTypeObjectTuple(
					XMLTagEntityEnum.DALYWEIGHTSFILENAME.getTheType(),
					new WritableValue(data.getDalyWeightsFileName(),
							String.class));
			diseaseModelData.add(tuple);
			diseasesMap.put(name, diseaseModelData);
		}
		put(XMLTagEntityEnum.DISEASES.getElementName(), diseasesMap);
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
