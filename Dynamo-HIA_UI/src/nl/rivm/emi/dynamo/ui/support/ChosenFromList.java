package nl.rivm.emi.dynamo.ui.support;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.ui.panels.simulation.DiseaseSelectionGroup;

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
public class ChosenFromList <String> extends LinkedHashSet<String> {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static ChosenFromList chosenFromNames = null;

	private ChosenFromList() {
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
	static synchronized public ChosenFromList getInstance(
			) throws ConfigurationException {
		if (chosenFromNames == null) {
			chosenFromNames = new ChosenFromList();
		}
		return chosenFromNames;
	}
	
	/**
	 * This method returns a list with the names of the diseases in the tree
	 * that have been correctly configured and can be chosen in a dropdown at
	 * this point.
	 * 
	 * @return
	 * @param currentToName
	 *            the name that is currently chosen in the dropdown and should
	 *            be able to be chosen again.
	 * @param configurations 
	 * @param chosenFromName 
	 * @return
	 */
	public Set<String> getChoosableToNames(String currentToName, 
			Set<String> completeToList
			) {
		log.debug("currentToName: " + currentToName);
		this.remove(currentToName);
		Set toNames = new LinkedHashSet<String>();
		toNames.addAll(completeToList);		
		log.debug("toNames: " + toNames);
		log.debug("Chosendiseases-1-1-1: " + this);
		
		for (String chosenName : (Set<String>)this) {
			log.debug("REMVOVING CHOSENNAME: " + chosenName);	
			toNames.remove(chosenName);
		}
		
		log.debug("diseaseNames222: " + toNames);
		log.debug("Chosendiseases-2-2-2: " + this);
		return toNames;
	}

	/**
	 * 
	 * Adds the name of the chosen disease to the list
	 * 
	 * @param chosenDiseaseName
	 */
	public void setChosenFromList(String chosenFromName) {
		log.debug("setChosenFromList" + chosenFromName);
		log.debug("setChosenFromList111BEFORE: " + this);
		this.add(chosenFromName);
		log.debug("setChosenFromList111AFTER: " + this);
	}

	public void removeChosenFromList(String chosenFromName) {
		log.debug("removeChosenFromListChosendiseasesXXXBEFORE: " + this);
		log.debug("removename:" + chosenFromName);		
		this.remove(chosenFromName);
		log.debug("removeChosenFromListXXXAFTER: " + this);
	}
	
	/**
	 * 
	 * Retrieves the first name from the available disease list
	 * @param map 
	 * @param chosenFromName 
	 * 
	 * @param currentDiseasesName
	 * @param lists
	 * @return String
	 * @throws ConfigurationException 
	 */
	public String getFirstToNameOfSet(String currentToName, 
			Set<String> completeToList
			) throws ConfigurationException {
		try {
			log.debug("ChsdfasdfEEEE: " + this);
			log.debug("completeToList: " + completeToList);
			log.debug("currentToName: " + currentToName);
			String result = this.getChoosableToNames(currentToName, 
					completeToList
					).iterator().next();
			return result;
		} catch(NoSuchElementException nse) {
			throw new ConfigurationException("A new entry is not available");
		}
			
	}
	
}
