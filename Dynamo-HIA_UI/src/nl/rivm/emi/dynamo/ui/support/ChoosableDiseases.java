package nl.rivm.emi.dynamo.ui.support;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

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
public class ChoosableDiseases <String> extends LinkedHashSet<String> {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static ChoosableDiseases chosenDiseaseNames = null;

	private ChoosableDiseases() {
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
	static synchronized public ChoosableDiseases getInstance(
			) throws ConfigurationException {
		if (chosenDiseaseNames == null) {
			chosenDiseaseNames = new ChoosableDiseases();
		}
		return chosenDiseaseNames;
	}
	
	/**
	 * This method returns a list with the names of the diseases in the tree
	 * that have been correctly configured and can be chosen in a dropdown at
	 * this point.
	 * 
	 * @return
	 * @param currentDiseasesName
	 *            the name that is currently chosen in the dropdown and should
	 *            be able to be chosen again.
	 * @return
	 */
	public Set<String> getChoosableDiseases(String currentDiseasesName, TreeAsDropdownLists lists) {
		this.remove(currentDiseasesName);		
		Set diseaseNames = lists.getValidDiseases();
		log.debug("diseaseNames: " + diseaseNames);
		log.debug("ChoosableDiseases-1-1-1: " + this);
		for (String chosenName : (Set<String>)this) {
			diseaseNames.remove(chosenName);
		}
		log.debug("diseaseNames222: " + diseaseNames);
		log.debug("ChoosableDiseases-2-2-2: " + this);
		return diseaseNames;
	}

	/**
	 * 
	 * Adds the name of the chosen disease to the list
	 * 
	 * @param chosenDiseaseName
	 */
	public void setChosenDisease(String chosenDiseaseName) {
		log.debug("setChosenDisease" + chosenDiseaseName);
		this.add(chosenDiseaseName);
		log.debug("ChoosableDiseases111: " + this);
	}

	public void removeChosenDisease(int index) {
		log.debug("index" + index);
		String name = ((String) this.toArray()[index]);
		log.debug("removename:" + name);
		this.remove(name);
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
	public String getFirstDiseaseOfSet(String currentDiseasesName, TreeAsDropdownLists lists) throws ConfigurationException {
		try {
			log.debug("ChoosableDiseases222: " + this);
			return (String) this.getChoosableDiseases(currentDiseasesName, lists).iterator().next();	
		} catch(NoSuchElementException nse) {
			throw new ConfigurationException("A new disease is not available");
		}
			
	}
	
}
