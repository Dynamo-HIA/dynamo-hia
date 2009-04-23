package nl.rivm.emi.dynamo.data.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IConfigurationCheck;
import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
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
import nl.rivm.emi.dynamo.data.interfaces.ITabRelativeRisksConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabRiskFactorConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IStartingYear;
import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabStoreConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITimeStep;
import nl.rivm.emi.dynamo.data.interfaces.PopulationFileName;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabScenarioConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.DALYWeightsFileName;
import nl.rivm.emi.dynamo.data.types.atomic.ExessMortFileName;
import nl.rivm.emi.dynamo.data.types.atomic.IncFileName;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.RelativeRiskIndex;
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
		return (String)((WritableValue) getObservablePopulationFileName()).doGetValue();
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

	public Map<String, TabRiskFactorConfigurationData> getRiskFactorConfigurations() {
		TypedHashMap<UniqueName> riskFactorMap = (TypedHashMap<UniqueName>) get(XMLTagEntityEnum.RISKFACTORS
				.getElementName());
		Map<String, TabRiskFactorConfigurationData> theMap = new LinkedHashMap<String, TabRiskFactorConfigurationData>();
		Set<Object> namesSet = riskFactorMap.keySet();
		for (Object nameObject : namesSet) {
			ArrayList<AtomicTypeObjectTuple> list = (ArrayList<AtomicTypeObjectTuple>) riskFactorMap
					.get(nameObject);
			TabRiskFactorConfigurationData data = new TabRiskFactorConfigurationData();
			data.initialize(nameObject, list);
			theMap.put(data.getName(), data);
		}
		return theMap;
	}

	public void setRiskFactorConfigurations(
			Map<String, TabRiskFactorConfigurationData> riskFactorConfigurations) {
		TypedHashMap<? extends XMLTagEntity> riskFactorsMap = new TypedHashMap(XMLTagEntityEnum.UNIQUENAME.getTheType());
		Set<String> nameSet = riskFactorConfigurations.keySet();
		for (String name : nameSet) {
			TabRiskFactorConfigurationData riskFactorConfiguration = riskFactorConfigurations
					.get(name);
			riskFactorsMap = riskFactorConfiguration
					.putInTypedHashMap(riskFactorsMap);
		}
		put(XMLTagEntityEnum.RISKFACTORS.getElementName(), riskFactorsMap);
	}

	public Map<String, ITabDiseaseConfiguration> getDiseaseConfigurations() {
		TypedHashMap<UniqueName> diseasesMap = (TypedHashMap<UniqueName>) get(XMLTagEntityEnum.DISEASES
				.getElementName());
		Map<String, ITabDiseaseConfiguration> resultMap = new LinkedHashMap<String, ITabDiseaseConfiguration>();
		Set<Object> keySet;
		if (diseasesMap == null) {
			// No entries exists in the xml datafile, so we provide an empty (not null) map 
			diseasesMap = new TypedHashMap(XMLTagEntityEnum.UNIQUENAME.getTheType());			
			// Put the map into this dynamosimulationobject model
			put(XMLTagEntityEnum.DISEASES.getElementName(), diseasesMap);			
		}
		keySet = diseasesMap.keySet();
		for (Object key : keySet) {
			String name = (String) key;
			ArrayList<AtomicTypeObjectTuple> diseaseModelData = (ArrayList<AtomicTypeObjectTuple>) diseasesMap
					.get(key);
			TabDiseaseConfigurationData data = new TabDiseaseConfigurationData();
			data.initialize(name, diseaseModelData);
			resultMap.put(name, data);
		}
		return resultMap;
	}

	public void setDiseaseConfigurations(
			Map<String, ITabDiseaseConfiguration> diseaseConfigurations) {
		TypedHashMap<? extends XMLTagEntity> diseasesMap = new TypedHashMap(XMLTagEntityEnum.UNIQUENAME.getTheType());
		Set<String> nameSet = diseaseConfigurations.keySet();
		for (String name : nameSet) {
			TabDiseaseConfigurationData data = (TabDiseaseConfigurationData) diseaseConfigurations
					.get(name);
		diseasesMap = data.putInTypedHashMap(diseasesMap);
		}
		put(XMLTagEntityEnum.DISEASES.getElementName(), diseasesMap);
	}

	public Map<Integer, TabRelativeRiskConfigurationData> getRelativeRiskConfigurations() {
		TypedHashMap<RelativeRiskIndex> relativeRisksMap = (TypedHashMap<RelativeRiskIndex>) get(XMLTagEntityEnum.RRS
				.getElementName());
		Map<Integer, TabRelativeRiskConfigurationData> resultMap = new LinkedHashMap<Integer, TabRelativeRiskConfigurationData>();
		Set<Object> keySet;
		if (relativeRisksMap == null) {
			// No entries exists in the xml datafile, so we provide an empty (not null) map 
			relativeRisksMap = new TypedHashMap(XMLTagEntityEnum.UNIQUENAME.getTheType());			
			// Put the map into this dynamosimulationobject model
			put(XMLTagEntityEnum.RRS.getElementName(), relativeRisksMap);			
		}
		keySet = relativeRisksMap.keySet();
		for (Object key : keySet) {
			Integer index = (Integer) key;
			ArrayList<AtomicTypeObjectTuple> relativeRiskModelData = (ArrayList<AtomicTypeObjectTuple>) relativeRisksMap
					.get(key);
			TabRelativeRiskConfigurationData data = new TabRelativeRiskConfigurationData();
			data.initialize(index, relativeRiskModelData);
			resultMap.put(index, data);
		}		
		return resultMap;
	}

	public void setRelativeRiskConfigurations(
			Map<Integer, TabRelativeRiskConfigurationData> relativeRiskConfigurations) {
		TypedHashMap<? extends XMLTagEntity> relativeRisksMap = new TypedHashMap(XMLTagEntityEnum.RRINDEX.getTheType());
		Set<Integer> indexSet = relativeRiskConfigurations.keySet();
		for (Integer index : indexSet) {
			TabRelativeRiskConfigurationData data = (TabRelativeRiskConfigurationData) relativeRiskConfigurations
					.get(index);
		relativeRisksMap = data.putInTypedHashMap(relativeRisksMap);
		}
		put(XMLTagEntityEnum.RRS.getElementName(), relativeRisksMap);
	}
	
	public Map<String, ITabScenarioConfiguration> getScenarioConfigurations() {
		TypedHashMap<UniqueName> scenariosMap = (TypedHashMap<UniqueName>) get(XMLTagEntityEnum.SCENARIOS
				.getElementName());
		Map<String,ITabScenarioConfiguration> resultMap = new LinkedHashMap<String, ITabScenarioConfiguration>();
		Set<Object> keySet;
		if (scenariosMap == null) {
			// No entries exists in the xml datafile, so we provide an empty (not null) map 
			scenariosMap = new TypedHashMap(XMLTagEntityEnum.UNIQUENAME.getTheType());			
			// Put the map into this dynamosimulationobject model
			put(XMLTagEntityEnum.SCENARIOS.getElementName(), scenariosMap);			
		}
		keySet = scenariosMap.keySet();			
		for (Object key : keySet) {
			String name = (String) key;
			ArrayList<AtomicTypeObjectTuple> scenarioModelData = (ArrayList<AtomicTypeObjectTuple>) scenariosMap
					.get(key);
			ITabStoreConfiguration data = new TabScenarioConfigurationData();
			data.initialize(name, scenarioModelData);
			resultMap.put(name, (ITabScenarioConfiguration) data);
		}
		return resultMap;
	}

	public void setScenarioConfigurations(
			Map<String, ITabScenarioConfiguration> scenarioConfigurations) {
		TypedHashMap<? extends XMLTagEntity> scenariosMap = new TypedHashMap(XMLTagEntityEnum.UNIQUENAME.getTheType());
		Set<String> nameSet = scenarioConfigurations.keySet();
		for (String name : nameSet) {
			TabScenarioConfigurationData data = (TabScenarioConfigurationData) scenarioConfigurations
					.get(name);
		scenariosMap = data.putInTypedHashMap(scenariosMap);
		}
		put(XMLTagEntityEnum.SCENARIOS.getElementName(), scenariosMap);
	}

	public boolean isConfigurationOK() {
		// TODO Auto-generated method stub
		return false;
	
}
}