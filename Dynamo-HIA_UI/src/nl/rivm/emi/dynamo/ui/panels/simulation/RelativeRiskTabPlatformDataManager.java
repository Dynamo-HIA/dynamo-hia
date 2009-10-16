package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.RelRisksCollectionForDropdown;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

/**
 * 
 * Handles the data actions of the relative risk tabs
 * 
 * @author schutb
 * 
 *         Refactored by mondeelr: Split in two to make the functionality
 *         correspond with the underlying tab-structure and hopefully make it
 *         easier to follow.
 * 
 */
public class RelativeRiskTabPlatformDataManager implements DynamoTabDataManager {

	private static final String RELRISK_DEATH = "Death";
	private static final String RELRISK_DISABILITY = "Disability";

	Log log = LogFactory.getLog(getClass().getSimpleName());
	/**
	 * Cross-link strictly to enable refreshing of the Relative Risks tabs when
	 * the DynamoSimulationObject RelativeRisk-configurations are changed
	 * through the backdoor.
	 */
	RelativeRisksTabPlatform myPlatform = null;

	// DynamoSimulationObject dynamoSimulationObject;

	// private Map<Integer, TabRelativeRiskConfigurationData> configurations;

	private TreeAsDropdownLists treeLists;

	private RelativeRiskTab tab = null;
	private RelRisksCollectionForDropdown relRisksCollectionForDropdown; 
	/**
	 * This object is a writethrough proxy for the relative risks part of the
	 * DynamoSimulation Object.
	 */
	private TabRelativeRiskConfigurationsProxy configurationsProxy;

	/**
	 * Constructor
	 * 
	 * @param selectedNode
	 * @param dynamoSimulationObject
	 * @param platform
	 *            TODO
	 * @throws ConfigurationException
	 */
	public RelativeRiskTabPlatformDataManager(BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			RelativeRisksTabPlatform platform) throws ConfigurationException {
		myPlatform = platform;
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		// this.dynamoSimulationObject = dynamoSimulationObject;
		configurationsProxy = new TabRelativeRiskConfigurationsProxy(
				dynamoSimulationObject, this);
		// this.configurations = dynamoSimulationObject
		// .getRelativeRiskConfigurations();
		// These two were always initialized with null, so they can be thrown
		// out of the parameters.
		// this.initialSelection = initialSelection;
		// this.tab = tab;
	
		relRisksCollectionForDropdown = RelRisksCollectionForDropdown
				.getInstance(dynamoSimulationObject, selectedNode);
	
		}

	// public Map<Integer, TabRelativeRiskConfigurationData> getConfigurations()
	// {
	// return dynamoSimulationObject.getRelativeRiskConfigurations();
	// }

	public TabRelativeRiskConfigurationData getConfiguration(Integer index) {
		return configurationsProxy.get(index);
	}

 	public RelRisksCollectionForDropdown getRelRisksCollectionForDropdown() {
 		return relRisksCollectionForDropdown;
	}
	
	public TreeAsDropdownLists getTreeLists() {
		return treeLists;
	}

	public void removeAndRebuildAllTabs(){
		try{
		myPlatform.removeAndRebuildAllTabs();
		} catch(Exception e){
			handleWarningMessage(e.getMessage());
		}
	}

	/**
	 * 
	 * Customized method for Relative Risks only. (As replacement of
	 * getContents(String name, String chosenValue)
	 * 
	 * @param name
	 *            : identifier of group ("from" or "to") for which to deliver
	 *            the contents
	 * @param chosenFromName
	 *            (from-name currently in model object)
	 * @param chosenToName
	 *            (to-name currently in model object)
	 * @return Set<String> Set of chosen values
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	// public Set<String> getContents(String name, String chosenFromName,
	// String chosenToName)
	// throws ConfigurationException, NoMoreDataException {
	// log.debug("GET CONTENTS");
	// Set<String> contents = new LinkedHashSet<String>();
	// /*
	// * if no relrisk has been set yet, take the first relative risk of the
	// * availlable ones, and use that
	// */
	// if (this.availlableRRs.isEmpty()) {
	// throw new NoMoreDataException(
	// "No more valid relative risk data present "
	// + "in Reference_Data");
	// }
	// if (this.singleConfiguration == null) {
	//
	// chosenToName = this.availlableRRs.getFirstTo();
	// // if (chosenToName!=null){
	// chosenFromName = availlableRRs.getFirstFrom();
	//
	// // if (this.singleConfiguration==null) this.singleConfiguration=new
	// // TabRelativeRiskConfigurationData ();
	// if (RelativeRiskSelectionGroup.FROM.equals(name))
	// updateObjectState(name, chosenFromName);
	//
	// if (RelativeRiskSelectionGroup.TO.equals(name))
	// updateObjectState(name, chosenToName);
	// if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name))
	// updateObjectState(name, availlableRRs.getFirstRRFileList());
	// }
	// Integer currentIndex = null;
	//
	// currentIndex = this.singleConfiguration.getIndex();
	//
	// // The chosenFromName is still empty
	// if (chosenFromName == null) {
	// chosenFromName = availlableRRs.getFirstFrom();
	// log.fatal("getContents::chosenFromName" + chosenFromName);
	// }
	// if (RelativeRiskSelectionGroup.FROM.equals(name)) {
	//
	// contents = availlableRRs.updateFromList();
	// log.fatal("getContents0: " + contents);
	//
	// } else if (RelativeRiskSelectionGroup.TO.equals(name)) {
	// // This is the full list of available diseases + death + disability
	// // contents = this.getInitialToList();
	// contents = availlableRRs.updateToList(chosenFromName);
	// log.debug("contents1" + contents);
	// } else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
	// contents = availlableRRs.updateRRFileList(chosenFromName,
	// chosenToName);
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

	/**
	 * @deprecated (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getValueFromSingleConfiguration
	 *      (java.lang.String)
	 */
	public String getValueFromSingleConfiguration(String name) throws ConfigurationException {
		// log.debug("GET CURRENT VALUE");
		// log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		// if (this.singleConfiguration != null) {
		// if (RelativeRiskSelectionGroup.FROM.equals(name)) {
		// value = singleConfiguration.getFrom();
		// log.debug("VALUE: " + value);
		// } else if (RelativeRiskSelectionGroup.TO.equals(name)) {
		// value = singleConfiguration.getTo();
		// log.debug("value" + value);
		// } else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
		// value = singleConfiguration.getDataFileName();
		// log.debug("value" + value);
		// }
		// }
		return value;
	}

	/**
	 * @deprecated Updates the object model every time a selection is made
	 * 
	 * @param dropDownName
	 * @param selectedValue
	 * @throws ConfigurationException
	 * @throws DynamoNoValidDataException
	 */
	public void updateObjectState(String name, String selectedValue)
			throws ConfigurationException {
		log.debug(name + ": " + selectedValue);

		log.debug("UPDATING OBJECT STATE");
		// try {
		// // In case a new Tab is created, no model exists yet
		// if (this.initialSelection == null && singleConfiguration == null) {
		// log.debug("CREATING NEW TAB");
		// createInDynamoSimulationObject();
		// singleConfiguration.setIndex(this.configurations.size());
		// }
		//
		// if (RelativeRiskSelectionGroup.FROM.equals(name)) {
		// singleConfiguration.setFrom(selectedValue);
		// setDefaultValue(RelativeRiskSelectionGroup.FROM, selectedValue);
		//
		// this.tab.refreshSelectionGroup();
		//
		// } else if (RelativeRiskSelectionGroup.TO.equals(name)) {
		// singleConfiguration.setTo(selectedValue);
		// setDefaultValue(RelativeRiskSelectionGroup.TO, selectedValue);
		// this.tab.refreshSelectionGroup();
		// } else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
		// singleConfiguration.setDataFileName(selectedValue);
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

	public TabRelativeRiskConfigurationData updateDynamoSimulationObject(
			TabRelativeRiskConfigurationData singleConfiguration)
			throws ConfigurationException {
		log.debug("Before the update: " + configurationsProxy.report());

		// Store the object
		// dynamoSimulationObject.getRelativeRiskConfigurations().put(
		// singleConfiguration.getIndex(), singleConfiguration);
		TabRelativeRiskConfigurationData actualConfiguration = configurationsProxy
				.update(singleConfiguration);
		// this.dynamoSimulationObject
		// .setRelativeRiskConfigurations(configurations);

		// refresh the list with availlable RR's;
		log.debug("After the update: " + configurationsProxy.report());
//		relRisksCollectionForDropdown = RelRisksCollectionForDropdown
//				.getInstance4RelRisks(configurationsProxy.getDynamoSimulationObject(),
//						treeLists);
		return actualConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * removeFromDynamoSimulationObject()
	 */
	public void removeFromDynamoSimulationObject(
			TabRelativeRiskConfigurationData singleConfiguration)
			throws ConfigurationException {
		log.debug("Before removal: " + configurationsProxy.report());
		configurationsProxy.remove(singleConfiguration);
		log.debug("After removal: " + configurationsProxy.report());
// Not nescessary at this time.
		//		relRisksCollectionForDropdown = RelRisksCollectionForDropdown
//				.getInstance4RelRisks(configurationsProxy.getDynamoSimulationObject(),
//						treeLists);

	}

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

	private String getInitialRiskFactorName() {
		String chosenRiskFactorNameFromTab = null;
		Map map = configurationsProxy.getDynamoSimulationObject()
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
		Map map = configurationsProxy.getDynamoSimulationObject()
				.getDiseaseConfigurations();

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

	/**
	 * @deprecated (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager# 
	 *                                                                   removeOldDefaultValue
	 *                                                                   (java.
	 *                                                                   lang.
	 *                                                                   String)
	 */
	public void removeOldDefaultValue(String name)
			throws ConfigurationException {
		// if (this.singleConfiguration != null) {
		// log.debug("OLDDEFAULT: " + this.singleConfiguration.getTo());
		// if (RelativeRiskSelectionGroup.FROM.equals(name)) {
		// ChosenFromList.getInstance().removeChosenFromList(
		// this.singleConfiguration.getFrom());
		// }
		//
		// if (RelativeRiskSelectionGroup.TO.equals(name)) {
		// ChosenToList.getInstance().removeChosenToList(
		// this.singleConfiguration.getTo());
		// }
		// }
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
		return null; // initialSelection;
	}

	public void refreshAvaillableRRlist() throws ConfigurationException {
		// TODO Auto-generated method stub
/* 20090918		relRisksCollectionForDropdown.refresh(configurationsProxy
				.getDynamoSimulationObject()); */
	}

	// public DynamoSimulationObject getDynamoSimulationObject() {
	// return dynamoSimulationObject;
	// }
	//
	// public void setDynamoSimulationObject(
	// DynamoSimulationObject dynamoSimulationObject) {
	// this.dynamoSimulationObject = dynamoSimulationObject;
	// }

	/**
	 * @deprecated
	 */
	public void setRelativeRiskConfigurations() {
		configurationsProxy.getDynamoSimulationObject()
				.setRelativeRiskConfigurations(/* configurations */null);

	}

	/**
	 * @deprecated
	 */
	public Map getCurrentRelativeRiskConfigurations() {
		return configurationsProxy.getDynamoSimulationObject()
				.getRelativeRiskConfigurations();
	}

	/**
	 * @deprecated author Hendriek: refreshes the configuration with the
	 *             contents of the index number of the current configuraton
	 * 
	 *             This is needed after deleting a tab, when the tabs have
	 *             gotten a new index number.
	 * 
	 *             20090904 RLM The index parameter was never used, so I removed
	 *             it.
	 */
	public void reloadConfigurationsFromModelObject() {
		/* configurations = */configurationsProxy.getDynamoSimulationObject()
				.getRelativeRiskConfigurations();
	}

	private void handleWarningMessage(String s) {

		MessageBox box = new MessageBox(myPlatform.plotComposite.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("WARNING");
		box.setMessage("WARNING:\n" + s);
		box.open();
	}

	/**
	 * Neutralized Interface methods department.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#setDefaultValue
	 * (java.lang.String, java.lang.String)
	 */
	// this was to add the selection to the chosen lists, not necessary anymore
	@Deprecated
	// Not 2b used for RRs.
	@Override
	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException {
		/*
		 * log.debug("SETDEFAULT: " + selectedValue); if
		 * (DiseaseSelectionGroup.DISEASE.equals(name)) { // FOR BOTH TO AND
		 * FROM LIST ChoosableDiseases choosableDiseases =
		 * ChoosableDiseases.getInstance(); // FOR BOTH TO AND FROM LIST
		 * choosableDiseases.setChosenDisease(selectedValue); }
		 * 
		 * if (this.singleConfiguration != null) { log.debug("OLDDEFAULT: " +
		 * this.singleConfiguration.getTo()); if
		 * (RelativeRiskSelectionGroup.FROM.equals(name)) {
		 * ChosenFromList.getInstance().setChosenFromList(selectedValue);
		 * RelRisksCollectionForDropdown
		 * .getInstance().updateFromList(selectedValue); }
		 * 
		 * if (RelativeRiskSelectionGroup.TO.equals(name)) {
		 * 
		 * ChosenToList.getInstance().setChosenToList(selectedValue); } }
		 */
	}

	@Override
	public WritableValue getCurrentWritableValue(String successRate) {
		// Will not be used
		return null;
	}

	@Deprecated
	// Not 2b used for RRs.
	@Override
	public DynamoSimulationObject getDynamoSimulationObject() {
		return configurationsProxy.getDynamoSimulationObject();
	}

	@Deprecated
	// Not 2b used for RRs.
	@Override
	public void removeFromDynamoSimulationObject()
			throws ConfigurationException {
		// TODO Auto-generated method stub

	}

	@Deprecated
	// Not 2b used for RRs.
	@Override
	public void updateDynamoSimulationObject() throws ConfigurationException {
		// TODO Auto-generated method stub

	}

	@Deprecated
	// Not 2b used for RRs.
	@Override
	public DropDownPropertiesSet getDropDownSet(String name, String selection)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		// TODO Auto-generated method stub
		return null;
	}
}
