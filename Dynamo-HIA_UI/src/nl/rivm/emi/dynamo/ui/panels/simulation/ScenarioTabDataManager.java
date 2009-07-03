package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabScenarioConfigurationData;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * 
 * Handles the simulation object data actions of the scenario tabs
 * 
 * @author schutb
 *
 */
public class ScenarioTabDataManager implements DynamoTabDataManager {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String MALE = "Male";
	private static final String FEMALE = "Female";
	private static final String MALE_FEMALE = "Male and Female";
	
	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject dynamoSimulationObject;
	private Map<String, ITabScenarioConfiguration> configurations;
	private ITabScenarioConfiguration singleConfiguration;
	private Set<String> initialSelection;
	
	public ScenarioTabDataManager(BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> selections) throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.configurations = this.dynamoSimulationObject.getScenarioConfigurations();
		this.initialSelection = selections;
		this.singleConfiguration = this.configurations.get(getInitialName());
	}

	public Set<String> getContents(String name, String chosenRiskFactorName)
			throws ConfigurationException {
		Set<String> contents = new LinkedHashSet<String>();
		// The name is still empty
		if (chosenRiskFactorName == null) {
			chosenRiskFactorName = this.getInitialRiskFactorName();
		}
		log.debug("HIERO chosenRiskFactorName DATAMANAGER: " + chosenRiskFactorName);		
		if (ScenarioSelectionGroup.MIN_AGE.equals(name)) {
			for  (int age = 0; age < 96; age++) {
				String showAge = age + "";
				contents.add(showAge);	
			}
			log.debug("contents0" + contents);
			log.debug("getContents MIN_AGE: " + contents);
		} else if (ScenarioSelectionGroup.MAX_AGE.equals(name)) {
			for  (int age = 0; age < 96; age++) {
				String showAge = age + "";
				contents.add(showAge);	
			}
			log.debug("contents1" + contents);
		} else if (ScenarioSelectionGroup.GENDER.equals(name)) {
			contents.add(MALE);
			contents.add(FEMALE);
			contents.add(MALE_FEMALE);
			log.debug("contents2" + contents);
		} else if (ScenarioResultGroup.TRANSITION.equals(name)) {
			contents = this.treeLists.getTransitions(chosenRiskFactorName);
			log.debug("contents3" + contents);
		} else if (ScenarioResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
			contents = this.treeLists.getRiskFactorPrevalences(chosenRiskFactorName);
			log.debug("contents4" + contents);
		}
		log.debug("contentsLast" + contents);
		return contents;
	}

	public String getCurrentValue(String name)
			throws ConfigurationException {
		log.debug("GET CURRENT VALUE");
		log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		Integer age = null;
		Integer gender = null;
		if (this.singleConfiguration != null) {
			if (ScenarioSelectionGroup.MIN_AGE.equals(name)) {
				age = singleConfiguration.getMinAge();
				// For all Integer values
				value = (age==null) ? null : age.toString();
				log.debug("VALUE: " + value);
			} else if (ScenarioSelectionGroup.MAX_AGE.equals(name)) {
				age = singleConfiguration.getMaxAge();
				// For all Integer values
				value = (age==null) ? null : age.toString();
				log.debug("value" + value);
			} else if (ScenarioSelectionGroup.GENDER.equals(name)) {
				gender = singleConfiguration.getTargetSex();
				log.debug("GENDER value " + gender);
				// For all Integer values
				value = convertToText(gender);
				log.debug("value" + value);
			} else if (ScenarioResultGroup.TRANSITION.equals(name)) {
				value = singleConfiguration.getAltTransitionFileName();
				log.debug("TRANSITION value " + value);
			} else if (ScenarioResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
				value = singleConfiguration.getAltPrevalenceFileName();
				log.debug("RISK_FACTOR_PREVALENCE value " + value);
			}
		}
		return value;
	}

	public WritableValue getCurrentWritableValue(String name) {
		WritableValue writableValue = null;
		if (this.singleConfiguration != null) {
			if (ScenarioSelectionGroup.NAME.equals(name)) {
				writableValue = singleConfiguration.getObservableName();
				log.debug("VALUE: " + writableValue);
			} else if (ScenarioSelectionGroup.SUCCESS_RATE.equals(name)) {
				writableValue = singleConfiguration.getObservableSuccessRate();
				// For all Integer writableValues
				log.debug("writableValue" + writableValue);				
			}
		}		
		return writableValue;
	}
	
	public DropDownPropertiesSet getDropDownSet(String name, String chosenScenarioName)
			throws ConfigurationException, NoMoreDataException {
		log.debug("HIERALOOK");
		String chosenRiskFactorName = null;
		// The model object already exists, get the name
		if (singleConfiguration != null && chosenRiskFactorName == null) {
			chosenRiskFactorName = this.getInitialRiskFactorName();
			log.debug("chosenRiskFactorName JUST CREATED" + chosenRiskFactorName);
		} 
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		
		Set<String> contents = this.getContents(name, chosenRiskFactorName);
		
		// Contents can never be empty

		if (contents != null) 
			set.addAll(contents);
		else throw new NoMoreDataException("no more configured diseases availlable");
	
		
		return set;
	}

	public DropDownPropertiesSet getRefreshedDropDownSet(String label)
			throws ConfigurationException, NoMoreDataException {
		return getDropDownSet(label, null);
	}

	public void removeFromDynamoSimulationObject()
			throws ConfigurationException {
		log.error("REMOVING OBJECT STATE");
		this.configurations.remove(this.singleConfiguration.getName());
		this.dynamoSimulationObject.setScenarioConfigurations(configurations);
	}

	public void removeOldDefaultValue(String label)
			throws ConfigurationException {
		// Will not be used

	}

	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException {
		// Will not be used

	}

	public void updateObjectState(String name, String selectedValue)
			throws ConfigurationException {
		log.debug(name + ": " + selectedValue);
		
		log.debug("UPDATING OBJECT STATE");
		// In case a new Tab is created, no model exists yet
		if (this.initialSelection == null && 
				singleConfiguration == null) {	
			log.debug("CREATING NEW TAB");
			createInDynamoSimulationObject();
		}		
	
		if (ScenarioSelectionGroup.NAME.equals(name)) {
			singleConfiguration.setName(selectedValue);			
		} else if (ScenarioSelectionGroup.SUCCESS_RATE.equals(name)) {
			singleConfiguration.setSuccessRate(new Integer(selectedValue));			
		} else if (ScenarioSelectionGroup.MIN_AGE.equals(name)) {
			singleConfiguration.setMinAge(new Integer(selectedValue));			
		} else if (ScenarioSelectionGroup.MAX_AGE.equals(name)) {
			singleConfiguration.setMaxAge(new Integer(selectedValue));				
		} else if (ScenarioSelectionGroup.GENDER.equals(name)) {
			Integer gender = convertToInteger(selectedValue);
			singleConfiguration.setTargetSex(gender);				
		} else if (ScenarioResultGroup.TRANSITION.equals(name)) {
			singleConfiguration.setAltTransitionFileName(selectedValue);							
		} else if (ScenarioResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
			singleConfiguration.setAltPrevalenceFileName(selectedValue);
		}		
		updateDynamoSimulationObject();
	}
	
	private Integer convertToInteger(String selectedValue) {
		log.debug("convertToInteger: selectedValue:: " + selectedValue);
		Integer gender = null;
		if (this.MALE.equals(selectedValue)) { 
			gender = new Integer(0);
		}
		else if (this.FEMALE.equals(selectedValue)) {
			gender = new Integer(1);
		}	
		else if (this.MALE_FEMALE.equals(selectedValue)) {
			gender = new Integer(2);
		}
		log.debug("convertToInteger:  gender:: " + gender);
		return gender;
	}

	private String convertToText(Integer gender) {
		String valueSelected = null;
		if (gender == null)
			return null;
		switch (gender.intValue()) {
			case 0:
				valueSelected = MALE;
				break;
			case 1:
				valueSelected = FEMALE;
				break;
			case 2:
				valueSelected = MALE_FEMALE;
				break;
		}
		return valueSelected;
	}
	
	public void updateDynamoSimulationObject() {
		log.error("UPDATING");
		log.debug("singleConfiguration" + singleConfiguration);
		log.debug("singleConfiguration.getName()" + singleConfiguration.getName());
		
		this.configurations.put(singleConfiguration.getName(), 
				singleConfiguration);
		this.dynamoSimulationObject.setScenarioConfigurations(configurations);
		
		/**
		 * TODO REMOVE: LOGGING BELOW
		 */
		Map map = this.dynamoSimulationObject.getScenarioConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			ITabScenarioConfiguration conf = (ITabScenarioConfiguration) map.get(key);
			log.error("conf.getName()" + conf.getName());
			log.error("conf.getMinAge()" + conf.getMinAge());
			log.error("conf.getMaxAge()" + conf.getMaxAge());
			log.error("conf.getTargetSex()" + conf.getTargetSex());
			log.error("conf.getAltTransitionFileName()" + conf.getAltTransitionFileName());
			log.error("conf.getAltPrevalenceFileName()" + conf.getAltPrevalenceFileName());
		}
		log.debug("configurations.size()" + configurations.size());
		/**
		 * TODO REMOVE: LOGGING ABOVE
		 */
	}

	private Object getInitialName() {
		String defaultScenarioName = null;
		if (this.initialSelection != null) {
			for (String chosenName : this.initialSelection) {
				defaultScenarioName = chosenName;		
			}			
		}
		return defaultScenarioName;
	}
	
	private String getInitialRiskFactorName() {
		String chosenRiskFactorName = null;
		Map map = this.dynamoSimulationObject.getRiskFactorConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			TabRiskFactorConfigurationData conf = (TabRiskFactorConfigurationData) map.get(key);
			log.error("conf.getName()" + conf.getName());
			chosenRiskFactorName = conf.getName();
		}		  
		return chosenRiskFactorName; 
	}

	private void createInDynamoSimulationObject() {
		this.singleConfiguration = new TabScenarioConfigurationData();
	}
	@Override
	public DynamoSimulationObject getDynamoSimulationObject() {
		// TODO Auto-generated method stub
		return this.getDynamoSimulationObject();
	}
	
}
