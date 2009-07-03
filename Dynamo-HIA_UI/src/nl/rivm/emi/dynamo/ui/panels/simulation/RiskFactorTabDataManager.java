package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.RelRisksCollectionForDropdown;
import nl.rivm.emi.dynamo.ui.support.SimilarityCounter;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;

public class RiskFactorTabDataManager implements DynamoTabDataManager {

	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.ui.panels.simulation.RiskFactorTabDataManager ");
	private TreeAsDropdownLists treeLists;

	public TreeAsDropdownLists getTreeLists() {
		return treeLists;
	}

	private DynamoSimulationObject dynamoSimulationObject;
	private Map<String, TabRiskFactorConfigurationData> configurations;
	private TabRiskFactorConfigurationData singleConfiguration;
	private Set<String> initialSelection;
	private RiskFactorTab tab;

	public RiskFactorTabDataManager(BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> initialSelection, RiskFactorTab tab)
			throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.configurations = dynamoSimulationObject
				.getRiskFactorConfigurations();
		this.initialSelection = initialSelection;
		this.singleConfiguration = this.configurations.get(getInitialName());
		this.tab = tab;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getContents
	 * (java.lang.String, java.lang.String)
	 * 
	 * @param name: name of dropdown (e.g. "to" or "from")
	 * 
	 * @param chosenRiskFactorName: name that has been choosen (can be null);
	 * can also be disease
	 */
	@Override
	public Set<String> getContents(String name, String chosenRiskFactorName)
			throws ConfigurationException {
		log.debug("GET CONTENTS");
		Set<String> contents = new LinkedHashSet<String>();
		// The name is still empty
		if (chosenRiskFactorName == null) {
			// No risk factor is chosen, get the first name from the treelist
			chosenRiskFactorName = getFirstRiskFactorOfTreeList();
		}
		log.debug("HIERO chosenRiskFactorName DATAMANAGER: "
				+ chosenRiskFactorName);
		if (RiskFactorSelectionGroup.RISK_FACTOR.equals(name)) {
			contents = this.treeLists.getRiskFactors();
			log.debug("getContents NAME: " + contents);
		} else if (RiskFactorResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
			contents = this.treeLists
					.getRiskFactorPrevalences(chosenRiskFactorName);
			log.debug("contents1" + contents);
		} else if (RiskFactorResultGroup.TRANSITION.equals(name)) {
			contents = this.treeLists.getTransitions(chosenRiskFactorName);
			log.debug("contents2" + contents);
		}
		log.debug("contentsLast" + contents);
		return contents;
	}

	private String getFirstRiskFactorOfTreeList() {
		return (String) this.treeLists.getRiskFactors().iterator().next();
	}

	@Override
	public String getCurrentValue(String dropDownName)
			throws ConfigurationException {
		log.debug("GET CURRENT VALUE");
		log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		if (this.singleConfiguration != null) {
			if (RiskFactorSelectionGroup.RISK_FACTOR.equals(dropDownName)) {
				value = singleConfiguration.getName();
				log.debug("VALUE: " + value);
			} else if (RiskFactorResultGroup.RISK_FACTOR_PREVALENCE
					.equals(dropDownName)) {
				value = singleConfiguration.getPrevalenceFileName();
				log.debug("value" + value);
			} else if (RiskFactorResultGroup.TRANSITION.equals(dropDownName)) {
				value = singleConfiguration.getTransitionFileName();
				log.debug("value" + value);
			}
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getDropDownSet
	 * (java.lang.String, java.lang.String) name = naam van control, bijv from
	 * of to chosenRisFactorName: selection (default of gekozen)
	 */
	@Override
	public DropDownPropertiesSet getDropDownSet(String name,
			String chosenRiskFactorName) throws ConfigurationException, NoMoreDataException {
		log.debug("HIERALOOK");

		// The model object already exists, get the name
		if (singleConfiguration != null && chosenRiskFactorName == null) {
			chosenRiskFactorName = this.singleConfiguration.getName();
			log.debug("chosenRiskFactorName JUST CREATED"
					+ chosenRiskFactorName);
		}
		DropDownPropertiesSet set = new DropDownPropertiesSet();
           Set<String> contents = this.getContents(name, chosenRiskFactorName);
		
		// Contents can never be empty

		if (contents != null) 
			set.addAll(contents);
		else throw new NoMoreDataException("no more configured diseases availlable");
	
		
		return set;
	}

	@Override
	public DropDownPropertiesSet getRefreshedDropDownSet(String label)
			throws ConfigurationException, NoMoreDataException {
		return getDropDownSet(label, null);
	}

	@Override
	public void removeFromDynamoSimulationObject()
			throws ConfigurationException {
		this.configurations.remove(this.singleConfiguration.getName());

		/*
		 * changed by Hendriek: use update. Then the setting of the
		 * configuration can be omitted, as this is done also in update
		 */
		// this.dynamoSimulationObject.setRiskFactorConfigurations(configurations);
		/* also remove the relative risks */
		updateDynamoSimulationObject();

	}

	@Override
	public void removeOldDefaultValue(String label)
			throws ConfigurationException {
		// Will not be used

	}

	@Override
	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException {
		// Will not be used

	}

	@Override
	public void updateObjectState(String name, String selectedValue)
			throws ConfigurationException {
		log.debug(name + ": " + selectedValue);
		log.fatal("UPDATING OBJECT STATE");

		log.fatal("this.initialSelection" + this.initialSelection);
		log.debug("this.singleConfiguration" + this.singleConfiguration);

		// In case a new Tab is created, no model exists yet
		if (this.initialSelection.size() == 0
				&& this.singleConfiguration == null) {
			log.debug("CREATING NEW TAB");
			createInDynamoSimulationObject();
		}

		if (RiskFactorSelectionGroup.RISK_FACTOR.equals(name)) {
			singleConfiguration.setName(selectedValue);
		} else if (RiskFactorResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
			singleConfiguration.setPrevalenceFileName(selectedValue);
		} else if (RiskFactorResultGroup.TRANSITION.equals(name)) {
			singleConfiguration.setTransitionFileName(selectedValue);
		}
		updateDynamoSimulationObject();
	}

	public void updateDynamoSimulationObject() throws ConfigurationException {
		log.error("UPDATING AFTER RISKFACTOR CHANGE");
		// log.debug("singleConfiguration" + singleConfiguration);
		// log.debug("singleConfiguration.getName()" +
		// singleConfiguration.getName());

		this.configurations.put(singleConfiguration.getName(),
				singleConfiguration);
		this.dynamoSimulationObject.setRiskFactorConfigurations(configurations);

		/*
		 * added by Hendriek Also check if the riskfactor names in the relative
		 * risks are still valid If not see if there is a valid entry with the
		 * new riskfactor name and the same to value. Otherwise remove and show
		 * warning message Valid = to is either the current riskfactor, or a
		 * diseasename that has been selected
		 */

		String riskfactorName = singleConfiguration.getName();
		Map<String, ITabDiseaseConfiguration> diseaseConfiguration = this.dynamoSimulationObject
				.getDiseaseConfigurations();
		Set<String> diseaseNames = new LinkedHashSet<String>();
		for (String key : diseaseConfiguration.keySet())
			if (key != null)
				diseaseNames.add(key);

		Map<Integer, TabRelativeRiskConfigurationData> relativeRiskConfigurations = this.dynamoSimulationObject
				.getRelativeRiskConfigurations();

		Map<Integer, TabRelativeRiskConfigurationData> relRiskConfiguration = relativeRiskConfigurations;
		TabRelativeRiskConfigurationData singleRRconfiguration;
		log.fatal("number of Relative risks: " + relRiskConfiguration.size());
		RelRisksCollectionForDropdown collection = RelRisksCollectionForDropdown
				.getInstance(dynamoSimulationObject, treeLists);
		// no refreshing needed, as this is part of get instance
		// collection.refresh(dynamoSimulationObject,treeLists);
		log.debug("availlable RRs: "
				+ collection.getAvaillableRelRisksForDropdown());
		for (Iterator<TabRelativeRiskConfigurationData> iter = relRiskConfiguration
				.values().iterator(); iter.hasNext();) {

			// for (Integer key2 : relRiskConfiguration.keySet())

			singleRRconfiguration = iter.next();
			boolean validEntry = false;

			if (singleRRconfiguration.getFrom().equals(riskfactorName))
				validEntry = true;
			log.debug(riskfactorName + "?=" + singleRRconfiguration.getFrom()
					+ ": validEntry= " + validEntry);
			for (String name : diseaseNames) {
				log.debug(name + " ?= " + singleRRconfiguration.getFrom() + "="
						+ (singleRRconfiguration.getFrom().equals(name)));
				if (singleRRconfiguration.getFrom().equals(name))
					validEntry = true;
			}

			/*
			 * NB we do not check here on a disease being both to and from this
			 * should have been done when entering the original relative risks
			 */

			if (!validEntry) {

				Set<String> fileName = collection.updateRRFileList(
						riskfactorName, singleRRconfiguration.getTo());
				if (fileName == null) {

					handleWarningMessage("removed: RR from "
							+ singleRRconfiguration.getFrom() + " to "
							+ singleRRconfiguration.getTo()
							+ "\n(not configurated)");

					log.fatal("removed: RR from "
							+ singleRRconfiguration.getFrom() + " to "
							+ singleRRconfiguration.getTo());
					iter.remove();
				} else {

					log.fatal("changed: RR from "
							+ singleRRconfiguration.getFrom() + " to "
							+ singleRRconfiguration.getTo() + " into: RR from "
							+ riskfactorName + " to "
							+ singleRRconfiguration.getTo());
					handleWarningMessage("RR from "
							+ singleRRconfiguration.getFrom() + " to "
							+ singleRRconfiguration.getTo()
							+ " is changed into:\nRR from " + riskfactorName
							+ " to " + singleRRconfiguration.getTo());

					singleRRconfiguration.setFrom(riskfactorName);
				}
			}
			log.fatal("number of RRs: " + relRiskConfiguration.size());
		}

		this.dynamoSimulationObject
				.setRelativeRiskConfigurations(relRiskConfiguration);

		/* now similarly adapt the scenario.configurations */
		Map<String, ITabScenarioConfiguration> scenarioConfigurations = this.dynamoSimulationObject
				.getScenarioConfigurations();
		Set<String> scenarioNames = scenarioConfigurations.keySet();
		ITabScenarioConfiguration singleScenarioConfiguration;
		Iterator scenIter = scenarioConfigurations.keySet().iterator();
		while (scenIter.hasNext())

		{
			String scenName=(String) scenIter.next();
			singleScenarioConfiguration = scenarioConfigurations.get(scenName);

			String transFile = singleScenarioConfiguration
					.getAltTransitionFileName();
			String prevFile = singleScenarioConfiguration
					.getAltPrevalenceFileName();
			Set<String> availlablePrevFiles = getContents(
					RiskFactorResultGroup.RISK_FACTOR_PREVALENCE,
					riskfactorName);
			Set<String> availlableTransFiles = getContents(
					RiskFactorResultGroup.TRANSITION, riskfactorName);
			if (!availlablePrevFiles.contains(prevFile)) {
				String newPrevFile = null;
				/* take the prevalence file with the most similar name */
				double distance = 1;
				for (String potentialPrevFile : availlablePrevFiles) {
					double newDistance = SimilarityCounter.compareStrings(
							prevFile, potentialPrevFile);
					if (newDistance <= distance) {
						newPrevFile = potentialPrevFile;
						distance = newDistance;
					}
				}

				if (newPrevFile == null) {
					scenIter.remove();
					handleWarningMessage("removed: Scenario " + scenName
							+ " due to missing prevalence "
							+ "file for riskfactor " + riskfactorName);

				}

				handleWarningMessage("Prevalence file for scenario " + scenName
						+ " is changed from " + prevFile + " into "
						+ newPrevFile);

				singleScenarioConfiguration
						.setAltPrevalenceFileName(newPrevFile);
			}
			;
			if (!availlableTransFiles.contains(transFile)) {
				String newTransFile = null;
				/* take the transition file with the most similar name */

				double distance = 1;
				for (String potentialTransFile : availlableTransFiles) {
					double newDistance = SimilarityCounter.compareStrings(
							transFile, potentialTransFile);
					if (newDistance <= distance) {
						newTransFile = potentialTransFile;
						distance = newDistance;
					}
				}
				if (newTransFile == null) {
					scenIter.remove();
					handleWarningMessage("removed: Scenario " + scenName
							+ " due to missing "
							+ "transition file for riskfactor "
							+ riskfactorName);

				} else {
					handleWarningMessage("Transition file for scenario "
							+ scenName + " is changed from " + transFile
							+ " into " + newTransFile);
					singleScenarioConfiguration
							.setAltTransitionFileName(newTransFile);
					;

					scenarioConfigurations
							.put(scenName, singleScenarioConfiguration);
				}
			}
		}

		this.dynamoSimulationObject
				.setScenarioConfigurations(scenarioConfigurations);

	}

	private String getInitialName() {
		String chosenRiskFactorName = null;
		if (this.initialSelection != null) {
			for (String chosenName : this.initialSelection) {
				chosenRiskFactorName = chosenName;
			}
		}
		return chosenRiskFactorName;
	}

	@Override
	public WritableValue getCurrentWritableValue(String successRate) {
		// Will not be used
		return null;
	}

	private void createInDynamoSimulationObject() {
		this.singleConfiguration = new TabRiskFactorConfigurationData();
	}

	@Override
	public DynamoSimulationObject getDynamoSimulationObject() {
		// TODO Auto-generated method stub
		return this.getDynamoSimulationObject();
	}

	private void handleWarningMessage(String s) {
		MessageBox box = new MessageBox(this.tab.getPlotComposite().getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("WARNING");
		box.setMessage("WARNING:\n" + s);
		box.open();
	}

}
