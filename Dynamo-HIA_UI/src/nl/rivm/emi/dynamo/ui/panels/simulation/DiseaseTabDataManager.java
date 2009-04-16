package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.ChoosableDiseases;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * 
 * Handles the data actions of the disease tabs
 * 
 * @author schutb
 *
 */
public class DiseaseTabDataManager implements DynamoTabDataManager {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject dynamoSimulationObject;
	private Map<String, ITabDiseaseConfiguration> configurations;
	private ITabDiseaseConfiguration singleConfiguration;
	private Set<String> initialSelection;

	/**
	 * 
	 * Constructor
	 * 
	 * @param selectedNode
	 * @param dynamoSimulationObject
	 * @param initialSelection
	 * @throws ConfigurationException
	 */
	public DiseaseTabDataManager(BaseNode selectedNode, 
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> initialSelection
			) throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.configurations = this.dynamoSimulationObject.getDiseaseConfigurations();
		this.initialSelection = initialSelection;
		this.singleConfiguration = this.configurations.get(getInitialName());
	}
	
	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getDropDownSet(java.lang.String, java.lang.String)
	 */
	@Override
	public DropDownPropertiesSet getDropDownSet(String name, String chosenDiseaseName) throws ConfigurationException {
		log.debug("HIERALOOK");

		// The model object already exists, get the name
		if (singleConfiguration != null && chosenDiseaseName == null) {
			chosenDiseaseName = this.singleConfiguration.getName();
			log.debug("chosenDiseaseName JUST CREATED" + chosenDiseaseName);
			setDefaultValue(DiseaseSelectionGroup.DISEASE, chosenDiseaseName);
		} 
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		set.addAll(this.getContents(name, chosenDiseaseName));
		return set;	
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getContents(java.lang.String, java.lang.String)
	 */
	public Set<String> getContents(String name, String chosenDiseaseName) throws ConfigurationException {
		log.debug("GET CONTENTS");
		Set<String> contents = new LinkedHashSet<String>();
		ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
		// The name is still empty
		if (chosenDiseaseName == null) {
			chosenDiseaseName = 
				(String) choosableDiseases.getFirstDiseaseOfSet(chosenDiseaseName, treeLists);
			setDefaultValue(DiseaseSelectionGroup.DISEASE, chosenDiseaseName);
		}
		log.debug("HIERO chosenDiseaseName DATAMANAGER: " + chosenDiseaseName);		
		if (DiseaseSelectionGroup.DISEASE.equals(name)) {
			//contents = this.treeLists.getValidDiseases();
			contents = choosableDiseases.getChoosableDiseases(chosenDiseaseName, treeLists);
			log.debug("getContents NAME: " + contents);
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

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getCurrentValue(java.lang.String)
	 */
	@Override
	public String getCurrentValue(String name) throws ConfigurationException {
		log.debug("GET CURRENT VALUE");
		log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		if (this.singleConfiguration != null) {
			if (DiseaseSelectionGroup.DISEASE.equals(name)) {
				value = singleConfiguration.getName();
				log.debug("VALUE: " + value);
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
		
		log.debug("UPDATING OBJECT STATE");
		// In case a new Tab is created, no model exists yet
		if (this.initialSelection == null && singleConfiguration == null) {	
			log.debug("CREATING NEW TAB");
			createInDynamoSimulationObject();
			ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
			String  chosenDiseaseName = 
					(String) choosableDiseases.getFirstDiseaseOfSet(null, treeLists);
			selectedValue = chosenDiseaseName;
		}		
		
		if (DiseaseSelectionGroup.DISEASE.equals(name)) {
			singleConfiguration.setName(selectedValue);
			setDefaultValue(DiseaseSelectionGroup.DISEASE, selectedValue);
		} else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(name)) {
			singleConfiguration.setPrevalenceFileName(selectedValue);				
		} else if (DiseaseResultGroup.INCIDENCE.equals(name)) {
			singleConfiguration.setIncidenceFileName(selectedValue);				
		} else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(name)) {
			singleConfiguration.setExcessMortalityFileName(selectedValue);				
		} else if (DiseaseResultGroup.DALY_WEIGHTS.equals(name)) {
			singleConfiguration.setDalyWeightsFileName(selectedValue);
		}		
		updateDynamoSimulationObject();
	}
	
	public void updateDynamoSimulationObject() {
		log.error("UPDATING");
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
			ITabDiseaseConfiguration conf = (ITabDiseaseConfiguration) map.get(key);
			log.error("conf.getName()" + conf.getName());
			log.error("conf.getPrevalenceFileName()" + conf.getPrevalenceFileName());
			log.error("conf.getIncidenceFileName()" + conf.getIncidenceFileName());
			log.error("conf.getExcessMortalityFileName()" + conf.getExcessMortalityFileName());
			log.error("conf.getDalyWeightsFileName()" + conf.getDalyWeightsFileName());
		}
		log.debug("configurations.size()" + configurations.size());
		/**
		 * TODO REMOVE: LOGGING ABOVE
		 */
	}

	private void createInDynamoSimulationObject() {
		this.singleConfiguration = new TabDiseaseConfigurationData();
	}
	
	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#removeFromDynamoSimulationObject()
	 */
	public void removeFromDynamoSimulationObject() throws ConfigurationException {
		log.error("REMOVING OBJECT STATE");
		ChoosableDiseases.getInstance().removeChosenDisease(this.singleConfiguration.getName());
		this.configurations.remove(this.singleConfiguration.getName());
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

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getRefreshedDropDownSet(java.lang.String)
	 */
	public DropDownPropertiesSet getRefreshedDropDownSet(String label) throws ConfigurationException {
		return getDropDownSet(label, null);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#setDefaultValue(java.lang.String, java.lang.String)
	 */
	@Override
	public void setDefaultValue(String name, String selectedValue) throws ConfigurationException {
		log.debug("SETDEFAULT: " + selectedValue);
		if (DiseaseSelectionGroup.DISEASE.equals(name)) {
			ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
			choosableDiseases.setChosenDisease(selectedValue);
		}
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#removeOldDefaultValue(java.lang.String)
	 */
	@Override
	public void removeOldDefaultValue(String name) throws ConfigurationException {
		if (this.singleConfiguration != null) {
			log.debug("OLDDEFAULT: " + this.singleConfiguration.getName());
			if (DiseaseSelectionGroup.DISEASE.equals(name)) {
				ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
				choosableDiseases.removeChosenDisease(this.singleConfiguration.getName());
			}			
		}
	}

	@Override
	public WritableValue getCurrentWritableValue(String successRate) {
		// Will not be used
		return null;
	}
	
	
}
