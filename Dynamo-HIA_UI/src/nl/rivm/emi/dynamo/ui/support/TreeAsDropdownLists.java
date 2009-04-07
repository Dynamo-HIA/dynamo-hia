package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

public class TreeAsDropdownLists extends HashMap<String, Object> {

	static TreeAsDropdownLists instance = null;

	Set<String> chosenDiseaseNames = new HashSet<String>();

	/**
	 * Instantiate the singleton Object and initialize it based on the current
	 * state of the tree.
	 * 
	 * @param selectedNode
	 * @throws ConfigurationException
	 */
	private TreeAsDropdownLists(BaseNode selectedNode)
			throws ConfigurationException {
		super();
	}

	/**
	 * Get the single instance.
	 * 
	 * @param selectedNode
	 * @return
	 * @throws ConfigurationException
	 */
	static synchronized public TreeAsDropdownLists getInstance(
			BaseNode selectedNode) throws ConfigurationException {
		if (instance == null) {
			instance = new TreeAsDropdownLists(selectedNode);
		}
		instance.refresh(selectedNode);
		return instance;
	}

	/**
	 * Rebuild the contents of the collection based on the present contents of
	 * the Tree.
	 * 
	 * @param selectedNode
	 * @throws ConfigurationException
	 */
	public void refresh(BaseNode selectedNode) throws ConfigurationException {
		this.clear();
		this.putAll(SimulationConfigurationDropdownsMapFactory
				.make(selectedNode));
	}

	/**
	 * This method returns a list with the names of the populations in the tree
	 * that have been correctly configured.
	 * 
	 * @return
	 */
	public Set<String> getPopulations() {
		Set<String> populations = (Set<String>) get(StandardTreeNodeLabelsEnum.POPULATIONS
				.getNodeLabel());
		return populations;
	}

	/**
	 * This method returns a list with the names of the diseases in the tree
	 * that have been correctly configured.
	 * 
	 * @return
	 * @param currentDiseasesName
	 *            the name that is currently chosen in the dropdown and should
	 *            be able to be chosen again.
	 * @return
	 */
	public Set<String> getValidDiseases() {
		HashMap<String, Object> diseasesMap = (HashMap<String, Object>) get(StandardTreeNodeLabelsEnum.DISEASES
				.getNodeLabel());
		Set<String> diseaseNames = diseasesMap.keySet();
		return diseaseNames;
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
	public Set<String> getChoosableDiseases(String currentDiseasesName) {
		chosenDiseaseNames.remove(currentDiseasesName);
		HashMap<String, Object> diseasesMap = (HashMap<String, Object>) get(StandardTreeNodeLabelsEnum.DISEASES
				.getNodeLabel());
		Set<String> diseaseNames = diseasesMap.keySet();
		for (String chosenName : chosenDiseaseNames) {
			diseaseNames.remove(chosenName);
		}
		return diseaseNames;
	}

	public void setChosenDisease(String chosenDiseaseName) {
		chosenDiseaseNames.add(chosenDiseaseName);
	}

	public Set<String> getDiseasePrevalences(String chosenDiseaseName) {
		String name = StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel();
		Set<String> theSet = getDiseaseSet(chosenDiseaseName, name);
		return theSet;
	}

	public Set<String> getDiseaseIncidences(String chosenDiseaseName) {
		String name = StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel();
		Set<String> theSet = getDiseaseSet(chosenDiseaseName, name);
		return theSet;
	}

	public Set<String> getDiseaseExcessMortalities(String chosenDiseaseName) {
		String name = StandardTreeNodeLabelsEnum.EXCESSMORTALITIES
				.getNodeLabel();
		Set<String> theSet = getDiseaseSet(chosenDiseaseName, name);
		return theSet;
	}

	public Set<String> getDALYWeights(String chosenDiseaseName) {
		String name = StandardTreeNodeLabelsEnum.DALYWEIGHTS.getNodeLabel();
		Set<String> theSet = getDiseaseSet(chosenDiseaseName, name);
		return theSet;
	}

	private Set<String> getDiseaseSet(String chosenDiseaseName, String name) {
		Set<String> theSet = null;
		HashMap<String, Object> diseasesMap = (HashMap<String, Object>) get(StandardTreeNodeLabelsEnum.DISEASES
				.getNodeLabel());
		if (diseasesMap != null) {
			HashMap<String, Object> diseaseMap = (HashMap<String, Object>) diseasesMap
					.get(chosenDiseaseName);
			if (diseaseMap != null) {
				theSet = (Set<String>) diseaseMap.get(name);
			}
		}
		return theSet;
	}

	/**
	 * This method returns a list with the names of the riskfactors in the tree
	 * that have been correctly configured.
	 * 
	 * @return
	 */
	public Set<String> getRiskFactors() {
		HashMap<String, Object> riskFactorsMap = (HashMap<String, Object>) get(StandardTreeNodeLabelsEnum.RISKFACTORS
				.getNodeLabel());
		Set<String> riskFactorNames = riskFactorsMap.keySet();
		return riskFactorNames;
	}

	public Set<String> getRiskFactorPrevalences(String chosenRiskFactorName) {
		String name = StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel();
		Set<String> theSet = getRiskFactorSet(chosenRiskFactorName, name);
		return theSet;
	}

	public Set<String> getTransitions(String chosenRiskFactorName) {
		String name = StandardTreeNodeLabelsEnum.TRANSITION.getNodeLabel();
		Set<String> theSet = getRiskFactorSet(chosenRiskFactorName, name);
		return theSet;
	}

	private Set<String> getRiskFactorSet(String chosenRiskFactorName,
			String name) {
		Set<String> theSet = null;
		HashMap<String, Object> riskFactorsMap = (HashMap<String, Object>) get(StandardTreeNodeLabelsEnum.RISKFACTORS
				.getNodeLabel());
		if (riskFactorsMap != null) {
			HashMap<String, Object> riskFactorMap = (HashMap<String, Object>) riskFactorsMap
					.get(chosenRiskFactorName);
			if (riskFactorMap != null) {
				theSet = (Set<String>) riskFactorMap.get(name);
			}
		}
		return theSet;
	}
}
