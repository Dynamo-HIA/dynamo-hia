package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.ChildNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
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
import nl.rivm.emi.dynamo.ui.main.RelativeRisksDiseaseOnDiseaseModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCategoricalPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorCompoundModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousModal;
import nl.rivm.emi.dynamo.ui.main.RiskFactorContinuousPrevalencesModal;
import nl.rivm.emi.dynamo.ui.main.SimulationModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftModal;
import nl.rivm.emi.dynamo.ui.main.TransitionDriftNettoModal;
import nl.rivm.emi.dynamo.ui.main.TransitionMatrixModal;
import nl.rivm.emi.dynamo.ui.main.parameters.AlphaAbilityModal;
import nl.rivm.emi.dynamo.ui.main.parameters.AlphasModal;
import nl.rivm.emi.dynamo.ui.main.parameters.AlphasOtherMortalityModal;
import nl.rivm.emi.dynamo.ui.main.parameters.AttributableMortalitiesModal;
import nl.rivm.emi.dynamo.ui.main.parameters.BaselineAbilityModal;
import nl.rivm.emi.dynamo.ui.main.parameters.BaselineFatalIncidencesModal;
import nl.rivm.emi.dynamo.ui.main.parameters.BaselineIncidencesModal;
import nl.rivm.emi.dynamo.ui.main.parameters.BaselineOtherMortalitiesModal;
import nl.rivm.emi.dynamo.ui.main.parameters.RelRiskForAbilityBeginModal;
import nl.rivm.emi.dynamo.ui.main.parameters.RelRiskForAbilityContinuousModal;
import nl.rivm.emi.dynamo.ui.main.parameters.RelRiskForAbilityEndModal;
import nl.rivm.emi.dynamo.ui.main.parameters.RelativeRisksModal;
import nl.rivm.emi.dynamo.ui.statusflags.FileCreationFlag;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.util.CategoricalRiskFactorProperties;
import nl.rivm.emi.dynamo.ui.util.CompoundRiskFactorProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
//ND: Use DisplayRealm instead of SWTObservables
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Action that initiates the editing/viewing of an existing XML-file.
 * 
 */

public class XMLFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String fileNameTrunk;
	private String rootElementName;
	private boolean configurationFileExists;
	private NewbornsObject modelObject;
	/**
	 * Flag to make it possible to open a new Modal that has a "dirty"
	 * ModelObject. Defaults to false. Currently only in use for Newborns.
	 */
	private boolean modelObjectChangedButNotYetSaved = false;

	/**
	 * Importing a file for a new (unsaved) categorical or compound relative
	 * risk from the modal screen blew up. Workaround.
	 */
	RiskSourceProperties props = null;

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            The Shell in which the Action must open the windows it may
	 *            create.
	 * @param v
	 *            The TreeViewer that contains the Menu the Action is put in.
	 * @param node
	 *            The BaseNode that was right clicked on.
	 * @param fileNameTrunk
	 *            The filename without the extension of the file that is going
	 *            to be edited/viewed.
	 * @param rootElementName
	 *            THe rootelementname contained in the file to be edited.
	 */
	public XMLFileAction(Shell shell, TreeViewer v, BaseNode node,
			String fileNameTrunk, String rootElementName) {
		super(shell, v, node, rootElementName);
		this.fileNameTrunk = fileNameTrunk;
		this.rootElementName = rootElementName;
	}

	public void setProps(RiskSourceProperties props) {
		this.props = props;
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
		File savedFile = new File(filePath);

		// Process the data and set the location of the file to be saved
		processThroughModal(savedFile, savedFile);
	}

	public void processThroughModal(File importFile, File savedFile) {
		try {
			FileCreationFlag.isOld = savedFile.exists();
			Runnable theModal = null;
/*
 * september 2012: terwille van de leesbaarheid alle { tussen else en if weggehaald (HB)
 * 
 * 
 */
			if (RootElementNamesEnum.POPULATIONSIZE.getNodeLabel().equals(
					rootElementName)) {
				theModal = new PopulationSizeModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.OVERALLMORTALITY.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new OverallMortalityModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.NEWBORNS.getNodeLabel().equals(
					rootElementName)) {
				theModal = new NewbornsModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node, this.modelObject);
				if (modelObjectChangedButNotYetSaved) {
					((NewbornsModal) theModal).setChanged(true);
				}
			} else if (RootElementNamesEnum.OVERALLDALYWEIGHTS.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new OverallDALYWeightsModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.DISEASEINCIDENCES.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new DiseaseIncidencesModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.DISEASEPREVALENCES.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new DiseasePrevalencesModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.DALYWEIGHTS.getNodeLabel()
					.equalsIgnoreCase(rootElementName)) {
				theModal = new DALYWeightsModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel()
					.equalsIgnoreCase(rootElementName)) {
				theModal = new ExcessMortalityModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node, null);
			} else

			if (RootElementNamesEnum.RELATIVERISKSFROMDISEASE.getNodeLabel()
					.equalsIgnoreCase(rootElementName)) {
				theModal = new RelRiskFromOtherDiseaseModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node, null);
			} else if (RootElementNamesEnum.SIMULATION.getNodeLabel().equals(
					rootElementName)) {
				if (simulationPreConditionsMet()) {
					theModal = new SimulationModal(shell, importFile
							.getAbsolutePath(), savedFile.getAbsolutePath(),
							rootElementName, node, this.configurationFileExists);
				}
			} else
			// RiskFactorConfigurations.
			if (RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel()
					.equals(rootElementName)) {
				// Passed
				// numberOfCategories is
				// not used here, it is
				// derived from the
				// contents of the file.
				theModal = new RiskFactorCategoricalModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node, -1);
			} else if (RootElementNamesEnum.RISKFACTOR_CONTINUOUS
					.getNodeLabel().equals(rootElementName)) {
				// Here the
				// selectedNumberOfCutoffs
				// is not
				// nescessary, it
				// will be
				// determined from
				// the file.
				theModal = new RiskFactorContinuousModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node, -1);
			} else if (RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RiskFactorCompoundModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node, -1, -1, theViewer);
			} else if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorCategoricalModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node,
						(CategoricalRiskFactorProperties) /* null */props);
			} else if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_COMPOUND
					.getNodeLabel().equals(rootElementName)) {
				if (props == null) {
					props = RiskSourcePropertiesMapFactory
							.getProperties((FileNode) node);
				}
				theModal = new RelRiskFromRiskFactorCompoundModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node,
						(CompoundRiskFactorProperties) props);
			} else if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorContinuousModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RiskFactorCompoundModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node, -1, -1, theViewer);
			} else if (RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RiskFactorCategoricalPrevalencesModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
			} else if (RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RiskFactorContinuousPrevalencesModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
			} else if (RootElementNamesEnum.RISKFACTORPREVALENCES_DURATION
					.getNodeLabel().equals(rootElementName)) {
				theModal = new DurationDistributionModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskForDeathCategoricalModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskForDeathContinuousModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND
					.getNodeLabel().equals(rootElementName)) {

				theModal = new RelRiskForDeathCompoundModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CATEGORICAL
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskForDisabilityCategoricalModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CONTINUOUS
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskForDisabilityContinuousModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKSFORDISABILITY_COMPOUND
					.getNodeLabel().equals(rootElementName)) {

				theModal = new RelRiskForDisabilityCompoundModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
			} else if (RootElementNamesEnum.TRANSITIONMATRIX.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new TransitionMatrixModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new TransitionDriftModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.TRANSITIONDRIFT_NETTO
					.getNodeLabel().equals(rootElementName)) {
				theModal = new TransitionDriftNettoModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} /*
			 * Estimated parameters
			 */
			else if (RootElementNamesEnum.ATTRIBUTABLEMORTALITIES.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new AttributableMortalitiesModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.BASELINEFATALINCIDENCES
					.getNodeLabel().equals(rootElementName)) {
				theModal = new BaselineFatalIncidencesModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.BASELINEINCIDENCES.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new BaselineIncidencesModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.BASELINEOTHERMORTALITIES
					.getNodeLabel().equals(rootElementName)) {
				theModal = new BaselineOtherMortalitiesModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKS.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RelativeRisksModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKSCLUSTER.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RelativeRisksDiseaseOnDiseaseModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL4P
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorCategoricalModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.RELATIVERISKS_OTHERMORT_CATEGORICAL
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorCategoricalModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_COMPOUND4P
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorCompoundModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else 	if (RootElementNamesEnum.RELATIVERISKS_OTHERMORT_CONTINUOUS
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorContinuousModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS4P
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorContinuousModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.ALFAS.getNodeLabel().equals(
					rootElementName)) {
				theModal = new AlphasModal(shell, importFile.getAbsolutePath(),
						savedFile.getAbsolutePath(), rootElementName, node);
			} else if (RootElementNamesEnum.ALPHASOTHERMORTALITY.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new AlphasOtherMortalityModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RELATIVERISKS_OTHERMORT_BEGIN
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorContinuousModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.RELATIVERISKS_OTHERMORT_END
					.getNodeLabel().equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorContinuousModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.RELATIVERISKS_BEGIN.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorContinuousModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.RELATIVERISKS_END.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorContinuousModal(shell,
						importFile.getAbsolutePath(), savedFile
								.getAbsolutePath(), rootElementName, node, null);
			} else if (RootElementNamesEnum.BASELINE_ABILITY.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new BaselineAbilityModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RR_RISKFACTOR_ABILITY_END.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RelRiskForAbilityEndModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RR_RISKFACTOR_ABILITY_BEGIN.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RelRiskForAbilityBeginModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RR_RISKFACTOR_ABILITY_ALPHA.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new AlphaAbilityModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RR_RISKFACTOR_ABILITY_CONT.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RelRiskForAbilityContinuousModal(shell, importFile
						.getAbsolutePath(), savedFile.getAbsolutePath(),
						rootElementName, node);
			} else if (RootElementNamesEnum.RR_RISKFACTOR_ABILITY_CAT.getNodeLabel()
					.equals(rootElementName)) {
				theModal = new RelRiskFromRiskFactorCategoricalModal(shell,
						importFile.getAbsolutePath(), savedFile
						.getAbsolutePath(), rootElementName, node, null);
				
			} else {
				throw new DynamoConfigurationException("RootElementName "
						+ rootElementName + " not implemented yet.");

			}

			if (theModal != null) {
				Realm.runWithDefault(DisplayRealm.getRealm(Display
						.getDefault()), theModal);
				boolean isPresentAfter = savedFile.exists();
				if (isPresentAfter && !FileCreationFlag.isOld) {
					((ParentNode) node).addChild((ChildNode) new FileNode(
							(ParentNode) node, savedFile));
					FileCreationFlag.isOld = true;
				}
				theViewer.refresh();
				// 20091029 Added.
				if (node instanceof DirectoryNode) {
					((DirectoryNode) node).updateStandardStructure();
					theViewer.refresh();
				}
				// ~ 20091029
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
