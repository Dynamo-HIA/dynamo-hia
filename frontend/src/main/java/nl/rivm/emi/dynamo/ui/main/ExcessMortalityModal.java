package nl.rivm.emi.dynamo.ui.main;

/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */

import java.io.File;
import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.data.types.atomic.ParameterType;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.dialogs.ExcessMortalityAddParameterTypeDialog;
import nl.rivm.emi.dynamo.ui.main.base.AbstractMultiRootChildDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.ExcessMortalityGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class ExcessMortalityModal extends AbstractMultiRootChildDataModal {
	// private static final String EXCESSMORTALITY = "excess_mortality";

	private Log log = LogFactory.getLog(this.getClass().getSimpleName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private ExcessMortalityObject modelObject;

	private boolean abortModalHandling = false;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 * @param parameterName
	 *            TODO
	 */
	public ExcessMortalityModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, String parameterName) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		if (!isImported()) {
			ExcessMortalityObject.ParameterTypeHelperClass.chosenParameterName = parameterName;
		} else {
			ExcessMortalityObject.ParameterTypeHelperClass.chosenParameterName = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#createCaption
	 * (nl.rivm.emi.dynamo.ui.treecontrol.BaseNode)
	 */
	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Excess mortality";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException,
			DynamoInconsistentDataException {
		// boolean doNotOpenModal = false;
		this.modelObject = (ExcessMortalityObject) manufactureModelObject();
		checkModelObjectForParameterTypeAndAddWhenNescessary();
		// Now check whether a new default Object has been created.
		if (!abortModalHandling) {
			// File dataFile = new File(this.dataFilePath);
			// if (!dataFile.exists()) {
			// updateModelObjectWithChosenParameterType(false);
			// }
			ExcessMortalityGroup excessMortalityGroup = new ExcessMortalityGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel);
			excessMortalityGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);

			this.shell.pack();
			// This is the first place this works.
			// this.shell.setSize(600, ModalStatics.defaultHeight);
			this.shell.setSize(920, ModalStatics.defaultHeight);
			this.shell.open();
		} else {
			shell.dispose();
		}
	}

	/**
	 * 
	 */
	private void updateModelObjectWithChosenParameterType(boolean zapOtherColumn) {
		if (ExcessMortalityObject.ParameterTypeHelperClass.CURED_FRACTION
				.equals(ExcessMortalityObject.ParameterTypeHelperClass.chosenParameterName)) {
			modelObject.insertParameterType(ParameterType.CURED_FRACTION,
					zapOtherColumn);
			changed = true;
		} else {
			if (ExcessMortalityObject.ParameterTypeHelperClass.ACUTELY_FATAL
					.equals(ExcessMortalityObject.ParameterTypeHelperClass.chosenParameterName)) {
				modelObject.insertParameterType(ParameterType.ACUTELY_FATAL,
						zapOtherColumn);
				changed = true;
			}
		}
	}

	@Override
	protected boolean ok2Run() {
		return !abortModalHandling;
	}

	/**
	 * 
	 */
	private void /* boolean */checkModelObjectForParameterTypeAndAddWhenNescessary() {
		// boolean doNotOpenModal = false;
		// if (ParameterTypeHelperClass.chosenParameterName == null) {
		// log.debug("chosenParameterType unavailable.");
		if (modelObject.hasNoParameterType()) {
			log.debug("modelObject with old format.");
			handleOldFormatModelObject();
		} else {
			log.debug("modelObject with parameterType ");
			File dataFile = new File(this.dataFilePath);
			if (!dataFile.exists()) {
				log.debug("Non existent new formay data file.");
				if (!isImported()) {
					log.debug("New configuration.");
					updateModelObjectWithChosenParameterType(false);
				} else {
					log.debug("Imported new format file, should be OK.");
				}
			} else {
				log.debug("Existing new format data file, should be OK.");

			}
		}
		// } else {
		// log.debug("chosenParameterType available.");
		// }
		// return doNotOpenModal;
	}

	/**
	 * 
	 */
	private void handleOldFormatModelObject() {
		// Old format Parameterfile has been opened / imported.
		log.debug("modelObject without parameterType ");
		if (Boolean.TRUE.equals(modelObject
				.getNotAllAcutelyFatalsAreZeroAtConstructionTime())) {
			if (Boolean.FALSE.equals(modelObject
					.getNotAllCuredFractionsAreZeroAtConstructionTime())) {
				log
						.debug("modelObject has only ACUTELY_FATAL nonzeroes, updating automagically. ");
				modelObject.insertParameterType(ParameterType.ACUTELY_FATAL,
						false);
			} else {
				// Both have non-zeroes.
				log
						.debug("modelObject has nonzeroes for both, start dilemma handling.");
				ExcessMortalityAddParameterTypeDialog dialog = new ExcessMortalityAddParameterTypeDialog(
						this.shell,
						"ExcessMortality configuration file",
						"It is not possible to have non-zero values in both\n"
								+ "\"Acutely Fatal\" and \"Cured Fraction\" columns.\n\n"
								+ "WARNING: There are non-zero values in both columns.\n"
								+ " these will be lost in the column you do not choose!!!!\n\n"
								+ "Please choose the column you want to use:");

				dialog.open();
				int returnCode = dialog.getReturnCode();
				log.debug("ReturnCode is: " + returnCode);
				if (returnCode == Window.OK) {
					log.debug("Dilemma: Updating modelObject with: "
							+ ExcessMortalityObject.ParameterTypeHelperClass.chosenParameterName);
					updateModelObjectWithChosenParameterType(true);
				} else {
					// doNotOpenModal = true;
					abortModalHandling = true;
				}
			}
		} else {
			if (Boolean.FALSE.equals(modelObject
					.getNotAllCuredFractionsAreZeroAtConstructionTime())) {
				// Both have only zeroes.
				log
						.debug("modelObject has nonzeroes for neither, start choice handling.");
				ExcessMortalityAddParameterTypeDialog dialog = new ExcessMortalityAddParameterTypeDialog(
						this.shell,
						"ExcessMortality configuration file",
						"It is not possible to have non-zero values in both\n"
								+ "\"Acutely Fatal\" and \"Cured Fraction\" columns.\n\n"
								+ "Please choose the column you want to use:");
				dialog.open();
				int returnCode = dialog.getReturnCode();
				log.debug("ReturnCode is: " + returnCode);
				if (returnCode == Window.OK) {
					log.debug("Choice: Updating modelObject with: "
							+ ExcessMortalityObject.ParameterTypeHelperClass.chosenParameterName);
					updateModelObjectWithChosenParameterType(false);
				} else {
					// doNotOpenModal = true;
					abortModalHandling = true;
				}
			} else {
				log
						.debug("modelObject has only CURED_FRACTION nonzeroes, updating automagically. ");
				modelObject.insertParameterType(ParameterType.CURED_FRACTION,
						false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractMultiRootChildDataModal#getData()
	 */
	@Override
	public Object getData() {
		return this.modelObject;
	}
}
