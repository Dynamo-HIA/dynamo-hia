package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.simulation.DiseasesTabPlatform;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents a String Set of diseases that have already been chosen. Is used to
 * compile a list of chosable diseases.
 * 
 * @author schutb
 * 
 *         20091012 mondeelr Changed from Singleton, that hangs around for the
 *         duration of the application run to an instance. This instance is
 *         contained by the DiseaseTabPlatform.
 * 
 *         Existing functionality for checking diseases will be concentrated
 *         here.
 * 
 * @param <String>
 */
public class ChoosableDiseaseNameManager extends LinkedHashMap<String, String> {
	private static final long serialVersionUID = -7192915647047106096L;

	private Log log = LogFactory.getLog(this.getClass().getName());

	private TreeAsDropdownLists treeLists;

	DiseasesTabPlatform diseasesTabPlatform;
	/**
	 * reverse Map, used for debugging.
	 */
	Map<String, String> diseaseNameByTabNameMap;

	public ChoosableDiseaseNameManager(DiseasesTabPlatform diseasesTabPlatform)
			throws ConfigurationException {
		super();
		log.debug("<init>!!!");
		this.diseasesTabPlatform = diseasesTabPlatform;
		this.treeLists = TreeAsDropdownLists.getInstance(diseasesTabPlatform
				.getSelectedNode());
		diseaseNameByTabNameMap = new HashMap<String, String>();
	}

	/**
	 * This method returns a list with the names of the diseases in the tree
	 * that have been correctly configured and can be chosen in a dropdown at
	 * this point.
	 * 
	 * @return
	 * @param currentDiseaseName
	 *            the name that is currently chosen in the dropdown and should
	 *            be able to be chosen again.
	 * @return
	 */
	public Set<String> getChoosableDiseaseNamesSet(String currentDiseaseName) {
		log.debug("ChoosableDiseaseNamesManager: " + this
				+ " currentDiseaseName: " + currentDiseaseName);
		// Exception e = new Exception();
		// e.printStackTrace(System.out);
		Set<String> choosableDiseaseNames = new LinkedHashSet<String>();
		Set<String> validDiseaseNames = (Set<String>) treeLists
				.getValidDiseaseNames();
		choosableDiseaseNames.addAll(validDiseaseNames);
		log.debug("validDiseaseNames: " + choosableDiseaseNames);
		Set<String> alreadyChosenDiseaseNamesSet = this.keySet();
		log.debug("alreadyChosenDiseaseNamesSet: "
				+ alreadyChosenDiseaseNamesSet);
		choosableDiseaseNames.removeAll(alreadyChosenDiseaseNamesSet);
		if (currentDiseaseName != null) {
			choosableDiseaseNames.add(currentDiseaseName);
		}
		log.debug("choosableDiseaseNames: " + choosableDiseaseNames);
		return choosableDiseaseNames;
	}

	/**
	 * The name didn't cover the functionality, the method returns a Set of
	 * diseasenames.
	 * 
	 * Now it also checks these names against the diseases with a valid
	 * configuration and removes diseases that have for instance been deleted
	 * from the simulation configuration.
	 * 
	 * This upfront filtering was chosen because the constructing of the
	 * disease-tabs fails on absent diseases.
	 * 
	 * Consistency-check methods scattered throughout the simulationscreen
	 * functionality may try to salvage derived configurations by changing them
	 * from disappeared diseases to other items.
	 */
	public Set<String> getAndCleanDiseaseNames(
			LinkedHashMap<String, ITabDiseaseConfiguration> configurations) {
		Set<String> configuredDiseaseNames = configurations.keySet();
		Set<String> validDiseaseNames = (Set<String>) treeLists
				.getValidDiseaseNames();
		Set<String> approvedDiseaseNames = new LinkedHashSet<String>();
		Set<String> disApprovedDiseaseNames = new LinkedHashSet<String>();
		for (String diseaseName : configuredDiseaseNames) {
			if (validDiseaseNames.contains(diseaseName)) {
				approvedDiseaseNames.add(diseaseName);
			} else {
				disApprovedDiseaseNames.add(diseaseName);
			}
		}
		if (disApprovedDiseaseNames.size() != 0) {
			for (String disApprovedDiseaseName : disApprovedDiseaseNames) {
				configurations.remove(disApprovedDiseaseName);
			}
			diseasesTabPlatform
					.getDynamoSimulationObject()
					.setDiseaseConfigurations(
							(Map<java.lang.String, ITabDiseaseConfiguration>) configurations);
		}
		return approvedDiseaseNames;
	}

	/**
	 * 
	 * Adds the name of the chosen disease to the list
	 * 
	 * @param chosenDiseaseName
	 * @param tabName
	 *            TODO
	 * @throws ConfigurationException
	 */
	public void setChosenDiseaseName(String chosenDiseaseName, String tabName)
			throws ConfigurationException {
		log.debug("setChosenDiseaseName: " + chosenDiseaseName + " for tab: "
				+ tabName);
		// Debugging...
		// Exception e = new Exception();
		// e.printStackTrace(System.out);
		// ~Debugging...
		String possibleDuplicate = put(chosenDiseaseName, tabName);
		if (possibleDuplicate != null) {
			if (!possibleDuplicate.equals(tabName)) {
				throw new ConfigurationException("Same diseasename: "
						+ chosenDiseaseName + " selected for tab: " + tabName
						+ " , it was already chosen for tab: "
						+ possibleDuplicate);
			} else {
				log.debug("Setting diseasename: " + chosenDiseaseName
						+ " that was already there.");
			}
		}
		// Check the reverse, multiple diseases for a tab.
		possibleDuplicate = diseaseNameByTabNameMap.put(tabName,
				chosenDiseaseName);
		if (possibleDuplicate != null) {
			if (!possibleDuplicate.equals(chosenDiseaseName)) {
				log.fatal("At least two diseasenames (old: "
						+ possibleDuplicate + ", new: " + chosenDiseaseName
						+ ") assigned to tab: " + tabName);
			}
		}
	}

	public void removeChosenDiseaseName(String name, String tabName) {
		log.debug("About to remove DiseaseName: " + name + " for tab: "
				+ tabName);
		if (containsKey(name)) {
			remove(name);
			if (diseaseNameByTabNameMap.containsKey(tabName)) {
				String storedDiseaseName = diseaseNameByTabNameMap.get(tabName);
				diseaseNameByTabNameMap.remove(tabName);
				if (!name.equals(storedDiseaseName)) {
					log.error("Wrong diseasename: " + storedDiseaseName
							+ " in reverse map, expected: " + name);
				}
			} else {
				log.error("Tried to remove a tabName that wasn't there.");
			}
		} else {
			log.error("Tried to remove a diseasename that wasn't there.");
		}
	}

	/**
	 * 
	 * Retrieves the first name from the available disease list
	 * 
	 * @param currentDiseasesName
	 * @return String
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	public String getFirstDiseaseNameOfSet(String currentDiseasesName)
			throws ConfigurationException, NoMoreDataException {
		try {
			String firstDiseaseName = (String) this
					.getChoosableDiseaseNamesSet(currentDiseasesName)
					.iterator().next();
			log.debug("getFirstDiseaseOfSet: " + firstDiseaseName);
			return firstDiseaseName;
		} catch (NoSuchElementException nse) {
			throw new NoMoreDataException("No diseasename is available");
		}

	}
}
