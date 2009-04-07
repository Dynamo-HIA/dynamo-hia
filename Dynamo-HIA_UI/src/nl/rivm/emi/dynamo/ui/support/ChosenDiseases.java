package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;

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
public class ChosenDiseases<String> extends HashSet<String> {
	
	private static ChosenDiseases chosenDiseaseNames = null;

	private ChosenDiseases() {
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
	static synchronized public ChosenDiseases getInstance(
			BaseNode selectedNode) throws ConfigurationException {
		if (chosenDiseaseNames == null) {
			chosenDiseaseNames = new ChosenDiseases();
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
		HashMap<String, Object> diseasesMap = (HashMap<String, Object>) lists.get(StandardTreeNodeLabelsEnum.DISEASES
				.getNodeLabel());
		Set<String> diseaseNames = diseasesMap.keySet();
		for (String chosenName : (Set<String>)this) {
			diseaseNames.remove(chosenName);
		}
		return diseaseNames;
	}

	/**
	 * 
	 * Adds the name of the chosen disease to the list
	 * 
	 * @param chosenDiseaseName
	 */
	public void setChosenDisease(String chosenDiseaseName) {
		this.add(chosenDiseaseName);
	}
	
}
