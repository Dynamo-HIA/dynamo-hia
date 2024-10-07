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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.CategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.main.base.AbstractMultiRootChildDataModal;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.panels.NewbornsDialog;
import nl.rivm.emi.dynamo.ui.panels.NewbornsGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class NewbornsModal extends AbstractMultiRootChildDataModal {
	private Log log = LogFactory.getLog(this.getClass().getName());

	private NewbornsObject modelObject;
	/**
	 * Flag that indicates a refresh of the screen is needed because the data
	 * has been updated
	 */
	public boolean modalIsDirty = false;

	/**
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public NewbornsModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, NewbornsObject modelObject) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		this.modelObject = modelObject;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Newborns";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Opens the modal screen
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#open()
	 */
	@Override
	public synchronized void openModal() throws ConfigurationException, DynamoInconsistentDataException {
			// If the modelObject != null, then the Update button has been used
			if (this.modelObject == null) {
				this.modelObject = new NewbornsObject(manufactureModelObject());
			}
			NewbornsGroup newbornsGroup = new NewbornsGroup(this.shell,
					this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel, this);
			newbornsGroup.setFormData(this.helpPanel.getGroup(), buttonPanel);
			this.shell.pack();
			// This is the first place this works.
//			this.shell.setSize(500, ModalStatics.defaultHeight);
			this.shell.setSize(575, ModalStatics.defaultHeight);
			this.shell.open();
	}

	/**
	 * This method constructs a model-object always containing Observables at
	 * the deepest level because these are needed for the databinding to work.
	 * 
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	protected LinkedHashMap<String, Object> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> producedData = null;
		AgnosticGroupFactory factory = (AgnosticGroupFactory) FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory instanceof CategoricalFactory) {

		}
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: "
							+ this.rootElementName);
		}
		File dataFile = new File(this.dataFilePath);

		if (dataFile.exists()) {
			// The configuration file with data already exists, fill the modal
			// with existing data
			if (dataFile.isFile() && dataFile.canRead()) {
				producedData = factory.manufactureObservable(dataFile,
						this.rootElementName);
				if (producedData == null) {
					throw new ConfigurationException(
							"DataModel could not be constructed.");
				}
			} else {
				// No file has been selected, continue without exceptions
				throw new ConfigurationException(this.dataFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			Integer maxYear = new Integer(calendar.get(Calendar.YEAR) + 29);
			// Set the limit to 30 years from the current year
			factory.setIndexLimit(maxYear);
			// The configuration file with data does not yet exist, create a new
			// screen object with default data
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

	@Override
	public Object getData() {
		return this.modelObject;
	}

	@Override
	/*
	 * Own implementation.
	 */
	public SideEffectProcessor getSavePreProcessor() {
		return new SavePreProcessor();
	}

	public class SavePreProcessor implements SideEffectProcessor {

		synchronized public boolean doIt() {
			boolean doSave = true;
			Integer startingYear = modelObject.getStartingYear();
			int startingYearInt = startingYear.intValue();
			int numberOfAmounts = modelObject.getNumberOfAmounts();
			int lastNonZeroIndex = -1;
			int lastNonZeroValue = -1;
			// Scan for non-zero values.
			for (int yearCounter = startingYearInt; yearCounter < startingYearInt
					+ numberOfAmounts; yearCounter++) {
				log.info("Non-zero scan, yearCounter: " + yearCounter);
				Integer currentNumber = modelObject.getNumber(yearCounter);
				if (currentNumber != null) {
					int currentNumberInt = currentNumber.intValue();
					if (currentNumberInt != 0) {
						lastNonZeroIndex = yearCounter;
						lastNonZeroValue = currentNumberInt;
					}
				} else {
					modelObject.updateNumber(yearCounter, new Integer(0));
				}
			}
			// Functionality relocated from save-selectionListener.
			try {
				if (((NewbornsObject) modelObject).isContainsPostfixZeros()) {
					Dialog dialog = new NewbornsDialog(
							getShell(),
							"Number values with 0 still exist for the final years. If you save, all of these 0 values will be replaced with the value of the last year that does contain a non zero value. Do you want it to be saved with these replacements?");
					dialog.open();
					if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
						if ((lastNonZeroIndex != -1)
								&& (lastNonZeroIndex < startingYearInt
										+ numberOfAmounts - 1))
						// Trailing zeroes present.
						{
							for (int yearCounter = startingYearInt
									+ numberOfAmounts - 1; yearCounter > lastNonZeroIndex; yearCounter--) {
								modelObject.updateNumber(yearCounter,
										lastNonZeroValue);
							}
						} else {
							MessageBox alertBox = new MessageBox(getShell(),
									SWT.ERROR_CANNOT_BE_ZERO);
							alertBox
									.setText("Postfix says there are zeroes, but there aren't.");
						}
					} else {
						doSave = false;
					}
				}
			} catch (DynamoConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (((NewbornsObject) modelObject).isContainsZeros()) {
				Dialog dialog = new NewbornsDialog(getShell(),
						"Number values with 0 still exist. Do you want this to be saved?");
				dialog.open();
				if (dialog.getReturnCode() != IDialogConstants.OK_ID) {
					doSave = false;
				}
			}
			return doSave;
		}
	}
}
