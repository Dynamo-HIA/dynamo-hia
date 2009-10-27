package nl.rivm.emi.dynamo.ui.actions;

/**
 * Develop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.main.DALYWeightsModal;
import nl.rivm.emi.dynamo.ui.main.DiseaseIncidencesModal;
import nl.rivm.emi.dynamo.ui.main.DiseasePrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.DurationDistributionModal;
import nl.rivm.emi.dynamo.ui.main.ExcessMortalityModal;
import nl.rivm.emi.dynamo.ui.main.NewbornsModal;
import nl.rivm.emi.dynamo.ui.main.OverallDALYWeightsModal;
import nl.rivm.emi.dynamo.ui.main.OverallMortalityModal;
import nl.rivm.emi.dynamo.ui.main.PopulationSizeModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDeathContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskForDisabilityContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromOtherDiseaseModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RelRiskFromRiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftNettoModal;
import nl.rivm.emi.dynamo.ui.main.TransitionMatrixModal;
import nl.rivm.emi.dynamo.ui.statusflags.FileCreationFlag;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.util.CompoundRiskFactorProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class NodeLessXMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String configurationFilePath;
	private String importFilePath;
	private String rootElementName;
	private boolean configurationFileExists;
	private NewbornsObject modelObject;
	/**
	 * Flag to make it possible to open a new Modal that has a "dirty"
	 * ModelObject. Defaults to false. Currently only in use for Newborns.
	 */
	private boolean modelObjectChangedButNotYetSaved = false;

	public NodeLessXMLFileAction(Shell shell, TreeViewer v,
			String configurationFilePath, String importFilePath,
			String rootElementName) {
		super(shell, v, null, rootElementName);
		this.configurationFilePath = configurationFilePath;
		this.importFilePath = importFilePath;
		this.rootElementName = rootElementName;
	}

	@Override
	public void run() {
		File configurationFile = new File(configurationFilePath);
		File importFile = new File(importFilePath);
		// Process the data and set the location of the file to be saved
		processThroughModal(importFile, configurationFile);
	}

	public void processThroughModal(File importFile, File savedFile) {
		try {
			FileCreationFlag.isOld = savedFile.exists();
			Runnable theModal = null;

			if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorCategoricalModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else {
				throw new DynamoConfigurationException("RootElementName "
						+ rootElementName + " not implemented yet.");
			}
			if (theModal != null) {
				Realm.runWithDefault(SWTObservables.getRealm(Display
						.getDefault()), theModal);
				boolean isPresentAfter = savedFile.exists();
				if (isPresentAfter && !FileCreationFlag.isOld) {
					((ParentNode) node).addChild((ChildNode) new FileNode(
							(ParentNode) node, savedFile));
					FileCreationFlag.isOld = true;
				}
				theViewer.refresh();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + savedFile.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}

	/**
	 * Method for checking the preconditions for making a new simulation
	 * configuration. When this check fails a messagebox indicating the error(s)
	 * is shown and false is returned.
	 * 
	 * @return true when creating a new simulation-configuration can go ahead.
	 *         false when no further action should be taken.
	 * @throws ConfigurationException
	 */
	private boolean simulationPreConditionsMet() throws ConfigurationException {
		boolean allTestsOK = true;
		StringBuffer errorMessage = new StringBuffer(
				"Not all preconditions have been met:\n");
		TreeAsDropdownLists instance = TreeAsDropdownLists.getInstance(node);
		instance.refresh(node);
		int numberOfPopulations = instance.getPopulations().size();
		if (numberOfPopulations == 0) {
			allTestsOK = false;
			errorMessage.append("No valid population was found.\n");
		}
		int numberOfDiseases = instance.getValidDiseaseNames().size();
		if (numberOfDiseases == 0) {
			allTestsOK = false;
			errorMessage.append("No valid disease was found.\n");
		}
		int numberOfRiskFactors = instance.getRiskFactorNames().size();
		if (numberOfRiskFactors == 0) {
			allTestsOK = false;
			errorMessage.append("No valid risk factor was found.\n");
		}
		if (!allTestsOK) {
			MessageBox errorMessageBox = new MessageBox(shell,
					SWT.ERROR_NULL_ARGUMENT);
			errorMessageBox.setMessage(errorMessage.toString());
			errorMessageBox.open();
		}
		return allTestsOK;
	}

	public void setConfigurationFileExists(boolean configurationFileExists) {
		this.configurationFileExists = configurationFileExists;
	}

	public void setModelObject(NewbornsObject modelObject) {
		this.modelObject = modelObject;
	}

	public void setModelObjectChangedButNotYetSaved(boolean flag) {
		this.modelObjectChangedButNotYetSaved = flag;
	}
}
