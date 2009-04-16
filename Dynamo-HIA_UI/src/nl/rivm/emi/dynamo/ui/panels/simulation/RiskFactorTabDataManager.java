package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabRiskFactorConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.ChoosableDiseases;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorTabDataManager implements DynamoTabDataManager {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject dynamoSimulationObject;
	private Map<String, TabRiskFactorConfigurationData> configurations;
	private TabRiskFactorConfigurationData singleConfiguration;
	private Set<String> initialSelection;
	
	public RiskFactorTabDataManager(BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> initialSelection) throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.configurations = dynamoSimulationObject.getRiskFactorConfigurations();
		this.initialSelection = initialSelection;
		this.singleConfiguration = this.configurations.get(getInitialName());
	}

	@Override
	public Set<String> getContents(String name, String chosenRiskFactorName)
			throws ConfigurationException {
		log.debug("GET CONTENTS");
		Set<String> contents = new LinkedHashSet<String>();
		// The name is still empty
		if (chosenRiskFactorName == null) {
			// TODO
		}
		log.debug("HIERO chosenRiskFactorName DATAMANAGER: " + chosenRiskFactorName);		
		if (RiskFactorSelectionGroup.RISK_FACTOR.equals(name)) {
			contents = this.treeLists.getRiskFactors();
			log.debug("getContents NAME: " + contents);
		} else if (RiskFactorResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
			contents = this.treeLists.getRiskFactorPrevalences(chosenRiskFactorName);
			log.debug("contents1" + contents);
		} else if (RiskFactorResultGroup.TRANSITION.equals(name)) {
			contents = this.treeLists.getTransitions(chosenRiskFactorName);
			log.debug("contents2" + contents);
		}
		log.debug("contentsLast" + contents);
		return contents; 
	}

	@Override
	public String getCurrentValue(String dropDownName)
			throws ConfigurationException {
		log.debug("GET CURRENT VALUE");
		log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		if (this.singleConfiguration != null) {
			if (RiskFactorSelectionGroup.RISK_FACTOR.equals(dropDownName)) {
				value = singleConfiguration.getName();
				log.debug("VALUE: " + value);
			} else if (RiskFactorResultGroup.RISK_FACTOR_PREVALENCE.equals(dropDownName)) {
				value = singleConfiguration.getPrevalenceFileName();
				log.debug("value" + value);
			} else if (RiskFactorResultGroup.TRANSITION.equals(dropDownName)) {
				value = singleConfiguration.getTransitionFileName();
				log.debug("value" + value);
			}
		}
		return value;
	}

	@Override
	public DropDownPropertiesSet getDropDownSet(String name, String chosenRiskFactorName)
			throws ConfigurationException {
		log.debug("HIERALOOK");

		// The model object already exists, get the name
		if (singleConfiguration != null && chosenRiskFactorName == null) {
			chosenRiskFactorName = this.singleConfiguration.getName();
			log.debug("chosenRiskFactorName JUST CREATED" + chosenRiskFactorName);
		} 
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		set.addAll(this.getContents(name, chosenRiskFactorName));
		return set;	
	}

	@Override
	public DropDownPropertiesSet getRefreshedDropDownSet(String label)
			throws ConfigurationException {
		return getDropDownSet(label, null);
	}

	@Override
	public void removeFromDynamoSimulationObject()
			throws ConfigurationException {
		this.configurations.remove(this.singleConfiguration.getName());
		this.dynamoSimulationObject.setRiskFactorConfigurations(configurations); 
	}

	@Override
	public void removeOldDefaultValue(String label)
			throws ConfigurationException {
		// Will not be used

	}

	@Override
	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException {
		// Will not be used

	}

	@Override
	public void updateObjectState(String name, String selectedValue)
			throws ConfigurationException {
		log.debug(name + ": " + selectedValue);		
		log.debug("UPDATING OBJECT STATE");
		
		if (RiskFactorSelectionGroup.RISK_FACTOR.equals(name)) {
			singleConfiguration.setName(selectedValue);
		} else if (RiskFactorResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
			singleConfiguration.setPrevalenceFileName(selectedValue);
		} else if (RiskFactorResultGroup.TRANSITION.equals(name)) {
			singleConfiguration.setTransitionFileName(selectedValue);
		}
		updateDynamoSimulationObject();
	}

	public void updateDynamoSimulationObject() {
		log.error("UPDATING");
		log.debug("singleConfiguration" + singleConfiguration);
		log.debug("singleConfiguration.getName()" + singleConfiguration.getName());
		
		this.configurations.put(singleConfiguration.getName(), 
				singleConfiguration);
		this.dynamoSimulationObject.setRiskFactorConfigurations(configurations);
		
		/**
		 * TODO REMOVE: LOGGING BELOW
		 */
		Map map = this.dynamoSimulationObject.getRiskFactorConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			TabRiskFactorConfigurationData conf = (TabRiskFactorConfigurationData) map.get(key);
			log.error("conf.getName()" + conf.getName());
			log.error("conf.getPrevalenceFileName()" + conf.getPrevalenceFileName());
			log.error("conf.getIncidenceFileName()" + conf.getTransitionFileName());
		}
		log.debug("configurations.size()" + configurations.size());
		/**
		 * TODO REMOVE: LOGGING ABOVE
		 */
	}

	
	private String getInitialName() {
		String chosenRiskFactorName = null;
		if (this.initialSelection != null) {
			for (String chosenName : this.initialSelection) {
				chosenRiskFactorName = chosenName;		
			}			
		}
		return chosenRiskFactorName; 
	}

	@Override
	public WritableValue getCurrentWritableValue(String successRate) {
		// Will not be used
		return null;
	}
	
}
