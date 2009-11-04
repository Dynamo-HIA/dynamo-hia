package nl.rivm.emi.dynamo.ui.support;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.simulation.DiseasesTabPlatform;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * Represents a String Set of diseases that have already been chosen (other than
 * implied by the name!!). Is used to compile a list of chosable diseases.
 * 
 * @author schutb
 * 
 * @param <String>
 */
public class ChoosableDiseases<String> extends LinkedHashSet<String> {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private static ChoosableDiseases chosenDiseaseNames = null;

	private ChoosableDiseases() {
		super();
	}

	DiseasesTabPlatform diseasesTabPlatform;

	/**
	 * Get the single instance. There can be only one.
	 * 
	 * @param selectedNode
	 * @return
	 * @throws ConfigurationException
	 */
	static synchronized public ChoosableDiseases getInstance()
			throws ConfigurationException {
		if (chosenDiseaseNames == null) {
			chosenDiseaseNames = new ChoosableDiseases();
		}
		return chosenDiseaseNames;
	}

	/**
	 * Get the single instance. There can be only one.
	 * 
	 * @param selectedNode
	 * @return
	 * @throws ConfigurationException
	 */
	static synchronized public ChoosableDiseases createFreshInstance()
			throws ConfigurationException {
		chosenDiseaseNames = new ChoosableDiseases();
		return chosenDiseaseNames;
	}

	/**
	 * This method returns a list with the names of the diseases in the tree
	 * that have been correctly configured and can be chosen in a dropdown at
	 * this point.
	 * For proper working of the program it should return null if the choosenDisease
	 * is not part of the diseases that can be choosen
	 * 
	 * @return
	 * @param currentDiseaseName
	 *            the name that is currently chosen in the dropdown and should
	 *            be able to be chosen again.
	 * @return
	 */
	public Set<String> getChoosableDiseases(String currentDiseaseName,
			TreeAsDropdownLists lists) {
		log.debug("currentDiseasesName: " + currentDiseaseName);

		
		Set diseaseNames = new LinkedHashSet<String>();
		diseaseNames.addAll(lists.getValidDiseaseNames());
		log.fatal("diseaseNames: " + diseaseNames);
		log.fatal("Chosendiseases-1-1-1: " + this);
		//loop over the names that already have been choosen and remove those from the set, unless
		// this is the current disease
		if (currentDiseaseName!=null && !diseaseNames.contains(currentDiseaseName)) diseaseNames=null;
		else
		for (String chosenName : (Set<String>) this) {
			log.debug("REMOVING CHOSENNAME: " + chosenName);
			// Hendriek 31-10-2009: condition added (see above)
			if (chosenName != currentDiseaseName)
				diseaseNames.remove(chosenName);
			
		}
		/* added by Hendriek 2-11-2009 */
		/* if the current disease is not a choosable disease, the method should return null */
		if (diseaseNames!=null && diseaseNames.isEmpty()) diseaseNames=null;
		// end addition 2-11-2009
		log.fatal("diseaseNames222: " + diseaseNames);
		log.fatal("Chosendiseases-2-2-2: " + this);
		
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
		log.debug("Chosendiseases111BEFORE: " + this);
		if( chosenDiseaseName!=null) this.add(chosenDiseaseName);
		log.debug("Chosendiseases111AFTER: " + this);
		log.fatal("Chosendiseases added: " + chosenDiseaseName);
		log.fatal("Chosendiseases : " + this);
	}

	/* this make the disease choosable again */
	public void removeChosenDisease(String name) {
		log.debug("ChosendiseasesXXXBEFORE: " + this);
		// log.debug("index" + index);
		// String name = ((String) this.toArray()[index]);
		log.debug("removename:" + name);
		this.remove(name);
		log.fatal("Chosendiseases removed: " + name);
		log.fatal("Chosendiseases: " + this);
	}

	/**
	 * 
	 * Retrieves the first name from the available disease list
	 * 
	 * @param currentDiseasesName
	 * @param lists
	 * @return String
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	public String getFirstDiseaseOfSet(String currentDiseaseName,
			TreeAsDropdownLists lists) throws ConfigurationException,
			NoMoreDataException {
		try {
			log.debug("ChosendiseasesEEEE: " + this);
			Set<String> stillAvaillableDiseases=this.getChoosableDiseases(currentDiseaseName,
					lists);
			if (stillAvaillableDiseases==null) throw new NoMoreDataException("A new disease is not available");
			else 
			return (String) stillAvaillableDiseases.iterator().next();
		} catch (NoSuchElementException nse) {
			throw new NoMoreDataException("A new disease is not available");
		}

	}
}
