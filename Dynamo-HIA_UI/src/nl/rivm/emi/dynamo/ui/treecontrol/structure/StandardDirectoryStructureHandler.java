package nl.rivm.emi.dynamo.ui.treecontrol.structure;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StandardDirectoryStructureHandler {

	static private StructureCollection theCollection = null;

	synchronized static public void process(DirectoryNode node) {
		if (theCollection == null) {
			theCollection = new StandardDirectoryStructureHandler.StructureCollection();
		}
		theCollection.process(node);
	}

	static class StructureCollection {
		Log log = LogFactory.getLog(this.getClass().getName());
		Set<StructureElement> theElements = new HashSet<StructureElement>();

		public StructureCollection() {
			theElements.add(new BaseDirectoryStructure());
			theElements.add(new RefDataDirectoryStructure());
			theElements.add(new DiseaseDirectoryStructure());
			theElements.add(new RiskFactorDirectoryStructure());
		}

		public void process(DirectoryNode node) {
			log.debug("Processing: "
					+ node.getPhysicalStorage().getAbsolutePath());
			for (StructureElement element : theElements) {
				element.process(node);
			}
		}

		static private abstract class StructureElement {
			Log log = LogFactory.getLog(this.getClass().getName());
			Set<String> requiredNames;

			protected StructureElement() {
				super();
			}

			protected void setRequiredNames(Set<String> requiredNames) {
				this.requiredNames = requiredNames;
			}

			abstract public void process(DirectoryNode theNode);

			protected void checkAndCreateNames(DirectoryNode node) {
				File theDirectory = node.getPhysicalStorage();
				String[] dirArray = theDirectory.list();
				HashSet<String> presentNames = new HashSet<String>();
				if (dirArray != null) {
					for (int count = 0; count < dirArray.length; count++) {
						presentNames.add(dirArray[count]);
					}
				}
				for (String requiredName : requiredNames) {
					if (presentNames.contains(requiredName.toLowerCase())) {
						continue;
					}
					File topRequiredDirectory = new File(theDirectory
							.getAbsolutePath()
							+ File.separator + requiredName);
					topRequiredDirectory.mkdir();
				}
			}
		}

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
			public void process(DirectoryNode node) {
				if (node.getParent() instanceof RootNode) {
					checkAndCreateNames(node);
				}
			}

		}

		static private class RefDataDirectoryStructure extends StructureElement {

			public RefDataDirectoryStructure() {
				super();
				Set<String> requiredNames = new LinkedHashSet<String>();
				requiredNames.add(StandardTreeNodeLabelsEnum.POPULATIONS
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.RISKFACTORS
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel());
				super.setRequiredNames(requiredNames);
			}

			@Override
			public void process(DirectoryNode node) {
				if (StandardTreeNodeLabelsEnum.REFERENCEDATA.getNodeLabel().equals(
						node.getPhysicalStorage().getName())) {
					checkAndCreateNames(node);
				}
			}
		}

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
			public void process(DirectoryNode node) {
				ParentNode parentNode = node.getParent();
				if (parentNode instanceof DirectoryNode) {
					log.debug("Diseases check, parentName: "
							+ ((DirectoryNode) parentNode).getPhysicalStorage()
									.getName());
					if (StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel().equals(
							((DirectoryNode) parentNode).getPhysicalStorage()
									.getName())) {
						checkAndCreateNames(node);
					}
				}
			}
		}
		static private class RiskFactorDirectoryStructure extends StructureElement {

			public RiskFactorDirectoryStructure() {
				super();
				Set<String> requiredNames = new LinkedHashSet<String>();
				requiredNames.add(StandardTreeNodeLabelsEnum.PREVALENCES
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR
						.getNodeLabel());
				requiredNames.add(StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR
						.getNodeLabel());
				super.setRequiredNames(requiredNames);
			}

			@Override
		public void process(DirectoryNode node) {
			ParentNode parentNode = node.getParent();
			if (parentNode instanceof DirectoryNode) {
				log.debug("Risk factors check, parentName: "
						+ ((DirectoryNode) parentNode).getPhysicalStorage()
								.getName());
				if (StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel().equals(
						((DirectoryNode) parentNode).getPhysicalStorage()
								.getName())) {
					checkAndCreateNames(node);
				}
			}
		}
	}

	}
}
