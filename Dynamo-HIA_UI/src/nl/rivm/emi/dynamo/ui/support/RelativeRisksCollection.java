package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelativeRisksCollection {
	private Log log = LogFactory.getLog("RelativeRisksCollection");

	TreeAsDropdownLists tADL;

	/**
	 * ParentNodeNames for the entities dropdowns have to be created for.
	 */
	
	
	static final String[] possibleParentNodeNames = {
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel(),
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel() };
	/* configuredRelRisk has been changed by Hendriek from a set to a collection with all RR information as
	 * entries. Keys are: from, to and the inner entry a Set with the relative risknames
	 * For this we need a class that holds the RR info, called RRdat
	 */
	
	HashMap<String, HashMap<String ,Set<String>>> configuredRelRisks = new HashMap<String,HashMap<String, Set<String>>>();
	public HashMap<String, HashMap<String, Set<String>>> getConfiguredRelRisks() {
		return configuredRelRisks;
	}

	HashMap<String, Set<String>> relRiskBySourceName = new HashMap<String, Set<String>>();
	HashMap<String, Set<String>> relRiskByTargetName = new HashMap<String, Set<String>>();

	public RelativeRisksCollection(BaseNode selectedNode, TreeAsDropdownLists treeAsDropdownLists)
			throws ConfigurationException {
		super();
		tADL = treeAsDropdownLists;
		findAllRelativeRisks(selectedNode, tADL);
	}

	public Set<String> getValidFromNames(){
		return relRiskBySourceName.keySet();
	}
	
	public HashMap<String, HashMap<String, Set<String>>> getValidRelRisks(){
		return configuredRelRisks;
	}

	public Set<String> getValidToNames(){
		return relRiskByTargetName.keySet();
	}

	public Set<String> getValidRelRiskFileNamesForFromName(String fromName){
		return relRiskBySourceName.get(fromName);
	}

	public Set<String> getValidRelRiskFileNamesForToName(String toName){
		return relRiskByTargetName.get(toName);
	}

	// Initialization.
	/**
	 * Find all useable relative risks irrespective of the current simulation
	 * configuration.
	 * @param  
	 * 
	 * @throws ConfigurationException
	 */
	public void findAllRelativeRisks(BaseNode selectedNode, TreeAsDropdownLists treelist)
			throws ConfigurationException {
		ParentNode referenceDataNode = FactoryCommon
				.findReferenceDataNode(selectedNode);
		Object[] refDataChildNodes = referenceDataNode.getChildren();
		//TreeAsDropdownLists tADL = TreeAsDropdownLists
			//	.getInstance(selectedNode);
		for (Object refDataChildNode : refDataChildNodes) {
			String validParentNodeName = returnParentNodeNameWhenValid((BaseNode) refDataChildNode);
			if (validParentNodeName != null) {
				if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel().equals(
						validParentNodeName)) {
					Set<String> validDiseases = treelist.getValidDiseases();
					Object[] diseaseChildren = ((ParentNode) refDataChildNode)
							.getChildren();
					for (Object diseaseChild : diseaseChildren) {
						String diseaseName = ((BaseNode) diseaseChild)
								.deriveNodeLabel();
						if (validDiseases.contains(diseaseName)) {
							Object[] diseaseSubDirChildren = ((ParentNode) diseaseChild)
									.getChildren();
							for (Object diseaseSubDirChild : diseaseSubDirChildren) {
								String diseasesSubDirName = ((BaseNode) diseaseSubDirChild)
										.deriveNodeLabel();
								if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
										.getNodeLabel().equals(
												diseasesSubDirName)) {
									Object[] relRiskFileChildren = ((ParentNode) diseaseSubDirChild)
											.getChildren();
									for (Object relRiskFileChild : relRiskFileChildren) {
										handleDiseaseRelRiskFileNode(
												diseaseName, relRiskFileChild);
									}
								} else {
									if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
											.getNodeLabel().equals(
													diseasesSubDirName)) {
										Object[] relRiskFileChildren = ((ParentNode) diseaseSubDirChild)
												.getChildren();
										for (Object relRiskFileChild : relRiskFileChildren) {
											handleDiseaseRelRiskFileNode(
													diseaseName,
													relRiskFileChild);
										}
									}
								}
							}
						}
					}
				} else {
					if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
							.equals(validParentNodeName)) {
						Set<String> validRiskFactors = treelist.getRiskFactors();
						Object[] riskFactorChildren = ((ParentNode) refDataChildNode)
								.getChildren();
						for (Object riskFactorChild : riskFactorChildren) {
							String riskFactorName = ((BaseNode) riskFactorChild)
									.deriveNodeLabel();
							if (validRiskFactors.contains(riskFactorName)) {
								Object[] riskFactorSubDirChildren = ((ParentNode) riskFactorChild)
										.getChildren();
								for (Object riskFactorSubDirChild : riskFactorSubDirChildren) {
									String riskFactorSubDirName = ((BaseNode) riskFactorSubDirChild)
											.deriveNodeLabel();
									if (StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR
											.getNodeLabel().equals(
													riskFactorSubDirName)) {
										Object[] relRiskFileChildren = ((ParentNode) riskFactorSubDirChild)
												.getChildren();
										for (Object relRiskFileChild : relRiskFileChildren) {
											handleRiskFactorRelRiskFileNode(
													riskFactorName, "Death",
													relRiskFileChild);
										}
									} else {
										if (StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR
												.getNodeLabel().equals(
														riskFactorSubDirName)) {
											Object[] relRiskFileChildren = ((ParentNode) riskFactorSubDirChild)
													.getChildren();
											for (Object relRiskFileChild : relRiskFileChildren) {
												handleRiskFactorRelRiskFileNode(
														riskFactorName,
														"Disability",
														relRiskFileChild);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void handleDiseaseRelRiskFileNode(String diseaseName,
			Object relRiskFileChild) throws DynamoConfigurationException {
		if (relRiskFileChild instanceof FileNode) {
			// File relRiskFile = ((FileNode) relRiskFileChild)
			// .getPhysicalStorage();
			// String rootElementName = ConfigurationFileUtil
			// .extractRootElementName(relRiskFile);
			String relRiskFileName = ((BaseNode) relRiskFileChild)
					.deriveNodeLabel();
			ParentNode relRiskSourceTypeNode = ((ChildNode) relRiskFileChild)
					.getParent();
			String relRiskSourceTypeNodeLabel = ((BaseNode) relRiskSourceTypeNode)
					.deriveNodeLabel();
			String relRiskSourceName = null;
			Set<String> relRiskNames = relRiskByTargetName.get(diseaseName);
			;
			log.debug("Getting disease relative-risks names for target-name: "
					+ diseaseName);
			// Disease has been checked above.
			log.debug("Checking disease relative-risks source: "
					+ relRiskFileName);
			boolean riskSourceValid = false;
			if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
					.getNodeLabel().equals(relRiskSourceTypeNodeLabel)) {
				relRiskSourceName = findSourceName(relRiskFileName, tADL
						.getValidDiseases());
				log.debug("Found relative risk source: " + relRiskSourceName);
				if (tADL.getValidDiseases().contains(relRiskSourceName)) {
					riskSourceValid = true;
				} else {
					log.debug("Relative risk source: " + relRiskSourceName
							+ " is not valid.");
				}
			} else {
				if (StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
						.getNodeLabel().equals(relRiskSourceTypeNodeLabel)) {
					relRiskSourceName = findSourceName(relRiskFileName, tADL
							.getRiskFactors());
					log.debug("Found relative risk source: "
							+ relRiskSourceName);
					if (tADL.getRiskFactors().contains(relRiskSourceName)) {
						riskSourceValid = true;
					} else {
						log.debug("Relative risk source: " + relRiskSourceName
								+ " is not valid.");
					}
				}
			}
			if (riskSourceValid) {
				if (relRiskNames == null) {
					relRiskNames = new LinkedHashSet<String>();
				}
				log.debug("Adding disease relative-risks name: "
						+ relRiskFileName);
				relRiskNames.add(relRiskFileName);
				relRiskByTargetName.put(relRiskSourceName, relRiskNames);
				if (relRiskSourceName != null) {
					relRiskNames = relRiskBySourceName.get(relRiskSourceName);
					log
							.debug("Getting disease relative-risks names for source-name: "
									+ relRiskSourceName);
					if (relRiskNames == null) {
						relRiskNames = new LinkedHashSet<String>();
					}
					log.debug("Adding disease relative-risks name: "
							+ relRiskFileName);
					relRiskNames.add(relRiskFileName);
					relRiskBySourceName.put(relRiskSourceName, relRiskNames);
					/* added by Hendriek: also fill configuredRelRisks */
					/* this reused relRiskFileName */
					
					// get the "to" data that belongs to thus RR
					
					HashMap<String,Set<String>> toCollection=new HashMap<String,Set<String>> ();
						if (configuredRelRisks.keySet().contains(relRiskSourceName)) configuredRelRisks.get(relRiskSourceName);
					if (toCollection.keySet().contains(diseaseName) )
							relRiskNames=toCollection.get(diseaseName);
					else relRiskNames = new LinkedHashSet<String>();
					relRiskNames=toCollection.get(relRiskSourceName);
					if (relRiskNames == null) {
						relRiskNames = new LinkedHashSet<String>();
					}
					relRiskNames.add(relRiskFileName);
					toCollection.put(diseaseName, relRiskNames);
					configuredRelRisks.put(relRiskSourceName, toCollection);
					
					/* end addition */
					
					
				}
			}
		} else {
			throw (new DynamoConfigurationException(
					"handleDiseaseRelRiskFileNode() got a "
							+ relRiskFileChild.getClass().getName()
							+ " instead of a FileNode."));
		}
	}

	private void handleRiskFactorRelRiskFileNode(String riskFactorName,
			String targetName, Object relRiskFileChild)
			throws DynamoConfigurationException {
		if (tADL.getRiskFactors().contains(riskFactorName)) {
			if (relRiskFileChild instanceof FileNode) {
				String relRiskName = ((BaseNode) relRiskFileChild)
						.deriveNodeLabel();
				String sourceName = riskFactorName;
				if (sourceName != null) {
					log
							.debug("Getting risk-factor relative-risks names for source-name: "
									+ sourceName);
					Set<String> relRiskNames = relRiskBySourceName
							.get(sourceName);
					if (relRiskNames == null) {
						relRiskNames = new LinkedHashSet<String>();
					}
					log.debug("Adding risk-factor relative-risks name: "
							+ relRiskName);
					relRiskNames.add(relRiskName);
					relRiskBySourceName.put(sourceName, relRiskNames);
				}
				log
						.debug("Getting risk-factor relative-risks names for target-name: "
								+ targetName);
				Set<String> relRiskNames = relRiskByTargetName.get(targetName);
				if (relRiskNames == null) {
					relRiskNames = new LinkedHashSet<String>();
				}
				log.debug("Adding risk-factor relative-risks name: "
						+ relRiskName);
				relRiskNames.add(relRiskName);
				relRiskByTargetName.put(targetName, relRiskNames);
				
				/* added by Hendriek: also fill configuredRelRisks */
				/* this reused relRiskFileName */
				
				HashMap<String,Set<String>> toCollection = new HashMap<String, Set<String> >();
				// get the "to" data that belongs to this RR
				if (relRiskBySourceName.containsKey(sourceName))		
				
			             toCollection=this.configuredRelRisks.get(sourceName);
				if (toCollection==null) toCollection=new HashMap<String, Set<String>>();
				if (toCollection.keySet().contains(targetName) )
						relRiskNames=toCollection.get(targetName);
				else relRiskNames = new LinkedHashSet<String>();
				
				relRiskNames.add(relRiskName);
				toCollection.put(targetName, relRiskNames);
				configuredRelRisks.put(sourceName, toCollection);
				
				/* end addition */
			} else {
				throw (new DynamoConfigurationException(
						"handleRiskFactorRelRiskFileNode() got a "
								+ relRiskFileChild.getClass().getName()
								+ " instead of a FileNode."));
			}
		} else {
			log.debug("Risk Factor: " + riskFactorName + " is invalid.");
		}
	}

	/**
	 * If the sourceLabel == null, the sourcelabel could not be found in the
	 * validNames list.
	 * 
	 * @param relRiskFileNodeLabel
	 * @param validNames
	 * @return
	 */
	private String findSourceName(String relRiskFileNodeLabel,
			Set<String> validNames) {
		String sourceLabel = null;
		for (String validName : validNames) {
			int index = relRiskFileNodeLabel.indexOf(validName);
			if (index != -1) {
				sourceLabel = validName;
				break;
			}
		}
		return sourceLabel;
	}

	/**
	 * If the validNodeName == null, the name is not in the
	 * possibleParentNodeNames list.
	 * 
	 * @param childNode
	 * @return validParentNodeName, null when invalid.
	 * @throws ConfigurationException
	 */
	static String returnParentNodeNameWhenValid(BaseNode childNode)
			throws ConfigurationException {
		String validNodeName = null;
		for (int count = 0; count < possibleParentNodeNames.length; count++) {
			if (possibleParentNodeNames[count].equals(childNode
					.deriveNodeLabel())) {
				validNodeName = possibleParentNodeNames[count];
				break;
			}
		}
		return validNodeName;
	}

	/**
	 * Make a collection of relative risks pertaining to the configured diseases
	 * and riskfactor, minus the already configured relative risks.
	 */

	/**
	 * Create possible risk sources. (RiskFactors plus Diseases)
	 */

	/**
	 * Create possible targets. (Diseases plus Death and Disability)
	 */

	/**
	 * Make the riskfactors accessible both by source-name and by target-name.
	 */
	/**
	 * Get
	 */

	// Dynamic behaviour.
	/**
	 * Add a relative risk to the configuration.
	 */
	/**
	 * Remove a relative risk from the configuration.
	 */
	/**
	 * Add a disease to the configuration.
	 */
	/**
	 * Remove a disease from the configuration.
	 */
	/**
	 * Add a riskfactor to the configuration.
	 */
	/**
	 * Remove a riskfactor from the configuration.
	 */
	/**
	 * 
	 */

	public void dump4Debug() {
		StringBuffer dumpBuffer = new StringBuffer(" \n");
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
		log.debug(dumpBuffer.toString());
	}
}
