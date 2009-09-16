package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.RelativeRiskComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.ChosenFromList;
import nl.rivm.emi.dynamo.ui.support.ChosenToList;
import nl.rivm.emi.dynamo.ui.support.RelRisksCollectionForDropdown;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Handles the data actions of the relative risk tabs
 * 
 * @author schutb
 * 
 */
public class RelativeRiskTabDataManager /* implements DynamoTabDataManager */{

	private static final String RELRISK_DEATH = "Death";
	private static final String RELRISK_DISABILITY = "Disability";

	private Log log = LogFactory.getLog(this.getClass().getName());

	private RelativeRiskTabPlatformDataManager myBoss;

	private RelRisksCollectionForDropdown possibleRelRisksProvider;

	private RelativeRiskTab tab;

	/**
	 * Nescessary to determine in the modify listener what to do with the event.
	 */
	HashMap<String, Combo> comboLookup = new HashMap<String, Combo>();

	/**
	 * Nescessary to determine in the modify listener what to do with the event.
	 */
	HashMap<Combo, String> reverseComboLabelLookup = new HashMap<Combo, String>();

	private TabRelativeRiskConfigurationData tabRelativeRiskConfigurationData;

	/**
	 * 
	 * Constructor
	 * 
	 * @param myBoss
	 *            TODO
	 * 
	 * @throws ConfigurationException
	 */
	public RelativeRiskTabDataManager(RelativeRiskTab tab,
			RelativeRiskTabPlatformDataManager myBoss, Integer tabIndex)
			throws ConfigurationException {
		// this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.tabRelativeRiskConfigurationData = myBoss
				.getConfiguration(tabIndex);
		this.tab = tab;
		// this.dynamoSimulationObject = dynamoSimulationObject;
		this.myBoss = myBoss;
		// log.debug("this.initialSelectionRelativeRiskTabDataManager"
		// + this.initialSelection);
		this.possibleRelRisksProvider = myBoss
				.getRelRisksCollectionForDropdown();
	}

	/*
	 * chosenName can be from the from list, the to list, or the relative risk
	 * list
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getDropDownSet
	 * (java.lang.String, java.lang.String)
	 */
	/**
	 * @param name
	 *            : identifier of group for which the dropdown list is to be
	 *            filled (e.g. "to" or "from")
	 * @param chosenname
	 *            : ??name of the item that is chosen by the user in this
	 *            session. This is null when the tab is refreshed or no object
	 *            has yet been choosen creation this is the last element of
	 *            selections
	 * @throws NoMoreDataException
	 *             : when there are no valid choices
	 * @throws DynamoNoValidDataException
	 *             ;: when the choice made is not valid and there are no
	 *             alternatives
	 * 
	 * 
	 */

	public void addCombo2Lookups(Combo widget, String label) {
		comboLookup.put(label, widget);
		reverseComboLabelLookup.put(widget, label);
	}

	public String findComboLabel(Combo widget) {
		String label = reverseComboLabelLookup.get(widget);
		return label;
	}

	public Combo findComboObject(String label) {
		Combo widget = comboLookup.get(label);
		return widget;
	}

	public DropDownPropertiesSet getFromSet() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		Set<String> fromList = possibleRelRisksProvider.updateFromList();
		set.addAll(fromList);
		return set;
	}

	public String getConfiguredFrom() {
		String result = null;
		if (tabRelativeRiskConfigurationData != null) {
			result = tabRelativeRiskConfigurationData.getFrom();
		}
		return result;
	}

	public void setConfiguredFrom(String from) throws ConfigurationException {
		if (tabRelativeRiskConfigurationData == null) {
			tabRelativeRiskConfigurationData = new TabRelativeRiskConfigurationData();
		}
		tabRelativeRiskConfigurationData.setFrom(from);
		tabRelativeRiskConfigurationData = myBoss
				.updateDynamoSimulationObject(tabRelativeRiskConfigurationData);
	}

	public DropDownPropertiesSet getToSet(String chosenFrom)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		Set<String> toList = possibleRelRisksProvider.updateToList(chosenFrom);
		set.addAll(toList);
		return set;
	}

	public String getConfiguredTo() {
		String result = null;
		if (tabRelativeRiskConfigurationData != null) {
			result = tabRelativeRiskConfigurationData.getTo();
		}
		return result;
	}

	public void setConfiguredTo(String to) throws ConfigurationException {
		if (tabRelativeRiskConfigurationData == null) {
			tabRelativeRiskConfigurationData = new TabRelativeRiskConfigurationData();
		}
		tabRelativeRiskConfigurationData.setTo(to);
		tabRelativeRiskConfigurationData = myBoss
				.updateDynamoSimulationObject(tabRelativeRiskConfigurationData);
	}

	public DropDownPropertiesSet getFileSet(String chosenFrom, String chosenTo)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		Set<String> fileList = possibleRelRisksProvider.updateRRFileList(
				chosenFrom, chosenTo);
		set.addAll(fileList);
		return set;
	}

	public String getConfiguredFileName() {
		String result = null;
		if (tabRelativeRiskConfigurationData != null) {
			result = tabRelativeRiskConfigurationData.getDataFileName();
		}
		return result;
	}
	
	public void setConfiguredFileName(String fileName) throws ConfigurationException {
		if (tabRelativeRiskConfigurationData == null) {
			tabRelativeRiskConfigurationData = new TabRelativeRiskConfigurationData();
		}
		tabRelativeRiskConfigurationData.setDataFileName(fileName);
		tabRelativeRiskConfigurationData = myBoss
				.updateDynamoSimulationObject(tabRelativeRiskConfigurationData);
	}


	/**
	 * Not very logical, but the cleanest way to get it to the DropDownPanels.
	 * 
	 * @return
	 */
	public RelativeRiskComboModifyListener getRelativeRiskComboModifyListener() {
		return tab.getRelativeRiskComboModifyListener();
	}

	public DropDownPropertiesSet getDropDownSet(String name, String chosenName)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		log.debug("HIERALOOK");
		//
		// /*
		// * get the current names from the model-object (in
		// singleConfiguration)
		// * if this is present
		// */
		//
		// String chosenFromName = null; /*
		// * this is the name that is considered to be
		// * chosen by the user
		// */
		// if (tabRelativeRiskConfigurationData != null) {
		// chosenFromName = this.tabRelativeRiskConfigurationData.getFrom(); //
		// Can
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
		return null;
	}

	/**
	 * 
	 * Customized method for Relative Risks only. (As replacement of
	 * getContents(String name, String chosenValue)
	 * 
	 * @param name
	 *            : identifier of group ("from" or "to") for which to deliver
	 *            the contents
	 * @param chosenName
	 *            (chosen name??? This seems not to be used for relative risks.
	 * @param chosenToName
	 *            (to-name currently in model object)
	 * @param chosenFromName
	 *            (from-name currently in model object)
	 * @return Set<String> Set of chosen values
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	public Set<String> getContents(String name, String chosenName,
			String chosenFromName, String chosenToName)
			throws ConfigurationException, NoMoreDataException {
		log.debug("GET CONTENTS");
		Set<String> contents = new LinkedHashSet<String>();
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
		return contents;
	}

	private Set<String> filterByRiskFactorType(Set<String> contents,
			String chosenFromName) {
		Set<String> newContents = new LinkedHashSet<String>();
		newContents.addAll(contents);
		for (String relativeRisk : contents) {
			if (!relativeRisk.contains(chosenFromName)) {
				newContents.remove(relativeRisk);
			}
		}
		return newContents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getCurrentValue
	 * (java.lang.String)
	 */
	public String getCurrentValue(String name) throws ConfigurationException {
		// log.debug("getCurrentValue()");
		// log
		// .debug("singleConfigurationXXX: "
		// + tabRelativeRiskConfigurationData);
		String value = null;
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
		return value;
	}

	/**
	 * 
	 * Updates the object model every time a selection is made
	 * 
	 * @param dropDownName
	 * @param selectedValue
	 * @throws ConfigurationException
	 * @throws DynamoNoValidDataException
	 */
	public void updateObjectState(String name, String selectedValue)
			throws ConfigurationException {
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
	}

	public void updateDynamoSimulationObject() {
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
		// //
		// this.configurations.put(tabRelativeRiskConfigurationData.getIndex(),
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
		// log.debug("configurations.size()" +
		// myBoss.getConfigurations().size());
		// /**
		// * TODO REMOVE: LOGGING ABOVE
		// */
	}

	private Integer getInitialIndex() {
		Integer chosenInitalIndex = null;
		// log.debug("initialSelection" + initialSelection);
		// if (this.initialSelection != null) {
		// for (String chosenIndex : this.initialSelection) {
		// log.debug("chosenIndex" + chosenIndex);
		// chosenInitalIndex = new Integer(chosenIndex);
		// }
		// }
		return chosenInitalIndex;
	}

	private String getInitialRiskFactorName() {
		String chosenRiskFactorNameFromTab = null;
		Map map = myBoss.getDynamoSimulationObject()
				.getRiskFactorConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			TabRiskFactorConfigurationData conf = (TabRiskFactorConfigurationData) map
					.get(key);
			log.error("conf.getName()" + conf.getName());
			chosenRiskFactorNameFromTab = conf.getName();
		}
		return chosenRiskFactorNameFromTab;
	}

	private Set<String> getInitialDiseasesList() {
		Set<String> chosenDiseases = new LinkedHashSet<String>();
		Map map = myBoss.getDynamoSimulationObject().getDiseaseConfigurations();

		Set<String> keys = map.keySet();
		for (String key : keys) {
			TabDiseaseConfigurationData conf = (TabDiseaseConfigurationData) map
					.get(key);
			log.error("conf.getName()" + conf.getName());
			chosenDiseases.add(conf.getName());
		}
		return chosenDiseases;
	}

	// TODO: From list (composed of getInitial)
	/**
	 * List for the From dropdown that consists of chosen diseases and the
	 * chosen (only one) risk factor.
	 * 
	 */
	private Set<String> getInitialFromList() {
		Set<String> initialDiseasesList = getInitialDiseasesList();
		initialDiseasesList.add(getInitialRiskFactorName());
		return initialDiseasesList;
	}

	// TODO: To list (composed)
	/**
	 * List for the To dropdown that consists of chosen diseases and the
	 * relativeriskfordeath and relativeriskfordisability.
	 * 
	 */
	private Set<String> getInitialToList() {
		Set<String> initialDiseasesList = getInitialDiseasesList();
		// Add relriskfordeath
		initialDiseasesList.add(RELRISK_DEATH);
		// Add relriskfordeath
		initialDiseasesList.add(RELRISK_DISABILITY);
		return initialDiseasesList;
	}

	/**
	 * @deprecated
	 * @author mondeelr
	 * 
	 *         Just proxy for now.
	 */
	public void reloadConfigurationsFromModelObject() {
		myBoss.reloadConfigurationsFromModelObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * getRefreshedDropDownSet(java.lang.String)
	 */

	/* label is the name of the dropdown, e.g. FROM or TO */
	public DropDownPropertiesSet getRefreshedDropDownSet(String label)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {

		return getDropDownSet(label, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#setDefaultValue
	 * (java.lang.String, java.lang.String)
	 */
	// this was to add the selection to the chosen lists, not necessary anymore
	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getContents
	 * (java.lang.String, java.lang.String)
	 */
	public Set<String> getContents(String name, String chosenDiseaseName)
			throws ConfigurationException, NoMoreDataException {
		// Will not be used
		return null;
	}

	public void refreshAvaillableRRlist() throws ConfigurationException {
		// TODO Auto-generated method stub
		possibleRelRisksProvider.refresh(myBoss.getDynamoSimulationObject());
	}

	// public void setDynamoSimulationObject(
	// DynamoSimulationObject dynamoSimulationObject) {
	// this.dynamoSimulationObject = dynamoSimulationObject;
	// }

	private void handleWarningMessage(String s) {

		MessageBox box = new MessageBox(this.tab.plotComposite.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("WARNING");
		box.setMessage("WARNING:\n" + s);
		box.open();
	}
}
