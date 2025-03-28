package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.ChoosableDiseases;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * 
 * Handles the simulation object data actions of the disease tabs
 * 
 * @author schutb
 * 
 */
public class DiseaseTabDataManager implements DynamoTabDataManager {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject dynamoSimulationObject;

	// private Map<String, ITabDiseaseConfiguration> configurations;
	public Map<String, ITabDiseaseConfiguration> getConfigurations() {
		Map<String, ITabDiseaseConfiguration> configurations = this
				.getDynamoSimulationObject().getDiseaseConfigurations();

		return configurations;
	}

	private ITabDiseaseConfiguration singleConfiguration;

	public ITabDiseaseConfiguration getSingleConfiguration() {
		return singleConfiguration;
	}

	public void setConfigurations(
			Map<String, ITabDiseaseConfiguration> newConfig) {

		this.dynamoSimulationObject.setDiseaseConfigurations(newConfig);
	}

	public void setSingleConfiguration(
			ITabDiseaseConfiguration singleConfiguration) {
		this.singleConfiguration = singleConfiguration;
	}

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
			Set<String> initialSelection) throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.initialSelection = initialSelection;
		log.debug("this.initialSelectionDiseaseTabManager"
				+ this.initialSelection);
		this.singleConfiguration = getConfigurations().get(getInitialName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getDropDownSet
	 * (java.lang.String, java.lang.String)
	 */
	public DropDownPropertiesSet getDropDownSet(String name,
			String chosenDiseaseName) throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		log.debug("HIERALOOK");

		// The model object already exists, get the name
		if (singleConfiguration != null && chosenDiseaseName == null) {
			chosenDiseaseName = this.singleConfiguration.getName();
			log.debug("chosenDiseaseName JUST CREATED" + chosenDiseaseName);
			setDefaultValue(DiseaseSelectionGroup.DISEASE, chosenDiseaseName);
		}
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		Set<String> contents = this.getContents(name, chosenDiseaseName);

		// Contents can never be empty

		if (contents != null)
			set.addAll(contents);
		else if (chosenDiseaseName == null)
			throw new NoMoreDataException(
					"no more configured diseases availlable");
		else {
			throw new DynamoNoValidDataException("the configured disease "
					+ chosenDiseaseName + " is no longer availlable");
		}

		return set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getContents
	 * (java.lang.String, java.lang.String)
	 */
	public Set<String> getContents(String name, String chosenDiseaseName)
			throws ConfigurationException, NoMoreDataException {
		log.debug("GET CONTENTS");
		Set<String> contents = new LinkedHashSet<String>();
		ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
		// The name is still empty
		if (chosenDiseaseName == null) {
			chosenDiseaseName = (String) choosableDiseases
					.getFirstDiseaseOfSet(chosenDiseaseName, treeLists);
			setDefaultValue(DiseaseSelectionGroup.DISEASE, chosenDiseaseName);
			createInDynamoSimulationObject(chosenDiseaseName);
		}
		log.debug("HIERO chosenDiseaseName DATAMANAGER: " + chosenDiseaseName);
		if (DiseaseSelectionGroup.DISEASE.equals(name)) {
			// contents = this.treeLists.getValidDiseases();
			contents = choosableDiseases.getChoosableDiseases(
					chosenDiseaseName, treeLists);
			// 2-10-2009 check if the choosen disease name is in contents,
			// otherwise
			log.debug("getContents NAME: " + contents);
		} else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(name)) {
			contents = this.treeLists.getDiseasePrevalences(chosenDiseaseName);
			log.debug("contents1" + contents);
		} else if (DiseaseResultGroup.INCIDENCE.equals(name)) {
			contents = this.treeLists.getDiseaseIncidences(chosenDiseaseName);
			log.debug("contents2" + contents);
		} else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(name)) {
			contents = this.treeLists
					.getDiseaseExcessMortalities(chosenDiseaseName);
			log.debug("contents3" + contents);
		} else if (DiseaseResultGroup.DALY_WEIGHTS.equals(name)) {
			contents = this.treeLists.getDALYWeights(chosenDiseaseName);
			log.debug("contents4" + contents);
		}
		log.debug("contentsLast" + contents);
		return contents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getCurrentValue
	 * (java.lang.String)
	 */
	public String getCurrentValue(String name) throws ConfigurationException {
		log.debug("GET CURRENT VALUE");
		log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		if (this.singleConfiguration != null) {
			value = singleConfiguration.getValueForDropDown(name);
		}
		return value;
	}

	/**
	 * 
	 * Updates the object model every time a selection is made It also creates a
	 * new element in case of a new tab
	 * 
	 * @param dropDownName
	 * @param selectedValue
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	public void updateObjectState(String name, String selectedValue)
			throws ConfigurationException, NoMoreDataException {
		log.debug(name + ": " + selectedValue);

		log.info("UPDATING OBJECT STATE for "+name+ " and selected value "+selectedValue);
		if (singleConfiguration != null) 
			log.info("name current singleConfiguration "+singleConfiguration.getName());
		else log.info(" current singleConfiguration == null, so new disease is added");
		if (this.initialSelection != null) 
			log.info("this.initialSelection" +this.initialSelection);
		else log.info("this.initialSelection == null");	
			
			
			
		
		
		// In case a new Tab is created, no model exists yet
		if (this.initialSelection == null && singleConfiguration == null) {
			log.debug("CREATING NEW TAB");
			createInDynamoSimulationObject();
			ChoosableDiseases choosableDiseases = ChoosableDiseases
					.getInstance();
			String chosenDiseaseName = (String) choosableDiseases
					.getFirstDiseaseOfSet(null, treeLists);
			selectedValue = chosenDiseaseName;
			this.initialSelection = new LinkedHashSet<String>();
			this.initialSelection.add(chosenDiseaseName);
			this.singleConfiguration.setName(chosenDiseaseName);
		}

		if (this.initialSelection == null && singleConfiguration != null) {
			log.debug("CREATING NEW TAB");
			selectedValue = this.singleConfiguration.getName();
			this.initialSelection = new LinkedHashSet<String>();
			this.initialSelection.add(selectedValue);
		}
		
		// added 2010-2-22: if another disease is chosen, it should be removed from the dynamosimulationObject
		
		if (DiseaseSelectionGroup.DISEASE.equals(name) && !selectedValue.equals(this.singleConfiguration.getName()))
		{
			
			removeFromDynamoSimulationObject();
		}
		
		
		// Moved to function below the block.
		// if (DiseaseSelectionGroup.DISEASE.equals(name)) {
		// singleConfiguration.setName(selectedValue);
		// setDefaultValue(DiseaseSelectionGroup.DISEASE, selectedValue);
		// } else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(name)) {
		// singleConfiguration.setPrevalenceFileName(selectedValue);
		// } else if (DiseaseResultGroup.INCIDENCE.equals(name)) {
		// singleConfiguration.setIncidenceFileName(selectedValue);
		// } else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(name)) {
		// singleConfiguration.setExcessMortalityFileName(selectedValue);
		// } else if (DiseaseResultGroup.DALY_WEIGHTS.equals(name)) {
		// singleConfiguration.setDalyWeightsFileName(selectedValue);
		// }
		singleConfiguration.setValueFromDropDown(name, selectedValue);
		updateDynamoSimulationObject();
		//this.updateRelativeRisks(getConfigurations());
	}

	public void updateDynamoSimulationObject() {
		log.debug("UPDATING");
		log.debug("singleConfiguration" + singleConfiguration);
		log.debug("singleConfiguration.getName()"
				+ singleConfiguration.getName());
		Map<String, ITabDiseaseConfiguration> configurations = getConfigurations();
		
		configurations.put(singleConfiguration.getName(), singleConfiguration);
		this.getDynamoSimulationObject().setDiseaseConfigurations(
				configurations);

		/**
		 * TODO REMOVE: LOGGING BELOW
		 */
		Map map = this.getDynamoSimulationObject().getDiseaseConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			ITabDiseaseConfiguration conf = (ITabDiseaseConfiguration) map
					.get(key);
			log.debug("conf.getName()" + conf.getName());
			log.debug("conf.getPrevalenceFileName()"
					+ conf.getPrevalenceFileName());
			log.debug("conf.getIncidenceFileName()"
					+ conf.getIncidenceFileName());
			log.debug("conf.getExcessMortalityFileName()"
					+ conf.getExcessMortalityFileName());
			log.debug("conf.getDalyWeightsFileName()"
					+ conf.getDalyWeightsFileName());
		}
		log.debug("configurations.size()" + configurations.size());
		/**
		 * TODO REMOVE: LOGGING ABOVE
		 */
	}

	private void createInDynamoSimulationObject() {
		this.singleConfiguration = new TabDiseaseConfigurationData();

	}

	// method added by Hendriek 2009-10-31
	/*
	 * this methods adds a new disease to the dynamosimulation object, knowing
	 * the name
	 */

	private void createInDynamoSimulationObject(String diseaseName) {
		this.singleConfiguration = new TabDiseaseConfigurationData();
		this.singleConfiguration.setName(diseaseName);

		Map<String, ITabDiseaseConfiguration> configurations = getConfigurations();
		if (configurations == null) {

			Map<String, ITabDiseaseConfiguration> diseaseConfigurations = new LinkedHashMap<String, ITabDiseaseConfiguration>();
			diseaseConfigurations.put(diseaseName, this.singleConfiguration);
			configurations = diseaseConfigurations;

		}

		configurations.put(diseaseName, singleConfiguration);
		this.getDynamoSimulationObject().setDiseaseConfigurations(
				configurations);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * removeFromDynamoSimulationObject()
	 */
	public void removeFromDynamoSimulationObject()
			throws ConfigurationException {
		
		
		String removedDisease = this.singleConfiguration.getName();
		log.info("REMOVING FROM OBJECT STATE: "+removedDisease);
		ChoosableDiseases.getInstance().removeChosenDisease(
				removedDisease);

		
		Map<String, ITabDiseaseConfiguration> configurations = getConfigurations();
		configurations.remove(removedDisease);
		setConfigurations(configurations);
		
		
		/*
		 * added by Hendriek Also check if the disease names in the relative
		 * risks are still valid
		 * 
		 *  
		 *  If not remove Both to and from can be
		 * diseasenames 
		 * 
		 * In the new version this is omitted: no check on RRs for diseases that are not included,
		 * as RRs for not included disease will be simply ignored by the program
		 * anyway		 */
		// String riskfactorName=((TabRiskFactorConfigurationData)
		// configurations).getName();
		
		/*
		
		Map<Integer, TabRelativeRiskConfigurationData> relRiskConfiguration = this
				.getDynamoSimulationObject().getRelativeRiskConfigurations();

		TabRelativeRiskConfigurationData singleRRconfiguration;

		for (Iterator<TabRelativeRiskConfigurationData> iter = relRiskConfiguration
				.values().iterator(); iter.hasNext();) {

			// for (Integer key2 : relRiskConfiguration.keySet())

			singleRRconfiguration = iter.next();

			if (singleRRconfiguration.getFrom().equals(removedDisease)
					|| singleRRconfiguration.getTo().equals(removedDisease)  )
				iter.remove();

			log.debug("stop5: " + "size: " + relRiskConfiguration.size());
		}

		this.getDynamoSimulationObject().setRelativeRiskConfigurations(
				relRiskConfiguration);
*/
	}

	/**
	 * Method checking whether the relative risks are still valid with the
	 * currently choosen diseases; if not the non-valid diseases are removed
	 * 
	 * @param diseaseConfigurations
	 */
	private boolean updateRelativeRisks (Set<String> chosenDiseases){
		
			
	    Set<String> chosenRiskFactor= this.getDynamoSimulationObject().getRiskFactorConfigurations().keySet();
	   
		boolean relRiskConfigurationHasChanged=false;
	    TabRelativeRiskConfigurationData singleRRconfiguration;
		Map<Integer, TabRelativeRiskConfigurationData> relRiskConfiguration = this
		.getDynamoSimulationObject().getRelativeRiskConfigurations();
		for (Iterator<TabRelativeRiskConfigurationData> iter = relRiskConfiguration
				.values().iterator(); iter.hasNext();) {
			singleRRconfiguration = iter.next();
         /* check if the to is still valid */       
			String currentTo=singleRRconfiguration.getTo();
			if (!chosenDiseases.contains(currentTo) &&  !currentTo.equals("disability") && !currentTo.equals("death"))
				{ iter.remove(); relRiskConfigurationHasChanged=true;}
			  /* check if the from is still valid */      
			else if (!chosenDiseases.contains(singleRRconfiguration.getTo()) &&
					!chosenRiskFactor.contains(singleRRconfiguration.getTo()))
			{ iter.remove(); relRiskConfigurationHasChanged=true;}
            
			
	
		}
		if (relRiskConfigurationHasChanged) this
		.getDynamoSimulationObject().setRelativeRiskConfigurations(relRiskConfiguration);
		return relRiskConfigurationHasChanged;
		
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * getRefreshedDropDownSet(java.lang.String)
	 */
	public DropDownPropertiesSet getRefreshedDropDownSet(String label)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		return getDropDownSet(label, null);
	}

	
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#setDefaultValue(java.lang.String,
	 *      java.lang.String)
	 */
	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException {
		log.debug("SETDEFAULT: " + selectedValue);
		// 20091029+ Functionality added as an attempt to compensate for
		// disabled listeners at construction time.
		if (singleConfiguration == null) {
			singleConfiguration = new TabDiseaseConfigurationData();
		}
		singleConfiguration.setValueFromDropDown(name, selectedValue);
		// ~20091029+
		if (DiseaseSelectionGroup.DISEASE.equals(name)) {

			ChoosableDiseases choosableDiseases = ChoosableDiseases
					.getInstance();
			choosableDiseases.setChosenDisease(selectedValue);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * removeOldDefaultValue(java.lang.String)
	 */
	public void removeOldDefaultValue(String name)
			throws ConfigurationException {
		if (this.singleConfiguration != null) {
			log.info("OLDDEFAULT to remove for " + name + " : "
					+ this.singleConfiguration.getName());}
			
				
			if (DiseaseSelectionGroup.DISEASE.equals(name)) {
				String removedDisease = this.singleConfiguration.getName();
				ChoosableDiseases choosableDiseases = ChoosableDiseases
						.getInstance();
				/*
				 * this removes the disease from the diseases that have been
				 * chosen and makes it choosable again
				 */
				choosableDiseases.removeChosenDisease(this.singleConfiguration
						.getName());
				// next lines added by hendriek 2-2010 ;
              //  Map<String, ITabDiseaseConfiguration> configurations = getConfigurations();
				
			//	configurations.remove(removedDisease);
			//	this.getDynamoSimulationObject().setDiseaseConfigurations(
			//			configurations);
			//	Set<String> diseaseset=this.getDynamoSimulationObject().getDiseaseConfigurations().keySet();
				// updateRelativeRisks(diseaseset);
				// log.fatal("REMOVING OLD VALUE FROM OBJECT STATE : "+removedDisease);
			}
		}
	

	public WritableValue getCurrentWritableValue(String successRate) {
		// Will not be used
		return null;
	}

	@Override
	public DynamoSimulationObject getDynamoSimulationObject() {

		return this.dynamoSimulationObject;
	}

}
