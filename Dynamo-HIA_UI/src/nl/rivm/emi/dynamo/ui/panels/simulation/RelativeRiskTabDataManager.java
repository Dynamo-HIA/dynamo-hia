package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
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
 * Handles the data actions of the relative risk tabs
 * 
 * @author schutb
 *
 */
public class RelativeRiskTabDataManager implements DynamoTabDataManager {

	private static final String RELRISK_DEATH = "relriskfordeath";

	private static final String RELRISK_DISABILITY = "relriskfordisability";

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject dynamoSimulationObject;
	private Map<Integer, TabRelativeRiskConfigurationData> configurations;
	private TabRelativeRiskConfigurationData singleConfiguration;
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
	public RelativeRiskTabDataManager(BaseNode selectedNode, 
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> initialSelection
			) throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.configurations = this.dynamoSimulationObject.getRelativeRiskConfigurations();
		this.singleConfiguration = (TabRelativeRiskConfigurationData) this.configurations.get(getInitialName());
	}
	
	/* 
	 * chosenName can be from the from list, the to list, or the relative risk list
	 * 
	 * (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getDropDownSet(java.lang.String, java.lang.String)
	 */
	@Override
	public DropDownPropertiesSet getDropDownSet(String name, String chosenName) throws ConfigurationException {
		log.debug("HIERALOOK");
		
		String chosenRiskFactorName = null;
		if (singleConfiguration != null && chosenRiskFactorName == null) {
			// Check if already the chosenRiskFactorName exists and if it is the risk factor
			if(this.getInitialRiskFactorName().equals(this.singleConfiguration.getFrom()) 
					&& RelativeRiskSelectionGroup.FROM.equals(name)) {
				chosenRiskFactorName =	this.singleConfiguration.getFrom();
				log.debug("chosenRiskFactorName JUST CREATED" + chosenRiskFactorName);
			}
		} else {
			if(this.getInitialRiskFactorName().equals(chosenName)) {
				chosenRiskFactorName =	chosenName;
			}
		}
		
		String chosenToName = null;
		// The model object already exists, get the name
		if (singleConfiguration != null && chosenToName == null) {
			chosenToName = this.singleConfiguration.getTo();
			log.debug("chosenToName JUST CREATED" + chosenToName);
			setDefaultValue(RelativeRiskSelectionGroup.TO, chosenToName);
		}
		
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		set.addAll(this.getContents(name, chosenName, chosenRiskFactorName, chosenToName));
		return set;	
	}


	/**
	 * 
	 * Customized method for Relative Risks only.
	 * (As replacement of getContents(String name, String chosenValue)
	 * 
	 * @param name
	 * @param chosenToName
	 * @param chosenRiskFactorName
	 * @return Set<String> Set of chosen values
	 * @throws ConfigurationException
	 */
	public Set<String> getContents(String name, String chosenName, 
			String chosenRiskFactorName, String chosenToName) throws ConfigurationException {
		log.debug("GET CONTENTS");
		Set<String> contents = new LinkedHashSet<String>();
		//TODO FOR BOTH TO AND FROM LIST ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();

		// TODO !!!!!!! The name is still empty
		if (chosenToName == null) {
			//TODO FOR BOTH TO AND FROM LIST  chosenToName = 
			//TODO FOR BOTH TO AND FROM LIST  (String) choosableDiseases.getFirstDiseaseOfSet(chosenToName, treeLists);
			//TODO FOR BOTH TO AND FROM LIST  setDefaultValue(RelativeRiskSelectionGroup.TO, chosenToName);
		}
		
		//log.debug("HIERO chosenDiseaseName DATAMANAGER: " + chosenDiseaseName);		
		if (RelativeRiskSelectionGroup.FROM.equals(name)) {
			// This is the full list of available diseases + relative risk
			contents = this.getInitialFromList();
			// TODO: Get Choosable list for fromList, that is being initialized with this list
			//TODO FOR BOTH TO AND FROM LIST contents = choosableDiseases.getChoosableDiseases(chosenDiseaseName, treeLists);
			log.debug("getContents NAME: " + contents);
		} else if (RelativeRiskSelectionGroup.TO.equals(name)) {
			// This is the full list of available diseases + death + disability
			contents = this.getInitialToList();
			// TODO: Get Choosable list for toList, that is being initialized with this list
			//TODO FOR BOTH TO AND FROM LIST contents = choosableDiseases.getChoosableDiseases(chosenDiseaseName, treeLists);
			log.debug("contents1" + contents);
		} else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
			contents = this.treeLists.getValidRelRiskFileNamesForFromName(chosenToName);
			// Filter only for the allowed risk factor type (identified by the unique chosenRiskFactorName)
			if (chosenRiskFactorName != null && chosenRiskFactorName.isEmpty()) {
				filterByRiskFactorType(contents, chosenRiskFactorName);	
			}
			log.debug("contents2" + contents);
		}
		log.debug("contentsLast" + contents);
		return contents;		
	}

	private void filterByRiskFactorType(Set<String> contents, String chosenRiskFactorName) {		
		for (String relativeRisk: contents) {
			if (!relativeRisk.contains(chosenRiskFactorName)) {				
				contents.remove(relativeRisk);
			}			
		}
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
			if (RelativeRiskSelectionGroup.FROM.equals(name)) {
				value = singleConfiguration.getFrom();
				log.debug("VALUE: " + value);
			} else if (RelativeRiskSelectionGroup.TO.equals(name)) {
				value = singleConfiguration.getTo();
				log.debug("value" + value);
			} else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
				value = singleConfiguration.getDataFileName();
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
			//TODO FOR BOTH TO AND FROM LIST  ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
			//TODO FOR BOTH TO AND FROM LIST  String  chosenDiseaseName = 
			//TODO FOR BOTH TO AND FROM LIST 	(String) choosableDiseases.getFirstDiseaseOfSet(null, treeLists);
			//TODO FOR BOTH TO AND FROM LIST  selectedValue = chosenDiseaseName;
		}		
		
		if (RelativeRiskSelectionGroup.FROM.equals(name)) {
			singleConfiguration.setFrom(selectedValue);
			setDefaultValue(DiseaseSelectionGroup.DISEASE, selectedValue);
		} else if (RelativeRiskSelectionGroup.TO.equals(name)) {
			singleConfiguration.setTo(selectedValue);				
		} else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
			singleConfiguration.setDataFileName(selectedValue);				
		}
		updateDynamoSimulationObject();
	}
	
	public void updateDynamoSimulationObject() {
		log.error("UPDATING");
		log.debug("singleConfiguration" + singleConfiguration);
		log.debug("singleConfiguration.getFrom()" + singleConfiguration.getFrom());
		log.debug("singleConfiguration.getFrom()" + singleConfiguration.getTo());
		log.debug("singleConfiguration.getDataFileName()" 
				+ singleConfiguration.getDataFileName());
		
		// Set the finalIndex in case the entry does not exist yet
		int finalIndex = this.configurations.size() - 1;
		// Check the triple key of the stored object before being replaced 
		for (Integer index : configurations.keySet()) {
			TabRelativeRiskConfigurationData config = this.configurations.get(index);
			if (config.getFrom().equals(this.singleConfiguration.getFrom())
					&& config.getTo().equals(this.singleConfiguration.getTo()) 
					&& config.getDataFileName().equals(this.singleConfiguration.getDataFileName())) {
				finalIndex = index;
			}				
		}
		// Store the object
		this.configurations.put(finalIndex, singleConfiguration);
		this.dynamoSimulationObject.setRelativeRiskConfigurations(configurations);
		
		/**
		 * TODO REMOVE: LOGGING BELOW
		 */
		Map map = this.dynamoSimulationObject.getDiseaseConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			TabRelativeRiskConfigurationData conf = (TabRelativeRiskConfigurationData) map.get(key);
			log.error("conf.getFrom()" + conf.getFrom());
			log.error("conf.getTo()" + conf.getTo());
			log.error("conf.getDataFileName()" + conf.getDataFileName());
		}
		log.debug("configurations.size()" + configurations.size());
		/**
		 * TODO REMOVE: LOGGING ABOVE
		 */
	}

	private void createInDynamoSimulationObject() {
		this.singleConfiguration = new TabRelativeRiskConfigurationData();
	}
	
	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#removeFromDynamoSimulationObject()
	 */
	public void removeFromDynamoSimulationObject() throws ConfigurationException {
		log.error("REMOVING OBJECT STATE");
		//TODO For both to and from list !!! ChoosableDiseases.getInstance().removeChosenDisease(this.singleConfiguration.getTo());
		 
		for (Integer index : configurations.keySet()) {
			TabRelativeRiskConfigurationData config = this.configurations.get(index);
			// Check the triple key of the stored object before being removed
			if (config.getFrom().equals(this.singleConfiguration.getFrom())
					&& config.getTo().equals(this.singleConfiguration.getTo()) 
					&& config.getDataFileName().equals(this.singleConfiguration.getDataFileName())) {
				this.configurations.remove(this.singleConfiguration);	
			}				
		}
		
		this.dynamoSimulationObject.setRelativeRiskConfigurations(configurations);
	}
	
	private String getInitialName() {
		String chosenRelativeRiskName = null;
		if (this.initialSelection != null) {
			for (String chosenName : this.initialSelection) {
				chosenRelativeRiskName = chosenName;		
			}			
		}
		return chosenRelativeRiskName; 
	}

	private String getInitialRiskFactorName() {
		String chosenRiskFactorNameFromTab = null;
		Map map = this.dynamoSimulationObject.getRiskFactorConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			TabRiskFactorConfigurationData conf = (TabRiskFactorConfigurationData) map.get(key);
			log.error("conf.getName()" + conf.getName());
			chosenRiskFactorNameFromTab = conf.getName();
		}		  
		return chosenRiskFactorNameFromTab; 
	}
	
	private Set<String> getInitialDiseasesList() {
		Set<String> chosenDiseases = new LinkedHashSet<String>();		
		Map map = this.dynamoSimulationObject.getDiseaseConfigurations();
		
		Set<String> keys = map.keySet();
		for (String key : keys) {			
			TabDiseaseConfigurationData conf = (TabDiseaseConfigurationData) map.get(key);
			log.error("conf.getName()" + conf.getName());
			chosenDiseases.add(conf.getName());			
		}		  
		return chosenDiseases; 
	}
	
	// TODO: From list (composed of getInitial) 
	/**
	 * List for the From dropdown that consists of chosen diseases
	 * and the chosen (only one) risk factor.
	 * 
	 */
	private Set<String> getInitialFromList() {		
		Set<String> initialDiseasesList = getInitialDiseasesList();
		initialDiseasesList.add(getInitialRiskFactorName());						 
		return initialDiseasesList; 
	}
	
	// TODO: To list (composed)
	/**
	 * List for the To dropdown that consists of chosen diseases
	 * and the relativeriskfordeath and relativeriskfordisability.
	 * 
	 */	
	private Set<String> getInitialToList() {		
		Set<String> initialDiseasesList = getInitialDiseasesList();
		// Add relriskfordeath
		initialDiseasesList.add(RELRISK_DEATH);
		// Add relriskfordeath
		initialDiseasesList.add(RELRISK_DISABILITY);
		return initialDiseasesList;
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
			//TODO FOR BOTH TO AND FROM LIST 	ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
			//TODO FOR BOTH TO AND FROM LIST choosableDiseases.setChosenDisease(selectedValue);
		}
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#removeOldDefaultValue(java.lang.String)
	 */
	@Override
	public void removeOldDefaultValue(String name) throws ConfigurationException {
		if (this.singleConfiguration != null) {
			log.debug("OLDDEFAULT: " + this.singleConfiguration.getTo());
			if (RelativeRiskSelectionGroup.TO.equals(name)) {
				//TODO FOR BOTH TO AND FROM LIST ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
				//TODO FOR BOTH TO AND FROM LIST choosableDiseases.removeChosenDisease(this.singleConfiguration.getTo());
			}			
		}
	}

	@Override
	public Set<String> getContents(String name, String chosenDiseaseName)
			throws ConfigurationException {
		// Will not be used
		return initialSelection;
	}

	@Override
	public WritableValue getCurrentWritableValue(String successRate) {
		// Will not be used
		return null;
	}
	
	
}
