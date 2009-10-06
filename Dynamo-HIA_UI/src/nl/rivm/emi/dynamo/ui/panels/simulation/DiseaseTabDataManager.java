package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
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
 * Handles the simulation object data actions of the disease tabs
 * 
 * @author schutb
 *
 */
public class DiseaseTabDataManager implements DynamoTabDataManager {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject dynamoSimulationObject;
//	private Map<String, ITabDiseaseConfiguration> configurations;
	
	public Map<String, ITabDiseaseConfiguration> getConfigurations() {
		Map<String, ITabDiseaseConfiguration> configurations = this.getDynamoSimulationObject().getDiseaseConfigurations();
		
		return configurations;
	}

	
	private ITabDiseaseConfiguration singleConfiguration;
	public ITabDiseaseConfiguration getSingleConfiguration() {
		return singleConfiguration;
	}

	public  void setConfigurations(Map<String, ITabDiseaseConfiguration> newConfig){
		
		this.dynamoSimulationObject.setDiseaseConfigurations(newConfig);
	}
	
	public void setSingleConfiguration(ITabDiseaseConfiguration singleConfiguration) {
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
			Set<String> initialSelection
			) throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject=dynamoSimulationObject;
		this.initialSelection = initialSelection;
		log.debug("this.initialSelectionDiseaseTabManager" + this.initialSelection);
		this.singleConfiguration = getConfigurations().get(getInitialName());
	}
	
	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getDropDownSet(java.lang.String, java.lang.String)
	 */
	public DropDownPropertiesSet getDropDownSet(String name, String chosenDiseaseName) throws ConfigurationException, NoMoreDataException {
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
		else if (chosenDiseaseName==null) throw new NoMoreDataException("no more configured diseases availlable");
		else throw new NoMoreDataException("the configured disease "+ chosenDiseaseName+" is no longer availlable");
		
		return set;	
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getContents(java.lang.String, java.lang.String)
	 */
	public Set<String> getContents(String name, String chosenDiseaseName) throws ConfigurationException, NoMoreDataException {
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
	 * @throws NoMoreDataException 
	 */
	public void updateObjectState(String name, String selectedValue) throws ConfigurationException, NoMoreDataException {
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
		Map<String, ITabDiseaseConfiguration> configurations = getConfigurations();
		configurations.put(singleConfiguration.getName(), 
				singleConfiguration);
		this.getDynamoSimulationObject().setDiseaseConfigurations(configurations);
		
		
		
		/**
		 * TODO REMOVE: LOGGING BELOW
		 */
		Map map = this.getDynamoSimulationObject().getDiseaseConfigurations();
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
		
		String removedDisease=this.singleConfiguration.getName();
		Map<String, ITabDiseaseConfiguration> configurations = getConfigurations();
		configurations.remove(removedDisease);
		this.getDynamoSimulationObject().setDiseaseConfigurations(configurations);
		/* added by Hendriek
		 * Also check if the disease names in the relative risks are still
		 * valid
		 * If not remove
		 * Both to and from can be diseasenames and should be checked
		 */
		//String riskfactorName=((TabRiskFactorConfigurationData) configurations).getName();
		Map<Integer,TabRelativeRiskConfigurationData> relRiskConfiguration =
		this.getDynamoSimulationObject().getRelativeRiskConfigurations();
		
			TabRelativeRiskConfigurationData singleRRconfiguration;
			
			
			for ( Iterator<TabRelativeRiskConfigurationData> iter = relRiskConfiguration.values().iterator(); iter.hasNext(); )
	        {
	        
		//	for (Integer key2 : relRiskConfiguration.keySet())
			
				
		
		
		singleRRconfiguration= iter.next();
	   
		
		if (singleRRconfiguration.getFrom().equals(removedDisease)||
				singleRRconfiguration.getTo().equals(removedDisease)	)
			iter.remove();
			
		log.fatal("stop5: "+"size: "+relRiskConfiguration.size());
        }
		
		
		this.getDynamoSimulationObject().setRelativeRiskConfigurations(relRiskConfiguration);
		
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
	public DropDownPropertiesSet getRefreshedDropDownSet(String label) throws ConfigurationException, NoMoreDataException {
		return getDropDownSet(label, null);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#setDefaultValue(java.lang.String, java.lang.String)
	 */
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
	public void removeOldDefaultValue(String name) throws ConfigurationException {
		if (this.singleConfiguration != null) {
			log.debug("OLDDEFAULT: " + this.singleConfiguration.getName());
			if (DiseaseSelectionGroup.DISEASE.equals(name)) {
				ChoosableDiseases choosableDiseases = ChoosableDiseases.getInstance();
				choosableDiseases.removeChosenDisease(this.singleConfiguration.getName());
			}			
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
