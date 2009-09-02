package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TreeAsDropdownLists extends HashMap<String, Object> {
	private static final long serialVersionUID = 4098178986054592061L;

	private Log log = LogFactory.getLog(this.getClass().getName());

	private static TreeAsDropdownLists instance = null;

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
		log.debug("Refreshing.....");
		this.clear();
		HashMap<String, Object> allButRelativeRisksMap = SimulationConfigurationDropdownsMapFactory
		.make(selectedNode);
		this.putAll(allButRelativeRisksMap);
		RelativeRisksCollection rrCollection = new RelativeRisksCollection(selectedNode, this);
		log.debug("Dumping.....");
		log.debug(rrCollection.dump4Debug());
		this.put(StandardTreeNodeLabelsEnum.RELATIVERISKS.getNodeLabel(),
				rrCollection);
	}

	/**
	 * This method returns a list with the names of the populations in the tree
	 * that have been correctly configured.
	 * 
	 * @return
	 */
	public Set<String> getPopulations() {
		log.debug("getPopulations::TreeAsDropdownLists instance: " + this);
		log.debug("StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel() "
				+ StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel());
		log.debug("TreeAsDropdownLists instance::GET POPULATIONS: "
				+ (Set<String>) get(StandardTreeNodeLabelsEnum.POPULATIONS
						.getNodeLabel()));
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
	public Set<String> getValidDiseaseNames() {
		log.debug("getValidDiseases().....");
		HashMap<String, Object> diseasesMap = (HashMap<String, Object>) get(StandardTreeNodeLabelsEnum.DISEASES
				.getNodeLabel());
		Set<String> diseaseNames = diseasesMap.keySet();
		return diseaseNames;
	}

	public Set<String> getDiseasePrevalences(String chosenDiseaseName) {
		log.debug("getDiseasePrevalences(" + chosenDiseaseName + ").....");
		String name = StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel();
		Set<String> theSet = getDiseaseSet(chosenDiseaseName, name);
		return theSet;
	}

	public Set<String> getDiseaseIncidences(String chosenDiseaseName) {
		log.debug("getDiseaseIncidences(" + chosenDiseaseName + ").....");
		String name = StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel();
		Set<String> theSet = getDiseaseSet(chosenDiseaseName, name);
		return theSet;
	}

	public Set<String> getDiseaseExcessMortalities(String chosenDiseaseName) {
		log
				.debug("getDiseaseExcessMortalities(" + chosenDiseaseName
						+ ").....");
		String name = StandardTreeNodeLabelsEnum.EXCESSMORTALITIES
				.getNodeLabel();
		Set<String> theSet = getDiseaseSet(chosenDiseaseName, name);
		return theSet;
	}

	public Set<String> getDALYWeights(String chosenDiseaseName) {
		log.debug("getDALYWeights(" + chosenDiseaseName + ").....");
		String name = StandardTreeNodeLabelsEnum.DALYWEIGHTS.getNodeLabel();
		Set<String> theSet = getDiseaseSet(chosenDiseaseName, name);
		return theSet;
	}

	private Set<String> getDiseaseSet(String chosenDiseaseName, String name) {
		log
				.debug("getDiseaseSet(" + chosenDiseaseName + ", " + name
						+ ").....");
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
	public Set<String> get2RiskFactors() {
		log.debug("get2RiskFactors().....");
		HashMap<String, Object> riskFactorsMap = (HashMap<String, Object>) get(StandardTreeNodeLabelsEnum.RISKFACTORS
				.getNodeLabel());
		Set<String> riskFactorNames = riskFactorsMap.keySet();
		return riskFactorNames;
	}

	/**
	 * This method returns an object containing the names of the relative risks
	 * in the tree that have been correctly configured.
	 * 
	 * @return
	 */
	public Set<String> getRiskFactorNames() {
		log.debug("getRiskFactors().....");
		Exception ex = new Exception();
		StackTraceElement[] stackTraceArray = ex.getStackTrace();
//		for (int count = 0; count < 2 && count < stackTraceArray.length; count++) {
//			log.debug("StackTraceElement at " + count + ": "
//					+ stackTraceArray[count].getClassName() + "."
//					+ stackTraceArray[count].getMethodName() + "(" + stackTraceArray[count].getLineNumber()+ ")");
//		}
		HashMap<String, Object> riskFactorsMap = (HashMap<String, Object>) get(StandardTreeNodeLabelsEnum.RISKFACTORS
				.getNodeLabel());
		Set<String> riskFactorNames = riskFactorsMap.keySet();
		return riskFactorNames;
	}

	public Set<String> getRiskFactorPrevalences(String chosenRiskFactorName) {
		log.debug("getRiskFactorPrevalences( " + chosenRiskFactorName
				+ ").....");
		String name = StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel();
		Set<String> theSet = getRiskFactorSet(chosenRiskFactorName, name);
		return theSet;
	}

	public Set<String> getTransitions(String chosenRiskFactorName) {
		log.debug("getTransitions( " + chosenRiskFactorName + ").....");
		String name = StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel();
		Set<String> theSet = getRiskFactorSet(chosenRiskFactorName, name);
		return theSet;
	}

	private Set<String> getRiskFactorSet(String chosenRiskFactorName,
			String name) {
		log.debug("getRiskFactorSet( " + chosenRiskFactorName + ", " + name
				+ ").....");
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

	public Set<String> getValidFromNames() {
		log.debug("getValidFromNames().....");
		RelativeRisksCollection collection = (RelativeRisksCollection) get(StandardTreeNodeLabelsEnum.RELATIVERISKS
				.getNodeLabel());
		return collection.getValidFromNames();
	}

	public Set<String> getValidToNames() {
		log.debug("getValidToNames().....");
		RelativeRisksCollection collection = (RelativeRisksCollection) get(StandardTreeNodeLabelsEnum.RELATIVERISKS
				.getNodeLabel());
		return collection.getValidToNames();
	}

	public RelativeRiskFileNamesBySourceAndTargetNameMap getValidRelativeRiskCollection() {
		log.debug("getValidRelativeRiskCollection().....");
		RelativeRisksCollection collection = (RelativeRisksCollection) get(StandardTreeNodeLabelsEnum.RELATIVERISKS
				.getNodeLabel());
		return collection.getConfiguredRelRisks();
	}

	public Set<String> getValidRelRiskFileNamesForFromName(String fromName) {
		log
				.debug("getValidRelRiskFileNamesForFromName( " + fromName
						+ ").....");
		RelativeRisksCollection collection = (RelativeRisksCollection) get(StandardTreeNodeLabelsEnum.RELATIVERISKS
				.getNodeLabel());
		return collection.getValidRelRiskFileNamesForFromName(fromName);
	}

	public Set<String> getValidRelRiskFileNamesForToName(String toName) {
		log.debug("getValidRelRiskFileNamesForToName( " + toName + ").....");
		RelativeRisksCollection collection = (RelativeRisksCollection) get(StandardTreeNodeLabelsEnum.RELATIVERISKS
				.getNodeLabel());
		return collection.getValidRelRiskFileNamesForToName(toName);
	}
}
