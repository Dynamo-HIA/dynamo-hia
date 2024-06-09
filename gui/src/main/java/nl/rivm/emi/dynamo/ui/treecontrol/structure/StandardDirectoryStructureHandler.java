package nl.rivm.emi.dynamo.ui.treecontrol.structure;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.data.util.ConfigurationFileUtil;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mondeelr <br/>
 * 
 *         Class providing the support for the generation of standard ChildNodes
 *         (and their physical counterparts) for a newly created DirectoryNode
 *         in the Tree.<br/>
 *         For instance the "Reference_Data" and "Simulations" below the
 *         rootdirectory.
 */
public class StandardDirectoryStructureHandler {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardDirectoryStructureHandler");

	static private StructureCollection theCollection = null;

	/**
	 * Adds the nescessary ChildNodes (in practice Directory-nodes) to this
	 * DirectoryNode.
	 * 
	 * @param node
	 *            Node just added to the tree.
	 * @throws StorageTreeException
	 */
	synchronized static public void process(DirectoryNode node)
			throws StorageTreeException {
		if (theCollection == null) {
			theCollection = new StandardDirectoryStructureHandler.StructureCollection();
		}
		log.debug("Before processing.");
		theCollection.process(node);
	}

	/**
	 * @author mondeelr <br/>
	 *         Collection containing the standard structures present in the
	 *         application.
	 * 
	 */
	static class StructureCollection {
		Set<StructureElement> theElements = new HashSet<StructureElement>();

		/**
		 * Constructor creating the StandardStructures and putting them in the
		 * collection.
		 */
		public StructureCollection() {
			log.debug("Constructing StructureCollection");
			theElements.add(new BaseDirectoryStructure());
			theElements.add(new RefDataDirectoryStructure());
			theElements.add(new DiseaseDirectoryStructure());
			theElements.add(new RiskFactorDirectoryStructure());
			theElements
					.add(new CompoundRiskFactorDirectoryStructureExtension());
		}

		/**
		 * Delegates the addition to all possible standard-structures in turn.
		 * 
		 * @param node
		 *            Node just added to the tree.
		 * @throws StorageTreeException
		 */
		public void process(DirectoryNode node) throws StorageTreeException {
			log.debug("StructureCollection Processing: "
					+ node.getPhysicalStorage().getAbsolutePath());
			for (StructureElement element : theElements) {
				element.process(node);
			}
		}

		/**
		 * @author mondeelr<br/>
		 *         BaseClass for all possible StandardStructures.
		 * 
		 */
		static private abstract class StructureElement {

			Set<String> requiredNames;

			protected StructureElement() {
				super();
			}

			protected void setRequiredNames(Set<String> requiredNames) {
				this.requiredNames = requiredNames;
			}

			abstract public void process(DirectoryNode theNode)
					throws StorageTreeException;

			/**
			 * Creates directories with requirednames that are not yet present.<br/>
			 * DURATIONSDISTRIBUTIONDIRECTORY is created here later than the
			 * normal time.
			 * 
			 * @param node
			 *            Node just added to the tree.
			 * @throws StorageTreeException
			 */
			protected void checkAndCreateNames(DirectoryNode node)
					throws StorageTreeException {
				log.debug("StructureElement: Entering checkAndCreateNames()");
				File theDirectory = node.getPhysicalStorage();
				String[] dirArray = theDirectory.list();
				HashSet<String> presentNames = new HashSet<String>();
				if (dirArray != null) {
					for (int count = 0; count < dirArray.length; count++) {
						presentNames.add(dirArray[count]);
					}
				}
				for (String requiredName : requiredNames) {
					log.debug("StructureElement: Checking required name: "
							+ requiredName);
					if (presentNames.contains(requiredName.toLowerCase())) {
						continue;
					}
					File topRequiredDirectory = new File(theDirectory
							.getAbsolutePath()
							+ File.separator + requiredName);
					if (!topRequiredDirectory.exists()) {
						topRequiredDirectory.mkdir();
						// TODO(mondeelr) Dirty hack, but nescessary to see the
						// directory at once and not only after a restart.
						if (StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY
								.getNodeLabel().equals(requiredName)) {
							node.addChild((ChildNode) new DirectoryNode(node,
									topRequiredDirectory));
						}
						log.debug("StructureElement: Required directory: "
								+ topRequiredDirectory.getAbsolutePath()
								+ " added.");
					} else {
						log.debug("StructureElement: Required directory: "
								+ topRequiredDirectory.getAbsolutePath()
								+ " already present.");
					}
				}
			}
		}

		/**
		 * @author mondeelr<br/>
		 *         Standardstructure for the BaseDirectory.
		 * 
		 */
		static private class BaseDirectoryStructure extends StructureElement {

			public BaseDirectoryStructure() {
				super();
				// Sequence counts.
				Set<String> requiredNames = new LinkedHashSet<String>();
				requiredNames.add(StandardTreeNodeLabelsEnum.SIMULATIONS
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.REFERENCEDATA
						.getNodeLabel());
				super.setRequiredNames(requiredNames);
			}

			@Override
			public void process(DirectoryNode node) throws StorageTreeException {
				if (node.getParent() instanceof RootNode) {
					checkAndCreateNames(node);
				}
			}

		}

		/**
		 * @author mondeelr<br/>
		 *         Standardstructure for the Reference_Data directory.
		 * 
		 */
		static private class RefDataDirectoryStructure extends StructureElement {

			public RefDataDirectoryStructure() {
				super();
				Set<String> requiredNames = new LinkedHashSet<String>();
				requiredNames.add(StandardTreeNodeLabelsEnum.POPULATIONS
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.RISKFACTORS
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.DISEASES
						.getNodeLabel());
				super.setRequiredNames(requiredNames);
			}

			@Override
			public void process(DirectoryNode node) throws StorageTreeException {
				if (StandardTreeNodeLabelsEnum.REFERENCEDATA.getNodeLabel()
						.equals(node.getPhysicalStorage().getName())) {
					checkAndCreateNames(node);
				}
			}
		}

		/**
		 * @author mondeelr<br/>
		 *         Standardstructure for a Disease directory.
		 * 
		 */
		static private class DiseaseDirectoryStructure extends StructureElement {

			public DiseaseDirectoryStructure() {
				super();
				Set<String> requiredNames = new LinkedHashSet<String>();
				requiredNames.add(StandardTreeNodeLabelsEnum.PREVALENCES
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.INCIDENCES
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.DALYWEIGHTS
						.getNodeLabel());
				requiredNames
						.add(StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
								.getNodeLabel());
				requiredNames
						.add(StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES
								.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.EXCESSMORTALITIES
						.getNodeLabel());
				super.setRequiredNames(requiredNames);
			}

			@Override
			public void process(DirectoryNode node) throws StorageTreeException {
				ParentNode parentNode = node.getParent();
				if (parentNode instanceof DirectoryNode) {
					log
							.debug("DiseaseDirectoryStructure, checking parentName: "
									+ ((DirectoryNode) parentNode)
											.getPhysicalStorage().getName());
					if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()
							.equals(
									((DirectoryNode) parentNode)
											.getPhysicalStorage().getName())) {
						checkAndCreateNames(node);
					}
				}
			}
		}

		/**
		 * @author mondeelr<br/>
		 *         Standardstructure for a RiskFactor directory.
		 * 
		 */
		static private class RiskFactorDirectoryStructure extends
				StructureElement {

			public RiskFactorDirectoryStructure() {
				super();
				Set<String> requiredNames = new LinkedHashSet<String>();
				requiredNames.add(StandardTreeNodeLabelsEnum.PREVALENCES
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.TRANSITIONSDIR
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR
						.getNodeLabel());
				requiredNames
						.add(StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR
								.getNodeLabel());
				super.setRequiredNames(requiredNames);
			}

			@Override
			public void process(DirectoryNode node) throws StorageTreeException {
				ParentNode parentNode = node.getParent();
				if (parentNode instanceof DirectoryNode) {
					String parentNodeName = ((DirectoryNode) parentNode)
							.getPhysicalStorage().getName();
					log
							.debug("RiskFactorDirectoryStructure, checking parentName: "
									+ parentNodeName
									+ " against "
									+ StandardTreeNodeLabelsEnum.RISKFACTORS
											.getNodeLabel());
					if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
							.equals(parentNodeName)) {
						checkAndCreateNames(node);
					}
				}
			}
		}

		/**
		 * @author mondeelr<br/>
		 *         Standardstructure for a compound Risk Factor directory.
		 * 
		 */
		static private class CompoundRiskFactorDirectoryStructureExtension
				extends StructureElement {

			public CompoundRiskFactorDirectoryStructureExtension() {
				super();
				Set<String> requiredNames = new LinkedHashSet<String>();
				requiredNames
						.add(StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY
								.getNodeLabel());
				super.setRequiredNames(requiredNames);
			}

			@Override
			public void process(DirectoryNode node) throws StorageTreeException {
				ParentNode parentNode = node.getParent();
				if (parentNode instanceof DirectoryNode) {
					String parentName = ((DirectoryNode) parentNode)
							.getPhysicalStorage().getName();
					log
							.debug("CompoundRiskFactorDirectoryStructureExtension, checking parentName: "
									+ parentName
									+ " against "
									+ StandardTreeNodeLabelsEnum.RISKFACTORS
											.getNodeLabel());
					String rootElementName = ConfigurationFileUtil
							.exceptionFreeExtractRootElementNameFromChildConfiguration(node);
					log
							.debug("CompoundRiskFactorDirectoryStructureExtension, AND checking parentName: "
									+ rootElementName
									+ " against "
									+ RootElementNamesEnum.RISKFACTOR_COMPOUND
											.getNodeLabel());
					if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()
							.equals(parentName)
							&& RootElementNamesEnum.RISKFACTOR_COMPOUND
									.getNodeLabel().equals(rootElementName)) {
						checkAndCreateNames(node);
					}
				}
			}
		}
	}
}
