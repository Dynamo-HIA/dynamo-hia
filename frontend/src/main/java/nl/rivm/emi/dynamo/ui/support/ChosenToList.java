package nl.rivm.emi.dynamo.ui.support;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * Represents a String Set of diseases that have already been chosen.
 * Is used to compile a list of chosable diseases. 
 * 
 * @author schutb
 *
 * @param <String>
 */
@SuppressWarnings("hiding")
public class ChosenToList <String> extends LinkedHashSet<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	@SuppressWarnings("rawtypes")
	private static ChosenToList chosenToNames = null;

	private ChosenToList() {
		super();
	}

	/**
	 * Get the single instance.
	 * There can be only one.
	 * 
	 * @param selectedNode
	 * @return
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("rawtypes")
	static synchronized public ChosenToList getInstance(
			) throws ConfigurationException {
		if (chosenToNames == null) {
			chosenToNames = new ChosenToList();
		}
		return chosenToNames;
	}
	
	/**
	 * This method returns a list with the names of the diseases in the tree
	 * that have been correctly configured and can be chosen in a dropdown at
	 * this point.
	 * 
	 * @return
	 * @param currentFromName
	 *            the name that is currently chosen in the dropdown and should
	 *            be able to be chosen again.
	 * @param map 
	 * @param chosenToName 
	 * @param integer 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getChoosableFromNames(String currentFromName, 
			Set<String> completeFromList) {
		log.debug("currentDiseasesName: " + currentFromName);
		this.remove(currentFromName);
		@SuppressWarnings("rawtypes")
		Set fromNames = new LinkedHashSet<String>();
		fromNames.addAll(completeFromList);		
		log.debug("fromNames: " + fromNames);
		log.debug("Chosendiseases-1-1-1: " + this);
		
		for (String chosenName : (Set<String>)this) {
			log.debug("REMVOVING CHOSENNAME: " + chosenName);	
			fromNames.remove(chosenName);
		}
		log.debug("diseaseNames222: " + fromNames);
		log.debug("Chosendiseases-2-2-2: " + this);
		return fromNames;
	}

	/**
	 * 
	 * Adds the name of the chosen disease to the list
	 * 
	 * @param chosenDiseaseName
	 */
	public void setChosenToList(String chosenToName) {
		log.debug("setChosenToList" + chosenToName);
		log.debug("setChosenToList111BEFORE: " + this);
		this.add(chosenToName);
		log.debug("setChosenToList111AFTER: " + this);
	}

	public void removeChosenToList(String chosenToName) {
		log.debug("removeChosenToListChosendiseasesXXXBEFORE: " + this);
		log.debug("removename:" + chosenToName);		
		this.remove(chosenToName);
		log.debug("removeChosenToListXXXAFTER: " + this);
	}
	
	/**
	 * 
	 * Retrieves the first name from the available disease list
	 * @param map 
	 * @param chosenToName 
	 * 
	 * @param currentDiseasesName
	 * @param lists
	 * @return String
	 * @throws ConfigurationException 
	 */
	public String getFirstFromNameOfSet(String currentFromName, 
			Set<String> completeFromList) throws ConfigurationException {
		try {
			log.debug("ChsdfasdfEEEE: " + this);
			return (String) this.getChoosableFromNames(currentFromName, 
					completeFromList).iterator().next();	
		} catch(NoSuchElementException nse) {
			throw new ConfigurationException("A new entry is not available");
		}
			
	}

	/** this method updates the list directly from the DynamoSimulationObject
	 * author: hendriek
	 * @param dynamoSimulationObject: the DynamoSimulationObject
	
	 */
	@SuppressWarnings("unchecked")
	public void updateChosenToList(DynamoSimulationObject dynamoSimulationObject) {
			
		Map<Integer,TabRelativeRiskConfigurationData> relRiskConfiguration =
		dynamoSimulationObject.getRelativeRiskConfigurations();
		TabRelativeRiskConfigurationData singleRRconfiguration;
		this.clear();
		for (Integer key : relRiskConfiguration.keySet())

		{singleRRconfiguration=relRiskConfiguration.get(key);
			this.add((String) singleRRconfiguration.getTo()) ;
			
	        }

		
		
		
		
			
	}
	
}
