package nl.rivm.emi.dynamo.ui.panels.simulation.listeners;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.DiseaseTabDataManager;
import nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager;
import nl.rivm.emi.dynamo.ui.panels.simulation.GenericDropDownPanel;
import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskDropDownPanel;
import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskResultGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskSelectionGroup;
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
import org.eclipse.swt.widgets.Shell;
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
	private String actualFromText = null;
	private String actualToText = null;
	private String actualFileText = null;
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

	/**
	 * 
	 */
	synchronized public void modifyText(ModifyEvent event) {
		helpGroup.getTheModal().setChanged(true);
		Combo myCombo = (Combo) event.widget;
		String comboLabel = dataManager.findComboLabel(myCombo);
		log.debug("modifyText from combo with label: " + comboLabel);
		if (comboLabel != null) {
			String newText = myCombo.getText();
			Composite parent = myCombo.getParent();
			log.debug("newText: " + newText);
			try {
				if (RelativeRiskDropDownPanel.FROM.equals(comboLabel)) {
					updateFromFromDown(newText);
				} else {
					if (RelativeRiskDropDownPanel.FROM.equals(comboLabel)) {
						updateFromToDown(newText);
				}
				}
			} catch (Exception e) {
				handleErrorMessage(e, myCombo);
			}
		}
	}

	private void updateFromFromDown(String newText)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		if ((newText != null)
				&& (!newText.equalsIgnoreCase(actualFromText))) {
			updateFromToDown(newText);
		}
	}

	private void updateFromToDown(String newText)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		// From text changed, update.
		Combo toCombo = dataManager
				.findComboObject(RelativeRiskDropDownPanel.TO);
		if (toCombo != null) {
			// Not constructing.
			DropDownPropertiesSet toSet = dataManager
					.getToSet(newText);
			if (toSet != null) {
				toCombo.removeAll();
				int toSetSize = toSet.size();
				for (int count = 0; count < toSetSize; count++) {
					String toSetItem = toSet
							.getSelectedString(count);
					toCombo.add(toSetItem, count);
				}
				toCombo.select(0);
				DropDownPropertiesSet fileSet = dataManager
						.getFileSet(newText, toSet
								.getSelectedString(0));
				if (fileSet != null) {
					Combo fileCombo = dataManager
							.findComboObject(RelativeRiskDropDownPanel.RELATIVE_RISK);
					fileCombo.removeAll();
					int fileSetSize = fileSet.size();
					for (int count = 0; count < fileSetSize; count++) {
						String fileSetItem = fileSet
								.getSelectedString(count);
						fileCombo.add(fileSetItem, count);
					}
					fileCombo.select(0);
				} else {
					// //////
				}
			}
		}
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
	// private void dummy_1(){
	// if(true){
	// contents = possibleRelRisksProvider.updateFromList();
	// log.fatal("getContents0: " + contents);
	//
	// } else if (RelativeRiskDropDownPanel.TO.equals(comboLabel)) {
	// // This is the full list of available diseases + death + disability
	// // contents = this.getInitialToList();
	// contents = possibleRelRisksProvider.updateToList(chosenFromName);
	// log.debug("contents1" + contents);
	// } else if (RelativeRiskDropDownPanel.RELATIVE_RISK.equals(comboLabel)) {
	// contents = possibleRelRisksProvider.updateRRFileList(
	// chosenFromName, chosenToName);
	//
	//		
	//		
	//		
	//		
	//		
	//		
	// // First update the model
	// /* hendriek : toegevoegd: synchronized */
	// try {
	// synchronized (this.dropDown) {
	// // this.dropDown.updateDataObjectModel(newText);
	// }
	// } catch (ConfigurationException ce) {
	// this.handleErrorMessage(ce, dropDown);
	// } catch (NoMoreDataException e) {
	// this.handleErrorMessage(e, dropDown);
	// e.printStackTrace();
	// }
	// // Iterate through the registered drop downs of this
	// log.debug("this.registeredDropDowns.size()"
	// + this.registeredDropDowns.size());
	// // Update the registered (dependend) drop downs
	// for (RelativeRiskDropDownPanel registeredDropDown :
	// this.registeredDropDowns) {
	// log.debug("registeredCombo" + registeredDropDown);
	// try {
	// registeredDropDown.update(newText);
	// } catch (ConfigurationException ce) {
	// this.handleErrorMessage(ce, registeredDropDown);
	// } catch (NoMoreDataException e) {
	// this.handleErrorMessage(e, registeredDropDown);
	//
	// } catch (DynamoNoValidDataException e) {
	//
	// this.handleErrorMessage(e, registeredDropDown);
	// }
	// }
	// }

	// /// From the DataManager.
	// public DropDownPropertiesSet getDropDownSet(String name, String
	// chosenName)
	// throws ConfigurationException, NoMoreDataException,
	// DynamoNoValidDataException {
	// log.debug("HIERALOOK");
	//
	// /*
	// * get the current names from the model-object (in singleConfiguration)
	// * if this is present
	// */
	//
	// String chosenFromName = null; /*
	// * this is the name that is considered to be
	// * chosen by the user
	// */
	// if (tabRelativeRiskConfigurationData != null) {
	// chosenFromName = this.tabRelativeRiskConfigurationData.getFrom(); // Can
	// // also
	// // be
	// // a disease
	// log.debug("chosenFromName JUST CREATED" + chosenFromName);
	// /*
	// * setDefaultValues voegt de selectie toe aan de bij te houden
	// * lijst, dat is hier niet meer relevant
	// */
	// // ssetDefaultValue(RelativeRiskSelectionGroup.FROM,
	// // chosenFromName);
	// }
	//
	// String chosenToName = null;/*
	// * this is the name that is considered to be
	// * chosen by the user
	// */
	// // The model object already exists, get the name
	// if (tabRelativeRiskConfigurationData != null) {
	// chosenToName = this.tabRelativeRiskConfigurationData.getTo();
	// log.debug("chosenToName JUST CREATED" + chosenToName);
	// /*
	// * in case the old To-name in the configuration is not possible in
	// * combination with the from name, take the first valid to name from
	// * the list do not update the modelobject, as this causes a loop??
	// */
	// if (possibleRelRisksProvider.updateToList(chosenFromName) == null)
	// throw new DynamoNoValidDataException(
	// "Configuration requests a relative risk that is not configured, namely from "
	// + chosenFromName + " to " + chosenToName + "\n");
	// if (!possibleRelRisksProvider.updateToList(chosenFromName)
	// .contains(chosenToName)) {
	// log.debug("chosenToName JUST CREATED" + chosenToName);
	// if (chosenToName != null
	// && name == RelativeRiskSelectionGroup.TO)
	// handleWarningMessage("Configuration requests a relative risk from "
	// + chosenFromName
	// + " to "
	// + chosenToName
	// +
	// " but this RR has not been configured. Changed to a relative risk from "
	// + chosenFromName
	// + " to "
	// + possibleRelRisksProvider
	// .getFirstTo(chosenFromName));
	// chosenToName = possibleRelRisksProvider
	// .getFirstTo(chosenFromName);
	// }
	// /*
	// * setDefaultValues voegt de selectie toe aan de bij te houden
	// * lijst, dat is hier niet meer relevant
	// */
	// // setDefaultValue(RelativeRiskSelectionGroup.TO, chosenToName);
	// }
	// Set<String> contents = this.getContents(name, chosenName,
	// chosenFromName, chosenToName);
	// DropDownPropertiesSet set = new DropDownPropertiesSet();
	// // Contents can never be empty
	//
	// if (contents != null) {
	// set.addAll(contents);
	// /* Debugging */
	// StringBuffer setDump = new StringBuffer();
	// for (String property : set) {
	// setDump.append("\n" + property);
	// }
	// log.debug("Properties: " + setDump.toString());
	// /* Debugging ends. */
	// } else {
	// throw new ConfigurationException("No entries found!" + "\n"
	// + "Choose another option.");
	// }
	// return set;
	// }
	//
	// /**
	// *
	// * Customized method for Relative Risks only. (As replacement of
	// * getContents(String name, String chosenValue)
	// *
	// * @param name
	// * : identifier of group ("from" or "to") for which to deliver
	// * the contents
	// * @param chosenName
	// * (chosen name??? This seems not to be used for relative risks.
	// * @param chosenToName
	// * (to-name currently in model object)
	// * @param chosenFromName
	// * (from-name currently in model object)
	// * @return Set<String> Set of chosen values
	// * @throws ConfigurationException
	// * @throws NoMoreDataException
	// */
	// public Set<String> getContents(String name, String chosenName,
	// String chosenFromName, String chosenToName)
	// throws ConfigurationException, NoMoreDataException {
	// log.debug("GET CONTENTS");
	// Set<String> contents = new LinkedHashSet<String>();
	// /*
	// * if no relrisk has been set yet, take the first relative risk of the
	// * availlable ones, and use that
	// */
	// if (possibleRelRisksProvider.isEmpty()) {
	// throw new NoMoreDataException(
	// "No more valid relative risk data present "
	// + "in Reference_Data");
	// }
	// if (this.tabRelativeRiskConfigurationData == null) {
	//
	// chosenToName = this.possibleRelRisksProvider.getFirstTo();
	// // if (chosenToName!=null){
	// chosenFromName = possibleRelRisksProvider.getFirstFrom();
	//
	// // if (this.singleConfiguration==null) this.singleConfiguration=new
	// // TabRelativeRiskConfigurationData ();
	// if (RelativeRiskSelectionGroup.FROM.equals(name))
	// updateObjectState(name, chosenFromName);
	//
	// if (RelativeRiskSelectionGroup.TO.equals(name))
	// updateObjectState(name, chosenToName);
	// if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name))
	// updateObjectState(name, possibleRelRisksProvider
	// .getFirstRRFileList());
	// }
	// Integer currentIndex = null;
	//
	// currentIndex = this.tabRelativeRiskConfigurationData.getIndex();
	//
	// // The chosenFromName is still empty
	// if (chosenFromName == null) {
	// chosenFromName = possibleRelRisksProvider.getFirstFrom();
	// log.fatal("getContents::chosenFromName" + chosenFromName);
	// }
	// if (RelativeRiskSelectionGroup.FROM.equals(name)) {
	//
	// contents = possibleRelRisksProvider.updateFromList();
	// log.fatal("getContents0: " + contents);
	//
	// } else if (RelativeRiskSelectionGroup.TO.equals(name)) {
	// // This is the full list of available diseases + death + disability
	// // contents = this.getInitialToList();
	// contents = possibleRelRisksProvider.updateToList(chosenFromName);
	// log.debug("contents1" + contents);
	// } else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
	// contents = possibleRelRisksProvider.updateRRFileList(
	// chosenFromName, chosenToName);
	// log.debug("contentsBEFOREFILTER: " + contents);
	// log.debug("contentsFromFILTER: " + chosenFromName);
	// log.debug("contentsToFILTER: " + chosenToName);
	// // Filter only for the allowed risk factor type (identified by the
	// // unique chosenFromName)
	// // if (chosenFromName != null && !chosenFromName.isEmpty()
	// // && !RELRISK_DEATH.endsWith(chosenToName)
	// // && !RELRISK_DISABILITY.endsWith(chosenToName)) {
	// // contents = filterByRiskFactorType(contents, chosenFromName);
	// log.debug("contentsFILTER: " + contents);
	// // }
	// log.debug("contents2" + contents);
	// }
	// /* update the lists for the combo */
	// // this condition just means that this is for the from combogroup
	// // log.debug("contentsLast" + contents);
	// return contents;
	// }
	//
	// private Set<String> filterByRiskFactorType(Set<String> contents,
	// String chosenFromName) {
	// Set<String> newContents = new LinkedHashSet<String>();
	// newContents.addAll(contents);
	// for (String relativeRisk : contents) {
	// if (!relativeRisk.contains(chosenFromName)) {
	// newContents.remove(relativeRisk);
	// }
	// }
	// return newContents;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getCurrentValue
	// * (java.lang.String)
	// */
	// public String getCurrentValue(String name) throws ConfigurationException
	// {
	// log.debug("getCurrentValue()");
	// log
	// .debug("singleConfigurationXXX: "
	// + tabRelativeRiskConfigurationData);
	// String value = null;
	// if (this.tabRelativeRiskConfigurationData != null) {
	// if (RelativeRiskSelectionGroup.FROM.equals(name)) {
	// value = tabRelativeRiskConfigurationData.getFrom();
	// log.debug("Value: " + value);
	// } else if (RelativeRiskSelectionGroup.TO.equals(name)) {
	// value = tabRelativeRiskConfigurationData.getTo();
	// log.debug("Value" + value);
	// } else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
	// value = tabRelativeRiskConfigurationData.getDataFileName();
	// log.debug("Value" + value);
	// }
	// }
	// return value;
	// }
	//
	// /**
	// *
	// * Updates the object model every time a selection is made
	// *
	// * @param dropDownName
	// * @param selectedValue
	// * @throws ConfigurationException
	// * @throws DynamoNoValidDataException
	// */
	// public void updateObjectState(String name, String selectedValue)
	// throws ConfigurationException {
	// log.debug(name + ": " + selectedValue);
	//
	// log.debug("updateObjectState");
	// try {
	// // In case a new Tab is created, no model exists yet
	// if (this.initialSelection == null
	// && tabRelativeRiskConfigurationData == null) {
	// log.debug("CREATING NEW TAB");
	// tabRelativeRiskConfigurationData = new
	// TabRelativeRiskConfigurationData();
	// // tabRelativeRiskConfigurationData.setIndex(this.configurations
	// // .size());
	// tabRelativeRiskConfigurationData.setIndex(myBoss
	// .getConfigurations().size());
	// }
	//
	// if (RelativeRiskSelectionGroup.FROM.equals(name)) {
	// tabRelativeRiskConfigurationData.setFrom(selectedValue);
	// setDefaultValue(RelativeRiskSelectionGroup.FROM, selectedValue);
	//
	// this.tab.refreshSelectionGroup();
	//
	// } else if (RelativeRiskSelectionGroup.TO.equals(name)) {
	// tabRelativeRiskConfigurationData.setTo(selectedValue);
	// setDefaultValue(RelativeRiskSelectionGroup.TO, selectedValue);
	// this.tab.refreshSelectionGroup();
	// } else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
	// tabRelativeRiskConfigurationData.setDataFileName(selectedValue);
	// }
	// updateDynamoSimulationObject();
	// } catch (NoMoreDataException e) {
	//
	// Shell messageShell = new Shell(tab.plotComposite.getDisplay());
	// MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
	// messageBox.setMessage(e.getMessage()
	// + "\nTab is not made or deleted");
	//
	// if (messageBox.open() == SWT.OK) {
	// messageShell.dispose();
	// }
	//
	// messageShell.open();
	// } catch (DynamoNoValidDataException e) {
	// Shell messageShell = new Shell(tab.plotComposite.getDisplay());
	// MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
	// messageBox.setMessage(e.getMessage()
	// + "\nTab is not made or deleted");
	//
	// if (messageBox.open() == SWT.OK) {
	// messageShell.dispose();// TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// public void updateDynamoSimulationObject() {
	// log.error("UPDATING");
	// log.debug("singleConfiguration" + tabRelativeRiskConfigurationData);
	// log.debug("singleConfiguration.getFrom()"
	// + tabRelativeRiskConfigurationData.getFrom());
	// log.debug("singleConfiguration.getTo()"
	// + tabRelativeRiskConfigurationData.getTo());
	// log.debug("singleConfiguration.getDataFileName()"
	// + tabRelativeRiskConfigurationData.getDataFileName());
	//
	// // Store the object
	// // this.configurations.put(tabRelativeRiskConfigurationData.getIndex(),
	// // tabRelativeRiskConfigurationData);
	// myBoss.getConfigurations().put(
	// tabRelativeRiskConfigurationData.getIndex(),
	// tabRelativeRiskConfigurationData);
	// // this.dynamoSimulationObject
	// // .setRelativeRiskConfigurations(configurations);
	// myBoss.setRelativeRiskConfigurations();
	//
	// // refresh the list with availlable RR's;
	//
	// /**
	// * TODO REMOVE: LOGGING BELOW
	// */
	// Map map = myBoss.getDynamoSimulationObject()
	// .getRelativeRiskConfigurations();
	// Set<Integer> keys = map.keySet();
	// for (Integer key : keys) {
	// TabRelativeRiskConfigurationData conf =
	// (TabRelativeRiskConfigurationData) map
	// .get(key);
	// log.error("conf.getFrom()" + conf.getFrom());
	// log.error("conf.getTo()" + conf.getTo());
	// log.error("conf.getDataFileName()" + conf.getDataFileName());
	// }
	// log.debug("configurations.size()" + myBoss.getConfigurations().size());
	// /**
	// * TODO REMOVE: LOGGING ABOVE
	// */
	// }
	//
	// private Integer getInitialIndex() {
	// Integer chosenInitalIndex = null;
	// log.debug("initialSelection" + initialSelection);
	// if (this.initialSelection != null) {
	// for (String chosenIndex : this.initialSelection) {
	// log.debug("chosenIndex" + chosenIndex);
	// chosenInitalIndex = new Integer(chosenIndex);
	// }
	// }
	// return chosenInitalIndex;
	// }
	//
	// /// From the DataManager ends.
}
