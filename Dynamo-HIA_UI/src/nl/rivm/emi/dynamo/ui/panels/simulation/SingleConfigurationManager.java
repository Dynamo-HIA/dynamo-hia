package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabDiseaseConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.ChoosableDiseaseNameManager;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of the inherited interface. This Class has been introduced to
 * get more meaningful method names without changing the interface.
 */
class SingleConfigurationManager {
	private Log log = LogFactory.getLog(this.getClass().getName());

	private DynamoSimulationObject dynamoSimulationObject;
	private ITabDiseaseConfiguration singleConfiguration;
	private String initialName;
	private TreeAsDropdownLists treeLists;
	private ChoosableDiseaseNameManager choosableDiseaseNameManager;
	private String tabName;

	public SingleConfigurationManager(BaseNode selectedNode,
			DynamoSimulationObject modelObject, Set<String> initialSelection,
			ChoosableDiseaseNameManager choosableDiseaseNameManager,
			String tabName) throws ConfigurationException, NoMoreDataException {
		super();
		this.dynamoSimulationObject = modelObject;
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.choosableDiseaseNameManager = choosableDiseaseNameManager;
		this.tabName = tabName;

		initialName = getInitialNameFromInitialSelection(initialSelection);
		log.debug("<init> initialName: " + initialName);
		if (initialName != null) {
			this.singleConfiguration = modelObject.getDiseaseConfigurations()
					.get(initialName);
		} else {
			createNewSingleConfigurationWithDefaults(null);
		}
	}

	public DropDownPropertiesSet getDropDownSetAndUpdateModel(String name,
			String chosenName) throws ConfigurationException,
			NoMoreDataException {
		if (DiseaseSelectionGroup.DISEASE.equals(name)) {
			chosenName = handlePossibleChangeOfDiseaseName(name, chosenName);
		}
		DropDownPropertiesSet set = null;
		if(chosenName != null){
		set = new DropDownPropertiesSet();
		Set<String> contents = this.getDropDownSetForField(name, chosenName);
		// Contents can never be empty
		if ((contents != null) && (!contents.isEmpty())) {
			set.addAll(contents);
			if (!DiseaseSelectionGroup.DISEASE.equals(name)) {
				updateFieldInSingleConfigurationAndUpdateSimulationObjectIfChanged(
						name, set.getSelectedString(0));
			}
		} else {
				throw new NoMoreDataException("the configured disease "
						+ chosenName + " is no longer availlable");
			}
		} else {
				throw new NoMoreDataException(
						"no more configured diseases availlable");
		}
		return set;
	}

	public Set<String> getDropDownSetForField(String fieldName,
			String chosenDiseaseName) throws ConfigurationException,
			NoMoreDataException {
		Set<String> contents = new LinkedHashSet<String>();
		// The name is still empty
		if (chosenDiseaseName == null) {
			throw new ConfigurationException("getContents() called for: "
					+ fieldName + " chosenDiseaseName: " + chosenDiseaseName
					+ " should not be null.");
		}
		// log.debug("HIERO chosenDiseaseName DATAMANAGER: " +
		// chosenDiseaseName);
		if (DiseaseSelectionGroup.DISEASE.equals(fieldName)) {
			// contents = this.treeLists.getValidDiseases();
			contents = choosableDiseaseNameManager
					.getChoosableDiseaseNamesSet(chosenDiseaseName);
			// log.debug("getContents NAME: " + contents);
		} else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(fieldName)) {
			contents = this.treeLists.getDiseasePrevalences(chosenDiseaseName);
			// log.debug("contents1" + contents);
		} else if (DiseaseResultGroup.INCIDENCE.equals(fieldName)) {
			contents = this.treeLists.getDiseaseIncidences(chosenDiseaseName);
			// log.debug("contents2" + contents);
		} else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(fieldName)) {
			contents = this.treeLists
					.getDiseaseExcessMortalities(chosenDiseaseName);
			// log.debug("contents3" + contents);
		} else if (DiseaseResultGroup.DALY_WEIGHTS.equals(fieldName)) {
			contents = this.treeLists.getDALYWeights(chosenDiseaseName);
			// log.debug("contents4" + contents);
		}
		// log.debug("contentsLast" + contents);
		log.debug("Got contents for dropdown-name: " + fieldName
				+ " , chosenDiseaseName: " + chosenDiseaseName + " contents: "
				+ contents);
		return contents;
	}

	public String getValueFromSingleConfiguration(String name)
			throws ConfigurationException {
		// log.debug("GET CURRENT VALUE");
		// log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		if (this.singleConfiguration != null) {
			if (DiseaseSelectionGroup.DISEASE.equals(name)) {
				value = singleConfiguration.getName();
				// log.debug("VALUE: " + value);
			} else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(name)) {
				value = singleConfiguration.getPrevalenceFileName();
				// log.debug("value" + value);
			} else if (DiseaseResultGroup.INCIDENCE.equals(name)) {
				value = singleConfiguration.getIncidenceFileName();
				// log.debug("value" + value);
			} else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(name)) {
				value = singleConfiguration.getExcessMortalityFileName();
				// log.debug("value" + value);
			} else if (DiseaseResultGroup.DALY_WEIGHTS.equals(name)) {
				value = singleConfiguration.getDalyWeightsFileName();
				// log.debug("value" + value);
			}
		}
		log.debug("getCurrentValue() returns: " + value + " for name: " + name);
		return value;
	}

	private String handlePossibleChangeOfDiseaseName(String name,
			String chosenName) throws ConfigurationException,
			NoMoreDataException {
		if (singleConfiguration != null) { // Should now always be true.
			String configuredName = singleConfiguration.getName();
			if (configuredName != null) {
				if (chosenName == null) {
					log.debug("Keeping configuredName: " + configuredName);
					chosenName = configuredName;
				} else {
					if (configuredName.equals(chosenName)) {
						log.debug("Keeping configuredName: " + configuredName
								+ " , chosenName: " + chosenName);
					} else {
						log.debug("Changing configuredName: " + configuredName
								+ " to chosenName: " + chosenName
								+ " going to setDefaultValues.");
						// Clean out the old configuration.
						removeMyConfigurationFromDynamoSimulationObjectAndMakeChoosable();
						createNewSingleConfigurationWithDefaults(chosenName);
						putMyConfigurationIntoDynamoSimulationObjectAndRemoveFromChoosableListWhenNew();
					}
				}
			} else {
				Set<String> diseaseNames = choosableDiseaseNameManager
						.getChoosableDiseaseNamesSet(null);
				if (diseaseNames.size() == 0) {
					throw new NoMoreDataException(
							"no more configured diseases availlable");
				}
				if ((chosenName == null) || (chosenName.equals(""))) {
					log.debug("Going to pick a new diseaseName.");
					chosenName = ((Iterator<String>) diseaseNames.iterator())
							.next();
				} else {
					if (!diseaseNames.contains(chosenName)) {
						throw new ConfigurationException("DiseaseName: "
								+ chosenName + " not found in dropDown: "
								+ diseaseNames.toString());
					}
				}
				updateFieldInSingleConfigurationAndUpdateSimulationObjectIfChanged(
						name, chosenName);
			}
		} else {
			log.debug("Unexpected!!!");
		}
		return chosenName;
	}

	private void createNewSingleConfigurationWithDefaults(String chosenName)
			throws NoMoreDataException, ConfigurationException {
		singleConfiguration = new TabDiseaseConfigurationData();
		String assignedName = null;
		if (chosenName != null) {
			assignedName = chosenName;
		} else {
			Set<String> possibleNames = choosableDiseaseNameManager
					.getChoosableDiseaseNamesSet(null);
			if (possibleNames.size() == 0) {
				throw new NoMoreDataException(
						"No more diseases for configuring.");
			}
			assignedName = possibleNames.iterator().next();
		}
		singleConfiguration.setName(assignedName);
		getDropDownSetAndUpdateModel(DiseaseSelectionGroup.DISEASE, null);
	}

	/**
	 * 
	 * Updates the object model every time a selection is made
	 * 
	 * @param dropDownName
	 * @param selectedValue
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	public void updateFieldInSingleConfigurationAndUpdateSimulationObjectIfChanged(
			String name, String selectedValue) throws ConfigurationException,
			NoMoreDataException {
		log.debug("Entering updateObjectState().");
//		Exception e = new Exception();
//		e.printStackTrace(System.out);
		if (!"".equals(selectedValue)) {
			boolean changed = false;
			if (DiseaseSelectionGroup.DISEASE.equals(name)) {
				String oldValue = singleConfiguration.getName();
				if ((oldValue == null) || (!oldValue.equals(selectedValue))) {
					singleConfiguration.setName(selectedValue);
					// updateDerivedFields!
					putMyConfigurationIntoDynamoSimulationObjectAndRemoveFromChoosableListWhenNew();
					changed = true;
				}
			} else if (DiseaseResultGroup.DISEASE_PREVALENCE.equals(name)) {
				String oldValue = singleConfiguration.getPrevalenceFileName();
				if ((oldValue == null) || (!oldValue.equals(selectedValue))) {
					singleConfiguration.setPrevalenceFileName(selectedValue);
					changed = true;
				}
			} else if (DiseaseResultGroup.INCIDENCE.equals(name)) {
				String oldValue = singleConfiguration.getIncidenceFileName();
				if ((oldValue == null) || (!oldValue.equals(selectedValue))) {
					singleConfiguration.setIncidenceFileName(selectedValue);
					changed = true;
				}
			} else if (DiseaseResultGroup.EXCESS_MORTALITY.equals(name)) {
				String oldValue = singleConfiguration
						.getExcessMortalityFileName();
				if ((oldValue == null) || (!oldValue.equals(selectedValue))) {
					singleConfiguration
							.setExcessMortalityFileName(selectedValue);
					changed = true;
				}
			} else if (DiseaseResultGroup.DALY_WEIGHTS.equals(name)) {
				String oldValue = singleConfiguration.getDalyWeightsFileName();
				if ((oldValue == null) || (!oldValue.equals(selectedValue))) {
					singleConfiguration.setDalyWeightsFileName(selectedValue);
					changed = true;
				}
			}
			if (changed) {
				putSingleConfigurationIntoDynamoSimulationObject();
			}
		}
	}

	/**
	 * This method is used when the diseaseName for a tab is changed.
	 * 
	 * 
	 * 
	 */
	public void removeMyConfigurationFromDynamoSimulationObjectAndMakeChoosable()
			throws ConfigurationException {
		if (this.singleConfiguration != null) {
			String diseaseName = this.singleConfiguration.getName();
			log
					.debug("removeMyConfigurationFromDynamoSimulationObjectAndMakeChoosable(), "
							+ "removing singleConfiguration for name: "
							+ diseaseName);
			Map<String, ITabDiseaseConfiguration> configurations = dynamoSimulationObject
					.getDiseaseConfigurations();
			ITabDiseaseConfiguration removedConfig = configurations
					.remove(diseaseName);
			if (removedConfig != null) {
				dynamoSimulationObject.setDiseaseConfigurations(configurations);
				choosableDiseaseNameManager.removeChosenDiseaseName(
						diseaseName, tabName);
// @@@@				updateDependentRelativeRisks(diseaseName);
			} else {
				log
						.error("Tried to remove a singleConfiguration for diseaseName: "
								+ diseaseName + " that wasn't there.");
			}
		} else {
			throw new RuntimeException(
					" singleConfiguration shouldn't be null.");
		}
	}

	/*
	 * added by Hendriek Also check if the disease names in the relative risks
	 * are still valid If not remove Both to and from can be diseasenames and
	 * should be checked
	 */
	private void updateDependentRelativeRisks(String removedDisease) {
		Map<Integer, TabRelativeRiskConfigurationData> relRiskConfiguration = dynamoSimulationObject
				.getRelativeRiskConfigurations();

		TabRelativeRiskConfigurationData singleRRconfiguration;

		for (Iterator<TabRelativeRiskConfigurationData> iter = relRiskConfiguration
				.values().iterator(); iter.hasNext();) {

			// for (Integer key2 : relRiskConfiguration.keySet())

			singleRRconfiguration = iter.next();

			if (singleRRconfiguration.getFrom().equals(removedDisease)
					|| singleRRconfiguration.getTo().equals(removedDisease))
				iter.remove();

			log.fatal("stop5: " + "size: " + relRiskConfiguration.size());
		}
		dynamoSimulationObject
				.setRelativeRiskConfigurations(relRiskConfiguration);
	}

	/**
	 * 20091012 mondeelr Methods sets the chosen diseasename to the central
	 * logic. Added the tabname to catch double use of diseasenames.
	 */
	public void putMyConfigurationIntoDynamoSimulationObjectAndRemoveFromChoosableListWhenNew()
			throws ConfigurationException {
		// log.debug("Setting name: " + selectedName + " for dropdownbox: "
		// + dropDownName);
		if (!putSingleConfigurationIntoDynamoSimulationObject()) {
			choosableDiseaseNameManager.setChosenDiseaseName(
					singleConfiguration.getName(), tabName);
		}
	}

	/**
	 * @return True when an existing configuration was replaced.
	 */
	public boolean putSingleConfigurationIntoDynamoSimulationObject() {
		log
				.debug("putSingleConfigurationIntoDynamoSimulationObject(), "
						+ "putting singleConfiguration"
						+ singleConfiguration.getName());
		Map<String, ITabDiseaseConfiguration> configurations = dynamoSimulationObject
				.getDiseaseConfigurations();
		ITabDiseaseConfiguration possiblyReplacedConfiguration = configurations
				.put(singleConfiguration.getName(), singleConfiguration);
		dynamoSimulationObject.setDiseaseConfigurations(configurations);
		log.debug("configurations.size()" + configurations.size());
		return (possiblyReplacedConfiguration != null);
	}

	/**
	 * TODO Probably superfluous. Functionality moved from the
	 * DiseaseTabPlatform. Because the {@link DynamoSimulationObject} is a
	 * reference, changes have already been done.
	 */
	public void touchConfigurations() {
		Map<String, ITabDiseaseConfiguration> configurations = dynamoSimulationObject
				.getDiseaseConfigurations();
		dynamoSimulationObject.setDiseaseConfigurations(configurations);
	}

	/**
	 * Method returns the last name of the initialSelection set. Needed to solve
	 * an impedance mismatch in the interface.
	 * 
	 * @param initialSelection
	 * 
	 * @return Null if the Set is empty or non-existent.
	 */
	private String getInitialNameFromInitialSelection(
			Set<String> initialSelection) {
		String initialDiseaseName = null;
		if (initialSelection != null) {
			for (String chosenName : initialSelection) {
				initialDiseaseName = chosenName;
			}
		}
		return initialDiseaseName;
	}
}
