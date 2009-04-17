package nl.rivm.emi.dynamo.ui.support;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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
public class ChosenToList <String> extends LinkedHashSet<String> {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
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
	 * @return
	 */
	public Set<String> getChoosableFromNames(String currentFromName, 
			Set<String> completeFromList) {
		log.debug("currentDiseasesName: " + currentFromName);
		this.remove(currentFromName);
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

	public void removeChosenFromList(String chosenToName) {
		log.debug("removeChosenFromListChosendiseasesXXXBEFORE: " + this);
		log.debug("removename:" + chosenToName);		
		this.remove(chosenToName);
		log.debug("removeChosenFromListXXXAFTER: " + this);
	}
	
	/**
	 * 
	 * Retrieves the first name from the available disease list
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
			return (String) this.getChoosableFromNames(currentFromName, completeFromList) 
				.iterator().next();	
		} catch(NoSuchElementException nse) {
			throw new ConfigurationException("A new entry is not available");
		}
			
	}
	
}
