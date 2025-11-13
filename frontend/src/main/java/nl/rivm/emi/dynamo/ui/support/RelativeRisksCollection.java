package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelativeRisksCollection {
	private Log log = LogFactory.getLog(getClass().getSimpleName());

	/**
	 * ParentNodeNames for the entities dropdowns have to be created for.
	 */

	static final String[] possibleParentNodeNames = {
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel() };
	/*
	 * configuredRelRisk has been changed by Hendriek from a set to a collection
	 * with all RR information as entries. Keys are: from, to and the inner
	 * entry a Set with the relative risknames For this we need a class that
	 * holds the RR info, called RRdat
	 */

	RelativeRiskFileNamesBySourceAndTargetNameMap relRisksBySourceNameAndTargetName = new RelativeRiskFileNamesBySourceAndTargetNameMap();

	private class RelativeRiskNamesByANameMap extends
			HashMap<String, Set<String>> {
		private static final long serialVersionUID = 471571555642536818L;

		public boolean add(String keyName, String relativeRiskFileName) {
			boolean hasBeenAdded = false;
			Set<String> relRiskFileNameSet = get(keyName);
			if (relRiskFileNameSet == null) {
				relRiskFileNameSet = new HashSet<String>();
				put(keyName, relRiskFileNameSet);
			}
			hasBeenAdded = relRiskFileNameSet.add(relativeRiskFileName);
			return hasBeenAdded;
		}
	}

	RelativeRiskNamesByANameMap relRiskBySourceName = new RelativeRiskNamesByANameMap();
	RelativeRiskNamesByANameMap relRiskByTargetName = new RelativeRiskNamesByANameMap();

	public RelativeRisksCollection(BaseNode selectedNode,
			TreeAsDropdownLists treeAsDropdownLists)
			throws ConfigurationException {
		super();
		log.debug("Constructing....");
		findAllRelativeRisks(selectedNode, treeAsDropdownLists);
		dump4Debug();
	}

	public RelativeRiskFileNamesBySourceAndTargetNameMap getConfiguredRelRisks() {
		return relRisksBySourceNameAndTargetName;
	}

	public Set<String> getValidFromNames() {
		return relRiskBySourceName.keySet();
	}

	public HashMap<String, HashMap<String, Set<String>>> getValidRelRisks() {
		return relRisksBySourceNameAndTargetName;
	}

	public Set<String> getValidToNames() {
		return relRiskByTargetName.keySet();
	}

	public Set<String> getValidRelRiskFileNamesForFromName(String fromName) {
		return relRiskBySourceName.get(fromName);
	}

	public Set<String> getValidRelRiskFileNamesForToName(String toName) {
		return relRiskByTargetName.get(toName);
	}

	// Initialization.
	/**
	 * Find all useable relative risks irrespective of the current simulation
	 * configuration.
	 * 
	 * @param
	 * 
	 * @throws ConfigurationException
	 */
	public void findAllRelativeRisks(BaseNode selectedNode,
			TreeAsDropdownLists treelist) throws ConfigurationException {
		ParentNode referenceDataNode = FactoryCommon
				.findReferenceDataNode(selectedNode);
		Object[] refDataChildNodes = referenceDataNode.getChildren();
		for (Object refDataChildObject : refDataChildNodes) {
			BaseNode refDataChildNode = (BaseNode) refDataChildObject;
			if (canContainRelativeRisks(refDataChildNode)) {
				if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel().equals(
						refDataChildNode.deriveNodeLabel())) {
					findRelRisks4Diseases(refDataChildNode, treelist);
				} else {
					if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
							.equals(refDataChildNode.deriveNodeLabel())) {
						findRelRisks4DeathOrDisability(refDataChildNode,
								treelist);
					}
				}
			}
		}
		log.debug("By sourceName.");
		dump4Log(relRiskBySourceName);
		log.debug("By targetName.");
		dump4Log(relRiskByTargetName);
	}

	private void findRelRisks4Diseases(Object refDataChildNode,
			TreeAsDropdownLists treelist) throws DynamoConfigurationException {
		Set<String> validDiseases = treelist.getValidDiseaseNames();
		Object[] diseaseChildren = ((ParentNode) refDataChildNode)
				.getChildren();
		for (Object diseaseChild : diseaseChildren) {
			String diseaseChildName = ((BaseNode) diseaseChild)
					.deriveNodeLabel();
			if (validDiseases.contains(diseaseChildName)) {
				processValidDisease(diseaseChild, diseaseChildName, treelist);
			}
		}
	}

	private void processValidDisease(Object diseaseChild,
			String diseaseChildName, TreeAsDropdownLists treeList)
			throws DynamoConfigurationException {
		Object[] diseaseSubDirChildren = ((ParentNode) diseaseChild)
				.getChildren();
		for (Object diseaseSubDirChild : diseaseSubDirChildren) {
			String diseasesSubDirName = ((BaseNode) diseaseSubDirChild)
					.deriveNodeLabel();
			if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
					.getNodeLabel().equals(diseasesSubDirName)) {
				Set<String> validDiseaseNames = treeList.getValidDiseaseNames();
				handlePossibleRelativeRisksForDiseaseFromSource(
						diseaseChildName, diseaseSubDirChild, validDiseaseNames);
			} else {
				if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
						.getNodeLabel().equals(diseasesSubDirName)) {
					Set<String> validRiskFactorNames = treeList
							.getRiskFactorNames();
					handlePossibleRelativeRisksForDiseaseFromSource(
							diseaseChildName, diseaseSubDirChild,
							validRiskFactorNames);
				}
			}
		}
	}

	private void handlePossibleRelativeRisksForDiseaseFromSource(
			String diseaseChildName, Object relRiskContainerDirectory,
			Set<String> validSourceNames) throws DynamoConfigurationException {
		Object[] relRiskChildren = ((ParentNode) relRiskContainerDirectory)
				.getChildren();
		for (Object relRiskChild : relRiskChildren) {
			if (relRiskChild instanceof FileNode) {
				String relRiskFileName = ((BaseNode) relRiskChild)
						.deriveNodeLabel();
				String delims = "[-]";
				String[] nameParts = relRiskFileName.split(delims);
				/* the part after the last - contains the source of the relative risk */
				String sourceName=nameParts[nameParts.length-1];
				for (String validSourceName : validSourceNames) {
					if (sourceName.equalsIgnoreCase(validSourceName)) {
						log.debug("Found relative risk \"" + relRiskFileName
								+ "\" from \"" + validSourceName + "\" on \""
								+ diseaseChildName + "\"");
						relRiskBySourceName.add(validSourceName,
								relRiskFileName);
						relRiskByTargetName.add(diseaseChildName,
								relRiskFileName);
						relRisksBySourceNameAndTargetName.add(validSourceName,
								diseaseChildName, relRiskFileName);
						break; // Handle only the first match.
					}
				}
			} else {
				// Not lethal, just not "comme il faut".
				log.warn("handlePossibleRelativeRisksFromSOurce() got a "
						+ relRiskChild.getClass().getName()
						+ " instead of a FileNode.");
			}
		}
	}

	private void findRelRisks4DeathOrDisability(Object refDataChildNode,
			TreeAsDropdownLists treelist) throws DynamoConfigurationException {
		Set<String> validRiskFactorNames = treelist.getRiskFactorNames();
		Object[] riskFactorChildren = ((ParentNode) refDataChildNode)
				.getChildren();
		for (Object riskFactorChild : riskFactorChildren) {
			String riskFactorChildName = ((BaseNode) riskFactorChild)
					.deriveNodeLabel();
			if (validRiskFactorNames.contains(riskFactorChildName)) {
				processValidRiskFactor(riskFactorChildName, riskFactorChild,
						treelist);
			}
		}
	}

	private void processValidRiskFactor(String riskFactorName,
			Object riskFactorChild, TreeAsDropdownLists treeList)
			throws DynamoConfigurationException {
		Object[] riskFactorSubDirChildren = ((ParentNode) riskFactorChild)
				.getChildren();
		for (Object riskFactorSubDirChild : riskFactorSubDirChildren) {
			String riskFactorSubDirName = ((BaseNode) riskFactorSubDirChild)
					.deriveNodeLabel();
			if (StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel()
					.equals(riskFactorSubDirName)) {
				handlePossibleRelativeRisksForTargetFromSource(riskFactorName,
						"death", riskFactorSubDirChild);
			} else {
				if (StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR
						.getNodeLabel().equals(riskFactorSubDirName)) {
					@SuppressWarnings("unused")
					Set<String> validRiskFactorNames = treeList
							.getRiskFactorNames();
					handlePossibleRelativeRisksForTargetFromSource(
							riskFactorName, "disability",
							riskFactorSubDirChild);
				} else {
				}
			}
		}
	}

	private void handlePossibleRelativeRisksForTargetFromSource(
			String riskFactorName, String targetName,
			Object relRiskContainerDirectoryNode)
			throws DynamoConfigurationException {
		Object[] relRiskFileNodes = ((ParentNode) relRiskContainerDirectoryNode)
				.getChildren();
		for (Object potentialRelRiskFileNode : relRiskFileNodes) {
			if (potentialRelRiskFileNode instanceof FileNode) {
				String relRiskFileName = ((BaseNode) potentialRelRiskFileNode)
						.deriveNodeLabel();
						log.debug("Found relative risk \"" + relRiskFileName
								+ "\" from \"" + riskFactorName + "\" on \""
								+ targetName + "\"");
						relRiskBySourceName.add(riskFactorName,
								relRiskFileName);
						relRiskByTargetName.add(targetName, relRiskFileName);
						relRisksBySourceNameAndTargetName.add(riskFactorName,
								targetName, relRiskFileName);
			} else {
				// Not lethal, just not "comme il faut".
				log.warn("handlePossibleRelativeRisksFromSOurce() got a "
						+ potentialRelRiskFileNode.getClass().getName()
						+ " instead of a FileNode.");
			}
		}
	}

	/**
	 * If the validNodeName == null, the name is not in the
	 * possibleParentNodeNames list.
	 * 
	 * @param childNode
	 * @return validParentNodeName, null when invalid.
	 * @throws ConfigurationException
	 */
	static boolean canContainRelativeRisks(BaseNode childNode)
			throws ConfigurationException {
		String validNodeName = null;
		for (int count = 0; count < possibleParentNodeNames.length; count++) {
			if (possibleParentNodeNames[count].equals(childNode
					.deriveNodeLabel())) {
				validNodeName = possibleParentNodeNames[count];
				break;
			}
		}
		return (validNodeName != null);
	}

	public String dump4Debug() {
		StringBuffer dumpBuffer = new StringBuffer("\nStarting dump.......");
		for (String sourceName : relRiskBySourceName.keySet()) {
			dumpBuffer.append("Source-name: " + sourceName + " \n");
			Set<String> relRiskNames = relRiskBySourceName.get(sourceName);
			for (String relRiskName : relRiskNames) {
				dumpBuffer.append("\t\tRR: " + relRiskName + " \n");
			}
		}
		for (String targetName : relRiskByTargetName.keySet()) {
			dumpBuffer.append("Target-name: " + targetName + " \n");
			Set<String> relRiskNames = relRiskByTargetName.get(targetName);
			for (String relRiskName : relRiskNames) {
				dumpBuffer.append("\t\tRR: " + relRiskName + " \n");
			}
		}
		dumpBuffer.append(relRisksBySourceNameAndTargetName.dump4Log());
		
		return(dumpBuffer.toString());
	}

	public String dump4Log(HashMap<String, Set<String>> map) {
		StringBuffer dumpBuffer = new StringBuffer();
		Set<String> sourceNamesKeySet = map.keySet();
		if (sourceNamesKeySet.isEmpty()) {
			dumpBuffer.append("No sourceNames present.");
		} else {
			for (String sourceName : sourceNamesKeySet) {
				dumpBuffer.append("\n\t" + "sourceName: " + sourceName);
				Set<String> relRiskNamesByTargetNameMap = map.get(sourceName);
				if (relRiskNamesByTargetNameMap.isEmpty()) {
					dumpBuffer.append("No relative risks present.");
				} else {
					for (String relRiskName : relRiskNamesByTargetNameMap) {
						dumpBuffer.append("\n\t\t" + "relRiskName: "
								+ relRiskName);
					}
				}
			}
		}
		return dumpBuffer.toString();
	}

}
