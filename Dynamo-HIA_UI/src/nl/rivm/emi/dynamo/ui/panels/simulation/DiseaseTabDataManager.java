package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.IDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.ChoosableDiseases;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiseaseTabDataManager implements DynamoTabDataManager {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject dynamoSimulationObject;
	private Map<String, IDiseaseConfiguration> configurations;
	private IDiseaseConfiguration singleConfiguration;
	private Set<String> initialSelection;
	private String tabName;
	
	public DiseaseTabDataManager(String tabName, BaseNode selectedNode, 
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> initialSelection
			) throws ConfigurationException {
		this.tabName = tabName;
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.configurations = this.dynamoSimulationObject.getDiseaseConfigurations();
		this.initialSelection = initialSelection;
		this.singleConfiguration = this.configurations.get(getInitialName());		
	}
	
	@Override
	public DropDownPropertiesSet getDropDownSet(String name, String chosenDiseaseName) throws ConfigurationException {
		log.debug("HIERALOOK");
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		set.addAll(this.getContents(name, chosenDiseaseName));
		return set;	
	}

	public Set<String> getContents(String name, String chosenDiseaseName) throws ConfigurationException {
		Set<String> contents = new LinkedHashSet<String>();
		ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
		if (chosenDiseaseName == null) {
			chosenDiseaseName = 
				(String) choosableDiseases.getFirstDiseaseOfSet(chosenDiseaseName, treeLists);
		}
		//choosableDiseases.setChosenDisease(chosenDiseaseName);
		log.debug("HIERO");		
		if (DiseaseSelectionGroup.DISEASE.equals(name)) {
			contents = this.treeLists.getValidDiseases();
			//contents = choosableDiseases.getChoosableDiseases(chosenDiseaseName, treeLists);
		} else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(name)) {
			contents = this.treeLists.getDiseasePrevalences(chosenDiseaseName);
			log.debug("contents1" + contents);
		} else if (DiseaseResultGroup.INCIDENCE.equals(name)) {
			contents = this.treeLists.getDiseaseIncidences(chosenDiseaseName);
			log.debug("contents2" + contents);
		} else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(name)) {
			contents = this.treeLists.getDiseaseExcessMortalities(chosenDiseaseName);
			log.debug("contents3" + contents);
		} else if (DiseaseResultGroup.DALY_WEIGHTS.equals(name)) {
			contents = this.treeLists.getDALYWeights(chosenDiseaseName);
			log.debug("contents4" + contents);
		}
		log.debug("contentsLast" + contents);
		return contents; 		
	}

	@Override
	public String getCurrentValue(String name) {
		//IDiseaseConfiguration singleConfiguration =
			//configuration.get(getInitialName());
		String value = null;
		if (this.initialSelection != null) {
			if (DiseaseSelectionGroup.DISEASE.equals(name)) {
				value = singleConfiguration.getName();
			} else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(name)) {
				value = singleConfiguration.getPrevalenceFileName();
				log.debug("value" + value);
			} else if (DiseaseResultGroup.INCIDENCE.equals(name)) {
				value = singleConfiguration.getIncidenceFileName();
				log.debug("value" + value);
			} else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(name)) {
				value = singleConfiguration.getExcessMortalityFileName();
				log.debug("value" + value);
			} else if (DiseaseResultGroup.DALY_WEIGHTS.equals(name)) {
				value = singleConfiguration.getDalyWeightsFileName();
				log.debug("value" + value);
			}
		}
		return value;
	}
	
	
	/**
	 * 
	 * Updates the object model every time a selection is made
	 * 
	 * @param dropDownName
	 * @param selectedValue
	 * @throws ConfigurationException 
	 */
	public void updateObjectState(String name, String selectedValue) throws ConfigurationException {
		log.debug(name + ": " + selectedValue);
		
		if (this.initialSelection == null) {
			log.debug("CREATING NEW TAB");
			createInDynamoSimulationObject();
			ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
			String  chosenDiseaseName = 
					(String) choosableDiseases.getFirstDiseaseOfSet(null, treeLists);
			singleConfiguration.setName(chosenDiseaseName);
			//singleConfiguration.setName("THISDISEASE");
		}		
		
		//if (this.initialSelection != null) {
			if (DiseaseSelectionGroup.DISEASE.equals(name)) {
				singleConfiguration.setName(selectedValue);
			} else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(name)) {
				singleConfiguration.setPrevalenceFileName(selectedValue);				
			} else if (DiseaseResultGroup.INCIDENCE.equals(name)) {
				singleConfiguration.setIncidenceFileName(selectedValue);				
			} else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(name)) {
				singleConfiguration.setExcessMortalityFileName(selectedValue);				
			} else if (DiseaseResultGroup.DALY_WEIGHTS.equals(name)) {
				singleConfiguration.setDalyWeightsFileName(selectedValue);
			}
		//}		
		updateDynamoSimulationObject();
	}
	
	private void updateDynamoSimulationObject() {
		log.debug("UPDATING");
		log.debug("singleConfiguration" + singleConfiguration);
		log.debug("singleConfiguration.getName()" + singleConfiguration.getName());
		
		this.configurations.put(singleConfiguration.getName(), 
				singleConfiguration);
		this.dynamoSimulationObject.setDiseaseConfigurations(configurations);
				
		
		/**
		 * TODO REMOVE: LOGGING BELOW
		 */
		Map map = this.dynamoSimulationObject.getDiseaseConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			IDiseaseConfiguration conf = (IDiseaseConfiguration) map.get(key);
			log.debug("conf.getPrevalenceFileName()" + conf.getPrevalenceFileName());
			log.debug("conf.getIncidenceFileName()" + conf.getIncidenceFileName());
			log.debug("conf.getExcessMortalityFileName()" + conf.getExcessMortalityFileName());
			log.debug("conf.getDalyWeightsFileName()" + conf.getDalyWeightsFileName());
		}
		log.debug("configurations.size()" + configurations.size());
		/**
		 * TODO REMOVE: LOGGING ABOVE
		 */
		
		
	}

	/**
	 * HIGHER LEVEL: TABMANAGER? removeByTabName
*/
	public void createInDynamoSimulationObject() {
		this.singleConfiguration = new TabDiseaseConfigurationData();
	}
	
	public void removeFromDynamoSimulationObject(String xxx) {		
		//this.configurations.remove(xxx);
		this.dynamoSimulationObject.setDiseaseConfigurations(configurations);		
	}
	
	private String getInitialName() {
		String chosenDiseaseName = null;
		if (this.initialSelection != null) {
			for (String chosenName : this.initialSelection) {
				chosenDiseaseName = chosenName;		
			}			
		}
		return chosenDiseaseName; 
	}
	
	
}
