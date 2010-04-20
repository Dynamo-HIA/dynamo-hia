package nl.rivm.emi.dynamo.ui.panels.simulation.listeners;

import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskDropDownPanel;
import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskTab;
import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskTabDataManager;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Widget;

/**
 * ModifyListener that is hooked up to all three dropdown-boxes in the active
 * relative risk tab.
 * 
 * @author mondeelr
 * 
 */
public class RelativeRiskComboModifyListener implements ModifyListener {

	private Log log = LogFactory.getLog(this.getClass().getSimpleName());

	private RelativeRiskTab relativeRiskTab;
	private RelativeRiskTabDataManager dataManager;
	private TabRelativeRiskConfigurationData myConfiguration = null;
	private boolean configurationPreloaded = false;
	private HelpGroup helpGroup;

	/**
	 * @param relativeRiskTab
	 *            - The tab the listener is working for.
	 * @param helpGroup
	 */
	public RelativeRiskComboModifyListener(RelativeRiskTab relativeRiskTab,
			HelpGroup helpGroup) {
		super();
		this.relativeRiskTab = relativeRiskTab;
		this.dataManager = relativeRiskTab.getDynamoTabDataManager();
		this.helpGroup = helpGroup;
	}

	public void initialize(TabRelativeRiskConfigurationData configuration) {
		Combo currentCombo = null;
		try {
			if (!configurationPreloaded) {
				configurationPreloaded = true;
				synchronized (this) {
					if (configuration != null) {
						currentCombo = setOrUpdateConfiguration(configuration);
					} else {
						// Create the initial configuration from scratch.
						DropDownPropertiesSet fromSet = null;
						int defaultFromIndex = 0;
						String defaultFrom = null;
						DropDownPropertiesSet toSet = null;
						int defaultToIndex = 0;
						String defaultTo = null;
						DropDownPropertiesSet fileNameSet = null;
						Combo fromCombo = dataManager
								.findComboObject(RelativeRiskDropDownPanel.FROM);
						Combo toCombo = null;
						Combo fileCombo = null;
						if (fromCombo != null) {
							fromSet = dataManager.getFromSet();
							defaultFrom = fromSet
									.getSelectedString(defaultFromIndex);
						} else {
							throw new Exception(
									"State-error: From-combo has not been initialized yet.");
						}
						toCombo = dataManager
								.findComboObject(RelativeRiskDropDownPanel.TO);
						if (toCombo != null) {
							toSet = dataManager.getToSet(defaultFrom);
							defaultTo = toSet.getSelectedString(defaultToIndex);
						} else {
							throw new Exception(
									"State-error: To-combo has not been initialized yet.");
						}
						fileCombo = dataManager
								.findComboObject(RelativeRiskDropDownPanel.RELATIVE_RISK);
						if (fileCombo != null) {
							fileNameSet = dataManager.getFileSet(defaultFrom,
									defaultTo);
							int defaultFileNameIndex = 0;
							String defaultFileName = fileNameSet
									.getSelectedString(defaultFileNameIndex);
							// The combo's are there, but do we have something
							// in the dropdowns?
							int fromSetSize = fromSet.size();
							int toSetSize = toSet.size();
							int fileNameSetSize = fileNameSet.size();
							if ((fromSetSize != 0) && (toSetSize != 0)
									&& (fileNameSetSize != 0)) {
								for (int count = 0; count < fromSetSize; count++) {
									String fromSetItem = fromSet
											.getSelectedString(count);
									fromCombo.add(fromSetItem, count);
								}
								selectSilent(fromCombo, 0);
								for (int count = 0; count < toSetSize; count++) {
									String toSetItem = toSet
											.getSelectedString(count);
									toCombo.add(toSetItem, count);
								}
								selectSilent(toCombo, 0);
								for (int count = 0; count < fileNameSetSize; count++) {
									String fileNameSetItem = fileNameSet
											.getSelectedString(count);
									fileCombo.add(fileNameSetItem, count);
								}
								selectSilent(fileCombo, defaultFileNameIndex);
								myConfiguration = new TabRelativeRiskConfigurationData();
								myConfiguration.setIndex(relativeRiskTab
										.getTabIndex());
								// The index is automagically updated upstair
								// for the DynamoSimulationObject.
								myConfiguration.setFrom(defaultFrom);
								dataManager.setConfiguredFrom(defaultFrom);
								myConfiguration.setTo(defaultTo);
								dataManager.setConfiguredTo(defaultTo);
								myConfiguration
										.setDataFileName(defaultFileName);
								dataManager
										.setConfiguredFileName(defaultFileName);
								// 20090918
								// dataManager.refreshAvaillableRRlist();
							} else {
								throw new Exception(
										"At least one dropdown was empty.");
							}
							selectSilent(fileCombo, defaultFileNameIndex);
							myConfiguration = new TabRelativeRiskConfigurationData();
							myConfiguration.setIndex(relativeRiskTab
									.getTabIndex());
							// The index is automagically updated upstair
							// for the DynamoSimulationObject.
							myConfiguration.setFrom(defaultFrom);
							dataManager.setConfiguredFrom(defaultFrom);
							myConfiguration.setTo(defaultTo);
							dataManager.setConfiguredTo(defaultTo);
							myConfiguration.setDataFileName(defaultFileName);
							dataManager.setConfiguredFileName(defaultFileName);
							// 20090918
							// dataManager.refreshAvaillableRRlist();
						} else {
						throw new Exception(
								"State-error: File-combo has not been initialized yet.");
					}

				}
				relativeRiskTab.redraw();
			}
			 } else {
			 log
			 .error("Error: Should not load configuration more than once.");
			 }
		} catch (Exception e) {
			handleErrorMessage(e, currentCombo);
		}
	}

	private Combo setOrUpdateConfiguration(
			TabRelativeRiskConfigurationData configuration)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException, Exception {
		log.debug("Entering setOrUpdateConfiguration()");
		/**
		 * Flag to signal that there have been changes from the loaded values
		 * inside this method. These changes will without intervention not be
		 * reflected in the simulation configuration due to the selectSilent's
		 * used.
		 */
		boolean internalCorrection = false;
		Combo currentCombo;
		myConfiguration = configuration;
		currentCombo = dataManager
				.findComboObject(RelativeRiskDropDownPanel.FROM);
		if (currentCombo != null) {
			DropDownPropertiesSet fromSet = dataManager.getFromSet();
			String loadedFrom = myConfiguration.getFrom();
			int loadedFromIndex = fromSet.getSelectedIndex(loadedFrom);
			log.debug("Processing loadedFrom: " + loadedFrom + " at index: "
					+ loadedFromIndex);
			if (!fromSet.contains(loadedFrom)) {
				handleWarningMessage("Invalid from: " + loadedFrom
						+ " changing it to: " + fromSet.getSelectedString(0),
						currentCombo);
				internalCorrection = true;
			}
			currentCombo.removeAll();
						int fromSetSize = fromSet.size();
			for (int count = 0; count < fromSetSize; count++) {
				String fromSetItem = fromSet.getSelectedString(count);
				currentCombo.add(fromSetItem, count);
			}
			if (!internalCorrection) {
				selectSilent(currentCombo, loadedFromIndex);
			} else {
				currentCombo.select(loadedFromIndex);
				return currentCombo;
			}
		} else {
			throw new Exception(
					"State-error: From-combo has not been initialized yet.");
		}
		currentCombo = dataManager
				.findComboObject(RelativeRiskDropDownPanel.TO);
		if (currentCombo != null) {
			DropDownPropertiesSet toSet = dataManager.getToSet(myConfiguration
					.getFrom());
			String loadedTo = myConfiguration.getTo();
			int loadedToIndex = toSet.getSelectedIndex(loadedTo);
			log.debug("Processing loadedTo: " + loadedTo + " at index: "
					+ loadedToIndex);
			if (!toSet.contains(loadedTo)) {
				handleWarningMessage("Invalid to: " + loadedTo
						+ " changing it to: " + toSet.getSelectedString(0),
						currentCombo);
				internalCorrection = true;
			}
			currentCombo.removeAll();
			int toSetSize = toSet.size();
			for (int count = 0; count < toSetSize; count++) {
				String toSetItem = toSet.getSelectedString(count);
				currentCombo.add(toSetItem, count);
			}
			if (!internalCorrection) {
				selectSilent(currentCombo, loadedToIndex);
			} else {
				currentCombo.select(loadedToIndex);
				return currentCombo;
			}
		} else {
			throw new Exception(
					"State-error: To-combo has not been initialized yet.");
		}
		currentCombo = dataManager
				.findComboObject(RelativeRiskDropDownPanel.RELATIVE_RISK);
		if (currentCombo != null) {
			DropDownPropertiesSet fileNameSet = dataManager.getFileSet(
					myConfiguration.getFrom(), myConfiguration.getTo());
			String loadedFileName = myConfiguration.getDataFileName();
			int loadedFileNameIndex = fileNameSet
					.getSelectedIndex(loadedFileName);
			log.debug("Processing loadedFileName: " + loadedFileName
					+ " at index: " + loadedFileNameIndex);
			if (!fileNameSet.contains(loadedFileName)) {
				handleWarningMessage("Invalid filename: " + loadedFileName
						+ " changing it to: "
						+ fileNameSet.getSelectedString(0), currentCombo);
				internalCorrection = true;
			}
			currentCombo.removeAll();
			int fileNameSetSize = fileNameSet.size();
			for (int count = 0; count < fileNameSetSize; count++) {
				String fileNameSetItem = fileNameSet.getSelectedString(count);
				currentCombo.add(fileNameSetItem, count);
			}
			if (!internalCorrection) {
				selectSilent(currentCombo, loadedFileNameIndex);
			} else {
				currentCombo.select(loadedFileNameIndex);
				return currentCombo;
			}
		} else {
			throw new Exception(
					"State-error: From-combo has not been initialized yet.");
		}
		return currentCombo;
	}

	public void selectSilent(Combo combo, Integer index) {
		combo.removeModifyListener(this);
		combo.select(index);
		combo.addModifyListener(this);
	}

	/**
	 * 
	 */
	public void modifyText(ModifyEvent event) {
		synchronized (this) {
			Combo myCombo = (Combo) event.widget;
			int selectionIndex = myCombo.getSelectionIndex();
			if (selectionIndex != -1) {
				String comboLabel = dataManager.findComboLabel(myCombo);
				log.debug("modifyText from combo with label: " + comboLabel
						+ " selectionindex: " + selectionIndex);
				if (comboLabel != null) {
					String newText = myCombo.getText();
					Composite parent = myCombo.getParent();
					log.debug("newText: " + newText);
					try {
						if (RelativeRiskDropDownPanel.FROM.equals(comboLabel)) {
							updateFromFromDown(myCombo, newText);
						} else {
							if (RelativeRiskDropDownPanel.TO.equals(comboLabel)) {
								updateFromToDown(myCombo, newText);
							} else {
								if (RelativeRiskDropDownPanel.RELATIVE_RISK
										.equals(comboLabel)) {
									updateFile(newText);
								}
							}
						}
					} catch (Exception e) {
						handleErrorMessage(e, myCombo);
					}
				}
			}
		}
	}

	private void updateFromFromDown(Combo fromCombo, String newText)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		if ((newText != null)
				&& (!newText.equalsIgnoreCase(myConfiguration.getFrom()))) {
			helpGroup.getTheModal().setChanged(true);
			dataManager.setConfiguredFrom(newText);
			myConfiguration.setFrom(newText);
			// Something sensible has changed.
			Combo toCombo = dataManager
					.findComboObject(RelativeRiskDropDownPanel.TO);
			// From text changed, update.
			if (toCombo != null) {
				// Not constructing.
				DropDownPropertiesSet toSet = dataManager.getToSet(newText);
				if (toSet != null) {
					toCombo.removeAll();
					int toSetSize = toSet.size();
					for (int count = 0; count < toSetSize; count++) {
						String toSetItem = toSet.getSelectedString(count);
						toCombo.add(toSetItem, count);
					}
					selectSilent(toCombo, 0);
					String currentTo = toSet.getSelectedString(0);
					dataManager.setConfiguredTo(currentTo);
					myConfiguration.setTo(currentTo);
					DropDownPropertiesSet fileSet = dataManager.getFileSet(
							newText, currentTo);
					if (fileSet != null) {
						Combo fileCombo = dataManager
								.findComboObject(RelativeRiskDropDownPanel.RELATIVE_RISK);
						if (fileCombo != null) {
							fileCombo.removeAll();
							int fileSetSize = fileSet.size();
							for (int count = 0; count < fileSetSize; count++) {
								String fileSetItem = fileSet
										.getSelectedString(count);
								fileCombo.add(fileSetItem, count);
							}
							selectSilent(fileCombo, 0);
							String currentFileName = fileSet
									.getSelectedString(0);
							dataManager.setConfiguredFileName(currentFileName);
							myConfiguration.setDataFileName(currentFileName);
						}
					} else {
						// //////
					}
				}
			}
		}
	}

	private void updateFromToDown(Combo toCombo, String newText)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		if ((newText != null)
				&& (!newText.equalsIgnoreCase(myConfiguration.getTo()))) {
			// Something sensible has changed.
			helpGroup.getTheModal().setChanged(true);
			dataManager.setConfiguredTo(newText);
			myConfiguration.setTo(newText);
			Combo fileCombo = dataManager
					.findComboObject(RelativeRiskDropDownPanel.RELATIVE_RISK);
			if (fileCombo != null) {
				// Not constructing.
				DropDownPropertiesSet fileSet = dataManager.getFileSet(
						myConfiguration.getFrom(), newText);
				if (fileSet != null) {
					fileCombo.removeAll();
					int fileSetSize = fileSet.size();
					for (int count = 0; count < fileSetSize; count++) {
						String fileSetItem = fileSet.getSelectedString(count);
						fileCombo.add(fileSetItem, count);
					}
					selectSilent(fileCombo, 0);
					String currentFileName = fileSet.getSelectedString(0);
					dataManager.setConfiguredFileName(currentFileName);
					myConfiguration.setDataFileName(currentFileName);
				}
			} else {
				// //////
			}
		}
	}

	/**
	 * @throws ConfigurationException
	 */
	private void updateFile(String newText) throws ConfigurationException {
		if ((newText != null)
				&& (!newText
						.equals(myConfiguration.getDataFileName()))) {
			helpGroup.getTheModal().setChanged(true);
			dataManager.setConfiguredFileName(newText);
			myConfiguration.setDataFileName(newText);
		}
	}

	private void handleWarningMessage(String s, Widget combo) {
		MessageBox box = new MessageBox(combo.getDisplay().getActiveShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("WARNING");
		box.setMessage("WARNING:\n" + s);
		box.open();
	}

	private void handleErrorMessage(Exception e,
			RelativeRiskDropDownPanel registeredDropDown) {
		this.log.debug(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(registeredDropDown.parent.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during update of the drop down "
				+ e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}

	private void handleErrorMessage(Exception e, Widget combo) {
		this.log.debug(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(combo.getDisplay().getActiveShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText(e.getClass().getSimpleName());
		box.setMessage("Error occured during modify event, exception message: "
				+ e.getMessage());
		box.open();
	}
}
