package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
		}

		public void process(DirectoryNode node) {
			log.fatal("Processing: "
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
				requiredNames.add("Simulations");
				requiredNames.add("Reference Data");
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
				requiredNames.add("Populations");
				requiredNames.add("Risk Factors");
				requiredNames.add("Diseases");
				super.setRequiredNames(requiredNames);
			}

			@Override
			public void process(DirectoryNode node) {
				if ("Reference Data"
						.equals(node.getPhysicalStorage().getName())) {
					checkAndCreateNames(node);
				}
			}
		}

		static private class DiseaseDirectoryStructure extends StructureElement {

			public DiseaseDirectoryStructure() {
				super();
				Set<String> requiredNames = new LinkedHashSet<String>();
				requiredNames.add("Prevalences");
				requiredNames.add("Incidences");
				requiredNames.add("Relative Risks");
				requiredNames.add("DALY Weights");
				super.setRequiredNames(requiredNames);
			}

			@Override
			public void process(DirectoryNode node) {
				ParentNode parentNode = node.getParent();
				if (parentNode instanceof DirectoryNode) {
					log.fatal("Diseases check, parentName: "
							+ ((DirectoryNode) parentNode).getPhysicalStorage()
									.getName());
					if ("Diseases".equals(((DirectoryNode) parentNode)
							.getPhysicalStorage().getName())) {
						checkAndCreateNames(node);
					}
				}
			}
		}
	}
}
