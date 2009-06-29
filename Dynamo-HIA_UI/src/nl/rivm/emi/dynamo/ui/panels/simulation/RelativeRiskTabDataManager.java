package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.ChosenFromList;
import nl.rivm.emi.dynamo.ui.support.ChosenToList;
import nl.rivm.emi.dynamo.ui.support.RelRisksCollectionForDropdown;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Handles the data actions of the relative risk tabs
 * 
 * @author schutb
 * 
 */
public class RelativeRiskTabDataManager implements DynamoTabDataManager {

	private static final String RELRISK_DEATH = "Death";
	private static final String RELRISK_DISABILITY = "Disability";

	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.simulation.RelativeRiskTabDataManager");
	private TreeAsDropdownLists treeLists;

	public TreeAsDropdownLists getTreeLists() {
		return treeLists;
	}

	private DynamoSimulationObject dynamoSimulationObject;

	public DynamoSimulationObject getDynamoSimulationObject() {
		return dynamoSimulationObject;
	}

	private Map<Integer, TabRelativeRiskConfigurationData> configurations;
	private TabRelativeRiskConfigurationData singleConfiguration;

	public void setSingleConfiguration(
			TabRelativeRiskConfigurationData singleConfiguration) {
		this.singleConfiguration = singleConfiguration;
	}

	private Set<String> initialSelection;
	private RelativeRiskTab tab;
	private RelRisksCollectionForDropdown availlableRRs;

	public Map<Integer, TabRelativeRiskConfigurationData> getConfigurations() {
		return configurations;
	}

	public TabRelativeRiskConfigurationData getSingleConfiguration() {
		return singleConfiguration;
	}

	public RelRisksCollectionForDropdown getAvaillableRRs() {
		return availlableRRs;
	}

	/**
	 * 
	 * Constructor
	 * 
	 * @param selectedNode
	 * @param dynamoSimulationObject
	 * @param initialSelection
	 * @throws ConfigurationException
	 */
	public RelativeRiskTabDataManager(BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> initialSelection, RelativeRiskTab tab)
			throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.configurations = this.dynamoSimulationObject
				.getRelativeRiskConfigurations();
		this.initialSelection = initialSelection;
		this.tab = tab;
		log.debug("this.initialSelectionRelativeRiskTabDataManager"
				+ this.initialSelection);
		this.singleConfiguration = (TabRelativeRiskConfigurationData) this.configurations
				.get(getInitialIndex());
		this.availlableRRs = RelRisksCollectionForDropdown.getInstance(
				dynamoSimulationObject, selectedNode);
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

	public DropDownPropertiesSet getDropDownSet(String name, String chosenName)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		log.debug("HIERALOOK");

		/*
		 * get the current names from the model-object (in singleConfiguration)
		 * if this is present
		 */

		String chosenFromName = null; /*
									 * this is the name that is considered to be
									 * chosen by the user
									 */
		if (singleConfiguration != null) {
			chosenFromName = this.singleConfiguration.getFrom(); // Can also be
																	// a disease
			log.debug("chosenFromName JUST CREATED" + chosenFromName);
			/*
			 * setDefaultValues voegt de selectie toe aan de bij te houden
			 * lijst, dat is hier niet meer relevant
			 */
			// ssetDefaultValue(RelativeRiskSelectionGroup.FROM,
			// chosenFromName);
		}

		String chosenToName = null;/*
									 * this is the name that is considered to be
									 * chosen by the user
									 */
		// The model object already exists, get the name
		if (singleConfiguration != null) {
			chosenToName = this.singleConfiguration.getTo();
			log.debug("chosenToName JUST CREATED" + chosenToName);
			/*
			 * in case the old To-name in the configuration is not possible in
			 * combination with the from name, take the first valid to name from
			 * the list do not update the modelobject, as this causes a loop??
			 */
			if (this.availlableRRs.updateToList(chosenFromName) == null)
				throw new DynamoNoValidDataException(
						"Configuration requests a relative risk that is not configured, namely from "
								+ chosenFromName + " to " + chosenToName + "\n");
			if (!this.availlableRRs.updateToList(chosenFromName).contains(
					chosenToName)) {
				log.debug("chosenToName JUST CREATED" + chosenToName);
				if (chosenToName != null
						&& name == RelativeRiskSelectionGroup.TO)
					handleWarningMessage("Configuration requests a relative risk from "
							+ chosenFromName
							+ " to "
							+ chosenToName
							+ " but this RR has not been configured. Changed to a relative risk from "
							+ chosenFromName
							+ " to "
							+ availlableRRs.getFirstTo(chosenFromName));
				chosenToName = availlableRRs.getFirstTo(chosenFromName);
			}
			/*
			 * setDefaultValues voegt de selectie toe aan de bij te houden
			 * lijst, dat is hier niet meer relevant
			 */
			// setDefaultValue(RelativeRiskSelectionGroup.TO, chosenToName);
		}
		Set<String> contents = this.getContents(name, chosenName,
				chosenFromName, chosenToName);
		DropDownPropertiesSet set = new DropDownPropertiesSet();
		// Contents can never be empty

		if (contents != null) {
			set.addAll(contents);
		} else {
			throw new ConfigurationException("No entries found!" + "\n"
					+ "Choose another option.");
		}
		return set;
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
		/*
		 * if no relrisk has been set yet, take the first relative risk of the
		 * availlable ones, and use that
		 */
		if (this.availlableRRs.isEmpty()) {
			throw new NoMoreDataException(
					"No more valid relative risk data present "
							+ "in Reference_Data");
		}
		if (this.singleConfiguration == null) {

			chosenToName = this.availlableRRs.getFirstTo();
			// if (chosenToName!=null){
			chosenFromName = availlableRRs.getFirstFrom();

			// if (this.singleConfiguration==null) this.singleConfiguration=new
			// TabRelativeRiskConfigurationData ();
			if (RelativeRiskSelectionGroup.FROM.equals(name))
				updateObjectState(name, chosenFromName);

			if (RelativeRiskSelectionGroup.TO.equals(name))
				updateObjectState(name, chosenToName);
			if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name))
				updateObjectState(name, availlableRRs.getFirstRRFileList());
		}
		Integer currentIndex = null;

		currentIndex = this.singleConfiguration.getIndex();

		// The chosenFromName is still empty
		if (chosenFromName == null) {
			chosenFromName = availlableRRs.getFirstFrom();
			log.fatal("getContents::chosenFromName" + chosenFromName);
		}
		if (RelativeRiskSelectionGroup.FROM.equals(name)) {

			contents = availlableRRs.updateFromList();
			log.fatal("getContents0: " + contents);

		} else if (RelativeRiskSelectionGroup.TO.equals(name)) {
			// This is the full list of available diseases + death + disability
			// contents = this.getInitialToList();
			contents = availlableRRs.updateToList(chosenFromName);
			log.debug("contents1" + contents);
		} else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
			contents = availlableRRs.updateRRFileList(chosenFromName,
					chosenToName);
			log.debug("contentsBEFOREFILTER: " + contents);
			log.debug("contentsFromFILTER: " + chosenFromName);
			log.debug("contentsToFILTER: " + chosenToName);
			// Filter only for the allowed risk factor type (identified by the
			// unique chosenFromName)
			// if (chosenFromName != null && !chosenFromName.isEmpty()
			// && !RELRISK_DEATH.endsWith(chosenToName)
			// && !RELRISK_DISABILITY.endsWith(chosenToName)) {
			// contents = filterByRiskFactorType(contents, chosenFromName);
			log.debug("contentsFILTER: " + contents);
			// }
			log.debug("contents2" + contents);
		}
		/* update the lists for the combo */
		// this condition just means that this is for the from combogroup

		// log.debug("contentsLast" + contents);
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
		log.debug("GET CURRENT VALUE");
		log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		if (this.singleConfiguration != null) {
			if (RelativeRiskSelectionGroup.FROM.equals(name)) {
				value = singleConfiguration.getFrom();
				log.debug("VALUE: " + value);
			} else if (RelativeRiskSelectionGroup.TO.equals(name)) {
				value = singleConfiguration.getTo();
				log.debug("value" + value);
			} else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
				value = singleConfiguration.getDataFileName();
				log.debug("value" + value);
			}
		}
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
		log.debug(name + ": " + selectedValue);

		log.debug("UPDATING OBJECT STATE");
		try {
			// In case a new Tab is created, no model exists yet
			if (this.initialSelection == null && singleConfiguration == null) {
				log.debug("CREATING NEW TAB");
				createInDynamoSimulationObject();
				singleConfiguration.setIndex(this.configurations.size());
			}

			if (RelativeRiskSelectionGroup.FROM.equals(name)) {
				singleConfiguration.setFrom(selectedValue);
				setDefaultValue(RelativeRiskSelectionGroup.FROM, selectedValue);

				this.tab.refreshSelectionGroup();

			} else if (RelativeRiskSelectionGroup.TO.equals(name)) {
				singleConfiguration.setTo(selectedValue);
				setDefaultValue(RelativeRiskSelectionGroup.TO, selectedValue);
				this.tab.refreshSelectionGroup();
			} else if (RelativeRiskResultGroup.RELATIVE_RISK.equals(name)) {
				singleConfiguration.setDataFileName(selectedValue);
			}
			updateDynamoSimulationObject();
		} catch (NoMoreDataException e) {

			Shell messageShell = new Shell(tab.plotComposite.getDisplay());
			MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
			messageBox.setMessage(e.getMessage()
					+ "\nTab is not made or deleted");

			if (messageBox.open() == SWT.OK) {
				messageShell.dispose();
			}

			messageShell.open();
		} catch (DynamoNoValidDataException e) {
			Shell messageShell = new Shell(tab.plotComposite.getDisplay());
			MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
			messageBox.setMessage(e.getMessage()
					+ "\nTab is not made or deleted");

			if (messageBox.open() == SWT.OK) {
				messageShell.dispose();// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void updateDynamoSimulationObject() {
		log.error("UPDATING");
		log.debug("singleConfiguration" + singleConfiguration);
		log.debug("singleConfiguration.getFrom()"
				+ singleConfiguration.getFrom());
		log.debug("singleConfiguration.getTo()" + singleConfiguration.getTo());
		log.debug("singleConfiguration.getDataFileName()"
				+ singleConfiguration.getDataFileName());

		// Store the object
		this.configurations.put(singleConfiguration.getIndex(),
				singleConfiguration);
		this.dynamoSimulationObject
				.setRelativeRiskConfigurations(configurations);

		// refresh the list with availlable RR's;

		/**
		 * TODO REMOVE: LOGGING BELOW
		 */
		Map map = this.dynamoSimulationObject.getRelativeRiskConfigurations();
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			TabRelativeRiskConfigurationData conf = (TabRelativeRiskConfigurationData) map
					.get(key);
			log.error("conf.getFrom()" + conf.getFrom());
			log.error("conf.getTo()" + conf.getTo());
			log.error("conf.getDataFileName()" + conf.getDataFileName());
		}
		log.debug("configurations.size()" + configurations.size());
		/**
		 * TODO REMOVE: LOGGING ABOVE
		 */
	}

	private void createInDynamoSimulationObject() {
		this.singleConfiguration = new TabRelativeRiskConfigurationData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * removeFromDynamoSimulationObject()
	 */
	public void removeFromDynamoSimulationObject()
			throws ConfigurationException {
		log.error("REMOVING OBJECT STATE");
		// Added by Hendriek: renumber the indexes so that the number are in
		// accord with the
		// numbering of the tabs

		/* in case this is an empty tab skip the next line */
		if (this.singleConfiguration != null)
			this.configurations.remove(this.singleConfiguration.getIndex());
		this.singleConfiguration = null;
		Map<Integer, TabRelativeRiskConfigurationData> newConfigurations = new LinkedHashMap<Integer, TabRelativeRiskConfigurationData>();
		int newIndex = 0;
		for (Integer index : this.configurations.keySet()) {
			this.configurations.get(index).setIndex(newIndex);
			newConfigurations.put((Integer) newIndex, this.configurations
					.get(index));
			newIndex++;
		}

		this.configurations = newConfigurations;
		this.dynamoSimulationObject
				.setRelativeRiskConfigurations(newConfigurations);

	}

	private Integer getInitialIndex() {
		Integer chosenInitalIndex = null;
		log.debug("initialSelection" + initialSelection);
		if (this.initialSelection != null) {
			for (String chosenIndex : this.initialSelection) {
				log.debug("chosenIndex" + chosenIndex);
				chosenInitalIndex = new Integer(chosenIndex);
			}
		}
		return chosenInitalIndex;
	}

	private String getInitialRiskFactorName() {
		String chosenRiskFactorNameFromTab = null;
		Map map = this.dynamoSimulationObject.getRiskFactorConfigurations();
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
		Map map = this.dynamoSimulationObject.getDiseaseConfigurations();

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

	/**
	 * author Hendriek: refreshes the configuration with the contents of the
	 * index number of the current configuraton
	 * 
	 * This is needed after deleting a tab, when the tabs have gotten a new
	 * index number.
	 * 
	 * @param index
	 */
	public void refreshConfigurations(int index) {

		this.configurations = this.dynamoSimulationObject
				.getRelativeRiskConfigurations();
		this.singleConfiguration = this.configurations.get(index);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * removeOldDefaultValue(java.lang.String)
	 */
	public void removeOldDefaultValue(String name)
			throws ConfigurationException {
		if (this.singleConfiguration != null) {
			log.debug("OLDDEFAULT: " + this.singleConfiguration.getTo());
			if (RelativeRiskSelectionGroup.FROM.equals(name)) {
				ChosenFromList.getInstance().removeChosenFromList(
						this.singleConfiguration.getFrom());
			}

			if (RelativeRiskSelectionGroup.TO.equals(name)) {
				ChosenToList.getInstance().removeChosenToList(
						this.singleConfiguration.getTo());
			}
		}
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
		return initialSelection;
	}

	public WritableValue getCurrentWritableValue(String successRate) {
		// Will not be used
		return null;
	}

	public void refreshAvaillableRRlist() throws ConfigurationException {
		// TODO Auto-generated method stub
		availlableRRs.refresh(dynamoSimulationObject);
	}

	public void setDynamoSimulationObject(
			DynamoSimulationObject dynamoSimulationObject) {
		this.dynamoSimulationObject = dynamoSimulationObject;
	}

	private void handleWarningMessage(String s) {

		MessageBox box = new MessageBox(this.tab.plotComposite.getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("WARNING");
		box.setMessage("WARNING:\n" + s);
		box.open();
	}

}
