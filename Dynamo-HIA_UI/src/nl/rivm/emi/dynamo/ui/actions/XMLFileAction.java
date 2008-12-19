package nl.rivm.emi.dynamo.ui.actions;

/**
 * Develop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.main.DiseaseIncidencesModal;
import nl.rivm.emi.dynamo.ui.main.DiseasePrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.OverallDALYWeightsModal;
import nl.rivm.emi.dynamo.ui.main.OverallMortalityModal;
import nl.rivm.emi.dynamo.ui.main.PopulationSizeModal;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class XMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String fileNameTrunk;
	private String rootElementName;

	public XMLFileAction(Shell shell, TreeViewer v, BaseNode node,
			String fileNameTrunk, String rootElementName) {
		super(shell, v, node, rootElementName);
		this.fileNameTrunk = fileNameTrunk;
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		String filePath = "";
		if (node instanceof DirectoryNode) {
			filePath = node.getPhysicalStorage().getAbsolutePath()
					+ File.separator + fileNameTrunk + ".xml";
		} else {
			filePath = node.getPhysicalStorage().getAbsolutePath();
		}
		File file = new File(filePath);
		processThroughModal(file);
	}

	private void processThroughModal(File file) {
		try {
			boolean isOld = file.exists();
			Runnable theModal = null;
			if ("populationsize".equals(rootElementName)) {
				theModal = new PopulationSizeModal(shell, file
						.getAbsolutePath(), rootElementName, node);
			} else {
				if ("overallmortality".equals(rootElementName)) {
					theModal = new OverallMortalityModal(shell, file
							.getAbsolutePath(), rootElementName, node);
				} else {
					if ("overalldalyweights".equals(rootElementName)) {
						theModal = new OverallDALYWeightsModal(shell, file
								.getAbsolutePath(), rootElementName, node);
					} else {
						if ("diseaseincidences".equals(rootElementName)) {
							theModal = new DiseaseIncidencesModal(shell, file
									.getAbsolutePath(), rootElementName, node);
						} else {
							if ("diseaseprevalences".equals(rootElementName)) {
								theModal = new DiseasePrevalencesModal(shell,
										file.getAbsolutePath(),
										rootElementName, node);
							} else {
								if ("simulation".equals(rootElementName)) {
									theModal = new SimulationModal(shell, file
											.getAbsolutePath(),
											rootElementName, node);
								} else {
									throw new DynamoConfigurationException(
											"RootElementName "
													+ rootElementName
													+ " not implemented yet.");
								}
							}
						}
					}
				}
			}
			Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
					theModal);
			boolean isPresentAfter = file.exists();
			if (isPresentAfter && !isOld) {
				((ParentNode) node).addChild((ChildNode) new FileNode(
						(ParentNode) node, file));
			}
			theViewer.refresh();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + file.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}
}
